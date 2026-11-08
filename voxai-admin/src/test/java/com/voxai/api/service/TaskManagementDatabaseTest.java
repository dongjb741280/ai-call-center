package com.voxai.api.service;

import com.voxai.api.exception.BusinessException;
import com.voxai.api.vo.task.*;
import com.voxai.core.entity.*;
import com.voxai.core.mapper.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;

/** Opt-in MySQL regression test. All fixture data is rolled back, including on assertion failure. */
class TaskManagementDatabaseTest {
    @Test
    void createsBindsClaimsRenewsCompletesAndArchivesWithDefaultMyBatisMapping() throws Exception {
        String url = System.getenv("TASK_TEST_JDBC_URL");
        assumeTrue(url != null, "Set TASK_TEST_JDBC_URL/USER/PASSWORD for migrated MySQL integration test");
        UnpooledDataSource dataSource = new UnpooledDataSource("com.mysql.cj.jdbc.Driver", url,
                System.getenv("TASK_TEST_JDBC_USER"), System.getenv("TASK_TEST_JDBC_PASSWORD"));
        Configuration configuration = new Configuration(new Environment("test", new JdbcTransactionFactory(), dataSource));
        for (String mapper : new String[]{"TaskConfigMapper", "TaskContactMapper", "TaskSourceMapper", "TaskFieldMapper", "TaskPauseMapper", "TaskManagementMapper"}) {
            String resource = "mapper/" + mapper + ".xml";
            try (InputStream input = Resources.getResourceAsStream(resource)) {
                new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
            }
        }
        assertFalse(configuration.isMapUnderscoreToCamelCase());
        try (SqlSession session = new SqlSessionFactoryBuilder().build(configuration).openSession(false)) {
            try {
                long company = Long.MAX_VALUE - 1;
                long now = Instant.now().getEpochSecond();
                TaskManagementMapper tasks = session.getMapper(TaskManagementMapper.class);
                TaskManagementService service = new TaskManagementService();
                ReflectionTestUtils.setField(service, "tasks", tasks);
                ReflectionTestUtils.setField(service, "sources", session.getMapper(TaskSourceMapper.class));
                ReflectionTestUtils.setField(service, "fields", session.getMapper(TaskFieldMapper.class));
                GroupMapper groups = mock(GroupMapper.class);
                Group group = new Group(); group.setCompanyId(company); group.setStatus(1); group.setName("test group");
                when(groups.selectByPrimaryKey(30L)).thenReturn(group);
                ReflectionTestUtils.setField(service, "groups", groups);

                TaskSource sourceInput = new TaskSource(); sourceInput.setName("regression-" + UUID.randomUUID());
                TaskSource source = service.saveSource(company, null, sourceInput);
                assertNotNull(source.getId()); assertEquals(Long.valueOf(0), source.getVersion());
                TaskField fieldInput = new TaskField(); fieldInput.setName("phone"); fieldInput.setIsPhone(1); fieldInput.setFieldType("系统字段");
                TaskField field = service.saveField(company, null, fieldInput);
                assertNotNull(field.getId());
                TaskFieldsEdit binding = new TaskFieldsEdit(); binding.setVersion(0L); binding.setFieldIds(Collections.singletonList(field.getId()));
                service.bind(company, source.getId(), binding);

                // Legacy task starts without stable source/target IDs, and can be repaired once.
                TaskConfig legacy = new TaskConfig(); legacy.setCompanyId(company); legacy.setName("regression"); legacy.setCts(now); legacy.setUts(now); legacy.setRetryInterval(1);
                tasks.insertTask(legacy);
                TaskEdit edit = new TaskEdit(); edit.setName("regression"); edit.setVersion(0L); edit.setSourceId(source.getId());
                edit.setTransferType("group"); edit.setTransferId(30L); edit.setCallStartTime("00:00:00"); edit.setCallEndTime("23:59:59");
                edit.setCloseTime(now + 600); edit.setConcurrency(1); edit.setCallRounds(2); edit.setCallTimeout(30); edit.setAfterCallDuration(0); edit.setCruiseType("cruise");
                edit.setStatusNotify("https://example.com/" + String.join("", Collections.nCopies(64, "x")));
                assertThrows(BusinessException.class, () -> service.saveTask(company, legacy.getId(), edit));
                edit.setStatusNotify(null);
                TaskConfig task = service.saveTask(company, legacy.getId(), edit);
                assertEquals(source.getId(), task.getSourceId());
                TaskContact row = new TaskContact(); row.setPhone("13800000000"); row.setUuid(UUID.randomUUID().toString());
                TaskImport batch = new TaskImport(); batch.setContacts(Collections.singletonList(row));
                assertEquals(1, service.importContacts(company, task.getId(), batch));
                TaskCommand start = new TaskCommand(); start.setCommandId(UUID.randomUUID().toString()); start.setAction("start"); start.setVersion(task.getVersion());
                service.command(company, task.getId(), start);
                task = service.acknowledge(company, task.getId(), start.getCommandId(), true);
                TaskClaim claim = new TaskClaim(); claim.setRequestId(UUID.randomUUID().toString()); claim.setLimit(1); claim.setLeaseSeconds(30);
                TaskClaimResult first = service.claim(company, task.getId(), claim).get(0);
                assertEquals(first.getAttemptId(), service.claim(company, task.getId(), claim).get(0).getAttemptId());
                claim.setAttemptId(first.getAttemptId()); claim.setLeaseToken(first.getLeaseToken()); claim.setContactId(first.getContactId());
                service.heartbeat(company, task.getId(), claim);
                TaskCommand close = new TaskCommand(); close.setCommandId(UUID.randomUUID().toString()); close.setAction("close"); close.setVersion(task.getVersion());
                service.command(company, task.getId(), close);
                service.acknowledge(company, task.getId(), close.getCommandId(), true);
                Long taskId = task.getId();
                assertThrows(BusinessException.class, () -> service.archive(company, taskId));
                claim.setResult("answered"); claim.setCallId(Long.MAX_VALUE - 2);
                assertThrows(BusinessException.class, () -> service.completeAttempt(company, taskId, claim));
                try (PreparedStatement insert = session.getConnection().prepareStatement("insert into cc_call_log(company_id,task_id,call_id) values(?,?,?)")) {
                    insert.setLong(1, company - 1); insert.setLong(2, taskId); insert.setLong(3, claim.getCallId()); insert.executeUpdate();
                }
                session.clearCache();
                assertThrows(BusinessException.class, () -> service.completeAttempt(company, taskId, claim));
                try (PreparedStatement update = session.getConnection().prepareStatement("update cc_call_log set company_id=?,end_time=1 where call_id=?")) {
                    update.setLong(1, company); update.setLong(2, claim.getCallId()); update.executeUpdate();
                }
                session.clearCache();
                service.completeAttempt(company, taskId, claim);
                service.completeAttempt(company, taskId, claim);
                TaskContact completed = service.contacts(company, taskId, 1, 20, null, null).getList().get(0);
                assertEquals("answered", completed.getCallResult()); assertEquals("completed", completed.getCallStatus());
                assertEquals(1, tasks.countCallAttempts(company, taskId, claim.getCallId()));
                assertNull(tasks.lockTask(company - 1, taskId));
                service.archive(company, taskId);
                assertNull(tasks.lockTask(company, taskId));
            } finally {
                session.rollback(true);
            }
        }
    }
}

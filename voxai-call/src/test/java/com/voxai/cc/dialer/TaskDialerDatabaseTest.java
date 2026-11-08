package com.voxai.cc.dialer;

import com.voxai.core.entity.*;
import com.voxai.core.mapper.*;
import com.voxai.core.po.dialer.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.sql.Statement;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Run against an isolated migrated MySQL schema; fixture DML is always rolled back. */
class TaskDialerDatabaseTest {
    @Test void dispatchJournalIsTenantScopedAndExpiredDispatchedCallsCannotBeReclaimed() throws Exception {
        String url=System.getenv("TASK_TEST_JDBC_URL"); assumeTrue(url!=null,"Opt-in migrated MySQL test");
        Configuration config=new Configuration(new Environment("test",new JdbcTransactionFactory(),new UnpooledDataSource(
                "com.mysql.cj.jdbc.Driver",url,System.getenv("TASK_TEST_JDBC_USER"),System.getenv("TASK_TEST_JDBC_PASSWORD"))));
        for(String mapper:new String[]{"TaskConfigMapper","TaskContactMapper","TaskSourceMapper","TaskFieldMapper","TaskPauseMapper","TaskManagementMapper","CallLogMapper","CallDetailMapper","CallDeviceMapper","CompanyPhoneMapper","TaskDialerMapper"}) {
            String resource="mapper/"+mapper+".xml";
            try(InputStream input=Resources.getResourceAsStream(resource)) { new XMLMapperBuilder(input,config,resource,config.getSqlFragments()).parse(); }
        }
        try(SqlSession session=new SqlSessionFactoryBuilder().build(config).openSession(false)) {
            try {
                long company=Long.MAX_VALUE-3,now=Instant.now().getEpochSecond();
                TaskManagementMapper tasks=session.getMapper(TaskManagementMapper.class); TaskDialerMapper dialer=session.getMapper(TaskDialerMapper.class);
                sql(session,"insert into cc_company(id,status) values("+company+",1)"); assertEquals(Long.valueOf(company),dialer.lockCompany(company));
                TaskConfig task=new TaskConfig(); task.setCompanyId(company); task.setName("regression"); task.setCts(now); task.setUts(now); task.setRetryInterval(1); task.setTransferType("group"); task.setTransferId(22L); task.setCallRounds(2);
                tasks.insertTask(task); Long taskId=task.getId();
                TaskContact contact=new TaskContact(); contact.setTaskId(taskId); contact.setPhone("13800000000"); contact.setUuid(UUID.randomUUID().toString()); contact.setCustomData("{\"test\":true}"); tasks.insertContact(contact);
                String attemptId=UUID.randomUUID().toString(); tasks.leaseContact(contact.getId(),taskId,attemptId,now+120,now);
                TaskAttempt attempt=new TaskAttempt(); attempt.setAttemptId(attemptId); attempt.setCompanyId(company); attempt.setTaskId(taskId); attempt.setContactId(contact.getId()); attempt.setRequestId(UUID.randomUUID().toString()); attempt.setLeaseToken("lease"); attempt.setLeaseUntil(now+120); attempt.setRound(1); attempt.setCts(now); attempt.setUts(now); tasks.insertAttempt(attempt);
                assertEquals(1,dialer.activeAttempts(company,taskId,"",100).size()); assertTrue(dialer.activeAttempts(company-1,taskId,"",100).isEmpty());
                attempt.setCallId(Long.MAX_VALUE-4); attempt.setMediaHost("fs:8021"); attempt.setDispatchTime(now); assertEquals(1,dialer.assignCall(attempt)); assertEquals(0,dialer.assignCall(attempt));
                DialerWork work=dialer.activeAttempts(company,taskId,"",100).get(0); assertEquals(attemptId,work.getAttemptId()); assertEquals(attempt.getCallId(),work.getCallId()); assertEquals("fs:8021",work.getMediaHost()); assertEquals(Long.valueOf(now),work.getDispatchTime()); assertEquals("13800000000",work.getPhone());
                DialerCapacity metrics=dialer.metrics(company,taskId,"group",22L,now-3600); assertEquals(1,metrics.getActiveCalls()); assertEquals(1,metrics.getTargetActiveCalls()); assertEquals(1,metrics.getCompanyActiveCalls()); assertEquals(0,metrics.getCompletedCalls());
                sql(session,"update cc_task_config set status=3 where id="+taskId);
                assertEquals(1,dialer.engineTasks(company,0L,100).size()); assertTrue(dialer.engineTasks(company-1,0L,100).isEmpty());
                sql(session,"update cc_task_attempt set lease_until="+(now-1)+" where attempt_id='"+attemptId+"'");
                assertEquals(0,tasks.releaseExpired(company,taskId,now)); assertEquals(0,tasks.expireAttempts(company,taskId,now));
                assertEquals(1,tasks.renewAttempt(company,taskId,attemptId,"lease",now+120,now)); assertEquals(0,tasks.renewAttempt(company,taskId,attemptId,"wrong",now+120,now));
                sql(session,"insert into cc_call_log(company_id,task_id,call_id,end_time,answer_flag) values("+company+","+taskId+","+attempt.getCallId()+",1700000001000,0)");
                assertNotNull(dialer.call(company,taskId,attempt.getCallId())); assertNull(dialer.call(company-1,taskId,attempt.getCallId()));
                attempt.setResult("answered"); assertEquals(1,tasks.completeAttempt(attempt));
                contact.setActiveAttemptId(attemptId); contact.setCallStatus("completed"); contact.setCallResult("answered"); contact.setCallTime(now); contact.setNextAttemptAt(Long.MAX_VALUE); assertEquals(1,tasks.completeContact(contact));
                metrics=dialer.metrics(company,taskId,"group",22L,now-3600); assertEquals(0,metrics.getActiveCalls()); assertEquals(1,metrics.getCompletedCalls()); assertEquals(1,metrics.getAnsweredCalls()); assertTrue(dialer.engineTasks(company,0L,100).isEmpty());
                assertEquals(0,dialer.pendingContacts(company,taskId));
                // An un-dispatched lease can expire and be retried without consuming a dialing round.
                contact.setId(null); contact.setUuid(UUID.randomUUID().toString()); tasks.insertContact(contact);
                attemptId=UUID.randomUUID().toString(); tasks.leaseContact(contact.getId(),taskId,attemptId,now-1,now-2);
                attempt.setAttemptId(attemptId); attempt.setRequestId(UUID.randomUUID().toString()); attempt.setContactId(contact.getId()); attempt.setLeaseUntil(now-1); tasks.insertAttempt(attempt);
                assertEquals(1,tasks.releaseExpired(company,taskId,now)); assertEquals(1,tasks.expireAttempts(company,taskId,now));
            } finally { session.rollback(true); }
        }
    }
    private void sql(SqlSession session,String value) throws Exception {
        try(Statement statement=session.getConnection().createStatement()) { statement.executeUpdate(value); }
        session.clearCache();
    }
}

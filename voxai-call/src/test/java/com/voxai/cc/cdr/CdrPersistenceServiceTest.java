package com.voxai.cc.cdr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.voxai.core.entity.*;
import com.voxai.core.mapper.*;
import com.voxai.core.po.CallLogPo;
import com.voxai.core.util.SnowflakeIdWorker;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CdrPersistenceServiceTest {
    private AnnotationConfigApplicationContext context;
    private JdbcTemplate jdbc;
    private CdrPersistenceService writer;
    private static final long START = Instant.parse("2026-10-31T15:59:50Z").toEpochMilli();
    private static final long ID = (START - SnowflakeIdWorker.WORK_START) << 22;

    @BeforeEach
    void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(Config.class);
        jdbc = context.getBean(JdbcTemplate.class);
        writer = context.getBean(CdrPersistenceService.class);
        String schema;
        try (java.io.InputStream input = new ClassPathResource("sql/voxai.sql").getInputStream()) {
            schema = new String(org.apache.commons.io.IOUtils.toByteArray(input), StandardCharsets.UTF_8);
        }
        for (String table : Arrays.asList("cc_call_log", "cc_call_device", "cc_call_detail", "cc_cdr_notify")) {
            String create;
            if (table.equals("cc_cdr_notify")) {
                create = "CREATE TABLE cc_cdr_notify (call_id bigint primary key, company_id bigint, cdr_notify_url varchar(255), " +
                        "content text, push_times int default 0, push_response varchar(255) default '', status int default 1," +
                        " next_attempt_at bigint, lease_until bigint default 0, lease_token varchar(36) default '')";
            } else {
                Matcher matcher = Pattern.compile("CREATE TABLE `" + table + "` \\((.*?)\\) ENGINE=", Pattern.DOTALL).matcher(schema);
                assertTrue(matcher.find());
                create = "CREATE TABLE " + table + " (" + matcher.group(1)
                        .replaceAll(" CHARACTER SET [a-zA-Z0-9_]+ COLLATE [a-zA-Z0-9_]+", "")
                        .replace(" USING BTREE", "") + ")";
            }
            jdbc.execute("DROP TABLE IF EXISTS " + table);
            jdbc.execute(create);
            if (!table.equals("cc_cdr_notify")) {
                jdbc.execute("DROP TABLE IF EXISTS " + table + "_202610");
                jdbc.execute(create.replace("CREATE TABLE " + table + " (", "CREATE TABLE " + table + "_202610 (")
                        .replaceAll("`(uniq_[^`]+|idx_[^`]+)`", "`$1_month`"));
            }
        }
    }

    @AfterEach void close() { context.close(); }

    @Test void finalSnapshotIsIdempotentAndLateBridgeCannotOverwriteIt() {
        CdrCompletion completion = completion(2);
        writer.complete(completion);
        writer.complete(completion);
        CallLog bridge = new CallLog();
        bridge.setCallId(ID); bridge.setCallTime(START); bridge.setAnswerCount(1); bridge.setAnswerTime(START + 2000);
        writer.saveLog(bridge);
        for (String table : Arrays.asList("cc_call_log", "cc_call_device", "cc_call_detail")) {
            assertEquals(1, jdbc.queryForObject("select count(*) from " + table, Integer.class));
            assertEquals(1, jdbc.queryForObject("select count(*) from " + table + "_202610", Integer.class));
        }
        assertEquals(1, jdbc.queryForObject("select count(*) from cc_cdr_notify", Integer.class));
        assertEquals(2, jdbc.queryForObject("select answer_count from cc_call_log", Integer.class));
        assertEquals(START + 30000, jdbc.queryForObject("select end_time from cc_call_log", Long.class));
        String json = jdbc.queryForObject("select content from cc_cdr_notify", String.class);
        JSONObject body = JSON.parseObject(json);
        assertEquals(2, body.getIntValue("cdrVersion"));
        assertEquals("epochMillis", body.getString("timestampUnit"));
        assertEquals("milliseconds", body.getString("durationUnit"));
        assertEquals(1, body.getJSONArray("callDetailList").size());
        CallLogPo loaded = context.getBean(CallLogMapper.class).getCall(1L, ID, "_202610");
        assertEquals(1, loaded.getCallDeviceList().size());
        assertEquals(1, loaded.getCallDetailList().size());
        assertEquals("_202610", loaded.getMonthTime());
    }

    @Test void outboxFailureRollsBackBothCopiesOfAllThreeTables() {
        jdbc.execute("drop table cc_cdr_notify");
        assertThrows(RuntimeException.class, () -> writer.complete(completion(2)));
        for (String table : Arrays.asList("cc_call_log", "cc_call_device", "cc_call_detail")) {
            assertEquals(0, jdbc.queryForObject("select count(*) from " + table, Integer.class));
            assertEquals(0, jdbc.queryForObject("select count(*) from " + table + "_202610", Integer.class));
        }
    }

    @Test void firstActiveCallCanBeQueriedBeforeAnyDeviceHasHungUp() {
        for (String table : Arrays.asList("cc_call_log", "cc_call_device", "cc_call_detail")) {
            jdbc.execute("drop table " + table + "_202610");
        }
        doAnswer(invocation -> {
            for (String table : Arrays.asList("cc_call_log", "cc_call_device", "cc_call_detail")) {
                jdbc.execute("create table " + table + "_202610 as select * from " + table + " where 1=0");
            }
            return null;
        }).when(context.getBean("tables", CdrMonthTables.class)).ensure("_202610");
        CallLog active = completion(1).getCallLog();
        active.setEndTime(null);
        writer.saveLog(active);
        CallLogPo loaded = context.getBean(CallLogMapper.class).getCall(1L, ID, null);
        assertNotNull(loaded);
        assertEquals(0L, loaded.getEndTime());
        assertTrue(loaded.getCallDeviceList().isEmpty());
        assertTrue(loaded.getCallDetailList().isEmpty());
        assertEquals(0, jdbc.queryForObject("select count(*) from cc_call_log_202610", Integer.class));
    }

    @Test void monthlyListCountAndExportStillWorkAfterCurrentRowsAreRemoved() {
        writer.complete(completion(2));
        jdbc.execute("delete from cc_call_log");
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("companyId", 1L);
        params.put("monthTime", "_202610");
        CallLogMapper mapper = context.getBean(CallLogMapper.class);
        assertEquals(1, mapper.selectCountByMap(params));
        assertEquals(ID, mapper.selectListByMap(params).get(0).getCallId());
        assertEquals(1, mapper.getCallList(params).get(0).getCallDeviceList().size());
        params.remove("monthTime");
        assertEquals(0, mapper.selectCountByMap(params));
        assertTrue(mapper.getCallList(params).isEmpty());
    }

    @Test void legacyCallbackKeepsItsShapeAndDoesNotExposeV2Metadata() {
        writer.complete(completion(1));
        JSONObject body = JSON.parseObject(jdbc.queryForObject("select content from cc_cdr_notify", String.class));
        assertFalse(body.containsKey("cdrVersion"));
        assertFalse(body.containsKey("timestampUnit"));
        assertFalse(body.containsKey("durationUnit"));
        assertFalse(body.containsKey("recordStartTime"));
        assertEquals(1, body.getJSONArray("callDeviceList").size());
        assertTrue(body.get("callDetailList") == null);
    }

    @Test void unansweredCallStillHasMonthlyArchiveAndNoCallbackWhenUrlIsEmpty() {
        CdrCompletion completion = completion(1);
        completion.getCallLog().setAnswerTime(null);
        completion.setNotifyUrl(null);
        writer.complete(completion);
        assertEquals(1, jdbc.queryForObject("select count(*) from cc_call_log_202610", Integer.class));
        assertEquals(0, jdbc.queryForObject("select count(*) from cc_cdr_notify", Integer.class));
    }

    @Test void outboxClaimAndLeasePreventConcurrentSendsAndAllowCrashRecovery() {
        writer.complete(completion(2));
        CdrNotifyMapper mapper = context.getBean(CdrNotifyMapper.class);
        long now = System.currentTimeMillis() + 1000;
        CdrNotify row = mapper.pending(now).get(0);
        row.setCts(now); row.setLeaseUntil(now + 60000); row.setLeaseToken("worker-a");
        assertEquals(1, mapper.claim(row));
        row.setLeaseToken("worker-b");
        assertEquals(0, mapper.claim(row));
        assertTrue(mapper.pending(now).isEmpty());
        row.setStatus(2); row.setUts(now); row.setPushResponse("OK");
        assertEquals(0, mapper.finish(row));
        row.setPushTimes(1);
        row.setCts(now + 60001); row.setLeaseUntil(now + 120000);
        assertEquals(1, mapper.claim(row));
        assertEquals(1, mapper.finish(row));
        assertTrue(mapper.pending(now + 180000).isEmpty());
    }

    @Test void monthProvisioningDoesNotImplicitlyCommitTheCallerTransaction() {
        Assumptions.assumeTrue(System.getProperty("cdr.test.jdbcUrl", "").startsWith("jdbc:mysql:"));
        for (String table : Arrays.asList("cc_call_log", "cc_call_device", "cc_call_detail")) {
            jdbc.execute("drop table if exists " + table + "_202612");
        }
        org.springframework.transaction.support.TransactionTemplate transaction =
                new org.springframework.transaction.support.TransactionTemplate(context.getBean(PlatformTransactionManager.class));
        assertThrows(IllegalStateException.class, () -> transaction.execute(status -> {
            jdbc.update("insert into cc_call_log (call_id) values (?)", ID);
            context.getBean("provisioner", CdrMonthTables.class).ensure("_202612");
            throw new IllegalStateException("force rollback after DDL");
        }));
        assertEquals(0, jdbc.queryForObject("select count(*) from cc_call_log", Integer.class));
        for (String table : Arrays.asList("cc_call_log", "cc_call_device", "cc_call_detail")) {
            assertEquals(0, jdbc.queryForObject("select count(*) from " + table + "_202612", Integer.class));
        }
    }

    private CdrCompletion completion(int version) {
        CdrCompletion completion = new CdrCompletion();
        CallLog log = new CallLog();
        log.setCallId(ID); log.setCompanyId(1L); log.setCallTime(START);
        log.setHangupDir(1); log.setAnswerTime(START + 2000); log.setEndTime(START + 30000); log.setAnswerCount(2);
        completion.setCallLog(log);
        CallDevice device = new CallDevice();
        device.setCallId(ID); device.setDeviceId("cross-month"); device.setCallTime(START + 20000);
        device.setEndTime(START + 30000); device.setHangupCause("NORMAL_CLEARING");
        completion.setDevices(Collections.singletonList(device));
        CallDetail detail = new CallDetail();
        detail.setCallId(ID); detail.setDetailIndex(1); detail.setStartTime(START + 20000);
        completion.setDetails(Collections.singletonList(detail));
        completion.setNotifyUrl("https://example.invalid/cdr");
        completion.setNotifyVersion(version);
        return completion;
    }

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean DataSource dataSource() {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setUrl(System.getProperty("cdr.test.jdbcUrl", "jdbc:h2:mem:cdr;MODE=MySQL;DB_CLOSE_DELAY=-1"));
            ds.setUsername(System.getProperty("cdr.test.username", "root"));
            ds.setPassword("");
            return ds;
        }
        @Bean JdbcTemplate jdbc(DataSource ds) { return new JdbcTemplate(ds); }
        @Bean PlatformTransactionManager transactions(DataSource ds) { return new DataSourceTransactionManager(ds); }
        @Bean SqlSessionFactory sessionFactory(DataSource ds) throws Exception {
            SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
            factory.setDataSource(ds);
            factory.setMapperLocations(new ClassPathResource("mapper/CallLogMapper.xml"),
                    new ClassPathResource("mapper/CallDeviceMapper.xml"), new ClassPathResource("mapper/CallDetailMapper.xml"));
            SqlSessionFactory result = factory.getObject();
            result.getConfiguration().addMapper(CdrNotifyMapper.class);
            return result;
        }
        @Bean SqlSessionTemplate sessions(SqlSessionFactory factory) { return new SqlSessionTemplate(factory); }
        @Bean CallLogMapper logs(SqlSessionTemplate session) { return session.getMapper(CallLogMapper.class); }
        @Bean CallDeviceMapper devices(SqlSessionTemplate session) { return session.getMapper(CallDeviceMapper.class); }
        @Bean CallDetailMapper details(SqlSessionTemplate session) { return session.getMapper(CallDetailMapper.class); }
        @Bean CdrNotifyMapper notifications(SqlSessionTemplate session) { return session.getMapper(CdrNotifyMapper.class); }
        @Bean CdrMonthTables provisioner() { return new CdrMonthTables(); }
        @Bean @Primary CdrMonthTables tables() { return mock(CdrMonthTables.class); }
        @Bean CdrPersistenceService writer() { return new CdrPersistenceService(); }
    }
}

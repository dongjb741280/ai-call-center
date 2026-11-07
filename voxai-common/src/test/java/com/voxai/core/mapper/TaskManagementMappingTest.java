package com.voxai.core.mapper;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskManagementMappingTest {
    @Test
    void parsesTaskMappersAndKeepsTenantAndLeaseGuardsInSql() throws Exception {
        Configuration configuration = new Configuration();
        String[] mappers = {"TaskConfigMapper", "TaskContactMapper", "TaskSourceMapper", "TaskFieldMapper", "TaskPauseMapper", "TaskManagementMapper"};
        for (String mapper : mappers) {
            String resource = "mapper/" + mapper + ".xml";
            try (InputStream input = Resources.getResourceAsStream(resource)) {
                new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("companyId", 7L);
        params.put("taskId", 11L);
        params.put("attemptId", "attempt");
        params.put("leaseToken", "secret");
        params.put("leaseUntil", 200L);
        params.put("now", 100L);
        MappedStatement heartbeat = configuration.getMappedStatement("com.voxai.core.mapper.TaskManagementMapper.renewAttempt");
        String heartbeatSql = heartbeat.getBoundSql(params).getSql().replaceAll("\\s+", " ");
        assertTrue(heartbeatSql.contains("company_id=?"));
        assertTrue(heartbeatSql.contains("task_id=?"));
        assertTrue(heartbeatSql.contains("lease_until > ?"));

        params.put("limit", 1);
        String claimSql = configuration.getMappedStatement("com.voxai.core.mapper.TaskManagementMapper.claimContacts")
                .getBoundSql(params).getSql().toLowerCase().replaceAll("\\s+", " ");
        assertTrue(claimSql.contains("t.company_id=?"));
        assertTrue(claimSql.contains("skip locked"));
    }
}

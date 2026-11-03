package com.voxai.core.mapper;

import com.voxai.core.po.AgentInfo;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AgentGroupsMappingTest {
    @Test
    void adminListAndDetailReadPrimaryFromRelationAndFilterThroughRelation() throws Exception {
        Configuration configuration = new Configuration();
        for (String mapper : new String[]{"AgentGroupMapper", "AgentMapper"}) {
            String resource = "mapper/" + mapper + ".xml";
            try (InputStream input = Resources.getResourceAsStream(resource)) {
                new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
            }
        }
        for (String statement : new String[]{"selectListByMap", "selectAgentInfo"}) {
            ResultMap resultMap = configuration.getMappedStatement("com.voxai.core.mapper.AgentMapper." + statement).getResultMaps().get(0);
            assertEquals(AgentInfo.class, resultMap.getType());
            assertTrue(resultMap.getResultMappings().stream().anyMatch(mapping ->
                    "groupId".equals(mapping.getProperty()) &&
                            "com.voxai.core.mapper.AgentGroupMapper.selectPrimaryByAgent".equals(mapping.getNestedQueryId())));
        }
        Map<String, Object> params = new HashMap<>();
        params.put("groupId", 20L);
        String sql = configuration.getMappedStatement("com.voxai.core.mapper.AgentMapper.selectListByMap")
                .getBoundSql(params).getSql();
        assertTrue(sql.contains("ag.is_primary = 1"));
        assertTrue(sql.contains("ag.agent_id = cc_agent.id"));
        assertTrue(sql.contains("ag.status = 1"));
    }
}

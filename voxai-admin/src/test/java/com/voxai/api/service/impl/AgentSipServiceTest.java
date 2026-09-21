package com.voxai.api.service.impl;

import com.voxai.core.entity.AgentSip;
import com.voxai.core.mapper.AgentSipMapper;
import com.voxai.core.vo.AgentSipVo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

class AgentSipServiceTest {
    @Test
    void createsSipWithNumberAndActiveStatus() {
        AgentSipMapper mapper = mock(AgentSipMapper.class);
        when(mapper.selectAgentSip(anyMap())).thenReturn(Collections.emptyList());
        AgentServiceImpl service = new AgentServiceImpl();
        ReflectionTestUtils.setField(service, "agentSipMapper", mapper);
        AgentSipVo request = new AgentSipVo();
        request.setSip("950001");
        request.setSipPwd("test-pass");
        request.setCompanyId(2L);

        service.saveOrUpdateAgentSip(request);

        ArgumentCaptor<AgentSip> record = ArgumentCaptor.forClass(AgentSip.class);
        verify(mapper).insertSelective(record.capture());
        assertEquals(Integer.valueOf(950001), record.getValue().getSip());
        assertEquals(Integer.valueOf(1), record.getValue().getStatus());
        assertEquals(Long.valueOf(2), record.getValue().getCompanyId());
    }

    @Test
    void updatesSipNumberWithoutChangingStatus() {
        AgentSipMapper mapper = mock(AgentSipMapper.class);
        when(mapper.selectAgentSip(anyMap())).thenReturn(Collections.emptyList());
        when(mapper.selectById(2L, 7L)).thenReturn(new AgentSip());
        AgentServiceImpl service = new AgentServiceImpl();
        ReflectionTestUtils.setField(service, "agentSipMapper", mapper);
        AgentSipVo request = new AgentSipVo();
        request.setId(7L);
        request.setCompanyId(2L);
        request.setSip("950002");
        request.setSipPwd("test-pass");

        service.saveOrUpdateAgentSip(request);

        ArgumentCaptor<AgentSip> record = ArgumentCaptor.forClass(AgentSip.class);
        verify(mapper).updateByPrimaryKeySelective(record.capture());
        assertEquals(Integer.valueOf(950002), record.getValue().getSip());
        assertNull(record.getValue().getStatus());
    }

    @Test
    void validatesNumbersAgainstIntegerStorage() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            AgentSipVo request = new AgentSipVo();
            request.setSipPwd("test-pass");
            for (String invalid : new String[]{"01234", "1234", "abcde", "2147483648", "9999999999999999"}) {
                request.setSip(invalid);
                assertFalse(factory.getValidator().validate(request).isEmpty(), invalid);
            }
            request.setSip("2147483647");
            assertTrue(factory.getValidator().validate(request).isEmpty());
        }
    }
}

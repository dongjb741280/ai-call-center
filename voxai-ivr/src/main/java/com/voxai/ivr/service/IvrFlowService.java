package com.voxai.ivr.service;

import com.voxai.core.entity.IvrFlow;
import com.voxai.core.mapper.IvrFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * IVR 轨迹落库。
 */
@Service
public class IvrFlowService {

    @Autowired
    private IvrFlowMapper ivrFlowMapper;

    public void save(IvrFlow ivrFlow) {
        ivrFlowMapper.insertSelective(ivrFlow);
    }
}

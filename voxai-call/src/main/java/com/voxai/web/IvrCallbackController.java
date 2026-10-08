package com.voxai.web;

import com.voxai.cc.command.TransferAgentHandler;
import com.voxai.cc.command.VdnHandler;
import com.voxai.core.enums.ErrorCode;
import com.voxai.core.po.AgentInfo;
import com.voxai.core.po.CallInfo;
import com.voxai.core.po.CommonResponse;
import com.voxai.core.po.DeviceInfo;
import com.voxai.core.po.GroupInfo;
import com.voxai.core.vo.IvrCompleteVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.voxai.web.base.BaseController;

/**
 * IVR 引擎结束回调：voxai-ivr 完成放音/收键后，将转接目标回传给本服务继续路由。
 *
 * @author dongjb
 * @date 2026/10/08
 */
@RestController
@RequestMapping("index")
public class IvrCallbackController extends BaseController {

    @Autowired
    private VdnHandler vdnHandler;

    @Autowired
    private TransferAgentHandler transferAgentHandler;

    @PostMapping("ivr/complete")
    public CommonResponse ivrComplete(@RequestBody IvrCompleteVo vo) {
        if (vo == null || vo.getCallId() == null || StringUtils.isBlank(vo.getDeviceId())) {
            return new CommonResponse(ErrorCode.PARAMETER_ERROR);
        }
        CallInfo callInfo = cacheService.getCallInfo(vo.getCallId());
        if (callInfo == null) {
            return new CommonResponse(ErrorCode.CALL_NOT_EXIST);
        }
        DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(vo.getDeviceId());
        if (deviceInfo == null) {
            return new CommonResponse(ErrorCode.DATA_NOT_EXIST);
        }

        String transferType = vo.getTransferType();
        logger.info("ivr complete callId:{}, deviceId:{}, transferType:{}, transferValue:{}",
                vo.getCallId(), vo.getDeviceId(), transferType, vo.getTransferValue());
        if ("group".equals(transferType)) {
            GroupInfo groupInfo = cacheService.getGroupInfo(Long.parseLong(vo.getTransferValue()));
            if (groupInfo == null) {
                return new CommonResponse(ErrorCode.DATA_NOT_EXIST);
            }
            groupHandler.hander(callInfo, groupInfo, vo.getDeviceId());
        } else if ("vdn".equals(transferType)) {
            vdnHandler.hanlder(callInfo, deviceInfo, Long.parseLong(vo.getTransferValue()));
        } else if ("agent".equals(transferType)) {
            AgentInfo agentInfo = cacheService.getAgentInfo(vo.getTransferValue());
            if (agentInfo == null) {
                return new CommonResponse(ErrorCode.DATA_NOT_EXIST);
            }
            transferAgentHandler.hanlder(callInfo, agentInfo, vo.getDeviceId());
        } else {
            // 默认挂机（含 hangup 及未识别的目标类型）
            fsListen.hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), vo.getDeviceId());
        }
        return new CommonResponse();
    }
}

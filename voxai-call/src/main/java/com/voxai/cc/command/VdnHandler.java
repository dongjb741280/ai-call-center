package com.voxai.cc.command;

import com.voxai.core.entity.Agent;
import com.voxai.core.entity.CallDetail;
import com.voxai.core.entity.Playback;
import com.voxai.core.entity.VdnDtmf;
import com.voxai.core.enums.NextType;
import com.voxai.core.po.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.voxai.cc.command.base.BaseHandler;

import java.time.Instant;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
public class VdnHandler extends BaseHandler {

    public void hanlder(CallInfo callInfo, DeviceInfo deviceInfo, Long vdnId) {
        String deviceId = deviceInfo.getDeviceId();
        VdnCodePo vdnCodePo = cacheService.getCompany(callInfo.getCompanyId()).getVdnCodeMap().get(vdnId);
        if (vdnCodePo == null || vdnCodePo.getStatus() == 0) {
            hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), deviceId);
            return;
        }
        //查询有效日程
        VdnSchedulePo vdnSchedulePo = vdnCodePo.getEffectiveSchedule();
        if (vdnSchedulePo == null) {
            logger.warn("callId:{} not match schedule, vdn:{}", callInfo.getCallId(), vdnCodePo.getName());
            hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), deviceId);
            return;
        }
        //电话经过vdn
        CallDetail callDetail = new CallDetail();
        callDetail.setCallId(callInfo.getCallId());
        callDetail.setStartTime(Instant.now().toEpochMilli());
        callDetail.setDetailIndex(callInfo.getCallDetails().size() + 1);
        callDetail.setTransferType(1);
        callDetail.setTransferId(vdnSchedulePo.getVdnId());
        callInfo.getCallDetails().add(callDetail);


        logger.info("vdnSchedule:{} routeType:{} routeValue:{}", vdnSchedulePo.getName(), vdnSchedulePo.getRouteType(), vdnSchedulePo.getRouteValue());
        /**
         * 1:技能组,2:放音,3:ivr,4:坐席,5:外呼
         */
        switch (vdnSchedulePo.getRouteType()) {
            case 1:
                GroupInfo groupInfo = cacheService.getGroupInfo(Long.parseLong(vdnSchedulePo.getRouteValue()));
                if (groupInfo == null) {
                    logger.warn("callId:{} join group is null", callInfo.getCallId());
                    hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), deviceId);
                    return;
                }
                logger.debug("callId:{} join groupId:{}, groupName:{}", callInfo.getCallId(), vdnSchedulePo.getRouteValue(), groupInfo.getName());
                callInfo.setGroupId(groupInfo.getId());
                groupHandler.hander(callInfo, groupInfo, deviceId);
                break;

            case 2:
                /**
                 * 普通放音或者按键导航音 1:按键导航,2:技能组,3:ivr,4:路由字码,5:挂机
                 */
                Playback playback = cacheService.getPlayback(Long.parseLong(vdnSchedulePo.getRouteValue()));
                if (playback == null) {
                    logger.warn("playback is null, callId:{} , device:{}", callInfo.getCallId(), deviceId);
                    hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), deviceId);
                    return;
                }
                //放音类型(1:按键导航,2:技能组,3:ivr,4:路由字码,5:挂机)
                logger.info("playback tyep:{}, value:{}", vdnSchedulePo.getPlayType(), vdnSchedulePo.getPlayValue());
                if (vdnSchedulePo.getPlayType() != 1) {
                    NextCommand nextCommand = null;
                    switch (vdnSchedulePo.getPlayType()) {
                        case 2:
                            nextCommand = new NextCommand(deviceId, NextType.NEXT_GROUP, vdnSchedulePo.getPlayValue().toString());
                            break;
                        case 3:
                            nextCommand = new NextCommand(deviceId, NextType.NEXT_IVR, vdnSchedulePo.getPlayValue().toString());
                            break;
                        case 4:
                            nextCommand = new NextCommand(deviceId, NextType.NEXT_VDN, vdnSchedulePo.getPlayValue().toString());
                            break;
                        case 5:
                            nextCommand = new NextCommand(deviceId, NextType.NEXT_HANGUP, vdnSchedulePo.getPlayValue().toString());
                            break;
                        default:
                            break;
                    }
                    playback(callInfo.getMediaHost(), deviceId, playback.getPlayback());
                    doNextCommand(callInfo, deviceInfo, nextCommand);
                    return;
                }

                /**
                 * 播放按键导航音
                 */
                callInfo.getProcessData().put(DTMF_NAVIGATE_KEY, String.valueOf(vdnSchedulePo.getId()));
                cacheService.addCallInfo(callInfo);
                receiveDtmf(callInfo.getMediaHost(), vdnSchedulePo, deviceId);
                break;

            case 3:
                /**
                 * 转IVR
                 */
                transferIvrHandler.handler(callInfo, deviceInfo, Long.parseLong(vdnSchedulePo.getRouteValue()));
                return;

            case 4:
                /**
                 * 转坐席
                 */
                AgentInfo agentInfo = cacheService.getAgentInfo(vdnSchedulePo.getRouteValue());
                if (agentInfo == null || agentInfo.getAgentState() != AgentState.READY) {
                    hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), deviceId);
                    return;
                }
                transferAgentHandler.hanlder(callInfo, agentInfo, deviceId);
                return;

            case 5:
                /**
                 * 转外呼
                 */
                transferCallHandler.hanlder(callInfo, vdnSchedulePo.getRouteValue(), deviceId);
                return;
            default:
                logger.warn("vdnCode not match callId:{} , case:{}", callInfo.getCallId(), vdnSchedulePo.getRouteType());
                break;
        }
    }

    /**
     * 按键导航收键后，匹配按键并路由
     *
     * @param callInfo
     * @param deviceInfo
     * @param dtmf
     */
    public void matchDtmf(CallInfo callInfo, DeviceInfo deviceInfo, String dtmf) {
        if (StringUtils.isBlank(dtmf)) {
            return;
        }
        Object navigateId = callInfo.getProcessData().get(DTMF_NAVIGATE_KEY);
        if (navigateId == null) {
            return;
        }
        VdnSchedulePo vdnSchedulePo = findSchedule(callInfo.getCompanyId(), navigateId.toString());
        if (vdnSchedulePo == null || vdnSchedulePo.getDtmfList() == null || vdnSchedulePo.getDtmfList().isEmpty()) {
            return;
        }
        VdnDtmf matched = null;
        for (VdnDtmf vdnDtmf : vdnSchedulePo.getDtmfList()) {
            if (dtmf.equals(vdnDtmf.getDtmf())) {
                matched = vdnDtmf;
                break;
            }
        }
        if (matched == null) {
            logger.info("callId:{} dtmf:{} not match, replay navigate", callInfo.getCallId(), dtmf);
            receiveDtmf(callInfo.getMediaHost(), vdnSchedulePo, deviceInfo.getDeviceId());
            return;
        }
        routeDtmf(callInfo, deviceInfo, matched);
        cacheService.addCallInfo(callInfo);
    }

    private void routeDtmf(CallInfo callInfo, DeviceInfo deviceInfo, VdnDtmf vdnDtmf) {
        String deviceId = deviceInfo.getDeviceId();
        logger.info("callId:{} route dtmf:{} routeType:{} routeValue:{}", callInfo.getCallId(), vdnDtmf.getDtmf(), vdnDtmf.getRouteType(), vdnDtmf.getRouteValue());
        /**
         * 1:技能组,2:IVR,3:路由字码,4:坐席分机,5:挂机
         */
        switch (vdnDtmf.getRouteType()) {
            case 1:
                GroupInfo groupInfo = cacheService.getGroupInfo(vdnDtmf.getRouteValue());
                if (groupInfo == null) {
                    logger.warn("callId:{} dtmf join group is null", callInfo.getCallId());
                    hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), deviceId);
                    return;
                }
                callInfo.setGroupId(groupInfo.getId());
                groupHandler.hander(callInfo, groupInfo, deviceId);
                break;

            case 2:
                transferIvrHandler.handler(callInfo, deviceInfo, vdnDtmf.getRouteValue());
                break;

            case 3:
                vdnHandler.hanlder(callInfo, deviceInfo, vdnDtmf.getRouteValue());
                break;

            case 4:
                transferToAgent(callInfo, deviceInfo, vdnDtmf.getRouteValue());
                break;

            case 5:
                hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), deviceId);
                break;

            default:
                logger.warn("callId:{} dtmf routeType:{} not match", callInfo.getCallId(), vdnDtmf.getRouteType());
                break;
        }
    }

    private void transferToAgent(CallInfo callInfo, DeviceInfo deviceInfo, Long ext) {
        Agent agent = agentService.getAgentBySip(String.valueOf(ext));
        if (agent == null) {
            logger.warn("callId:{} dtmf transfer agent sip:{} not found", callInfo.getCallId(), ext);
            hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), deviceInfo.getDeviceId());
            return;
        }
        AgentInfo agentInfo = cacheService.getAgentInfo(agent.getAgentKey());
        if (agentInfo == null || agentInfo.getAgentState() != AgentState.READY) {
            logger.warn("callId:{} dtmf transfer agent:{} not ready", callInfo.getCallId(), agent.getAgentKey());
            hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), deviceInfo.getDeviceId());
            return;
        }
        transferAgentHandler.hanlder(callInfo, agentInfo, deviceInfo.getDeviceId());
    }

    private VdnSchedulePo findSchedule(Long companyId, String navigateId) {
        CompanyInfo companyInfo = cacheService.getCompany(companyId);
        if (companyInfo == null || companyInfo.getVdnCodeMap() == null) {
            return null;
        }
        for (VdnCodePo vdnCodePo : companyInfo.getVdnCodeMap().values()) {
            if (vdnCodePo.getVdnSchedulePoList() == null) {
                continue;
            }
            for (VdnSchedulePo vdnSchedulePo : vdnCodePo.getVdnSchedulePoList()) {
                if (vdnSchedulePo.getId() != null && navigateId.equals(vdnSchedulePo.getId().toString())) {
                    return vdnSchedulePo;
                }
            }
        }
        return null;
    }
}

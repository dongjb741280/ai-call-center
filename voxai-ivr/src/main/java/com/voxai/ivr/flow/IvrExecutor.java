package com.voxai.ivr.flow;

import com.voxai.core.entity.IvrFlow;
import com.voxai.core.entity.IvrWorkflow;
import com.voxai.core.esl.transport.event.EslEvent;
import com.voxai.core.mapper.IvrWorkflowMapper;
import com.voxai.core.vo.IvrCompleteVo;
import com.voxai.ivr.flow.model.IvrFlowDef;
import com.voxai.ivr.flow.model.IvrNode;
import com.voxai.ivr.fs.IvrFsClient;
import com.voxai.ivr.service.IvrFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IVR 执行器：维护通话会话，驱动流程节点（放音/收键/转接），并落轨迹。
 */
@Component
public class IvrExecutor {
    private static final Logger logger = LoggerFactory.getLogger(IvrExecutor.class);

    private final Map<String, IvrSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    private IvrWorkflowMapper ivrWorkflowMapper;

    @Autowired
    private IvrFlowParser ivrFlowParser;

    @Autowired
    private IvrFlowService ivrFlowService;

    @Autowired
    private IvrFsClient ivrFsClient;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ivr.call.url:http://voxai-call:7200/voxai-call}")
    private String callUrl;

    public void start(Long callId, String deviceId, Long ivrId, String mediaHost) {
        IvrWorkflow workflow = ivrWorkflowMapper.selectByPrimaryKey(ivrId);
        if (workflow == null) {
            logger.warn("ivr workflow {} not found, callId:{}", ivrId, callId);
            ivrFsClient.hangup(mediaHost, deviceId);
            return;
        }
        IvrFlowDef def;
        try {
            def = ivrFlowParser.parse(workflow.getContent());
        } catch (Exception e) {
            logger.error("ivr content parse error ivrId:{}, callId:{}", ivrId, callId, e);
            ivrFsClient.hangup(mediaHost, deviceId);
            return;
        }
        Map<String, IvrNode> nodeMap = ivrFlowParser.index(def);
        IvrNode startNode = nodeMap.get(def.getStartNode());
        if (startNode == null) {
            logger.warn("ivr startNode {} not found, callId:{}", def.getStartNode(), callId);
            ivrFsClient.hangup(mediaHost, deviceId);
            return;
        }
        IvrSession session = new IvrSession(callId, deviceId, ivrId, mediaHost, nodeMap, startNode);
        sessions.put(deviceId, session);
        execute(session);
    }

    public void onEvent(String mediaHost, EslEvent event) {
        String deviceId = event.getEventHeaders().get("Unique-ID");
        if (deviceId == null) {
            return;
        }
        IvrSession session = sessions.get(deviceId);
        if (session == null) {
            return;
        }
        String eventName = event.getEventName();
        if ("CHANNEL_EXECUTE_COMPLETE".equals(eventName)) {
            onExecuteComplete(session, event);
        } else if ("CHANNEL_HANGUP_COMPLETE".equals(eventName)) {
            onHangup(session);
        }
    }

    private void onExecuteComplete(IvrSession session, EslEvent event) {
        IvrNode node = session.currentNode;
        if (node == null) {
            return;
        }
        String app = event.getEventHeaders().get("Application");
        if ("playback".equals(app)) {
            advance(session, node.getNext(), null);
        } else if ("play_and_get_digits".equals(app)) {
            String dtmf = event.getEventHeaders().get("variable_SYMWRD_DTMF_RETURN");
            advance(session, matchBranch(node, dtmf), dtmf);
        }
    }

    private void onHangup(IvrSession session) {
        recordNode(session, System.currentTimeMillis(), null);
        sessions.remove(session.deviceId);
        logger.info("ivr hangup callId:{}, deviceId:{}", session.callId, session.deviceId);
    }

    private void advance(IvrSession session, String nextId, String keyPress) {
        recordNode(session, System.currentTimeMillis(), keyPress);
        if (nextId == null) {
            finish(session, "hangup", null);
            return;
        }
        IvrNode next = session.nodeMap.get(nextId);
        if (next == null) {
            logger.warn("ivr node {} not found, callId:{}", nextId, session.callId);
            finish(session, "hangup", null);
            return;
        }
        session.currentNode = next;
        session.nodeStartTime = System.currentTimeMillis();
        execute(session);
    }

    private void execute(IvrSession session) {
        IvrNode node = session.currentNode;
        String type = node.getType();
        if ("play".equals(type)) {
            ivrFsClient.playback(session.mediaHost, session.deviceId, node.getFile());
        } else if ("input".equals(type)) {
            ivrFsClient.playAndGetDigits(session.mediaHost, session.deviceId, node.getFile(),
                    node.getMaxDigits() == null ? 1 : node.getMaxDigits(),
                    node.getTimeout() == null ? 5 : node.getTimeout());
        } else if ("transfer".equals(type)) {
            recordNode(session, System.currentTimeMillis(), null);
            finish(session, node.getTargetType(), node.getTargetValue());
        } else {
            logger.warn("ivr unknown node type:{} callId:{}", type, session.callId);
            finish(session, "hangup", null);
        }
    }

    private void finish(IvrSession session, String transferType, String transferValue) {
        sessions.remove(session.deviceId);
        callback(session, transferType, transferValue);
    }

    private void callback(IvrSession session, String transferType, String transferValue) {
        IvrCompleteVo vo = new IvrCompleteVo();
        vo.setCallId(session.callId);
        vo.setDeviceId(session.deviceId);
        vo.setTransferType(StringUtils.isEmpty(transferType) ? "hangup" : transferType);
        vo.setTransferValue(transferValue);
        try {
            restTemplate.postForEntity(callUrl + "/index/ivr/complete", vo, String.class);
            logger.info("ivr complete callId:{}, transferType:{}, transferValue:{}", session.callId, vo.getTransferType(), transferValue);
        } catch (Exception e) {
            logger.error("ivr callback error callId:{}", session.callId, e);
        }
    }

    private String matchBranch(IvrNode node, String dtmf) {
        if (node.getBranches() != null) {
            for (IvrNode.Branch branch : node.getBranches()) {
                if (branch.getKey() != null && branch.getKey().equals(dtmf)) {
                    return branch.getNext();
                }
            }
        }
        return node.getDefaultNext();
    }

    private void recordNode(IvrSession session, long endTime, String keyPress) {
        IvrFlow flow = new IvrFlow();
        flow.setCallId(session.callId);
        flow.setIvrId(session.ivrId);
        flow.setCts(session.nodeStartTime);
        flow.setNodeId(session.currentNode.getId());
        flow.setNodeType(session.currentNode.getType());
        flow.setKeyPress(keyPress);
        flow.setStartTime(session.nodeStartTime);
        flow.setEndTime(endTime);
        ivrFlowService.save(flow);
    }

    private static class IvrSession {
        final Long callId;
        final String deviceId;
        final Long ivrId;
        final String mediaHost;
        final Map<String, IvrNode> nodeMap;
        IvrNode currentNode;
        long nodeStartTime;

        IvrSession(Long callId, String deviceId, Long ivrId, String mediaHost, Map<String, IvrNode> nodeMap, IvrNode startNode) {
            this.callId = callId;
            this.deviceId = deviceId;
            this.ivrId = ivrId;
            this.mediaHost = mediaHost;
            this.nodeMap = nodeMap;
            this.currentNode = startNode;
            this.nodeStartTime = System.currentTimeMillis();
        }
    }
}

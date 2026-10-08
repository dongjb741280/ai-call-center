package com.voxai.ivr.flow;

import com.alibaba.fastjson.JSON;
import com.voxai.ivr.flow.model.IvrFlowDef;
import com.voxai.ivr.flow.model.IvrNode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 解析 IVR 流程 JSON。
 */
@Component
public class IvrFlowParser {

    public IvrFlowDef parse(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new IllegalArgumentException("ivr content is empty");
        }
        IvrFlowDef def = JSON.parseObject(content, IvrFlowDef.class);
        if (StringUtils.isEmpty(def.getStartNode()) || CollectionUtils.isEmpty(def.getNodes())) {
            throw new IllegalArgumentException("ivr content invalid: missing startNode or nodes");
        }
        return def;
    }

    /**
     * 按 id 建立节点索引。
     */
    public Map<String, IvrNode> index(IvrFlowDef def) {
        Map<String, IvrNode> map = new HashMap<>();
        for (IvrNode node : def.getNodes()) {
            map.put(node.getId(), node);
        }
        return map;
    }
}

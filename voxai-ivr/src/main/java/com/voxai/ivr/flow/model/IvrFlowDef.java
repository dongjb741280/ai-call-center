package com.voxai.ivr.flow.model;

import java.util.List;

/**
 * IVR 流程定义（cc_ivr_workflow.content 的 JSON 结构）。
 */
public class IvrFlowDef {

    private String startNode;

    private List<IvrNode> nodes;

    public String getStartNode() {
        return startNode;
    }

    public void setStartNode(String startNode) {
        this.startNode = startNode;
    }

    public List<IvrNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<IvrNode> nodes) {
        this.nodes = nodes;
    }
}

package com.voxai.ivr.flow.model;

import java.util.List;

/**
 * IVR 流程节点。
 *
 * type 取值：
 * - play：放音，file 为语音文件，放音结束转 next
 * - input：放音并收键，file 为提示音，branches 按键分支，defaultNext 超时/无效按键去向
 * - transfer：结束节点，targetType ∈ {group, vdn, agent, hangup}，targetValue 为目标值
 */
public class IvrNode {

    private String id;

    private String type;

    private String file;

    private String next;

    private Integer maxDigits;

    private Integer timeout;

    private List<Branch> branches;

    private String defaultNext;

    private String targetType;

    private String targetValue;

    public static class Branch {
        private String key;
        private String next;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Integer getMaxDigits() {
        return maxDigits;
    }

    public void setMaxDigits(Integer maxDigits) {
        this.maxDigits = maxDigits;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public String getDefaultNext() {
        return defaultNext;
    }

    public void setDefaultNext(String defaultNext) {
        this.defaultNext = defaultNext;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }
}

package com.voice9.core.entity;

import java.io.Serializable;

public class AiEngine implements Serializable {
    private Long id;
    private Long cts;
    private Long uts;
    private Long companyId;
    private String name;
    private String mrcpProfile;
    private Integer aiType;
    private String aiProduct;
    private String voice;
    private String grammar;
    private String aiParams;
    private String ttsTemplate;
    private Integer status;

    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCts() { return cts; }
    public void setCts(Long cts) { this.cts = cts; }
    public Long getUts() { return uts; }
    public void setUts(Long uts) { this.uts = uts; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMrcpProfile() { return mrcpProfile; }
    public void setMrcpProfile(String mrcpProfile) { this.mrcpProfile = mrcpProfile; }
    public Integer getAiType() { return aiType; }
    public void setAiType(Integer aiType) { this.aiType = aiType; }
    public String getAiProduct() { return aiProduct; }
    public void setAiProduct(String aiProduct) { this.aiProduct = aiProduct; }
    public String getVoice() { return voice; }
    public void setVoice(String voice) { this.voice = voice; }
    public String getGrammar() { return grammar; }
    public void setGrammar(String grammar) { this.grammar = grammar; }
    public String getAiParams() { return aiParams; }
    public void setAiParams(String aiParams) { this.aiParams = aiParams; }
    public String getTtsTemplate() { return ttsTemplate; }
    public void setTtsTemplate(String ttsTemplate) { this.ttsTemplate = ttsTemplate; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

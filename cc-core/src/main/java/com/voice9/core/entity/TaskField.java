package com.voice9.core.entity;

import java.io.Serializable;

public class TaskField implements Serializable {
    private Long id;
    private Long cts;
    private Long uts;
    private Long companyId;
    private Long sourceId;
    private String name;
    private Integer isPhone;
    private String fieldType;
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
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.trim(); }
    public Integer getIsPhone() { return isPhone; }
    public void setIsPhone(Integer isPhone) { this.isPhone = isPhone; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

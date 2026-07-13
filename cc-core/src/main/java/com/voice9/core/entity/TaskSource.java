package com.voice9.core.entity;

import java.io.Serializable;

public class TaskSource implements Serializable {
    private Long id;
    private Long cts;
    private Long uts;
    private Long companyId;
    private String name;
    private Integer status;
    private Integer fieldCount;
    private Integer listCount;
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
    public void setName(String name) { this.name = name == null ? null : name.trim(); }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getFieldCount() { return fieldCount; }
    public void setFieldCount(Integer fieldCount) { this.fieldCount = fieldCount; }
    public Integer getListCount() { return listCount; }
    public void setListCount(Integer listCount) { this.listCount = listCount; }
}

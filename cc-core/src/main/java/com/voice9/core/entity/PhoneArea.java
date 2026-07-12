package com.voice9.core.entity;

import java.io.Serializable;

/**
 * 号码归属地表
 */
public class PhoneArea implements Serializable {
    private Integer id;
    private Long cts;
    private Long uts;
    private String numPrefix;
    private String operator;
    private Integer numType;
    private String numCode;
    private String numProvince;
    private String numCity;
    private Integer status;

    private static final long serialVersionUID = 1L;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Long getCts() { return cts; }
    public void setCts(Long cts) { this.cts = cts; }
    public Long getUts() { return uts; }
    public void setUts(Long uts) { this.uts = uts; }
    public String getNumPrefix() { return numPrefix; }
    public void setNumPrefix(String numPrefix) { this.numPrefix = numPrefix; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Integer getNumType() { return numType; }
    public void setNumType(Integer numType) { this.numType = numType; }
    public String getNumCode() { return numCode; }
    public void setNumCode(String numCode) { this.numCode = numCode; }
    public String getNumProvince() { return numProvince; }
    public void setNumProvince(String numProvince) { this.numProvince = numProvince; }
    public String getNumCity() { return numCity; }
    public void setNumCity(String numCity) { this.numCity = numCity; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

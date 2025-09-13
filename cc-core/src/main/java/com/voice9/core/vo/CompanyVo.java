package com.voice9.core.vo;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by dongjb on 2025/09/03
 */
public class CompanyVo {

    @NotBlank(message = "企业名称不能为空")
    @Size(min = 2, max = 16, message = "企业名称必须在2,16字符")
    private String name;

    /**
     * 父企业ID
     */
    private String idPath;

    /**
     * 父企业
     */
    private Long pid;

    /**
     * 简称
     */
    @NotNull(message = "企业缩写不能为空")
    @Size(min = 2, max = 8, message = "企业缩写必须在2,8字符")
    private String companyCode;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 电话
     */
    private String phone;

    /**
     * 金额
     */
    private Long balance;

    /**
     * 1:呼出计费,2:呼入计费,3:双向计费
     */
    private Integer billType;

    /**
     * 0:预付费;1:后付费
     */
    private Integer payType;

    /**
     * 状态 0:禁用,1:免费,2:试用,3:付费
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 隐藏客户号码(0:不隐藏;1:隐藏)
     */
    @NotNull(message = "隐藏客户号码设置不能为空")
    @Range(min = 0, max = 1, message = "隐藏号码设置错误")
    private Integer hiddenCustomer;

    /**
     * IVR通道数
     */
    @NotNull(message = "IVR通道数不能为空")
    @Range(min = 1, max = 1000000, message = "IVR通道数设置错误")
    private Integer ivrLimit;

    /**
     * 开通坐席
     */
    @NotNull(message = "开通坐席数不能为空")
    @Range(min = 1, max = 1000000, message = "开通坐席数设置错误")
    private Integer agentLimit;

    /**
     * 开通技能组
     */
    @NotNull(message = "开通技能组数不能为空")
    @Range(min = 1, max = 2000, message = "开通技能组数设置错误")
    private Integer groupLimit;

    /**
     * 单技能组中坐席上限
     */
    @NotNull(message = "单技能组中坐席上限数不能为空")
    @Range(min = 0, max = 1000, message = "单技能组中坐席上限数设置错误")
    private Integer groupAgentLimit;

    /**
     * 时区概念(默认是GTM+8)
     */
    @Range(min = -12, max = 12, message = "时区设置错误")
    private Integer gmt;

    /**
     * 坐席密码等级(1:不限制 2:数字和字母 3:大小写字母和数字组合)
     */
    @Range(min = 1, max = 3, message = "坐席密码等级设置错误")
    private Integer secretType;

    /**
     * 验证秘钥
     */
    private String secretKey;

    /**
     * 企业坐席数量（实时/缓存）
     */
    private Integer agentSize;

    /**
     * 外呼并发限制
     */
    @Range(min = 0, max = 10000, message = "外呼并发限制设置错误")
    private Integer outboundLimit;

    /**
     * 到期时间（时间戳）
     */
    private Long expireTime;

    /**
     * 录音保留x个月
     */
    @Range(min = 0, max = 120, message = "录音保留月数设置错误")
    private Integer recordStorage;

    /**
     * 话单回调通知
     */
    private String notifyUrl;

    /**
     * 外显号码
     */
    private String displayNumber;

    /**
     * 录音OSS产品
     */
    private String ossProduct;

    /**
     * 录音文件类型
     */
    private String recordFileType;

    /**
     * 录音文件加密
     */
    private String recordEncrypt;

    /**
     * 话单加密推送
     */
    private String cdrEncryptPush;

    /**
     * 扩展1
     */
    private String ext1;

    /**
     * 扩展2
     */
    private String ext2;

    /**
     * 扩展3
     */
    private String ext3;

    /**
     * 扩展4
     */
    private String ext4;

    /**
     * 扩展5
     */
    private String ext5;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getHiddenCustomer() {
        return hiddenCustomer;
    }

    public void setHiddenCustomer(Integer hiddenCustomer) {
        this.hiddenCustomer = hiddenCustomer;
    }

    public Integer getIvrLimit() {
        return ivrLimit;
    }

    public void setIvrLimit(Integer ivrLimit) {
        this.ivrLimit = ivrLimit;
    }

    public Integer getAgentLimit() {
        return agentLimit;
    }

    public void setAgentLimit(Integer agentLimit) {
        this.agentLimit = agentLimit;
    }

    public Integer getGroupLimit() {
        return groupLimit;
    }

    public void setGroupLimit(Integer groupLimit) {
        this.groupLimit = groupLimit;
    }

    public Integer getGroupAgentLimit() {
        return groupAgentLimit;
    }

    public void setGroupAgentLimit(Integer groupAgentLimit) {
        this.groupAgentLimit = groupAgentLimit;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public Integer getGmt() {
        return gmt;
    }

    public void setGmt(Integer gmt) {
        this.gmt = gmt;
    }

    public Integer getSecretType() {
        return secretType;
    }

    public void setSecretType(Integer secretType) {
        this.secretType = secretType;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getAgentSize() {
        return agentSize;
    }

    public void setAgentSize(Integer agentSize) {
        this.agentSize = agentSize;
    }

    public Integer getOutboundLimit() {
        return outboundLimit;
    }

    public void setOutboundLimit(Integer outboundLimit) {
        this.outboundLimit = outboundLimit;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getRecordStorage() {
        return recordStorage;
    }

    public void setRecordStorage(Integer recordStorage) {
        this.recordStorage = recordStorage;
    }

    public String getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(String displayNumber) {
        this.displayNumber = displayNumber;
    }

    public String getOssProduct() {
        return ossProduct;
    }

    public void setOssProduct(String ossProduct) {
        this.ossProduct = ossProduct;
    }

    public String getRecordFileType() {
        return recordFileType;
    }

    public void setRecordFileType(String recordFileType) {
        this.recordFileType = recordFileType;
    }

    public String getRecordEncrypt() {
        return recordEncrypt;
    }

    public void setRecordEncrypt(String recordEncrypt) {
        this.recordEncrypt = recordEncrypt;
    }

    public String getCdrEncryptPush() {
        return cdrEncryptPush;
    }

    public void setCdrEncryptPush(String cdrEncryptPush) {
        this.cdrEncryptPush = cdrEncryptPush;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getExt4() {
        return ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getExt5() {
        return ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "CompanyVo{" +
                "name='" + name + '\'' +
                ", idPath='" + idPath + '\'' +
                ", pid=" + pid +
                ", companyCode='" + companyCode + '\'' +
                ", contact='" + contact + '\'' +
                ", phone='" + phone + '\'' +
                ", balance=" + balance +
                ", billType=" + billType +
                ", payType=" + payType +
                ", status=" + status +
                ", hiddenCustomer=" + hiddenCustomer +
                ", ivrLimit=" + ivrLimit +
                ", agentLimit=" + agentLimit +
                ", groupLimit=" + groupLimit +
                ", groupAgentLimit=" + groupAgentLimit +
                ", gmt=" + gmt +
                ", secretType=" + secretType +
                ", secretKey='" + secretKey + '\'' +
                ", agentSize=" + agentSize +
                ", outboundLimit=" + outboundLimit +
                ", expireTime=" + expireTime +
                ", recordStorage=" + recordStorage +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", displayNumber='" + displayNumber + '\'' +
                ", ossProduct='" + ossProduct + '\'' +
                ", recordFileType='" + recordFileType + '\'' +
                ", recordEncrypt='" + recordEncrypt + '\'' +
                ", cdrEncryptPush='" + cdrEncryptPush + '\'' +
                ", ext1='" + ext1 + '\'' +
                ", ext2='" + ext2 + '\'' +
                ", ext3='" + ext3 + '\'' +
                ", ext4='" + ext4 + '\'' +
                ", ext5='" + ext5 + '\'' +
                '}';
    }
}

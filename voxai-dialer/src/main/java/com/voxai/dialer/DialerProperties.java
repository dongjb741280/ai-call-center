package com.voxai.dialer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.net.URI;

@ConfigurationProperties(prefix="dialer")
public class DialerProperties {
    private boolean enabled;
    private Long companyId;
    private String adminBaseUrl="http://127.0.0.1:7100/voxai-admin";
    private String callBaseUrl="http://127.0.0.1:7200/voxai-call";
    private String username;
    private String password;
    private String callToken;
    private int leaseSeconds=120;
    private int batchSize=5;
    private int connectTimeoutMillis=1500;
    private int readTimeoutMillis=5000;
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean value) { enabled=value; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long value) { companyId=value; }
    public String getAdminBaseUrl() { return adminBaseUrl; }
    public void setAdminBaseUrl(String value) { adminBaseUrl=value; }
    public String getCallBaseUrl() { return callBaseUrl; }
    public void setCallBaseUrl(String value) { callBaseUrl=value; }
    public String getUsername() { return username; }
    public void setUsername(String value) { username=value; }
    public String getPassword() { return password; }
    public void setPassword(String value) { password=value; }
    public String getCallToken() { return callToken; }
    public void setCallToken(String value) { callToken=value; }
    public int getLeaseSeconds() { return leaseSeconds; }
    public void setLeaseSeconds(int value) { leaseSeconds=value; }
    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int value) { batchSize=value; }
    public int getConnectTimeoutMillis() { return connectTimeoutMillis; }
    public void setConnectTimeoutMillis(int value) { connectTimeoutMillis=value; }
    public int getReadTimeoutMillis() { return readTimeoutMillis; }
    public void setReadTimeoutMillis(int value) { readTimeoutMillis=value; }
    @javax.annotation.PostConstruct
    public void validate() {
        if (!enabled) { return; }
        if (companyId==null || companyId<=0 || username==null || username.trim().isEmpty() || username.contains(":")
                || password==null || password.isEmpty() || callToken==null || callToken.length()<32
                || leaseSeconds<30 || leaseSeconds>300 || batchSize<1 || batchSize>100
                || connectTimeoutMillis<100 || connectTimeoutMillis>10000 || readTimeoutMillis<100 || readTimeoutMillis>30000) {
            throw new IllegalArgumentException("Missing or invalid dialer configuration");
        }
        validateUrl(adminBaseUrl); validateUrl(callBaseUrl);
    }
    private void validateUrl(String value) {
        URI uri=URI.create(value);
        if (!("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) || uri.getHost()==null
                || uri.getUserInfo()!=null || uri.getQuery()!=null || uri.getFragment()!=null || value.endsWith("/")) {
            throw new IllegalArgumentException("Invalid dialer service URL");
        }
    }
}

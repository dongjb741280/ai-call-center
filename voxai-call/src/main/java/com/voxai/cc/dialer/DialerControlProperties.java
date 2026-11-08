package com.voxai.cc.dialer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix="dialer.control")
public class DialerControlProperties {
    private boolean enabled;
    private Map<Long,String> tokens = new HashMap<>();
    private int companyConcurrency = 20;
    private int maxCallSeconds = 3600;
    private double maxPredictiveRatio = 2;
    private double maxAbandonRate = 0.03;
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }
    public Map<Long,String> getTokens() { return tokens; }
    public void setTokens(Map<Long,String> value) { tokens = value; }
    public int getCompanyConcurrency() { return companyConcurrency; }
    public void setCompanyConcurrency(int value) { companyConcurrency = value; }
    public int getMaxCallSeconds() { return maxCallSeconds; }
    public void setMaxCallSeconds(int value) { maxCallSeconds = value; }
    public double getMaxPredictiveRatio() { return maxPredictiveRatio; }
    public void setMaxPredictiveRatio(double value) { maxPredictiveRatio = value; }
    public double getMaxAbandonRate() { return maxAbandonRate; }
    public void setMaxAbandonRate(double value) { maxAbandonRate = value; }

    @javax.annotation.PostConstruct
    public void validate() {
        if (!enabled) { return; }
        if (tokens == null || tokens.isEmpty() || tokens.entrySet().stream().anyMatch(e -> e.getKey() <= 0 || e.getValue() == null || e.getValue().length() < 32)
                || companyConcurrency < 1 || companyConcurrency > 10000 || maxCallSeconds < 60 || maxCallSeconds > 86400
                || !Double.isFinite(maxPredictiveRatio) || maxPredictiveRatio < 1 || maxPredictiveRatio > 3
                || !Double.isFinite(maxAbandonRate) || maxAbandonRate < 0 || maxAbandonRate > 0.1) {
            throw new IllegalArgumentException("Invalid dialer.control configuration");
        }
    }
}

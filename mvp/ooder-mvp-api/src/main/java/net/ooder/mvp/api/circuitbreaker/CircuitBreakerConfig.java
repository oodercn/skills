package net.ooder.mvp.api.circuitbreaker;

import java.io.Serializable;

public class CircuitBreakerConfig implements Serializable {
    private boolean enabled = true;
    private int failureThreshold = 5;
    private int slowCallThreshold = 3;
    private long slowCallDurationThreshold = 5000;
    private long openDuration = 30000;
    private int halfOpenMaxCalls = 3;
    
    public CircuitBreakerConfig() {}
    
    public CircuitBreakerConfig(boolean enabled, int failureThreshold, long openDuration) {
        this.enabled = enabled;
        this.failureThreshold = failureThreshold;
        this.openDuration = openDuration;
    }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getFailureThreshold() { return failureThreshold; }
    public void setFailureThreshold(int failureThreshold) { this.failureThreshold = failureThreshold; }
    public int getSlowCallThreshold() { return slowCallThreshold; }
    public void setSlowCallThreshold(int slowCallThreshold) { this.slowCallThreshold = slowCallThreshold; }
    public long getSlowCallDurationThreshold() { return slowCallDurationThreshold; }
    public void setSlowCallDurationThreshold(long slowCallDurationThreshold) { this.slowCallDurationThreshold = slowCallDurationThreshold; }
    public long getOpenDuration() { return openDuration; }
    public void setOpenDuration(long openDuration) { this.openDuration = openDuration; }
    public int getHalfOpenMaxCalls() { return halfOpenMaxCalls; }
    public void setHalfOpenMaxCalls(int halfOpenMaxCalls) { this.halfOpenMaxCalls = halfOpenMaxCalls; }
}

package net.ooder.bpm.designer.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheConfig {
    
    private boolean enabled = true;
    private Duration defaultTtl = Duration.ofMinutes(30);
    private int maxSize = 1000;
    private Duration cleanupInterval = Duration.ofMinutes(5);
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Duration getDefaultTtl() {
        return defaultTtl;
    }
    
    public void setDefaultTtl(Duration defaultTtl) {
        this.defaultTtl = defaultTtl;
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public Duration getCleanupInterval() {
        return cleanupInterval;
    }
    
    public void setCleanupInterval(Duration cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }
}

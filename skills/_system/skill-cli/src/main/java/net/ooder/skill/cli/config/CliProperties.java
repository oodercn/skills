package net.ooder.skill.cli.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "skill.cli")
public class CliProperties {
    
    private boolean enabled = true;
    private String outputFormat = "text";
    private boolean autoRefresh = true;
    private int refreshInterval = 3000;
    private Map<String, String> aliases = new HashMap<>();
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getOutputFormat() {
        return outputFormat;
    }
    
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
    
    public boolean isAutoRefresh() {
        return autoRefresh;
    }
    
    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }
    
    public int getRefreshInterval() {
        return refreshInterval;
    }
    
    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
    
    public Map<String, String> getAliases() {
        return aliases;
    }
    
    public void setAliases(Map<String, String> aliases) {
        this.aliases = aliases;
    }
}

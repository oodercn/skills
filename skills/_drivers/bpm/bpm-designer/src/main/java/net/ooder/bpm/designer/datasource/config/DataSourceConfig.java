package net.ooder.bpm.designer.datasource.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "datasource.bpm")
public class DataSourceConfig {
    
    private boolean useRealData = false;
    private String bpmServerUrl = "http://localhost:8084";
    private String capabilityServiceUrl = "http://localhost:8085";
    private String formServiceUrl = "http://localhost:8086";
    private String sceneServiceUrl = "http://localhost:8087";
    private long cacheTtl = 300000;
    
    public boolean isUseRealData() {
        return useRealData;
    }
    
    public void setUseRealData(boolean useRealData) {
        this.useRealData = useRealData;
    }
    
    public String getBpmServerUrl() {
        return bpmServerUrl;
    }
    
    public void setBpmServerUrl(String bpmServerUrl) {
        this.bpmServerUrl = bpmServerUrl;
    }
    
    public String getCapabilityServiceUrl() {
        return capabilityServiceUrl;
    }
    
    public void setCapabilityServiceUrl(String capabilityServiceUrl) {
        this.capabilityServiceUrl = capabilityServiceUrl;
    }
    
    public String getFormServiceUrl() {
        return formServiceUrl;
    }
    
    public void setFormServiceUrl(String formServiceUrl) {
        this.formServiceUrl = formServiceUrl;
    }
    
    public String getSceneServiceUrl() {
        return sceneServiceUrl;
    }
    
    public void setSceneServiceUrl(String sceneServiceUrl) {
        this.sceneServiceUrl = sceneServiceUrl;
    }
    
    public long getCacheTtl() {
        return cacheTtl;
    }
    
    public void setCacheTtl(long cacheTtl) {
        this.cacheTtl = cacheTtl;
    }
}

package net.ooder.skill.llm.config.dto;

import java.io.Serializable;
import java.util.Map;

public class ExtendedConfigDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Map<String, String> customHeaders;
    private String proxyUrl;
    private Integer connectionTimeout;
    private Integer readTimeout;
    private Boolean enableCaching;
    private Integer cacheTtl;
    private Boolean enableLogging;
    private String logLevel;
    
    public ExtendedConfigDTO() {}
    
    public Map<String, String> getCustomHeaders() {
        return customHeaders;
    }
    
    public void setCustomHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
    }
    
    public String getProxyUrl() {
        return proxyUrl;
    }
    
    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }
    
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public Integer getReadTimeout() {
        return readTimeout;
    }
    
    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public Boolean getEnableCaching() {
        return enableCaching;
    }
    
    public void setEnableCaching(Boolean enableCaching) {
        this.enableCaching = enableCaching;
    }
    
    public Integer getCacheTtl() {
        return cacheTtl;
    }
    
    public void setCacheTtl(Integer cacheTtl) {
        this.cacheTtl = cacheTtl;
    }
    
    public Boolean getEnableLogging() {
        return enableLogging;
    }
    
    public void setEnableLogging(Boolean enableLogging) {
        this.enableLogging = enableLogging;
    }
    
    public String getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
}

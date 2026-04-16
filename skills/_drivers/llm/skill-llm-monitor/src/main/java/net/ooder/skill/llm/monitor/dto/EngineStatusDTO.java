package net.ooder.skill.llm.monitor.dto;

import java.util.Map;

public class EngineStatusDTO {
    
    private boolean sdkAvailable;
    private boolean monitorEnabled;
    private String dataSource;
    private Map<String, Object> extensions;

    public boolean isSdkAvailable() { return sdkAvailable; }
    public void setSdkAvailable(boolean sdkAvailable) { this.sdkAvailable = sdkAvailable; }
    public boolean isMonitorEnabled() { return monitorEnabled; }
    public void setMonitorEnabled(boolean monitorEnabled) { this.monitorEnabled = monitorEnabled; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public Map<String, Object> getExtensions() { return extensions; }
    public void setExtensions(Map<String, Object> extensions) { this.extensions = extensions; }
}

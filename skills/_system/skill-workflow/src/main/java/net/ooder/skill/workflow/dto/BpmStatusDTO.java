package net.ooder.skill.workflow.dto;

public class BpmStatusDTO {
    
    private boolean available;
    private String systemCode;

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getSystemCode() { return systemCode; }
    public void setSystemCode(String systemCode) { this.systemCode = systemCode; }
}

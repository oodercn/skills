package net.ooder.skill.org.dto;

public class DeviceDTO {
    private String id;
    private String name;
    private String type;
    private String lastActive;
    private boolean current;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getLastActive() { return lastActive; }
    public void setLastActive(String lastActive) { this.lastActive = lastActive; }
    
    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }
}

package net.ooder.bpm.designer.model;

import java.util.HashMap;
import java.util.Map;

public class ActivityDef {
    private String activityDefId;
    private String name;
    private String description;
    private String position = "NORMAL";
    private String activityType = "TASK";
    private String activityCategory = "HUMAN";
    private String implementation = "IMPL_NO";
    private String execClass;
    private Map<String, Object> positionCoord = new HashMap<>();
    private Map<String, Object> timing = new HashMap<>();
    private Map<String, Object> routing = new HashMap<>();
    private Map<String, Object> right = new HashMap<>();
    private Map<String, Object> subFlow = new HashMap<>();
    private Map<String, Object> device = new HashMap<>();
    private Map<String, Object> service = new HashMap<>();
    private Map<String, Object> event = new HashMap<>();
    private Map<String, Object> agentConfig;
    private Map<String, Object> sceneConfig;
    private Map<String, Object> extendedAttributes = new HashMap<>();

    public ActivityDef() {
        this.activityDefId = "act_" + System.currentTimeMillis();
        this.positionCoord.put("x", 0);
        this.positionCoord.put("y", 0);
        this.right.put("moveSponsorTo", "SPONSOR");
    }

    public String getActivityDefId() { return activityDefId; }
    public void setActivityDefId(String activityDefId) { this.activityDefId = activityDefId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    
    public String getActivityCategory() { return activityCategory; }
    public void setActivityCategory(String activityCategory) { this.activityCategory = activityCategory; }
    
    public String getImplementation() { return implementation; }
    public void setImplementation(String implementation) { this.implementation = implementation; }
    
    public String getExecClass() { return execClass; }
    public void setExecClass(String execClass) { this.execClass = execClass; }
    
    public Map<String, Object> getPositionCoord() { return positionCoord; }
    public void setPositionCoord(Map<String, Object> positionCoord) { this.positionCoord = positionCoord; }
    
    public Map<String, Object> getTiming() { return timing; }
    public void setTiming(Map<String, Object> timing) { this.timing = timing; }
    
    public Map<String, Object> getRouting() { return routing; }
    public void setRouting(Map<String, Object> routing) { this.routing = routing; }
    
    public Map<String, Object> getRight() { return right; }
    public void setRight(Map<String, Object> right) { this.right = right; }
    
    public Map<String, Object> getSubFlow() { return subFlow; }
    public void setSubFlow(Map<String, Object> subFlow) { this.subFlow = subFlow; }
    
    public Map<String, Object> getDevice() { return device; }
    public void setDevice(Map<String, Object> device) { this.device = device; }
    
    public Map<String, Object> getService() { return service; }
    public void setService(Map<String, Object> service) { this.service = service; }
    
    public Map<String, Object> getEvent() { return event; }
    public void setEvent(Map<String, Object> event) { this.event = event; }
    
    public Map<String, Object> getAgentConfig() { return agentConfig; }
    public void setAgentConfig(Map<String, Object> agentConfig) { this.agentConfig = agentConfig; }
    
    public Map<String, Object> getSceneConfig() { return sceneConfig; }
    public void setSceneConfig(Map<String, Object> sceneConfig) { this.sceneConfig = sceneConfig; }
    
    public Map<String, Object> getExtendedAttributes() { return extendedAttributes; }
    public void setExtendedAttributes(Map<String, Object> extendedAttributes) { this.extendedAttributes = extendedAttributes; }
}

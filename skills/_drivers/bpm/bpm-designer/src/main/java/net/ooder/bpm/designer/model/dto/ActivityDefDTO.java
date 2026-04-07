package net.ooder.bpm.designer.model.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;

public class ActivityDefDTO {
    
    @NotBlank(message = "活动ID不能为空")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]*$", message = "活动ID必须以字母开头，只能包含字母、数字、下划线和连字符")
    private String activityDefId;
    
    @NotBlank(message = "活动名称不能为空")
    @Size(min = 1, max = 100, message = "活动名称长度必须在1-100之间")
    private String name;
    
    @Size(max = 500, message = "活动描述长度不能超过500")
    private String description;
    
    @NotBlank(message = "活动位置不能为空")
    private String position;
    
    @NotBlank(message = "活动类型不能为空")
    private String activityType;
    
    @NotBlank(message = "活动分类不能为空")
    private String activityCategory;
    
    @NotBlank(message = "实现方式不能为空")
    private String implementation;
    
    private String execClass;
    
    private Map<String, Object> positionCoord;
    
    private Map<String, Object> timing;
    
    private Map<String, Object> routing;
    
    private Map<String, Object> right;
    
    private Map<String, Object> device;
    
    private Map<String, Object> service;
    
    private Map<String, Object> event;
    
    private Map<String, Object> subflow;
    
    private Map<String, Object> tool;
    
    private Map<String, Object> agentConfig;
    
    private Map<String, Object> sceneConfig;
    
    private Map<String, Object> extendedAttributes;

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
    
    public Map<String, Object> getDevice() { return device; }
    public void setDevice(Map<String, Object> device) { this.device = device; }
    
    public Map<String, Object> getService() { return service; }
    public void setService(Map<String, Object> service) { this.service = service; }
    
    public Map<String, Object> getEvent() { return event; }
    public void setEvent(Map<String, Object> event) { this.event = event; }
    
    public Map<String, Object> getSubflow() { return subflow; }
    public void setSubflow(Map<String, Object> subflow) { this.subflow = subflow; }
    
    public Map<String, Object> getTool() { return tool; }
    public void setTool(Map<String, Object> tool) { this.tool = tool; }
    
    public Map<String, Object> getAgentConfig() { return agentConfig; }
    public void setAgentConfig(Map<String, Object> agentConfig) { this.agentConfig = agentConfig; }
    
    public Map<String, Object> getSceneConfig() { return sceneConfig; }
    public void setSceneConfig(Map<String, Object> sceneConfig) { this.sceneConfig = sceneConfig; }
    
    public Map<String, Object> getExtendedAttributes() { return extendedAttributes; }
    public void setExtendedAttributes(Map<String, Object> extendedAttributes) { this.extendedAttributes = extendedAttributes; }
}

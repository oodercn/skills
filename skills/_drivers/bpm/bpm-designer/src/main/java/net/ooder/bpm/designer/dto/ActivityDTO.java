package net.ooder.bpm.designer.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.ooder.bpm.designer.dto.enums.ActivityCategory;
import net.ooder.bpm.designer.dto.enums.ActivityPosition;
import net.ooder.bpm.designer.dto.enums.ActivityType;
import net.ooder.bpm.designer.dto.sub.*;

import java.util.Map;

/**
 * 活动定义DTO - 使用枚举和子对象替代Map
 */
public class ActivityDTO {

    @JSONField(name = "activityDefId")
    private String activityDefId;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "description")
    private String description;

    @JSONField(name = "position")
    private ActivityPosition position;

    @JSONField(name = "activityType")
    private ActivityType activityType;

    @JSONField(name = "activityCategory")
    private ActivityCategory activityCategory;

    @JSONField(name = "implementation")
    private String implementation;

    @JSONField(name = "execClass")
    private String execClass;

    @JSONField(name = "positionCoord")
    @JsonProperty("positionCoord")
    private PositionCoordDTO positionCoord;

    @JSONField(name = "timing")
    private TimingDTO timing;

    @JSONField(name = "routing")
    private RoutingDTO routing;

    @JSONField(name = "right")
    private RightDTO right;

    @JSONField(name = "subFlow")
    private SubFlowDTO subFlow;

    @JSONField(name = "device")
    private DeviceDTO device;

    @JSONField(name = "service")
    private ServiceDTO service;

    @JSONField(name = "event")
    private EventDTO event;

    @JSONField(name = "agentConfig")
    private AgentConfigDTO agentConfig;

    @JSONField(name = "sceneConfig")
    private SceneConfigDTO sceneConfig;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    // Getters and Setters
    public String getActivityDefId() {
        return activityDefId;
    }

    public void setActivityDefId(String activityDefId) {
        this.activityDefId = activityDefId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivityPosition getPosition() {
        return position;
    }

    public void setPosition(ActivityPosition position) {
        this.position = position;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public ActivityCategory getActivityCategory() {
        return activityCategory;
    }

    public void setActivityCategory(ActivityCategory activityCategory) {
        this.activityCategory = activityCategory;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public String getExecClass() {
        return execClass;
    }

    public void setExecClass(String execClass) {
        this.execClass = execClass;
    }

    public PositionCoordDTO getPositionCoord() {
        return positionCoord;
    }

    public void setPositionCoord(PositionCoordDTO positionCoord) {
        this.positionCoord = positionCoord;
    }

    public TimingDTO getTiming() {
        return timing;
    }

    public void setTiming(TimingDTO timing) {
        this.timing = timing;
    }

    public RoutingDTO getRouting() {
        return routing;
    }

    public void setRouting(RoutingDTO routing) {
        this.routing = routing;
    }

    public RightDTO getRight() {
        return right;
    }

    public void setRight(RightDTO right) {
        this.right = right;
    }

    public SubFlowDTO getSubFlow() {
        return subFlow;
    }

    public void setSubFlow(SubFlowDTO subFlow) {
        this.subFlow = subFlow;
    }

    public DeviceDTO getDevice() {
        return device;
    }

    public void setDevice(DeviceDTO device) {
        this.device = device;
    }

    public ServiceDTO getService() {
        return service;
    }

    public void setService(ServiceDTO service) {
        this.service = service;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public AgentConfigDTO getAgentConfig() {
        return agentConfig;
    }

    public void setAgentConfig(AgentConfigDTO agentConfig) {
        this.agentConfig = agentConfig;
    }

    public SceneConfigDTO getSceneConfig() {
        return sceneConfig;
    }

    public void setSceneConfig(SceneConfigDTO sceneConfig) {
        this.sceneConfig = sceneConfig;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }

    @Override
    public String toString() {
        return "ActivityDTO{" +
                "activityDefId='" + activityDefId + '\'' +
                ", name='" + name + '\'' +
                ", positionCoord=" + positionCoord +
                '}';
    }
}

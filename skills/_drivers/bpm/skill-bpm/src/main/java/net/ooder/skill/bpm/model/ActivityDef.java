package net.ooder.skill.bpm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDef implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String displayName;
    private String description;
    private String type;
    private String performer;
    private String performerType;
    private String formDef;
    private String serviceUrl;
    private String deviceId;
    private String eventType;
    private int x;
    private int y;
    private int width;
    private int height;
    private Map<String, Object> extendedAttributes;
    private List<String> incomingTransitions;
    private List<String> outgoingTransitions;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public ActivityDef() {
        this.extendedAttributes = new HashMap<>();
        this.incomingTransitions = new ArrayList<>();
        this.outgoingTransitions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getPerformerType() {
        return performerType;
    }

    public void setPerformerType(String performerType) {
        this.performerType = performerType;
    }

    public String getFormDef() {
        return formDef;
    }

    public void setFormDef(String formDef) {
        this.formDef = formDef;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }

    public List<String> getIncomingTransitions() {
        return incomingTransitions;
    }

    public void setIncomingTransitions(List<String> incomingTransitions) {
        this.incomingTransitions = incomingTransitions;
    }

    public List<String> getOutgoingTransitions() {
        return outgoingTransitions;
    }

    public void setOutgoingTransitions(List<String> outgoingTransitions) {
        this.outgoingTransitions = outgoingTransitions;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public void addIncomingTransition(String transitionId) {
        this.incomingTransitions.add(transitionId);
    }

    public void addOutgoingTransition(String transitionId) {
        this.outgoingTransitions.add(transitionId);
    }
}

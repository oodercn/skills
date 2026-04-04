package net.ooder.skill.bpm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessDef implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String displayName;
    private String description;
    private String category;
    private String version;
    private String status;
    private String xpdlContent;
    private String formDef;
    private Map<String, Object> extendedAttributes;
    private List<ActivityDef> activities;
    private List<TransitionDef> transitions;
    private String creatorId;
    private String creatorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishTime;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    public ProcessDef() {
        this.extendedAttributes = new HashMap<>();
        this.activities = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.status = "DRAFT";
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getXpdlContent() {
        return xpdlContent;
    }

    public void setXpdlContent(String xpdlContent) {
        this.xpdlContent = xpdlContent;
    }

    public String getFormDef() {
        return formDef;
    }

    public void setFormDef(String formDef) {
        this.formDef = formDef;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }

    public List<ActivityDef> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDef> activities) {
        this.activities = activities;
    }

    public List<TransitionDef> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TransitionDef> transitions) {
        this.transitions = transitions;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
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

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public void addActivity(ActivityDef activity) {
        this.activities.add(activity);
    }

    public void addTransition(TransitionDef transition) {
        this.transitions.add(transition);
    }

    public ActivityDef findActivity(String activityId) {
        return activities.stream()
                .filter(a -> a.getId().equals(activityId))
                .findFirst()
                .orElse(null);
    }
}

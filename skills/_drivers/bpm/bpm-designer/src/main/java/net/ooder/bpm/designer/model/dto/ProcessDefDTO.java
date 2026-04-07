package net.ooder.bpm.designer.model.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;

public class ProcessDefDTO {
    
    @NotBlank(message = "流程ID不能为空")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]*$", message = "流程ID必须以字母开头，只能包含字母、数字、下划线和连字符")
    private String processDefId;
    
    @NotBlank(message = "流程名称不能为空")
    @Size(min = 1, max = 100, message = "流程名称长度必须在1-100之间")
    private String name;
    
    @Size(max = 500, message = "流程描述长度不能超过500")
    private String description;
    
    private String classification;
    
    private String systemCode;
    
    @NotBlank(message = "访问级别不能为空")
    private String accessLevel;
    
    @Min(value = 0, message = "版本号不能为负数")
    private Integer version;
    
    private String publicationStatus;
    
    private Integer limit;
    
    private String durationUnit;
    
    private String activeTime;
    
    private String freezeTime;
    
    private String creatorId;
    
    private String creatorName;
    
    private String createdTime;
    
    private String modifierId;
    
    private String modifierName;
    
    private String modifyTime;
    
    private String mark;
    
    private String lock;
    
    private Boolean autoSave;
    
    private Boolean noSqlType;
    
    private List<String> tableNames;
    
    private List<String> moduleNames;
    
    private List<ActivityDefDTO> activities;
    
    private List<RouteDefDTO> routes;
    
    private Map<String, Object> extendedAttributes;

    public String getProcessDefId() { return processDefId; }
    public void setProcessDefId(String processDefId) { this.processDefId = processDefId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }
    
    public String getSystemCode() { return systemCode; }
    public void setSystemCode(String systemCode) { this.systemCode = systemCode; }
    
    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    
    public String getPublicationStatus() { return publicationStatus; }
    public void setPublicationStatus(String publicationStatus) { this.publicationStatus = publicationStatus; }
    
    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
    
    public String getDurationUnit() { return durationUnit; }
    public void setDurationUnit(String durationUnit) { this.durationUnit = durationUnit; }
    
    public String getActiveTime() { return activeTime; }
    public void setActiveTime(String activeTime) { this.activeTime = activeTime; }
    
    public String getFreezeTime() { return freezeTime; }
    public void setFreezeTime(String freezeTime) { this.freezeTime = freezeTime; }
    
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    
    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
    
    public String getModifierId() { return modifierId; }
    public void setModifierId(String modifierId) { this.modifierId = modifierId; }
    
    public String getModifierName() { return modifierName; }
    public void setModifierName(String modifierName) { this.modifierName = modifierName; }
    
    public String getModifyTime() { return modifyTime; }
    public void setModifyTime(String modifyTime) { this.modifyTime = modifyTime; }
    
    public String getMark() { return mark; }
    public void setMark(String mark) { this.mark = mark; }
    
    public String getLock() { return lock; }
    public void setLock(String lock) { this.lock = lock; }
    
    public Boolean getAutoSave() { return autoSave; }
    public void setAutoSave(Boolean autoSave) { this.autoSave = autoSave; }
    
    public Boolean getNoSqlType() { return noSqlType; }
    public void setNoSqlType(Boolean noSqlType) { this.noSqlType = noSqlType; }
    
    public List<String> getTableNames() { return tableNames; }
    public void setTableNames(List<String> tableNames) { this.tableNames = tableNames; }
    
    public List<String> getModuleNames() { return moduleNames; }
    public void setModuleNames(List<String> moduleNames) { this.moduleNames = moduleNames; }
    
    public List<ActivityDefDTO> getActivities() { return activities; }
    public void setActivities(List<ActivityDefDTO> activities) { this.activities = activities; }
    
    public List<RouteDefDTO> getRoutes() { return routes; }
    public void setRoutes(List<RouteDefDTO> routes) { this.routes = routes; }
    
    public Map<String, Object> getExtendedAttributes() { return extendedAttributes; }
    public void setExtendedAttributes(Map<String, Object> extendedAttributes) { this.extendedAttributes = extendedAttributes; }
}

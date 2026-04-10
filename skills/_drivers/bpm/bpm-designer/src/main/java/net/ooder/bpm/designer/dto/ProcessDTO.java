package net.ooder.bpm.designer.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import net.ooder.bpm.designer.dto.sub.AgentConfigDTO;
import net.ooder.bpm.designer.dto.sub.SceneConfigDTO;

import java.util.List;
import java.util.Map;

/**
 * 流程定义DTO - 使用子对象替代Map
 */
public class ProcessDTO {

    @JSONField(name = "processDefId")
    private String processDefId;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "description")
    private String description;

    @JSONField(name = "classification")
    private String classification;

    @JSONField(name = "systemCode")
    private String systemCode;

    @JSONField(name = "accessLevel")
    private String accessLevel;

    @JSONField(name = "version")
    private Integer version;

    @JSONField(name = "publicationStatus")
    private String publicationStatus;

    @JSONField(name = "limit")
    private Integer limit;

    @JSONField(name = "durationUnit")
    private String durationUnit;

    @JSONField(name = "activeTime")
    private String activeTime;

    @JSONField(name = "freezeTime")
    private String freezeTime;

    @JSONField(name = "creatorId")
    private String creatorId;

    @JSONField(name = "creatorName")
    private String creatorName;

    @JSONField(name = "createdTime")
    private String createdTime;

    @JSONField(name = "modifierId")
    private String modifierId;

    @JSONField(name = "modifierName")
    private String modifierName;

    @JSONField(name = "modifyTime")
    private String modifyTime;

    @JSONField(name = "updatedTime")
    private String updatedTime;

    @JSONField(name = "mark")
    private String mark;

    @JSONField(name = "lock")
    private String lock;

    @JSONField(name = "autoSave")
    private Boolean autoSave;

    @JSONField(name = "noSqlType")
    private Boolean noSqlType;

    @JSONField(name = "tableNames")
    private List<String> tableNames;

    @JSONField(name = "moduleNames")
    private List<String> moduleNames;

    @JSONField(name = "activities")
    private List<ActivityDTO> activities;

    @JSONField(name = "routes")
    private List<RouteDTO> routes;

    @JSONField(name = "listeners")
    private List<Map<String, Object>> listeners;

    @JSONField(name = "formulas")
    private List<Map<String, Object>> formulas;

    @JSONField(name = "parameters")
    private List<Map<String, Object>> parameters;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    @JSONField(name = "agentConfig")
    private AgentConfigDTO agentConfig;

    @JSONField(name = "sceneConfig")
    private SceneConfigDTO sceneConfig;

    // Getters and Setters
    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
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

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getPublicationStatus() {
        return publicationStatus;
    }

    public void setPublicationStatus(String publicationStatus) {
        this.publicationStatus = publicationStatus;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public String getFreezeTime() {
        return freezeTime;
    }

    public void setFreezeTime(String freezeTime) {
        this.freezeTime = freezeTime;
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

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifierId() {
        return modifierId;
    }

    public void setModifierId(String modifierId) {
        this.modifierId = modifierId;
    }

    public String getModifierName() {
        return modifierName;
    }

    public void setModifierName(String modifierName) {
        this.modifierName = modifierName;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public Boolean getAutoSave() {
        return autoSave;
    }

    public void setAutoSave(Boolean autoSave) {
        this.autoSave = autoSave;
    }

    public Boolean getNoSqlType() {
        return noSqlType;
    }

    public void setNoSqlType(Boolean noSqlType) {
        this.noSqlType = noSqlType;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    public List<String> getModuleNames() {
        return moduleNames;
    }

    public void setModuleNames(List<String> moduleNames) {
        this.moduleNames = moduleNames;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

    public List<RouteDTO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteDTO> routes) {
        this.routes = routes;
    }

    public List<Map<String, Object>> getListeners() {
        return listeners;
    }

    public void setListeners(List<Map<String, Object>> listeners) {
        this.listeners = listeners;
    }

    public List<Map<String, Object>> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<Map<String, Object>> formulas) {
        this.formulas = formulas;
    }

    public List<Map<String, Object>> getParameters() {
        return parameters;
    }

    public void setParameters(List<Map<String, Object>> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
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

    @Override
    public String toString() {
        return "ProcessDTO{" +
                "processDefId='" + processDefId + '\'' +
                ", name='" + name + '\'' +
                ", activitiesCount=" + (activities != null ? activities.size() : 0) +
                '}';
    }
}

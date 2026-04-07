package net.ooder.bpm.designer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessDef {
    private String processDefId;
    private String name;
    private String description;
    private String classification;
    private String systemCode;
    private String accessLevel = "INDEPENDENT";
    private Integer version = 1;
    private String publicationStatus = "DRAFT";
    private Integer limit;
    private String durationUnit = "D";
    private String activeTime;
    private String freezeTime;
    private String creatorId;
    private String creatorName;
    private String createdTime;
    private String modifierId;
    private String modifierName;
    private String modifyTime;
    private String mark = "GLOBAL";
    private String lock = "NO_LOCK";
    private Boolean autoSave = false;
    private Boolean noSqlType = false;
    private List<String> tableNames = new ArrayList<>();
    private List<String> moduleNames = new ArrayList<>();
    private List<ActivityDef> activities = new ArrayList<>();
    private List<RouteDef> routes = new ArrayList<>();
    private List<Map<String, Object>> listeners = new ArrayList<>();
    private List<Map<String, Object>> formulas = new ArrayList<>();
    private List<Map<String, Object>> parameters = new ArrayList<>();
    private Map<String, Object> extendedAttributes = new HashMap<>();
    private Map<String, Object> agentConfig;
    private Map<String, Object> sceneConfig;

    public ProcessDef() {
        this.processDefId = "proc_" + System.currentTimeMillis();
        this.createdTime = java.time.Instant.now().toString();
        this.updatedTime = this.createdTime;
    }

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
    
    public List<ActivityDef> getActivities() { return activities; }
    public void setActivities(List<ActivityDef> activities) { this.activities = activities; }
    
    public List<RouteDef> getRoutes() { return routes; }
    public void setRoutes(List<RouteDef> routes) { this.routes = routes; }
    
    public List<Map<String, Object>> getListeners() { return listeners; }
    public void setListeners(List<Map<String, Object>> listeners) { this.listeners = listeners; }
    
    public List<Map<String, Object>> getFormulas() { return formulas; }
    public void setFormulas(List<Map<String, Object>> formulas) { this.formulas = formulas; }
    
    public List<Map<String, Object>> getParameters() { return parameters; }
    public void setParameters(List<Map<String, Object>> parameters) { this.parameters = parameters; }
    
    public Map<String, Object> getExtendedAttributes() { return extendedAttributes; }
    public void setExtendedAttributes(Map<String, Object> extendedAttributes) { this.extendedAttributes = extendedAttributes; }
    
    public Map<String, Object> getAgentConfig() { return agentConfig; }
    public void setAgentConfig(Map<String, Object> agentConfig) { this.agentConfig = agentConfig; }
    
    public Map<String, Object> getSceneConfig() { return sceneConfig; }
    public void setSceneConfig(Map<String, Object> sceneConfig) { this.sceneConfig = sceneConfig; }
    
    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
}

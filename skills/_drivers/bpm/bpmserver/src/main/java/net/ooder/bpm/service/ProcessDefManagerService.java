package net.ooder.bpm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.database.*;
import net.ooder.bpm.engine.inter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProcessDefManagerService {

    private static final Logger log = LoggerFactory.getLogger(ProcessDefManagerService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private DbProcessDefManager processDefManager;
    
    @Autowired
    private DbProcessDefVersionManager processDefVersionManager;
    
    @Autowired
    private DbActivityDefManager activityDefManager;
    
    @Autowired
    private DbRouteDefManager routeDefManager;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getProcessDef(String processDefId) {
        try {
            EIProcessDef processDef = processDefManager.loadByKey(processDefId);
            if (processDef == null) {
                // 如果 loadByKey 返回 null，尝试从 loadAll 中查找
                // 这可能是因为缓存没有同步
                log.warn("loadByKey returned null for {}, trying loadAll", processDefId);
                List<EIProcessDef> allProcessDefs = processDefManager.loadAll();
                for (EIProcessDef pd : allProcessDefs) {
                    if (processDefId.equals(pd.getProcessDefId())) {
                        processDef = pd;
                        log.info("Found process {} in loadAll", processDefId);
                        break;
                    }
                }
                if (processDef == null) {
                    log.error("Process {} not found in loadAll either", processDefId);
                    return null;
                }
            }
            return convertProcessDef(processDef);
        } catch (BPMException e) {
            log.error("Failed to get process def: {}", e.getMessage());
            return null;
        }
    }

    public void deleteProcessDef(String processDefId) {
        try {
            List<String> versionIds = jdbcTemplate.queryForList(
                "SELECT PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF_VERSION WHERE PROCESSDEF_ID = ?",
                String.class, processDefId);
            
            for (String versionId : versionIds) {
                List<String> activityIds = jdbcTemplate.queryForList(
                    "SELECT ACTIVITYDEF_ID FROM BPM_ACTIVITYDEF WHERE PROCESSDEF_VERSION_ID = ?",
                    String.class, versionId);
                for (String activityId : activityIds) {
                    jdbcTemplate.update("DELETE FROM BPM_ACTIVITYDEF_PROPERTY WHERE ACTIVITYDEF_ID = ?", activityId);
                }
                jdbcTemplate.update("DELETE FROM BPM_ACTIVITYDEF WHERE PROCESSDEF_VERSION_ID = ?", versionId);
                jdbcTemplate.update("DELETE FROM BPM_ROUTEDEF WHERE PROCESSDEF_VERSION_ID = ?", versionId);
                jdbcTemplate.update("DELETE FROM BPM_PROCESSDEF_VERSION WHERE PROCESSDEF_VERSION_ID = ?", versionId);
            }
            
            jdbcTemplate.update("DELETE FROM BPM_PROCESSDEF_PARTICIPANT WHERE PROCESSDEF_ID = ?", processDefId);
            jdbcTemplate.update("DELETE FROM BPM_PROCESSDEF WHERE PROCESSDEF_ID = ?", processDefId);
            
            log.info("Deleted process def: {}", processDefId);
        } catch (Exception e) {
            log.error("Failed to delete process def: {}", processDefId, e);
            throw new RuntimeException("Failed to delete process def: " + processDefId, e);
        }
    }

    public List<Map<String, Object>> getAllProcessDefs() {
        List<EIProcessDef> processDefs = processDefManager.loadAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (EIProcessDef processDef : processDefs) {
            result.add(convertProcessDef(processDef));
        }
        return result;
    }

    public Map<String, Object> getProcessDefVersion(String versionId) {
        try {
            EIProcessDefVersion version = processDefVersionManager.loadByKey(versionId);
            if (version == null) {
                return null;
            }
            return convertProcessDefVersion(version);
        } catch (BPMException e) {
            log.error("Failed to get process def version: {}", e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getActiveProcessDefVersion(String processDefId) {
        try {
            EIProcessDefVersion version = processDefVersionManager.getActiveProcessDefVersion(processDefId);
            if (version == null) {
                return null;
            }
            return convertProcessDefVersion(version);
        } catch (BPMException e) {
            log.error("Failed to get active process def version: {}", e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getProcessDefVersions(String processDefId) {
        try {
            List<EIProcessDefVersion> versions = processDefVersionManager.loadByProcessdefId(processDefId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (EIProcessDefVersion version : versions) {
                result.add(convertProcessDefVersion(version));
            }
            return result;
        } catch (BPMException e) {
            log.error("Failed to get process def versions: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, Object> getActivityDef(String activityDefId) {
        try {
            EIActivityDef activityDef = activityDefManager.loadByKey(activityDefId);
            if (activityDef == null) {
                return null;
            }
            return convertActivityDef(activityDef);
        } catch (BPMException e) {
            log.error("Failed to get activity def: {}", e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getActivityDefsByVersion(String versionId) {
        log.info("[DEBUG] getActivityDefsByVersion: versionId={}", versionId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            List<Map<String, Object>> activities = jdbcTemplate.queryForList(
                "SELECT ACTIVITYDEF_ID, DEFNAME, DESCRIPTION, POSITION, IMPLEMENTATION FROM BPM_ACTIVITYDEF WHERE PROCESSDEF_VERSION_ID = ?",
                versionId
            );
            
            log.info("[DEBUG] Found {} activities in DB for versionId={}", activities.size(), versionId);
            
            int index = 0;
            for (Map<String, Object> activityData : activities) {
                String activityDefId = (String) activityData.get("ACTIVITYDEF_ID");
                String name = (String) activityData.get("DEFNAME");
                String description = (String) activityData.get("DESCRIPTION");
                String position = (String) activityData.get("POSITION");
                
                log.info("[DEBUG] Processing activity {}: id={}, name={}, position={}", index, activityDefId, name, position);
                
                Map<String, Object> activityMap = new LinkedHashMap<>();
                activityMap.put("activityDefId", activityDefId);
                activityMap.put("name", name);
                activityMap.put("description", description);
                // 将数据库的 POSITION_START/POSITION_END/POSITION_NORMAL 转换为前端期望的 START/END/NORMAL
                String normalizedPosition = position;
                if ("POSITION_START".equals(position)) {
                    normalizedPosition = "START";
                } else if ("POSITION_END".equals(position)) {
                    normalizedPosition = "END";
                } else if ("POSITION_NORMAL".equals(position)) {
                    normalizedPosition = "NORMAL";
                }
                activityMap.put("position", normalizedPosition);
                activityMap.put("implementation", activityData.get("IMPLEMENTATION"));
                
                List<Map<String, Object>> props = jdbcTemplate.queryForList(
                    "SELECT PROPNAME, PROPVALUE, PARENTPROP_ID FROM BPM_ACTIVITYDEF_PROPERTY WHERE ACTIVITYDEF_ID = ?",
                    activityDefId
                );
                
                String activityType = null;
                String activityCategory = null;
                String positionCoord = null;
                
                log.info("[DEBUG] Activity {} has {} properties", activityDefId, props.size());
                
                for (Map<String, Object> prop : props) {
                    String propName = (String) prop.get("PROPNAME");
                    String propValue = (String) prop.get("PROPVALUE");
                    Object parentPropId = prop.get("PARENTPROP_ID");
                    
                    log.debug("[DEBUG] Property: name={}, value={}, parentId={}", propName, propValue, parentPropId);
                    
                    // 检查属性名匹配（无论是否有父属性）
                    if ("positionCoord".equals(propName)) {
                        positionCoord = propValue;
                        log.info("[DEBUG] Found positionCoord: {}", propValue);
                    } else if ("activityType".equals(propName)) {
                        activityType = propValue;
                        log.info("[DEBUG] Found activityType: {}", propValue);
                    } else if ("activityCategory".equals(propName)) {
                        activityCategory = propValue;
                        log.info("[DEBUG] Found activityCategory: {}", propValue);
                    }
                }
                
                if (activityType == null || activityType.isEmpty()) {
                    if ("POSITION_START".equals(position)) {
                        activityType = "START";
                    } else if ("POSITION_END".equals(position)) {
                        activityType = "END";
                    } else {
                        activityType = "TASK";
                    }
                    log.info("[DEBUG] Inferred activityType: {} from position: {}", activityType, position);
                }
                
                if (activityCategory == null || activityCategory.isEmpty()) {
                    activityCategory = "HUMAN";
                    log.info("[DEBUG] Default activityCategory: {}", activityCategory);
                }
                
                activityMap.put("activityType", activityType);
                activityMap.put("activityCategory", activityCategory);
                
                if (positionCoord != null && !positionCoord.isEmpty()) {
                    try {
                        Map<String, Object> positionCoordMap = mapper.readValue(positionCoord, Map.class);
                        activityMap.put("positionCoord", positionCoordMap);
                        log.info("[DEBUG] Activity {} positionCoord: {}", activityDefId, positionCoordMap);
                    } catch (Exception e) {
                        log.error("[DEBUG] Failed to parse positionCoord for activity {}: {}", activityDefId, e.getMessage());
                    }
                } else {
                    log.warn("[DEBUG] No positionCoord found for activity {}", activityDefId);
                }
                
                result.add(activityMap);
                index++;
            }
            
            log.info("[DEBUG] Returning {} activities", result.size());
        } catch (Exception e) {
            log.error("[DEBUG] Failed to get activity defs by version: {}", e.getMessage(), e);
        }
        
        return result;
    }

    public List<Map<String, Object>> getStartActivitiesByVersion(String versionId) {
        try {
            List<EIActivityDef> activities = activityDefManager.loadByProcessDefVersionId(versionId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (EIActivityDef activity : activities) {
                if ("POSITION_START".equals(activity.getPosition())) {
                    result.add(convertActivityDef(activity));
                }
            }
            return result;
        } catch (BPMException e) {
            log.error("Failed to get start activities: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, Object> getRouteDef(String routeDefId) {
        try {
            EIRouteDef routeDef = routeDefManager.loadByKey(routeDefId);
            if (routeDef == null) {
                return null;
            }
            return convertRouteDef(routeDef);
        } catch (BPMException e) {
            log.error("Failed to get route def: {}", e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getRouteDefsByVersion(String versionId) {
        try {
            List<EIRouteDef> routes = routeDefManager.loadByProcessDefVersionId(versionId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (EIRouteDef route : routes) {
                result.add(convertRouteDef(route));
            }
            return result;
        } catch (BPMException e) {
            log.error("Failed to get route defs by version: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getOutRoutes(String activityDefId) {
        try {
            List<EIRouteDef> routes = routeDefManager.getOutRoutesFromActivity(activityDefId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (EIRouteDef route : routes) {
                result.add(convertRouteDef(route));
            }
            return result;
        } catch (BPMException e) {
            log.error("Failed to get out routes: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, Object> getFullProcessDef(String processDefId) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        Map<String, Object> processDef = getProcessDef(processDefId);
        if (processDef == null) {
            return null;
        }
        result.put("processDef", processDef);
        
        Map<String, Object> activeVersion = getActiveProcessDefVersion(processDefId);
        if (activeVersion != null) {
            result.put("activeVersion", activeVersion);
            
            String versionId = (String) activeVersion.get("processDefVersionId");
            
            List<Map<String, Object>> activities = getActivityDefsByVersion(versionId);
            result.put("activities", activities);
            
            List<Map<String, Object>> routes = getRouteDefsByVersion(versionId);
            result.put("routes", routes);
        }
        
        return result;
    }

    private Map<String, Object> convertProcessDef(EIProcessDef processDef) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("processDefId", processDef.getProcessDefId());
        map.put("name", processDef.getName());
        map.put("description", processDef.getDescription());
        map.put("classification", processDef.getClassification());
        map.put("systemCode", processDef.getSystemCode());
        map.put("accessLevel", processDef.getAccessLevel());
        return map;
    }

    private Map<String, Object> convertProcessDefVersion(EIProcessDefVersion version) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("processDefVersionId", version.getProcessDefVersionId());
        map.put("processDefId", version.getProcessDefId());
        map.put("version", version.getVersion());
        map.put("state", version.getPublicationStatus());
        map.put("createTime", version.getCreated());
        map.put("activeTime", version.getActiveTime());
        map.put("freezeTime", version.getFreezeTime());
        map.put("description", version.getDescription());
        return map;
    }

    private Map<String, Object> convertActivityDef(EIActivityDef activityDef) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("activityDefId", activityDef.getActivityDefId());
        map.put("processDefId", activityDef.getProcessDefId());
        map.put("processDefVersionId", activityDef.getProcessDefVersionId());
        map.put("name", activityDef.getName());
        map.put("description", activityDef.getDescription());
        map.put("position", activityDef.getPosition());
        map.put("implementation", activityDef.getImplementation());
        map.put("limitTime", activityDef.getLimit());
        map.put("alertTime", activityDef.getAlertTime());
        map.put("durationUnit", activityDef.getDurationUnit());
        map.put("canRouteBack", activityDef.getCanRouteBack());
        
        String activityDefId = activityDef.getActivityDefId();
        log.info("[LOAD] ===== Loading attributes for activity: {} =====", activityDefId);
        
        // 首先尝试从 WORKFLOW 属性组中加载
        String activityType = activityDef.getAttributeValue("WORKFLOW.activityType");
        String activityCategory = activityDef.getAttributeValue("WORKFLOW.activityCategory");
        String positionCoord = activityDef.getAttributeValue("WORKFLOW.positionCoord");
        
        log.info("[LOAD] Step 1 - From WORKFLOW group: activityType={}, activityCategory={}, positionCoord={}", 
            activityType, activityCategory, positionCoord);
        
        // 如果 WORKFLOW 组中没有，尝试直接从顶层属性加载（兼容直接保存的属性名）
        if (activityType == null || activityType.isEmpty()) {
            activityType = activityDef.getAttributeValue("activityType");
            log.info("[LOAD] Step 2 - From top-level: activityType={}", activityType);
        }
        if (activityCategory == null || activityCategory.isEmpty()) {
            activityCategory = activityDef.getAttributeValue("activityCategory");
            log.info("[LOAD] Step 2 - From top-level: activityCategory={}", activityCategory);
        }
        if (positionCoord == null || positionCoord.isEmpty()) {
            positionCoord = activityDef.getAttributeValue("positionCoord");
            log.info("[LOAD] Step 2 - From top-level: positionCoord={}", positionCoord);
        }
        
        // 根据 position 推断 activityType（如果没有保存过）
        if (activityType == null || activityType.isEmpty()) {
            String position = activityDef.getPosition();
            if ("POSITION_START".equals(position)) {
                activityType = "START";
            } else if ("POSITION_END".equals(position)) {
                activityType = "END";
            } else {
                activityType = "TASK";
            }
            log.info("[LOAD] Step 3 - Inferred activityType={} from position={}", activityType, position);
        }
        
        // 默认 activityCategory 为 HUMAN
        if (activityCategory == null || activityCategory.isEmpty()) {
            activityCategory = "HUMAN";
            log.info("[LOAD] Step 3 - Default activityCategory={}", activityCategory);
        }
        
        map.put("activityType", activityType);
        map.put("activityCategory", activityCategory);
        
        if (positionCoord != null && !positionCoord.isEmpty()) {
            try {
                Map<String, Object> positionCoordMap = mapper.readValue(positionCoord, Map.class);
                log.info("[LOAD] Step 4 - Parsed positionCoord for activity {}: {}", activityDefId, positionCoordMap);
                map.put("positionCoord", positionCoordMap);
            } catch (Exception e) {
                log.error("[LOAD] Failed to parse positionCoord for activity {}: {}", activityDefId, e.getMessage(), e);
            }
        } else {
            log.warn("[LOAD] Step 4 - No positionCoord found for activity {}, will NOT set default", activityDefId);
            // 不设置默认值，让前端处理
        }
        
        // 加载三维度分类 (classification)
        String classification = activityDef.getAttributeValue("classification");
        if (classification != null && !classification.isEmpty()) {
            try {
                Map<String, Object> classificationMap = mapper.readValue(classification, Map.class);
                map.put("classification", classificationMap);
                log.info("[LOAD] Activity {} classification loaded: {}", activityDefId, classificationMap);
            } catch (Exception e) {
                log.warn("[LOAD] Failed to parse classification for activity {}: {}", activityDefId, e.getMessage());
            }
        }
        
        // 加载执行者配置 (performer)
        String performer = activityDef.getAttributeValue("performer");
        if (performer != null && !performer.isEmpty()) {
            try {
                Map<String, Object> performerMap = mapper.readValue(performer, Map.class);
                map.put("performer", performerMap);
                log.info("[LOAD] Activity {} performer loaded: {}", activityDefId, performerMap);
            } catch (Exception e) {
                log.warn("[LOAD] Failed to parse performer for activity {}: {}", activityDefId, e.getMessage());
            }
        }
        
        // 加载 Agent 配置 (agentConfig)
        String agentConfig = activityDef.getAttributeValue("agentConfig");
        if (agentConfig != null && !agentConfig.isEmpty()) {
            try {
                Map<String, Object> agentConfigMap = mapper.readValue(agentConfig, Map.class);
                map.put("agentConfig", agentConfigMap);
                log.info("[LOAD] Activity {} agentConfig loaded: {}", activityDefId, agentConfigMap);
            } catch (Exception e) {
                log.warn("[LOAD] Failed to parse agentConfig for activity {}: {}", activityDefId, e.getMessage());
            }
        }
        
        // 加载 Scene 配置 (sceneConfig)
        String sceneConfig = activityDef.getAttributeValue("sceneConfig");
        if (sceneConfig != null && !sceneConfig.isEmpty()) {
            try {
                Map<String, Object> sceneConfigMap = mapper.readValue(sceneConfig, Map.class);
                map.put("sceneConfig", sceneConfigMap);
                log.info("[LOAD] Activity {} sceneConfig loaded: {}", activityDefId, sceneConfigMap);
            } catch (Exception e) {
                log.warn("[LOAD] Failed to parse sceneConfig for activity {}: {}", activityDefId, e.getMessage());
            }
        }
        
        // 加载扩展属性 (extendedAttributes)
        String extendedAttributes = activityDef.getAttributeValue("extendedAttributes");
        if (extendedAttributes != null && !extendedAttributes.isEmpty()) {
            try {
                Map<String, Object> extAttrMap = mapper.readValue(extendedAttributes, Map.class);
                map.put("extendedAttributes", extAttrMap);
                log.info("[LOAD] Activity {} extendedAttributes loaded: {}", activityDefId, extAttrMap);
            } catch (Exception e) {
                log.warn("[LOAD] Failed to parse extendedAttributes for activity {}: {}", activityDefId, e.getMessage());
            }
        }
        
        log.info("[LOAD] ===== Final result for activity {}: activityType={}, activityCategory={}, hasPositionCoord={}, hasClassification={}, hasPerformer={} =====", 
            activityDefId, activityType, activityCategory, map.containsKey("positionCoord"), 
            map.containsKey("classification"), map.containsKey("performer"));
        
        return map;
    }

    private Map<String, Object> convertRouteDef(EIRouteDef routeDef) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("routeDefId", routeDef.getRouteDefId());
        map.put("processDefId", routeDef.getProcessDefId());
        map.put("processDefVersionId", routeDef.getProcessDefVersionId());
        map.put("name", routeDef.getName());
        map.put("description", routeDef.getDescription());
        map.put("fromActivityDefId", routeDef.getFromActivityDefId());
        map.put("toActivityDefId", routeDef.getToActivityDefId());
        map.put("routeOrder", routeDef.getRouteOrder());
        map.put("routeDirection", routeDef.getRouteDirection());
        map.put("routeCondition", routeDef.getRouteCondition());
        map.put("routeConditionType", routeDef.getRouteConditionType());
        return map;
    }

    @org.springframework.transaction.annotation.Transactional
    public Map<String, Object> saveProcessDef(Map<String, Object> processData) {
        log.info("[SAVE] ========== saveProcessDef called ==========");
        log.info("[SAVE] processData keys: {}", processData.keySet());
        
        // 调试：打印接收到的 activities
        Object activitiesObj = processData.get("activities");
        log.info("[SAVE] activities type: {}", activitiesObj != null ? activitiesObj.getClass().getName() : "null");
        log.info("[SAVE] activities value: {}", activitiesObj);
        
        List<Map<String, Object>> activities = null;
        if (activitiesObj instanceof List) {
            activities = (List<Map<String, Object>>) activitiesObj;
        }
        
        if (activities != null) {
            log.info("[SAVE] Received {} activities", activities.size());
            for (int i = 0; i < activities.size(); i++) {
                Map<String, Object> act = activities.get(i);
                log.info("[SAVE] Activity {}: id={}, name={}, positionCoord={}", 
                    i, act.get("activityDefId"), act.get("name"), act.get("positionCoord"));
            }
        } else {
            log.warn("[SAVE] activities is null or not a List!");
        }
        
        try {
            String processDefId = (String) processData.get("processDefId");
            String name = (String) processData.get("name");
            String description = (String) processData.get("description");
            String classification = (String) processData.getOrDefault("classification", "办公流程");
            String accessLevel = (String) processData.getOrDefault("accessLevel", "Public");
            
            EIProcessDef processDef = processDefManager.loadByKey(processDefId);
            boolean isNew = (processDef == null);
            
            if (isNew) {
                processDef = processDefManager.createProcessDef();
                processDef.setProcessDefId(processDefId);
            }
            
            processDef.setName(name);
            processDef.setDescription(description);
            processDef.setClassification(classification);
            processDef.setSystemCode("bpm");
            processDef.setAccessLevel(accessLevel);
            
            processDefManager.save(processDef);
            
            EIProcessDefVersion activeVersion = processDefVersionManager.getActiveProcessDefVersion(processDefId);
            
            String versionId;
            if (activeVersion == null) {
                versionId = processDefId + "-v1";
                activeVersion = processDefVersionManager.createProcessDefVersion();
                activeVersion.setProcessDefVersionId(versionId);
                activeVersion.setProcessDefId(processDefId);
                activeVersion.setVersion(1);
                activeVersion.setPublicationStatus("RELEASED");
                activeVersion.setDescription(description);
                processDefVersionManager.save(activeVersion);
            } else {
                versionId = activeVersion.getProcessDefVersionId();
            }
            
            activityDefManager.deleteByProcessDefVersionId(versionId);
            routeDefManager.deleteByProcessDefVersionId(versionId);
            
            // 使用前面定义的 activities 变量
            if (activities != null) {
                for (Map<String, Object> activityData : activities) {
                    EIActivityDef activity = activityDefManager.createActivityDef();
                    activity.setActivityDefId((String) activityData.get("activityDefId"));
                    String activityDefId = activity.getActivityDefId();
                    String activityName = (String) activityData.get("name");
                    String activityDesc = (String) activityData.get("description");
                    
                    String position = (String) activityData.getOrDefault("position", "NORMAL");
                    String dbPosition;
                    if ("START".equals(position)) {
                        dbPosition = "POSITION_START";
                    } else if ("END".equals(position)) {
                        dbPosition = "POSITION_END";
                    } else {
                        dbPosition = "POSITION_NORMAL";
                    }
                    
                    Map<String, Object> positionCoord = (Map<String, Object>) activityData.get("positionCoord");
                    String activityType = (String) activityData.get("activityType");
                    String activityCategory = (String) activityData.get("activityCategory");
                    
                    log.info("[SAVE] Activity {} received positionCoord: {}, activityType: {}, activityCategory: {}", 
                        activityDefId, positionCoord, activityType, activityCategory);
                    
                    // 使用 JdbcTemplate 直接插入活动基本信息（在同一事务中）
                    jdbcTemplate.update(
                        "INSERT INTO BPM_ACTIVITYDEF (ACTIVITYDEF_ID, PROCESSDEF_ID, PROCESSDEF_VERSION_ID, DEFNAME, DESCRIPTION, POSITION, IMPLEMENTATION, LIMITTIME, ALERTTIME, DURATIONUNIT, CANROUTEBACK) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        activityDefId, processDefId, versionId, activityName, activityDesc, dbPosition, "No", 0, 0, "D", "N"
                    );
                    log.info("[SAVE] Activity {} inserted via JdbcTemplate", activityDefId);
                    
                    // 保存扩展属性（positionCoord, activityType, activityCategory）
                    try {
                        String workflowPropId = "prop-" + activityDefId + "-workflow";
                        
                        jdbcTemplate.update(
                            "INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            workflowPropId, activityDefId, "WORKFLOW", "", null, "WORKFLOW", null, 0, "Y"
                        );
                        
                        // 保存 positionCoord
                        if (positionCoord != null) {
                            String positionCoordJson = mapper.writeValueAsString(positionCoord);
                            String posPropId = "prop-" + activityDefId + "-poscoord";
                            jdbcTemplate.update(
                                "INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                posPropId, activityDefId, "positionCoord", positionCoordJson, null, "WORKFLOW", workflowPropId, 0, "Y"
                            );
                            log.info("[SAVE] Activity {} positionCoord saved: {}", activityDefId, positionCoordJson);
                        } else {
                            log.warn("[SAVE] Activity {} has no positionCoord!", activityDefId);
                        }
                        
                        // 保存 activityType
                        if (activityType != null && !activityType.isEmpty()) {
                            String typePropId = "prop-" + activityDefId + "-type";
                            jdbcTemplate.update(
                                "INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                typePropId, activityDefId, "activityType", activityType, null, "WORKFLOW", workflowPropId, 0, "Y"
                            );
                            log.info("[SAVE] Activity {} activityType saved: {}", activityDefId, activityType);
                        }
                        
                        // 保存 activityCategory
                        if (activityCategory != null && !activityCategory.isEmpty()) {
                            String catPropId = "prop-" + activityDefId + "-cat";
                            jdbcTemplate.update(
                                "INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                catPropId, activityDefId, "activityCategory", activityCategory, null, "WORKFLOW", workflowPropId, 0, "Y"
                            );
                            log.info("[SAVE] Activity {} activityCategory saved: {}", activityDefId, activityCategory);
                        }
                        
                        // 保存三维度分类 (classification)
                        Map<String, Object> activityClassification = (Map<String, Object>) activityData.get("classification");
                        if (activityClassification != null) {
                            String classificationJson = mapper.writeValueAsString(activityClassification);
                            String classPropId = "prop-" + activityDefId + "-classification";
                            jdbcTemplate.update(
                                "INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                classPropId, activityDefId, "classification", classificationJson, null, "WORKFLOW", workflowPropId, 0, "Y"
                            );
                            log.info("[SAVE] Activity {} classification saved: {}", activityDefId, classificationJson);
                        }
                        
                        // 保存执行者配置 (performer)
                        Map<String, Object> performer = (Map<String, Object>) activityData.get("performer");
                        if (performer != null) {
                            String performerJson = mapper.writeValueAsString(performer);
                            String performerPropId = "prop-" + activityDefId + "-performer";
                            jdbcTemplate.update(
                                "INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                performerPropId, activityDefId, "performer", performerJson, null, "WORKFLOW", workflowPropId, 0, "Y"
                            );
                            log.info("[SAVE] Activity {} performer saved: {}", activityDefId, performerJson);
                        }
                        
                        // 保存 Agent 配置 (agentConfig)
                        Map<String, Object> agentConfig = (Map<String, Object>) activityData.get("agentConfig");
                        if (agentConfig != null) {
                            String agentConfigJson = mapper.writeValueAsString(agentConfig);
                            String agentConfigPropId = "prop-" + activityDefId + "-agentConfig";
                            jdbcTemplate.update(
                                "INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                agentConfigPropId, activityDefId, "agentConfig", agentConfigJson, null, "WORKFLOW", workflowPropId, 0, "Y"
                            );
                            log.info("[SAVE] Activity {} agentConfig saved: {}", activityDefId, agentConfigJson);
                        }
                        
                        // 保存 Scene 配置 (sceneConfig)
                        Map<String, Object> sceneConfig = (Map<String, Object>) activityData.get("sceneConfig");
                        if (sceneConfig != null) {
                            String sceneConfigJson = mapper.writeValueAsString(sceneConfig);
                            String sceneConfigPropId = "prop-" + activityDefId + "-sceneConfig";
                            jdbcTemplate.update(
                                "INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                sceneConfigPropId, activityDefId, "sceneConfig", sceneConfigJson, null, "WORKFLOW", workflowPropId, 0, "Y"
                            );
                            log.info("[SAVE] Activity {} sceneConfig saved: {}", activityDefId, sceneConfigJson);
                        }
                        
                        // 保存扩展属性 (extendedAttributes)
                        Map<String, Object> extendedAttributes = (Map<String, Object>) activityData.get("extendedAttributes");
                        if (extendedAttributes != null && !extendedAttributes.isEmpty()) {
                            String extAttrJson = mapper.writeValueAsString(extendedAttributes);
                            String extAttrPropId = "prop-" + activityDefId + "-extendedAttributes";
                            jdbcTemplate.update(
                                "INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                extAttrPropId, activityDefId, "extendedAttributes", extAttrJson, null, "WORKFLOW", workflowPropId, 0, "Y"
                            );
                            log.info("[SAVE] Activity {} extendedAttributes saved: {}", activityDefId, extAttrJson);
                        }
                    } catch (Exception e) {
                        log.error("[SAVE] Failed to save attributes for activity {}: {}", activityDefId, e.getMessage(), e);
                        throw new RuntimeException("Failed to save attributes for activity " + activityDefId, e);
                    }
                }
            }
            
            List<Map<String, Object>> routes = (List<Map<String, Object>>) processData.get("routes");
            if (routes != null) {
                int routeOrder = 1;
                for (Map<String, Object> routeData : routes) {
                    String routeDefId = (String) routeData.get("routeDefId");
                    String routeName = (String) routeData.get("name");
                    String fromActivityId = (String) routeData.get("from");
                    String toActivityId = (String) routeData.get("to");
                    String routeDirection = (String) routeData.getOrDefault("routeDirection", "FORWARD");
                    String routeCondition = (String) routeData.get("condition");
                    
                    // 使用 JdbcTemplate 直接插入路由（在同一事务中）
                    jdbcTemplate.update(
                        "INSERT INTO BPM_ROUTEDEF (ROUTEDEF_ID, PROCESSDEF_ID, PROCESSDEF_VERSION_ID, ROUTENAME, DESCRIPTION, FROMACTIVITYDEF_ID, TOACTIVITYDEF_ID, ROUTEORDER, ROUTEDIRECTION, ROUTECONDITION, ROUTECONDITIONTYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        routeDefId, processDefId, versionId, routeName, "", fromActivityId, toActivityId, routeOrder++, routeDirection, routeCondition, "CONDITION"
                    );
                    log.info("[SAVE] Route {} inserted via JdbcTemplate", routeDefId);
                }
            }
            
            return getFullProcessDef(processDefId);
         } catch (BPMException e) {
            log.error("Failed to save process def: {}", e.getMessage(), e);
            throw new RuntimeException("保存流程失败: " + e.getMessage(), e);
        }
    }
}

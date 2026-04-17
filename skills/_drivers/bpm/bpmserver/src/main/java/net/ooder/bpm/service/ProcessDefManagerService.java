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
        try {
            List<EIActivityDef> activities = activityDefManager.loadByProcessDefVersionId(versionId);
            log.info("[DEBUG] getActivityDefsByVersion: versionId=" + versionId + ", activities.size()=" + activities.size());
            List<Map<String, Object>> result = new ArrayList<>();
            int index = 0;
            for (EIActivityDef activity : activities) {
                log.info("[DEBUG] Processing activity " + index + ": id=" + activity.getActivityDefId() 
                    + ", name=" + activity.getName() + ", position=" + activity.getPosition());
                result.add(convertActivityDef(activity));
                index++;
            }
            log.info("[DEBUG] Returning " + result.size() + " activities");
            return result;
        } catch (BPMException e) {
            log.error("Failed to get activity defs by version: " + e.getMessage());
            return new ArrayList<>();
        }
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
        
        log.info("[LOAD] Loading attributes for activity: {}", activityDef.getActivityDefId());
        
        // 加载 activityType 和 activityCategory
        String activityType = activityDef.getAttributeValue("WORKFLOW.activityType");
        String activityCategory = activityDef.getAttributeValue("WORKFLOW.activityCategory");
        
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
        }
        
        // 默认 activityCategory 为 HUMAN
        if (activityCategory == null || activityCategory.isEmpty()) {
            activityCategory = "HUMAN";
        }
        
        map.put("activityType", activityType);
        map.put("activityCategory", activityCategory);
        
        log.info("[LOAD] activityType: {}, activityCategory: {}", activityType, activityCategory);
        
        EIAttributeDef workflowAttr = activityDef.getAttribute("WORKFLOW");
        log.info("[LOAD] WORKFLOW attribute: {}", workflowAttr);
        
        String positionCoord = activityDef.getAttributeValue("WORKFLOW.positionCoord");
        log.info("[LOAD] positionCoord raw value: {}", positionCoord);
        if (positionCoord != null && !positionCoord.isEmpty()) {
            try {
                Map<String, Object> positionCoordMap = mapper.readValue(positionCoord, Map.class);
                log.info("[LOAD] Parsed positionCoord for activity {}: {}", activityDef.getActivityDefId(), positionCoordMap);
                map.put("positionCoord", positionCoordMap);
            } catch (Exception e) {
                log.error("[LOAD] Failed to parse positionCoord for activity {}: {}", activityDef.getActivityDefId(), e.getMessage(), e);
            }
        } else {
            log.warn("[LOAD] No positionCoord found for activity {}", activityDef.getActivityDefId());
        }
        
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
            String classification = (String) processData.getOrDefault("category", "办公流程");
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

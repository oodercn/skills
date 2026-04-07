package net.ooder.bpm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.database.*;
import net.ooder.bpm.engine.inter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Map<String, Object> getProcessDef(String processDefId) {
        try {
            EIProcessDef processDef = processDefManager.loadByKey(processDefId);
            if (processDef == null) {
                return null;
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
            List<Map<String, Object>> result = new ArrayList<>();
            for (EIActivityDef activity : activities) {
                result.add(convertActivityDef(activity));
            }
            return result;
        } catch (BPMException e) {
            log.error("Failed to get activity defs by version: {}", e.getMessage());
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
        
        log.debug("Loading attributes for activity: {}", activityDef.getActivityDefId());
        
        EIAttributeDef workflowAttr = activityDef.getAttribute("WORKFLOW");
        log.debug("WORKFLOW attribute: {}", workflowAttr);
        
        String positionCoord = activityDef.getAttributeValue("WORKFLOW.positionCoord");
        log.debug("positionCoord value: {}", positionCoord);
        if (positionCoord != null && !positionCoord.isEmpty()) {
            try {
                Map<String, Object> positionCoordMap = mapper.readValue(positionCoord, Map.class);
                map.put("positionCoord", positionCoordMap);
            } catch (Exception e) {
                log.warn("Failed to parse positionCoord for activity {}: {}", activityDef.getActivityDefId(), e.getMessage());
            }
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
            
            List<Map<String, Object>> activities = (List<Map<String, Object>>) processData.get("activities");
            if (activities != null) {
                for (Map<String, Object> activityData : activities) {
                    EIActivityDef activity = activityDefManager.createActivityDef();
                    activity.setActivityDefId((String) activityData.get("activityDefId"));
                    activity.setProcessDefId(processDefId);
                    activity.setProcessDefVersionId(versionId);
                    activity.setName((String) activityData.get("name"));
                    activity.setDescription((String) activityData.get("description"));
                    
                    String position = (String) activityData.getOrDefault("position", "NORMAL");
                    if ("START".equals(position)) {
                        activity.setPosition("POSITION_START");
                    } else if ("END".equals(position)) {
                        activity.setPosition("POSITION_END");
                    } else {
                        activity.setPosition("POSITION_NORMAL");
                    }
                    
                    activity.setImplementation("No");
                    activity.setLimit(0);
                    activity.setAlertTime(0);
                    activity.setDurationUnit("D");
                    activity.setCanRouteBack("N");
                    
                    Map<String, Object> positionCoord = (Map<String, Object>) activityData.get("positionCoord");
                    if (positionCoord != null) {
                        try {
                            String positionCoordJson = mapper.writeValueAsString(positionCoord);
                            DbAttributeDef attrDef = new DbAttributeDef();
                            attrDef.setId(java.util.UUID.randomUUID().toString());
                            attrDef.setName("positionCoord");
                            attrDef.setValue(positionCoordJson);
                            attrDef.setType("WORKFLOW");
                            activity.setAttribute(null, attrDef);
                        } catch (Exception e) {
                            log.warn("Failed to set positionCoord: {}", e.getMessage());
                        }
                    }
                    
                    activityDefManager.save(activity);
                }
            }
            
            List<Map<String, Object>> routes = (List<Map<String, Object>>) processData.get("routes");
            if (routes != null) {
                int routeOrder = 1;
                for (Map<String, Object> routeData : routes) {
                    EIRouteDef route = routeDefManager.createRouteDef();
                    route.setRouteDefId((String) routeData.get("routeDefId"));
                    route.setProcessDefId(processDefId);
                    route.setProcessDefVersionId(versionId);
                    route.setName((String) routeData.get("name"));
                    route.setDescription("");
                    route.setFromActivityDefId((String) routeData.get("from"));
                    route.setToActivityDefId((String) routeData.get("to"));
                    route.setRouteOrder(routeOrder++);
                    route.setRouteDirection((String) routeData.getOrDefault("routeDirection", "FORWARD"));
                    route.setRouteCondition((String) routeData.get("condition"));
                    route.setRouteConditionType("CONDITION");
                    
                    routeDefManager.save(route);
                }
            }
            
            return getFullProcessDef(processDefId);
         } catch (BPMException e) {
            log.error("Failed to save process def: {}", e.getMessage(), e);
            throw new RuntimeException("保存流程失败: " + e.getMessage(), e);
        }
    }
}

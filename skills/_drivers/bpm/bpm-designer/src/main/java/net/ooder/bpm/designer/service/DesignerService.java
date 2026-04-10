package net.ooder.bpm.designer.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import net.ooder.bpm.designer.dto.ActivityDTO;
import net.ooder.bpm.designer.dto.PositionCoordDTO;
import net.ooder.bpm.designer.dto.ProcessDTO;
import net.ooder.bpm.designer.dto.RouteDTO;
import net.ooder.bpm.designer.dto.enums.ActivityCategory;
import net.ooder.bpm.designer.dto.enums.ActivityPosition;
import net.ooder.bpm.designer.dto.enums.ActivityType;
import net.ooder.bpm.designer.dto.sub.*;
import net.ooder.bpm.designer.model.ActivityDef;
import net.ooder.bpm.designer.model.ProcessDef;
import net.ooder.bpm.designer.model.RouteDef;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 设计器服务 - 使用FastJSON和DTO进行前后端数据交互
 * 所有数据直接从服务端获取，不缓存
 */
@Service
public class DesignerService {

    @Value("${bpm.server.url:http://localhost:8080}")
    private String bpmServerUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 获取流程定义 - 直接从服务端获取
     */
    public ProcessDef getProcess(String processId, String version) {
        try {
            String url = bpmServerUrl + "/api/processdef/" + processId;
            System.out.println("[DesignerService] Fetching process from: " + url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String body = response.getBody();
                System.out.println("[DesignerService] Response body: " + body);
                
                Map<String, Object> result = JSON.parseObject(body, new TypeReference<Map<String, Object>>() {});
                
                if (result.get("data") != null) {
                    String dataJson = JSON.toJSONString(result.get("data"));
                    System.out.println("[DesignerService] Data JSON: " + dataJson);
                    
                    ProcessDTO processDTO = JSON.parseObject(dataJson, ProcessDTO.class);
                    System.out.println("[DesignerService] Parsed ProcessDTO: " + processDTO);
                    
                    ProcessDef process = convertToProcessDef(processDTO);
                    System.out.println("[DesignerService] Converted ProcessDef with " + 
                        process.getActivities().size() + " activities");
                    
                    return process;
                }
            }
        } catch (Exception e) {
            System.err.println("[DesignerService] Failed to get process from bpmserver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get process: " + processId, e);
        }
        throw new RuntimeException("Process not found: " + processId);
    }

    /**
     * 将ProcessDTO转换为ProcessDef Model
     */
    private ProcessDef convertToProcessDef(ProcessDTO dto) {
        ProcessDef process = new ProcessDef();
        process.setProcessDefId(dto.getProcessDefId());
        process.setName(dto.getName());
        process.setDescription(dto.getDescription());
        process.setSystemCode(dto.getSystemCode());
        process.setVersion(dto.getVersion());
        process.setStatus(dto.getPublicationStatus());
        process.setClassification(dto.getClassification());
        process.setAccessLevel(dto.getAccessLevel());
        process.setLimit(dto.getLimit());
        process.setDurationUnit(dto.getDurationUnit());
        process.setActiveTime(dto.getActiveTime());
        process.setFreezeTime(dto.getFreezeTime());
        process.setCreatorId(dto.getCreatorId());
        process.setCreatorName(dto.getCreatorName());
        process.setCreatedTime(dto.getCreatedTime());
        process.setModifierId(dto.getModifierId());
        process.setModifierName(dto.getModifierName());
        process.setModifyTime(dto.getModifyTime());
        process.setUpdatedTime(dto.getUpdatedTime());
        process.setMark(dto.getMark());
        process.setLock(dto.getLock());
        process.setAutoSave(dto.getAutoSave());
        process.setNoSqlType(dto.getNoSqlType());
        process.setTableNames(dto.getTableNames());
        process.setModuleNames(dto.getModuleNames());
        process.setListeners(dto.getListeners());
        process.setFormulas(dto.getFormulas());
        process.setParameters(dto.getParameters());
        process.setExtendedAttributes(dto.getExtendedAttributes());
        
        if (dto.getAgentConfig() != null) {
            process.setAgentConfig(convertAgentConfigToMap(dto.getAgentConfig()));
        }
        
        if (dto.getSceneConfig() != null) {
            process.setSceneConfig(convertSceneConfigToMap(dto.getSceneConfig()));
        }
        
        if (dto.getActivities() != null) {
            for (ActivityDTO activityDTO : dto.getActivities()) {
                ActivityDef activity = convertToActivityDef(activityDTO);
                process.getActivities().add(activity);
            }
        }
        
        if (dto.getRoutes() != null) {
            for (RouteDTO routeDTO : dto.getRoutes()) {
                RouteDef route = convertToRouteDef(routeDTO);
                process.getRoutes().add(route);
            }
        }
        
        return process;
    }

    /**
     * 将ActivityDTO转换为ActivityDef Model
     */
    private ActivityDef convertToActivityDef(ActivityDTO dto) {
        ActivityDef activity = new ActivityDef();
        activity.setActivityDefId(dto.getActivityDefId());
        activity.setName(dto.getName());
        activity.setDescription(dto.getDescription());
        activity.setPosition(dto.getPosition() != null ? dto.getPosition().getCode() : "NORMAL");
        activity.setActivityType(dto.getActivityType() != null ? dto.getActivityType().getCode() : "TASK");
        activity.setActivityCategory(dto.getActivityCategory() != null ? dto.getActivityCategory().getCode() : "HUMAN");
        activity.setImplementation(dto.getImplementation());
        activity.setExecClass(dto.getExecClass());
        activity.setExtendedAttributes(dto.getExtendedAttributes());
        
        PositionCoordDTO coordDTO = dto.getPositionCoord();
        System.out.println("[DesignerService] Activity " + dto.getActivityDefId() + 
            " received PositionCoordDTO: " + coordDTO);
        
        if (coordDTO != null && coordDTO.getX() != null && coordDTO.getY() != null) {
            Map<String, Object> coord = new HashMap<>();
            coord.put("x", coordDTO.getX());
            coord.put("y", coordDTO.getY());
            activity.setPositionCoord(coord);
            System.out.println("[DesignerService] Activity " + dto.getActivityDefId() + 
                " using received coord: " + coord);
        } else {
            Map<String, Object> defaultCoord = new HashMap<>();
            defaultCoord.put("x", 100);
            defaultCoord.put("y", 100);
            activity.setPositionCoord(defaultCoord);
            System.err.println("[DesignerService] Activity " + dto.getActivityDefId() + 
                " using DEFAULT coord: " + defaultCoord + " (received was: " + coordDTO + ")");
        }
        
        if (dto.getTiming() != null) {
            activity.setTiming(convertTimingToMap(dto.getTiming()));
        }
        
        if (dto.getRouting() != null) {
            activity.setRouting(convertRoutingToMap(dto.getRouting()));
        }
        
        if (dto.getRight() != null) {
            activity.setRight(convertRightToMap(dto.getRight()));
        }
        
        if (dto.getSubFlow() != null) {
            activity.setSubFlow(convertSubFlowToMap(dto.getSubFlow()));
        }
        
        if (dto.getDevice() != null) {
            activity.setDevice(convertDeviceToMap(dto.getDevice()));
        }
        
        if (dto.getService() != null) {
            activity.setService(convertServiceToMap(dto.getService()));
        }
        
        if (dto.getEvent() != null) {
            activity.setEvent(convertEventToMap(dto.getEvent()));
        }
        
        if (dto.getAgentConfig() != null) {
            activity.setAgentConfig(convertAgentConfigToMap(dto.getAgentConfig()));
        }
        
        if (dto.getSceneConfig() != null) {
            activity.setSceneConfig(convertSceneConfigToMap(dto.getSceneConfig()));
        }
        
        return activity;
    }

    /**
     * 将RouteDTO转换为RouteDef Model
     */
    private RouteDef convertToRouteDef(RouteDTO dto) {
        RouteDef route = new RouteDef();
        route.setRouteDefId(dto.getRouteDefId());
        route.setName(dto.getName());
        route.setDescription(dto.getDescription());
        route.setFrom(dto.getFromActivityId());
        route.setTo(dto.getToActivityId());
        route.setRouteOrder(dto.getRouteOrder());
        route.setRouteDirection(dto.getRouteDirection());
        route.setRouteConditionType(dto.getRouteConditionType());
        route.setCondition(dto.getCondition());
        route.setExtendedAttributes(dto.getExtendedAttributes());
        return route;
    }

    /**
     * 保存流程定义 - 直接保存到服务端
     */
    public ProcessDef saveProcess(ProcessDef processDef) {
        try {
            String url = bpmServerUrl + "/api/processdef/save";
            System.out.println("[DesignerService] Saving process to: " + url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            ProcessDTO processDTO = convertToProcessDTO(processDef);
            String requestBody = JSON.toJSONString(processDTO);
            System.out.println("[DesignerService] Request body: " + requestBody);
            
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String body = response.getBody();
                System.out.println("[DesignerService] Save response: " + body);
                
                Map<String, Object> result = JSON.parseObject(body, new TypeReference<Map<String, Object>>() {});
                if (result.get("data") != null) {
                    String dataJson = JSON.toJSONString(result.get("data"));
                    ProcessDTO savedDTO = JSON.parseObject(dataJson, ProcessDTO.class);
                    return convertToProcessDef(savedDTO);
                }
            }
        } catch (Exception e) {
            System.err.println("[DesignerService] Failed to save process to bpmserver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save process", e);
        }
        throw new RuntimeException("Failed to save process");
    }

    /**
     * 将ProcessDef Model转换为ProcessDTO
     */
    private ProcessDTO convertToProcessDTO(ProcessDef processDef) {
        ProcessDTO dto = new ProcessDTO();
        dto.setProcessDefId(processDef.getProcessDefId());
        dto.setName(processDef.getName());
        dto.setDescription(processDef.getDescription());
        dto.setSystemCode(processDef.getSystemCode());
        dto.setVersion(processDef.getVersion());
        dto.setPublicationStatus(processDef.getStatus());
        dto.setClassification(processDef.getClassification());
        dto.setAccessLevel(processDef.getAccessLevel());
        dto.setLimit(processDef.getLimit());
        dto.setDurationUnit(processDef.getDurationUnit());
        dto.setActiveTime(processDef.getActiveTime());
        dto.setFreezeTime(processDef.getFreezeTime());
        dto.setCreatorId(processDef.getCreatorId());
        dto.setCreatorName(processDef.getCreatorName());
        dto.setCreatedTime(processDef.getCreatedTime());
        dto.setModifierId(processDef.getModifierId());
        dto.setModifierName(processDef.getModifierName());
        dto.setModifyTime(processDef.getModifyTime());
        dto.setUpdatedTime(processDef.getUpdatedTime());
        dto.setMark(processDef.getMark());
        dto.setLock(processDef.getLock());
        dto.setAutoSave(processDef.getAutoSave());
        dto.setNoSqlType(processDef.getNoSqlType());
        dto.setTableNames(processDef.getTableNames());
        dto.setModuleNames(processDef.getModuleNames());
        dto.setListeners(processDef.getListeners());
        dto.setFormulas(processDef.getFormulas());
        dto.setParameters(processDef.getParameters());
        dto.setExtendedAttributes(processDef.getExtendedAttributes());
        
        List<ActivityDTO> activityDTOs = new ArrayList<>();
        for (ActivityDef activity : processDef.getActivities()) {
            ActivityDTO activityDTO = convertToActivityDTO(activity);
            activityDTOs.add(activityDTO);
        }
        dto.setActivities(activityDTOs);
        
        List<RouteDTO> routeDTOs = new ArrayList<>();
        for (RouteDef route : processDef.getRoutes()) {
            RouteDTO routeDTO = convertToRouteDTO(route);
            routeDTOs.add(routeDTO);
        }
        dto.setRoutes(routeDTOs);
        
        return dto;
    }

    /**
     * 将ActivityDef Model转换为ActivityDTO
     */
    private ActivityDTO convertToActivityDTO(ActivityDef activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setActivityDefId(activity.getActivityDefId());
        dto.setName(activity.getName());
        dto.setDescription(activity.getDescription());
        dto.setPosition(ActivityPosition.fromCode(activity.getPosition()));
        dto.setActivityType(ActivityType.fromCode(activity.getActivityType()));
        dto.setActivityCategory(ActivityCategory.fromCode(activity.getActivityCategory()));
        dto.setImplementation(activity.getImplementation());
        dto.setExecClass(activity.getExecClass());
        dto.setExtendedAttributes(activity.getExtendedAttributes());
        
        Map<String, Object> coord = activity.getPositionCoord();
        System.out.println("[DesignerService] Converting Activity " + activity.getActivityDefId() + 
            " positionCoord: " + coord);
        if (coord != null && coord.get("x") != null && coord.get("y") != null) {
            PositionCoordDTO coordDTO = new PositionCoordDTO();
            Object xVal = coord.get("x");
            Object yVal = coord.get("y");
            System.out.println("[DesignerService] Coord values - x: " + xVal + " (type: " + (xVal != null ? xVal.getClass().getName() : "null") + 
                "), y: " + yVal + " (type: " + (yVal != null ? yVal.getClass().getName() : "null") + ")");
            coordDTO.setX(((Number) xVal).intValue());
            coordDTO.setY(((Number) yVal).intValue());
            dto.setPositionCoord(coordDTO);
            System.out.println("[DesignerService] Set PositionCoordDTO: " + coordDTO);
        } else {
            System.out.println("[DesignerService] Warning: positionCoord is null or missing x/y values");
        }
        
        if (activity.getTiming() != null) {
            dto.setTiming(convertMapToTiming(activity.getTiming()));
        }
        
        if (activity.getRouting() != null) {
            dto.setRouting(convertMapToRouting(activity.getRouting()));
        }
        
        if (activity.getRight() != null) {
            dto.setRight(convertMapToRight(activity.getRight()));
        }
        
        if (activity.getSubFlow() != null) {
            dto.setSubFlow(convertMapToSubFlow(activity.getSubFlow()));
        }
        
        if (activity.getDevice() != null) {
            dto.setDevice(convertMapToDevice(activity.getDevice()));
        }
        
        if (activity.getService() != null) {
            dto.setService(convertMapToService(activity.getService()));
        }
        
        if (activity.getEvent() != null) {
            dto.setEvent(convertMapToEvent(activity.getEvent()));
        }
        
        if (activity.getAgentConfig() != null) {
            dto.setAgentConfig(convertMapToAgentConfig(activity.getAgentConfig()));
        }
        
        if (activity.getSceneConfig() != null) {
            dto.setSceneConfig(convertMapToSceneConfig(activity.getSceneConfig()));
        }
        
        return dto;
    }

    /**
     * 将RouteDef Model转换为RouteDTO
     */
    private RouteDTO convertToRouteDTO(RouteDef route) {
        RouteDTO dto = new RouteDTO();
        dto.setRouteDefId(route.getRouteDefId());
        dto.setName(route.getName());
        dto.setDescription(route.getDescription());
        dto.setFromActivityId(route.getFrom());
        dto.setToActivityId(route.getTo());
        dto.setRouteOrder(route.getRouteOrder());
        dto.setRouteDirection(route.getRouteDirection());
        dto.setRouteConditionType(route.getRouteConditionType());
        dto.setCondition(route.getCondition());
        dto.setExtendedAttributes(route.getExtendedAttributes());
        return dto;
    }

    // ==================== DTO与Map转换工具方法 ====================

    private Map<String, Object> convertTimingToMap(TimingDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getLimitTime() != null) map.put("limitTime", dto.getLimitTime());
        if (dto.getAlertTime() != null) map.put("alertTime", dto.getAlertTime());
        if (dto.getDurationUnit() != null) map.put("durationUnit", dto.getDurationUnit());
        if (dto.getStartTime() != null) map.put("startTime", dto.getStartTime());
        if (dto.getEndTime() != null) map.put("endTime", dto.getEndTime());
        if (dto.getRemindType() != null) map.put("remindType", dto.getRemindType());
        if (dto.getRemindInterval() != null) map.put("remindInterval", dto.getRemindInterval());
        if (dto.getExtendedAttributes() != null) map.putAll(dto.getExtendedAttributes());
        return map;
    }

    private TimingDTO convertMapToTiming(Map<String, Object> map) {
        TimingDTO dto = new TimingDTO();
        if (map.get("limitTime") != null) dto.setLimitTime(((Number) map.get("limitTime")).intValue());
        if (map.get("alertTime") != null) dto.setAlertTime(((Number) map.get("alertTime")).intValue());
        if (map.get("durationUnit") != null) dto.setDurationUnit((String) map.get("durationUnit"));
        if (map.get("startTime") != null) dto.setStartTime((String) map.get("startTime"));
        if (map.get("endTime") != null) dto.setEndTime((String) map.get("endTime"));
        if (map.get("remindType") != null) dto.setRemindType((String) map.get("remindType"));
        if (map.get("remindInterval") != null) dto.setRemindInterval(((Number) map.get("remindInterval")).intValue());
        return dto;
    }

    private Map<String, Object> convertRoutingToMap(RoutingDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getJoin() != null) map.put("join", dto.getJoin());
        if (dto.getSplit() != null) map.put("split", dto.getSplit());
        if (dto.getCanRouteBack() != null) map.put("canRouteBack", dto.getCanRouteBack());
        if (dto.getRouteBackMethod() != null) map.put("routeBackMethod", dto.getRouteBackMethod());
        if (dto.getCanSpecialSend() != null) map.put("canSpecialSend", dto.getCanSpecialSend());
        if (dto.getSpecialScope() != null) map.put("specialScope", dto.getSpecialScope());
        if (dto.getDefaultRoute() != null) map.put("defaultRoute", dto.getDefaultRoute());
        if (dto.getParallelMode() != null) map.put("parallelMode", dto.getParallelMode());
        if (dto.getMergeCondition() != null) map.put("mergeCondition", dto.getMergeCondition());
        if (dto.getExtendedAttributes() != null) map.putAll(dto.getExtendedAttributes());
        return map;
    }

    private RoutingDTO convertMapToRouting(Map<String, Object> map) {
        RoutingDTO dto = new RoutingDTO();
        if (map.get("join") != null) dto.setJoin((String) map.get("join"));
        if (map.get("split") != null) dto.setSplit((String) map.get("split"));
        if (map.get("canRouteBack") != null) dto.setCanRouteBack((String) map.get("canRouteBack"));
        if (map.get("routeBackMethod") != null) dto.setRouteBackMethod((String) map.get("routeBackMethod"));
        if (map.get("canSpecialSend") != null) dto.setCanSpecialSend((String) map.get("canSpecialSend"));
        if (map.get("specialScope") != null) dto.setSpecialScope((String) map.get("specialScope"));
        if (map.get("defaultRoute") != null) dto.setDefaultRoute((String) map.get("defaultRoute"));
        if (map.get("parallelMode") != null) dto.setParallelMode((String) map.get("parallelMode"));
        if (map.get("mergeCondition") != null) dto.setMergeCondition((String) map.get("mergeCondition"));
        return dto;
    }

    private Map<String, Object> convertRightToMap(RightDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getMoveSponsorTo() != null) map.put("moveSponsorTo", dto.getMoveSponsorTo());
        if (dto.getPerformer() != null) map.put("performer", dto.getPerformer());
        if (dto.getPerformerType() != null) map.put("performerType", dto.getPerformerType());
        if (dto.getParticipationType() != null) map.put("participationType", dto.getParticipationType());
        if (dto.getParticipationScope() != null) map.put("participationScope", dto.getParticipationScope());
        if (dto.getParticipationScopeValue() != null) map.put("participationScopeValue", dto.getParticipationScopeValue());
        if (dto.getCandidateUsers() != null) map.put("candidateUsers", dto.getCandidateUsers());
        if (dto.getCandidateGroups() != null) map.put("candidateGroups", dto.getCandidateGroups());
        if (dto.getCandidateRoles() != null) map.put("candidateRoles", dto.getCandidateRoles());
        if (dto.getAssignee() != null) map.put("assignee", dto.getAssignee());
        if (dto.getOwner() != null) map.put("owner", dto.getOwner());
        if (dto.getReassignable() != null) map.put("reassignable", dto.getReassignable());
        if (dto.getDelegatable() != null) map.put("delegatable", dto.getDelegatable());
        if (dto.getTransferable() != null) map.put("transferable", dto.getTransferable());
        if (dto.getExtendedAttributes() != null) map.putAll(dto.getExtendedAttributes());
        return map;
    }

    private RightDTO convertMapToRight(Map<String, Object> map) {
        RightDTO dto = new RightDTO();
        if (map.get("moveSponsorTo") != null) dto.setMoveSponsorTo((String) map.get("moveSponsorTo"));
        if (map.get("performer") != null) dto.setPerformer((String) map.get("performer"));
        if (map.get("performerType") != null) dto.setPerformerType((String) map.get("performerType"));
        if (map.get("participationType") != null) dto.setParticipationType((String) map.get("participationType"));
        if (map.get("participationScope") != null) dto.setParticipationScope((String) map.get("participationScope"));
        if (map.get("participationScopeValue") != null) dto.setParticipationScopeValue((String) map.get("participationScopeValue"));
        if (map.get("candidateUsers") != null) dto.setCandidateUsers((List<String>) map.get("candidateUsers"));
        if (map.get("candidateGroups") != null) dto.setCandidateGroups((List<String>) map.get("candidateGroups"));
        if (map.get("candidateRoles") != null) dto.setCandidateRoles((List<String>) map.get("candidateRoles"));
        if (map.get("assignee") != null) dto.setAssignee((String) map.get("assignee"));
        if (map.get("owner") != null) dto.setOwner((String) map.get("owner"));
        if (map.get("reassignable") != null) dto.setReassignable((Boolean) map.get("reassignable"));
        if (map.get("delegatable") != null) dto.setDelegatable((Boolean) map.get("delegatable"));
        if (map.get("transferable") != null) dto.setTransferable((Boolean) map.get("transferable"));
        return dto;
    }

    private Map<String, Object> convertSubFlowToMap(SubFlowDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getSubProcessDefId() != null) map.put("subProcessDefId", dto.getSubProcessDefId());
        if (dto.getSubProcessName() != null) map.put("subProcessName", dto.getSubProcessName());
        if (dto.getSubProcessVersion() != null) map.put("subProcessVersion", dto.getSubProcessVersion());
        if (dto.getExecutionMode() != null) map.put("executionMode", dto.getExecutionMode());
        if (dto.getWaitForCompletion() != null) map.put("waitForCompletion", dto.getWaitForCompletion());
        if (dto.getDataMapping() != null) map.put("dataMapping", dto.getDataMapping());
        if (dto.getParameterMapping() != null) map.put("parameterMapping", dto.getParameterMapping());
        if (dto.getExtendedAttributes() != null) map.putAll(dto.getExtendedAttributes());
        return map;
    }

    private SubFlowDTO convertMapToSubFlow(Map<String, Object> map) {
        SubFlowDTO dto = new SubFlowDTO();
        if (map.get("subProcessDefId") != null) dto.setSubProcessDefId((String) map.get("subProcessDefId"));
        if (map.get("subProcessName") != null) dto.setSubProcessName((String) map.get("subProcessName"));
        if (map.get("subProcessVersion") != null) dto.setSubProcessVersion((String) map.get("subProcessVersion"));
        if (map.get("executionMode") != null) dto.setExecutionMode((String) map.get("executionMode"));
        if (map.get("waitForCompletion") != null) dto.setWaitForCompletion((Boolean) map.get("waitForCompletion"));
        if (map.get("dataMapping") != null) dto.setDataMapping((Map<String, Object>) map.get("dataMapping"));
        if (map.get("parameterMapping") != null) dto.setParameterMapping((Map<String, Object>) map.get("parameterMapping"));
        return dto;
    }

    private Map<String, Object> convertDeviceToMap(DeviceDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getDeviceId() != null) map.put("deviceId", dto.getDeviceId());
        if (dto.getDeviceName() != null) map.put("deviceName", dto.getDeviceName());
        if (dto.getDeviceType() != null) map.put("deviceType", dto.getDeviceType());
        if (dto.getDeviceModel() != null) map.put("deviceModel", dto.getDeviceModel());
        if (dto.getConnectionString() != null) map.put("connectionString", dto.getConnectionString());
        if (dto.getProtocol() != null) map.put("protocol", dto.getProtocol());
        if (dto.getParameters() != null) map.put("parameters", dto.getParameters());
        if (dto.getExtendedAttributes() != null) map.putAll(dto.getExtendedAttributes());
        return map;
    }

    private DeviceDTO convertMapToDevice(Map<String, Object> map) {
        DeviceDTO dto = new DeviceDTO();
        if (map.get("deviceId") != null) dto.setDeviceId((String) map.get("deviceId"));
        if (map.get("deviceName") != null) dto.setDeviceName((String) map.get("deviceName"));
        if (map.get("deviceType") != null) dto.setDeviceType((String) map.get("deviceType"));
        if (map.get("deviceModel") != null) dto.setDeviceModel((String) map.get("deviceModel"));
        if (map.get("connectionString") != null) dto.setConnectionString((String) map.get("connectionString"));
        if (map.get("protocol") != null) dto.setProtocol((String) map.get("protocol"));
        if (map.get("parameters") != null) dto.setParameters((Map<String, Object>) map.get("parameters"));
        return dto;
    }

    private Map<String, Object> convertServiceToMap(ServiceDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getServiceId() != null) map.put("serviceId", dto.getServiceId());
        if (dto.getServiceName() != null) map.put("serviceName", dto.getServiceName());
        if (dto.getServiceType() != null) map.put("serviceType", dto.getServiceType());
        if (dto.getServiceUrl() != null) map.put("serviceUrl", dto.getServiceUrl());
        if (dto.getServiceMethod() != null) map.put("serviceMethod", dto.getServiceMethod());
        if (dto.getServiceProtocol() != null) map.put("serviceProtocol", dto.getServiceProtocol());
        if (dto.getInputParameters() != null) map.put("inputParameters", dto.getInputParameters());
        if (dto.getOutputParameters() != null) map.put("outputParameters", dto.getOutputParameters());
        if (dto.getHeaders() != null) map.put("headers", dto.getHeaders());
        if (dto.getTimeout() != null) map.put("timeout", dto.getTimeout());
        if (dto.getRetryCount() != null) map.put("retryCount", dto.getRetryCount());
        if (dto.getExtendedAttributes() != null) map.putAll(dto.getExtendedAttributes());
        return map;
    }

    private ServiceDTO convertMapToService(Map<String, Object> map) {
        ServiceDTO dto = new ServiceDTO();
        if (map.get("serviceId") != null) dto.setServiceId((String) map.get("serviceId"));
        if (map.get("serviceName") != null) dto.setServiceName((String) map.get("serviceName"));
        if (map.get("serviceType") != null) dto.setServiceType((String) map.get("serviceType"));
        if (map.get("serviceUrl") != null) dto.setServiceUrl((String) map.get("serviceUrl"));
        if (map.get("serviceMethod") != null) dto.setServiceMethod((String) map.get("serviceMethod"));
        if (map.get("serviceProtocol") != null) dto.setServiceProtocol((String) map.get("serviceProtocol"));
        if (map.get("inputParameters") != null) dto.setInputParameters((Map<String, Object>) map.get("inputParameters"));
        if (map.get("outputParameters") != null) dto.setOutputParameters((Map<String, Object>) map.get("outputParameters"));
        if (map.get("headers") != null) dto.setHeaders((Map<String, String>) map.get("headers"));
        if (map.get("timeout") != null) dto.setTimeout(((Number) map.get("timeout")).intValue());
        if (map.get("retryCount") != null) dto.setRetryCount(((Number) map.get("retryCount")).intValue());
        return dto;
    }

    private Map<String, Object> convertEventToMap(EventDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getEventId() != null) map.put("eventId", dto.getEventId());
        if (dto.getEventName() != null) map.put("eventName", dto.getEventName());
        if (dto.getEventType() != null) map.put("eventType", dto.getEventType());
        if (dto.getEventTrigger() != null) map.put("eventTrigger", dto.getEventTrigger());
        if (dto.getTriggerCondition() != null) map.put("triggerCondition", dto.getTriggerCondition());
        if (dto.getEventAction() != null) map.put("eventAction", dto.getEventAction());
        if (dto.getActionParameters() != null) map.put("actionParameters", dto.getActionParameters());
        if (dto.getListenerClass() != null) map.put("listenerClass", dto.getListenerClass());
        if (dto.getListenerExpression() != null) map.put("listenerExpression", dto.getListenerExpression());
        if (dto.getExtendedAttributes() != null) map.putAll(dto.getExtendedAttributes());
        return map;
    }

    private EventDTO convertMapToEvent(Map<String, Object> map) {
        EventDTO dto = new EventDTO();
        if (map.get("eventId") != null) dto.setEventId((String) map.get("eventId"));
        if (map.get("eventName") != null) dto.setEventName((String) map.get("eventName"));
        if (map.get("eventType") != null) dto.setEventType((String) map.get("eventType"));
        if (map.get("eventTrigger") != null) dto.setEventTrigger((String) map.get("eventTrigger"));
        if (map.get("triggerCondition") != null) dto.setTriggerCondition((String) map.get("triggerCondition"));
        if (map.get("eventAction") != null) dto.setEventAction((String) map.get("eventAction"));
        if (map.get("actionParameters") != null) dto.setActionParameters((Map<String, Object>) map.get("actionParameters"));
        if (map.get("listenerClass") != null) dto.setListenerClass((String) map.get("listenerClass"));
        if (map.get("listenerExpression") != null) dto.setListenerExpression((String) map.get("listenerExpression"));
        return dto;
    }

    private Map<String, Object> convertAgentConfigToMap(AgentConfigDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getAgentId() != null) map.put("agentId", dto.getAgentId());
        if (dto.getAgentName() != null) map.put("agentName", dto.getAgentName());
        if (dto.getAgentType() != null) map.put("agentType", dto.getAgentType());
        if (dto.getModelName() != null) map.put("modelName", dto.getModelName());
        if (dto.getSystemPrompt() != null) map.put("systemPrompt", dto.getSystemPrompt());
        if (dto.getTemperature() != null) map.put("temperature", dto.getTemperature());
        if (dto.getMaxTokens() != null) map.put("maxTokens", dto.getMaxTokens());
        if (dto.getTools() != null) map.put("tools", dto.getTools());
        if (dto.getCapabilities() != null) map.put("capabilities", dto.getCapabilities());
        if (dto.getInputSchema() != null) map.put("inputSchema", dto.getInputSchema());
        if (dto.getOutputSchema() != null) map.put("outputSchema", dto.getOutputSchema());
        if (dto.getMemoryEnabled() != null) map.put("memoryEnabled", dto.getMemoryEnabled());
        if (dto.getContextWindow() != null) map.put("contextWindow", dto.getContextWindow());
        if (dto.getExtendedAttributes() != null) map.putAll(dto.getExtendedAttributes());
        return map;
    }

    private AgentConfigDTO convertMapToAgentConfig(Map<String, Object> map) {
        AgentConfigDTO dto = new AgentConfigDTO();
        if (map.get("agentId") != null) dto.setAgentId((String) map.get("agentId"));
        if (map.get("agentName") != null) dto.setAgentName((String) map.get("agentName"));
        if (map.get("agentType") != null) dto.setAgentType((String) map.get("agentType"));
        if (map.get("modelName") != null) dto.setModelName((String) map.get("modelName"));
        if (map.get("systemPrompt") != null) dto.setSystemPrompt((String) map.get("systemPrompt"));
        if (map.get("temperature") != null) dto.setTemperature(((Number) map.get("temperature")).doubleValue());
        if (map.get("maxTokens") != null) dto.setMaxTokens(((Number) map.get("maxTokens")).intValue());
        if (map.get("tools") != null) dto.setTools((List<String>) map.get("tools"));
        if (map.get("capabilities") != null) dto.setCapabilities((List<String>) map.get("capabilities"));
        if (map.get("inputSchema") != null) dto.setInputSchema((Map<String, Object>) map.get("inputSchema"));
        if (map.get("outputSchema") != null) dto.setOutputSchema((Map<String, Object>) map.get("outputSchema"));
        if (map.get("memoryEnabled") != null) dto.setMemoryEnabled((Boolean) map.get("memoryEnabled"));
        if (map.get("contextWindow") != null) dto.setContextWindow(((Number) map.get("contextWindow")).intValue());
        return dto;
    }

    private Map<String, Object> convertSceneConfigToMap(SceneConfigDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getSceneId() != null) map.put("sceneId", dto.getSceneId());
        if (dto.getSceneName() != null) map.put("sceneName", dto.getSceneName());
        if (dto.getSceneType() != null) map.put("sceneType", dto.getSceneType());
        if (dto.getSceneCategory() != null) map.put("sceneCategory", dto.getSceneCategory());
        if (dto.getTriggerEvents() != null) map.put("triggerEvents", dto.getTriggerEvents());
        if (dto.getEntryConditions() != null) map.put("entryConditions", dto.getEntryConditions());
        if (dto.getExitConditions() != null) map.put("exitConditions", dto.getExitConditions());
        if (dto.getSceneData() != null) map.put("sceneData", dto.getSceneData());
        if (dto.getSceneRules() != null) map.put("sceneRules", dto.getSceneRules());
        if (dto.getSceneState() != null) map.put("sceneState", dto.getSceneState());
        if (dto.getPriority() != null) map.put("priority", dto.getPriority());
        if (dto.getValidityPeriod() != null) map.put("validityPeriod", dto.getValidityPeriod());
        if (dto.getExtendedAttributes() != null) map.putAll(dto.getExtendedAttributes());
        return map;
    }

    private SceneConfigDTO convertMapToSceneConfig(Map<String, Object> map) {
        SceneConfigDTO dto = new SceneConfigDTO();
        if (map.get("sceneId") != null) dto.setSceneId((String) map.get("sceneId"));
        if (map.get("sceneName") != null) dto.setSceneName((String) map.get("sceneName"));
        if (map.get("sceneType") != null) dto.setSceneType((String) map.get("sceneType"));
        if (map.get("sceneCategory") != null) dto.setSceneCategory((String) map.get("sceneCategory"));
        if (map.get("triggerEvents") != null) dto.setTriggerEvents((List<String>) map.get("triggerEvents"));
        if (map.get("entryConditions") != null) dto.setEntryConditions((List<String>) map.get("entryConditions"));
        if (map.get("exitConditions") != null) dto.setExitConditions((List<String>) map.get("exitConditions"));
        if (map.get("sceneData") != null) dto.setSceneData((Map<String, Object>) map.get("sceneData"));
        if (map.get("sceneRules") != null) dto.setSceneRules((List<Map<String, Object>>) map.get("sceneRules"));
        if (map.get("sceneState") != null) dto.setSceneState((String) map.get("sceneState"));
        if (map.get("priority") != null) dto.setPriority(((Number) map.get("priority")).intValue());
        if (map.get("validityPeriod") != null) dto.setValidityPeriod((Map<String, String>) map.get("validityPeriod"));
        return dto;
    }

    /**
     * 获取流程列表 - 从服务端获取
     */
    public List<ProcessDef> getProcessList(String category, String status, int page, int size) {
        try {
            String url = bpmServerUrl + "/api/processdef/list";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> result = JSON.parseObject(response.getBody(), new TypeReference<Map<String, Object>>() {});
                if (result.get("data") != null) {
                    List<ProcessDTO> processDTOs = JSON.parseArray(JSON.toJSONString(result.get("data")), ProcessDTO.class);
                    List<ProcessDef> processes = new ArrayList<>();
                    for (ProcessDTO dto : processDTOs) {
                        processes.add(convertToProcessDef(dto));
                    }
                    return processes;
                }
            }
        } catch (Exception e) {
            System.err.println("[DesignerService] Failed to get process list: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * 删除流程 - 直接删除服务端数据
     */
    public void deleteProcess(String processId) {
        try {
            String url = bpmServerUrl + "/api/processdef/" + processId;
            restTemplate.delete(url);
            System.out.println("[DesignerService] Process deleted from server: " + processId);
        } catch (Exception e) {
            System.err.println("[DesignerService] Failed to delete process: " + e.getMessage());
            throw new RuntimeException("Failed to delete process", e);
        }
    }

    public String exportYaml(String processId) {
        return "";
    }

    public ProcessDef importYaml(String yaml) {
        return null;
    }

    public List<String> getCapabilities() {
        return Arrays.asList(
            "EMAIL", "CALENDAR", "DOCUMENT", "ANALYSIS", 
            "SEARCH", "NOTIFICATION", "APPROVAL", "SCHEDULING"
        );
    }

    // ==================== Controller需要的附加方法 ====================

    /**
     * 获取流程树结构 - 从服务端获取
     */
    public List<Map<String, Object>> getProcessTree() {
        List<Map<String, Object>> tree = new ArrayList<>();
        try {
            String url = bpmServerUrl + "/api/processdef/list";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> result = JSON.parseObject(response.getBody(), new TypeReference<Map<String, Object>>() {});
                if (result.get("data") != null) {
                    List<Map<String, Object>> processList = (List<Map<String, Object>>) result.get("data");
                    for (Map<String, Object> process : processList) {
                        Map<String, Object> node = new HashMap<>();
                        node.put("id", process.get("processDefId"));
                        node.put("name", process.get("name"));
                        node.put("type", "process");
                        node.put("status", process.get("publicationStatus"));
                        node.put("version", process.get("version"));
                        tree.add(node);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DesignerService] Failed to get process tree from server: " + e.getMessage());
        }
        return tree;
    }

    /**
     * 添加活动到流程 - 直接保存到服务端
     */
    public ProcessDef addActivity(String processId, Map<String, Object> activityDef) {
        ProcessDef process = getProcess(processId, null);
        if (process == null) {
            throw new RuntimeException("Process not found: " + processId);
        }
        
        ActivityDef activity = new ActivityDef();
        activity.setActivityDefId((String) activityDef.get("activityDefId"));
        activity.setName((String) activityDef.get("name"));
        activity.setDescription((String) activityDef.get("description"));
        
        Map<String, Object> positionCoord = (Map<String, Object>) activityDef.get("positionCoord");
        if (positionCoord != null) {
            activity.setPositionCoord(positionCoord);
        }
        
        process.getActivities().add(activity);
        return saveProcess(process);
    }

    /**
     * 更新流程中的活动 - 直接保存到服务端
     */
    public ProcessDef updateActivity(String processId, String activityId, Map<String, Object> activityDef) {
        ProcessDef process = getProcess(processId, null);
        if (process == null) {
            throw new RuntimeException("Process not found: " + processId);
        }
        
        ActivityDef existingActivity = null;
        for (ActivityDef activity : process.getActivities()) {
            if (activity.getActivityDefId().equals(activityId)) {
                existingActivity = activity;
                break;
            }
        }
        
        if (existingActivity == null) {
            throw new RuntimeException("Activity not found: " + activityId);
        }
        
        if (activityDef.get("name") != null) {
            existingActivity.setName((String) activityDef.get("name"));
        }
        if (activityDef.get("description") != null) {
            existingActivity.setDescription((String) activityDef.get("description"));
        }
        if (activityDef.get("positionCoord") != null) {
            existingActivity.setPositionCoord((Map<String, Object>) activityDef.get("positionCoord"));
        }
        
        return saveProcess(process);
    }

    /**
     * 从流程中移除活动 - 直接保存到服务端
     */
    public ProcessDef removeActivity(String processId, String activityId) {
        ProcessDef process = getProcess(processId, null);
        if (process == null) {
            throw new RuntimeException("Process not found: " + processId);
        }
        
        process.getActivities().removeIf(activity -> activity.getActivityDefId().equals(activityId));
        return saveProcess(process);
    }

    /**
     * 添加路由到流程 - 直接保存到服务端
     */
    public ProcessDef addRoute(String processId, Map<String, Object> routeDef) {
        ProcessDef process = getProcess(processId, null);
        if (process == null) {
            throw new RuntimeException("Process not found: " + processId);
        }
        
        RouteDef route = new RouteDef();
        route.setRouteDefId((String) routeDef.get("routeDefId"));
        route.setName((String) routeDef.get("name"));
        route.setFrom((String) routeDef.get("fromActivityId"));
        route.setTo((String) routeDef.get("toActivityId"));
        route.setCondition((String) routeDef.get("condition"));
        
        process.getRoutes().add(route);
        return saveProcess(process);
    }

    /**
     * 更新流程中的路由 - 直接保存到服务端
     */
    public ProcessDef updateRoute(String processId, String routeId, Map<String, Object> routeDef) {
        ProcessDef process = getProcess(processId, null);
        if (process == null) {
            throw new RuntimeException("Process not found: " + processId);
        }
        
        RouteDef existingRoute = null;
        for (RouteDef route : process.getRoutes()) {
            if (route.getRouteDefId().equals(routeId)) {
                existingRoute = route;
                break;
            }
        }
        
        if (existingRoute == null) {
            throw new RuntimeException("Route not found: " + routeId);
        }
        
        if (routeDef.get("name") != null) {
            existingRoute.setName((String) routeDef.get("name"));
        }
        if (routeDef.get("fromActivityId") != null) {
            existingRoute.setFrom((String) routeDef.get("fromActivityId"));
        }
        if (routeDef.get("toActivityId") != null) {
            existingRoute.setTo((String) routeDef.get("toActivityId"));
        }
        if (routeDef.get("condition") != null) {
            existingRoute.setCondition((String) routeDef.get("condition"));
        }
        
        return saveProcess(process);
    }

    /**
     * 从流程中移除路由 - 直接保存到服务端
     */
    public ProcessDef removeRoute(String processId, String routeId) {
        ProcessDef process = getProcess(processId, null);
        if (process == null) {
            throw new RuntimeException("Process not found: " + processId);
        }
        
        process.getRoutes().removeIf(route -> route.getRouteDefId().equals(routeId));
        return saveProcess(process);
    }

    /**
     * 获取枚举选项
     */
    public List<Map<String, String>> getEnumOptions(String enumType) {
        List<Map<String, String>> options = new ArrayList<>();
        
        switch (enumType) {
            case "ActivityPosition":
                for (ActivityPosition pos : ActivityPosition.values()) {
                    Map<String, String> option = new HashMap<>();
                    option.put("value", pos.getCode());
                    option.put("label", pos.getLabel());
                    options.add(option);
                }
                break;
            case "ActivityType":
                for (ActivityType type : ActivityType.values()) {
                    Map<String, String> option = new HashMap<>();
                    option.put("value", type.getCode());
                    option.put("label", type.getLabel());
                    options.add(option);
                }
                break;
            case "ActivityCategory":
                for (ActivityCategory cat : ActivityCategory.values()) {
                    Map<String, String> option = new HashMap<>();
                    option.put("value", cat.getCode());
                    option.put("label", cat.getLabel());
                    options.add(option);
                }
                break;
            default:
                break;
        }
        
        return options;
    }
}

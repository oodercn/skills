package net.ooder.bpm.designer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.bpm.designer.model.ActivityDef;
import net.ooder.bpm.designer.model.ProcessDef;
import net.ooder.bpm.designer.model.RouteDef;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DesignerService {

    @Value("${bpm.server.url:http://localhost:8080}")
    private String bpmServerUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, ProcessDef> localCache = new ConcurrentHashMap<>();

    public ProcessDef getProcess(String processId, String version) {
        try {
            String url = bpmServerUrl + "/api/processdef/" + processId;
            System.out.println("[DesignerService] Fetching process from: " + url);
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                System.out.println("[DesignerService] Response: " + body);
                if (body.get("data") != null) {
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    return parseBpmServerProcessData(data);
                }
            }
        } catch (Exception e) {
            System.out.println("[DesignerService] Failed to get process from bpmserver: " + e.getMessage());
            e.printStackTrace();
            return localCache.get(processId);
        }
        throw new RuntimeException("Process not found: " + processId);
    }

    private ProcessDef parseBpmServerProcessData(Map<String, Object> data) {
        ProcessDef process = new ProcessDef();
        process.setProcessDefId((String) data.get("processDefId"));
        process.setName((String) data.get("name"));
        process.setDescription((String) data.get("description"));
        process.setCategory((String) data.get("category"));
        process.setAccessLevel((String) data.get("accessLevel"));
        process.setStatus((String) data.get("status"));
        if (data.get("version") != null) {
            process.setVersion(((Number) data.get("version")).intValue());
        }
        
        List<Map<String, Object>> activities = (List<Map<String, Object>>) data.get("activities");
        if (activities != null) {
            for (Map<String, Object> a : activities) {
                ActivityDef activity = new ActivityDef();
                activity.setActivityDefId((String) a.get("activityDefId"));
                activity.setName((String) a.get("name"));
                activity.setDescription((String) a.get("description"));
                activity.setPosition((String) a.get("position"));
                activity.setActivityType((String) a.get("activityType"));
                activity.setActivityCategory((String) a.get("activityCategory"));
                activity.setImplementation((String) a.get("implementation"));
                
                Map<String, Object> posCoord = (Map<String, Object>) a.get("positionCoord");
                if (posCoord != null) {
                    activity.setPositionCoord(posCoord);
                } else {
                    Map<String, Object> defaultCoord = new HashMap<>();
                    defaultCoord.put("x", 100 + process.getActivities().size() * 150);
                    defaultCoord.put("y", 100);
                    activity.setPositionCoord(defaultCoord);
                }
                
                process.getActivities().add(activity);
            }
        }
        
        List<Map<String, Object>> routes = (List<Map<String, Object>>) data.get("routes");
        if (routes != null) {
            for (Map<String, Object> r : routes) {
                RouteDef route = new RouteDef();
                route.setRouteDefId((String) r.get("routeDefId"));
                route.setName((String) r.get("name"));
                route.setFrom((String) r.get("from"));
                route.setTo((String) r.get("to"));
                route.setCondition((String) r.get("condition"));
                route.setRouteDirection((String) r.get("routeDirection"));
                process.getRoutes().add(route);
            }
        }
        
        localCache.put(process.getProcessDefId(), process);
        return process;
    }

    public List<ProcessDef> getProcessList(String category, String status, int page, int size) {
        try {
            String url = bpmServerUrl + "/api/bpm/process?category=" + (category != null ? category : "") 
                + "&status=" + (status != null ? status : "") 
                + "&page=" + page + "&size=" + size;
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<ProcessDef> result = new ArrayList<>();
                for (Object item : response.getBody()) {
                    if (item instanceof Map) {
                        result.add(mapToProcessDef((Map) item));
                    }
                }
                return result;
            }
        } catch (Exception e) {
            return new ArrayList<>(localCache.values());
        }
        return new ArrayList<>();
    }

    public ProcessDef saveProcess(ProcessDef processDef) {
        try {
            String url = bpmServerUrl + "/api/processdef/save";
            System.out.println("[DesignerService] Saving process to: " + url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> processData = processDefToMap(processDef);
            HttpEntity<Map> request = new HttpEntity<>(processData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                System.out.println("[DesignerService] Save response: " + body);
                if (body.get("data") != null) {
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    return parseBpmServerProcessData(data);
                }
            }
        } catch (Exception e) {
            System.out.println("[DesignerService] Failed to save process to bpmserver: " + e.getMessage());
            e.printStackTrace();
        }
        
        localCache.put(processDef.getProcessDefId(), processDef);
        System.out.println("[DesignerService] Saved process to local cache: " + processDef.getProcessDefId());
        return processDef;
    }

    public void deleteProcess(String processId) {
        try {
            String url = bpmServerUrl + "/api/bpm/process/" + processId;
            restTemplate.delete(url);
        } catch (Exception e) {
        }
        localCache.remove(processId);
    }

    public String exportYaml(String processId) {
        try {
            String url = bpmServerUrl + "/api/bpm/process/" + processId + "/export/yaml";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            ProcessDef process = localCache.get(processId);
            if (process != null) {
                return processToYaml(process);
            }
        }
        return "";
    }

    public ProcessDef importYaml(String yaml) {
        try {
            String url = bpmServerUrl + "/api/bpm/process/import/yaml";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = new HashMap<>();
            body.put("yaml", yaml);
            HttpEntity<Map> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return mapToProcessDef(response.getBody());
            }
        } catch (Exception e) {
            return yamlToProcess(yaml);
        }
        throw new RuntimeException("Failed to import YAML");
    }

    public ProcessDef addActivity(String processId, Map<String, Object> activityDef) {
        try {
            String url = bpmServerUrl + "/api/bpm/process/" + processId + "/activity";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map> request = new HttpEntity<>(activityDef, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ProcessDef process = mapToProcessDef(response.getBody());
                localCache.put(processId, process);
                return process;
            }
        } catch (Exception e) {
            ProcessDef process = localCache.get(processId);
            if (process != null) {
                process.getActivities().add(mapToActivity(activityDef));
                return process;
            }
        }
        throw new RuntimeException("Failed to add activity");
    }

    public ProcessDef updateActivity(String processId, String activityId, Map<String, Object> activityDef) {
        try {
            String url = bpmServerUrl + "/api/bpm/process/" + processId + "/activity/" + activityId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map> request = new HttpEntity<>(activityDef, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ProcessDef process = mapToProcessDef(response.getBody());
                localCache.put(processId, process);
                return process;
            }
        } catch (Exception e) {
            ProcessDef process = localCache.get(processId);
            if (process != null) {
                for (int i = 0; i < process.getActivities().size(); i++) {
                    if (process.getActivities().get(i).getActivityDefId().equals(activityId)) {
                        ActivityDef activity = mapToActivity(activityDef);
                        activity.setActivityDefId(activityId);
                        process.getActivities().set(i, activity);
                        break;
                    }
                }
                return process;
            }
        }
        throw new RuntimeException("Failed to update activity");
    }

    public ProcessDef removeActivity(String processId, String activityId) {
        try {
            String url = bpmServerUrl + "/api/bpm/process/" + processId + "/activity/" + activityId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ProcessDef process = mapToProcessDef(response.getBody());
                localCache.put(processId, process);
                return process;
            }
        } catch (Exception e) {
            ProcessDef process = localCache.get(processId);
            if (process != null) {
                process.getActivities().removeIf(a -> a.getActivityDefId().equals(activityId));
                process.getRoutes().removeIf(r -> 
                    r.getFrom().equals(activityId) || r.getTo().equals(activityId));
                return process;
            }
        }
        throw new RuntimeException("Failed to remove activity");
    }

    public ProcessDef addRoute(String processId, Map<String, Object> routeDef) {
        try {
            String url = bpmServerUrl + "/api/bpm/process/" + processId + "/route";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map> request = new HttpEntity<>(routeDef, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ProcessDef process = mapToProcessDef(response.getBody());
                localCache.put(processId, process);
                return process;
            }
        } catch (Exception e) {
            ProcessDef process = localCache.get(processId);
            if (process != null) {
                process.getRoutes().add(mapToRoute(routeDef));
                return process;
            }
        }
        throw new RuntimeException("Failed to add route");
    }

    public ProcessDef updateRoute(String processId, String routeId, Map<String, Object> routeDef) {
        try {
            String url = bpmServerUrl + "/api/bpm/process/" + processId + "/route/" + routeId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map> request = new HttpEntity<>(routeDef, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ProcessDef process = mapToProcessDef(response.getBody());
                localCache.put(processId, process);
                return process;
            }
        } catch (Exception e) {
            ProcessDef process = localCache.get(processId);
            if (process != null) {
                for (int i = 0; i < process.getRoutes().size(); i++) {
                    if (process.getRoutes().get(i).getRouteDefId().equals(routeId)) {
                        RouteDef route = mapToRoute(routeDef);
                        route.setRouteDefId(routeId);
                        process.getRoutes().set(i, route);
                        break;
                    }
                }
                return process;
            }
        }
        throw new RuntimeException("Failed to update route");
    }

    public ProcessDef removeRoute(String processId, String routeId) {
        try {
            String url = bpmServerUrl + "/api/bpm/process/" + processId + "/route/" + routeId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ProcessDef process = mapToProcessDef(response.getBody());
                localCache.put(processId, process);
                return process;
            }
        } catch (Exception e) {
            ProcessDef process = localCache.get(processId);
            if (process != null) {
                process.getRoutes().removeIf(r -> r.getRouteDefId().equals(routeId));
                return process;
            }
        }
        throw new RuntimeException("Failed to remove route");
    }

    public List<String> getCapabilities() {
        return Arrays.asList(
            "EMAIL", "CALENDAR", "DOCUMENT", "ANALYSIS", 
            "SEARCH", "NOTIFICATION", "APPROVAL", "SCHEDULING"
        );
    }

    public List<Map<String, Object>> getProcessTree() {
        try {
            String url = bpmServerUrl + "/api/processdef/list";
            System.out.println("[DesignerService] Fetching process list from: " + url);
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                System.out.println("[DesignerService] Response: " + body);
                if (body.get("data") != null) {
                    List<Map<String, Object>> processes = (List<Map<String, Object>>) body.get("data");
                    List<Map<String, Object>> tree = new ArrayList<>();
                    for (Map<String, Object> p : processes) {
                        Map<String, Object> node = new HashMap<>();
                        node.put("id", p.get("PROCESSDEF_ID") != null ? p.get("PROCESSDEF_ID") : p.get("processDefId"));
                        node.put("name", p.get("DEFNAME") != null ? p.get("DEFNAME") : p.get("name"));
                        node.put("type", "process");
                        node.put("status", p.get("STATUS") != null ? p.get("STATUS") : p.get("status"));
                        tree.add(node);
                    }
                    return tree;
                }
            }
        } catch (Exception e) {
            System.out.println("[DesignerService] Failed to get process list from bpmserver: " + e.getMessage());
            e.printStackTrace();
        }
        
        List<Map<String, Object>> tree = new ArrayList<>();
        for (ProcessDef process : localCache.values()) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", process.getProcessDefId());
            node.put("name", process.getName());
            node.put("type", "process");
            node.put("status", process.getStatus());
            node.put("version", process.getVersion());
            tree.add(node);
        }
        
        return tree;
    }

    public List<Map<String, String>> getEnumOptions(String enumType) {
        List<Map<String, String>> options = new ArrayList<>();
        switch (enumType) {
            case "accessLevel":
                options.add(createOption("Public", "独立启动"));
                options.add(createOption("Private", "子流程"));
                options.add(createOption("Block", "流程块"));
                break;
            case "publicationStatus":
                options.add(createOption("UNDER_REVISION", "修订中"));
                options.add(createOption("RELEASED", "已发布"));
                options.add(createOption("UNDER_TEST", "测试中"));
                break;
            case "position":
                options.add(createOption("START", "起始活动"));
                options.add(createOption("NORMAL", "普通活动"));
                options.add(createOption("END", "结束活动"));
                break;
            case "category":
                options.add(createOption("HUMAN", "人工活动"));
                options.add(createOption("AGENT", "Agent活动"));
                options.add(createOption("SCENE", "场景活动"));
                break;
            case "implementation":
                options.add(createOption("IMPL_NO", "手动活动"));
                options.add(createOption("IMPL_TOOL", "自动活动"));
                options.add(createOption("IMPL_SUBFLOW", "子流程活动"));
                options.add(createOption("IMPL_OUTFLOW", "跳转流程活动"));
                options.add(createOption("IMPL_DEVICE", "设备活动"));
                options.add(createOption("IMPL_EVENT", "事件活动"));
                options.add(createOption("IMPL_SERVICE", "服务活动"));
                break;
            case "durationUnit":
                options.add(createOption("Y", "年"));
                options.add(createOption("M", "月"));
                options.add(createOption("D", "日"));
                options.add(createOption("H", "时"));
                options.add(createOption("m", "分"));
                options.add(createOption("s", "秒"));
                options.add(createOption("W", "工作日"));
                break;
            case "deadlineOperation":
                options.add(createOption("DEFAULT", "默认处理"));
                options.add(createOption("DELAY", "延期办理"));
                options.add(createOption("TAKEBACK", "自动收回"));
                options.add(createOption("SURROGATE", "代办人自动接收"));
                break;
            case "join":
                options.add(createOption("DEFAULT", "默认"));
                options.add(createOption("AND", "AND 合并"));
                options.add(createOption("XOR", "XOR 合并"));
                break;
            case "split":
                options.add(createOption("DEFAULT", "默认"));
                options.add(createOption("AND", "AND 并行"));
                options.add(createOption("XOR", "XOR 并行"));
                break;
            case "performType":
                options.add(createOption("SINGLE", "单人办理"));
                options.add(createOption("MULTIPLE", "多人办理"));
                options.add(createOption("JOINTSIGN", "会签办理"));
                options.add(createOption("NEEDNOTSELECT", "无需选择"));
                options.add(createOption("NOSELECT", "不需要选择"));
                break;
            case "performSequence":
                options.add(createOption("FIRST", "抢占办理"));
                options.add(createOption("SEQUENCE", "顺序办理"));
                options.add(createOption("MEANWHILE", "同时办理"));
                options.add(createOption("AUTOSIGN", "自动签收"));
                break;
            case "agentType":
                options.add(createOption("LLM", "大语言模型"));
                options.add(createOption("TASK", "任务执行"));
                options.add(createOption("EVENT", "事件触发"));
                options.add(createOption("HYBRID", "混合模式"));
                options.add(createOption("COORDINATOR", "协调器"));
                options.add(createOption("TOOL", "工具调用"));
                break;
            case "scheduleStrategy":
                options.add(createOption("SEQUENTIAL", "顺序执行"));
                options.add(createOption("PARALLEL", "并行执行"));
                options.add(createOption("CONDITIONAL", "条件执行"));
                options.add(createOption("ROUND_ROBIN", "轮询执行"));
                options.add(createOption("PRIORITY", "优先级执行"));
                break;
            case "collaborationMode":
                options.add(createOption("SOLO", "独立模式"));
                options.add(createOption("HIERARCHICAL", "层级模式"));
                options.add(createOption("PEER", "对等模式"));
                options.add(createOption("DEBATE", "辩论模式"));
                options.add(createOption("VOTING", "投票模式"));
                break;
        }
        return options;
    }

    private Map<String, String> createOption(String value, String label) {
        Map<String, String> option = new HashMap<>();
        option.put("value", value);
        option.put("label", label);
        return option;
    }

    private ProcessDef mapToProcessDef(Map<String, Object> map) {
        ProcessDef process = new ProcessDef();
        process.setProcessDefId((String) map.get("processDefId"));
        process.setName((String) map.get("name"));
        process.setDescription((String) map.get("description"));
        process.setCategory((String) map.get("category"));
        process.setAccessLevel((String) map.get("accessLevel"));
        process.setStatus((String) map.get("status"));
        if (map.get("version") != null) {
            process.setVersion(((Number) map.get("version")).intValue());
        }
        
        if (map.get("activities") != null) {
            List<Map<String, Object>> activities = (List<Map<String, Object>>) map.get("activities");
            for (Map<String, Object> a : activities) {
                process.getActivities().add(mapToActivity(a));
            }
        }
        
        if (map.get("routes") != null) {
            List<Map<String, Object>> routes = (List<Map<String, Object>>) map.get("routes");
            for (Map<String, Object> r : routes) {
                process.getRoutes().add(mapToRoute(r));
            }
        }
        
        return process;
    }

    private Map<String, Object> processDefToMap(ProcessDef process) {
        Map<String, Object> map = new HashMap<>();
        map.put("processDefId", process.getProcessDefId());
        map.put("name", process.getName());
        map.put("description", process.getDescription());
        map.put("category", process.getCategory());
        map.put("accessLevel", process.getAccessLevel());
        map.put("status", process.getStatus());
        map.put("version", process.getVersion());
        
        List<Map<String, Object>> activities = new ArrayList<>();
        for (ActivityDef a : process.getActivities()) {
            activities.add(activityToMap(a));
        }
        map.put("activities", activities);
        
        List<Map<String, Object>> routes = new ArrayList<>();
        for (RouteDef r : process.getRoutes()) {
            routes.add(routeToMap(r));
        }
        map.put("routes", routes);
        
        return map;
    }

    private ActivityDef mapToActivity(Map<String, Object> map) {
        ActivityDef activity = new ActivityDef();
        activity.setActivityDefId((String) map.get("activityDefId"));
        activity.setName((String) map.get("name"));
        activity.setDescription((String) map.get("description"));
        activity.setPosition((String) map.getOrDefault("position", "NORMAL"));
        activity.setActivityType((String) map.get("activityType"));
        activity.setActivityCategory((String) map.get("activityCategory"));
        activity.setImplementation((String) map.get("implementation"));
        activity.setExecClass((String) map.get("execClass"));
        
        if (map.get("positionCoord") != null) {
            activity.setPositionCoord((Map<String, Object>) map.get("positionCoord"));
        }
        if (map.get("timing") != null) {
            activity.setTiming((Map<String, Object>) map.get("timing"));
        }
        if (map.get("routing") != null) {
            activity.setRouting((Map<String, Object>) map.get("routing"));
        }
        if (map.get("right") != null) {
            activity.setRight((Map<String, Object>) map.get("right"));
        }
        if (map.get("subFlow") != null) {
            activity.setSubFlow((Map<String, Object>) map.get("subFlow"));
        }
        if (map.get("device") != null) {
            activity.setDevice((Map<String, Object>) map.get("device"));
        }
        if (map.get("service") != null) {
            activity.setService((Map<String, Object>) map.get("service"));
        }
        if (map.get("event") != null) {
            activity.setEvent((Map<String, Object>) map.get("event"));
        }
        if (map.get("agentConfig") != null) {
            activity.setAgentConfig((Map<String, Object>) map.get("agentConfig"));
        }
        if (map.get("sceneConfig") != null) {
            activity.setSceneConfig((Map<String, Object>) map.get("sceneConfig"));
        }
        
        return activity;
    }

    private Map<String, Object> activityToMap(ActivityDef activity) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityDefId", activity.getActivityDefId());
        map.put("name", activity.getName());
        map.put("description", activity.getDescription());
        map.put("position", activity.getPosition());
        map.put("activityType", activity.getActivityType());
        map.put("activityCategory", activity.getActivityCategory());
        map.put("implementation", activity.getImplementation());
        map.put("execClass", activity.getExecClass());
        map.put("positionCoord", activity.getPositionCoord());
        map.put("timing", activity.getTiming());
        map.put("routing", activity.getRouting());
        map.put("right", activity.getRight());
        map.put("subFlow", activity.getSubFlow());
        map.put("device", activity.getDevice());
        map.put("service", activity.getService());
        map.put("event", activity.getEvent());
        map.put("agentConfig", activity.getAgentConfig());
        map.put("sceneConfig", activity.getSceneConfig());
        return map;
    }

    private RouteDef mapToRoute(Map<String, Object> map) {
        RouteDef route = new RouteDef();
        route.setRouteDefId((String) map.get("routeDefId"));
        route.setName((String) map.get("name"));
        route.setFrom((String) map.get("from"));
        route.setTo((String) map.get("to"));
        route.setCondition((String) map.get("condition"));
        route.setRouteDirection((String) map.get("routeDirection"));
        route.setRouteConditionType((String) map.get("routeConditionType"));
        return route;
    }

    private Map<String, Object> routeToMap(RouteDef route) {
        Map<String, Object> map = new HashMap<>();
        map.put("routeDefId", route.getRouteDefId());
        map.put("name", route.getName());
        map.put("from", route.getFrom());
        map.put("to", route.getTo());
        map.put("condition", route.getCondition());
        map.put("routeDirection", route.getRouteDirection());
        map.put("routeConditionType", route.getRouteConditionType());
        return map;
    }

    private String processToYaml(ProcessDef process) {
        StringBuilder sb = new StringBuilder();
        sb.append("apiVersion: bpm.ooder.net/v1\n");
        sb.append("kind: ProcessDef\n");
        sb.append("metadata:\n");
        sb.append("  id: ").append(process.getProcessDefId()).append("\n");
        sb.append("  name: ").append(escapeYaml(process.getName())).append("\n");
        sb.append("  description: ").append(escapeYaml(process.getDescription())).append("\n");
        sb.append("  classification: ").append(process.getCategory() != null ? process.getCategory() : "").append("\n");
        sb.append("spec:\n");
        sb.append("  accessLevel: ").append(process.getAccessLevel()).append("\n");
        sb.append("  version:\n");
        sb.append("    version: ").append(process.getVersion()).append("\n");
        sb.append("    publicationStatus: ").append(process.getStatus()).append("\n");
        sb.append("\n  activities:\n");
        for (ActivityDef a : process.getActivities()) {
            sb.append("    - id: ").append(a.getActivityDefId()).append("\n");
            sb.append("      name: ").append(escapeYaml(a.getName())).append("\n");
            sb.append("      position: ").append(a.getPosition()).append("\n");
            sb.append("      category: ").append(a.getActivityCategory()).append("\n");
            sb.append("      implementation: ").append(a.getImplementation()).append("\n");
            if (a.getPositionCoord() != null && !a.getPositionCoord().isEmpty()) {
                sb.append("      positionCoord:\n");
                sb.append("        x: ").append(a.getPositionCoord().get("x")).append("\n");
                sb.append("        y: ").append(a.getPositionCoord().get("y")).append("\n");
            }
        }
        sb.append("\n  routes:\n");
        for (RouteDef r : process.getRoutes()) {
            sb.append("    - id: ").append(r.getRouteDefId()).append("\n");
            sb.append("      name: ").append(escapeYaml(r.getName())).append("\n");
            sb.append("      connection:\n");
            sb.append("        from: ").append(r.getFrom()).append("\n");
            sb.append("        to: ").append(r.getTo()).append("\n");
            if (r.getCondition() != null) {
                sb.append("      condition:\n");
                sb.append("        routeCondition: ").append(escapeYaml(r.getCondition())).append("\n");
            }
        }
        return sb.toString();
    }

    private String escapeYaml(String value) {
        if (value == null) return "";
        if (value.contains(":") || value.contains("#") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\\\"") + "\"";
        }
        return value;
    }

    private ProcessDef yamlToProcess(String yaml) {
        ProcessDef process = new ProcessDef();
        String[] lines = yaml.split("\n");
        String currentSection = "";
        ActivityDef currentActivity = null;
        RouteDef currentRoute = null;
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            
            if (trimmed.equals("metadata:")) {
                currentSection = "metadata";
                continue;
            } else if (trimmed.equals("spec:")) {
                currentSection = "spec";
                continue;
            } else if (trimmed.equals("activities:")) {
                currentSection = "activities";
                continue;
            } else if (trimmed.equals("routes:")) {
                currentSection = "routes";
                continue;
            }
            
            if (currentSection.equals("metadata")) {
                if (trimmed.startsWith("id:")) {
                    process.setProcessDefId(trimmed.substring(3).trim());
                } else if (trimmed.startsWith("name:")) {
                    process.setName(trimmed.substring(5).trim());
                } else if (trimmed.startsWith("description:")) {
                    process.setDescription(trimmed.substring(12).trim());
                } else if (trimmed.startsWith("classification:")) {
                    process.setCategory(trimmed.substring(14).trim());
                }
            } else if (currentSection.equals("spec")) {
                if (trimmed.startsWith("accessLevel:")) {
                    process.setAccessLevel(trimmed.substring(12).trim());
                } else if (trimmed.startsWith("version:")) {
                    process.setVersion(Integer.parseInt(trimmed.substring(8).trim()));
                } else if (trimmed.startsWith("publicationStatus:")) {
                    process.setStatus(trimmed.substring(18).trim());
                }
            } else if (currentSection.equals("activities")) {
                if (trimmed.startsWith("- id:")) {
                    if (currentActivity != null) {
                        process.getActivities().add(currentActivity);
                    }
                    currentActivity = new ActivityDef();
                    currentActivity.setActivityDefId(trimmed.substring(5).trim());
                } else if (currentActivity != null) {
                    if (trimmed.startsWith("name:")) {
                        currentActivity.setName(trimmed.substring(5).trim());
                    } else if (trimmed.startsWith("position:")) {
                        currentActivity.setPosition(trimmed.substring(9).trim());
                    } else if (trimmed.startsWith("category:")) {
                        currentActivity.setActivityCategory(trimmed.substring(9).trim());
                    } else if (trimmed.startsWith("implementation:")) {
                        currentActivity.setImplementation(trimmed.substring(15).trim());
                    } else if (trimmed.startsWith("x:")) {
                        currentActivity.getPositionCoord().put("x", Integer.parseInt(trimmed.substring(2).trim()));
                    } else if (trimmed.startsWith("y:")) {
                        currentActivity.getPositionCoord().put("y", Integer.parseInt(trimmed.substring(2).trim()));
                    }
                }
            } else if (currentSection.equals("routes")) {
                if (trimmed.startsWith("- id:")) {
                    if (currentRoute != null) {
                        process.getRoutes().add(currentRoute);
                    }
                    currentRoute = new RouteDef();
                    currentRoute.setRouteDefId(trimmed.substring(5).trim());
                } else if (currentRoute != null) {
                    if (trimmed.startsWith("name:")) {
                        currentRoute.setName(trimmed.substring(5).trim());
                    } else if (trimmed.startsWith("from:")) {
                        currentRoute.setFrom(trimmed.substring(5).trim());
                    } else if (trimmed.startsWith("to:")) {
                        currentRoute.setTo(trimmed.substring(3).trim());
                    } else if (trimmed.startsWith("routeCondition:")) {
                        currentRoute.setCondition(trimmed.substring(15).trim());
                    }
                }
            }
        }
        
        if (currentActivity != null) {
            process.getActivities().add(currentActivity);
        }
        if (currentRoute != null) {
            process.getRoutes().add(currentRoute);
        }
        
        return process;
    }
}

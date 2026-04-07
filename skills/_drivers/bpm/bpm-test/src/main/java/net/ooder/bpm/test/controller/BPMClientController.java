package net.ooder.bpm.test.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;
import net.ooder.config.ResultModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/bpm")
public class BPMClientController {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public BPMClientController(RestTemplate restTemplate, @Value("${bpm.server.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @GetMapping("/processdef/get")
    public ResultModel getProcessDef(@RequestParam String processDefId) {
        String url = baseUrl + "/api/processdef/get?processDefId=" + processDefId;
        return callBpmServer(url);
    }

    @GetMapping("/processdef/list")
    public ResultModel listProcessDefs() {
        String url = baseUrl + "/api/processdef/list";
        return callBpmServerList(url);
    }

    @GetMapping("/processdef/full")
    public ResultModel getFullProcessDef(@RequestParam String processDefId) {
        String url = baseUrl + "/api/processdef/full?processDefId=" + processDefId;
        return callBpmServer(url);
    }

    @GetMapping("/activitydef/get")
    public ResultModel getActivityDef(@RequestParam String activityDefId) {
        String url = baseUrl + "/api/processdef/activity/get?activityDefId=" + activityDefId;
        return callBpmServer(url);
    }

    @GetMapping("/routedef/get")
    public ResultModel getRouteDef(@RequestParam String routeDefId) {
        String url = baseUrl + "/api/processdef/route/get?routeDefId=" + routeDefId;
        return callBpmServer(url);
    }

    @PostMapping("/processinst/new")
    public ResultModel newProcess(
            @RequestParam String processDefId,
            @RequestParam String processInstName,
            @RequestParam(required = false, defaultValue = "normal") String urgency,
            @RequestParam(required = false, defaultValue = "user1") String userId) {
        String url = baseUrl + "/api/processinst/new?processDefId=" + processDefId + 
                     "&processInstName=" + processInstName + 
                     "&urgency=" + urgency +
                     "&userId=" + userId;
        return callBpmServerPost(url);
    }

    @GetMapping("/processinst/get")
    public ResultModel getProcessInst(@RequestParam String processInstId) {
        String url = baseUrl + "/api/processinst/get?processInstId=" + processInstId;
        return callBpmServer(url);
    }

    @GetMapping("/processinst/list")
    public ResultModel listProcessInsts() {
        String url = baseUrl + "/api/processinst/list";
        return callBpmServerList(url);
    }

    @GetMapping("/activityinst/get")
    public ResultModel getActivityInst(@RequestParam String activityInstId) {
        String url = baseUrl + "/api/processinst/activity/get?activityInstId=" + activityInstId;
        return callBpmServer(url);
    }

    @GetMapping("/activityinst/list")
    public ResultModel listActivityInsts(@RequestParam String processInstId) {
        String url = baseUrl + "/api/processinst/activity/list?processInstId=" + processInstId;
        return callBpmServerList(url);
    }

    @GetMapping("/history/list")
    public ResultModel listHistory(@RequestParam String processInstId) {
        String url = baseUrl + "/api/processinst/history/list?processInstId=" + processInstId;
        return callBpmServerList(url);
    }

    @PostMapping("/route")
    public ResultModel routeTo(
            @RequestParam String activityInstId,
            @RequestParam String targetActivityDefId,
            @RequestParam(required = false, defaultValue = "user1") String userId) {
        String url = baseUrl + "/api/processinst/route?activityInstId=" + activityInstId + 
                     "&targetActivityDefId=" + targetActivityDefId + "&userId=" + userId;
        return callBpmServerPost(url);
    }

    @PostMapping("/endtask")
    public ResultModel endTask(
            @RequestParam String activityInstId,
            @RequestParam(required = false, defaultValue = "user1") String userId) {
        String url = baseUrl + "/api/processinst/endtask?activityInstId=" + activityInstId + "&userId=" + userId;
        return callBpmServerPost(url);
    }

    @PostMapping("/complete")
    public ResultModel completeProcessInst(@RequestParam String processInstId) {
        String url = baseUrl + "/api/processinst/complete?processInstId=" + processInstId;
        return callBpmServerPost(url);
    }

    @GetMapping("/db/verify")
    public ResultModel verifyDatabase() {
        String url = baseUrl + "/api/db/verify";
        return callBpmServer(url);
    }

    private ResultModel callBpmServer(String url) {
        ResultModel result = new ResultModel<>();
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject json = JSON.parseObject(response.getBody());
                if (json.getInteger("maincode") != null && json.getInteger("maincode") == 200) {
                    result.setData(json.get("data"));
                }
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            result.setData(error);
        }
        return result;
    }

    private ResultModel callBpmServerList(String url) {
        ResultModel result = new ResultModel<>();
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject json = JSON.parseObject(response.getBody());
                if (json.getInteger("maincode") != null && json.getInteger("maincode") == 200) {
                    Object data = json.get("data");
                    if (data instanceof JSONArray) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        for (Object item : (JSONArray) data) {
                            if (item instanceof Map) {
                                list.add((Map<String, Object>) item);
                            }
                        }
                        result.setData(list);
                    }
                }
            }
        } catch (Exception e) {
            result.setData(new ArrayList<>());
        }
        return result;
    }

    private ResultModel callBpmServerPost(String url) {
        ResultModel result = new ResultModel<>();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject json = JSON.parseObject(response.getBody());
                if (json.getInteger("maincode") != null && json.getInteger("maincode") == 200) {
                    result.setData(json.get("data"));
                }
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            result.setData(error);
        }
        return result;
    }
}

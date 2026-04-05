package net.ooder.bpm.test.client;

import net.ooder.bpm.client.*;
import net.ooder.config.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class BPMRestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${bpm.server.url}")
    private String baseUrl;

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public ResultModel getActivityInst(String activityInstID) {
        String url = baseUrl + "/api/activityinst/get?activityInstId=" + activityInstID;
        return restTemplate.getForObject(url, ResultModel.class);
    }

    public ResultModel getProcessDef(String processDefID) {
        String url = baseUrl + "/api/processdef/get?processDefId=" + processDefID;
        return restTemplate.getForObject(url, ResultModel.class);
    }

    public ResultModel getActivityDef(String activityDefID) {
        String url = baseUrl + "/api/activitydef/get?activityDefId=" + activityDefID;
        return restTemplate.getForObject(url, ResultModel.class);
    }

    public ResultModel getRouteDef(String routeDefId) {
        String url = baseUrl + "/api/routedef/get?routeDefId=" + routeDefId;
        return restTemplate.getForObject(url, ResultModel.class);
    }

    public ResultModel newProcess(String processDefId, String processInstName) {
        String url = baseUrl + "/api/process/new";
        Map<String, String> request = Map.of(
            "processDefId", processDefId,
            "processInstName", processInstName
        );
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, createHeaders());
        return restTemplate.postForObject(url, entity, ResultModel.class);
    }

    public ResultModel getProcessInst(String processInstID) {
        String url = baseUrl + "/api/processinst/get?processInstId=" + processInstID;
        return restTemplate.getForObject(url, ResultModel.class);
    }

    public ResultModel getProcessDefVersion(String processDefVersionID) {
        String url = baseUrl + "/api/processdefversion/get?processDefVersionId=" + processDefVersionID;
        return restTemplate.getForObject(url, ResultModel.class);
    }

    public ResultModel getRouteInst(String routeInstId) {
        String url = baseUrl + "/api/routeinst/get?routeInstId=" + routeInstId;
        return restTemplate.getForObject(url, ResultModel.class);
    }

    public ResultModel getActivityInstHistory(String activityInstHistoryId) {
        String url = baseUrl + "/api/activityinsthistory/get?activityInstHistoryId=" + activityInstHistoryId;
        return restTemplate.getForObject(url, ResultModel.class);
    }

    public ResultModel endTask(String activityInstId) {
        String url = baseUrl + "/api/activityinst/endtask?activityInstId=" + activityInstId;
        return restTemplate.postForObject(url, null, ResultModel.class);
    }

    public ResultModel routeBack(String activityInstId, String activityInstHistoryId) {
        String url = baseUrl + "/api/activityinst/routeback";
        Map<String, String> request = Map.of(
            "activityInstId", activityInstId,
            "activityInstHistoryId", activityInstHistoryId
        );
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, createHeaders());
        return restTemplate.postForObject(url, entity, ResultModel.class);
    }

    public ResultModel signReceive(String activityInstId) {
        String url = baseUrl + "/api/activityinst/signreceive?activityInstId=" + activityInstId;
        return restTemplate.postForObject(url, null, ResultModel.class);
    }

    public ResultModel completeProcessInst(String processInstId) {
        String url = baseUrl + "/api/processinst/complete?processInstId=" + processInstId;
        return restTemplate.postForObject(url, null, ResultModel.class);
    }

    public ResultModel abortProcessInst(String processInstId) {
        String url = baseUrl + "/api/processinst/abort?processInstId=" + processInstId;
        return restTemplate.postForObject(url, null, ResultModel.class);
    }
}

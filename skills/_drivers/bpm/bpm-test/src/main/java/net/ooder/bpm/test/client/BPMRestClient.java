package net.ooder.bpm.test.client;

import net.ooder.bpm.client.*;
import net.ooder.config.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class BPMRestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${bpm.server.url}")
    private String baseUrl;

    private HttpHeaders createFormHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
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
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("processDefId", processDefId);
        request.add("processInstName", processInstName);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(request, createFormHeaders());
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
        String url = baseUrl + "/api/activityinst/endtask";
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("activityInstId", activityInstId);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(request, createFormHeaders());
        return restTemplate.postForObject(url, entity, ResultModel.class);
    }

    public ResultModel routeBack(String activityInstId, String activityInstHistoryId) {
        String url = baseUrl + "/api/activityinst/routeback";
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("activityInstId", activityInstId);
        request.add("activityInstHistoryId", activityInstHistoryId);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(request, createFormHeaders());
        return restTemplate.postForObject(url, entity, ResultModel.class);
    }

    public ResultModel signReceive(String activityInstId) {
        String url = baseUrl + "/api/activityinst/signreceive";
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("activityInstId", activityInstId);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(request, createFormHeaders());
        return restTemplate.postForObject(url, entity, ResultModel.class);
    }

    public ResultModel completeProcessInst(String processInstId) {
        String url = baseUrl + "/api/processinst/complete";
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("processInstId", processInstId);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(request, createFormHeaders());
        return restTemplate.postForObject(url, entity, ResultModel.class);
    }

    public ResultModel abortProcessInst(String processInstId) {
        String url = baseUrl + "/api/processinst/abort";
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("processInstId", processInstId);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(request, createFormHeaders());
        return restTemplate.postForObject(url, entity, ResultModel.class);
    }
}

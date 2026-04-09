package net.ooder.bpm.designer.datasource;

import net.ooder.bpm.designer.datasource.config.DataSourceConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class BpmDataSourceAdapter extends AbstractDataSourceAdapter {
    
    private final RestTemplate restTemplate;
    private final DataSourceConfig config;
    
    public BpmDataSourceAdapter(RestTemplate restTemplate, DataSourceConfig config) {
        super(config.getCacheTtl());
        this.restTemplate = restTemplate;
        this.config = config;
    }
    
    @Override
    public List<Map<String, Object>> getOrganizationTree(String tenantId) {
        String cacheKey = buildCacheKey(tenantId, "orgTree");
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getBpmServerUrl() + "/api/org/tree?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get organization tree for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> getUsersByRole(String tenantId, String roleId) {
        String cacheKey = buildCacheKey(tenantId, "usersByRole", roleId);
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getBpmServerUrl() + "/api/org/users/byRole?tenantId=" + tenantId + "&roleId=" + roleId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get users by role: {}", roleId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public Map<String, Object> getUserInfo(String tenantId, String userId) {
        String cacheKey = buildCacheKey(tenantId, "userInfo", userId);
        Map<String, Object> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getBpmServerUrl() + "/api/org/user/" + userId + "?tenantId=" + tenantId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), Map.class);
            Map<String, Object> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get user info: {}", userId, e);
            return Collections.emptyMap();
        }
    }
    
    @Override
    public List<Map<String, Object>> searchUsers(String tenantId, String keyword) {
        try {
            String url = config.getBpmServerUrl() + "/api/org/users/search?tenantId=" + tenantId + "&keyword=" + keyword;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to search users: {}", keyword, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> getDepartmentMembers(String tenantId, String deptId) {
        String cacheKey = buildCacheKey(tenantId, "deptMembers", deptId);
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getBpmServerUrl() + "/api/org/dept/" + deptId + "/members?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get department members: {}", deptId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public Map<String, Object> getDepartmentLeader(String tenantId, String deptId) {
        String cacheKey = buildCacheKey(tenantId, "deptLeader", deptId);
        Map<String, Object> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getBpmServerUrl() + "/api/org/dept/" + deptId + "/leader?tenantId=" + tenantId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), Map.class);
            Map<String, Object> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get department leader: {}", deptId, e);
            return Collections.emptyMap();
        }
    }
    
    @Override
    public List<Map<String, Object>> listRoles(String tenantId) {
        String cacheKey = buildCacheKey(tenantId, "roles");
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getBpmServerUrl() + "/api/org/roles?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to list roles for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> listCapabilities(String tenantId) {
        String cacheKey = buildCacheKey(tenantId, "capabilities");
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getCapabilityServiceUrl() + "/api/capabilities?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to list capabilities for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> searchCapabilities(String tenantId, String keyword, String category) {
        try {
            StringBuilder url = new StringBuilder(config.getCapabilityServiceUrl())
                .append("/api/capabilities/search?tenantId=").append(tenantId);
            if (keyword != null && !keyword.isEmpty()) {
                url.append("&keyword=").append(keyword);
            }
            if (category != null && !category.isEmpty()) {
                url.append("&category=").append(category);
            }
            ResponseEntity<List> response = restTemplate.exchange(url.toString(), HttpMethod.GET, createEntity(), List.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to search capabilities: {}", keyword, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public Map<String, Object> getCapabilityDetail(String tenantId, String capId) {
        String cacheKey = buildCacheKey(tenantId, "capDetail", capId);
        Map<String, Object> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getCapabilityServiceUrl() + "/api/capabilities/" + capId + "?tenantId=" + tenantId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), Map.class);
            Map<String, Object> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get capability detail: {}", capId, e);
            return Collections.emptyMap();
        }
    }
    
    @Override
    public List<Map<String, Object>> getCapabilitySkills(String tenantId, String capId) {
        String cacheKey = buildCacheKey(tenantId, "capSkills", capId);
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getCapabilityServiceUrl() + "/api/capabilities/" + capId + "/skills?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get capability skills: {}", capId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> matchCapabilityByActivity(String tenantId, String activityDesc) {
        try {
            String url = config.getCapabilityServiceUrl() + "/api/capabilities/match?tenantId=" + tenantId;
            Map<String, String> body = Map.of("activityDesc", activityDesc);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.POST, entity, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to match capability by activity: {}", activityDesc, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> getCapabilityProviders(String tenantId, String capId) {
        String cacheKey = buildCacheKey(tenantId, "capProviders", capId);
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getCapabilityServiceUrl() + "/api/capabilities/" + capId + "/providers?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get capability providers: {}", capId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> listCapabilityCategories(String tenantId) {
        String cacheKey = buildCacheKey(tenantId, "capCategories");
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getCapabilityServiceUrl() + "/api/capabilities/categories?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to list capability categories for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> listForms(String tenantId) {
        String cacheKey = buildCacheKey(tenantId, "forms");
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getFormServiceUrl() + "/api/forms?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to list forms for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> searchForms(String tenantId, String keyword, String formType) {
        try {
            StringBuilder url = new StringBuilder(config.getFormServiceUrl())
                .append("/api/forms/search?tenantId=").append(tenantId);
            if (keyword != null && !keyword.isEmpty()) {
                url.append("&keyword=").append(keyword);
            }
            if (formType != null && !formType.isEmpty()) {
                url.append("&formType=").append(formType);
            }
            ResponseEntity<List> response = restTemplate.exchange(url.toString(), HttpMethod.GET, createEntity(), List.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to search forms: {}", keyword, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public Map<String, Object> getFormSchema(String tenantId, String formId) {
        String cacheKey = buildCacheKey(tenantId, "formSchema", formId);
        Map<String, Object> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getFormServiceUrl() + "/api/forms/" + formId + "/schema?tenantId=" + tenantId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), Map.class);
            Map<String, Object> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get form schema: {}", formId, e);
            return Collections.emptyMap();
        }
    }
    
    @Override
    public List<Map<String, Object>> matchFormByActivity(String tenantId, String activityDesc) {
        try {
            String url = config.getFormServiceUrl() + "/api/forms/match?tenantId=" + tenantId;
            Map<String, String> body = Map.of("activityDesc", activityDesc);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.POST, entity, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to match form by activity: {}", activityDesc, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public Map<String, Object> generateFormSchema(String tenantId, String activityDesc) {
        try {
            String url = config.getFormServiceUrl() + "/api/forms/generate?tenantId=" + tenantId;
            Map<String, String> body = Map.of("activityDesc", activityDesc);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to generate form schema: {}", activityDesc, e);
            return Collections.emptyMap();
        }
    }
    
    @Override
    public List<Map<String, Object>> getFormFieldMappings(String tenantId, String formId, String activityDesc) {
        try {
            String url = config.getFormServiceUrl() + "/api/forms/" + formId + "/mappings?tenantId=" + tenantId;
            Map<String, String> body = Map.of("activityDesc", activityDesc);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.POST, entity, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to get form field mappings: {}", formId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> listFormCategories(String tenantId) {
        String cacheKey = buildCacheKey(tenantId, "formCategories");
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getFormServiceUrl() + "/api/forms/categories?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to list form categories for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> listSceneTemplates(String tenantId) {
        String cacheKey = buildCacheKey(tenantId, "sceneTemplates");
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getSceneServiceUrl() + "/api/scenes/templates?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to list scene templates for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public Map<String, Object> getSceneTemplate(String tenantId, String templateId) {
        String cacheKey = buildCacheKey(tenantId, "sceneTemplate", templateId);
        Map<String, Object> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getSceneServiceUrl() + "/api/scenes/templates/" + templateId + "?tenantId=" + tenantId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), Map.class);
            Map<String, Object> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get scene template: {}", templateId, e);
            return Collections.emptyMap();
        }
    }
    
    @Override
    public List<Map<String, Object>> getSceneCapabilities(String tenantId, String sceneId) {
        String cacheKey = buildCacheKey(tenantId, "sceneCapabilities", sceneId);
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getSceneServiceUrl() + "/api/scenes/" + sceneId + "/capabilities?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get scene capabilities: {}", sceneId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> listSceneGroups(String tenantId) {
        String cacheKey = buildCacheKey(tenantId, "sceneGroups");
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getSceneServiceUrl() + "/api/scenes/groups?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to list scene groups for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> getSceneParticipants(String tenantId, String sceneId) {
        String cacheKey = buildCacheKey(tenantId, "sceneParticipants", sceneId);
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getSceneServiceUrl() + "/api/scenes/" + sceneId + "/participants?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get scene participants: {}", sceneId, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> matchSceneByActivity(String tenantId, String activityDesc) {
        try {
            String url = config.getSceneServiceUrl() + "/api/scenes/match?tenantId=" + tenantId;
            Map<String, String> body = Map.of("activityDesc", activityDesc);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.POST, entity, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to match scene by activity: {}", activityDesc, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Map<String, Object>> getUserCapabilities(String tenantId, String userId) {
        String cacheKey = buildCacheKey(tenantId, "userCapabilities", userId);
        List<Map<String, Object>> cached = getFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        try {
            String url = config.getCapabilityServiceUrl() + "/api/capabilities/user/" + userId + "?tenantId=" + tenantId;
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createEntity(), List.class);
            List<Map<String, Object>> result = response.getBody();
            putToCache(cacheKey, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to get user capabilities: {}", userId, e);
            return Collections.emptyList();
        }
    }
    
    private HttpEntity<?> createEntity() {
        return new HttpEntity<>(createHeaders());
    }
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "default");
        return headers;
    }
}

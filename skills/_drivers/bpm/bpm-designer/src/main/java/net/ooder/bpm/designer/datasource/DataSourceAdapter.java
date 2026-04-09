package net.ooder.bpm.designer.datasource;

import java.util.List;
import java.util.Map;

public interface DataSourceAdapter {
    
    List<Map<String, Object>> getOrganizationTree(String tenantId);
    
    List<Map<String, Object>> getUsersByRole(String tenantId, String roleId);
    
    Map<String, Object> getUserInfo(String tenantId, String userId);
    
    List<Map<String, Object>> searchUsers(String tenantId, String keyword);
    
    List<Map<String, Object>> getDepartmentMembers(String tenantId, String deptId);
    
    Map<String, Object> getDepartmentLeader(String tenantId, String deptId);
    
    List<Map<String, Object>> listRoles(String tenantId);
    
    List<Map<String, Object>> listCapabilities(String tenantId);
    
    List<Map<String, Object>> searchCapabilities(String tenantId, String keyword, String category);
    
    Map<String, Object> getCapabilityDetail(String tenantId, String capId);
    
    List<Map<String, Object>> getCapabilitySkills(String tenantId, String capId);
    
    List<Map<String, Object>> matchCapabilityByActivity(String tenantId, String activityDesc);
    
    List<Map<String, Object>> getCapabilityProviders(String tenantId, String capId);
    
    List<Map<String, Object>> listCapabilityCategories(String tenantId);
    
    List<Map<String, Object>> listForms(String tenantId);
    
    List<Map<String, Object>> searchForms(String tenantId, String keyword, String formType);
    
    Map<String, Object> getFormSchema(String tenantId, String formId);
    
    List<Map<String, Object>> matchFormByActivity(String tenantId, String activityDesc);
    
    Map<String, Object> generateFormSchema(String tenantId, String activityDesc);
    
    List<Map<String, Object>> getFormFieldMappings(String tenantId, String formId, String activityDesc);
    
    List<Map<String, Object>> listFormCategories(String tenantId);
    
    List<Map<String, Object>> listSceneTemplates(String tenantId);
    
    Map<String, Object> getSceneTemplate(String tenantId, String templateId);
    
    List<Map<String, Object>> getSceneCapabilities(String tenantId, String sceneId);
    
    List<Map<String, Object>> listSceneGroups(String tenantId);
    
    List<Map<String, Object>> getSceneParticipants(String tenantId, String sceneId);
    
    List<Map<String, Object>> matchSceneByActivity(String tenantId, String activityDesc);
    
    List<Map<String, Object>> getUserCapabilities(String tenantId, String userId);
}

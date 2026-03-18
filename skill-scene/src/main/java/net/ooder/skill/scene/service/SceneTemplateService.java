package net.ooder.skill.scene.service;

import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.scene.SceneTemplateDTO;
import net.ooder.skill.scene.dto.scene.RoleDefinitionDTO;
import net.ooder.skill.scene.dto.scene.CapabilityDefDTO;

public interface SceneTemplateService {
    
    SceneTemplateDTO create(SceneTemplateDTO template);
    
    SceneTemplateDTO get(String templateId);
    
    PageResult<SceneTemplateDTO> listAll(int pageNum, int pageSize);
    
    PageResult<SceneTemplateDTO> listByCategory(String category, int pageNum, int pageSize);
    
    boolean delete(String templateId);
    
    boolean publish(String templateId);
    
    boolean activate(String templateId);
    
    boolean deactivate(String templateId);
    
    boolean addCapability(String templateId, CapabilityDefDTO capability);
    
    boolean removeCapability(String templateId, String capId);
    
    boolean addRole(String templateId, RoleDefinitionDTO role);
    
    boolean removeRole(String templateId, String roleName);
}

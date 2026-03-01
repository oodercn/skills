package net.ooder.skill.scene.service.impl;

import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.scene.SceneTemplateDTO;
import net.ooder.skill.scene.dto.scene.RoleDefinitionDTO;
import net.ooder.skill.scene.dto.scene.CapabilityDefDTO;
import net.ooder.skill.scene.service.SceneTemplateService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SceneTemplateServiceMemoryImpl implements SceneTemplateService {

    private final Map<String, SceneTemplateDTO> templates = new ConcurrentHashMap<>();

    @Override
    public SceneTemplateDTO create(SceneTemplateDTO template) {
        if (template.getTemplateId() == null || template.getTemplateId().isEmpty()) {
            template.setTemplateId("tpl-" + System.currentTimeMillis());
        }
        if (template.getCreateTime() == 0) {
            template.setCreateTime(System.currentTimeMillis());
        }
        if (template.getStatus() == null) {
            template.setStatus("draft");
        }
        templates.put(template.getTemplateId(), template);
        return template;
    }

    @Override
    public SceneTemplateDTO get(String templateId) {
        return templates.get(templateId);
    }

    @Override
    public PageResult<SceneTemplateDTO> listAll(int pageNum, int pageSize) {
        List<SceneTemplateDTO> allTemplates = new ArrayList<>(templates.values());
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allTemplates.size());
        
        List<SceneTemplateDTO> pagedTemplates = start < allTemplates.size() 
            ? allTemplates.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<SceneTemplateDTO> result = new PageResult<>();
        result.setList(pagedTemplates);
        result.setTotal(allTemplates.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public PageResult<SceneTemplateDTO> listByCategory(String category, int pageNum, int pageSize) {
        List<SceneTemplateDTO> filtered = templates.values().stream()
            .filter(t -> category.equals(t.getCategory()))
            .collect(Collectors.toList());
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        
        List<SceneTemplateDTO> pagedTemplates = start < filtered.size() 
            ? filtered.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<SceneTemplateDTO> result = new PageResult<>();
        result.setList(pagedTemplates);
        result.setTotal(filtered.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public boolean delete(String templateId) {
        return templates.remove(templateId) != null;
    }

    @Override
    public boolean publish(String templateId) {
        SceneTemplateDTO template = templates.get(templateId);
        if (template != null) {
            template.setStatus("published");
            template.setUpdateTime(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public boolean activate(String templateId) {
        SceneTemplateDTO template = templates.get(templateId);
        if (template != null) {
            template.setStatus("active");
            template.setUpdateTime(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public boolean deactivate(String templateId) {
        SceneTemplateDTO template = templates.get(templateId);
        if (template != null) {
            template.setStatus("inactive");
            template.setUpdateTime(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public boolean addCapability(String templateId, CapabilityDefDTO capability) {
        SceneTemplateDTO template = templates.get(templateId);
        if (template != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean removeCapability(String templateId, String capId) {
        SceneTemplateDTO template = templates.get(templateId);
        if (template != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean addRole(String templateId, RoleDefinitionDTO role) {
        SceneTemplateDTO template = templates.get(templateId);
        if (template != null) {
            List<RoleDefinitionDTO> roles = template.getRoles();
            if (roles == null) {
                roles = new ArrayList<>();
                template.setRoles(roles);
            }
            roles.add(role);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeRole(String templateId, String roleName) {
        SceneTemplateDTO template = templates.get(templateId);
        if (template != null && template.getRoles() != null) {
            return template.getRoles().removeIf(role -> roleName.equals(role.getName()));
        }
        return false;
    }
}

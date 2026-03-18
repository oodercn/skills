package net.ooder.mvp.skill.scene.service.impl;

import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.scene.SceneTemplateDTO;
import net.ooder.mvp.skill.scene.dto.scene.RoleDefinitionDTO;
import net.ooder.mvp.skill.scene.dto.scene.CapabilityDefDTO;
import net.ooder.mvp.skill.scene.service.SceneTemplateService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SceneTemplateServiceMemoryImpl implements SceneTemplateService {

    private final Map<String, SceneTemplateDTO> templates = new ConcurrentHashMap<>();

    public SceneTemplateServiceMemoryImpl() {
        initDemoTemplates();
    }

    private void initDemoTemplates() {
        SceneTemplateDTO tpl1 = new SceneTemplateDTO();
        tpl1.setTemplateId("tpl-daily-report");
        tpl1.setName("日报汇报模板");
        tpl1.setDescription("团队日报汇报场景模板，支持日志提交、汇总、分析");
        tpl1.setCategory("daily");
        tpl1.setStatus("active");
        tpl1.setCreateTime(System.currentTimeMillis() - 86400000);
        tpl1.setUpdateTime(System.currentTimeMillis());
        
        List<RoleDefinitionDTO> roles1 = new ArrayList<>();
        RoleDefinitionDTO role1 = new RoleDefinitionDTO();
        role1.setName("MANAGER");
        role1.setDescription("管理者，可查看所有日报");
        roles1.add(role1);
        RoleDefinitionDTO role2 = new RoleDefinitionDTO();
        role2.setName("EMPLOYEE");
        role2.setDescription("员工，提交日报");
        roles1.add(role2);
        RoleDefinitionDTO role3 = new RoleDefinitionDTO();
        role3.setName("HR");
        role3.setDescription("HR，可查看团队日报统计");
        roles1.add(role3);
        RoleDefinitionDTO role4 = new RoleDefinitionDTO();
        role4.setName("LLM_ASSISTANT");
        role4.setDescription("LLM助手，辅助日报分析和汇总");
        roles1.add(role4);
        RoleDefinitionDTO role5 = new RoleDefinitionDTO();
        role5.setName("COORDINATOR");
        role5.setDescription("协调Agent，负责日报收集和分发");
        roles1.add(role5);
        tpl1.setRoles(roles1);
        
        List<CapabilityDefDTO> caps1 = new ArrayList<>();
        CapabilityDefDTO cap1 = new CapabilityDefDTO();
        cap1.setCapId("report-analyze");
        cap1.setName("日志分析能力");
        cap1.setDescription("分析日报内容，提取关键信息");
        caps1.add(cap1);
        CapabilityDefDTO cap2 = new CapabilityDefDTO();
        cap2.setCapId("daily-summary");
        cap2.setName("日报汇总能力");
        cap2.setDescription("汇总团队日报，生成统计报告");
        caps1.add(cap2);
        CapabilityDefDTO cap3 = new CapabilityDefDTO();
        cap3.setCapId("notification-push");
        cap3.setName("消息推送能力");
        cap3.setDescription("推送日报提醒和通知");
        caps1.add(cap3);
        tpl1.setCapabilities(caps1);
        
        templates.put(tpl1.getTemplateId(), tpl1);
        
        SceneTemplateDTO tpl2 = new SceneTemplateDTO();
        tpl2.setTemplateId("tpl-weekly-review");
        tpl2.setName("周报复盘模板");
        tpl2.setDescription("团队周报复盘场景模板，支持周报提交、复盘会议");
        tpl2.setCategory("weekly");
        tpl2.setStatus("active");
        tpl2.setCreateTime(System.currentTimeMillis() - 86400000);
        tpl2.setUpdateTime(System.currentTimeMillis());
        tpl2.setRoles(roles1);
        tpl2.setCapabilities(caps1);
        templates.put(tpl2.getTemplateId(), tpl2);
        
        SceneTemplateDTO tpl3 = new SceneTemplateDTO();
        tpl3.setTemplateId("tpl-knowledge-qa");
        tpl3.setName("知识问答模板");
        tpl3.setDescription("知识库问答场景模板，支持RAG检索增强生成");
        tpl3.setCategory("knowledge");
        tpl3.setStatus("active");
        tpl3.setCreateTime(System.currentTimeMillis() - 86400000);
        tpl3.setUpdateTime(System.currentTimeMillis());
        templates.put(tpl3.getTemplateId(), tpl3);
    }

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
            List<CapabilityDefDTO> capabilities = template.getCapabilities();
            if (capabilities == null) {
                capabilities = new ArrayList<>();
                template.setCapabilities(capabilities);
            }
            capabilities.add(capability);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeCapability(String templateId, String capId) {
        SceneTemplateDTO template = templates.get(templateId);
        if (template != null && template.getCapabilities() != null) {
            return template.getCapabilities().removeIf(cap -> capId.equals(cap.getCapId()));
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

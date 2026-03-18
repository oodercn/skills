package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.capability.model.Capability;
import net.ooder.mvp.skill.scene.capability.service.CapabilityService;
import net.ooder.mvp.skill.scene.capability.service.CapabilityBindingService;
import net.ooder.mvp.skill.scene.model.ResultModel;
import net.ooder.mvp.skill.scene.service.SceneGroupService;
import net.ooder.mvp.skill.scene.service.SceneTemplateService;
import net.ooder.mvp.skill.scene.dto.PageResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/selectors")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SelectorController {

    @Autowired
    private CapabilityService capabilityService;

    @Autowired
    private CapabilityBindingService bindingService;

    @Autowired
    private SceneGroupService sceneGroupService;

    @Autowired
    private SceneTemplateService templateService;

    @GetMapping("/org-tree")
    public ResultModel<List<Map<String, Object>>> getOrgTree() {
        List<Map<String, Object>> tree = new ArrayList<Map<String, Object>>();

        Map<String, Object> rd = new HashMap<String, Object>();
        rd.put("id", "dept-rd");
        rd.put("name", "研发部");
        rd.put("type", "department");
        List<Map<String, Object>> rdChildren = new ArrayList<Map<String, Object>>();
        rdChildren.add(createUserNode("user-manager-001", "张经理", "manager"));
        rdChildren.add(createUserNode("user-employee-001", "李员工", "employee"));
        rdChildren.add(createUserNode("user-employee-002", "王员工", "employee"));
        rdChildren.add(createUserNode("user-employee-003", "赵员工", "employee"));
        rd.put("children", rdChildren);
        tree.add(rd);

        Map<String, Object> hr = new HashMap<String, Object>();
        hr.put("id", "dept-hr");
        hr.put("name", "人力资源部");
        hr.put("type", "department");
        List<Map<String, Object>> hrChildren = new ArrayList<Map<String, Object>>();
        hrChildren.add(createUserNode("user-hr-001", "刘HR", "hr"));
        hr.put("children", hrChildren);
        tree.add(hr);

        return ResultModel.success(tree);
    }

    private Map<String, Object> createUserNode(String id, String name, String role) {
        Map<String, Object> user = new HashMap<String, Object>();
        user.put("id", id);
        user.put("name", name);
        user.put("type", "user");
        user.put("role", role);
        user.put("icon", "ri-user-line");
        return user;
    }

    @GetMapping("/users")
    public ResultModel<List<Map<String, Object>>> getUsers() {
        List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();

        users.add(createUserItem("user-manager-001", "张经理", "manager", "dept-rd"));
        users.add(createUserItem("user-employee-001", "李员工", "employee", "dept-rd"));
        users.add(createUserItem("user-employee-002", "王员工", "employee", "dept-rd"));
        users.add(createUserItem("user-employee-003", "赵员工", "employee", "dept-rd"));
        users.add(createUserItem("user-hr-001", "刘HR", "hr", "dept-hr"));

        return ResultModel.success(users);
    }

    private Map<String, Object> createUserItem(String id, String name, String role, String dept) {
        Map<String, Object> user = new HashMap<String, Object>();
        user.put("id", id);
        user.put("name", name);
        user.put("role", role);
        user.put("departmentId", dept);
        return user;
    }

    @GetMapping("/capabilities")
    public ResultModel<List<Map<String, Object>>> getCapabilities(
            @RequestParam(required = false) String type) {
        
        List<Capability> capabilities;
        if (type != null && !type.isEmpty()) {
            capabilities = capabilityService.findByType(
                net.ooder.mvp.skill.scene.capability.model.CapabilityType.valueOf(type));
        } else {
            capabilities = capabilityService.findAll();
        }

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Capability cap : capabilities) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("id", cap.getCapabilityId());
            item.put("name", cap.getName());
            item.put("description", cap.getDescription());
            item.put("type", cap.getType() != null ? cap.getType().name() : "CUSTOM");
            item.put("status", cap.getStatus() != null ? cap.getStatus().name() : "ENABLED");
            result.add(item);
        }

        return ResultModel.success(result);
    }

    @GetMapping("/capability-types")
    public ResultModel<List<Map<String, Object>>> getCapabilityTypes() {
        List<Map<String, Object>> types = new ArrayList<Map<String, Object>>();

        Map<String, Integer> typeCounts = new HashMap<String, Integer>();
        List<Capability> allCapabilities = capabilityService.findAll();
        for (Capability cap : allCapabilities) {
            String typeName = cap.getType() != null ? cap.getType().name() : "CUSTOM";
            typeCounts.put(typeName, typeCounts.getOrDefault(typeName, 0) + 1);
        }

        for (net.ooder.mvp.skill.scene.capability.model.CapabilityType type : 
                net.ooder.mvp.skill.scene.capability.model.CapabilityType.values()) {
            Map<String, Object> typeInfo = new HashMap<String, Object>();
            typeInfo.put("id", type.name());
            typeInfo.put("name", type.getName());
            typeInfo.put("description", type.getDescription());
            typeInfo.put("count", typeCounts.getOrDefault(type.name(), 0));
            types.add(typeInfo);
        }

        return ResultModel.success(types);
    }

    @GetMapping("/scene-groups")
    public ResultModel<List<Map<String, Object>>> getSceneGroups() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        try {
            PageResult<?> pageResult = sceneGroupService.listAll(1, 100);
            List<?> groups = pageResult.getList();
            for (Object obj : groups) {
                Map<String, Object> item = new HashMap<String, Object>();
                if (obj instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) obj;
                    item.put("id", map.get("sceneGroupId"));
                    item.put("name", map.get("name"));
                    item.put("description", map.get("description"));
                    item.put("status", map.get("status"));
                } else if (obj instanceof net.ooder.mvp.skill.scene.dto.scene.SceneGroupDTO) {
                    net.ooder.mvp.skill.scene.dto.scene.SceneGroupDTO dto = 
                        (net.ooder.mvp.skill.scene.dto.scene.SceneGroupDTO) obj;
                    item.put("id", dto.getSceneGroupId());
                    item.put("name", dto.getName());
                    item.put("description", dto.getDescription());
                    item.put("status", dto.getStatus() != null ? dto.getStatus().name() : null);
                } else {
                    item.put("id", obj.toString());
                    item.put("name", obj.toString());
                }
                result.add(item);
            }
        } catch (Exception e) {
            result.add(createDefaultItem("sg-daily-report", "日志汇报组"));
            result.add(createDefaultItem("sg-project-alpha", "项目Alpha组"));
        }

        return ResultModel.success(result);
    }

    @GetMapping("/templates")
    public ResultModel<List<Map<String, Object>>> getTemplates() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        try {
            PageResult<?> pageResult = templateService.listAll(1, 100);
            List<?> templates = pageResult.getList();
            for (Object obj : templates) {
                Map<String, Object> item = new HashMap<String, Object>();
                if (obj instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) obj;
                    item.put("id", map.get("templateId"));
                    item.put("name", map.get("name"));
                    item.put("description", map.get("description"));
                } else if (obj instanceof net.ooder.mvp.skill.scene.dto.scene.SceneTemplateDTO) {
                    net.ooder.mvp.skill.scene.dto.scene.SceneTemplateDTO dto = 
                        (net.ooder.mvp.skill.scene.dto.scene.SceneTemplateDTO) obj;
                    item.put("id", dto.getTemplateId());
                    item.put("name", dto.getName());
                    item.put("description", dto.getDescription());
                } else {
                    item.put("id", obj.toString());
                    item.put("name", obj.toString());
                }
                result.add(item);
            }
        } catch (Exception e) {
            result.add(createDefaultItem("tpl-daily-report", "日志汇报模板"));
            result.add(createDefaultItem("tpl-meeting", "会议模板"));
        }

        return ResultModel.success(result);
    }

    @GetMapping("/providers")
    public ResultModel<List<Map<String, Object>>> getProviders(
            @RequestParam(required = false) String type) {
        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        if ("SKILL".equals(type) || type == null) {
            result.add(createProviderItem("skill-daily-report", "日志汇报技能", "SKILL"));
            result.add(createProviderItem("skill-notification", "通知技能", "SKILL"));
        }
        if ("AGENT".equals(type) || type == null) {
            result.add(createProviderItem("agent-llm-001", "LLM分析助手", "AGENT"));
            result.add(createProviderItem("agent-coordinator-001", "协调Agent", "AGENT"));
        }
        if ("SUPER_AGENT".equals(type) || type == null) {
            result.add(createProviderItem("super-agent-001", "超级Agent", "SUPER_AGENT"));
        }
        if ("DEVICE".equals(type) || type == null) {
            result.add(createProviderItem("device-sensor-001", "传感器设备", "DEVICE"));
        }

        return ResultModel.success(result);
    }

    private Map<String, Object> createProviderItem(String id, String name, String type) {
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("id", id);
        item.put("name", name);
        item.put("type", type);
        item.put("description", type + " 提供者");
        return item;
    }

    private Map<String, Object> createDefaultItem(String id, String name) {
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("id", id);
        item.put("name", name);
        return item;
    }
}

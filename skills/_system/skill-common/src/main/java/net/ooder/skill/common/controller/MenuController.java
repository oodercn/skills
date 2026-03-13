package net.ooder.skill.common.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import net.ooder.skill.common.model.ResultModel;
import net.ooder.skill.common.model.UserSession;
import net.ooder.skill.common.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private AuthService authService;

    private JSONObject menuConfig;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("config/menu-config.json");
            if (resource.exists()) {
                String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                menuConfig = JSON.parseObject(content);
                log.info("Menu config loaded successfully");
            }
        } catch (IOException e) {
            log.warn("Failed to load menu config: {}", e.getMessage());
            menuConfig = new JSONObject();
        }
    }

    @GetMapping
    public ResultModel<List<Map<String, Object>>> getCurrentUserMenu(
            @RequestParam(required = false) String role,
            javax.servlet.http.HttpServletRequest request) {
        
        UserSession user = authService.getCurrentUser(request);
        String roleType = role;
        
        if (roleType == null && user != null) {
            roleType = user.getRoleType();
        }
        
        if (roleType == null) {
            roleType = "collaborator";
        }

        List<Map<String, Object>> menus = buildMenuForRole(roleType);
        return ResultModel.success(menus);
    }

    @GetMapping("/config")
    public ResultModel<Object> getMenuConfig() {
        return ResultModel.success(menuConfig);
    }

    @GetMapping("/roles")
    public ResultModel<List<Map<String, Object>>> getAvailableRoles() {
        List<Map<String, Object>> roles = Arrays.asList(
            createRole("installer", "系统安装者", "安装技能包，初始化系统", 
                Arrays.asList("skill:install", "skill:view", "system:init")),
            createRole("admin", "系统管理员", "发现场景技能，配置分发", 
                Arrays.asList("capability:discover", "capability:install", "capability:distribute", 
                    "scene:create", "scene:manage", "user:assign")),
            createRole("leader", "主导者", "激活场景，获取KEY，执行入网", 
                Arrays.asList("scene:activate", "scene:manage", "key:generate", 
                    "participant:manage", "task:assign")),
            createRole("collaborator", "协作者", "参与业务流转，执行任务", 
                Arrays.asList("task:view", "task:execute", "task:submit", "scene:view"))
        );
        return ResultModel.success(roles);
    }

    private List<Map<String, Object>> buildMenuForRole(String roleType) {
        List<Map<String, Object>> menus = new ArrayList<>();

        menus.add(createMenuItem("home", "首页", "ri-home-line", "/", null));
        
        if ("installer".equals(roleType)) {
            menus.add(createMenuItem("install", "安装向导", "ri-install-line", "/pages/install", null));
            menus.add(createMenuItem("capabilities", "能力管理", "ri-puzzle-line", "/pages/capabilities", null));
        }
        
        if ("admin".equals(roleType)) {
            menus.add(createMenuItem("dashboard", "仪表盘", "ri-dashboard-line", "/pages/dashboard", null));
            menus.add(createMenuItem("capabilities", "能力管理", "ri-puzzle-line", "/pages/capabilities", 
                Arrays.asList(
                    createMenuItem("capability-list", "能力列表", "ri-list-check", "/pages/capabilities", null),
                    createMenuItem("capability-discover", "发现能力", "ri-search-line", "/pages/capability-discovery", null)
                )));
            menus.add(createMenuItem("scenes", "场景管理", "ri-git-branch-line", "/pages/scenes", null));
            menus.add(createMenuItem("users", "用户管理", "ri-user-settings-line", "/pages/users", null));
            menus.add(createMenuItem("config", "系统配置", "ri-settings-3-line", "/pages/config", null));
        }
        
        if ("leader".equals(roleType)) {
            menus.add(createMenuItem("dashboard", "仪表盘", "ri-dashboard-line", "/pages/dashboard", null));
            menus.add(createMenuItem("my-scenes", "我的场景", "ri-folder-line", "/pages/my-scenes", null));
            menus.add(createMenuItem("tasks", "任务管理", "ri-task-line", "/pages/tasks", null));
        }
        
        if ("collaborator".equals(roleType)) {
            menus.add(createMenuItem("dashboard", "仪表盘", "ri-dashboard-line", "/pages/dashboard", null));
            menus.add(createMenuItem("my-tasks", "我的任务", "ri-task-line", "/pages/my-tasks", null));
            menus.add(createMenuItem("history", "操作历史", "ri-history-line", "/pages/history", null));
        }

        return menus;
    }

    private Map<String, Object> createMenuItem(String id, String name, String icon, String url, List<Map<String, Object>> children) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("name", name);
        item.put("icon", icon);
        item.put("url", url);
        if (children != null && !children.isEmpty()) {
            item.put("children", children);
        }
        return item;
    }

    private Map<String, Object> createRole(String id, String name, String description, List<String> permissions) {
        Map<String, Object> role = new LinkedHashMap<>();
        role.put("id", id);
        role.put("name", name);
        role.put("description", description);
        role.put("permissions", permissions);
        return role;
    }
}

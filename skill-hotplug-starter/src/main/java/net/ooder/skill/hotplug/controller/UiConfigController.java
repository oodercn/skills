package net.ooder.skill.hotplug.controller;

import net.ooder.skill.hotplug.PluginManager;
import net.ooder.skill.hotplug.model.SkillUiConfig;
import net.ooder.skill.hotplug.ui.UiRouteRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * UI配置控制器
 * 提供Skill UI配置查询API
 */
@RestController
@RequestMapping("/api/v1/skill-ui")
public class UiConfigController {

    private static final Logger logger = LoggerFactory.getLogger(UiConfigController.class);

    @Autowired
    private PluginManager pluginManager;

    @Autowired
    private UiRouteRegistry uiRouteRegistry;

    @GetMapping("/menus")
    public ResponseEntity<Map<String, Object>> getAllMenus() {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, List<?>> allMenus = new LinkedHashMap<>();

        for (String skillId : uiRouteRegistry.getRegisteredSkillIds()) {
            allMenus.put(skillId, uiRouteRegistry.getMenus(skillId));
        }

        result.put("success", true);
        result.put("data", allMenus);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/menus/{skillId}")
    public ResponseEntity<Map<String, Object>> getMenusBySkill(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (!uiRouteRegistry.hasUi(skillId)) {
            result.put("success", false);
            result.put("error", "Skill not found or has no UI: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", uiRouteRegistry.getMenus(skillId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/menus/{skillId}/role/{role}")
    public ResponseEntity<Map<String, Object>> getMenusByRole(
            @PathVariable String skillId,
            @PathVariable String role) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (!uiRouteRegistry.hasUi(skillId)) {
            result.put("success", false);
            result.put("error", "Skill not found or has no UI: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", uiRouteRegistry.getMenusByRole(skillId, role));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pages/{skillId}")
    public ResponseEntity<Map<String, Object>> getPagesBySkill(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (!uiRouteRegistry.hasUi(skillId)) {
            result.put("success", false);
            result.put("error", "Skill not found or has no UI: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", uiRouteRegistry.getPages(skillId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/config/{skillId}")
    public ResponseEntity<Map<String, Object>> getUiConfig(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        SkillUiConfig config = uiRouteRegistry.getUiConfig(skillId);
        if (config == null) {
            result.put("success", false);
            result.put("error", "Skill UI config not found: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", config);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/route-info/{skillId}")
    public ResponseEntity<Map<String, Object>> getRouteInfo(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        Map<String, Object> routeInfo = uiRouteRegistry.getRouteInfo(skillId);
        if (routeInfo.isEmpty()) {
            result.put("success", false);
            result.put("error", "Skill route info not found: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", routeInfo);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listSkillsWithUi() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> skills = new ArrayList<>();

        for (String skillId : uiRouteRegistry.getRegisteredSkillIds()) {
            Map<String, Object> skillInfo = new LinkedHashMap<>();
            skillInfo.put("skillId", skillId);
            skillInfo.put("menus", uiRouteRegistry.getMenus(skillId).size());
            skillInfo.put("pages", uiRouteRegistry.getPages(skillId).size());
            skills.add(skillInfo);
        }

        result.put("success", true);
        result.put("data", skills);
        result.put("total", skills.size());
        return ResponseEntity.ok(result);
    }
}

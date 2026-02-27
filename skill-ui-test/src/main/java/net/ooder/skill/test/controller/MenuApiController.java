package net.ooder.skill.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.skill.test.model.SkillMetadata;
import net.ooder.skill.test.service.MenuRegistry;
import net.ooder.skill.test.service.SkillDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/api/menu")
public class MenuApiController {
    
    @Autowired
    private SkillDiscoveryService discoveryService;
    
    @Autowired
    private MenuRegistry menuRegistry;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @GetMapping("/config")
    public Object getMenuConfig() {
        try {
            File configFile = new File("menu-config.json");
            if (configFile.exists()) {
                return objectMapper.readValue(configFile, Object.class);
            }
        } catch (Exception e) {
        }
        
        Map<String, Object> defaultConfig = new HashMap<>();
        List<Object> defaultMenu = new ArrayList<>();
        
        List<SkillMetadata> skills = discoveryService.getLocalNexusUiSkills();
        for (SkillMetadata skill : skills) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", skill.getId());
            item.put("name", skill.getName());
            item.put("icon", "ri-apps-line");
            item.put("url", "/console/skills/" + skill.getId() + "/ui/pages/index.html");
            item.put("status", "implemented");
            item.put("roles", "personal,mcp");
            defaultMenu.add(item);
        }
        
        defaultConfig.put("menu", defaultMenu);
        return defaultConfig;
    }
    
    @GetMapping("/list")
    public List<Map<String, Object>> getMenuList() {
        List<Map<String, Object>> menuList = new ArrayList<>();
        
        List<SkillMetadata> skills = discoveryService.getLocalNexusUiSkills();
        for (SkillMetadata skill : skills) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", skill.getId());
            item.put("name", skill.getName());
            item.put("icon", "ri-apps-line");
            item.put("url", "/console/skills/" + skill.getId() + "/ui/pages/index.html");
            item.put("status", "implemented");
            menuList.add(item);
        }
        
        return menuList;
    }
}

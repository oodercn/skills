package net.ooder.nexus.service.skill;

import net.ooder.skill.hotplug.model.SkillMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 菜单动态注册服务
 * 
 * 基于发现的 Nexus-UI Skill 动态注册菜单到 menu-config.json
 */
@Service
public class MenuRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(MenuRegistry.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${nexus.menu.config.path:./static/console/menu-config.json}")
    private String menuConfigPath;
    
    /**
     * 注册 Skill 菜单
     *
     * @param skill Skill 元数据
     */
    @SuppressWarnings("unchecked")
    public void registerSkillMenu(SkillMetadata skill) {
        if (skill == null) {
            log.warn("Skill metadata is null");
            return;
        }
        
        Map<String, Object> ui = skill.getUi();
        if (ui == null || !ui.containsKey("nexusUi")) {
            log.debug("Skill is not a Nexus-UI type: {}", skill.getId());
            return;
        }
        
        Map<String, Object> nexusUi = (Map<String, Object>) ui.get("nexusUi");
        if (nexusUi == null) {
            return;
        }
        
        Map<String, Object> menu = (Map<String, Object>) nexusUi.get("menu");
        Map<String, Object> entry = (Map<String, Object>) nexusUi.get("entry");
        
        if (menu == null || entry == null) {
            log.debug("Skill missing menu or entry config: {}", skill.getId());
            return;
        }
        
        try {
            File configFile = new File(menuConfigPath);
            ObjectNode config;
            
            if (configFile.exists()) {
                config = (ObjectNode) objectMapper.readTree(configFile);
            } else {
                config = objectMapper.createObjectNode();
                config.set("menu", objectMapper.createArrayNode());
            }
            
            ArrayNode menuArray = (ArrayNode) config.get("menu");
            
            removeExistingMenu(menuArray, skill.getId());
            
            ObjectNode menuItem = objectMapper.createObjectNode();
            menuItem.put("id", skill.getId());
            menuItem.put("name", skill.getName());
            menuItem.put("icon", (String) entry.get("icon"));
            menuItem.put("url", "/console/skills/" + skill.getId() + "/pages/" + entry.get("page"));
            menuItem.put("status", "implemented");
            menuItem.put("roles", "personal,mcp");
            
            String category = (String) menu.get("category");
            String position = (String) menu.get("position");
            
            if ("sidebar".equals(position)) {
                ObjectNode categoryMenu = findOrCreateCategory(menuArray, category);
                if (categoryMenu != null) {
                    ArrayNode children = (ArrayNode) categoryMenu.get("children");
                    if (children == null) {
                        children = objectMapper.createArrayNode();
                        categoryMenu.set("children", children);
                    }
                    children.add(menuItem);
                } else {
                    menuArray.add(menuItem);
                }
            } else {
                menuArray.add(menuItem);
            }
            
            File parentDir = configFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, config);
            log.info("Registered menu for skill: {}", skill.getId());
            
        } catch (IOException e) {
            log.error("Failed to register menu for skill: {}", skill.getId(), e);
        }
    }
    
    /**
     * 注销 Skill 菜单
     *
     * @param skillId Skill ID
     */
    public void unregisterSkillMenu(String skillId) {
        try {
            File configFile = new File(menuConfigPath);
            if (!configFile.exists()) {
                return;
            }
            
            ObjectNode config = (ObjectNode) objectMapper.readTree(configFile);
            ArrayNode menuArray = (ArrayNode) config.get("menu");
            
            removeExistingMenu(menuArray, skillId);
            
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, config);
            log.info("Unregistered menu for skill: {}", skillId);
            
        } catch (IOException e) {
            log.error("Failed to unregister menu for skill: {}", skillId, e);
        }
    }
    
    /**
     * 批量注册 Skill 菜单
     *
     * @param skills Skill 列表
     */
    public void registerAllSkillMenus(List<SkillMetadata> skills) {
        for (SkillMetadata skill : skills) {
            registerSkillMenu(skill);
        }
    }
    
    /**
     * 从菜单中移除已存在的项
     */
    private void removeExistingMenu(ArrayNode menuArray, String skillId) {
        for (int i = 0; i < menuArray.size(); i++) {
            ObjectNode item = (ObjectNode) menuArray.get(i);
            if (skillId.equals(item.get("id").asText())) {
                menuArray.remove(i);
                break;
            }
            
            if (item.has("children")) {
                ArrayNode children = (ArrayNode) item.get("children");
                for (int j = 0; j < children.size(); j++) {
                    ObjectNode child = (ObjectNode) children.get(j);
                    if (skillId.equals(child.get("id").asText())) {
                        children.remove(j);
                        break;
                    }
                }
            }
        }
    }
    
    private ObjectNode findOrCreateCategory(ArrayNode menuArray, String category) {
        if (category == null) {
            return null;
        }
        
        for (int i = 0; i < menuArray.size(); i++) {
            ObjectNode item = (ObjectNode) menuArray.get(i);
            if (item.has("id") && category.equals(item.get("id").asText())) {
                return item;
            }
        }
        
        return null;
    }
}

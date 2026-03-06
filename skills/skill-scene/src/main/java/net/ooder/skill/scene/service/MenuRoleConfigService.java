package net.ooder.skill.scene.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.skill.scene.model.MenuRoleConfig;
import net.ooder.skill.scene.dto.MenuItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 菜单角色配置服务
 * 使用 fastjson 完成持久化
 */
@Service
public class MenuRoleConfigService {

    private static final Logger log = LoggerFactory.getLogger(MenuRoleConfigService.class);
    
    private static final String CONFIG_DIR = "data/config";
    private static final String CONFIG_FILE = "menu-role-config.json";
    
    private final Map<String, MenuRoleConfig> configs = new LinkedHashMap<>();
    
    @PostConstruct
    public void init() {
        loadConfig();
        log.info("MenuRoleConfigService initialized with {} configs", configs.size());
    }
    
    /**
     * 加载配置文件
     */
    private void loadConfig() {
        Path configPath = Paths.get(CONFIG_DIR, CONFIG_FILE);
        
        if (Files.exists(configPath)) {
            try {
                String content = new String(Files.readAllBytes(configPath), StandardCharsets.UTF_8);
                JSONObject root = JSON.parseObject(content);
                JSONArray configArray = root.getJSONArray("configs");
                
                if (configArray != null) {
                    for (int i = 0; i < configArray.size(); i++) {
                        JSONObject configJson = configArray.getJSONObject(i);
                        MenuRoleConfig config = JSON.toJavaObject(configJson, MenuRoleConfig.class);
                        if (config != null && config.getRoleId() != null) {
                            configs.put(config.getRoleId(), config);
                        }
                    }
                }
                
                log.info("Loaded {} menu role configs from {}", configs.size(), configPath);
            } catch (Exception e) {
                log.error("Failed to load menu role config: {}", e.getMessage());
                initDefaultConfigs();
            }
        } else {
            initDefaultConfigs();
            saveConfig();
        }
    }
    
    /**
     * 保存配置文件
     */
    private void saveConfig() {
        try {
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            
            Path configPath = Paths.get(CONFIG_DIR, CONFIG_FILE);
            
            JSONObject root = new JSONObject();
            root.put("version", "1.0");
            root.put("updatedAt", System.currentTimeMillis());
            
            JSONArray configArray = new JSONArray();
            for (MenuRoleConfig config : configs.values()) {
                configArray.add(JSON.parseObject(JSON.toJSONString(config)));
            }
            root.put("configs", configArray);
            
            String content = JSON.toJSONString(root, true);
            Files.write(configPath, content.getBytes(StandardCharsets.UTF_8));
            
            log.info("Saved {} menu role configs to {}", configs.size(), configPath);
        } catch (Exception e) {
            log.error("Failed to save menu role config: {}", e.getMessage());
        }
    }
    
    /**
     * 获取所有配置
     */
    public List<MenuRoleConfig> getAllConfigs() {
        return new ArrayList<>(configs.values());
    }
    
    /**
     * 初始化默认配置
     */
    private void initDefaultConfigs() {
        createDefaultConfig("installer", "系统安装者", Arrays.asList(
            createMenuItem("menu-1", "工作台", "/console/pages/role-installer.html", "ri-home-line", 1),
            createMenuItem("menu-2", "技能市场", "/console/pages/capability-discovery.html", "ri-store-2-line", 2),
            createMenuItem("menu-3", "已安装技能", "/console/pages/installed-scene-capabilities.html", "ri-download-cloud-line", 3)
        ));
        
        createDefaultConfig("admin", "系统管理员", Arrays.asList(
            createMenuItem("menu-1", "工作台", "/console/pages/role-admin.html", "ri-home-line", 1),
            createMenuItem("menu-2", "场景能力", "/console/pages/scene-capabilities.html", "ri-puzzle-line", 2),
            createMenuItem("menu-3", "发现场景", "/console/pages/capability-discovery.html", "ri-compass-discover-line", 3),
            createMenuItem("menu-4", "场景组管理", "/console/pages/scene-group-management.html", "ri-folder-line", 4),
            createMenuItem("menu-5", "能力统计", "/console/pages/capability-stats.html", "ri-bar-chart-box-line", 5),
            createMenuItem("menu-6", "组织管理", "/console/pages/org-management.html", "ri-organization-chart", 6),
            createMenuItem("menu-7", "架构检查", "/console/pages/arch-check.html", "ri-shield-check-line", 7)
        ));
        
        createDefaultConfig("leader", "主导者", Arrays.asList(
            createMenuItem("menu-1", "工作台", "/console/pages/role-leader.html", "ri-home-line", 1),
            createMenuItem("menu-2", "待激活场景", "/console/pages/my-todos.html", "ri-task-line", 2),
            createMenuItem("menu-3", "我的场景", "/console/pages/my-scenes.html", "ri-artboard-line", 3),
            createMenuItem("menu-4", "密钥管理", "/console/pages/key-management.html", "ri-key-2-line", 4)
        ));
        
        createDefaultConfig("collaborator", "协作者", Arrays.asList(
            createMenuItem("menu-1", "工作台", "/console/pages/role-collaborator.html", "ri-home-line", 1),
            createMenuItem("menu-2", "我的待办", "/console/pages/my-todos.html", "ri-task-line", 2),
            createMenuItem("menu-3", "参与场景", "/console/pages/my-scenes.html", "ri-artboard-line", 3),
            createMenuItem("menu-4", "历史记录", "/console/pages/my-history.html", "ri-history-line", 4)
        ));
    }
    
    private void createDefaultConfig(String roleId, String roleName, List<MenuRoleConfig.MenuItemConfig> menus) {
        MenuRoleConfig config = new MenuRoleConfig();
        config.setId("config-" + roleId);
        config.setRoleId(roleId);
        config.setRoleName(roleName);
        config.setMenus(menus);
        configs.put(roleId, config);
    }
    
    private MenuRoleConfig.MenuItemConfig createMenuItem(String id, String name, String url, String icon, int sort) {
        MenuRoleConfig.MenuItemConfig item = new MenuRoleConfig.MenuItemConfig();
        item.setId(id);
        item.setName(name);
        item.setUrl(url);
        item.setIcon(icon);
        item.setSort(sort);
        item.setActive(sort == 1);
        return item;
    }
    
    /**
     * 获取角色的菜单配置
     */
    public MenuRoleConfig getConfigByRole(String roleId) {
        return configs.get(roleId);
    }
    
    /**
     * 获取角色的菜单列表
     */
    public List<MenuItemDTO> getMenuItemsByRole(String roleId) {
        MenuRoleConfig config = configs.get(roleId);
        if (config == null || config.getMenus() == null) {
            return new ArrayList<>();
        }
        
        List<MenuItemDTO> items = new ArrayList<>();
        for (MenuRoleConfig.MenuItemConfig menuItem : config.getMenus()) {
            items.add(convertToDTO(menuItem));
        }
        
        return items;
    }
    
    private MenuItemDTO convertToDTO(MenuRoleConfig.MenuItemConfig config) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(config.getId());
        dto.setName(config.getName());
        dto.setUrl(config.getUrl());
        dto.setIcon(config.getIcon());
        dto.setSort(config.getSort());
        dto.setActive(config.isActive());
        
        if (config.getChildren() != null && !config.getChildren().isEmpty()) {
            List<MenuItemDTO> children = new ArrayList<>();
            for (MenuRoleConfig.MenuItemConfig child : config.getChildren()) {
                children.add(convertToDTO(child));
            }
            dto.setChildren(children);
        }
        
        return dto;
    }
    
    /**
     * 更新角色的菜单配置
     */
    public MenuRoleConfig updateConfig(String roleId, MenuRoleConfig config) {
        config.setRoleId(roleId);
        config.setUpdatedAt(System.currentTimeMillis());
        configs.put(roleId, config);
        saveConfig();
        log.info("Updated menu config for role: {}", roleId);
        return config;
    }
    
    /**
     * 添加菜单项到角色
     */
    public void addMenuItem(String roleId, MenuRoleConfig.MenuItemConfig menuItem) {
        MenuRoleConfig config = configs.get(roleId);
        if (config == null) {
            config = new MenuRoleConfig();
            config.setId("config-" + roleId);
            config.setRoleId(roleId);
            config.setRoleName(getRoleNameById(roleId));
            config.setMenus(new ArrayList<>());
            configs.put(roleId, config);
        }
        
        if (menuItem.getId() == null || menuItem.getId().isEmpty()) {
            menuItem.setId("menu-" + roleId + "-" + (config.getMenus().size() + 1));
        }
        
        if (config.getMenus() == null) {
            config.setMenus(new ArrayList<>());
        }
        
        if (!isMenuExists(config.getMenus(), menuItem.getId())) {
            config.getMenus().add(menuItem);
            config.setUpdatedAt(System.currentTimeMillis());
            saveConfig();
            log.info("Added menu item {} to role {}", menuItem.getId(), roleId);
        } else {
            log.debug("Menu item {} already exists in role {}", menuItem.getId(), roleId);
        }
    }
    
    private boolean isMenuExists(List<MenuRoleConfig.MenuItemConfig> menus, String menuId) {
        if (menus == null) return false;
        return menus.stream().anyMatch(m -> menuId.equals(m.getId()));
    }
    
    private String getRoleNameById(String roleId) {
        switch (roleId) {
            case "installer": return "系统安装者";
            case "admin": return "系统管理员";
            case "leader": return "主导者";
            case "collaborator": return "协作者";
            default: return roleId;
        }
    }
    
    /**
     * 移除菜单项
     */
    public boolean removeMenuItem(String roleId, String menuItemId) {
        MenuRoleConfig config = configs.get(roleId);
        if (config != null && config.getMenus() != null) {
            boolean removed = config.getMenus().removeIf(item -> menuItemId.equals(item.getId()));
            if (removed) {
                config.setUpdatedAt(System.currentTimeMillis());
                saveConfig();
                log.info("Removed menu item {} from role {}", menuItemId, roleId);
            }
            return removed;
        }
        return false;
    }
    
    /**
     * 导出配置为JSON
     */
    public String exportConfig() {
        JSONObject root = new JSONObject();
        root.put("version", "1.0");
        root.put("exportedAt", System.currentTimeMillis());
        
        JSONArray configArray = new JSONArray();
        for (MenuRoleConfig config : configs.values()) {
            configArray.add(JSON.parseObject(JSON.toJSONString(config)));
        }
        root.put("configs", configArray);
        
        return JSON.toJSONString(root, true);
    }
    
    /**
     * 导入配置
     */
    public void importConfig(String jsonContent) {
        try {
            JSONObject root = JSON.parseObject(jsonContent);
            JSONArray configArray = root.getJSONArray("configs");
            
            if (configArray != null) {
                for (int i = 0; i < configArray.size(); i++) {
                    JSONObject configJson = configArray.getJSONObject(i);
                    MenuRoleConfig config = JSON.toJavaObject(configJson, MenuRoleConfig.class);
                    if (config != null && config.getRoleId() != null) {
                        configs.put(config.getRoleId(), config);
                    }
                }
                saveConfig();
                log.info("Imported {} menu role configs", configArray.size());
            }
        } catch (Exception e) {
            log.error("Failed to import menu role config: {}", e.getMessage());
            throw new RuntimeException("导入配置失败: " + e.getMessage());
        }
    }
}

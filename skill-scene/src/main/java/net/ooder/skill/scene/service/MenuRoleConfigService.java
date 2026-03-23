package net.ooder.skill.scene.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.skill.scene.dto.menu.MenuConfigDTO;
import net.ooder.skill.scene.dto.menu.MenuItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 菜单角色配置服务
 * 使用 fastjson 完成持久�?
 */
@Service
public class MenuRoleConfigService {

    private static final Logger log = LoggerFactory.getLogger(MenuRoleConfigService.class);
    
    private static final String CONFIG_DIR = "data/config";
    private static final String CONFIG_FILE = "menu-role-config.json";
    
    private JSONObject menuConfig = null;
    
    @PostConstruct
    public void init() {
        loadConfig();
        log.info("MenuRoleConfigService initialized");
    }
    
    /**
     * 加载配置文件
     */
    private void loadConfig() {
        Path configPath = Paths.get(CONFIG_DIR, CONFIG_FILE);
        
        if (Files.exists(configPath)) {
            try {
                String content = new String(Files.readAllBytes(configPath), StandardCharsets.UTF_8);
                menuConfig = JSON.parseObject(content);
                log.info("Loaded menu config from {}", configPath);
            } catch (Exception e) {
                log.error("Failed to load menu config: {}", e.getMessage());
                initDefaultConfig();
            }
        } else {
            initDefaultConfig();
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
            String content = JSON.toJSONString(menuConfig, true);
            Files.write(configPath, content.getBytes(StandardCharsets.UTF_8));
            
            log.info("Saved menu config to {}", configPath);
        } catch (Exception e) {
            log.error("Failed to save menu config: {}", e.getMessage());
        }
    }
    
    /**
     * 初始化默认配�?
     */
    private void initDefaultConfig() {
        menuConfig = new JSONObject();
        menuConfig.put("version", "2.0");
        menuConfig.put("updatedAt", System.currentTimeMillis());
        
        JSONObject roles = new JSONObject();
        
        roles.put("admin", createRoleConfig("admin", "管理�?, getAdminMenus()));
        roles.put("user", createRoleConfig("user", "普通用�?, getUserMenus()));
        roles.put("developer", createRoleConfig("developer", "开发�?, getDeveloperMenus()));
        
        menuConfig.put("roles", roles);
    }
    
    private JSONObject createRoleConfig(String id, String name, JSONArray menus) {
        JSONObject role = new JSONObject();
        role.put("id", id);
        role.put("name", name);
        role.put("menus", menus);
        return role;
    }
    
    private JSONArray getAdminMenus() {
        JSONArray menus = new JSONArray();
        menus.add(createMenuItem("menu-admin-1", "工作�?, "/console/pages/role-admin.html", "ri-home-line", 1, true));
        menus.add(createMenuItem("menu-admin-2", "能力市场", "/console/pages/capability-discovery.html", "ri-store-2-line", 2, false));
        menus.add(createMenuItem("menu-admin-3", "已安装能�?, "/console/pages/installed-scene-capabilities.html", "ri-download-cloud-line", 3, false));
        menus.add(createMenuItem("menu-admin-4", "场景管理", "/console/pages/scene-group-management.html", "ri-folder-line", 4, false));
        menus.add(createMenuItem("menu-admin-5", "组织管理", "/console/pages/org-management.html", "ri-organization-chart", 5, false));
        menus.add(createMenuItem("menu-admin-6", "系统配置", "/console/pages/llm-config.html", "ri-settings-3-line", 6, false));
        menus.add(createMenuItem("menu-admin-7", "系统监控", "/console/pages/llm-monitor.html", "ri-line-chart-line", 7, false));
        menus.add(createMenuItem("menu-admin-8", "审计日志", "/console/pages/audit-logs.html", "ri-file-list-3-line", 8, false));
        return menus;
    }
    
    private JSONArray getUserMenus() {
        JSONArray menus = new JSONArray();
        menus.add(createMenuItem("menu-user-1", "工作�?, "/console/pages/role-user.html", "ri-home-line", 1, true));
        menus.add(createMenuItem("menu-user-2", "我的待办", "/console/pages/my-todos.html", "ri-task-line", 2, false));
        menus.add(createMenuItem("menu-user-3", "我的场景", "/console/pages/my-scenes.html", "ri-artboard-line", 3, false));
        menus.add(createMenuItem("menu-user-4", "历史记录", "/console/pages/my-history.html", "ri-history-line", 4, false));
        menus.add(createMenuItem("menu-user-5", "密钥管理", "/console/pages/key-management.html", "ri-key-2-line", 5, false));
        return menus;
    }
    
    private JSONArray getDeveloperMenus() {
        JSONArray menus = new JSONArray();
        menus.add(createMenuItem("menu-developer-1", "工作�?, "/console/pages/role-developer.html", "ri-home-line", 1, true));
        menus.add(createMenuItem("menu-developer-2", "我的能力", "/console/pages/my-capabilities.html", "ri-puzzle-line", 2, false));
        menus.add(createMenuItem("menu-developer-3", "创建能力", "/console/pages/capability-create.html", "ri-add-circle-line", 3, false));
        menus.add(createMenuItem("menu-developer-4", "架构检�?, "/console/pages/arch-check.html", "ri-shield-check-line", 4, false));
        menus.add(createMenuItem("menu-developer-5", "能力统计", "/console/pages/capability-stats.html", "ri-bar-chart-box-line", 5, false));
        return menus;
    }
    
    private JSONObject createMenuItem(String id, String name, String url, String icon, int sort, boolean active) {
        JSONObject item = new JSONObject();
        item.put("id", id);
        item.put("name", name);
        item.put("url", url);
        item.put("icon", icon);
        item.put("sort", sort);
        item.put("active", active);
        item.put("parentId", null);
        item.put("level", 0);
        return item;
    }
    
    private JSONObject createMenuItem(String id, String parentId, String name, String url, String icon, int sort, boolean active, int level) {
        JSONObject item = new JSONObject();
        item.put("id", id);
        item.put("parentId", parentId);
        item.put("name", name);
        item.put("url", url);
        item.put("icon", icon);
        item.put("sort", sort);
        item.put("active", active);
        item.put("level", level);
        return item;
    }
    
    /**
     * 获取角色的菜单列�?
     */
    public List<MenuItemDTO> getMenusByRole(String roleId) {
        List<MenuItemDTO> items = new ArrayList<>();
        
        if (menuConfig == null) {
            return items;
        }
        
        JSONObject roles = menuConfig.getJSONObject("roles");
        if (roles == null) {
            return items;
        }
        
        JSONObject role = roles.getJSONObject(roleId);
        if (role == null) {
            return items;
        }
        
        JSONArray menus = role.getJSONArray("menus");
        if (menus == null) {
            return items;
        }
        
        for (int i = 0; i < menus.size(); i++) {
            JSONObject menu = menus.getJSONObject(i);
            items.add(convertToMenuItem(menu));
        }
        
        return items;
    }
    
    /**
     * 获取所有角色配�?
     */
    public JSONObject getAllRoles() {
        if (menuConfig == null) {
            return new JSONObject();
        }
        return menuConfig.getJSONObject("roles");
    }
    
    /**
     * 获取所有角色配�?(DTO)
     */
    public Map<String, MenuConfigDTO.MenuRoleDTO> getAllRolesAsDTO() {
        Map<String, MenuConfigDTO.MenuRoleDTO> result = new HashMap<String, MenuConfigDTO.MenuRoleDTO>();
        
        if (menuConfig == null) {
            return result;
        }
        
        JSONObject roles = menuConfig.getJSONObject("roles");
        if (roles == null) {
            return result;
        }
        
        for (String roleId : roles.keySet()) {
            JSONObject roleJson = roles.getJSONObject(roleId);
            MenuConfigDTO.MenuRoleDTO roleDTO = new MenuConfigDTO.MenuRoleDTO();
            roleDTO.setId(roleJson.getString("id"));
            roleDTO.setName(roleJson.getString("name"));
            roleDTO.setDescription(roleJson.getString("description"));
            roleDTO.setIcon(roleJson.getString("icon"));
            roleDTO.setMenus(convertToMenuConfigDTOList(roleJson.getJSONArray("menus")));
            result.put(roleId, roleDTO);
        }
        
        return result;
    }
    
    /**
     * 获取角色菜单 (DTO)
     */
    public List<MenuItemDTO> getRoleMenus(String roleId) {
        List<MenuItemDTO> items = new ArrayList<MenuItemDTO>();
        
        if (menuConfig == null) {
            return items;
        }
        
        JSONObject roles = menuConfig.getJSONObject("roles");
        if (roles == null) {
            return items;
        }
        
        JSONObject role = roles.getJSONObject(roleId);
        if (role == null) {
            return items;
        }
        
        return convertToMenuItemList(role.getJSONArray("menus"));
    }
    
    /**
     * 更新角色菜单 (DTO)
     */
    public void updateRoleMenus(String roleId, List<MenuItemDTO> menus) {
        JSONArray menuArray = convertFromMenuItemList(menus);
        updateRoleMenus(roleId, menuArray);
    }
    
    /**
     * 添加菜单到角�?(DTO)
     */
    public void addMenuToRole(String roleId, MenuItemDTO menu) {
        JSONObject menuJson = convertFromMenuItem(menu);
        addMenuToRole(roleId, menuJson);
    }
    
    /**
     * 获取用户菜单 (DTO)
     */
    public List<MenuItemDTO> getUserMenusAsDTO(String userId) {
        JSONArray menus = getUserMenus(userId);
        return convertToMenuItemList(menus);
    }
    
    /**
     * 更新用户菜单配置 (DTO)
     */
    public void updateUserMenus(String userId, List<MenuItemDTO> menus) {
        JSONArray menuArray = convertFromMenuItemList(menus);
        updateUserMenus(userId, menuArray);
    }
    
    /**
     * 获取用户最终菜�?(DTO)
     */
    public List<MenuItemDTO> getFinalMenusForUserAsDTO(String userId, String roleId) {
        return getFinalMenusForUser(userId, roleId);
    }
    
    /**
     * 转换 JSONArray �?MenuItemDTO 列表
     */
    private List<MenuItemDTO> convertToMenuItemList(JSONArray menus) {
        List<MenuItemDTO> items = new ArrayList<MenuItemDTO>();
        
        if (menus == null) {
            return items;
        }
        
        for (int i = 0; i < menus.size(); i++) {
            JSONObject menu = menus.getJSONObject(i);
            items.add(convertToMenuItem(menu));
        }
        
        return items;
    }
    
    /**
     * 转换 JSONArray �?MenuConfigDTO 列表
     */
    private List<MenuConfigDTO> convertToMenuConfigDTOList(JSONArray menus) {
        List<MenuConfigDTO> items = new ArrayList<MenuConfigDTO>();
        
        if (menus == null) {
            return items;
        }
        
        for (int i = 0; i < menus.size(); i++) {
            JSONObject menu = menus.getJSONObject(i);
            items.add(convertToMenuConfigDTO(menu));
        }
        
        return items;
    }
    
    /**
     * 转换 JSONObject �?MenuConfigDTO
     */
    private MenuConfigDTO convertToMenuConfigDTO(JSONObject menu) {
        MenuConfigDTO item = new MenuConfigDTO();
        item.setId(menu.getString("id"));
        item.setName(menu.getString("name"));
        item.setUrl(menu.getString("url"));
        item.setIcon(menu.getString("icon"));
        item.setOrder(menu.getIntValue("order"));
        item.setVisible(menu.getBooleanValue("visible"));
        
        JSONArray children = menu.getJSONArray("children");
        if (children != null && !children.isEmpty()) {
            item.setChildren(convertToMenuConfigDTOList(children));
        }
        
        return item;
    }
    
    /**
     * 转换 JSONObject �?MenuItemDTO
     */
    private MenuItemDTO convertToMenuItem(JSONObject menu) {
        MenuItemDTO item = new MenuItemDTO();
        item.setId(menu.getString("id"));
        item.setParentId(menu.getString("parentId"));
        item.setName(menu.getString("name"));
        item.setUrl(menu.getString("url"));
        item.setIcon(menu.getString("icon"));
        item.setSort(menu.getIntValue("sort"));
        item.setActive(menu.getBooleanValue("active"));
        item.setLevel(menu.getIntValue("level"));
        return item;
    }
    
    /**
     * 转换 MenuItemDTO 列表�?JSONArray
     */
    private JSONArray convertFromMenuItemList(List<MenuItemDTO> items) {
        JSONArray array = new JSONArray();
        if (items == null) {
            return array;
        }
        for (MenuItemDTO item : items) {
            array.add(convertFromMenuItem(item));
        }
        return array;
    }
    
    /**
     * 转换 MenuItemDTO �?JSONObject
     */
    private JSONObject convertFromMenuItem(MenuItemDTO item) {
        JSONObject json = new JSONObject();
        json.put("id", item.getId());
        json.put("parentId", item.getParentId());
        json.put("name", item.getName());
        json.put("url", item.getUrl());
        json.put("icon", item.getIcon());
        json.put("sort", item.getSort());
        json.put("active", item.isActive());
        json.put("level", item.getLevel());
        return json;
    }
    
    /**
     * 更新角色菜单
     */
    public void updateRoleMenus(String roleId, JSONArray menus) {
        if (menuConfig == null) {
            return;
        }
        
        JSONObject roles = menuConfig.getJSONObject("roles");
        if (roles == null) {
            return;
        }
        
        JSONObject role = roles.getJSONObject(roleId);
        if (role == null) {
            role = new JSONObject();
            role.put("id", roleId);
            role.put("name", roleId);
            roles.put(roleId, role);
        }
        
        role.put("menus", menus);
        menuConfig.put("updatedAt", System.currentTimeMillis());
        saveConfig();
    }
    
    /**
     * 添加菜单到角�?
     */
    public void addMenuToRole(String roleId, JSONObject menu) {
        if (menuConfig == null) {
            return;
        }
        
        JSONObject roles = menuConfig.getJSONObject("roles");
        if (roles == null) {
            return;
        }
        
        JSONObject role = roles.getJSONObject(roleId);
        if (role == null) {
            return;
        }
        
        JSONArray menus = role.getJSONArray("menus");
        if (menus == null) {
            menus = new JSONArray();
            role.put("menus", menus);
        }
        
        menus.add(menu);
        menuConfig.put("updatedAt", System.currentTimeMillis());
        saveConfig();
    }
    
    /**
     * 导出配置
     */
    public String exportConfig() {
        if (menuConfig == null) {
            return "{}";
        }
        return JSON.toJSONString(menuConfig, true);
    }
    
    /**
     * 导入配置
     */
    public void importConfig(String jsonContent) {
        try {
            menuConfig = JSON.parseObject(jsonContent);
            saveConfig();
            log.info("Imported menu config");
        } catch (Exception e) {
            log.error("Failed to import menu config: {}", e.getMessage());
            throw new RuntimeException("导入配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的菜单配�?
     */
    public JSONArray getUserMenus(String userId) {
        if (menuConfig == null) {
            return new JSONArray();
        }
        
        JSONObject users = menuConfig.getJSONObject("users");
        if (users != null && users.containsKey(userId)) {
            JSONObject user = users.getJSONObject(userId);
            return user.getJSONArray("menus");
        }
        return new JSONArray();
    }
    
    /**
     * 更新用户菜单配置
     */
    public void updateUserMenus(String userId, JSONArray menus) {
        if (menuConfig == null) {
            return;
        }
        
        JSONObject users = menuConfig.getJSONObject("users");
        if (users == null) {
            users = new JSONObject();
            menuConfig.put("users", users);
        }
        
        JSONObject user = users.getJSONObject(userId);
        if (user == null) {
            user = new JSONObject();
            user.put("userId", userId);
            users.put(userId, user);
        }
        
        user.put("menus", menus);
        menuConfig.put("updatedAt", System.currentTimeMillis());
        saveConfig();
        log.info("Updated menus for user: {}", userId);
    }
    
    /**
     * 获取所有用户配�?
     */
    public JSONObject getAllUsers() {
        if (menuConfig == null) {
            return new JSONObject();
        }
        return menuConfig.getJSONObject("users");
    }
    
    /**
     * 删除用户菜单配置
     */
    public void deleteUserMenus(String userId) {
        if (menuConfig == null) {
            return;
        }
        
        JSONObject users = menuConfig.getJSONObject("users");
        if (users != null && users.containsKey(userId)) {
            users.remove(userId);
            menuConfig.put("updatedAt", System.currentTimeMillis());
            saveConfig();
            log.info("Deleted menus for user: {}", userId);
        }
    }
    
    /**
     * 获取用户最终菜单（合并角色菜单和用户菜单）
     */
    public List<MenuItemDTO> getFinalMenusForUser(String userId, String roleId) {
        List<MenuItemDTO> result = new ArrayList<>();
        Map<String, MenuItemDTO> menuMap = new LinkedHashMap<>();
        
        List<MenuItemDTO> roleMenus = getMenusByRole(roleId);
        for (MenuItemDTO menu : roleMenus) {
            menuMap.put(menu.getId(), menu);
        }
        
        JSONArray userMenus = getUserMenus(userId);
        if (userMenus != null) {
            for (int i = 0; i < userMenus.size(); i++) {
                JSONObject menu = userMenus.getJSONObject(i);
                MenuItemDTO item = new MenuItemDTO();
                item.setId(menu.getString("id"));
                item.setParentId(menu.getString("parentId"));
                item.setName(menu.getString("name"));
                item.setUrl(menu.getString("url"));
                item.setIcon(menu.getString("icon"));
                item.setSort(menu.getIntValue("sort"));
                item.setActive(menu.getBooleanValue("active"));
                item.setLevel(menu.getIntValue("level"));
                menuMap.put(item.getId(), item);
            }
        }
        
        result.addAll(menuMap.values());
        result.sort(Comparator.comparingInt(MenuItemDTO::getSort));
        
        return result;
    }
    
    /**
     * 获取角色的菜单树
     */
    public List<MenuItemDTO> getMenuTreeByRole(String roleId) {
        List<MenuItemDTO> allMenus = getMenusByRole(roleId);
        return buildMenuTree(allMenus);
    }
    
    /**
     * 构建菜单�?
     */
    private List<MenuItemDTO> buildMenuTree(List<MenuItemDTO> allMenus) {
        Map<String, MenuItemDTO> menuMap = new LinkedHashMap<>();
        List<MenuItemDTO> rootMenus = new ArrayList<>();
        
        for (MenuItemDTO menu : allMenus) {
            menuMap.put(menu.getId(), menu);
        }
        
        for (MenuItemDTO menu : allMenus) {
            String parentId = menu.getParentId();
            if (parentId == null || parentId.isEmpty()) {
                rootMenus.add(menu);
            } else {
                MenuItemDTO parent = menuMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<MenuItemDTO>());
                    }
                    parent.getChildren().add(menu);
                } else {
                    rootMenus.add(menu);
                }
            }
        }
        
        sortMenuTree(rootMenus);
        return rootMenus;
    }
    
    /**
     * 排序菜单�?
     */
    private void sortMenuTree(List<MenuItemDTO> menus) {
        if (menus == null || menus.isEmpty()) {
            return;
        }
        
        menus.sort(Comparator.comparingInt(MenuItemDTO::getSort));
        
        for (MenuItemDTO menu : menus) {
            if (menu.getChildren() != null) {
                sortMenuTree(menu.getChildren());
            }
        }
    }
    
    /**
     * 添加子菜�?
     */
    public void addChildMenu(String roleId, String parentId, MenuItemDTO menu) {
        if (menuConfig == null) {
            return;
        }
        
        JSONObject roles = menuConfig.getJSONObject("roles");
        if (roles == null) {
            return;
        }
        
        JSONObject role = roles.getJSONObject(roleId);
        if (role == null) {
            return;
        }
        
        JSONArray menus = role.getJSONArray("menus");
        if (menus == null) {
            menus = new JSONArray();
            role.put("menus", menus);
        }
        
        int level = 1;
        int sort = 1;
        
        if (parentId != null && !parentId.isEmpty()) {
            for (int i = 0; i < menus.size(); i++) {
                JSONObject m = menus.getJSONObject(i);
                if (parentId.equals(m.getString("id"))) {
                    level = m.getIntValue("level") + 1;
                    break;
                }
            }
            
            int maxSort = 0;
            for (int i = 0; i < menus.size(); i++) {
                JSONObject m = menus.getJSONObject(i);
                if (parentId.equals(m.getString("parentId"))) {
                    maxSort = Math.max(maxSort, m.getIntValue("sort"));
                }
            }
            sort = maxSort + 1;
        } else {
            int maxSort = 0;
            for (int i = 0; i < menus.size(); i++) {
                JSONObject m = menus.getJSONObject(i);
                if (m.getString("parentId") == null || m.getString("parentId").isEmpty()) {
                    maxSort = Math.max(maxSort, m.getIntValue("sort"));
                }
            }
            sort = maxSort + 1;
        }
        
        JSONObject menuJson = convertFromMenuItem(menu);
        menuJson.put("parentId", parentId);
        menuJson.put("level", level);
        menuJson.put("sort", sort);
        menus.add(menuJson);
        
        menuConfig.put("updatedAt", System.currentTimeMillis());
        saveConfig();
        log.info("Added child menu {} to role {}, parentId: {}", menu.getId(), roleId, parentId);
    }
    
    /**
     * 移动菜单
     */
    public void moveMenu(String roleId, String menuId, String newParentId, int newSort) {
        if (menuConfig == null) {
            return;
        }
        
        JSONObject roles = menuConfig.getJSONObject("roles");
        if (roles == null) {
            return;
        }
        
        JSONObject role = roles.getJSONObject(roleId);
        if (role == null) {
            return;
        }
        
        JSONArray menus = role.getJSONArray("menus");
        if (menus == null) {
            return;
        }
        
        for (int i = 0; i < menus.size(); i++) {
            JSONObject m = menus.getJSONObject(i);
            if (menuId.equals(m.getString("id"))) {
                int newLevel = 0;
                
                if (newParentId != null && !newParentId.isEmpty()) {
                    for (int j = 0; j < menus.size(); j++) {
                        JSONObject parent = menus.getJSONObject(j);
                        if (newParentId.equals(parent.getString("id"))) {
                            newLevel = parent.getIntValue("level") + 1;
                            break;
                        }
                    }
                }
                
                m.put("parentId", newParentId);
                m.put("level", newLevel);
                m.put("sort", newSort);
                break;
            }
        }
        
        menuConfig.put("updatedAt", System.currentTimeMillis());
        saveConfig();
        log.info("Moved menu {} in role {} to parentId: {}, sort: {}", menuId, roleId, newParentId, newSort);
    }
    
    /**
     * 删除菜单及其子菜�?
     */
    public void deleteMenuWithChildren(String roleId, String menuId) {
        if (menuConfig == null) {
            return;
        }
        
        JSONObject roles = menuConfig.getJSONObject("roles");
        if (roles == null) {
            return;
        }
        
        JSONObject role = roles.getJSONObject(roleId);
        if (role == null) {
            return;
        }
        
        JSONArray menus = role.getJSONArray("menus");
        if (menus == null) {
            return;
        }
        
        Set<String> toDelete = new HashSet<>();
        toDelete.add(menuId);
        
        boolean found = true;
        while (found) {
            found = false;
            for (int i = 0; i < menus.size(); i++) {
                JSONObject m = menus.getJSONObject(i);
                String parentId = m.getString("parentId");
                if (parentId != null && toDelete.contains(parentId) && !toDelete.contains(m.getString("id"))) {
                    toDelete.add(m.getString("id"));
                    found = true;
                }
            }
        }
        
        JSONArray newMenus = new JSONArray();
        for (int i = 0; i < menus.size(); i++) {
            JSONObject m = menus.getJSONObject(i);
            if (!toDelete.contains(m.getString("id"))) {
                newMenus.add(m);
            }
        }
        
        role.put("menus", newMenus);
        menuConfig.put("updatedAt", System.currentTimeMillis());
        saveConfig();
        log.info("Deleted menu {} and {} children in role {}", menuId, toDelete.size() - 1, roleId);
    }
    
    /**
     * 更新菜单信息
     */
    public void updateMenu(String roleId, String menuId, MenuItemDTO menu) {
        if (menuConfig == null) {
            return;
        }
        
        JSONObject roles = menuConfig.getJSONObject("roles");
        if (roles == null) {
            return;
        }
        
        JSONObject role = roles.getJSONObject(roleId);
        if (role == null) {
            return;
        }
        
        JSONArray menus = role.getJSONArray("menus");
        if (menus == null) {
            return;
        }
        
        for (int i = 0; i < menus.size(); i++) {
            JSONObject m = menus.getJSONObject(i);
            if (menuId.equals(m.getString("id"))) {
                m.put("name", menu.getName());
                m.put("url", menu.getUrl());
                m.put("icon", menu.getIcon());
                m.put("active", menu.isActive());
                if (menu.getSort() > 0) {
                    m.put("sort", menu.getSort());
                }
                break;
            }
        }
        
        menuConfig.put("updatedAt", System.currentTimeMillis());
        saveConfig();
        log.info("Updated menu {} in role {}", menuId, roleId);
    }
    
    /**
     * 注册场景技能菜�?
     * @param sceneGroupId 场景组ID
     * @param sceneName 场景名称
     * @param userId 用户ID
     * @param roleInScene 用户在场景中的角�?(MANAGER, EMPLOYEE, HR�?
     * @param menuItems 菜单项列�?
     */
    public void registerSceneMenus(String sceneGroupId, String sceneName, String userId, String roleInScene, List<MenuItemDTO> menuItems) {
        log.info("Registering scene menus for user: {}, scene: {}, role: {}", userId, sceneName, roleInScene);
        
        if (menuConfig == null) {
            initDefaultConfig();
        }
        
        JSONObject users = menuConfig.getJSONObject("users");
        if (users == null) {
            users = new JSONObject();
            menuConfig.put("users", users);
        }
        
        JSONObject user = users.getJSONObject(userId);
        if (user == null) {
            user = new JSONObject();
            user.put("id", userId);
            user.put("sceneMenus", new JSONObject());
            users.put(userId, user);
        }
        
        JSONObject sceneMenus = user.getJSONObject("sceneMenus");
        if (sceneMenus == null) {
            sceneMenus = new JSONObject();
            user.put("sceneMenus", sceneMenus);
        }
        
        JSONObject sceneMenu = new JSONObject();
        sceneMenu.put("sceneGroupId", sceneGroupId);
        sceneMenu.put("sceneName", sceneName);
        sceneMenu.put("role", roleInScene);
        sceneMenu.put("createdAt", System.currentTimeMillis());
        
        JSONArray itemsArray = new JSONArray();
        if (menuItems != null) {
            for (MenuItemDTO item : menuItems) {
                itemsArray.add(convertFromMenuItem(item));
            }
        }
        sceneMenu.put("items", itemsArray);
        
        sceneMenus.put(sceneGroupId, sceneMenu);
        
        menuConfig.put("updatedAt", System.currentTimeMillis());
        saveConfig();
        
        log.info("Scene menus registered successfully: {} items", menuItems != null ? menuItems.size() : 0);
    }
    
    /**
     * 获取用户的场景菜�?
     */
    public List<MenuItemDTO> getUserSceneMenus(String userId) {
        List<MenuItemDTO> result = new ArrayList<>();
        
        if (menuConfig == null) {
            return result;
        }
        
        JSONObject users = menuConfig.getJSONObject("users");
        if (users == null) {
            return result;
        }
        
        JSONObject user = users.getJSONObject(userId);
        if (user == null) {
            return result;
        }
        
        JSONObject sceneMenus = user.getJSONObject("sceneMenus");
        if (sceneMenus == null) {
            return result;
        }
        
        for (String sceneGroupId : sceneMenus.keySet()) {
            JSONObject sceneMenu = sceneMenus.getJSONObject(sceneGroupId);
            JSONArray items = sceneMenu.getJSONArray("items");
            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    result.add(convertToMenuItem(items.getJSONObject(i)));
                }
            }
        }
        
        return result;
    }
    
    /**
     * 移除用户的场景菜�?
     */
    public void removeSceneMenus(String userId, String sceneGroupId) {
        log.info("Removing scene menus for user: {}, scene: {}", userId, sceneGroupId);
        
        if (menuConfig == null) {
            return;
        }
        
        JSONObject users = menuConfig.getJSONObject("users");
        if (users == null) {
            return;
        }
        
        JSONObject user = users.getJSONObject(userId);
        if (user == null) {
            return;
        }
        
        JSONObject sceneMenus = user.getJSONObject("sceneMenus");
        if (sceneMenus != null) {
            sceneMenus.remove(sceneGroupId);
            menuConfig.put("updatedAt", System.currentTimeMillis());
            saveConfig();
        }
    }
    
    /**
     * 获取用户在指定场景中的菜�?
     */
    public List<MenuItemDTO> getUserSceneMenu(String userId, String sceneGroupId) {
        List<MenuItemDTO> result = new ArrayList<>();
        
        if (menuConfig == null) {
            return result;
        }
        
        JSONObject users = menuConfig.getJSONObject("users");
        if (users == null) {
            return result;
        }
        
        JSONObject user = users.getJSONObject(userId);
        if (user == null) {
            return result;
        }
        
        JSONObject sceneMenus = user.getJSONObject("sceneMenus");
        if (sceneMenus == null) {
            return result;
        }
        
        JSONObject sceneMenu = sceneMenus.getJSONObject(sceneGroupId);
        if (sceneMenu == null) {
            return result;
        }
        
        JSONArray items = sceneMenu.getJSONArray("items");
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                result.add(convertToMenuItem(items.getJSONObject(i)));
            }
        }
        
        return result;
    }
    
    /**
     * 从场景模板注册菜�?
     */
    public void registerSceneMenusFromTemplate(String sceneGroupId, String sceneName, String userId, String roleInScene, List<net.ooder.skill.scene.template.MenuConfig> templateMenus) {
        List<MenuItemDTO> menuItems = new ArrayList<>();
        
        if (templateMenus != null) {
            for (net.ooder.skill.scene.template.MenuConfig templateMenu : templateMenus) {
                MenuItemDTO item = convertTemplateToMenuItem(templateMenu, sceneGroupId);
                menuItems.add(item);
            }
        }
        
        registerSceneMenus(sceneGroupId, sceneName, userId, roleInScene, menuItems);
    }
    
    /**
     * 从场景模板注册菜�?(DTO版本)
     */
    public void registerSceneMenusFromTemplateDTO(String sceneGroupId, String sceneName, String userId, String roleInScene, List<MenuConfigDTO> templateMenus) {
        List<MenuItemDTO> menuItems = new ArrayList<>();
        
        if (templateMenus != null) {
            for (MenuConfigDTO templateMenu : templateMenus) {
                MenuItemDTO item = convertDTOToMenuItem(templateMenu, sceneGroupId);
                menuItems.add(item);
            }
        }
        
        registerSceneMenus(sceneGroupId, sceneName, userId, roleInScene, menuItems);
    }
    
    /**
     * 将DTO菜单配置转换为MenuItemDTO
     */
    private MenuItemDTO convertDTOToMenuItem(MenuConfigDTO dto, String sceneGroupId) {
        MenuItemDTO item = new MenuItemDTO();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setIcon(dto.getIcon());
        
        String url = dto.getUrl();
        if (url != null && !url.isEmpty()) {
            if (url.contains("?")) {
                url += "&sceneGroupId=" + sceneGroupId;
            } else {
                url += "?sceneGroupId=" + sceneGroupId;
            }
        }
        item.setUrl(url);
        item.setSort(dto.getOrder());
        item.setVisible(dto.isVisible());
        item.setActive(true);
        
        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            List<MenuItemDTO> children = new ArrayList<>();
            for (MenuConfigDTO child : dto.getChildren()) {
                children.add(convertDTOToMenuItem(child, sceneGroupId));
            }
            item.setChildren(children);
        }
        
        return item;
    }
    
    /**
     * 将模板菜单配置转换为MenuItemDTO
     */
    private MenuItemDTO convertTemplateToMenuItem(net.ooder.skill.scene.template.MenuConfig templateMenu, String sceneGroupId) {
        MenuItemDTO item = new MenuItemDTO();
        item.setId(templateMenu.getId());
        item.setName(templateMenu.getName());
        item.setIcon(templateMenu.getIcon());
        
        String url = templateMenu.getUrl();
        if (url != null && !url.isEmpty()) {
            if (url.contains("?")) {
                url += "&sceneGroupId=" + sceneGroupId;
            } else {
                url += "?sceneGroupId=" + sceneGroupId;
            }
        }
        item.setUrl(url);
        item.setSort(templateMenu.getOrder());
        item.setVisible(templateMenu.isVisible());
        item.setActive(true);
        
        if (templateMenu.getChildren() != null && !templateMenu.getChildren().isEmpty()) {
            List<MenuItemDTO> children = new ArrayList<>();
            for (net.ooder.skill.scene.template.MenuConfig child : templateMenu.getChildren()) {
                children.add(convertTemplateToMenuItem(child, sceneGroupId));
            }
            item.setChildren(children);
        }
        
        return item;
    }
    
    /**
     * 获取用户的最终菜单（包含角色菜单和场景菜单）
     */
    public List<MenuItemDTO> getFinalMenusForUserWithScene(String userId, String roleId) {
        List<MenuItemDTO> result = new ArrayList<>();
        Map<String, MenuItemDTO> menuMap = new LinkedHashMap<>();
        
        List<MenuItemDTO> roleMenus = getMenusByRole(roleId);
        for (MenuItemDTO menu : roleMenus) {
            menuMap.put(menu.getId(), menu);
        }
        
        List<MenuItemDTO> sceneMenus = getUserSceneMenus(userId);
        for (MenuItemDTO menu : sceneMenus) {
            menuMap.put(menu.getId(), menu);
        }
        
        JSONArray userMenus = getUserMenus(userId);
        if (userMenus != null) {
            for (int i = 0; i < userMenus.size(); i++) {
                JSONObject menu = userMenus.getJSONObject(i);
                MenuItemDTO item = new MenuItemDTO();
                item.setId(menu.getString("id"));
                item.setParentId(menu.getString("parentId"));
                item.setName(menu.getString("name"));
                item.setUrl(menu.getString("url"));
                item.setIcon(menu.getString("icon"));
                item.setSort(menu.getIntValue("sort"));
                item.setActive(menu.getBooleanValue("active"));
                item.setLevel(menu.getIntValue("level"));
                menuMap.put(item.getId(), item);
            }
        }
        
        result.addAll(menuMap.values());
        result.sort(Comparator.comparingInt(MenuItemDTO::getSort));
        
        return result;
    }
    
    /**
     * 获取用户的场景菜单树（按场景分组�?
     */
    public Map<String, List<MenuItemDTO>> getUserSceneMenuTree(String userId) {
        Map<String, List<MenuItemDTO>> result = new LinkedHashMap<>();
        
        if (menuConfig == null) {
            return result;
        }
        
        JSONObject users = menuConfig.getJSONObject("users");
        if (users == null) {
            return result;
        }
        
        JSONObject user = users.getJSONObject(userId);
        if (user == null) {
            return result;
        }
        
        JSONObject sceneMenus = user.getJSONObject("sceneMenus");
        if (sceneMenus == null) {
            return result;
        }
        
        for (String sceneGroupId : sceneMenus.keySet()) {
            JSONObject sceneMenu = sceneMenus.getJSONObject(sceneGroupId);
            String sceneName = sceneMenu.getString("sceneName");
            JSONArray items = sceneMenu.getJSONArray("items");
            
            List<MenuItemDTO> menuItems = new ArrayList<>();
            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    menuItems.add(convertToMenuItem(items.getJSONObject(i)));
                }
            }
            
            result.put(sceneName, menuItems);
        }
        
        return result;
    }
}

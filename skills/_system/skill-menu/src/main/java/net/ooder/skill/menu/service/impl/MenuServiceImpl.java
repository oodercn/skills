package net.ooder.skill.menu.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import net.ooder.skill.menu.dto.MenuDTO;
import net.ooder.skill.menu.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuServiceImpl.class);
    
    private static final String CONFIG_DIR = "data/config";
    private static final String MENUS_FILE = "menus.json";
    
    private final Map<String, MenuDTO> menuRegistry = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        loadMenus();
        log.info("[MenuServiceImpl] Initialized with {} menus", menuRegistry.size());
    }
    
    private void loadMenus() {
        Path menuPath = Paths.get(CONFIG_DIR, MENUS_FILE);
        
        if (Files.exists(menuPath)) {
            try {
                String content = new String(Files.readAllBytes(menuPath), StandardCharsets.UTF_8);
                List<MenuDTO> menus = JSON.parseArray(content, MenuDTO.class);
                if (menus != null) {
                    for (MenuDTO menu : menus) {
                        String key = menu.getMenuId() != null ? menu.getMenuId() : 
                                    (menu.getId() != null ? String.valueOf(menu.getId()) : null);
                        if (key != null) {
                            menuRegistry.put(key, menu);
                        }
                    }
                }
                log.info("Loaded {} menus from {}", menus != null ? menus.size() : 0, menuPath);
            } catch (Exception e) {
                log.error("Failed to load menus: {}", e.getMessage());
                initializeDefaultMenus();
            }
        } else {
            initializeDefaultMenus();
            saveMenus();
        }
    }
    
    private void saveMenus() {
        try {
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            
            Path menuPath = Paths.get(CONFIG_DIR, MENUS_FILE);
            List<MenuDTO> menus = new ArrayList<>(menuRegistry.values());
            String content = JSON.toJSONString(menus, JSONWriter.Feature.PrettyFormat);
            Files.write(menuPath, content.getBytes(StandardCharsets.UTF_8));
            
            log.info("Saved {} menus to {}", menus.size(), menuPath);
        } catch (Exception e) {
            log.error("Failed to save menus: {}", e.getMessage());
        }
    }
    
    @Override
    public void initializeDefaultMenus() {
        int sort = 0;

        createMenuDTO(++sort, "menu-dashboard", null,
                "工作台", "ri-home-line", "/console/pages/workbench.html");

        createMenuDTO(++sort, "menu-capability", null,
                "能力管理", "ri-apps-line", null);
        createMenuDTO(++sort, "menu-my-capabilities", "menu-capability",
                "我的能力", "ri-user-star-line", "/console/pages/my-capabilities.html");
        createMenuDTO(++sort, "menu-capability-discovery", "menu-capability",
                "能力发现", "ri-store-2-line", "/console/pages/capability-discovery.html");
        createMenuDTO(++sort, "menu-install-scene", "menu-capability",
                "场景安装", "ri-install-line", "/console/pages/install-scene.html");

        createMenuDTO(++sort, "menu-agent", null,
                "智能体管理", "ri-robot-line", null);
        createMenuDTO(++sort, "menu-agent-chat", "menu-agent",
                "对话管理", "ri-chat-3-line", "/console/pages/agent/chat.html");

        createMenuDTO(++sort, "menu-knowledge", null,
                "知识库管理", "ri-book-open-line", null);
        createMenuDTO(++sort, "menu-knowledge-base", "menu-knowledge",
                "知识库列表", "ri-database-2-line", "/console/pages/knowledge-base.html");

        createMenuDTO(++sort, "menu-settings", null,
                "系统设置", "ri-settings-3-line", null);
        createMenuDTO(++sort, "menu-settings-menu", "menu-settings",
                "菜单管理", "ri-menu-line", "/console/pages/settings/menus.html");
        createMenuDTO(++sort, "menu-settings-role", "menu-settings",
                "角色权限", "ri-shield-user-line", "/console/pages/settings/roles.html");
    }

    private MenuDTO createMenuDTO(int sort, String menuId, String parentId,
                                       String name, String icon, String url) {
        MenuDTO item = new MenuDTO();
        item.setId((long) sort);
        item.setMenuId(menuId);
        item.setParentId(parentId);
        item.setName(name);
        item.setTitle(name);
        item.setIcon(icon);
        item.setUrl(url);
        item.setSort(sort);
        item.setVisible(true);
        item.setEnabled(true);
        menuRegistry.put(menuId, item);
        return item;
    }
    
    @Override
    public MenuDTO createMenu(MenuDTO menuDTO) {
        if (menuDTO == null) {
            return null;
        }
        
        String key = menuDTO.getMenuId() != null ? menuDTO.getMenuId() : 
                    (menuDTO.getId() != null ? String.valueOf(menuDTO.getId()) : UUID.randomUUID().toString());
        
        if (menuDTO.getMenuId() == null) {
            menuDTO.setMenuId(key);
        }
        if (menuDTO.getSort() == null || menuDTO.getSort() == 0) {
            menuDTO.setSort(menuRegistry.size() + 1);
        }
        if (menuDTO.getVisible() == null) {
            menuDTO.setVisible(true);
        }
        if (menuDTO.getEnabled() == null) {
            menuDTO.setEnabled(true);
        }
        
        menuRegistry.put(key, menuDTO);
        saveMenus();
        
        log.info("[createMenu] Created menu: {} - {}", key, menuDTO.getName());
        return menuDTO;
    }
    
    @Override
    public MenuDTO updateMenu(String menuId, MenuDTO menuDTO) {
        if (menuId == null || menuDTO == null) {
            return null;
        }
        
        MenuDTO existing = menuRegistry.get(menuId);
        if (existing == null) {
            return null;
        }
        
        if (menuDTO.getName() != null) existing.setName(menuDTO.getName());
        if (menuDTO.getTitle() != null) existing.setTitle(menuDTO.getTitle());
        if (menuDTO.getIcon() != null) existing.setIcon(menuDTO.getIcon());
        if (menuDTO.getUrl() != null) existing.setUrl(menuDTO.getUrl());
        if (menuDTO.getSort() != null) existing.setSort(menuDTO.getSort());
        if (menuDTO.getVisible() != null) existing.setVisible(menuDTO.getVisible());
        if (menuDTO.getEnabled() != null) existing.setEnabled(menuDTO.getEnabled());
        
        saveMenus();
        
        log.info("[updateMenu] Updated menu: {}", menuId);
        return existing;
    }
    
    @Override
    public void deleteMenu(String menuId) {
        if (menuId == null) {
            return;
        }
        
        menuRegistry.remove(menuId);
        
        for (MenuDTO menu : menuRegistry.values()) {
            if (menuId.equals(menu.getParentId())) {
                menu.setParentId(null);
            }
        }
        
        saveMenus();
        log.info("[deleteMenu] Deleted menu: {}", menuId);
    }
    
    @Override
    public MenuDTO getMenu(String menuId) {
        return menuRegistry.get(menuId);
    }
    
    @Override
    public List<MenuDTO> getAllMenus() {
        return new ArrayList<>(menuRegistry.values());
    }
    
    @Override
    public List<MenuDTO> getMenuTree() {
        List<MenuDTO> allMenus = menuRegistry.values().stream()
            .sorted(Comparator.comparingInt(m -> m.getSort() != null ? m.getSort() : 0))
            .collect(Collectors.toList());
        
        Map<String, MenuDTO> menuMap = new LinkedHashMap<>();
        for (MenuDTO menu : allMenus) {
            menuMap.put(menu.getMenuId(), menu);
            menu.setChildren(new ArrayList<>());
        }
        
        List<MenuDTO> rootMenus = new ArrayList<>();
        for (MenuDTO menu : allMenus) {
            String parentId = menu.getParentId();
            if (parentId == null || parentId.isEmpty()) {
                rootMenus.add(menu);
            } else {
                MenuDTO parent = menuMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(menu);
                } else {
                    rootMenus.add(menu);
                }
            }
        }
        
        return rootMenus;
    }
    
    @Override
    public List<MenuDTO> getMenusByCategory(String category) {
        List<MenuDTO> result = new ArrayList<>();
        for (MenuDTO menu : menuRegistry.values()) {
            if (category == null || category.isEmpty() || category.equals(menu.getCategory())) {
                result.add(menu);
            }
        }
        return result;
    }
}

package net.ooder.skill.menu.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import net.ooder.skill.menu.dto.MenuItemDTO;
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
    
    private final Map<String, MenuItemDTO> menuRegistry = new ConcurrentHashMap<>();
    
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
                List<MenuItemDTO> menus = JSON.parseArray(content, MenuItemDTO.class);
                if (menus != null) {
                    for (MenuItemDTO menu : menus) {
                        if (menu.getId() != null) {
                            menuRegistry.put(menu.getId(), menu);
                        }
                    }
                }
                log.info("Loaded {} menus from {}", menus != null ? menus.size() : 0, menuPath);
            } catch (Exception e) {
                log.error("Failed to load menus: {}", e.getMessage());
                initDefaultMenus();
            }
        } else {
            initDefaultMenus();
            saveMenus();
        }
    }
    
    private void initDefaultMenus() {
        MenuItemDTO dashboard = new MenuItemDTO();
        dashboard.setId("menu-dashboard");
        dashboard.setName("工作台");
        dashboard.setUrl("/console/pages/workbench.html");
        dashboard.setIcon("ri-home-line");
        dashboard.setSort(1);
        dashboard.setVisible(true);
        dashboard.setActive(true);
        dashboard.setLevel(0);
        menuRegistry.put(dashboard.getId(), dashboard);
        
        MenuItemDTO settings = new MenuItemDTO();
        settings.setId("menu-settings");
        settings.setName("系统设置");
        settings.setUrl("/console/pages/settings.html");
        settings.setIcon("ri-settings-3-line");
        settings.setSort(2);
        settings.setVisible(true);
        settings.setActive(true);
        settings.setLevel(0);
        menuRegistry.put(settings.getId(), settings);
    }
    
    private void saveMenus() {
        try {
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            
            Path menuPath = Paths.get(CONFIG_DIR, MENUS_FILE);
            List<MenuItemDTO> menus = new ArrayList<>(menuRegistry.values());
            String content = JSON.toJSONString(menus, JSONWriter.Feature.PrettyFormat);
            Files.write(menuPath, content.getBytes(StandardCharsets.UTF_8));
            
            log.info("Saved {} menus to {}", menus.size(), menuPath);
        } catch (Exception e) {
            log.error("Failed to save menus: {}", e.getMessage());
        }
    }
    
    @Override
    public MenuItemDTO create(MenuItemDTO menu) {
        if (menu == null || menu.getId() == null) {
            return null;
        }
        
        if (menu.getSort() == 0) {
            menu.setSort(menuRegistry.size() + 1);
        }
        if (menu.getParentId() == null || menu.getParentId().isEmpty()) {
            menu.setLevel(0);
        }
        
        menuRegistry.put(menu.getId(), menu);
        saveMenus();
        
        log.info("[create] Created menu: {} - {}", menu.getId(), menu.getName());
        return menu;
    }
    
    @Override
    public MenuItemDTO update(MenuItemDTO menu) {
        if (menu == null || menu.getId() == null) {
            return null;
        }
        
        MenuItemDTO existing = menuRegistry.get(menu.getId());
        if (existing == null) {
            return null;
        }
        
        if (menu.getName() != null) existing.setName(menu.getName());
        if (menu.getIcon() != null) existing.setIcon(menu.getIcon());
        if (menu.getUrl() != null) existing.setUrl(menu.getUrl());
        if (menu.getSort() > 0) existing.setSort(menu.getSort());
        existing.setVisible(menu.isVisible());
        existing.setActive(menu.isActive());
        
        saveMenus();
        
        log.info("[update] Updated menu: {}", menu.getId());
        return existing;
    }
    
    @Override
    public void delete(String menuId) {
        if (menuId == null) {
            return;
        }
        
        menuRegistry.remove(menuId);
        
        for (MenuItemDTO menu : menuRegistry.values()) {
            if (menuId.equals(menu.getParentId())) {
                menu.setParentId(null);
            }
        }
        
        saveMenus();
        log.info("[delete] Deleted menu: {}", menuId);
    }
    
    @Override
    public MenuItemDTO findById(String menuId) {
        return menuRegistry.get(menuId);
    }
    
    @Override
    public List<MenuItemDTO> findAll() {
        return new ArrayList<>(menuRegistry.values());
    }
    
    @Override
    public List<MenuItemDTO> getMenuTree() {
        List<MenuItemDTO> allMenus = menuRegistry.values().stream()
            .sorted(Comparator.comparingInt(MenuItemDTO::getSort))
            .collect(Collectors.toList());
        
        Map<String, MenuItemDTO> menuMap = new LinkedHashMap<>();
        for (MenuItemDTO menu : allMenus) {
            menuMap.put(menu.getId(), menu);
            menu.setChildren(new ArrayList<>());
        }
        
        List<MenuItemDTO> rootMenus = new ArrayList<>();
        for (MenuItemDTO menu : allMenus) {
            String parentId = menu.getParentId();
            if (parentId == null || parentId.isEmpty()) {
                rootMenus.add(menu);
            } else {
                MenuItemDTO parent = menuMap.get(parentId);
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
    public void move(String menuId, String newParentId, int newSort) {
        MenuItemDTO menu = menuRegistry.get(menuId);
        if (menu == null) {
            return;
        }
        
        menu.setParentId(newParentId);
        menu.setSort(newSort);
        
        if (newParentId != null && !newParentId.isEmpty()) {
            MenuItemDTO parent = menuRegistry.get(newParentId);
            if (parent != null) {
                menu.setLevel(parent.getLevel() + 1);
            }
        } else {
            menu.setLevel(0);
        }
        
        saveMenus();
        log.info("[move] Moved menu {} to parentId: {}, sort: {}", menuId, newParentId, newSort);
    }
    
    @Override
    public List<MenuItemDTO> findByRoleId(String roleId) {
        return new ArrayList<>(menuRegistry.values());
    }
    
    @Override
    public List<MenuItemDTO> findByUserId(String userId) {
        return new ArrayList<>(menuRegistry.values());
    }
    
    @Override
    public void setRoleMenus(String roleId, List<String> menuIds) {
        log.info("[setRoleMenus] Set {} menus for role {}", menuIds != null ? menuIds.size() : 0, roleId);
    }
}

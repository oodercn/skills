package net.ooder.nexus.common.service;

import net.ooder.nexus.common.model.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MenuRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(MenuRegistry.class);
    
    private final Map<String, MenuItem> menuItems = new ConcurrentHashMap<>();
    private final Map<String, List<MenuItem>> categoryMenus = new ConcurrentHashMap<>();
    
    public void register(MenuItem item) {
        if (item == null || item.getId() == null) {
            return;
        }
        
        menuItems.put(item.getId(), item);
        
        String category = item.getCategory() != null ? item.getCategory() : "default";
        categoryMenus.computeIfAbsent(category, k -> new ArrayList<>()).add(item);
        
        log.info("Registered menu item: {} in category: {}", item.getId(), category);
    }
    
    public void unregister(String itemId) {
        MenuItem removed = menuItems.remove(itemId);
        if (removed != null) {
            String category = removed.getCategory() != null ? removed.getCategory() : "default";
            List<MenuItem> categoryList = categoryMenus.get(category);
            if (categoryList != null) {
                categoryList.removeIf(item -> item.getId().equals(itemId));
            }
            log.info("Unregistered menu item: {}", itemId);
        }
    }
    
    public MenuItem get(String itemId) {
        return menuItems.get(itemId);
    }
    
    public List<MenuItem> getAll() {
        return new ArrayList<>(menuItems.values());
    }
    
    public List<MenuItem> getByCategory(String category) {
        List<MenuItem> items = categoryMenus.get(category);
        return items != null ? new ArrayList<>(items) : new ArrayList<>();
    }
    
    public List<MenuItem> getRootMenuItems() {
        List<MenuItem> roots = new ArrayList<>();
        for (MenuItem item : menuItems.values()) {
            if (item.getCategory() == null || "root".equals(item.getCategory())) {
                roots.add(item);
            }
        }
        roots.sort(Comparator.comparingInt(MenuItem::getOrder));
        return roots;
    }
    
    public Map<String, List<MenuItem>> getAllByCategory() {
        Map<String, List<MenuItem>> result = new HashMap<>();
        for (Map.Entry<String, List<MenuItem>> entry : categoryMenus.entrySet()) {
            List<MenuItem> items = new ArrayList<>(entry.getValue());
            items.sort(Comparator.comparingInt(MenuItem::getOrder));
            result.put(entry.getKey(), items);
        }
        return result;
    }
    
    public void clear() {
        menuItems.clear();
        categoryMenus.clear();
        log.info("Cleared all menu items");
    }
    
    public int size() {
        return menuItems.size();
    }
}

package net.ooder.skill.menu.service;

import java.util.List;
import net.ooder.skill.menu.dto.*;

public interface MenuService {
    
    MenuItemDTO create(MenuItemDTO menu);
    
    MenuItemDTO update(MenuItemDTO menu);
    
    void delete(String menuId);
    
    MenuItemDTO findById(String menuId);
    
    List<MenuItemDTO> findAll();
    
    List<MenuItemDTO> getMenuTree();
    
    void move(String menuId, String newParentId, int newSort);
    
    List<MenuItemDTO> findByRoleId(String roleId);
    
    List<MenuItemDTO> findByUserId(String userId);
    
    void setRoleMenus(String roleId, List<String> menuIds);
}

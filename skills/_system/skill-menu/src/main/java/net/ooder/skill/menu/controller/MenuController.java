package net.ooder.skill.menu.controller;

import net.ooder.skill.menu.dto.MenuItemDTO;
import net.ooder.skill.menu.model.ResultModel;
import net.ooder.skill.menu.service.MenuService;
import net.ooder.skill.menu.service.MenuRoleConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    @Autowired(required = false)
    private MenuService menuService;

    @Autowired(required = false)
    private MenuRoleConfigService menuRoleConfigService;

    @GetMapping("/menus")
    public ResultModel<List<MenuItemDTO>> listMenus() {
        log.info("[listMenus] Loading all menus");
        
        if (menuService == null) {
            return ResultModel.success(new ArrayList<>());
        }
        
        List<MenuItemDTO> menus = menuService.findAll();
        return ResultModel.success(menus);
    }

    @GetMapping("/menus/tree")
    public ResultModel<List<MenuItemDTO>> getMenuTree() {
        log.info("[getMenuTree] Loading menu tree");
        
        if (menuService == null) {
            return ResultModel.success(new ArrayList<>());
        }
        
        List<MenuItemDTO> tree = menuService.getMenuTree();
        return ResultModel.success(tree);
    }

    @GetMapping("/menus/{menuId}")
    public ResultModel<MenuItemDTO> getMenu(@PathVariable String menuId) {
        log.info("[getMenu] Loading menu: {}", menuId);
        
        if (menuService == null) {
            return ResultModel.error(ResultModel.CODE_INTERNAL_SERVER_ERROR, "MenuService not available");
        }
        
        MenuItemDTO menu = menuService.findById(menuId);
        if (menu == null) {
            return ResultModel.notFound("Menu not found: " + menuId);
        }
        return ResultModel.success(menu);
    }

    @PostMapping("/menus")
    public ResultModel<MenuItemDTO> createMenu(@RequestBody MenuItemDTO menu) {
        log.info("[createMenu] Creating menu: {}", menu != null ? menu.getId() : null);
        
        if (menuService == null) {
            return ResultModel.error(ResultModel.CODE_INTERNAL_SERVER_ERROR, "MenuService not available");
        }
        
        MenuItemDTO created = menuService.create(menu);
        if (created == null) {
            return ResultModel.error(ResultModel.CODE_INTERNAL_SERVER_ERROR, "Failed to create menu");
        }
        return ResultModel.success(created);
    }

    @PutMapping("/menus/{menuId}")
    public ResultModel<MenuItemDTO> updateMenu(@PathVariable String menuId, @RequestBody MenuItemDTO menu) {
        log.info("[updateMenu] Updating menu: {}", menuId);
        
        if (menuService == null) {
            return ResultModel.error(ResultModel.CODE_INTERNAL_SERVER_ERROR, "MenuService not available");
        }
        
        if (menu != null) {
            menu.setId(menuId);
        }
        
        MenuItemDTO updated = menuService.update(menu);
        if (updated == null) {
            return ResultModel.error(ResultModel.CODE_INTERNAL_SERVER_ERROR, "Failed to update menu: " + menuId);
        }
        return ResultModel.success(updated);
    }

    @DeleteMapping("/menus/{menuId}")
    public ResultModel<Void> deleteMenu(@PathVariable String menuId) {
        log.info("[deleteMenu] Deleting menu: {}", menuId);
        
        if (menuService == null) {
            return ResultModel.error(ResultModel.CODE_INTERNAL_SERVER_ERROR, "MenuService not available");
        }
        
        menuService.delete(menuId);
        return ResultModel.success(null);
    }

    @PostMapping("/menus/{menuId}/move")
    public ResultModel<Void> moveMenu(
            @PathVariable String menuId,
            @RequestParam(required = false) String newParentId,
            @RequestParam(defaultValue = "1") int newSort) {
        
        log.info("[moveMenu] Moving menu: {} to parentId: {}, sort: {}", menuId, newParentId, newSort);
        
        if (menuService == null) {
            return ResultModel.error(ResultModel.CODE_INTERNAL_SERVER_ERROR, "MenuService not available");
        }
        
        menuService.move(menuId, newParentId, newSort);
        return ResultModel.success(null);
    }

    @GetMapping("/roles/{roleId}/menus")
    public ResultModel<List<MenuItemDTO>> getRoleMenus(@PathVariable String roleId) {
        log.info("[getRoleMenus] Loading menus for role: {}", roleId);
        
        if (menuRoleConfigService == null) {
            return ResultModel.success(new ArrayList<>());
        }
        
        List<MenuItemDTO> menus = menuRoleConfigService.getMenusByRole(roleId);
        return ResultModel.success(menus);
    }

    @PutMapping("/roles/{roleId}/menus")
    public ResultModel<Void> setRoleMenus(@PathVariable String roleId, @RequestBody List<MenuItemDTO> menus) {
        log.info("[setRoleMenus] Setting menus for role: {}, count: {}", roleId, menus != null ? menus.size() : 0);
        
        if (menuRoleConfigService == null) {
            return ResultModel.error(ResultModel.CODE_INTERNAL_SERVER_ERROR, "MenuRoleConfigService not available");
        }
        
        menuRoleConfigService.updateRoleMenus(roleId, menus);
        return ResultModel.success(null);
    }

    @GetMapping("/users/{userId}/menus")
    public ResultModel<List<MenuItemDTO>> getUserMenus(@PathVariable String userId) {
        log.info("[getUserMenus] Loading menus for user: {}", userId);
        
        if (menuRoleConfigService == null) {
            return ResultModel.success(new ArrayList<>());
        }
        
        List<MenuItemDTO> menus = menuRoleConfigService.getUserMenusAsDTO(userId);
        return ResultModel.success(menus);
    }
}

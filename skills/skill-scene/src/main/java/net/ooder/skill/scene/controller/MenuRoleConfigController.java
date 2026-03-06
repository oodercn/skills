package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.dto.MenuItemDTO;
import net.ooder.skill.scene.model.MenuRoleConfig;
import net.ooder.skill.scene.model.ResultModel;
import net.ooder.skill.scene.service.MenuRoleConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单角色配置API控制器
 */
@RestController
@RequestMapping("/api/v1/menu-config")
@CrossOrigin(origins = "*")
public class MenuRoleConfigController {

    @Autowired
    private MenuRoleConfigService menuRoleConfigService;

    /**
     * 获取所有菜单角色配置
     */
    @GetMapping
    public ResultModel<List<MenuRoleConfig>> getAllConfigs() {
        return ResultModel.success(menuRoleConfigService.getAllConfigs());
    }

    /**
     * 获取指定角色的菜单配置
     */
    @GetMapping("/{roleId}")
    public ResultModel<MenuRoleConfig> getConfigByRole(@PathVariable String roleId) {
        MenuRoleConfig config = menuRoleConfigService.getConfigByRole(roleId);
        if (config == null) {
            return ResultModel.notFound("角色配置不存在");
        }
        return ResultModel.success(config);
    }

    /**
     * 获取指定角色的菜单列表
     */
    @GetMapping("/{roleId}/menus")
    public ResultModel<List<MenuItemDTO>> getMenusByRole(@PathVariable String roleId) {
        return ResultModel.success(menuRoleConfigService.getMenuItemsByRole(roleId));
    }

    /**
     * 更新角色的菜单配置
     */
    @PutMapping("/{roleId}")
    public ResultModel<MenuRoleConfig> updateConfig(
            @PathVariable String roleId,
            @RequestBody MenuRoleConfig config) {
        return ResultModel.success(menuRoleConfigService.updateConfig(roleId, config));
    }

    /**
     * 添加菜单项到角色
     */
    @PostMapping("/{roleId}/menus")
    public ResultModel<MenuRoleConfig.MenuItemConfig> addMenuItem(
            @PathVariable String roleId,
            @RequestBody MenuRoleConfig.MenuItemConfig menuItem) {
        menuRoleConfigService.addMenuItem(roleId, menuItem);
        return ResultModel.success(menuItem);
    }

    /**
     * 移除菜单项
     */
    @DeleteMapping("/{roleId}/menus/{menuId}")
    public ResultModel<Boolean> removeMenuItem(
            @PathVariable String roleId,
            @PathVariable String menuId) {
        return ResultModel.success(menuRoleConfigService.removeMenuItem(roleId, menuId));
    }

    /**
     * 导出配置
     */
    @GetMapping("/export")
    public ResultModel<String> exportConfig() {
        return ResultModel.success(menuRoleConfigService.exportConfig());
    }

    /**
     * 导入配置
     */
    @PostMapping("/import")
    public ResultModel<String> importConfig(@RequestBody String jsonContent) {
        try {
            menuRoleConfigService.importConfig(jsonContent);
            return ResultModel.success("导入成功");
        } catch (Exception e) {
            return ResultModel.error("导入失败: " + e.getMessage());
        }
    }
}

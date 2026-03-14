package net.ooder.skill.scene.service;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.scene.dto.menu.MenuItemDTO;
import net.ooder.skill.scene.dto.menu.MenuConfigDTO;
import net.ooder.skill.scene.dto.scene.SceneTemplateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MenuAutoRegisterService {

    @Autowired
    private MenuRoleConfigService menuRoleConfigService;
    
    @Autowired
    private SceneTemplateService sceneTemplateService;

    /**
     * 激活完成时自动注册菜单
     * @param sceneGroupId 场景组ID
     * @param templateId 模板ID
     * @param userId 用户ID
     * @param roleInScene 用户在场景中的角色
     */
    public void registerMenusOnActivation(String sceneGroupId, String templateId, String userId, String roleInScene) {
        log.info("Auto registering menus for user: {}, sceneGroup: {}, template: {}, role: {}", 
            userId, sceneGroupId, templateId, roleInScene);
        
        try {
            SceneTemplateDTO template = sceneTemplateService.get(templateId);
            if (template == null) {
                log.warn("Template not found: {}", templateId);
                return;
            }
            
            List<MenuConfigDTO> menuConfigs = template.getMenus(roleInScene);
            if (menuConfigs == null || menuConfigs.isEmpty()) {
                log.info("No menu config found for role: {} in template: {}", roleInScene, templateId);
                return;
            }
            
            String sceneName = template.getName();
            menuRoleConfigService.registerSceneMenusFromTemplateDTO(sceneGroupId, sceneName, userId, roleInScene, menuConfigs);
            
            log.info("Menus registered successfully for user: {}, scene: {}, role: {}", userId, sceneName, roleInScene);
        } catch (Exception e) {
            log.error("Failed to register menus for user: {}, sceneGroup: {}", userId, sceneGroupId, e);
        }
    }

    /**
     * 场景销毁时自动清理菜单
     * @param sceneGroupId 场景组ID
     * @param userId 用户ID
     */
    public void removeMenusOnSceneDestroy(String sceneGroupId, String userId) {
        log.info("Removing menus for user: {}, sceneGroup: {}", userId, sceneGroupId);
        menuRoleConfigService.removeSceneMenus(userId, sceneGroupId);
    }

    /**
     * 获取用户的场景菜单
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<MenuItemDTO> getUserSceneMenus(String userId) {
        return menuRoleConfigService.getUserSceneMenus(userId);
    }

    /**
     * 获取用户的完整菜单（包含角色菜单和场景菜单）
     * @param userId 用户ID
     * @param roleId 用户角色ID
     * @return 菜单列表
     */
    public List<MenuItemDTO> getFinalMenusForUser(String userId, String roleId) {
        return menuRoleConfigService.getFinalMenusForUserWithScene(userId, roleId);
    }
}

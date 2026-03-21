package net.ooder.mvp.skill.scene.service;

import net.ooder.mvp.skill.scene.dto.menu.MenuItemDTO;
import net.ooder.mvp.skill.scene.dto.menu.MenuConfigDTO;
import net.ooder.mvp.skill.scene.dto.scene.SceneTemplateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuAutoRegisterService {

    private static final Logger log = LoggerFactory.getLogger(MenuAutoRegisterService.class);

    @Autowired
    private MenuRoleConfigService menuRoleConfigService;
    
    @Autowired
    private SceneTemplateService sceneTemplateService;

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

    public void removeMenusOnSceneDestroy(String sceneGroupId, String userId) {
        log.info("Removing menus for user: {}, sceneGroup: {}", userId, sceneGroupId);
        menuRoleConfigService.removeSceneMenus(userId, sceneGroupId);
    }

    public List<MenuItemDTO> getUserSceneMenus(String userId) {
        return menuRoleConfigService.getUserSceneMenus(userId);
    }

    public List<MenuItemDTO> getFinalMenusForUser(String userId, String roleId) {
        return menuRoleConfigService.getFinalMenusForUserWithScene(userId, roleId);
    }
}

package net.ooder.mvp.skill.scene.service;

import net.ooder.mvp.skill.scene.dto.menu.MenuItemDTO;
import net.ooder.mvp.skill.scene.dto.menu.MenuConfigDTO;
import net.ooder.mvp.skill.scene.dto.scene.SceneTemplateDTO;
import net.ooder.mvp.skill.scene.template.SceneTemplate;
import net.ooder.mvp.skill.scene.template.MenuConfig;
import net.ooder.mvp.skill.scene.template.SceneTemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuAutoRegisterService {

    private static final Logger log = LoggerFactory.getLogger(MenuAutoRegisterService.class);

    @Autowired
    private MenuRoleConfigService menuRoleConfigService;
    
    @Autowired
    private SceneTemplateService sceneTemplateService;
    
    @Autowired(required = false)
    private SceneTemplateLoader sceneTemplateLoader;

    public void registerMenusOnActivation(String sceneGroupId, String templateId, String userId, String roleInScene) {
        log.info("Auto registering menus for user: {}, sceneGroup: {}, template: {}, role: {}", 
            userId, sceneGroupId, templateId, roleInScene);
        
        try {
            List<MenuConfigDTO> menuConfigs = null;
            String sceneName = null;
            
            if (sceneTemplateLoader != null) {
                SceneTemplate template = sceneTemplateLoader.getTemplate(templateId);
                if (template != null) {
                    sceneName = template.getName();
                    List<MenuConfig> templateMenus = template.getMenus(roleInScene);
                    if (templateMenus != null && !templateMenus.isEmpty()) {
                        menuConfigs = convertToMenuConfigDTO(templateMenus);
                        log.info("Found {} menus from SceneTemplateLoader for role: {}", menuConfigs.size(), roleInScene);
                    }
                }
            }
            
            if (menuConfigs == null || menuConfigs.isEmpty()) {
                SceneTemplateDTO templateDTO = sceneTemplateService.get(templateId);
                if (templateDTO != null) {
                    sceneName = templateDTO.getName();
                    menuConfigs = templateDTO.getMenus(roleInScene);
                    log.info("Found {} menus from SceneTemplateService for role: {}", 
                        menuConfigs != null ? menuConfigs.size() : 0, roleInScene);
                }
            }
            
            if (menuConfigs == null || menuConfigs.isEmpty()) {
                log.warn("No menu config found for template: {}, role: {}", templateId, roleInScene);
                return;
            }
            
            if (sceneName == null) {
                sceneName = templateId;
            }
            
            menuRoleConfigService.registerSceneMenusFromTemplateDTO(sceneGroupId, sceneName, userId, roleInScene, menuConfigs);
            
            log.info("Menus registered successfully for user: {}, scene: {}, role: {}", userId, sceneName, roleInScene);
        } catch (Exception e) {
            log.error("Failed to register menus for user: {}, sceneGroup: {}", userId, sceneGroupId, e);
        }
    }
    
    private List<MenuConfigDTO> convertToMenuConfigDTO(List<MenuConfig> templateMenus) {
        List<MenuConfigDTO> result = new ArrayList<>();
        for (MenuConfig config : templateMenus) {
            result.add(convertToDTO(config));
        }
        return result;
    }
    
    private MenuConfigDTO convertToDTO(MenuConfig config) {
        MenuConfigDTO dto = new MenuConfigDTO();
        dto.setId(config.getId());
        dto.setName(config.getName());
        dto.setIcon(config.getIcon());
        dto.setUrl(config.getUrl());
        dto.setOrder(config.getOrder());
        dto.setVisible(config.isVisible());
        
        if (config.getChildren() != null && !config.getChildren().isEmpty()) {
            List<MenuConfigDTO> children = new ArrayList<>();
            for (MenuConfig child : config.getChildren()) {
                children.add(convertToDTO(child));
            }
            dto.setChildren(children);
        }
        
        return dto;
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

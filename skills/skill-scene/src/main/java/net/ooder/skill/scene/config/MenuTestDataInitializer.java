package net.ooder.skill.scene.config;

import net.ooder.skill.scene.model.MenuRoleConfig;
import net.ooder.skill.scene.service.MenuRoleConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 菜单测试数据初始化器
 * 在应用启动时自动注册菜单配置
 */
@Component
public class MenuTestDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(MenuTestDataInitializer.class);

    @Autowired
    private MenuRoleConfigService menuRoleConfigService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("开始初始化菜单测试数据...");
        
        initInstallerMenus();
        initAdminMenus();
        initLeaderMenus();
        initCollaboratorMenus();
        
        log.info("菜单测试数据初始化完成");
    }

    /**
     * 初始化系统安装者菜单
     */
    private void initInstallerMenus() {
        String roleId = "installer";
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-installer-1", "工作台", "/console/pages/role-installer.html", "ri-home-line", 1, true
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-installer-2", "技能市场", "/console/pages/capability-discovery.html", "ri-store-2-line", 2, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-installer-3", "已安装技能", "/console/pages/installed-scene-capabilities.html", "ri-download-cloud-line", 3, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-installer-4", "安装日志", "/console/pages/audit-logs.html", "ri-file-list-3-line", 4, false
        ));
        
        log.info("初始化系统安装者菜单完成");
    }

    /**
     * 初始化系统管理员菜单
     */
    private void initAdminMenus() {
        String roleId = "admin";
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-admin-1", "工作台", "/console/pages/role-admin.html", "ri-home-line", 1, true
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-admin-2", "场景能力", "/console/pages/scene-capabilities.html", "ri-puzzle-line", 2, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-admin-3", "发现场景", "/console/pages/capability-discovery.html", "ri-compass-discover-line", 3, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-admin-4", "场景组管理", "/console/pages/scene-group-management.html", "ri-folder-line", 4, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-admin-5", "能力统计", "/console/pages/capability-stats.html", "ri-bar-chart-box-line", 5, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-admin-6", "组织管理", "/console/pages/org-management.html", "ri-organization-chart", 6, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-admin-7", "架构检查", "/console/pages/arch-check.html", "ri-shield-check-line", 7, false
        ));
        
        log.info("初始化系统管理员菜单完成");
    }

    /**
     * 初始化主导者菜单
     */
    private void initLeaderMenus() {
        String roleId = "leader";
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-leader-1", "工作台", "/console/pages/role-leader.html", "ri-home-line", 1, true
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-leader-2", "待激活场景", "/console/pages/my-todos.html", "ri-task-line", 2, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-leader-3", "我的场景", "/console/pages/my-scenes.html", "ri-artboard-line", 3, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-leader-4", "密钥管理", "/console/pages/key-management.html", "ri-key-2-line", 4, false
        ));
        
        log.info("初始化主导者菜单完成");
    }

    /**
     * 初始化协作者菜单
     */
    private void initCollaboratorMenus() {
        String roleId = "collaborator";
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-collaborator-1", "工作台", "/console/pages/role-collaborator.html", "ri-home-line", 1, true
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-collaborator-2", "我的待办", "/console/pages/my-todos.html", "ri-task-line", 2, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-collaborator-3", "参与场景", "/console/pages/my-scenes.html", "ri-artboard-line", 3, false
        ));
        
        menuRoleConfigService.addMenuItem(roleId, createMenuItem(
            "menu-collaborator-4", "历史记录", "/console/pages/my-history.html", "ri-history-line", 4, false
        ));
        
        log.info("初始化协作者菜单完成");
    }

    /**
     * 创建菜单项配置
     */
    private MenuRoleConfig.MenuItemConfig createMenuItem(
            String id, String name, String url, String icon, int sort, boolean active) {
        MenuRoleConfig.MenuItemConfig item = new MenuRoleConfig.MenuItemConfig();
        item.setId(id);
        item.setName(name);
        item.setUrl(url);
        item.setIcon(icon);
        item.setSort(sort);
        item.setActive(active);
        return item;
    }
}

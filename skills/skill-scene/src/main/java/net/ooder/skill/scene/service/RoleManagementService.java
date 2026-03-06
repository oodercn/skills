package net.ooder.skill.scene.service;

import net.ooder.skill.org.base.OrgSkill;
import net.ooder.skill.org.base.UserInfo;
import net.ooder.skill.org.base.OrgInfo;
import net.ooder.skill.scene.dto.MenuItemDTO;
import net.ooder.skill.scene.dto.RoleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class RoleManagementService {

    private static final Logger log = LoggerFactory.getLogger(RoleManagementService.class);

    @Autowired
    private OrgSkill orgSkill;

    private final Map<String, RoleDTO> roles = new LinkedHashMap<>();
    private final Map<String, List<MenuItemDTO>> roleMenus = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        initDefaultRoles();
        initDefaultMenus();
        initDefaultUsers();
        log.info("RoleManagementService initialized with {} roles", roles.size());
    }

    private void initDefaultRoles() {
        createRole("installer", "系统安装者", "安装基础技能包，初始化系统环境", "ri-install-line",
            "installer", Arrays.asList("skill:install", "skill:view", "system:init"));

        createRole("admin", "系统管理员", "发现场景技能，配置分发，推送给参与者", "ri-admin-line",
            "admin", Arrays.asList("capability:discover", "capability:install", "capability:distribute",
                "scene:create", "scene:manage", "user:assign", "capability:view", "scene:view"));

        createRole("leader", "主导者", "激活场景，获取KEY，执行入网动作", "ri-user-star-line",
            "manager", Arrays.asList("scene:activate", "scene:manage", "scene:view",
                "key:generate", "participant:manage", "task:assign"));

        createRole("collaborator", "协作者", "参与业务流转，执行分配的任务", "ri-team-line",
            "employee", Arrays.asList("task:view", "task:execute", "task:submit", "scene:view", "todo:view"));
    }

    private void initDefaultMenus() {
        List<MenuItemDTO> installerMenus = Arrays.asList(
            createMenuItem("menu-installer-1", "工作台", "/console/pages/role-installer.html", "ri-home-line", 1, true),
            createMenuItem("menu-installer-2", "技能市场", "/console/pages/capability-discovery.html", "ri-store-2-line", 2, false),
            createMenuItem("menu-installer-3", "已安装技能", "/console/pages/my-capabilities.html", "ri-download-cloud-line", 3, false),
            createMenuItem("menu-installer-4", "安装日志", "/console/pages/audit-logs.html", "ri-file-list-3-line", 4, false)
        );
        roleMenus.put("installer", installerMenus);

        List<MenuItemDTO> adminMenus = Arrays.asList(
            createMenuItem("menu-admin-1", "工作台", "/console/pages/role-admin.html", "ri-home-line", 1, true),
            createMenuItem("menu-admin-2", "场景能力", "/console/pages/scene-capabilities.html", "ri-layout-grid-line", 2, false),
            createMenuItem("menu-admin-3", "发现场景", "/console/pages/capability-discovery.html", "ri-compass-discover-line", 3, false),
            createMenuItem("menu-admin-4", "场景组管理", "/console/pages/scene-group-management.html", "ri-folder-line", 4, false),
            createMenuItem("menu-admin-5", "能力统计", "/console/pages/capability-stats.html", "ri-bar-chart-box-line", 5, false),
            createMenuItem("menu-admin-6", "组织管理", "/console/pages/org-management.html", "ri-organization-chart", 6, false),
            createMenuItem("menu-admin-7", "架构检查", "/console/pages/arch-check.html", "ri-shield-check-line", 7, false)
        );
        roleMenus.put("admin", adminMenus);

        List<MenuItemDTO> leaderMenus = Arrays.asList(
            createMenuItem("menu-leader-1", "工作台", "/console/pages/role-leader.html", "ri-home-line", 1, true),
            createMenuItem("menu-leader-2", "待激活场景", "/console/pages/my-todos.html", "ri-task-line", 2, false),
            createMenuItem("menu-leader-3", "我的场景", "/console/pages/my-scenes.html", "ri-artboard-line", 3, false),
            createMenuItem("menu-leader-4", "密钥管理", "/console/pages/key-management.html", "ri-key-2-line", 4, false)
        );
        roleMenus.put("leader", leaderMenus);

        List<MenuItemDTO> collaboratorMenus = Arrays.asList(
            createMenuItem("menu-collaborator-1", "工作台", "/console/pages/role-collaborator.html", "ri-home-line", 1, true),
            createMenuItem("menu-collaborator-2", "我的待办", "/console/pages/my-todos.html", "ri-task-line", 2, false),
            createMenuItem("menu-collaborator-3", "参与场景", "/console/pages/my-scenes.html", "ri-artboard-line", 3, false),
            createMenuItem("menu-collaborator-4", "历史记录", "/console/pages/my-history.html", "ri-history-line", 4, false)
        );
        roleMenus.put("collaborator", collaboratorMenus);
    }

    private void initDefaultUsers() {
        initUserIfNotExists("installer", "系统安装者", "installer@ooder.local", "root", Arrays.asList("installer"));
        initUserIfNotExists("admin", "系统管理员", "admin@ooder.local", "root", Arrays.asList("admin"));
        initUserIfNotExists("leader", "张主导", "leader@ooder.local", "root", Arrays.asList("leader"));
        initUserIfNotExists("user", "李协作者", "user@ooder.local", "root", Arrays.asList("collaborator"));
    }

    private void initUserIfNotExists(String username, String nickname, String email, String orgId, List<String> roles) {
        UserInfo existing = orgSkill.getUserByAccount(username);
        if (existing == null) {
            UserInfo user = new UserInfo();
            user.setUsername(username);
            user.setNickname(nickname);
            user.setEmail(email);
            user.setOrgId(orgId);
            user.setRoles(roles);
            user.setStatus("active");
            orgSkill.registerUser(user);
            log.info("Created default user: {}", username);
        }
    }

    private RoleDTO createRole(String id, String name, String description, String icon,
                               String orgRole, List<String> permissions) {
        RoleDTO role = new RoleDTO();
        role.setId(id);
        role.setName(name);
        role.setDescription(description);
        role.setIcon(icon);
        role.setOrgRole(orgRole);
        role.setPermissions(permissions);
        roles.put(id, role);
        return role;
    }

    private MenuItemDTO createMenuItem(String id, String name, String url, String icon, int sort, boolean active) {
        MenuItemDTO item = new MenuItemDTO();
        item.setId(id);
        item.setName(name);
        item.setUrl(url);
        item.setIcon(icon);
        item.setSort(sort);
        item.setActive(active);
        return item;
    }

    public RoleDTO createRole(RoleDTO role) {
        if (role.getId() == null || role.getId().isEmpty()) {
            role.setId("role-" + UUID.randomUUID().toString().substring(0, 8));
        }
        roles.put(role.getId(), role);
        log.info("Created role: {}", role.getId());
        return role;
    }

    public RoleDTO getRole(String roleId) {
        return roles.get(roleId);
    }

    public List<RoleDTO> getAllRoles() {
        return new ArrayList<>(roles.values());
    }

    public RoleDTO updateRole(String roleId, RoleDTO role) {
        if (!roles.containsKey(roleId)) {
            return null;
        }
        role.setId(roleId);
        roles.put(roleId, role);
        log.info("Updated role: {}", roleId);
        return role;
    }

    public boolean deleteRole(String roleId) {
        if (roles.remove(roleId) != null) {
            roleMenus.remove(roleId);
            log.info("Deleted role: {}", roleId);
            return true;
        }
        return false;
    }

    public List<MenuItemDTO> getMenusByRole(String roleId) {
        return roleMenus.getOrDefault(roleId, new ArrayList<>());
    }

    public void setMenusForRole(String roleId, List<MenuItemDTO> menus) {
        roleMenus.put(roleId, menus);
        log.info("Set {} menus for role: {}", menus.size(), roleId);
    }

    public MenuItemDTO addMenuToRole(String roleId, MenuItemDTO menu) {
        List<MenuItemDTO> menus = roleMenus.computeIfAbsent(roleId, k -> new ArrayList<>());
        if (menu.getId() == null || menu.getId().isEmpty()) {
            menu.setId("menu-" + roleId + "-" + (menus.size() + 1));
        }
        menu.setParentRoleId(roleId);
        menus.add(menu);
        log.info("Added menu {} to role {}", menu.getId(), roleId);
        return menu;
    }

    public boolean removeMenuFromRole(String roleId, String menuId) {
        List<MenuItemDTO> menus = roleMenus.get(roleId);
        if (menus != null) {
            return menus.removeIf(m -> menuId.equals(m.getId()));
        }
        return false;
    }

    public UserInfo createUser(String name, String email, String orgRole, String departmentId) {
        UserInfo user = new UserInfo();
        user.setUsername(name.toLowerCase().replace(" ", ""));
        user.setNickname(name);
        user.setEmail(email);
        user.setOrgId(departmentId);
        user.setRoles(Arrays.asList(orgRole));
        user.setStatus("active");

        RoleDTO matchedRole = findRoleByOrgRole(orgRole);
        if (matchedRole != null) {
            user.setRoles(Arrays.asList(matchedRole.getOrgRole()));
        }

        UserInfo created = orgSkill.registerUser(user);
        log.info("Created user: {} with orgRole: {}", created.getUserId(), orgRole);
        return created;
    }

    public UserInfo bindUserToRole(String userId, String roleId) {
        UserInfo user = orgSkill.getUser(userId);
        if (user == null) {
            log.warn("User not found: {}", userId);
            return null;
        }

        RoleDTO role = roles.get(roleId);
        if (role == null) {
            log.warn("Role not found: {}", roleId);
            return null;
        }

        List<String> currentRoles = user.getRoles() != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>();
        if (!currentRoles.contains(role.getOrgRole())) {
            currentRoles.add(role.getOrgRole());
        }
        user.setRoles(currentRoles);
        
        UserInfo updated = orgSkill.updateUser(userId, user);
        log.info("Bound user {} to role {}", userId, roleId);
        return updated;
    }

    public UserInfo setUserPassword(String userId, String password) {
        UserInfo user = orgSkill.getUser(userId);
        if (user == null) {
            return null;
        }
        log.info("Password update requested for user: {} (note: password storage depends on OrgSkill implementation)", userId);
        return user;
    }

    private RoleDTO findRoleByOrgRole(String orgRole) {
        for (RoleDTO role : roles.values()) {
            if (orgRole.equals(role.getOrgRole())) {
                return role;
            }
        }
        return null;
    }

    public List<UserInfo> getUsersByRole(String roleId) {
        RoleDTO role = roles.get(roleId);
        if (role != null) {
            List<OrgInfo> orgTree = orgSkill.getOrgTree();
            List<UserInfo> allUsers = new ArrayList<>();
            for (OrgInfo org : orgTree) {
                allUsers.addAll(orgSkill.getOrgUsers(org.getOrgId()));
            }
            List<UserInfo> filtered = new ArrayList<>();
            for (UserInfo user : allUsers) {
                if (user.getRoles() != null && user.getRoles().contains(role.getOrgRole())) {
                    filtered.add(user);
                }
            }
            return filtered;
        }
        return new ArrayList<>();
    }

    public Map<String, Object> getRoleWithUsers(String roleId) {
        Map<String, Object> result = new HashMap<>();
        RoleDTO role = roles.get(roleId);
        if (role != null) {
            result.put("role", role);
            result.put("menus", getMenusByRole(roleId));
            result.put("users", getUsersByRole(roleId));
        }
        return result;
    }

    public Map<String, Object> getFullConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("roles", getAllRoles());

        Map<String, Object> roleConfigs = new HashMap<>();
        for (String roleId : roles.keySet()) {
            roleConfigs.put(roleId, getRoleWithUsers(roleId));
        }
        config.put("roleConfigs", roleConfigs);

        return config;
    }
}

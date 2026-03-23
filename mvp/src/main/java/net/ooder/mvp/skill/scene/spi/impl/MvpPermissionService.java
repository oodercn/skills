package net.ooder.mvp.skill.scene.spi.impl;

import net.ooder.mvp.skill.scene.spi.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MvpPermissionService implements PermissionService {
    
    private static final Logger log = LoggerFactory.getLogger(MvpPermissionService.class);
    
    private final Map<String, Set<String>> userPermissions = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Set<String>>> scenePermissions = new ConcurrentHashMap<>();
    
    private static final Set<String> DEFAULT_PERMISSIONS = new HashSet<>(Arrays.asList(
        "scene:view",
        "scene:join",
        "capability:invoke"
    ));
    
    private static final Set<String> ADMIN_PERMISSIONS = new HashSet<>(Arrays.asList(
        "scene:create",
        "scene:manage",
        "scene:delete",
        "scene:activate",
        "scene:deactivate",
        "capability:install",
        "capability:uninstall",
        "capability:manage"
    ));
    
    public MvpPermissionService() {
        initDefaultPermissions();
    }
    
    private void initDefaultPermissions() {
        userPermissions.put("default-user", new HashSet<>(DEFAULT_PERMISSIONS));
        userPermissions.put("admin", new HashSet<>(ADMIN_PERMISSIONS));
    }
    
    @Override
    public boolean hasPermission(String userId, String permission) {
        if (userId == null || permission == null) {
            return false;
        }
        
        Set<String> permissions = userPermissions.get(userId);
        if (permissions == null) {
            permissions = new HashSet<>(DEFAULT_PERMISSIONS);
            userPermissions.put(userId, permissions);
        }
        
        return permissions.contains(permission) || permissions.contains("*");
    }
    
    @Override
    public boolean hasScenePermission(String userId, String sceneId, String permission) {
        if (userId == null || sceneId == null || permission == null) {
            return false;
        }
        
        Map<String, Set<String>> userScenePerms = scenePermissions.get(userId);
        if (userScenePerms == null) {
            return hasPermission(userId, permission);
        }
        
        Set<String> scenePerms = userScenePerms.get(sceneId);
        if (scenePerms == null) {
            return hasPermission(userId, permission);
        }
        
        return scenePerms.contains(permission) || scenePerms.contains("*");
    }
    
    @Override
    public List<String> getUserPermissions(String userId, String sceneId) {
        Set<String> allPermissions = new HashSet<>();
        
        Set<String> basePerms = userPermissions.get(userId);
        if (basePerms != null) {
            allPermissions.addAll(basePerms);
        } else {
            allPermissions.addAll(DEFAULT_PERMISSIONS);
        }
        
        if (sceneId != null) {
            Map<String, Set<String>> userScenePerms = scenePermissions.get(userId);
            if (userScenePerms != null) {
                Set<String> scenePerms = userScenePerms.get(sceneId);
                if (scenePerms != null) {
                    allPermissions.addAll(scenePerms);
                }
            }
        }
        
        return new ArrayList<>(allPermissions);
    }
    
    @Override
    public void grantPermission(String userId, String sceneId, String permission) {
        if (userId == null || permission == null) {
            return;
        }
        
        if (sceneId == null) {
            Set<String> perms = userPermissions.computeIfAbsent(userId, k -> new HashSet<>());
            perms.add(permission);
            log.info("Granted permission {} to user {}", permission, userId);
        } else {
            Map<String, Set<String>> userScenePerms = scenePermissions.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
            Set<String> scenePerms = userScenePerms.computeIfAbsent(sceneId, k -> new HashSet<>());
            scenePerms.add(permission);
            log.info("Granted permission {} to user {} for scene {}", permission, userId, sceneId);
        }
    }
    
    @Override
    public void revokePermission(String userId, String sceneId, String permission) {
        if (userId == null || permission == null) {
            return;
        }
        
        if (sceneId == null) {
            Set<String> perms = userPermissions.get(userId);
            if (perms != null) {
                perms.remove(permission);
                log.info("Revoked permission {} from user {}", permission, userId);
            }
        } else {
            Map<String, Set<String>> userScenePerms = scenePermissions.get(userId);
            if (userScenePerms != null) {
                Set<String> scenePerms = userScenePerms.get(sceneId);
                if (scenePerms != null) {
                    scenePerms.remove(permission);
                    log.info("Revoked permission {} from user {} for scene {}", permission, userId, sceneId);
                }
            }
        }
    }
    
    public void addDefaultPermissions(String userId) {
        userPermissions.putIfAbsent(userId, new HashSet<>(DEFAULT_PERMISSIONS));
    }
}

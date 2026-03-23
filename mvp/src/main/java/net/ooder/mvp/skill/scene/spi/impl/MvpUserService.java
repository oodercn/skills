package net.ooder.mvp.skill.scene.spi.impl;

import net.ooder.mvp.skill.scene.spi.UserService;
import net.ooder.mvp.skill.scene.spi.user.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MvpUserService implements UserService {
    
    private static final Logger log = LoggerFactory.getLogger(MvpUserService.class);
    
    private final Map<String, UserInfo> userCache = new ConcurrentHashMap<>();
    
    public MvpUserService() {
        initDefaultUsers();
    }
    
    private void initDefaultUsers() {
        UserInfo defaultUser = new UserInfo();
        defaultUser.setUserId("default-user");
        defaultUser.setUsername("default");
        defaultUser.setDisplayName("默认用户");
        defaultUser.setEmail("default@example.com");
        defaultUser.setDepartmentId("dept-default");
        defaultUser.setDepartmentName("默认部门");
        userCache.put("default-user", defaultUser);
    }
    
    @Override
    public UserInfo getUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        
        UserInfo user = userCache.get(userId);
        if (user == null) {
            log.warn("User not found: {}", userId);
            user = createUnknownUser(userId);
        }
        return user;
    }
    
    @Override
    public Map<String, UserInfo> getUsers(List<String> userIds) {
        Map<String, UserInfo> result = new HashMap<>();
        if (userIds == null || userIds.isEmpty()) {
            return result;
        }
        
        for (String userId : userIds) {
            UserInfo user = getUser(userId);
            if (user != null) {
                result.put(userId, user);
            }
        }
        return result;
    }
    
    @Override
    public List<UserInfo> searchUsers(String keyword, int limit) {
        if (keyword == null || keyword.isEmpty()) {
            return new ArrayList<>(userCache.values());
        }
        
        String lowerKeyword = keyword.toLowerCase();
        return userCache.values().stream()
            .filter(u -> matchesKeyword(u, lowerKeyword))
            .limit(limit > 0 ? limit : 10)
            .collect(Collectors.toList());
    }
    
    private boolean matchesKeyword(UserInfo user, String keyword) {
        return (user.getUsername() != null && user.getUsername().toLowerCase().contains(keyword))
            || (user.getDisplayName() != null && user.getDisplayName().toLowerCase().contains(keyword))
            || (user.getEmail() != null && user.getEmail().toLowerCase().contains(keyword));
    }
    
    @Override
    public String getUserDepartment(String userId) {
        UserInfo user = getUser(userId);
        return user != null ? user.getDepartmentId() : "dept-default";
    }
    
    @Override
    public String getUserRole(String userId, String sceneId) {
        return "MEMBER";
    }
    
    private UserInfo createUnknownUser(String userId) {
        UserInfo user = new UserInfo();
        user.setUserId(userId);
        user.setUsername(userId);
        user.setDisplayName("未知用户 (" + userId + ")");
        user.setEmail(userId + "@unknown.local");
        user.setDepartmentId("dept-unknown");
        user.setDepartmentName("未知部门");
        return user;
    }
    
    public void registerUser(UserInfo user) {
        if (user != null && user.getUserId() != null) {
            userCache.put(user.getUserId(), user);
            log.info("Registered user: {}", user.getUserId());
        }
    }
}

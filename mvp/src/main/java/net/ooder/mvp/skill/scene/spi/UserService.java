package net.ooder.mvp.skill.scene.spi;

import net.ooder.mvp.skill.scene.spi.user.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserService {
    
    UserInfo getUser(String userId);
    
    Map<String, UserInfo> getUsers(List<String> userIds);
    
    List<UserInfo> searchUsers(String keyword, int limit);
    
    String getUserDepartment(String userId);
    
    String getUserRole(String userId, String sceneId);
}

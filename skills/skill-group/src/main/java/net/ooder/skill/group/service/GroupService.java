package net.ooder.skill.group.service;

import net.ooder.skill.group.dto.*;

import java.util.List;
import java.util.Map;

public interface GroupService {
    List<Group> getGroupList(String userId);
    Group createGroup(String name, String ownerId, String ownerName, List<String> memberIds, String groupType);
    Group getGroup(String groupId);
    List<GroupMember> getGroupMembers(String groupId);
    boolean addMember(String groupId, String userId, String userName);
    boolean removeMember(String groupId, String userId);
    boolean updateGroup(String groupId, Map<String, Object> params);
    boolean dismissGroup(String groupId, String userId);
    boolean setAnnouncement(String groupId, String announcement);
    boolean setMemberRole(String groupId, String userId, String role);
}

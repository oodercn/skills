package net.ooder.skill.group.service.impl;

import net.ooder.skill.group.dto.*;
import net.ooder.skill.group.service.GroupService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GroupServiceImpl implements GroupService {

    private final Map<String, Group> groups = new ConcurrentHashMap<>();
    private final Map<String, Map<String, GroupMember>> groupMembers = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userGroups = new ConcurrentHashMap<>();

    @Override
    public List<Group> getGroupList(String userId) {
        List<String> groupIds = userGroups.get(userId);
        if (groupIds == null || groupIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Group> result = new ArrayList<>();
        for (String groupId : groupIds) {
            Group group = groups.get(groupId);
            if (group != null) {
                result.add(group);
            }
        }
        return result;
    }

    @Override
    public Group createGroup(String name, String ownerId, String ownerName, List<String> memberIds, String groupType) {
        Group group = new Group();
        group.setGroupId("group-" + UUID.randomUUID().toString().substring(0, 8));
        group.setName(name);
        group.setOwnerId(ownerId);
        group.setOwnerName(ownerName);
        group.setGroupType(groupType != null ? groupType : "normal");
        group.setMemberCount(1);
        
        groups.put(group.getGroupId(), group);
        
        Map<String, GroupMember> members = new ConcurrentHashMap<>();
        GroupMember owner = new GroupMember();
        owner.setMemberId("member-" + UUID.randomUUID().toString().substring(0, 8));
        owner.setUserId(ownerId);
        owner.setUserName(ownerName);
        owner.setRole("owner");
        members.put(ownerId, owner);
        
        if (memberIds != null) {
            for (String memberId : memberIds) {
                if (!memberId.equals(ownerId)) {
                    GroupMember member = new GroupMember();
                    member.setMemberId("member-" + UUID.randomUUID().toString().substring(0, 8));
                    member.setUserId(memberId);
                    member.setRole("member");
                    members.put(memberId, member);
                    group.setMemberCount(group.getMemberCount() + 1);
                    
                    userGroups.computeIfAbsent(memberId, k -> new ArrayList<>()).add(group.getGroupId());
                }
            }
        }
        
        groupMembers.put(group.getGroupId(), members);
        userGroups.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(group.getGroupId());
        
        return group;
    }

    @Override
    public Group getGroup(String groupId) {
        return groups.get(groupId);
    }

    @Override
    public List<GroupMember> getGroupMembers(String groupId) {
        Map<String, GroupMember> members = groupMembers.get(groupId);
        if (members == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(members.values());
    }

    @Override
    public boolean addMember(String groupId, String userId, String userName) {
        Group group = groups.get(groupId);
        if (group == null) {
            return false;
        }
        Map<String, GroupMember> members = groupMembers.get(groupId);
        if (members == null) {
            return false;
        }
        if (members.containsKey(userId)) {
            return false;
        }
        GroupMember member = new GroupMember();
        member.setMemberId("member-" + UUID.randomUUID().toString().substring(0, 8));
        member.setUserId(userId);
        member.setUserName(userName);
        member.setRole("member");
        members.put(userId, member);
        group.setMemberCount(group.getMemberCount() + 1);
        group.setUpdateTime(System.currentTimeMillis());
        
        userGroups.computeIfAbsent(userId, k -> new ArrayList<>()).add(groupId);
        
        return true;
    }

    @Override
    public boolean removeMember(String groupId, String userId) {
        Group group = groups.get(groupId);
        if (group == null) {
            return false;
        }
        Map<String, GroupMember> members = groupMembers.get(groupId);
        if (members == null || !members.containsKey(userId)) {
            return false;
        }
        GroupMember member = members.get(userId);
        if ("owner".equals(member.getRole())) {
            return false;
        }
        members.remove(userId);
        group.setMemberCount(group.getMemberCount() - 1);
        group.setUpdateTime(System.currentTimeMillis());
        
        List<String> userGroupList = userGroups.get(userId);
        if (userGroupList != null) {
            userGroupList.remove(groupId);
        }
        
        return true;
    }

    @Override
    public boolean updateGroup(String groupId, Map<String, Object> params) {
        Group group = groups.get(groupId);
        if (group == null) {
            return false;
        }
        if (params.get("name") != null) {
            group.setName((String) params.get("name"));
        }
        if (params.get("avatar") != null) {
            group.setAvatar((String) params.get("avatar"));
        }
        if (params.get("description") != null) {
            group.setDescription((String) params.get("description"));
        }
        group.setUpdateTime(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean dismissGroup(String groupId, String userId) {
        Group group = groups.get(groupId);
        if (group == null) {
            return false;
        }
        if (!group.getOwnerId().equals(userId)) {
            return false;
        }
        Map<String, GroupMember> members = groupMembers.get(groupId);
        if (members != null) {
            for (String memberId : members.keySet()) {
                List<String> userGroupList = userGroups.get(memberId);
                if (userGroupList != null) {
                    userGroupList.remove(groupId);
                }
            }
        }
        groups.remove(groupId);
        groupMembers.remove(groupId);
        return true;
    }

    @Override
    public boolean setAnnouncement(String groupId, String announcement) {
        Group group = groups.get(groupId);
        if (group == null) {
            return false;
        }
        group.setAnnouncement(announcement);
        group.setUpdateTime(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean setMemberRole(String groupId, String userId, String role) {
        Group group = groups.get(groupId);
        if (group == null) {
            return false;
        }
        Map<String, GroupMember> members = groupMembers.get(groupId);
        if (members == null || !members.containsKey(userId)) {
            return false;
        }
        if (!"owner".equals(role) && !"admin".equals(role) && !"member".equals(role)) {
            return false;
        }
        GroupMember member = members.get(userId);
        if ("owner".equals(member.getRole())) {
            return false;
        }
        member.setRole(role);
        return true;
    }
}

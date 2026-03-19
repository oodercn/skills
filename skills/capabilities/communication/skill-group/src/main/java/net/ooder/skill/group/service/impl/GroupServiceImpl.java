package net.ooder.skill.group.service.impl;

import net.ooder.skill.group.dto.*;
import net.ooder.skill.group.service.GroupService;
import net.ooder.skill.common.storage.JsonStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GroupServiceImpl implements GroupService {

    @Value("${group.data-path:./data/group}")
    private String dataPath;
    
    private JsonStorage storage;
    
    private final Map<String, Group> groups = new ConcurrentHashMap<>();
    private final Map<String, Map<String, GroupMember>> groupMembers = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userGroups = new ConcurrentHashMap<>();
    
    private static final String GROUPS_KEY = "groups";
    private static final String GROUP_MEMBERS_KEY = "group_members";
    
    @PostConstruct
    public void init() {
        storage = new JsonStorage(dataPath);
        
        List<Group> savedGroups = storage.loadList(GROUPS_KEY, Group.class);
        if (savedGroups != null) {
            for (Group group : savedGroups) {
                groups.put(group.getGroupId(), group);
            }
        }
        
        Map<String, List<GroupMember>> savedMembers = storage.load(GROUP_MEMBERS_KEY, Map.class);
        if (savedMembers != null) {
            for (Map.Entry<String, List<GroupMember>> entry : savedMembers.entrySet()) {
                Map<String, GroupMember> memberMap = new ConcurrentHashMap<>();
                if (entry.getValue() != null) {
                    for (GroupMember member : entry.getValue()) {
                        memberMap.put(member.getUserId(), member);
                    }
                }
                groupMembers.put(entry.getKey(), memberMap);
            }
        }
    }
    
    private void saveGroups() {
        storage.save(GROUPS_KEY, new ArrayList<>(groups.values()));
    }
    
    private void saveGroupMembers() {
        Map<String, List<GroupMember>> membersToSave = new HashMap<>();
        for (Map.Entry<String, Map<String, GroupMember>> entry : groupMembers.entrySet()) {
            membersToSave.put(entry.getKey(), new ArrayList<>(entry.getValue().values()));
        }
        storage.save(GROUP_MEMBERS_KEY, membersToSave);
    }

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
        group.setCreateTime(System.currentTimeMillis());
        group.setUpdateTime(System.currentTimeMillis());
        
        groups.put(group.getGroupId(), group);
        
        Map<String, GroupMember> members = new ConcurrentHashMap<>();
        GroupMember owner = new GroupMember();
        owner.setMemberId("member-" + UUID.randomUUID().toString().substring(0, 8));
        owner.setUserId(ownerId);
        owner.setUserName(ownerName);
        owner.setRole("owner");
        owner.setJoinTime(System.currentTimeMillis());
        members.put(ownerId, owner);
        
        if (memberIds != null) {
            for (String memberId : memberIds) {
                if (!memberId.equals(ownerId)) {
                    GroupMember member = new GroupMember();
                    member.setMemberId("member-" + UUID.randomUUID().toString().substring(0, 8));
                    member.setUserId(memberId);
                    member.setRole("member");
                    member.setJoinTime(System.currentTimeMillis());
                    members.put(memberId, member);
                    group.setMemberCount(group.getMemberCount() + 1);
                    
                    userGroups.computeIfAbsent(memberId, k -> new ArrayList<>()).add(group.getGroupId());
                }
            }
        }
        
        groupMembers.put(group.getGroupId(), members);
        userGroups.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(group.getGroupId());
        
        saveGroups();
        saveGroupMembers();
        
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
        member.setJoinTime(System.currentTimeMillis());
        members.put(userId, member);
        group.setMemberCount(group.getMemberCount() + 1);
        group.setUpdateTime(System.currentTimeMillis());
        
        userGroups.computeIfAbsent(userId, k -> new ArrayList<>()).add(groupId);
        
        saveGroups();
        saveGroupMembers();
        
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
        
        saveGroups();
        saveGroupMembers();
        
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
        saveGroups();
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
        
        saveGroups();
        saveGroupMembers();
        
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
        saveGroups();
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
        saveGroupMembers();
        return true;
    }
}

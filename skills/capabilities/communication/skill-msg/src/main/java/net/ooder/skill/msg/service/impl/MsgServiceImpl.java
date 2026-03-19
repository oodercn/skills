package net.ooder.skill.msg.service.impl;

import net.ooder.skill.msg.dto.*;
import net.ooder.skill.msg.service.MsgService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MsgServiceImpl implements MsgService {

    private final Map<String, MsgMessage> messages = new ConcurrentHashMap<>();
    private final Map<String, MsgGroup> groups = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> groupMembers = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userMessages = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userGroups = new ConcurrentHashMap<>();

    @Override
    public MsgResult sendMessage(MsgSendRequest request) {
        if (request.getToUserId() == null && request.getToGroupId() == null) {
            return MsgResult.fail("Missing recipient");
        }
        
        MsgMessage message = new MsgMessage();
        message.setMessageId("msg-" + UUID.randomUUID().toString().substring(0, 8));
        message.setFromUserId(request.getFromUserId());
        message.setFromUserName(request.getFromUserName());
        message.setToUserId(request.getToUserId());
        message.setToGroupId(request.getToGroupId());
        message.setContent(request.getContent());
        message.setType(request.getType() != null ? request.getType() : "text");
        message.setCreateTime(System.currentTimeMillis());
        message.setStatus(1);
        message.setIsRead(false);
        message.setIsRecalled(false);
        
        messages.put(message.getMessageId(), message);
        
        if (request.getToUserId() != null) {
            userMessages.computeIfAbsent(request.getToUserId(), k -> new ArrayList<>())
                .add(message.getMessageId());
        } else if (request.getToGroupId() != null) {
            Set<String> members = groupMembers.get(request.getToGroupId());
            if (members != null) {
                for (String memberId : members) {
                    userMessages.computeIfAbsent(memberId, k -> new ArrayList<>())
                        .add(message.getMessageId());
                }
            }
        }
        
        return MsgResult.success(message.getMessageId());
    }

    @Override
    public MsgResult broadcastMessage(String fromUserId, String groupId, String content) {
        MsgGroup group = groups.get(groupId);
        if (group == null) {
            return MsgResult.fail("Group not found");
        }
        
        MsgMessage message = new MsgMessage();
        message.setMessageId("msg-" + UUID.randomUUID().toString().substring(0, 8));
        message.setFromUserId(fromUserId);
        message.setToGroupId(groupId);
        message.setContent(content);
        message.setType("broadcast");
        message.setCreateTime(System.currentTimeMillis());
        message.setStatus(1);
        message.setIsRead(false);
        message.setIsRecalled(false);
        
        messages.put(message.getMessageId(), message);
        
        Set<String> members = groupMembers.get(groupId);
        if (members != null) {
            for (String memberId : members) {
                userMessages.computeIfAbsent(memberId, k -> new ArrayList<>())
                    .add(message.getMessageId());
            }
        }
        
        return MsgResult.success(message.getMessageId());
    }

    @Override
    public List<MsgMessage> getMessages(String userId, Integer limit, Long beforeTime) {
        List<String> messageIds = userMessages.get(userId);
        if (messageIds == null || messageIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        int lim = limit != null && limit > 0 ? limit : 100;
        
        List<MsgMessage> result = new ArrayList<>();
        for (int i = messageIds.size() - 1; i >= 0 && result.size() < lim; i--) {
            MsgMessage msg = messages.get(messageIds.get(i));
            if (msg != null) {
                if (beforeTime != null && msg.getCreateTime() >= beforeTime) {
                    continue;
                }
                result.add(msg);
            }
        }
        
        return result;
    }

    @Override
    public boolean markAsRead(String messageId, String userId) {
        MsgMessage message = messages.get(messageId);
        if (message == null) {
            return false;
        }
        message.setIsRead(true);
        return true;
    }

    @Override
    public boolean recallMessage(String messageId, String userId) {
        MsgMessage message = messages.get(messageId);
        if (message == null) {
            return false;
        }
        if (!userId.equals(message.getFromUserId())) {
            return false;
        }
        long recallDeadline = message.getCreateTime() + 120000;
        if (System.currentTimeMillis() > recallDeadline) {
            return false;
        }
        message.setIsRecalled(true);
        message.setContent("Message recalled");
        return true;
    }

    @Override
    public MsgGroup createGroup(String name, String type, String ownerId, String ownerName) {
        MsgGroup group = new MsgGroup();
        group.setGroupId("msggroup-" + UUID.randomUUID().toString().substring(0, 8));
        group.setName(name);
        group.setType(type != null ? type : "normal");
        group.setOwnerId(ownerId);
        group.setOwnerName(ownerName);
        group.setMemberCount(1);
        group.setCreateTime(System.currentTimeMillis());
        group.setUpdateTime(System.currentTimeMillis());
        
        groups.put(group.getGroupId(), group);
        
        Set<String> members = new HashSet<>();
        members.add(ownerId);
        groupMembers.put(group.getGroupId(), members);
        
        userGroups.computeIfAbsent(ownerId, k -> new HashSet<>()).add(group.getGroupId());
        
        return group;
    }

    @Override
    public boolean joinGroup(String groupId, String userId) {
        MsgGroup group = groups.get(groupId);
        if (group == null) {
            return false;
        }
        
        Set<String> members = groupMembers.get(groupId);
        if (members == null) {
            return false;
        }
        
        if (members.contains(userId)) {
            return false;
        }
        
        members.add(userId);
        group.setMemberCount(group.getMemberCount() + 1);
        group.setUpdateTime(System.currentTimeMillis());
        
        userGroups.computeIfAbsent(userId, k -> new HashSet<>()).add(groupId);
        
        return true;
    }

    @Override
    public List<MsgGroup> listGroups(String userId) {
        Set<String> userGroupIds = userGroups.get(userId);
        if (userGroupIds == null || userGroupIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        return userGroupIds.stream()
            .map(groups::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}

package net.ooder.skill.msg.service;

import net.ooder.skill.msg.dto.*;

import java.util.List;

public interface MsgService {
    MsgResult sendMessage(MsgSendRequest request);
    MsgResult broadcastMessage(String fromUserId, String groupId, String content);
    List<MsgMessage> getMessages(String userId, Integer limit, Long beforeTime);
    boolean markAsRead(String messageId, String userId);
    boolean recallMessage(String messageId, String userId);
    MsgGroup createGroup(String name, String type, String ownerId, String ownerName);
    boolean joinGroup(String groupId, String userId);
    List<MsgGroup> listGroups(String userId);
}

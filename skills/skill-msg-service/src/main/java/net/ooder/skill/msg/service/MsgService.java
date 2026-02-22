package net.ooder.skill.msg.service;

import net.ooder.skill.msg.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 消息服务接口
 * 
 * <p>提供消息推送、Topic管理和P2P通信功能。</p>
 */
public interface MsgService {
    
    // 消息推送
    PushResult push(Message message);
    List<PushResult> batchPush(List<Message> messages);
    List<Message> getHistory(String userId, Long startTime, Long endTime, int page, int size);
    Message getMessage(String messageId);
    
    // Topic管理
    List<Topic> listTopics();
    Topic createTopic(Topic topic);
    Topic getTopic(String topicId);
    boolean deleteTopic(String topicId);
    boolean subscribe(String topicId, String userId);
    boolean unsubscribe(String topicId, String userId);
    PushResult publish(String topicId, Message message);
    List<String> getSubscribers(String topicId);
    
    // P2P通信
    PushResult sendP2P(Message message);
    List<Message> getP2PHistory(String fromUserId, String toUserId, int page, int size);
    
    // 统计
    Map<String, Object> getStatistics();
}

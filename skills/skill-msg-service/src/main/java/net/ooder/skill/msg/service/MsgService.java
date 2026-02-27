package net.ooder.skill.msg.service;

import net.ooder.skill.msg.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 娑堟伅鏈嶅姟鎺ュ彛
 * 
 * <p>鎻愪緵娑堟伅鎺ㄩ€併€乀opic绠＄悊鍜孭2P閫氫俊鍔熻兘銆?/p>
 */
public interface MsgService {
    
    // 娑堟伅鎺ㄩ€?
    PushResult push(Message message);
    List<PushResult> batchPush(List<Message> messages);
    List<Message> getHistory(String userId, Long startTime, Long endTime, int page, int size);
    Message getMessage(String messageId);
    
    // Topic绠＄悊
    List<Topic> listTopics();
    Topic createTopic(Topic topic);
    Topic getTopic(String topicId);
    boolean deleteTopic(String topicId);
    boolean subscribe(String topicId, String userId);
    boolean unsubscribe(String topicId, String userId);
    PushResult publish(String topicId, Message message);
    List<String> getSubscribers(String topicId);
    
    // P2P閫氫俊
    PushResult sendP2P(Message message);
    List<Message> getP2PHistory(String fromUserId, String toUserId, int page, int size);
    
    // 缁熻
    Map<String, Object> getStatistics();
}

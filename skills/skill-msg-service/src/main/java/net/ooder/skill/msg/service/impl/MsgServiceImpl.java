package net.ooder.skill.msg.service.impl;

import net.ooder.skill.msg.dto.*;
import net.ooder.skill.msg.service.MsgService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MsgServiceImpl implements MsgService {

    private final Map<String, Message> messages = new ConcurrentHashMap<>();
    private final Map<String, Topic> topics = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> topicSubscribers = new ConcurrentHashMap<>();

    @Override
    public PushResult push(Message message) {
        if (message.getMessageId() == null || message.getMessageId().isEmpty()) {
            message.setMessageId("msg-" + UUID.randomUUID().toString().substring(0, 8));
        }
        message.setCreateTime(System.currentTimeMillis());
        message.setStatus("delivered");
        messages.put(message.getMessageId(), message);
        return PushResult.success(message.getMessageId());
    }

    @Override
    public List<PushResult> batchPush(List<Message> messageList) {
        List<PushResult> results = new ArrayList<>();
        for (Message message : messageList) {
            results.add(push(message));
        }
        return results;
    }

    @Override
    public List<Message> getHistory(String userId, Long startTime, Long endTime, int page, int size) {
        List<Message> filtered = new ArrayList<>();
        for (Message msg : messages.values()) {
            boolean match = true;
            if (userId != null && !userId.equals(msg.getToUserId()) && !userId.equals(msg.getFromUserId())) {
                match = false;
            }
            if (startTime != null && msg.getCreateTime() < startTime) {
                match = false;
            }
            if (endTime != null && msg.getCreateTime() > endTime) {
                match = false;
            }
            if (match) {
                filtered.add(msg);
            }
        }
        filtered.sort((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()));
        int start = page * size;
        int end = Math.min(start + size, filtered.size());
        return start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>();
    }

    @Override
    public Message getMessage(String messageId) {
        return messages.get(messageId);
    }

    @Override
    public List<Topic> listTopics() {
        return new ArrayList<>(topics.values());
    }

    @Override
    public Topic createTopic(Topic topic) {
        if (topic.getTopicId() == null || topic.getTopicId().isEmpty()) {
            topic.setTopicId("topic-" + UUID.randomUUID().toString().substring(0, 8));
        }
        topic.setCreateTime(System.currentTimeMillis());
        topic.setUpdateTime(System.currentTimeMillis());
        topics.put(topic.getTopicId(), topic);
        topicSubscribers.put(topic.getTopicId(), new HashSet<>());
        return topic;
    }

    @Override
    public Topic getTopic(String topicId) {
        return topics.get(topicId);
    }

    @Override
    public boolean deleteTopic(String topicId) {
        topics.remove(topicId);
        topicSubscribers.remove(topicId);
        return true;
    }

    @Override
    public boolean subscribe(String topicId, String userId) {
        Set<String> subscribers = topicSubscribers.get(topicId);
        if (subscribers == null) {
            return false;
        }
        subscribers.add(userId);
        Topic topic = topics.get(topicId);
        if (topic != null) {
            topic.setSubscriberCount(subscribers.size());
        }
        return true;
    }

    @Override
    public boolean unsubscribe(String topicId, String userId) {
        Set<String> subscribers = topicSubscribers.get(topicId);
        if (subscribers == null) {
            return false;
        }
        subscribers.remove(userId);
        Topic topic = topics.get(topicId);
        if (topic != null) {
            topic.setSubscriberCount(subscribers.size());
        }
        return true;
    }

    @Override
    public PushResult publish(String topicId, Message message) {
        Topic topic = topics.get(topicId);
        if (topic == null) {
            return PushResult.failure(message.getMessageId(), "Topic not found");
        }
        if (message.getMessageId() == null || message.getMessageId().isEmpty()) {
            message.setMessageId("msg-" + UUID.randomUUID().toString().substring(0, 8));
        }
        message.setTopicId(topicId);
        message.setStatus("published");
        messages.put(message.getMessageId(), message);
        return PushResult.success(message.getMessageId());
    }

    @Override
    public List<String> getSubscribers(String topicId) {
        Set<String> subscribers = topicSubscribers.get(topicId);
        return subscribers != null ? new ArrayList<>(subscribers) : new ArrayList<>();
    }

    @Override
    public PushResult sendP2P(Message message) {
        if (message.getToUserId() == null || message.getToUserId().isEmpty()) {
            return PushResult.failure(message.getMessageId(), "Target user is required");
        }
        if (message.getMessageId() == null || message.getMessageId().isEmpty()) {
            message.setMessageId("msg-" + UUID.randomUUID().toString().substring(0, 8));
        }
        message.setMessageType("p2p");
        message.setStatus("delivered");
        messages.put(message.getMessageId(), message);
        return PushResult.success(message.getMessageId());
    }

    @Override
    public List<Message> getP2PHistory(String fromUserId, String toUserId, int page, int size) {
        List<Message> filtered = new ArrayList<>();
        for (Message msg : messages.values()) {
            if (!"p2p".equals(msg.getMessageType())) continue;
            boolean match = true;
            if (fromUserId != null && !fromUserId.equals(msg.getFromUserId())) {
                match = false;
            }
            if (toUserId != null && !toUserId.equals(msg.getToUserId())) {
                match = false;
            }
            if (match) {
                filtered.add(msg);
            }
        }
        filtered.sort((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()));
        int start = page * size;
        int end = Math.min(start + size, filtered.size());
        return start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>();
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMessages", messages.size());
        stats.put("totalTopics", topics.size());
        stats.put("totalSubscriptions", topicSubscribers.values().stream().mapToInt(Set::size).sum());
        return stats;
    }
}

package net.ooder.mvp.skill.scene.agent.service.impl;

import net.ooder.mvp.skill.scene.agent.dto.AgentMessageDTO;
import net.ooder.mvp.skill.scene.agent.dto.MessageType;
import net.ooder.mvp.skill.scene.agent.dto.MessageStatus;
import net.ooder.mvp.skill.scene.agent.service.AgentMessageService;
import net.ooder.skill.common.storage.JsonStorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class AgentMessageServiceImpl implements AgentMessageService {

    private static final Logger log = LoggerFactory.getLogger(AgentMessageServiceImpl.class);
    
    private static final String STORAGE_KEY_MESSAGES = "agent-messages";
    private static final int DEFAULT_MESSAGE_EXPIRE = 86400;
    private static final int DEFAULT_MAX_QUEUE_SIZE = 1000;

    @Value("${agent.message.expire:86400}")
    private int messageExpireSeconds = DEFAULT_MESSAGE_EXPIRE;

    @Value("${agent.message.max-queue:1000}")
    private int maxQueueSize = DEFAULT_MAX_QUEUE_SIZE;

    @Autowired
    private JsonStorageService storage;

    private final Map<String, AgentMessageDTO> allMessages = new ConcurrentHashMap<>();
    private final Map<String, PriorityBlockingQueue<AgentMessageDTO>> agentQueues = new ConcurrentHashMap<>();
    private final Map<String, Consumer<AgentMessageDTO>> subscribers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        loadFromStorage();
        log.info("[AgentMessage] Initialized with {} messages, expire: {}s", 
            allMessages.size(), messageExpireSeconds);
    }

    private void loadFromStorage() {
        try {
            Map<String, AgentMessageDTO> storedMessages = storage.getAll(STORAGE_KEY_MESSAGES);
            if (storedMessages != null) {
                for (Map.Entry<String, AgentMessageDTO> entry : storedMessages.entrySet()) {
                    AgentMessageDTO msg = entry.getValue();
                    if (!msg.isExpired()) {
                        allMessages.put(entry.getKey(), msg);
                        addToAgentQueue(msg);
                    }
                }
                log.info("[AgentMessage] Loaded {} valid messages from storage", allMessages.size());
            }
        } catch (Exception e) {
            log.warn("[AgentMessage] Failed to load from storage: {}", e.getMessage());
        }
    }

    @Override
    public String sendMessage(AgentMessageDTO message) {
        if (message.getMessageId() == null || message.getMessageId().isEmpty()) {
            message.setMessageId("msg-" + UUID.randomUUID().toString().substring(0, 12));
        }
        if (message.getCreateTime() == 0) {
            message.setCreateTime(System.currentTimeMillis());
        }
        if (message.getExpireTime() == 0) {
            message.setExpireTime(System.currentTimeMillis() + messageExpireSeconds * 1000L);
        }
        if (message.getStatus() == null) {
            message.setStatus(MessageStatus.PENDING.name());
        }
        if (message.getPriority() == 0) {
            message.setPriority(5);
        }

        allMessages.put(message.getMessageId(), message);
        addToAgentQueue(message);
        persistMessage(message);

        notifySubscriber(message);

        log.info("[AgentMessage] Message sent: {} -> {} [{}]", 
            message.getFromAgent(), message.getToAgent(), message.getType());
        return message.getMessageId();
    }

    @Override
    public String sendMessage(String fromAgent, String toAgent, MessageType type, 
                             String title, String content) {
        return sendMessage(fromAgent, toAgent, null, type, title, content, null, 5);
    }

    @Override
    public String sendMessage(String fromAgent, String toAgent, String sceneGroupId,
                             MessageType type, String title, String content, 
                             Map<String, Object> payload, int priority) {
        AgentMessageDTO message = new AgentMessageDTO();
        message.setFromAgent(fromAgent);
        message.setToAgent(toAgent);
        message.setSceneGroupId(sceneGroupId);
        message.setType(type.name());
        message.setTitle(title);
        message.setContent(content);
        message.setPayload(payload);
        message.setPriority(priority);
        return sendMessage(message);
    }

    @Override
    public List<AgentMessageDTO> receiveMessages(String agentId) {
        PriorityBlockingQueue<AgentMessageDTO> queue = agentQueues.get(agentId);
        if (queue == null) {
            return new ArrayList<>();
        }
        return queue.stream()
            .filter(msg -> !msg.isExpired())
            .sorted((a, b) -> {
                int priorityCompare = Integer.compare(b.getPriority(), a.getPriority());
                if (priorityCompare != 0) return priorityCompare;
                return Long.compare(a.getCreateTime(), b.getCreateTime());
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentMessageDTO> receiveMessages(String agentId, String type) {
        return receiveMessages(agentId).stream()
            .filter(msg -> type.equals(msg.getType()))
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentMessageDTO> receiveUnreadMessages(String agentId) {
        return receiveMessages(agentId).stream()
            .filter(msg -> MessageStatus.PENDING.name().equals(msg.getStatus()) ||
                          MessageStatus.DELIVERED.name().equals(msg.getStatus()))
            .collect(Collectors.toList());
    }

    @Override
    public AgentMessageDTO getMessage(String messageId) {
        AgentMessageDTO msg = allMessages.get(messageId);
        if (msg != null && msg.isExpired()) {
            allMessages.remove(messageId);
            removeFromAgentQueue(msg);
            return null;
        }
        return msg;
    }

    @Override
    public void acknowledge(String agentId, String messageId) {
        AgentMessageDTO msg = allMessages.get(messageId);
        if (msg != null && agentId.equals(msg.getToAgent())) {
            msg.setStatus(MessageStatus.DELIVERED.name());
            persistMessage(msg);
            log.debug("[AgentMessage] Message {} acknowledged by {}", messageId, agentId);
        }
    }

    @Override
    public void markAsRead(String agentId, String messageId) {
        AgentMessageDTO msg = allMessages.get(messageId);
        if (msg != null && agentId.equals(msg.getToAgent())) {
            msg.setStatus(MessageStatus.READ.name());
            persistMessage(msg);
            log.debug("[AgentMessage] Message {} marked as read by {}", messageId, agentId);
        }
    }

    @Override
    public void markAsProcessed(String agentId, String messageId) {
        AgentMessageDTO msg = allMessages.get(messageId);
        if (msg != null && agentId.equals(msg.getToAgent())) {
            msg.setStatus(MessageStatus.PROCESSED.name());
            persistMessage(msg);
            log.debug("[AgentMessage] Message {} marked as processed by {}", messageId, agentId);
        }
    }

    @Override
    public void subscribe(String agentId, Consumer<AgentMessageDTO> handler) {
        subscribers.put(agentId, handler);
        log.info("[AgentMessage] Agent {} subscribed to message notifications", agentId);
    }

    @Override
    public void unsubscribe(String agentId) {
        subscribers.remove(agentId);
        log.info("[AgentMessage] Agent {} unsubscribed from message notifications", agentId);
    }

    @Override
    public int getPendingCount(String agentId) {
        return (int) receiveMessages(agentId).stream()
            .filter(msg -> MessageStatus.PENDING.name().equals(msg.getStatus()))
            .count();
    }

    @Override
    public int getUnreadCount(String agentId) {
        return (int) receiveMessages(agentId).stream()
            .filter(msg -> !MessageStatus.PROCESSED.name().equals(msg.getStatus()))
            .count();
    }

    @Override
    public List<AgentMessageDTO> getMessagesByScene(String sceneGroupId) {
        return allMessages.values().stream()
            .filter(msg -> sceneGroupId.equals(msg.getSceneGroupId()))
            .filter(msg -> !msg.isExpired())
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentMessageDTO> getMessagesFromAgent(String fromAgent) {
        return allMessages.values().stream()
            .filter(msg -> fromAgent.equals(msg.getFromAgent()))
            .filter(msg -> !msg.isExpired())
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentMessageDTO> getMessagesToAgent(String toAgent) {
        return receiveMessages(toAgent);
    }

    @Override
    public boolean deleteMessage(String messageId) {
        AgentMessageDTO msg = allMessages.remove(messageId);
        if (msg != null) {
            removeFromAgentQueue(msg);
            storage.remove(STORAGE_KEY_MESSAGES, messageId);
            log.info("[AgentMessage] Message {} deleted", messageId);
            return true;
        }
        return false;
    }

    @Override
    public int cleanupExpiredMessages() {
        int cleaned = 0;
        Iterator<Map.Entry<String, AgentMessageDTO>> it = allMessages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, AgentMessageDTO> entry = it.next();
            if (entry.getValue().isExpired()) {
                removeFromAgentQueue(entry.getValue());
                storage.remove(STORAGE_KEY_MESSAGES, entry.getKey());
                it.remove();
                cleaned++;
            }
        }
        if (cleaned > 0) {
            log.info("[AgentMessage] Cleaned up {} expired messages", cleaned);
        }
        return cleaned;
    }

    private void addToAgentQueue(AgentMessageDTO message) {
        String toAgent = message.getToAgent();
        if (toAgent != null) {
            PriorityBlockingQueue<AgentMessageDTO> queue = 
                agentQueues.computeIfAbsent(toAgent, k -> new PriorityBlockingQueue<>(100, 
                    (a, b) -> {
                        int priorityCompare = Integer.compare(b.getPriority(), a.getPriority());
                        if (priorityCompare != 0) return priorityCompare;
                        return Long.compare(a.getCreateTime(), b.getCreateTime());
                    }));
            
            if (queue.size() >= maxQueueSize) {
                AgentMessageDTO oldest = queue.poll();
                if (oldest != null) {
                    allMessages.remove(oldest.getMessageId());
                    log.warn("[AgentMessage] Queue full for {}, dropped oldest message", toAgent);
                }
            }
            queue.offer(message);
        }
    }

    private void removeFromAgentQueue(AgentMessageDTO message) {
        String toAgent = message.getToAgent();
        if (toAgent != null) {
            PriorityBlockingQueue<AgentMessageDTO> queue = agentQueues.get(toAgent);
            if (queue != null) {
                queue.remove(message);
            }
        }
    }

    private void notifySubscriber(AgentMessageDTO message) {
        String toAgent = message.getToAgent();
        if (toAgent != null) {
            Consumer<AgentMessageDTO> handler = subscribers.get(toAgent);
            if (handler != null) {
                try {
                    handler.accept(message);
                    log.debug("[AgentMessage] Notified subscriber for {}", toAgent);
                } catch (Exception e) {
                    log.warn("[AgentMessage] Failed to notify subscriber: {}", e.getMessage());
                }
            }
        }
    }

    private void persistMessage(AgentMessageDTO message) {
        try {
            storage.put(STORAGE_KEY_MESSAGES, message.getMessageId(), message);
        } catch (Exception e) {
            log.error("[AgentMessage] Failed to persist message: {}", e.getMessage());
        }
    }
}

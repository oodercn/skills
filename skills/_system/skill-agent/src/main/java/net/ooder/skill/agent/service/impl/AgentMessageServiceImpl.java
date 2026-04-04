package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.dto.AgentMessageDTO;
import net.ooder.skill.agent.dto.MessageStatus;
import net.ooder.skill.agent.dto.MessageType;
import net.ooder.skill.agent.service.AgentMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AgentMessageServiceImpl implements AgentMessageService {

    private static final Logger log = LoggerFactory.getLogger(AgentMessageServiceImpl.class);

    private Map<String, AgentMessageDTO> messageStore = new ConcurrentHashMap<>();
    private Map<String, Set<String>> agentMessages = new ConcurrentHashMap<>();
    private Map<String, Set<String>> sceneMessages = new ConcurrentHashMap<>();
    private Map<String, String> messageReadStatus = new ConcurrentHashMap<>();
    
    private int maxRetryCount = 3;
    private long messageTTL = 86400;

    @Override
    public String sendMessage(AgentMessageDTO message) {
        log.info("[sendMessage] {} -> {}", message.getFromAgent(), message.getToAgent());
        
        String messageId = UUID.randomUUID().toString();
        message.setMessageId(messageId);
        message.setCreateTime(System.currentTimeMillis());
        message.setStatus(MessageStatus.PENDING.name());
        
        if (message.getExpireTime() <= 0) {
            message.setExpireTime(message.getCreateTime() + messageTTL * 1000);
        }
        
        messageStore.put(messageId, message);
        
        agentMessages.computeIfAbsent(message.getToAgent(), k -> ConcurrentHashMap.newKeySet()).add(messageId);
        
        if (message.getFromAgent() != null) {
            agentMessages.computeIfAbsent(message.getFromAgent(), k -> ConcurrentHashMap.newKeySet()).add(messageId);
        }
        
        if (message.getSceneGroupId() != null) {
            sceneMessages.computeIfAbsent(message.getSceneGroupId(), k -> ConcurrentHashMap.newKeySet()).add(messageId);
        }
        
        message.setStatus(MessageStatus.DELIVERED.name());
        
        return messageId;
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
        log.debug("[receiveMessages] Agent: {}", agentId);
        
        Set<String> messageIds = agentMessages.get(agentId);
        if (messageIds == null || messageIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        return messageIds.stream()
            .map(messageStore::get)
            .filter(Objects::nonNull)
            .filter(m -> !isExpired(m))
            .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentMessageDTO> receiveMessages(String agentId, String type) {
        log.debug("[receiveMessages] Agent: {}, type: {}", agentId, type);
        
        return receiveMessages(agentId).stream()
            .filter(m -> type.equals(m.getType()))
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentMessageDTO> receiveUnreadMessages(String agentId) {
        log.debug("[receiveUnreadMessages] Agent: {}", agentId);
        
        return receiveMessages(agentId).stream()
            .filter(m -> {
                String key = m.getMessageId() + ":" + agentId;
                return !messageReadStatus.containsKey(key);
            })
            .collect(Collectors.toList());
    }

    @Override
    public AgentMessageDTO getMessage(String messageId) {
        AgentMessageDTO message = messageStore.get(messageId);
        if (message != null && isExpired(message)) {
            return null;
        }
        return message;
    }

    @Override
    public void acknowledge(String agentId, String messageId) {
        log.info("[acknowledge] Agent: {}, Message: {}", agentId, messageId);
        
        AgentMessageDTO message = messageStore.get(messageId);
        if (message != null) {
            message.setStatus(MessageStatus.DELIVERED.name());
        }
    }

    @Override
    public void markAsRead(String agentId, String messageId) {
        log.info("[markAsRead] Agent: {}, Message: {}", agentId, messageId);
        
        String key = messageId + ":" + agentId;
        messageReadStatus.put(key, "READ");
        
        AgentMessageDTO message = messageStore.get(messageId);
        if (message != null) {
            message.setStatus(MessageStatus.READ.name());
        }
    }

    @Override
    public void markAsProcessed(String agentId, String messageId) {
        log.info("[markAsProcessed] Agent: {}, Message: {}", agentId, messageId);
        
        AgentMessageDTO message = messageStore.get(messageId);
        if (message != null) {
            message.setStatus(MessageStatus.PROCESSED.name());
        }
    }

    @Override
    public int getPendingCount(String agentId) {
        return (int) receiveMessages(agentId).stream()
            .filter(m -> MessageStatus.PENDING.name().equals(m.getStatus()) ||
                        MessageStatus.DELIVERED.name().equals(m.getStatus()))
            .count();
    }

    @Override
    public int getUnreadCount(String agentId) {
        return receiveUnreadMessages(agentId).size();
    }

    @Override
    public List<AgentMessageDTO> getMessagesByScene(String sceneGroupId) {
        log.debug("[getMessagesByScene] Scene: {}", sceneGroupId);
        
        Set<String> messageIds = sceneMessages.get(sceneGroupId);
        if (messageIds == null || messageIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        return messageIds.stream()
            .map(messageStore::get)
            .filter(Objects::nonNull)
            .filter(m -> !isExpired(m))
            .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
            .collect(Collectors.toList());
    }

    @Override
    public boolean deleteMessage(String messageId) {
        log.info("[deleteMessage] Message: {}", messageId);
        
        AgentMessageDTO message = messageStore.remove(messageId);
        if (message != null) {
            if (message.getToAgent() != null) {
                Set<String> toAgentMsgs = agentMessages.get(message.getToAgent());
                if (toAgentMsgs != null) {
                    toAgentMsgs.remove(messageId);
                }
            }
            if (message.getFromAgent() != null) {
                Set<String> fromAgentMsgs = agentMessages.get(message.getFromAgent());
                if (fromAgentMsgs != null) {
                    fromAgentMsgs.remove(messageId);
                }
            }
            if (message.getSceneGroupId() != null) {
                Set<String> sceneMsgs = sceneMessages.get(message.getSceneGroupId());
                if (sceneMsgs != null) {
                    sceneMsgs.remove(messageId);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void expireMessage(String messageId) {
        AgentMessageDTO message = messageStore.get(messageId);
        if (message != null) {
            message.setStatus(MessageStatus.EXPIRED.name());
        }
    }

    @Override
    public void cleanExpiredMessages() {
        log.info("[cleanExpiredMessages] Cleaning expired messages");
        
        List<String> expiredIds = messageStore.values().stream()
            .filter(this::isExpired)
            .map(AgentMessageDTO::getMessageId)
            .collect(Collectors.toList());
        
        for (String id : expiredIds) {
            deleteMessage(id);
        }
        
        log.info("[cleanExpiredMessages] Cleaned {} expired messages", expiredIds.size());
    }

    @Override
    public void setMaxRetryCount(int count) {
        this.maxRetryCount = count;
    }

    @Override
    public void setMessageTTL(long ttlSeconds) {
        this.messageTTL = ttlSeconds;
    }

    private boolean isExpired(AgentMessageDTO message) {
        return message.getExpireTime() > 0 && System.currentTimeMillis() > message.getExpireTime();
    }
}

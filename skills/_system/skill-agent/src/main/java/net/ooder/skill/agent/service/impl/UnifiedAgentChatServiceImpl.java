package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.config.UnifiedInterfaceConfig.UnifiedInterfaceAdapter;
import net.ooder.skill.agent.dto.AgentChatMessageDTO;
import net.ooder.skill.agent.dto.SceneChatContextDTO;
import net.ooder.skill.agent.service.AgentChatService;
import net.ooder.skill.agent.spi.LocalSessionManager;
import net.ooder.skill.agent.spi.LocalAgentContextService;
import net.ooder.skill.agent.spi.LocalA2AProtocolService;

import net.ooder.scene.session.unified.UnifiedSessionManager;
import net.ooder.scene.session.unified.UnifiedSession;
import net.ooder.scene.session.unified.SessionType;
import net.ooder.scene.session.unified.OnlineStatus;
import net.ooder.scene.message.queue.MessageQueueService;
import net.ooder.scene.message.queue.MessageEnvelope;
import net.ooder.scene.message.queue.MessageParticipant;
import net.ooder.scene.message.queue.MessagePriority;
import net.ooder.scene.agent.context.AgentContextManager;
import net.ooder.scene.agent.context.VirtualAgentConfig;
import net.ooder.scene.a2a.A2AProtocolService;
import net.ooder.scene.a2a.A2AMessage;
import net.ooder.scene.a2a.A2AMessageType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnifiedAgentChatServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(UnifiedAgentChatServiceImpl.class);

    @Autowired
    private UnifiedInterfaceAdapter unifiedAdapter;

    @Autowired
    private AgentChatService chatService;

    @Autowired(required = false)
    private UnifiedSessionManager sessionManager;

    @Autowired(required = false)
    private MessageQueueService messageQueueService;

    @Autowired(required = false)
    private AgentContextManager agentContextManager;

    @Autowired(required = false)
    private A2AProtocolService a2aProtocolService;

    @Deprecated
    @Autowired(required = false)
    private LocalSessionManager localSessionManager;

    @Deprecated
    @Autowired(required = false)
    private LocalAgentContextService localAgentContextService;

    @Deprecated
    @Autowired(required = false)
    private LocalA2AProtocolService localA2AProtocolService;

    public boolean useSeSdk() {
        return sessionManager != null && messageQueueService != null && 
               agentContextManager != null && a2aProtocolService != null;
    }
    
    public boolean useUnifiedInterface() {
        return unifiedAdapter.hasSessionManager() && unifiedAdapter.hasAgentContextManager();
    }

    public SceneChatContextDTO getUnifiedChatContext(String sceneGroupId, String userId) {
        log.debug("[getUnifiedChatContext] sceneGroupId={}, userId={}", sceneGroupId, userId);
        
        SceneChatContextDTO context = chatService.getChatContext(sceneGroupId, userId);
        
        if (useSeSdk()) {
            try {
                List<UnifiedSession> sessions = sessionManager.getActiveSessionsByScene(sceneGroupId);
                if (sessions != null) {
                    context.setActiveSessionCount(sessions.size());
                }
                OnlineStatus status = sessionManager.getOnlineStatus(userId);
                if (status != null) {
                    context.setUserOnline(status == OnlineStatus.ONLINE);
                }
                log.debug("[getUnifiedChatContext] Found {} active sessions via SE SDK", sessions != null ? sessions.size() : 0);
            } catch (Exception e) {
                log.warn("[getUnifiedChatContext] SE SDK call failed: {}", e.getMessage());
            }
        } else if (useUnifiedInterface()) {
            try {
                List<?> sessions = unifiedAdapter.getSessionManager().getActiveSessionsByScene(sceneGroupId);
                log.debug("[getUnifiedChatContext] Found {} active sessions via legacy adapter", sessions.size());
            } catch (Exception e) {
                log.warn("[getUnifiedChatContext] Legacy adapter call failed: {}", e.getMessage());
            }
        } else if (localSessionManager != null) {
            try {
                List<LocalSessionManager.SessionInfo> sessions = 
                    localSessionManager.getActiveSessionsByScene(sceneGroupId);
                log.debug("[getUnifiedChatContext] Found {} active sessions via local fallback", sessions.size());
            } catch (Exception e) {
                log.warn("[getUnifiedChatContext] Local fallback failed: {}", e.getMessage());
            }
        }
        
        return context;
    }

    public String sendUnifiedMessage(String sceneGroupId, AgentChatMessageDTO message) {
        log.info("[sendUnifiedMessage] sceneGroupId={}, type={}", sceneGroupId, message.getMessageType());
        
        String messageId = chatService.sendMessage(sceneGroupId, message);
        
        if (useSeSdk()) {
            try {
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("sceneGroupId", sceneGroupId);
                metadata.put("messageType", message.getMessageType());
                metadata.put("senderId", message.getSenderId());
                metadata.put("receiverId", message.getReceiverId());

                MessageEnvelope envelope = new MessageEnvelope();
                envelope.setMessageId(messageId);
                envelope.setFrom(MessageParticipant.user(message.getSenderId()));
                envelope.setTo(message.getReceiverId() != null ? 
                    MessageParticipant.user(message.getReceiverId()) : MessageParticipant.system());
                envelope.setContent(message.getContent());
                envelope.setMetadata(metadata);
                envelope.setCreatedAt(System.currentTimeMillis());
                
                MessagePriority priority = message.getPriority() <= 3 ? MessagePriority.HIGH :
                    message.getPriority() <= 7 ? MessagePriority.NORMAL : MessagePriority.LOW;
                
                String queueId = messageQueueService.sendPriorityMessage(envelope, priority);
                log.debug("[sendUnifiedMessage] Enqueued message {} via SE SDK, queueId={}", messageId, queueId);
            } catch (Exception e) {
                log.warn("[sendUnifiedMessage] Failed to enqueue via SE SDK: {}", e.getMessage());
            }
        }
        
        return messageId;
    }

    public void processOfflineMessages(String userId) {
        log.info("[processOfflineMessages] userId={}", userId);
        
        if (useSeSdk()) {
            try {
                List<MessageEnvelope> messages = messageQueueService.getOfflineMessages(userId);
                log.info("[processOfflineMessages] Found {} offline messages for user {} via SE SDK", messages.size(), userId);
            } catch (Exception e) {
                log.warn("[processOfflineMessages] SE SDK call failed: {}", e.getMessage());
            }
        }
    }

    public void ensureReliableDelivery(String messageId) {
        log.debug("[ensureReliableDelivery] messageId={}", messageId);
        
        if (useSeSdk()) {
            try {
                messageQueueService.retryMessage(messageId);
                log.debug("[ensureReliableDelivery] Retried message {} via SE SDK", messageId);
            } catch (Exception e) {
                log.warn("[ensureReliableDelivery] SE SDK retry failed: {}", e.getMessage());
            }
        }
    }

    public void acknowledgeMessage(String messageId, String userId) {
        log.debug("[acknowledgeMessage] messageId={}, userId={}", messageId, userId);
        
        if (useSeSdk()) {
            try {
                messageQueueService.acknowledgeMessage(messageId, userId);
                log.debug("[acknowledgeMessage] Acknowledged message {} via SE SDK", messageId);
            } catch (Exception e) {
                log.warn("[acknowledgeMessage] SE SDK ack failed: {}", e.getMessage());
            }
        }
    }

    public void sendA2AMessage(String fromAgentId, String toAgentId, String content) {
        log.info("[sendA2AMessage] from={} to={}", fromAgentId, toAgentId);
        
        if (useSeSdk()) {
            try {
                A2AMessage message = new A2AMessage();
                message.setFromAgentId(fromAgentId);
                message.setToAgentId(toAgentId);
                message.setPayload(content);
                message.setSceneGroupId(null);
                message.setMessageType(A2AMessageType.CHAT);
                
                a2aProtocolService.sendMessage(message);
                log.debug("[sendA2AMessage] Sent A2A message via SE SDK");
            } catch (Exception e) {
                log.warn("[sendA2AMessage] SE SDK send failed: {}", e.getMessage());
            }
        } else if (localA2AProtocolService != null) {
            try {
                LocalA2AProtocolService.A2AMessage message = new LocalA2AProtocolService.A2AMessage();
                message.setFromAgentId(fromAgentId);
                message.setToAgentId(toAgentId);
                message.setContent(content);
                message.setMessageType("A2A_CHAT");
                
                localA2AProtocolService.sendA2AMessage(message);
                log.debug("[sendA2AMessage] Sent A2A message via local fallback");
            } catch (Exception e) {
                log.warn("[sendA2AMessage] Local fallback failed: {}", e.getMessage());
            }
        }
    }

    public void broadcastToSceneGroup(String sceneGroupId, String content, String fromAgentId) {
        log.info("[broadcastToSceneGroup] sceneGroupId={}, from={}", sceneGroupId, fromAgentId);
        
        if (useSeSdk()) {
            try {
                A2AMessage message = new A2AMessage();
                message.setFromAgentId(fromAgentId);
                message.setPayload(content);
                message.setSceneGroupId(sceneGroupId);
                message.setMessageType(A2AMessageType.NOTIFICATION);
                
                a2aProtocolService.broadcast(sceneGroupId, message);
                log.debug("[broadcastToSceneGroup] Broadcast via SE SDK");
            } catch (Exception e) {
                log.warn("[broadcastToSceneGroup] SE SDK broadcast failed: {}", e.getMessage());
            }
        }
    }

    public Map<String, Object> getUnifiedStats(String sceneGroupId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("seSdkAvailable", useSeSdk());
        stats.put("unifiedInterfaceEnabled", useUnifiedInterface());
        stats.put("sessionManagerAvailable", sessionManager != null);
        stats.put("messageQueueAvailable", messageQueueService != null);
        stats.put("agentContextAvailable", agentContextManager != null);
        stats.put("a2aProtocolAvailable", a2aProtocolService != null);
        stats.put("legacyAdapterEnabled", unifiedAdapter.hasSessionManager());
        
        if (useSeSdk()) {
            try {
                var mqStats = messageQueueService.getStats();
                if (mqStats != null) {
                    stats.put("totalMessages", mqStats.getTotalMessages());
                    stats.put("pendingMessages", mqStats.getPendingMessages());
                    stats.put("deliveredMessages", mqStats.getDeliveredMessages());
                }
                stats.put("sessionCount", sessionManager.getSessionCount());
                stats.put("activeSessionCount", sessionManager.getActiveSessionCount());
            } catch (Exception e) {
                log.warn("[getUnifiedStats] Failed to get SE SDK stats: {}", e.getMessage());
            }
        }
        
        return stats;
    }
    
    public void registerAgentInScene(String agentId, String agentName, String sceneGroupId) {
        log.info("[registerAgentInScene] agent={}, name={}, scene={}", agentId, agentName, sceneGroupId);
        
        if (agentContextManager != null) {
            try {
                VirtualAgentConfig config = new VirtualAgentConfig(agentId, agentName);
                config.setSceneGroupId(sceneGroupId);
                
                agentContextManager.registerVirtualAgent(config);
                log.info("[registerAgentInScene] Registered agent {} in scene {} via SE SDK", agentId, sceneGroupId);
            } catch (Exception e) {
                log.warn("[registerAgentInScene] SE SDK register failed: {}", e.getMessage());
            }
        } else if (localAgentContextService != null) {
            try {
                var registration = new LocalAgentContextService.AgentRegistration();
                registration.setAgentId(agentId);
                registration.setAgentName(agentName);
                registration.setSceneGroupId(sceneGroupId);
                registration.setRole("AGENT");
                
                localAgentContextService.registerAgent(registration);
                log.info("[registerAgentInScene] Registered agent {} in scene {} via local", agentId, sceneGroupId);
            } catch (Exception e) {
                log.warn("[registerAgentInScene] Local register failed: {}", e.getMessage());
            }
        }
    }
}

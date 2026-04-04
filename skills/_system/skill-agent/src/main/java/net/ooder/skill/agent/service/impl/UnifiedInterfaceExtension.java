package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.config.UnifiedInterfaceConfig.UnifiedInterfaceAdapter;
import net.ooder.scene.a2a.A2AMessageRouter;
import net.ooder.scene.a2a.A2AProtocolService;
import net.ooder.scene.agent.context.AgentContextManager;
import net.ooder.scene.message.northbound.NorthboundMessageQueue;
import net.ooder.scene.message.offline.OfflineMessageService;
import net.ooder.scene.message.queue.MessageQueueService;
import net.ooder.scene.message.reliability.MessageReliabilityService;
import net.ooder.scene.session.unified.UnifiedSessionManager;
import net.ooder.scene.websocket.auth.WebSocketAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UnifiedInterfaceExtension {

    private static final Logger log = LoggerFactory.getLogger(UnifiedInterfaceExtension.class);

    @Autowired
    private UnifiedInterfaceAdapter adapter;

    public boolean hasUnifiedSessionManager() {
        return adapter.hasSessionManager();
    }

    public boolean hasAgentContextManager() {
        return adapter.hasAgentContextManager();
    }

    public boolean hasMessageQueueService() {
        return adapter.hasMessageQueueService();
    }

    public boolean hasA2AProtocolService() {
        return adapter.hasA2AProtocolService();
    }

    public boolean hasA2AMessageRouter() {
        return adapter.hasA2AMessageRouter();
    }

    public boolean hasNorthboundMessageQueue() {
        return adapter.hasNorthboundMessageQueue();
    }

    public boolean hasOfflineMessageService() {
        return adapter.hasOfflineMessageService();
    }

    public boolean hasMessageReliabilityService() {
        return adapter.hasMessageReliabilityService();
    }

    public boolean hasWebSocketAuthService() {
        return adapter.hasWebSocketAuthService();
    }

    public UnifiedSessionManager getUnifiedSessionManager() {
        return adapter.getSessionManager();
    }

    public AgentContextManager getAgentContextManager() {
        return adapter.getAgentContextManager();
    }

    public MessageQueueService getMessageQueueService() {
        return adapter.getMessageQueueService();
    }

    public A2AProtocolService getA2AProtocolService() {
        return adapter.getA2aProtocolService();
    }

    public A2AMessageRouter getA2AMessageRouter() {
        return adapter.getA2aMessageRouter();
    }

    public NorthboundMessageQueue getNorthboundMessageQueue() {
        return adapter.getNorthboundMessageQueue();
    }

    public OfflineMessageService getOfflineMessageService() {
        return adapter.getOfflineMessageService();
    }

    public MessageReliabilityService getMessageReliabilityService() {
        return adapter.getMessageReliabilityService();
    }

    public WebSocketAuthService getWebSocketAuthService() {
        return adapter.getWebSocketAuthService();
    }

    public Map<String, Boolean> getAvailabilityStatus() {
        Map<String, Boolean> status = new HashMap<>();
        status.put("unifiedSessionManager", hasUnifiedSessionManager());
        status.put("agentContextManager", hasAgentContextManager());
        status.put("messageQueueService", hasMessageQueueService());
        status.put("a2aProtocolService", hasA2AProtocolService());
        status.put("a2aMessageRouter", hasA2AMessageRouter());
        status.put("northboundMessageQueue", hasNorthboundMessageQueue());
        status.put("offlineMessageService", hasOfflineMessageService());
        status.put("messageReliabilityService", hasMessageReliabilityService());
        status.put("webSocketAuthService", hasWebSocketAuthService());
        return status;
    }

    public void logAvailabilityStatus() {
        log.info("[UnifiedInterface] Availability Status:");
        log.info("  - UnifiedSessionManager: {}", hasUnifiedSessionManager() ? "Available" : "Not Available");
        log.info("  - AgentContextManager: {}", hasAgentContextManager() ? "Available" : "Not Available");
        log.info("  - MessageQueueService: {}", hasMessageQueueService() ? "Available" : "Not Available");
        log.info("  - A2AProtocolService: {}", hasA2AProtocolService() ? "Available" : "Not Available");
        log.info("  - A2AMessageRouter: {}", hasA2AMessageRouter() ? "Available" : "Not Available");
        log.info("  - NorthboundMessageQueue: {}", hasNorthboundMessageQueue() ? "Available" : "Not Available");
        log.info("  - OfflineMessageService: {}", hasOfflineMessageService() ? "Available" : "Not Available");
        log.info("  - MessageReliabilityService: {}", hasMessageReliabilityService() ? "Available" : "Not Available");
        log.info("  - WebSocketAuthService: {}", hasWebSocketAuthService() ? "Available" : "Not Available");
    }
}

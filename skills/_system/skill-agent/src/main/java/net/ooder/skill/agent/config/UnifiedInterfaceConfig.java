package net.ooder.skill.agent.config;

import net.ooder.scene.session.unified.UnifiedSessionManager;
import net.ooder.scene.agent.context.AgentContextManager;
import net.ooder.scene.message.queue.MessageQueueService;
import net.ooder.scene.a2a.A2AProtocolService;
import net.ooder.scene.a2a.A2AMessageRouter;
import net.ooder.scene.message.northbound.NorthboundMessageQueue;
import net.ooder.scene.message.offline.OfflineMessageService;
import net.ooder.scene.message.reliability.MessageReliabilityService;
import net.ooder.scene.websocket.auth.WebSocketAuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UnifiedInterfaceConfig {

    private static final Logger log = LoggerFactory.getLogger(UnifiedInterfaceConfig.class);

    @Autowired(required = false)
    private UnifiedSessionManager unifiedSessionManager;

    @Autowired(required = false)
    private AgentContextManager agentContextManager;

    @Autowired(required = false)
    private MessageQueueService messageQueueService;

    @Autowired(required = false)
    private A2AProtocolService a2aProtocolService;

    @Autowired(required = false)
    private A2AMessageRouter a2aMessageRouter;

    @Autowired(required = false)
    private NorthboundMessageQueue northboundMessageQueue;

    @Autowired(required = false)
    private OfflineMessageService offlineMessageService;

    @Autowired(required = false)
    private MessageReliabilityService messageReliabilityService;

    @Autowired(required = false)
    private WebSocketAuthService webSocketAuthService;

    @Bean
    @ConditionalOnMissingBean
    public UnifiedInterfaceAdapter unifiedInterfaceAdapter() {
        log.info("[UnifiedInterface] Initializing unified interface adapter");
        log.info("  - UnifiedSessionManager: {}", unifiedSessionManager != null ? "Available" : "Not Available");
        log.info("  - AgentContextManager: {}", agentContextManager != null ? "Available" : "Not Available");
        log.info("  - MessageQueueService: {}", messageQueueService != null ? "Available" : "Not Available");
        log.info("  - A2AProtocolService: {}", a2aProtocolService != null ? "Available" : "Not Available");
        log.info("  - A2AMessageRouter: {}", a2aMessageRouter != null ? "Available" : "Not Available");
        log.info("  - NorthboundMessageQueue: {}", northboundMessageQueue != null ? "Available" : "Not Available");
        log.info("  - OfflineMessageService: {}", offlineMessageService != null ? "Available" : "Not Available");
        log.info("  - MessageReliabilityService: {}", messageReliabilityService != null ? "Available" : "Not Available");
        log.info("  - WebSocketAuthService: {}", webSocketAuthService != null ? "Available" : "Not Available");
        
        return new UnifiedInterfaceAdapter(
            unifiedSessionManager,
            agentContextManager,
            messageQueueService,
            a2aProtocolService,
            a2aMessageRouter,
            northboundMessageQueue,
            offlineMessageService,
            messageReliabilityService,
            webSocketAuthService
        );
    }

    public static class UnifiedInterfaceAdapter {
        private final UnifiedSessionManager sessionManager;
        private final AgentContextManager agentContextManager;
        private final MessageQueueService messageQueueService;
        private final A2AProtocolService a2aProtocolService;
        private final A2AMessageRouter a2aMessageRouter;
        private final NorthboundMessageQueue northboundMessageQueue;
        private final OfflineMessageService offlineMessageService;
        private final MessageReliabilityService messageReliabilityService;
        private final WebSocketAuthService webSocketAuthService;

        public UnifiedInterfaceAdapter(
                UnifiedSessionManager sessionManager,
                AgentContextManager agentContextManager,
                MessageQueueService messageQueueService,
                A2AProtocolService a2aProtocolService,
                A2AMessageRouter a2aMessageRouter,
                NorthboundMessageQueue northboundMessageQueue,
                OfflineMessageService offlineMessageService,
                MessageReliabilityService messageReliabilityService,
                WebSocketAuthService webSocketAuthService) {
            this.sessionManager = sessionManager;
            this.agentContextManager = agentContextManager;
            this.messageQueueService = messageQueueService;
            this.a2aProtocolService = a2aProtocolService;
            this.a2aMessageRouter = a2aMessageRouter;
            this.northboundMessageQueue = northboundMessageQueue;
            this.offlineMessageService = offlineMessageService;
            this.messageReliabilityService = messageReliabilityService;
            this.webSocketAuthService = webSocketAuthService;
        }

        public UnifiedSessionManager getSessionManager() {
            return sessionManager;
        }

        public AgentContextManager getAgentContextManager() {
            return agentContextManager;
        }

        public MessageQueueService getMessageQueueService() {
            return messageQueueService;
        }

        public A2AProtocolService getA2aProtocolService() {
            return a2aProtocolService;
        }

        public A2AMessageRouter getA2aMessageRouter() {
            return a2aMessageRouter;
        }

        public NorthboundMessageQueue getNorthboundMessageQueue() {
            return northboundMessageQueue;
        }

        public OfflineMessageService getOfflineMessageService() {
            return offlineMessageService;
        }

        public MessageReliabilityService getMessageReliabilityService() {
            return messageReliabilityService;
        }

        public WebSocketAuthService getWebSocketAuthService() {
            return webSocketAuthService;
        }

        public boolean hasSessionManager() {
            return sessionManager != null;
        }

        public boolean hasAgentContextManager() {
            return agentContextManager != null;
        }

        public boolean hasMessageQueueService() {
            return messageQueueService != null;
        }

        public boolean hasA2AProtocolService() {
            return a2aProtocolService != null;
        }

        public boolean hasA2AMessageRouter() {
            return a2aMessageRouter != null;
        }

        public boolean hasNorthboundMessageQueue() {
            return northboundMessageQueue != null;
        }

        public boolean hasOfflineMessageService() {
            return offlineMessageService != null;
        }

        public boolean hasMessageReliabilityService() {
            return messageReliabilityService != null;
        }

        public boolean hasWebSocketAuthService() {
            return webSocketAuthService != null;
        }
    }
}

package net.ooder.skill.agent.pipeline;

import net.ooder.skill.agent.model.AgentRoleConfig;
import net.ooder.skill.agent.service.AgentLLMService;
import net.ooder.skill.agent.llm.LLMResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class AgentGroupPipeline {

    private static final Logger log = LoggerFactory.getLogger(AgentGroupPipeline.class);

    @Autowired
    private AgentLLMService agentLLMService;

    @Autowired(required = false)
    private List<AgentTriggerRule> triggerRules;

    private final Executor asyncExecutor = Executors.newFixedThreadPool(4,
        r -> { Thread t = new Thread(r, "agent-pipeline-"); t.setDaemon(true); return t; });

    public interface ChatMessage {
        String getContent();
        String getMessageId();
        String getSender();
        Object getFromParticipant();
        List<String> getCcParticipants();
    }

    public interface ChatContext {
        String getSceneGroupId();
        List<AgentRoleConfig> getActiveAgents();
    }

    public interface ChatMessageFactory {
        AgentResponse createReply(String messageId, String content, AgentRoleConfig agent);
        void broadcast(String sceneGroupId, AgentResponse reply);
    }

    public interface AgentResponse {
        String messageId();
        String messageType();
        String content();
        String status();
    }

    public CompletableFuture<Optional<AgentResponse>> processGroupMessage(
            ChatContext context,
            ChatMessage incoming,
            List<AgentRoleConfig> activeAgents) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                for (AgentRoleConfig agent : activeAgents) {
                    if (shouldTrigger(agent, incoming)) {
                        log.info("[AgentGroupPipeline] Agent '{}' triggered [mode={}]",
                            agent.getAgentName(), agent.getTriggerMode());

                        var response = generateAgentResponse(context, incoming, agent);
                        return Optional.ofNullable(response);
                    }
                }
                return Optional.empty();
            } catch (Exception e) {
                log.error("[AgentGroupPipeline] Error processing group message", e);
                return Optional.empty();
            }
        }, asyncExecutor);
    }

    boolean shouldTrigger(AgentRoleConfig agent, ChatMessage msg) {
        if (!agent.isActive()) return false;

        switch (agent.getTriggerMode()) {
            case ALL: return true;
            case MENTION: return checkMention(agent, msg);
            case KEYWORD: return checkKeyword(agent, msg);
            case NONE:
            default: return false;
        }
    }

    private boolean checkMention(AgentRoleConfig agent, ChatMessage msg) {
        String content = msg.getContent() != null ? msg.getContent().toLowerCase() : "";
        if (content.contains("@" + agent.getAgentName().toLowerCase())
            || content.contains("@agent") || content.contains("@all")) {
            return true;
        }
        var cc = msg.getCcParticipants();
        if (cc != null && cc.contains(agent.getAgentId())) return true;
        return false;
    }

    private boolean checkKeyword(AgentRoleConfig agent, ChatMessage msg) {
        if (agent.getTriggerKeywords() == null || agent.getTriggerKeywords().isEmpty()) return false;
        String text = msg.getContent() != null ? msg.getContent().toLowerCase() : "";
        return agent.getTriggerKeywords().stream()
            .anyMatch(kw -> kw != null && text.contains(kw.toLowerCase()));
    }

    AgentResponse generateAgentResponse(ChatContext context, ChatMessage incoming, AgentRoleConfig agent) {
        try {
            List<String> kbIds = agent.getKnowledgeBaseIds();
            boolean useRAG = kbIds != null && !kbIds.isEmpty();

            LLMResponse response;
            if (useRAG) {
                log.info("[AgentGroupPipeline] Agent '{}' using RAG mode ({} knowledge bases)",
                        agent.getAgentName(), kbIds.size());
                response = agentLLMService.processWithRAG(
                        agent.getAgentId(),
                        incoming.getContent(),
                        kbIds
                );
            } else {
                response = agentLLMService.chatWithHistory(
                        agent.getAgentId(),
                        context.getSceneGroupId(),
                        incoming.getContent(),
                        Collections.emptyList()
                );
            }

            String replyContent = (response != null && response.getMessage() != null)
                    ? response.getMessage() : "（暂无回复）";

            return new SimpleAgentResponse(
                    "agent-reply-" + UUID.randomUUID().toString().substring(0, 12),
                    "A2A", replyContent, "SENT"
            );
        } catch (Exception e) {
            log.error("[AgentGroupPipeline] Failed to generate response for agent {}", agent.getAgentName(), e);
            return new SimpleAgentResponse(
                    "agent-error-" + UUID.randomUUID().toString().substring(0, 12),
                    "SYSTEM", "抱歉，处理请求时出现错误: " + e.getMessage(), "ERROR"
            );
        }
    }

    static final class SimpleAgentResponse implements AgentResponse {
        private final String messageId;
        private final String messageType;
        private final String content;
        private final String status;

        SimpleAgentResponse(String messageId, String messageType, String content, String status) {
            this.messageId = messageId; this.messageType = messageType;
            this.content = content; this.status = status;
        }
        @Override public String messageId() { return messageId; }
        @Override public String messageType() { return messageType; }
        @Override public String content() { return content; }
        @Override public String status() { return status; }
    }

    public void shutdown() {
        log.info("[AgentGroupPipeline] Shutting down");
        if (asyncExecutor instanceof java.util.concurrent.ExecutorService) {
            ((java.util.concurrent.ExecutorService) asyncExecutor).shutdown();
        }
    }
}

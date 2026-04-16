package net.ooder.skill.agent.pipeline;

import net.ooder.skill.agent.model.AgentRoleConfig;
import org.springframework.stereotype.Component;

public interface AgentTriggerRule {

    int getPriority();

    String getRuleName();

    boolean matches(AgentRoleConfig agent, String messageContent);

    default int compareTo(AgentTriggerRule other) {
        return Integer.compare(other.getPriority(), this.getPriority());
    }
}

@Component
class MentionTriggerRule implements AgentTriggerRule {

    @Override
    public int getPriority() { return 100; }

    @Override
    public String getRuleName() { return "MENTION_TRIGGER"; }

    @Override
    public boolean matches(AgentRoleConfig agent, String messageContent) {
        if (agent.getTriggerMode() != AgentRoleConfig.TriggerMode.MENTION) return false;
        String text = messageContent != null ? messageContent.toLowerCase() : "";
        return text.contains("@" + agent.getAgentName().toLowerCase());
    }
}

@Component
class KeywordTriggerRule implements AgentTriggerRule {

    @Override
    public int getPriority() { return 90; }

    @Override
    public String getRuleName() { return "KEYWORD_TRIGGER"; }

    @Override
    public boolean matches(AgentRoleConfig agent, String messageContent) {
        if (agent.getTriggerMode() != AgentRoleConfig.TriggerMode.KEYWORD) return false;
        if (agent.getTriggerKeywords() == null || agent.getTriggerKeywords().isEmpty()) return false;
        String text = messageContent != null ? messageContent.toLowerCase() : "";
        return agent.getTriggerKeywords().stream()
            .anyMatch(kw -> kw != null && text.contains(kw.toLowerCase()));
    }
}

@Component
class AllMessageTriggerRule implements AgentTriggerRule {

    @Override
    public int getPriority() { return 80; }

    @Override
    public String getRuleName() { return "ALL_MESSAGE_TRIGGER"; }

    @Override
    public boolean matches(AgentRoleConfig agent, String messageContent) {
        return agent.getTriggerMode() == AgentRoleConfig.TriggerMode.ALL && agent.isActive();
    }
}

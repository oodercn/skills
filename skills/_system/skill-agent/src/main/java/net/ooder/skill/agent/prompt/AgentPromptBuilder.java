package net.ooder.skill.agent.prompt;

import net.ooder.skill.agent.dto.AgentDTO;
import net.ooder.skill.agent.model.AgentRoleConfig;
import net.ooder.skill.agent.dto.SceneChatContextDTO;
import net.ooder.skill.agent.function.FunctionCallingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AgentPromptBuilder {

    @Autowired
    private FunctionCallingService functionCallingService;

    public String buildSystemPrompt(AgentRoleConfig roleConfig, SceneChatContextDTO context) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append(buildRoleSection(roleConfig));
        prompt.append("\n");
        prompt.append(buildContextSection(context));
        prompt.append("\n");
        prompt.append(buildCapabilitiesSection(roleConfig));
        prompt.append("\n");
        prompt.append(buildToolsSection(roleConfig));
        prompt.append("\n");
        prompt.append(buildRulesSection());
        
        return prompt.toString();
    }

    private String buildRoleSection(AgentRoleConfig roleConfig) {
        StringBuilder section = new StringBuilder();
        section.append("## Role Definition\n");
        section.append("You are ").append(roleConfig.getAgentName());
        if (roleConfig.getRole() != null) {
            section.append(", acting as ").append(roleConfig.getRole());
        }
        section.append(".\n\n");
        
        if (roleConfig.getSystemPrompt() != null && !roleConfig.getSystemPrompt().isEmpty()) {
            section.append("### Core Instructions\n");
            section.append(roleConfig.getSystemPrompt()).append("\n\n");
        }
        
        return section.toString();
    }

    private String buildContextSection(SceneChatContextDTO context) {
        StringBuilder section = new StringBuilder();
        section.append("## Scene Context\n");
        
        if (context != null) {
            section.append("### Scene Information\n");
            section.append("- Scene Group: ").append(context.getSceneGroupName()).append("\n");
            if (context.getSceneType() != null) {
                section.append("- Scene Type: ").append(context.getSceneType()).append("\n");
            }
            if (context.getSceneDescription() != null) {
                section.append("- Description: ").append(context.getSceneDescription()).append("\n");
            }
            section.append("\n");
            
            if (context.getParticipants() != null && !context.getParticipants().isEmpty()) {
                section.append("### Participants\n");
                for (SceneChatContextDTO.ParticipantInfo p : context.getParticipants()) {
                    section.append("- ").append(p.getName())
                           .append(" (").append(p.getRole()).append(")")
                           .append(p.isOnline() ? " [Online]" : " [Offline]")
                           .append("\n");
                }
                section.append("\n");
            }
            
            if (context.getAgents() != null && !context.getAgents().isEmpty()) {
                section.append("### Other Agents\n");
                for (SceneChatContextDTO.AgentInfo agent : context.getAgents()) {
                    section.append("- ").append(agent.getName())
                           .append(" (").append(agent.getType()).append(")")
                           .append(" - Status: ").append(agent.getStatus())
                           .append("\n");
                }
                section.append("\n");
            }
        }
        
        return section.toString();
    }

    private String buildCapabilitiesSection(AgentRoleConfig roleConfig) {
        StringBuilder section = new StringBuilder();
        section.append("## Capabilities\n");
        
        if (roleConfig.getCapabilities() != null && !roleConfig.getCapabilities().isEmpty()) {
            for (String capability : roleConfig.getCapabilities()) {
                section.append("- ").append(capability).append("\n");
            }
        } else {
            section.append("- General conversation and assistance\n");
        }
        section.append("\n");
        
        return section.toString();
    }

    private String buildToolsSection(AgentRoleConfig roleConfig) {
        StringBuilder section = new StringBuilder();
        section.append("## Available Tools\n");
        
        if (roleConfig.isFunctionCallingEnabled()) {
            List<FunctionCallingService.FunctionDefinition> functions = 
                functionCallingService.getAvailableFunctions();
            
            List<FunctionCallingService.FunctionDefinition> allowedFunctions = functions.stream()
                .filter(f -> roleConfig.isToolAllowed(f.getName()))
                .collect(Collectors.toList());
            
            if (!allowedFunctions.isEmpty()) {
                for (FunctionCallingService.FunctionDefinition func : allowedFunctions) {
                    section.append("- **").append(func.getName()).append("**: ")
                           .append(func.getDescription()).append("\n");
                }
            } else {
                section.append("- No specific tools configured\n");
            }
        } else {
            section.append("- Function calling is disabled\n");
        }
        section.append("\n");
        
        return section.toString();
    }

    private String buildRulesSection() {
        StringBuilder section = new StringBuilder();
        section.append("## Interaction Rules\n");
        section.append("1. Always be helpful and professional\n");
        section.append("2. Use available tools when appropriate to complete tasks\n");
        section.append("3. Communicate clearly with other agents when collaboration is needed\n");
        section.append("4. Respect the scene context and participant roles\n");
        section.append("5. If you cannot complete a task, explain why and suggest alternatives\n");
        section.append("\n");
        section.append("## Response Format\n");
        section.append("- Provide clear, concise responses\n");
        section.append("- Use markdown formatting when appropriate\n");
        section.append("- When using tools, explain what you're doing\n");
        
        return section.toString();
    }

    public String buildConversationPrompt(List<Map<String, String>> messages) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("## Conversation History\n");
        
        for (Map<String, String> message : messages) {
            String role = message.getOrDefault("role", "unknown");
            String content = message.getOrDefault("content", "");
            
            switch (role.toLowerCase()) {
                case "user":
                    prompt.append("User: ").append(content).append("\n");
                    break;
                case "assistant":
                    prompt.append("Assistant: ").append(content).append("\n");
                    break;
                case "system":
                    prompt.append("[System] ").append(content).append("\n");
                    break;
                case "function":
                    prompt.append("[Function Result] ").append(content).append("\n");
                    break;
                default:
                    prompt.append("[").append(role).append("] ").append(content).append("\n");
            }
        }
        
        return prompt.toString();
    }

    public String buildAgentIntroduction(AgentDTO agent) {
        StringBuilder intro = new StringBuilder();
        intro.append("Hello! I'm ").append(agent.getAgentName());
        
        if (agent.getDescription() != null && !agent.getDescription().isEmpty()) {
            intro.append(", ").append(agent.getDescription());
        }
        
        intro.append(". ");
        
        if (agent.getCapabilities() != null && !agent.getCapabilities().isEmpty()) {
            intro.append("I can help you with: ");
            intro.append(String.join(", ", agent.getCapabilities()));
            intro.append(". ");
        }
        
        intro.append("How can I assist you today?");
        
        return intro.toString();
    }
}

package net.ooder.skill.agent.function;

import net.ooder.skill.agent.llm.LLMService;
import net.ooder.skill.agent.llm.LLMRequest;
import net.ooder.skill.agent.llm.LLMResponse;
import net.ooder.skill.agent.llm.FunctionCall;
import net.ooder.skill.agent.llm.FunctionResult;
import net.ooder.skill.agent.dto.AgentDTO;
import net.ooder.skill.agent.dto.AgentMessageDTO;
import net.ooder.skill.agent.dto.MessageType;
import net.ooder.skill.agent.service.AgentService;
import net.ooder.skill.agent.service.AgentMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FunctionCallingService {

    private static final Logger log = LoggerFactory.getLogger(FunctionCallingService.class);

    @Autowired
    private LLMService llmService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentMessageService messageService;

    private Map<String, FunctionDefinition> functionRegistry = new ConcurrentHashMap<>();

    public FunctionCallingService() {
        registerBuiltInFunctions();
    }

    private void registerBuiltInFunctions() {
        registerFunction(FunctionDefinition.builder()
            .name("get_agent_status")
            .description("Get the current status of an agent")
            .addParameter("agentId", "string", "The ID of the agent", true)
            .handler(this::handleGetAgentStatus)
            .build());

        registerFunction(FunctionDefinition.builder()
            .name("send_message_to_agent")
            .description("Send a message to another agent")
            .addParameter("toAgentId", "string", "The target agent ID", true)
            .addParameter("messageType", "string", "Type of message (TASK_DELEGATE, DATA_SHARE, COLLAB_REQUEST)", true)
            .addParameter("title", "string", "Message title", true)
            .addParameter("content", "string", "Message content", false)
            .handler(this::handleSendMessage)
            .build());

        registerFunction(FunctionDefinition.builder()
            .name("list_available_agents")
            .description("List all available agents in the system")
            .addParameter("agentType", "string", "Filter by agent type", false)
            .handler(this::handleListAgents)
            .build());

        registerFunction(FunctionDefinition.builder()
            .name("request_collaboration")
            .description("Request collaboration with another agent")
            .addParameter("targetAgentId", "string", "The agent to collaborate with", true)
            .addParameter("taskDescription", "string", "Description of the collaboration task", true)
            .handler(this::handleRequestCollaboration)
            .build());
    }

    public void registerFunction(FunctionDefinition function) {
        functionRegistry.put(function.getName(), function);
        log.info("Registered function: {}", function.getName());
    }

    public List<FunctionDefinition> getAvailableFunctions() {
        return new ArrayList<>(functionRegistry.values());
    }

    public List<Map<String, Object>> getFunctionSchemas() {
        List<Map<String, Object>> schemas = new ArrayList<>();
        for (FunctionDefinition func : functionRegistry.values()) {
            schemas.add(func.toOpenAISchema());
        }
        return schemas;
    }

    public FunctionResult executeFunction(String functionName, Map<String, Object> arguments) {
        log.info("Executing function: {} with arguments: {}", functionName, arguments);
        
        FunctionDefinition function = functionRegistry.get(functionName);
        if (function == null) {
            return FunctionResult.error("Function not found: " + functionName);
        }

        try {
            Object result = function.getHandler().apply(arguments);
            return FunctionResult.success(result);
        } catch (Exception e) {
            log.error("Error executing function {}: {}", functionName, e.getMessage(), e);
            return FunctionResult.error("Function execution failed: " + e.getMessage());
        }
    }

    public List<FunctionResult> processFunctionCalls(List<FunctionCall> functionCalls) {
        List<FunctionResult> results = new ArrayList<>();
        for (FunctionCall call : functionCalls) {
            FunctionResult result = executeFunction(call.getName(), call.getArguments());
            results.add(result);
        }
        return results;
    }

    private Object handleGetAgentStatus(Map<String, Object> args) {
        String agentId = (String) args.get("agentId");
        AgentDTO agent = agentService.getAgent(agentId);
        if (agent == null) {
            return Map.of("error", "Agent not found: " + agentId);
        }
        return Map.of(
            "agentId", agent.getAgentId(),
            "name", agent.getAgentName(),
            "type", agent.getAgentType(),
            "status", agent.getStatus(),
            "health", agent.getHealthStatus(),
            "load", agent.getLoadPercentage()
        );
    }

    private Object handleSendMessage(Map<String, Object> args) {
        String toAgentId = (String) args.get("toAgentId");
        String messageTypeStr = (String) args.get("messageType");
        String title = (String) args.get("title");
        String content = (String) args.getOrDefault("content", "");

        AgentMessageDTO message = new AgentMessageDTO();
        message.setToAgent(toAgentId);
        message.setType(messageTypeStr);
        message.setTitle(title);
        message.setContent(content);
        message.setPriority(5);

        String messageId = messageService.sendMessage(message);
        return Map.of("messageId", messageId, "status", "sent");
    }

    private Object handleListAgents(Map<String, Object> args) {
        String agentType = (String) args.get("agentType");
        List<AgentDTO> agents;
        if (agentType != null && !agentType.isEmpty()) {
            agents = agentService.listByType(agentType);
        } else {
            agents = agentService.listAgents(1, 100).getList();
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (AgentDTO agent : agents) {
            result.add(Map.of(
                "agentId", agent.getAgentId(),
                "name", agent.getAgentName(),
                "type", agent.getAgentType(),
                "status", agent.getStatus()
            ));
        }
        return Map.of("agents", result, "count", result.size());
    }

    private Object handleRequestCollaboration(Map<String, Object> args) {
        String targetAgentId = (String) args.get("targetAgentId");
        String taskDescription = (String) args.get("taskDescription");

        AgentMessageDTO message = new AgentMessageDTO();
        message.setToAgent(targetAgentId);
        message.setType(MessageType.COLLAB_REQUEST.name());
        message.setTitle("Collaboration Request");
        message.setContent(taskDescription);
        message.setPriority(7);

        String messageId = messageService.sendMessage(message);
        return Map.of("messageId", messageId, "status", "request_sent");
    }

    public static class FunctionDefinition {
        private String name;
        private String description;
        private Map<String, ParameterDefinition> parameters;
        private List<String> required;
        private java.util.function.Function<Map<String, Object>, Object> handler;

        public static FunctionDefinitionBuilder builder() {
            return new FunctionDefinitionBuilder();
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, ParameterDefinition> getParameters() { return parameters; }
        public void setParameters(Map<String, ParameterDefinition> parameters) { this.parameters = parameters; }
        public List<String> getRequired() { return required; }
        public void setRequired(List<String> required) { this.required = required; }
        public java.util.function.Function<Map<String, Object>, Object> getHandler() { return handler; }
        public void setHandler(java.util.function.Function<Map<String, Object>, Object> handler) { this.handler = handler; }

        public Map<String, Object> toOpenAISchema() {
            Map<String, Object> schema = new HashMap<>();
            schema.put("name", name);
            schema.put("description", description);

            Map<String, Object> paramsSchema = new HashMap<>();
            paramsSchema.put("type", "object");
            
            Map<String, Object> properties = new HashMap<>();
            for (Map.Entry<String, ParameterDefinition> entry : parameters.entrySet()) {
                properties.put(entry.getKey(), entry.getValue().toSchema());
            }
            paramsSchema.put("properties", properties);
            paramsSchema.put("required", required);

            schema.put("parameters", paramsSchema);
            return schema;
        }

        public static class ParameterDefinition {
            private String type;
            private String description;
            private List<String> enumValues;

            public ParameterDefinition(String type, String description) {
                this.type = type;
                this.description = description;
            }

            public Map<String, Object> toSchema() {
                Map<String, Object> schema = new HashMap<>();
                schema.put("type", type);
                schema.put("description", description);
                if (enumValues != null && !enumValues.isEmpty()) {
                    schema.put("enum", enumValues);
                }
                return schema;
            }

            public String getType() { return type; }
            public void setType(String type) { this.type = type; }
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
            public List<String> getEnumValues() { return enumValues; }
            public void setEnumValues(List<String> enumValues) { this.enumValues = enumValues; }
        }

        public static class FunctionDefinitionBuilder {
            private FunctionDefinition function = new FunctionDefinition();

            public FunctionDefinitionBuilder name(String name) {
                function.setName(name);
                return this;
            }

            public FunctionDefinitionBuilder description(String description) {
                function.setDescription(description);
                return this;
            }

            public FunctionDefinitionBuilder addParameter(String name, String type, String description, boolean required) {
                if (function.getParameters() == null) {
                    function.setParameters(new HashMap<>());
                }
                if (function.getRequired() == null) {
                    function.setRequired(new ArrayList<>());
                }
                function.getParameters().put(name, new ParameterDefinition(type, description));
                if (required) {
                    function.getRequired().add(name);
                }
                return this;
            }

            public FunctionDefinitionBuilder handler(java.util.function.Function<Map<String, Object>, Object> handler) {
                function.setHandler(handler);
                return this;
            }

            public FunctionDefinition build() {
                return function;
            }
        }
    }
}

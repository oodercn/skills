package net.ooder.bpm.designer.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.bpm.designer.function.DesignerFunctionRegistry;
import net.ooder.bpm.designer.function.tools.WorkflowFunctionTools;
import net.ooder.bpm.designer.llm.FunctionCall;
import net.ooder.bpm.designer.llm.LLMResponse;
import net.ooder.bpm.designer.llm.LLMService;
import net.ooder.bpm.designer.prompt.DesignerPromptBuilder;
import net.ooder.bpm.designer.model.dto.DesignerContextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService {
    
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    
    private static final int MAX_TOOL_CALL_ROUNDS = 5;
    private static final int MAX_CONTEXT_MESSAGES = 20;
    
    private final LLMService llmService;
    private final ChatSessionManager sessionManager;
    private final DesignerFunctionRegistry functionRegistry;
    private final DesignerPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public ChatService(
            @Autowired(required = false) LLMService llmService,
            ChatSessionManager sessionManager,
            DesignerFunctionRegistry functionRegistry,
            DesignerPromptBuilder promptBuilder) {
        this.llmService = llmService;
        this.sessionManager = sessionManager;
        this.functionRegistry = functionRegistry;
        this.promptBuilder = promptBuilder;
    }
    
    public ChatResponse sendMessage(String sessionId, String userId, String skillId,
                                     String content, Map<String, Object> context) {
        log.info("Chat message from session: {}, user: {}, content length: {}", 
            sessionId, userId, content != null ? content.length() : 0);
        
        ChatSession session = sessionManager.getOrCreateSession(sessionId, userId, skillId);
        
        if (context != null) {
            session.setContext(context);
        }
        
        WorkflowFunctionTools.setCurrentSessionId(sessionId);
        try {
            if (session.getMessages().isEmpty()) {
                String systemPrompt = buildSystemPrompt(session);
                session.addSystemMessage(systemPrompt);
            } else {
                refreshSystemPrompt(session);
            }
            
            session.addUserMessage(content);
            
            if (!isLLMAvailable()) {
                return handleWithLocalFallback(session, content);
            }
            
            return handleWithLLM(session);
        } finally {
            WorkflowFunctionTools.clearCurrentSessionId();
        }
    }
    
    private void refreshSystemPrompt(ChatSession session) {
        List<Map<String, Object>> messages = session.getMessages();
        if (messages.isEmpty()) return;
        
        String updatedSystemPrompt = buildSystemPrompt(session);
        
        for (int i = 0; i < messages.size(); i++) {
            if ("system".equals(messages.get(i).get("role"))) {
                messages.get(i).put("content", updatedSystemPrompt);
                return;
            }
        }
        
        messages.add(0, Map.of("role", "system", "content", updatedSystemPrompt));
    }
    
    private ChatResponse handleWithLLM(ChatSession session) {
        List<Map<String, Object>> tools = getToolsSchemas();
        
        List<Map<String, Object>> messagesForLLM = trimMessages(session.getMessagesForLLM());
        
        LLMResponse llmResponse = llmService.chatWithMessages(messagesForLLM, tools);
        
        if (!llmResponse.isSuccess()) {
            log.warn("LLM call failed: {}", llmResponse.getError());
            return handleWithLocalFallback(session, getLastUserMessage(session));
        }
        
        int round = 0;
        List<ActionDefinition> allActions = new ArrayList<>();
        
        while (llmResponse.hasFunctionCalls() && round < MAX_TOOL_CALL_ROUNDS) {
            round++;
            List<Map<String, Object>> toolCallsForMessage = new ArrayList<>();
            
            for (FunctionCall call : llmResponse.getFunctionCalls()) {
                log.info("Executing tool call: {} (round {})", call.getName(), round);
                
                Map<String, Object> toolCallMap = new HashMap<>();
                toolCallMap.put("id", call.getId() != null ? call.getId() : call.getName());
                toolCallMap.put("type", "function");
                Map<String, Object> funcMap = new HashMap<>();
                funcMap.put("name", call.getName());
                funcMap.put("arguments", call.getArguments());
                toolCallMap.put("function", funcMap);
                toolCallsForMessage.add(toolCallMap);
                
                try {
                    Object result = functionRegistry.executeFunction(call.getName(), call.getArguments());
                    
                    String resultJson = objectMapper.writeValueAsString(result);
                    String toolCallId = call.getId() != null ? call.getId() : call.getName();
                    session.addToolResult(toolCallId, resultJson);
                    
                    allActions.add(new ActionDefinition(call.getName(), call.getArguments(), result, true, null));
                    
                    log.info("Tool {} executed successfully, result length: {}", 
                        call.getName(), resultJson.length());
                } catch (Exception e) {
                    log.error("Tool {} execution failed: {}", call.getName(), e.getMessage());
                    String errorJson = "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}";
                    String toolCallId = call.getId() != null ? call.getId() : call.getName();
                    session.addToolResult(toolCallId, errorJson);
                    
                    allActions.add(new ActionDefinition(call.getName(), call.getArguments(), null, false, e.getMessage()));
                }
            }
            
            session.addAssistantToolCalls(toolCallsForMessage);
            
            messagesForLLM = trimMessages(session.getMessagesForLLM());
            llmResponse = llmService.chatWithMessages(messagesForLLM, tools);
            
            if (!llmResponse.isSuccess()) {
                log.warn("LLM call failed in tool loop round {}: {}", round, llmResponse.getError());
                break;
            }
        }
        
        String assistantContent = llmResponse.getContent();
        if (assistantContent != null && !assistantContent.isEmpty()) {
            session.addAssistantMessage(assistantContent);
        } else {
            assistantContent = allActions.isEmpty() ? "请继续描述您的需求。" : "操作已完成。";
            session.addAssistantMessage(assistantContent);
        }
        
        ChatResponse response = new ChatResponse();
        response.setStatus("success");
        response.setContent(assistantContent);
        response.setSessionId(session.getSessionId());
        
        if (!allActions.isEmpty()) {
            response.setActions(allActions);
        }
        
        return response;
    }
    
    private ChatResponse handleWithLocalFallback(ChatSession session, String userMessage) {
        log.info("Using local fallback for message: {}", userMessage);
        
        String reply;
        List<ActionDefinition> actions = new ArrayList<>();
        String lowerMsg = userMessage.toLowerCase();
        
        if (lowerMsg.contains("创建") || lowerMsg.contains("新建") || lowerMsg.contains("添加")) {
            reply = handleLocalCreate(userMessage, actions);
        } else if (lowerMsg.contains("修改") || lowerMsg.contains("更新") || lowerMsg.contains("设置")) {
            reply = "请在右侧属性面板中修改属性。";
        } else if (lowerMsg.contains("删除") || lowerMsg.contains("移除")) {
            reply = "请选中要删除的元素，按 Delete 键删除。";
        } else if (lowerMsg.contains("验证") || lowerMsg.contains("检查")) {
            reply = handleLocalValidate(session, actions);
        } else if (lowerMsg.contains("帮助") || lowerMsg.contains("help")) {
            reply = buildHelpText();
        } else {
            reply = "我理解您的需求。您可以尝试：\n• 创建一个用户任务\n• 添加开始节点\n• 验证流程\n• 帮助";
        }
        
        session.addAssistantMessage(reply);
        
        ChatResponse response = new ChatResponse();
        response.setStatus("success");
        response.setContent(reply);
        response.setSessionId(session.getSessionId());
        
        if (!actions.isEmpty()) {
            response.setActions(actions);
        }
        
        return response;
    }
    
    private String handleLocalCreate(String message, List<ActionDefinition> actions) {
        if (message.contains("用户任务") || message.contains("任务")) {
            ActionDefinition action = new ActionDefinition(
                "create_activity",
                Map.of("name", "新用户任务", "activityType", "TASK", "activityCategory", "HUMAN"),
                null, true, null
            );
            actions.add(action);
            return "已创建用户任务，请在画布中调整位置。";
        } else if (message.contains("开始")) {
            ActionDefinition action = new ActionDefinition(
                "create_activity",
                Map.of("name", "开始", "activityType", "START", "activityCategory", "HUMAN"),
                null, true, null
            );
            actions.add(action);
            return "已创建开始节点。";
        } else if (message.contains("结束")) {
            ActionDefinition action = new ActionDefinition(
                "create_activity",
                Map.of("name", "结束", "activityType", "END", "activityCategory", "HUMAN"),
                null, true, null
            );
            actions.add(action);
            return "已创建结束节点。";
        } else if (message.contains("agent") || message.contains("llm")) {
            ActionDefinition action = new ActionDefinition(
                "create_activity",
                Map.of("name", "LLM任务", "activityType", "LLM_TASK", "activityCategory", "AGENT"),
                null, true, null
            );
            actions.add(action);
            return "已创建 LLM 任务。";
        }
        return "请指定要创建的元素类型，例如：创建用户任务、添加开始节点。";
    }
    
    @SuppressWarnings("unchecked")
    private String handleLocalValidate(ChatSession session, List<ActionDefinition> actions) {
        Map<String, Object> ctx = session.getContext();
        if (ctx == null) return "当前没有流程上下文信息。";
        
        List<Map<String, Object>> activities = (List<Map<String, Object>>) ctx.getOrDefault("activities", Collections.emptyList());
        List<Map<String, Object>> routes = (List<Map<String, Object>>) ctx.getOrDefault("routes", Collections.emptyList());
        
        StringBuilder sb = new StringBuilder();
        sb.append("**流程验证结果**\n\n");
        sb.append("• 活动数量：").append(activities.size()).append("\n");
        sb.append("• 路由数量：").append(routes.size()).append("\n");
        
        boolean hasStart = activities.stream().anyMatch(a -> "START".equals(a.get("activityType")));
        boolean hasEnd = activities.stream().anyMatch(a -> "END".equals(a.get("activityType")));
        
        if (!hasStart) sb.append("⚠️ 缺少开始节点\n");
        if (!hasEnd) sb.append("⚠️ 缺少结束节点\n");
        if (hasStart && hasEnd) sb.append("✅ 流程结构基本完整\n");
        
        return sb.toString();
    }
    
    private String buildSystemPrompt(ChatSession session) {
        DesignerContextDTO context = buildContextFromSession(session);
        String basePrompt = promptBuilder.buildSystemPrompt(context);
        
        String contextSummary = buildContextSummary(session);
        if (contextSummary != null && !contextSummary.isEmpty()) {
            return basePrompt + "\n\n## 当前流程状态\n\n" + contextSummary;
        }
        
        return basePrompt;
    }
    
    @SuppressWarnings("unchecked")
    private String buildContextSummary(ChatSession session) {
        Map<String, Object> ctx = session.getContext();
        if (ctx == null || ctx.isEmpty()) return "";
        
        StringBuilder sb = new StringBuilder();
        
        String processName = (String) ctx.get("processName");
        String processId = (String) ctx.get("processId");
        if (processName != null || processId != null) {
            sb.append("流程：").append(processName != null ? processName : processId).append("\n");
        }
        
        List<Map<String, Object>> activities = (List<Map<String, Object>>) ctx.get("activities");
        if (activities != null && !activities.isEmpty()) {
            sb.append("\n### 活动列表\n");
            for (Map<String, Object> act : activities) {
                String id = (String) act.get("activityDefId");
                String name = (String) act.get("name");
                String type = (String) act.get("activityType");
                String category = (String) act.getOrDefault("activityCategory", "HUMAN");
                sb.append("- ").append(name != null ? name : id)
                  .append(" (").append(type).append("/").append(category).append(")\n");
            }
        }
        
        List<Map<String, Object>> routes = (List<Map<String, Object>>) ctx.get("routes");
        if (routes != null && !routes.isEmpty()) {
            sb.append("\n### 路由列表\n");
            for (Map<String, Object> route : routes) {
                String from = (String) route.get("from");
                String to = (String) route.get("to");
                String name = (String) route.get("name");
                sb.append("- ").append(from).append(" → ").append(to);
                if (name != null && !name.isEmpty()) sb.append(" [").append(name).append("]");
                sb.append("\n");
            }
        }
        
        String currentActivityId = (String) ctx.get("activityId");
        if (currentActivityId != null) {
            sb.append("\n### 当前选中\n");
            sb.append("活动ID: ").append(currentActivityId);
            String actName = (String) ctx.get("activityName");
            String actType = (String) ctx.get("activityType");
            if (actName != null) sb.append(", 名称: ").append(actName);
            if (actType != null) sb.append(", 类型: ").append(actType);
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    private DesignerContextDTO buildContextFromSession(ChatSession session) {
        DesignerContextDTO context = new DesignerContextDTO();
        context.setSessionId(session.getSessionId());
        context.setUserId(session.getUserId());
        
        if (session.getContext() != null) {
            Map<String, Object> ctx = session.getContext();
            if (ctx.containsKey("processId")) {
                net.ooder.bpm.designer.model.dto.ProcessDefDTO process = 
                    new net.ooder.bpm.designer.model.dto.ProcessDefDTO();
                process.setProcessDefId((String) ctx.get("processId"));
                process.setName((String) ctx.get("processName"));
                context.setCurrentProcess(process);
            }
            if (ctx.containsKey("activityId")) {
                net.ooder.bpm.designer.model.dto.ActivityDefDTO activity = 
                    new net.ooder.bpm.designer.model.dto.ActivityDefDTO();
                activity.setActivityDefId((String) ctx.get("activityId"));
                activity.setName((String) ctx.get("activityName"));
                activity.setActivityType((String) ctx.get("activityType"));
                activity.setActivityCategory((String) ctx.get("activityCategory"));
                context.setCurrentActivity(activity);
            }
        }
        
        return context;
    }
    
    private List<Map<String, Object>> getToolsSchemas() {
        List<Map<String, Object>> schemas = functionRegistry.getOpenAISchemas();
        List<Map<String, Object>> tools = new ArrayList<>();
        for (Map<String, Object> schema : schemas) {
            Map<String, Object> tool = new HashMap<>();
            tool.put("type", "function");
            tool.put("function", schema);
            tools.add(tool);
        }
        return tools;
    }
    
    private List<Map<String, Object>> trimMessages(List<Map<String, Object>> messages) {
        if (messages.size() <= MAX_CONTEXT_MESSAGES) {
            return messages;
        }
        
        Map<String, Object> systemMsg = null;
        if (!messages.isEmpty() && "system".equals(messages.get(0).get("role"))) {
            systemMsg = new HashMap<>(messages.get(0));
        }
        
        int trimFrom = systemMsg != null ? 1 : 0;
        int available = MAX_CONTEXT_MESSAGES - trimFrom;
        int startIdx = messages.size() - available;
        if (startIdx < trimFrom) startIdx = trimFrom;
        
        List<Map<String, Object>> trimmed = new ArrayList<>();
        if (systemMsg != null) {
            trimmed.add(systemMsg);
        }
        for (int i = startIdx; i < messages.size(); i++) {
            trimmed.add(messages.get(i));
        }
        
        return trimmed;
    }
    
    private String getLastUserMessage(ChatSession session) {
        List<Map<String, Object>> msgs = session.getMessages();
        for (int i = msgs.size() - 1; i >= 0; i--) {
            if ("user".equals(msgs.get(i).get("role"))) {
                return (String) msgs.get(i).get("content");
            }
        }
        return "";
    }
    
    public boolean isLLMAvailable() {
        return llmService != null && llmService.isAvailable();
    }
    
    private String buildHelpText() {
        return "**BPM 设计助手帮助**\n\n" +
            "**创建元素：**\n" +
            "• 创建用户任务\n" +
            "• 添加开始/结束节点\n" +
            "• 创建 LLM 任务\n\n" +
            "**编辑元素：**\n" +
            "• 选中活动后可在右侧面板修改属性\n" +
            "• 支持拖拽调整位置\n\n" +
            "**流程操作：**\n" +
            "• 验证流程 - 检查流程完整性\n" +
            "• 建议下一步 - 获取智能建议\n\n" +
            "**快捷键：**\n" +
            "• Ctrl+S 保存\n" +
            "• Ctrl+Z 撤销\n" +
            "• Delete 删除";
    }
    
    public static class ChatResponse {
        private String status;
        private String content;
        private String sessionId;
        private List<ActionDefinition> actions;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public List<ActionDefinition> getActions() { return actions; }
        public void setActions(List<ActionDefinition> actions) { this.actions = actions; }
    }
    
    public static class ActionDefinition {
        private String type;
        private Map<String, Object> data;
        private Object result;
        private boolean success;
        private String error;
        
        public ActionDefinition(String type, Map<String, Object> data, Object result, boolean success, String error) {
            this.type = type;
            this.data = data;
            this.result = result;
            this.success = success;
            this.error = error;
        }
        
        public String getType() { return type; }
        public Map<String, Object> getData() { return data; }
        public Object getResult() { return result; }
        public boolean isSuccess() { return success; }
        public String getError() { return error; }
    }
}

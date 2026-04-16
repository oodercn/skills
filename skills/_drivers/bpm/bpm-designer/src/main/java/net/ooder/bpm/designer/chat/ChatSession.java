package net.ooder.bpm.designer.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatSession {
    
    private final String sessionId;
    private final String userId;
    private final String skillId;
    private final long createdAt;
    private long lastActiveAt;
    private List<Map<String, Object>> messages;
    private Map<String, Object> context;
    
    public ChatSession(String sessionId, String userId, String skillId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.skillId = skillId;
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = this.createdAt;
        this.messages = new ArrayList<>();
    }
    
    public void addSystemMessage(String content) {
        Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("role", "system");
        msg.put("content", content);
        messages.add(msg);
        touch();
    }
    
    public void addUserMessage(String content) {
        Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("role", "user");
        msg.put("content", content);
        messages.add(msg);
        touch();
    }
    
    public void addAssistantMessage(String content) {
        Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("role", "assistant");
        msg.put("content", content);
        messages.add(msg);
        touch();
    }
    
    public void addAssistantToolCalls(List<Map<String, Object>> toolCalls) {
        Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("role", "assistant");
        msg.put("tool_calls", toolCalls);
        msg.put("content", "");
        messages.add(msg);
        touch();
    }
    
    public void addToolResult(String toolCallId, String content) {
        Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("role", "tool");
        msg.put("tool_call_id", toolCallId);
        msg.put("content", content);
        messages.add(msg);
        touch();
    }
    
    public List<Map<String, Object>> getMessagesForLLM() {
        return new ArrayList<>(messages);
    }
    
    public List<Map<String, Object>> getConversationHistory() {
        List<Map<String, Object>> history = new ArrayList<>();
        for (Map<String, Object> msg : messages) {
            String role = (String) msg.get("role");
            if ("user".equals(role) || "assistant".equals(role)) {
                Map<String, Object> entry = new java.util.HashMap<>();
                entry.put("role", role);
                entry.put("content", msg.get("content"));
                history.add(entry);
            }
        }
        return history;
    }
    
    private void touch() {
        this.lastActiveAt = System.currentTimeMillis();
    }
    
    public String getSessionId() { return sessionId; }
    public String getUserId() { return userId; }
    public String getSkillId() { return skillId; }
    public long getCreatedAt() { return createdAt; }
    public long getLastActiveAt() { return lastActiveAt; }
    public List<Map<String, Object>> getMessages() { return messages; }
    public void setMessages(List<Map<String, Object>> messages) { this.messages = messages; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; touch(); }
}

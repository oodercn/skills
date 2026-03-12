package net.ooder.skill.llm.base;

public class LlmMessage {
    
    public enum Role {
        SYSTEM, USER, ASSISTANT, FUNCTION
    }
    
    private Role role;
    private String content;
    private String name;
    
    public LlmMessage() {}
    
    public LlmMessage(Role role, String content) {
        this.role = role;
        this.content = content;
    }
    
    public static LlmMessage system(String content) {
        return new LlmMessage(Role.SYSTEM, content);
    }
    
    public static LlmMessage user(String content) {
        return new LlmMessage(Role.USER, content);
    }
    
    public static LlmMessage assistant(String content) {
        return new LlmMessage(Role.ASSISTANT, content);
    }
    
    public static LlmMessage function(String name, String content) {
        LlmMessage msg = new LlmMessage(Role.FUNCTION, content);
        msg.setName(name);
        return msg;
    }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

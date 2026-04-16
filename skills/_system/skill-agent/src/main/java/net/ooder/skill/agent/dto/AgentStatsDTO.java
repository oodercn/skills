package net.ooder.skill.agent.dto;

public class AgentStatsDTO {
    
    private int totalAgents;
    private int activeAgents;
    private int inactiveAgents;
    private int totalConversations;
    private long totalMessages;

    public int getTotalAgents() { return totalAgents; }
    public void setTotalAgents(int totalAgents) { this.totalAgents = totalAgents; }
    public int getActiveAgents() { return activeAgents; }
    public void setActiveAgents(int activeAgents) { this.activeAgents = activeAgents; }
    public int getInactiveAgents() { return inactiveAgents; }
    public void setInactiveAgents(int inactiveAgents) { this.inactiveAgents = inactiveAgents; }
    public int getTotalConversations() { return totalConversations; }
    public void setTotalConversations(int totalConversations) { this.totalConversations = totalConversations; }
    public long getTotalMessages() { return totalMessages; }
    public void setTotalMessages(long totalMessages) { this.totalMessages = totalMessages; }
}

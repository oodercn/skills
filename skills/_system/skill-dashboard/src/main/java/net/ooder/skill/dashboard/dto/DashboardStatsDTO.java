package net.ooder.skill.dashboard.dto;

public class DashboardStatsDTO {
    
    private int totalSkills;
    private int activeSkills;
    private int totalScenes;
    private int activeScenes;
    private int totalUsers;
    private int activeUsers;

    public int getTotalSkills() { return totalSkills; }
    public void setTotalSkills(int totalSkills) { this.totalSkills = totalSkills; }
    public int getActiveSkills() { return activeSkills; }
    public void setActiveSkills(int activeSkills) { this.activeSkills = activeSkills; }
    public int getTotalScenes() { return totalScenes; }
    public void setTotalScenes(int totalScenes) { this.totalScenes = totalScenes; }
    public int getActiveScenes() { return activeScenes; }
    public void setActiveScenes(int activeScenes) { this.activeScenes = activeScenes; }
    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
    public int getActiveUsers() { return activeUsers; }
    public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }
}

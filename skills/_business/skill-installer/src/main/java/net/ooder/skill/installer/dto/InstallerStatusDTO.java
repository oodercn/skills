package net.ooder.skill.installer.dto;

public class InstallerStatusDTO {

    private String loop;
    private String status;
    private String completedAt;
    private boolean initialized;
    private boolean saved;

    public String getLoop() { return loop; }
    public void setLoop(String loop) { this.loop = loop; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public boolean isInitialized() { return initialized; }
    public void setInitialized(boolean initialized) { this.initialized = initialized; }
    public boolean isSaved() { return saved; }
    public void setSaved(boolean saved) { this.saved = saved; }
}
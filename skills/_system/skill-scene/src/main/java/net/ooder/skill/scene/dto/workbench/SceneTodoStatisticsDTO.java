package net.ooder.skill.scene.dto.workbench;

import java.util.Map;

public class SceneTodoStatisticsDTO {
    
    private int total;
    private int pending;
    private int completed;
    private Map<String, Integer> byType;

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getPending() { return pending; }
    public void setPending(int pending) { this.pending = pending; }
    public int getCompleted() { return completed; }
    public void setCompleted(int completed) { this.completed = completed; }
    public Map<String, Integer> getByType() { return byType; }
    public void setByType(Map<String, Integer> byType) { this.byType = byType; }
}

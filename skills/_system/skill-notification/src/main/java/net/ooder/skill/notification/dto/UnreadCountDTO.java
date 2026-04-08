package net.ooder.skill.notification.dto;

import java.util.Map;

public class UnreadCountDTO {
    
    private int total;
    private Map<String, Integer> byType;

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public Map<String, Integer> getByType() { return byType; }
    public void setByType(Map<String, Integer> byType) { this.byType = byType; }
}

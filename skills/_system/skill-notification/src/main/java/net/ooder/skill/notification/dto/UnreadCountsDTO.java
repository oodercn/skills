package net.ooder.skill.notification.dto;

import java.util.Map;
import java.util.HashMap;

public class UnreadCountsDTO {

    private int total;
    private int scene;
    private int todo;
    private int system;
    private int message;

    public static UnreadCountsDTO fromMap(Map<String, Integer> map) {
        if (map == null) return new UnreadCountsDTO();
        
        UnreadCountsDTO dto = new UnreadCountsDTO();
        dto.setTotal(map.getOrDefault("total", 0));
        dto.setScene(map.getOrDefault("scene", 0));
        dto.setTodo(map.getOrDefault("todo", 0));
        dto.setSystem(map.getOrDefault("system", 0));
        dto.setMessage(map.getOrDefault("message", 0));
        
        return dto;
    }

    public Map<String, Integer> toMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("total", total);
        map.put("scene", scene);
        map.put("todo", todo);
        map.put("system", system);
        map.put("message", message);
        return map;
    }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getScene() { return scene; }
    public void setScene(int scene) { this.scene = scene; }
    public int getTodo() { return todo; }
    public void setTodo(int todo) { this.todo = todo; }
    public int getSystem() { return system; }
    public void setSystem(int system) { this.system = system; }
    public int getMessage() { return message; }
    public void setMessage(int message) { this.message = message; }
}

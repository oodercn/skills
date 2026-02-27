package net.ooder.skill.test.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/system")
public class SystemApiController {
    
    @PostMapping("/status")
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        status.put("systemVersion", "v1.0.0-test");
        status.put("uptime", formatUptime(System.currentTimeMillis()));
        status.put("systemStatus", "运行正常");
        status.put("lastRestart", new Date().toString());
        
        Map<String, Object> resources = new HashMap<>();
        resources.put("cpuUsage", "15%");
        resources.put("memoryUsage", String.format("%.1f%%", (usedMemory * 100.0 / maxMemory)));
        resources.put("diskUsage", "42%");
        resources.put("networkUsage", "1.2 MB/s");
        status.put("resources", resources);
        
        List<Map<String, Object>> services = new ArrayList<>();
        services.add(createService("skill-discovery", "运行中", 1234, "2026-02-27 21:00:00", "128MB", "2%"));
        services.add(createService("menu-registry", "运行中", 1235, "2026-02-27 21:00:01", "64MB", "1%"));
        services.add(createService("static-resource", "运行中", 1236, "2026-02-27 21:00:02", "32MB", "0.5%"));
        status.put("services", services);
        
        List<Map<String, Object>> events = new ArrayList<>();
        events.add(createEvent("info", "系统启动", "系统成功启动", "21:00:00"));
        events.add(createEvent("success", "技能加载", "加载了 4 个 Nexus-UI 技能", "21:00:01"));
        events.add(createEvent("info", "菜单注册", "注册了 4 个菜单项", "21:00:02"));
        status.put("events", events);
        
        return status;
    }
    
    @GetMapping("/health")
    public Map<String, Object> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        return health;
    }
    
    private Map<String, Object> createService(String name, String status, int pid, String startTime, String memory, String cpu) {
        Map<String, Object> service = new HashMap<>();
        service.put("name", name);
        service.put("status", status);
        service.put("pid", pid);
        service.put("startTime", startTime);
        service.put("memory", memory);
        service.put("cpu", cpu);
        return service;
    }
    
    private Map<String, Object> createEvent(String type, String title, String message, String time) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", type);
        event.put("title", title);
        event.put("message", message);
        event.put("time", time);
        return event;
    }
    
    private String formatUptime(long currentTime) {
        long uptime = currentTime % (24 * 60 * 60 * 1000);
        long hours = uptime / (60 * 60 * 1000);
        long minutes = (uptime % (60 * 60 * 1000)) / (60 * 1000);
        return hours + "小时" + minutes + "分钟";
    }
}

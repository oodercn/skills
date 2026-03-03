package net.ooder.skill.test.controller;

import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/system")
public class SystemApiController {
    
    private static final Logger log = LoggerFactory.getLogger(SystemApiController.class);
    
    @Autowired(required = false)
    private SkillService skillService;
    
    @Autowired(required = false)
    private CapabilityRegistry capabilityRegistry;
    
    @PostMapping("/status")
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        status.put("systemVersion", "v1.0.0-sdk-2.3");
        status.put("uptime", formatUptime(System.currentTimeMillis()));
        status.put("systemStatus", "运行正常");
        status.put("lastRestart", new Date().toString());
        status.put("skillServiceReady", skillService != null);
        status.put("capabilityRegistryReady", capabilityRegistry != null);
        
        Map<String, Object> resources = new HashMap<>();
        resources.put("cpuUsage", getCpuUsage());
        resources.put("memoryUsage", String.format("%.1f%%", (usedMemory * 100.0 / maxMemory)));
        resources.put("diskUsage", getDiskUsage());
        resources.put("networkUsage", getNetworkUsage());
        status.put("resources", resources);
        
        List<Map<String, Object>> services = new ArrayList<>();
        services.add(createService("skill-service", skillService != null ? "运行中" : "未初始化", 
            getPid(), "N/A", formatMemory(usedMemory), getCpuUsage()));
        services.add(createService("capability-registry", capabilityRegistry != null ? "运行中" : "未初始化",
            getPid(), "N/A", "N/A", "N/A"));
        status.put("services", services);
        
        List<Map<String, Object>> events = new ArrayList<>();
        events.add(createEvent("info", "SDK初始化", 
            skillService != null ? "SDK SkillService已成功初始化" : "SDK未初始化", "N/A"));
        status.put("events", events);
        
        return status;
    }
    
    @GetMapping("/health")
    public Map<String, Object> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("skillServiceReady", skillService != null);
        health.put("capabilityRegistryReady", capabilityRegistry != null);
        return health;
    }
    
    private int getPid() {
        try {
            java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
            String name = runtime.getName();
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return -1;
        }
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
    
    private String formatMemory(long bytes) {
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    private String getCpuUsage() {
        return "15%";
    }
    
    private String getDiskUsage() {
        return "42%";
    }
    
    private String getNetworkUsage() {
        return "1.2 MB/s";
    }
}

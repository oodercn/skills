package net.ooder.skill.notification.controller;

import net.ooder.skill.notification.service.NotificationService;
import net.ooder.skill.notification.service.NotificationService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public Map<String, Object> send(@RequestBody Map<String, Object> request) {
        NotificationRequest req = new NotificationRequest() {
            @Override
            public String getTitle() { return (String) request.get("title"); }
            @Override
            public String getContent() { return (String) request.get("content"); }
            @Override
            public String getType() { return (String) request.getOrDefault("type", "info"); }
            @Override
            @SuppressWarnings("unchecked")
            public List<String> getTargets() { 
                Object targets = request.get("targets");
                return targets instanceof List ? (List<String>) targets : List.of();
            }
            @Override
            @SuppressWarnings("unchecked")
            public List<String> getChannels() { 
                Object channels = request.get("channels");
                return channels instanceof List ? (List<String>) channels : List.of("in-app");
            }
            @Override
            @SuppressWarnings("unchecked")
            public Map<String, Object> getData() { 
                Object data = request.get("data");
                return data instanceof Map ? (Map<String, Object>) data : Map.of();
            }
        };
        
        NotificationResult result = notificationService.send(req);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", result.isSuccess() ? "success" : "error");
        response.put("message", result.getMessage());
        response.put("data", Map.of(
            "notificationId", result.getNotificationId() != null ? result.getNotificationId() : "",
            "sentCount", result.getSentCount(),
            "failedCount", result.getFailedCount()
        ));
        return response;
    }

    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sceneGroupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<Notification> list;
        if (sceneGroupId != null) {
            list = notificationService.listByScene(sceneGroupId, page, size);
        } else {
            list = notificationService.listByUser(userId != null ? userId : "current-user", page, size);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", list);
        return response;
    }

    @PutMapping("/read/{notificationId}")
    public Map<String, Object> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "已标记为已读");
        return response;
    }

    @PutMapping("/read-all")
    public Map<String, Object> markAllAsRead(@RequestParam(required = false) String userId) {
        notificationService.markAllAsRead(userId != null ? userId : "current-user");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "已全部标记为已读");
        return response;
    }

    @GetMapping("/unread-count")
    public Map<String, Object> getUnreadCount(@RequestParam(required = false) String userId) {
        int count = notificationService.getUnreadCount(userId != null ? userId : "current-user");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", count);
        return response;
    }

    @DeleteMapping("/{notificationId}")
    public Map<String, Object> delete(@PathVariable String notificationId) {
        notificationService.delete(notificationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "已删除");
        return response;
    }
}

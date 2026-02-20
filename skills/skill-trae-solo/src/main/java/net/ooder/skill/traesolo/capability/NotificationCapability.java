package net.ooder.skill.traesolo.capability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/capability/notification")
public class NotificationCapability {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationCapability.class);
    private static final String GITHUB_ROOT = "E:\\github";
    private static final String NOTIFICATIONS_PATH = GITHUB_ROOT + "\\.shared\\notifications";
    
    @PostMapping
    public Map<String, Object> execute(@RequestBody Map<String, Object> request) {
        log.info("Notification request: {}", request);
        
        String operation = (String) request.getOrDefault("operation", "list");
        
        try {
            Object result;
            switch (operation) {
                case "list":
                    result = listNotifications(request);
                    break;
                case "get":
                    String notifId = (String) request.get("notificationId");
                    result = getNotification(notifId);
                    break;
                case "send":
                    result = sendNotification(request);
                    break;
                case "acknowledge":
                    result = acknowledgeNotification(request);
                    break;
                case "archive":
                    result = archiveNotification(request);
                    break;
                default:
                    return error("INVALID_OPERATION", "Unknown operation: " + operation);
            }
            return success(result);
        } catch (Exception e) {
            return error("EXECUTION_ERROR", e.getMessage());
        }
    }
    
    private List<Map<String, Object>> listNotifications(Map<String, Object> params) {
        List<Map<String, Object>> notifications = new ArrayList<Map<String, Object>>();
        
        String projectCode = (String) params.get("projectCode");
        String type = (String) params.get("type");
        
        File notifDir = new File(NOTIFICATIONS_PATH);
        if (!notifDir.exists()) {
            notifDir.mkdirs();
            return notifications;
        }
        
        File[] monthDirs = notifDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && !file.getName().startsWith(".");
            }
        });
        
        if (monthDirs == null) return notifications;
        
        for (File monthDir : monthDirs) {
            File[] files = monthDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (!name.endsWith(".md")) return false;
                    
                    if (projectCode != null && !name.startsWith(projectCode + "-NOTIF-")) {
                        return false;
                    }
                    
                    return true;
                }
            });
            
            if (files != null) {
                for (File file : files) {
                    Map<String, Object> notif = parseNotificationFile(file);
                    if (notif != null) {
                        if (type == null || type.equals(notif.get("type"))) {
                            notifications.add(notif);
                        }
                    }
                }
            }
        }
        
        return notifications;
    }
    
    private Map<String, Object> getNotification(String notificationId) {
        if (notificationId == null || notificationId.isEmpty()) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "notificationId is required");
            return error;
        }
        
        File notifDir = new File(NOTIFICATIONS_PATH);
        if (!notifDir.exists()) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "Notification not found: " + notificationId);
            return error;
        }
        
        for (File monthDir : notifDir.listFiles()) {
            if (!monthDir.isDirectory()) continue;
            
            for (File file : monthDir.listFiles()) {
                if (file.getName().contains(notificationId)) {
                    return parseNotificationFile(file);
                }
            }
        }
        
        Map<String, Object> error = new LinkedHashMap<String, Object>();
        error.put("error", "Notification not found: " + notificationId);
        return error;
    }
    
    private Map<String, Object> sendNotification(Map<String, Object> params) {
        String sender = (String) params.get("sender");
        String recipients = (String) params.get("recipients");
        String type = (String) params.getOrDefault("type", "INFO");
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        String impact = (String) params.get("impact");
        String action = (String) params.get("action");
        
        if (sender == null || title == null) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "sender and title are required");
            return error;
        }
        
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String monthDir = new SimpleDateFormat("yyyy-MM").format(new Date());
        
        String notificationId = generateNotificationId(sender);
        
        StringBuilder sb = new StringBuilder();
        sb.append("# 通知：").append(title).append("\n\n");
        sb.append("**通知编号**: ").append(notificationId).append("\n");
        sb.append("**类型**: ").append(type).append("\n");
        sb.append("**发送者**: ").append(sender).append("\n");
        sb.append("**接收者**: ").append(recipients != null ? recipients : "ALL").append("\n");
        sb.append("**发送时间**: ").append(timestamp).append("\n");
        sb.append("**有效期**: \n\n");
        sb.append("---\n\n");
        sb.append("## 通知内容\n");
        sb.append(content != null ? content : "").append("\n\n");
        sb.append("## 影响范围\n");
        sb.append(impact != null ? impact : "").append("\n\n");
        sb.append("## 行动要求\n");
        sb.append(action != null ? action : "").append("\n\n");
        sb.append("---\n");
        sb.append("**确认回执**\n\n");
        sb.append("| 工程 | 确认人 | 确认时间 |\n");
        sb.append("|------|--------|----------|\n");
        
        try {
            File monthDirectory = new File(NOTIFICATIONS_PATH, monthDir);
            if (!monthDirectory.exists()) {
                monthDirectory.mkdirs();
            }
            
            File notifFile = new File(monthDirectory, notificationId + ".md");
            FileWriter writer = new FileWriter(notifFile);
            writer.write(sb.toString());
            writer.close();
            
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("success", true);
            result.put("notificationId", notificationId);
            result.put("path", notifFile.getAbsolutePath());
            return result;
        } catch (IOException e) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "Failed to send notification: " + e.getMessage());
            return error;
        }
    }
    
    private Map<String, Object> acknowledgeNotification(Map<String, Object> params) {
        String notificationId = (String) params.get("notificationId");
        String projectCode = (String) params.get("projectCode");
        String acknowledgedBy = (String) params.get("acknowledgedBy");
        
        if (notificationId == null || projectCode == null) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "notificationId and projectCode are required");
            return error;
        }
        
        File notifFile = findNotificationFile(notificationId);
        if (notifFile == null) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "Notification not found: " + notificationId);
            return error;
        }
        
        try {
            String content = readFile(notifFile);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            String ackLine = "| " + projectCode + " | " + 
                (acknowledgedBy != null ? acknowledgedBy : "system") + " | " + 
                timestamp + " |\n";
            
            int lastRowIndex = content.lastIndexOf("|------|--------|----------|");
            if (lastRowIndex > 0) {
                int insertPos = content.indexOf("\n", lastRowIndex) + 1;
                content = content.substring(0, insertPos) + ackLine + content.substring(insertPos);
            }
            
            FileWriter writer = new FileWriter(notifFile);
            writer.write(content);
            writer.close();
            
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("success", true);
            result.put("notificationId", notificationId);
            return result;
        } catch (IOException e) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "Failed to acknowledge notification: " + e.getMessage());
            return error;
        }
    }
    
    private Map<String, Object> archiveNotification(Map<String, Object> params) {
        String notificationId = (String) params.get("notificationId");
        
        if (notificationId == null) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "notificationId is required");
            return error;
        }
        
        File notifFile = findNotificationFile(notificationId);
        if (notifFile == null) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "Notification not found: " + notificationId);
            return error;
        }
        
        File archiveDir = new File(NOTIFICATIONS_PATH, "archived");
        if (!archiveDir.exists()) {
            archiveDir.mkdirs();
        }
        
        File destFile = new File(archiveDir, notifFile.getName());
        if (notifFile.renameTo(destFile)) {
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("success", true);
            result.put("notificationId", notificationId);
            return result;
        } else {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "Failed to archive notification");
            return error;
        }
    }
    
    private String generateNotificationId(String sender) {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        
        int nextNum = 1;
        String monthDir = new SimpleDateFormat("yyyy-MM").format(new Date());
        File monthDirectory = new File(NOTIFICATIONS_PATH, monthDir);
        
        if (monthDirectory.exists()) {
            File[] files = monthDirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(sender + "-NOTIF-" + date);
                }
            });
            
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    String numPart = name.replace(sender + "-NOTIF-" + date + "-", "").replace(".md", "");
                    try {
                        int num = Integer.parseInt(numPart);
                        if (num >= nextNum) {
                            nextNum = num + 1;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        
        return sender + "-NOTIF-" + date + "-" + String.format("%03d", nextNum);
    }
    
    private File findNotificationFile(String notificationId) {
        File notifDir = new File(NOTIFICATIONS_PATH);
        if (!notifDir.exists()) return null;
        
        for (File monthDir : notifDir.listFiles()) {
            if (!monthDir.isDirectory()) continue;
            
            for (File file : monthDir.listFiles()) {
                if (file.getName().contains(notificationId) || 
                    file.getName().equals(notificationId + ".md")) {
                    return file;
                }
            }
        }
        
        return null;
    }
    
    private Map<String, Object> parseNotificationFile(File file) {
        Map<String, Object> notif = new LinkedHashMap<String, Object>();
        
        try {
            String content = readFile(file);
            
            notif.put("notificationId", extractValue(content, "通知编号"));
            notif.put("title", extractTitle(content));
            notif.put("type", extractValue(content, "类型"));
            notif.put("sender", extractValue(content, "发送者"));
            notif.put("recipients", extractValue(content, "接收者"));
            notif.put("sentTime", extractValue(content, "发送时间"));
            notif.put("path", file.getAbsolutePath());
            
        } catch (IOException e) {
            notif.put("error", "Failed to parse notification file");
        }
        
        return notif;
    }
    
    private String extractValue(String content, String field) {
        int start = content.indexOf("**" + field + "**:");
        if (start < 0) return "";
        
        start = content.indexOf(":", start) + 1;
        int end = content.indexOf("\n", start);
        if (end < 0) end = content.length();
        
        return content.substring(start, end).trim();
    }
    
    private String extractTitle(String content) {
        int start = content.indexOf("# 通知：");
        if (start < 0) return "";
        
        start += 5;
        int end = content.indexOf("\n", start);
        if (end < 0) end = content.length();
        
        return content.substring(start, end).trim();
    }
    
    private String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
    
    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("data", data);
        return result;
    }
    
    private Map<String, Object> error(String code, String message) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", false);
        result.put("errorCode", code);
        result.put("errorMessage", message);
        return result;
    }
}

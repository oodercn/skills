package net.ooder.skill.traesolo.capability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/capability/task-management")
public class TaskManagementCapability {
    
    private static final Logger log = LoggerFactory.getLogger(TaskManagementCapability.class);
    private static final String GITHUB_ROOT = "E:\\github";
    private static final String TASKS_PATH = GITHUB_ROOT + "\\.shared\\tasks\\active";
    private static final String ARCHIVED_PATH = GITHUB_ROOT + "\\.shared\\tasks\\archived";
    
    @PostMapping
    public Map<String, Object> execute(@RequestBody Map<String, Object> request) {
        log.info("Task management request: {}", request);
        
        String operation = (String) request.getOrDefault("operation", "list");
        
        try {
            Object result;
            switch (operation) {
                case "list":
                    result = listTasks();
                    break;
                case "get":
                    String taskId = (String) request.get("taskId");
                    result = getTask(taskId);
                    break;
                case "create":
                    result = createTask(request);
                    break;
                case "update":
                    result = updateTask(request);
                    break;
                case "progress":
                    result = updateProgress(request);
                    break;
                default:
                    return error("INVALID_OPERATION", "Unknown operation: " + operation);
            }
            return success(result);
        } catch (Exception e) {
            return error("EXECUTION_ERROR", e.getMessage());
        }
    }
    
    private List<Map<String, Object>> listTasks() {
        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        File taskDir = new File(TASKS_PATH);
        
        if (!taskDir.exists()) {
            taskDir.mkdirs();
            return tasks;
        }
        
        File[] files = taskDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".md");
            }
        });
        
        if (files == null) return tasks;
        
        for (File file : files) {
            Map<String, Object> task = parseTaskFile(file);
            if (task != null) {
                tasks.add(task);
            }
        }
        
        return tasks;
    }
    
    private Map<String, Object> getTask(String taskId) {
        if (taskId == null || taskId.isEmpty()) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "taskId is required");
            return error;
        }
        
        File taskFile = new File(TASKS_PATH, "TASK_" + taskId + ".md");
        if (!taskFile.exists()) {
            File archivedFile = new File(ARCHIVED_PATH, "TASK_" + taskId + ".md");
            if (archivedFile.exists()) {
                return parseTaskFile(archivedFile);
            }
            
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "Task not found: " + taskId);
            return error;
        }
        
        return parseTaskFile(taskFile);
    }
    
    private Map<String, Object> createTask(Map<String, Object> params) {
        String projectCode = (String) params.get("projectCode");
        String title = (String) params.get("title");
        String description = (String) params.get("description");
        String priority = (String) params.getOrDefault("priority", "P2");
        String assignee = (String) params.get("assignee");
        
        if (projectCode == null || title == null) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "projectCode and title are required");
            return error;
        }
        
        String taskId = generateTaskId(projectCode);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        StringBuilder content = new StringBuilder();
        content.append("# 任务：").append(title).append("\n\n");
        content.append("**任务编号**: ").append(taskId).append("\n");
        content.append("**优先级**: ").append(priority).append("\n");
        content.append("**状态**: PENDING\n");
        content.append("**负责人**: ").append(assignee != null ? assignee : "").append("\n");
        content.append("**创建日期**: ").append(date).append("\n");
        content.append("**计划完成**: \n");
        content.append("**实际完成**: \n\n");
        content.append("---\n\n");
        content.append("## 任务描述\n");
        content.append(description != null ? description : "").append("\n\n");
        content.append("## 子任务\n\n");
        content.append("| 编号 | 任务 | 状态 | 负责人 |\n");
        content.append("|------|------|------|--------|\n\n");
        content.append("## 进度记录\n\n");
        content.append("| 日期 | 进度 | 说明 |\n");
        content.append("|------|------|------|\n");
        content.append("| ").append(date).append(" | 0% | 任务创建 |\n\n");
        content.append("## 关联文档\n\n");
        content.append("## 变更记录\n\n");
        content.append("| 版本 | 日期 | 变更说明 |\n");
        content.append("|------|------|---------|\n");
        content.append("| 1.0 | ").append(date).append(" | 初始创建 |\n");
        
        try {
            File taskDir = new File(TASKS_PATH);
            if (!taskDir.exists()) {
                taskDir.mkdirs();
            }
            
            File taskFile = new File(taskDir, "TASK_" + taskId + ".md");
            FileWriter writer = new FileWriter(taskFile);
            writer.write(content.toString());
            writer.close();
            
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("success", true);
            result.put("taskId", taskId);
            result.put("path", taskFile.getAbsolutePath());
            return result;
        } catch (IOException e) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "Failed to create task: " + e.getMessage());
            return error;
        }
    }
    
    private Map<String, Object> updateTask(Map<String, Object> params) {
        String taskId = (String) params.get("taskId");
        String status = (String) params.get("status");
        String progress = (String) params.get("progress");
        String note = (String) params.get("note");
        
        if (taskId == null) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "taskId is required");
            return error;
        }
        
        File taskFile = new File(TASKS_PATH, "TASK_" + taskId + ".md");
        if (!taskFile.exists()) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "Task not found: " + taskId);
            return error;
        }
        
        try {
            String content = readFile(taskFile);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            
            if (status != null) {
                content = content.replaceFirst("\\*\\*状态\\*\\*: \\w+", "**状态**: " + status);
            }
            
            if (progress != null || note != null) {
                String progressLine = "| " + date + " | " + 
                    (progress != null ? progress : "-") + " | " + 
                    (note != null ? note : "-") + " |\n";
                
                int lastRowIndex = content.lastIndexOf("|------|------|------|");
                if (lastRowIndex > 0) {
                    int insertPos = content.indexOf("\n", lastRowIndex) + 1;
                    content = content.substring(0, insertPos) + progressLine + content.substring(insertPos);
                }
            }
            
            FileWriter writer = new FileWriter(taskFile);
            writer.write(content);
            writer.close();
            
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("success", true);
            result.put("taskId", taskId);
            return result;
        } catch (IOException e) {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", "Failed to update task: " + e.getMessage());
            return error;
        }
    }
    
    private Map<String, Object> updateProgress(Map<String, Object> params) {
        return updateTask(params);
    }
    
    private String generateTaskId(String projectCode) {
        int nextNum = 1;
        File taskDir = new File(TASKS_PATH);
        
        if (taskDir.exists()) {
            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            String prefix = projectCode + "-" + year + "-";
            
            File[] files = taskDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith("TASK_" + prefix);
                }
            });
            
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    String numPart = name.replace("TASK_" + prefix, "").replace(".md", "");
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
        
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        return projectCode + "-" + year + "-" + String.format("%04d", nextNum);
    }
    
    private Map<String, Object> parseTaskFile(File file) {
        Map<String, Object> task = new LinkedHashMap<String, Object>();
        
        try {
            String content = readFile(file);
            
            task.put("taskId", extractValue(content, "任务编号"));
            task.put("title", extractTitle(content));
            task.put("priority", extractValue(content, "优先级"));
            task.put("status", extractValue(content, "状态"));
            task.put("assignee", extractValue(content, "负责人"));
            task.put("createdDate", extractValue(content, "创建日期"));
            task.put("path", file.getAbsolutePath());
            
        } catch (IOException e) {
            task.put("error", "Failed to parse task file");
        }
        
        return task;
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
        int start = content.indexOf("# 任务：");
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

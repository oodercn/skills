package net.ooder.skill.cmd.service.impl;

import net.ooder.skill.cmd.dto.*;
import net.ooder.skill.cmd.service.CmdService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CmdServiceImpl implements CmdService {

    private final Map<String, Command> commands = new ConcurrentHashMap<>();
    private final Map<String, Task> tasks = new ConcurrentHashMap<>();
    private final Map<String, List<CommandLog>> commandLogs = new ConcurrentHashMap<>();

    @Override
    public Command dispatch(Command command) {
        if (command.getCommandId() == null || command.getCommandId().isEmpty()) {
            command.setCommandId("cmd-" + UUID.randomUUID().toString().substring(0, 8));
        }
        command.setCreateTime(System.currentTimeMillis());
        command.setStatus("dispatched");
        commands.put(command.getCommandId(), command);
        
        addLog(command.getCommandId(), command.getAgentId(), "info", "Command dispatched");
        return command;
    }

    @Override
    public List<Command> batchDispatch(List<Command> commandList) {
        List<Command> results = new ArrayList<>();
        for (Command cmd : commandList) {
            results.add(dispatch(cmd));
        }
        return results;
    }

    @Override
    public Command getCommand(String commandId) {
        return commands.get(commandId);
    }

    @Override
    public boolean cancelCommand(String commandId) {
        Command command = commands.get(commandId);
        if (command == null) {
            return false;
        }
        if ("pending".equals(command.getStatus()) || "dispatched".equals(command.getStatus())) {
            command.setStatus("cancelled");
            command.setEndTime(System.currentTimeMillis());
            addLog(commandId, command.getAgentId(), "info", "Command cancelled");
            return true;
        }
        return false;
    }

    @Override
    public Command retryCommand(String commandId) {
        Command command = commands.get(commandId);
        if (command == null) {
            return null;
        }
        if (command.getRetryCount() < command.getMaxRetry()) {
            command.setRetryCount(command.getRetryCount() + 1);
            command.setStatus("dispatched");
            command.setStartTime(0);
            command.setEndTime(0);
            command.setErrorMessage(null);
            addLog(commandId, command.getAgentId(), "info", "Command retry #" + command.getRetryCount());
        }
        return command;
    }

    @Override
    public List<Command> getCommands(String agentId, String status, int page, int size) {
        List<Command> filtered = new ArrayList<>();
        for (Command cmd : commands.values()) {
            boolean match = true;
            if (agentId != null && !agentId.equals(cmd.getAgentId())) {
                match = false;
            }
            if (status != null && !status.equals(cmd.getStatus())) {
                match = false;
            }
            if (match) {
                filtered.add(cmd);
            }
        }
        filtered.sort((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()));
        int start = page * size;
        int end = Math.min(start + size, filtered.size());
        return start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>();
    }

    @Override
    public List<Task> listTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task createTask(Task task) {
        if (task.getTaskId() == null || task.getTaskId().isEmpty()) {
            task.setTaskId("task-" + UUID.randomUUID().toString().substring(0, 8));
        }
        task.setCreateTime(System.currentTimeMillis());
        task.setUpdateTime(System.currentTimeMillis());
        tasks.put(task.getTaskId(), task);
        return task;
    }

    @Override
    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    @Override
    public boolean deleteTask(String taskId) {
        return tasks.remove(taskId) != null;
    }

    @Override
    public boolean enableTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return false;
        }
        task.setEnabled(true);
        task.setStatus("active");
        task.setUpdateTime(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean disableTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return false;
        }
        task.setEnabled(false);
        task.setStatus("disabled");
        task.setUpdateTime(System.currentTimeMillis());
        return true;
    }

    @Override
    public Task updateTask(Task task) {
        Task existing = tasks.get(task.getTaskId());
        if (existing == null) {
            return null;
        }
        task.setUpdateTime(System.currentTimeMillis());
        tasks.put(task.getTaskId(), task);
        return task;
    }

    @Override
    public List<CommandLog> getCommandLogs(String commandId, String level, int page, int size) {
        List<CommandLog> logs = commandLogs.get(commandId);
        if (logs == null) {
            return new ArrayList<>();
        }
        List<CommandLog> filtered = new ArrayList<>();
        for (CommandLog log : logs) {
            if (level == null || level.equals(log.getLevel())) {
                filtered.add(log);
            }
        }
        int start = page * size;
        int end = Math.min(start + size, filtered.size());
        return start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>();
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCommands", commands.size());
        stats.put("totalTasks", tasks.size());
        
        Map<String, Long> statusCounts = new HashMap<>();
        for (Command cmd : commands.values()) {
            statusCounts.merge(cmd.getStatus(), 1L, Long::sum);
        }
        stats.put("commandStatusCounts", statusCounts);
        
        long pendingCount = commands.values().stream()
                .filter(c -> "pending".equals(c.getStatus()) || "dispatched".equals(c.getStatus()))
                .count();
        stats.put("pendingCommands", pendingCount);
        
        return stats;
    }
    
    private void addLog(String commandId, String agentId, String level, String message) {
        CommandLog log = new CommandLog();
        log.setCommandId(commandId);
        log.setAgentId(agentId);
        log.setLevel(level);
        log.setMessage(message);
        
        commandLogs.computeIfAbsent(commandId, k -> new ArrayList<>()).add(log);
    }
}

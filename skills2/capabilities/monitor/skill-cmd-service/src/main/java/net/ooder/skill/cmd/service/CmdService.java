package net.ooder.skill.cmd.service;

import net.ooder.skill.cmd.dto.*;

import java.util.List;
import java.util.Map;

public interface CmdService {
    // Command operations
    Command dispatch(Command command);
    List<Command> batchDispatch(List<Command> commands);
    Command getCommand(String commandId);
    boolean cancelCommand(String commandId);
    Command retryCommand(String commandId);
    List<Command> getCommands(String agentId, String status, int page, int size);
    
    // Task operations
    List<Task> listTasks();
    Task createTask(Task task);
    Task getTask(String taskId);
    boolean deleteTask(String taskId);
    boolean enableTask(String taskId);
    boolean disableTask(String taskId);
    Task updateTask(Task task);
    
    // Logs and statistics
    List<CommandLog> getCommandLogs(String commandId, String level, int page, int size);
    Map<String, Object> getStatistics();
}

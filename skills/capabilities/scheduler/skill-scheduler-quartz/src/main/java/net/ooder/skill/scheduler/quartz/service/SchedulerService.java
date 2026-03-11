package net.ooder.skill.scheduler.quartz.service;

import net.ooder.skill.scheduler.quartz.dto.*;

import java.util.List;
import java.util.Map;

public interface SchedulerService {
    String schedule(String taskName, String cronExpression, Map<String, Object> taskData, Map<String, Object> options);
    boolean cancel(String taskId);
    boolean pause(String taskId);
    boolean resume(String taskId);
    TaskInfo getTask(String taskId);
    TaskListResult listTasks(String status, int page, int pageSize);
    boolean triggerNow(String taskId);
    boolean updateCron(String taskId, String cronExpression);
    List<TaskExecution> getExecutionHistory(String taskId, int limit);
}

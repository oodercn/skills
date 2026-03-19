package net.ooder.skill.scheduler.quartz.dto;

import lombok.Data;
import java.util.List;

@Data
public class TaskListResult {
    private List<TaskInfo> tasks;
    private int total;
    private int page;
    private int pageSize;
    
    public TaskListResult(List<TaskInfo> tasks, int total) {
        this.tasks = tasks;
        this.total = total;
    }
}

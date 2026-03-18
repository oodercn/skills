package net.ooder.skill.scheduler.quartz.controller;

import net.ooder.skill.scheduler.quartz.dto.*;
import net.ooder.skill.scheduler.quartz.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {

    @Autowired
    private SchedulerService schedulerService;

    @PostMapping("/schedule")
    public ResponseEntity<SchedulerResult> schedule(@RequestBody ScheduleRequest request) {
        String taskId = schedulerService.schedule(
            request.getTaskName(),
            request.getCronExpression(),
            request.getTaskData(),
            request.getOptions()
        );
        return ResponseEntity.ok(SchedulerResult.success(taskId));
    }

    @PostMapping("/cancel/{taskId}")
    public ResponseEntity<SchedulerResult> cancel(@PathVariable String taskId) {
        boolean result = schedulerService.cancel(taskId);
        if (result) {
            return ResponseEntity.ok(SchedulerResult.success("Task cancelled", taskId));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/pause/{taskId}")
    public ResponseEntity<SchedulerResult> pause(@PathVariable String taskId) {
        boolean result = schedulerService.pause(taskId);
        if (result) {
            return ResponseEntity.ok(SchedulerResult.success("Task paused", taskId));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/resume/{taskId}")
    public ResponseEntity<SchedulerResult> resume(@PathVariable String taskId) {
        boolean result = schedulerService.resume(taskId);
        if (result) {
            return ResponseEntity.ok(SchedulerResult.success("Task resumed", taskId));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/trigger/{taskId}")
    public ResponseEntity<SchedulerResult> triggerNow(@PathVariable String taskId) {
        boolean result = schedulerService.triggerNow(taskId);
        if (result) {
            return ResponseEntity.ok(SchedulerResult.success("Task triggered", taskId));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskInfo> getTask(@PathVariable String taskId) {
        TaskInfo task = schedulerService.getTask(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<TaskListResult> listTasks(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(schedulerService.listTasks(status, page, pageSize));
    }

    @PutMapping("/tasks/{taskId}/cron")
    public ResponseEntity<SchedulerResult> updateCron(
            @PathVariable String taskId,
            @RequestBody Map<String, String> request) {
        String cronExpression = request.get("cronExpression");
        if (cronExpression == null || cronExpression.isEmpty()) {
            return ResponseEntity.badRequest().body(SchedulerResult.fail("cronExpression is required"));
        }
        boolean result = schedulerService.updateCron(taskId, cronExpression);
        if (result) {
            return ResponseEntity.ok(SchedulerResult.success("Cron updated", taskId));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/history/{taskId}")
    public ResponseEntity<List<TaskExecution>> getExecutionHistory(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(schedulerService.getExecutionHistory(taskId, limit));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }
}

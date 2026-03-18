package net.ooder.skill.cmd.controller;

import net.ooder.skill.cmd.dto.*;
import net.ooder.skill.cmd.service.CmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/north/task")
public class TaskController {

    @Autowired
    private CmdService cmdService;

    @GetMapping
    public ResponseEntity<List<Task>> listTasks() {
        return ResponseEntity.ok(cmdService.listTasks());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(cmdService.createTask(task));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable String taskId) {
        Task task = cmdService.getTask(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Boolean> deleteTask(@PathVariable String taskId) {
        return ResponseEntity.ok(cmdService.deleteTask(taskId));
    }

    @PostMapping("/{taskId}/enable")
    public ResponseEntity<Boolean> enableTask(@PathVariable String taskId) {
        return ResponseEntity.ok(cmdService.enableTask(taskId));
    }

    @PostMapping("/{taskId}/disable")
    public ResponseEntity<Boolean> disableTask(@PathVariable String taskId) {
        return ResponseEntity.ok(cmdService.disableTask(taskId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable String taskId, @RequestBody Task task) {
        task.setTaskId(taskId);
        Task updated = cmdService.updateTask(task);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}

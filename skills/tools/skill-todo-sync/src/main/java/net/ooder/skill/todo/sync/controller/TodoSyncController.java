package net.ooder.skill.todo.sync.controller;

import net.ooder.skill.todo.sync.dto.*;
import net.ooder.skill.todo.sync.service.TodoSyncService;
import net.ooder.api.result.ResultModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/todo")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TodoSyncController {

    @Autowired
    private TodoSyncService todoSyncService;

    @PostMapping
    public ResultModel<TodoDTO> createTodo(@RequestBody TodoDTO todo) {
        log.info("Creating todo: {}", todo.getTitle());
        TodoDTO created = todoSyncService.createTodo(todo);
        return ResultModel.success(created);
    }

    @GetMapping("/{todoId}")
    public ResultModel<TodoDTO> getTodo(@PathVariable String todoId) {
        TodoDTO todo = todoSyncService.getTodo(todoId);
        return ResultModel.success(todo);
    }

    @PutMapping("/{todoId}")
    public ResultModel<TodoDTO> updateTodo(
            @PathVariable String todoId,
            @RequestBody TodoDTO todo) {
        TodoDTO updated = todoSyncService.updateTodo(todoId, todo);
        return ResultModel.success(updated);
    }

    @DeleteMapping("/{todoId}")
    public ResultModel<Boolean> deleteTodo(
            @PathVariable String todoId,
            @RequestParam(required = false) String platform) {
        boolean result = todoSyncService.deleteTodo(todoId, platform);
        return ResultModel.success(result);
    }

    @PostMapping("/{todoId}/complete")
    public ResultModel<TodoDTO> completeTodo(
            @PathVariable String todoId,
            @RequestParam(required = false) String platform) {
        TodoDTO todo = todoSyncService.completeTodo(todoId, platform);
        return ResultModel.success(todo);
    }

    @PostMapping("/sync")
    public ResultModel<TodoSyncResultDTO> syncTodos(
            @RequestParam String userId,
            @RequestParam String platform) {
        TodoSyncResultDTO result = todoSyncService.syncTodos(userId, platform);
        return ResultModel.success(result);
    }

    @GetMapping("/list")
    public ResultModel<List<TodoDTO>> listTodos(
            @RequestParam String userId,
            @RequestParam(required = false) String status) {
        List<TodoDTO> todos = todoSyncService.listTodos(userId, status);
        return ResultModel.success(todos);
    }
}

package net.ooder.skill.todo.controller;

import net.ooder.skill.todo.dto.TodoDTO;
import net.ooder.skill.todo.model.PageResult;
import net.ooder.skill.todo.model.ResultModel;
import net.ooder.skill.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/my/todos")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class TodoController {

    private static final Logger log = LoggerFactory.getLogger(TodoController.class);

    @Autowired
    private TodoService todoService;

    @GetMapping
    public ResultModel<PageResult<TodoDTO>> listMyTodos(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String userId = getCurrentUserId(userIdHeader);
        log.info("[TodoController] List todos called for user: {}, status: {}, type: {}", userId, status, type);
        PageResult<TodoDTO> result = todoService.listMyTodos(userId, status, type, pageNum, pageSize);
        return ResultModel.success(result);
    }

    @GetMapping("/pending")
    public ResultModel<List<TodoDTO>> listPendingTodos(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String userId = getCurrentUserId(userIdHeader);
        log.info("[TodoController] List pending todos called for user: {}", userId);
        List<TodoDTO> result = todoService.listPendingTodos(userId);
        return ResultModel.success(result);
    }

    @GetMapping("/count")
    public ResultModel<Map<String, Integer>> countByType(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String userId = getCurrentUserId(userIdHeader);
        log.info("[TodoController] Count todos called for user: {}", userId);
        Map<String, Integer> result = todoService.countByType(userId);
        return ResultModel.success(result);
    }

    @GetMapping("/{todoId}")
    public ResultModel<TodoDTO> getTodo(@PathVariable String todoId) {
        log.info("[TodoController] Get todo called: {}", todoId);
        TodoDTO todo = todoService.get(todoId);
        if (todo == null) {
            return ResultModel.notFound("Todo not found: " + todoId);
        }
        return ResultModel.success(todo);
    }

    @PostMapping
    public ResultModel<TodoDTO> createTodo(
            @RequestBody TodoDTO todo,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String userId = getCurrentUserId(userIdHeader);
        log.info("[TodoController] Create todo called for user: {}", userId);
        TodoDTO created = todoService.create(userId, todo);
        return ResultModel.success(created);
    }

    @PutMapping("/{todoId}")
    public ResultModel<TodoDTO> updateTodo(
            @PathVariable String todoId,
            @RequestBody TodoDTO todo) {
        log.info("[TodoController] Update todo called: {}", todoId);
        TodoDTO updated = todoService.update(todoId, todo);
        if (updated == null) {
            return ResultModel.notFound("Todo not found: " + todoId);
        }
        return ResultModel.success(updated);
    }

    @DeleteMapping("/{todoId}")
    public ResultModel<Boolean> deleteTodo(@PathVariable String todoId) {
        log.info("[TodoController] Delete todo called: {}", todoId);
        boolean result = todoService.delete(todoId);
        if (!result) {
            return ResultModel.notFound("Todo not found: " + todoId);
        }
        return ResultModel.success(true);
    }

    @PostMapping("/{todoId}/complete")
    public ResultModel<TodoDTO> completeTodo(@PathVariable String todoId) {
        log.info("[TodoController] Complete todo called: {}", todoId);
        TodoDTO completed = todoService.complete(todoId);
        if (completed == null) {
            return ResultModel.notFound("Todo not found: " + todoId);
        }
        return ResultModel.success(completed);
    }

    @PostMapping("/{todoId}/process")
    public ResultModel<TodoDTO> processTodo(
            @PathVariable String todoId,
            @RequestBody(required = false) Map<String, Object> actionData) {
        log.info("[TodoController] Process todo called: {}", todoId);
        if (actionData == null) {
            actionData = new java.util.HashMap<>();
        }
        TodoDTO processed = todoService.process(todoId, actionData);
        if (processed == null) {
            return ResultModel.notFound("Todo not found: " + todoId);
        }
        return ResultModel.success(processed);
    }

    @PostMapping("/{todoId}/accept")
    public ResultModel<TodoDTO> acceptTodo(@PathVariable String todoId) {
        log.info("[TodoController] Accept todo called: {}", todoId);
        TodoDTO accepted = todoService.accept(todoId);
        if (accepted == null) {
            return ResultModel.notFound("Todo not found: " + todoId);
        }
        return ResultModel.success(accepted);
    }

    @PostMapping("/{todoId}/reject")
    public ResultModel<TodoDTO> rejectTodo(
            @PathVariable String todoId,
            @RequestBody(required = false) Map<String, Object> reason) {
        log.info("[TodoController] Reject todo called: {}", todoId);
        TodoDTO rejected = todoService.reject(todoId, reason);
        if (rejected == null) {
            return ResultModel.notFound("Todo not found: " + todoId);
        }
        return ResultModel.success(rejected);
    }

    private String getCurrentUserId(String userIdHeader) {
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            return userIdHeader;
        }
        return "current-user";
    }
}
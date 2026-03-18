package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.todo.TodoDTO;
import net.ooder.mvp.skill.scene.model.ResultModel;
import net.ooder.mvp.skill.scene.service.TodoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/my/todos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping
    public ResultModel<PageResult<TodoDTO>> listMyTodos(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        String currentUserId = "current-user";
        PageResult<TodoDTO> result = todoService.listMyTodos(currentUserId, status, pageNum, pageSize);
        return ResultModel.success(result);
    }

    @GetMapping("/{todoId}")
    public ResultModel<TodoDTO> getTodo(@PathVariable String todoId) {
        TodoDTO todo = todoService.getTodo(todoId);
        if (todo == null) {
            return ResultModel.notFound("待办不存在: " + todoId);
        }
        return ResultModel.success(todo);
    }

    @PostMapping("/{todoId}/accept")
    public ResultModel<Boolean> acceptTodo(@PathVariable String todoId) {
        String currentUserId = "current-user";
        boolean result = todoService.acceptTodo(currentUserId, todoId);
        if (result) {
            return ResultModel.success(true);
        }
        return ResultModel.error(400, "接受失败");
    }

    @PostMapping("/{todoId}/reject")
    public ResultModel<Boolean> rejectTodo(@PathVariable String todoId) {
        String currentUserId = "current-user";
        boolean result = todoService.rejectTodo(currentUserId, todoId);
        if (result) {
            return ResultModel.success(true);
        }
        return ResultModel.error(400, "拒绝失败");
    }

    @PostMapping("/{todoId}/complete")
    public ResultModel<Boolean> completeTodo(@PathVariable String todoId) {
        String currentUserId = "current-user";
        boolean result = todoService.completeTodo(currentUserId, todoId);
        if (result) {
            return ResultModel.success(true);
        }
        return ResultModel.error(400, "完成失败");
    }

    @PostMapping("/{todoId}/approve")
    public ResultModel<Boolean> approveTodo(@PathVariable String todoId) {
        String currentUserId = "current-user";
        boolean result = todoService.approveTodo(currentUserId, todoId);
        if (result) {
            return ResultModel.success(true);
        }
        return ResultModel.error(400, "审批失败");
    }
}

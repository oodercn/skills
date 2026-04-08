package net.ooder.skill.todo.service;

import net.ooder.skill.todo.dto.TodoDTO;
import net.ooder.skill.todo.model.PageResult;

import java.util.List;
import java.util.Map;

public interface TodoService {

    PageResult<TodoDTO> listMyTodos(String userId, String status, String type, int pageNum, int pageSize);

    List<TodoDTO> listPendingTodos(String userId);

    Map<String, Integer> countByType(String userId);

    TodoDTO get(String todoId);

    TodoDTO create(String userId, TodoDTO todo);

    TodoDTO update(String todoId, TodoDTO todo);

    boolean delete(String todoId);

    TodoDTO complete(String todoId);

    TodoDTO process(String todoId, Map<String, Object> actionData);

    TodoDTO accept(String todoId);

    TodoDTO reject(String todoId, Map<String, Object> reason);
}
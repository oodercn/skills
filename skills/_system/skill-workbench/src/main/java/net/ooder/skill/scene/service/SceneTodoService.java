package net.ooder.skill.scene.service;

import net.ooder.skill.scene.dto.todo.TodoDTO;
import net.ooder.skill.scene.dto.workbench.SceneTodoGroup;
import net.ooder.skill.scene.dto.workbench.WorkbenchDTO;

import java.util.List;
import java.util.Map;

public interface SceneTodoService {

    WorkbenchDTO getWorkbenchData(String userId);

    List<SceneTodoGroup> getSceneTodoGroups(String userId, String status);

    List<TodoDTO> getSceneTodos(String sceneGroupId);

    List<TodoDTO> getMyTodosInScene(String userId, String sceneGroupId);

    boolean processTodo(String userId, String todoId, String action);

    boolean completeTodoWithCallback(String userId, String todoId);

    int batchProcessSceneTodos(String userId, String sceneGroupId, String action);

    Map<String, Integer> getTodoStatistics(String userId);

    Map<String, Object> getSceneTodoStatistics(String sceneGroupId);

    boolean hasPendingTodos(String sceneGroupId, String userId);

    String getNextActionHint(String sceneGroupId, String userId);
}

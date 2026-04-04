package net.ooder.skill.scene.service;

import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.scene.SceneGroupDTO;
import net.ooder.skill.scene.dto.todo.TodoDTO;
import net.ooder.skill.scene.dto.workbench.SceneTodoGroup;
import net.ooder.skill.scene.dto.workbench.WorkbenchDTO;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface SceneTodoService {

    WorkbenchDTO getWorkbenchData(String userId);

    List<SceneTodoGroup> getSceneTodoGroups(String userId, String status);

    List<TodoDTO> getSceneTodos(String sceneGroupId);

    List<TodoDTO> getMyTodosInScene(String userId, String sceneGroupId);

    List<SceneGroupDTO> getMyActiveScenes(String userId);

    TodoDTO createSceneDrivenTodo(String sceneGroupId, String userId,
                                   String type, String title,
                                   Map<String, Object> context,
                                   Consumer<TodoDTO> onComplete);

    boolean completeTodoWithCallback(String userId, String todoId);

    boolean processTodo(String userId, String todoId, String action);

    void syncTodosOnSceneStatusChange(String sceneGroupId, String newStatus);

    Map<String, Integer> getTodoStatistics(String userId);

    Map<String, Object> getSceneTodoStatistics(String sceneGroupId);

    int batchProcessSceneTodos(String userId, String sceneGroupId, String action);

    TodoDTO createActivationTodo(String userId, String installId,
                                  String capabilityId, String capabilityName,
                                  String sceneGroupId);

    TodoDTO createInvitationTodo(String sceneGroupId, String fromUserId,
                                  String toUserId, String role);

    TodoDTO createApprovalTodo(String sceneGroupId, String fromUserId,
                                String toUserId, String title, String description);

    boolean hasPendingTodos(String sceneGroupId, String userId);

    String getNextActionHint(String sceneGroupId, String userId);
}

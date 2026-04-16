package net.ooder.skill.scene.dto.workbench;

import net.ooder.skill.scene.dto.scene.SceneGroupDTO;
import net.ooder.skill.scene.dto.todo.TodoDTO;

import java.util.List;
import java.util.Map;

public class WorkbenchDTO {

    private String userId;
    private List<SceneGroupDTO> activeScenes;
    private List<TodoDTO> pendingTodos;
    private List<SceneTodoGroup> sceneTodoGroups;
    private WorkbenchStatistics statistics;
    private List<QuickAction> quickActions;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<SceneGroupDTO> getActiveScenes() { return activeScenes; }
    public void setActiveScenes(List<SceneGroupDTO> activeScenes) { this.activeScenes = activeScenes; }

    public List<TodoDTO> getPendingTodos() { return pendingTodos; }
    public void setPendingTodos(List<TodoDTO> pendingTodos) { this.pendingTodos = pendingTodos; }

    public List<SceneTodoGroup> getSceneTodoGroups() { return sceneTodoGroups; }
    public void setSceneTodoGroups(List<SceneTodoGroup> sceneTodoGroups) { this.sceneTodoGroups = sceneTodoGroups; }

    public WorkbenchStatistics getStatistics() { return statistics; }
    public void setStatistics(WorkbenchStatistics statistics) { this.statistics = statistics; }

    public List<QuickAction> getQuickActions() { return quickActions; }
    public void setQuickActions(List<QuickAction> quickActions) { this.quickActions = quickActions; }

    public static class WorkbenchStatistics {
        private int activeSceneCount;
        private int pendingTodoCount;
        private int highPriorityTodoCount;
        private int dueTodayCount;
        private int pendingActivationCount;
        private int pendingApprovalCount;
        private Map<String, Integer> todoCountByType;

        public int getActiveSceneCount() { return activeSceneCount; }
        public void setActiveSceneCount(int activeSceneCount) { this.activeSceneCount = activeSceneCount; }

        public int getPendingTodoCount() { return pendingTodoCount; }
        public void setPendingTodoCount(int pendingTodoCount) { this.pendingTodoCount = pendingTodoCount; }

        public int getHighPriorityTodoCount() { return highPriorityTodoCount; }
        public void setHighPriorityTodoCount(int highPriorityTodoCount) { this.highPriorityTodoCount = highPriorityTodoCount; }

        public int getDueTodayCount() { return dueTodayCount; }
        public void setDueTodayCount(int dueTodayCount) { this.dueTodayCount = dueTodayCount; }

        public int getPendingActivationCount() { return pendingActivationCount; }
        public void setPendingActivationCount(int pendingActivationCount) { this.pendingActivationCount = pendingActivationCount; }

        public int getPendingApprovalCount() { return pendingApprovalCount; }
        public void setPendingApprovalCount(int pendingApprovalCount) { this.pendingApprovalCount = pendingApprovalCount; }

        public Map<String, Integer> getTodoCountByType() { return todoCountByType; }
        public void setTodoCountByType(Map<String, Integer> todoCountByType) { this.todoCountByType = todoCountByType; }
    }

    public static class QuickAction {
        private String id;
        private String name;
        private String icon;
        private String url;
        private String type;
        private String sceneGroupId;
        private String todoId;
        private int priority;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }

        public String getTodoId() { return todoId; }
        public void setTodoId(String todoId) { this.todoId = todoId; }

        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }
}

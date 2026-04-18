package net.ooder.skill.scene.dto.workbench;

import net.ooder.skill.scene.dto.scene.SceneGroupDTO;
import net.ooder.skill.scene.dto.todo.TodoDTO;

import java.util.List;

public class SceneTodoGroup {

    private String sceneGroupId;
    private String sceneName;
    private String sceneDescription;
    private String sceneStatus;
    private String sceneIcon;
    private String templateId;
    private String templateName;
    private String myRole;
    private List<TodoDTO> todos;
    private int todoCount;
    private int pendingCount;
    private int highPriorityCount;
    private boolean hasAction;
    private String nextActionHint;
    private Long sceneCreateTime;
    private Long sceneLastUpdateTime;
    private int memberCount;
    private SceneGroupDTO sceneDetail;

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }

    public String getSceneName() { return sceneName; }
    public void setSceneName(String sceneName) { this.sceneName = sceneName; }

    public String getSceneDescription() { return sceneDescription; }
    public void setSceneDescription(String sceneDescription) { this.sceneDescription = sceneDescription; }

    public String getSceneStatus() { return sceneStatus; }
    public void setSceneStatus(String sceneStatus) { this.sceneStatus = sceneStatus; }

    public String getSceneIcon() { return sceneIcon; }
    public void setSceneIcon(String sceneIcon) { this.sceneIcon = sceneIcon; }

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getMyRole() { return myRole; }
    public void setMyRole(String myRole) { this.myRole = myRole; }

    public List<TodoDTO> getTodos() { return todos; }
    public void setTodos(List<TodoDTO> todos) { this.todos = todos; }

    public int getTodoCount() { return todoCount; }
    public void setTodoCount(int todoCount) { this.todoCount = todoCount; }

    public int getPendingCount() { return pendingCount; }
    public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }

    public int getHighPriorityCount() { return highPriorityCount; }
    public void setHighPriorityCount(int highPriorityCount) { this.highPriorityCount = highPriorityCount; }

    public boolean isHasAction() { return hasAction; }
    public void setHasAction(boolean hasAction) { this.hasAction = hasAction; }

    public String getNextActionHint() { return nextActionHint; }
    public void setNextActionHint(String nextActionHint) { this.nextActionHint = nextActionHint; }

    public Long getSceneCreateTime() { return sceneCreateTime; }
    public void setSceneCreateTime(Long sceneCreateTime) { this.sceneCreateTime = sceneCreateTime; }

    public Long getSceneLastUpdateTime() { return sceneLastUpdateTime; }
    public void setSceneLastUpdateTime(Long sceneLastUpdateTime) { this.sceneLastUpdateTime = sceneLastUpdateTime; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public SceneGroupDTO getSceneDetail() { return sceneDetail; }
    public void setSceneDetail(SceneGroupDTO sceneDetail) { this.sceneDetail = sceneDetail; }
}

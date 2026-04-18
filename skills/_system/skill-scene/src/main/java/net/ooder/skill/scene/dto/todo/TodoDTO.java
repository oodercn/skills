package net.ooder.skill.scene.dto.todo;

import java.util.List;
import java.util.Map;

public class TodoDTO {

    private String id;
    private String type;
    private String title;
    private String description;
    private String sceneGroupId;
    private String sceneGroupName;
    private String fromUser;
    private String fromUserName;
    private String toUser;
    private String toUserName;
    private String role;
    private String installId;
    private String capabilityId;
    private String priority;
    private String actionType;
    private Long deadline;
    private Long createTime;
    private String status;
    private Long completedTime;
    private String completedBy;
    private String errorMessage;
    private String sceneStatus;
    private String expectedAction;
    private String contextJson;
    private String callbackUrl;
    private Boolean autoProcess;
    private String processResult;
    private Long processTime;
    private String source;
    private String businessId;
    private String businessType;
    private List<String> tags;
    private Map<String, Object> metadata;

    private String assignee;
    private String creator;
    private Long updateTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }

    public String getSceneGroupName() { return sceneGroupName; }
    public void setSceneGroupName(String sceneGroupName) { this.sceneGroupName = sceneGroupName; }

    public String getFromUser() { return fromUser; }
    public void setFromUser(String fromUser) { this.fromUser = fromUser; }

    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }

    public String getToUser() { return toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }

    public String getToUserName() { return toUserName; }
    public void setToUserName(String toUserName) { this.toUserName = toUserName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getInstallId() { return installId; }
    public void setInstallId(String installId) { this.installId = installId; }

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public Long getDeadline() { return deadline; }
    public void setDeadline(Long deadline) { this.deadline = deadline; }

    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCompletedTime() { return completedTime; }
    public void setCompletedTime(Long completedTime) { this.completedTime = completedTime; }

    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getSceneStatus() { return sceneStatus; }
    public void setSceneStatus(String sceneStatus) { this.sceneStatus = sceneStatus; }

    public String getExpectedAction() { return expectedAction; }
    public void setExpectedAction(String expectedAction) { this.expectedAction = expectedAction; }

    public String getContextJson() { return contextJson; }
    public void setContextJson(String contextJson) { this.contextJson = contextJson; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    public Boolean getAutoProcess() { return autoProcess; }
    public void setAutoProcess(Boolean autoProcess) { this.autoProcess = autoProcess; }

    public String getProcessResult() { return processResult; }
    public void setProcessResult(String processResult) { this.processResult = processResult; }

    public Long getProcessTime() { return processTime; }
    public void setProcessTime(Long processTime) { this.processTime = processTime; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getBusinessId() { return businessId; }
    public void setBusinessId(String businessId) { this.businessId = businessId; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public Long getUpdateTime() { return updateTime; }
    public void setUpdateTime(Long updateTime) { this.updateTime = updateTime; }
}

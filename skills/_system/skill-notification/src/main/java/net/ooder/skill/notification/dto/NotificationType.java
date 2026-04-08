package net.ooder.skill.notification.dto;

public enum NotificationType {
    TODO_INVITATION("todo-invitation", "协作邀请"),
    TODO_DELEGATION("todo-delegation", "领导委派"),
    TODO_REMINDER("todo-reminder", "待办提醒"),
    TODO_APPROVAL("todo-approval", "审批请求"),
    TODO_ACTIVATION("todo-activation", "待激活能力"),
    TODO_SCENE_NOTIFICATION("todo-scene-notification", "场景通知"),
    A2A_MESSAGE("a2a-message", "Agent消息"),
    A2P_MESSAGE("a2p-message", "人员消息"),
    SYSTEM_NOTICE("system-notice", "系统通知"),
    SCENE_EVENT("scene-event", "场景事件");

    private final String code;
    private final String label;

    NotificationType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public static NotificationType fromCode(String code) {
        if (code == null) return null;
        for (NotificationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}

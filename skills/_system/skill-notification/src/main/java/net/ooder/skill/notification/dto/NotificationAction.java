package net.ooder.skill.notification.dto;

public class NotificationAction {
    
    private String action;
    private String label;
    private boolean primary;
    private String confirmMessage;
    private String apiEndpoint;
    private String apiMethod;

    public NotificationAction() {}

    public NotificationAction(String action, String label, boolean primary) {
        this.action = action;
        this.label = label;
        this.primary = primary;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }

    public String getConfirmMessage() { return confirmMessage; }
    public void setConfirmMessage(String confirmMessage) { this.confirmMessage = confirmMessage; }

    public String getApiEndpoint() { return apiEndpoint; }
    public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }

    public String getApiMethod() { return apiMethod; }
    public void setApiMethod(String apiMethod) { this.apiMethod = apiMethod; }

    public static NotificationAction primary(String action, String label) {
        return new NotificationAction(action, label, true);
    }

    public static NotificationAction secondary(String action, String label) {
        return new NotificationAction(action, label, false);
    }
}

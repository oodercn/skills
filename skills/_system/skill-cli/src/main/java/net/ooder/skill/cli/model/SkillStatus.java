package net.ooder.skill.cli.model;

public enum SkillStatus {
    
    INSTALLED("installed"),
    STARTING("starting"),
    RUNNING("running"),
    STOPPING("stopping"),
    STOPPED("stopped"),
    ERROR("error"),
    UNINSTALLING("uninstalling");
    
    private final String value;
    
    SkillStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static SkillStatus fromValue(String value) {
        for (SkillStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return ERROR;
    }
}

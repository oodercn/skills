package net.ooder.skill.cli.model;

public enum SceneStatus {
    
    CREATED("created"),
    STARTING("starting"),
    RUNNING("running"),
    STOPPING("stopping"),
    STOPPED("stopped"),
    ERROR("error");
    
    private final String value;
    
    SceneStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static SceneStatus fromValue(String value) {
        for (SceneStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return ERROR;
    }
}

package net.ooder.bpm.enums.agent;

import net.ooder.annotation.Enumstype;

public enum AgentPerformStatus implements Enumstype {

    WAITING("WAITING", "Waiting"),

    CURRENT("CURRENT", "Executing"),

    FINISH("FINISH", "Completed"),

    ERROR("ERROR", "Error"),

    TIMEOUT("TIMEOUT", "Timeout"),

    DELETE("DELETE", "Deleted"),

    NULL("", "Waiting");

    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    AgentPerformStatus(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type;
    }

    public static AgentPerformStatus fromType(String typeName) {
        for (AgentPerformStatus type : AgentPerformStatus.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return NULL;
    }
}

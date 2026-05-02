package net.ooder.bpm.enums.agent;

import net.ooder.annotation.Enumstype;

public enum AgentType implements Enumstype {

    LLM("LLM", "LLM Agent"),

    TASK("TASK", "Task Agent"),

    EVENT("EVENT", "Event Agent"),

    HYBRID("HYBRID", "Hybrid Agent"),

    COORDINATOR("COORDINATOR", "Coordinator Agent"),

    TOOL("TOOL", "Tool Agent");

    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    AgentType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type;
    }

    public static AgentType fromType(String typeName) {
        for (AgentType type : AgentType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return LLM;
    }
}

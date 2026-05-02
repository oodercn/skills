package net.ooder.bpm.enums.agent;

import net.ooder.annotation.Enumstype;

public enum AgentConditionEnums implements Enumstype {

    CONDITION_WAITEDWORK("CONDITION_WAITEDWORK", "Pending tasks"),

    CONDITION_CURRENTWORK("CONDITION_CURRENTWORK", "Active tasks"),

    CONDITION_JOINWORK("CONDITION_JOINWORK", "Received tasks"),

    CONDITION_OUTWORK("CONDITION_OUTWORK", "Sent tasks"),

    CONDITION_PERFORMWORK("CONDITION_PERFORMWORK", "Executed tasks"),

    CONDITION_MYWORK("CONDITION_MYWORK", "My tasks"),

    CONDITION_COMPLETEDWORK("CONDITION_COMPLETEDWORK", "Completed tasks"),

    CONDITION_ALLWORK("CONDITION_ALLWORK", "All tasks");

    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    AgentConditionEnums(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type;
    }

    public static AgentConditionEnums fromType(String typeName) {
        for (AgentConditionEnums type : AgentConditionEnums.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }
}

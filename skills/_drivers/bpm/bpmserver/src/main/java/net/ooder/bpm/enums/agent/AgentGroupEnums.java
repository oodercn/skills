package net.ooder.bpm.enums.agent;

import net.ooder.annotation.Enumstype;

public enum AgentGroupEnums implements Enumstype {

    PERFORMER("PERFORMER", "Current agent"),

    SPONSOR("SPONSOR", "Initiator agent"),

    MONITOR("MONITOR", "Monitor agent"),

    COORDINATOR("COORDINATOR", "Coordinator agent"),

    HISTORYPERFORMER("HISTORYPERFORMER", "Previous agent"),

    HISSPONSOR("HISSPONSOR", "History initiator"),

    HISTORYMONITOR("HISTORYMONITOR", "History monitor"),

    NORIGHT("NORIGHT", "No group"),

    NULL("", "Visitor");

    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    AgentGroupEnums(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type;
    }

    public static AgentGroupEnums fromType(String typeName) {
        for (AgentGroupEnums type : AgentGroupEnums.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }
}

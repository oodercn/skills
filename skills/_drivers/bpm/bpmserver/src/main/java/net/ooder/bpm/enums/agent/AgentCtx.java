package net.ooder.bpm.enums.agent;

import net.ooder.annotation.CTXContext;

public enum AgentCtx implements CTXContext {

    AGENT_ID("AGENT_ID", "Agent ID"),

    AGENT_TYPE("AGENT_TYPE", "Agent type"),

    PERFORMERS("PERFORMERS", "Agent performer list"),

    MONITORS("MONITORS", "Monitor agent list"),

    COORDINATOR("COORDINATOR", "Coordinator agent"),

    PREVIOUS_AGENT("PREVIOUS_AGENT", "Previous agent"),

    AGENT_GROUP("AGENT_GROUP", "Agent skill group"),

    LLM_CONTEXT("LLM_CONTEXT", "LLM context"),

    MCP_TOOLS("MCP_TOOLS", "MCP tool list"),

    USERID("USERID", "Current user ID"),

    ORGS("ORGS", "Organization"),

    PERMISSION("PERMISSION", "Permission"),

    PERFOMERTYPE("PERFOMERTYPE", "Perform type"),

    ACTIVITYINST_ID("ACTIVITYINST_ID", "Activity instance ID"),

    PROCESSINST_ID("PROCESSINST_ID", "Process instance ID"),

    ACTIVITYINSTHISTORY_ID("ACTIVITYINSTHISTORY_ID", "Activity history ID"),

    PERFORMSEQUENCE("PERFORMSEQUENCE", "Perform sequence"),

    SUSPENDORCOMBINE("SUSPENDORCOMBINE", "Suspend or combine"),

    COMBINABLEACTIVITYINSTS("COMBINABLEACTIVITYINSTS", "Combinable activity instances"),

    CURRENT_ACTIVITYINST("CURRENT_ACTIVITYINST", "Current activity instance"),

    CURRENT_PROCESSINST("CURRENT_PROCESSINST", "Current process instance"),

    CONTEXT("CONTEXT", "Context"),

    CONTEXT_ACTIVITYINSTHISTORY("CONTEXT_ACTIVITYINSTHISTORY", "Activity instance history");

    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    AgentCtx(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type;
    }

    public static AgentCtx fromType(String typeName) {
        for (AgentCtx type : AgentCtx.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }
}

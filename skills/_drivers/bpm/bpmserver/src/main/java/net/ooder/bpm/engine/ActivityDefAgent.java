package net.ooder.bpm.engine;

import java.io.Serializable;

public class ActivityDefAgent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String activityDefId;
    private String agentSelectedId;
    private String agentType;
    private String performType;
    private String performSequence;
    private String canRouteBack;
    private String routeBackMethod;
    private String canTakeBack;
    private String coordinatorId;
    private String llmProvider;
    private String llmModel;
    private String systemPrompt;
    private double temperature;
    private int maxTokens;
    private String mcpTools;
    private String capabilities;
    private String extendedAttributes;

    public ActivityDefAgent() {}

    public String getActivityDefId() { return activityDefId; }
    public void setActivityDefId(String activityDefId) { this.activityDefId = activityDefId; }

    public String getAgentSelectedId() { return agentSelectedId; }
    public void setAgentSelectedId(String agentSelectedId) { this.agentSelectedId = agentSelectedId; }

    public String getAgentType() { return agentType; }
    public void setAgentType(String agentType) { this.agentType = agentType; }

    public String getPerformType() { return performType; }
    public void setPerformType(String performType) { this.performType = performType; }

    public String getPerformSequence() { return performSequence; }
    public void setPerformSequence(String performSequence) { this.performSequence = performSequence; }

    public String getCanRouteBack() { return canRouteBack; }
    public void setCanRouteBack(String canRouteBack) { this.canRouteBack = canRouteBack; }

    public String getRouteBackMethod() { return routeBackMethod; }
    public void setRouteBackMethod(String routeBackMethod) { this.routeBackMethod = routeBackMethod; }

    public String getCanTakeBack() { return canTakeBack; }
    public void setCanTakeBack(String canTakeBack) { this.canTakeBack = canTakeBack; }

    public String getCoordinatorId() { return coordinatorId; }
    public void setCoordinatorId(String coordinatorId) { this.coordinatorId = coordinatorId; }

    public String getLlmProvider() { return llmProvider; }
    public void setLlmProvider(String llmProvider) { this.llmProvider = llmProvider; }

    public String getLlmModel() { return llmModel; }
    public void setLlmModel(String llmModel) { this.llmModel = llmModel; }

    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

    public String getMcpTools() { return mcpTools; }
    public void setMcpTools(String mcpTools) { this.mcpTools = mcpTools; }

    public String getCapabilities() { return capabilities; }
    public void setCapabilities(String capabilities) { this.capabilities = capabilities; }

    public String getExtendedAttributes() { return extendedAttributes; }
    public void setExtendedAttributes(String extendedAttributes) { this.extendedAttributes = extendedAttributes; }
}

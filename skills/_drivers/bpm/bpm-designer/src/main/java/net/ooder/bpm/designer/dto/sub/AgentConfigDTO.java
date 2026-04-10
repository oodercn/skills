package net.ooder.bpm.designer.dto.sub;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;
import java.util.Map;

/**
 * Agent配置DTO
 */
public class AgentConfigDTO {

    @JSONField(name = "agentId")
    private String agentId;

    @JSONField(name = "agentName")
    private String agentName;

    @JSONField(name = "agentType")
    private String agentType;

    @JSONField(name = "modelName")
    private String modelName;

    @JSONField(name = "systemPrompt")
    private String systemPrompt;

    @JSONField(name = "temperature")
    private Double temperature;

    @JSONField(name = "maxTokens")
    private Integer maxTokens;

    @JSONField(name = "tools")
    private List<String> tools;

    @JSONField(name = "capabilities")
    private List<String> capabilities;

    @JSONField(name = "inputSchema")
    private Map<String, Object> inputSchema;

    @JSONField(name = "outputSchema")
    private Map<String, Object> outputSchema;

    @JSONField(name = "memoryEnabled")
    private Boolean memoryEnabled;

    @JSONField(name = "contextWindow")
    private Integer contextWindow;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    public Map<String, Object> getInputSchema() {
        return inputSchema;
    }

    public void setInputSchema(Map<String, Object> inputSchema) {
        this.inputSchema = inputSchema;
    }

    public Map<String, Object> getOutputSchema() {
        return outputSchema;
    }

    public void setOutputSchema(Map<String, Object> outputSchema) {
        this.outputSchema = outputSchema;
    }

    public Boolean getMemoryEnabled() {
        return memoryEnabled;
    }

    public void setMemoryEnabled(Boolean memoryEnabled) {
        this.memoryEnabled = memoryEnabled;
    }

    public Integer getContextWindow() {
        return contextWindow;
    }

    public void setContextWindow(Integer contextWindow) {
        this.contextWindow = contextWindow;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }
}

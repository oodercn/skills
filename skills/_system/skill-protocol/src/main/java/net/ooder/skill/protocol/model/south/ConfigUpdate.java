package net.ooder.skill.protocol.model.south;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigUpdate {
    
    @JsonProperty("config_id")
    private String configId;
    
    @JsonProperty("target_agent_id")
    private String targetAgentId;
    
    @JsonProperty("config_type")
    private String configType;
    
    @JsonProperty("config_data")
    private Map<String, Object> configData;
    
    @JsonProperty("version")
    private String version;
    
    @JsonProperty("force_restart")
    private Boolean forceRestart;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getTargetAgentId() {
        return targetAgentId;
    }

    public void setTargetAgentId(String targetAgentId) {
        this.targetAgentId = targetAgentId;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public Map<String, Object> getConfigData() {
        return configData;
    }

    public void setConfigData(Map<String, Object> configData) {
        this.configData = configData;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getForceRestart() {
        return forceRestart;
    }

    public void setForceRestart(Boolean forceRestart) {
        this.forceRestart = forceRestart;
    }
}

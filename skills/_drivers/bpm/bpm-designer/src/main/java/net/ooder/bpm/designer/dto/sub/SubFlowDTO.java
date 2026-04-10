package net.ooder.bpm.designer.dto.sub;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Map;

/**
 * 子流程配置DTO
 */
public class SubFlowDTO {

    @JSONField(name = "subProcessDefId")
    private String subProcessDefId;

    @JSONField(name = "subProcessName")
    private String subProcessName;

    @JSONField(name = "subProcessVersion")
    private String subProcessVersion;

    @JSONField(name = "executionMode")
    private String executionMode;

    @JSONField(name = "waitForCompletion")
    private Boolean waitForCompletion;

    @JSONField(name = "dataMapping")
    private Map<String, Object> dataMapping;

    @JSONField(name = "parameterMapping")
    private Map<String, Object> parameterMapping;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    public String getSubProcessDefId() {
        return subProcessDefId;
    }

    public void setSubProcessDefId(String subProcessDefId) {
        this.subProcessDefId = subProcessDefId;
    }

    public String getSubProcessName() {
        return subProcessName;
    }

    public void setSubProcessName(String subProcessName) {
        this.subProcessName = subProcessName;
    }

    public String getSubProcessVersion() {
        return subProcessVersion;
    }

    public void setSubProcessVersion(String subProcessVersion) {
        this.subProcessVersion = subProcessVersion;
    }

    public String getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(String executionMode) {
        this.executionMode = executionMode;
    }

    public Boolean getWaitForCompletion() {
        return waitForCompletion;
    }

    public void setWaitForCompletion(Boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
    }

    public Map<String, Object> getDataMapping() {
        return dataMapping;
    }

    public void setDataMapping(Map<String, Object> dataMapping) {
        this.dataMapping = dataMapping;
    }

    public Map<String, Object> getParameterMapping() {
        return parameterMapping;
    }

    public void setParameterMapping(Map<String, Object> parameterMapping) {
        this.parameterMapping = parameterMapping;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }
}

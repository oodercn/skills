package net.ooder.bpm.designer.dto.sub;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Map;

/**
 * 服务配置DTO
 */
public class ServiceDTO {

    @JSONField(name = "serviceId")
    private String serviceId;

    @JSONField(name = "serviceName")
    private String serviceName;

    @JSONField(name = "serviceType")
    private String serviceType;

    @JSONField(name = "serviceUrl")
    private String serviceUrl;

    @JSONField(name = "serviceMethod")
    private String serviceMethod;

    @JSONField(name = "serviceProtocol")
    private String serviceProtocol;

    @JSONField(name = "inputParameters")
    private Map<String, Object> inputParameters;

    @JSONField(name = "outputParameters")
    private Map<String, Object> outputParameters;

    @JSONField(name = "headers")
    private Map<String, String> headers;

    @JSONField(name = "timeout")
    private Integer timeout;

    @JSONField(name = "retryCount")
    private Integer retryCount;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public String getServiceProtocol() {
        return serviceProtocol;
    }

    public void setServiceProtocol(String serviceProtocol) {
        this.serviceProtocol = serviceProtocol;
    }

    public Map<String, Object> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(Map<String, Object> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public Map<String, Object> getOutputParameters() {
        return outputParameters;
    }

    public void setOutputParameters(Map<String, Object> outputParameters) {
        this.outputParameters = outputParameters;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }
}

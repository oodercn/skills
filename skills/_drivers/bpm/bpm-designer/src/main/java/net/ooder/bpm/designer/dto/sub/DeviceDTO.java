package net.ooder.bpm.designer.dto.sub;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Map;

/**
 * 设备配置DTO
 */
public class DeviceDTO {

    @JSONField(name = "deviceId")
    private String deviceId;

    @JSONField(name = "deviceName")
    private String deviceName;

    @JSONField(name = "deviceType")
    private String deviceType;

    @JSONField(name = "deviceModel")
    private String deviceModel;

    @JSONField(name = "connectionString")
    private String connectionString;

    @JSONField(name = "protocol")
    private String protocol;

    @JSONField(name = "parameters")
    private Map<String, Object> parameters;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }
}

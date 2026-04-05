package net.ooder.agent.client.iot;

import java.io.Serializable;

public class DeviceEndPoint implements Serializable {
    private String endPointId;
    private String ieeeaddress;
    private Device device;

    public String getEndPointId() {
        return endPointId;
    }

    public void setEndPointId(String endPointId) {
        this.endPointId = endPointId;
    }

    public String getIeeeaddress() {
        return ieeeaddress;
    }

    public void setIeeeaddress(String ieeeaddress) {
        this.ieeeaddress = ieeeaddress;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}

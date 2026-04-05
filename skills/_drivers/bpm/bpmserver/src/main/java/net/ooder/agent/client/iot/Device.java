package net.ooder.agent.client.iot;

import java.io.Serializable;

public class Device implements Serializable {
    private String serialno;
    private Device rootDevice;
    private SensorType sensortype;

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public Device getRootDevice() {
        return rootDevice;
    }

    public void setRootDevice(Device rootDevice) {
        this.rootDevice = rootDevice;
    }

    public SensorType getSensortype() {
        return sensortype;
    }

    public void setSensortype(SensorType sensortype) {
        this.sensortype = sensortype;
    }

    public static class SensorType implements Serializable {
        private Integer type;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
    }
}

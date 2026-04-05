package net.ooder.agent.client.iot.enums;

public enum DeviceDataTypeKey {
    TEMPERATURE("temperature"),
    HUMIDITY("humidity"),
    PRESSURE("pressure"),
    VOLTAGE("voltage"),
    CURRENT("current"),
    POWER("power"),
    STATUS("status"),
    VALUE("value");

    private final String type;

    DeviceDataTypeKey(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static DeviceDataTypeKey fromType(String type) {
        for (DeviceDataTypeKey e : values()) {
            if (e.type.equalsIgnoreCase(type)) {
                return e;
            }
        }
        return VALUE;
    }
}

package net.ooder.skill.openwrt.driver;

import lombok.Data;
import java.util.Map;

@Data
public class NetworkSetting {
    private String type;
    private String name;
    private String status;
    private Map<String, Object> config;
    private long lastUpdated;
}

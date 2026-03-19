package net.ooder.skill.network.model;

import lombok.Data;
import java.util.Map;

@Data
public class NetworkSetting {
    private String type;
    private String name;
    private String category;
    private String status;
    private String description;
    private Map<String, Object> config;
    private long lastUpdated;
}

package net.ooder.skill.market.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SdkConfig {
    private String mode;
    private Integer mockDelay;
    private String version;
    private String endpoint;
    private Map<String, Object> settings;
    private Long updateTime;
}

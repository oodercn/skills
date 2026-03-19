package net.ooder.skill.market.dto;

import lombok.Data;
import java.util.List;

@Data
public class SdkStatus {
    private String status;
    private String mode;
    private String version;
    private Boolean healthy;
    private Long uptime;
    private Integer activeConnections;
    private List<SdkComponent> components;
    
    @Data
    public static class SdkComponent {
        private String name;
        private String status;
        private String version;
    }
}

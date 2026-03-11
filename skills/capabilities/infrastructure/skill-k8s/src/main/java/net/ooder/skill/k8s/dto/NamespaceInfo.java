package net.ooder.skill.k8s.dto;

import lombok.Data;
import java.util.Map;

@Data
public class NamespaceInfo {
    private String name;
    private String status;
    private String phase;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private Integer podCount;
    private Integer resourceQuotaCount;
    private Long createTime;
}

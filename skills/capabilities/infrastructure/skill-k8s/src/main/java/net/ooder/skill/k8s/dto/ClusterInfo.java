package net.ooder.skill.k8s.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ClusterInfo {
    private String clusterId;
    private String name;
    private String version;
    private String status;
    private String endpoint;
    private Integer nodeCount;
    private Integer namespaceCount;
    private Integer podCount;
    private Long createTime;
    private Map<String, Object> capacity;
    private Map<String, Object> conditions;
}

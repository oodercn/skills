package net.ooder.skill.k8s.dto;

import lombok.Data;
import java.util.Map;

@Data
public class NodeInfo {
    private String nodeName;
    private String status;
    private String kubeletVersion;
    private String osImage;
    private String kernelVersion;
    private String containerRuntime;
    private Map<String, String> labels;
    private Map<String, Object> capacity;
    private Map<String, Object> allocatable;
    private Map<String, Object> conditions;
    private Long createTime;
    private Double cpuUsage;
    private Double memoryUsage;
}

package net.ooder.skill.k8s.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PodInfo {
    private String podName;
    private String namespace;
    private String status;
    private String podIP;
    private String hostIP;
    private String nodeName;
    private String restartPolicy;
    private Integer restartCount;
    private List<ContainerInfo> containers;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private Long createTime;
    private Long startTime;
    
    @Data
    public static class ContainerInfo {
        private String name;
        private String image;
        private String status;
        private Integer restartCount;
        private Boolean ready;
    }
}

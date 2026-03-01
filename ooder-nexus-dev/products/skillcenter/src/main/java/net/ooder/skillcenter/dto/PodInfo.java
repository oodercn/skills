package net.ooder.skillcenter.dto;

import java.util.List;
import java.util.Map;

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
    
    public static class ContainerInfo {
        private String name;
        private String image;
        private String status;
        private Integer restartCount;
        private Boolean ready;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getRestartCount() { return restartCount; }
        public void setRestartCount(Integer restartCount) { this.restartCount = restartCount; }
        public Boolean getReady() { return ready; }
        public void setReady(Boolean ready) { this.ready = ready; }
    }
    
    public String getPodName() { return podName; }
    public void setPodName(String podName) { this.podName = podName; }
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPodIP() { return podIP; }
    public void setPodIP(String podIP) { this.podIP = podIP; }
    public String getHostIP() { return hostIP; }
    public void setHostIP(String hostIP) { this.hostIP = hostIP; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public String getRestartPolicy() { return restartPolicy; }
    public void setRestartPolicy(String restartPolicy) { this.restartPolicy = restartPolicy; }
    public Integer getRestartCount() { return restartCount; }
    public void setRestartCount(Integer restartCount) { this.restartCount = restartCount; }
    public List<ContainerInfo> getContainers() { return containers; }
    public void setContainers(List<ContainerInfo> containers) { this.containers = containers; }
    public Map<String, String> getLabels() { return labels; }
    public void setLabels(Map<String, String> labels) { this.labels = labels; }
    public Map<String, String> getAnnotations() { return annotations; }
    public void setAnnotations(Map<String, String> annotations) { this.annotations = annotations; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) { this.startTime = startTime; }
}

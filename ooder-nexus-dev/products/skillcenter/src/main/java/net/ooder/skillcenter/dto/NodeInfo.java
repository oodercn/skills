package net.ooder.skillcenter.dto;

import java.util.Map;

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
    
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getKubeletVersion() { return kubeletVersion; }
    public void setKubeletVersion(String kubeletVersion) { this.kubeletVersion = kubeletVersion; }
    public String getOsImage() { return osImage; }
    public void setOsImage(String osImage) { this.osImage = osImage; }
    public String getKernelVersion() { return kernelVersion; }
    public void setKernelVersion(String kernelVersion) { this.kernelVersion = kernelVersion; }
    public String getContainerRuntime() { return containerRuntime; }
    public void setContainerRuntime(String containerRuntime) { this.containerRuntime = containerRuntime; }
    public Map<String, String> getLabels() { return labels; }
    public void setLabels(Map<String, String> labels) { this.labels = labels; }
    public Map<String, Object> getCapacity() { return capacity; }
    public void setCapacity(Map<String, Object> capacity) { this.capacity = capacity; }
    public Map<String, Object> getAllocatable() { return allocatable; }
    public void setAllocatable(Map<String, Object> allocatable) { this.allocatable = allocatable; }
    public Map<String, Object> getConditions() { return conditions; }
    public void setConditions(Map<String, Object> conditions) { this.conditions = conditions; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
    public Double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(Double cpuUsage) { this.cpuUsage = cpuUsage; }
    public Double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(Double memoryUsage) { this.memoryUsage = memoryUsage; }
}

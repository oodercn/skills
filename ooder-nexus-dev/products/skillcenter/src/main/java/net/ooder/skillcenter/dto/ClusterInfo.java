package net.ooder.skillcenter.dto;

import java.util.Map;

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
    
    public String getClusterId() { return clusterId; }
    public void setClusterId(String clusterId) { this.clusterId = clusterId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public Integer getNodeCount() { return nodeCount; }
    public void setNodeCount(Integer nodeCount) { this.nodeCount = nodeCount; }
    public Integer getNamespaceCount() { return namespaceCount; }
    public void setNamespaceCount(Integer namespaceCount) { this.namespaceCount = namespaceCount; }
    public Integer getPodCount() { return podCount; }
    public void setPodCount(Integer podCount) { this.podCount = podCount; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
    public Map<String, Object> getCapacity() { return capacity; }
    public void setCapacity(Map<String, Object> capacity) { this.capacity = capacity; }
    public Map<String, Object> getConditions() { return conditions; }
    public void setConditions(Map<String, Object> conditions) { this.conditions = conditions; }
}

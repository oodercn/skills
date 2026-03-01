package net.ooder.skillcenter.dto;

public class NamespaceInfo {
    private String name;
    private String status;
    private String phase;
    private Integer podCount;
    private Long createTime;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public Integer getPodCount() { return podCount; }
    public void setPodCount(Integer podCount) { this.podCount = podCount; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
}

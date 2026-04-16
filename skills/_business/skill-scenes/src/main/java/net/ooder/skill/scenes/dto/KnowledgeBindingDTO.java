package net.ooder.skill.scenes.dto;

public class KnowledgeBindingDTO {
    private String kbId;
    private String kbName;
    private String layer;
    private Integer priority;
    private long bindTime;

    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    public String getKbName() { return kbName; }
    public void setKbName(String kbName) { this.kbName = kbName; }
    public String getLayer() { return layer; }
    public void setLayer(String layer) { this.layer = layer; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public long getBindTime() { return bindTime; }
    public void setBindTime(long bindTime) { this.bindTime = bindTime; }
}

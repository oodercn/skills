package net.ooder.skill.scene.dto.scene;

public class KnowledgeBindingDTO {
    
    private String kbId;
    private String kbName;
    private String bindingType;
    private int priority;

    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    public String getKbName() { return kbName; }
    public void setKbName(String kbName) { this.kbName = kbName; }
    public String getBindingType() { return bindingType; }
    public void setBindingType(String bindingType) { this.bindingType = bindingType; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}

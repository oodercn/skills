package net.ooder.skill.selector.dto;

public class TemplateItemDTO {

    private String templateId;
    private String name;
    private String description;
    private String category;
    private String icon;
    private String status;
    private int capabilityCount;

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getCapabilityCount() { return capabilityCount; }
    public void setCapabilityCount(int capabilityCount) { this.capabilityCount = capabilityCount; }
}
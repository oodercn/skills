package net.ooder.skill.selector.dto;

public class CapabilityTypeDTO {

    private String typeId;
    private String name;
    private String description;
    private String icon;
    private int count;

    public CapabilityTypeDTO() {}

    public CapabilityTypeDTO(String typeId, String name, String description) {
        this.typeId = typeId;
        this.name = name;
        this.description = description;
    }

    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
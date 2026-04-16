package net.ooder.skill.dict.dto;

import java.util.List;

public class DictDTO {
    private String code;
    private String name;
    private String description;
    private List<DictItemDTO> items;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<DictItemDTO> getItems() { return items; }
    public void setItems(List<DictItemDTO> items) { this.items = items; }
}

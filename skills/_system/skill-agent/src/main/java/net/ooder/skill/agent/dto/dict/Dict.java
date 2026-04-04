package net.ooder.skill.agent.dto.dict;

import java.util.List;

public class Dict {
    
    private String code;
    private String name;
    private String description;
    private List<DictItem> items;
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<DictItem> getItems() {
        return items;
    }
    
    public void setItems(List<DictItem> items) {
        this.items = items;
    }
}

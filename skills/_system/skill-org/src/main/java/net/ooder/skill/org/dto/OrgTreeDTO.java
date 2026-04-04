package net.ooder.skill.org.dto;

import java.util.List;

public class OrgTreeDTO {
    private String id;
    private String name;
    private List<OrgTreeDTO> children;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<OrgTreeDTO> getChildren() { return children; }
    public void setChildren(List<OrgTreeDTO> children) { this.children = children; }
}

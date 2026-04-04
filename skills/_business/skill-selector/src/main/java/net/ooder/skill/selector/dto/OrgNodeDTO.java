package net.ooder.skill.selector.dto;

import java.util.List;

public class OrgNodeDTO {
    private String id;
    private String name;
    private String type;
    private String parentId;
    private List<OrgNodeDTO> children;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public List<OrgNodeDTO> getChildren() { return children; }
    public void setChildren(List<OrgNodeDTO> children) { this.children = children; }
}

package net.ooder.skill.selector.dto;

import java.util.List;

public class OrgNodeDTO {

    private String nodeId;
    private String name;
    private String type;
    private List<OrgNodeDTO> children;

    public OrgNodeDTO() {}

    public OrgNodeDTO(String nodeId, String name, String type) {
        this.nodeId = nodeId;
        this.name = name;
        this.type = type;
    }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<OrgNodeDTO> getChildren() { return children; }
    public void setChildren(List<OrgNodeDTO> children) { this.children = children; }
}
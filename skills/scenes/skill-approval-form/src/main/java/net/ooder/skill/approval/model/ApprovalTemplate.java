package net.ooder.skill.approval.model;

import java.time.LocalDateTime;
import java.util.List;

public class ApprovalTemplate {
    private String id;
    private String type;
    private String name;
    private String description;
    private String icon;
    private String color;
    private List<ApprovalNode> nodes;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static class ApprovalNode {
        private String nodeId;
        private String nodeName;
        private List<String> approverRoles;
        private int order;
        private boolean required;

        public String getNodeId() { return nodeId; }
        public void setNodeId(String nodeId) { this.nodeId = nodeId; }
        public String getNodeName() { return nodeName; }
        public void setNodeName(String nodeName) { this.nodeName = nodeName; }
        public List<String> getApproverRoles() { return approverRoles; }
        public void setApproverRoles(List<String> approverRoles) { this.approverRoles = approverRoles; }
        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public List<ApprovalNode> getNodes() { return nodes; }
    public void setNodes(List<ApprovalNode> nodes) { this.nodes = nodes; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

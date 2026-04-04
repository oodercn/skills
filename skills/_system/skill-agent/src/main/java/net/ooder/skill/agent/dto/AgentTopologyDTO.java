package net.ooder.skill.agent.dto;

import java.util.List;
import java.util.Map;

public class AgentTopologyDTO {
    private List<AgentNode> nodes;
    private List<AgentEdge> edges;
    private Map<String, Object> layout;
    private long timestamp;

    public static class AgentNode {
        private String id;
        private String name;
        private String type;
        private String status;
        private String clusterId;
        private int x;
        private int y;
        private Map<String, Object> data;
        private String icon;
        private String color;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getClusterId() { return clusterId; }
        public void setClusterId(String clusterId) { this.clusterId = clusterId; }
        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    public static class AgentEdge {
        private String id;
        private String source;
        private String target;
        private String type;
        private String label;
        private Map<String, Object> data;
        private boolean animated;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
        public boolean isAnimated() { return animated; }
        public void setAnimated(boolean animated) { this.animated = animated; }
    }

    public List<AgentNode> getNodes() { return nodes; }
    public void setNodes(List<AgentNode> nodes) { this.nodes = nodes; }
    public List<AgentEdge> getEdges() { return edges; }
    public void setEdges(List<AgentEdge> edges) { this.edges = edges; }
    public Map<String, Object> getLayout() { return layout; }
    public void setLayout(Map<String, Object> layout) { this.layout = layout; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

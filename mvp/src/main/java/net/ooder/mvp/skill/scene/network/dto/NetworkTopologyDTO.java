package net.ooder.mvp.skill.scene.network.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkTopologyDTO {
    private String topologyId;
    private String name;
    private List<TopologyNode> nodes = new ArrayList<>();
    private List<TopologyLink> links = new ArrayList<>();
    private Map<String, Object> metadata = new HashMap<>();
    private long updatedAt;

    public String getTopologyId() { return topologyId; }
    public void setTopologyId(String topologyId) { this.topologyId = topologyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<TopologyNode> getNodes() { return nodes; }
    public void setNodes(List<TopologyNode> nodes) { this.nodes = nodes; }
    public List<TopologyLink> getLinks() { return links; }
    public void setLinks(List<TopologyLink> links) { this.links = links; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public static class TopologyNode {
        private String id;
        private String name;
        private String type;
        private String ip;
        private String status;
        private int x;
        private int y;

        public TopologyNode() {}
        
        public TopologyNode(String id, String name, String type, String ip, String status) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.ip = ip;
            this.status = status;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
    }

    public static class TopologyLink {
        private String id;
        private String source;
        private String target;
        private String type;
        private long latency;
        private String status;

        public TopologyLink() {}
        
        public TopologyLink(String id, String source, String target, String type) {
            this.id = id;
            this.source = source;
            this.target = target;
            this.type = type;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public long getLatency() { return latency; }
        public void setLatency(long latency) { this.latency = latency; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}

package net.ooder.skill.network.dto;

import java.util.List;
import java.util.Map;

public class NetworkTopology {
    private String topologyId;
    private List<String> nodes;
    private List<NetworkLink> links;
    private Map<String, Object> metadata;
    private long updatedAt;

    public NetworkTopology() {
        this.updatedAt = System.currentTimeMillis();
    }

    public String getTopologyId() {
        return topologyId;
    }

    public void setTopologyId(String topologyId) {
        this.topologyId = topologyId;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public List<NetworkLink> getLinks() {
        return links;
    }

    public void setLinks(List<NetworkLink> links) {
        this.links = links;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}

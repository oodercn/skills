package net.ooder.skill.network.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NetworkTopology implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private List<TopologyNode> nodes;
    private List<TopologyLink> links;
    private long updatedAt;

    public NetworkTopology() {
        this.nodes = new ArrayList<>();
        this.links = new ArrayList<>();
        this.updatedAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TopologyNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TopologyNode> nodes) {
        this.nodes = nodes != null ? nodes : new ArrayList<>();
    }

    public List<TopologyLink> getLinks() {
        return links;
    }

    public void setLinks(List<TopologyLink> links) {
        this.links = links != null ? links : new ArrayList<>();
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void addNode(TopologyNode node) {
        if (node != null) {
            nodes.add(node);
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public void addLink(TopologyLink link) {
        if (link != null) {
            links.add(link);
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public static class TopologyNode implements Serializable {
        private String id;
        private String name;
        private String type;
        private String ip;
        private String status;
        private int x;
        private int y;

        public TopologyNode() {
        }

        public TopologyNode(String id, String name, String type, String ip, String status) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.ip = ip;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    public static class TopologyLink implements Serializable {
        private String id;
        private String source;
        private String target;
        private String type;
        private long bandwidth;
        private int latency;
        private String status;

        public TopologyLink() {
        }

        public TopologyLink(String id, String source, String target, String type) {
            this.id = id;
            this.source = source;
            this.target = target;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public int getLatency() {
            return latency;
        }

        public void setLatency(int latency) {
            this.latency = latency;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}

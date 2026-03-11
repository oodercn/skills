package net.ooder.skill.project.knowledge.dto;

import java.util.List;

public class KnowledgeGraphResponse {
    private List<NodeResponse> nodes;
    private List<EdgeResponse> edges;

    public List<NodeResponse> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeResponse> nodes) {
        this.nodes = nodes;
    }

    public List<EdgeResponse> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeResponse> edges) {
        this.edges = edges;
    }

    public static class NodeResponse {
        private String id;
        private String label;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class EdgeResponse {
        private String source;
        private String target;
        private String label;

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

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}

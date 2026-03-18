package net.ooder.skill.scene.dto.scene;

public class KnowledgeBindingDTO {
    private String kbId;
    private String name;
    private String layer;
    private Integer topK;
    private Double threshold;

    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLayer() { return layer; }
    public void setLayer(String layer) { this.layer = layer; }

    public Integer getTopK() { return topK; }
    public void setTopK(Integer topK) { this.topK = topK; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }
}

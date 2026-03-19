package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;

public class KnowledgeBaseBindRequest implements Serializable {
    private String kbId;
    private String kbName;
    private int topK;
    private double threshold;
    private String layer;
    
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    public String getKbName() { return kbName; }
    public void setKbName(String kbName) { this.kbName = kbName; }
    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }
    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }
    public String getLayer() { return layer; }
    public void setLayer(String layer) { this.layer = layer; }
}

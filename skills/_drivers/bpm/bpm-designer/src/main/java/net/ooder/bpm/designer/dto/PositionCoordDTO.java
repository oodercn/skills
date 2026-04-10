package net.ooder.bpm.designer.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 坐标DTO - 同时支持FastJSON2和Jackson
 */
public class PositionCoordDTO {

    @JSONField(name = "x")
    @JsonProperty("x")
    private Integer x;

    @JSONField(name = "y")
    @JsonProperty("y")
    private Integer y;
    
    public PositionCoordDTO() {
    }
    
    public PositionCoordDTO(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }
    
    public Integer getX() {
        return x;
    }
    
    public void setX(Integer x) {
        this.x = x;
    }
    
    public Integer getY() {
        return y;
    }
    
    public void setY(Integer y) {
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "PositionCoordDTO{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

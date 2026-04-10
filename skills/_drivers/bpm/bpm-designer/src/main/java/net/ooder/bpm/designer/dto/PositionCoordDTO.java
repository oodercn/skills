package net.ooder.bpm.designer.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 坐标DTO - 同时支持FastJSON2和Jackson
 * 使用Double类型支持浮点数坐标
 */
public class PositionCoordDTO {

    @JSONField(name = "x")
    @JsonProperty("x")
    private Double x;

    @JSONField(name = "y")
    @JsonProperty("y")
    private Double y;

    public PositionCoordDTO() {
    }

    public PositionCoordDTO(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
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

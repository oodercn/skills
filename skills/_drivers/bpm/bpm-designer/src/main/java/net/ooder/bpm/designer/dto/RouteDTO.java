package net.ooder.bpm.designer.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * 路由定义DTO - 不使用Map
 */
public class RouteDTO {

    @JSONField(name = "routeDefId")
    @JsonProperty("routeDefId")
    private String routeDefId;

    @JSONField(name = "name")
    @JsonProperty("name")
    private String name;

    @JSONField(name = "description")
    @JsonProperty("description")
    private String description;

    @JSONField(name = "from")
    @JsonProperty("from")
    private String fromActivityId;

    @JSONField(name = "to")
    @JsonProperty("to")
    private String toActivityId;

    @JSONField(name = "routeOrder")
    @JsonProperty("routeOrder")
    private Integer routeOrder;

    @JSONField(name = "routeDirection")
    @JsonProperty("routeDirection")
    private String routeDirection;

    @JSONField(name = "routeConditionType")
    @JsonProperty("routeConditionType")
    private String routeConditionType;

    @JSONField(name = "condition")
    @JsonProperty("condition")
    private String condition;

    @JSONField(name = "extendedAttributes")
    @JsonProperty("extendedAttributes")
    private Map<String, Object> extendedAttributes;

    public String getRouteDefId() {
        return routeDefId;
    }

    public void setRouteDefId(String routeDefId) {
        this.routeDefId = routeDefId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFromActivityId() {
        return fromActivityId;
    }

    public void setFromActivityId(String fromActivityId) {
        this.fromActivityId = fromActivityId;
    }

    public String getToActivityId() {
        return toActivityId;
    }

    public void setToActivityId(String toActivityId) {
        this.toActivityId = toActivityId;
    }

    public Integer getRouteOrder() {
        return routeOrder;
    }

    public void setRouteOrder(Integer routeOrder) {
        this.routeOrder = routeOrder;
    }

    public String getRouteDirection() {
        return routeDirection;
    }

    public void setRouteDirection(String routeDirection) {
        this.routeDirection = routeDirection;
    }

    public String getRouteConditionType() {
        return routeConditionType;
    }

    public void setRouteConditionType(String routeConditionType) {
        this.routeConditionType = routeConditionType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }

    @Override
    public String toString() {
        return "RouteDTO{" +
                "routeDefId='" + routeDefId + '\'' +
                ", fromActivityId='" + fromActivityId + '\'' +
                ", toActivityId='" + toActivityId + '\'' +
                '}';
    }
}

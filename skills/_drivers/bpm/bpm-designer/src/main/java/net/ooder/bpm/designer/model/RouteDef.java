package net.ooder.bpm.designer.model;

import java.util.HashMap;
import java.util.Map;

public class RouteDef {
    private String routeDefId;
    private String name;
    private String description;
    private String from;
    private String to;
    private Integer routeOrder;
    private String routeDirection = "FORWARD";
    private String routeConditionType = "CONDITION";
    private String condition;
    private Map<String, Object> extendedAttributes = new HashMap<>();

    public RouteDef() {
        this.routeDefId = "route_" + System.currentTimeMillis();
    }

    public String getRouteDefId() { return routeDefId; }
    public void setRouteDefId(String routeDefId) { this.routeDefId = routeDefId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    
    public Integer getRouteOrder() { return routeOrder; }
    public void setRouteOrder(Integer routeOrder) { this.routeOrder = routeOrder; }
    
    public String getRouteDirection() { return routeDirection; }
    public void setRouteDirection(String routeDirection) { this.routeDirection = routeDirection; }
    
    public String getRouteConditionType() { return routeConditionType; }
    public void setRouteConditionType(String routeConditionType) { this.routeConditionType = routeConditionType; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public Map<String, Object> getExtendedAttributes() { return extendedAttributes; }
    public void setExtendedAttributes(Map<String, Object> extendedAttributes) { this.extendedAttributes = extendedAttributes; }
}

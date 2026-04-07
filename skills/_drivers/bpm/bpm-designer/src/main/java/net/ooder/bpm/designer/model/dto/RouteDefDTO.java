package net.ooder.bpm.designer.model.dto;

import jakarta.validation.constraints.*;
import java.util.Map;

public class RouteDefDTO {
    
    @NotBlank(message = "路由ID不能为空")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]*$", message = "路由ID必须以字母开头，只能包含字母、数字、下划线和连字符")
    private String routeDefId;
    
    @Size(max = 100, message = "路由名称长度不能超过100")
    private String name;
    
    @Size(max = 500, message = "路由描述长度不能超过500")
    private String description;
    
    @NotBlank(message = "起始活动ID不能为空")
    private String from;
    
    @NotBlank(message = "目标活动ID不能为空")
    private String to;
    
    @Min(value = 0, message = "路由顺序不能为负数")
    private Integer routeOrder;
    
    private String routeDirection;
    
    private String routeConditionType;
    
    private String condition;
    
    @Min(value = 0, message = "优先级不能为负数")
    @Max(value = 100, message = "优先级不能超过100")
    private Integer priority;
    
    private Boolean isDefault;
    
    private Boolean showLabel;
    
    private String labelPosition;
    
    private String splitType;
    
    private String joinType;
    
    private Map<String, Object> extendedAttributes;

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
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    
    public Boolean getShowLabel() { return showLabel; }
    public void setShowLabel(Boolean showLabel) { this.showLabel = showLabel; }
    
    public String getLabelPosition() { return labelPosition; }
    public void setLabelPosition(String labelPosition) { this.labelPosition = labelPosition; }
    
    public String getSplitType() { return splitType; }
    public void setSplitType(String splitType) { this.splitType = splitType; }
    
    public String getJoinType() { return joinType; }
    public void setJoinType(String joinType) { this.joinType = joinType; }
    
    public Map<String, Object> getExtendedAttributes() { return extendedAttributes; }
    public void setExtendedAttributes(Map<String, Object> extendedAttributes) { this.extendedAttributes = extendedAttributes; }
}

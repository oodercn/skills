package net.ooder.bpm.designer.dto.sub;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Map;

/**
 * 事件配置DTO
 */
public class EventDTO {

    @JSONField(name = "eventId")
    private String eventId;

    @JSONField(name = "eventName")
    private String eventName;

    @JSONField(name = "eventType")
    private String eventType;

    @JSONField(name = "eventTrigger")
    private String eventTrigger;

    @JSONField(name = "triggerCondition")
    private String triggerCondition;

    @JSONField(name = "eventAction")
    private String eventAction;

    @JSONField(name = "actionParameters")
    private Map<String, Object> actionParameters;

    @JSONField(name = "listenerClass")
    private String listenerClass;

    @JSONField(name = "listenerExpression")
    private String listenerExpression;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventTrigger() {
        return eventTrigger;
    }

    public void setEventTrigger(String eventTrigger) {
        this.eventTrigger = eventTrigger;
    }

    public String getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public String getEventAction() {
        return eventAction;
    }

    public void setEventAction(String eventAction) {
        this.eventAction = eventAction;
    }

    public Map<String, Object> getActionParameters() {
        return actionParameters;
    }

    public void setActionParameters(Map<String, Object> actionParameters) {
        this.actionParameters = actionParameters;
    }

    public String getListenerClass() {
        return listenerClass;
    }

    public void setListenerClass(String listenerClass) {
        this.listenerClass = listenerClass;
    }

    public String getListenerExpression() {
        return listenerExpression;
    }

    public void setListenerExpression(String listenerExpression) {
        this.listenerExpression = listenerExpression;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }
}

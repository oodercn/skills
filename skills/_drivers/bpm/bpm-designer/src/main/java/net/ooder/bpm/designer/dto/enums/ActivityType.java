package net.ooder.bpm.designer.dto.enums;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 活动类型枚举
 */
public enum ActivityType {
    @JSONField(name = "TASK")
    TASK("TASK", "任务活动"),
    
    @JSONField(name = "SUBFLOW")
    SUBFLOW("SUBFLOW", "子流程活动"),
    
    @JSONField(name = "EVENT")
    EVENT("EVENT", "事件活动"),
    
    @JSONField(name = "GATEWAY")
    GATEWAY("GATEWAY", "网关活动");
    
    private final String code;
    private final String label;
    
    ActivityType(String code, String label) {
        this.code = code;
        this.label = label;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getLabel() {
        return label;
    }
    
    public static ActivityType fromCode(String code) {
        if (code == null) return TASK;
        for (ActivityType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return TASK;
    }
}

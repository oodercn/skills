package net.ooder.bpm.designer.dto.enums;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 活动位置枚举
 */
public enum ActivityPosition {
    @JSONField(name = "START")
    START("START", "起始活动"),
    
    @JSONField(name = "NORMAL")
    NORMAL("NORMAL", "普通活动"),
    
    @JSONField(name = "END")
    END("END", "结束活动");
    
    private final String code;
    private final String label;
    
    ActivityPosition(String code, String label) {
        this.code = code;
        this.label = label;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getLabel() {
        return label;
    }
    
    public static ActivityPosition fromCode(String code) {
        if (code == null) return NORMAL;
        for (ActivityPosition position : values()) {
            if (position.code.equalsIgnoreCase(code)) {
                return position;
            }
        }
        return NORMAL;
    }
}

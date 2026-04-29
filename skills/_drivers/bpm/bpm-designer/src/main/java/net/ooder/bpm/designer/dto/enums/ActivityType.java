package net.ooder.bpm.designer.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityType {
    START("START", "开始活动"),
    END("END", "结束活动"),
    TASK("TASK", "任务活动"),
    SERVICE("SERVICE", "服务任务"),
    SCRIPT("SCRIPT", "脚本任务"),
    SUBFLOW("SUBFLOW", "子流程活动"),
    LLM_TASK("LLM_TASK", "LLM任务"),
    AGENT_TASK("AGENT_TASK", "Agent任务"),
    COORDINATOR("COORDINATOR", "协调器"),
    XOR_GATEWAY("XOR_GATEWAY", "排他网关"),
    AND_GATEWAY("AND_GATEWAY", "并行网关"),
    OR_GATEWAY("OR_GATEWAY", "包容网关"),
    EVENT("EVENT", "事件活动"),
    GATEWAY("GATEWAY", "网关活动"),
    SCENE("SCENE", "场景活动");

    private final String code;
    private final String label;

    ActivityType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    @JsonCreator
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

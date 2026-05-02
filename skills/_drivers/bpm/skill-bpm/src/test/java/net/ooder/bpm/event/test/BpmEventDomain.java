package net.ooder.bpm.event.test;

import java.util.*;

public enum BpmEventDomain {
    PROCESS("process", "流程事件"),
    ACTIVITY("activity", "活动事件"),
    SKILLFLOW("skillflow", "SkillFlow生命周期事件"),
    AGENT("agent", "Agent委派/协作事件"),
    SCENE_BRIDGE("scene_bridge", "Scene桥接事件");

    private final String code;
    private final String description;

    BpmEventDomain(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
}

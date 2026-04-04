package net.ooder.skill.bpm.enums;

import net.ooder.skill.common.dict.DictItem;
import net.ooder.skill.common.dict.Dict;

@Dict(code = "activity_type", name = "活动类型", description = "流程活动的类型")
public enum ActivityType implements DictItem {

    START("START", "开始节点", "流程开始节点", "ri-play-circle-line", 1),
    END("END", "结束节点", "流程结束节点", "ri-stop-circle-line", 2),
    TASK("TASK", "任务节点", "人工任务节点", "ri-user-line", 3),
    SERVICE("SERVICE", "服务节点", "自动服务节点", "ri-server-line", 4),
    GATEWAY("GATEWAY", "网关节点", "流程网关节点", "ri-git-branch-line", 5),
    SUBPROCESS("SUBPROCESS", "子流程", "子流程节点", "ri-flow-chart-line", 6),
    DEVICE("DEVICE", "设备节点", "IoT设备节点", "ri-cpu-line", 7),
    EVENT("EVENT", "事件节点", "事件触发节点", "ri-flashlight-line", 8);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ActivityType(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public int getSort() {
        return sort;
    }
}

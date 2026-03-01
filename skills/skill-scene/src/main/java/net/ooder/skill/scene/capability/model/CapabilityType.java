package net.ooder.skill.scene.capability.model;

import net.ooder.skill.scene.dto.dict.Dict;
import net.ooder.skill.scene.dto.dict.DictItem;

@Dict(code = "capability_type", name = "能力类型", description = "能力的分类类型")
public enum CapabilityType implements DictItem {
    
    DRIVER("DRIVER", "驱动类型", "设备驱动、硬件接口", "ri-hard-drive-2-line", 1),
    SERVICE("SERVICE", "服务类型", "业务服务、API服务", "ri-server-line", 2),
    MANAGEMENT("MANAGEMENT", "管理类型", "配置管理、监控管理", "ri-settings-3-line", 3),
    AI("AI", "AI类型", "LLM、机器学习", "ri-brain-line", 4),
    STORAGE("STORAGE", "存储类型", "文件存储、数据库", "ri-database-2-line", 5),
    COMMUNICATION("COMMUNICATION", "通信类型", "消息、通知", "ri-message-3-line", 6),
    SECURITY("SECURITY", "安全类型", "认证、加密", "ri-shield-check-line", 7),
    MONITORING("MONITORING", "监控类型", "日志、指标", "ri-pulse-line", 8),
    SKILL("SKILL", "技能类型", "可安装的技能包", "ri-flashlight-line", 9),
    SCENE("SCENE", "场景类型", "场景本身作为能力", "ri-layout-grid-line", 10),
    SCENE_GROUP("SCENE_GROUP", "场景组类型", "场景组作为能力", "ri-layout-grid-line", 11),
    CAPABILITY_CHAIN("CAPABILITY_CHAIN", "能力链类型", "能力组合", "ri-link", 12),
    CUSTOM("CUSTOM", "自定义类型", "用户自定义能力", "ri-tools-line", 99);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    CapabilityType(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getCode() {
        return code;
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

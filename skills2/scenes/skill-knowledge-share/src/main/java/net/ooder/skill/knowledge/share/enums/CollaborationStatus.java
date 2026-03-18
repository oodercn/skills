package net.ooder.skill.knowledge.share.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "collaboration_status", name = "协作状态", description = "知识库协作编辑状态")
public enum CollaborationStatus implements DictItem {
    
    IDLE("IDLE", "空闲", "无协作会话", "ri-stop-circle-line", 1),
    ACTIVE("ACTIVE", "协作中", "正在协作编辑", "ri-team-line", 2),
    PAUSED("PAUSED", "已暂停", "协作已暂停", "ri-pause-circle-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    CollaborationStatus(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}

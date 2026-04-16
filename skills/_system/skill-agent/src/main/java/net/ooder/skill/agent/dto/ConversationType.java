package net.ooder.skill.agent.dto;

import net.ooder.skill.dict.dto.Dict;
import net.ooder.skill.dict.dto.DictItem;

@Dict(code = "conversation_type", name = "对话类型", description = "Agent-Chat对话类型")
public enum ConversationType implements DictItem {
    
    A2A("A2A", "Agent-to-Agent", "Agent之间的对话", "ri-robot-line", "#9C27B0", 1),
    P2A("P2A", "Participant-to-Agent", "参与者与Agent的对话", "ri-user-voice-line", "#4CAF50", 2),
    P2P("P2P", "Participant-to-Participant", "参与者之间的对话", "ri-team-line", "#FF9800", 3),
    SYSTEM("SYSTEM", "System", "系统消息", "ri-information-line", "#2196F3", 4),
    TODO("TODO", "Todo", "待办消息", "ri-checkbox-line", "#f44336", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final String color;
    private final int sort;

    ConversationType(String code, String name, String description, String icon, String color, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.color = color;
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

    public String getColor() {
        return color;
    }
}

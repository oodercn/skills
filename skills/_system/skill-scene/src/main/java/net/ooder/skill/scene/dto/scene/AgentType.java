package net.ooder.skill.scene.dto.scene;

public enum AgentType {
    ASSISTANT("ASSISTANT", "助手型智能体"),
    AUTONOMOUS("AUTONOMOUS", "自主型智能体"),
    REACTIVE("REACTIVE", "反应型智能体"),
    DELIBERATIVE("DELIBERATIVE", "思考型智能体"),
    HYBRID("HYBRID", "混合型智能体");

    private final String code;
    private final String name;

    AgentType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static AgentType fromCode(String code) {
        for (AgentType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return ASSISTANT;
    }
}

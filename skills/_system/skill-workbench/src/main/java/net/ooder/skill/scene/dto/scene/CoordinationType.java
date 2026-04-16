package net.ooder.skill.scene.dto.scene;

public enum CoordinationType {
    SEQUENTIAL("SEQUENTIAL", "顺序协调"),
    PARALLEL("PARALLEL", "并行协调"),
    CONDITIONAL("CONDITIONAL", "条件协调"),
    ITERATIVE("ITERATIVE", "迭代协调"),
    EVENT_DRIVEN("EVENT_DRIVEN", "事件驱动协调");

    private final String code;
    private final String name;

    CoordinationType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static CoordinationType fromCode(String code) {
        for (CoordinationType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return SEQUENTIAL;
    }
}

package net.ooder.skill.scene.dto.scene;

public enum ConflictResolution {
    OVERWRITE("OVERWRITE", "覆盖"),
    MERGE("MERGE", "合并"),
    SKIP("SKIP", "跳过"),
    ABORT("ABORT", "中止"),
    ASK("ASK", "询问用户");

    private final String code;
    private final String name;

    ConflictResolution(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static ConflictResolution fromCode(String code) {
        for (ConflictResolution resolution : values()) {
            if (resolution.code.equals(code)) return resolution;
        }
        return ASK;
    }
}

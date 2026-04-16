package net.ooder.skill.scene.dto.scene;

public enum SceneType {
    
    PRIMARY("PRIMARY", "主场景", "主要场景，独立运行", "ri-layout-top-line", 1),
    COLLABORATIVE("COLLABORATIVE", "协作场景", "协作场景，与其他场景配合", "ri-layout-bottom-line", 2),
    ENTERPRISE("ENTERPRISE", "企业网络", "企业级网络场景", "ri-building-line", 3),
    PERSONAL("PERSONAL", "个人网络", "个人级网络场景", "ri-user-line", 4),
    TEST("TEST", "测试网络", "测试环境场景", "ri-flask-line", 5),
    DEVELOPMENT("DEVELOPMENT", "开发环境", "开发环境场景", "ri-code-line", 6);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    SceneType(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public int getSort() { return sort; }
    
    public static SceneType fromCode(String code) {
        for (SceneType type : values()) {
            if (type.code.equals(code)) return type;
        }
        return PRIMARY;
    }
}

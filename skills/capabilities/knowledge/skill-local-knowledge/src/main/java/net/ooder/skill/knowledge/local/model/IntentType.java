package net.ooder.skill.knowledge.local.model;

public enum IntentType {
    DATA_QUERY("DATA_QUERY", "数据查询"),
    CREATE_ACTION("CREATE_ACTION", "创建操作"),
    FORM_ASSIST("FORM_ASSIST", "表单辅助"),
    DOC_SEARCH("DOC_SEARCH", "文档检索"),
    SYSTEM_HELP("SYSTEM_HELP", "系统帮助"),
    UNKNOWN("UNKNOWN", "未知意图");

    private final String code;
    private final String description;

    IntentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
}

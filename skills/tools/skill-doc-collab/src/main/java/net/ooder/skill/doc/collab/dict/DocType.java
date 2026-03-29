package net.ooder.skill.doc.collab.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "doc_type", name = "文档类型")
public enum DocType implements DictItem {
    DOCUMENT("DOCUMENT", "文档", "文本文档", "ri-file-text-line", 1),
    SPREADSHEET("SPREADSHEET", "表格", "电子表格", "ri-file-excel-line", 2),
    PRESENTATION("PRESENTATION", "演示", "演示文稿", "ri-file-ppt-line", 3),
    MINDMAP("MINDMAP", "思维导图", "思维导图", "ri-mind-map", 4),
    WIKI("WIKI", "知识库", "知识库文档", "ri-book-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    DocType(String code, String name, String description, String icon, int sort) {
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

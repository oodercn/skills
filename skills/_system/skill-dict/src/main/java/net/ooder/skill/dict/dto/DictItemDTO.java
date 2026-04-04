package net.ooder.skill.dict.dto;

public class DictItemDTO {
    private String code;
    private String name;
    private String description;
    private String icon;
    private int sort;
    private String parentCode;

    public DictItemDTO() {}

    public DictItemDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public int getSort() { return sort; }
    public void setSort(int sort) { this.sort = sort; }
    public String getParentCode() { return parentCode; }
    public void setParentCode(String parentCode) { this.parentCode = parentCode; }
}

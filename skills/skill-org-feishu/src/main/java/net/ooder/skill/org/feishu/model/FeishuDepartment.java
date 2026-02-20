package net.ooder.skill.org.feishu.model;

public class FeishuDepartment {

    private String openDepartmentId;
    private String name;
    private String parentDepartmentId;
    private String departmentId;
    private int memberCount;

    public String getOpenDepartmentId() {
        return openDepartmentId;
    }

    public void setOpenDepartmentId(String openDepartmentId) {
        this.openDepartmentId = openDepartmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentDepartmentId() {
        return parentDepartmentId;
    }

    public void setParentDepartmentId(String parentDepartmentId) {
        this.parentDepartmentId = parentDepartmentId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}

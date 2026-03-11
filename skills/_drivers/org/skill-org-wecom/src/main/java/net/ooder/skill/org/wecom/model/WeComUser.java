package net.ooder.skill.org.wecom.model;

import java.util.List;

public class WeComUser {

    private String userid;
    private String name;
    private String mobile;
    private String email;
    private List<String> department;
    private Integer status;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getDepartment() {
        return department;
    }

    public void setDepartment(List<String> department) {
        this.department = department;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDeptId() {
        if (department != null && !department.isEmpty()) {
            return department.get(0);
        }
        return null;
    }
}

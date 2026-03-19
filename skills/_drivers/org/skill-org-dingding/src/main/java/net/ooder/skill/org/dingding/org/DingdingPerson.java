package net.ooder.skill.org.dingding.org;

import net.ooder.org.Org;
import net.ooder.org.Person;
import net.ooder.org.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DingdingPerson implements Person {

    private String id;
    private String account;
    private String password;
    private String name;
    private String nickName;
    private String mobile;
    private String email;
    private String orgId;
    private Org org;
    private List<Org> orgList = new ArrayList<>();
    private String status = "1";
    private Integer index = 0;
    private List<Role> roleList = new ArrayList<>();
    private Set<String> orgIdList = new HashSet<>();
    private Set<String> roleIdList = new HashSet<>();
    private String cloudDiskPath;

    @Override
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    @Override
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    @Override
    public List<Org> getOrgList() {
        return orgList;
    }

    public void setOrgList(List<Org> orgList) {
        this.orgList = orgList;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    @Override
    public Set<String> getOrgIdList() {
        return orgIdList;
    }

    public void setOrgIdList(Set<String> orgIdList) {
        this.orgIdList = orgIdList;
    }

    @Override
    public Set<String> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(Set<String> roleIdList) {
        this.roleIdList = roleIdList;
    }

    @Override
    public String getCloudDiskPath() {
        return cloudDiskPath;
    }

    public void setCloudDiskPath(String cloudDiskPath) {
        this.cloudDiskPath = cloudDiskPath;
    }

    @Override
    public int compareTo(Person other) {
        if (other == null) return 1;
        if (this.index == null && other.getIndex() == null) return 0;
        if (this.index == null) return -1;
        if (other.getIndex() == null) return 1;
        return this.index.compareTo(other.getIndex());
    }
}

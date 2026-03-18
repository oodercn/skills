package net.ooder.skill.org.feishu.org;

import net.ooder.org.Org;
import net.ooder.org.Person;
import net.ooder.org.Role;

import java.util.ArrayList;
import java.util.List;

public class FeishuOrg implements Org {

    private String orgId;
    private String name;
    private String brief;
    private String city;
    private Integer tier = 0;
    private Person leader;
    private String leaderId;
    private Integer index = 0;
    private String parentId;
    private Org parent;
    private List<Org> children = new ArrayList<>();
    private List<Person> persons = new ArrayList<>();
    private List<String> personIdList = new ArrayList<>();
    private List<String> childIdList = new ArrayList<>();
    private List<String> roleIdList = new ArrayList<>();
    private List<Role> roleList = new ArrayList<>();

    @Override
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    @Override
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    @Override
    public Person getLeader() {
        return leader;
    }

    public void setLeader(Person leader) {
        this.leader = leader;
    }

    @Override
    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public Org getParent() {
        return parent;
    }

    public void setParent(Org parent) {
        this.parent = parent;
    }

    public List<Org> getChildren() {
        return children;
    }

    public void setChildren(List<Org> children) {
        this.children = children;
    }

    @Override
    public List<Person> getPersonList() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    @Override
    public List<String> getPersonIdList() {
        return personIdList;
    }

    public void setPersonIdList(List<String> personIdList) {
        this.personIdList = personIdList;
    }

    @Override
    public List<String> getChildIdList() {
        return childIdList;
    }

    public void setChildIdList(List<String> childIdList) {
        this.childIdList = childIdList;
    }

    @Override
    public List<Org> getChildrenList() {
        return children;
    }

    @Override
    public List<String> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<String> roleIdList) {
        this.roleIdList = roleIdList;
    }

    @Override
    public List<Org> getChildrenRecursivelyList() {
        List<Org> result = new ArrayList<>();
        collectChildrenRecursively(this, result);
        return result;
    }

    @Override
    public List<Person> getPersonListRecursively() {
        List<Person> result = new ArrayList<>();
        result.addAll(this.persons);
        for (Org child : children) {
            if (child instanceof FeishuOrg) {
                result.addAll(((FeishuOrg) child).getPersonListRecursively());
            }
        }
        return result;
    }

    private void collectChildrenRecursively(Org org, List<Org> result) {
        List<Org> childrenList = org.getChildrenList();
        if (childrenList != null) {
            for (Org child : childrenList) {
                result.add(child);
                collectChildrenRecursively(child, result);
            }
        }
    }

    @Override
    public int compareTo(Org other) {
        if (other == null) return 1;
        if (this.index == null && other.getIndex() == null) return 0;
        if (this.index == null) return -1;
        if (other.getIndex() == null) return 1;
        return this.index.compareTo(other.getIndex());
    }
}

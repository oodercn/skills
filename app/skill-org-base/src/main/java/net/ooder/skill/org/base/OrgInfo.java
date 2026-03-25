package net.ooder.skill.org.base;

import java.util.List;

/**
 * OrgInfo 缁勭粐淇℃伅
 * 
 * @author Ooder Team
 * @version 2.3
 */
public class OrgInfo {

    private String orgId;
    private String name;
    private String brief;
    private String parentId;
    private String leaderId;
    private int tier;
    private int index;
    private int memberCount;
    private List<OrgInfo> children;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public List<OrgInfo> getChildren() {
        return children;
    }

    public void setChildren(List<OrgInfo> children) {
        this.children = children;
    }
}

package net.ooder.bpm.designer.dto.sub;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;
import java.util.Map;

/**
 * 权限配置DTO
 */
public class RightDTO {

    @JSONField(name = "moveSponsorTo")
    private String moveSponsorTo;

    @JSONField(name = "performer")
    private String performer;

    @JSONField(name = "performerType")
    private String performerType;

    @JSONField(name = "participationType")
    private String participationType;

    @JSONField(name = "participationScope")
    private String participationScope;

    @JSONField(name = "participationScopeValue")
    private String participationScopeValue;

    @JSONField(name = "candidateUsers")
    private List<String> candidateUsers;

    @JSONField(name = "candidateGroups")
    private List<String> candidateGroups;

    @JSONField(name = "candidateRoles")
    private List<String> candidateRoles;

    @JSONField(name = "assignee")
    private String assignee;

    @JSONField(name = "owner")
    private String owner;

    @JSONField(name = "reassignable")
    private Boolean reassignable;

    @JSONField(name = "delegatable")
    private Boolean delegatable;

    @JSONField(name = "transferable")
    private Boolean transferable;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    public String getMoveSponsorTo() {
        return moveSponsorTo;
    }

    public void setMoveSponsorTo(String moveSponsorTo) {
        this.moveSponsorTo = moveSponsorTo;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getPerformerType() {
        return performerType;
    }

    public void setPerformerType(String performerType) {
        this.performerType = performerType;
    }

    public String getParticipationType() {
        return participationType;
    }

    public void setParticipationType(String participationType) {
        this.participationType = participationType;
    }

    public String getParticipationScope() {
        return participationScope;
    }

    public void setParticipationScope(String participationScope) {
        this.participationScope = participationScope;
    }

    public String getParticipationScopeValue() {
        return participationScopeValue;
    }

    public void setParticipationScopeValue(String participationScopeValue) {
        this.participationScopeValue = participationScopeValue;
    }

    public List<String> getCandidateUsers() {
        return candidateUsers;
    }

    public void setCandidateUsers(List<String> candidateUsers) {
        this.candidateUsers = candidateUsers;
    }

    public List<String> getCandidateGroups() {
        return candidateGroups;
    }

    public void setCandidateGroups(List<String> candidateGroups) {
        this.candidateGroups = candidateGroups;
    }

    public List<String> getCandidateRoles() {
        return candidateRoles;
    }

    public void setCandidateRoles(List<String> candidateRoles) {
        this.candidateRoles = candidateRoles;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getReassignable() {
        return reassignable;
    }

    public void setReassignable(Boolean reassignable) {
        this.reassignable = reassignable;
    }

    public Boolean getDelegatable() {
        return delegatable;
    }

    public void setDelegatable(Boolean delegatable) {
        this.delegatable = delegatable;
    }

    public Boolean getTransferable() {
        return transferable;
    }

    public void setTransferable(Boolean transferable) {
        this.transferable = transferable;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }
}

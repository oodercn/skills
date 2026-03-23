package net.ooder.skill.approval.service;

import net.ooder.skill.approval.dto.ApprovalActionRequest;
import net.ooder.skill.approval.dto.ApprovalCreateRequest;
import net.ooder.skill.approval.dto.PageResult;
import net.ooder.skill.approval.model.Approval;
import net.ooder.skill.approval.model.ApprovalTemplate;

import java.util.List;

public interface ApprovalService {
    
    Approval createApproval(ApprovalCreateRequest request);
    
    Approval getApproval(String id);
    
    PageResult<Approval> listApprovals(String status, String type, String applicant, int page, int size);
    
    PageResult<Approval> listMyApprovals(String applicantId, String status, int page, int size);
    
    Approval approve(String id, ApprovalActionRequest request);
    
    Approval reject(String id, ApprovalActionRequest request);
    
    Approval withdraw(String id, String applicantId);
    
    void deleteApproval(String id);
    
    List<ApprovalTemplate> listTemplates();
    
    ApprovalTemplate getTemplate(String id);
    
    ApprovalTemplate createTemplate(ApprovalTemplate template);
    
    ApprovalTemplate updateTemplate(String id, ApprovalTemplate template);
    
    void deleteTemplate(String id);
    
    Object getStatistics();
}

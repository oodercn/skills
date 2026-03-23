package net.ooder.skill.approval.controller;

import net.ooder.skill.approval.dto.ApiResponse;
import net.ooder.skill.approval.dto.ApprovalActionRequest;
import net.ooder.skill.approval.dto.ApprovalCreateRequest;
import net.ooder.skill.approval.dto.PageResult;
import net.ooder.skill.approval.model.Approval;
import net.ooder.skill.approval.model.ApprovalTemplate;
import net.ooder.skill.approval.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-form")
public class ApprovalController {
    
    @Autowired
    private ApprovalService approvalService;
    
    @GetMapping("/templates")
    public ApiResponse<List<ApprovalTemplate>> listTemplates() {
        return new ApiResponse<>(approvalService.listTemplates());
    }
    
    @GetMapping("/templates/{id}")
    public ApiResponse<ApprovalTemplate> getTemplate(@PathVariable String id) {
        ApprovalTemplate template = approvalService.getTemplate(id);
        if (template == null) {
            return new ApiResponse<>("error", "模板不存在");
        }
        return new ApiResponse<>(template);
    }
    
    @PostMapping("/templates")
    public ApiResponse<ApprovalTemplate> createTemplate(@RequestBody ApprovalTemplate template) {
        return new ApiResponse<>(approvalService.createTemplate(template));
    }
    
    @PutMapping("/templates/{id}")
    public ApiResponse<ApprovalTemplate> updateTemplate(
            @PathVariable String id,
            @RequestBody ApprovalTemplate template) {
        ApprovalTemplate updated = approvalService.updateTemplate(id, template);
        if (updated == null) {
            return new ApiResponse<>("error", "模板不存在");
        }
        return new ApiResponse<>(updated);
    }
    
    @DeleteMapping("/templates/{id}")
    public ApiResponse<Void> deleteTemplate(@PathVariable String id) {
        approvalService.deleteTemplate(id);
        return new ApiResponse<>(null);
    }
    
    @PostMapping("/apply")
    public ApiResponse<Approval> createApproval(@RequestBody ApprovalCreateRequest request) {
        Approval approval = approvalService.createApproval(request);
        return new ApiResponse<>(approval);
    }
    
    @GetMapping("/approvals")
    public ApiResponse<PageResult<Approval>> listApprovals(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String applicant,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(approvalService.listApprovals(status, type, applicant, page, size));
    }
    
    @GetMapping("/my-approvals")
    public ApiResponse<PageResult<Approval>> listMyApprovals(
            @RequestParam(required = false) String applicantId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(approvalService.listMyApprovals(applicantId, status, page, size));
    }
    
    @GetMapping("/approvals/{id}")
    public ApiResponse<Approval> getApproval(@PathVariable String id) {
        Approval approval = approvalService.getApproval(id);
        if (approval == null) {
            return new ApiResponse<>("error", "审批不存在");
        }
        return new ApiResponse<>(approval);
    }
    
    @PostMapping("/approvals/{id}/approve")
    public ApiResponse<Approval> approve(
            @PathVariable String id,
            @RequestBody ApprovalActionRequest request) {
        Approval approval = approvalService.approve(id, request);
        if (approval == null) {
            return new ApiResponse<>("error", "审批不存在");
        }
        return new ApiResponse<>(approval);
    }
    
    @PostMapping("/approvals/{id}/reject")
    public ApiResponse<Approval> reject(
            @PathVariable String id,
            @RequestBody ApprovalActionRequest request) {
        Approval approval = approvalService.reject(id, request);
        if (approval == null) {
            return new ApiResponse<>("error", "审批不存在");
        }
        return new ApiResponse<>(approval);
    }
    
    @PostMapping("/approvals/{id}/withdraw")
    public ApiResponse<Approval> withdraw(
            @PathVariable String id,
            @RequestParam String applicantId) {
        Approval approval = approvalService.withdraw(id, applicantId);
        if (approval == null) {
            return new ApiResponse<>("error", "无法撤回");
        }
        return new ApiResponse<>(approval);
    }
    
    @DeleteMapping("/approvals/{id}")
    public ApiResponse<Void> deleteApproval(@PathVariable String id) {
        approvalService.deleteApproval(id);
        return new ApiResponse<>(null);
    }
    
    @GetMapping("/statistics")
    public ApiResponse<Object> getStatistics() {
        return new ApiResponse<>(approvalService.getStatistics());
    }
}

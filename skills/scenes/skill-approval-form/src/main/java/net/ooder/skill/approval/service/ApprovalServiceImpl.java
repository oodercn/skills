package net.ooder.skill.approval.service;

import net.ooder.skill.approval.dto.ApprovalActionRequest;
import net.ooder.skill.approval.dto.ApprovalCreateRequest;
import net.ooder.skill.approval.dto.PageResult;
import net.ooder.skill.approval.model.Approval;
import net.ooder.skill.approval.model.ApprovalTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ApprovalServiceImpl implements ApprovalService {
    
    private final Map<String, Approval> approvals = new ConcurrentHashMap<>();
    private final Map<String, ApprovalTemplate> templates = new ConcurrentHashMap<>();
    
    private static final Map<String, String> TYPE_NAMES = new HashMap<>();
    static {
        TYPE_NAMES.put("leave", "请假申请");
        TYPE_NAMES.put("expense", "费用报销");
        TYPE_NAMES.put("purchase", "采购申请");
        TYPE_NAMES.put("contract", "合同审批");
        TYPE_NAMES.put("overtime", "加班申请");
        TYPE_NAMES.put("business", "出差申请");
    }
    
    public ApprovalServiceImpl() {
        initTemplates();
        initTestData();
    }
    
    private void initTemplates() {
        createTemplate(createLeaveTemplate());
        createTemplate(createExpenseTemplate());
        createTemplate(createPurchaseTemplate());
        createTemplate(createContractTemplate());
    }
    
    private ApprovalTemplate createLeaveTemplate() {
        ApprovalTemplate template = new ApprovalTemplate();
        template.setId("tpl-leave");
        template.setType("leave");
        template.setName("请假申请");
        template.setDescription("适用于员工请假申请，支持年假、病假、事假等多种类型");
        template.setIcon("ri-calendar-line");
        template.setColor("#3b82f6");
        template.setEnabled(true);
        template.setCreatedAt(LocalDateTime.now());
        return template;
    }
    
    private ApprovalTemplate createExpenseTemplate() {
        ApprovalTemplate template = new ApprovalTemplate();
        template.setId("tpl-expense");
        template.setType("expense");
        template.setName("费用报销");
        template.setDescription("适用于差旅费、招待费、办公费等各类费用报销");
        template.setIcon("ri-money-cny-circle-line");
        template.setColor("#22c55e");
        template.setEnabled(true);
        template.setCreatedAt(LocalDateTime.now());
        return template;
    }
    
    private ApprovalTemplate createPurchaseTemplate() {
        ApprovalTemplate template = new ApprovalTemplate();
        template.setId("tpl-purchase");
        template.setType("purchase");
        template.setName("采购申请");
        template.setDescription("适用于办公用品、设备、原材料等采购申请");
        template.setIcon("ri-shopping-cart-line");
        template.setColor("#f59e0b");
        template.setEnabled(true);
        template.setCreatedAt(LocalDateTime.now());
        return template;
    }
    
    private ApprovalTemplate createContractTemplate() {
        ApprovalTemplate template = new ApprovalTemplate();
        template.setId("tpl-contract");
        template.setType("contract");
        template.setName("合同审批");
        template.setDescription("适用于各类业务合同的审批流程");
        template.setIcon("ri-file-text-line");
        template.setColor("#8b5cf6");
        template.setEnabled(true);
        template.setCreatedAt(LocalDateTime.now());
        return template;
    }
    
    private void initTestData() {
        Approval a1 = new Approval();
        a1.setId("APP-20250306-001");
        a1.setType("leave");
        a1.setTypeName(TYPE_NAMES.get("leave"));
        a1.setApplicant("张三");
        a1.setApplicantId("user001");
        a1.setApplyTime(LocalDateTime.now().minusDays(1));
        a1.setCurrentNode("部门经理审批");
        a1.setCurrentApprover("李经理");
        a1.setPriority("normal");
        a1.setStatus("pending");
        a1.setReason("因个人原因需要请假处理家庭事务");
        Map<String, Object> detail1 = new HashMap<>();
        detail1.put("leaveType", "annual");
        detail1.put("days", 3);
        detail1.put("startTime", "2026-03-10 09:00");
        detail1.put("endTime", "2026-03-12 18:00");
        a1.setDetail(detail1);
        a1.setCreatedAt(LocalDateTime.now().minusDays(1));
        approvals.put(a1.getId(), a1);
        
        Approval a2 = new Approval();
        a2.setId("APP-20250306-002");
        a2.setType("expense");
        a2.setTypeName(TYPE_NAMES.get("expense"));
        a2.setApplicant("李四");
        a2.setApplicantId("user002");
        a2.setApplyTime(LocalDateTime.now().minusHours(5));
        a2.setCurrentNode("财务审批");
        a2.setCurrentApprover("赵会计");
        a2.setPriority("urgent");
        a2.setStatus("pending");
        a2.setReason("出差上海参加技术交流会");
        Map<String, Object> detail2 = new HashMap<>();
        detail2.put("amount", 2580.50);
        detail2.put("category", "travel");
        a2.setDetail(detail2);
        a2.setCreatedAt(LocalDateTime.now().minusHours(5));
        approvals.put(a2.getId(), a2);
    }
    
    @Override
    public Approval createApproval(ApprovalCreateRequest request) {
        Approval approval = new Approval();
        approval.setId("APP-" + System.currentTimeMillis());
        approval.setType(request.getType());
        approval.setTypeName(TYPE_NAMES.getOrDefault(request.getType(), request.getType()));
        approval.setApplicant("当前用户");
        approval.setApplicantId("currentUser");
        approval.setApplyTime(LocalDateTime.now());
        approval.setCurrentNode("待审批");
        approval.setCurrentApprover(request.getApprover());
        approval.setPriority(request.getPriority() != null ? request.getPriority() : "normal");
        approval.setStatus("pending");
        approval.setReason(request.getReason());
        approval.setDetail(request.getDetail());
        approval.setAttachments(request.getAttachments());
        approval.setCreatedAt(LocalDateTime.now());
        
        approvals.put(approval.getId(), approval);
        return approval;
    }
    
    @Override
    public Approval getApproval(String id) {
        return approvals.get(id);
    }
    
    @Override
    public PageResult<Approval> listApprovals(String status, String type, String applicant, int page, int size) {
        List<Approval> filtered = approvals.values().stream()
            .filter(a -> status == null || status.isEmpty() || a.getStatus().equals(status))
            .filter(a -> type == null || type.isEmpty() || a.getType().equals(type))
            .filter(a -> applicant == null || applicant.isEmpty() || 
                a.getApplicant().contains(applicant) || a.getApplicantId().contains(applicant))
            .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
            .collect(Collectors.toList());
        
        PageResult<Approval> result = new PageResult<>();
        result.setTotal(filtered.size());
        result.setPage(page);
        result.setSize(size);
        
        int start = (page - 1) * size;
        int end = Math.min(start + size, filtered.size());
        result.setList(start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>());
        
        return result;
    }
    
    @Override
    public PageResult<Approval> listMyApprovals(String applicantId, String status, int page, int size) {
        List<Approval> filtered = approvals.values().stream()
            .filter(a -> applicantId == null || applicantId.isEmpty() || a.getApplicantId().equals(applicantId))
            .filter(a -> status == null || status.isEmpty() || a.getStatus().equals(status))
            .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
            .collect(Collectors.toList());
        
        PageResult<Approval> result = new PageResult<>();
        result.setTotal(filtered.size());
        result.setPage(page);
        result.setSize(size);
        
        int start = (page - 1) * size;
        int end = Math.min(start + size, filtered.size());
        result.setList(start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>());
        
        return result;
    }
    
    @Override
    public Approval approve(String id, ApprovalActionRequest request) {
        Approval approval = approvals.get(id);
        if (approval == null) return null;
        
        approval.setStatus("approved");
        approval.setUpdatedAt(LocalDateTime.now());
        return approval;
    }
    
    @Override
    public Approval reject(String id, ApprovalActionRequest request) {
        Approval approval = approvals.get(id);
        if (approval == null) return null;
        
        approval.setStatus("rejected");
        approval.setUpdatedAt(LocalDateTime.now());
        return approval;
    }
    
    @Override
    public Approval withdraw(String id, String applicantId) {
        Approval approval = approvals.get(id);
        if (approval == null || !approval.getApplicantId().equals(applicantId)) return null;
        
        approval.setStatus("withdrawn");
        approval.setUpdatedAt(LocalDateTime.now());
        return approval;
    }
    
    @Override
    public void deleteApproval(String id) {
        approvals.remove(id);
    }
    
    @Override
    public List<ApprovalTemplate> listTemplates() {
        return new ArrayList<>(templates.values());
    }
    
    @Override
    public ApprovalTemplate getTemplate(String id) {
        return templates.get(id);
    }
    
    @Override
    public ApprovalTemplate createTemplate(ApprovalTemplate template) {
        if (template.getId() == null) {
            template.setId("tpl-" + System.currentTimeMillis());
        }
        template.setCreatedAt(LocalDateTime.now());
        templates.put(template.getId(), template);
        return template;
    }
    
    @Override
    public ApprovalTemplate updateTemplate(String id, ApprovalTemplate template) {
        ApprovalTemplate existing = templates.get(id);
        if (existing == null) return null;
        
        template.setId(id);
        template.setCreatedAt(existing.getCreatedAt());
        template.setUpdatedAt(LocalDateTime.now());
        templates.put(id, template);
        return template;
    }
    
    @Override
    public void deleteTemplate(String id) {
        templates.remove(id);
    }
    
    @Override
    public Object getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        List<Approval> all = new ArrayList<>(approvals.values());
        
        stats.put("pending", all.stream().filter(a -> "pending".equals(a.getStatus())).count());
        stats.put("approved", all.stream().filter(a -> "approved".equals(a.getStatus())).count());
        stats.put("rejected", all.stream().filter(a -> "rejected".equals(a.getStatus())).count());
        stats.put("total", all.size());
        
        Map<String, Long> typeStats = all.stream()
            .collect(Collectors.groupingBy(Approval::getType, Collectors.counting()));
        stats.put("typeDistribution", typeStats);
        
        return stats;
    }
}

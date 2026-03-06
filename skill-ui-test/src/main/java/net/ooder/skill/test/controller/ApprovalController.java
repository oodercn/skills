package net.ooder.skill.test.controller;

import net.ooder.skill.test.dto.ApprovalFormDTO;
import net.ooder.skill.test.dto.ApprovalProcessDTO;
import net.ooder.skill.test.dto.ApprovalRecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/approval")
public class ApprovalController {
    
    private static final Logger log = LoggerFactory.getLogger(ApprovalController.class);
    
    private final Map<String, Map<String, Object>> formStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> processStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> recordStore = new ConcurrentHashMap<>();
    
    public ApprovalController() {
        initMockData();
    }
    
    private void initMockData() {
        // 初始化审批表单模板
        createForm("form-leave", "leave", "请假申请表单", "员工请假申请", "active");
        createForm("form-expense", "expense", "费用报销表单", "各类费用报销申请", "active");
        createForm("form-purchase", "purchase", "采购申请表单", "办公用品及设备采购", "active");
        createForm("form-contract", "contract", "合同审批表单", "业务合同审批", "active");
        createForm("form-overtime", "overtime", "加班申请表单", "员工加班及调休", "active");
        createForm("form-business", "business", "出差申请表单", "员工出差申请", "active");
        
        // 初始化审批流程
        createProcess("APP-20250306-001", "leave", "张三", "部门经理审批", "normal", "pending", 
            "请假申请", "因个人原因需要请假处理家庭事务");
        createProcess("APP-20250306-002", "expense", "李四", "财务审批", "urgent", "pending",
            "费用报销", "出差上海参加技术交流会");
        createProcess("APP-20250306-003", "purchase", "王五", "部门经理审批", "normal", "pending",
            "采购申请", "新员工入职配置开发环境");
        createProcess("APP-20250305-001", "leave", "张三", "-", "normal", "approved",
            "请假申请", "身体不适");
        createProcess("APP-20250304-002", "expense", "李四", "李经理", "normal", "rejected",
            "费用报销", "客户招待费用");
        createProcess("APP-20250303-003", "contract", "王五", "张总监", "high", "pending",
            "合同审批", "技术服务合同签署");
        
        // 初始化审批记录
        createRecord("REC-001", "APP-20250305-001", "leave", "张三", "李经理", "approved", "同意请假");
        createRecord("REC-002", "APP-20250304-002", "expense", "李四", "李经理", "rejected", "费用超标，请重新申请");
        createRecord("REC-003", "APP-20250303-003", "contract", "王五", "张总监", "approved", "合同条款无误，同意签署");
    }
    
    private void createForm(String id, String type, String title, String description, String status) {
        Map<String, Object> form = new HashMap<>();
        form.put("id", id);
        form.put("type", type);
        form.put("title", title);
        form.put("description", description);
        form.put("status", status);
        form.put("createdAt", System.currentTimeMillis() - (long)(Math.random() * 86400000 * 30));
        form.put("updatedAt", System.currentTimeMillis());
        formStore.put(id, form);
    }
    
    private void createProcess(String id, String type, String applicant, String currentNode, 
                               String priority, String status, String title, String reason) {
        Map<String, Object> process = new HashMap<>();
        process.put("id", id);
        process.put("type", type);
        process.put("applicant", applicant);
        process.put("currentNode", currentNode);
        process.put("priority", priority);
        process.put("status", status);
        process.put("title", title);
        process.put("reason", reason);
        process.put("applyTime", System.currentTimeMillis() - (long)(Math.random() * 86400000 * 5));
        process.put("createdAt", System.currentTimeMillis());
        process.put("updatedAt", System.currentTimeMillis());
        
        // 创建时间线
        List<Map<String, Object>> timeline = new ArrayList<>();
        Map<String, Object> submitNode = new HashMap<>();
        submitNode.put("node", "提交申请");
        submitNode.put("operator", applicant);
        submitNode.put("time", process.get("applyTime"));
        submitNode.put("status", "approved");
        submitNode.put("comment", "");
        timeline.add(submitNode);
        
        if (!"pending".equals(status)) {
            Map<String, Object> approveNode = new HashMap<>();
            approveNode.put("node", currentNode);
            approveNode.put("operator", "系统");
            approveNode.put("time", System.currentTimeMillis());
            approveNode.put("status", status);
            approveNode.put("comment", "自动审批");
            timeline.add(approveNode);
        } else {
            Map<String, Object> currentApproveNode = new HashMap<>();
            currentApproveNode.put("node", currentNode);
            currentApproveNode.put("operator", "待审批");
            currentApproveNode.put("time", "");
            currentApproveNode.put("status", "pending");
            currentApproveNode.put("comment", "");
            timeline.add(currentApproveNode);
        }
        
        process.put("timeline", timeline);
        processStore.put(id, process);
    }
    
    private void createRecord(String id, String processId, String type, String applicant, 
                              String approver, String action, String comment) {
        Map<String, Object> record = new HashMap<>();
        record.put("id", id);
        record.put("processId", processId);
        record.put("type", type);
        record.put("applicant", applicant);
        record.put("approver", approver);
        record.put("action", action);
        record.put("comment", comment);
        record.put("time", System.currentTimeMillis() - (long)(Math.random() * 86400000 * 3));
        record.put("createdAt", System.currentTimeMillis());
        recordStore.put(id, record);
    }
    
    // ==================== 表单管理API ====================
    
    @GetMapping("/forms")
    public ResponseEntity<Map<String, Object>> getForms(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        
        log.info("[getForms] type: {}, status: {}", type, status);
        
        List<Map<String, Object>> forms = new ArrayList<>(formStore.values());
        
        if (type != null && !type.isEmpty()) {
            forms = forms.stream()
                .filter(f -> type.equals(f.get("type")))
                .collect(Collectors.toList());
        }
        
        if (status != null && !status.isEmpty()) {
            forms = forms.stream()
                .filter(f -> status.equals(f.get("status")))
                .collect(Collectors.toList());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("forms", forms);
        data.put("total", forms.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/forms/{id}")
    public ResponseEntity<Map<String, Object>> getForm(@PathVariable String id) {
        log.info("[getForm] id: {}", id);
        
        Map<String, Object> form = formStore.get(id);
        
        Map<String, Object> result = new HashMap<>();
        if (form != null) {
            result.put("status", "success");
            result.put("data", form);
        } else {
            result.put("status", "error");
            result.put("message", "表单不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/forms")
    public ResponseEntity<Map<String, Object>> createForm(@RequestBody ApprovalFormDTO form) {
        log.info("[createForm] form: {}", form.getTitle());
        
        String id = "form-" + System.currentTimeMillis();
        form.setId(id);
        
        Map<String, Object> formMap = convertFormToMap(form);
        formMap.put("createdAt", System.currentTimeMillis());
        formMap.put("updatedAt", System.currentTimeMillis());
        
        if (form.getStatus() == null) {
            formMap.put("status", "active");
        }
        
        formStore.put(id, formMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", formMap);
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/forms/{id}")
    public ResponseEntity<Map<String, Object>> updateForm(@PathVariable String id, @RequestBody ApprovalFormDTO form) {
        log.info("[updateForm] id: {}, form: {}", id, form.getTitle());
        
        Map<String, Object> existing = formStore.get(id);
        
        if (existing == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "表单不存在");
            return ResponseEntity.ok(result);
        }
        
        form.setId(id);
        Map<String, Object> formMap = convertFormToMap(form);
        formMap.put("createdAt", existing.get("createdAt"));
        formMap.put("updatedAt", System.currentTimeMillis());
        
        formStore.put(id, formMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", formMap);
        
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/forms/{id}")
    public ResponseEntity<Map<String, Object>> deleteForm(@PathVariable String id) {
        log.info("[deleteForm] id: {}", id);
        
        Map<String, Object> removed = formStore.remove(id);
        
        Map<String, Object> result = new HashMap<>();
        if (removed != null) {
            result.put("status", "success");
            result.put("message", "表单已删除");
        } else {
            result.put("status", "error");
            result.put("message", "表单不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    private Map<String, Object> convertFormToMap(ApprovalFormDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getId() != null) map.put("id", dto.getId());
        if (dto.getType() != null) map.put("type", dto.getType());
        if (dto.getTitle() != null) map.put("title", dto.getTitle());
        if (dto.getDescription() != null) map.put("description", dto.getDescription());
        if (dto.getFormConfig() != null) map.put("formConfig", dto.getFormConfig());
        if (dto.getFields() != null) map.put("fields", dto.getFields());
        if (dto.getStatus() != null) map.put("status", dto.getStatus());
        return map;
    }
    
    // ==================== 流程管理API ====================
    
    @GetMapping("/processes")
    public ResponseEntity<Map<String, Object>> getProcesses(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String applicant) {
        
        log.info("[getProcesses] status: {}, type: {}, applicant: {}", status, type, applicant);
        
        List<Map<String, Object>> processes = new ArrayList<>(processStore.values());
        
        if (status != null && !status.isEmpty()) {
            processes = processes.stream()
                .filter(p -> status.equals(p.get("status")))
                .collect(Collectors.toList());
        }
        
        if (type != null && !type.isEmpty()) {
            processes = processes.stream()
                .filter(p -> type.equals(p.get("type")))
                .collect(Collectors.toList());
        }
        
        if (applicant != null && !applicant.isEmpty()) {
            processes = processes.stream()
                .filter(p -> applicant.equals(p.get("applicant")))
                .collect(Collectors.toList());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("processes", processes);
        data.put("total", processes.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/processes/{id}")
    public ResponseEntity<Map<String, Object>> getProcess(@PathVariable String id) {
        log.info("[getProcess] id: {}", id);
        
        Map<String, Object> process = processStore.get(id);
        
        Map<String, Object> result = new HashMap<>();
        if (process != null) {
            result.put("status", "success");
            result.put("data", process);
        } else {
            result.put("status", "error");
            result.put("message", "流程不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/processes")
    public ResponseEntity<Map<String, Object>> createProcess(@RequestBody ApprovalProcessDTO process) {
        log.info("[createProcess] process: {}", process.getTitle());
        
        String id = "APP-" + System.currentTimeMillis();
        process.setId(id);
        
        Map<String, Object> processMap = convertProcessToMap(process);
        processMap.put("createdAt", System.currentTimeMillis());
        processMap.put("updatedAt", System.currentTimeMillis());
        
        if (process.getStatus() == null) {
            processMap.put("status", "pending");
        }
        
        // 创建初始时间线
        List<Map<String, Object>> timeline = new ArrayList<>();
        Map<String, Object> submitNode = new HashMap<>();
        submitNode.put("node", "提交申请");
        submitNode.put("operator", process.getApplicant());
        submitNode.put("time", System.currentTimeMillis());
        submitNode.put("status", "approved");
        submitNode.put("comment", "");
        timeline.add(submitNode);
        
        Map<String, Object> approveNode = new HashMap<>();
        approveNode.put("node", process.getCurrentNode());
        approveNode.put("operator", "待审批");
        approveNode.put("time", "");
        approveNode.put("status", "pending");
        approveNode.put("comment", "");
        timeline.add(approveNode);
        
        processMap.put("timeline", timeline);
        processStore.put(id, processMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", processMap);
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/processes/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveProcess(@PathVariable String id,
                                                               @RequestParam String approver,
                                                               @RequestParam String comment) {
        log.info("[approveProcess] id: {}, approver: {}", id, approver);
        
        Map<String, Object> process = processStore.get(id);
        
        if (process == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "流程不存在");
            return ResponseEntity.ok(result);
        }
        
        process.put("status", "approved");
        process.put("currentNode", "-");
        process.put("updatedAt", System.currentTimeMillis());
        process.put("completedAt", System.currentTimeMillis());
        
        // 更新时间线
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> timeline = (List<Map<String, Object>>) process.get("timeline");
        if (timeline != null && !timeline.isEmpty()) {
            Map<String, Object> lastNode = timeline.get(timeline.size() - 1);
            lastNode.put("operator", approver);
            lastNode.put("time", System.currentTimeMillis());
            lastNode.put("status", "approved");
            lastNode.put("comment", comment);
        }
        
        // 创建审批记录
        createRecord("REC-" + System.currentTimeMillis(), id, (String) process.get("type"),
            (String) process.get("applicant"), approver, "approved", comment);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", process);
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/processes/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectProcess(@PathVariable String id,
                                                              @RequestParam String approver,
                                                              @RequestParam String comment) {
        log.info("[rejectProcess] id: {}, approver: {}", id, approver);
        
        Map<String, Object> process = processStore.get(id);
        
        if (process == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "流程不存在");
            return ResponseEntity.ok(result);
        }
        
        process.put("status", "rejected");
        process.put("currentNode", "-");
        process.put("updatedAt", System.currentTimeMillis());
        process.put("completedAt", System.currentTimeMillis());
        
        // 更新时间线
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> timeline = (List<Map<String, Object>>) process.get("timeline");
        if (timeline != null && !timeline.isEmpty()) {
            Map<String, Object> lastNode = timeline.get(timeline.size() - 1);
            lastNode.put("operator", approver);
            lastNode.put("time", System.currentTimeMillis());
            lastNode.put("status", "rejected");
            lastNode.put("comment", comment);
        }
        
        // 创建审批记录
        createRecord("REC-" + System.currentTimeMillis(), id, (String) process.get("type"),
            (String) process.get("applicant"), approver, "rejected", comment);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", process);
        
        return ResponseEntity.ok(result);
    }
    
    private Map<String, Object> convertProcessToMap(ApprovalProcessDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getId() != null) map.put("id", dto.getId());
        if (dto.getFormId() != null) map.put("formId", dto.getFormId());
        if (dto.getApplicant() != null) map.put("applicant", dto.getApplicant());
        if (dto.getApprover() != null) map.put("approver", dto.getApprover());
        if (dto.getTitle() != null) map.put("title", dto.getTitle());
        if (dto.getType() != null) map.put("type", dto.getType());
        if (dto.getPriority() != null) map.put("priority", dto.getPriority());
        if (dto.getFormData() != null) map.put("formData", dto.getFormData());
        if (dto.getStatus() != null) map.put("status", dto.getStatus());
        if (dto.getCurrentNode() != null) map.put("currentNode", dto.getCurrentNode());
        if (dto.getTimeline() != null) map.put("timeline", dto.getTimeline());
        if (dto.getAttachments() != null) map.put("attachments", dto.getAttachments());
        return map;
    }
    
    // ==================== 记录管理API ====================
    
    @GetMapping("/records")
    public ResponseEntity<Map<String, Object>> getRecords(
            @RequestParam(required = false) String processId,
            @RequestParam(required = false) String type) {
        
        log.info("[getRecords] processId: {}, type: {}", processId, type);
        
        List<Map<String, Object>> records = new ArrayList<>(recordStore.values());
        
        if (processId != null && !processId.isEmpty()) {
            records = records.stream()
                .filter(r -> processId.equals(r.get("processId")))
                .collect(Collectors.toList());
        }
        
        if (type != null && !type.isEmpty()) {
            records = records.stream()
                .filter(r -> type.equals(r.get("type")))
                .collect(Collectors.toList());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", records.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    // ==================== 统计API ====================
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("[getStatistics]");
        
        long pending = processStore.values().stream()
            .filter(p -> "pending".equals(p.get("status")))
            .count();
        long approved = processStore.values().stream()
            .filter(p -> "approved".equals(p.get("status")))
            .count();
        long rejected = processStore.values().stream()
            .filter(p -> "rejected".equals(p.get("status")))
            .count();
        long total = processStore.size();
        
        // 按类型统计
        Map<String, Long> typeStats = processStore.values().stream()
            .collect(Collectors.groupingBy(p -> (String) p.get("type"), Collectors.counting()));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", pending);
        stats.put("approved", approved);
        stats.put("rejected", rejected);
        stats.put("total", total);
        stats.put("typeDistribution", typeStats);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", stats);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingApprovals() {
        log.info("[getPendingApprovals]");
        
        List<Map<String, Object>> pending = processStore.values().stream()
            .filter(p -> "pending".equals(p.get("status")))
            .collect(Collectors.toList());
        
        Map<String, Object> data = new HashMap<>();
        data.put("processes", pending);
        data.put("total", pending.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/submitted")
    public ResponseEntity<Map<String, Object>> getSubmittedApprovals(
            @RequestParam(required = false) String applicant) {
        
        log.info("[getSubmittedApprovals] applicant: {}", applicant);
        
        List<Map<String, Object>> submitted = processStore.values().stream()
            .filter(p -> applicant == null || applicant.equals(p.get("applicant")))
            .collect(Collectors.toList());
        
        Map<String, Object> data = new HashMap<>();
        data.put("processes", submitted);
        data.put("total", submitted.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/templates")
    public ResponseEntity<Map<String, Object>> getTemplates() {
        log.info("[getTemplates]");
        
        List<Map<String, Object>> templates = formStore.values().stream()
            .filter(f -> "active".equals(f.get("status")))
            .collect(Collectors.toList());
        
        Map<String, Object> data = new HashMap<>();
        data.put("templates", templates);
        data.put("total", templates.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
}

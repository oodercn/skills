package net.ooder.skill.dailyreport.service;

import net.ooder.skill.dailyreport.dto.PageResult;
import net.ooder.skill.dailyreport.dto.ReportCreateRequest;
import net.ooder.skill.dailyreport.dto.ReviewRequest;
import net.ooder.skill.dailyreport.model.DailyReport;
import net.ooder.skill.dailyreport.model.DailyReport.WorkItem;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DailyReportServiceImpl implements DailyReportService {
    
    private final Map<String, DailyReport> reports = new ConcurrentHashMap<>();
    
    public DailyReportServiceImpl() {
        initTestData();
    }
    
    private void initTestData() {
        DailyReport r1 = new DailyReport();
        r1.setId("RPT-20260323-001");
        r1.setUserId("user001");
        r1.setUserName("张三");
        r1.setDepartment("技术研发部");
        r1.setReportDate(LocalDate.now());
        r1.setContent("今日完成了用户中心模块的开发，修复了若干bug");
        r1.setWorkItems(Arrays.asList(
            createWorkItem("用户中心开发", "完成登录、注册功能", 80, "进行中"),
            createWorkItem("Bug修复", "修复了3个线上bug", 100, "已完成")
        ));
        r1.setProblems(Arrays.asList("接口文档需要更新"));
        r1.setPlans(Arrays.asList("明天继续完成用户中心剩余功能", "参加技术评审会议"));
        r1.setStatus("submitted");
        r1.setCreatedAt(LocalDateTime.now().minusHours(2));
        reports.put(r1.getId(), r1);
        
        DailyReport r2 = new DailyReport();
        r2.setId("RPT-20260323-002");
        r2.setUserId("user002");
        r2.setUserName("李四");
        r2.setDepartment("产品设计部");
        r2.setReportDate(LocalDate.now());
        r2.setContent("完成V2.0版本PRD文档初稿");
        r2.setWorkItems(Arrays.asList(
            createWorkItem("PRD文档", "完成核心功能模块设计", 60, "进行中")
        ));
        r2.setProblems(Arrays.asList("需要与运营确认需求细节"));
        r2.setPlans(Arrays.asList("明天与运营开会确认需求"));
        r2.setStatus("draft");
        r2.setCreatedAt(LocalDateTime.now().minusHours(5));
        reports.put(r2.getId(), r2);
    }
    
    private WorkItem createWorkItem(String content, String progress, int percentage, String status) {
        WorkItem item = new WorkItem();
        item.setId("WI-" + System.nanoTime());
        item.setContent(content);
        item.setProgress(progress);
        item.setPercentage(percentage);
        item.setStatus(status);
        return item;
    }
    
    @Override
    public DailyReport createReport(ReportCreateRequest request) {
        DailyReport report = new DailyReport();
        report.setId("RPT-" + request.getReportDate().toString().replace("-", "") + "-" + System.currentTimeMillis() % 1000);
        report.setUserId("currentUser");
        report.setUserName("当前用户");
        report.setDepartment("技术研发部");
        report.setReportDate(request.getReportDate());
        report.setContent(request.getContent());
        
        if (request.getWorkItems() != null) {
            List<WorkItem> items = request.getWorkItems().stream()
                .map(wi -> {
                    WorkItem item = new WorkItem();
                    item.setId("WI-" + System.nanoTime());
                    item.setContent(wi.getContent());
                    item.setProgress(wi.getProgress());
                    item.setPercentage(wi.getPercentage());
                    item.setStatus(wi.getStatus());
                    return item;
                })
                .collect(Collectors.toList());
            report.setWorkItems(items);
        }
        
        report.setProblems(request.getProblems());
        report.setPlans(request.getPlans());
        report.setStatus("draft");
        report.setCreatedAt(LocalDateTime.now());
        
        reports.put(report.getId(), report);
        return report;
    }
    
    @Override
    public DailyReport getReport(String id) {
        return reports.get(id);
    }
    
    @Override
    public DailyReport getReportByDate(String userId, LocalDate date) {
        return reports.values().stream()
            .filter(r -> r.getUserId().equals(userId) && r.getReportDate().equals(date))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public PageResult<DailyReport> listReports(String userId, String status, LocalDate startDate, LocalDate endDate, int page, int size) {
        List<DailyReport> filtered = reports.values().stream()
            .filter(r -> userId == null || userId.isEmpty() || r.getUserId().equals(userId))
            .filter(r -> status == null || status.isEmpty() || r.getStatus().equals(status))
            .filter(r -> startDate == null || !r.getReportDate().isBefore(startDate))
            .filter(r -> endDate == null || !r.getReportDate().isAfter(endDate))
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
            .collect(Collectors.toList());
        
        PageResult<DailyReport> result = new PageResult<>();
        result.setTotal(filtered.size());
        result.setPage(page);
        result.setSize(size);
        
        int start = (page - 1) * size;
        int end = Math.min(start + size, filtered.size());
        result.setList(start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>());
        
        return result;
    }
    
    @Override
    public PageResult<DailyReport> listAllReports(String department, String status, LocalDate startDate, LocalDate endDate, int page, int size) {
        List<DailyReport> filtered = reports.values().stream()
            .filter(r -> department == null || department.isEmpty() || r.getDepartment().equals(department))
            .filter(r -> status == null || status.isEmpty() || r.getStatus().equals(status))
            .filter(r -> startDate == null || !r.getReportDate().isBefore(startDate))
            .filter(r -> endDate == null || !r.getReportDate().isAfter(endDate))
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
            .collect(Collectors.toList());
        
        PageResult<DailyReport> result = new PageResult<>();
        result.setTotal(filtered.size());
        result.setPage(page);
        result.setSize(size);
        
        int start = (page - 1) * size;
        int end = Math.min(start + size, filtered.size());
        result.setList(start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>());
        
        return result;
    }
    
    @Override
    public DailyReport updateReport(String id, ReportCreateRequest request) {
        DailyReport report = reports.get(id);
        if (report == null) return null;
        
        report.setContent(request.getContent());
        report.setReportDate(request.getReportDate());
        report.setProblems(request.getProblems());
        report.setPlans(request.getPlans());
        report.setUpdatedAt(LocalDateTime.now());
        
        return report;
    }
    
    @Override
    public void deleteReport(String id) {
        reports.remove(id);
    }
    
    @Override
    public DailyReport submitReport(String id) {
        DailyReport report = reports.get(id);
        if (report == null) return null;
        
        report.setStatus("submitted");
        report.setUpdatedAt(LocalDateTime.now());
        return report;
    }
    
    @Override
    public DailyReport reviewReport(String id, ReviewRequest request) {
        DailyReport report = reports.get(id);
        if (report == null) return null;
        
        if ("approve".equals(request.getAction())) {
            report.setStatus("approved");
        } else if ("reject".equals(request.getAction())) {
            report.setStatus("rejected");
        }
        report.setComment(request.getComment());
        report.setReviewer("管理员");
        report.setReviewerId("admin");
        report.setReviewedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        
        return report;
    }
    
    @Override
    public Map<String, Object> getStatistics(String userId) {
        Map<String, Object> stats = new HashMap<>();
        List<DailyReport> userReports = reports.values().stream()
            .filter(r -> r.getUserId().equals(userId))
            .collect(Collectors.toList());
        
        stats.put("total", userReports.size());
        stats.put("draft", userReports.stream().filter(r -> "draft".equals(r.getStatus())).count());
        stats.put("submitted", userReports.stream().filter(r -> "submitted".equals(r.getStatus())).count());
        stats.put("approved", userReports.stream().filter(r -> "approved".equals(r.getStatus())).count());
        stats.put("thisMonth", userReports.stream()
            .filter(r -> r.getReportDate().getMonth() == LocalDate.now().getMonth())
            .count());
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getDepartmentStatistics(String department) {
        Map<String, Object> stats = new HashMap<>();
        List<DailyReport> deptReports = reports.values().stream()
            .filter(r -> department == null || department.isEmpty() || r.getDepartment().equals(department))
            .collect(Collectors.toList());
        
        stats.put("total", deptReports.size());
        stats.put("todaySubmitted", deptReports.stream()
            .filter(r -> r.getReportDate().equals(LocalDate.now()) && "submitted".equals(r.getStatus()))
            .count());
        stats.put("todayPending", deptReports.stream()
            .filter(r -> r.getReportDate().equals(LocalDate.now()) && "draft".equals(r.getStatus()))
            .count());
        
        return stats;
    }
    
    @Override
    public List<Map<String, Object>> getSubmissionRanking(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> counts = reports.values().stream()
            .filter(r -> !r.getReportDate().isBefore(startDate) && !r.getReportDate().isAfter(endDate))
            .filter(r -> "submitted".equals(r.getStatus()) || "approved".equals(r.getStatus()))
            .collect(Collectors.groupingBy(DailyReport::getUserName, Collectors.counting()));
        
        return counts.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .map(e -> {
                Map<String, Object> item = new HashMap<>();
                item.put("name", e.getKey());
                item.put("count", e.getValue());
                return item;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public byte[] exportReports(String department, LocalDate startDate, LocalDate endDate, String format) {
        return "日报导出数据".getBytes();
    }
}

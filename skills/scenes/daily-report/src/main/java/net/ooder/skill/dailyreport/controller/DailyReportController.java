package net.ooder.skill.dailyreport.controller;

import net.ooder.skill.dailyreport.dto.ApiResponse;
import net.ooder.skill.dailyreport.dto.PageResult;
import net.ooder.skill.dailyreport.dto.ReportCreateRequest;
import net.ooder.skill.dailyreport.dto.ReviewRequest;
import net.ooder.skill.dailyreport.model.DailyReport;
import net.ooder.skill.dailyreport.service.DailyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/daily-report")
public class DailyReportController {
    
    @Autowired
    private DailyReportService dailyReportService;
    
    @PostMapping("/submit")
    public ApiResponse<DailyReport> createReport(@RequestBody ReportCreateRequest request) {
        return new ApiResponse<>(dailyReportService.createReport(request));
    }
    
    @GetMapping("/reports/{id}")
    public ApiResponse<DailyReport> getReport(@PathVariable String id) {
        DailyReport report = dailyReportService.getReport(id);
        if (report == null) {
            return new ApiResponse<>("error", "日报不存在");
        }
        return new ApiResponse<>(report);
    }
    
    @GetMapping("/reports/by-date")
    public ApiResponse<DailyReport> getReportByDate(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyReport report = dailyReportService.getReportByDate(userId, date);
        if (report == null) {
            return new ApiResponse<>("error", "该日期没有日报");
        }
        return new ApiResponse<>(report);
    }
    
    @GetMapping("/my-reports")
    public ApiResponse<PageResult<DailyReport>> listMyReports(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(dailyReportService.listReports(userId, status, startDate, endDate, page, size));
    }
    
    @GetMapping("/reports")
    public ApiResponse<PageResult<DailyReport>> listAllReports(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(dailyReportService.listAllReports(department, status, startDate, endDate, page, size));
    }
    
    @PutMapping("/reports/{id}")
    public ApiResponse<DailyReport> updateReport(
            @PathVariable String id,
            @RequestBody ReportCreateRequest request) {
        DailyReport report = dailyReportService.updateReport(id, request);
        if (report == null) {
            return new ApiResponse<>("error", "日报不存在");
        }
        return new ApiResponse<>(report);
    }
    
    @DeleteMapping("/reports/{id}")
    public ApiResponse<Void> deleteReport(@PathVariable String id) {
        dailyReportService.deleteReport(id);
        return new ApiResponse<>(null);
    }
    
    @PostMapping("/reports/{id}/submit")
    public ApiResponse<DailyReport> submitReport(@PathVariable String id) {
        DailyReport report = dailyReportService.submitReport(id);
        if (report == null) {
            return new ApiResponse<>("error", "日报不存在");
        }
        return new ApiResponse<>(report);
    }
    
    @PostMapping("/reports/{id}/review")
    public ApiResponse<DailyReport> reviewReport(
            @PathVariable String id,
            @RequestBody ReviewRequest request) {
        DailyReport report = dailyReportService.reviewReport(id, request);
        if (report == null) {
            return new ApiResponse<>("error", "日报不存在");
        }
        return new ApiResponse<>(report);
    }
    
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics(
            @RequestParam(required = false) String userId) {
        return new ApiResponse<>(dailyReportService.getStatistics(userId));
    }
    
    @GetMapping("/department-statistics")
    public ApiResponse<Map<String, Object>> getDepartmentStatistics(
            @RequestParam(required = false) String department) {
        return new ApiResponse<>(dailyReportService.getDepartmentStatistics(department));
    }
    
    @GetMapping("/ranking")
    public ApiResponse<List<Map<String, Object>>> getSubmissionRanking(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return new ApiResponse<>(dailyReportService.getSubmissionRanking(startDate, endDate));
    }
    
    @GetMapping("/export")
    public void exportReports(
            @RequestParam(required = false) String department,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "xlsx") String format,
            HttpServletResponse response) {
        try {
            byte[] data = dailyReportService.exportReports(department, startDate, endDate, format);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=reports." + format);
            response.getOutputStream().write(data);
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }
}

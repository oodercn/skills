package net.ooder.skill.dailyreport.service;

import net.ooder.skill.dailyreport.dto.PageResult;
import net.ooder.skill.dailyreport.dto.ReportCreateRequest;
import net.ooder.skill.dailyreport.dto.ReviewRequest;
import net.ooder.skill.dailyreport.model.DailyReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DailyReportService {
    
    DailyReport createReport(ReportCreateRequest request);
    
    DailyReport getReport(String id);
    
    DailyReport getReportByDate(String userId, LocalDate date);
    
    PageResult<DailyReport> listReports(String userId, String status, LocalDate startDate, LocalDate endDate, int page, int size);
    
    PageResult<DailyReport> listAllReports(String department, String status, LocalDate startDate, LocalDate endDate, int page, int size);
    
    DailyReport updateReport(String id, ReportCreateRequest request);
    
    void deleteReport(String id);
    
    DailyReport submitReport(String id);
    
    DailyReport reviewReport(String id, ReviewRequest request);
    
    Map<String, Object> getStatistics(String userId);
    
    Map<String, Object> getDepartmentStatistics(String department);
    
    List<Map<String, Object>> getSubmissionRanking(LocalDate startDate, LocalDate endDate);
    
    byte[] exportReports(String department, LocalDate startDate, LocalDate endDate, String format);
}

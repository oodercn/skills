package net.ooder.skill.scene.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.scene.dto.report.*;
import net.ooder.skill.scene.service.ReportSkillService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ReportSkillServiceMemoryImpl implements ReportSkillService {

    private final Map<String, ReportDetailDTO> reports = new ConcurrentHashMap<>();
    private final Map<String, List<ReportDetailDTO>> reportsBySceneGroup = new ConcurrentHashMap<>();

    @Override
    public EmailFetchResultDTO fetchEmails(EmailFetchRequestDTO request) {
        log.info("Fetching emails for user: {}", request.getUserId());
        
        EmailFetchResultDTO result = new EmailFetchResultDTO();
        result.setUserId(request.getUserId());
        
        List<EmailFetchResultDTO.EmailItem> emails = new ArrayList<>();
        
        EmailFetchResultDTO.EmailItem email1 = new EmailFetchResultDTO.EmailItem();
        email1.setSubject("项目进度汇报");
        email1.setFrom("张三");
        email1.setTime(System.currentTimeMillis() - 3600000);
        email1.setSummary("完成了用户登录功能的开发，进度符合预期");
        emails.add(email1);
        
        EmailFetchResultDTO.EmailItem email2 = new EmailFetchResultDTO.EmailItem();
        email2.setSubject("客户需求确认");
        email2.setFrom("李四");
        email2.setTime(System.currentTimeMillis() - 7200000);
        email2.setSummary("确认了新功能的需求细节，预计下周开始开发");
        emails.add(email2);
        
        EmailFetchResultDTO.EmailItem email3 = new EmailFetchResultDTO.EmailItem();
        email3.setSubject("跨部门协作事项");
        email3.setFrom("王五");
        email3.setTime(System.currentTimeMillis() - 10800000);
        email3.setSummary("协调了测试资源和上线时间");
        emails.add(email3);
        
        result.setEmails(emails);
        result.setSummary("处理了" + emails.size() + "封邮件，包括项目进度汇报和客户需求确认");
        result.setWorkItems(Arrays.asList(
            "处理了" + emails.size() + "封邮件，包括项目进度汇报和客户需求确认",
            "回复了重要邮件，协调了跨部门协作事项"
        ));
        
        return result;
    }

    @Override
    public GitFetchResultDTO fetchGitCommits(GitFetchRequestDTO request) {
        log.info("Fetching git commits for user: {}", request.getUserId());
        
        GitFetchResultDTO result = new GitFetchResultDTO();
        result.setUserId(request.getUserId());
        result.setRepoUrl(request.getRepoUrl());
        
        List<GitFetchResultDTO.CommitItem> commits = new ArrayList<>();
        
        GitFetchResultDTO.CommitItem commit1 = new GitFetchResultDTO.CommitItem();
        commit1.setCommitId("abc123");
        commit1.setMessage("feat: 完成用户登录功能开发");
        commit1.setBranch("feature/user-login");
        commit1.setTime(System.currentTimeMillis() - 3600000);
        commit1.setFilesChanged(5);
        commits.add(commit1);
        
        GitFetchResultDTO.CommitItem commit2 = new GitFetchResultDTO.CommitItem();
        commit2.setCommitId("def456");
        commit2.setMessage("fix: 修复日志汇总模块的bug");
        commit2.setBranch("bugfix/report-aggregate");
        commit2.setTime(System.currentTimeMillis() - 7200000);
        commit2.setFilesChanged(3);
        commits.add(commit2);
        
        GitFetchResultDTO.CommitItem commit3 = new GitFetchResultDTO.CommitItem();
        commit3.setCommitId("ghi789");
        commit3.setMessage("perf: 优化数据库查询性能");
        commit3.setBranch("perf/db-query");
        commit3.setTime(System.currentTimeMillis() - 10800000);
        commit3.setFilesChanged(2);
        commits.add(commit3);
        
        result.setCommits(commits);
        result.setSummary("提交了" + commits.size() + "个commit，涉及登录功能、bug修复和性能优化");
        result.setWorkItems(Arrays.asList(
            "完成了用户登录功能的开发 (commit: abc123)",
            "修复了日志汇总模块的bug (commit: def456)",
            "优化了数据库查询性能 (commit: ghi789)"
        ));
        
        return result;
    }

    @Override
    public AiGenerateResultDTO aiGenerate(AiGenerateRequestDTO request) {
        log.info("AI generating content for user: {}", request.getUserId());
        
        AiGenerateResultDTO result = new AiGenerateResultDTO();
        result.setUserId(request.getUserId());
        
        List<String> workItems = new ArrayList<>();
        workItems.add("完成了分配的开发任务");
        workItems.add("参加了团队会议");
        workItems.add("处理了日常事务");
        result.setWorkItems(workItems);
        
        List<String> planItems = new ArrayList<>();
        planItems.add("继续开发待完成的功能");
        planItems.add("跟进项目进度");
        planItems.add("处理待办事项");
        result.setPlanItems(planItems);
        
        result.setIssuesSuggestion("暂无问题或建议");
        
        return result;
    }

    @Override
    public ReportSubmitResultDTO submitReport(ReportSubmitRequestDTO request) {
        log.info("Submitting report for scene group: {}", request.getSceneGroupId());
        
        String reportId = "report-" + System.currentTimeMillis();
        
        ReportDetailDTO detail = new ReportDetailDTO();
        detail.setReportId(reportId);
        detail.setSceneGroupId(request.getSceneGroupId());
        detail.setUserId(request.getUserId());
        detail.setUserName(request.getUserName());
        detail.setWorkItems(request.getWorkItems());
        detail.setPlanItems(request.getPlanItems());
        detail.setIssues(request.getIssues());
        detail.setAttachments(request.getAttachments());
        detail.setSubmitTime(System.currentTimeMillis());
        detail.setDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        detail.setStatus("submitted");
        
        reports.put(reportId, detail);
        reportsBySceneGroup.computeIfAbsent(request.getSceneGroupId(), k -> new ArrayList<>()).add(detail);
        
        ReportSubmitResultDTO result = new ReportSubmitResultDTO();
        result.setReportId(reportId);
        result.setStatus("success");
        result.setMessage("日志提交成功");
        
        return result;
    }

    @Override
    public List<ReportHistoryDTO> getHistory(String sceneGroupId, Integer limit) {
        log.info("Getting history for scene group: {}", sceneGroupId);
        
        List<ReportDetailDTO> reportList = reportsBySceneGroup.getOrDefault(sceneGroupId, new ArrayList<>());
        
        int count = limit != null ? Math.min(limit, reportList.size()) : reportList.size();
        
        List<ReportHistoryDTO> history = new ArrayList<>();
        for (int i = reportList.size() - 1; i >= 0 && history.size() < count; i--) {
            ReportDetailDTO detail = reportList.get(i);
            ReportHistoryDTO dto = new ReportHistoryDTO();
            dto.setReportId(detail.getReportId());
            dto.setDate(detail.getDate());
            dto.setStatus(detail.getStatus());
            dto.setSummary(detail.getWorkItems() != null && !detail.getWorkItems().isEmpty() 
                ? detail.getWorkItems().get(0) : "无内容");
            history.add(dto);
        }
        
        return history;
    }

    @Override
    public ReportDetailDTO getDetail(String reportId) {
        log.info("Getting detail for report: {}", reportId);
        return reports.get(reportId);
    }

    @Override
    public ReportAggregateResultDTO aggregateReports(ReportAggregateRequestDTO request) {
        log.info("Aggregating reports for scene group: {}", request.getSceneGroupId());
        
        List<ReportDetailDTO> reportList = reportsBySceneGroup.getOrDefault(request.getSceneGroupId(), new ArrayList<>());
        
        ReportAggregateResultDTO result = new ReportAggregateResultDTO();
        result.setSceneGroupId(request.getSceneGroupId());
        result.setTotalCount(reportList.size());
        result.setSubmittedCount((int) reportList.stream().filter(r -> "submitted".equals(r.getStatus())).count());
        
        List<String> allWorkItems = new ArrayList<>();
        reportList.forEach(r -> {
            if (r.getWorkItems() != null) {
                allWorkItems.addAll(r.getWorkItems());
            }
        });
        result.setAllWorkItems(allWorkItems);
        result.setSummary("共收到 " + reportList.size() + " 份日志");
        
        return result;
    }

    @Override
    public ReportAnalyzeResultDTO analyzeReports(ReportAnalyzeRequestDTO request) {
        log.info("Analyzing reports for scene group: {}", request.getSceneGroupId());
        
        ReportAnalyzeResultDTO result = new ReportAnalyzeResultDTO();
        result.setSceneGroupId(request.getSceneGroupId());
        
        result.setAnalysisSummary("团队整体工作进展顺利，主要完成了功能开发和bug修复。");
        result.setKeyFindings(Arrays.asList(
            "开发进度符合预期",
            "代码质量良好",
            "团队协作顺畅"
        ));
        result.setSuggestions(Arrays.asList(
            "建议加强代码审查",
            "建议增加单元测试覆盖率"
        ));
        
        return result;
    }

    @Override
    public boolean sendRemind(ReportRemindRequestDTO request) {
        log.info("Sending remind for scene group: {}", request.getSceneGroupId());
        return true;
    }

    @Override
    public boolean sendNotify(ReportNotifyRequestDTO request) {
        log.info("Sending notify for scene group: {}", request.getSceneGroupId());
        return true;
    }
}

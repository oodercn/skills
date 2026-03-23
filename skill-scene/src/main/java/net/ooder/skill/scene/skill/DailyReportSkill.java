package net.ooder.skill.scene.skill;

import net.ooder.skill.scene.dto.discovery.CapabilityDTO;
import net.ooder.skill.scene.dto.dailyreport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/skills/daily-report")
public class DailyReportSkill {

    private static final Logger log = LoggerFactory.getLogger(DailyReportSkill.class);

    private final Map<String, List<Report>> reports = new ConcurrentHashMap<>();
    private final Map<String, List<Reminder>> reminders = new ConcurrentHashMap<>();

    @GetMapping("/capabilities")
    public List<CapabilityDTO> getCapabilities() {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        CapabilityDTO remindCap = new CapabilityDTO();
        remindCap.setCapId("report-remind");
        remindCap.setName("鏃ュ織鎻愰啋");
        remindCap.setDescription("瀹氭椂鎻愰啋鍛樺伐鎻愪氦鏃ュ織");
        remindCap.setCategory("notification");
        capabilities.add(remindCap);
        
        CapabilityDTO submitCap = new CapabilityDTO();
        submitCap.setCapId("report-submit");
        submitCap.setName("鏃ュ織鎻愪氦");
        submitCap.setDescription("鍛樺伐鎻愪氦宸ヤ綔鏃ュ織");
        submitCap.setCategory("data-input");
        capabilities.add(submitCap);
        
        CapabilityDTO aggregateCap = new CapabilityDTO();
        aggregateCap.setCapId("report-aggregate");
        aggregateCap.setName("鏃ュ織姹囨€?);
        aggregateCap.setDescription("姹囨€绘墍鏈夊憳宸ユ棩蹇?);
        aggregateCap.setCategory("data-processing");
        capabilities.add(aggregateCap);
        
        CapabilityDTO analyzeCap = new CapabilityDTO();
        analyzeCap.setCapId("report-analyze");
        analyzeCap.setName("鏃ュ織鍒嗘瀽");
        analyzeCap.setDescription("AI鍒嗘瀽鏃ュ織鍐呭");
        analyzeCap.setCategory("intelligence");
        capabilities.add(analyzeCap);
        
        return capabilities;
    }

    @PostMapping("/remind")
    public DailyReportResponseDTO sendReminder(@RequestBody @Valid ReminderRequestDTO request) {
        log.info("Sending reminder with params: sceneGroupId={}, targetUsers={}", 
                request.getSceneGroupId(), request.getTargetUsers());
        
        String sceneGroupId = request.getSceneGroupId();
        List<String> targetUsers = request.getTargetUsers();
        String message = request.getMessage() != null ? request.getMessage() : "璇峰強鏃舵彁浜や粖鏃ュ伐浣滄棩蹇?;
        
        Reminder reminder = new Reminder();
        reminder.setReminderId("remind-" + System.currentTimeMillis());
        reminder.setSceneGroupId(sceneGroupId);
        reminder.setTargetUsers(targetUsers);
        reminder.setMessage(message);
        reminder.setSendTime(System.currentTimeMillis());
        reminder.setStatus("sent");
        
        reminders.computeIfAbsent(sceneGroupId, k -> new ArrayList<>()).add(reminder);
        
        DailyReportResponseDTO response = DailyReportResponseDTO.success("鎻愰啋宸插彂閫?);
        response.setReminderId(reminder.getReminderId());
        response.setSentCount(targetUsers != null ? targetUsers.size() : 0);
        
        return response;
    }

    @PostMapping("/submit")
    public DailyReportResponseDTO submitReport(@RequestBody @Valid ReportSubmitRequestDTO request) {
        log.info("Submitting report with params: sceneGroupId={}, userId={}", 
                request.getSceneGroupId(), request.getUserId());
        
        Report report = new Report();
        report.setReportId("report-" + System.currentTimeMillis());
        report.setSceneGroupId(request.getSceneGroupId());
        report.setUserId(request.getUserId());
        report.setContent(request.getContent());
        report.setAttachments(request.getAttachments());
        report.setSubmitTime(System.currentTimeMillis());
        report.setDate(new Date());
        
        reports.computeIfAbsent(request.getSceneGroupId(), k -> new ArrayList<>()).add(report);
        
        DailyReportResponseDTO response = DailyReportResponseDTO.success("鏃ュ織鎻愪氦鎴愬姛");
        response.setReportId(report.getReportId());
        
        return response;
    }

    @PostMapping("/aggregate")
    public DailyReportResponseDTO aggregateReports(@RequestBody @Valid AggregateRequestDTO request) {
        log.info("Aggregating reports with params: sceneGroupId={}", request.getSceneGroupId());
        
        String sceneGroupId = request.getSceneGroupId();
        List<String> userIds = request.getUserIds();
        
        List<Report> sceneReports = reports.getOrDefault(sceneGroupId, new ArrayList<>());
        
        List<Report> filteredReports = new ArrayList<>();
        for (Report report : sceneReports) {
            if (userIds == null || userIds.isEmpty() || userIds.contains(report.getUserId())) {
                filteredReports.add(report);
            }
        }
        
        Map<String, Integer> userReportCount = new HashMap<>();
        
        for (Report report : filteredReports) {
            String userId = report.getUserId();
            userReportCount.merge(userId, 1, Integer::sum);
        }
        
        DailyReportResponseDTO response = DailyReportResponseDTO.success("姹囨€诲畬鎴?);
        response.setTotalReports(filteredReports.size());
        response.setUserReportCount(userReportCount);
        response.setReports(filteredReports);
        response.setAggregateTime(System.currentTimeMillis());
        
        return response;
    }

    @PostMapping("/analyze")
    public DailyReportResponseDTO analyzeReports(@RequestBody @Valid AnalyzeRequestDTO request) {
        log.info("Analyzing reports with params: sceneGroupId={}", request.getSceneGroupId());
        
        String analyzeType = request.getAnalyzeType() != null ? request.getAnalyzeType() : "summary";
        
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("analyzeType", analyzeType);
        analysis.put("analyzeTime", System.currentTimeMillis());
        
        if ("summary".equals(analyzeType)) {
            analysis.put("summary", "浠婃棩鍥㈤槦鏁翠綋宸ヤ綔杩涘睍椤哄埄锛屽叡瀹屾垚" + 
                    (request.getReports() != null ? request.getReports().size() : 0) + "椤逛换鍔°€?);
            analysis.put("highlights", Arrays.asList("椤圭洰杩涘害姝ｅ父", "鍥㈤槦鍗忎綔鑹ソ", "鏃犻噸澶ч棶棰?));
            analysis.put("suggestions", Arrays.asList("缁х画淇濇寔鑹ソ鐨勫伐浣滆妭濂?, "鍏虫敞椤圭洰鍏抽敭鑺傜偣"));
        } else if ("sentiment".equals(analyzeType)) {
            analysis.put("sentiment", "positive");
            analysis.put("confidence", 0.85);
            analysis.put("keywords", Arrays.asList("瀹屾垚", "杩涘睍", "椤哄埄", "鍗忎綔"));
        }
        
        DailyReportResponseDTO response = DailyReportResponseDTO.success("鍒嗘瀽瀹屾垚");
        response.setAnalysis(analysis);
        
        return response;
    }

    @GetMapping("/reports/{sceneGroupId}")
    public DailyReportResponseDTO getReports(@PathVariable String sceneGroupId) {
        List<Report> sceneReports = reports.getOrDefault(sceneGroupId, new ArrayList<>());
        
        DailyReportResponseDTO response = DailyReportResponseDTO.success("鏌ヨ鎴愬姛");
        response.setReports(sceneReports);
        response.setTotal(sceneReports.size());
        
        return response;
    }

    public static class Report {
        private String reportId;
        private String sceneGroupId;
        private String userId;
        private String content;
        private List<String> attachments;
        private long submitTime;
        private Date date;

        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public List<String> getAttachments() { return attachments; }
        public void setAttachments(List<String> attachments) { this.attachments = attachments; }
        public long getSubmitTime() { return submitTime; }
        public void setSubmitTime(long submitTime) { this.submitTime = submitTime; }
        public Date getDate() { return date; }
        public void setDate(Date date) { this.date = date; }
    }

    public static class Reminder {
        private String reminderId;
        private String sceneGroupId;
        private List<String> targetUsers;
        private String message;
        private long sendTime;
        private String status;

        public String getReminderId() { return reminderId; }
        public void setReminderId(String reminderId) { this.reminderId = reminderId; }
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        public List<String> getTargetUsers() { return targetUsers; }
        public void setTargetUsers(List<String> targetUsers) { this.targetUsers = targetUsers; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getSendTime() { return sendTime; }
        public void setSendTime(long sendTime) { this.sendTime = sendTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}

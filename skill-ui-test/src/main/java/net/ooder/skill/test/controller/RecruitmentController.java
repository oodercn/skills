package net.ooder.skill.test.controller;

import net.ooder.skill.test.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recruitment")
public class RecruitmentController {
    
    private static final Logger log = LoggerFactory.getLogger(RecruitmentController.class);
    
    private final Map<String, Map<String, Object>> jobStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> resumeStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> interviewStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> offerStore = new ConcurrentHashMap<>();
    
    public RecruitmentController() {
        initMockData();
    }
    
    private void initMockData() {
        // 初始化职位数据
        createJob("job-001", "Java高级工程师", "技术部", "北京", "全职", 3, 25000.0, 40000.0, 
            "负责核心业务系统开发", Arrays.asList("5年以上Java经验", "熟悉Spring Boot", "熟悉微服务架构"), 
            "active", "张HR", "hr@company.com");
        createJob("job-002", "前端开发工程师", "技术部", "北京", "全职", 2, 18000.0, 30000.0,
            "负责前端页面开发", Arrays.asList("3年以上前端经验", "熟悉Vue/React", "熟悉TypeScript"),
            "active", "张HR", "hr@company.com");
        createJob("job-003", "产品经理", "产品部", "上海", "全职", 1, 20000.0, 35000.0,
            "负责产品规划与设计", Arrays.asList("3年以上产品经验", "熟悉互联网产品", "良好的沟通能力"),
            "paused", "李HR", "hr@company.com");
        createJob("job-004", "UI设计师", "设计部", "深圳", "全职", 2, 15000.0, 25000.0,
            "负责产品界面设计", Arrays.asList("3年以上设计经验", "熟练使用Figma/Sketch", "良好的审美"),
            "closed", "王HR", "hr@company.com");
        
        // 初始化简历数据
        createResume("res-001", "张三", "13800138001", "zhangsan@email.com", "男", 28, "本科", 
            "北京大学", "计算机科学", 5, "ABC科技", "Java开发", 20000.0, 30000.0, "北京",
            "job-001", "Java高级工程师", "screening", "主动投递");
        createResume("res-002", "李四", "13800138002", "lisi@email.com", "女", 26, "硕士",
            "清华大学", "软件工程", 3, "XYZ公司", "前端开发", 18000.0, 25000.0, "北京",
            "job-002", "前端开发工程师", "interview", "内部推荐");
        createResume("res-003", "王五", "13800138003", "wangwu@email.com", "男", 30, "本科",
            "复旦大学", "信息管理", 6, "DEF集团", "产品经理", 25000.0, 35000.0, "上海",
            "job-003", "产品经理", "offer", "猎头推荐");
        createResume("res-004", "赵六", "13800138004", "zhaoliu@email.com", "女", 25, "本科",
            "浙江大学", "视觉设计", 3, "GHI设计", "UI设计师", 15000.0, 22000.0, "深圳",
            "job-004", "UI设计师", "rejected", "招聘网站");
        createResume("res-005", "钱七", "13800138005", "qianqi@email.com", "男", 27, "硕士",
            "南京大学", "计算机技术", 4, "JKL科技", "后端开发", 22000.0, 32000.0, "北京",
            "job-001", "Java高级工程师", "interview", "主动投递");
        
        // 初始化面试数据
        createInterview("int-001", "res-001", "张三", "job-001", "Java高级工程师", "一面", "技术经理A",
            "视频面试", System.currentTimeMillis() + 86400000, "腾讯会议", "scheduled");
        createInterview("int-002", "res-002", "李四", "job-002", "前端开发工程师", "二面", "前端负责人",
            "现场面试", System.currentTimeMillis() + 172800000, "公司会议室", "completed");
        createInterview("int-003", "res-005", "钱七", "job-001", "Java高级工程师", "HR面", "HR经理",
            "电话面试", System.currentTimeMillis() + 259200000, "", "scheduled");
        
        // 初始化Offer数据
        createOffer("off-001", "res-003", "王五", "job-003", "产品经理", 32000.0, "3个月", "2026-04-01",
            "五险一金、带薪年假、股票期权", "部门总监", "approved");
    }
    
    private void createJob(String id, String title, String department, String location, String type, 
                          Integer headcount, Double minSalary, Double maxSalary, String description,
                          List<String> requirements, String status, String hrName, String hrContact) {
        Map<String, Object> job = new HashMap<>();
        job.put("id", id);
        job.put("title", title);
        job.put("department", department);
        job.put("location", location);
        job.put("type", type);
        job.put("headcount", headcount);
        job.put("minSalary", minSalary);
        job.put("maxSalary", maxSalary);
        job.put("description", description);
        job.put("requirements", requirements);
        job.put("status", status);
        job.put("hrName", hrName);
        job.put("hrContact", hrContact);
        job.put("createdAt", System.currentTimeMillis() - (long)(Math.random() * 86400000 * 30));
        job.put("updatedAt", System.currentTimeMillis());
        job.put("publishDate", System.currentTimeMillis() - (long)(Math.random() * 86400000 * 20));
        jobStore.put(id, job);
    }
    
    private void createResume(String id, String name, String phone, String email, String gender, Integer age,
                             String education, String school, String major, Integer workYears, String currentCompany,
                             String currentPosition, Double currentSalary, Double expectedSalary, String expectedCity,
                             String jobId, String jobTitle, String status, String source) {
        Map<String, Object> resume = new HashMap<>();
        resume.put("id", id);
        resume.put("name", name);
        resume.put("phone", phone);
        resume.put("email", email);
        resume.put("gender", gender);
        resume.put("age", age);
        resume.put("education", education);
        resume.put("school", school);
        resume.put("major", major);
        resume.put("workYears", workYears);
        resume.put("currentCompany", currentCompany);
        resume.put("currentPosition", currentPosition);
        resume.put("currentSalary", currentSalary);
        resume.put("expectedSalary", expectedSalary);
        resume.put("expectedCity", expectedCity);
        resume.put("jobId", jobId);
        resume.put("jobTitle", jobTitle);
        resume.put("status", status);
        resume.put("source", source);
        // 添加前端需要的字段
        resume.put("experience", workYears + "年");
        resume.put("matchScore", (int)(Math.random() * 30) + 70); // 70-100之间的随机分数
        resume.put("applyDate", System.currentTimeMillis() - (long)(Math.random() * 86400000 * 15));
        resume.put("createdAt", System.currentTimeMillis());
        resume.put("updatedAt", System.currentTimeMillis());
        resumeStore.put(id, resume);
    }
    
    private void createInterview(String id, String resumeId, String candidateName, String jobId, String jobTitle,
                                String round, String interviewer, String interviewType, Long scheduleTime,
                                String location, String status) {
        Map<String, Object> interview = new HashMap<>();
        interview.put("id", id);
        interview.put("resumeId", resumeId);
        interview.put("candidateName", candidateName);
        interview.put("jobId", jobId);
        interview.put("jobTitle", jobTitle);
        interview.put("round", round);
        interview.put("interviewer", interviewer);
        interview.put("interviewType", interviewType);
        interview.put("scheduleTime", scheduleTime);
        interview.put("location", location);
        interview.put("status", status);
        interview.put("createdAt", System.currentTimeMillis());
        interview.put("updatedAt", System.currentTimeMillis());
        interviewStore.put(id, interview);
    }
    
    private void createOffer(String id, String resumeId, String candidateName, String jobId, String jobTitle,
                            Double offeredSalary, String probationPeriod, String entryDate, String benefits,
                            String approver, String status) {
        Map<String, Object> offer = new HashMap<>();
        offer.put("id", id);
        offer.put("resumeId", resumeId);
        offer.put("candidateName", candidateName);
        offer.put("jobId", jobId);
        offer.put("jobTitle", jobTitle);
        offer.put("offeredSalary", offeredSalary);
        offer.put("probationPeriod", probationPeriod);
        offer.put("entryDate", entryDate);
        offer.put("benefits", benefits);
        offer.put("approver", approver);
        offer.put("status", status);
        offer.put("createdAt", System.currentTimeMillis());
        offer.put("updatedAt", System.currentTimeMillis());
        offerStore.put(id, offer);
    }
    
    // ==================== 职位管理API ====================
    
    @GetMapping("/jobs")
    public ResponseEntity<Map<String, Object>> getJobs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String keyword) {
        
        log.info("[getJobs] status: {}, department: {}, keyword: {}", status, department, keyword);
        
        List<Map<String, Object>> jobs = new ArrayList<>(jobStore.values());
        
        if (status != null && !status.isEmpty()) {
            jobs = jobs.stream()
                .filter(j -> status.equals(j.get("status")))
                .collect(Collectors.toList());
        }
        
        if (department != null && !department.isEmpty()) {
            jobs = jobs.stream()
                .filter(j -> department.equals(j.get("department")))
                .collect(Collectors.toList());
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            jobs = jobs.stream()
                .filter(j -> {
                    String title = (String) j.get("title");
                    String desc = (String) j.get("description");
                    return (title != null && title.toLowerCase().contains(lowerKeyword)) ||
                           (desc != null && desc.toLowerCase().contains(lowerKeyword));
                })
                .collect(Collectors.toList());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("jobs", jobs);
        data.put("total", jobs.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/jobs/{id}")
    public ResponseEntity<Map<String, Object>> getJob(@PathVariable String id) {
        log.info("[getJob] id: {}", id);
        
        Map<String, Object> job = jobStore.get(id);
        
        Map<String, Object> result = new HashMap<>();
        if (job != null) {
            result.put("status", "success");
            result.put("data", job);
        } else {
            result.put("status", "error");
            result.put("message", "职位不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/jobs")
    public ResponseEntity<Map<String, Object>> createJob(@RequestBody JobDTO job) {
        log.info("[createJob] job: {}", job.getTitle());
        
        String id = "job-" + System.currentTimeMillis();
        job.setId(id);
        
        Map<String, Object> jobMap = convertJobToMap(job);
        jobMap.put("createdAt", System.currentTimeMillis());
        jobMap.put("updatedAt", System.currentTimeMillis());
        
        if (job.getStatus() == null) {
            jobMap.put("status", "draft");
        }
        
        jobStore.put(id, jobMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", jobMap);
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/jobs/{id}")
    public ResponseEntity<Map<String, Object>> updateJob(@PathVariable String id, @RequestBody JobDTO job) {
        log.info("[updateJob] id: {}, job: {}", id, job.getTitle());
        
        Map<String, Object> existing = jobStore.get(id);
        
        if (existing == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "职位不存在");
            return ResponseEntity.ok(result);
        }
        
        job.setId(id);
        Map<String, Object> jobMap = convertJobToMap(job);
        jobMap.put("createdAt", existing.get("createdAt"));
        jobMap.put("updatedAt", System.currentTimeMillis());
        
        jobStore.put(id, jobMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", jobMap);
        
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Map<String, Object>> deleteJob(@PathVariable String id) {
        log.info("[deleteJob] id: {}", id);
        
        Map<String, Object> removed = jobStore.remove(id);
        
        Map<String, Object> result = new HashMap<>();
        if (removed != null) {
            result.put("status", "success");
            result.put("message", "职位已删除");
        } else {
            result.put("status", "error");
            result.put("message", "职位不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    private Map<String, Object> convertJobToMap(JobDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getId() != null) map.put("id", dto.getId());
        if (dto.getTitle() != null) map.put("title", dto.getTitle());
        if (dto.getDepartment() != null) map.put("department", dto.getDepartment());
        if (dto.getLocation() != null) map.put("location", dto.getLocation());
        if (dto.getType() != null) map.put("type", dto.getType());
        if (dto.getHeadcount() != null) map.put("headcount", dto.getHeadcount());
        if (dto.getMinSalary() != null) map.put("minSalary", dto.getMinSalary());
        if (dto.getMaxSalary() != null) map.put("maxSalary", dto.getMaxSalary());
        if (dto.getDescription() != null) map.put("description", dto.getDescription());
        if (dto.getRequirements() != null) map.put("requirements", dto.getRequirements());
        if (dto.getStatus() != null) map.put("status", dto.getStatus());
        if (dto.getHrName() != null) map.put("hrName", dto.getHrName());
        if (dto.getHrContact() != null) map.put("hrContact", dto.getHrContact());
        if (dto.getPublishDate() != null) map.put("publishDate", dto.getPublishDate());
        if (dto.getCloseDate() != null) map.put("closeDate", dto.getCloseDate());
        return map;
    }
    
    // ==================== 简历管理API ====================
    
    @GetMapping("/resumes")
    public ResponseEntity<Map<String, Object>> getResumes(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String jobId,
            @RequestParam(required = false) String keyword) {
        
        log.info("[getResumes] status: {}, jobId: {}, keyword: {}", status, jobId, keyword);
        
        List<Map<String, Object>> resumes = new ArrayList<>(resumeStore.values());
        
        if (status != null && !status.isEmpty()) {
            resumes = resumes.stream()
                .filter(r -> status.equals(r.get("status")))
                .collect(Collectors.toList());
        }
        
        if (jobId != null && !jobId.isEmpty()) {
            resumes = resumes.stream()
                .filter(r -> jobId.equals(r.get("jobId")))
                .collect(Collectors.toList());
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            resumes = resumes.stream()
                .filter(r -> {
                    String name = (String) r.get("name");
                    String phone = (String) r.get("phone");
                    String email = (String) r.get("email");
                    return (name != null && name.toLowerCase().contains(lowerKeyword)) ||
                           (phone != null && phone.contains(keyword)) ||
                           (email != null && email.toLowerCase().contains(lowerKeyword));
                })
                .collect(Collectors.toList());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("resumes", resumes);
        data.put("total", resumes.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/resumes/{id}")
    public ResponseEntity<Map<String, Object>> getResume(@PathVariable String id) {
        log.info("[getResume] id: {}", id);
        
        Map<String, Object> resume = resumeStore.get(id);
        
        Map<String, Object> result = new HashMap<>();
        if (resume != null) {
            result.put("status", "success");
            result.put("data", resume);
        } else {
            result.put("status", "error");
            result.put("message", "简历不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/resumes")
    public ResponseEntity<Map<String, Object>> createResume(@RequestBody ResumeDTO resume) {
        log.info("[createResume] resume: {}", resume.getName());
        
        String id = "res-" + System.currentTimeMillis();
        resume.setId(id);
        
        Map<String, Object> resumeMap = convertResumeToMap(resume);
        resumeMap.put("createdAt", System.currentTimeMillis());
        resumeMap.put("updatedAt", System.currentTimeMillis());
        
        if (resume.getStatus() == null) {
            resumeMap.put("status", "new");
        }
        
        resumeStore.put(id, resumeMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", resumeMap);
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/resumes/{id}/status")
    public ResponseEntity<Map<String, Object>> updateResumeStatus(@PathVariable String id, 
                                                                  @RequestParam String status) {
        log.info("[updateResumeStatus] id: {}, status: {}", id, status);
        
        Map<String, Object> resume = resumeStore.get(id);
        
        if (resume == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "简历不存在");
            return ResponseEntity.ok(result);
        }
        
        resume.put("status", status);
        resume.put("updatedAt", System.currentTimeMillis());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", resume);
        
        return ResponseEntity.ok(result);
    }
    
    private Map<String, Object> convertResumeToMap(ResumeDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getId() != null) map.put("id", dto.getId());
        if (dto.getName() != null) map.put("name", dto.getName());
        if (dto.getPhone() != null) map.put("phone", dto.getPhone());
        if (dto.getEmail() != null) map.put("email", dto.getEmail());
        if (dto.getGender() != null) map.put("gender", dto.getGender());
        if (dto.getAge() != null) map.put("age", dto.getAge());
        if (dto.getEducation() != null) map.put("education", dto.getEducation());
        if (dto.getSchool() != null) map.put("school", dto.getSchool());
        if (dto.getMajor() != null) map.put("major", dto.getMajor());
        if (dto.getWorkYears() != null) {
            map.put("workYears", dto.getWorkYears());
            map.put("experience", dto.getWorkYears() + "年");
        }
        if (dto.getCurrentCompany() != null) map.put("currentCompany", dto.getCurrentCompany());
        if (dto.getCurrentPosition() != null) map.put("currentPosition", dto.getCurrentPosition());
        if (dto.getCurrentSalary() != null) map.put("currentSalary", dto.getCurrentSalary());
        if (dto.getExpectedSalary() != null) map.put("expectedSalary", dto.getExpectedSalary());
        if (dto.getExpectedCity() != null) map.put("expectedCity", dto.getExpectedCity());
        if (dto.getSelfEvaluation() != null) map.put("selfEvaluation", dto.getSelfEvaluation());
        if (dto.getWorkExperiences() != null) map.put("workExperiences", dto.getWorkExperiences());
        if (dto.getProjectExperiences() != null) map.put("projectExperiences", dto.getProjectExperiences());
        if (dto.getJobId() != null) map.put("jobId", dto.getJobId());
        if (dto.getJobTitle() != null) map.put("jobTitle", dto.getJobTitle());
        if (dto.getStatus() != null) map.put("status", dto.getStatus());
        if (dto.getSource() != null) map.put("source", dto.getSource());
        if (dto.getApplyDate() != null) map.put("applyDate", dto.getApplyDate());
        // 添加匹配分数（用于前端展示）
        map.put("matchScore", (int)(Math.random() * 30) + 70);
        return map;
    }
    
    // ==================== 面试管理API ====================
    
    @GetMapping("/interviews")
    public ResponseEntity<Map<String, Object>> getInterviews(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String resumeId) {
        
        log.info("[getInterviews] status: {}, resumeId: {}", status, resumeId);
        
        List<Map<String, Object>> interviews = new ArrayList<>(interviewStore.values());
        
        if (status != null && !status.isEmpty()) {
            interviews = interviews.stream()
                .filter(i -> status.equals(i.get("status")))
                .collect(Collectors.toList());
        }
        
        if (resumeId != null && !resumeId.isEmpty()) {
            interviews = interviews.stream()
                .filter(i -> resumeId.equals(i.get("resumeId")))
                .collect(Collectors.toList());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("interviews", interviews);
        data.put("total", interviews.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/interviews")
    public ResponseEntity<Map<String, Object>> createInterview(@RequestBody InterviewDTO interview) {
        log.info("[createInterview] candidate: {}", interview.getCandidateName());
        
        String id = "int-" + System.currentTimeMillis();
        interview.setId(id);
        
        Map<String, Object> interviewMap = convertInterviewToMap(interview);
        interviewMap.put("createdAt", System.currentTimeMillis());
        interviewMap.put("updatedAt", System.currentTimeMillis());
        
        if (interview.getStatus() == null) {
            interviewMap.put("status", "scheduled");
        }
        
        interviewStore.put(id, interviewMap);
        
        // 更新简历状态为面试中
        Map<String, Object> resume = resumeStore.get(interview.getResumeId());
        if (resume != null) {
            resume.put("status", "interview");
            resume.put("updatedAt", System.currentTimeMillis());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", interviewMap);
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/interviews/{id}/result")
    public ResponseEntity<Map<String, Object>> updateInterviewResult(@PathVariable String id,
                                                                     @RequestParam String result,
                                                                     @RequestParam(required = false) Integer score,
                                                                     @RequestParam(required = false) String feedback) {
        log.info("[updateInterviewResult] id: {}, result: {}", id, result);
        
        Map<String, Object> interview = interviewStore.get(id);
        
        if (interview == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "面试记录不存在");
            return ResponseEntity.ok(response);
        }
        
        interview.put("result", result);
        interview.put("status", "completed");
        if (score != null) interview.put("score", score);
        if (feedback != null) interview.put("feedback", feedback);
        interview.put("updatedAt", System.currentTimeMillis());
        
        // 根据面试结果更新简历状态
        String resumeId = (String) interview.get("resumeId");
        Map<String, Object> resume = resumeStore.get(resumeId);
        if (resume != null) {
            if ("passed".equals(result)) {
                resume.put("status", "offer");
            } else if ("rejected".equals(result)) {
                resume.put("status", "rejected");
            }
            resume.put("updatedAt", System.currentTimeMillis());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", interview);
        
        return ResponseEntity.ok(response);
    }
    
    private Map<String, Object> convertInterviewToMap(InterviewDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getId() != null) map.put("id", dto.getId());
        if (dto.getResumeId() != null) map.put("resumeId", dto.getResumeId());
        if (dto.getCandidateName() != null) map.put("candidateName", dto.getCandidateName());
        if (dto.getJobId() != null) map.put("jobId", dto.getJobId());
        if (dto.getJobTitle() != null) map.put("jobTitle", dto.getJobTitle());
        if (dto.getRound() != null) map.put("round", dto.getRound());
        if (dto.getInterviewer() != null) map.put("interviewer", dto.getInterviewer());
        if (dto.getInterviewType() != null) map.put("interviewType", dto.getInterviewType());
        if (dto.getScheduleTime() != null) map.put("scheduleTime", dto.getScheduleTime());
        if (dto.getLocation() != null) map.put("location", dto.getLocation());
        if (dto.getStatus() != null) map.put("status", dto.getStatus());
        if (dto.getScore() != null) map.put("score", dto.getScore());
        if (dto.getFeedback() != null) map.put("feedback", dto.getFeedback());
        if (dto.getResult() != null) map.put("result", dto.getResult());
        return map;
    }
    
    // ==================== Offer管理API ====================
    
    @GetMapping("/offers")
    public ResponseEntity<Map<String, Object>> getOffers(
            @RequestParam(required = false) String status) {
        
        log.info("[getOffers] status: {}", status);
        
        List<Map<String, Object>> offers = new ArrayList<>(offerStore.values());
        
        if (status != null && !status.isEmpty()) {
            offers = offers.stream()
                .filter(o -> status.equals(o.get("status")))
                .collect(Collectors.toList());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("offers", offers);
        data.put("total", offers.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/offers")
    public ResponseEntity<Map<String, Object>> createOffer(@RequestBody OfferDTO offer) {
        log.info("[createOffer] candidate: {}", offer.getCandidateName());
        
        String id = "off-" + System.currentTimeMillis();
        offer.setId(id);
        
        Map<String, Object> offerMap = convertOfferToMap(offer);
        offerMap.put("createdAt", System.currentTimeMillis());
        offerMap.put("updatedAt", System.currentTimeMillis());
        offerMap.put("status", "pending");
        
        offerStore.put(id, offerMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", offerMap);
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/offers/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveOffer(@PathVariable String id,
                                                            @RequestParam String action,
                                                            @RequestParam(required = false) String reason) {
        log.info("[approveOffer] id: {}, action: {}", id, action);
        
        Map<String, Object> offer = offerStore.get(id);
        
        if (offer == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "Offer不存在");
            return ResponseEntity.ok(result);
        }
        
        if ("approve".equals(action)) {
            offer.put("status", "approved");
            offer.put("approvedAt", System.currentTimeMillis());
            
            // 更新简历状态为已录用
            String resumeId = (String) offer.get("resumeId");
            Map<String, Object> resume = resumeStore.get(resumeId);
            if (resume != null) {
                resume.put("status", "hired");
                resume.put("updatedAt", System.currentTimeMillis());
            }
        } else if ("reject".equals(action)) {
            offer.put("status", "rejected");
            offer.put("rejectReason", reason);
            
            // 更新简历状态为面试中
            String resumeId = (String) offer.get("resumeId");
            Map<String, Object> resume = resumeStore.get(resumeId);
            if (resume != null) {
                resume.put("status", "interview");
                resume.put("updatedAt", System.currentTimeMillis());
            }
        }
        
        offer.put("updatedAt", System.currentTimeMillis());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", offer);
        
        return ResponseEntity.ok(result);
    }
    
    private Map<String, Object> convertOfferToMap(OfferDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getId() != null) map.put("id", dto.getId());
        if (dto.getResumeId() != null) map.put("resumeId", dto.getResumeId());
        if (dto.getCandidateName() != null) map.put("candidateName", dto.getCandidateName());
        if (dto.getJobId() != null) map.put("jobId", dto.getJobId());
        if (dto.getJobTitle() != null) map.put("jobTitle", dto.getJobTitle());
        if (dto.getOfferedSalary() != null) map.put("offeredSalary", dto.getOfferedSalary());
        if (dto.getProbationPeriod() != null) map.put("probationPeriod", dto.getProbationPeriod());
        if (dto.getEntryDate() != null) map.put("entryDate", dto.getEntryDate());
        if (dto.getBenefits() != null) map.put("benefits", dto.getBenefits());
        if (dto.getApprover() != null) map.put("approver", dto.getApprover());
        if (dto.getStatus() != null) map.put("status", dto.getStatus());
        if (dto.getRejectReason() != null) map.put("rejectReason", dto.getRejectReason());
        return map;
    }
    
    // ==================== 统计API ====================
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("[getStatistics]");
        
        // 职位统计
        long activeJobs = jobStore.values().stream()
            .filter(j -> "active".equals(j.get("status")))
            .count();
        long totalJobs = jobStore.size();
        
        // 简历统计
        long newResumes = resumeStore.values().stream()
            .filter(r -> Arrays.asList("new", "screening").contains(r.get("status")))
            .count();
        long totalResumes = resumeStore.size();
        
        // 面试统计
        long todayInterviews = interviewStore.values().stream()
            .filter(i -> {
                Long scheduleTime = (Long) i.get("scheduleTime");
                if (scheduleTime == null) return false;
                long today = System.currentTimeMillis();
                return scheduleTime >= today - 86400000 && scheduleTime < today + 86400000;
            })
            .count();
        long pendingInterviews = interviewStore.values().stream()
            .filter(i -> "scheduled".equals(i.get("status")))
            .count();
        
        // Offer统计
        long pendingOffers = offerStore.values().stream()
            .filter(o -> "pending".equals(o.get("status")))
            .count();
        long approvedOffers = offerStore.values().stream()
            .filter(o -> "approved".equals(o.get("status")))
            .count();
        
        // 招聘漏斗数据
        Map<String, Object> funnelData = new LinkedHashMap<>();
        funnelData.put("简历投递", totalResumes);
        funnelData.put("简历筛选", resumeStore.values().stream()
            .filter(r -> !Arrays.asList("new", "rejected").contains(r.get("status")))
            .count());
        funnelData.put("面试安排", interviewStore.size());
        funnelData.put("面试通过", interviewStore.values().stream()
            .filter(i -> "passed".equals(i.get("result")))
            .count());
        funnelData.put("Offer发放", offerStore.size());
        funnelData.put("已入职", offerStore.values().stream()
            .filter(o -> "approved".equals(o.get("status")))
            .count());
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeJobs", activeJobs);
        stats.put("totalJobs", totalJobs);
        stats.put("newResumes", newResumes);
        stats.put("totalResumes", totalResumes);
        stats.put("todayInterviews", todayInterviews);
        stats.put("pendingInterviews", pendingInterviews);
        stats.put("pendingOffers", pendingOffers);
        stats.put("approvedOffers", approvedOffers);
        stats.put("funnel", funnelData);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", stats);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/departments")
    public ResponseEntity<Map<String, Object>> getDepartments() {
        log.info("[getDepartments]");
        
        List<String> departments = Arrays.asList("技术部", "产品部", "设计部", "运营部", "市场部", "销售部", "人事部", "财务部");
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", departments);
        
        return ResponseEntity.ok(result);
    }
}

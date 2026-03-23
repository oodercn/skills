package net.ooder.skill.recruitment.service;

import net.ooder.skill.recruitment.dto.InterviewFeedbackRequest;
import net.ooder.skill.recruitment.dto.InterviewScheduleRequest;
import net.ooder.skill.recruitment.dto.JobCreateRequest;
import net.ooder.skill.recruitment.dto.PageResult;
import net.ooder.skill.recruitment.dto.ResumeCreateRequest;
import net.ooder.skill.recruitment.model.Job;
import net.ooder.skill.recruitment.model.Resume;
import net.ooder.skill.recruitment.model.Resume.InterviewRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RecruitmentServiceImpl implements RecruitmentService {
    
    private final Map<String, Job> jobs = new ConcurrentHashMap<>();
    private final Map<String, Resume> resumes = new ConcurrentHashMap<>();
    
    public RecruitmentServiceImpl() {
        initTestData();
    }
    
    private void initTestData() {
        Job job1 = new Job();
        job1.setId("JOB-001");
        job1.setTitle("高级Java工程师");
        job1.setDepartment("技术研发部");
        job1.setLocation("北京");
        job1.setType("全职");
        job1.setExperience("3-5年");
        job1.setEducation("本科及以上");
        job1.setSalary("25k-40k");
        job1.setDescription("负责核心系统架构设计与开发");
        job1.setRequirements(Arrays.asList("精通Java", "熟悉Spring生态", "有分布式系统经验"));
        job1.setBenefits(Arrays.asList("五险一金", "弹性工作", "股票期权"));
        job1.setStatus("published");
        job1.setApplicantCount(12);
        job1.setPublishedAt(LocalDateTime.now().minusDays(7));
        job1.setCreatedAt(LocalDateTime.now().minusDays(10));
        jobs.put(job1.getId(), job1);
        
        Job job2 = new Job();
        job2.setId("JOB-002");
        job2.setTitle("产品经理");
        job2.setDepartment("产品部");
        job2.setLocation("上海");
        job2.setType("全职");
        job2.setExperience("3年以上");
        job2.setEducation("本科及以上");
        job2.setSalary("20k-35k");
        job2.setDescription("负责产品规划与需求分析");
        job2.setRequirements(Arrays.asList("有产品经验", "沟通能力强", "数据分析能力"));
        job2.setBenefits(Arrays.asList("五险一金", "弹性工作", "培训机会"));
        job2.setStatus("published");
        job2.setApplicantCount(8);
        job2.setPublishedAt(LocalDateTime.now().minusDays(3));
        job2.setCreatedAt(LocalDateTime.now().minusDays(5));
        jobs.put(job2.getId(), job2);
        
        Resume r1 = new Resume();
        r1.setId("RES-001");
        r1.setJobId("JOB-001");
        r1.setJobTitle("高级Java工程师");
        r1.setName("王小明");
        r1.setPhone("13800138001");
        r1.setEmail("wangxm@example.com");
        r1.setEducation("硕士");
        r1.setExperience("5年");
        r1.setCurrentCompany("某互联网公司");
        r1.setExpectedSalary("30k-40k");
        r1.setSource("猎聘");
        r1.setStatus("active");
        r1.setStage("interview");
        r1.setRating("A");
        r1.setAppliedAt(LocalDateTime.now().minusDays(5));
        r1.setCreatedAt(LocalDateTime.now().minusDays(5));
        resumes.put(r1.getId(), r1);
        
        Resume r2 = new Resume();
        r2.setId("RES-002");
        r2.setJobId("JOB-001");
        r2.setJobTitle("高级Java工程师");
        r2.setName("李华");
        r2.setPhone("13800138002");
        r2.setEmail("lihua@example.com");
        r2.setEducation("本科");
        r2.setExperience("3年");
        r2.setCurrentCompany("某科技公司");
        r2.setExpectedSalary("25k-30k");
        r2.setSource("BOSS直聘");
        r2.setStatus("active");
        r2.setStage("screening");
        r2.setRating("B");
        r2.setAppliedAt(LocalDateTime.now().minusDays(3));
        r2.setCreatedAt(LocalDateTime.now().minusDays(3));
        resumes.put(r2.getId(), r2);
    }
    
    @Override
    public List<Job> listJobs(String status, String department) {
        return jobs.values().stream()
            .filter(j -> status == null || status.isEmpty() || j.getStatus().equals(status))
            .filter(j -> department == null || department.isEmpty() || j.getDepartment().equals(department))
            .sorted((j1, j2) -> j2.getCreatedAt().compareTo(j1.getCreatedAt()))
            .collect(Collectors.toList());
    }
    
    @Override
    public Job getJob(String id) {
        return jobs.get(id);
    }
    
    @Override
    public Job createJob(JobCreateRequest request) {
        Job job = new Job();
        job.setId("JOB-" + System.currentTimeMillis());
        job.setTitle(request.getTitle());
        job.setDepartment(request.getDepartment());
        job.setLocation(request.getLocation());
        job.setType(request.getType());
        job.setExperience(request.getExperience());
        job.setEducation(request.getEducation());
        job.setSalary(request.getSalary());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setBenefits(request.getBenefits());
        job.setStatus("draft");
        job.setApplicantCount(0);
        job.setCreatedAt(LocalDateTime.now());
        jobs.put(job.getId(), job);
        return job;
    }
    
    @Override
    public Job updateJob(String id, JobCreateRequest request) {
        Job job = jobs.get(id);
        if (job == null) return null;
        job.setTitle(request.getTitle());
        job.setDepartment(request.getDepartment());
        job.setLocation(request.getLocation());
        job.setType(request.getType());
        job.setExperience(request.getExperience());
        job.setEducation(request.getEducation());
        job.setSalary(request.getSalary());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setBenefits(request.getBenefits());
        job.setUpdatedAt(LocalDateTime.now());
        return job;
    }
    
    @Override
    public void deleteJob(String id) {
        jobs.remove(id);
    }
    
    @Override
    public Job publishJob(String id) {
        Job job = jobs.get(id);
        if (job == null) return null;
        job.setStatus("published");
        job.setPublishedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        return job;
    }
    
    @Override
    public Job closeJob(String id) {
        Job job = jobs.get(id);
        if (job == null) return null;
        job.setStatus("closed");
        job.setUpdatedAt(LocalDateTime.now());
        return job;
    }
    
    @Override
    public PageResult<Resume> listResumes(String jobId, String status, String stage, int page, int size) {
        List<Resume> filtered = resumes.values().stream()
            .filter(r -> jobId == null || jobId.isEmpty() || r.getJobId().equals(jobId))
            .filter(r -> status == null || status.isEmpty() || r.getStatus().equals(status))
            .filter(r -> stage == null || stage.isEmpty() || r.getStage().equals(stage))
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
            .collect(Collectors.toList());
        
        PageResult<Resume> result = new PageResult<>();
        result.setTotal(filtered.size());
        result.setPage(page);
        result.setSize(size);
        
        int start = (page - 1) * size;
        int end = Math.min(start + size, filtered.size());
        result.setList(start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>());
        
        return result;
    }
    
    @Override
    public Resume getResume(String id) {
        return resumes.get(id);
    }
    
    @Override
    public Resume createResume(ResumeCreateRequest request) {
        Resume resume = new Resume();
        resume.setId("RES-" + System.currentTimeMillis());
        resume.setJobId(request.getJobId());
        
        Job job = jobs.get(request.getJobId());
        if (job != null) {
            resume.setJobTitle(job.getTitle());
            job.setApplicantCount(job.getApplicantCount() + 1);
        }
        
        resume.setName(request.getName());
        resume.setPhone(request.getPhone());
        resume.setEmail(request.getEmail());
        resume.setEducation(request.getEducation());
        resume.setExperience(request.getExperience());
        resume.setCurrentCompany(request.getCurrentCompany());
        resume.setExpectedSalary(request.getExpectedSalary());
        resume.setSource(request.getSource());
        resume.setAttachments(request.getAttachments());
        resume.setStatus("active");
        resume.setStage("new");
        resume.setAppliedAt(LocalDateTime.now());
        resume.setCreatedAt(LocalDateTime.now());
        resumes.put(resume.getId(), resume);
        return resume;
    }
    
    @Override
    public Resume updateResume(String id, ResumeCreateRequest request) {
        Resume resume = resumes.get(id);
        if (resume == null) return null;
        resume.setName(request.getName());
        resume.setPhone(request.getPhone());
        resume.setEmail(request.getEmail());
        resume.setEducation(request.getEducation());
        resume.setExperience(request.getExperience());
        resume.setCurrentCompany(request.getCurrentCompany());
        resume.setExpectedSalary(request.getExpectedSalary());
        resume.setSource(request.getSource());
        resume.setUpdatedAt(LocalDateTime.now());
        return resume;
    }
    
    @Override
    public void deleteResume(String id) {
        resumes.remove(id);
    }
    
    @Override
    public Resume parseResume(String id) {
        Resume resume = resumes.get(id);
        if (resume == null) return null;
        
        Map<String, Object> parsedInfo = new HashMap<>();
        parsedInfo.put("skills", Arrays.asList("Java", "Spring", "MySQL", "Redis"));
        parsedInfo.put("workYears", 5);
        parsedInfo.put("educationHistory", Arrays.asList("XX大学 计算机科学 硕士"));
        parsedInfo.put("workHistory", Arrays.asList("某互联网公司 高级工程师 2020-至今"));
        resume.setParsedInfo(parsedInfo);
        resume.setUpdatedAt(LocalDateTime.now());
        return resume;
    }
    
    @Override
    public Resume updateResumeStatus(String id, String status, String stage) {
        Resume resume = resumes.get(id);
        if (resume == null) return null;
        if (status != null) resume.setStatus(status);
        if (stage != null) resume.setStage(stage);
        resume.setUpdatedAt(LocalDateTime.now());
        return resume;
    }
    
    @Override
    public Resume scheduleInterview(String resumeId, InterviewScheduleRequest request) {
        Resume resume = resumes.get(resumeId);
        if (resume == null) return null;
        
        InterviewRecord interview = new InterviewRecord();
        interview.setId("INT-" + System.currentTimeMillis());
        interview.setType(request.getType());
        interview.setInterviewer(request.getInterviewer());
        interview.setTime(LocalDateTime.parse(request.getTime().replace(" ", "T")));
        interview.setLocation(request.getLocation());
        interview.setResult("pending");
        
        if (resume.getInterviews() == null) {
            resume.setInterviews(new ArrayList<>());
        }
        resume.getInterviews().add(interview);
        resume.setStage("interview");
        resume.setUpdatedAt(LocalDateTime.now());
        return resume;
    }
    
    @Override
    public Resume submitInterviewFeedback(String resumeId, String interviewId, InterviewFeedbackRequest request) {
        Resume resume = resumes.get(resumeId);
        if (resume == null || resume.getInterviews() == null) return null;
        
        resume.getInterviews().stream()
            .filter(i -> i.getId().equals(interviewId))
            .findFirst()
            .ifPresent(i -> {
                i.setResult(request.getResult());
                i.setFeedback(request.getFeedback());
                i.setScore(request.getScore());
            });
        
        resume.setUpdatedAt(LocalDateTime.now());
        return resume;
    }
    
    @Override
    public Resume approveOffer(String resumeId) {
        Resume resume = resumes.get(resumeId);
        if (resume == null) return null;
        resume.setStatus("hired");
        resume.setStage("onboarding");
        resume.setUpdatedAt(LocalDateTime.now());
        return resume;
    }
    
    @Override
    public Resume rejectResume(String resumeId, String reason) {
        Resume resume = resumes.get(resumeId);
        if (resume == null) return null;
        resume.setStatus("rejected");
        resume.setComment(reason);
        resume.setUpdatedAt(LocalDateTime.now());
        return resume;
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJobs", jobs.size());
        stats.put("activeJobs", jobs.values().stream().filter(j -> "published".equals(j.getStatus())).count());
        stats.put("totalResumes", resumes.size());
        stats.put("newResumes", resumes.values().stream().filter(r -> "new".equals(r.getStage())).count());
        stats.put("interviewResumes", resumes.values().stream().filter(r -> "interview".equals(r.getStage())).count());
        stats.put("hiredResumes", resumes.values().stream().filter(r -> "hired".equals(r.getStatus())).count());
        return stats;
    }
    
    @Override
    public Map<String, Object> aiMatch(String resumeId, String jobId) {
        Map<String, Object> result = new HashMap<>();
        result.put("matchScore", 85);
        result.put("matchedSkills", Arrays.asList("Java", "Spring", "MySQL"));
        result.put("missingSkills", Arrays.asList("Kubernetes"));
        result.put("recommendation", "候选人技术栈与岗位要求高度匹配，建议进入面试环节");
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getDashboardData() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("title", "待处理简历");
        item1.put("value", resumes.values().stream().filter(r -> "new".equals(r.getStage())).count());
        item1.put("icon", "ri-file-user-line");
        item1.put("color", "#3b82f6");
        data.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("title", "本周面试");
        item2.put("value", 5);
        item2.put("icon", "ri-calendar-check-line");
        item2.put("color", "#22c55e");
        data.add(item2);
        
        Map<String, Object> item3 = new HashMap<>();
        item3.put("title", "在招职位");
        item3.put("value", jobs.values().stream().filter(j -> "published".equals(j.getStatus())).count());
        item3.put("icon", "ri-briefcase-line");
        item3.put("color", "#f59e0b");
        data.add(item3);
        
        Map<String, Object> item4 = new HashMap<>();
        item4.put("title", "本月入职");
        item4.put("value", resumes.values().stream().filter(r -> "hired".equals(r.getStatus())).count());
        item4.put("icon", "ri-user-add-line");
        item4.put("color", "#8b5cf6");
        data.add(item4);
        
        return data;
    }
}

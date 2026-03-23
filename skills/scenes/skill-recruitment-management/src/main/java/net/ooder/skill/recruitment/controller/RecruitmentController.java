package net.ooder.skill.recruitment.controller;

import net.ooder.skill.recruitment.dto.ApiResponse;
import net.ooder.skill.recruitment.dto.InterviewFeedbackRequest;
import net.ooder.skill.recruitment.dto.InterviewScheduleRequest;
import net.ooder.skill.recruitment.dto.JobCreateRequest;
import net.ooder.skill.recruitment.dto.PageResult;
import net.ooder.skill.recruitment.dto.ResumeCreateRequest;
import net.ooder.skill.recruitment.model.Job;
import net.ooder.skill.recruitment.model.Resume;
import net.ooder.skill.recruitment.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitment")
public class RecruitmentController {
    
    @Autowired
    private RecruitmentService recruitmentService;
    
    @GetMapping("/jobs")
    public ApiResponse<List<Job>> listJobs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String department) {
        return new ApiResponse<>(recruitmentService.listJobs(status, department));
    }
    
    @GetMapping("/jobs/{id}")
    public ApiResponse<Job> getJob(@PathVariable String id) {
        Job job = recruitmentService.getJob(id);
        if (job == null) {
            return new ApiResponse<>("error", "职位不存在");
        }
        return new ApiResponse<>(job);
    }
    
    @PostMapping("/jobs")
    public ApiResponse<Job> createJob(@RequestBody JobCreateRequest request) {
        return new ApiResponse<>(recruitmentService.createJob(request));
    }
    
    @PutMapping("/jobs/{id}")
    public ApiResponse<Job> updateJob(
            @PathVariable String id,
            @RequestBody JobCreateRequest request) {
        Job job = recruitmentService.updateJob(id, request);
        if (job == null) {
            return new ApiResponse<>("error", "职位不存在");
        }
        return new ApiResponse<>(job);
    }
    
    @DeleteMapping("/jobs/{id}")
    public ApiResponse<Void> deleteJob(@PathVariable String id) {
        recruitmentService.deleteJob(id);
        return new ApiResponse<>(null);
    }
    
    @PostMapping("/jobs/{id}/publish")
    public ApiResponse<Job> publishJob(@PathVariable String id) {
        Job job = recruitmentService.publishJob(id);
        if (job == null) {
            return new ApiResponse<>("error", "职位不存在");
        }
        return new ApiResponse<>(job);
    }
    
    @PostMapping("/jobs/{id}/close")
    public ApiResponse<Job> closeJob(@PathVariable String id) {
        Job job = recruitmentService.closeJob(id);
        if (job == null) {
            return new ApiResponse<>("error", "职位不存在");
        }
        return new ApiResponse<>(job);
    }
    
    @GetMapping("/resumes")
    public ApiResponse<PageResult<Resume>> listResumes(
            @RequestParam(required = false) String jobId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String stage,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(recruitmentService.listResumes(jobId, status, stage, page, size));
    }
    
    @GetMapping("/resumes/{id}")
    public ApiResponse<Resume> getResume(@PathVariable String id) {
        Resume resume = recruitmentService.getResume(id);
        if (resume == null) {
            return new ApiResponse<>("error", "简历不存在");
        }
        return new ApiResponse<>(resume);
    }
    
    @PostMapping("/resumes")
    public ApiResponse<Resume> createResume(@RequestBody ResumeCreateRequest request) {
        return new ApiResponse<>(recruitmentService.createResume(request));
    }
    
    @PutMapping("/resumes/{id}")
    public ApiResponse<Resume> updateResume(
            @PathVariable String id,
            @RequestBody ResumeCreateRequest request) {
        Resume resume = recruitmentService.updateResume(id, request);
        if (resume == null) {
            return new ApiResponse<>("error", "简历不存在");
        }
        return new ApiResponse<>(resume);
    }
    
    @DeleteMapping("/resumes/{id}")
    public ApiResponse<Void> deleteResume(@PathVariable String id) {
        recruitmentService.deleteResume(id);
        return new ApiResponse<>(null);
    }
    
    @PostMapping("/resumes/{id}/parse")
    public ApiResponse<Resume> parseResume(@PathVariable String id) {
        Resume resume = recruitmentService.parseResume(id);
        if (resume == null) {
            return new ApiResponse<>("error", "简历不存在");
        }
        return new ApiResponse<>(resume);
    }
    
    @PutMapping("/resumes/{id}/status")
    public ApiResponse<Resume> updateResumeStatus(
            @PathVariable String id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String stage) {
        Resume resume = recruitmentService.updateResumeStatus(id, status, stage);
        if (resume == null) {
            return new ApiResponse<>("error", "简历不存在");
        }
        return new ApiResponse<>(resume);
    }
    
    @PostMapping("/resumes/{id}/interview")
    public ApiResponse<Resume> scheduleInterview(
            @PathVariable String id,
            @RequestBody InterviewScheduleRequest request) {
        Resume resume = recruitmentService.scheduleInterview(id, request);
        if (resume == null) {
            return new ApiResponse<>("error", "简历不存在");
        }
        return new ApiResponse<>(resume);
    }
    
    @PostMapping("/resumes/{resumeId}/interview/{interviewId}/feedback")
    public ApiResponse<Resume> submitInterviewFeedback(
            @PathVariable String resumeId,
            @PathVariable String interviewId,
            @RequestBody InterviewFeedbackRequest request) {
        Resume resume = recruitmentService.submitInterviewFeedback(resumeId, interviewId, request);
        if (resume == null) {
            return new ApiResponse<>("error", "简历或面试记录不存在");
        }
        return new ApiResponse<>(resume);
    }
    
    @PostMapping("/resumes/{id}/approve")
    public ApiResponse<Resume> approveOffer(@PathVariable String id) {
        Resume resume = recruitmentService.approveOffer(id);
        if (resume == null) {
            return new ApiResponse<>("error", "简历不存在");
        }
        return new ApiResponse<>(resume);
    }
    
    @PostMapping("/resumes/{id}/reject")
    public ApiResponse<Resume> rejectResume(
            @PathVariable String id,
            @RequestParam String reason) {
        Resume resume = recruitmentService.rejectResume(id, reason);
        if (resume == null) {
            return new ApiResponse<>("error", "简历不存在");
        }
        return new ApiResponse<>(resume);
    }
    
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        return new ApiResponse<>(recruitmentService.getStatistics());
    }
    
    @GetMapping("/ai-match")
    public ApiResponse<Map<String, Object>> aiMatch(
            @RequestParam String resumeId,
            @RequestParam String jobId) {
        return new ApiResponse<>(recruitmentService.aiMatch(resumeId, jobId));
    }
    
    @GetMapping("/dashboard")
    public ApiResponse<List<Map<String, Object>>> getDashboardData() {
        return new ApiResponse<>(recruitmentService.getDashboardData());
    }
}

package net.ooder.skill.recruitment.service;

import net.ooder.skill.recruitment.dto.InterviewFeedbackRequest;
import net.ooder.skill.recruitment.dto.InterviewScheduleRequest;
import net.ooder.skill.recruitment.dto.JobCreateRequest;
import net.ooder.skill.recruitment.dto.PageResult;
import net.ooder.skill.recruitment.dto.ResumeCreateRequest;
import net.ooder.skill.recruitment.model.Job;
import net.ooder.skill.recruitment.model.Resume;

import java.util.List;
import java.util.Map;

public interface RecruitmentService {
    
    List<Job> listJobs(String status, String department);
    
    Job getJob(String id);
    
    Job createJob(JobCreateRequest request);
    
    Job updateJob(String id, JobCreateRequest request);
    
    void deleteJob(String id);
    
    Job publishJob(String id);
    
    Job closeJob(String id);
    
    PageResult<Resume> listResumes(String jobId, String status, String stage, int page, int size);
    
    Resume getResume(String id);
    
    Resume createResume(ResumeCreateRequest request);
    
    Resume updateResume(String id, ResumeCreateRequest request);
    
    void deleteResume(String id);
    
    Resume parseResume(String id);
    
    Resume updateResumeStatus(String id, String status, String stage);
    
    Resume scheduleInterview(String resumeId, InterviewScheduleRequest request);
    
    Resume submitInterviewFeedback(String resumeId, String interviewId, InterviewFeedbackRequest request);
    
    Resume approveOffer(String resumeId);
    
    Resume rejectResume(String resumeId, String reason);
    
    Map<String, Object> getStatistics();
    
    Map<String, Object> aiMatch(String resumeId, String jobId);
    
    List<Map<String, Object>> getDashboardData();
}

package net.ooder.skill.onboarding.assistant.controller;

import net.ooder.skill.onboarding.assistant.dto.LearningPathResponse;
import net.ooder.skill.onboarding.assistant.dto.LearningProgressResponse;
import net.ooder.skill.onboarding.assistant.dto.LearningReportResponse;
import net.ooder.skill.onboarding.assistant.dto.ProgressUpdateResponse;
import net.ooder.skill.onboarding.assistant.dto.TrainingAnswerResponse;
import net.ooder.skill.onboarding.assistant.service.OnboardingAssistantService;
import net.ooder.skill.onboarding.assistant.service.OnboardingAssistantService.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/onboarding")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class OnboardingAssistantController {
    
    @Autowired
    private OnboardingAssistantService onboardingAssistantService;
    
    @PostMapping("/learning-path")
    public ResponseEntity<LearningPathResponse> initLearningPath(
            @RequestParam String employeeId,
            @RequestParam String position,
            @RequestParam(required = false) String department) {
        
        LearningPath path = onboardingAssistantService.initLearningPath(employeeId, position, department);
        
        LearningPathResponse response = new LearningPathResponse();
        response.setPathId(path.getPathId());
        response.setEmployeeId(path.getEmployeeId());
        response.setPosition(path.getPosition());
        response.setDepartment(path.getDepartment());
        response.setStages(path.getStages());
        response.setTotalDuration(path.getTotalDuration());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/learning-path/{employeeId}")
    public ResponseEntity<LearningPathResponse> getLearningPath(@PathVariable String employeeId) {
        LearningPath path = onboardingAssistantService.getLearningPath(employeeId);
        
        if (path == null) {
            return ResponseEntity.notFound().build();
        }
        
        LearningPathResponse response = new LearningPathResponse();
        response.setPathId(path.getPathId());
        response.setStages(path.getStages());
        response.setTotalDuration(path.getTotalDuration());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/question")
    public ResponseEntity<TrainingAnswerResponse> askQuestion(
            @RequestParam String employeeId,
            @RequestParam String question,
            @RequestParam(required = false) String context) {
        
        TrainingAnswer answer = onboardingAssistantService.askQuestion(employeeId, question, context);
        
        TrainingAnswerResponse response = new TrainingAnswerResponse();
        response.setAnswer(answer.getAnswer());
        response.setSources(answer.getSources());
        response.setConfidence(answer.getConfidence());
        response.setNeedHumanSupport(answer.isNeedHumanSupport());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/report/{employeeId}")
    public ResponseEntity<LearningReportResponse> generateReport(@PathVariable String employeeId) {
        LearningReport report = onboardingAssistantService.generateReport(employeeId);
        
        LearningReportResponse response = new LearningReportResponse();
        response.setEmployeeId(report.getEmployeeId());
        response.setProgress(report.getProgress());
        response.setCompletedTasks(report.getCompletedTasks());
        response.setRemainingTasks(report.getRemainingTasks());
        response.setAssessmentScore(report.getAssessmentScore());
        response.setStages(report.getStages());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/progress")
    public ResponseEntity<ProgressUpdateResponse> updateProgress(
            @RequestParam String employeeId,
            @RequestParam String stageId,
            @RequestParam(defaultValue = "false") boolean completed) {
        
        ProgressUpdate update = onboardingAssistantService.updateProgress(employeeId, stageId, completed);
        
        ProgressUpdateResponse response = new ProgressUpdateResponse();
        response.setStatus(update.getStatus());
        response.setProgress(update.getProgress());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/progress/{employeeId}")
    public ResponseEntity<LearningProgressResponse> getProgress(@PathVariable String employeeId) {
        LearningProgress progress = onboardingAssistantService.getProgress(employeeId);
        
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }
        
        LearningProgressResponse response = new LearningProgressResponse();
        response.setEmployeeId(progress.getEmployeeId());
        response.setCompletedStages(progress.getCompletedStages());
        response.setTotalStages(progress.getTotalStages());
        response.setProgress(progress.getProgress());
        response.setStartedAt(progress.getStartedAt());
        
        return ResponseEntity.ok(response);
    }
}

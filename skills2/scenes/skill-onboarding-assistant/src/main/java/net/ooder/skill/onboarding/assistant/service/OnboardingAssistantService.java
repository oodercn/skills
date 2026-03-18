package net.ooder.skill.onboarding.assistant.service;

import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.KnowledgeBase;
import net.ooder.scene.skill.knowledge.KnowledgeBaseCreateRequest;
import net.ooder.scene.skill.rag.RagApi;
import net.ooder.scene.skill.rag.RagContext;
import net.ooder.scene.skill.rag.RagResult;
import net.ooder.scene.skill.conversation.ConversationService;
import net.ooder.scene.skill.conversation.MessageRequest;
import net.ooder.scene.skill.conversation.MessageResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnboardingAssistantService {
    
    private static final Logger log = LoggerFactory.getLogger(OnboardingAssistantService.class);
    
    @Value("${onboarding.confidence-threshold:0.6}")
    private float confidenceThreshold;
    
    @Value("${onboarding.default-kb-ids:company-policy-kb,hr-kb}")
    private String defaultKbIds;
    
    @Autowired(required = false)
    private KnowledgeBaseService knowledgeBaseService;
    
    @Autowired(required = false)
    private RagApi ragPipeline;
    
    @Autowired(required = false)
    private ConversationService conversationService;
    
    private final Map<String, LearningPath> learningPaths = new ConcurrentHashMap<>();
    private final Map<String, LearningProgress> progressMap = new ConcurrentHashMap<>();
    private final Map<String, String> sessionMap = new ConcurrentHashMap<>();
    
    public LearningPath initLearningPath(String employeeId, String position, String department) {
        log.info("Initializing learning path: employeeId={}, position={}, department={}", 
            employeeId, position, department);
        
        String personalKbId = createPersonalKb(employeeId);
        
        List<LearningStage> stages = buildLearningStages(position, department);
        
        LearningPath path = new LearningPath();
        path.setPathId(UUID.randomUUID().toString());
        path.setEmployeeId(employeeId);
        path.setPosition(position);
        path.setDepartment(department);
        path.setPersonalKbId(personalKbId);
        path.setStages(stages);
        path.setCreatedAt(new Date());
        path.setTotalDuration(stages.stream().mapToInt(LearningStage::getDuration).sum());
        
        learningPaths.put(employeeId, path);
        
        LearningProgress progress = new LearningProgress();
        progress.setEmployeeId(employeeId);
        progress.setPathId(path.getPathId());
        progress.setCompletedStages(new ArrayList<>());
        progress.setTotalStages(stages.size());
        progress.setProgress(0.0);
        progress.setStartedAt(new Date());
        progressMap.put(employeeId, progress);
        
        return path;
    }
    
    public TrainingAnswer askQuestion(String employeeId, String question, String context) {
        log.info("Processing question: employeeId={}, question={}", employeeId, question);
        
        List<String> kbIds = getKbIdsForEmployee(employeeId);
        
        float confidence = 0.0f;
        String answer;
        List<SourceReference> sources = new ArrayList<>();
        
        if (ragPipeline != null) {
            try {
                RagContext ragContext = RagContext.builder()
                    .query(question)
                    .kbIds(kbIds)
                    .topK(3)
                    .build();
                
                RagResult result = ragPipeline.retrieve(ragContext);
                answer = ragPipeline.generate(question, result);
                
                if (result.getChunks() != null && !result.getChunks().isEmpty()) {
                    confidence = result.getChunks().get(0).getScore();
                    for (var chunk : result.getChunks()) {
                        sources.add(new SourceReference(
                            chunk.getDocId(),
                            chunk.getDocTitle(),
                            chunk.getScore()
                        ));
                    }
                }
            } catch (Exception e) {
                log.error("RAG pipeline failed", e);
                answer = generateDefaultAnswer(question);
            }
        } else {
            answer = generateDefaultAnswer(question);
        }
        
        boolean needHumanSupport = confidence < confidenceThreshold;
        
        return new TrainingAnswer(answer, sources, confidence, needHumanSupport);
    }
    
    public LearningReport generateReport(String employeeId) {
        log.info("Generating learning report: employeeId={}", employeeId);
        
        LearningProgress progress = progressMap.get(employeeId);
        LearningPath path = learningPaths.get(employeeId);
        
        if (progress == null || path == null) {
            return new LearningReport(employeeId, 0.0, 0, 0, 0.0, null);
        }
        
        int completedTasks = progress.getCompletedStages().size();
        int remainingTasks = progress.getTotalStages() - completedTasks;
        
        double assessmentScore = calculateAssessmentScore(employeeId);
        
        return new LearningReport(
            employeeId,
            progress.getProgress(),
            completedTasks,
            remainingTasks,
            assessmentScore,
            path.getStages()
        );
    }
    
    public ProgressUpdate updateProgress(String employeeId, String stageId, boolean completed) {
        log.info("Updating progress: employeeId={}, stageId={}, completed={}", employeeId, stageId, completed);
        
        LearningProgress progress = progressMap.get(employeeId);
        
        if (progress == null) {
            return new ProgressUpdate("not-found", 0.0);
        }
        
        if (completed && !progress.getCompletedStages().contains(stageId)) {
            progress.getCompletedStages().add(stageId);
        }
        
        double progressValue = (double) progress.getCompletedStages().size() / progress.getTotalStages() * 100;
        progress.setProgress(progressValue);
        
        return new ProgressUpdate("updated", progressValue);
    }
    
    public LearningPath getLearningPath(String employeeId) {
        return learningPaths.get(employeeId);
    }
    
    public LearningProgress getProgress(String employeeId) {
        return progressMap.get(employeeId);
    }
    
    private String createPersonalKb(String employeeId) {
        if (knowledgeBaseService == null) {
            return "personal-kb-" + employeeId;
        }
        
        try {
            KnowledgeBaseCreateRequest request = KnowledgeBaseCreateRequest.builder()
                .name("我的入职学习")
                .ownerId(employeeId)
                .visibility(KnowledgeBase.VISIBILITY_PRIVATE)
                .build();
            
            KnowledgeBase kb = knowledgeBaseService.create(request);
            return kb.getKbId();
        } catch (Exception e) {
            log.error("Failed to create personal KB", e);
            return "personal-kb-" + employeeId;
        }
    }
    
    private List<LearningStage> buildLearningStages(String position, String department) {
        List<LearningStage> stages = new ArrayList<>();
        
        stages.add(new LearningStage("stage-1", "公司介绍", "了解公司历史、文化、组织架构", 1, "company-intro"));
        stages.add(new LearningStage("stage-2", "规章制度", "学习公司规章制度和考勤制度", 2, "policy"));
        stages.add(new LearningStage("stage-3", "部门介绍", "了解" + department + "部门职责和团队", 1, "dept-intro"));
        stages.add(new LearningStage("stage-4", "岗位培训", position + "岗位技能培训", 3, "position-training"));
        stages.add(new LearningStage("stage-5", "系统操作", "学习公司系统和工具使用", 2, "system-training"));
        stages.add(new LearningStage("stage-6", "考核评估", "完成入职考核", 1, "assessment"));
        
        return stages;
    }
    
    private List<String> getKbIdsForEmployee(String employeeId) {
        List<String> kbIds = new ArrayList<>();
        
        LearningPath path = learningPaths.get(employeeId);
        if (path != null && path.getPersonalKbId() != null) {
            kbIds.add(path.getPersonalKbId());
        }
        
        kbIds.addAll(Arrays.asList(defaultKbIds.split(",")));
        
        return kbIds;
    }
    
    private String generateDefaultAnswer(String question) {
        return "感谢您的提问。关于\"" + question + "\"，建议您咨询HR部门或您的直属上级获取更详细的解答。";
    }
    
    private double calculateAssessmentScore(String employeeId) {
        LearningProgress progress = progressMap.get(employeeId);
        if (progress == null) return 0.0;
        
        return progress.getProgress() * 0.8 + Math.random() * 20;
    }
    
    public static class LearningPath {
        private String pathId;
        private String employeeId;
        private String position;
        private String department;
        private String personalKbId;
        private List<LearningStage> stages;
        private int totalDuration;
        private Date createdAt;
        
        public String getPathId() { return pathId; }
        public void setPathId(String pathId) { this.pathId = pathId; }
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getPersonalKbId() { return personalKbId; }
        public void setPersonalKbId(String personalKbId) { this.personalKbId = personalKbId; }
        public List<LearningStage> getStages() { return stages; }
        public void setStages(List<LearningStage> stages) { this.stages = stages; }
        public int getTotalDuration() { return totalDuration; }
        public void setTotalDuration(int totalDuration) { this.totalDuration = totalDuration; }
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    }
    
    public static class LearningStage {
        private String stageId;
        private String name;
        private String description;
        private int duration;
        private String type;
        
        public LearningStage(String stageId, String name, String description, int duration, String type) {
            this.stageId = stageId;
            this.name = name;
            this.description = description;
            this.duration = duration;
            this.type = type;
        }
        
        public String getStageId() { return stageId; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getDuration() { return duration; }
        public String getType() { return type; }
    }
    
    public static class LearningProgress {
        private String employeeId;
        private String pathId;
        private List<String> completedStages;
        private int totalStages;
        private double progress;
        private Date startedAt;
        
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public String getPathId() { return pathId; }
        public void setPathId(String pathId) { this.pathId = pathId; }
        public List<String> getCompletedStages() { return completedStages; }
        public void setCompletedStages(List<String> completedStages) { this.completedStages = completedStages; }
        public int getTotalStages() { return totalStages; }
        public void setTotalStages(int totalStages) { this.totalStages = totalStages; }
        public double getProgress() { return progress; }
        public void setProgress(double progress) { this.progress = progress; }
        public Date getStartedAt() { return startedAt; }
        public void setStartedAt(Date startedAt) { this.startedAt = startedAt; }
    }
    
    public static class TrainingAnswer {
        private String answer;
        private List<SourceReference> sources;
        private float confidence;
        private boolean needHumanSupport;
        
        public TrainingAnswer(String answer, List<SourceReference> sources, float confidence, boolean needHumanSupport) {
            this.answer = answer;
            this.sources = sources;
            this.confidence = confidence;
            this.needHumanSupport = needHumanSupport;
        }
        
        public String getAnswer() { return answer; }
        public List<SourceReference> getSources() { return sources; }
        public float getConfidence() { return confidence; }
        public boolean isNeedHumanSupport() { return needHumanSupport; }
    }
    
    public static class SourceReference {
        private String docId;
        private String title;
        private float score;
        
        public SourceReference(String docId, String title, float score) {
            this.docId = docId;
            this.title = title;
            this.score = score;
        }
        
        public String getDocId() { return docId; }
        public String getTitle() { return title; }
        public float getScore() { return score; }
    }
    
    public static class LearningReport {
        private String employeeId;
        private double progress;
        private int completedTasks;
        private int remainingTasks;
        private double assessmentScore;
        private List<LearningStage> stages;
        
        public LearningReport(String employeeId, double progress, int completedTasks, 
                              int remainingTasks, double assessmentScore, List<LearningStage> stages) {
            this.employeeId = employeeId;
            this.progress = progress;
            this.completedTasks = completedTasks;
            this.remainingTasks = remainingTasks;
            this.assessmentScore = assessmentScore;
            this.stages = stages;
        }
        
        public String getEmployeeId() { return employeeId; }
        public double getProgress() { return progress; }
        public int getCompletedTasks() { return completedTasks; }
        public int getRemainingTasks() { return remainingTasks; }
        public double getAssessmentScore() { return assessmentScore; }
        public List<LearningStage> getStages() { return stages; }
    }
    
    public static class ProgressUpdate {
        private String status;
        private double progress;
        
        public ProgressUpdate(String status, double progress) {
            this.status = status;
            this.progress = progress;
        }
        
        public String getStatus() { return status; }
        public double getProgress() { return progress; }
    }
}

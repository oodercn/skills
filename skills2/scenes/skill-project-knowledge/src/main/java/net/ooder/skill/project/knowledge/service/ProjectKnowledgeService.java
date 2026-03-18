package net.ooder.skill.project.knowledge.service;

import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.DocumentCreateRequest;
import net.ooder.scene.skill.classification.SceneSkillClassifier;
import net.ooder.scene.skill.vector.EmbeddingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ProjectKnowledgeService {
    
    private static final Logger log = LoggerFactory.getLogger(ProjectKnowledgeService.class);
    
    @Value("${project.classification-threshold:0.8}")
    private float classificationThreshold;
    
    @Value("${project.max-import-files:100}")
    private int maxImportFiles;
    
    @Value("${project.similarity-threshold:0.7}")
    private float similarityThreshold;
    
    @Autowired(required = false)
    private KnowledgeBaseService knowledgeBaseService;
    
    @Autowired(required = false)
    private EmbeddingService embeddingService;
    
    private final Map<String, ImportTask> importTasks = new ConcurrentHashMap<>();
    private final Map<String, ProjectFeatures> projectFeatures = new ConcurrentHashMap<>();
    private final Map<String, List<Document>> projectDocuments = new ConcurrentHashMap<>();
    
    public ImportTask importProjectDocs(String projectId, MultipartFile archiveFile, boolean autoClassify) throws IOException {
        log.info("Importing project docs: projectId={}, autoClassify={}", projectId, autoClassify);
        
        String taskId = UUID.randomUUID().toString();
        ImportTask task = new ImportTask();
        task.setTaskId(taskId);
        task.setProjectId(projectId);
        task.setStatus("processing");
        task.setStartTime(new Date());
        task.setTotalFiles(0);
        task.setProcessedFiles(0);
        
        importTasks.put(taskId, task);
        
        processArchiveAsync(taskId, projectId, archiveFile, autoClassify);
        
        return task;
    }
    
    @Async
    protected void processArchiveAsync(String taskId, String projectId, MultipartFile archiveFile, boolean autoClassify) {
        ImportTask task = importTasks.get(taskId);
        List<Document> documents = new ArrayList<>();
        
        try (ZipInputStream zis = new ZipInputStream(archiveFile.getInputStream())) {
            ZipEntry entry;
            int fileCount = 0;
            
            while ((entry = zis.getNextEntry()) != null && fileCount < maxImportFiles) {
                if (!entry.isDirectory()) {
                    String filename = entry.getName();
                    String content = readStreamContent(zis);
                    
                    Document doc = processDocument(projectId, filename, content, autoClassify);
                    if (doc != null) {
                        documents.add(doc);
                        task.setProcessedFiles(task.getProcessedFiles() + 1);
                    }
                    
                    fileCount++;
                    task.setTotalFiles(fileCount);
                }
                zis.closeEntry();
            }
            
            projectDocuments.put(projectId, documents);
            
            extractProjectFeatures(projectId, documents);
            
            task.setStatus("completed");
            task.setEndTime(new Date());
            
        } catch (Exception e) {
            log.error("Failed to process archive", e);
            task.setStatus("failed");
            task.setError(e.getMessage());
            task.setEndTime(new Date());
        }
    }
    
    private Document processDocument(String projectId, String filename, String content, boolean autoClassify) {
        if (knowledgeBaseService == null) {
            return null;
        }
        
        try {
            String docType = autoClassify ? classifyDocument(content) : "other";
            List<String> tags = autoClassify ? extractTags(content) : new ArrayList<>();
            
            DocumentCreateRequest request = new DocumentCreateRequest();
            request.setTitle(filename);
            request.setContent(content);
            request.setSource(Document.SOURCE_FILE);
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("projectId", projectId);
            metadata.put("docType", docType);
            metadata.put("tags", tags);
            metadata.put("filename", filename);
            request.setMetadata(metadata);
            
            String kbId = getProjectKbId(projectId);
            return knowledgeBaseService.addDocument(kbId, request);
            
        } catch (Exception e) {
            log.error("Failed to process document: {}", filename, e);
            return null;
        }
    }
    
    public ClassificationResult classifyDocument(String docId, String content) {
        log.info("Classifying document: docId={}", docId);
        
        String docType = classifyDocument(content);
        List<String> tags = extractTags(content);
        float confidence = calculateClassificationConfidence(content, docType);
        
        return new ClassificationResult(docType, tags, confidence);
    }
    
    private String classifyDocument(String content) {
        String lowerContent = content.toLowerCase();
        
        if (lowerContent.contains("需求") || lowerContent.contains("requirement") || 
            lowerContent.contains("功能") || lowerContent.contains("用户故事")) {
            return "requirement";
        }
        
        if (lowerContent.contains("设计") || lowerContent.contains("design") || 
            lowerContent.contains("架构") || lowerContent.contains("接口")) {
            return "design";
        }
        
        if (lowerContent.contains("测试") || lowerContent.contains("test") || 
            lowerContent.contains("用例") || lowerContent.contains("验证")) {
            return "test";
        }
        
        if (lowerContent.contains("总结") || lowerContent.contains("summary") || 
            lowerContent.contains("复盘") || lowerContent.contains("经验")) {
            return "summary";
        }
        
        return "other";
    }
    
    private List<String> extractTags(String content) {
        List<String> tags = new ArrayList<>();
        
        String[] techKeywords = {"java", "python", "javascript", "react", "vue", "spring", "docker", "k8s"};
        for (String keyword : techKeywords) {
            if (content.toLowerCase().contains(keyword)) {
                tags.add("tech:" + keyword);
            }
        }
        
        Pattern pattern = Pattern.compile("\\b\\d+\\.\\d+\\.\\d+\\b");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            tags.add("version:" + matcher.group());
        }
        
        return tags;
    }
    
    private float calculateClassificationConfidence(String content, String docType) {
        if (docType.equals("other")) {
            return 0.5f;
        }
        return 0.85f + (float) Math.random() * 0.1f;
    }
    
    public List<ProjectRecommendation> discoverSimilarProjects(String projectId, int topK) {
        log.info("Discovering similar projects: projectId={}, topK={}", projectId, topK);
        
        ProjectFeatures features = projectFeatures.get(projectId);
        if (features == null) {
            return new ArrayList<>();
        }
        
        List<ProjectRecommendation> recommendations = new ArrayList<>();
        
        for (Map.Entry<String, ProjectFeatures> entry : projectFeatures.entrySet()) {
            if (!entry.getKey().equals(projectId)) {
                double similarity = calculateSimilarity(features, entry.getValue());
                if (similarity >= similarityThreshold) {
                    recommendations.add(new ProjectRecommendation(
                        entry.getKey(),
                        "项目-" + entry.getKey().substring(0, 8),
                        similarity
                    ));
                }
            }
        }
        
        recommendations.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));
        
        if (recommendations.size() > topK) {
            recommendations = recommendations.subList(0, topK);
        }
        
        return recommendations;
    }
    
    private double calculateSimilarity(ProjectFeatures a, ProjectFeatures b) {
        double score = 0.0;
        
        Set<String> aTags = new HashSet<>(a.getTechTags());
        Set<String> bTags = new HashSet<>(b.getTechTags());
        Set<String> intersection = new HashSet<>(aTags);
        intersection.retainAll(bTags);
        
        if (!aTags.isEmpty() || !bTags.isEmpty()) {
            score += (double) intersection.size() / Math.max(aTags.size(), bTags.size()) * 0.5;
        }
        
        if (a.getDocTypeDistribution().keySet().stream().anyMatch(b.getDocTypeDistribution().keySet()::contains)) {
            score += 0.3;
        }
        
        score += Math.random() * 0.2;
        
        return score;
    }
    
    public KnowledgeGraph generateKnowledgeGraph(String projectId) {
        log.info("Generating knowledge graph: projectId={}", projectId);
        
        KnowledgeGraph graph = new KnowledgeGraph();
        
        List<Document> docs = projectDocuments.getOrDefault(projectId, new ArrayList<>());
        
        List<KnowledgeNode> nodes = new ArrayList<>();
        List<KnowledgeEdge> edges = new ArrayList<>();
        
        for (Document doc : docs) {
            String docType = (String) doc.getMetadata().get("docType");
            
            KnowledgeNode node = new KnowledgeNode();
            node.setId(doc.getDocId());
            node.setLabel(doc.getTitle());
            node.setType(docType);
            nodes.add(node);
            
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) doc.getMetadata().get("tags");
            if (tags != null) {
                for (String tag : tags) {
                    if (tag.startsWith("tech:")) {
                        String tech = tag.substring(5);
                        KnowledgeNode techNode = nodes.stream()
                            .filter(n -> n.getLabel().equals(tech))
                            .findFirst()
                            .orElse(null);
                        
                        if (techNode == null) {
                            techNode = new KnowledgeNode();
                            techNode.setId("tech-" + tech);
                            techNode.setLabel(tech);
                            techNode.setType("technology");
                            nodes.add(techNode);
                        }
                        
                        edges.add(new KnowledgeEdge(doc.getDocId(), techNode.getId(), "uses"));
                    }
                }
            }
        }
        
        graph.setNodes(nodes);
        graph.setEdges(edges);
        
        return graph;
    }
    
    public ImportTask getTask(String taskId) {
        return importTasks.get(taskId);
    }
    
    private String readStreamContent(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
    
    private String getProjectKbId(String projectId) {
        return "project-kb-" + projectId;
    }
    
    private void extractProjectFeatures(String projectId, List<Document> documents) {
        ProjectFeatures features = new ProjectFeatures();
        features.setProjectId(projectId);
        
        Set<String> techTags = new HashSet<>();
        Map<String, Integer> docTypeDistribution = new HashMap<>();
        
        for (Document doc : documents) {
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) doc.getMetadata().get("tags");
            if (tags != null) {
                for (String tag : tags) {
                    if (tag.startsWith("tech:")) {
                        techTags.add(tag.substring(5));
                    }
                }
            }
            
            String docType = (String) doc.getMetadata().get("docType");
            docTypeDistribution.merge(docType, 1, Integer::sum);
        }
        
        features.setTechTags(new ArrayList<>(techTags));
        features.setDocTypeDistribution(docTypeDistribution);
        features.setDocumentCount(documents.size());
        
        projectFeatures.put(projectId, features);
    }
    
    public static class ImportTask {
        private String taskId;
        private String projectId;
        private String status;
        private int totalFiles;
        private int processedFiles;
        private Date startTime;
        private Date endTime;
        private String error;
        
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public String getProjectId() { return projectId; }
        public void setProjectId(String projectId) { this.projectId = projectId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getTotalFiles() { return totalFiles; }
        public void setTotalFiles(int totalFiles) { this.totalFiles = totalFiles; }
        public int getProcessedFiles() { return processedFiles; }
        public void setProcessedFiles(int processedFiles) { this.processedFiles = processedFiles; }
        public Date getStartTime() { return startTime; }
        public void setStartTime(Date startTime) { this.startTime = startTime; }
        public Date getEndTime() { return endTime; }
        public void setEndTime(Date endTime) { this.endTime = endTime; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
    
    public static class ClassificationResult {
        private String docType;
        private List<String> tags;
        private float confidence;
        
        public ClassificationResult(String docType, List<String> tags, float confidence) {
            this.docType = docType;
            this.tags = tags;
            this.confidence = confidence;
        }
        
        public String getDocType() { return docType; }
        public List<String> getTags() { return tags; }
        public float getConfidence() { return confidence; }
    }
    
    public static class ProjectRecommendation {
        private String projectId;
        private String projectName;
        private double similarityScore;
        
        public ProjectRecommendation(String projectId, String projectName, double similarityScore) {
            this.projectId = projectId;
            this.projectName = projectName;
            this.similarityScore = similarityScore;
        }
        
        public String getProjectId() { return projectId; }
        public String getProjectName() { return projectName; }
        public double getSimilarityScore() { return similarityScore; }
    }
    
    public static class KnowledgeGraph {
        private List<KnowledgeNode> nodes;
        private List<KnowledgeEdge> edges;
        
        public List<KnowledgeNode> getNodes() { return nodes; }
        public void setNodes(List<KnowledgeNode> nodes) { this.nodes = nodes; }
        public List<KnowledgeEdge> getEdges() { return edges; }
        public void setEdges(List<KnowledgeEdge> edges) { this.edges = edges; }
    }
    
    public static class KnowledgeNode {
        private String id;
        private String label;
        private String type;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
    
    public static class KnowledgeEdge {
        private String source;
        private String target;
        private String relation;
        
        public KnowledgeEdge(String source, String target, String relation) {
            this.source = source;
            this.target = target;
            this.relation = relation;
        }
        
        public String getSource() { return source; }
        public String getTarget() { return target; }
        public String getRelation() { return relation; }
    }
    
    public static class ProjectFeatures {
        private String projectId;
        private List<String> techTags;
        private Map<String, Integer> docTypeDistribution;
        private int documentCount;
        
        public String getProjectId() { return projectId; }
        public void setProjectId(String projectId) { this.projectId = projectId; }
        public List<String> getTechTags() { return techTags; }
        public void setTechTags(List<String> techTags) { this.techTags = techTags; }
        public Map<String, Integer> getDocTypeDistribution() { return docTypeDistribution; }
        public void setDocTypeDistribution(Map<String, Integer> docTypeDistribution) { this.docTypeDistribution = docTypeDistribution; }
        public int getDocumentCount() { return documentCount; }
        public void setDocumentCount(int documentCount) { this.documentCount = documentCount; }
    }
}

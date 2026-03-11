package net.ooder.skill.project.knowledge.controller;

import net.ooder.skill.project.knowledge.dto.ClassifyDocumentResponse;
import net.ooder.skill.project.knowledge.dto.ImportTaskResponse;
import net.ooder.skill.project.knowledge.dto.KnowledgeGraphResponse;
import net.ooder.skill.project.knowledge.dto.ProjectRecommendationResponse;
import net.ooder.skill.project.knowledge.service.ProjectKnowledgeService;
import net.ooder.skill.project.knowledge.service.ProjectKnowledgeService.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/project-knowledge")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ProjectKnowledgeController {
    
    @Autowired
    private ProjectKnowledgeService projectKnowledgeService;
    
    @PostMapping("/import")
    public ResponseEntity<ImportTaskResponse> importProjectDocs(
            @RequestParam("projectId") String projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "autoClassify", defaultValue = "true") boolean autoClassify) throws IOException {
        
        ImportTask task = projectKnowledgeService.importProjectDocs(projectId, file, autoClassify);
        
        ImportTaskResponse response = new ImportTaskResponse();
        response.setTaskId(task.getTaskId());
        response.setProjectId(task.getProjectId());
        response.setStatus(task.getStatus());
        response.setTotalFiles(task.getTotalFiles());
        response.setProcessedFiles(task.getProcessedFiles());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/task/{taskId}")
    public ResponseEntity<ImportTaskResponse> getTask(@PathVariable String taskId) {
        ImportTask task = projectKnowledgeService.getTask(taskId);
        
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        
        ImportTaskResponse response = new ImportTaskResponse();
        response.setTaskId(task.getTaskId());
        response.setProjectId(task.getProjectId());
        response.setStatus(task.getStatus());
        response.setTotalFiles(task.getTotalFiles());
        response.setProcessedFiles(task.getProcessedFiles());
        response.setStartTime(task.getStartTime());
        response.setEndTime(task.getEndTime());
        response.setError(task.getError());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/classify")
    public ResponseEntity<ClassifyDocumentResponse> classifyDocument(
            @RequestParam String docId,
            @RequestParam String content) {
        
        ClassificationResult result = projectKnowledgeService.classifyDocument(docId, content);
        
        ClassifyDocumentResponse response = new ClassifyDocumentResponse();
        response.setDocType(result.getDocType());
        response.setTags(result.getTags());
        response.setConfidence(result.getConfidence());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/similar/{projectId}")
    public ResponseEntity<List<ProjectRecommendationResponse>> discoverSimilarProjects(
            @PathVariable String projectId,
            @RequestParam(value = "topK", defaultValue = "5") int topK) {
        
        List<ProjectRecommendation> recommendations = projectKnowledgeService.discoverSimilarProjects(projectId, topK);
        List<ProjectRecommendationResponse> responseList = recommendations.stream()
            .map(this::toProjectRecommendationResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responseList);
    }
    
    @GetMapping("/graph/{projectId}")
    public ResponseEntity<KnowledgeGraphResponse> generateKnowledgeGraph(@PathVariable String projectId) {
        KnowledgeGraph graph = projectKnowledgeService.generateKnowledgeGraph(projectId);
        
        KnowledgeGraphResponse response = new KnowledgeGraphResponse();
        if (graph != null) {
            if (graph.getNodes() != null) {
                response.setNodes(graph.getNodes().stream()
                    .map(this::toNodeResponse)
                    .collect(Collectors.toList()));
            }
            if (graph.getEdges() != null) {
                response.setEdges(graph.getEdges().stream()
                    .map(this::toEdgeResponse)
                    .collect(Collectors.toList()));
            }
        }
        
        return ResponseEntity.ok(response);
    }
    
    private ProjectRecommendationResponse toProjectRecommendationResponse(ProjectRecommendation rec) {
        ProjectRecommendationResponse response = new ProjectRecommendationResponse();
        response.setProjectId(rec.getProjectId());
        response.setProjectName(rec.getProjectName());
        response.setSimilarity(rec.getSimilarity());
        response.setDescription(rec.getDescription());
        return response;
    }
    
    private KnowledgeGraphResponse.NodeResponse toNodeResponse(Node node) {
        KnowledgeGraphResponse.NodeResponse response = new KnowledgeGraphResponse.NodeResponse();
        response.setId(node.getId());
        response.setLabel(node.getLabel());
        response.setType(node.getType());
        return response;
    }
    
    private KnowledgeGraphResponse.EdgeResponse toEdgeResponse(Edge edge) {
        KnowledgeGraphResponse.EdgeResponse response = new KnowledgeGraphResponse.EdgeResponse();
        response.setSource(edge.getSource());
        response.setTarget(edge.getTarget());
        response.setLabel(edge.getLabel());
        return response;
    }
}

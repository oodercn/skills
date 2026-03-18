package net.ooder.skill.recording.qa.controller;

import net.ooder.skill.recording.qa.model.Recording;
import net.ooder.skill.recording.qa.model.ScoreItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recording-qa")
public class RecordingController {
    
    private static final Logger log = LoggerFactory.getLogger(RecordingController.class);
    
    // 模拟数据存储
    private final Map<String, Recording> recordings = new ConcurrentHashMap<>();
    private final String storagePath = "./recordings";
    
    // 评分标准
    private final List<ScoreItem> scoringStandard = Arrays.asList(
        new ScoreItem("greeting", "开场白", 10, 10),
        new ScoreItem("communication", "沟通技巧", 30, 30),
        new ScoreItem("professionalism", "专业度", 25, 25),
        new ScoreItem("problemSolving", "问题解决", 25, 25),
        new ScoreItem("closing", "结束语", 10, 10)
    );
    
    public RecordingController() {
        // 确保存储目录存在
        try {
            Files.createDirectories(Paths.get(storagePath));
        } catch (IOException e) {
            log.error("Failed to create storage directory", e);
        }
        
        // 初始化一些测试数据
        initTestData();
    }
    
    private void initTestData() {
        Recording r1 = new Recording();
        r1.setId("REC001");
        r1.setFileName("recording_001.mp3");
        r1.setOriginalFileName("客服通话_20240301_001.mp3");
        r1.setFileSize(1024 * 1024 * 5L);
        r1.setFileType("mp3");
        r1.setDuration(180);
        r1.setUploadedBy("admin");
        r1.setUploadedAt(LocalDateTime.now().minusDays(2));
        r1.setDepartment("客服一部");
        r1.setAgentId("AG001");
        r1.setAgentName("张三");
        r1.setCustomerPhone("138****8888");
        r1.setCallType("来电");
        r1.setStatus("pending");
        recordings.put(r1.getId(), r1);
        
        Recording r2 = new Recording();
        r2.setId("REC002");
        r2.setFileName("recording_002.mp3");
        r2.setOriginalFileName("客服通话_20240301_002.mp3");
        r2.setFileSize(1024 * 1024 * 3L);
        r2.setFileType("mp3");
        r2.setDuration(120);
        r2.setUploadedBy("admin");
        r2.setUploadedAt(LocalDateTime.now().minusDays(1));
        r2.setDepartment("客服二部");
        r2.setAgentId("AG002");
        r2.setAgentName("李四");
        r2.setCustomerPhone("139****9999");
        r2.setCallType("去电");
        r2.setStatus("completed");
        r2.setReviewResult("pass");
        r2.setTotalScore(85);
        r2.setReviewerId("reviewer001");
        r2.setReviewerName("王审核");
        r2.setReviewedAt(LocalDateTime.now());
        r2.setReviewComment("整体表现良好，沟通流畅");
        recordings.put(r2.getId(), r2);
    }
    
    /**
     * 获取评分标准
     */
    @GetMapping("/scoring-standard")
    public ResponseEntity<Map<String, Object>> getScoringStandard() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", scoringStandard);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 上传录音文件
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadRecording(
            @RequestParam("file") MultipartFile file,
            @RequestParam("agentId") String agentId,
            @RequestParam("agentName") String agentName,
            @RequestParam("department") String department,
            @RequestParam("customerPhone") String customerPhone,
            @RequestParam("callType") String callType) {
        
        log.info("[uploadRecording] file: {}, agent: {}", file.getOriginalFilename(), agentName);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            List<String> allowedFormats = Arrays.asList("mp3", "wav", "m4a", "ogg");
            if (!allowedFormats.contains(extension)) {
                result.put("status", "error");
                result.put("message", "不支持的文件格式，仅支持: mp3, wav, m4a, ogg");
                return ResponseEntity.ok(result);
            }
            
            // 生成文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String newFileName = "recording_" + timestamp + "_" + System.currentTimeMillis() + "." + extension;
            
            // 保存文件
            Path filePath = Paths.get(storagePath, newFileName);
            Files.copy(file.getInputStream(), filePath);
            
            // 创建记录
            Recording recording = new Recording();
            recording.setId("REC" + System.currentTimeMillis());
            recording.setFileName(newFileName);
            recording.setOriginalFileName(originalFilename);
            recording.setFilePath(filePath.toString());
            recording.setFileSize(file.getSize());
            recording.setFileType(extension);
            recording.setUploadedBy("currentUser");
            recording.setUploadedAt(LocalDateTime.now());
            recording.setDepartment(department);
            recording.setAgentId(agentId);
            recording.setAgentName(agentName);
            recording.setCustomerPhone(customerPhone);
            recording.setCallType(callType);
            recording.setStatus("pending");
            
            recordings.put(recording.getId(), recording);
            
            result.put("status", "success");
            result.put("message", "上传成功");
            result.put("data", recording);
            
        } catch (Exception e) {
            log.error("[uploadRecording] Error: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "上传失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取录音列表
     */
    @GetMapping("/recordings")
    public ResponseEntity<Map<String, Object>> getRecordings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("[getRecordings] status: {}, department: {}, keyword: {}", status, department, keyword);
        
        List<Recording> list = recordings.values().stream()
            .filter(r -> status == null || status.isEmpty() || r.getStatus().equals(status))
            .filter(r -> department == null || department.isEmpty() || r.getDepartment().equals(department))
            .filter(r -> keyword == null || keyword.isEmpty() || 
                r.getAgentName().contains(keyword) || 
                r.getCustomerPhone().contains(keyword) ||
                r.getOriginalFileName().contains(keyword))
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
            .collect(Collectors.toList());
        
        // 分页
        int total = list.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Recording> pageData = start < total ? list.subList(start, end) : new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", pageData);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取录音详情
     */
    @GetMapping("/recordings/{id}")
    public ResponseEntity<Map<String, Object>> getRecording(@PathVariable String id) {
        log.info("[getRecording] id: {}", id);
        
        Recording recording = recordings.get(id);
        
        Map<String, Object> result = new HashMap<>();
        if (recording != null) {
            result.put("status", "success");
            result.put("data", recording);
        } else {
            result.put("status", "error");
            result.put("message", "录音不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 提交质检评分
     */
    @PostMapping("/recordings/{id}/review")
    public ResponseEntity<Map<String, Object>> submitReview(
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        
        log.info("[submitReview] id: {}", id);
        
        Map<String, Object> result = new HashMap<>();
        
        Recording recording = recordings.get(id);
        if (recording == null) {
            result.put("status", "error");
            result.put("message", "录音不存在");
            return ResponseEntity.ok(result);
        }
        
        try {
            // 解析评分
            List<Map<String, Object>> scoresData = (List<Map<String, Object>>) request.get("scores");
            List<ScoreItem> scores = new ArrayList<>();
            int totalScore = 0;
            
            for (Map<String, Object> scoreData : scoresData) {
                ScoreItem item = new ScoreItem();
                item.setId((String) scoreData.get("id"));
                item.setName((String) scoreData.get("name"));
                item.setActualScore((Integer) scoreData.get("actualScore"));
                item.setComment((String) scoreData.get("comment"));
                scores.add(item);
                totalScore += item.getActualScore();
            }
            
            // 更新录音信息
            recording.setScores(scores);
            recording.setTotalScore(totalScore);
            recording.setReviewResult((String) request.get("result"));
            recording.setReviewComment((String) request.get("comment"));
            recording.setReviewerId("currentUser");
            recording.setReviewerName("当前审核员");
            recording.setReviewedAt(LocalDateTime.now());
            recording.setStatus("completed");
            recording.setUpdatedAt(LocalDateTime.now());
            
            result.put("status", "success");
            result.put("message", "审核完成");
            result.put("data", recording);
            
        } catch (Exception e) {
            log.error("[submitReview] Error: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "审核失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("[getStatistics]");
        
        List<Recording> list = new ArrayList<>(recordings.values());
        
        // 统计各状态数量
        long pendingCount = list.stream().filter(r -> "pending".equals(r.getStatus())).count();
        long reviewingCount = list.stream().filter(r -> "reviewing".equals(r.getStatus())).count();
        long completedCount = list.stream().filter(r -> "completed".equals(r.getStatus())).count();
        
        // 统计审核结果
        long passCount = list.stream().filter(r -> "pass".equals(r.getReviewResult())).count();
        long failCount = list.stream().filter(r -> "fail".equals(r.getReviewResult())).count();
        
        // 平均分
        double avgScore = list.stream()
            .filter(r -> r.getTotalScore() != null)
            .mapToInt(Recording::getTotalScore)
            .average()
            .orElse(0);
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", list.size());
        data.put("pending", pendingCount);
        data.put("reviewing", reviewingCount);
        data.put("completed", completedCount);
        data.put("pass", passCount);
        data.put("fail", failCount);
        data.put("averageScore", Math.round(avgScore * 100) / 100.0);
        
        // 部门统计
        Map<String, Long> deptStats = list.stream()
            .collect(Collectors.groupingBy(Recording::getDepartment, Collectors.counting()));
        data.put("departmentStats", deptStats);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除录音
     */
    @DeleteMapping("/recordings/{id}")
    public ResponseEntity<Map<String, Object>> deleteRecording(@PathVariable String id) {
        log.info("[deleteRecording] id: {}", id);
        
        Map<String, Object> result = new HashMap<>();
        
        Recording recording = recordings.remove(id);
        if (recording != null) {
            // 删除文件
            try {
                Files.deleteIfExists(Paths.get(recording.getFilePath()));
            } catch (IOException e) {
                log.warn("Failed to delete file: {}", recording.getFilePath());
            }
            result.put("status", "success");
            result.put("message", "删除成功");
        } else {
            result.put("status", "error");
            result.put("message", "录音不存在");
        }
        
        return ResponseEntity.ok(result);
    }
}

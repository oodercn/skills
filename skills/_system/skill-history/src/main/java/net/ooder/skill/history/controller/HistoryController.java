package net.ooder.skill.history.controller;

import net.ooder.skill.history.dto.HistoryDTO;
import net.ooder.skill.history.dto.HistoryStatisticsDTO;
import net.ooder.skill.history.service.HistoryService;
import net.ooder.skill.history.service.impl.HistoryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/my/history")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class HistoryController {

    private static final Logger log = LoggerFactory.getLogger(HistoryController.class);

    private HistoryService historyService;

    private HistoryService getHistoryService() {
        if (historyService == null) {
            historyService = new HistoryServiceImpl();
        }
        return historyService;
    }

    private String getCurrentUserId(String userIdHeader) {
        return userIdHeader != null && !userIdHeader.isEmpty() ? userIdHeader : "default-user";
    }

    @GetMapping("/scenes")
    public ResultModel<HistoryService.PageResult<HistoryDTO>> listMyHistory(
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("[listMyHistory] days: {}, category: {}, status: {}, keyword: {}", days, category, status, keyword);
        String userId = getCurrentUserId(userIdHeader);
        HistoryService.PageResult<HistoryDTO> result = getHistoryService().listMyHistory(
            userId, days, category, status, keyword, pageNum, pageSize);
        return ResultModel.success(result);
    }

    @GetMapping("/{executionId}")
    public ResultModel<HistoryDTO> getExecutionDetail(
            @PathVariable String executionId,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("[getExecutionDetail] executionId: {}", executionId);
        String userId = getCurrentUserId(userIdHeader);
        HistoryDTO result = getHistoryService().getExecutionDetail(executionId, userId);
        if (result == null) {
            return ResultModel.error(404, "执行记录不存在");
        }
        return ResultModel.success(result);
    }

    @GetMapping("/statistics")
    public ResultModel<HistoryStatisticsDTO> getStatistics(
            @RequestParam(required = false) Integer days,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("[getStatistics] days: {}", days);
        String userId = getCurrentUserId(userIdHeader);
        HistoryStatisticsDTO result = getHistoryService().getStatistics(userId, days);
        return ResultModel.success(result);
    }

    @PostMapping("/{sceneGroupId}/rerun")
    public ResultModel<Boolean> rerunScene(
            @PathVariable String sceneGroupId,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("[rerunScene] sceneGroupId: {}", sceneGroupId);
        String userId = getCurrentUserId(userIdHeader);
        boolean result = getHistoryService().rerunScene(userId, sceneGroupId);
        if (result) {
            return ResultModel.success(true);
        }
        return ResultModel.error(400, "重新执行失败");
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportHistory(
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("[exportHistory] days: {}, category: {}, status: {}", days, category, status);
        String userId = getCurrentUserId(userIdHeader);
        byte[] data = getHistoryService().exportHistory(userId, days, category, status);
        
        String filename = "场景历史记录_" + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) + ".csv";
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
            .body(data);
    }

    public static class ResultModel<T> {
        private int code;
        private String status;
        private String message;
        private T data;
        private long timestamp;
        private String requestId;

        public ResultModel() {
            this.timestamp = System.currentTimeMillis();
            this.requestId = "REQ_" + timestamp + "_" + new Random().nextInt(1000);
        }

        public static <T> ResultModel<T> success(T data) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(200);
            result.setStatus("success");
            result.setMessage("操作成功");
            result.setData(data);
            return result;
        }

        public static <T> ResultModel<T> error(int code, String message) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(code);
            result.setStatus("error");
            result.setMessage(message);
            return result;
        }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
    }
}

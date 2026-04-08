package net.ooder.skill.dashboard.controller;

import net.ooder.skill.dashboard.dto.*;
import net.ooder.skill.dashboard.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Autowired(required = false)
    private DashboardService dashboardService;

    @PostMapping("/stats")
    public ResultModel<DashboardStatsDTO> getStats() {
        log.info("[getStats] Getting dashboard stats");
        
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalSkills(0);
        stats.setActiveSkills(0);
        stats.setTotalScenes(0);
        stats.setActiveScenes(0);
        stats.setTotalUsers(1);
        stats.setActiveUsers(1);
        
        return ResultModel.success(stats);
    }

    @PostMapping("/execution-stats")
    public ResultModel<ExecutionStatsDTO> getExecutionStats() {
        log.info("[getExecutionStats] Getting execution stats");
        
        ExecutionStatsDTO stats = new ExecutionStatsDTO();
        stats.setTotalExecutions(0);
        stats.setSuccessCount(0);
        stats.setFailureCount(0);
        stats.setSuccessRate(0);
        stats.setAvgExecutionTime(0);
        
        return ResultModel.success(stats);
    }

    @PostMapping("/market-stats")
    public ResultModel<MarketStatsDTO> getMarketStats() {
        log.info("[getMarketStats] Getting market stats");
        
        MarketStatsDTO stats = new MarketStatsDTO();
        stats.setTotalDownloads(0);
        stats.setTotalReviews(0);
        stats.setAvgRating(0.0);
        stats.setTrendingSkills(Collections.emptyList());
        
        return ResultModel.success(stats);
    }

    @PostMapping("/system-stats")
    public ResultModel<SystemStatsDTO> getSystemStats() {
        log.info("[getSystemStats] Getting system stats");
        
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        int memoryUsage = (int) ((usedMemory * 100) / maxMemory);
        
        SystemStatsDTO stats = new SystemStatsDTO();
        stats.setCpuUsage(0);
        stats.setMemoryUsage(memoryUsage);
        stats.setTotalMemory(totalMemory / (1024 * 1024));
        stats.setUsedMemory(usedMemory / (1024 * 1024));
        stats.setMaxMemory(maxMemory / (1024 * 1024));
        stats.setThreadCount(Thread.activeCount());
        stats.setUptime(System.currentTimeMillis());
        
        return ResultModel.success(stats);
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

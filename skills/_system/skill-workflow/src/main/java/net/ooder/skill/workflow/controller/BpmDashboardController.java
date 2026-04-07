package net.ooder.skill.workflow.controller;

import net.ooder.bpm.engine.BPMException;
import net.ooder.skill.workflow.core.BpmCoreService;
import net.ooder.skill.workflow.dto.BpmOverviewDTO;
import net.ooder.skill.workflow.dto.BpmTodoSummaryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/bpm/dashboard")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class BpmDashboardController {

    private static final Logger log = LoggerFactory.getLogger(BpmDashboardController.class);

    @Autowired private BpmCoreService bpmCoreService;

    @GetMapping("/overview")
    public ResultModel<BpmOverviewDTO> getOverview() {
        try {
            Map<String, Object> raw = bpmCoreService.getDashboardOverview();
            BpmOverviewDTO overview = new BpmOverviewDTO();
            overview.setWaitedCount(raw.get("waitedCount") != null ? ((Number)raw.get("waitedCount")).intValue() : 0);
            overview.setMyWorkCount(raw.get("myWorkCount") != null ? ((Number)raw.get("myWorkCount")).intValue() : 0);
            overview.setCompletedCount(raw.get("completedCount") != null ? ((Number)raw.get("completedCount")).intValue() : 0);
            overview.setReadCount(raw.get("readCount") != null ? ((Number)raw.get("readCount")).intValue() : 0);
            overview.setDraftCount(raw.get("draftCount") != null ? ((Number)raw.get("draftCount")).intValue() : 0);
            return ResultModel.success(overview);
        }
        catch (BPMException e) { return ResultModel.fail("获取概览失败: " + e.getMessage()); }
    }

    @GetMapping("/todo-summary")
    public ResultModel<BpmTodoSummaryDTO> getTodoSummary() {
        try {
            Map<String, Object> overview = bpmCoreService.getDashboardOverview();
            BpmTodoSummaryDTO summary = new BpmTodoSummaryDTO();
            summary.setWaitedCount(overview.getOrDefault("waitedCount", 0) instanceof Number ? ((Number)overview.get("waitedCount")).intValue() : 0);
            summary.setMyWorkCount(overview.getOrDefault("myWorkCount", 0) instanceof Number ? ((Number)overview.get("myWorkCount")).intValue() : 0);
            summary.setCompletedCount(overview.getOrDefault("completedCount", 0) instanceof Number ? ((Number)overview.get("completedCount")).intValue() : 0);
            summary.setReadCount(overview.getOrDefault("readCount", 0) instanceof Number ? ((Number)overview.get("readCount")).intValue() : 0);
            summary.setDraftCount(overview.getOrDefault("draftCount", 0) instanceof Number ? ((Number)overview.get("draftCount")).intValue() : 0);
            return ResultModel.success(summary);
        } catch (BPMException e) { return ResultModel.fail(e.getMessage()); }
    }

    @GetMapping("/recent-waited")
    public ResultModel<List<?>> getRecentWaited(@RequestParam(defaultValue = "5") int count) {
        try {
            Map<String, Object> overview = bpmCoreService.getDashboardOverview();
            Object recent = overview.getOrDefault("recentWaited", Collections.emptyList());
            return ResultModel.success(recent instanceof List ? (List<?>) recent : Collections.emptyList());
        } catch (BPMException e) { return ResultModel.fail(e.getMessage()); }
    }

    public static class ResultModel<T> {
        private int code;
        private String message;
        private T data;

        public static <T> ResultModel<T> success(T data) {
            ResultModel<T> result = new ResultModel<>();
            result.code = 200;
            result.data = data;
            return result;
        }

        public static <T> ResultModel<T> fail(String message) {
            ResultModel<T> result = new ResultModel<>();
            result.code = 500;
            result.message = message;
            return result;
        }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
}

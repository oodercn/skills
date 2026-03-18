package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.llm.LlmCallLogDTO;
import net.ooder.mvp.skill.scene.model.ResultModel;
import net.ooder.mvp.skill.scene.integration.SceneEngineIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

@RestController
@RequestMapping("/api/v1/llm/monitor")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LlmMonitorController {

    private static final Logger log = LoggerFactory.getLogger(LlmMonitorController.class);
    
    @Autowired(required = false)
    private SceneEngineIntegration engineIntegration;
    
    private List<LlmCallLogDTO> callLogs = new ArrayList<LlmCallLogDTO>();
    
    public LlmMonitorController() {
        initMockData();
    }
    
    private void initMockData() {
        String[] providers = {"deepseek", "baidu", "openai"};
        String[][] models = {
            {"deepseek-chat", "deepseek-coder"},
            {"ernie-bot-4", "ernie-bot-turbo"},
            {"gpt-4-turbo", "gpt-3.5-turbo"}
        };
        String[] providerNames = {"DeepSeek", "百度文心", "OpenAI"};
        
        Random random = new Random();
        
        for (int i = 0; i < 100; i++) {
            int providerIndex = random.nextInt(providers.length);
            String provider = providers[providerIndex];
            String model = models[providerIndex][random.nextInt(models[providerIndex].length)];
            int inputTokens = random.nextInt(2000) + 100;
            int outputTokens = random.nextInt(1000) + 50;
            String status = random.nextDouble() > 0.05 ? "success" : "error";
            
            LlmCallLogDTO logEntry = new LlmCallLogDTO();
            logEntry.setLogId("log-" + (i + 1));
            logEntry.setProviderId(provider);
            logEntry.setProviderName(providerNames[providerIndex]);
            logEntry.setModel(model);
            logEntry.setInputTokens(inputTokens);
            logEntry.setOutputTokens(outputTokens);
            logEntry.setTotalTokens(inputTokens + outputTokens);
            logEntry.setLatency(random.nextInt(2000) + 100);
            logEntry.setCost((inputTokens + outputTokens) * 0.00001);
            logEntry.setStatus(status);
            logEntry.setCreateTime(System.currentTimeMillis() - random.nextInt(86400000));
            logEntry.setRequestType("chat");
            
            callLogs.add(logEntry);
        }
    }

    @GetMapping("/stats")
    public ResultModel<Map<String, Object>> getStats(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) String providerId) {
        log.info("[getStats] timeRange: {}, providerId: {}", timeRange, providerId);
        
        Map<String, Object> stats = new HashMap<String, Object>();
        
        // 尝试从引擎获取监控数据
        if (engineIntegration != null && engineIntegration.isSdkAvailable()) {
            try {
                Map<String, Object> engineStats = getEngineMonitorStats();
                if (engineStats != null) {
                    stats.putAll(engineStats);
                    stats.put("source", "engine");
                }
            } catch (Exception e) {
                log.warn("Failed to get engine monitor stats: {}", e.getMessage());
            }
        }
        
        // 如果引擎数据不可用，使用本地数据
        if (stats.isEmpty()) {
            stats = calculateLocalStats(providerId);
            stats.put("source", "local");
        }
        
        return ResultModel.success(stats);
    }
    
    private Map<String, Object> getEngineMonitorStats() {
        Map<String, Object> stats = new HashMap<String, Object>();
        
        try {
            // 尝试获取 SceneMonitor
            Object sceneMonitor = engineIntegration.getSdkService("sceneMonitor");
            if (sceneMonitor != null) {
                // 获取性能监控
                Method getPerformanceMonitor = sceneMonitor.getClass().getMethod("getPerformanceMonitor");
                Object performanceMonitor = getPerformanceMonitor.invoke(sceneMonitor);
                
                if (performanceMonitor != null) {
                    Method getCurrentMetrics = performanceMonitor.getClass().getMethod("getCurrentMetrics", String.class);
                    Object metrics = getCurrentMetrics.invoke(performanceMonitor, "default");
                    
                    if (metrics != null) {
                        // 提取指标
                        stats.put("engineMetrics", convertToMap(metrics));
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Engine monitor not available: {}", e.getMessage());
        }
        
        return stats;
    }
    
    private Map<String, Object> convertToMap(Object obj) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Method[] methods = obj.getClass().getMethods();
            for (Method method : methods) {
                String name = method.getName();
                if (name.startsWith("get") && method.getParameterCount() == 0 && !name.equals("getClass")) {
                    String key = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                    Object value = method.invoke(obj);
                    if (value != null && !value.getClass().getName().startsWith("java.lang.Class")) {
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to convert object to map: {}", e.getMessage());
        }
        return map;
    }
    
    private Map<String, Object> calculateLocalStats(String providerId) {
        Map<String, Object> stats = new HashMap<String, Object>();
        
        List<LlmCallLogDTO> filtered = callLogs;
        if (providerId != null && !providerId.isEmpty()) {
            filtered = new ArrayList<LlmCallLogDTO>();
            for (LlmCallLogDTO l : callLogs) {
                if (providerId.equals(l.getProviderId())) {
                    filtered.add(l);
                }
            }
        }
        
        long totalCalls = filtered.size();
        long totalTokens = 0;
        double totalCost = 0;
        long totalLatency = 0;
        long successCount = 0;
        
        for (LlmCallLogDTO l : filtered) {
            totalTokens += l.getTotalTokens();
            totalCost += l.getCost();
            totalLatency += l.getLatency();
            if ("success".equals(l.getStatus())) {
                successCount++;
            }
        }
        
        stats.put("totalCalls", totalCalls);
        stats.put("totalTokens", totalTokens);
        stats.put("totalCost", Math.round(totalCost * 100.0) / 100.0);
        stats.put("avgLatency", totalCalls > 0 ? totalLatency / totalCalls : 0);
        stats.put("successRate", totalCalls > 0 ? Math.round(successCount * 1000.0 / totalCalls) / 10.0 : 0);
        stats.put("errorCount", totalCalls - successCount);
        
        return stats;
    }
    
    @GetMapping("/logs")
    public ResultModel<PageResult<LlmCallLogDTO>> getLogs(
            @RequestParam(required = false) String providerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String model,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[getLogs] providerId: {}, status: {}, pageNum: {}, pageSize: {}", providerId, status, pageNum, pageSize);
        
        List<LlmCallLogDTO> filtered = new ArrayList<LlmCallLogDTO>();
        
        for (LlmCallLogDTO l : callLogs) {
            if (providerId != null && !providerId.isEmpty() && !providerId.equals(l.getProviderId())) {
                continue;
            }
            if (status != null && !status.isEmpty() && !status.equals(l.getStatus())) {
                continue;
            }
            if (model != null && !model.isEmpty() && !l.getModel().toLowerCase().contains(model.toLowerCase())) {
                continue;
            }
            filtered.add(l);
        }
        
        Collections.sort(filtered, new Comparator<LlmCallLogDTO>() {
            @Override
            public int compare(LlmCallLogDTO a, LlmCallLogDTO b) {
                return Long.compare(b.getCreateTime(), a.getCreateTime());
            }
        });
        
        int total = filtered.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<LlmCallLogDTO> paged = new ArrayList<LlmCallLogDTO>();
        if (start < total) {
            paged = filtered.subList(start, end);
        }
        
        PageResult<LlmCallLogDTO> result = new PageResult<LlmCallLogDTO>();
        result.setList(paged);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return ResultModel.success(result);
    }
    
    @GetMapping("/logs/{logId}")
    public ResultModel<LlmCallLogDTO> getLogDetail(@PathVariable String logId) {
        log.info("[getLogDetail] logId: {}", logId);
        
        for (LlmCallLogDTO l : callLogs) {
            if (logId.equals(l.getLogId())) {
                return ResultModel.success(l);
            }
        }
        
        return ResultModel.notFound("Log not found: " + logId);
    }
    
    @GetMapping("/provider-stats")
    public ResultModel<List<Map<String, Object>>> getProviderStats() {
        log.info("[getProviderStats] request start");
        
        Map<String, Map<String, Object>> providerStats = new HashMap<String, Map<String, Object>>();
        
        for (LlmCallLogDTO l : callLogs) {
            String providerId = l.getProviderId();
            Map<String, Object> stats = providerStats.get(providerId);
            if (stats == null) {
                stats = new HashMap<String, Object>();
                stats.put("providerId", providerId);
                stats.put("providerName", l.getProviderName());
                stats.put("totalCalls", 0L);
                stats.put("totalTokens", 0L);
                stats.put("totalCost", 0.0);
                providerStats.put(providerId, stats);
            }
            
            stats.put("totalCalls", (Long) stats.get("totalCalls") + 1);
            stats.put("totalTokens", (Long) stats.get("totalTokens") + l.getTotalTokens());
            stats.put("totalCost", (Double) stats.get("totalCost") + l.getCost());
        }
        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(providerStats.values());
        
        return ResultModel.success(result);
    }
    
    @GetMapping("/engine/status")
    public ResultModel<Map<String, Object>> getEngineStatus() {
        log.info("[getEngineStatus] request start");
        
        Map<String, Object> status = new HashMap<String, Object>();
        
        boolean sdkAvailable = engineIntegration != null && engineIntegration.isSdkAvailable();
        status.put("sdkAvailable", sdkAvailable);
        status.put("monitorEnabled", sdkAvailable);
        status.put("dataSource", sdkAvailable ? "engine" : "local");
        
        return ResultModel.success(status);
    }
}

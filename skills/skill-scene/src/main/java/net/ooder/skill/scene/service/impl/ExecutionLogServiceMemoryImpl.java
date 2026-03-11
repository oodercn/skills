package net.ooder.skill.scene.service.impl;

import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.scene.ExecutionLogDTO;
import net.ooder.skill.scene.service.ExecutionLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ExecutionLogServiceMemoryImpl implements ExecutionLogService {

    private static final Logger log = LoggerFactory.getLogger(ExecutionLogServiceMemoryImpl.class);
    
    private final Map<String, LinkedList<ExecutionLogDTO>> logsBySceneGroup = new ConcurrentHashMap<>();
    private static final int MAX_LOGS_PER_GROUP = 1000;

    @Override
    public void log(String sceneGroupId, String action, String status, String message) {
        ExecutionLogDTO logEntry = new ExecutionLogDTO();
        logEntry.setLogId("log-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8));
        logEntry.setAction(action);
        logEntry.setStatus(status);
        logEntry.setMessage(message);
        logEntry.setTimestamp(System.currentTimeMillis());
        
        LinkedList<ExecutionLogDTO> logs = logsBySceneGroup.computeIfAbsent(sceneGroupId, k -> new LinkedList<>());
        
        synchronized (logs) {
            logs.addFirst(logEntry);
            while (logs.size() > MAX_LOGS_PER_GROUP) {
                logs.removeLast();
            }
        }
        
        log.debug("Logged [{}] {} - {}: {}", sceneGroupId, action, status, message);
    }

    @Override
    public List<ExecutionLogDTO> listLogs(String sceneGroupId, int limit) {
        LinkedList<ExecutionLogDTO> logs = logsBySceneGroup.get(sceneGroupId);
        if (logs == null || logs.isEmpty()) {
            return new ArrayList<>();
        }
        
        synchronized (logs) {
            return logs.stream().limit(limit).collect(Collectors.toList());
        }
    }

    @Override
    public PageResult<ExecutionLogDTO> listLogs(String sceneGroupId, long startTime, long endTime, String level, int pageNum, int pageSize) {
        LinkedList<ExecutionLogDTO> logs = logsBySceneGroup.get(sceneGroupId);
        
        List<ExecutionLogDTO> filtered = new ArrayList<>();
        if (logs != null) {
            synchronized (logs) {
                for (ExecutionLogDTO entry : logs) {
                    if (startTime > 0 && entry.getTimestamp() < startTime) continue;
                    if (endTime > 0 && entry.getTimestamp() > endTime) continue;
                    if (level != null && !level.isEmpty() && !level.equalsIgnoreCase(entry.getStatus())) continue;
                    filtered.add(entry);
                }
            }
        }
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        
        List<ExecutionLogDTO> pagedList = start < filtered.size() 
            ? filtered.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<ExecutionLogDTO> result = new PageResult<>();
        result.setList(pagedList);
        result.setTotal(filtered.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
}

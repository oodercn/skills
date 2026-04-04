package net.ooder.skill.bpm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.ooder.skill.bpm.model.ProcessDef;

public class ProcessDefService {

    private final Map<String, ProcessDef> processDefCache = new ConcurrentHashMap<>();

    public ProcessDef createProcessDef(ProcessDef processDef) {
        if (processDef.getId() == null || processDef.getId().isEmpty()) {
            processDef.setId(generateId());
        }
        processDef.setCreateTime(LocalDateTime.now());
        processDef.setUpdateTime(LocalDateTime.now());
        processDefCache.put(processDef.getId(), processDef);
        return processDef;
    }

    public ProcessDef updateProcessDef(ProcessDef processDef) {
        ProcessDef existing = processDefCache.get(processDef.getId());
        if (existing == null) {
            return null;
        }
        processDef.setUpdateTime(LocalDateTime.now());
        processDefCache.put(processDef.getId(), processDef);
        return processDef;
    }

    public ProcessDef getProcessDef(String processDefId) {
        return processDefCache.get(processDefId);
    }

    public List<ProcessDef> listProcessDefs() {
        return new ArrayList<>(processDefCache.values());
    }

    public List<ProcessDef> listProcessDefsByCategory(String category) {
        List<ProcessDef> result = new ArrayList<>();
        for (ProcessDef def : processDefCache.values()) {
            if (category == null || category.equals(def.getCategory())) {
                result.add(def);
            }
        }
        return result;
    }

    public List<ProcessDef> listPublishedProcessDefs() {
        List<ProcessDef> result = new ArrayList<>();
        for (ProcessDef def : processDefCache.values()) {
            if ("PUBLISHED".equals(def.getStatus())) {
                result.add(def);
            }
        }
        return result;
    }

    public ProcessDef publishProcessDef(String processDefId) {
        ProcessDef def = processDefCache.get(processDefId);
        if (def != null) {
            def.setStatus("PUBLISHED");
            def.setPublishTime(LocalDateTime.now());
            def.setUpdateTime(LocalDateTime.now());
        }
        return def;
    }

    public ProcessDef unpublishProcessDef(String processDefId) {
        ProcessDef def = processDefCache.get(processDefId);
        if (def != null) {
            def.setStatus("DRAFT");
            def.setUpdateTime(LocalDateTime.now());
        }
        return def;
    }

    public boolean deleteProcessDef(String processDefId) {
        ProcessDef def = processDefCache.get(processDefId);
        if (def != null && "DRAFT".equals(def.getStatus())) {
            processDefCache.remove(processDefId);
            return true;
        }
        return false;
    }

    private String generateId() {
        return "PD_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
}

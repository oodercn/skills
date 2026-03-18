package net.ooder.mvp.skill.scene.capability.service;

import net.ooder.mvp.skill.scene.capability.model.Capability;
import net.ooder.mvp.skill.scene.capability.model.SkillForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocalDiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(LocalDiscoveryService.class);

    @Autowired
    private CapabilityService capabilityService;

    @Autowired
    private SkillCapabilitySyncService syncService;

    public DiscoveryResult discover() {
        log.info("[discover] Starting local discovery");
        
        List<Capability> allCaps = capabilityService.findAll();
        log.info("[discover] Found {} capabilities from storage", allCaps.size());
        
        List<Map<String, Object>> capabilities = allCaps.stream()
            .filter(cap -> !isInternal(cap))
            .map(this::convertToMap)
            .collect(Collectors.toList());
        
        Map<String, Integer> stats = calculateStats(allCaps);
        
        DiscoveryResult result = new DiscoveryResult();
        result.setCapabilities(capabilities);
        result.setTotal(capabilities.size());
        result.setStats(stats);
        result.setSource("local");
        result.setTimestamp(System.currentTimeMillis());
        
        log.info("[discover] Discovery completed: {} capabilities (filtered {} internal)", 
                capabilities.size(), allCaps.size() - capabilities.size());
        
        return result;
    }

    public SyncResult sync() {
        log.info("[sync] Starting manual sync from skill.yaml files");
        
        Map<String, Object> syncResult = syncService.syncAllSkills();
        
        SyncResult result = new SyncResult();
        result.setSynced((Integer) syncResult.getOrDefault("synced", 0));
        result.setSkipped((Integer) syncResult.getOrDefault("skipped", 0));
        result.setErrors((Integer) syncResult.getOrDefault("errors", 0));
        result.setTimestamp(System.currentTimeMillis());
        
        log.info("[sync] Sync completed: synced={}, skipped={}, errors={}", 
                result.getSynced(), result.getSkipped(), result.getErrors());
        
        return result;
    }

    public Map<String, Object> getCapabilityDetail(String capabilityId) {
        Capability cap = capabilityService.findById(capabilityId);
        if (cap == null) {
            return null;
        }
        return convertToMap(cap);
    }

    private boolean isInternal(Capability cap) {
        String visibility = cap.getVisibility();
        return "internal".equals(visibility);
    }

    private Map<String, Object> convertToMap(Capability cap) {
        Map<String, Object> map = new LinkedHashMap<>();
        
        map.put("id", cap.getCapabilityId());
        map.put("name", cap.getName());
        map.put("description", cap.getDescription());
        map.put("version", cap.getVersion());
        map.put("icon", cap.getIcon());
        map.put("skillId", cap.getSkillId());
        map.put("installed", cap.isInstalled());
        map.put("visibility", cap.getVisibility());
        
        SkillForm skillForm = cap.getSkillForm();
        map.put("skillForm", skillForm != null ? skillForm.getCode() : "PROVIDER");
        
        map.put("sceneType", cap.getSceneType());
        map.put("sceneCapability", cap.isSceneCapability());
        map.put("hasSelfDrive", cap.isHasSelfDrive());
        map.put("businessCategory", cap.getBusinessCategory());
        map.put("businessSemanticsScore", cap.getBusinessSemanticsScore());
        map.put("mainFirst", cap.isMainFirst());
        map.put("category", cap.getCategory());
        
        if (cap.getCapabilityCategory() != null) {
            map.put("capabilityCategory", cap.getCapabilityCategory().getCode());
        }
        
        map.put("dependencies", cap.getDependencies());
        map.put("tags", cap.getTags());
        map.put("participants", cap.getParticipants());
        map.put("type", cap.getType() != null ? cap.getType().name() : null);
        
        return map;
    }

    private Map<String, Integer> calculateStats(List<Capability> allCaps) {
        Map<String, Integer> stats = new HashMap<>();
        
        int scene = 0, provider = 0, driver = 0;
        int installed = 0, notInstalled = 0;
        
        for (Capability cap : allCaps) {
            SkillForm form = cap.getSkillForm();
            if (form == SkillForm.SCENE) {
                scene++;
            } else if (form == SkillForm.PROVIDER) {
                provider++;
            } else if (form == SkillForm.DRIVER) {
                driver++;
            }
            
            if (cap.isInstalled()) {
                installed++;
            } else {
                notInstalled++;
            }
        }
        
        stats.put("scene", scene);
        stats.put("provider", provider);
        stats.put("driver", driver);
        stats.put("installed", installed);
        stats.put("notInstalled", notInstalled);
        stats.put("total", allCaps.size());
        
        return stats;
    }

    public static class DiscoveryResult {
        private List<Map<String, Object>> capabilities;
        private int total;
        private Map<String, Integer> stats;
        private String source;
        private long timestamp;

        public List<Map<String, Object>> getCapabilities() { return capabilities; }
        public void setCapabilities(List<Map<String, Object>> capabilities) { this.capabilities = capabilities; }
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public Map<String, Integer> getStats() { return stats; }
        public void setStats(Map<String, Integer> stats) { this.stats = stats; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class SyncResult {
        private int synced;
        private int skipped;
        private int errors;
        private long timestamp;

        public int getSynced() { return synced; }
        public void setSynced(int synced) { this.synced = synced; }
        public int getSkipped() { return skipped; }
        public void setSkipped(int skipped) { this.skipped = skipped; }
        public int getErrors() { return errors; }
        public void setErrors(int errors) { this.errors = errors; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}

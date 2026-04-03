package net.ooder.skill.scenes.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.skill.scenes.dto.SceneGroupDTO;
import net.ooder.skill.scenes.entity.SceneGroup;
import net.ooder.skill.scenes.model.PageResult;
import net.ooder.skill.scenes.repository.SceneGroupRepository;
import net.ooder.skill.scenes.service.SceneGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SceneGroupServiceImpl implements SceneGroupService {

    private static final Logger log = LoggerFactory.getLogger(SceneGroupServiceImpl.class);

    @Autowired
    private SceneGroupRepository sceneGroupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // In-memory stores for related data (will be migrated to separate entities in future)
    private final Map<String, List<Map<String, Object>>> groupCapabilities = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> groupParticipants = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> groupSnapshots = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> groupKnowledgeBases = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> groupEventLogs = new HashMap<>();

    @Override
    public PageResult<SceneGroupDTO> getMyCreatedGroups(int pageNum, int pageSize) {
        log.info("[SceneGroupService] Get my created groups - pageNum: {}, pageSize: {}", pageNum, pageSize);
        // TODO: Get current user ID from security context
        String currentUserId = "current-user";
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<SceneGroup> page = sceneGroupRepository.findByOwnerId(currentUserId, pageable);
        return convertToPageResult(page);
    }

    @Override
    public PageResult<SceneGroupDTO> getMyLedGroups(int pageNum, int pageSize) {
        log.info("[SceneGroupService] Get my led groups - pageNum: {}, pageSize: {}", pageNum, pageSize);
        // For now, same as created groups
        return getMyCreatedGroups(pageNum, pageSize);
    }

    @Override
    public PageResult<SceneGroupDTO> getMyParticipatedGroups(int pageNum, int pageSize) {
        log.info("[SceneGroupService] Get my participated groups - pageNum: {}, pageSize: {}", pageNum, pageSize);
        // TODO: Implement based on participant records
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<SceneGroup> page = sceneGroupRepository.findAll(pageable);
        return convertToPageResult(page);
    }

    @Override
    public SceneGroupDTO create(SceneGroupDTO dto) {
        log.info("[SceneGroupService] Create group: {}", dto.getName());
        SceneGroup entity = new SceneGroup();
        entity.setId(UUID.randomUUID().toString());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus("active");
        entity.setOwnerId("current-user"); // TODO: Get from security context
        entity.setOwnerName("Current User");
        
        try {
            if (dto.getLlmConfig() != null) {
                entity.setLlmConfig(objectMapper.writeValueAsString(dto.getLlmConfig()));
            }
            if (dto.getKnowledgeConfig() != null) {
                entity.setKnowledgeConfig(objectMapper.writeValueAsString(dto.getKnowledgeConfig()));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize config", e);
        }
        
        SceneGroup saved = sceneGroupRepository.save(entity);
        
        // Initialize related data stores
        groupCapabilities.put(saved.getId(), new ArrayList<>());
        groupParticipants.put(saved.getId(), new ArrayList<>());
        groupSnapshots.put(saved.getId(), new ArrayList<>());
        groupKnowledgeBases.put(saved.getId(), new ArrayList<>());
        groupEventLogs.put(saved.getId(), new ArrayList<>());
        
        return convertToDTO(saved);
    }

    @Override
    public SceneGroupDTO get(String id) {
        log.info("[SceneGroupService] Get group: {}", id);
        Optional<SceneGroup> optional = sceneGroupRepository.findById(id);
        return optional.map(this::convertToDTO).orElse(null);
    }

    @Override
    public SceneGroupDTO update(SceneGroupDTO dto) {
        log.info("[SceneGroupService] Update group: {}", dto.getId());
        Optional<SceneGroup> optional = sceneGroupRepository.findById(dto.getId());
        if (optional.isEmpty()) {
            return null;
        }
        
        SceneGroup entity = optional.get();
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        
        try {
            if (dto.getLlmConfig() != null) {
                entity.setLlmConfig(objectMapper.writeValueAsString(dto.getLlmConfig()));
            }
            if (dto.getKnowledgeConfig() != null) {
                entity.setKnowledgeConfig(objectMapper.writeValueAsString(dto.getKnowledgeConfig()));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize config", e);
        }
        
        SceneGroup saved = sceneGroupRepository.save(entity);
        return convertToDTO(saved);
    }

    @Override
    public boolean delete(String id) {
        log.info("[SceneGroupService] Delete group: {}", id);
        if (!sceneGroupRepository.existsById(id)) {
            return false;
        }
        sceneGroupRepository.deleteById(id);
        
        // Clean up related data
        groupCapabilities.remove(id);
        groupParticipants.remove(id);
        groupSnapshots.remove(id);
        groupKnowledgeBases.remove(id);
        groupEventLogs.remove(id);
        
        return true;
    }

    @Override
    public SceneGroupDTO activate(String id) {
        log.info("[SceneGroupService] Activate group: {}", id);
        Optional<SceneGroup> optional = sceneGroupRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        SceneGroup entity = optional.get();
        entity.setStatus("active");
        SceneGroup saved = sceneGroupRepository.save(entity);
        return convertToDTO(saved);
    }

    @Override
    public SceneGroupDTO deactivate(String id) {
        log.info("[SceneGroupService] Deactivate group: {}", id);
        Optional<SceneGroup> optional = sceneGroupRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        SceneGroup entity = optional.get();
        entity.setStatus("inactive");
        SceneGroup saved = sceneGroupRepository.save(entity);
        return convertToDTO(saved);
    }

    @Override
    public List<Map<String, Object>> getCapabilities(String groupId) {
        log.info("[SceneGroupService] Get capabilities for group: {}", groupId);
        return groupCapabilities.getOrDefault(groupId, new ArrayList<>());
    }

    @Override
    public Map<String, Object> addCapability(String groupId, String capId) {
        log.info("[SceneGroupService] Add capability {} to group {}", capId, groupId);
        Map<String, Object> cap = new HashMap<>();
        cap.put("id", capId);
        cap.put("name", "Capability " + capId);
        cap.put("status", "active");
        cap.put("addTime", LocalDateTime.now().toString());
        groupCapabilities.computeIfAbsent(groupId, k -> new ArrayList<>()).add(cap);
        return cap;
    }

    @Override
    public boolean removeCapability(String groupId, String capId) {
        log.info("[SceneGroupService] Remove capability {} from group {}", capId, groupId);
        List<Map<String, Object>> caps = groupCapabilities.get(groupId);
        if (caps != null) {
            return caps.removeIf(c -> capId.equals(c.get("id")));
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getParticipants(String groupId) {
        log.info("[SceneGroupService] Get participants for group: {}", groupId);
        return groupParticipants.getOrDefault(groupId, new ArrayList<>());
    }

    @Override
    public Map<String, Object> addParticipant(String groupId, String userId, String role) {
        log.info("[SceneGroupService] Add participant {} to group {} with role {}", userId, groupId, role);
        Map<String, Object> participant = new HashMap<>();
        participant.put("id", userId);
        participant.put("userId", userId);
        participant.put("role", role != null ? role : "member");
        participant.put("joinTime", LocalDateTime.now().toString());
        groupParticipants.computeIfAbsent(groupId, k -> new ArrayList<>()).add(participant);
        return participant;
    }

    @Override
    public boolean updateParticipantRole(String groupId, String participantId, String role) {
        log.info("[SceneGroupService] Update participant {} role to {} in group {}", participantId, role, groupId);
        List<Map<String, Object>> participants = groupParticipants.get(groupId);
        if (participants != null) {
            for (Map<String, Object> p : participants) {
                if (participantId.equals(p.get("id"))) {
                    p.put("role", role);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean removeParticipant(String groupId, String participantId) {
        log.info("[SceneGroupService] Remove participant {} from group {}", participantId, groupId);
        List<Map<String, Object>> participants = groupParticipants.get(groupId);
        if (participants != null) {
            return participants.removeIf(p -> participantId.equals(p.get("id")));
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getSnapshots(String groupId) {
        log.info("[SceneGroupService] Get snapshots for group: {}", groupId);
        return groupSnapshots.getOrDefault(groupId, new ArrayList<>());
    }

    @Override
    public Map<String, Object> createSnapshot(String groupId, String name) {
        log.info("[SceneGroupService] Create snapshot for group {} with name {}", groupId, name);
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("id", UUID.randomUUID().toString());
        snapshot.put("name", name != null ? name : "Snapshot " + System.currentTimeMillis());
        snapshot.put("groupId", groupId);
        snapshot.put("createTime", LocalDateTime.now().toString());
        groupSnapshots.computeIfAbsent(groupId, k -> new ArrayList<>()).add(snapshot);
        return snapshot;
    }

    @Override
    public boolean restoreSnapshot(String groupId, String snapshotId) {
        log.info("[SceneGroupService] Restore snapshot {} for group {}", snapshotId, groupId);
        return true;
    }

    @Override
    public boolean deleteSnapshot(String groupId, String snapshotId) {
        log.info("[SceneGroupService] Delete snapshot {} for group {}", snapshotId, groupId);
        List<Map<String, Object>> snapshots = groupSnapshots.get(groupId);
        if (snapshots != null) {
            return snapshots.removeIf(s -> snapshotId.equals(s.get("id")));
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getKnowledgeBases(String groupId) {
        log.info("[SceneGroupService] Get knowledge bases for group: {}", groupId);
        return groupKnowledgeBases.getOrDefault(groupId, new ArrayList<>());
    }

    @Override
    public Map<String, Object> addKnowledgeBase(String groupId, String kbId) {
        log.info("[SceneGroupService] Add knowledge base {} to group {}", kbId, groupId);
        Map<String, Object> kb = new HashMap<>();
        kb.put("id", kbId);
        kb.put("name", "Knowledge Base " + kbId);
        kb.put("addTime", LocalDateTime.now().toString());
        groupKnowledgeBases.computeIfAbsent(groupId, k -> new ArrayList<>()).add(kb);
        return kb;
    }

    @Override
    public boolean removeKnowledgeBase(String groupId, String kbId) {
        log.info("[SceneGroupService] Remove knowledge base {} from group {}", kbId, groupId);
        List<Map<String, Object>> kbs = groupKnowledgeBases.get(groupId);
        if (kbs != null) {
            return kbs.removeIf(k -> kbId.equals(k.get("id")));
        }
        return false;
    }

    @Override
    public Map<String, Object> getLlmConfig(String groupId) {
        log.info("[SceneGroupService] Get LLM config for group: {}", groupId);
        Optional<SceneGroup> optional = sceneGroupRepository.findById(groupId);
        if (optional.isPresent() && optional.get().getLlmConfig() != null) {
            try {
                return objectMapper.readValue(optional.get().getLlmConfig(), Map.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize LLM config", e);
            }
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> updateLlmConfig(String groupId, Map<String, Object> config) {
        log.info("[SceneGroupService] Update LLM config for group: {}", groupId);
        Optional<SceneGroup> optional = sceneGroupRepository.findById(groupId);
        if (optional.isPresent()) {
            SceneGroup entity = optional.get();
            try {
                entity.setLlmConfig(objectMapper.writeValueAsString(config));
                sceneGroupRepository.save(entity);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize LLM config", e);
            }
        }
        return config;
    }

    @Override
    public PageResult<Map<String, Object>> getEventLog(String groupId, int pageNum, int pageSize) {
        log.info("[SceneGroupService] Get event log for group: {}", groupId);
        List<Map<String, Object>> logs = groupEventLogs.getOrDefault(groupId, new ArrayList<>());
        return paginate(logs, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> startWorkflow(String groupId, String workflowId, Map<String, Object> params) {
        log.info("[SceneGroupService] Start workflow {} for group {}", workflowId, groupId);
        Map<String, Object> result = new HashMap<>();
        result.put("workflowId", workflowId);
        result.put("groupId", groupId);
        result.put("status", "started");
        result.put("startTime", LocalDateTime.now().toString());
        result.put("instanceId", UUID.randomUUID().toString());
        return result;
    }

    @Override
    public SceneGroupDTO createFromFusion(String fusionId, Map<String, Object> params) {
        log.info("[SceneGroupService] Create group from fusion: {}", fusionId);
        SceneGroupDTO dto = new SceneGroupDTO();
        dto.setName("Group from Fusion " + fusionId);
        dto.setDescription("Created from fusion template: " + fusionId);
        return create(dto);
    }

    @Override
    public Map<String, Object> getCapability(String groupId, String capId) {
        log.info("[SceneGroupService] Get capability {} for group {}", capId, groupId);
        List<Map<String, Object>> caps = groupCapabilities.get(groupId);
        if (caps != null) {
            for (Map<String, Object> cap : caps) {
                if (capId.equals(cap.get("id"))) {
                    return cap;
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getKnowledgeConfig(String groupId) {
        log.info("[SceneGroupService] Get knowledge config for group: {}", groupId);
        Optional<SceneGroup> optional = sceneGroupRepository.findById(groupId);
        if (optional.isPresent() && optional.get().getKnowledgeConfig() != null) {
            try {
                return objectMapper.readValue(optional.get().getKnowledgeConfig(), Map.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize knowledge config", e);
            }
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> updateKnowledgeConfig(String groupId, Map<String, Object> config) {
        log.info("[SceneGroupService] Update knowledge config for group: {}", groupId);
        Optional<SceneGroup> optional = sceneGroupRepository.findById(groupId);
        if (optional.isPresent()) {
            SceneGroup entity = optional.get();
            try {
                entity.setKnowledgeConfig(objectMapper.writeValueAsString(config));
                sceneGroupRepository.save(entity);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize knowledge config", e);
            }
        }
        return config;
    }

    @Override
    public List<Map<String, Object>> getLlmProviderModels(String groupId, String providerId) {
        log.info("[SceneGroupService] Get LLM provider {} models for group {}", providerId, groupId);
        List<Map<String, Object>> models = new ArrayList<>();
        Map<String, Object> model1 = new HashMap<>();
        model1.put("id", "gpt-4");
        model1.put("name", "GPT-4");
        model1.put("providerId", providerId);
        models.add(model1);
        Map<String, Object> model2 = new HashMap<>();
        model2.put("id", "gpt-3.5-turbo");
        model2.put("name", "GPT-3.5 Turbo");
        model2.put("providerId", providerId);
        models.add(model2);
        return models;
    }

    @Override
    public boolean resetLlmConfig(String groupId) {
        log.info("[SceneGroupService] Reset LLM config for group: {}", groupId);
        Optional<SceneGroup> optional = sceneGroupRepository.findById(groupId);
        if (optional.isPresent()) {
            SceneGroup entity = optional.get();
            entity.setLlmConfig(null);
            sceneGroupRepository.save(entity);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> executeAction(String groupId, String action, Map<String, Object> params) {
        log.info("[SceneGroupService] Execute action {} for group {}", action, groupId);
        Map<String, Object> result = new HashMap<>();
        result.put("groupId", groupId);
        result.put("action", action);
        result.put("status", "executed");
        result.put("executeTime", LocalDateTime.now().toString());
        result.put("params", params);
        return result;
    }

    @Override
    public List<Map<String, Object>> getMyLedMembers() {
        log.info("[SceneGroupService] Get my led members");
        List<Map<String, Object>> members = new ArrayList<>();
        Map<String, Object> member = new HashMap<>();
        member.put("id", "user-001");
        member.put("name", "Admin");
        member.put("role", "leader");
        members.add(member);
        return members;
    }

    private SceneGroupDTO convertToDTO(SceneGroup entity) {
        SceneGroupDTO dto = new SceneGroupDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setOwnerId(entity.getOwnerId());
        dto.setOwnerName(entity.getOwnerName());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        
        try {
            if (entity.getLlmConfig() != null) {
                dto.setLlmConfig(objectMapper.readValue(entity.getLlmConfig(), Map.class));
            }
            if (entity.getKnowledgeConfig() != null) {
                dto.setKnowledgeConfig(objectMapper.readValue(entity.getKnowledgeConfig(), Map.class));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize config", e);
        }
        
        return dto;
    }

    private PageResult<SceneGroupDTO> convertToPageResult(Page<SceneGroup> page) {
        PageResult<SceneGroupDTO> result = new PageResult<>();
        result.setList(page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        result.setTotal(page.getTotalElements());
        result.setPageNum(page.getNumber() + 1);
        result.setPageSize(page.getSize());
        result.setTotalPages(page.getTotalPages());
        return result;
    }

    private <T> PageResult<T> paginate(List<T> list, int pageNum, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(list.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        
        if (fromIndex < list.size()) {
            result.setList(list.subList(fromIndex, toIndex));
        } else {
            result.setList(new ArrayList<>());
        }
        
        result.setTotalPages((list.size() + pageSize - 1) / pageSize);
        return result;
    }
}

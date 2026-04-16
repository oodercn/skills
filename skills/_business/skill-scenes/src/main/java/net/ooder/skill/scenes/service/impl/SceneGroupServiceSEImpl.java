package net.ooder.skill.scenes.service.impl;

import net.ooder.skill.scenes.dto.*;
import net.ooder.skill.scenes.event.SceneGroupEventLogService;
import net.ooder.skill.scenes.model.PageResult;
import net.ooder.skill.scenes.service.SceneGroupService;
import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.skill.knowledge.KnowledgeBindingManager;
import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.llm.config.SceneLlmConfigManager;
import net.ooder.scene.llm.config.SceneLlmConfigInfo;
import net.ooder.scene.participant.Participant;
import net.ooder.scene.capability.CapabilityBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SceneGroupServiceSEImpl implements SceneGroupService {

    private static final Logger log = LoggerFactory.getLogger(SceneGroupServiceSEImpl.class);

    private final SceneGroupManager sceneGroupManager;
    private final KnowledgeBindingManager knowledgeBindingManager;
    private final SceneLlmConfigManager llmConfigManager;
    private final SceneGroupEventLogService eventLogService;

    public SceneGroupServiceSEImpl(
            SceneGroupManager sceneGroupManager,
            KnowledgeBindingManager knowledgeBindingManager,
            SceneLlmConfigManager llmConfigManager,
            SceneGroupEventLogService eventLogService) {
        
        this.sceneGroupManager = sceneGroupManager;
        this.knowledgeBindingManager = knowledgeBindingManager;
        this.llmConfigManager = llmConfigManager;
        this.eventLogService = eventLogService;
        
        if (sceneGroupManager == null) {
            log.warn("SceneGroupManager bean not provided by SE SDK 3.0.1. " +
                "SceneGroupService will not function properly.");
        } else {
            log.info("SceneGroupServiceSEImpl initialized with SE SDK 3.0.1 - " +
                "KnowledgeBinding: {}, LlmConfig: {}", 
                knowledgeBindingManager != null ? "enabled" : "disabled",
                llmConfigManager != null ? "enabled" : "disabled");
        }
    }

    @Override
    public SceneGroupDTO create(SceneGroupDTO group) {
        SceneGroupConfigDTO config = new SceneGroupConfigDTO();
        config.setName(group.getName());
        config.setDescription(group.getDescription());
        config.setCreatorId(group.getCreatorId());
        config.setCreatorType(group.getCreatorType());
        
        return create(group.getTemplateId(), config);
    }
    
    public SceneGroupDTO create(String templateId, SceneGroupConfigDTO config) {
        String sceneGroupId = "sg-" + System.currentTimeMillis();
        String creatorId = config != null ? config.getCreatorId() : "system";
        SceneGroup.CreatorType creatorType = convertCreatorType(config != null ? config.getCreatorType() : null);

        SceneGroup sceneGroup = sceneGroupManager.createSceneGroup(
            sceneGroupId,
            templateId,
            creatorId,
            creatorType
        );

        if (config != null) {
            if (config.getName() != null) {
                sceneGroup.setName(config.getName());
            }
            if (config.getDescription() != null) {
                sceneGroup.setDescription(config.getDescription());
            }
        }

        log.info("Created scene group {} via SE SDK 3.0.1", sceneGroupId);
        
        addDefaultParticipants(sceneGroup, creatorId, templateId);
        
        if (eventLogService != null) {
            eventLogService.logCreateEvent(sceneGroupId, creatorId, creatorId, 
                config != null ? config.getName() : sceneGroupId);
        }
        
        return convertToDTO(sceneGroup);
    }
    
    private void addDefaultParticipants(SceneGroup sceneGroup, String creatorId, String templateId) {
        try {
            Participant creator = new Participant(
                "p-" + System.currentTimeMillis() + "-creator",
                creatorId,
                creatorId,
                Participant.Type.USER
            );
            creator.setRole(Participant.Role.MANAGER);
            creator.join();
            creator.activate();
            sceneGroupManager.addParticipant(sceneGroup.getSceneGroupId(), creator);
            log.info("Added creator {} as MANAGER to scene group {}", creatorId, sceneGroup.getSceneGroupId());
            
            Participant llmAssistant = new Participant(
                "p-" + System.currentTimeMillis() + "-llm",
                "llm-assistant",
                "LLM智能助手",
                Participant.Type.AGENT
            );
            llmAssistant.setRole(Participant.Role.LLM_ASSISTANT);
            llmAssistant.join();
            llmAssistant.activate();
            sceneGroupManager.addParticipant(sceneGroup.getSceneGroupId(), llmAssistant);
            log.info("Added LLM Assistant to scene group {}", sceneGroup.getSceneGroupId());
            
        } catch (Exception e) {
            log.warn("Failed to add default participants to scene group {}: {}", 
                sceneGroup.getSceneGroupId(), e.getMessage());
        }
    }

    @Override
    public SceneGroupDTO update(SceneGroupDTO group) {
        SceneGroupConfigDTO config = new SceneGroupConfigDTO();
        config.setName(group.getName());
        config.setDescription(group.getDescription());
        return update(group.getSceneGroupId(), config);
    }
    
    public SceneGroupDTO update(String sceneGroupId, SceneGroupConfigDTO config) {
        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (sceneGroup == null) {
            return null;
        }

        if (config != null) {
            if (config.getName() != null) {
                sceneGroup.setName(config.getName());
            }
            if (config.getDescription() != null) {
                sceneGroup.setDescription(config.getDescription());
            }
        }

        return convertToDTO(sceneGroup);
    }

    @Override
    public boolean delete(String sceneGroupId) {
        sceneGroupManager.destroySceneGroup(sceneGroupId);
        
        if (knowledgeBindingManager != null) {
            knowledgeBindingManager.clearAllBindings(sceneGroupId);
        }
        
        log.info("Destroyed scene group {} via SE SDK 3.0.1", sceneGroupId);
        return true;
    }

    @Override
    public SceneGroupDTO get(String sceneGroupId) {
        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(sceneGroupId);
        return convertToDTO(sceneGroup);
    }

    @Override
    public PageResult<SceneGroupDTO> getAllGroups(int pageNum, int pageSize) {
        return listAll(pageNum, pageSize);
    }
    
    public PageResult<SceneGroupDTO> listAll(int pageNum, int pageSize) {
        List<SceneGroup> allGroups = sceneGroupManager.getAllSceneGroups();
        return pagedResult(allGroups, pageNum, pageSize);
    }

    @Override
    public PageResult<SceneGroupDTO> getMyCreatedGroups(int pageNum, int pageSize) {
        return listByCreator("current-user", pageNum, pageSize);
    }
    
    public PageResult<SceneGroupDTO> listByTemplate(String templateId, int pageNum, int pageSize) {
        List<SceneGroup> templateGroups = sceneGroupManager.getSceneGroupsByTemplate(templateId);
        return pagedResult(templateGroups, pageNum, pageSize);
    }

    public PageResult<SceneGroupDTO> listByCreator(String creatorId, int pageNum, int pageSize) {
        List<SceneGroup> allGroups = sceneGroupManager.getAllSceneGroups();
        List<SceneGroup> filtered = new ArrayList<>();
        for (SceneGroup group : allGroups) {
            if (creatorId.equals(group.getCreatorId())) {
                filtered.add(group);
            }
        }
        return pagedResult(filtered, pageNum, pageSize);
    }

    public PageResult<SceneGroupDTO> listByParticipant(String participantId, int pageNum, int pageSize) {
        List<SceneGroup> allGroups = sceneGroupManager.getAllSceneGroups();
        List<SceneGroup> filtered = new ArrayList<>();
        for (SceneGroup group : allGroups) {
            List<Participant> participants = group.getAllParticipants();
            for (Participant p : participants) {
                if (participantId.equals(p.getUserId())) {
                    filtered.add(group);
                    break;
                }
            }
        }
        return pagedResult(filtered, pageNum, pageSize);
    }

    @Override
    public SceneGroupDTO activate(String sceneGroupId) {
        sceneGroupManager.activateSceneGroup(sceneGroupId);
        log.info("Activated scene group {} via SE SDK 3.0.1", sceneGroupId);
        return get(sceneGroupId);
    }

    @Override
    public SceneGroupDTO deactivate(String sceneGroupId) {
        sceneGroupManager.suspendSceneGroup(sceneGroupId);
        log.info("Deactivated scene group {} via SE SDK 3.0.1", sceneGroupId);
        return get(sceneGroupId);
    }

    @Override
    public List<SceneGroupCapabilityDTO> getCapabilities(String sceneGroupId) {
        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (sceneGroup == null) {
            return new ArrayList<>();
        }
        
        List<CapabilityBinding> bindings = sceneGroup.getAllCapabilityBindings();
        List<SceneGroupCapabilityDTO> result = new ArrayList<>();
        for (CapabilityBinding b : bindings) {
            SceneGroupCapabilityDTO dto = new SceneGroupCapabilityDTO();
            dto.setCapId(b.getCapId());
            dto.setSceneGroupId(sceneGroupId);
            dto.setName(b.getCapName());
            dto.setStatus(b.getStatus() != null ? b.getStatus().name() : "ACTIVE");
            dto.setBindTime(System.currentTimeMillis());
            result.add(dto);
        }
        return result;
    }

    @Override
    public SceneGroupCapabilityDTO addCapability(String sceneGroupId, String capId) {
        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (sceneGroup == null) {
            return null;
        }

        CapabilityBinding binding = new CapabilityBinding(
            "cb-" + System.currentTimeMillis(),
            sceneGroupId,
            capId
        );
        binding.setCapName(capId);
        binding.activate();

        sceneGroup.addCapabilityBinding(binding);
        
        log.info("Bound capability {} to scene group {} via SE SDK 3.0.1", capId, sceneGroupId);
        
        SceneGroupCapabilityDTO result = new SceneGroupCapabilityDTO();
        result.setCapId(capId);
        result.setSceneGroupId(sceneGroupId);
        result.setName(capId);
        result.setStatus("ACTIVE");
        result.setBindTime(System.currentTimeMillis());
        return result;
    }

    @Override
    public boolean removeCapability(String sceneGroupId, String capId) {
        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (sceneGroup == null) {
            return false;
        }

        List<CapabilityBinding> bindings = sceneGroup.getAllCapabilityBindings();
        for (CapabilityBinding b : bindings) {
            if (capId.equals(b.getCapId())) {
                sceneGroup.removeCapabilityBinding(b.getBindingId());
                log.info("Unbound capability {} from scene group {} via SE SDK 3.0.1", capId, sceneGroupId);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<SceneParticipantDTO> getParticipants(String sceneGroupId) {
        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (sceneGroup == null) {
            return new ArrayList<>();
        }
        
        List<Participant> participants = sceneGroup.getAllParticipants();
        List<SceneParticipantDTO> result = new ArrayList<>();
        for (Participant p : participants) {
            result.add(convertParticipantToDTO(p, sceneGroupId));
        }
        return result;
    }

    @Override
    public SceneParticipantDTO addParticipant(String sceneGroupId, String userId, String role) {
        Participant participant = new Participant(
            "p-" + System.currentTimeMillis(),
            userId,
            userId,
            Participant.Type.USER
        );
        participant.setRole(convertRole(role));
        participant.join();
        participant.activate();

        sceneGroupManager.addParticipant(sceneGroupId, participant);

        log.info("Participant {} joined scene group {} via SE SDK 3.0.1", userId, sceneGroupId);
        
        return convertParticipantToDTO(participant, sceneGroupId);
    }

    @Override
    public boolean removeParticipant(String sceneGroupId, String participantId) {
        sceneGroupManager.removeParticipant(sceneGroupId, participantId);
        log.info("Participant {} left scene group {} via SE SDK 3.0.1", participantId, sceneGroupId);
        return true;
    }

    @Override
    public boolean bindKnowledgeBase(String sceneGroupId, KnowledgeBindingDTO binding) {
        if (knowledgeBindingManager == null) {
            log.warn("KnowledgeBindingManager not available in SE SDK 3.0.1");
            return false;
        }
        
        KnowledgeBinding kbBinding = new KnowledgeBinding();
        kbBinding.setKnowledgeBaseId(binding.getKbId());
        kbBinding.setKnowledgeBaseName(binding.getKbName());
        kbBinding.setPriority(binding.getPriority() != null ? binding.getPriority() : 0);
        
        String bindingId = knowledgeBindingManager.bind(sceneGroupId, kbBinding);
        
        log.info("Bound knowledge base {} to scene group {} via SE SDK 3.0.1, bindingId: {}", 
            binding.getKbId(), sceneGroupId, bindingId);
        
        if (eventLogService != null) {
            eventLogService.logCapabilityBind(sceneGroupId, binding.getKbId(), binding.getKbName(), "system");
        }
        
        return true;
    }

    @Override
    public boolean unbindKnowledgeBase(String sceneGroupId, String kbId) {
        if (knowledgeBindingManager == null) {
            log.warn("KnowledgeBindingManager not available in SE SDK 3.0.1");
            return false;
        }
        
        knowledgeBindingManager.unbind(sceneGroupId, kbId);
        log.info("Unbound knowledge base {} from scene group {} via SE SDK 3.0.1", kbId, sceneGroupId);
        
        if (eventLogService != null) {
            eventLogService.logCapabilityUnbind(sceneGroupId, kbId, kbId);
        }
        
        return true;
    }

    @Override
    public List<KnowledgeBindingDTO> getKnowledgeBases(String sceneGroupId) {
        if (knowledgeBindingManager == null) {
            log.warn("KnowledgeBindingManager not available in SE SDK 3.0.1");
            return new ArrayList<>();
        }
        
        List<KnowledgeBinding> bindings = knowledgeBindingManager.getBindings(sceneGroupId);
        List<KnowledgeBindingDTO> result = new ArrayList<>();
        
        for (KnowledgeBinding info : bindings) {
            KnowledgeBindingDTO dto = new KnowledgeBindingDTO();
            dto.setKbId(info.getKnowledgeBaseId());
            dto.setKbName(info.getKnowledgeBaseName());
            dto.setLayer(info.getLayer() != null ? info.getLayer() : "SCENE_GROUP");
            dto.setPriority(info.getPriority());
            dto.setBindTime(info.getBindTime());
            result.add(dto);
        }
        
        return result;
    }

    @Override
    public SceneLlmConfigDTO getLlmConfig(String sceneGroupId) {
        if (llmConfigManager == null) {
            log.warn("SceneLlmConfigManager not available in SE SDK 3.0.1");
            return null;
        }
        
        SceneLlmConfigInfo config = llmConfigManager.getLlmConfig(sceneGroupId);
        if (config == null) {
            config = llmConfigManager.getDefaultConfig();
        }
        
        if (config == null) {
            return null;
        }
        
        SceneLlmConfigDTO dto = new SceneLlmConfigDTO();
        dto.setProvider(config.getProvider());
        dto.setModel(config.getModel());
        dto.setTemperature(config.getTemperature());
        dto.setMaxTokens(config.getMaxTokens());
        dto.setExtendedConfig(config.getExtensions());
        
        return dto;
    }

    @Override
    public boolean setLlmConfig(String sceneGroupId, SceneLlmConfigDTO config) {
        if (llmConfigManager == null) {
            log.warn("SceneLlmConfigManager not available in SE SDK 3.0.1");
            return false;
        }
        
        SceneLlmConfigInfo configInfo = new SceneLlmConfigInfo(sceneGroupId);
        configInfo.setProvider(config.getProvider());
        configInfo.setModel(config.getModel());
        configInfo.setTemperature(config.getTemperature() != null ? config.getTemperature() : 0.7);
        configInfo.setMaxTokens(config.getMaxTokens() != null ? config.getMaxTokens() : 2048);
        
        if (config.getExtendedConfig() != null) {
            configInfo.getExtensions().putAll(config.getExtendedConfig());
        }
        
        llmConfigManager.setLlmConfig(sceneGroupId, configInfo);
        
        log.info("Set LLM config for scene group {} via SE SDK 3.0.1: provider={}, model={}", 
            sceneGroupId, config.getProvider(), config.getModel());
        
        return true;
    }

    private SceneGroup.CreatorType convertCreatorType(SceneGroupConfigDTO.CreatorType type) {
        if (type == null) {
            return SceneGroup.CreatorType.USER;
        }
        switch (type) {
            case AGENT:
                return SceneGroup.CreatorType.AGENT;
            case SYSTEM:
                return SceneGroup.CreatorType.SYSTEM;
            default:
                return SceneGroup.CreatorType.USER;
        }
    }

    private Participant.Role convertRole(String role) {
        if (role == null) {
            return Participant.Role.EMPLOYEE;
        }
        switch (role.toUpperCase()) {
            case "OWNER":
                return Participant.Role.OWNER;
            case "MANAGER":
                return Participant.Role.MANAGER;
            case "LLM_ASSISTANT":
                return Participant.Role.LLM_ASSISTANT;
            case "COORDINATOR":
                return Participant.Role.COORDINATOR;
            case "OBSERVER":
                return Participant.Role.OBSERVER;
            default:
                return Participant.Role.EMPLOYEE;
        }
    }

    private SceneGroupDTO convertToDTO(SceneGroup sceneGroup) {
        if (sceneGroup == null) {
            return null;
        }

        SceneGroupDTO dto = new SceneGroupDTO();
        dto.setSceneGroupId(sceneGroup.getSceneGroupId());
        dto.setTemplateId(sceneGroup.getTemplateId());
        dto.setName(sceneGroup.getName());
        dto.setDescription(sceneGroup.getDescription());
        dto.setStatus(convertStatus(sceneGroup.getStatus()));
        dto.setCreatorId(sceneGroup.getCreatorId());
        dto.setCreatorType(convertCreatorTypeFromSE(sceneGroup.getCreatorType()));
        dto.setCreateTime(sceneGroup.getCreateTime() != null ? sceneGroup.getCreateTime().toEpochMilli() : 0L);
        dto.setLastUpdateTime(sceneGroup.getLastUpdateTime() != null ? sceneGroup.getLastUpdateTime().toEpochMilli() : 0L);

        List<Participant> participants = sceneGroup.getAllParticipants();
        List<SceneParticipantDTO> participantDTOs = new ArrayList<>();
        for (Participant p : participants) {
            participantDTOs.add(convertParticipantToDTO(p, sceneGroup.getSceneGroupId()));
        }
        dto.setParticipants(participantDTOs);
        dto.setMemberCount(participants.size());

        List<CapabilityBinding> bindings = sceneGroup.getAllCapabilityBindings();
        List<CapabilityBindingDTO> bindingDTOs = new ArrayList<>();
        for (CapabilityBinding b : bindings) {
            bindingDTOs.add(convertCapabilityBindingToDTO(b));
        }
        dto.setCapabilityBindings(bindingDTOs);

        if (knowledgeBindingManager != null) {
            dto.setKnowledgeBases(getKnowledgeBases(sceneGroup.getSceneGroupId()));
        } else {
            dto.setKnowledgeBases(new ArrayList<>());
        }

        return dto;
    }

    private SceneParticipantDTO convertParticipantToDTO(Participant participant, String sceneGroupId) {
        if (participant == null) {
            return null;
        }

        SceneParticipantDTO dto = new SceneParticipantDTO();
        dto.setParticipantId(participant.getParticipantId());
        dto.setSceneGroupId(sceneGroupId);
        dto.setName(participant.getName());
        dto.setUserId(participant.getUserId());
        dto.setParticipantType(convertParticipantTypeFromSE(participant.getType()));
        dto.setRole(convertRoleFromSE(participant.getRole()));
        dto.setStatus(convertParticipantStatusFromSE(participant.getStatus()));
        dto.setJoinTime(participant.getJoinTime() != null ? participant.getJoinTime().toEpochMilli() : 0L);
        dto.setLastHeartbeat(participant.getLastHeartbeat() != null ? participant.getLastHeartbeat().toEpochMilli() : 0L);
        return dto;
    }

    private CapabilityBindingDTO convertCapabilityBindingToDTO(CapabilityBinding binding) {
        if (binding == null) {
            return null;
        }

        CapabilityBindingDTO dto = new CapabilityBindingDTO();
        dto.setBindingId(binding.getBindingId());
        dto.setSceneGroupId(binding.getSceneGroupId());
        dto.setCapId(binding.getCapId());
        dto.setCapName(binding.getCapName());
        dto.setProviderType(convertProviderTypeFromSE(binding.getProviderType()));
        dto.setPriority(binding.getPriority());
        dto.setStatus(convertBindingStatusFromSE(binding.getStatus()));
        return dto;
    }

    private SceneGroupConfigDTO.CreatorType convertCreatorTypeFromSE(SceneGroup.CreatorType type) {
        if (type == null) {
            return SceneGroupConfigDTO.CreatorType.USER;
        }
        switch (type) {
            case AGENT:
                return SceneGroupConfigDTO.CreatorType.AGENT;
            case SYSTEM:
                return SceneGroupConfigDTO.CreatorType.SYSTEM;
            default:
                return SceneGroupConfigDTO.CreatorType.USER;
        }
    }

    private SceneGroupStatus convertStatus(SceneGroup.Status status) {
        if (status == null) {
            return SceneGroupStatus.CREATING;
        }
        switch (status) {
            case ACTIVE:
                return SceneGroupStatus.ACTIVE;
            case SUSPENDED:
                return SceneGroupStatus.SUSPENDED;
            case DESTROYING:
                return SceneGroupStatus.DESTROYING;
            case DESTROYED:
                return SceneGroupStatus.DESTROYED;
            default:
                return SceneGroupStatus.CREATING;
        }
    }

    private ParticipantType convertParticipantTypeFromSE(Participant.Type type) {
        if (type == null) {
            return ParticipantType.USER;
        }
        switch (type) {
            case AGENT:
                return ParticipantType.AGENT;
            case SUPER_AGENT:
                return ParticipantType.SUPER_AGENT;
            default:
                return ParticipantType.USER;
        }
    }

    private String convertRoleFromSE(Participant.Role role) {
        if (role == null) {
            return "EMPLOYEE";
        }
        return role.name();
    }

    private ParticipantStatus convertParticipantStatusFromSE(Participant.Status status) {
        if (status == null) {
            return ParticipantStatus.JOINED;
        }
        switch (status) {
            case JOINED:
                return ParticipantStatus.JOINED;
            case ACTIVE:
                return ParticipantStatus.ACTIVE;
            case SUSPENDED:
                return ParticipantStatus.SUSPENDED;
            default:
                return ParticipantStatus.JOINED;
        }
    }

    private CapabilityProviderType convertProviderTypeFromSE(CapabilityBinding.ProviderType type) {
        if (type == null) {
            return CapabilityProviderType.PLATFORM;
        }
        switch (type) {
            case AGENT:
                return CapabilityProviderType.AGENT;
            default:
                return CapabilityProviderType.PLATFORM;
        }
    }

    private CapabilityBindingStatus convertBindingStatusFromSE(CapabilityBinding.Status status) {
        if (status == null) {
            return CapabilityBindingStatus.ACTIVE;
        }
        switch (status) {
            case ACTIVE:
                return CapabilityBindingStatus.ACTIVE;
            case INACTIVE:
                return CapabilityBindingStatus.INACTIVE;
            case ERROR:
                return CapabilityBindingStatus.ERROR;
            default:
                return CapabilityBindingStatus.ACTIVE;
        }
    }

    private PageResult<SceneGroupDTO> pagedResult(List<SceneGroup> groups, int pageNum, int pageSize) {
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, groups.size());

        List<SceneGroup> pagedGroups = start < groups.size()
            ? groups.subList(start, end)
            : new ArrayList<>();

        List<SceneGroupDTO> dtoList = new ArrayList<>();
        for (SceneGroup group : pagedGroups) {
            dtoList.add(convertToDTO(group));
        }

        PageResult<SceneGroupDTO> result = new PageResult<>();
        result.setList(dtoList);
        result.setTotal(groups.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public PageResult<SceneGroupDTO> getMyLedGroups(int pageNum, int pageSize) {
        return listByCreator("current-user", pageNum, pageSize);
    }

    @Override
    public PageResult<SceneGroupDTO> getMyParticipatedGroups(int pageNum, int pageSize) {
        return listByParticipant("current-user", pageNum, pageSize);
    }

    @Override
    public boolean updateParticipantRole(String groupId, String participantId, String role) {
        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(groupId);
        if (sceneGroup == null) {
            return false;
        }
        
        List<Participant> participants = sceneGroup.getAllParticipants();
        for (Participant p : participants) {
            if (participantId.equals(p.getParticipantId())) {
                p.setRole(convertRole(role));
                log.info("Updated participant {} role to {} in scene group {} via SE SDK 3.0.1", 
                    participantId, role, groupId);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<SceneSnapshotDTO> getSnapshots(String groupId) {
        log.warn("[SceneGroupServiceSEImpl] Snapshot management not yet supported in SE SDK 3.0.1, returning empty list for group: {}", groupId);
        return new ArrayList<>();
    }

    @Override
    public SceneSnapshotDTO createSnapshot(String groupId, String name) {
        log.warn("[SceneGroupServiceSEImpl] Snapshot management not yet supported in SE SDK 3.0.1");
        SceneSnapshotDTO result = new SceneSnapshotDTO();
        result.setSnapshotId(UUID.randomUUID().toString());
        result.setSceneGroupId(groupId);
        result.setName(name);
        result.setStatus("not_supported");
        return result;
    }

    @Override
    public boolean restoreSnapshot(String groupId, String snapshotId) {
        log.warn("[SceneGroupServiceSEImpl] Snapshot management not yet supported in SE SDK 3.0.1");
        return false;
    }

    @Override
    public boolean deleteSnapshot(String groupId, String snapshotId) {
        log.warn("[SceneGroupServiceSEImpl] Snapshot management not yet supported in SE SDK 3.0.1");
        return false;
    }

    @Override
    public PageResult<SceneGroupEventLogDTO> getEventLog(String groupId, int pageNum, int pageSize) {
        if (eventLogService == null) {
            return new PageResult<>();
        }
        
        List<SceneGroupEventLogDTO> logs = eventLogService.getEventLogs(groupId, 100);
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, logs.size());
        List<SceneGroupEventLogDTO> pagedLogs = start < logs.size() ? logs.subList(start, end) : new ArrayList<>();
        
        PageResult<SceneGroupEventLogDTO> result = new PageResult<>();
        result.setList(pagedLogs);
        result.setTotal(logs.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public WorkflowResultDTO startWorkflow(String groupId, String workflowId) {
        throw new UnsupportedOperationException("Workflow management not yet supported in SE SDK 3.0.1");
    }

    @Override
    public SceneGroupDTO createFromFusion(String fusionId) {
        throw new UnsupportedOperationException("Fusion template not yet supported in SE SDK 3.0.1");
    }

    @Override
    public SceneGroupCapabilityDTO getCapability(String groupId, String capId) {
        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(groupId);
        if (sceneGroup == null) {
            return null;
        }
        
        List<CapabilityBinding> bindings = sceneGroup.getAllCapabilityBindings();
        for (CapabilityBinding b : bindings) {
            if (capId.equals(b.getCapId())) {
                SceneGroupCapabilityDTO dto = new SceneGroupCapabilityDTO();
                dto.setCapId(b.getCapId());
                dto.setSceneGroupId(groupId);
                dto.setName(b.getCapName());
                dto.setStatus(b.getStatus() != null ? b.getStatus().name() : "ACTIVE");
                return dto;
            }
        }
        return null;
    }

    @Override
    public SceneKnowledgeConfigDTO getKnowledgeConfig(String groupId) {
        if (knowledgeBindingManager == null) {
            return null;
        }
        
        SceneKnowledgeConfigDTO config = new SceneKnowledgeConfigDTO();
        config.setTopK(5);
        config.setThreshold(0.7);
        config.setCrossLayerSearch(false);
        return config;
    }

    @Override
    public SceneKnowledgeConfigDTO updateKnowledgeConfig(String groupId, SceneKnowledgeConfigDTO config) {
        throw new UnsupportedOperationException("Knowledge config update not yet supported in SE SDK 3.0.1");
    }

    @Override
    public List<LlmModelDTO> getLlmProviderModels(String groupId, String providerId) {
        throw new UnsupportedOperationException("LLM provider models query not yet supported in SE SDK 3.0.1");
    }

    @Override
    public boolean resetLlmConfig(String groupId) {
        if (llmConfigManager == null) {
            return false;
        }
        
        llmConfigManager.resetLlmConfig(groupId);
        log.info("Reset LLM config for scene group {} via SE SDK 3.0.1", groupId);
        return true;
    }

    @Override
    public ActionResultDTO executeAction(String groupId, String action) {
        throw new UnsupportedOperationException("Custom action execution not yet supported in SE SDK 3.0.1");
    }

    @Override
    public List<MemberDTO> getMyLedMembers() {
        throw new UnsupportedOperationException("My led members query not yet supported in SE SDK 3.0.1");
    }
}

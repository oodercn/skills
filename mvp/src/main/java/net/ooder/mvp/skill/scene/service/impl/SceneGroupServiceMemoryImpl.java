package net.ooder.mvp.skill.scene.service.impl;

import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.scene.*;
import net.ooder.mvp.skill.scene.service.SceneGroupService;
import net.ooder.mvp.skill.scene.service.SceneTemplateService;
import net.ooder.mvp.skill.scene.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class SceneGroupServiceMemoryImpl implements SceneGroupService {

    private static final Logger log = LoggerFactory.getLogger(SceneGroupServiceMemoryImpl.class);

    private final Map<String, SceneGroupDTO> sceneGroups = new ConcurrentHashMap<>();
    private final Map<String, List<SceneParticipantDTO>> participants = new ConcurrentHashMap<>();
    private final Map<String, List<CapabilityBindingDTO>> capabilityBindings = new ConcurrentHashMap<>();
    private final Map<String, List<SceneSnapshotDTO>> snapshots = new ConcurrentHashMap<>();
    private final Map<String, List<KnowledgeBindingDTO>> knowledgeBindings = new ConcurrentHashMap<>();
    
    @Autowired(required = false)
    private SceneTemplateService templateService;
    
    @Autowired(required = false)
    private TodoService todoService;

    public SceneGroupServiceMemoryImpl() {
        log.info("SceneGroupServiceMemoryImpl initialized");
        initDemoData();
    }

    
    private void initDemoData() {
        SceneGroupConfigDTO config = new SceneGroupConfigDTO();
        config.setName("研发部日志汇报组");
        config.setDescription("研发部团队的日常日志汇报场景组");
        config.setCreatorId("user-001");
        config.setCreatorType(ParticipantType.USER);
        
        SceneGroupDTO group1 = create("tpl-daily-report", config);
        group1.setStatus(SceneGroupStatus.ACTIVE);
        group1.setCreateTime(System.currentTimeMillis() - 3600000);
        group1.setLastUpdateTime(System.currentTimeMillis());
        
        SceneParticipantDTO p1 = new SceneParticipantDTO();
        p1.setParticipantId("user-001");
        p1.setName("张三");
        p1.setParticipantType(ParticipantType.USER);
        p1.setRole("MANAGER");
        p1.setJoinTime(System.currentTimeMillis() - 3600000);
        p1.setLastHeartbeat(System.currentTimeMillis());
        p1.setStatus(ParticipantStatus.JOINED);
        
        SceneParticipantDTO p2 = new SceneParticipantDTO();
        p2.setParticipantId("user-002");
        p2.setName("李四");
        p2.setParticipantType(ParticipantType.USER);
        p2.setRole("EMPLOYEE");
        p2.setJoinTime(System.currentTimeMillis() - 3500000);
        p2.setLastHeartbeat(System.currentTimeMillis());
        p2.setStatus(ParticipantStatus.JOINED);
        
        SceneParticipantDTO p3 = new SceneParticipantDTO();
        p3.setParticipantId("agent-001");
        p3.setName("日报助手");
        p3.setParticipantType(ParticipantType.AGENT);
        p3.setRole("LLM_ASSISTANT");
        p3.setJoinTime(System.currentTimeMillis() - 3400000);
        p3.setLastHeartbeat(System.currentTimeMillis());
        p3.setStatus(ParticipantStatus.JOINED);
        
        SceneParticipantDTO p4 = new SceneParticipantDTO();
        p4.setParticipantId("agent-002");
        p4.setName("周报汇总Agent");
        p4.setParticipantType(ParticipantType.AGENT);
        p4.setRole("COORDINATOR");
        p4.setJoinTime(System.currentTimeMillis() - 3300000);
        p4.setLastHeartbeat(System.currentTimeMillis());
        p4.setStatus(ParticipantStatus.JOINED);
        
        SceneParticipantDTO p5 = new SceneParticipantDTO();
        p5.setParticipantId("user-003");
        p5.setName("王五");
        p5.setParticipantType(ParticipantType.USER);
        p5.setRole("HR");
        p5.setJoinTime(System.currentTimeMillis() - 3200000);
        p5.setLastHeartbeat(System.currentTimeMillis());
        p5.setStatus(ParticipantStatus.JOINED);
        
        SceneParticipantDTO p6 = new SceneParticipantDTO();
        p6.setParticipantId("user-004");
        p6.setName("赵六");
        p6.setParticipantType(ParticipantType.USER);
        p6.setRole("EMPLOYEE");
        p6.setJoinTime(System.currentTimeMillis() - 3100000);
        p6.setLastHeartbeat(System.currentTimeMillis());
        p6.setStatus(ParticipantStatus.JOINED);
        
        List<SceneParticipantDTO> participantList = new ArrayList<>();
        participantList.add(p1);
        participantList.add(p2);
        participantList.add(p3);
        participantList.add(p4);
        participantList.add(p5);
        participantList.add(p6);
        participants.put(group1.getSceneGroupId(), participantList);
        group1.setMemberCount(6);
        
        CapabilityBindingDTO b1 = new CapabilityBindingDTO();
        b1.setBindingId("cb-" + System.currentTimeMillis() + "-1");
        b1.setSceneGroupId(group1.getSceneGroupId());
        b1.setCapId("report-analyze");
        b1.setCapName("日志分析能力");
        b1.setProviderType(CapabilityProviderType.AGENT);
        b1.setConnectorType(ConnectorType.INTERNAL);
        b1.setPriority(1);
        b1.setFallback(true);
        b1.setStatus(CapabilityBindingStatus.ACTIVE);
        
        CapabilityBindingDTO b2 = new CapabilityBindingDTO();
        b2.setBindingId("cb-" + System.currentTimeMillis() + "-2");
        b2.setSceneGroupId(group1.getSceneGroupId());
        b2.setCapId("daily-summary");
        b2.setCapName("日报汇总能力");
        b2.setProviderType(CapabilityProviderType.AGENT);
        b2.setConnectorType(ConnectorType.INTERNAL);
        b2.setPriority(2);
        b2.setFallback(true);
        b2.setStatus(CapabilityBindingStatus.ACTIVE);
        
        CapabilityBindingDTO b3 = new CapabilityBindingDTO();
        b3.setBindingId("cb-" + System.currentTimeMillis() + "-3");
        b3.setSceneGroupId(group1.getSceneGroupId());
        b3.setCapId("notification-push");
        b3.setCapName("消息推送能力");
        b3.setProviderType(CapabilityProviderType.PLATFORM);
        b3.setConnectorType(ConnectorType.INTERNAL);
        b3.setPriority(3);
        b3.setFallback(false);
        b3.setStatus(CapabilityBindingStatus.ACTIVE);
        
        List<CapabilityBindingDTO> bindingList = new ArrayList<>();
        bindingList.add(b1);
        bindingList.add(b2);
        bindingList.add(b3);
        capabilityBindings.put(group1.getSceneGroupId(), bindingList);
        group1.setCapabilityBindings(bindingList);
        
        sceneGroups.put(group1.getSceneGroupId(), group1);
        log.info("Created demo scene group: {} with {} participants and {} capabilities", 
            group1.getSceneGroupId(), participantList.size(), bindingList.size());
    }
    
    @Override
    public SceneGroupDTO create(String templateId, SceneGroupConfigDTO config) {
        SceneGroupDTO group = new SceneGroupDTO();
        group.setSceneGroupId("sg-" + System.currentTimeMillis());
        group.setTemplateId(templateId);
        group.setName(config != null ? config.getName() : "New Scene Group");
        group.setDescription(config != null ? config.getDescription() : "");
        group.setStatus(SceneGroupStatus.CREATING);
        
        String creatorId = config != null ? config.getCreatorId() : "system";
        ParticipantType creatorType = config != null ? config.getCreatorType() : ParticipantType.USER;
        
        group.setCreatorId(creatorId);
        group.setCreatorType(creatorType);
        group.setConfig(config);
        group.setCreateTime(System.currentTimeMillis());
        group.setLastUpdateTime(System.currentTimeMillis());
        
        sceneGroups.put(group.getSceneGroupId(), group);
        
        SceneParticipantDTO creatorParticipant = new SceneParticipantDTO();
        creatorParticipant.setParticipantId(creatorId);
        creatorParticipant.setParticipantType(creatorType);
        creatorParticipant.setRole("owner");
        creatorParticipant.setName(creatorId);
        creatorParticipant.setSceneGroupId(group.getSceneGroupId());
        creatorParticipant.setJoinTime(System.currentTimeMillis());
        creatorParticipant.setLastHeartbeat(System.currentTimeMillis());
        creatorParticipant.setStatus(ParticipantStatus.JOINED);
        
        List<SceneParticipantDTO> participantList = new ArrayList<>();
        participantList.add(creatorParticipant);
        participants.put(group.getSceneGroupId(), participantList);
        group.setMemberCount(1);
        
        if (templateId != null && templateService != null) {
            SceneTemplateDTO template = templateService.get(templateId);
            if (template != null && template.getCapabilities() != null) {
                List<CapabilityBindingDTO> bindingList = new ArrayList<>();
                int priority = 1;
                for (CapabilityDefDTO capDef : template.getCapabilities()) {
                    CapabilityBindingDTO binding = new CapabilityBindingDTO();
                    binding.setBindingId("cb-" + System.currentTimeMillis() + "-" + priority);
                    binding.setSceneGroupId(group.getSceneGroupId());
                    binding.setCapId(capDef.getCapId());
                    binding.setCapName(capDef.getName());
                    binding.setPriority(priority++);
                    binding.setFallback(true);
                    binding.setStatus(CapabilityBindingStatus.ACTIVE);
                    bindingList.add(binding);
                    log.info("Auto-bound capability {} to scene group {}", capDef.getCapId(), group.getSceneGroupId());
                }
                capabilityBindings.put(group.getSceneGroupId(), bindingList);
            }
        }
        
        log.info("Created scene group {} with creator {} as owner", group.getSceneGroupId(), creatorId);
        return group;
    }

    @Override
    public SceneGroupDTO update(String sceneGroupId, SceneGroupConfigDTO config) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group == null) return null;
        
        if (config != null) {
            if (config.getName() != null) {
                group.setName(config.getName());
            }
            if (config.getDescription() != null) {
                group.setDescription(config.getDescription());
            }
            if (config.getMinMembers() != null) {
                if (group.getConfig() == null) {
                    group.setConfig(new SceneGroupConfigDTO());
                }
                group.getConfig().setMinMembers(config.getMinMembers());
            }
            if (config.getMaxMembers() != null) {
                if (group.getConfig() == null) {
                    group.setConfig(new SceneGroupConfigDTO());
                }
                group.getConfig().setMaxMembers(config.getMaxMembers());
            }
        }
        group.setLastUpdateTime(System.currentTimeMillis());
        return group;
    }

    @Override
    public boolean destroy(String sceneGroupId) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group != null) {
            group.setStatus(SceneGroupStatus.DESTROYING);
            group.setLastUpdateTime(System.currentTimeMillis());
            group.setStatus(SceneGroupStatus.DESTROYED);
            return true;
        }
        return false;
    }

    @Override
    public SceneGroupDTO get(String sceneGroupId) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group != null) {
            group.setParticipants(participants.getOrDefault(sceneGroupId, new ArrayList<>()));
            group.setCapabilityBindings(capabilityBindings.getOrDefault(sceneGroupId, new ArrayList<>()));
        }
        return group;
    }
    
    @Override
    public List<SceneSnapshotDTO> listSnapshots(String sceneGroupId) {
        return snapshots.getOrDefault(sceneGroupId, new ArrayList<>());
    }

    @Override
    public PageResult<SceneGroupDTO> listAll(int pageNum, int pageSize) {
        List<SceneGroupDTO> allGroups = new ArrayList<>(sceneGroups.values());
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allGroups.size());
        
        List<SceneGroupDTO> pagedGroups = start < allGroups.size() 
            ? allGroups.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<SceneGroupDTO> result = new PageResult<>();
        result.setList(pagedGroups);
        result.setTotal(allGroups.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public PageResult<SceneGroupDTO> listByTemplate(String templateId, int pageNum, int pageSize) {
        List<SceneGroupDTO> filtered = new ArrayList<>();
        for (SceneGroupDTO group : sceneGroups.values()) {
            if (templateId.equals(group.getTemplateId())) {
                filtered.add(group);
            }
        }
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        
        List<SceneGroupDTO> pagedGroups = start < filtered.size() 
            ? filtered.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<SceneGroupDTO> result = new PageResult<>();
        result.setList(pagedGroups);
        result.setTotal(filtered.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public boolean activate(String sceneGroupId) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group != null) {
            group.setStatus(SceneGroupStatus.ACTIVE);
            group.setLastUpdateTime(System.currentTimeMillis());
            
            if (todoService != null && group.getCreatorId() != null) {
                try {
                    todoService.createSceneNotificationTodo(
                        sceneGroupId,
                        group.getCreatorId(),
                        "场景已激活",
                        "场景组 " + group.getName() + " 已成功激活"
                    );
                    log.info("Created activation notification todo for creator: {}", group.getCreatorId());
                } catch (Exception e) {
                    log.error("Failed to create activation notification todo: {}", e.getMessage());
                }
            }
            
            return true;
        }
        return false;
    }

    @Override
    public boolean deactivate(String sceneGroupId) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group != null) {
            group.setStatus(SceneGroupStatus.SUSPENDED);
            group.setLastUpdateTime(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public boolean join(String sceneGroupId, SceneParticipantDTO participant) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group == null) return false;
        
        if (participant.getParticipantId() == null || participant.getParticipantId().isEmpty()) {
            participant.setParticipantId("p-" + System.currentTimeMillis());
        }
        participant.setSceneGroupId(sceneGroupId);
        participant.setJoinTime(System.currentTimeMillis());
        participant.setLastHeartbeat(System.currentTimeMillis());
        participant.setStatus(ParticipantStatus.JOINED);
        
        List<SceneParticipantDTO> list = participants.computeIfAbsent(sceneGroupId, k -> new ArrayList<>());
        list.add(participant);
        
        group.setMemberCount(list.size());
        group.setLastUpdateTime(System.currentTimeMillis());
        
        if (todoService != null && group.getCreatorId() != null && !group.getCreatorId().equals(participant.getParticipantId())) {
            try {
                String participantName = participant.getName() != null ? participant.getName() : participant.getParticipantId();
                todoService.createSceneNotificationTodo(
                    sceneGroupId,
                    group.getCreatorId(),
                    "新成员加入场景",
                    participantName + " 加入了场景组 " + group.getName()
                );
                log.info("Created scene notification todo for creator: {}", group.getCreatorId());
            } catch (Exception e) {
                log.error("Failed to create scene notification todo: {}", e.getMessage());
            }
        }
        
        return true;
    }

    @Override
    public boolean leave(String sceneGroupId, String participantId) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        List<SceneParticipantDTO> list = participants.get(sceneGroupId);
        
        if (group != null && list != null) {
            boolean removed = list.removeIf(p -> participantId.equals(p.getParticipantId()));
            if (removed) {
                group.setMemberCount(list.size());
                group.setLastUpdateTime(System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean changeRole(String sceneGroupId, String participantId, String newRole) {
        List<SceneParticipantDTO> list = participants.get(sceneGroupId);
        if (list != null) {
            for (SceneParticipantDTO p : list) {
                if (participantId.equals(p.getParticipantId())) {
                    p.setRole(newRole);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public PageResult<SceneParticipantDTO> listParticipants(String sceneGroupId, int pageNum, int pageSize) {
        List<SceneParticipantDTO> list = participants.getOrDefault(sceneGroupId, new ArrayList<>());
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        
        List<SceneParticipantDTO> pagedList = start < list.size() 
            ? list.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<SceneParticipantDTO> result = new PageResult<>();
        result.setList(pagedList);
        result.setTotal(list.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public SceneParticipantDTO getParticipant(String sceneGroupId, String participantId) {
        List<SceneParticipantDTO> list = participants.get(sceneGroupId);
        if (list != null) {
            for (SceneParticipantDTO p : list) {
                if (participantId.equals(p.getParticipantId())) {
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public boolean bindCapability(String sceneGroupId, CapabilityBindingDTO binding) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group == null) return false;
        
        if (binding.getBindingId() == null || binding.getBindingId().isEmpty()) {
            binding.setBindingId("cb-" + System.currentTimeMillis());
        }
        binding.setSceneGroupId(sceneGroupId);
        binding.setStatus(CapabilityBindingStatus.ACTIVE);
        
        List<CapabilityBindingDTO> list = capabilityBindings.computeIfAbsent(sceneGroupId, k -> new ArrayList<>());
        list.add(binding);
        
        group.setLastUpdateTime(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean unbindCapability(String sceneGroupId, String bindingId) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        List<CapabilityBindingDTO> list = capabilityBindings.get(sceneGroupId);
        
        if (group != null && list != null) {
            boolean removed = list.removeIf(b -> bindingId.equals(b.getBindingId()));
            if (removed) {
                group.setLastUpdateTime(System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateCapabilityBinding(String sceneGroupId, String bindingId, CapabilityBindingDTO binding) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        List<CapabilityBindingDTO> list = capabilityBindings.get(sceneGroupId);
        
        if (group != null && list != null) {
            for (CapabilityBindingDTO b : list) {
                if (bindingId.equals(b.getBindingId())) {
                    if (binding.getPriority() > 0) {
                        b.setPriority(binding.getPriority());
                    }
                    b.setFallback(binding.isFallback());
                    if (binding.getConnectorConfig() != null) {
                        b.setConnectorConfig(binding.getConnectorConfig());
                    }
                    group.setLastUpdateTime(System.currentTimeMillis());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public PageResult<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId, int pageNum, int pageSize) {
        List<CapabilityBindingDTO> list = capabilityBindings.getOrDefault(sceneGroupId, new ArrayList<>());
        
        PageResult<CapabilityBindingDTO> result = new PageResult<>();
        result.setList(list);
        result.setTotal(list.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public SceneSnapshotDTO createSnapshot(String sceneGroupId) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group == null) return null;
        
        SceneSnapshotDTO snapshot = new SceneSnapshotDTO();
        snapshot.setSnapshotId("snap-" + System.currentTimeMillis());
        snapshot.setSceneGroupId(sceneGroupId);
        snapshot.setCreateTime(System.currentTimeMillis());
        snapshot.setStatus("valid");
        
        Map<String, Object> state = new HashMap<>();
        state.put("name", group.getName());
        state.put("description", group.getDescription());
        state.put("status", group.getStatus() != null ? group.getStatus().name() : null);
        state.put("config", group.getConfig());
        
        List<SceneParticipantDTO> participantsList = participants.get(sceneGroupId);
        if (participantsList != null) {
            state.put("participants", new ArrayList<>(participantsList));
        }
        
        List<CapabilityBindingDTO> bindingsList = capabilityBindings.get(sceneGroupId);
        if (bindingsList != null) {
            state.put("capabilityBindings", new ArrayList<>(bindingsList));
        }
        
        snapshot.setState(state);
        
        List<SceneSnapshotDTO> list = snapshots.computeIfAbsent(sceneGroupId, k -> new ArrayList<>());
        list.add(snapshot);
        
        log.info("Created snapshot {} for scene group {}", snapshot.getSnapshotId(), sceneGroupId);
        return snapshot;
    }

    @Override
    public boolean restoreSnapshot(String sceneGroupId, SceneSnapshotDTO snapshot) {
        if (snapshot == null || !sceneGroupId.equals(snapshot.getSceneGroupId())) {
            return false;
        }
        
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group == null) return false;
        
        Map<String, Object> state = snapshot.getState();
        if (state == null) return false;
        
        if (state.get("name") != null) {
            group.setName((String) state.get("name"));
        }
        if (state.get("description") != null) {
            group.setDescription((String) state.get("description"));
        }
        if (state.get("config") != null) {
            group.setConfig((SceneGroupConfigDTO) state.get("config"));
        }
        
        if (state.get("participants") != null) {
            @SuppressWarnings("unchecked")
            List<SceneParticipantDTO> participantsList = (List<SceneParticipantDTO>) state.get("participants");
            participants.put(sceneGroupId, new ArrayList<>(participantsList));
            group.setMemberCount(participantsList.size());
        }
        
        if (state.get("capabilityBindings") != null) {
            @SuppressWarnings("unchecked")
            List<CapabilityBindingDTO> bindingsList = (List<CapabilityBindingDTO>) state.get("capabilityBindings");
            capabilityBindings.put(sceneGroupId, new ArrayList<>(bindingsList));
        }
        
        group.setLastUpdateTime(System.currentTimeMillis());
        
        log.info("Restored snapshot {} for scene group {}", snapshot.getSnapshotId(), sceneGroupId);
        return true;
    }

    @Override
    public boolean deleteSnapshot(String sceneGroupId, String snapshotId) {
        List<SceneSnapshotDTO> list = snapshots.get(sceneGroupId);
        if (list != null) {
            boolean removed = list.removeIf(s -> snapshotId.equals(s.getSnapshotId()));
            if (removed) {
                SceneGroupDTO group = sceneGroups.get(sceneGroupId);
                if (group != null) {
                    group.setLastUpdateTime(System.currentTimeMillis());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public PageResult<SceneGroupDTO> listByCreator(String creatorId, int pageNum, int pageSize) {
        List<SceneGroupDTO> filtered = new ArrayList<>();
        for (SceneGroupDTO group : sceneGroups.values()) {
            if (creatorId.equals(group.getCreatorId())) {
                filtered.add(group);
            }
        }
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        
        List<SceneGroupDTO> pagedGroups = start < filtered.size() 
            ? filtered.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<SceneGroupDTO> result = new PageResult<>();
        result.setList(pagedGroups);
        result.setTotal(filtered.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public PageResult<SceneGroupDTO> listByParticipant(String participantId, int pageNum, int pageSize) {
        Set<String> groupIds = new HashSet<>();
        for (Map.Entry<String, List<SceneParticipantDTO>> entry : participants.entrySet()) {
            for (SceneParticipantDTO p : entry.getValue()) {
                if (participantId.equals(p.getParticipantId())) {
                    groupIds.add(entry.getKey());
                    break;
                }
            }
        }
        
        List<SceneGroupDTO> filtered = new ArrayList<>();
        for (String groupId : groupIds) {
            SceneGroupDTO group = sceneGroups.get(groupId);
            if (group != null) {
                filtered.add(group);
            }
        }
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        
        List<SceneGroupDTO> pagedGroups = start < filtered.size() 
            ? filtered.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<SceneGroupDTO> result = new PageResult<>();
        result.setList(pagedGroups);
        result.setTotal(filtered.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public FailoverStatusDTO getFailoverStatus(String sceneGroupId) {
        FailoverStatusDTO status = new FailoverStatusDTO();
        status.setSceneGroupId(sceneGroupId);
        status.setStatus("normal");
        return status;
    }

    @Override
    public boolean handleFailover(String sceneGroupId, String failedParticipantId) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group != null) {
            group.setLastUpdateTime(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public boolean bindKnowledgeBase(String sceneGroupId, KnowledgeBindingDTO binding) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group == null) return false;
        
        List<KnowledgeBindingDTO> bindings = knowledgeBindings.computeIfAbsent(sceneGroupId, k -> new ArrayList<>());
        
        bindings.removeIf(b -> b.getKbId().equals(binding.getKbId()));
        
        if (binding.getTopK() == null) binding.setTopK(5);
        if (binding.getThreshold() == null) binding.setThreshold(0.7);
        if (binding.getLayer() == null) binding.setLayer("SCENE");
        
        bindings.add(binding);
        
        group.setKnowledgeBases(bindings);
        group.setLastUpdateTime(System.currentTimeMillis());
        
        log.info("Bound knowledge base {} to scene group {}", binding.getKbId(), sceneGroupId);
        return true;
    }

    @Override
    public boolean unbindKnowledgeBase(String sceneGroupId, String kbId) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group == null) return false;
        
        List<KnowledgeBindingDTO> bindings = knowledgeBindings.get(sceneGroupId);
        if (bindings == null) return false;
        
        boolean removed = bindings.removeIf(b -> b.getKbId().equals(kbId));
        
        if (removed) {
            group.setKnowledgeBases(bindings);
            group.setLastUpdateTime(System.currentTimeMillis());
            log.info("Unbound knowledge base {} from scene group {}", kbId, sceneGroupId);
        }
        
        return removed;
    }

    @Override
    public List<KnowledgeBindingDTO> listKnowledgeBindings(String sceneGroupId) {
        return knowledgeBindings.getOrDefault(sceneGroupId, new ArrayList<>());
    }

    @Override
    public boolean updateKnowledgeConfig(String sceneGroupId, Map<String, Object> config) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group == null) return false;
        
        if (group.getConfig() == null) {
            group.setConfig(new SceneGroupConfigDTO());
        }
        
        if (config.get("topK") != null) {
            group.getConfig().setKnowledgeTopK(((Number) config.get("topK")).intValue());
        }
        if (config.get("threshold") != null) {
            group.getConfig().setKnowledgeThreshold(((Number) config.get("threshold")).doubleValue());
        }
        if (config.get("crossLayerSearch") != null) {
            group.getConfig().setCrossLayerSearch((Boolean) config.get("crossLayerSearch"));
        }
        
        group.setLastUpdateTime(System.currentTimeMillis());
        return true;
    }

    private final Map<String, Map<String, Object>> llmConfigs = new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> getLlmConfig(String sceneGroupId) {
        Map<String, Object> config = llmConfigs.get(sceneGroupId);
        if (config == null) {
            config = new HashMap<>();
            config.put("provider", "deepseek");
            config.put("model", "deepseek-chat");
            config.put("decisionMode", "ONLINE_FIRST");
            config.put("decisionTimeout", 30000);
            config.put("decisionCache", true);
            config.put("cacheTtl", 300000);
            config.put("functionCalling", true);
            config.put("maxIterations", 5);
            config.put("llmTimeout", 60000);
            config.put("dailyTokenLimit", 100000);
            config.put("usedTokens", 0);
            config.put("remainingTokens", 100000);
        }
        return config;
    }

    @Override
    public boolean updateLlmConfig(String sceneGroupId, Map<String, Object> config) {
        SceneGroupDTO group = sceneGroups.get(sceneGroupId);
        if (group == null) return false;
        
        Map<String, Object> existingConfig = llmConfigs.computeIfAbsent(sceneGroupId, k -> new HashMap<>());
        existingConfig.putAll(config);
        
        group.setLastUpdateTime(System.currentTimeMillis());
        return true;
    }
}

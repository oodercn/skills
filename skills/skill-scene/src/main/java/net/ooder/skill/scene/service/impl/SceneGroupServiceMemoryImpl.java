package net.ooder.skill.scene.service.impl;

import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.scene.*;
import net.ooder.skill.scene.service.SceneGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public SceneGroupServiceMemoryImpl() {
        log.info("SceneGroupServiceMemoryImpl initialized");
    }

    @Override
    public SceneGroupDTO create(String templateId, SceneGroupConfigDTO config) {
        SceneGroupDTO group = new SceneGroupDTO();
        group.setSceneGroupId("sg-" + System.currentTimeMillis());
        group.setTemplateId(templateId);
        group.setName(config != null ? config.getName() : "New Scene Group");
        group.setDescription(config != null ? config.getDescription() : "");
        group.setStatus(SceneGroupStatus.CREATING);
        group.setCreatorId(config != null ? config.getCreatorId() : "system");
        group.setCreatorType(config != null ? config.getCreatorType() : ParticipantType.USER);
        group.setConfig(config);
        group.setCreateTime(System.currentTimeMillis());
        group.setLastUpdateTime(System.currentTimeMillis());
        
        sceneGroups.put(group.getSceneGroupId(), group);
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
        return sceneGroups.get(sceneGroupId);
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
        
        List<SceneSnapshotDTO> list = snapshots.computeIfAbsent(sceneGroupId, k -> new ArrayList<>());
        list.add(snapshot);
        
        return snapshot;
    }

    @Override
    public boolean restoreSnapshot(String sceneGroupId, SceneSnapshotDTO snapshot) {
        return snapshot != null && sceneGroupId.equals(snapshot.getSceneGroupId());
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
}

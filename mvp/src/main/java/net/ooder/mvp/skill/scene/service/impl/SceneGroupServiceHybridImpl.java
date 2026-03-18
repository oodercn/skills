package net.ooder.mvp.skill.scene.service.impl;

import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.scene.*;
import net.ooder.mvp.skill.scene.sdk.SceneSdkAdapter;
import net.ooder.mvp.skill.scene.service.SceneGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class SceneGroupServiceHybridImpl implements SceneGroupService {

    private static final Logger log = LoggerFactory.getLogger(SceneGroupServiceHybridImpl.class);

    private final SceneSdkAdapter sdkAdapter;
    private final SceneGroupServiceMemoryImpl memoryService;

    public SceneGroupServiceHybridImpl(
            @Autowired(required = false) SceneSdkAdapter sdkAdapter,
            SceneGroupServiceMemoryImpl memoryService) {
        this.sdkAdapter = sdkAdapter;
        this.memoryService = memoryService;
        
        if (sdkAdapter != null && sdkAdapter.isAvailable()) {
            log.info("SceneGroupServiceHybridImpl initialized with SDK adapter");
        } else {
            log.info("SceneGroupServiceHybridImpl initialized with memory fallback");
        }
    }

    private boolean useSdk() {
        return sdkAdapter != null && sdkAdapter.isAvailable();
    }

    @Override
    public SceneGroupDTO create(String templateId, SceneGroupConfigDTO config) {
        if (useSdk()) {
            SceneGroupDTO result = sdkAdapter.createSceneGroup(templateId, config);
            if (result != null) {
                return result;
            }
        }
        return memoryService.create(templateId, config);
    }

    @Override
    public boolean destroy(String sceneGroupId) {
        if (useSdk()) {
            sdkAdapter.deactivateSceneGroup(sceneGroupId);
        }
        return memoryService.destroy(sceneGroupId);
    }

    @Override
    public SceneGroupDTO get(String sceneGroupId) {
        if (useSdk()) {
            SceneGroupDTO result = sdkAdapter.getSceneGroup(sceneGroupId);
            if (result != null) {
                return result;
            }
        }
        return memoryService.get(sceneGroupId);
    }

    @Override
    public PageResult<SceneGroupDTO> listAll(int pageNum, int pageSize) {
        return memoryService.listAll(pageNum, pageSize);
    }

    @Override
    public PageResult<SceneGroupDTO> listByTemplate(String templateId, int pageNum, int pageSize) {
        return memoryService.listByTemplate(templateId, pageNum, pageSize);
    }

    @Override
    public boolean activate(String sceneGroupId) {
        if (useSdk()) {
            sdkAdapter.activateSceneGroup(sceneGroupId);
        }
        return memoryService.activate(sceneGroupId);
    }

    @Override
    public boolean deactivate(String sceneGroupId) {
        if (useSdk()) {
            sdkAdapter.deactivateSceneGroup(sceneGroupId);
        }
        return memoryService.deactivate(sceneGroupId);
    }

    @Override
    public boolean join(String sceneGroupId, SceneParticipantDTO participant) {
        if (useSdk()) {
            sdkAdapter.joinSceneGroup(sceneGroupId, participant);
        }
        return memoryService.join(sceneGroupId, participant);
    }

    @Override
    public boolean leave(String sceneGroupId, String participantId) {
        if (useSdk()) {
            sdkAdapter.leaveSceneGroup(sceneGroupId, participantId);
        }
        return memoryService.leave(sceneGroupId, participantId);
    }

    @Override
    public boolean changeRole(String sceneGroupId, String participantId, String newRole) {
        return memoryService.changeRole(sceneGroupId, participantId, newRole);
    }

    @Override
    public PageResult<SceneParticipantDTO> listParticipants(String sceneGroupId, int pageNum, int pageSize) {
        return memoryService.listParticipants(sceneGroupId, pageNum, pageSize);
    }

    @Override
    public SceneParticipantDTO getParticipant(String sceneGroupId, String participantId) {
        return memoryService.getParticipant(sceneGroupId, participantId);
    }

    @Override
    public boolean bindCapability(String sceneGroupId, CapabilityBindingDTO binding) {
        if (useSdk()) {
            CapabilityBindingDTO result = sdkAdapter.bindCapability(sceneGroupId, binding);
            if (result != null) {
                return true;
            }
        }
        return memoryService.bindCapability(sceneGroupId, binding);
    }

    @Override
    public boolean unbindCapability(String sceneGroupId, String bindingId) {
        if (useSdk()) {
            sdkAdapter.unbindCapability(sceneGroupId, bindingId);
        }
        return memoryService.unbindCapability(sceneGroupId, bindingId);
    }

    @Override
    public PageResult<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId, int pageNum, int pageSize) {
        return memoryService.listCapabilityBindings(sceneGroupId, pageNum, pageSize);
    }

    @Override
    public SceneSnapshotDTO createSnapshot(String sceneGroupId) {
        return memoryService.createSnapshot(sceneGroupId);
    }

    @Override
    public boolean restoreSnapshot(String sceneGroupId, SceneSnapshotDTO snapshot) {
        return memoryService.restoreSnapshot(sceneGroupId, snapshot);
    }

    @Override
    public boolean deleteSnapshot(String sceneGroupId, String snapshotId) {
        return memoryService.deleteSnapshot(sceneGroupId, snapshotId);
    }

    @Override
    public PageResult<SceneGroupDTO> listByCreator(String creatorId, int pageNum, int pageSize) {
        return memoryService.listByCreator(creatorId, pageNum, pageSize);
    }

    @Override
    public PageResult<SceneGroupDTO> listByParticipant(String participantId, int pageNum, int pageSize) {
        return memoryService.listByParticipant(participantId, pageNum, pageSize);
    }

    @Override
    public FailoverStatusDTO getFailoverStatus(String sceneGroupId) {
        return memoryService.getFailoverStatus(sceneGroupId);
    }

    @Override
    public boolean handleFailover(String sceneGroupId, String failedParticipantId) {
        return memoryService.handleFailover(sceneGroupId, failedParticipantId);
    }

    @Override
    public SceneGroupDTO update(String sceneGroupId, SceneGroupConfigDTO config) {
        return memoryService.update(sceneGroupId, config);
    }

    @Override
    public boolean updateCapabilityBinding(String sceneGroupId, String bindingId, CapabilityBindingDTO binding) {
        return memoryService.updateCapabilityBinding(sceneGroupId, bindingId, binding);
    }

    @Override
    public List<SceneSnapshotDTO> listSnapshots(String sceneGroupId) {
        return memoryService.listSnapshots(sceneGroupId);
    }

    @Override
    public boolean bindKnowledgeBase(String sceneGroupId, KnowledgeBindingDTO binding) {
        return memoryService.bindKnowledgeBase(sceneGroupId, binding);
    }

    @Override
    public boolean unbindKnowledgeBase(String sceneGroupId, String kbId) {
        return memoryService.unbindKnowledgeBase(sceneGroupId, kbId);
    }
}

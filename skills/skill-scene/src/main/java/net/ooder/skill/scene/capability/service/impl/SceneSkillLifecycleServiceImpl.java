package net.ooder.skill.scene.capability.service.impl;

import net.ooder.skill.scene.capability.model.Capability;
import net.ooder.skill.scene.capability.model.CapabilityStatus;
import net.ooder.skill.scene.capability.model.CapabilityType;
import net.ooder.skill.scene.capability.model.SceneSkillCategory;
import net.ooder.skill.scene.capability.service.CapabilityService;
import net.ooder.skill.scene.capability.service.SceneSkillCategoryDetector;
import net.ooder.skill.scene.capability.service.SceneSkillLifecycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SceneSkillLifecycleServiceImpl implements SceneSkillLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(SceneSkillLifecycleServiceImpl.class);

    @Autowired
    private CapabilityService capabilityService;

    @Autowired
    private SceneSkillCategoryDetector categoryDetector;

    private Map<String, CapabilityStatus> statusMap = new ConcurrentHashMap<String, CapabilityStatus>();

    @Override
    public LifecycleResult activate(String capabilityId, String userId) {
        log.info("[activate] Activating capability: {} by user: {}", capabilityId, userId);
        
        Capability capability = capabilityService.findById(capabilityId);
        if (capability == null) {
            return LifecycleResult.failure(capabilityId, "Capability not found: " + capabilityId);
        }
        
        if (capability.getType() != CapabilityType.SCENE) {
            return LifecycleResult.failure(capabilityId, "Only scene capabilities can be activated");
        }
        
        SceneSkillCategory category = categoryDetector.detectCategory(capability);
        if (category == SceneSkillCategory.NOT_SCENE_SKILL || category == SceneSkillCategory.INVALID) {
            return LifecycleResult.failure(capabilityId, "Invalid scene skill category: " + category);
        }
        
        CapabilityStatus currentStatus = getStatus(capabilityId);
        CapabilityStatus targetStatus = determineActivateStatus(category);
        
        if (!canTransition(currentStatus, targetStatus, category)) {
            return LifecycleResult.failure(capabilityId, 
                "Cannot activate from status: " + currentStatus + " to: " + targetStatus);
        }
        
        statusMap.put(capabilityId, targetStatus);
        
        LifecycleResult result = LifecycleResult.success(capabilityId, currentStatus, targetStatus);
        result.setCategory(category);
        result.setMessage("Activated successfully");
        result.setNextSteps(getNextSteps(targetStatus, category));
        
        return result;
    }

    @Override
    public LifecycleResult pause(String capabilityId, String userId) {
        log.info("[pause] Pausing capability: {} by user: {}", capabilityId, userId);
        
        Capability capability = capabilityService.findById(capabilityId);
        if (capability == null) {
            return LifecycleResult.failure(capabilityId, "Capability not found: " + capabilityId);
        }
        
        SceneSkillCategory category = categoryDetector.detectCategory(capability);
        if (!category.supportsPause()) {
            return LifecycleResult.failure(capabilityId, "Category " + category + " does not support pause");
        }
        
        CapabilityStatus currentStatus = getStatus(capabilityId);
        if (currentStatus != CapabilityStatus.RUNNING && currentStatus != CapabilityStatus.SCHEDULED) {
            return LifecycleResult.failure(capabilityId, "Can only pause from RUNNING or SCHEDULED status");
        }
        
        statusMap.put(capabilityId, CapabilityStatus.PAUSED);
        
        LifecycleResult result = LifecycleResult.success(capabilityId, currentStatus, CapabilityStatus.PAUSED);
        result.setCategory(category);
        result.setMessage("Paused successfully");
        
        return result;
    }

    @Override
    public LifecycleResult resume(String capabilityId, String userId) {
        log.info("[resume] Resuming capability: {} by user: {}", capabilityId, userId);
        
        Capability capability = capabilityService.findById(capabilityId);
        if (capability == null) {
            return LifecycleResult.failure(capabilityId, "Capability not found: " + capabilityId);
        }
        
        SceneSkillCategory category = categoryDetector.detectCategory(capability);
        
        CapabilityStatus currentStatus = getStatus(capabilityId);
        if (currentStatus != CapabilityStatus.PAUSED) {
            return LifecycleResult.failure(capabilityId, "Can only resume from PAUSED status");
        }
        
        CapabilityStatus targetStatus = category == SceneSkillCategory.ASS 
            ? CapabilityStatus.SCHEDULED : CapabilityStatus.RUNNING;
        
        statusMap.put(capabilityId, targetStatus);
        
        LifecycleResult result = LifecycleResult.success(capabilityId, currentStatus, targetStatus);
        result.setCategory(category);
        result.setMessage("Resumed successfully");
        
        return result;
    }

    @Override
    public LifecycleResult trigger(String capabilityId, TriggerRequest request) {
        log.info("[trigger] Triggering capability: {} with action: {}", capabilityId, request.getAction());
        
        Capability capability = capabilityService.findById(capabilityId);
        if (capability == null) {
            return LifecycleResult.failure(capabilityId, "Capability not found: " + capabilityId);
        }
        
        SceneSkillCategory category = categoryDetector.detectCategory(capability);
        if (category != SceneSkillCategory.TBS) {
            return LifecycleResult.failure(capabilityId, "Only SEMI_AUTO capabilities can be manually triggered");
        }
        
        CapabilityStatus currentStatus = getStatus(capabilityId);
        if (currentStatus != CapabilityStatus.PENDING && currentStatus != CapabilityStatus.WAITING) {
            return LifecycleResult.failure(capabilityId, 
                "Can only trigger from PENDING or WAITING status, current: " + currentStatus);
        }
        
        statusMap.put(capabilityId, CapabilityStatus.RUNNING);
        
        LifecycleResult result = LifecycleResult.success(capabilityId, currentStatus, CapabilityStatus.RUNNING);
        result.setCategory(category);
        result.setMessage("Triggered successfully with action: " + request.getAction());
        
        return result;
    }

    @Override
    public LifecycleResult archive(String capabilityId, String userId) {
        log.info("[archive] Archiving capability: {} by user: {}", capabilityId, userId);
        
        Capability capability = capabilityService.findById(capabilityId);
        if (capability == null) {
            return LifecycleResult.failure(capabilityId, "Capability not found: " + capabilityId);
        }
        
        SceneSkillCategory category = categoryDetector.detectCategory(capability);
        if (!category.supportsArchive()) {
            return LifecycleResult.failure(capabilityId, "Category " + category + " does not support archive");
        }
        
        CapabilityStatus currentStatus = getStatus(capabilityId);
        if (currentStatus != CapabilityStatus.COMPLETED && currentStatus != CapabilityStatus.PAUSED) {
            return LifecycleResult.failure(capabilityId, 
                "Can only archive from COMPLETED or PAUSED status");
        }
        
        statusMap.put(capabilityId, CapabilityStatus.ARCHIVED);
        
        LifecycleResult result = LifecycleResult.success(capabilityId, currentStatus, CapabilityStatus.ARCHIVED);
        result.setCategory(category);
        result.setMessage("Archived successfully");
        
        return result;
    }

    @Override
    public LifecycleState getState(String capabilityId) {
        Capability capability = capabilityService.findById(capabilityId);
        if (capability == null) {
            return null;
        }
        
        SceneSkillCategory category = categoryDetector.detectCategory(capability);
        CapabilityStatus status = getStatus(capabilityId);
        
        LifecycleState state = new LifecycleState();
        state.setCapabilityId(capabilityId);
        state.setStatus(status);
        state.setCategory(category);
        state.setCanPause(canPause(status, category));
        state.setCanResume(canResume(status, category));
        state.setCanTrigger(canTrigger(status, category));
        state.setCanArchive(canArchive(status, category));
        state.setAvailableTransitions(getAvailableTransitions(capabilityId));
        state.setContext(new HashMap<String, Object>());
        
        return state;
    }

    @Override
    public List<LifecycleTransition> getAvailableTransitions(String capabilityId) {
        List<LifecycleTransition> transitions = new ArrayList<LifecycleTransition>();
        
        Capability capability = capabilityService.findById(capabilityId);
        if (capability == null) {
            return transitions;
        }
        
        SceneSkillCategory category = categoryDetector.detectCategory(capability);
        CapabilityStatus currentStatus = getStatus(capabilityId);
        
        if (canTransition(currentStatus, CapabilityStatus.SCHEDULED, category)) {
            transitions.add(createTransition("activate", "激活", "激活场景", CapabilityStatus.SCHEDULED));
        }
        
        if (canPause(currentStatus, category)) {
            transitions.add(createTransition("pause", "暂停", "暂停场景运行", CapabilityStatus.PAUSED));
        }
        
        if (canResume(currentStatus, category)) {
            transitions.add(createTransition("resume", "恢复", "恢复场景运行", CapabilityStatus.RUNNING));
        }
        
        if (canTrigger(currentStatus, category)) {
            transitions.add(createTransition("trigger", "触发", "手动触发场景", CapabilityStatus.RUNNING));
        }
        
        if (canArchive(currentStatus, category)) {
            transitions.add(createTransition("archive", "归档", "归档场景记录", CapabilityStatus.ARCHIVED));
        }
        
        return transitions;
    }

    private CapabilityStatus getStatus(String capabilityId) {
        CapabilityStatus status = statusMap.get(capabilityId);
        if (status == null) {
            return CapabilityStatus.DRAFT;
        }
        return status;
    }

    private CapabilityStatus determineActivateStatus(SceneSkillCategory category) {
        switch (category) {
            case ABS:
                return CapabilityStatus.SCHEDULED;
            case ASS:
                return CapabilityStatus.INITIALIZING;
            case TBS:
                return CapabilityStatus.PENDING;
            default:
                return CapabilityStatus.DRAFT;
        }
    }

    private boolean canTransition(CapabilityStatus from, CapabilityStatus to, SceneSkillCategory category) {
        if (from == to) {
            return false;
        }
        
        switch (from) {
            case DRAFT:
                return to == CapabilityStatus.PENDING 
                    || to == CapabilityStatus.SCHEDULED 
                    || to == CapabilityStatus.INITIALIZING;
            case PENDING:
                return to == CapabilityStatus.WAITING 
                    || to == CapabilityStatus.RUNNING;
            case SCHEDULED:
                return to == CapabilityStatus.RUNNING 
                    || to == CapabilityStatus.PAUSED 
                    || to == CapabilityStatus.ERROR;
            case RUNNING:
                return to == CapabilityStatus.COMPLETED 
                    || to == CapabilityStatus.PAUSED 
                    || to == CapabilityStatus.ERROR 
                    || to == CapabilityStatus.SCHEDULED;
            case PAUSED:
                return to == CapabilityStatus.RUNNING 
                    || to == CapabilityStatus.SCHEDULED 
                    || to == CapabilityStatus.ARCHIVED;
            case ERROR:
                return to == CapabilityStatus.RUNNING 
                    || to == CapabilityStatus.DRAFT 
                    || to == CapabilityStatus.ARCHIVED;
            case WAITING:
                return to == CapabilityStatus.RUNNING 
                    || to == CapabilityStatus.PAUSED 
                    || to == CapabilityStatus.COMPLETED;
            case INITIALIZING:
                return to == CapabilityStatus.SCHEDULED 
                    || to == CapabilityStatus.ERROR;
            default:
                return false;
        }
    }

    private boolean canPause(CapabilityStatus status, SceneSkillCategory category) {
        if (!category.supportsPause()) {
            return false;
        }
        return status == CapabilityStatus.RUNNING || status == CapabilityStatus.SCHEDULED;
    }

    private boolean canResume(CapabilityStatus status, SceneSkillCategory category) {
        return status == CapabilityStatus.PAUSED;
    }

    private boolean canTrigger(CapabilityStatus status, SceneSkillCategory category) {
        return category == SceneSkillCategory.TBS 
            && (status == CapabilityStatus.PENDING || status == CapabilityStatus.WAITING);
    }

    private boolean canArchive(CapabilityStatus status, SceneSkillCategory category) {
        if (!category.supportsArchive()) {
            return false;
        }
        return status == CapabilityStatus.COMPLETED || status == CapabilityStatus.PAUSED;
    }

    private LifecycleTransition createTransition(String action, String name, String description, 
            CapabilityStatus targetStatus) {
        LifecycleTransition transition = new LifecycleTransition();
        transition.setAction(action);
        transition.setName(name);
        transition.setDescription(description);
        transition.setTargetStatus(targetStatus);
        transition.setRequiresPermission(true);
        
        List<String> permissions = new ArrayList<String>();
        if ("activate".equals(action) || "archive".equals(action)) {
            permissions.add("activate");
        } else if ("pause".equals(action) || "resume".equals(action)) {
            permissions.add("configure");
        } else if ("trigger".equals(action)) {
            permissions.add("participate");
        }
        transition.setRequiredPermissions(permissions);
        
        return transition;
    }

    private List<String> getNextSteps(CapabilityStatus status, SceneSkillCategory category) {
        List<String> steps = new ArrayList<String>();
        
        switch (status) {
            case SCHEDULED:
                steps.add("等待定时触发");
                steps.add("执行能力调用链");
                break;
            case INITIALIZING:
                steps.add("初始化驱动能力");
                steps.add("初始化子能力");
                break;
            case PENDING:
                steps.add("等待人工触发");
                steps.add("或通过API触发");
                break;
            default:
                break;
        }
        
        return steps;
    }
}

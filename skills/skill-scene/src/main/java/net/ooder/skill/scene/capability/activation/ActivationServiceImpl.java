package net.ooder.skill.scene.capability.activation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActivationServiceImpl implements ActivationService {

    private static final Logger log = LoggerFactory.getLogger(ActivationServiceImpl.class);

    private Map<String, ActivationProcess> processes = new ConcurrentHashMap<String, ActivationProcess>();

    @Autowired
    private NetworkActionExecutor networkActionExecutor;

    @Override
    public ActivationProcess getProcess(String installId) {
        return processes.get(installId);
    }

    @Override
    public ActivationProcess startProcess(String installId, String activator) {
        log.info("[startProcess] Starting activation process for: {} by {}", installId, activator);
        
        ActivationProcess process = ActivationProcess.createDefault(installId);
        process.setActivator(activator);
        process.setStatus(ActivationProcess.ActivationStatus.IN_PROGRESS);
        process.setCreateTime(System.currentTimeMillis());
        process.setUpdateTime(System.currentTimeMillis());
        
        processes.put(installId, process);
        
        return process;
    }

    @Override
    public ActivationProcess executeStep(String installId, String stepId, Map<String, Object> data) {
        log.info("[executeStep] Executing step: {} for install: {}", stepId, installId);
        
        ActivationProcess process = processes.get(installId);
        if (process == null) {
            return null;
        }
        
        for (ActivationProcess.ActivationStep step : process.getSteps()) {
            if (step.getStepId().equals(stepId)) {
                step.setStatus(ActivationProcess.ActivationStep.StepStatus.COMPLETED);
                step.setEndTime(System.currentTimeMillis());
                step.setData(data);
                break;
            }
        }
        
        process.setUpdateTime(System.currentTimeMillis());
        return process;
    }

    @Override
    public KeyResult getKey(String installId) {
        log.info("[getKey] Getting key for install: {}", installId);
        
        KeyResult result = new KeyResult();
        result.setKeyId("key-" + installId);
        result.setKeyStatus("ACTIVE");
        result.setExpireTime(System.currentTimeMillis() + 86400000L);
        result.setMessage("Key generated successfully");
        
        return result;
    }

    @Override
    public ActivationProcess confirmActivation(String installId) {
        log.info("[confirmActivation] Confirming activation for: {}", installId);
        
        ActivationProcess process = processes.get(installId);
        if (process == null) {
            return null;
        }
        
        process.setStatus(ActivationProcess.ActivationStatus.COMPLETED);
        process.setUpdateTime(System.currentTimeMillis());
        
        return process;
    }

    @Override
    public ActivationProcess cancelActivation(String installId) {
        log.info("[cancelActivation] Cancelling activation for: {}", installId);
        
        ActivationProcess process = processes.get(installId);
        if (process == null) {
            return null;
        }
        
        process.setStatus(ActivationProcess.ActivationStatus.CANCELLED);
        process.setUpdateTime(System.currentTimeMillis());
        
        return process;
    }

    @Override
    public List<ActivationProcess.NetworkAction> getNetworkActions(String installId) {
        log.info("[getNetworkActions] Getting network actions for: {}", installId);
        
        ActivationProcess process = processes.get(installId);
        if (process != null) {
            return process.getNetworkActions();
        }
        
        return new ArrayList<ActivationProcess.NetworkAction>();
    }

    @Override
    public CompletableFuture<ActivationProcess> executeNetworkActions(String installId) {
        log.info("[executeNetworkActions] Executing network actions for: {}", installId);
        
        return networkActionExecutor.executeAll(installId).thenApply(statuses -> {
            ActivationProcess process = processes.get(installId);
            if (process != null) {
                for (NetworkActionExecutor.NetworkActionStatus status : statuses) {
                    for (ActivationProcess.NetworkAction action : process.getNetworkActions()) {
                        if (action.getActionId().equals(status.getAction())) {
                            if ("COMPLETED".equals(status.getStatus())) {
                                action.setStatus(ActivationProcess.NetworkAction.ActionStatus.COMPLETED);
                            } else {
                                action.setStatus(ActivationProcess.NetworkAction.ActionStatus.FAILED);
                            }
                            action.setTimestamp(status.getTimestamp());
                            break;
                        }
                    }
                }
                process.setUpdateTime(System.currentTimeMillis());
            }
            return process;
        });
    }

    @Override
    public ActivationProcess skipStep(String installId, String stepId) {
        log.info("[skipStep] Skipping step: {} for install: {}", stepId, installId);
        
        ActivationProcess process = processes.get(installId);
        if (process == null) {
            return null;
        }
        
        for (ActivationProcess.ActivationStep step : process.getSteps()) {
            if (step.getStepId().equals(stepId)) {
                step.setStatus(ActivationProcess.ActivationStep.StepStatus.SKIPPED);
                step.setEndTime(System.currentTimeMillis());
                break;
            }
        }
        
        process.setUpdateTime(System.currentTimeMillis());
        return process;
    }
}

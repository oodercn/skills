package net.ooder.mvp.api.sdk;

import net.ooder.mvp.api.circuitbreaker.CircuitBreaker;
import net.ooder.mvp.api.circuitbreaker.CircuitBreakerConfig;
import net.ooder.mvp.api.common.PageQuery;
import net.ooder.mvp.api.common.PageResult;
import net.ooder.mvp.api.common.SdkHealthStatus;
import net.ooder.mvp.api.scene.dto.*;
import net.ooder.mvp.api.exception.SdkException;
import net.ooder.mvp.api.scene.sdk.SceneSdkAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class AbstractSdkAdapter implements SceneSdkAdapter {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final CircuitBreaker circuitBreaker;
    
    public AbstractSdkAdapter() {
        CircuitBreakerConfig config = new CircuitBreakerConfig();
        config.setFailureThreshold(5);
        config.setOpenDuration(30000);
        config.setHalfOpenMaxCalls(3);
        this.circuitBreaker = CircuitBreaker.of(getClass().getSimpleName(), config);
    }
    
    public AbstractSdkAdapter(CircuitBreakerConfig config) {
        this.circuitBreaker = CircuitBreaker.of(getClass().getSimpleName(), config);
    }
    
    protected <T> T executeWithCircuitBreaker(SdkOperation<T> operation) {
        if (!circuitBreaker.allowRequest()) {
            log.warn("Circuit breaker is OPEN, request rejected");
            return null;
        }
        
        try {
            T result = operation.execute();
            circuitBreaker.recordSuccess();
            return result;
        } catch (Exception e) {
            circuitBreaker.recordFailure();
            log.error("SDK operation failed: {}", e.getMessage());
            throw new SdkException("SDK_OPERATION_FAILED", e.getMessage(), 500);
        }
    }
    
    @FunctionalInterface
    public interface SdkOperation<T> {
        T execute() throws Exception;
    }
    
    @Override
    public SdkHealthStatus healthCheck() {
        SdkHealthStatus status = new SdkHealthStatus();
        try {
            boolean available = doHealthCheck();
            status.setAvailable(available);
            status.setStatus(available ? SdkHealthStatus.STATUS_UP : SdkHealthStatus.STATUS_DOWN);
            status.setMessage(available ? "SDK is available" : "SDK is not available");
        } catch (Exception e) {
            status.setAvailable(false);
            status.setStatus(SdkHealthStatus.STATUS_DOWN);
            status.setMessage(e.getMessage());
        }
        return status;
    }
    
    protected abstract boolean doHealthCheck();
    
    @Override
    public SceneGroupDTO createSceneGroup(SceneGroupCreateRequest request) {
        return executeWithCircuitBreaker(() -> doCreateSceneGroup(request));
    }
    
    @Override
    public SceneGroupDTO getSceneGroup(String sceneGroupId) {
        return executeWithCircuitBreaker(() -> doGetSceneGroup(sceneGroupId));
    }
    
    @Override
    public PageResult<SceneGroupDTO> listSceneGroups(SceneGroupQuery query) {
        return executeWithCircuitBreaker(() -> doListSceneGroups(query));
    }
    
    @Override
    public SceneGroupDTO updateSceneGroup(String sceneGroupId, SceneGroupUpdateRequest request) {
        return executeWithCircuitBreaker(() -> doUpdateSceneGroup(sceneGroupId, request));
    }
    
    @Override
    public void deleteSceneGroup(String sceneGroupId) {
        executeWithCircuitBreaker(() -> {
            doDeleteSceneGroup(sceneGroupId);
            return null;
        });
    }
    
    @Override
    public void activateSceneGroup(String sceneGroupId) {
        executeWithCircuitBreaker(() -> {
            doActivateSceneGroup(sceneGroupId);
            return null;
        });
    }
    
    @Override
    public void deactivateSceneGroup(String sceneGroupId) {
        executeWithCircuitBreaker(() -> {
            doDeactivateSceneGroup(sceneGroupId);
            return null;
        });
    }
    
    @Override
    public void joinSceneGroup(String sceneGroupId, ParticipantJoinRequest request) {
        executeWithCircuitBreaker(() -> {
            doJoinSceneGroup(sceneGroupId, request);
            return null;
        });
    }
    
    @Override
    public void leaveSceneGroup(String sceneGroupId, String participantId) {
        executeWithCircuitBreaker(() -> {
            doLeaveSceneGroup(sceneGroupId, participantId);
            return null;
        });
    }
    
    @Override
    public PageResult<ParticipantDTO> listParticipants(String sceneGroupId, PageQuery query) {
        return executeWithCircuitBreaker(() -> doListParticipants(sceneGroupId, query));
    }
    
    @Override
    public CapabilityBindingDTO bindCapability(String sceneGroupId, CapabilityBindRequest request) {
        return executeWithCircuitBreaker(() -> doBindCapability(sceneGroupId, request));
    }
    
    @Override
    public void unbindCapability(String sceneGroupId, String bindingId) {
        executeWithCircuitBreaker(() -> {
            doUnbindCapability(sceneGroupId, bindingId);
            return null;
        });
    }
    
    @Override
    public PageResult<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId, PageQuery query) {
        return executeWithCircuitBreaker(() -> doListCapabilityBindings(sceneGroupId, query));
    }
    
    @Override
    public void bindKnowledgeBase(String sceneGroupId, KnowledgeBaseBindRequest request) {
        executeWithCircuitBreaker(() -> {
            doBindKnowledgeBase(sceneGroupId, request);
            return null;
        });
    }
    
    @Override
    public void unbindKnowledgeBase(String sceneGroupId, String kbId) {
        executeWithCircuitBreaker(() -> {
            doUnbindKnowledgeBase(sceneGroupId, kbId);
            return null;
        });
    }
    
    @Override
    public List<KnowledgeBaseBindingDTO> listKnowledgeBaseBindings(String sceneGroupId) {
        return executeWithCircuitBreaker(() -> doListKnowledgeBaseBindings(sceneGroupId));
    }
    
    @Override
    public Object invokeCapability(String skillId, Map<String, Object> params) {
        return executeWithCircuitBreaker(() -> doInvokeCapability(skillId, params));
    }
    
    protected abstract SceneGroupDTO doCreateSceneGroup(SceneGroupCreateRequest request);
    protected abstract SceneGroupDTO doGetSceneGroup(String sceneGroupId);
    protected abstract PageResult<SceneGroupDTO> doListSceneGroups(SceneGroupQuery query);
    protected abstract SceneGroupDTO doUpdateSceneGroup(String sceneGroupId, SceneGroupUpdateRequest request);
    protected abstract void doDeleteSceneGroup(String sceneGroupId);
    protected abstract void doActivateSceneGroup(String sceneGroupId);
    protected abstract void doDeactivateSceneGroup(String sceneGroupId);
    protected abstract void doJoinSceneGroup(String sceneGroupId, ParticipantJoinRequest request);
    protected abstract void doLeaveSceneGroup(String sceneGroupId, String participantId);
    protected abstract PageResult<ParticipantDTO> doListParticipants(String sceneGroupId, PageQuery query);
    protected abstract CapabilityBindingDTO doBindCapability(String sceneGroupId, CapabilityBindRequest request);
    protected abstract void doUnbindCapability(String sceneGroupId, String bindingId);
    protected abstract PageResult<CapabilityBindingDTO> doListCapabilityBindings(String sceneGroupId, PageQuery query);
    protected abstract void doBindKnowledgeBase(String sceneGroupId, KnowledgeBaseBindRequest request);
    protected abstract void doUnbindKnowledgeBase(String sceneGroupId, String kbId);
    protected abstract List<KnowledgeBaseBindingDTO> doListKnowledgeBaseBindings(String sceneGroupId);
    protected abstract Object doInvokeCapability(String skillId, Map<String, Object> params);
}

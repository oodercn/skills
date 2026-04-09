package net.ooder.bpm.designer.websocket.service;

import net.ooder.bpm.designer.websocket.DerivationWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DerivationProgressService {
    
    private static final Logger log = LoggerFactory.getLogger(DerivationProgressService.class);
    
    private final DerivationWebSocketHandler webSocketHandler;
    
    @Autowired
    public DerivationProgressService(DerivationWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }
    
    public String startDerivation(String sessionId) {
        String derivationId = UUID.randomUUID().toString();
        webSocketHandler.sendProgress(sessionId, derivationId, 0, "init", "开始推导...");
        return derivationId;
    }
    
    public void reportProgress(String sessionId, String derivationId, int progress, String stage, String message) {
        log.debug("Derivation progress: {} - {}% - {} - {}", derivationId, progress, stage, message);
        webSocketHandler.sendProgress(sessionId, derivationId, progress, stage, message);
    }
    
    public void reportPerformerDerivationStart(String sessionId, String derivationId) {
        reportProgress(sessionId, derivationId, 10, "performer", "开始办理人推导...");
    }
    
    public void reportPerformerDerivationProgress(String sessionId, String derivationId, int subProgress, String detail) {
        int totalProgress = 10 + (subProgress * 20 / 100);
        reportProgress(sessionId, derivationId, totalProgress, "performer", detail);
    }
    
    public void reportPerformerDerivationComplete(String sessionId, String derivationId, Object result) {
        reportProgress(sessionId, derivationId, 30, "performer", "办理人推导完成");
        webSocketHandler.sendResult(sessionId, derivationId, "performer", result);
    }
    
    public void reportCapabilityMatchingStart(String sessionId, String derivationId) {
        reportProgress(sessionId, derivationId, 35, "capability", "开始能力匹配...");
    }
    
    public void reportCapabilityMatchingProgress(String sessionId, String derivationId, int subProgress, String detail) {
        int totalProgress = 35 + (subProgress * 20 / 100);
        reportProgress(sessionId, derivationId, totalProgress, "capability", detail);
    }
    
    public void reportCapabilityMatchingComplete(String sessionId, String derivationId, Object result) {
        reportProgress(sessionId, derivationId, 55, "capability", "能力匹配完成");
        webSocketHandler.sendResult(sessionId, derivationId, "capability", result);
    }
    
    public void reportFormMatchingStart(String sessionId, String derivationId) {
        reportProgress(sessionId, derivationId, 60, "form", "开始表单匹配...");
    }
    
    public void reportFormMatchingProgress(String sessionId, String derivationId, int subProgress, String detail) {
        int totalProgress = 60 + (subProgress * 20 / 100);
        reportProgress(sessionId, derivationId, totalProgress, "form", detail);
    }
    
    public void reportFormMatchingComplete(String sessionId, String derivationId, Object result) {
        reportProgress(sessionId, derivationId, 80, "form", "表单匹配完成");
        webSocketHandler.sendResult(sessionId, derivationId, "form", result);
    }
    
    public void reportAggregationStart(String sessionId, String derivationId) {
        reportProgress(sessionId, derivationId, 85, "aggregation", "聚合推导结果...");
    }
    
    public void reportComplete(String sessionId, String derivationId, Object result) {
        reportProgress(sessionId, derivationId, 100, "complete", "推导完成");
        webSocketHandler.sendResult(sessionId, derivationId, "full", result);
    }
    
    public void reportError(String sessionId, String derivationId, String error) {
        log.error("Derivation error: {} - {}", derivationId, error);
        webSocketHandler.sendError(sessionId, derivationId, error);
    }
}

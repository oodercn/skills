package net.ooder.skill.protocol.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import net.ooder.skill.protocol.router.AiBridgeProtocolRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AiBridgeProtocolDispatcher {
    
    private static final Logger logger = LoggerFactory.getLogger(AiBridgeProtocolDispatcher.class);
    
    @Autowired
    private AiBridgeProtocolRouter router;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    public AiBridgeMessage dispatch(String json) {
        try {
            AiBridgeMessage message = objectMapper.readValue(json, AiBridgeMessage.class);
            return dispatch(message);
        } catch (Exception e) {
            logger.error("Failed to parse message: " + json, e);
            return createErrorResponse(null, 400, "Invalid message format: " + e.getMessage());
        }
    }
    
    public AiBridgeMessage dispatch(AiBridgeMessage message) {
        logger.debug("Dispatching message: id={}, command={}", 
            message.getId(), message.getCommand());
        
        return router.route(message);
    }
    
    public CompletableFuture<AiBridgeMessage> dispatchAsync(AiBridgeMessage message) {
        return CompletableFuture.supplyAsync(() -> dispatch(message), executorService);
    }
    
    public CompletableFuture<AiBridgeMessage> dispatchAsync(String json) {
        return CompletableFuture.supplyAsync(() -> dispatch(json), executorService);
    }
    
    public String dispatchAndSerialize(String json) {
        AiBridgeMessage response = dispatch(json);
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            logger.error("Failed to serialize response", e);
            return "{\"status\":\"error\",\"error\":{\"code\":500,\"message\":\"Failed to serialize response\"}}";
        }
    }
    
    public String dispatchAndSerialize(AiBridgeMessage message) {
        AiBridgeMessage response = dispatch(message);
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            logger.error("Failed to serialize response", e);
            return "{\"status\":\"error\",\"error\":{\"code\":500,\"message\":\"Failed to serialize response\"}}";
        }
    }
    
    private AiBridgeMessage createErrorResponse(AiBridgeMessage request, int code, String message) {
        AiBridgeMessage response = new AiBridgeMessage();
        response.setId(java.util.UUID.randomUUID().toString());
        response.setTimestamp(System.currentTimeMillis());
        response.setStatus("error");
        
        if (request != null) {
            response.setResponseTo(request.getId());
            response.setCommand(request.getCommand());
        }
        
        net.ooder.skill.protocol.model.ErrorInfo error = new net.ooder.skill.protocol.model.ErrorInfo();
        error.setCode(code);
        error.setMessage(message);
        response.setError(error);
        
        return response;
    }
}

package net.ooder.skill.protocol.handler.south;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.skill.protocol.model.south.SouthMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SouthProtocolHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(SouthProtocolHandler.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public SouthMessage handle(String json) {
        try {
            SouthMessage message = objectMapper.readValue(json, SouthMessage.class);
            return handle(message);
        } catch (Exception e) {
            logger.error("Failed to parse south message: " + json, e);
            return createErrorResponse("PARSE_ERROR", "Invalid message format: " + e.getMessage());
        }
    }
    
    public SouthMessage handle(SouthMessage message) {
        if (message == null) {
            return createErrorResponse("NULL_MESSAGE", "Message is null");
        }
        
        String type = message.getType();
        if (type == null || type.isEmpty()) {
            return createErrorResponse("MISSING_TYPE", "Message type is missing");
        }
        
        logger.debug("Handling south message: type={}, target={}", 
            type, message.getTargetAgentId());
        
        switch (type) {
            case "REGISTER_RESPONSE":
                return handleRegisterResponse(message);
            case "HEARTBEAT_RESPONSE":
                return handleHeartbeatResponse(message);
            case "COMMAND":
                return handleCommand(message);
            case "SCENE_CREATE":
                return handleSceneCreate(message);
            case "CONFIG_UPDATE":
                return handleConfigUpdate(message);
            default:
                return createErrorResponse("UNKNOWN_TYPE", "Unknown message type: " + type);
        }
    }
    
    private SouthMessage handleRegisterResponse(SouthMessage message) {
        logger.info("Handling register response for agent: {}", message.getTargetAgentId());
        return createSuccessResponse(message, "Register response processed");
    }
    
    private SouthMessage handleHeartbeatResponse(SouthMessage message) {
        logger.debug("Handling heartbeat response for agent: {}", message.getTargetAgentId());
        return createSuccessResponse(message, "Heartbeat response received");
    }
    
    private SouthMessage handleCommand(SouthMessage message) {
        logger.info("Handling command for agent: {}", message.getTargetAgentId());
        return createSuccessResponse(message, "Command processed");
    }
    
    private SouthMessage handleSceneCreate(SouthMessage message) {
        logger.info("Handling scene create for agent: {}", message.getTargetAgentId());
        return createSuccessResponse(message, "Scene create processed");
    }
    
    private SouthMessage handleConfigUpdate(SouthMessage message) {
        logger.info("Handling config update for agent: {}", message.getTargetAgentId());
        return createSuccessResponse(message, "Config update processed");
    }
    
    private SouthMessage createSuccessResponse(SouthMessage request, String message) {
        SouthMessage response = new SouthMessage();
        response.setType(request.getType() + "_ACK");
        response.setTimestamp(System.currentTimeMillis());
        response.setSourceAgentId(request.getTargetAgentId());
        response.setTargetAgentId(request.getSourceAgentId());
        
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("success", true);
        data.put("message", message);
        response.setData(data);
        
        return response;
    }
    
    private SouthMessage createErrorResponse(String errorCode, String errorMessage) {
        SouthMessage response = new SouthMessage();
        response.setType("ERROR_RESPONSE");
        response.setTimestamp(System.currentTimeMillis());
        
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("success", false);
        data.put("error_code", errorCode);
        data.put("error_message", errorMessage);
        response.setData(data);
        
        return response;
    }
}

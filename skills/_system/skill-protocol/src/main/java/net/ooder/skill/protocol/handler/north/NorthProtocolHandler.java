package net.ooder.skill.protocol.handler.north;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.skill.protocol.model.north.NorthMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NorthProtocolHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(NorthProtocolHandler.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public NorthMessage handle(String json) {
        try {
            NorthMessage message = objectMapper.readValue(json, NorthMessage.class);
            return handle(message);
        } catch (Exception e) {
            logger.error("Failed to parse north message: " + json, e);
            return createErrorResponse("PARSE_ERROR", "Invalid message format: " + e.getMessage());
        }
    }
    
    public NorthMessage handle(NorthMessage message) {
        if (message == null) {
            return createErrorResponse("NULL_MESSAGE", "Message is null");
        }
        
        String type = message.getType();
        if (type == null || type.isEmpty()) {
            return createErrorResponse("MISSING_TYPE", "Message type is missing");
        }
        
        logger.debug("Handling north message: type={}, source={}", 
            type, message.getSourceAgentId());
        
        switch (type) {
            case "REGISTER_REQUEST":
                return handleRegisterRequest(message);
            case "HEARTBEAT_REQUEST":
                return handleHeartbeatRequest(message);
            case "SKILL_INVOKE_REQUEST":
                return handleSkillInvokeRequest(message);
            case "SCENE_JOIN_REQUEST":
                return handleSceneJoinRequest(message);
            case "STATUS_REPORT":
                return handleStatusReport(message);
            default:
                return createErrorResponse("UNKNOWN_TYPE", "Unknown message type: " + type);
        }
    }
    
    private NorthMessage handleRegisterRequest(NorthMessage message) {
        logger.info("Handling register request from agent: {}", message.getSourceAgentId());
        return createSuccessResponse(message, "Register request processed");
    }
    
    private NorthMessage handleHeartbeatRequest(NorthMessage message) {
        logger.debug("Handling heartbeat request from agent: {}", message.getSourceAgentId());
        return createSuccessResponse(message, "Heartbeat received");
    }
    
    private NorthMessage handleSkillInvokeRequest(NorthMessage message) {
        logger.info("Handling skill invoke request from agent: {}", message.getSourceAgentId());
        return createSuccessResponse(message, "Skill invoke request processed");
    }
    
    private NorthMessage handleSceneJoinRequest(NorthMessage message) {
        logger.info("Handling scene join request from agent: {}", message.getSourceAgentId());
        return createSuccessResponse(message, "Scene join request processed");
    }
    
    private NorthMessage handleStatusReport(NorthMessage message) {
        logger.debug("Handling status report from agent: {}", message.getSourceAgentId());
        return createSuccessResponse(message, "Status report received");
    }
    
    private NorthMessage createSuccessResponse(NorthMessage request, String message) {
        NorthMessage response = new NorthMessage();
        response.setType(request.getType() + "_RESPONSE");
        response.setTimestamp(System.currentTimeMillis());
        response.setSourceAgentId(request.getTargetAgentId());
        response.setTargetAgentId(request.getSourceAgentId());
        
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("success", true);
        data.put("message", message);
        response.setData(data);
        
        return response;
    }
    
    private NorthMessage createErrorResponse(String errorCode, String errorMessage) {
        NorthMessage response = new NorthMessage();
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

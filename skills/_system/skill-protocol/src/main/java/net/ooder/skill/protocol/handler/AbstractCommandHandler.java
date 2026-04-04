package net.ooder.skill.protocol.handler;

import net.ooder.skill.protocol.builder.AiBridgeMessageBuilder;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import net.ooder.skill.protocol.model.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCommandHandler implements CommandHandler {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Override
    public AiBridgeMessage handle(AiBridgeMessage message) {
        try {
            logger.debug("Handling command: {} with message id: {}", getCommand(), message.getId());
            AiBridgeMessage response = doHandle(message);
            logger.debug("Command {} handled successfully", getCommand());
            return response;
        } catch (Exception e) {
            logger.error("Error handling command: " + getCommand(), e);
            return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, "Internal error: " + e.getMessage());
        }
    }
    
    protected abstract AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception;
    
    protected AiBridgeMessage buildSuccessResponse(AiBridgeMessage request, Object result) {
        return AiBridgeMessageBuilder.successResponse(request, result);
    }
    
    protected AiBridgeMessage buildErrorResponse(AiBridgeMessage request, int code, String message) {
        return AiBridgeMessageBuilder.errorResponse(request, code, message);
    }
    
    protected AiBridgeMessage buildErrorResponse(AiBridgeMessage request, int code, String message, String details) {
        return AiBridgeMessageBuilder.errorResponse(request, code, message, details);
    }
    
    protected Object getParam(AiBridgeMessage message, String key) {
        if (message.getParams() == null) {
            return null;
        }
        return message.getParams().get(key);
    }
    
    protected String getParamAsString(AiBridgeMessage message, String key) {
        Object value = getParam(message, key);
        return value != null ? value.toString() : null;
    }
    
    protected Integer getParamAsInteger(AiBridgeMessage message, String key) {
        Object value = getParam(message, key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    protected Boolean getParamAsBoolean(AiBridgeMessage message, String key) {
        Object value = getParam(message, key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }
}

package net.ooder.skill.protocol.router;

import net.ooder.skill.protocol.handler.CommandHandler;
import net.ooder.skill.protocol.registry.CommandHandlerRegistry;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AiBridgeProtocolRouter {
    
    private static final Logger logger = LoggerFactory.getLogger(AiBridgeProtocolRouter.class);
    
    @Autowired
    private CommandHandlerRegistry handlerRegistry;
    
    public AiBridgeMessage route(AiBridgeMessage message) {
        if (message == null) {
            logger.error("Received null message");
            return createErrorResponse(null, 400, "Message is null");
        }
        
        String command = message.getCommand();
        if (command == null || command.isEmpty()) {
            logger.error("Message missing command field");
            return createErrorResponse(message, 400, "Missing command field");
        }
        
        logger.debug("Routing message: command={}, id={}", command, message.getId());
        
        CommandHandler handler = handlerRegistry.getHandler(command);
        if (handler == null) {
            logger.warn("No handler found for command: {}", command);
            return createErrorResponse(message, 400, "Unknown command: " + command);
        }
        
        try {
            return handler.handle(message);
        } catch (Exception e) {
            logger.error("Error handling command: " + command, e);
            return createErrorResponse(message, 500, "Internal error: " + e.getMessage());
        }
    }
    
    public boolean canRoute(String command) {
        return handlerRegistry.hasHandler(command);
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

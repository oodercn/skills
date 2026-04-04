package net.ooder.skill.protocol.registry;

import net.ooder.skill.protocol.builder.AiBridgeMessageBuilder;
import net.ooder.skill.protocol.handler.CommandHandler;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandHandlerRegistry {
    
    private final Map<String, CommandHandler> handlers = new HashMap<>();
    
    public CommandHandlerRegistry(List<CommandHandler> commandHandlers) {
        if (commandHandlers != null) {
            for (CommandHandler handler : commandHandlers) {
                registerHandler(handler);
            }
        }
    }
    
    public void registerHandler(CommandHandler handler) {
        handlers.put(handler.getCommand(), handler);
    }
    
    public CommandHandler getHandler(String command) {
        return handlers.get(command);
    }
    
    public boolean hasHandler(String command) {
        return handlers.containsKey(command);
    }
    
    public AiBridgeMessage handle(AiBridgeMessage message) {
        String command = message.getCommand();
        CommandHandler handler = getHandler(command);
        
        if (handler == null) {
            return AiBridgeMessageBuilder.errorResponse(
                message,
                400,
                "Unknown command: " + command
            );
        }
        
        return handler.handle(message);
    }
}

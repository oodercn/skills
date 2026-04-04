package net.ooder.skill.protocol.handler;

import net.ooder.skill.protocol.model.AiBridgeMessage;

public interface CommandHandler {
    
    String getCommand();
    
    AiBridgeMessage handle(AiBridgeMessage message);
    
    default boolean supports(String command) {
        return getCommand().equals(command);
    }
}

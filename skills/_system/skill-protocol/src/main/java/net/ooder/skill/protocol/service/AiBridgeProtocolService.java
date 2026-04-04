package net.ooder.skill.protocol.service;

import net.ooder.skill.protocol.dispatcher.AiBridgeProtocolDispatcher;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiBridgeProtocolService {
    
    @Autowired
    private AiBridgeProtocolDispatcher dispatcher;
    
    public AiBridgeMessage sendMessage(AiBridgeMessage message) {
        return dispatcher.dispatch(message);
    }
    
    public AiBridgeMessage sendMessage(String json) {
        return dispatcher.dispatch(json);
    }
    
    public String sendMessageAndSerialize(AiBridgeMessage message) {
        return dispatcher.dispatchAndSerialize(message);
    }
    
    public String sendMessageAndSerialize(String json) {
        return dispatcher.dispatchAndSerialize(json);
    }
}

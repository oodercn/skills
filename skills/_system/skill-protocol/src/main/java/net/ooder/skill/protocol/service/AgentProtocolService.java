package net.ooder.skill.protocol.service;

import net.ooder.skill.protocol.handler.north.NorthProtocolHandler;
import net.ooder.skill.protocol.handler.south.SouthProtocolHandler;
import net.ooder.skill.protocol.model.north.NorthMessage;
import net.ooder.skill.protocol.model.south.SouthMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentProtocolService {
    
    @Autowired
    private NorthProtocolHandler northProtocolHandler;
    
    @Autowired
    private SouthProtocolHandler southProtocolHandler;
    
    public NorthMessage handleNorthMessage(String json) {
        return northProtocolHandler.handle(json);
    }
    
    public NorthMessage handleNorthMessage(NorthMessage message) {
        return northProtocolHandler.handle(message);
    }
    
    public SouthMessage handleSouthMessage(String json) {
        return southProtocolHandler.handle(json);
    }
    
    public SouthMessage handleSouthMessage(SouthMessage message) {
        return southProtocolHandler.handle(message);
    }
    
    public String handleNorthMessageAndSerialize(String json) {
        NorthMessage response = handleNorthMessage(json);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"type\":\"ERROR_RESPONSE\",\"data\":{\"success\":false,\"error_message\":\"Failed to serialize response\"}}";
        }
    }
    
    public String handleSouthMessageAndSerialize(String json) {
        SouthMessage response = handleSouthMessage(json);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"type\":\"ERROR_RESPONSE\",\"data\":{\"success\":false,\"error_message\":\"Failed to serialize response\"}}";
        }
    }
}

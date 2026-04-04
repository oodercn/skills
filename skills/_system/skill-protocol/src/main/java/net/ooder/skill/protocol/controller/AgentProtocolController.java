package net.ooder.skill.protocol.controller;

import net.ooder.skill.protocol.model.north.NorthMessage;
import net.ooder.skill.protocol.model.south.SouthMessage;
import net.ooder.skill.protocol.service.AgentProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/protocol/agent")
public class AgentProtocolController {
    
    @Autowired
    private AgentProtocolService agentProtocolService;
    
    @PostMapping("/north")
    public ResponseEntity<NorthMessage> handleNorthMessage(@RequestBody NorthMessage message) {
        NorthMessage response = agentProtocolService.handleNorthMessage(message);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/north/json")
    public ResponseEntity<String> handleNorthMessageJson(@RequestBody String json) {
        String response = agentProtocolService.handleNorthMessageAndSerialize(json);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/south")
    public ResponseEntity<SouthMessage> handleSouthMessage(@RequestBody SouthMessage message) {
        SouthMessage response = agentProtocolService.handleSouthMessage(message);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/south/json")
    public ResponseEntity<String> handleSouthMessageJson(@RequestBody String json) {
        String response = agentProtocolService.handleSouthMessageAndSerialize(json);
        return ResponseEntity.ok(response);
    }
}

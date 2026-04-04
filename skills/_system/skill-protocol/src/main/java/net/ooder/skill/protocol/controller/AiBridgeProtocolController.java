package net.ooder.skill.protocol.controller;

import net.ooder.skill.protocol.dispatcher.AiBridgeProtocolDispatcher;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/protocol/aibridge")
public class AiBridgeProtocolController {
    
    @Autowired
    private AiBridgeProtocolDispatcher dispatcher;
    
    @PostMapping("/message")
    public ResponseEntity<AiBridgeMessage> handleMessage(@RequestBody AiBridgeMessage message) {
        AiBridgeMessage response = dispatcher.dispatch(message);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/message/json")
    public ResponseEntity<String> handleMessageJson(@RequestBody String json) {
        String response = dispatcher.dispatchAndSerialize(json);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/message/async")
    public CompletableFuture<ResponseEntity<AiBridgeMessage>> handleMessageAsync(
            @RequestBody AiBridgeMessage message) {
        return dispatcher.dispatchAsync(message)
                .thenApply(ResponseEntity::ok);
    }
    
    @PostMapping("/batch")
    public ResponseEntity<Map<String, AiBridgeMessage>> handleBatch(
            @RequestBody Map<String, AiBridgeMessage> messages) {
        Map<String, AiBridgeMessage> responses = new java.util.HashMap<>();
        
        for (Map.Entry<String, AiBridgeMessage> entry : messages.entrySet()) {
            AiBridgeMessage response = dispatcher.dispatch(entry.getValue());
            responses.put(entry.getKey(), response);
        }
        
        return ResponseEntity.ok(responses);
    }
}

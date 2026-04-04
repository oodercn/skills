package net.ooder.skill.protocol.handler.batch;

import net.ooder.skill.protocol.dispatcher.AiBridgeProtocolDispatcher;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BatchExecuteCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private AiBridgeProtocolDispatcher dispatcher;
    
    @Override
    public String getCommand() {
        return "batch.execute";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        Object commandsObj = getParam(message, "commands");
        
        if (commandsObj == null) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: commands");
        }
        
        List<Map<String, Object>> commands;
        try {
            if (commandsObj instanceof List) {
                commands = (List<Map<String, Object>>) commandsObj;
            } else {
                return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                    "Invalid parameter type: commands must be an array");
            }
        } catch (ClassCastException e) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Invalid parameter type: commands must be an array of objects");
        }
        
        if (commands.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Commands array is empty");
        }
        
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        for (int i = 0; i < commands.size(); i++) {
            Map<String, Object> commandMap = commands.get(i);
            
            try {
                AiBridgeMessage commandMessage = new AiBridgeMessage();
                commandMessage.setId(java.util.UUID.randomUUID().toString());
                commandMessage.setTimestamp(System.currentTimeMillis());
                
                if (commandMap.containsKey("command")) {
                    commandMessage.setCommand((String) commandMap.get("command"));
                }
                
                if (commandMap.containsKey("params")) {
                    commandMessage.setParams((Map<String, Object>) commandMap.get("params"));
                }
                
                if (commandMap.containsKey("source")) {
                    commandMessage.setSource((String) commandMap.get("source"));
                }
                
                if (commandMap.containsKey("target")) {
                    commandMessage.setTarget((String) commandMap.get("target"));
                }
                
                AiBridgeMessage response = dispatcher.dispatch(commandMessage);
                
                Map<String, Object> result = new HashMap<>();
                result.put("index", i);
                result.put("command", commandMessage.getCommand());
                result.put("status", response.getStatus());
                result.put("result", response.getResult());
                
                if (response.getError() != null) {
                    result.put("error", Map.of(
                        "code", response.getError().getCode(),
                        "message", response.getError().getMessage()
                    ));
                    failureCount++;
                } else {
                    successCount++;
                }
                
                results.add(result);
                
            } catch (Exception e) {
                Map<String, Object> result = new HashMap<>();
                result.put("index", i);
                result.put("status", "error");
                result.put("error", Map.of(
                    "code", 500,
                    "message", "Failed to execute command: " + e.getMessage()
                ));
                results.add(result);
                failureCount++;
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", commands.size());
        response.put("success_count", successCount);
        response.put("failure_count", failureCount);
        response.put("results", results);
        
        return buildSuccessResponse(message, response);
    }
}

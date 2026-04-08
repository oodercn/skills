package net.ooder.bpm.designer.function.impl;

import net.ooder.bpm.designer.function.DesignerFunctionDefinition;
import net.ooder.bpm.designer.function.DesignerFunctionRegistry;
import net.ooder.bpm.designer.function.FunctionCallRequest;
import net.ooder.bpm.designer.model.dto.FunctionCallTraceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DesignerFunctionRegistryImpl implements DesignerFunctionRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(DesignerFunctionRegistryImpl.class);
    
    private final Map<String, DesignerFunctionDefinition> functionRegistry = new ConcurrentHashMap<>();
    
    @Override
    public void registerFunction(DesignerFunctionDefinition function) {
        if (function == null || function.getName() == null) {
            throw new IllegalArgumentException("Function and function name must not be null");
        }
        functionRegistry.put(function.getName(), function);
        log.info("Registered function: {} [{}]", function.getName(), function.getCategory());
    }
    
    @Override
    public void registerFunctions(List<DesignerFunctionDefinition> functions) {
        if (functions != null) {
            functions.forEach(this::registerFunction);
        }
    }
    
    @Override
    public void unregisterFunction(String functionName) {
        if (functionName != null) {
            functionRegistry.remove(functionName);
            log.info("Unregistered function: {}", functionName);
        }
    }
    
    @Override
    public DesignerFunctionDefinition getFunction(String functionName) {
        return functionRegistry.get(functionName);
    }
    
    @Override
    public List<DesignerFunctionDefinition> getAllFunctions() {
        return new ArrayList<>(functionRegistry.values());
    }
    
    @Override
    public List<DesignerFunctionDefinition> getFunctionsByCategory(DesignerFunctionDefinition.FunctionCategory category) {
        return functionRegistry.values().stream()
                .filter(f -> f.getCategory() == category)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getOpenAISchemas() {
        return functionRegistry.values().stream()
                .map(DesignerFunctionDefinition::toOpenAISchema)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getOpenAISchemasByCategory(DesignerFunctionDefinition.FunctionCategory category) {
        return functionRegistry.values().stream()
                .filter(f -> f.getCategory() == category)
                .map(DesignerFunctionDefinition::toOpenAISchema)
                .collect(Collectors.toList());
    }
    
    @Override
    public Object executeFunction(String functionName, Map<String, Object> arguments) {
        log.info("Executing function: {} with arguments: {}", functionName, arguments);
        
        DesignerFunctionDefinition function = functionRegistry.get(functionName);
        if (function == null) {
            throw new IllegalArgumentException("Function not found: " + functionName);
        }
        
        if (function.getHandler() == null) {
            throw new IllegalStateException("Function has no handler: " + functionName);
        }
        
        try {
            Object result = function.getHandler().apply(arguments);
            log.info("Function {} executed successfully", functionName);
            return result;
        } catch (Exception e) {
            log.error("Error executing function {}: {}", functionName, e.getMessage(), e);
            throw new RuntimeException("Function execution failed: " + functionName, e);
        }
    }
    
    @Override
    public List<FunctionCallTraceDTO> executeFunctionCalls(List<FunctionCallRequest> calls) {
        List<FunctionCallTraceDTO> traces = new ArrayList<>();
        
        if (calls == null || calls.isEmpty()) {
            return traces;
        }
        
        int sequence = 1;
        for (FunctionCallRequest call : calls) {
            long startTime = System.currentTimeMillis();
            
            try {
                Object result = executeFunction(call.getName(), call.getArguments());
                long executionTime = System.currentTimeMillis() - startTime;
                
                traces.add(FunctionCallTraceDTO.success(
                    sequence++, 
                    call.getName(), 
                    call.getArguments(), 
                    result, 
                    executionTime
                ));
            } catch (Exception e) {
                long executionTime = System.currentTimeMillis() - startTime;
                
                traces.add(FunctionCallTraceDTO.failure(
                    sequence++, 
                    call.getName(), 
                    call.getArguments(), 
                    e.getMessage(), 
                    executionTime
                ));
            }
        }
        
        return traces;
    }
    
    @Override
    public boolean hasFunction(String functionName) {
        return functionRegistry.containsKey(functionName);
    }
    
    @Override
    public int getFunctionCount() {
        return functionRegistry.size();
    }
}

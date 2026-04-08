package net.ooder.bpm.designer.function;

import net.ooder.bpm.designer.model.dto.FunctionCallTraceDTO;

import java.util.List;
import java.util.Map;

public interface DesignerFunctionRegistry {
    
    void registerFunction(DesignerFunctionDefinition function);
    
    void registerFunctions(List<DesignerFunctionDefinition> functions);
    
    void unregisterFunction(String functionName);
    
    DesignerFunctionDefinition getFunction(String functionName);
    
    List<DesignerFunctionDefinition> getAllFunctions();
    
    List<DesignerFunctionDefinition> getFunctionsByCategory(DesignerFunctionDefinition.FunctionCategory category);
    
    List<Map<String, Object>> getOpenAISchemas();
    
    List<Map<String, Object>> getOpenAISchemasByCategory(DesignerFunctionDefinition.FunctionCategory category);
    
    Object executeFunction(String functionName, Map<String, Object> arguments);
    
    List<FunctionCallTraceDTO> executeFunctionCalls(List<FunctionCallRequest> calls);
    
    boolean hasFunction(String functionName);
    
    int getFunctionCount();
}

package net.ooder.bpm.designer.llm;

import java.util.Map;

public class FunctionCall {
    
    private String name;
    private Map<String, Object> arguments;
    private String id;
    
    public FunctionCall() {}
    
    public FunctionCall(String name, Map<String, Object> arguments) {
        this.name = name;
        this.arguments = arguments;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, Object> getArguments() { return arguments; }
    public void setArguments(Map<String, Object> arguments) { this.arguments = arguments; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}

package net.ooder.skill.llm.base;

public interface LlmStreamCallback {
    
    void onToken(String token);
    
    void onComplete(LlmResponse response);
    
    void onError(Throwable error);
}

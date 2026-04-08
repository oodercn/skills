package net.ooder.skill.messaging.dto;

public class RefreshTokenRequest {
    
    private String currentToken;

    public String getCurrentToken() { return currentToken; }
    public void setCurrentToken(String currentToken) { this.currentToken = currentToken; }
}

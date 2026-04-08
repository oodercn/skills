package net.ooder.skill.key.dto;

public class KeyAccessResultDTO {
    
    private boolean allowed;
    private String reason;
    private KeyDTO keyInfo;

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public KeyDTO getKeyInfo() { return keyInfo; }
    public void setKeyInfo(KeyDTO keyInfo) { this.keyInfo = keyInfo; }
}

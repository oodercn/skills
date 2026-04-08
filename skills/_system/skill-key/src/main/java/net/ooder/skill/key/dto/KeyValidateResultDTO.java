package net.ooder.skill.key.dto;

public class KeyValidateResultDTO {
    
    private String keyId;
    private boolean valid;
    private String reason;

    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

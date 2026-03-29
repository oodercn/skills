package net.ooder.skill.org.dingding.dto;

import java.io.Serializable;

public class QrCodeDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sessionId;
    private String qrCodeUrl;
    private String qrCodeData;
    private long expireTime;
    private int expireSeconds;
    private String platform;
    
    public QrCodeDTO() {}
    
    public QrCodeDTO(String sessionId, String qrCodeUrl, int expireSeconds) {
        this.sessionId = sessionId;
        this.qrCodeUrl = qrCodeUrl;
        this.expireSeconds = expireSeconds;
        this.expireTime = System.currentTimeMillis() + expireSeconds * 1000L;
        this.platform = "dingtalk";
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }
    
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
    
    public String getQrCodeData() {
        return qrCodeData;
    }
    
    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
    
    public int getExpireSeconds() {
        return expireSeconds;
    }
    
    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
}

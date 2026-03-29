package net.ooder.skill.org.wecom.dto;

import java.io.Serializable;

public class AuthTokenDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String accessToken;
    private String refreshToken;
    private long expireTime;
    private int expiresIn;
    private String userId;
    private String userName;
    private String deviceId;
    private String openUserId;
    private String platform;
    
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
    public int getExpiresIn() { return expiresIn; }
    public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; this.expireTime = System.currentTimeMillis() + expiresIn * 1000L; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getOpenUserId() { return openUserId; }
    public void setOpenUserId(String openUserId) { this.openUserId = openUserId; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public boolean isExpired() { return System.currentTimeMillis() > expireTime; }
}

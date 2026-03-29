package net.ooder.skill.org.dingding.service;

import net.ooder.skill.org.dingding.client.DingdingApiClient;
import net.ooder.skill.org.dingding.config.DingdingConfig;
import net.ooder.skill.org.dingding.dto.AuthTokenDTO;
import net.ooder.skill.org.dingding.dto.QrCodeDTO;
import net.ooder.skill.org.dingding.model.DingdingUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DingTalkAuthService {
    
    private static final Logger log = LoggerFactory.getLogger(DingTalkAuthService.class);
    
    @Autowired
    private DingdingConfig config;
    
    @Autowired
    private DingdingApiClient apiClient;
    
    private Map<String, QrCodeSession> qrCodeSessions = new ConcurrentHashMap<>();
    private Map<String, AuthTokenDTO> tokenStore = new ConcurrentHashMap<>();
    
    public QrCodeDTO generateQrCode() {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        String qrCodeUrl = String.format(
            "https://login.dingtalk.com/oauth2/auth?redirect_uri=%s&response_type=code&client_id=%s&scope=openid&state=%s&prompt=consent",
            config.getRedirectUri(),
            config.getAppKey(),
            sessionId
        );
        
        QrCodeDTO dto = new QrCodeDTO(sessionId, qrCodeUrl, 300);
        dto.setPlatform("dingtalk");
        
        QrCodeSession session = new QrCodeSession();
        session.setSessionId(sessionId);
        session.setQrCodeUrl(qrCodeUrl);
        session.setCreateTime(System.currentTimeMillis());
        session.setStatus("PENDING");
        qrCodeSessions.put(sessionId, session);
        
        log.info("Generated DingTalk QR code for session: {}", sessionId);
        return dto;
    }
    
    public String checkScanStatus(String sessionId) {
        QrCodeSession session = qrCodeSessions.get(sessionId);
        if (session == null) {
            return "NOT_FOUND";
        }
        if (System.currentTimeMillis() - session.getCreateTime() > 300000) {
            qrCodeSessions.remove(sessionId);
            return "EXPIRED";
        }
        return session.getStatus();
    }
    
    public AuthTokenDTO handleCallback(String code, String state) {
        log.info("Handling DingTalk callback, code: {}, state: {}", code, state);
        
        try {
            String userAccessToken = getUserAccessToken(code);
            if (userAccessToken == null) {
                return null;
            }
            
            DingdingUser user = getUserInfo(userAccessToken);
            if (user == null) {
                return null;
            }
            
            AuthTokenDTO token = new AuthTokenDTO();
            token.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
            token.setRefreshToken(UUID.randomUUID().toString().replace("-", ""));
            token.setExpiresIn(7200);
            token.setUserId(user.getUserid());
            token.setUserName(user.getName());
            token.setUnionId(user.getUnionid());
            token.setOpenId(user.getOpenid());
            token.setPlatform("dingtalk");
            
            tokenStore.put(token.getAccessToken(), token);
            
            QrCodeSession session = qrCodeSessions.get(state);
            if (session != null) {
                session.setStatus("CONFIRMED");
                session.setUserId(user.getUserid());
            }
            
            log.info("DingTalk auth success for user: {}", user.getName());
            return token;
            
        } catch (Exception e) {
            log.error("Failed to handle DingTalk callback", e);
            return null;
        }
    }
    
    public AuthTokenDTO refreshToken(String refreshToken) {
        log.info("Refreshing token");
        for (AuthTokenDTO token : tokenStore.values()) {
            if (refreshToken.equals(token.getRefreshToken())) {
                token.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
                token.setExpiresIn(7200);
                tokenStore.put(token.getAccessToken(), token);
                return token;
            }
        }
        return null;
    }
    
    public boolean validateToken(String accessToken) {
        AuthTokenDTO token = tokenStore.get(accessToken);
        return token != null && !token.isExpired();
    }
    
    public AuthTokenDTO getToken(String accessToken) {
        return tokenStore.get(accessToken);
    }
    
    public boolean unbind(String accessToken) {
        AuthTokenDTO token = tokenStore.remove(accessToken);
        if (token != null) {
            log.info("Unbind DingTalk account for user: {}", token.getUserName());
            return true;
        }
        return false;
    }
    
    private String getUserAccessToken(String code) {
        return "user_access_token_" + System.currentTimeMillis();
    }
    
    private DingdingUser getUserInfo(String accessToken) {
        DingdingUser user = new DingdingUser();
        user.setUserid("user_" + System.currentTimeMillis());
        user.setName("DingTalk User");
        return user;
    }
    
    private static class QrCodeSession {
        private String sessionId;
        private String qrCodeUrl;
        private long createTime;
        private String status;
        private String userId;
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getQrCodeUrl() { return qrCodeUrl; }
        public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
}

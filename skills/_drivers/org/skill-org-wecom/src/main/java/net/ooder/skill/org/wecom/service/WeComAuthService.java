package net.ooder.skill.org.wecom.service;

import net.ooder.skill.org.wecom.client.WeComApiClient;
import net.ooder.skill.org.wecom.config.WeComConfig;
import net.ooder.skill.org.wecom.dto.AuthTokenDTO;
import net.ooder.skill.org.wecom.dto.QrCodeDTO;
import net.ooder.skill.org.wecom.model.WeComUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeComAuthService {
    
    private static final Logger log = LoggerFactory.getLogger(WeComAuthService.class);
    
    @Autowired
    private WeComConfig config;
    
    @Autowired
    private WeComApiClient apiClient;
    
    private Map<String, QrCodeSession> qrCodeSessions = new ConcurrentHashMap<>();
    private Map<String, AuthTokenDTO> tokenStore = new ConcurrentHashMap<>();
    
    public QrCodeDTO generateQrCode() {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        
        String qrCodeUrl = String.format(
            "https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=%s&agentid=%s&redirect_uri=%s&state=%s",
            config.getCorpId(),
            config.getAgentId(),
            config.getRedirectUri(),
            sessionId
        );
        
        QrCodeDTO dto = new QrCodeDTO(sessionId, qrCodeUrl, 300);
        dto.setPlatform("wecom");
        
        QrCodeSession session = new QrCodeSession();
        session.setSessionId(sessionId);
        session.setQrCodeUrl(qrCodeUrl);
        session.setCreateTime(System.currentTimeMillis());
        session.setStatus("PENDING");
        qrCodeSessions.put(sessionId, session);
        
        log.info("Generated WeCom QR code for session: {}", sessionId);
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
        log.info("Handling WeCom callback, code: {}, state: {}", code, state);
        
        try {
            Map<String, Object> userInfo = apiClient.getUserInfoByCode(code);
            if (userInfo == null) {
                return null;
            }
            
            String userId = (String) userInfo.get("UserId");
            String deviceId = (String) userInfo.get("DeviceId");
            String openUserId = (String) userInfo.get("OpenId");
            
            WeComUser user = apiClient.getUser(userId);
            
            AuthTokenDTO token = new AuthTokenDTO();
            token.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
            token.setRefreshToken(UUID.randomUUID().toString().replace("-", ""));
            token.setExpiresIn(7200);
            token.setUserId(userId);
            token.setUserName(user != null ? user.getName() : userId);
            token.setDeviceId(deviceId);
            token.setOpenUserId(openUserId);
            token.setPlatform("wecom");
            
            tokenStore.put(token.getAccessToken(), token);
            
            QrCodeSession session = qrCodeSessions.get(state);
            if (session != null) {
                session.setStatus("CONFIRMED");
                session.setUserId(userId);
            }
            
            log.info("WeCom auth success for user: {}", userId);
            return token;
            
        } catch (Exception e) {
            log.error("Failed to handle WeCom callback", e);
            return null;
        }
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
            log.info("Unbind WeCom account for user: {}", token.getUserName());
            return true;
        }
        return false;
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

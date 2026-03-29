package net.ooder.skill.org.feishu.service;

import net.ooder.skill.org.feishu.client.FeishuApiClient;
import net.ooder.skill.org.feishu.config.FeishuConfig;
import net.ooder.skill.org.feishu.dto.AuthTokenDTO;
import net.ooder.skill.org.feishu.dto.QrCodeDTO;
import net.ooder.skill.org.feishu.model.FeishuUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FeishuAuthService {
    
    private static final Logger log = LoggerFactory.getLogger(FeishuAuthService.class);
    
    @Autowired
    private FeishuConfig config;
    
    @Autowired
    private FeishuApiClient apiClient;
    
    private Map<String, QrCodeSession> qrCodeSessions = new ConcurrentHashMap<>();
    private Map<String, AuthTokenDTO> tokenStore = new ConcurrentHashMap<>();
    
    public QrCodeDTO generateQrCode() {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        
        String qrCodeUrl = String.format(
            "https://open.feishu.cn/open-apis/authen/v1/authorize?app_id=%s&redirect_uri=%s&state=%s",
            config.getAppId(),
            config.getRedirectUri(),
            sessionId
        );
        
        QrCodeDTO dto = new QrCodeDTO(sessionId, qrCodeUrl, 300);
        dto.setPlatform("feishu");
        
        QrCodeSession session = new QrCodeSession();
        session.setSessionId(sessionId);
        session.setQrCodeUrl(qrCodeUrl);
        session.setCreateTime(System.currentTimeMillis());
        session.setStatus("PENDING");
        qrCodeSessions.put(sessionId, session);
        
        log.info("Generated Feishu QR code for session: {}", sessionId);
        return dto;
    }
    
    public QrCodeDTO generateQrCodeWithRecommend() {
        QrCodeDTO dto = generateQrCode();
        dto.setQrCodeUrl(dto.getQrCodeUrl() + "&scope=contact:user.base:readonly,contact:user.email:readonly");
        log.info("Generated Feishu QR code with recommend scopes for session: {}", dto.getSessionId());
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
        log.info("Handling Feishu callback, code: {}, state: {}", code, state);
        
        try {
            Map<String, Object> tokenInfo = apiClient.getUserAccessToken(code);
            if (tokenInfo == null) {
                return null;
            }
            
            String userAccessToken = (String) tokenInfo.get("access_token");
            FeishuUser user = apiClient.getUserInfo(userAccessToken);
            if (user == null) {
                return null;
            }
            
            AuthTokenDTO token = new AuthTokenDTO();
            token.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
            token.setRefreshToken(UUID.randomUUID().toString().replace("-", ""));
            token.setExpiresIn(7200);
            token.setUserId(user.getUserId());
            token.setUserName(user.getName());
            token.setUnionId(user.getUnionId());
            token.setOpenId(user.getOpenId());
            token.setTenantKey((String) tokenInfo.get("tenant_key"));
            token.setPlatform("feishu");
            
            tokenStore.put(token.getAccessToken(), token);
            
            QrCodeSession session = qrCodeSessions.get(state);
            if (session != null) {
                session.setStatus("CONFIRMED");
                session.setUserId(user.getUserId());
            }
            
            log.info("Feishu auth success for user: {}", user.getName());
            return token;
            
        } catch (Exception e) {
            log.error("Failed to handle Feishu callback", e);
            return null;
        }
    }
    
    public AuthTokenDTO refreshToken(String refreshToken) {
        log.info("Refreshing Feishu token");
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
            log.info("Unbind Feishu account for user: {}", token.getUserName());
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

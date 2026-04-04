package net.ooder.skill.org.dingding.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.skill.org.dingding.client.DingdingApiClient;
import net.ooder.skill.org.dingding.config.DingdingConfig;
import net.ooder.skill.org.dingding.dto.AuthTokenDTO;
import net.ooder.skill.org.dingding.dto.QrCodeDTO;
import net.ooder.skill.org.dingding.model.DingdingUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/v1.0/oauth2/userAccessToken?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("clientId", config.getAppKey());
            body.put("clientSecret", config.getAppSecret());
            body.put("code", code);
            body.put("grantType", "authorization_code");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("accessToken")) {
                return result.getString("accessToken");
            }
        } catch (Exception e) {
            log.error("Failed to get user access token", e);
        }
        return null;
    }
    
    private DingdingUser getUserInfo(String userAccessToken) {
        try {
            String url = config.getApiBaseUrl() + "/v1.0/contact/users/me";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-acs-dingtalk-access-token", userAccessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("openId")) {
                DingdingUser user = new DingdingUser();
                user.setOpenid(result.getString("openId"));
                user.setUnionid(result.getString("unionId"));
                user.setUserid(result.getString("userId"));
                user.setName(result.getString("nickName"));
                user.setMobile(result.getString("mobile"));
                user.setEmail(result.getString("email"));
                return user;
            }
        } catch (Exception e) {
            log.error("Failed to get user info", e);
        }
        return null;
    }
    
    private void ensureToken() {
        if (accessToken == null || System.currentTimeMillis() > tokenExpireTime) {
            refreshToken();
        }
    }
    
    private void refreshToken() {
        try {
            String url = config.getApiBaseUrl() + "/v1.0/oauth2/accessToken";
            
            JSONObject body = new JSONObject();
            body.put("appKey", config.getAppKey());
            body.put("appSecret", config.getAppSecret());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("accessToken")) {
                accessToken = result.getString("accessToken");
                int expire = result.getIntValue("expireIn");
                tokenExpireTime = System.currentTimeMillis() + (expire - 300) * 1000L;
                log.info("DingTalk access token refreshed");
            }
        } catch (Exception e) {
            log.error("Failed to refresh token", e);
        }
    }
    
    private String accessToken;
    private long tokenExpireTime;
    private RestTemplate restTemplate = new RestTemplate();
    
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

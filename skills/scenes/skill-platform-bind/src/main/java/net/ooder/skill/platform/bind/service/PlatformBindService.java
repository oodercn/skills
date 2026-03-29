package net.ooder.skill.platform.bind.service;

import net.ooder.skill.platform.bind.dto.*;
import net.ooder.skill.platform.bind.dict.PlatformType;
import net.ooder.skill.platform.bind.dict.BindStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PlatformBindService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DingTalkBindService dingTalkBindService;

    @Autowired
    private FeishuBindService feishuBindService;

    @Autowired
    private WeComBindService weComBindService;

    private static final int QR_CODE_EXPIRE_SECONDS = 300;
    private static final String BIND_SESSION_PREFIX = "platform:bind:session:";
    private static final String BIND_USER_PREFIX = "platform:bind:user:";

    public QrCodeDTO generateQrCode(String platform, String userId) {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        
        QrCodeDTO qrCode = QrCodeDTO.builder()
                .sessionId(sessionId)
                .platform(platform)
                .expireSeconds(QR_CODE_EXPIRE_SECONDS)
                .expireTime(System.currentTimeMillis() + QR_CODE_EXPIRE_SECONDS * 1000)
                .build();

        switch (PlatformType.valueOf(platform.toUpperCase())) {
            case DINGTALK:
                qrCode = dingTalkBindService.generateQrCode(sessionId);
                break;
            case FEISHU:
                qrCode = feishuBindService.generateQrCode(sessionId);
                break;
            case WECOM:
                qrCode = weComBindService.generateQrCode(sessionId);
                break;
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }

        String sessionKey = BIND_SESSION_PREFIX + sessionId;
        redisTemplate.opsForValue().set(sessionKey, userId + ":" + platform + ":" + BindStatus.PENDING.getCode(), 
                QR_CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        
        log.info("Generated QR code for platform: {}, sessionId: {}", platform, sessionId);
        return qrCode;
    }

    public BindStatusDTO checkBindStatus(String sessionId) {
        String sessionKey = BIND_SESSION_PREFIX + sessionId;
        String sessionData = redisTemplate.opsForValue().get(sessionKey);
        
        if (sessionData == null) {
            return BindStatusDTO.builder()
                    .sessionId(sessionId)
                    .status(BindStatus.EXPIRED.getCode())
                    .bound(false)
                    .message("二维码已过期，请重新获取")
                    .build();
        }

        String[] parts = sessionData.split(":");
        String userId = parts[0];
        String platform = parts[1];
        String status = parts.length > 2 ? parts[2] : BindStatus.PENDING.getCode();

        BindStatusDTO statusDTO = BindStatusDTO.builder()
                .sessionId(sessionId)
                .platform(platform)
                .status(status)
                .bound(BindStatus.BOUND.getCode().equals(status))
                .build();

        if (BindStatus.BOUND.getCode().equals(status) && parts.length > 3) {
            statusDTO.setPlatformUserId(parts[3]);
            statusDTO.setPlatformUserName(parts.length > 4 ? parts[4] : null);
        }

        return statusDTO;
    }

    public AuthTokenDTO handleCallback(String platform, String code, String state) {
        AuthTokenDTO token = null;
        
        switch (PlatformType.valueOf(platform.toUpperCase())) {
            case DINGTALK:
                token = dingTalkBindService.handleCallback(code, state);
                break;
            case FEISHU:
                token = feishuBindService.handleCallback(code, state);
                break;
            case WECOM:
                token = weComBindService.handleCallback(code, state);
                break;
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }

        if (token != null && state != null) {
            String sessionKey = BIND_SESSION_PREFIX + state;
            String sessionData = redisTemplate.opsForValue().get(sessionKey);
            
            if (sessionData != null) {
                String[] parts = sessionData.split(":");
                String updatedData = parts[0] + ":" + parts[1] + ":" + BindStatus.BOUND.getCode() + 
                        ":" + token.getPlatformUserId() + ":" + token.getPlatformUserName();
                redisTemplate.opsForValue().set(sessionKey, updatedData, 60, TimeUnit.SECONDS);
                
                saveUserBinding(parts[0], platform, token);
            }
        }

        log.info("Handled callback for platform: {}, userId: {}", platform, token != null ? token.getPlatformUserId() : null);
        return token;
    }

    public boolean unbind(String userId, String platform) {
        String userBindKey = BIND_USER_PREFIX + userId + ":" + platform;
        redisTemplate.delete(userBindKey);
        
        log.info("Unbound platform: {} for user: {}", platform, userId);
        return true;
    }

    public PlatformBindingDTO getUserBinding(String userId, String platform) {
        String userBindKey = BIND_USER_PREFIX + userId + ":" + platform;
        String bindData = redisTemplate.opsForValue().get(userBindKey);
        
        if (bindData == null) {
            return null;
        }

        String[] parts = bindData.split("\\|");
        return PlatformBindingDTO.builder()
                .userId(userId)
                .platform(platform)
                .platformUserId(parts[0])
                .platformUserName(parts.length > 1 ? parts[1] : null)
                .platformAvatar(parts.length > 2 ? parts[2] : null)
                .status(BindStatus.BOUND.getCode())
                .build();
    }

    private void saveUserBinding(String userId, String platform, AuthTokenDTO token) {
        String userBindKey = BIND_USER_PREFIX + userId + ":" + platform;
        String bindData = token.getPlatformUserId() + "|" + token.getPlatformUserName() + "|" + 
                (token.getScope() != null ? token.getScope() : "");
        redisTemplate.opsForValue().set(userBindKey, bindData);
    }

    public void updateBindStatus(String sessionId, BindStatus status) {
        String sessionKey = BIND_SESSION_PREFIX + sessionId;
        String sessionData = redisTemplate.opsForValue().get(sessionKey);
        
        if (sessionData != null) {
            String[] parts = sessionData.split(":");
            String updatedData = parts[0] + ":" + parts[1] + ":" + status.getCode();
            Long ttl = redisTemplate.getExpire(sessionKey, TimeUnit.SECONDS);
            if (ttl != null && ttl > 0) {
                redisTemplate.opsForValue().set(sessionKey, updatedData, ttl, TimeUnit.SECONDS);
            }
        }
    }
}

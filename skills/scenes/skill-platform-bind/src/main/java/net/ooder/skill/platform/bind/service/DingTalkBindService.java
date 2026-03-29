package net.ooder.skill.platform.bind.service;

import net.ooder.skill.platform.bind.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Service
public class DingTalkBindService {

    @Value("${platform.dingtalk.app-key:}")
    private String appKey;

    @Value("${platform.dingtalk.app-secret:}")
    private String appSecret;

    @Value("${platform.dingtalk.callback-url:}")
    private String callbackUrl;

    public QrCodeDTO generateQrCode(String sessionId) {
        String qrCodeUrl = String.format(
                "https://login.dingtalk.com/oauth2/auth?redirect_uri=%s&client_id=%s&response_type=code&scope=openid&state=%s&prompt=consent",
                callbackUrl, appKey, sessionId
        );
        
        return QrCodeDTO.builder()
                .sessionId(sessionId)
                .platform("DINGTALK")
                .qrCodeUrl(qrCodeUrl)
                .qrCodeData(generateQrCodeData(qrCodeUrl))
                .expireSeconds(300)
                .expireTime(System.currentTimeMillis() + 300000)
                .build();
    }

    public AuthTokenDTO handleCallback(String code, String state) {
        log.info("DingTalk callback: code={}, state={}", code, state);
        
        return AuthTokenDTO.builder()
                .platform("DINGTALK")
                .platformUserId("dingtalk_user_" + System.currentTimeMillis())
                .platformUserName("钉钉用户")
                .accessToken("access_token_" + UUID.randomUUID())
                .expiresIn(7200L)
                .scope("openid,contact")
                .build();
    }

    private String generateQrCodeData(String url) {
        return "data:image/svg+xml;base64," + java.util.Base64.getEncoder().encodeToString(url.getBytes());
    }
}

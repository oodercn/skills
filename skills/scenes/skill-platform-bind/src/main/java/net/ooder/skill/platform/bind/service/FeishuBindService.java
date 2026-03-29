package net.ooder.skill.platform.bind.service;

import net.ooder.skill.platform.bind.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Service
public class FeishuBindService {

    @Value("${platform.feishu.app-id:}")
    private String appId;

    @Value("${platform.feishu.app-secret:}")
    private String appSecret;

    @Value("${platform.feishu.callback-url:}")
    private String callbackUrl;

    public QrCodeDTO generateQrCode(String sessionId) {
        String qrCodeUrl = String.format(
                "https://open.feishu.cn/open-apis/authen/v1/authorize?redirect_uri=%s&app_id=%s&state=%s",
                callbackUrl, appId, sessionId
        );
        
        return QrCodeDTO.builder()
                .sessionId(sessionId)
                .platform("FEISHU")
                .qrCodeUrl(qrCodeUrl)
                .qrCodeData(generateQrCodeData(qrCodeUrl))
                .expireSeconds(300)
                .expireTime(System.currentTimeMillis() + 300000)
                .build();
    }

    public AuthTokenDTO handleCallback(String code, String state) {
        log.info("Feishu callback: code={}, state={}", code, state);
        
        return AuthTokenDTO.builder()
                .platform("FEISHU")
                .platformUserId("feishu_user_" + System.currentTimeMillis())
                .platformUserName("飞书用户")
                .accessToken("access_token_" + UUID.randomUUID())
                .expiresIn(7200L)
                .scope("contact:user.base,contact:user.email")
                .build();
    }

    private String generateQrCodeData(String url) {
        return "data:image/svg+xml;base64," + java.util.Base64.getEncoder().encodeToString(url.getBytes());
    }
}

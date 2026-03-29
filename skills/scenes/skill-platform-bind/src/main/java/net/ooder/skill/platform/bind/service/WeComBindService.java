package net.ooder.skill.platform.bind.service;

import net.ooder.skill.platform.bind.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Service
public class WeComBindService {

    @Value("${platform.wecom.corp-id:}")
    private String corpId;

    @Value("${platform.wecom.agent-id:}")
    private String agentId;

    @Value("${platform.wecom.secret:}")
    private String secret;

    @Value("${platform.wecom.callback-url:}")
    private String callbackUrl;

    public QrCodeDTO generateQrCode(String sessionId) {
        String qrCodeUrl = String.format(
                "https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=%s&agentid=%s&redirect_uri=%s&state=%s",
                corpId, agentId, callbackUrl, sessionId
        );
        
        return QrCodeDTO.builder()
                .sessionId(sessionId)
                .platform("WECOM")
                .qrCodeUrl(qrCodeUrl)
                .qrCodeData(generateQrCodeData(qrCodeUrl))
                .expireSeconds(300)
                .expireTime(System.currentTimeMillis() + 300000)
                .build();
    }

    public AuthTokenDTO handleCallback(String code, String state) {
        log.info("WeCom callback: code={}, state={}", code, state);
        
        return AuthTokenDTO.builder()
                .platform("WECOM")
                .platformUserId("wecom_user_" + System.currentTimeMillis())
                .platformUserName("企业微信用户")
                .accessToken("access_token_" + UUID.randomUUID())
                .expiresIn(7200L)
                .scope("snsapi_base,snsapi_privateinfo")
                .build();
    }

    private String generateQrCodeData(String url) {
        return "data:image/svg+xml;base64," + java.util.Base64.getEncoder().encodeToString(url.getBytes());
    }
}

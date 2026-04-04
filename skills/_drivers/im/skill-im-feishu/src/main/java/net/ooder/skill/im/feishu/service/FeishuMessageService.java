package net.ooder.skill.im.feishu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.skill.im.feishu.dto.MessageDTO;
import net.ooder.skill.im.feishu.dto.SendResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FeishuMessageService {

    private static final Logger log = LoggerFactory.getLogger(FeishuMessageService.class);

    @Value("${feishu.api.endpoint:https://open.feishu.cn/open-apis}")
    private String apiEndpoint;

    @Value("${feishu.app.id:}")
    private String appId;

    @Value("${feishu.app.secret:}")
    private String appSecret;

    private RestTemplate restTemplate = new RestTemplate();
    private String tenantAccessToken;
    private long tokenExpireTime;

    public SendResultDTO sendMessage(MessageDTO message) {
        log.info("[sendMessage] Sending message to: {}", message.getReceiverId());

        try {
            String token = getTenantAccessToken();
            if (token == null) {
                return SendResultDTO.fail("TOKEN_ERROR", "获取租户访问令牌失败");
            }

            String url = apiEndpoint + "/im/v1/messages?receive_id_type=" + (message.getReceiveIdType() != null ? message.getReceiveIdType() : "user_id");

            Map<String, Object> body = new HashMap<>();
            body.put("receive_id", message.getReceiverId());

            String msgType = message.getMsgType() != null ? message.getMsgType().toLowerCase() : "text";
            body.put("msg_type", msgType);

            Map<String, Object> content = new HashMap<>();

            if ("text".equals(msgType)) {
                Map<String, String> textContent = new HashMap<>();
                textContent.put("text", message.getContent());
                body.put("content", JSON.toJSONString(textContent));
            } else if ("post".equals(msgType)) {
                Map<String, Object> postContent = new HashMap<>();
                postContent.put("title", message.getTitle());
                postContent.put("content", message.getContent());
                body.put("content", JSON.toJSONString(postContent));
            } else if ("interactive".equals(msgType)) {
                Map<String, Object> interactiveContent = new HashMap<>();
                interactiveContent.put("title", message.getTitle());
                if (message.getExtra() != null) {
                    interactiveContent.put("elements", message.getExtra());
                }
                body.put("content", JSON.toJSONString(interactiveContent));
            } else if ("image".equals(msgType)) {
                Map<String, String> imageContent = new HashMap<>();
                imageContent.put("image_key", message.getImageKey());
                body.put("content", JSON.toJSONString(imageContent));
            } else if ("file".equals(msgType)) {
                Map<String, String> fileContent = new HashMap<>();
                fileContent.put("file_key", message.getFileKey());
                body.put("content", JSON.toJSONString(fileContent));
            } else {
                Map<String, String> textContent = new HashMap<>();
                textContent.put("text", message.getContent());
                body.put("content", JSON.toJSONString(textContent));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JSONObject result = JSON.parseObject(response.getBody());

            if (result.getIntValue("code") == 0) {
                JSONObject data = result.getJSONObject("data");
                String messageId = data != null ? data.getString("message_id") : null;
                log.info("Message sent successfully, messageId: {}", messageId);
                return SendResultDTO.success(messageId);
            } else {
                log.error("Failed to send message: {} - {}", result.getIntValue("code"), result.getString("msg"));
                return SendResultDTO.fail(String.valueOf(result.getIntValue("code")), result.getString("msg"));
            }

        } catch (Exception e) {
            log.error("Failed to send message", e);
            return SendResultDTO.fail("SEND_ERROR", e.getMessage());
        }
    }

    public SendResultDTO sendText(String userId, String content) {
        log.info("[sendText] Sending text to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("text");
        message.setReceiverId(userId);
        message.setContent(content);

        return sendMessage(message);
    }

    public SendResultDTO sendPost(String userId, String title, String content) {
        log.info("[sendPost] Sending post to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("post");
        message.setReceiverId(userId);
        message.setTitle(title);
        message.setContent(content);

        return sendMessage(message);
    }

    public SendResultDTO sendInteractive(String userId, String title, Map<String, Object> elements) {
        log.info("[sendInteractive] Sending interactive card to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("interactive");
        message.setReceiverId(userId);
        message.setTitle(title);
        message.setExtra(elements);

        return sendMessage(message);
    }

    public SendResultDTO sendToGroup(String chatId, String content) {
        log.info("[sendToGroup] Sending message to group: {}", chatId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("text");
        message.setReceiverId(chatId);
        message.setReceiveIdType("chat_id");
        message.setContent(content);

        return sendMessage(message);
    }

    public SendResultDTO sendImage(String userId, String imageKey) {
        log.info("[sendImage] Sending image to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("image");
        message.setReceiverId(userId);
        message.setImageKey(imageKey);

        return sendMessage(message);
    }

    public SendResultDTO sendFile(String userId, String fileKey) {
        log.info("[sendFile] Sending file to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("file");
        message.setReceiverId(userId);
        message.setFileKey(fileKey);

        return sendMessage(message);
    }

    private synchronized String getTenantAccessToken() {
        if (tenantAccessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return tenantAccessToken;
        }
        return refreshTenantToken();
    }

    private String refreshTenantToken() {
        try {
            if (appId == null || appId.isEmpty() || appSecret == null || appSecret.isEmpty()) {
                log.error("Feishu app id or secret is not configured");
                return null;
            }

            String url = apiEndpoint + "/auth/v3/tenant_access_token/internal";

            Map<String, String> body = new HashMap<>();
            body.put("app_id", appId);
            body.put("app_secret", appSecret);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JSONObject result = JSON.parseObject(response.getBody());

            if (result.getIntValue("code") == 0) {
                tenantAccessToken = result.getString("tenant_access_token");
                int expire = result.getIntValue("expire");
                tokenExpireTime = System.currentTimeMillis() + (expire - 300) * 1000L;
                log.info("Feishu tenant access token refreshed, expires in {} seconds", expire);
                return tenantAccessToken;
            } else {
                log.error("Failed to refresh tenant token: {} - {}", result.getIntValue("code"), result.getString("msg"));
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to refresh tenant token", e);
            return null;
        }
    }
}

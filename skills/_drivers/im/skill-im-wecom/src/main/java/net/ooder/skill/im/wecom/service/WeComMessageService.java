package net.ooder.skill.im.wecom.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.skill.im.wecom.dto.MessageDTO;
import net.ooder.skill.im.wecom.dto.SendResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeComMessageService {

    private static final Logger log = LoggerFactory.getLogger(WeComMessageService.class);

    @Value("${wecom.api-endpoint:https://qyapi.weixin.qq.com}")
    private String apiEndpoint;

    @Value("${wecom.corp-id:}")
    private String corpId;

    @Value("${wecom.agent-id:}")
    private String agentId;

    @Value("${wecom.secret:}")
    private String secret;

    private RestTemplate restTemplate = new RestTemplate();
    private String accessToken;
    private long tokenExpireTime;

    public SendResultDTO sendMessage(MessageDTO message) {
        log.info("[sendMessage] Sending message to: {}", message.getReceiverId());

        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                return SendResultDTO.fail("TOKEN_ERROR", "获取访问令牌失败");
            }

            String url = apiEndpoint + "/cgi-bin/message/send?access_token=" + accessToken;

            Map<String, Object> body = new HashMap<>();

            // 接收者处理
            if (message.getReceiverId() != null && !message.getReceiverId().isEmpty()) {
                body.put("touser", message.getReceiverId());
            }
            if (message.getPartyId() != null && !message.getPartyId().isEmpty()) {
                body.put("toparty", message.getPartyId());
            }
            if (message.getTagId() != null && !message.getTagId().isEmpty()) {
                body.put("totag", message.getTagId());
            }

            body.put("msgtype", message.getMsgType() != null ? message.getMsgType() : "text");
            body.put("agentid", agentId);

            String msgType = message.getMsgType() != null ? message.getMsgType().toLowerCase() : "text";
            Map<String, Object> contentMap = new HashMap<>();

            if ("text".equals(msgType)) {
                contentMap.put("content", message.getContent());
                body.put("text", contentMap);
            } else if ("markdown".equals(msgType)) {
                contentMap.put("content", message.getContent());
                body.put("markdown", contentMap);
            } else if ("news".equals(msgType)) {
                contentMap.put("articles", message.getExtra());
                body.put("news", contentMap);
            } else if ("file".equals(msgType)) {
                contentMap.put("media_id", message.getMediaId());
                body.put("file", contentMap);
            } else if ("image".equals(msgType)) {
                contentMap.put("media_id", message.getMediaId());
                body.put("image", contentMap);
            } else {
                contentMap.put("content", message.getContent());
                body.put("text", contentMap);
            }

            // 安全设置
            body.put("safe", 0);
            body.put("enable_id_trans", 0);
            body.put("enable_duplicate_check", 0);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JSONObject result = JSON.parseObject(response.getBody());

            if (result.getIntValue("errcode") == 0) {
                String messageId = result.getString("msgid");
                log.info("Message sent successfully, msgid: {}", messageId);
                return SendResultDTO.success(messageId);
            } else {
                log.error("Failed to send message: {} - {}", result.getIntValue("errcode"), result.getString("errmsg"));
                return SendResultDTO.fail(String.valueOf(result.getIntValue("errcode")), result.getString("errmsg"));
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

    public SendResultDTO sendMarkdown(String userId, String content) {
        log.info("[sendMarkdown] Sending markdown to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("markdown");
        message.setReceiverId(userId);
        message.setContent(content);

        return sendMessage(message);
    }

    public SendResultDTO sendToGroup(String chatId, String content) {
        log.info("[sendToGroup] Sending message to group: {}", chatId);

        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                return SendResultDTO.fail("TOKEN_ERROR", "获取访问令牌失败");
            }

            String url = apiEndpoint + "/cgi-bin/appchat/send?access_token=" + accessToken;

            Map<String, Object> body = new HashMap<>();
            body.put("chatid", chatId);
            body.put("msgtype", "text");

            Map<String, Object> text = new HashMap<>();
            text.put("content", content);
            body.put("text", text);

            body.put("safe", 0);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JSONObject result = JSON.parseObject(response.getBody());

            if (result.getIntValue("errcode") == 0) {
                log.info("Group message sent successfully");
                return SendResultDTO.success("group_" + System.currentTimeMillis());
            } else {
                log.error("Failed to send group message: {} - {}", result.getIntValue("errcode"), result.getString("errmsg"));
                return SendResultDTO.fail(String.valueOf(result.getIntValue("errcode")), result.getString("errmsg"));
            }

        } catch (Exception e) {
            log.error("Failed to send group message", e);
            return SendResultDTO.fail("SEND_ERROR", e.getMessage());
        }
    }

    public SendResultDTO sendNews(String userId, String title, String description, String url, String picUrl) {
        log.info("[sendNews] Sending news to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("news");
        message.setReceiverId(userId);

        Map<String, Object> article = new HashMap<>();
        article.put("title", title);
        article.put("description", description);
        article.put("url", url);
        article.put("picurl", picUrl);

        message.setExtra(java.util.Arrays.asList(article));

        return sendMessage(message);
    }

    public SendResultDTO sendFile(String userId, String mediaId) {
        log.info("[sendFile] Sending file to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("file");
        message.setReceiverId(userId);
        message.setMediaId(mediaId);

        return sendMessage(message);
    }

    public SendResultDTO sendImage(String userId, String mediaId) {
        log.info("[sendImage] Sending image to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("image");
        message.setReceiverId(userId);
        message.setMediaId(mediaId);

        return sendMessage(message);
    }

    private synchronized String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }
        return refreshToken();
    }

    private String refreshToken() {
        try {
            if (corpId == null || corpId.isEmpty() || secret == null || secret.isEmpty()) {
                log.error("WeCom corp id or secret is not configured");
                return null;
            }

            String url = apiEndpoint + "/cgi-bin/gettoken?corpid=" + corpId + "&corpsecret=" + secret;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject result = JSON.parseObject(response.getBody());

            if (result.getIntValue("errcode") == 0) {
                accessToken = result.getString("access_token");
                int expire = result.getIntValue("expires_in");
                tokenExpireTime = System.currentTimeMillis() + (expire - 300) * 1000L;
                log.info("WeCom access token refreshed, expires in {} seconds", expire);
                return accessToken;
            } else {
                log.error("Failed to refresh token: {} - {}", result.getIntValue("errcode"), result.getString("errmsg"));
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to refresh token", e);
            return null;
        }
    }
}

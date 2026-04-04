package net.ooder.skill.im.dingding.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.skill.im.dingding.dto.DingMessageDTO;
import net.ooder.skill.im.dingding.dto.MessageDTO;
import net.ooder.skill.im.dingding.dto.SendResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DingTalkMessageService {

    private static final Logger log = LoggerFactory.getLogger(DingTalkMessageService.class);

    @Value("${dingtalk.api.endpoint:https://oapi.dingtalk.com}")
    private String apiEndpoint;

    @Value("${dingtalk.app.key:}")
    private String appKey;

    @Value("${dingtalk.app.secret:}")
    private String appSecret;

    @Value("${dingtalk.agent.id:}")
    private String agentId;

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

            String url = apiEndpoint + "/topapi/message/corpconversation/asyncsend_v2?access_token=" + accessToken;

            Map<String, Object> body = new HashMap<>();
            body.put("agent_id", agentId);

            if ("user".equals(message.getReceiver())) {
                body.put("userid_list", message.getReceiverId());
            } else if ("group".equals(message.getReceiver())) {
                body.put("chatid", message.getReceiverId());
            }

            String msgType = message.getMsgType() != null ? message.getMsgType().toLowerCase() : "text";

            if ("markdown".equals(msgType)) {
                Map<String, Object> markdown = new HashMap<>();
                markdown.put("title", message.getTitle());
                markdown.put("text", message.getContent());
                body.put("msgtype", "markdown");
                body.put("markdown", markdown);
            } else if ("action_card".equals(msgType)) {
                Map<String, Object> actionCard = new HashMap<>();
                actionCard.put("title", message.getTitle());
                actionCard.put("markdown", message.getContent());
                if (message.getExtra() != null) {
                    actionCard.putAll(message.getExtra());
                }
                body.put("msgtype", "action_card");
                body.put("action_card", actionCard);
            } else {
                Map<String, Object> text = new HashMap<>();
                text.put("content", message.getContent());
                body.put("msgtype", "text");
                body.put("text", text);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JSONObject result = JSON.parseObject(response.getBody());

            if (result.getIntValue("errcode") == 0) {
                String messageId = result.getString("task_id");
                log.info("Message sent successfully, taskId: {}", messageId);
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

    public SendResultDTO sendDing(DingMessageDTO ding) {
        log.info("[sendDing] Sending DING to user: {}", ding.getUserId());

        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                return SendResultDTO.fail("TOKEN_ERROR", "获取访问令牌失败");
            }

            String url = apiEndpoint + "/topapi/ding/create?access_token=" + accessToken;

            Map<String, Object> body = new HashMap<>();
            body.put("userid_list", ding.getUserId());

            Map<String, Object> dingContent = new HashMap<>();
            dingContent.put("content", ding.getContent());
            if (ding.getTitle() != null) {
                dingContent.put("title", ding.getTitle());
            }
            body.put("content", dingContent);

            body.put("reminder_type", ding.getReminderType() > 0 ? ding.getReminderType() : 1);

            if (ding.getReminderTime() != null && !ding.getReminderTime().isEmpty()) {
                body.put("reminder_time", ding.getReminderTime());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JSONObject result = JSON.parseObject(response.getBody());

            if (result.getIntValue("errcode") == 0) {
                String dingId = result.getString("ding_id");
                log.info("DING sent successfully, dingId: {}", dingId);
                return SendResultDTO.success(dingId);
            } else {
                log.error("Failed to send DING: {} - {}", result.getIntValue("errcode"), result.getString("errmsg"));
                return SendResultDTO.fail(String.valueOf(result.getIntValue("errcode")), result.getString("errmsg"));
            }

        } catch (Exception e) {
            log.error("Failed to send DING", e);
            return SendResultDTO.fail("DING_ERROR", e.getMessage());
        }
    }

    public SendResultDTO sendToGroup(String groupId, String title, String content) {
        log.info("[sendToGroup] Sending message to group: {}", groupId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("text");
        message.setReceiver("group");
        message.setReceiverId(groupId);
        message.setTitle(title);
        message.setContent(content);

        return sendMessage(message);
    }

    public SendResultDTO sendMarkdown(String userId, String title, String content) {
        log.info("[sendMarkdown] Sending markdown to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("markdown");
        message.setReceiver("user");
        message.setReceiverId(userId);
        message.setTitle(title);
        message.setContent(content);

        return sendMessage(message);
    }

    public SendResultDTO sendActionCard(String userId, String title, String content, Map<String, Object> extra) {
        log.info("[sendActionCard] Sending action card to user: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("action_card");
        message.setReceiver("user");
        message.setReceiverId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setExtra(extra);

        return sendMessage(message);
    }

    public SendResultDTO sendText(String userId, String content) {
        log.info("[sendText] Sending text to: {}", userId);

        MessageDTO message = new MessageDTO();
        message.setMsgType("text");
        message.setReceiver("user");
        message.setReceiverId(userId);
        message.setContent(content);

        return sendMessage(message);
    }

    public SendResultDTO sendImage(String userId, String mediaId) {
        log.info("[sendImage] Sending image to: {}", userId);

        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                return SendResultDTO.fail("TOKEN_ERROR", "获取访问令牌失败");
            }

            String url = apiEndpoint + "/topapi/message/corpconversation/asyncsend_v2?access_token=" + accessToken;

            Map<String, Object> body = new HashMap<>();
            body.put("agent_id", agentId);
            body.put("userid_list", userId);

            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("media_id", mediaId);
            imageContent.set("photo_download_code", mediaId);
            body.put("msgtype", "image");
            body.put("image", imageContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JSONObject result = JSON.parseObject(response.getBody());

            if (result.getIntValue("errcode") == 0) {
                log.info("Image sent successfully");
                return SendResultDTO.success(result.getString("task_id"));
            } else {
                log.error("Failed to send image: {} - {}", result.getIntValue("errcode"), result.getString("errmsg"));
                return SendResultDTO.fail(String.valueOf(result.getIntValue("errcode")), result.getString("errmsg"));
            }
        } catch (Exception e) {
            log.error("Failed to send image", e);
            return SendResultDTO.fail("SEND_ERROR", e.getMessage());
        }
    }

    public SendResultDTO sendFile(String userId, String mediaId) {
        log.info("[sendFile] Sending file to: {}", userId);

        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                return SendResultDTO.fail("TOKEN_ERROR", "获取访问令牌失败");
            }

            String url = apiEndpoint + "/topapi/message/corpconversation/asyncsend_v2?access_token=" + accessToken;

            Map<String, Object> body = new HashMap<>();
            body.put("agent_id", agentId);
            body.put("userid_list", userId);

            Map<String, Object> fileContent = new HashMap<>();
            fileContent.put("media_id", mediaId);
            body.put("msgtype", "file");
            body.put("file", fileContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JSONObject result = JSON.parseObject(response.getBody());

            if (result.getIntValue("errcode") == 0) {
                log.info("File sent successfully");
                return SendResultDTO.success(result.getString("task_id"));
            } else {
                log.error("Failed to send file: {} - {}", result.getIntValue("errcode"), result.getString("errmsg"));
                return SendResultDTO.fail(String.valueOf(result.getIntValue("errcode")), result.getString("errmsg"));
            }
        } catch (Exception e) {
            log.error("Failed to send file", e);
            return SendResultDTO.fail("SEND_ERROR", e.getMessage());
        }
    }

    public SendResultDTO sendToGroupSimple(String groupId, String content) {
        log.info("[sendToGroupSimple] Sending group message to: {}", groupId);
        return sendToGroup(groupId, null, content);
    }

    private synchronized String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }
        return refreshToken();
    }

    private String refreshToken() {
        try {
            if (appKey == null || appKey.isEmpty() || appSecret == null || appSecret.isEmpty()) {
                log.error("DingTalk app key or secret is not configured");
                return null;
            }

            String url = apiEndpoint + "/gettoken?appkey=" + appKey + "&appsecret=" + appSecret;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject result = JSON.parseObject(response.getBody());

            if (result.getIntValue("errcode") == 0) {
                accessToken = result.getString("access_token");
                int expire = result.getIntValue("expires_in");
                tokenExpireTime = System.currentTimeMillis() + (expire - 300) * 1000L;
                log.info("DingTalk access token refreshed, expires in {} seconds", expire);
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

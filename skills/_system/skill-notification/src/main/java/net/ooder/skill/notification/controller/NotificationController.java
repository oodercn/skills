package net.ooder.skill.notification.controller;

import net.ooder.skill.notification.dto.*;
import net.ooder.skill.notification.service.NotificationService;
import net.ooder.skill.notification.service.impl.NotificationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private NotificationService notificationService;

    private NotificationService getNotificationService() {
        if (notificationService == null) {
            notificationService = new NotificationServiceImpl();
        }
        return notificationService;
    }

    @GetMapping
    public ResultModel<NotificationService.NotificationListResult> getNotifications(
            @RequestParam(defaultValue = "all") String tab,
            @RequestParam(defaultValue = "20") int limit,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("[getNotifications] tab: {}, limit: {}", tab, limit);
        String userId = getCurrentUserId(userIdHeader);
        NotificationService.NotificationListResult result = getNotificationService().getNotifications(userId, tab, limit);
        return ResultModel.success(result);
    }

    @GetMapping("/unread-count")
    public ResultModel<UnreadCountDTO> getUnreadCount(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("[getUnreadCount] Getting unread count");
        String userId = getCurrentUserId(userIdHeader);
        Map<String, Integer> counts = getNotificationService().getUnreadCounts(userId);
        
        UnreadCountDTO result = new UnreadCountDTO();
        result.setByType(counts);
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        result.setTotal(total);
        
        return ResultModel.success(result);
    }

    @PostMapping("/{notificationId}/read")
    public ResultModel<Void> markAsRead(
            @PathVariable String notificationId,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("[markAsRead] notificationId: {}", notificationId);
        String userId = getCurrentUserId(userIdHeader);
        getNotificationService().markAsRead(userId, notificationId);
        return ResultModel.success();
    }

    @PostMapping("/read-all")
    public ResultModel<Void> markAllAsRead(
            @RequestParam(required = false) String tab,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("[markAllAsRead] tab: {}", tab);
        String userId = getCurrentUserId(userIdHeader);
        getNotificationService().markAllAsRead(userId, tab);
        return ResultModel.success();
    }

    @PostMapping("/{notificationId}/action")
    public ResultModel<NotificationActionResultDTO> handleAction(
            @PathVariable String notificationId,
            @RequestBody NotificationActionRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("[handleAction] notificationId: {}, action: {}", notificationId, request.getAction());
        String userId = getCurrentUserId(userIdHeader);
        String action = request.getAction();
        Map<String, Object> resultData = getNotificationService().handleAction(userId, notificationId, action);
        
        NotificationActionResultDTO result = new NotificationActionResultDTO();
        result.setNotificationId(notificationId);
        result.setAction(action);
        result.setSuccess(true);
        result.setData(resultData);
        
        return ResultModel.success(result);
    }

    @GetMapping("/channels")
    public ResultModel<List<String>> getAvailableChannels() {
        log.info("[getAvailableChannels] Getting available channels");
        List<String> channels = Arrays.asList("web", "email", "dingtalk", "wecom", "feishu");
        return ResultModel.success(channels);
    }

    @PostMapping("/send")
    public ResultModel<SendMessageResultDTO> sendMessage(@RequestBody SendMessageRequest message) {
        log.info("[sendMessage] Sending message via channel: {}", message.getChannel());
        
        SendMessageResultDTO result = new SendMessageResultDTO();
        result.setMessageId("msg-" + System.currentTimeMillis());
        result.setChannel(message.getChannel());
        result.setStatus("sent");
        result.setTimestamp(System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    private String getCurrentUserId(String userIdHeader) {
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            return userIdHeader;
        }
        return "default-user";
    }

    public static class ResultModel<T> {
        private int code;
        private String status;
        private String message;
        private T data;
        private long timestamp;
        private String requestId;

        public ResultModel() {
            this.timestamp = System.currentTimeMillis();
            this.requestId = "REQ_" + timestamp + "_" + new Random().nextInt(1000);
        }

        public static <T> ResultModel<T> success(T data) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(200);
            result.setStatus("success");
            result.setMessage("操作成功");
            result.setData(data);
            return result;
        }

        public static <T> ResultModel<T> success() {
            return success(null);
        }

        public static <T> ResultModel<T> error(int code, String message) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(code);
            result.setStatus("error");
            result.setMessage(message);
            return result;
        }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
    }
}

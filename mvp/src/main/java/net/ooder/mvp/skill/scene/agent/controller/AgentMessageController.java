package net.ooder.mvp.skill.scene.agent.controller;

import net.ooder.mvp.skill.scene.agent.dto.AgentMessageDTO;
import net.ooder.mvp.skill.scene.agent.dto.MessageType;
import net.ooder.mvp.skill.scene.agent.service.AgentMessageService;
import net.ooder.mvp.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agents")
public class AgentMessageController {

    private static final Logger log = LoggerFactory.getLogger(AgentMessageController.class);

    @Autowired
    private AgentMessageService messageService;

    @PostMapping("/{agentId}/messages")
    public ResultModel<Map<String, Object>> sendMessage(
            @PathVariable String agentId,
            @RequestBody AgentMessageDTO message) {
        log.info("[sendMessage] {} -> {}", message.getFromAgent(), message.getToAgent());
        
        try {
            if (message.getFromAgent() == null) {
                message.setFromAgent(agentId);
            }
            
            String messageId = messageService.sendMessage(message);
            
            Map<String, Object> result = new HashMap<>();
            result.put("messageId", messageId);
            result.put("createTime", message.getCreateTime());
            result.put("status", message.getStatus());
            
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[sendMessage] Failed: {}", e.getMessage());
            return ResultModel.error(500, "发送消息失败: " + e.getMessage());
        }
    }

    @GetMapping("/{agentId}/messages")
    public ResultModel<List<AgentMessageDTO>> receiveMessages(
            @PathVariable String agentId,
            @RequestParam(required = false) String type) {
        log.debug("[receiveMessages] Agent: {}, type: {}", agentId, type);
        
        try {
            List<AgentMessageDTO> messages;
            if (type != null && !type.isEmpty()) {
                messages = messageService.receiveMessages(agentId, type);
            } else {
                messages = messageService.receiveMessages(agentId);
            }
            return ResultModel.success(messages);
        } catch (Exception e) {
            log.error("[receiveMessages] Failed: {}", e.getMessage());
            return ResultModel.error(500, "接收消息失败: " + e.getMessage());
        }
    }

    @GetMapping("/{agentId}/messages/unread")
    public ResultModel<List<AgentMessageDTO>> receiveUnreadMessages(@PathVariable String agentId) {
        log.debug("[receiveUnreadMessages] Agent: {}", agentId);
        
        try {
            List<AgentMessageDTO> messages = messageService.receiveUnreadMessages(agentId);
            return ResultModel.success(messages);
        } catch (Exception e) {
            log.error("[receiveUnreadMessages] Failed: {}", e.getMessage());
            return ResultModel.error(500, "获取未读消息失败: " + e.getMessage());
        }
    }

    @GetMapping("/{agentId}/messages/{messageId}")
    public ResultModel<AgentMessageDTO> getMessage(
            @PathVariable String agentId,
            @PathVariable String messageId) {
        log.debug("[getMessage] Agent: {}, message: {}", agentId, messageId);
        
        try {
            AgentMessageDTO message = messageService.getMessage(messageId);
            if (message == null) {
                return ResultModel.notFound("消息不存在或已过期");
            }
            return ResultModel.success(message);
        } catch (Exception e) {
            log.error("[getMessage] Failed: {}", e.getMessage());
            return ResultModel.error(500, "获取消息失败: " + e.getMessage());
        }
    }

    @PostMapping("/{agentId}/messages/{messageId}/ack")
    public ResultModel<Boolean> acknowledge(
            @PathVariable String agentId,
            @PathVariable String messageId) {
        log.info("[acknowledge] Agent: {}, message: {}", agentId, messageId);
        
        try {
            messageService.acknowledge(agentId, messageId);
            return ResultModel.success(true);
        } catch (Exception e) {
            log.error("[acknowledge] Failed: {}", e.getMessage());
            return ResultModel.error(500, "确认消息失败: " + e.getMessage());
        }
    }

    @PostMapping("/{agentId}/messages/{messageId}/read")
    public ResultModel<Boolean> markAsRead(
            @PathVariable String agentId,
            @PathVariable String messageId) {
        log.info("[markAsRead] Agent: {}, message: {}", agentId, messageId);
        
        try {
            messageService.markAsRead(agentId, messageId);
            return ResultModel.success(true);
        } catch (Exception e) {
            log.error("[markAsRead] Failed: {}", e.getMessage());
            return ResultModel.error(500, "标记已读失败: " + e.getMessage());
        }
    }

    @PostMapping("/{agentId}/messages/{messageId}/processed")
    public ResultModel<Boolean> markAsProcessed(
            @PathVariable String agentId,
            @PathVariable String messageId) {
        log.info("[markAsProcessed] Agent: {}, message: {}", agentId, messageId);
        
        try {
            messageService.markAsProcessed(agentId, messageId);
            return ResultModel.success(true);
        } catch (Exception e) {
            log.error("[markAsProcessed] Failed: {}", e.getMessage());
            return ResultModel.error(500, "标记已处理失败: " + e.getMessage());
        }
    }

    @GetMapping("/{agentId}/messages/pending")
    public ResultModel<Map<String, Object>> getPendingCount(@PathVariable String agentId) {
        log.debug("[getPendingCount] Agent: {}", agentId);
        
        try {
            int pending = messageService.getPendingCount(agentId);
            int unread = messageService.getUnreadCount(agentId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("agentId", agentId);
            result.put("pendingCount", pending);
            result.put("unreadCount", unread);
            
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[getPendingCount] Failed: {}", e.getMessage());
            return ResultModel.error(500, "获取待处理消息数失败: " + e.getMessage());
        }
    }

    @GetMapping("/messages/scene/{sceneGroupId}")
    public ResultModel<List<AgentMessageDTO>> getMessagesByScene(@PathVariable String sceneGroupId) {
        log.debug("[getMessagesByScene] Scene: {}", sceneGroupId);
        
        try {
            List<AgentMessageDTO> messages = messageService.getMessagesByScene(sceneGroupId);
            return ResultModel.success(messages);
        } catch (Exception e) {
            log.error("[getMessagesByScene] Failed: {}", e.getMessage());
            return ResultModel.error(500, "获取场景消息失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{agentId}/messages/{messageId}")
    public ResultModel<Boolean> deleteMessage(
            @PathVariable String agentId,
            @PathVariable String messageId) {
        log.info("[deleteMessage] Agent: {}, message: {}", agentId, messageId);
        
        try {
            boolean deleted = messageService.deleteMessage(messageId);
            if (!deleted) {
                return ResultModel.notFound("消息不存在");
            }
            return ResultModel.success(true);
        } catch (Exception e) {
            log.error("[deleteMessage] Failed: {}", e.getMessage());
            return ResultModel.error(500, "删除消息失败: " + e.getMessage());
        }
    }

    @PostMapping("/task-delegate")
    public ResultModel<Map<String, Object>> delegateTask(
            @RequestParam String fromAgent,
            @RequestParam String toAgent,
            @RequestParam(required = false) String sceneGroupId,
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestBody(required = false) Map<String, Object> payload) {
        log.info("[delegateTask] {} -> {}: {}", fromAgent, toAgent, title);
        
        try {
            String messageId = messageService.sendMessage(
                fromAgent, toAgent, sceneGroupId,
                MessageType.TASK_DELEGATE, title, content, payload, 5);
            
            Map<String, Object> result = new HashMap<>();
            result.put("messageId", messageId);
            result.put("type", MessageType.TASK_DELEGATE.name());
            
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[delegateTask] Failed: {}", e.getMessage());
            return ResultModel.error(500, "委派任务失败: " + e.getMessage());
        }
    }

    @PostMapping("/task-result")
    public ResultModel<Map<String, Object>> submitTaskResult(
            @RequestParam String fromAgent,
            @RequestParam String toAgent,
            @RequestParam(required = false) String sceneGroupId,
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestBody(required = false) Map<String, Object> payload) {
        log.info("[submitTaskResult] {} -> {}: {}", fromAgent, toAgent, title);
        
        try {
            String messageId = messageService.sendMessage(
                fromAgent, toAgent, sceneGroupId,
                MessageType.TASK_RESULT, title, content, payload, 5);
            
            Map<String, Object> result = new HashMap<>();
            result.put("messageId", messageId);
            result.put("type", MessageType.TASK_RESULT.name());
            
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[submitTaskResult] Failed: {}", e.getMessage());
            return ResultModel.error(500, "提交任务结果失败: " + e.getMessage());
        }
    }

    @PostMapping("/collab-request")
    public ResultModel<Map<String, Object>> requestCollaboration(
            @RequestParam String fromAgent,
            @RequestParam String toAgent,
            @RequestParam(required = false) String sceneGroupId,
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestBody(required = false) Map<String, Object> payload) {
        log.info("[requestCollaboration] {} -> {}: {}", fromAgent, toAgent, title);
        
        try {
            String messageId = messageService.sendMessage(
                fromAgent, toAgent, sceneGroupId,
                MessageType.COLLAB_REQUEST, title, content, payload, 5);
            
            Map<String, Object> result = new HashMap<>();
            result.put("messageId", messageId);
            result.put("type", MessageType.COLLAB_REQUEST.name());
            
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[requestCollaboration] Failed: {}", e.getMessage());
            return ResultModel.error(500, "请求协作失败: " + e.getMessage());
        }
    }

    @PostMapping("/data-share")
    public ResultModel<Map<String, Object>> shareData(
            @RequestParam String fromAgent,
            @RequestParam String toAgent,
            @RequestParam(required = false) String sceneGroupId,
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestBody(required = false) Map<String, Object> payload) {
        log.info("[shareData] {} -> {}: {}", fromAgent, toAgent, title);
        
        try {
            String messageId = messageService.sendMessage(
                fromAgent, toAgent, sceneGroupId,
                MessageType.DATA_SHARE, title, content, payload, 5);
            
            Map<String, Object> result = new HashMap<>();
            result.put("messageId", messageId);
            result.put("type", MessageType.DATA_SHARE.name());
            
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[shareData] Failed: {}", e.getMessage());
            return ResultModel.error(500, "共享数据失败: " + e.getMessage());
        }
    }
}

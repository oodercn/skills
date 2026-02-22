package net.ooder.skill.msg.controller;

import net.ooder.skill.msg.dto.*;
import net.ooder.skill.msg.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息服务REST API控制器
 */
@RestController
@RequestMapping("/api/msg")
public class MsgController {

    @Autowired
    private MsgService msgService;

    // 消息推送API
    @PostMapping("/push")
    public ResponseEntity<PushResult> push(@RequestBody Message message) {
        return ResponseEntity.ok(msgService.push(message));
    }

    @PostMapping("/push/batch")
    public ResponseEntity<List<PushResult>> batchPush(@RequestBody List<Message> messages) {
        return ResponseEntity.ok(msgService.batchPush(messages));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Message>> getHistory(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(msgService.getHistory(userId, startTime, endTime, page, size));
    }

    @GetMapping("/message/{messageId}")
    public ResponseEntity<Message> getMessage(@PathVariable String messageId) {
        Message message = msgService.getMessage(messageId);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(message);
    }

    // Topic管理API
    @GetMapping("/topic")
    public ResponseEntity<List<Topic>> listTopics() {
        return ResponseEntity.ok(msgService.listTopics());
    }

    @PostMapping("/topic")
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        return ResponseEntity.ok(msgService.createTopic(topic));
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<Topic> getTopic(@PathVariable String topicId) {
        Topic topic = msgService.getTopic(topicId);
        if (topic == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(topic);
    }

    @DeleteMapping("/topic/{topicId}")
    public ResponseEntity<Boolean> deleteTopic(@PathVariable String topicId) {
        return ResponseEntity.ok(msgService.deleteTopic(topicId));
    }

    @PostMapping("/topic/{topicId}/subscribe")
    public ResponseEntity<Boolean> subscribe(
            @PathVariable String topicId,
            @RequestBody Map<String, String> params) {
        String userId = params.get("userId");
        return ResponseEntity.ok(msgService.subscribe(topicId, userId));
    }

    @PostMapping("/topic/{topicId}/unsubscribe")
    public ResponseEntity<Boolean> unsubscribe(
            @PathVariable String topicId,
            @RequestBody Map<String, String> params) {
        String userId = params.get("userId");
        return ResponseEntity.ok(msgService.unsubscribe(topicId, userId));
    }

    @PostMapping("/topic/{topicId}/publish")
    public ResponseEntity<PushResult> publish(
            @PathVariable String topicId,
            @RequestBody Message message) {
        return ResponseEntity.ok(msgService.publish(topicId, message));
    }

    @GetMapping("/topic/{topicId}/subscribers")
    public ResponseEntity<List<String>> getSubscribers(@PathVariable String topicId) {
        return ResponseEntity.ok(msgService.getSubscribers(topicId));
    }

    // P2P通信API
    @PostMapping("/p2p/send")
    public ResponseEntity<PushResult> sendP2P(@RequestBody Message message) {
        return ResponseEntity.ok(msgService.sendP2P(message));
    }

    @GetMapping("/p2p/history")
    public ResponseEntity<List<Message>> getP2PHistory(
            @RequestParam(required = false) String fromUserId,
            @RequestParam(required = false) String toUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(msgService.getP2PHistory(fromUserId, toUserId, page, size));
    }

    // 统计API
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(msgService.getStatistics());
    }
}

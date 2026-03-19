package net.ooder.skill.msg.controller;

import net.ooder.skill.msg.dto.*;
import net.ooder.skill.msg.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/msg")
public class MsgController {

    @Autowired
    private MsgService msgService;

    @PostMapping("/send")
    public ResponseEntity<MsgResult> sendMessage(@RequestBody MsgSendRequest request) {
        MsgResult result = msgService.sendMessage(request);
        if (result.getSuccess()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/broadcast")
    public ResponseEntity<MsgResult> broadcastMessage(@RequestBody Map<String, Object> params) {
        String fromUserId = (String) params.get("fromUserId");
        String groupId = (String) params.get("groupId");
        String content = (String) params.get("content");
        MsgResult result = msgService.broadcastMessage(fromUserId, groupId, content);
        if (result.getSuccess()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/list")
    public ResponseEntity<List<MsgMessage>> getMessages(@RequestBody Map<String, Object> params) {
        String userId = (String) params.get("userId");
        Integer limit = params.get("limit") != null ? (Integer) params.get("limit") : 100;
        Long beforeTime = params.get("beforeTime") != null ? ((Number) params.get("beforeTime")).longValue() : null;
        return ResponseEntity.ok(msgService.getMessages(userId, limit, beforeTime));
    }

    @PostMapping("/read")
    public ResponseEntity<Boolean> markAsRead(@RequestBody Map<String, Object> params) {
        String messageId = (String) params.get("messageId");
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(msgService.markAsRead(messageId, userId));
    }

    @PostMapping("/recall")
    public ResponseEntity<Boolean> recallMessage(@RequestBody Map<String, Object> params) {
        String messageId = (String) params.get("messageId");
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(msgService.recallMessage(messageId, userId));
    }

    @PostMapping("/group/create")
    public ResponseEntity<MsgGroup> createGroup(@RequestBody Map<String, Object> params) {
        String name = (String) params.get("name");
        String type = (String) params.get("type");
        String ownerId = (String) params.get("ownerId");
        String ownerName = (String) params.get("ownerName");
        return ResponseEntity.ok(msgService.createGroup(name, type, ownerId, ownerName));
    }

    @PostMapping("/group/join")
    public ResponseEntity<Boolean> joinGroup(@RequestBody Map<String, Object> params) {
        String groupId = (String) params.get("groupId");
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(msgService.joinGroup(groupId, userId));
    }

    @PostMapping("/group/list")
    public ResponseEntity<List<MsgGroup>> listGroups(@RequestBody Map<String, Object> params) {
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(msgService.listGroups(userId));
    }
}

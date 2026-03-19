package net.ooder.skill.im.controller;

import net.ooder.skill.im.dto.*;
import net.ooder.skill.im.service.ImService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/im")
public class ImController {

    @Autowired
    private ImService imService;

    @PostMapping("/conversation/list")
    public ResponseEntity<List<Conversation>> getConversationList(@RequestBody Map<String, Object> params) {
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(imService.getConversationList(userId));
    }

    @PostMapping("/conversation/create")
    public ResponseEntity<Conversation> createConversation(@RequestBody Map<String, Object> params) {
        String type = (String) params.get("type");
        String targetId = (String) params.get("targetId");
        String name = (String) params.get("name");
        return ResponseEntity.ok(imService.createConversation(type, targetId, name));
    }

    @PostMapping("/conversation/read")
    public ResponseEntity<Boolean> markConversationRead(@RequestBody Map<String, Object> params) {
        String conversationId = (String) params.get("conversationId");
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(imService.markConversationRead(conversationId, userId));
    }

    @PostMapping("/conversation/unread")
    public ResponseEntity<UnreadSummary> getUnreadSummary(@RequestBody Map<String, Object> params) {
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(imService.getUnreadSummary(userId));
    }

    @PostMapping("/conversation/delete")
    public ResponseEntity<Boolean> deleteConversation(@RequestBody Map<String, Object> params) {
        String conversationId = (String) params.get("conversationId");
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(imService.deleteConversation(conversationId, userId));
    }

    @PostMapping("/contact/list")
    public ResponseEntity<List<Contact>> getContactList(@RequestBody Map<String, Object> params) {
        String userId = (String) params.get("userId");
        String group = (String) params.get("group");
        return ResponseEntity.ok(imService.getContactList(userId, group));
    }

    @PostMapping("/contact/search")
    public ResponseEntity<List<Contact>> searchContacts(@RequestBody Map<String, Object> params) {
        String userId = (String) params.get("userId");
        String keyword = (String) params.get("keyword");
        return ResponseEntity.ok(imService.searchContacts(userId, keyword));
    }

    @PostMapping("/contact/add")
    public ResponseEntity<Contact> addContact(@RequestBody Map<String, Object> params) {
        return ResponseEntity.ok(imService.addContact(params));
    }

    @PostMapping("/contact/update")
    public ResponseEntity<Boolean> updateContact(@RequestBody Map<String, Object> params) {
        String contactId = (String) params.get("contactId");
        return ResponseEntity.ok(imService.updateContact(contactId, params));
    }

    @PostMapping("/contact/delete")
    public ResponseEntity<Boolean> deleteContact(@RequestBody Map<String, Object> params) {
        String contactId = (String) params.get("contactId");
        return ResponseEntity.ok(imService.deleteContact(contactId));
    }

    @PostMapping("/contact/byDepartment")
    public ResponseEntity<Map<String, List<Contact>>> getContactsByDepartment(@RequestBody Map<String, Object> params) {
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(imService.getContactsByDepartment(userId));
    }
}

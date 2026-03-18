package net.ooder.skill.group.controller;

import net.ooder.skill.group.dto.*;
import net.ooder.skill.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/im/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/list")
    public ResponseEntity<List<Group>> getGroupList(@RequestBody Map<String, Object> params) {
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(groupService.getGroupList(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestBody Map<String, Object> params) {
        String name = (String) params.get("name");
        String ownerId = (String) params.get("ownerId");
        String ownerName = (String) params.get("ownerName");
        @SuppressWarnings("unchecked")
        List<String> memberIds = (List<String>) params.get("memberIds");
        String groupType = (String) params.getOrDefault("groupType", "normal");
        return ResponseEntity.ok(groupService.createGroup(name, ownerId, ownerName, memberIds, groupType));
    }

    @PostMapping("/get")
    public ResponseEntity<Group> getGroup(@RequestBody Map<String, Object> params) {
        String groupId = (String) params.get("groupId");
        Group group = groupService.getGroup(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(group);
    }

    @PostMapping("/members")
    public ResponseEntity<List<GroupMember>> getGroupMembers(@RequestBody Map<String, Object> params) {
        String groupId = (String) params.get("groupId");
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }

    @PostMapping("/addMember")
    public ResponseEntity<Boolean> addMember(@RequestBody Map<String, Object> params) {
        String groupId = (String) params.get("groupId");
        String userId = (String) params.get("userId");
        String userName = (String) params.get("userName");
        return ResponseEntity.ok(groupService.addMember(groupId, userId, userName));
    }

    @PostMapping("/removeMember")
    public ResponseEntity<Boolean> removeMember(@RequestBody Map<String, Object> params) {
        String groupId = (String) params.get("groupId");
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(groupService.removeMember(groupId, userId));
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean> updateGroup(@RequestBody Map<String, Object> params) {
        String groupId = (String) params.get("groupId");
        return ResponseEntity.ok(groupService.updateGroup(groupId, params));
    }

    @PostMapping("/dismiss")
    public ResponseEntity<Boolean> dismissGroup(@RequestBody Map<String, Object> params) {
        String groupId = (String) params.get("groupId");
        String userId = (String) params.get("userId");
        return ResponseEntity.ok(groupService.dismissGroup(groupId, userId));
    }

    @PostMapping("/setAnnouncement")
    public ResponseEntity<Boolean> setAnnouncement(@RequestBody Map<String, Object> params) {
        String groupId = (String) params.get("groupId");
        String announcement = (String) params.get("announcement");
        return ResponseEntity.ok(groupService.setAnnouncement(groupId, announcement));
    }

    @PostMapping("/setMemberRole")
    public ResponseEntity<Boolean> setMemberRole(@RequestBody Map<String, Object> params) {
        String groupId = (String) params.get("groupId");
        String userId = (String) params.get("userId");
        String role = (String) params.get("role");
        return ResponseEntity.ok(groupService.setMemberRole(groupId, userId, role));
    }
}

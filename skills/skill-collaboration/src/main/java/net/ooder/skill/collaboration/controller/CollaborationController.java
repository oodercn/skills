package net.ooder.skill.collaboration.controller;

import net.ooder.skill.collaboration.dto.*;
import net.ooder.skill.collaboration.service.CollaborationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaboration/scene")
public class CollaborationController {

    @Autowired
    private CollaborationService collaborationService;

    @PostMapping("/create")
    public ResponseEntity<CollaborationScene> createScene(@RequestBody SceneCreateRequest request) {
        CollaborationScene scene = collaborationService.createScene(request);
        return ResponseEntity.ok(scene);
    }

    @GetMapping("/list")
    public ResponseEntity<List<CollaborationScene>> listScenes(
            @RequestParam(required = false) String ownerId) {
        return ResponseEntity.ok(collaborationService.listScenes(ownerId));
    }

    @GetMapping("/{sceneId}")
    public ResponseEntity<CollaborationScene> getScene(@PathVariable String sceneId) {
        CollaborationScene scene = collaborationService.getScene(sceneId);
        if (scene == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(scene);
    }

    @PutMapping("/{sceneId}")
    public ResponseEntity<CollaborationScene> updateScene(
            @PathVariable String sceneId,
            @RequestBody SceneUpdateRequest request) {
        CollaborationScene scene = collaborationService.updateScene(sceneId, request);
        if (scene == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(scene);
    }

    @DeleteMapping("/{sceneId}")
    public ResponseEntity<Boolean> deleteScene(@PathVariable String sceneId) {
        boolean result = collaborationService.deleteScene(sceneId);
        if (!result) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping("/{sceneId}/member")
    public ResponseEntity<Boolean> addMember(
            @PathVariable String sceneId,
            @RequestBody MemberAddRequest request) {
        boolean result = collaborationService.addMember(sceneId, request);
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{sceneId}/member/{memberId}")
    public ResponseEntity<Boolean> removeMember(
            @PathVariable String sceneId,
            @PathVariable String memberId) {
        boolean result = collaborationService.removeMember(sceneId, memberId);
        if (!result) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/{sceneId}/members")
    public ResponseEntity<List<SceneMember>> getMembers(@PathVariable String sceneId) {
        if (collaborationService.getScene(sceneId) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(collaborationService.getMembers(sceneId));
    }

    @PostMapping("/{sceneId}/key")
    public ResponseEntity<SceneKeyResult> generateKey(@PathVariable String sceneId) {
        SceneKeyResult result = collaborationService.generateKey(sceneId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{sceneId}/status")
    public ResponseEntity<Boolean> changeStatus(
            @PathVariable String sceneId,
            @RequestParam SceneStatus status) {
        boolean result = collaborationService.changeStatus(sceneId, status);
        if (!result) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }
}

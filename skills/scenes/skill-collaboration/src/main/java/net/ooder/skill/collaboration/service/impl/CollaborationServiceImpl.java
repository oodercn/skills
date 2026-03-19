package net.ooder.skill.collaboration.service.impl;

import net.ooder.skill.collaboration.dto.*;
import net.ooder.skill.collaboration.service.CollaborationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CollaborationServiceImpl implements CollaborationService {

    private final Map<String, CollaborationScene> sceneCache = new ConcurrentHashMap<>();
    private final Map<String, List<SceneMember>> memberCache = new ConcurrentHashMap<>();
    private final Map<String, SceneKeyResult> keyCache = new ConcurrentHashMap<>();

    @Override
    public CollaborationScene createScene(SceneCreateRequest request) {
        String sceneId = "scene-" + UUID.randomUUID().toString().substring(0, 8);
        
        CollaborationScene scene = new CollaborationScene();
        scene.setSceneId(sceneId);
        scene.setName(request.getName());
        scene.setDescription(request.getDescription());
        scene.setOwnerId(request.getOwnerId());
        scene.setSkillIds(request.getSkillIds() != null ? request.getSkillIds() : new ArrayList<>());
        scene.setStatus(SceneStatus.CREATED);
        scene.setCreateTime(System.currentTimeMillis());
        scene.setUpdateTime(System.currentTimeMillis());
        
        List<SceneMember> members = new ArrayList<>();
        if (request.getOwnerName() != null) {
            SceneMember owner = new SceneMember();
            owner.setMemberId(request.getOwnerId());
            owner.setMemberName(request.getOwnerName());
            owner.setRole("owner");
            owner.setJoinedAt(System.currentTimeMillis());
            members.add(owner);
        }
        
        if (request.getMemberIds() != null) {
            for (String memberId : request.getMemberIds()) {
                if (!memberId.equals(request.getOwnerId())) {
                    SceneMember member = new SceneMember();
                    member.setMemberId(memberId);
                    member.setRole("member");
                    member.setJoinedAt(System.currentTimeMillis());
                    members.add(member);
                }
            }
        }
        
        scene.setMembers(members);
        sceneCache.put(sceneId, scene);
        memberCache.put(sceneId, members);
        
        return scene;
    }

    @Override
    public List<CollaborationScene> listScenes(String ownerId) {
        if (ownerId == null || ownerId.isEmpty()) {
            return new ArrayList<>(sceneCache.values());
        }
        
        List<CollaborationScene> result = new ArrayList<>();
        for (CollaborationScene scene : sceneCache.values()) {
            if (ownerId.equals(scene.getOwnerId())) {
                result.add(scene);
            } else {
                List<SceneMember> members = memberCache.get(scene.getSceneId());
                if (members != null) {
                    boolean isMember = members.stream()
                        .anyMatch(m -> ownerId.equals(m.getMemberId()));
                    if (isMember) {
                        result.add(scene);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public CollaborationScene getScene(String sceneId) {
        CollaborationScene scene = sceneCache.get(sceneId);
        if (scene != null) {
            List<SceneMember> members = memberCache.get(sceneId);
            scene.setMembers(members != null ? members : new ArrayList<>());
        }
        return scene;
    }

    @Override
    public CollaborationScene updateScene(String sceneId, SceneUpdateRequest request) {
        CollaborationScene scene = sceneCache.get(sceneId);
        if (scene == null) {
            return null;
        }
        
        if (request.getName() != null) {
            scene.setName(request.getName());
        }
        if (request.getDescription() != null) {
            scene.setDescription(request.getDescription());
        }
        if (request.getSkillIds() != null) {
            scene.setSkillIds(request.getSkillIds());
        }
        scene.setUpdateTime(System.currentTimeMillis());
        
        sceneCache.put(sceneId, scene);
        return scene;
    }

    @Override
    public boolean deleteScene(String sceneId) {
        if (!sceneCache.containsKey(sceneId)) {
            return false;
        }
        
        sceneCache.remove(sceneId);
        memberCache.remove(sceneId);
        keyCache.remove(sceneId);
        return true;
    }

    @Override
    public boolean addMember(String sceneId, MemberAddRequest request) {
        List<SceneMember> members = memberCache.get(sceneId);
        if (members == null) {
            return false;
        }
        
        boolean exists = members.stream()
            .anyMatch(m -> request.getMemberId().equals(m.getMemberId()));
        if (exists) {
            return false;
        }
        
        SceneMember member = new SceneMember();
        member.setMemberId(request.getMemberId());
        member.setMemberName(request.getMemberName());
        member.setRole(request.getRole() != null ? request.getRole() : "member");
        member.setJoinedAt(System.currentTimeMillis());
        
        members.add(member);
        return true;
    }

    @Override
    public boolean removeMember(String sceneId, String memberId) {
        List<SceneMember> members = memberCache.get(sceneId);
        if (members == null) {
            return false;
        }
        
        return members.removeIf(m -> memberId.equals(m.getMemberId()));
    }

    @Override
    public List<SceneMember> getMembers(String sceneId) {
        List<SceneMember> members = memberCache.get(sceneId);
        return members != null ? members : new ArrayList<>();
    }

    @Override
    public SceneKeyResult generateKey(String sceneId) {
        if (!sceneCache.containsKey(sceneId)) {
            return null;
        }
        
        String key = "sk-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        
        SceneKeyResult result = new SceneKeyResult();
        result.setSceneId(sceneId);
        result.setSceneKey(key);
        result.setCreateTime(System.currentTimeMillis());
        result.setExpireTime(System.currentTimeMillis() + 86400000L);
        
        keyCache.put(sceneId, result);
        
        CollaborationScene scene = sceneCache.get(sceneId);
        scene.setSceneKey(key);
        scene.setUpdateTime(System.currentTimeMillis());
        
        return result;
    }

    @Override
    public boolean changeStatus(String sceneId, SceneStatus status) {
        CollaborationScene scene = sceneCache.get(sceneId);
        if (scene == null) {
            return false;
        }
        
        scene.setStatus(status);
        scene.setUpdateTime(System.currentTimeMillis());
        return true;
    }
}

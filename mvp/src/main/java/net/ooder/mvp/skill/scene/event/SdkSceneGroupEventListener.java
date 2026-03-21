package net.ooder.mvp.skill.scene.event;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.participant.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class SdkSceneGroupEventListener implements Consumer<SceneGroup> {
    
    private static final Logger log = LoggerFactory.getLogger(SdkSceneGroupEventListener.class);
    
    @Override
    public void accept(SceneGroup sceneGroup) {
        log.info("[accept] SceneGroup event received: {}", sceneGroup.getSceneGroupId());
    }
    
    public void onMemberJoined(String sceneGroupId, Participant participant) {
        log.info("[onMemberJoined] Member {} joined scene group {}", 
            participant.getParticipantId(), sceneGroupId);
    }
    
    public void onMemberLeft(String sceneGroupId, String participantId) {
        log.info("[onMemberLeft] Member {} left scene group {}", participantId, sceneGroupId);
    }
    
    public void onStatusChanged(String sceneGroupId, String oldStatus, String newStatus) {
        log.info("[onStatusChanged] SceneGroup {} status changed: {} -> {}", 
            sceneGroupId, oldStatus, newStatus);
    }
    
    public void onCapabilityBound(String sceneGroupId, String capabilityId) {
        log.info("[onCapabilityBound] Capability {} bound to scene group {}", 
            capabilityId, sceneGroupId);
    }
    
    public void onCapabilityUnbound(String sceneGroupId, String capabilityId) {
        log.info("[onCapabilityUnbound] Capability {} unbound from scene group {}", 
            capabilityId, sceneGroupId);
    }
}

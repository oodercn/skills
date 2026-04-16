package net.ooder.skill.scenes.config;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.group.persistence.SceneGroupPersistence;
import net.ooder.scene.participant.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Configuration
public class PersistentSceneGroupConfig {

    private static final Logger log = LoggerFactory.getLogger(PersistentSceneGroupConfig.class);

    @Bean
    public SceneGroupPersistenceService sceneGroupPersistenceService(
            SceneGroupManager sceneGroupManager) {
        SceneGroupPersistence persistence = new CustomSceneGroupPersistence();
        log.info("[sceneGroupPersistenceService] Creating SceneGroupPersistenceService with CustomSceneGroupPersistence");
        return new SceneGroupPersistenceService(sceneGroupManager, persistence);
    }

    public static class SceneGroupPersistenceService {
        
        private static final Logger logger = LoggerFactory.getLogger(SceneGroupPersistenceService.class);
        
        private final SceneGroupManager sceneGroupManager;
        private final SceneGroupPersistence persistence;
        
        public SceneGroupPersistenceService(SceneGroupManager sceneGroupManager, 
                                            SceneGroupPersistence persistence) {
            this.sceneGroupManager = sceneGroupManager;
            this.persistence = persistence;
            logger.info("SceneGroupPersistenceService initialized. Persistence directory: {}", 
                SceneGroupPersistence.BASE_DIR);
            loadPersistedGroups();
        }
        
        public void loadPersistedGroups() {
            logger.info("============================================================");
            logger.info("Loading persisted scene groups...");
            logger.info("============================================================");
            
            try {
                List<String> groupIds = persistence.listAllSceneGroupIds();
                logger.info("Found {} persisted scene groups", groupIds.size());
                
                for (String groupId : groupIds) {
                    try {
                        Optional<SceneGroup> loaded = persistence.load(groupId);
                        if (loaded.isPresent()) {
                            SceneGroup group = loaded.get();
                            
                            SceneGroup existingGroup = sceneGroupManager.getSceneGroup(groupId);
                            if (existingGroup != null) {
                                logger.info("Scene group {} already exists in memory, skipping", groupId);
                                continue;
                            }
                            
                            SceneGroup recreatedGroup = sceneGroupManager.createSceneGroup(
                                groupId,
                                group.getTemplateId(),
                                group.getCreatorId(),
                                group.getCreatorType()
                            );
                            
                            recreatedGroup.setName(group.getName());
                            recreatedGroup.setDescription(group.getDescription());
                            
                            if (group.getStatus() == SceneGroup.Status.ACTIVE) {
                                recreatedGroup.activate();
                            } else if (group.getStatus() == SceneGroup.Status.SUSPENDED) {
                                recreatedGroup.suspend();
                            }
                            
                            List<Participant> participants = persistence.loadParticipants(groupId);
                            for (Participant p : participants) {
                                try {
                                    recreatedGroup.addParticipant(p);
                                    logger.debug("Added participant {} to group {}", 
                                        p.getParticipantId(), groupId);
                                } catch (Exception e) {
                                    logger.warn("Failed to add participant {} to group {}: {}", 
                                        p.getParticipantId(), groupId, e.getMessage());
                                }
                            }
                            
                            logger.info("Loaded scene group: {} with {} participants (status: {})", 
                                groupId, participants.size(), recreatedGroup.getStatus());
                        }
                    } catch (Exception e) {
                        logger.error("Failed to load scene group: {}", groupId, e);
                    }
                }
                
                logger.info("============================================================");
                logger.info("Scene group loading complete. Total groups in memory: {}", 
                    sceneGroupManager.getAllSceneGroups().size());
                logger.info("============================================================");
                
            } catch (IOException e) {
                logger.error("Failed to list persisted scene groups", e);
            }
        }
        
        @PreDestroy
        public void saveAllOnShutdown() {
            logger.info("============================================================");
            logger.info("Saving all scene groups before shutdown...");
            logger.info("============================================================");
            
            List<SceneGroup> groups = sceneGroupManager.getAllSceneGroups();
            logger.info("Found {} scene groups to save", groups.size());
            
            for (SceneGroup group : groups) {
                saveGroup(group);
            }
            
            logger.info("============================================================");
            logger.info("All scene groups saved successfully");
            logger.info("============================================================");
        }
        
        public void saveGroup(SceneGroup group) {
            try {
                persistence.save(group);
                persistence.saveParticipants(group.getSceneGroupId(), group.getAllParticipants());
                logger.debug("Saved scene group: {} (status: {}, participants: {})", 
                    group.getSceneGroupId(), 
                    group.getStatus(), 
                    group.getAllParticipants().size());
            } catch (IOException e) {
                logger.error("Failed to save scene group: {}", group.getSceneGroupId(), e);
            }
        }
        
        public void deleteGroup(String sceneGroupId) {
            try {
                persistence.delete(sceneGroupId);
                logger.info("Deleted persisted scene group: {}", sceneGroupId);
            } catch (IOException e) {
                logger.error("Failed to delete persisted scene group: {}", sceneGroupId, e);
            }
        }
    }
}

package net.ooder.skill.scenes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.persistence.SceneGroupPersistence;
import net.ooder.scene.participant.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CustomSceneGroupPersistence implements SceneGroupPersistence {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomSceneGroupPersistence.class);
    
    private static final String METADATA_FILE = "metadata.yaml";
    private static final String PARTICIPANTS_FILE = "participants.yaml";
    private static final String CONFIG_FILE = "config.yaml";
    
    private final ObjectMapper yamlMapper;
    
    public CustomSceneGroupPersistence() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.yamlMapper.registerModule(new JavaTimeModule());
        this.yamlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        logger.info("CustomSceneGroupPersistence initialized. Base directory: {}", BASE_DIR);
    }
    
    @Override
    public void save(SceneGroup group) throws IOException {
        Path groupDir = BASE_DIR.resolve(group.getSceneGroupId());
        Files.createDirectories(groupDir);
        
        saveMetadata(groupDir, group);
        saveConfig(groupDir, group);
        
        logger.debug("Saved SceneGroup: {}", group.getSceneGroupId());
    }
    
    @Override
    public Optional<SceneGroup> load(String sceneGroupId) throws IOException {
        Path groupDir = BASE_DIR.resolve(sceneGroupId);
        
        if (!Files.exists(groupDir)) {
            return Optional.empty();
        }
        
        return loadMetadata(groupDir, sceneGroupId);
    }
    
    @Override
    public void delete(String sceneGroupId) throws IOException {
        Path groupDir = BASE_DIR.resolve(sceneGroupId);
        
        if (Files.exists(groupDir)) {
            deleteDirectory(groupDir);
            logger.info("Deleted SceneGroup: {}", sceneGroupId);
        }
    }
    
    @Override
    public List<String> listAllSceneGroupIds() throws IOException {
        logger.info("listAllSceneGroupIds called. BASE_DIR: {}, exists: {}", BASE_DIR, Files.exists(BASE_DIR));
        if (!Files.exists(BASE_DIR)) {
            logger.warn("BASE_DIR does not exist: {}", BASE_DIR);
            return Collections.emptyList();
        }
        
        List<String> ids = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(BASE_DIR)) {
            for (Path entry : stream) {
                logger.debug("Found entry: {}, isDirectory: {}", entry, Files.isDirectory(entry));
                if (Files.isDirectory(entry)) {
                    ids.add(entry.getFileName().toString());
                }
            }
        }
        logger.info("Found {} scene group IDs", ids.size());
        return ids;
    }
    
    @Override
    public void saveParticipants(String sceneGroupId, List<Participant> participants) throws IOException {
        Path groupDir = BASE_DIR.resolve(sceneGroupId);
        Files.createDirectories(groupDir);
        
        Path participantsFile = groupDir.resolve(PARTICIPANTS_FILE);
        
        List<ParticipantData> participantDataList = new ArrayList<>();
        for (Participant p : participants) {
            ParticipantData data = new ParticipantData();
            data.participantId = p.getParticipantId();
            data.userId = p.getUserId();
            data.name = p.getName();
            data.type = p.getType() != null ? p.getType().name() : null;
            data.role = p.getRole() != null ? p.getRole().name() : null;
            data.status = p.getStatus() != null ? p.getStatus().name() : null;
            data.joinTime = p.getJoinTime() != null ? p.getJoinTime().toString() : null;
            data.lastHeartbeat = p.getLastHeartbeat() != null ? p.getLastHeartbeat().toString() : null;
            participantDataList.add(data);
        }
        
        String content = yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(participantDataList);
        Files.write(participantsFile, content.getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    public List<Participant> loadParticipants(String sceneGroupId) throws IOException {
        Path groupDir = BASE_DIR.resolve(sceneGroupId);
        Path participantsFile = groupDir.resolve(PARTICIPANTS_FILE);
        
        if (!Files.exists(participantsFile)) {
            return Collections.emptyList();
        }
        
        try {
            ParticipantData[] dataArray = yamlMapper.readValue(participantsFile.toFile(), ParticipantData[].class);
            List<Participant> participants = new ArrayList<>();
            
            for (ParticipantData data : dataArray) {
                Participant p = new Participant(
                    data.participantId,
                    data.userId,
                    data.name,
                    data.type != null ? Participant.Type.valueOf(data.type) : Participant.Type.USER
                );
                if (data.role != null) {
                    p.setRole(Participant.Role.valueOf(data.role));
                }
                if (data.status != null) {
                    setStatus(p, data.status);
                }
                participants.add(p);
            }
            
            return participants;
        } catch (Exception e) {
            logger.error("Failed to load participants for: {}", sceneGroupId, e);
            return Collections.emptyList();
        }
    }
    
    private void setStatus(Participant p, String status) {
        try {
            switch (status) {
                case "JOINED":
                    p.join();
                    break;
                case "ACTIVE":
                    p.join();
                    p.activate();
                    break;
                case "SUSPENDED":
                    p.join();
                    p.activate();
                    p.suspend();
                    break;
            }
        } catch (Exception e) {
            logger.warn("Failed to set participant status: {}", status);
        }
    }
    
    private void saveMetadata(Path groupDir, SceneGroup group) throws IOException {
        SceneGroupMetadata metadata = new SceneGroupMetadata();
        metadata.sceneGroupId = group.getSceneGroupId();
        metadata.templateId = group.getTemplateId();
        metadata.name = group.getName();
        metadata.description = group.getDescription();
        metadata.status = group.getStatus() != null ? group.getStatus().name() : null;
        metadata.creatorId = group.getCreatorId();
        metadata.creatorType = group.getCreatorType() != null ? group.getCreatorType().name() : null;
        metadata.createTime = group.getCreateTime() != null ? group.getCreateTime().toString() : null;
        metadata.lastUpdateTime = group.getLastUpdateTime() != null ? group.getLastUpdateTime().toString() : null;
        
        Path metadataFile = groupDir.resolve(METADATA_FILE);
        String content = yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata);
        Files.write(metadataFile, content.getBytes(StandardCharsets.UTF_8));
    }
    
    private Optional<SceneGroup> loadMetadata(Path groupDir, String sceneGroupId) throws IOException {
        Path metadataFile = groupDir.resolve(METADATA_FILE);
        
        if (!Files.exists(metadataFile)) {
            return Optional.empty();
        }
        
        try {
            SceneGroupMetadata metadata = yamlMapper.readValue(metadataFile.toFile(), SceneGroupMetadata.class);
            
            SceneGroup group = new SceneGroup(
                metadata.sceneGroupId,
                metadata.templateId,
                metadata.creatorId,
                metadata.creatorType != null ? SceneGroup.CreatorType.valueOf(metadata.creatorType) : SceneGroup.CreatorType.USER
            );
            
            group.setName(metadata.name);
            group.setDescription(metadata.description);
            
            if ("ACTIVE".equals(metadata.status)) {
                group.activate();
            } else if ("SUSPENDED".equals(metadata.status)) {
                group.suspend();
            }
            
            return Optional.of(group);
        } catch (Exception e) {
            logger.error("Failed to load metadata for: {}", sceneGroupId, e);
            return Optional.empty();
        }
    }
    
    private void saveConfig(Path groupDir, SceneGroup group) throws IOException {
        Path configFile = groupDir.resolve(CONFIG_FILE);
        
        java.util.Map<String, Object> config = new java.util.HashMap<>();
        config.put("name", group.getName());
        config.put("description", group.getDescription());
        
        String content = yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
        Files.write(configFile, content.getBytes(StandardCharsets.UTF_8));
    }
    
    private void deleteDirectory(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    deleteDirectory(entry);
                } else {
                    Files.delete(entry);
                }
            }
        }
        Files.delete(dir);
    }
    
    public static class SceneGroupMetadata {
        public String sceneGroupId;
        public String templateId;
        public String templateVersion;
        public String name;
        public String description;
        public String status;
        public String creatorId;
        public String creatorType;
        public String createTime;
        public String lastUpdateTime;
    }
    
    public static class ParticipantData {
        public String participantId;
        public String userId;
        public String name;
        public String type;
        public String role;
        public String status;
        public String joinTime;
        public String lastHeartbeat;
    }
}

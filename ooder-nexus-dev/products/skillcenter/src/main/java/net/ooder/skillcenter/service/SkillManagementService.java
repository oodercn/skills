package net.ooder.skillcenter.service;

import net.ooder.nexus.common.constants.SkillConstants;
import net.ooder.nexus.common.exceptions.SkillException;
import net.ooder.nexus.protocol.dto.ApiResponse;
import net.ooder.nexus.protocol.dto.SkillMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SkillManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(SkillManagementService.class);
    
    @Value("${nexus.skills.path:./skills}")
    private String skillsPath;
    
    @Value("${nexus.skills.source.path:../skills}")
    private String skillsSourcePath;
    
    @Value("${nexus.skill.index.path:./skill-index.yaml}")
    private String skillIndexPath;
    
    private final Map<String, SkillMetadata> localSkills = new ConcurrentHashMap<>();
    private final Map<String, SkillMetadata> remoteSkills = new ConcurrentHashMap<>();
    
    public void loadSkillIndex() {
        try {
            Path indexFile = Paths.get(skillIndexPath);
            if (Files.exists(indexFile)) {
                log.info("Loading skill index from: {}", skillIndexPath);
            } else {
                log.warn("Skill index file not found: {}", skillIndexPath);
            }
        } catch (Exception e) {
            log.error("Failed to load skill index", e);
        }
    }
    
    public void scanLocalSkills() {
        localSkills.clear();
        try {
            Path skillsDir = Paths.get(skillsPath);
            if (!Files.exists(skillsDir)) {
                Files.createDirectories(skillsDir);
                log.info("Created skills directory: {}", skillsPath);
                return;
            }
            
            Files.list(skillsDir)
                .filter(Files::isDirectory)
                .forEach(this::scanSkillDirectory);
                
            log.info("Scanned {} local skills", localSkills.size());
        } catch (Exception e) {
            log.error("Failed to scan local skills", e);
        }
    }
    
    private void scanSkillDirectory(Path skillDir) {
        Path skillYaml = skillDir.resolve(SkillConstants.SKILL_CONFIG_FILE);
        if (!Files.exists(skillYaml)) {
            return;
        }
        
        try (InputStream is = Files.newInputStream(skillYaml)) {
            SkillMetadata metadata = readSkillYaml(is);
            if (metadata != null && metadata.getSkillId() != null) {
                localSkills.put(metadata.getSkillId(), metadata);
                log.debug("Loaded skill: {}", metadata.getSkillId());
            }
        } catch (Exception e) {
            log.warn("Failed to load skill from: {}", skillDir, e);
        }
    }
    
    private SkillMetadata readSkillYaml(InputStream is) {
        try {
            java.util.Map<String, Object> yamlMap = new com.fasterxml.jackson.databind.ObjectMapper(
                new com.fasterxml.jackson.dataformat.yaml.YAMLFactory()
            ).readValue(is, java.util.Map.class);
            
            java.util.Map<String, Object> metadata = (java.util.Map<String, Object>) yamlMap.get("metadata");
            java.util.Map<String, Object> spec = (java.util.Map<String, Object>) yamlMap.get("spec");
            
            SkillMetadata skill = new SkillMetadata();
            skill.setSkillId((String) metadata.get("id"));
            skill.setName((String) metadata.get("name"));
            skill.setVersion((String) metadata.get("version"));
            skill.setDescription((String) metadata.get("description"));
            skill.setType((String) spec.get("type"));
            skill.setCategory((String) ((java.util.Map<String, Object>) spec.get("nexusUi")).get("category"));
            skill.setAuthor((String) metadata.get("author"));
            skill.setLicense((String) metadata.get("license"));
            
            java.util.List<String> keywords = (java.util.List<String>) metadata.get("keywords");
            skill.setKeywords(keywords != null ? keywords : new ArrayList<>());
            
            skill.setStatus("installed");
            skill.setInstalledAt(System.currentTimeMillis());
            skill.setUpdatedAt(System.currentTimeMillis());
            
            return skill;
        } catch (Exception e) {
            log.error("Failed to parse skill YAML", e);
            return null;
        }
    }
    
    public List<SkillMetadata> getLocalSkills() {
        return new ArrayList<>(localSkills.values());
    }
    
    public List<SkillMetadata> getRemoteSkills() {
        return new ArrayList<>(remoteSkills.values());
    }
    
    public List<SkillMetadata> getLocalSkillsByCategory(String category) {
        return localSkills.values().stream()
            .filter(s -> category.equals(s.getCategory()))
            .collect(Collectors.toList());
    }
    
    public List<SkillMetadata> getRemoteSkillsByCategory(String category) {
        return remoteSkills.values().stream()
            .filter(s -> category.equals(s.getCategory()))
            .collect(Collectors.toList());
    }
    
    public SkillMetadata getLocalSkill(String skillId) {
        return localSkills.get(skillId);
    }
    
    public SkillMetadata getRemoteSkill(String skillId) {
        return remoteSkills.get(skillId);
    }
    
    public boolean installSkill(String skillId) {
        if (localSkills.containsKey(skillId)) {
            log.info("Skill already installed locally: {}", skillId);
            return true;
        }
        
        SkillMetadata entry = getRemoteSkill(skillId);
        if (entry == null) {
            throw new SkillException(skillId, "Skill not found in index");
        }
        
        try {
            Path targetDir = Paths.get(skillsPath, skillId);
            Files.createDirectories(targetDir);
            
            boolean installed = false;
            
            Path sourceDir = Paths.get(skillsSourcePath, skillId);
            if (Files.exists(sourceDir)) {
                log.info("Copying skill from local source: {}", sourceDir);
                copyDirectory(sourceDir, targetDir);
                installed = true;
            } else {
                String downloadUrl = entry.getDownloadUrl();
                if (downloadUrl != null && !downloadUrl.isEmpty()) {
                    if (downloadUrl.endsWith(".zip")) {
                        downloadAndExtractZip(downloadUrl, skillId, targetDir);
                        installed = true;
                    } else if (downloadUrl.endsWith(".jar")) {
                        downloadJar(downloadUrl, skillId, targetDir);
                        installed = true;
                    }
                }
            }
            
            if (installed) {
                scanLocalSkills();
                log.info("Installed skill: {}", skillId);
                return true;
            } else {
                log.error("Failed to install skill: {}", skillId);
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to install skill: {}", skillId, e);
            throw new SkillException(skillId, "Failed to install skill", e);
        }
    }
    
    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(s -> {
            try {
                Path t = target.resolve(source.relativize(s));
                if (Files.isDirectory(s)) {
                    Files.createDirectories(t);
                } else {
                    Files.copy(s, t, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                log.warn("Failed to copy: {}", s, e);
            }
        });
    }
    
    private void downloadAndExtractZip(String url, String skillId, Path targetDir) throws IOException {
        log.info("Downloading skill from: {}", url);
        
        java.net.URL downloadUrl = new java.net.URL(url);
        Path zipFile = Files.createTempFile(skillId, ".zip");
        
        try (InputStream is = downloadUrl.openStream()) {
            Files.copy(is, zipFile, StandardCopyOption.REPLACE_EXISTING);
        }
        
        log.info("Downloaded to: {}", zipFile);
        
        java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(Files.newInputStream(zipFile));
        java.util.zip.ZipEntry entry;
        
        while ((entry = zis.getNextEntry()) != null) {
            Path entryPath = targetDir.resolve(entry.getName());
            if (entry.isDirectory()) {
                Files.createDirectories(entryPath);
            } else {
                Files.createDirectories(entryPath.getParent());
                Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
            }
            zis.closeEntry();
        }
        zis.close();
        
        Files.deleteIfExists(zipFile);
        log.info("Extracted skill to: {}", targetDir);
    }
    
    private void downloadJar(String url, String skillId, Path targetDir) throws IOException {
        log.info("Downloading JAR from: {}", url);
        
        java.net.URL downloadUrl = new java.net.URL(url);
        String fileName = skillId + ".jar";
        Path jarPath = targetDir.resolve(fileName);
        
        try (InputStream is = downloadUrl.openStream()) {
            Files.copy(is, jarPath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        log.info("Downloaded JAR to: {}", jarPath);
    }
    
    public boolean uninstallSkill(String skillId) {
        try {
            localSkills.remove(skillId);
            
            Path skillDir = Paths.get(skillsPath, skillId);
            if (Files.exists(skillDir)) {
                deleteDirectory(skillDir);
            }
            
            log.info("Uninstalled skill: {}", skillId);
            return true;
        } catch (Exception e) {
            log.error("Failed to uninstall skill: {}", skillId, e);
            throw new SkillException(skillId, "Failed to uninstall skill", e);
        }
    }
    
    private void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.list(path).forEach(p -> {
                try {
                    deleteDirectory(p);
                } catch (IOException e) {
                    log.warn("Failed to delete: {}", p, e);
                }
            });
        }
        Files.deleteIfExists(path);
    }
    
    public void refresh() {
        loadSkillIndex();
        scanLocalSkills();
    }
    
    public List<String> getCategories() {
        Set<String> categories = new HashSet<>();
        categories.addAll(localSkills.values().stream()
            .map(SkillMetadata::getCategory)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));
        categories.addAll(remoteSkills.values().stream()
            .map(SkillMetadata::getCategory)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));
        return new ArrayList<>(categories);
    }
}

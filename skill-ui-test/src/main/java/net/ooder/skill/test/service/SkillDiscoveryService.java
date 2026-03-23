package net.ooder.skill.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.ooder.skill.test.model.SkillIndex;
import net.ooder.skill.test.model.SkillIndex.SkillEntry;
import net.ooder.skill.test.model.SkillMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SkillDiscoveryService {
    
    private static final Logger log = LoggerFactory.getLogger(SkillDiscoveryService.class);
    
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final ObjectMapper jsonMapper = new ObjectMapper();
    
    @Value("${nexus.skills.path:./skills}")
    private String skillsPath;
    
    @Value("${nexus.skill-index.path:./skill-index.yaml}")
    private String skillIndexPath;
    
    @Value("${nexus.skills.source.path:../skills}")
    private String skillsSourcePath;
    
    private final Map<String, SkillMetadata> localSkills = new ConcurrentHashMap<>();
    private SkillIndex skillIndex;
    
    @PostConstruct
    public void init() {
        loadSkillIndex();
        scanLocalSkills();
    }
    
    public void loadSkillIndex() {
        try {
            File indexFile = new File(skillIndexPath);
            if (indexFile.exists()) {
                skillIndex = yamlMapper.readValue(indexFile, SkillIndex.class);
                log.info("Loaded skill index: {} skills, {} categories", 
                    skillIndex.getSkills().size(), skillIndex.getCategories().size());
            } else {
                log.warn("Skill index file not found: {}", skillIndexPath);
                skillIndex = new SkillIndex();
            }
        } catch (Exception e) {
            log.error("Failed to load skill index", e);
            skillIndex = new SkillIndex();
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
        Path skillYaml = skillDir.resolve("skill.yaml");
        if (!Files.exists(skillYaml)) {
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(skillYaml.toFile())) {
            SkillMetadata metadata = SkillMetadata.loadFromYaml(fis);
            if (metadata != null && metadata.getId() != null) {
                localSkills.put(metadata.getId(), metadata);
                log.debug("Loaded skill: {}", metadata.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to load skill from: {}", skillDir, e);
        }
    }
    
    public List<SkillIndex.Category> getCategories() {
        return skillIndex != null ? skillIndex.getCategories() : Collections.emptyList();
    }
    
    public List<SkillEntry> getRemoteSkills() {
        return skillIndex != null ? skillIndex.getSkills() : Collections.emptyList();
    }
    
    public List<SkillEntry> getRemoteSkillsByCategory(String category) {
        return getRemoteSkills().stream()
            .filter(s -> category.equals(s.getCategory()))
            .collect(Collectors.toList());
    }
    
    public List<SkillEntry> getNexusUiSkills() {
        return getRemoteSkillsByCategory("nexus-ui");
    }
    
    public List<SkillMetadata> getLocalSkills() {
        return new ArrayList<>(localSkills.values());
    }
    
    public List<SkillMetadata> getLocalNexusUiSkills() {
        return localSkills.values().stream()
            .filter(this::isNexusUiSkill)
            .collect(Collectors.toList());
    }
    
    public SkillMetadata getLocalSkill(String skillId) {
        return localSkills.get(skillId);
    }
    
    public SkillEntry getRemoteSkill(String skillId) {
        return getRemoteSkills().stream()
            .filter(s -> skillId.equals(s.getSkillId()))
            .findFirst()
            .orElse(null);
    }
    
    public boolean installSkill(String skillId) {
        if (localSkills.containsKey(skillId)) {
            log.info("Skill already installed locally: {}", skillId);
            return true;
        }
        
        SkillEntry entry = getRemoteSkill(skillId);
        if (entry == null) {
            log.error("Skill not found in index: {}", skillId);
            return false;
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
                if (downloadUrl == null || downloadUrl.isEmpty()) {
                    downloadUrl = entry.getGiteeDownloadUrl();
                }
                
                if (downloadUrl != null && !downloadUrl.isEmpty()) {
                    if (downloadUrl.endsWith(".zip")) {
                        downloadAndExtractZip(downloadUrl, skillId, targetDir);
                        installed = true;
                    } else if (downloadUrl.endsWith(".jar")) {
                        downloadJar(downloadUrl, skillId, targetDir);
                        installed = true;
                    } else {
                        log.error("Unsupported download format: {}", downloadUrl);
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
            return false;
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
        log.info("Copied directory from {} to {}", source, target);
    }
    
    private void downloadAndExtractZip(String url, String skillId, Path targetDir) throws IOException {
        log.info("Downloading skill from: {}", url);
        
        URL downloadUrl = new URL(url);
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
        
        URL downloadUrl = new URL(url);
        String fileName = skillId + "-" + ".jar";
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
            return false;
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
    
    private boolean isNexusUiSkill(SkillMetadata skill) {
        if (skill == null) {
            return false;
        }
        
        if ("nexus-ui".equals(skill.getType())) {
            return true;
        }
        
        Map<String, Object> ui = skill.getUi();
        return ui != null && ui.containsKey("nexusUi");
    }
    
    public void refresh() {
        loadSkillIndex();
        scanLocalSkills();
    }
}

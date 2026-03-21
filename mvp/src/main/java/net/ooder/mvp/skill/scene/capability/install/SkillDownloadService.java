package net.ooder.mvp.skill.scene.capability.install;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class SkillDownloadService {

    private static final Logger log = LoggerFactory.getLogger(SkillDownloadService.class);

    @Autowired
    private SkillDirectoryConfig directoryConfig;

    private final Yaml yaml = new Yaml();

    public static class DownloadResult {
        private boolean success;
        private String skillId;
        private String message;
        private Path skillPath;
        private Map<String, Object> skillMetadata;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Path getSkillPath() { return skillPath; }
        public void setSkillPath(Path skillPath) { this.skillPath = skillPath; }
        public Map<String, Object> getSkillMetadata() { return skillMetadata; }
        public void setSkillMetadata(Map<String, Object> skillMetadata) { this.skillMetadata = skillMetadata; }
    }

    public DownloadResult downloadFromUrl(String downloadUrl, String skillId) {
        log.info("[downloadFromUrl] Downloading skill from: {} with id: {}", downloadUrl, skillId);
        
        DownloadResult result = new DownloadResult();
        
        try {
            Path targetPath = directoryConfig.getSkillDownloadPath(skillId);
            
            if (Files.exists(targetPath)) {
                log.info("[downloadFromUrl] Skill already downloaded: {}", skillId);
                result.setSuccess(true);
                result.setSkillId(skillId);
                result.setSkillPath(targetPath);
                result.setMessage("技能已存在于下载目录");
                return result;
            }
            
            Files.createDirectories(targetPath);
            
            log.info("[downloadFromUrl] Download placeholder created for: {}", skillId);
            result.setSuccess(true);
            result.setSkillId(skillId);
            result.setSkillPath(targetPath);
            result.setMessage("下载任务已创建");
            
        } catch (Exception e) {
            log.error("[downloadFromUrl] Failed to download skill: {}", e.getMessage());
            result.setSuccess(false);
            result.setMessage("下载失败: " + e.getMessage());
        }
        
        return result;
    }

    public DownloadResult extractFromZip(Path zipPath, String skillId) {
        log.info("[extractFromZip] Extracting skill from: {} with id: {}", zipPath, skillId);
        
        DownloadResult result = new DownloadResult();
        
        try {
            Path targetPath = directoryConfig.getSkillDownloadPath(skillId);
            
            if (Files.exists(targetPath)) {
                deleteDirectory(targetPath);
            }
            
            Files.createDirectories(targetPath);
            
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath.toFile()))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path entryPath = targetPath.resolve(entry.getName());
                    
                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                    } else {
                        Files.createDirectories(entryPath.getParent());
                        Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zis.closeEntry();
                }
            }
            
            Map<String, Object> metadata = loadSkillMetadata(targetPath);
            
            result.setSuccess(true);
            result.setSkillId(skillId);
            result.setSkillPath(targetPath);
            result.setSkillMetadata(metadata);
            result.setMessage("解压成功");
            
            log.info("[extractFromZip] Skill extracted successfully: {}", skillId);
            
        } catch (Exception e) {
            log.error("[extractFromZip] Failed to extract skill: {}", e.getMessage());
            result.setSuccess(false);
            result.setMessage("解压失败: " + e.getMessage());
        }
        
        return result;
    }

    public DownloadResult copyFromSource(String skillId) {
        log.info("[copyFromSource] Copying skill from source: {}", skillId);
        
        DownloadResult result = new DownloadResult();
        
        try {
            Path sourceSkillPath = findSkillInSource(skillId);
            
            if (sourceSkillPath == null) {
                log.error("[copyFromSource] Skill not found in source: {}", skillId);
                result.setSuccess(false);
                result.setMessage("在源码目录中未找到技能: " + skillId);
                return result;
            }
            
            Path targetPath = directoryConfig.getSkillDownloadPath(skillId);
            
            if (Files.exists(targetPath)) {
                deleteDirectory(targetPath);
            }
            
            copyDirectory(sourceSkillPath, targetPath);
            
            Map<String, Object> metadata = loadSkillMetadata(targetPath);
            
            result.setSuccess(true);
            result.setSkillId(skillId);
            result.setSkillPath(targetPath);
            result.setSkillMetadata(metadata);
            result.setMessage("从源码目录复制成功");
            
            log.info("[copyFromSource] Skill copied successfully: {} -> {}", sourceSkillPath, targetPath);
            
        } catch (Exception e) {
            log.error("[copyFromSource] Failed to copy skill: {}", e.getMessage());
            result.setSuccess(false);
            result.setMessage("复制失败: " + e.getMessage());
        }
        
        return result;
    }

    private Path findSkillInSource(String skillId) throws IOException {
        Path sourceDir = directoryConfig.getSourceDir();
        
        if (!Files.exists(sourceDir)) {
            return null;
        }
        
        return Files.walk(sourceDir)
            .filter(Files::isDirectory)
            .filter(dir -> dir.getFileName().toString().equals(skillId))
            .filter(dir -> Files.exists(dir.resolve("skill.yaml")))
            .findFirst()
            .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadSkillMetadata(Path skillPath) {
        Path skillYamlPath = skillPath.resolve("skill.yaml");
        
        if (!Files.exists(skillYamlPath)) {
            return new HashMap<>();
        }
        
        try (FileInputStream fis = new FileInputStream(skillYamlPath.toFile())) {
            Map<String, Object> skillData = yaml.load(fis);
            if (skillData != null) {
                Map<String, Object> metadata = (Map<String, Object>) skillData.get("metadata");
                return metadata != null ? metadata : new HashMap<>();
            }
        } catch (Exception e) {
            log.warn("[loadSkillMetadata] Failed to load skill.yaml: {}", e.getMessage());
        }
        
        return new HashMap<>();
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source)
            .forEach(sourcePath -> {
                try {
                    Path relativePath = source.relativize(sourcePath);
                    Path targetPath = target.resolve(relativePath);
                    
                    if (Files.isDirectory(sourcePath)) {
                        Files.createDirectories(targetPath);
                    } else {
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy: " + sourcePath, e);
                }
            });
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        log.warn("[deleteDirectory] Failed to delete: {}", p);
                    }
                });
        }
    }

    public boolean isDownloaded(String skillId) {
        Path downloadPath = directoryConfig.getSkillDownloadPath(skillId);
        return Files.exists(downloadPath) && Files.exists(downloadPath.resolve("skill.yaml"));
    }

    public List<String> listDownloadedSkills() {
        List<String> skills = new ArrayList<>();
        Path downloadsDir = directoryConfig.getDownloadsDir();
        
        if (!Files.exists(downloadsDir)) {
            return skills;
        }
        
        try {
            Files.list(downloadsDir)
                .filter(Files::isDirectory)
                .filter(dir -> Files.exists(dir.resolve("skill.yaml")))
                .forEach(dir -> skills.add(dir.getFileName().toString()));
        } catch (IOException e) {
            log.error("[listDownloadedSkills] Failed to list downloads: {}", e.getMessage());
        }
        
        return skills;
    }

    public boolean deleteDownload(String skillId) {
        Path downloadPath = directoryConfig.getSkillDownloadPath(skillId);
        
        if (!Files.exists(downloadPath)) {
            return true;
        }
        
        try {
            deleteDirectory(downloadPath);
            log.info("[deleteDownload] Deleted download: {}", skillId);
            return true;
        } catch (IOException e) {
            log.error("[deleteDownload] Failed to delete: {}", e.getMessage());
            return false;
        }
    }
}

package net.ooder.skill.installer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class SkillInstallerService {
    
    @Value("${nexus.skills.path:./skills}")
    private String skillsPath;
    
    @Value("${nexus.skills.download-timeout:30000}")
    private int downloadTimeout;
    
    @Value("${nexus.skills.retry-count:3}")
    private int retryCount;
    
    private final Map<String, InstallProgress> installProgress = new ConcurrentHashMap<>();
    
    public enum InstallStatus {
        PENDING,
        DOWNLOADING,
        EXTRACTING,
        INSTALLING,
        COMPLETED,
        FAILED
    }
    
    public static class InstallProgress {
        private String skillId;
        private InstallStatus status;
        private int progress;
        private String message;
        private long totalBytes;
        private long downloadedBytes;
        private String error;
        
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public InstallStatus getStatus() { return status; }
        public void setStatus(InstallStatus status) { this.status = status; }
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTotalBytes() { return totalBytes; }
        public void setTotalBytes(long totalBytes) { this.totalBytes = totalBytes; }
        public long getDownloadedBytes() { return downloadedBytes; }
        public void setDownloadedBytes(long downloadedBytes) { this.downloadedBytes = downloadedBytes; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
    
    public InstallProgress getProgress(String skillId) {
        return installProgress.get(skillId);
    }
    
    public boolean installFromUrl(String skillId, String primaryUrl, String fallbackUrl) {
        InstallProgress progress = new InstallProgress();
        progress.setSkillId(skillId);
        progress.setStatus(InstallStatus.PENDING);
        progress.setMessage("Starting installation...");
        installProgress.put(skillId, progress);
        
        try {
            Path targetDir = Paths.get(skillsPath, skillId);
            Files.createDirectories(targetDir);
            
            progress.setStatus(InstallStatus.DOWNLOADING);
            progress.setMessage("Downloading from primary source...");
            
            File downloadedFile = downloadWithFallback(primaryUrl, fallbackUrl, skillId, progress);
            
            if (downloadedFile == null) {
                progress.setStatus(InstallStatus.FAILED);
                progress.setError("Failed to download from all sources");
                return false;
            }
            
            progress.setStatus(InstallStatus.EXTRACTING);
            progress.setMessage("Extracting files...");
            progress.setProgress(70);
            
            if (downloadedFile.getName().endsWith(".zip")) {
                extractZip(downloadedFile, targetDir);
            } else if (downloadedFile.getName().endsWith(".jar")) {
                Files.copy(downloadedFile.toPath(), targetDir.resolve(downloadedFile.getName()), StandardCopyOption.REPLACE_EXISTING);
            }
            
            downloadedFile.delete();
            
            progress.setStatus(InstallStatus.COMPLETED);
            progress.setMessage("Installation completed");
            progress.setProgress(100);
            
            log.info("Successfully installed skill: {}", skillId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to install skill: {}", skillId, e);
            progress.setStatus(InstallStatus.FAILED);
            progress.setError(e.getMessage());
            return false;
        }
    }
    
    private File downloadWithFallback(String primaryUrl, String fallbackUrl, String skillId, InstallProgress progress) {
        List<String> urls = new ArrayList<>();
        if (primaryUrl != null && !primaryUrl.isEmpty()) {
            urls.add(primaryUrl);
        }
        if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
            urls.add(fallbackUrl);
        }
        
        for (int attempt = 0; attempt < retryCount; attempt++) {
            for (String url : urls) {
                try {
                    log.info("Downloading skill {} from: {} (attempt {})", skillId, url, attempt + 1);
                    progress.setMessage("Downloading from: " + url);
                    
                    File file = downloadFile(url, skillId, progress);
                    if (file != null) {
                        return file;
                    }
                } catch (Exception e) {
                    log.warn("Failed to download from {}: {}", url, e.getMessage());
                }
            }
        }
        
        return null;
    }
    
    private File downloadFile(String urlStr, String skillId, InstallProgress progress) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(downloadTimeout);
        conn.setReadTimeout(downloadTimeout);
        conn.setRequestProperty("User-Agent", "Ooder-Skill-Installer/1.0");
        
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP response code: " + responseCode);
        }
        
        long contentLength = conn.getContentLengthLong();
        progress.setTotalBytes(contentLength);
        
        String fileName = skillId + (urlStr.endsWith(".zip") ? ".zip" : ".jar");
        File tempFile = Files.createTempFile(skillId, fileName.substring(fileName.lastIndexOf('.'))).toFile();
        
        try (InputStream is = conn.getInputStream();
             FileOutputStream fos = new FileOutputStream(tempFile)) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalRead = 0;
            
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                progress.setDownloadedBytes(totalRead);
                
                if (contentLength > 0) {
                    int percent = (int) (totalRead * 100 / contentLength);
                    progress.setProgress(10 + (int)(percent * 0.6));
                }
            }
        }
        
        conn.disconnect();
        log.info("Downloaded {} bytes to {}", tempFile.length(), tempFile);
        return tempFile;
    }
    
    private void extractZip(File zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
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
        }
        log.info("Extracted zip to: {}", targetDir);
    }
    
    public boolean uninstall(String skillId) {
        try {
            Path skillDir = Paths.get(skillsPath, skillId);
            if (Files.exists(skillDir)) {
                deleteDirectory(skillDir);
            }
            installProgress.remove(skillId);
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
    
    public boolean isInstalled(String skillId) {
        return Files.exists(Paths.get(skillsPath, skillId));
    }
    
    public List<String> listInstalled() {
        List<String> installed = new ArrayList<>();
        try {
            Path skillsDir = Paths.get(skillsPath);
            if (Files.exists(skillsDir)) {
                Files.list(skillsDir)
                    .filter(Files::isDirectory)
                    .forEach(p -> installed.add(p.getFileName().toString()));
            }
        } catch (Exception e) {
            log.error("Failed to list installed skills", e);
        }
        return installed;
    }
}

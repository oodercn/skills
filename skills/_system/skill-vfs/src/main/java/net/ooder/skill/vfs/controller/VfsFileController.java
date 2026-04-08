package net.ooder.skill.vfs.controller;

import net.ooder.skill.vfs.VfsService;
import net.ooder.skill.vfs.model.VfsFileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/chat/files")
public class VfsFileController {

    private static final Logger log = LoggerFactory.getLogger(VfsFileController.class);

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml",
        "application/pdf",
        "text/plain", "text/markdown", "text/csv",
        "application/json", "application/xml",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/zip", "application/x-rar-compressed", "application/x-7z-compressed"
    );

    @Autowired
    private VfsService vfsService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "scope", defaultValue = "CHAT_ATTACHMENT") String scope,
            @RequestParam(value = "refId", required = false) String refId) {
        log.info("[VfsFile] Upload attempt: {}, size={}, scope={}", file.getOriginalFilename(), file.getSize(), scope);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "文件不能为空"));
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("[VfsFile] File too large: {} bytes (max={})", file.getSize(), MAX_FILE_SIZE);
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of("error", "文件大小超过限制(最大50MB)", "maxSize", MAX_FILE_SIZE));
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("application/octet-stream") && !ALLOWED_TYPES.contains(contentType)) {
            log.warn("[VfsFile] Blocked unsupported type: {}", contentType);
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(Map.of("error", "不支持的文件类型: " + contentType, "allowedTypes", ALLOWED_TYPES));
        }

        String filename = file.getOriginalFilename();
        if (filename != null) {
            String lowerName = filename.toLowerCase();
            if (lowerName.endsWith(".exe") || lowerName.endsWith(".bat") || lowerName.endsWith(".sh")
                || lowerName.endsWith(".cmd") || lowerName.endsWith(".ps1")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "不允许上传可执行文件"));
            }
        }

        try {
            VfsFileMetadata metadata = vfsService.upload(file, scope, refId);
            return ResponseEntity.ok(metadata);
        } catch (Exception e) {
            log.error("[VfsFile] Upload failed", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "文件上传失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{fileId}/download")
    public void downloadFile(@PathVariable String fileId, HttpServletResponse response) {
        try {
            VfsFileMetadata meta = vfsService.getMetadata(fileId);
            if (meta == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            response.setContentType(meta.getMimeType());
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(meta.getOriginalName(), StandardCharsets.UTF_8));
            response.setContentLengthLong(meta.getSize());
            try (var is = vfsService.download(fileId)) { is.transferTo(response.getOutputStream()); }
        } catch (Exception e) { log.error("[VfsFile] Download failed: {}", fileId, e); }
    }

    @GetMapping("/{fileId}/preview")
    public ResponseEntity<VfsFileMetadata> previewFile(@PathVariable String fileId) {
        VfsFileMetadata meta = vfsService.getMetadata(fileId);
        if (meta == null) return ResponseEntity.notFound().build();
        meta.setPreviewUrl(vfsService.getPreviewUrl(fileId, 3600));
        return ResponseEntity.ok(meta);
    }

    @GetMapping("/{fileId}/thumbnail")
    public ResponseEntity<byte[]> thumbnail(@PathVariable String fileId) {
        VfsFileMetadata meta = vfsService.getMetadata(fileId);
        if (meta == null || !meta.isImage()) return ResponseEntity.notFound().build();
        try (var is = vfsService.download(fileId)) {
            byte[] data = is.readAllBytes();
            HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.parseMediaType(meta.getMimeType()));
            h.setCacheControl(CacheControl.maxAge(java.time.Duration.ofSeconds(86400)).cachePublic());
            return new ResponseEntity<>(data, h, HttpStatus.OK);
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
    }

    @GetMapping("/list")
    public ResponseEntity<List<VfsFileMetadata>> listFiles(@RequestParam String scope, @RequestParam String refId) {
        return ResponseEntity.ok(vfsService.listByRef(scope, refId));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileId) {
        return vfsService.delete(fileId) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

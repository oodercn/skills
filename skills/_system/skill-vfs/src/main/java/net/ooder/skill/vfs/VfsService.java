package net.ooder.skill.vfs;

import net.ooder.skill.vfs.model.VfsFileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface VfsService {

    VfsFileMetadata upload(MultipartFile file, String scope, String refId);

    VfsFileMetadata upload(InputStream inputStream, String originalFilename,
                           long size, String contentType, String scope, String refId);

    InputStream download(String fileId);

    VfsFileMetadata getMetadata(String fileId);

    boolean delete(String fileId);

    List<VfsFileMetadata> listByRef(String scope, String refId);

    String getPreviewUrl(String fileId, int expirySeconds);
}

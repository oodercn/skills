package net.ooder.vfs.sync;

import net.ooder.vfs.ct.CtVfsFactory;
import net.ooder.vfs.ct.CtVfsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Callable;

public class OptimizedUploadTask implements Callable<TaskResult> {

    private static final Logger log = LoggerFactory.getLogger(OptimizedUploadTask.class);

    private final String vfsPath;
    private final File localFile;
    private final SyncProgressListener listener;

    public OptimizedUploadTask(String vfsPath, File localFile, SyncProgressListener listener) {
        this.vfsPath = vfsPath;
        this.localFile = localFile;
        this.listener = listener;
    }

    @Override
    public TaskResult call() {
        TaskResult result = new TaskResult();
        result.setData(vfsPath);
        long startTime = System.currentTimeMillis();

        try {
            CtVfsService service = CtVfsFactory.getCtVfsService();

            net.ooder.vfs.FileInfo fileInfo = null;
            try {
                fileInfo = service.getFileByPath(vfsPath);
            } catch (Exception ignored) {
            }

            if (fileInfo == null) {
                fileInfo = service.createFile(vfsPath);
            }

            if (fileInfo != null) {
                service.upload(vfsPath, localFile, null);
                long elapsed = System.currentTimeMillis() - startTime;
                log.info("Uploaded: {} ({}bytes, {}ms)", vfsPath, localFile.length(), elapsed);

                if (listener != null) {
                    listener.onFileUploaded(vfsPath, localFile.length(), elapsed);
                }
                result.setResult(1);
            } else {
                log.error("Failed to create file record: {}", vfsPath);
                result.setResult(-1);
                if (listener != null) {
                    listener.onFileError(vfsPath, new RuntimeException("Failed to create file record"));
                }
            }
        } catch (Exception e) {
            log.error("Upload failed: {}", vfsPath, e);
            result.setResult(-1);
            if (listener != null) {
                listener.onFileError(vfsPath, e);
            }
        }

        return result;
    }
}

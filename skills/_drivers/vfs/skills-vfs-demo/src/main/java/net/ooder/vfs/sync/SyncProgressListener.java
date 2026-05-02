package net.ooder.vfs.sync;

public interface SyncProgressListener {
    void onFileScanned(int total, int current);
    void onFileUploaded(String path, long bytes, long elapsedMs);
    void onFileDownloaded(String path, long bytes, long elapsedMs);
    void onFileSkipped(String path, String reason);
    void onFileError(String path, Throwable error);
    void onComplete(SyncSummary summary);
}

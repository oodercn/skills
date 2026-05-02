package net.ooder.vfs.sync;

public class SyncSummary {
    private final int totalFiles;
    private final int uploaded;
    private final int downloaded;
    private final int skipped;
    private final int errors;
    private final long elapsedMs;

    public SyncSummary(int totalFiles, int uploaded, int downloaded, int skipped, int errors, long elapsedMs) {
        this.totalFiles = totalFiles;
        this.uploaded = uploaded;
        this.downloaded = downloaded;
        this.skipped = skipped;
        this.errors = errors;
        this.elapsedMs = elapsedMs;
    }

    public int getTotalFiles() { return totalFiles; }
    public int getUploaded() { return uploaded; }
    public int getDownloaded() { return downloaded; }
    public int getSkipped() { return skipped; }
    public int getErrors() { return errors; }
    public long getElapsedMs() { return elapsedMs; }

    @Override
    public String toString() {
        return String.format("SyncSummary{total=%d, uploaded=%d, downloaded=%d, skipped=%d, errors=%d, elapsed=%dms}",
            totalFiles, uploaded, downloaded, skipped, errors, elapsedMs);
    }
}

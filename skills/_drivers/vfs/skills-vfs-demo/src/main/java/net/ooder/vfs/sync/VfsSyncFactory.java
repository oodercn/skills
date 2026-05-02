package net.ooder.vfs.sync;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VfsSyncFactory {

    private static final Logger log = LoggerFactory.getLogger(VfsSyncFactory.class);

    private final ConcurrentHashMap<Path, SyncContext> syncContexts = new ConcurrentHashMap<>();
    private final PathMappingStrategy pathMapping;
    private final int defaultMaxTaskSize;
    private final String vfsRootPath;
    private final String localRootPath;

    private volatile static VfsSyncFactory instance;

    public static synchronized VfsSyncFactory getInstance() {
        if (instance == null) {
            instance = new VfsSyncFactory("/root/cediskroot/", ".", 50);
        }
        return instance;
    }

    public VfsSyncFactory(String vfsRootPath, String localRootPath, int defaultMaxTaskSize) {
        this.vfsRootPath = vfsRootPath;
        this.localRootPath = localRootPath;
        this.defaultMaxTaskSize = defaultMaxTaskSize;
        this.pathMapping = new DefaultPathMappingStrategy();
    }

    public static class SyncContext {
        final Path localPath;
        final String vfsPath;
        final AtomicInteger tasksSize = new AtomicInteger(0);
        final AtomicInteger updateSize = new AtomicInteger(0);
        final AtomicInteger errorSize = new AtomicInteger(0);
        final ConcurrentLinkedQueue<Callable<TaskResult>> pendingTasks = new ConcurrentLinkedQueue<>();
        volatile ScheduledExecutorService executor;

        SyncContext(Path localPath, String vfsPath) {
            this.localPath = localPath;
            this.vfsPath = vfsPath;
        }

        synchronized ScheduledExecutorService getOrCreateExecutor(int maxTaskSize) {
            if (executor == null || executor.isShutdown()) {
                int poolSize = Math.max(2, Math.min(maxTaskSize, Runtime.getRuntime().availableProcessors() * 2));
                executor = Executors.newScheduledThreadPool(poolSize, r -> {
                    Thread t = new Thread(r, "vfs-sync-" + localPath.getFileName());
                    t.setDaemon(true);
                    return t;
                });
            }
            return executor;
        }

        synchronized void shutdownExecutor() {
            if (executor != null && !executor.isShutdown()) {
                executor.shutdownNow();
                try {
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        log.warn("Executor for {} did not terminate in 5s", localPath);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private SyncContext getOrCreateContext(Path localPath, String vfsPath) {
        return syncContexts.computeIfAbsent(localPath, p -> new SyncContext(p, vfsPath));
    }

    public void push(Path localPath, String vfsPath) throws IOException {
        push(localPath, vfsPath, defaultMaxTaskSize, null);
    }

    public void push(Path localPath, String vfsPath, int maxTaskSize, SyncProgressListener listener) throws IOException {
        SyncContext ctx = getOrCreateContext(localPath, vfsPath);
        ctx.tasksSize.set(0);
        ctx.updateSize.set(0);
        ctx.errorSize.set(0);
        ctx.pendingTasks.clear();

        long startTime = System.currentTimeMillis();
        SyncDecisionEngine engine = new SyncDecisionEngine(SyncDecisionEngine.ConflictStrategy.LOCAL_WINS);

        OptimizedLocalFileVisitor visitor = new OptimizedLocalFileVisitor(
            vfsPath, localPath, pathMapping, engine, ctx, maxTaskSize, listener);

        java.nio.file.Files.walkFileTree(localPath, visitor);

        int totalTasks = ctx.pendingTasks.size();
        ctx.tasksSize.set(totalTasks);

        if (totalTasks == 0) {
            log.info("No files to sync");
            if (listener != null) {
                listener.onComplete(new SyncSummary(0, 0, 0, 0, 0, System.currentTimeMillis() - startTime));
            }
            return;
        }

        ScheduledExecutorService executor = ctx.getOrCreateExecutor(maxTaskSize);
        try {
            List<Callable<TaskResult>> taskList = new ArrayList<>(ctx.pendingTasks);
            List<Future<TaskResult>> futures = executor.invokeAll(taskList);

            int uploaded = 0;
            int errors = 0;
            for (Future<TaskResult> future : futures) {
                try {
                    TaskResult result = future.get(60, TimeUnit.SECONDS);
                    if (result.getResult() == 1) uploaded++;
                    else if (result.getResult() == -1) errors++;
                } catch (TimeoutException e) {
                    errors++;
                    log.error("Task timed out", e);
                } catch (Exception e) {
                    errors++;
                    log.error("Task failed", e);
                }
            }

            long elapsed = System.currentTimeMillis() - startTime;
            log.info("Push completed: total={}, uploaded={}, errors={}, elapsed={}ms",
                totalTasks, uploaded, errors, elapsed);

            if (listener != null) {
                listener.onComplete(new SyncSummary(totalTasks, uploaded, 0, 0, errors, elapsed));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Push interrupted", e);
        }
    }

    public void pull(Path localPath, String vfsPath) throws IOException {
        pull(localPath, vfsPath, defaultMaxTaskSize, null);
    }

    public void pull(Path localPath, String vfsPath, int maxTaskSize, SyncProgressListener listener) throws IOException {
        SyncContext ctx = getOrCreateContext(localPath, vfsPath);
        ctx.tasksSize.set(0);
        ctx.updateSize.set(0);
        ctx.errorSize.set(0);
        ctx.pendingTasks.clear();

        long startTime = System.currentTimeMillis();

        OptimizedSyncPull pullEngine = new OptimizedSyncPull(
            localPath, vfsPath, pathMapping, maxTaskSize, listener);
        pullEngine.execute();

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("Pull completed: elapsed={}ms", elapsed);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down VfsSyncFactory, closing {} executors", syncContexts.size());
        syncContexts.values().forEach(SyncContext::shutdownExecutor);
        syncContexts.clear();
    }

    public String getVfsRootPath() { return vfsRootPath; }
    public String getLocalRootPath() { return localRootPath; }
    public PathMappingStrategy getPathMapping() { return pathMapping; }
}

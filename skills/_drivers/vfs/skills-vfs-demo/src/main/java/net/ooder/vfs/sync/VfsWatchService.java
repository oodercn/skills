package net.ooder.vfs.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.*;

public class VfsWatchService implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(VfsWatchService.class);

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Path, Long> debounceMap = new ConcurrentHashMap<>();
    private final long debounceMs;
    private final VfsSyncFactory syncFactory;
    private final ExecutorService executor;
    private volatile boolean running = true;

    public VfsWatchService(VfsSyncFactory syncFactory, long debounceMs) throws IOException {
        this.syncFactory = syncFactory;
        this.debounceMs = debounceMs;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "vfs-watch-service");
            t.setDaemon(true);
            return t;
        });
    }

    public void registerTree(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                WatchKey key = dir.register(watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
                keys.put(key, dir);
                log.debug("Registered watch for: {}", dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void start() {
        executor.submit(this);
    }

    @Override
    public void run() {
        log.info("VfsWatchService started");
        while (running && !Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException | ClosedWatchServiceException e) {
                break;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                Path context = (Path) event.context();
                Path fullPath = dir.resolve(context);

                long now = System.currentTimeMillis();
                long last = debounceMap.getOrDefault(fullPath, 0L);
                if (now - last < debounceMs) {
                    continue;
                }
                debounceMap.put(fullPath, now);

                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    log.info("Detected deletion: {}", fullPath);
                    asyncDelete(fullPath);
                } else {
                    log.info("Detected change: {} ({})", fullPath, event.kind().name());
                    asyncPush(fullPath);
                }

                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE &&
                    fullPath.toFile().isDirectory()) {
                    try {
                        registerTree(fullPath);
                    } catch (IOException e) {
                        log.error("Failed to register new directory: {}", fullPath, e);
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
        log.info("VfsWatchService stopped");
    }

    private void asyncPush(Path localPath) {
        String vfsPath = syncFactory.getPathMapping().localToVfs(
            localPath.toFile().getAbsolutePath(),
            syncFactory.getLocalRootPath(),
            syncFactory.getVfsRootPath()
        );
        CompletableFuture.runAsync(() -> {
            try {
                syncFactory.push(localPath, vfsPath);
            } catch (IOException e) {
                log.error("Async push failed for: {}", localPath, e);
            }
        });
    }

    private void asyncDelete(Path localPath) {
        String vfsPath = syncFactory.getPathMapping().localToVfs(
            localPath.toFile().getAbsolutePath(),
            syncFactory.getLocalRootPath(),
            syncFactory.getVfsRootPath()
        );
        log.info("Delete sync for: {} -> {}", localPath, vfsPath);
    }

    public void shutdown() {
        running = false;
        executor.shutdownNow();
        try {
            watcher.close();
        } catch (IOException ignored) {
        }
    }
}

package net.ooder.vfs.jdbc.cache;

import net.ooder.common.cache.Cache;
import net.ooder.vfs.FileCopy;
import net.ooder.vfs.FileLink;
import net.ooder.vfs.FileVersion;
import net.ooder.vfs.FileView;
import net.ooder.vfs.engine.VFSRoManager;
import net.ooder.vfs.manager.inner.EIFileInfo;
import net.ooder.vfs.manager.inner.EIFolder;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

public class VfsRoManagerCacheAdapter implements VfsCacheProvider {

    private final VFSRoManager roManager;

    public VfsRoManagerCacheAdapter(VFSRoManager roManager) {
        this.roManager = roManager;
    }

    @Override
    public Map<String, EIFolder> getFolderCache() {
        return new CacheMapAdapter<>(roManager.getFolderCache());
    }

    @Override
    public Map<String, EIFileInfo> getFileCache() {
        return new CacheMapAdapter<>(roManager.getFileCache());
    }

    @Override
    public Map<String, FileVersion> getFileVersionCache() {
        return new CacheMapAdapter<>(roManager.getFileVersionCache());
    }

    @Override
    public Map<String, FileView> getFileViewCache() {
        return new CacheMapAdapter<>(roManager.getFileViewCache());
    }

    @Override
    public Map<String, FileLink> getFileLinkCache() {
        return new CacheMapAdapter<>(roManager.getFileLinkCache());
    }

    @Override
    public Map<String, FileCopy> getFileCopyCache() {
        return new CacheMapAdapter<>(roManager.getFileCopyCache());
    }

    private static class CacheMapAdapter<V> extends AbstractMap<String, V> {
        private final Cache<String, V> cache;

        CacheMapAdapter(Cache<String, V> cache) {
            this.cache = cache;
        }

        @Override
        public V get(Object key) {
            return cache.get(key);
        }

        @Override
        public V put(String key, V value) {
            V old = cache.get(key);
            cache.put(key, value);
            return old;
        }

        @Override
        public V remove(Object key) {
            V old = cache.get(key);
            cache.remove(key);
            return old;
        }

        @Override
        public boolean containsKey(Object key) {
            return cache.containsKey(key);
        }

        @Override
        public Set<Entry<String, V>> entrySet() {
            return cache.entrySet();
        }
    }
}

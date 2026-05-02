package net.ooder.vfs.sync;

public interface PathMappingStrategy {
    String localToVfs(String localPath, String localRoot, String vfsRoot);
    String vfsToLocal(String vfsPath, String localRoot, String vfsRoot);
}

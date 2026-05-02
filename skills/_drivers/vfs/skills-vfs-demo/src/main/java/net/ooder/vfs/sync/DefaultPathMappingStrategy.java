package net.ooder.vfs.sync;

public class DefaultPathMappingStrategy implements PathMappingStrategy {

    @Override
    public String localToVfs(String localPath, String localRoot, String vfsRoot) {
        String normalized = normalize(localPath);
        String root = normalizeDir(localRoot);
        if (normalized.startsWith(root)) {
            return normalizeDir(vfsRoot) + normalized.substring(root.length());
        }
        throw new IllegalArgumentException(
            "Local path '" + localPath + "' is not under root '" + localRoot + "'");
    }

    @Override
    public String vfsToLocal(String vfsPath, String localRoot, String vfsRoot) {
        String normalized = normalize(vfsPath);
        String root = normalizeDir(vfsRoot);
        if (normalized.startsWith(root)) {
            return normalizeDir(localRoot) + normalized.substring(root.length());
        }
        throw new IllegalArgumentException(
            "VFS path '" + vfsPath + "' is not under root '" + vfsRoot + "'");
    }

    private String normalize(String path) {
        return path.replace('\\', '/').replaceAll("//+", "/");
    }

    private String normalizeDir(String path) {
        String n = normalize(path);
        if (!n.endsWith("/")) {
            n = n + "/";
        }
        return n;
    }
}

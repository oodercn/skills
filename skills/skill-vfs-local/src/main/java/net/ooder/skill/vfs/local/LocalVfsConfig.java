package net.ooder.skill.vfs.local;

import net.ooder.common.CommonConfig;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.vfs.adapter.FileAdapter;

import java.io.File;

public class LocalVfsConfig {

    private static final Log log = LogFactory.getLog("vfs", LocalVfsConfig.class);

    private static String metaPath = "./data/vfs-meta";
    private static String filePath = "./data/vfs-files";
    private static FileAdapter fileAdapter;
    private static String rootPath;

    private static boolean initialized = false;

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        metaPath = getConfigValue("vfs.local.metaPath", metaPath);
        filePath = getConfigValue("vfs.local.filePath", filePath);
        rootPath = filePath;

        File metaDir = new File(metaPath);
        if (!metaDir.exists()) {
            metaDir.mkdirs();
        }
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        fileAdapter = new LocalFileAdapter(rootPath);
        initialized = true;
        log.info("LocalVfsConfig initialized: metaPath=" + metaPath + ", filePath=" + filePath);
    }

    public static String getConfigValue(String key, String defaultValue) {
        String value = CommonConfig.getValue(key);
        return value != null ? value : defaultValue;
    }

    public static String getMetaPath() {
        if (!initialized) {
            init();
        }
        return metaPath;
    }

    public static String getFilePath() {
        if (!initialized) {
            init();
        }
        return filePath;
    }

    public static FileAdapter getFileAdapter() {
        if (!initialized) {
            init();
        }
        return fileAdapter;
    }

    public static FileAdapter getFileAdapter(String customRootPath) {
        if (customRootPath == null || customRootPath.isEmpty()) {
            return getFileAdapter();
        }
        return new LocalFileAdapter(customRootPath);
    }

    public static String getRootPath() {
        if (!initialized) {
            init();
        }
        return rootPath;
    }

    public static void setMetaPath(String path) {
        metaPath = path;
    }

    public static void setFilePath(String path) {
        filePath = path;
        rootPath = path;
    }
}

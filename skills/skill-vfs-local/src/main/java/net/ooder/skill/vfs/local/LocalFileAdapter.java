package net.ooder.skill.vfs.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LocalFileAdapter implements net.ooder.vfs.adapter.FileAdapter {

    private static final Logger log = LoggerFactory.getLogger(LocalFileAdapter.class);
    
    private final String basePath;

    public LocalFileAdapter(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public boolean exists(String path) {
        if (basePath == null || path == null) {
            return false;
        }
        File file = new File(basePath, path);
        return file.exists();
    }

    @Override
    public boolean createDirectory(String path) {
        if (basePath == null || path == null) {
            return false;
        }
        File dir = new File(basePath, path);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    @Override
    public boolean delete(String path) {
        if (basePath == null || path == null) {
            return false;
        }
        File file = new File(basePath, path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    @Override
    public java.io.InputStream getInputStream(String path) throws java.io.IOException {
        if (basePath == null || path == null) {
            return null;
        }
        File file = new File(basePath, path);
        if (file.exists()) {
            return new java.io.FileInputStream(file);
        }
        return null;
    }

    @Override
    public java.io.OutputStream getOutputStream(String path) throws java.io.IOException {
        if (basePath == null || path == null) {
            return null;
        }
        File file = new File(basePath, path);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        return new java.io.FileOutputStream(file);
    }

    @Override
    public long getSize(String path) {
        if (basePath == null || path == null) {
            return 0;
        }
        File file = new File(basePath, path);
        return file.exists() ? file.length() : 0;
    }

    @Override
    public long getLastModified(String path) {
        if (basePath == null || path == null) {
            return 0;
        }
        File file = new File(basePath, path);
        return file.exists() ? file.lastModified() : 0;
    }

    @Override
    public String[] list(String path) {
        if (basePath == null || path == null) {
            return new String[0];
        }
        File dir = new File(basePath, path);
        if (dir.exists() && dir.isDirectory()) {
            return dir.list();
        }
        return new String[0];
    }
}

package net.ooder.skill.hotplug.model;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Skill包封装
 * 表示一个已打包的Skill JAR文件
 */
public class SkillPackage {

    private final File file;
    private final SkillMetadata metadata;

    public SkillPackage(File file, SkillMetadata metadata) {
        this.file = file;
        this.metadata = metadata;
    }

    /**
     * 从文件加载Skill包
     */
    public static SkillPackage fromFile(File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid skill package file: " + file);
        }

        // 读取skill.yaml中的元数据
        SkillMetadata metadata = loadMetadata(file);

        return new SkillPackage(file, metadata);
    }

    /**
     * 获取资源
     */
    public InputStream getResource(String path) throws IOException {
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry entry = jarFile.getJarEntry(path);
            if (entry != null) {
                // 读取数据到内存，避免流被关闭
                try (InputStream is = jarFile.getInputStream(entry)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    return new ByteArrayInputStream(baos.toByteArray());
                }
            }
        }
        return null;
    }

    /**
     * 获取资源URL
     */
    public URL getResourceUrl(String path) throws IOException {
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry entry = jarFile.getJarEntry(path);
            if (entry != null) {
                return new URL("jar:file:" + file.getAbsolutePath() + "!/" + path);
            }
        }
        return null;
    }

    /**
     * 列出所有资源
     */
    public java.util.List<String> listResources(String prefix) throws IOException {
        java.util.List<String> resources = new java.util.ArrayList<>();

        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(prefix)) {
                    resources.add(name);
                }
            }
        }

        return resources;
    }

    // ==================== 私有方法 ====================

    private static SkillMetadata loadMetadata(File jarFile) throws IOException {
        // 使用 getResource 方法读取 skill.yaml（已修复流关闭问题）
        SkillPackage tempPackage = new SkillPackage(jarFile, null);
        try (InputStream is = tempPackage.getResource("skill.yaml")) {
            if (is == null) {
                throw new IOException("skill.yaml not found in: " + jarFile);
            }
            return SkillMetadata.loadFromYaml(is);
        }
    }

    // Getters

    public File getFile() {
        return file;
    }

    public SkillMetadata getMetadata() {
        return metadata;
    }

    public String getVersion() {
        return metadata.getVersion();
    }

    @Override
    public String toString() {
        return "SkillPackage{" +
                "file=" + file.getName() +
                ", metadata=" + metadata +
                '}';
    }
}

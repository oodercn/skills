package net.ooder.nexus.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
    
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    
    public static void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(s -> {
            try {
                Path t = target.resolve(source.relativize(s));
                if (Files.isDirectory(s)) {
                    Files.createDirectories(t);
                } else {
                    Files.copy(s, t, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                log.warn("Failed to copy: {}", s, e);
            }
        });
    }
    
    public static void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.list(path).forEach(p -> {
                try {
                    deleteDirectory(p);
                } catch (IOException e) {
                    log.warn("Failed to delete: {}", p, e);
                }
            });
        }
        Files.deleteIfExists(path);
    }
    
    public static void extractZip(Path zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }
    
    public static <T> T readYaml(Path path, Class<T> clazz) throws IOException {
        if (!Files.exists(path)) {
            return null;
        }
        return YAML_MAPPER.readValue(path.toFile(), clazz);
    }
    
    public static <T> T readYaml(InputStream is, Class<T> clazz) throws IOException {
        return YAML_MAPPER.readValue(is, clazz);
    }
    
    public static void writeYaml(Path path, Object obj) throws IOException {
        YAML_MAPPER.writeValue(path.toFile(), obj);
    }
    
    public static <T> T readJson(Path path, Class<T> clazz) throws IOException {
        if (!Files.exists(path)) {
            return null;
        }
        return JSON_MAPPER.readValue(path.toFile(), clazz);
    }
    
    public static void writeJson(Path path, Object obj) throws IOException {
        JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), obj);
    }
    
    public static String readString(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }
    
    public static void writeString(Path path, String content) throws IOException {
        Files.write(path, content.getBytes());
    }
    
    public static List<Path> listDirectories(Path parent) throws IOException {
        if (!Files.exists(parent)) {
            return Collections.emptyList();
        }
        List<Path> dirs = new ArrayList<>();
        Files.list(parent)
            .filter(Files::isDirectory)
            .forEach(dirs::add);
        return dirs;
    }
    
    public static List<Path> listFiles(Path parent, String extension) throws IOException {
        if (!Files.exists(parent)) {
            return Collections.emptyList();
        }
        List<Path> files = new ArrayList<>();
        Files.list(parent)
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(extension))
            .forEach(files::add);
        return files;
    }
    
    private FileUtils() {}
}

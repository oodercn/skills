package net.ooder.skill.scene.spec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Skill Specification Validation Tests")
public class SkillSpecificationTest {

    private static final String SKILLS_DIR = System.getProperty("skills.dir", "../");
    private static final String EXPECTED_API_VERSION = "skill.ooder.net/v1";
    private static final String EXPECTED_KIND = "Skill";
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    
    private static List<File> skillYamlFiles;
    
    @BeforeAll
    static void setup() {
        File skillsRoot = new File(SKILLS_DIR);
        skillYamlFiles = findSkillYamlFiles(skillsRoot);
        System.out.println("Found " + skillYamlFiles.size() + " skill.yaml files");
        for (File f : skillYamlFiles) {
            System.out.println("  - " + f.getAbsolutePath());
        }
    }
    
    private static List<File> findSkillYamlFiles(File root) {
        List<File> result = new ArrayList<>();
        if (root.exists() && root.isDirectory()) {
            findFilesRecursive(root, "skill.yaml", result);
        }
        result.removeIf(f -> f.getAbsolutePath().contains(File.separator + "target" + File.separator));
        return result;
    }
    
    private static void findFilesRecursive(File dir, String fileName, List<File> result) {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                if (!file.getName().equals("target") && !file.getName().equals("node_modules") && !file.getName().equals("build")) {
                    findFilesRecursive(file, fileName, result);
                }
            } else if (file.getName().equals(fileName)) {
                result.add(file);
            }
        }
    }
    
    @Test
    @DisplayName("1.1 All skill.yaml files should have correct apiVersion")
    void testApiVersion() throws Exception {
        int passed = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        
        for (File skillFile : skillYamlFiles) {
            try {
                Map<String, Object> yaml = yamlMapper.readValue(skillFile, Map.class);
                String apiVersion = (String) yaml.get("apiVersion");
                
                if (EXPECTED_API_VERSION.equals(apiVersion)) {
                    passed++;
                } else {
                    failed++;
                    errors.add(skillFile.getAbsolutePath() + ": apiVersion=" + apiVersion);
                }
            } catch (Exception e) {
                failed++;
                errors.add(skillFile.getAbsolutePath() + ": " + e.getMessage());
            }
        }
        
        System.out.println("apiVersion Test: " + passed + " passed, " + failed + " failed");
        if (!errors.isEmpty()) {
            System.out.println("Errors: " + String.join(", ", errors));
        }
        
        assertEquals(0, failed, "Some files have incorrect apiVersion: " + String.join(", ", errors));
    }
    
    @Test
    @DisplayName("1.2 All skill.yaml files should have correct kind")
    void testKind() throws Exception {
        int passed = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        
        for (File skillFile : skillYamlFiles) {
            try {
                Map<String, Object> yaml = yamlMapper.readValue(skillFile, Map.class);
                String kind = (String) yaml.get("kind");
                
                if (EXPECTED_KIND.equals(kind)) {
                    passed++;
                } else {
                    failed++;
                    errors.add(skillFile.getParentFile().getName() + ": kind=" + kind);
                }
            } catch (Exception e) {
                failed++;
                errors.add(skillFile.getParentFile().getName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("kind Test: " + passed + " passed, " + failed + " failed");
        if (!errors.isEmpty()) {
            System.out.println("Errors: " + String.join(", ", errors));
        }
        
        assertEquals(0, failed, "Some files have incorrect kind: " + String.join(", ", errors));
    }
    
    @Test
    @DisplayName("1.3 All skill.yaml files should have metadata.id")
    void testMetadataId() throws Exception {
        int passed = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        
        for (File skillFile : skillYamlFiles) {
            try {
                Map<String, Object> yaml = yamlMapper.readValue(skillFile, Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> metadata = (Map<String, Object>) yaml.get("metadata");
                
                if (metadata != null && metadata.containsKey("id")) {
                    String id = (String) metadata.get("id");
                    if (id != null && !id.isEmpty()) {
                        passed++;
                    } else {
                        failed++;
                        errors.add(skillFile.getParentFile().getName() + ": id is empty");
                    }
                } else {
                    failed++;
                    errors.add(skillFile.getParentFile().getName() + ": missing metadata.id");
                }
            } catch (Exception e) {
                failed++;
                errors.add(skillFile.getParentFile().getName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("metadata.id Test: " + passed + " passed, " + failed + " failed");
        if (!errors.isEmpty()) {
            System.out.println("Errors: " + String.join(", ", errors));
        }
        
        assertEquals(0, failed, "Some files missing metadata.id: " + String.join(", ", errors));
    }
    
    @Test
    @DisplayName("1.4 All skill.yaml files should have capabilities in detailed format")
    void testCapabilitiesFormat() throws Exception {
        int passed = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        
        for (File skillFile : skillYamlFiles) {
            try {
                Map<String, Object> yaml = yamlMapper.readValue(skillFile, Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> spec = (Map<String, Object>) yaml.get("spec");
                
                if (spec != null && spec.containsKey("capabilities")) {
                    @SuppressWarnings("unchecked")
                    List<Object> capabilities = (List<Object>) spec.get("capabilities");
                    
                    boolean allDetailed = true;
                    for (Object cap : capabilities) {
                        if (cap instanceof String) {
                            allDetailed = false;
                            break;
                        } else if (cap instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> capMap = (Map<String, Object>) cap;
                            if (!capMap.containsKey("id") || !capMap.containsKey("name")) {
                                allDetailed = false;
                                break;
                            }
                        }
                    }
                    
                    if (allDetailed) {
                        passed++;
                    } else {
                        failed++;
                        errors.add(skillFile.getParentFile().getName() + ": capabilities not in detailed format");
                    }
                } else {
                    passed++;
                }
            } catch (Exception e) {
                failed++;
                errors.add(skillFile.getParentFile().getName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("capabilities format Test: " + passed + " passed, " + failed + " failed");
        if (!errors.isEmpty()) {
            System.out.println("Errors: " + String.join(", ", errors));
        }
        
        assertEquals(0, failed, "Some files have incorrect capabilities format: " + String.join(", ", errors));
    }
    
    @Test
    @DisplayName("1.5 All skill.yaml files should have spec.type")
    void testSpecType() throws Exception {
        int passed = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        Set<String> validTypes = new HashSet<>(Arrays.asList("service-skill", "tool-skill", "nexus-ui", "enterprise-skill", "system-service"));
        
        for (File skillFile : skillYamlFiles) {
            try {
                Map<String, Object> yaml = yamlMapper.readValue(skillFile, Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> spec = (Map<String, Object>) yaml.get("spec");
                
                if (spec != null && spec.containsKey("type")) {
                    String type = (String) spec.get("type");
                    if (validTypes.contains(type)) {
                        passed++;
                    } else {
                        failed++;
                        errors.add(skillFile.getParentFile().getName() + ": invalid type=" + type);
                    }
                } else {
                    failed++;
                    errors.add(skillFile.getParentFile().getName() + ": missing spec.type");
                }
            } catch (Exception e) {
                failed++;
                errors.add(skillFile.getParentFile().getName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("spec.type Test: " + passed + " passed, " + failed + " failed");
        if (!errors.isEmpty()) {
            System.out.println("Errors: " + String.join(", ", errors));
        }
        
        assertEquals(0, failed, "Some files have invalid spec.type: " + String.join(", ", errors));
    }
    
    @Test
    @DisplayName("1.6 All skill.yaml files should have endpoints with capability reference (warning)")
    void testEndpointsCapabilityReference() throws Exception {
        int passed = 0;
        int warning = 0;
        List<String> warnings = new ArrayList<>();
        
        for (File skillFile : skillYamlFiles) {
            try {
                Map<String, Object> yaml = yamlMapper.readValue(skillFile, Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> spec = (Map<String, Object>) yaml.get("spec");
                
                if (spec != null && spec.containsKey("endpoints")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> endpoints = (List<Map<String, Object>>) spec.get("endpoints");
                    
                    boolean allHaveCapability = true;
                    for (Map<String, Object> endpoint : endpoints) {
                        String path = (String) endpoint.get("path");
                        if (!endpoint.containsKey("capability")) {
                            if (path != null && !path.contains("/health")) {
                                allHaveCapability = false;
                            }
                        }
                    }
                    
                    if (allHaveCapability) {
                        passed++;
                    } else {
                        warning++;
                        warnings.add(skillFile.getParentFile().getName() + ": some endpoints missing capability reference");
                    }
                } else {
                    passed++;
                }
            } catch (Exception e) {
                warning++;
                warnings.add(skillFile.getParentFile().getName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("endpoints capability Test: " + passed + " passed, " + warning + " warnings");
        if (!warnings.isEmpty()) {
            System.out.println("Warnings: " + String.join(", ", warnings));
        }
        
        assertTrue(passed >= skillYamlFiles.size() * 0.8, 
            "At least 80% of files should have endpoints with capability reference");
    }
    
    @Test
    @DisplayName("1.7 No skill-manifest.yaml files should exist")
    void testNoSkillManifestFiles() {
        File skillsRoot = new File(SKILLS_DIR);
        List<File> manifestFiles = new ArrayList<>();
        findFilesRecursive(skillsRoot, "skill-manifest.yaml", manifestFiles);
        
        System.out.println("Found " + manifestFiles.size() + " skill-manifest.yaml files (should be 0)");
        if (!manifestFiles.isEmpty()) {
            String names = manifestFiles.stream()
                .map(f -> f.getParentFile().getName())
                .collect(Collectors.joining(", "));
            System.out.println("Files: " + names);
        }
        
        assertEquals(0, manifestFiles.size(), "skill-manifest.yaml files should not exist: " + 
            manifestFiles.stream().map(f -> f.getParentFile().getName()).collect(Collectors.joining(", ")));
    }
    
    @Test
    @DisplayName("1.8 Generate specification compliance report")
    void generateComplianceReport() throws Exception {
        int totalFiles = skillYamlFiles.size();
        int compliantFiles = 0;
        List<String> nonCompliantFiles = new ArrayList<>();
        
        for (File skillFile : skillYamlFiles) {
            try {
                Map<String, Object> yaml = yamlMapper.readValue(skillFile, Map.class);
                
                boolean isCompliant = true;
                
                String apiVersion = (String) yaml.get("apiVersion");
                if (!EXPECTED_API_VERSION.equals(apiVersion)) isCompliant = false;
                
                String kind = (String) yaml.get("kind");
                if (!EXPECTED_KIND.equals(kind)) isCompliant = false;
                
                @SuppressWarnings("unchecked")
                Map<String, Object> metadata = (Map<String, Object>) yaml.get("metadata");
                if (metadata == null || !metadata.containsKey("id")) isCompliant = false;
                
                @SuppressWarnings("unchecked")
                Map<String, Object> spec = (Map<String, Object>) yaml.get("spec");
                if (spec == null || !spec.containsKey("type")) isCompliant = false;
                
                if (isCompliant) {
                    compliantFiles++;
                } else {
                    nonCompliantFiles.add(skillFile.getParentFile().getName());
                }
            } catch (Exception e) {
                nonCompliantFiles.add(skillFile.getParentFile().getName() + " (error: " + e.getMessage() + ")");
            }
        }
        
        double complianceRate = totalFiles > 0 ? (compliantFiles * 100.0 / totalFiles) : 0;
        
        System.out.println("\n========================================");
        System.out.println("Skill Specification Compliance Report");
        System.out.println("========================================");
        System.out.println("Total files: " + totalFiles);
        System.out.println("Compliant files: " + compliantFiles);
        System.out.println("Non-compliant files: " + nonCompliantFiles.size());
        System.out.println("Compliance rate: " + String.format("%.1f%%", complianceRate));
        
        if (!nonCompliantFiles.isEmpty()) {
            System.out.println("\nNon-compliant files:");
            nonCompliantFiles.forEach(f -> System.out.println("  - " + f));
        }
        System.out.println("========================================\n");
        
        assertTrue(complianceRate >= 95.0, 
            "Compliance rate should be >= 95%, but was " + complianceRate + "%");
    }
}

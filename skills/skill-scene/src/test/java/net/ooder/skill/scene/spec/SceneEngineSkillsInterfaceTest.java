package net.ooder.skill.scene.spec;

import net.ooder.scene.SceneEngine;
import net.ooder.scene.SceneEngineImpl;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.model.SkillPackage;
import net.ooder.skills.api.model.Capability;
import net.ooder.skills.core.impl.SkillRegistryImpl;
import net.ooder.skills.core.discovery.LocalDiscoverer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SceneEngine Skills Specification Interface Tests")
public class SceneEngineSkillsInterfaceTest {

    private SkillRegistry skillRegistry;
    private SkillDiscoverer skillDiscoverer;
    
    @BeforeEach
    void setUp() {
        skillRegistry = new SkillRegistryImpl();
        skillDiscoverer = new LocalDiscoverer();
    }
    
    @Test
    @DisplayName("2.1 SkillRegistry should be initialized")
    void testSkillRegistryInitialization() {
        assertNotNull(skillRegistry, "SkillRegistry should not be null");
        System.out.println("SkillRegistry initialized successfully");
    }
    
    @Test
    @DisplayName("2.2 SkillDiscoverer should be initialized")
    void testSkillDiscovererInitialization() {
        assertNotNull(skillDiscoverer, "SkillDiscoverer should not be null");
        System.out.println("SkillDiscoverer initialized successfully");
    }
    
    @Test
    @DisplayName("2.3 SkillRegistry should register skill package")
    void testSkillRegistryRegister() {
        SkillPackage skillPackage = createTestSkillPackage();
        
        skillRegistry.register(skillPackage);
        
        Optional<SkillPackage> retrieved = skillRegistry.getSkill(skillPackage.getId());
        assertTrue(retrieved.isPresent(), "Skill should be registered");
        assertEquals(skillPackage.getId(), retrieved.get().getId());
        
        System.out.println("Skill registered and retrieved: " + skillPackage.getId());
    }
    
    @Test
    @DisplayName("2.4 SkillRegistry should list all skills")
    void testSkillRegistryListAll() {
        SkillPackage skill1 = createTestSkillPackage("test-skill-1", "Test Skill 1");
        SkillPackage skill2 = createTestSkillPackage("test-skill-2", "Test Skill 2");
        
        skillRegistry.register(skill1);
        skillRegistry.register(skill2);
        
        List<SkillPackage> allSkills = skillRegistry.getAllSkills();
        assertFalse(allSkills.isEmpty(), "Skills list should not be empty");
        assertTrue(allSkills.size() >= 2, "Should have at least 2 skills");
        
        System.out.println("Total skills registered: " + allSkills.size());
    }
    
    @Test
    @DisplayName("2.5 SkillRegistry should find skill by capability")
    void testSkillRegistryFindByCapability() {
        SkillPackage skillPackage = createTestSkillPackage();
        skillRegistry.register(skillPackage);
        
        List<SkillPackage> skills = skillRegistry.findByCapability("test-capability");
        assertFalse(skills.isEmpty(), "Should find skill by capability");
        
        System.out.println("Found " + skills.size() + " skills with capability: test-capability");
    }
    
    @Test
    @DisplayName("2.6 SkillDiscoverer should discover local skills")
    void testSkillDiscovererDiscoverLocal(@TempDir Path tempDir) throws Exception {
        File skillYaml = tempDir.resolve("skill.yaml").toFile();
        String yamlContent = createTestSkillYaml();
        Files.write(skillYaml.toPath(), yamlContent.getBytes());
        
        List<SkillPackage> discovered = skillDiscoverer.discover(tempDir.toFile());
        
        assertNotNull(discovered, "Discovered skills should not be null");
        System.out.println("Discovered " + discovered.size() + " skills from temp directory");
    }
    
    @Test
    @DisplayName("2.7 SkillPackage should have valid capabilities")
    void testSkillPackageCapabilities() {
        SkillPackage skillPackage = createTestSkillPackage();
        
        List<Capability> capabilities = skillPackage.getCapabilities();
        assertNotNull(capabilities, "Capabilities should not be null");
        assertFalse(capabilities.isEmpty(), "Capabilities should not be empty");
        
        for (Capability cap : capabilities) {
            assertNotNull(cap.getId(), "Capability id should not be null");
            assertNotNull(cap.getName(), "Capability name should not be null");
            assertNotNull(cap.getCategory(), "Capability category should not be null");
            
            System.out.println("Capability: " + cap.getId() + " - " + cap.getName());
        }
    }
    
    @Test
    @DisplayName("2.8 SkillPackage should have valid metadata")
    void testSkillPackageMetadata() {
        SkillPackage skillPackage = createTestSkillPackage();
        
        assertEquals("test-skill", skillPackage.getId());
        assertEquals("Test Skill", skillPackage.getName());
        assertNotNull(skillPackage.getVersion(), "Version should not be null");
        assertNotNull(skillPackage.getDescription(), "Description should not be null");
        assertNotNull(skillPackage.getAuthor(), "Author should not be null");
        
        System.out.println("Skill metadata validated: " + skillPackage.getId());
    }
    
    private SkillPackage createTestSkillPackage() {
        return createTestSkillPackage("test-skill", "Test Skill");
    }
    
    private SkillPackage createTestSkillPackage(String id, String name) {
        SkillPackage skillPackage = new SkillPackage();
        skillPackage.setId(id);
        skillPackage.setName(name);
        skillPackage.setVersion("1.0.0");
        skillPackage.setDescription("Test skill for unit testing");
        skillPackage.setAuthor("Test Author");
        skillPackage.setType("service-skill");
        
        Capability capability = new Capability();
        capability.setId("test-capability");
        capability.setName("Test Capability");
        capability.setDescription("Test capability for unit testing");
        capability.setCategory("test");
        
        skillPackage.setCapabilities(List.of(capability));
        
        return skillPackage;
    }
    
    private String createTestSkillYaml() {
        return "apiVersion: skill.ooder.net/v1\n" +
               "kind: Skill\n" +
               "\n" +
               "metadata:\n" +
               "  id: test-skill-yaml\n" +
               "  name: Test Skill YAML\n" +
               "  version: 1.0.0\n" +
               "  description: Test skill from YAML\n" +
               "  author: Test Author\n" +
               "\n" +
               "spec:\n" +
               "  type: service-skill\n" +
               "  capabilities:\n" +
               "    - id: test-capability-yaml\n" +
               "      name: Test Capability YAML\n" +
               "      description: Test capability from YAML\n" +
               "      category: test\n";
    }
}

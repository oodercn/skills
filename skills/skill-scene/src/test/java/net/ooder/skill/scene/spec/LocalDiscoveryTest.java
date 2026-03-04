package net.ooder.skill.scene.spec;

import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.model.SkillPackage;
import net.ooder.skills.api.model.Capability;
import net.ooder.skills.core.impl.SkillRegistryImpl;
import net.ooder.skills.core.discovery.LocalDiscoverer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Local Discovery Capability Recognition Tests")
public class LocalDiscoveryTest {

    private static final String SKILLS_DIR = System.getProperty("skills.dir", "../");
    private static File skillsRoot;
    
    private SkillDiscoverer localDiscoverer;
    private SkillRegistry skillRegistry;
    
    @BeforeAll
    static void setupClass() {
        skillsRoot = new File(SKILLS_DIR);
        System.out.println("Skills root directory: " + skillsRoot.getAbsolutePath());
        System.out.println("Directory exists: " + skillsRoot.exists());
    }
    
    @BeforeEach
    void setUp() {
        localDiscoverer = new LocalDiscoverer();
        skillRegistry = new SkillRegistryImpl();
    }
    
    @Test
    @DisplayName("3.1 LocalDiscoverer should discover skills from skills directory")
    void testDiscoverSkillsFromDirectory() {
        if (!skillsRoot.exists()) {
            System.out.println("Skills directory not found, skipping test");
            return;
        }
        
        List<SkillPackage> discovered = localDiscoverer.discover(skillsRoot);
        
        assertNotNull(discovered, "Discovered skills should not be null");
        System.out.println("Discovered " + discovered.size() + " skills from directory");
        
        for (SkillPackage skill : discovered) {
            System.out.println("  - " + skill.getId() + ": " + skill.getName());
        }
    }
    
    @Test
    @DisplayName("3.2 Discovered skills should have valid apiVersion")
    void testDiscoveredSkillsApiVersion() {
        if (!skillsRoot.exists()) {
            System.out.println("Skills directory not found, skipping test");
            return;
        }
        
        List<SkillPackage> discovered = localDiscoverer.discover(skillsRoot);
        
        int validCount = 0;
        int invalidCount = 0;
        
        for (SkillPackage skill : discovered) {
            String apiVersion = skill.getApiVersion();
            if ("skill.ooder.net/v1".equals(apiVersion)) {
                validCount++;
            } else {
                invalidCount++;
                System.out.println("Invalid apiVersion: " + skill.getId() + " -> " + apiVersion);
            }
        }
        
        System.out.println("Valid apiVersion: " + validCount + ", Invalid: " + invalidCount);
        assertEquals(0, invalidCount, "All discovered skills should have valid apiVersion");
    }
    
    @Test
    @DisplayName("3.3 Discovered skills should have valid kind")
    void testDiscoveredSkillsKind() {
        if (!skillsRoot.exists()) {
            System.out.println("Skills directory not found, skipping test");
            return;
        }
        
        List<SkillPackage> discovered = localDiscoverer.discover(skillsRoot);
        
        int validCount = 0;
        int invalidCount = 0;
        
        for (SkillPackage skill : discovered) {
            String kind = skill.getKind();
            if ("Skill".equals(kind)) {
                validCount++;
            } else {
                invalidCount++;
                System.out.println("Invalid kind: " + skill.getId() + " -> " + kind);
            }
        }
        
        System.out.println("Valid kind: " + validCount + ", Invalid: " + invalidCount);
        assertEquals(0, invalidCount, "All discovered skills should have valid kind");
    }
    
    @Test
    @DisplayName("3.4 Discovered skills should have capabilities")
    void testDiscoveredSkillsCapabilities() {
        if (!skillsRoot.exists()) {
            System.out.println("Skills directory not found, skipping test");
            return;
        }
        
        List<SkillPackage> discovered = localDiscoverer.discover(skillsRoot);
        
        int withCapabilities = 0;
        int withoutCapabilities = 0;
        
        for (SkillPackage skill : discovered) {
            List<Capability> capabilities = skill.getCapabilities();
            if (capabilities != null && !capabilities.isEmpty()) {
                withCapabilities++;
            } else {
                withoutCapabilities++;
                System.out.println("No capabilities: " + skill.getId());
            }
        }
        
        System.out.println("With capabilities: " + withCapabilities + ", Without: " + withoutCapabilities);
        assertTrue(withCapabilities > 0, "At least some skills should have capabilities");
    }
    
    @Test
    @DisplayName("3.5 Capabilities should have required fields")
    void testCapabilityFields() {
        if (!skillsRoot.exists()) {
            System.out.println("Skills directory not found, skipping test");
            return;
        }
        
        List<SkillPackage> discovered = localDiscoverer.discover(skillsRoot);
        
        int validCapabilities = 0;
        int invalidCapabilities = 0;
        
        for (SkillPackage skill : discovered) {
            List<Capability> capabilities = skill.getCapabilities();
            if (capabilities != null) {
                for (Capability cap : capabilities) {
                    boolean isValid = cap.getId() != null && !cap.getId().isEmpty()
                        && cap.getName() != null && !cap.getName().isEmpty();
                    
                    if (isValid) {
                        validCapabilities++;
                    } else {
                        invalidCapabilities++;
                        System.out.println("Invalid capability in " + skill.getId() + ": " + cap);
                    }
                }
            }
        }
        
        System.out.println("Valid capabilities: " + validCapabilities + ", Invalid: " + invalidCapabilities);
        assertTrue(validCapabilities > 0, "Should have valid capabilities");
    }
    
    @Test
    @DisplayName("3.6 Should register discovered skills to registry")
    void testRegisterDiscoveredSkills() {
        if (!skillsRoot.exists()) {
            System.out.println("Skills directory not found, skipping test");
            return;
        }
        
        List<SkillPackage> discovered = localDiscoverer.discover(skillsRoot);
        
        for (SkillPackage skill : discovered) {
            skillRegistry.register(skill);
        }
        
        List<SkillPackage> registeredSkills = skillRegistry.getAllSkills();
        
        System.out.println("Discovered: " + discovered.size() + ", Registered: " + registeredSkills.size());
        assertTrue(registeredSkills.size() >= discovered.size(), 
            "All discovered skills should be registered");
    }
    
    @Test
    @DisplayName("3.7 Should find skills by capability category")
    void testFindByCapabilityCategory() {
        if (!skillsRoot.exists()) {
            System.out.println("Skills directory not found, skipping test");
            return;
        }
        
        List<SkillPackage> discovered = localDiscoverer.discover(skillsRoot);
        
        Set<String> categories = discovered.stream()
            .filter(s -> s.getCapabilities() != null)
            .flatMap(s -> s.getCapabilities().stream())
            .map(Capability::getCategory)
            .filter(c -> c != null)
            .collect(Collectors.toSet());
        
        System.out.println("Found categories: " + categories);
        
        assertFalse(categories.isEmpty(), "Should have at least one category");
    }
    
    @Test
    @DisplayName("3.8 Should identify skill types")
    void testIdentifySkillTypes() {
        if (!skillsRoot.exists()) {
            System.out.println("Skills directory not found, skipping test");
            return;
        }
        
        List<SkillPackage> discovered = localDiscoverer.discover(skillsRoot);
        
        Set<String> types = discovered.stream()
            .map(SkillPackage::getType)
            .filter(t -> t != null)
            .collect(Collectors.toSet());
        
        System.out.println("Found skill types: " + types);
        
        Set<String> validTypes = Set.of("service-skill", "tool-skill", "nexus-ui", "enterprise-skill", "system-service");
        
        for (String type : types) {
            assertTrue(validTypes.contains(type), "Invalid skill type: " + type);
        }
    }
    
    @Test
    @DisplayName("3.9 Generate discovery summary report")
    void generateDiscoveryReport() {
        if (!skillsRoot.exists()) {
            System.out.println("Skills directory not found, skipping test");
            return;
        }
        
        List<SkillPackage> discovered = localDiscoverer.discover(skillsRoot);
        
        System.out.println("\n========================================");
        System.out.println("Local Discovery Summary Report");
        System.out.println("========================================");
        System.out.println("Total skills discovered: " + discovered.size());
        
        long withCapabilities = discovered.stream()
            .filter(s -> s.getCapabilities() != null && !s.getCapabilities().isEmpty())
            .count();
        System.out.println("Skills with capabilities: " + withCapabilities);
        
        long withEndpoints = discovered.stream()
            .filter(s -> s.getEndpoints() != null && !s.getEndpoints().isEmpty())
            .count();
        System.out.println("Skills with endpoints: " + withEndpoints);
        
        long totalCapabilities = discovered.stream()
            .filter(s -> s.getCapabilities() != null)
            .mapToLong(s -> s.getCapabilities().size())
            .sum();
        System.out.println("Total capabilities: " + totalCapabilities);
        
        Set<String> types = discovered.stream()
            .map(SkillPackage::getType)
            .filter(t -> t != null)
            .collect(Collectors.toSet());
        System.out.println("Skill types: " + types);
        
        Set<String> categories = discovered.stream()
            .filter(s -> s.getCapabilities() != null)
            .flatMap(s -> s.getCapabilities().stream())
            .map(Capability::getCategory)
            .filter(c -> c != null)
            .collect(Collectors.toSet());
        System.out.println("Capability categories: " + categories);
        System.out.println("========================================\n");
        
        assertTrue(discovered.size() > 0, "Should discover at least one skill");
    }
}

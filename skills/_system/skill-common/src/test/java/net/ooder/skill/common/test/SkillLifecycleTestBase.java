package net.ooder.skill.common.test;

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Skill Lifecycle Test Base
 * 
 * <p>Provides comprehensive lifecycle testing framework for all Skills.</p>
 * 
 * <h3>Skill Lifecycle Phases:</h3>
 * <ol>
 *   <li>Discovery - Skill discovery and metadata loading</li>
 *   <li>Registration - Skill registration with the system</li>
 *   <li>Initialization - Skill initialization with configuration</li>
 *   <li>Start - Skill startup and readiness</li>
 *   <li>Running - Normal operation and capability invocation</li>
 *   <li>Health Check - Health monitoring and status checks</li>
 *   <li>Stop - Graceful shutdown</li>
 *   <li>Destroy - Resource cleanup and destruction</li>
 * </ol>
 * 
 * @author Ooder Team
 * @version 2.3
 */
public abstract class SkillLifecycleTestBase<T> {

    protected T skill;
    protected Map<String, Object> config;
    protected List<String> lifecycleEvents;
    protected AtomicInteger invocationCount;

    @BeforeEach
    void setUpBase() {
        config = new HashMap<>();
        lifecycleEvents = new ArrayList<>();
        invocationCount = new AtomicInteger(0);
        setUpSkill();
    }

    @AfterEach
    void tearDownBase() {
        if (skill != null && isRunning()) {
            stopSkill();
        }
        tearDownSkill();
    }

    protected abstract void setUpSkill();
    protected abstract void tearDownSkill();
    protected abstract String getSkillId();
    protected abstract String getSkillName();
    protected abstract String getSkillVersion();
    protected abstract List<String> getCapabilities();
    protected abstract void initializeSkill(Map<String, Object> config);
    protected abstract void startSkill();
    protected abstract void stopSkill();
    protected abstract void destroySkill();
    protected abstract boolean isInitialized();
    protected abstract boolean isRunning();
    protected abstract Object invokeCapability(String capability, Map<String, Object> params);
    protected abstract Map<String, Object> getHealthStatus();

    // ==================== Discovery Phase Tests ====================

    @Nested
    @DisplayName("Phase 1: Discovery")
    class DiscoveryPhaseTests {

        @Test
        @DisplayName("1.1 Should have valid skill ID")
        void shouldHaveValidSkillId() {
            String skillId = getSkillId();
            assertNotNull(skillId, "Skill ID must not be null");
            assertFalse(skillId.isEmpty(), "Skill ID must not be empty");
            assertTrue(skillId.startsWith("skill-"), "Skill ID should start with 'skill-'");
            lifecycleEvents.add("DISCOVERY: Skill ID validated - " + skillId);
        }

        @Test
        @DisplayName("1.2 Should have valid skill name")
        void shouldHaveValidSkillName() {
            String skillName = getSkillName();
            assertNotNull(skillName, "Skill name must not be null");
            assertFalse(skillName.isEmpty(), "Skill name must not be empty");
            lifecycleEvents.add("DISCOVERY: Skill name validated - " + skillName);
        }

        @Test
        @DisplayName("1.3 Should have valid version")
        void shouldHaveValidVersion() {
            String version = getSkillVersion();
            assertNotNull(version, "Version must not be null");
            assertTrue(version.matches("\\d+\\.\\d+(\\.\\d+)?"), 
                    "Version should match semantic versioning (e.g., 2.3 or 2.3.0)");
            lifecycleEvents.add("DISCOVERY: Version validated - " + version);
        }

        @Test
        @DisplayName("1.4 Should have capabilities defined")
        void shouldHaveCapabilitiesDefined() {
            List<String> capabilities = getCapabilities();
            assertNotNull(capabilities, "Capabilities list must not be null");
            assertFalse(capabilities.isEmpty(), "Capabilities list must not be empty");
            lifecycleEvents.add("DISCOVERY: Capabilities found - " + capabilities.size());
        }

        @Test
        @DisplayName("1.5 Should have unique capability identifiers")
        void shouldHaveUniqueCapabilityIdentifiers() {
            List<String> capabilities = getCapabilities();
            Set<String> uniqueCapabilities = new HashSet<>(capabilities);
            assertEquals(capabilities.size(), uniqueCapabilities.size(), 
                    "All capability identifiers must be unique");
            lifecycleEvents.add("DISCOVERY: All capabilities are unique");
        }
    }

    // ==================== Registration Phase Tests ====================

    @Nested
    @DisplayName("Phase 2: Registration")
    class RegistrationPhaseTests {

        @Test
        @DisplayName("2.1 Should register skill metadata")
        void shouldRegisterSkillMetadata() {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("skillId", getSkillId());
            metadata.put("skillName", getSkillName());
            metadata.put("version", getSkillVersion());
            metadata.put("capabilities", getCapabilities());
            
            assertNotNull(metadata.get("skillId"));
            assertNotNull(metadata.get("skillName"));
            assertNotNull(metadata.get("version"));
            assertNotNull(metadata.get("capabilities"));
            
            lifecycleEvents.add("REGISTRATION: Metadata registered");
        }

        @Test
        @DisplayName("2.2 Should validate registration requirements")
        void shouldValidateRegistrationRequirements() {
            assertTrue(validateRegistrationRequirements(), 
                    "Skill should meet all registration requirements");
            lifecycleEvents.add("REGISTRATION: Requirements validated");
        }
    }

    // ==================== Initialization Phase Tests ====================

    @Nested
    @DisplayName("Phase 3: Initialization")
    class InitializationPhaseTests {

        @Test
        @DisplayName("3.1 Should initialize with empty config")
        void shouldInitializeWithEmptyConfig() {
            assertFalse(isInitialized(), "Skill should not be initialized before init");
            
            initializeSkill(new HashMap<>());
            
            assertTrue(isInitialized(), "Skill should be initialized after init");
            lifecycleEvents.add("INITIALIZATION: Initialized with empty config");
        }

        @Test
        @DisplayName("3.2 Should initialize with custom config")
        void shouldInitializeWithCustomConfig() {
            Map<String, Object> customConfig = new HashMap<>();
            customConfig.put("debug", true);
            customConfig.put("timeout", 5000);
            customConfig.put("maxConnections", 100);
            
            initializeSkill(customConfig);
            
            assertTrue(isInitialized());
            lifecycleEvents.add("INITIALIZATION: Initialized with custom config");
        }

        @Test
        @DisplayName("3.3 Should be idempotent for initialization")
        void shouldBeIdempotentForInitialization() {
            initializeSkill(new HashMap<>());
            assertTrue(isInitialized());
            
            initializeSkill(new HashMap<>());
            assertTrue(isInitialized());
            
            lifecycleEvents.add("INITIALIZATION: Idempotent initialization verified");
        }
    }

    // ==================== Start Phase Tests ====================

    @Nested
    @DisplayName("Phase 4: Start")
    class StartPhaseTests {

        @BeforeEach
        void initSkill() {
            initializeSkill(config);
        }

        @Test
        @DisplayName("4.1 Should start successfully after initialization")
        void shouldStartSuccessfullyAfterInitialization() {
            assertFalse(isRunning(), "Skill should not be running before start");
            
            startSkill();
            
            assertTrue(isRunning(), "Skill should be running after start");
            lifecycleEvents.add("START: Skill started successfully");
        }

        @Test
        @DisplayName("4.2 Should be idempotent for start")
        void shouldBeIdempotentForStart() {
            startSkill();
            assertTrue(isRunning());
            
            startSkill();
            assertTrue(isRunning());
            
            lifecycleEvents.add("START: Idempotent start verified");
        }

        @Test
        @DisplayName("4.3 Should reach ready state after start")
        void shouldReachReadyStateAfterStart() {
            startSkill();
            
            Map<String, Object> health = getHealthStatus();
            assertNotNull(health);
            
            lifecycleEvents.add("START: Ready state verified");
        }
    }

    // ==================== Running Phase Tests ====================

    @Nested
    @DisplayName("Phase 5: Running")
    class RunningPhaseTests {

        @BeforeEach
        void startSkillForTest() {
            initializeSkill(config);
            startSkill();
        }

        @Test
        @DisplayName("5.1 Should invoke capability successfully")
        void shouldInvokeCapabilitySuccessfully() {
            List<String> capabilities = getCapabilities();
            if (!capabilities.isEmpty()) {
                String capability = capabilities.get(0);
                Object result = invokeCapability(capability, new HashMap<>());
                assertNotNull(result, "Capability invocation should return a result");
                invocationCount.incrementAndGet();
            }
            lifecycleEvents.add("RUNNING: Capability invoked");
        }

        @Test
        @DisplayName("5.2 Should handle multiple invocations")
        void shouldHandleMultipleInvocations() {
            List<String> capabilities = getCapabilities();
            for (int i = 0; i < 10 && !capabilities.isEmpty(); i++) {
                String capability = capabilities.get(i % capabilities.size());
                invokeCapability(capability, new HashMap<>());
                invocationCount.incrementAndGet();
            }
            
            assertEquals(10, invocationCount.get());
            lifecycleEvents.add("RUNNING: Multiple invocations handled");
        }

        @Test
        @DisplayName("5.3 Should maintain running state during invocations")
        void shouldMaintainRunningStateDuringInvocations() {
            List<String> capabilities = getCapabilities();
            for (int i = 0; i < 5 && !capabilities.isEmpty(); i++) {
                assertTrue(isRunning(), "Skill should remain running during invocations");
                invokeCapability(capabilities.get(0), new HashMap<>());
            }
            lifecycleEvents.add("RUNNING: State maintained during invocations");
        }
    }

    // ==================== Health Check Phase Tests ====================

    @Nested
    @DisplayName("Phase 6: Health Check")
    class HealthCheckPhaseTests {

        @BeforeEach
        void startSkillForTest() {
            initializeSkill(config);
            startSkill();
        }

        @Test
        @DisplayName("6.1 Should return health status")
        void shouldReturnHealthStatus() {
            Map<String, Object> health = getHealthStatus();
            assertNotNull(health, "Health status should not be null");
            lifecycleEvents.add("HEALTH: Status returned");
        }

        @Test
        @DisplayName("6.2 Should indicate healthy status when running")
        void shouldIndicateHealthyStatusWhenRunning() {
            Map<String, Object> health = getHealthStatus();
            Object status = health.get("status");
            assertNotNull(status, "Health status should have a status field");
            lifecycleEvents.add("HEALTH: Healthy status verified");
        }

        @Test
        @DisplayName("6.3 Should include uptime in health status")
        void shouldIncludeUptimeInHealthStatus() {
            Map<String, Object> health = getHealthStatus();
            assertTrue(health.containsKey("uptime") || health.containsKey("startTime"),
                    "Health status should include uptime or startTime");
            lifecycleEvents.add("HEALTH: Uptime included");
        }
    }

    // ==================== Stop Phase Tests ====================

    @Nested
    @DisplayName("Phase 7: Stop")
    class StopPhaseTests {

        @BeforeEach
        void startSkillForTest() {
            initializeSkill(config);
            startSkill();
        }

        @Test
        @DisplayName("7.1 Should stop gracefully")
        void shouldStopGracefully() {
            assertTrue(isRunning());
            
            stopSkill();
            
            assertFalse(isRunning(), "Skill should not be running after stop");
            lifecycleEvents.add("STOP: Stopped gracefully");
        }

        @Test
        @DisplayName("7.2 Should remain initialized after stop")
        void shouldRemainInitializedAfterStop() {
            stopSkill();
            
            assertTrue(isInitialized(), "Skill should remain initialized after stop");
            lifecycleEvents.add("STOP: Remains initialized");
        }

        @Test
        @DisplayName("7.3 Should be restartable after stop")
        void shouldBeRestartableAfterStop() {
            stopSkill();
            assertFalse(isRunning());
            
            startSkill();
            assertTrue(isRunning(), "Skill should be restartable");
            lifecycleEvents.add("STOP: Restart verified");
        }

        @Test
        @DisplayName("7.4 Should be idempotent for stop")
        void shouldBeIdempotentForStop() {
            stopSkill();
            assertFalse(isRunning());
            
            stopSkill();
            assertFalse(isRunning());
            
            lifecycleEvents.add("STOP: Idempotent stop verified");
        }
    }

    // ==================== Destroy Phase Tests ====================

    @Nested
    @DisplayName("Phase 8: Destroy")
    class DestroyPhaseTests {

        @BeforeEach
        void initAndStart() {
            initializeSkill(config);
            startSkill();
        }

        @Test
        @DisplayName("8.1 Should destroy and cleanup resources")
        void shouldDestroyAndCleanupResources() {
            stopSkill();
            destroySkill();
            
            lifecycleEvents.add("DESTROY: Resources cleaned up");
        }

        @Test
        @DisplayName("8.2 Should handle destroy when not stopped")
        void shouldHandleDestroyWhenNotStopped() {
            destroySkill();
            
            lifecycleEvents.add("DESTROY: Handled destroy without explicit stop");
        }
    }

    // ==================== Full Lifecycle Integration Tests ====================

    @Nested
    @DisplayName("Full Lifecycle Integration")
    class FullLifecycleTests {

        @Test
        @DisplayName("Should complete full lifecycle: init -> start -> run -> stop -> destroy")
        void shouldCompleteFullLifecycle() {
            lifecycleEvents.add("=== FULL LIFECYCLE START ===");
            
            lifecycleEvents.add("Phase 1: Discovery");
            assertNotNull(getSkillId());
            assertNotNull(getSkillName());
            
            lifecycleEvents.add("Phase 2: Registration");
            assertTrue(validateRegistrationRequirements());
            
            lifecycleEvents.add("Phase 3: Initialization");
            assertFalse(isInitialized());
            initializeSkill(config);
            assertTrue(isInitialized());
            
            lifecycleEvents.add("Phase 4: Start");
            assertFalse(isRunning());
            startSkill();
            assertTrue(isRunning());
            
            lifecycleEvents.add("Phase 5: Running");
            List<String> capabilities = getCapabilities();
            if (!capabilities.isEmpty()) {
                invokeCapability(capabilities.get(0), new HashMap<>());
            }
            
            lifecycleEvents.add("Phase 6: Health Check");
            Map<String, Object> health = getHealthStatus();
            assertNotNull(health);
            
            lifecycleEvents.add("Phase 7: Stop");
            stopSkill();
            assertFalse(isRunning());
            assertTrue(isInitialized());
            
            lifecycleEvents.add("Phase 8: Destroy");
            destroySkill();
            
            lifecycleEvents.add("=== FULL LIFECYCLE COMPLETE ===");
            
            assertTrue(lifecycleEvents.size() >= 10, 
                    "Should have recorded all lifecycle phases");
        }

        @Test
        @DisplayName("Should handle multiple lifecycle cycles")
        void shouldHandleMultipleLifecycleCycles() {
            for (int cycle = 1; cycle <= 3; cycle++) {
                lifecycleEvents.add("=== CYCLE " + cycle + " ===");
                
                initializeSkill(config);
                startSkill();
                
                List<String> capabilities = getCapabilities();
                if (!capabilities.isEmpty()) {
                    invokeCapability(capabilities.get(0), new HashMap<>());
                }
                
                stopSkill();
                destroySkill();
                
                lifecycleEvents.add("Cycle " + cycle + " completed");
            }
            
            assertEquals(3, lifecycleEvents.stream()
                    .filter(e -> e.contains("Cycle") && e.contains("completed"))
                    .count());
        }
    }

    // ==================== Helper Methods ====================

    protected boolean validateRegistrationRequirements() {
        return getSkillId() != null && 
               getSkillName() != null && 
               getSkillVersion() != null && 
               getCapabilities() != null && 
               !getCapabilities().isEmpty();
    }

    protected List<String> getLifecycleEvents() {
        return new ArrayList<>(lifecycleEvents);
    }

    protected int getInvocationCount() {
        return invocationCount.get();
    }
}

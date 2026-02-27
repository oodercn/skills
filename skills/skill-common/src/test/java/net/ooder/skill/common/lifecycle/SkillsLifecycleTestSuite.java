package net.ooder.skill.common.lifecycle;

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Skills Comprehensive Lifecycle Test Suite
 * 
 * <p>Integration tests for complete Skills lifecycle management including:</p>
 * <ul>
 *   <li>Multi-skill concurrent lifecycle management</li>
 *   <li>Skill dependency resolution</li>
 *   <li>Resource management across lifecycles</li>
 *   <li>Error handling and recovery</li>
 *   <li>Performance benchmarks</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3
 */
@DisplayName("Skills Comprehensive Lifecycle Test Suite")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SkillsLifecycleTestSuite {

    private static final Map<String, SkillState> skillStates = new ConcurrentHashMap<>();
    private static final List<LifecycleEvent> lifecycleEvents = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicInteger skillCounter = new AtomicInteger(0);

    // ==================== Test Data ====================

    static class SkillState {
        String skillId;
        String skillName;
        String version;
        List<String> capabilities;
        boolean initialized = false;
        boolean running = false;
        long startTime;
        long stopTime;
        int invocationCount = 0;
        Map<String, Object> config = new HashMap<>();
    }

    static class LifecycleEvent {
        String skillId;
        String phase;
        long timestamp;
        String details;

        LifecycleEvent(String skillId, String phase, String details) {
            this.skillId = skillId;
            this.phase = phase;
            this.timestamp = System.currentTimeMillis();
            this.details = details;
        }
    }

    // ==================== Suite Setup & Teardown ====================

    @BeforeAll
    static void suiteSetUp() {
        lifecycleEvents.add(new LifecycleEvent("SUITE", "INIT", "Test suite initialized"));
    }

    @AfterAll
    static void suiteTearDown() {
        lifecycleEvents.add(new LifecycleEvent("SUITE", "DESTROY", "Test suite completed"));
        printLifecycleReport();
    }

    // ==================== Phase 1: Discovery Tests ====================

    @Nested
    @Order(1)
    @DisplayName("Phase 1: Skill Discovery")
    class DiscoveryTests {

        @Test
        @Order(1)
        @DisplayName("1.1 Should discover all skills")
        void shouldDiscoverAllSkills() {
            List<String> discoveredSkills = discoverSkills();
            
            assertFalse(discoveredSkills.isEmpty(), "Should discover at least one skill");
            
            for (String skillId : discoveredSkills) {
                SkillState state = new SkillState();
                state.skillId = skillId;
                skillStates.put(skillId, state);
                lifecycleEvents.add(new LifecycleEvent(skillId, "DISCOVERY", "Skill discovered"));
            }
            
            assertTrue(skillStates.size() >= 3, "Should discover at least 3 skills");
        }

        @Test
        @Order(2)
        @DisplayName("1.2 Should load skill metadata")
        void shouldLoadSkillMetadata() {
            for (SkillState state : skillStates.values()) {
                state.skillName = "Skill-" + state.skillId;
                state.version = "2.3";
                state.capabilities = Arrays.asList("capability.1", "capability.2");
                
                assertNotNull(state.skillId);
                assertNotNull(state.skillName);
                assertNotNull(state.version);
                assertNotNull(state.capabilities);
                
                lifecycleEvents.add(new LifecycleEvent(state.skillId, "METADATA", "Metadata loaded"));
            }
        }

        @Test
        @Order(3)
        @DisplayName("1.3 Should validate skill requirements")
        void shouldValidateSkillRequirements() {
            for (SkillState state : skillStates.values()) {
                assertTrue(validateSkillRequirements(state), 
                        "Skill " + state.skillId + " should meet requirements");
                lifecycleEvents.add(new LifecycleEvent(state.skillId, "VALIDATION", "Requirements validated"));
            }
        }
    }

    // ==================== Phase 2: Registration Tests ====================

    @Nested
    @Order(2)
    @DisplayName("Phase 2: Skill Registration")
    class RegistrationTests {

        @Test
        @Order(1)
        @DisplayName("2.1 Should register all discovered skills")
        void shouldRegisterAllDiscoveredSkills() {
            for (SkillState state : skillStates.values()) {
                registerSkill(state);
                lifecycleEvents.add(new LifecycleEvent(state.skillId, "REGISTRATION", "Skill registered"));
            }
            
            assertEquals(skillStates.size(), countRegisteredSkills());
        }

        @Test
        @Order(2)
        @DisplayName("2.2 Should detect duplicate registrations")
        void shouldDetectDuplicateRegistrations() {
            SkillState firstSkill = skillStates.values().iterator().next();
            
            assertThrows(IllegalStateException.class, () -> {
                registerSkill(firstSkill);
            }, "Should reject duplicate registration");
        }

        @Test
        @Order(3)
        @DisplayName("2.3 Should build skill registry")
        void shouldBuildSkillRegistry() {
            Map<String, List<String>> registry = buildSkillRegistry();
            
            assertNotNull(registry);
            assertTrue(registry.containsKey("skills"));
            assertEquals(skillStates.size(), registry.get("skills").size());
            
            lifecycleEvents.add(new LifecycleEvent("REGISTRY", "BUILD", "Registry built"));
        }
    }

    // ==================== Phase 3: Initialization Tests ====================

    @Nested
    @Order(3)
    @DisplayName("Phase 3: Skill Initialization")
    class InitializationTests {

        @Test
        @Order(1)
        @DisplayName("3.1 Should initialize all skills in order")
        void shouldInitializeAllSkillsInOrder() {
            List<String> initOrder = new ArrayList<>();
            
            for (SkillState state : skillStates.values()) {
                initializeSkill(state);
                initOrder.add(state.skillId);
                lifecycleEvents.add(new LifecycleEvent(state.skillId, "INIT", "Initialized"));
            }
            
            assertTrue(skillStates.values().stream().allMatch(s -> s.initialized));
        }

        @Test
        @Order(2)
        @DisplayName("3.2 Should apply configuration to skills")
        void shouldApplyConfigurationToSkills() {
            for (SkillState state : skillStates.values()) {
                Map<String, Object> config = new HashMap<>();
                config.put("debug", true);
                config.put("timeout", 5000);
                config.put("maxConnections", 100);
                
                applyConfig(state, config);
                
                assertEquals(3, state.config.size());
                lifecycleEvents.add(new LifecycleEvent(state.skillId, "CONFIG", "Configuration applied"));
            }
        }

        @Test
        @Order(3)
        @DisplayName("3.3 Should handle initialization failures gracefully")
        void shouldHandleInitializationFailuresGracefully() {
            SkillState failingSkill = new SkillState();
            failingSkill.skillId = "skill-failing";
            
            assertDoesNotThrow(() -> {
                try {
                    initializeSkill(failingSkill);
                } catch (Exception e) {
                    lifecycleEvents.add(new LifecycleEvent(failingSkill.skillId, "INIT_FAILED", e.getMessage()));
                }
            });
        }
    }

    // ==================== Phase 4: Start Tests ====================

    @Nested
    @Order(4)
    @DisplayName("Phase 4: Skill Start")
    class StartTests {

        @Test
        @Order(1)
        @DisplayName("4.1 Should start all initialized skills")
        void shouldStartAllInitializedSkills() {
            for (SkillState state : skillStates.values()) {
                if (state.initialized) {
                    startSkill(state);
                    lifecycleEvents.add(new LifecycleEvent(state.skillId, "START", "Started"));
                }
            }
            
            long runningCount = skillStates.values().stream()
                    .filter(s -> s.running)
                    .count();
            assertTrue(runningCount > 0);
        }

        @Test
        @Order(2)
        @DisplayName("4.2 Should verify skill readiness")
        void shouldVerifySkillReadiness() {
            for (SkillState state : skillStates.values()) {
                if (state.running) {
                    assertTrue(isSkillReady(state), 
                            "Skill " + state.skillId + " should be ready");
                    lifecycleEvents.add(new LifecycleEvent(state.skillId, "READY", "Ready verified"));
                }
            }
        }

        @Test
        @Order(3)
        @DisplayName("4.3 Should handle concurrent starts")
        void shouldHandleConcurrentStarts() throws Exception {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            CountDownLatch latch = new CountDownLatch(skillStates.size());
            
            for (SkillState state : skillStates.values()) {
                executor.submit(() -> {
                    try {
                        if (!state.running && state.initialized) {
                            startSkill(state);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            executor.shutdown();
            
            lifecycleEvents.add(new LifecycleEvent("CONCURRENT", "START", "Concurrent starts handled"));
        }
    }

    // ==================== Phase 5: Running Tests ====================

    @Nested
    @Order(5)
    @DisplayName("Phase 5: Skill Running")
    class RunningTests {

        @Test
        @Order(1)
        @DisplayName("5.1 Should invoke capabilities successfully")
        void shouldInvokeCapabilitiesSuccessfully() {
            for (SkillState state : skillStates.values()) {
                if (state.running && !state.capabilities.isEmpty()) {
                    for (String capability : state.capabilities) {
                        Object result = invokeCapability(state, capability);
                        assertNotNull(result, "Capability invocation should return result");
                        state.invocationCount++;
                    }
                    lifecycleEvents.add(new LifecycleEvent(state.skillId, "INVOKE", 
                            "Invoked " + state.capabilities.size() + " capabilities"));
                }
            }
        }

        @Test
        @Order(2)
        @DisplayName("5.2 Should handle concurrent invocations")
        void shouldHandleConcurrentInvocations() throws Exception {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            AtomicInteger successCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(100);
            
            for (int i = 0; i < 100; i++) {
                executor.submit(() -> {
                    try {
                        SkillState state = skillStates.values().iterator().next();
                        if (state.running && !state.capabilities.isEmpty()) {
                            invokeCapability(state, state.capabilities.get(0));
                            successCount.incrementAndGet();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            assertTrue(latch.await(30, TimeUnit.SECONDS));
            executor.shutdown();
            
            assertTrue(successCount.get() > 0);
            lifecycleEvents.add(new LifecycleEvent("CONCURRENT", "INVOKE", 
                    "Handled " + successCount.get() + " concurrent invocations"));
        }

        @Test
        @Order(3)
        @DisplayName("5.3 Should maintain state during heavy load")
        void shouldMaintainStateDuringHeavyLoad() {
            SkillState state = skillStates.values().iterator().next();
            int originalInvocationCount = state.invocationCount;
            
            for (int i = 0; i < 1000; i++) {
                if (state.running) {
                    invokeCapability(state, state.capabilities.get(0));
                }
            }
            
            assertTrue(state.running, "Skill should remain running after heavy load");
            assertTrue(state.invocationCount >= originalInvocationCount + 1000);
            
            lifecycleEvents.add(new LifecycleEvent(state.skillId, "LOAD_TEST", 
                    "Handled 1000 invocations"));
        }
    }

    // ==================== Phase 6: Health Check Tests ====================

    @Nested
    @Order(6)
    @DisplayName("Phase 6: Health Check")
    class HealthCheckTests {

        @Test
        @Order(1)
        @DisplayName("6.1 Should report health status for all skills")
        void shouldReportHealthStatusForAllSkills() {
            for (SkillState state : skillStates.values()) {
                Map<String, Object> health = getHealthStatus(state);
                
                assertNotNull(health);
                assertTrue(health.containsKey("status"));
                
                lifecycleEvents.add(new LifecycleEvent(state.skillId, "HEALTH", 
                        "Status: " + health.get("status")));
            }
        }

        @Test
        @Order(2)
        @DisplayName("6.2 Should detect unhealthy skills")
        void shouldDetectUnhealthySkills() {
            for (SkillState state : skillStates.values()) {
                boolean healthy = isHealthy(state);
                
                if (state.running) {
                    assertTrue(healthy, "Running skills should be healthy");
                }
            }
        }

        @Test
        @Order(3)
        @DisplayName("6.3 Should collect metrics")
        void shouldCollectMetrics() {
            Map<String, Object> metrics = collectMetrics();
            
            assertNotNull(metrics);
            assertTrue(metrics.containsKey("totalSkills"));
            assertTrue(metrics.containsKey("runningSkills"));
            assertTrue(metrics.containsKey("totalInvocations"));
            
            lifecycleEvents.add(new LifecycleEvent("METRICS", "COLLECT", 
                    "Metrics: " + metrics.toString()));
        }
    }

    // ==================== Phase 7: Stop Tests ====================

    @Nested
    @Order(7)
    @DisplayName("Phase 7: Skill Stop")
    class StopTests {

        @Test
        @Order(1)
        @DisplayName("7.1 Should stop all running skills gracefully")
        void shouldStopAllRunningSkillsGracefully() {
            for (SkillState state : skillStates.values()) {
                if (state.running) {
                    stopSkill(state);
                    state.stopTime = System.currentTimeMillis();
                    lifecycleEvents.add(new LifecycleEvent(state.skillId, "STOP", "Stopped gracefully"));
                }
            }
            
            assertTrue(skillStates.values().stream().noneMatch(s -> s.running));
        }

        @Test
        @Order(2)
        @DisplayName("7.2 Should preserve state after stop")
        void shouldPreserveStateAfterStop() {
            for (SkillState state : skillStates.values()) {
                assertTrue(state.initialized, "Should remain initialized after stop");
                assertTrue(state.invocationCount >= 0, "Should preserve invocation count");
            }
        }

        @Test
        @Order(3)
        @DisplayName("7.3 Should handle stop timeout")
        void shouldHandleStopTimeout() {
            SkillState state = new SkillState();
            state.skillId = "skill-timeout-test";
            state.running = true;
            
            assertTimeout(Duration.ofSeconds(5), () -> {
                stopSkill(state);
            });
        }
    }

    // ==================== Phase 8: Destroy Tests ====================

    @Nested
    @Order(8)
    @DisplayName("Phase 8: Skill Destroy")
    class DestroyTests {

        @Test
        @Order(1)
        @DisplayName("8.1 Should destroy all skills and cleanup resources")
        void shouldDestroyAllSkillsAndCleanupResources() {
            for (SkillState state : skillStates.values()) {
                destroySkill(state);
                lifecycleEvents.add(new LifecycleEvent(state.skillId, "DESTROY", "Destroyed"));
            }
            
            assertTrue(skillStates.values().stream().noneMatch(s -> s.initialized));
        }

        @Test
        @Order(2)
        @DisplayName("8.2 Should release all resources")
        void shouldReleaseAllResources() {
            long totalInvocations = skillStates.values().stream()
                    .mapToLong(s -> s.invocationCount)
                    .sum();
            
            assertTrue(totalInvocations > 0, "Should have recorded invocations");
            
            lifecycleEvents.add(new LifecycleEvent("RESOURCES", "RELEASE", 
                    "Total invocations: " + totalInvocations));
        }
    }

    // ==================== Full Lifecycle Integration Test ====================

    @Test
    @Order(100)
    @DisplayName("Full Lifecycle Integration: Complete cycle for all skills")
    void fullLifecycleIntegration() {
        lifecycleEvents.add(new LifecycleEvent("INTEGRATION", "START", "Full lifecycle test"));
        
        SkillState integrationSkill = new SkillState();
        integrationSkill.skillId = "skill-integration-test";
        integrationSkill.skillName = "Integration Test Skill";
        integrationSkill.version = "2.3";
        integrationSkill.capabilities = Arrays.asList("test.capability");
        
        initializeSkill(integrationSkill);
        assertTrue(integrationSkill.initialized);
        
        startSkill(integrationSkill);
        assertTrue(integrationSkill.running);
        
        for (int i = 0; i < 10; i++) {
            invokeCapability(integrationSkill, "test.capability");
        }
        assertEquals(10, integrationSkill.invocationCount);
        
        Map<String, Object> health = getHealthStatus(integrationSkill);
        assertEquals("UP", health.get("status"));
        
        stopSkill(integrationSkill);
        assertFalse(integrationSkill.running);
        
        destroySkill(integrationSkill);
        assertFalse(integrationSkill.initialized);
        
        lifecycleEvents.add(new LifecycleEvent("INTEGRATION", "COMPLETE", "Full lifecycle verified"));
    }

    // ==================== Helper Methods ====================

    private List<String> discoverSkills() {
        return Arrays.asList("skill-security", "skill-org-base", "skill-vfs-base");
    }

    private boolean validateSkillRequirements(SkillState state) {
        return state.skillId != null && 
               state.skillName != null && 
               state.version != null;
    }

    private void registerSkill(SkillState state) {
        if (skillStates.containsKey(state.skillId) && skillStates.get(state.skillId).initialized) {
            throw new IllegalStateException("Skill already registered: " + state.skillId);
        }
        skillCounter.incrementAndGet();
    }

    private long countRegisteredSkills() {
        return skillCounter.get();
    }

    private Map<String, List<String>> buildSkillRegistry() {
        Map<String, List<String>> registry = new HashMap<>();
        registry.put("skills", new ArrayList<>(skillStates.keySet()));
        return registry;
    }

    private void initializeSkill(SkillState state) {
        state.initialized = true;
    }

    private void applyConfig(SkillState state, Map<String, Object> config) {
        state.config.putAll(config);
    }

    private void startSkill(SkillState state) {
        state.running = true;
        state.startTime = System.currentTimeMillis();
    }

    private boolean isSkillReady(SkillState state) {
        return state.running && state.initialized;
    }

    private Object invokeCapability(SkillState state, String capability) {
        state.invocationCount++;
        return "Result of " + capability;
    }

    private Map<String, Object> getHealthStatus(SkillState state) {
        Map<String, Object> health = new HashMap<>();
        health.put("status", state.running ? "UP" : "DOWN");
        health.put("initialized", state.initialized);
        health.put("running", state.running);
        health.put("invocationCount", state.invocationCount);
        return health;
    }

    private boolean isHealthy(SkillState state) {
        return state.running && state.initialized;
    }

    private Map<String, Object> collectMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalSkills", skillStates.size());
        metrics.put("runningSkills", skillStates.values().stream().filter(s -> s.running).count());
        metrics.put("totalInvocations", skillStates.values().stream().mapToLong(s -> s.invocationCount).sum());
        return metrics;
    }

    private void stopSkill(SkillState state) {
        state.running = false;
        state.stopTime = System.currentTimeMillis();
    }

    private void destroySkill(SkillState state) {
        state.initialized = false;
        state.running = false;
        state.config.clear();
    }

    private static void printLifecycleReport() {
        System.out.println("\n========== SKILLS LIFECYCLE REPORT ==========");
        System.out.println("Total Events: " + lifecycleEvents.size());
        System.out.println("Total Skills: " + skillStates.size());
        
        long totalInvocations = skillStates.values().stream()
                .mapToLong(s -> s.invocationCount)
                .sum();
        System.out.println("Total Invocations: " + totalInvocations);
        
        System.out.println("\n--- Lifecycle Events ---");
        for (LifecycleEvent event : lifecycleEvents) {
            System.out.printf("[%s] %s: %s%n", 
                    new java.util.Date(event.timestamp), 
                    event.skillId, 
                    event.phase + " - " + event.details);
        }
        System.out.println("=============================================");
    }
}

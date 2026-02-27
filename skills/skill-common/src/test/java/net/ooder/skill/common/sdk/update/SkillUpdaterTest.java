package net.ooder.skill.common.sdk.update;

import net.ooder.skill.common.sdk.lifecycle.SkillLifecycleManager;
import net.ooder.skill.common.sdk.registry.SkillRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SkillUpdater 鍗曞厓娴嬭瘯
 */
class SkillUpdaterTest {

    private SkillUpdater updater;

    @Mock
    private SkillLifecycleManager lifecycleManager;

    @Mock
    private SkillRegistry registry;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updater = new SkillUpdater(lifecycleManager, registry, tempDir);
    }

    @Test
    void testIsPatchUpdate() {
        // Test patch version updates
        assertTrue(updater.isPatchUpdate("1.0.0", "1.0.1"));
        assertTrue(updater.isPatchUpdate("2.5.3", "2.5.4"));
        
        // Test minor version updates
        assertFalse(updater.isPatchUpdate("1.0.0", "1.1.0"));
        
        // Test major version updates
        assertFalse(updater.isPatchUpdate("1.0.0", "2.0.0"));
        
        // Test same version
        assertFalse(updater.isPatchUpdate("1.0.0", "1.0.0"));
    }

    @Test
    void testCompareVersions() {
        // Test equal versions
        assertEquals(0, updater.compareVersions("1.0.0", "1.0.0"));
        
        // Test greater than
        assertTrue(updater.compareVersions("2.0.0", "1.0.0") > 0);
        assertTrue(updater.compareVersions("1.1.0", "1.0.0") > 0);
        assertTrue(updater.compareVersions("1.0.1", "1.0.0") > 0);
        
        // Test less than
        assertTrue(updater.compareVersions("1.0.0", "2.0.0") < 0);
        assertTrue(updater.compareVersions("1.0.0", "1.1.0") < 0);
        assertTrue(updater.compareVersions("1.0.0", "1.0.1") < 0);
    }

    @Test
    void testUpdateInfoBuilder() {
        // Given
        UpdateInfo updateInfo = UpdateInfo.builder()
                .skillId("test-skill")
                .skillName("Test Skill")
                .currentVersion("1.0.0")
                .newVersion("1.0.1")
                .updateType(UpdateInfo.UpdateType.PATCH)
                .requiresRestart(false)
                .downloadUrl("http://example.com/test.jar")
                .sha256Hash("abc123")
                .mandatory(false)
                .build();

        // Then
        assertEquals("test-skill", updateInfo.getSkillId());
        assertEquals("1.0.0", updateInfo.getCurrentVersion());
        assertEquals("1.0.1", updateInfo.getNewVersion());
        assertTrue(updateInfo.isPatchUpdate());
        assertFalse(updateInfo.isSecurityUpdate());
    }

    @Test
    void testUpdateInfoSecurityUpdate() {
        // Given
        UpdateInfo updateInfo = UpdateInfo.builder()
                .skillId("test-skill")
                .currentVersion("1.0.0")
                .newVersion("1.0.1")
                .releaseNotes("Fixed security vulnerability")
                .build();

        // Then
        assertTrue(updateInfo.isSecurityUpdate());
    }

    @Test
    void testUpdateResultSuccess() {
        // When
        UpdateResult result = UpdateResult.success("test-skill", "1.0.1");

        // Then
        assertTrue(result.isSuccess());
        assertEquals("test-skill", result.getSkillId());
        assertEquals("1.0.1", result.getNewVersion());
        assertNotNull(result.getUpdateTime());
    }

    @Test
    void testUpdateResultFailed() {
        // When
        UpdateResult result = UpdateResult.failed("test-skill", "Download failed");

        // Then
        assertFalse(result.isSuccess());
        assertEquals("test-skill", result.getSkillId());
        assertEquals("Download failed", result.getErrorMessage());
    }

    @Test
    void testUpdateResultHotSwap() {
        // When
        UpdateResult result = UpdateResult.hotSwapSuccess("test-skill", "1.0.0", "1.0.1");

        // Then
        assertTrue(result.isSuccess());
        assertEquals(UpdateResult.UpdateStrategy.HOT_SWAP, result.getStrategy());
        assertFalse(result.isRequiresRestart());
        assertEquals("1.0.0", result.getOldVersion());
        assertEquals("1.0.1", result.getNewVersion());
    }

    @Test
    void testUpdateResultRestartRequired() {
        // When
        UpdateResult result = UpdateResult.restartRequired("test-skill", "1.0.0", "2.0.0");

        // Then
        assertTrue(result.isSuccess());
        assertEquals(UpdateResult.UpdateStrategy.RESTART, result.getStrategy());
        assertTrue(result.isRequiresRestart());
    }

    @Test
    void testUpdateResultRollback() {
        // When
        UpdateResult result = UpdateResult.rollback("test-skill", "1.0.0");

        // Then
        assertTrue(result.isSuccess());
        assertEquals(UpdateResult.UpdateStrategy.ROLLBACK, result.getStrategy());
        assertTrue(result.isRollback());
    }
}

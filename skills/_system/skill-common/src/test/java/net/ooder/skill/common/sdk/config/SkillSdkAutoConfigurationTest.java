package net.ooder.skill.common.sdk.config;

import net.ooder.sdk.api.skill.SkillRegistry;
import net.ooder.sdk.classloader.ClassLoaderManager;
import net.ooder.sdk.lifecycle.LifecycleManager;
import net.ooder.sdk.driver.DriverLoader;
import net.ooder.sdk.security.SecurityManager;
import net.ooder.sdk.service.monitoring.metrics.MetricsCollector;
import net.ooder.sdk.service.monitoring.health.HealthMonitor;
import net.ooder.scene.discovery.CapabilityDiscoveryService;

// SDK 2.3 鏂板妯″潡
import net.ooder.sdk.deployment.BlueGreenDeploymentManager;
import net.ooder.sdk.migration.DataMigrationEngine;
import net.ooder.sdk.migration.MigrationDetector;
import net.ooder.sdk.config.DynamicSceneConfigManager;
import net.ooder.sdk.update.SkillUpdater;
import net.ooder.skills.container.api.SkillContainer;
import net.ooder.skills.container.api.CapabilityRegistry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Skill SDK 2.3 鑷姩閰嶇疆闆嗘垚娴嬭瘯
 * 
 * 楠岃瘉鎵€鏈?SDK 2.3 缁勪欢鏄惁姝ｇ‘鍔犺浇
 */
@SpringBootTest(classes = SkillSdkAutoConfiguration.class)
public class SkillSdkAutoConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    // ============================================================
    // 鏍稿績缁勪欢娴嬭瘯
    // ============================================================

    @Test
    public void testSkillRegistryBeanExists() {
        // SkillRegistry 鐢?SDK 鎻愪緵
        assertTrue(applicationContext.containsBean("skillRegistry") ||
                   applicationContext.getBeanNamesForType(SkillRegistry.class).length > 0,
                "SkillRegistry should be available");
    }

    @Test
    public void testLifecycleManagerBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(LifecycleManager.class).length > 0,
                "LifecycleManager should be available");
    }

    @Test
    public void testClassLoaderManagerBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(ClassLoaderManager.class).length > 0,
                "ClassLoaderManager should be available");
    }

    @Test
    public void testDriverLoaderBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(DriverLoader.class).length > 0,
                "DriverLoader should be available");
    }

    // ============================================================
    // 瀹夊叏涓庣洃鎺х粍浠舵祴璇?    // ============================================================

    @Test
    public void testSecurityManagerBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(SecurityManager.class).length > 0,
                "SecurityManager should be available");
    }

    @Test
    public void testMetricsCollectorBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(MetricsCollector.class).length > 0,
                "MetricsCollector should be available");
    }

    @Test
    public void testHealthMonitorBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(HealthMonitor.class).length > 0,
                "HealthMonitor should be available");
    }

    // ============================================================
    // 鍙戠幇缁勪欢娴嬭瘯
    // ============================================================

    @Test
    public void testCapabilityDiscoveryServiceBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(CapabilityDiscoveryService.class).length > 0,
                "CapabilityDiscoveryService should be available");
    }

    // ============================================================
    // SDK 2.3 鏂板缁勪欢娴嬭瘯 - 钃濈豢閮ㄧ讲
    // ============================================================

    @Test
    public void testBlueGreenDeploymentManagerBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(BlueGreenDeploymentManager.class).length > 0,
                "BlueGreenDeploymentManager should be available");
    }

    // ============================================================
    // SDK 2.3 鏂板缁勪欢娴嬭瘯 - 鏁版嵁杩佺Щ
    // ============================================================

    @Test
    public void testDataMigrationEngineBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(DataMigrationEngine.class).length > 0,
                "DataMigrationEngine should be available");
    }

    @Test
    public void testMigrationDetectorBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(MigrationDetector.class).length > 0,
                "MigrationDetector should be available");
    }

    // ============================================================
    // SDK 2.3 鏂板缁勪欢娴嬭瘯 - 鍔ㄦ€侀厤缃?    // ============================================================

    @Test
    public void testDynamicSceneConfigManagerBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(DynamicSceneConfigManager.class).length > 0,
                "DynamicSceneConfigManager should be available");
    }

    // ============================================================
    // SDK 2.3 鏂板缁勪欢娴嬭瘯 - 鑷姩鏇存柊
    // ============================================================

    @Test
    public void testSkillUpdaterBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(SkillUpdater.class).length > 0,
                "SkillUpdater should be available");
    }

    // ============================================================
    // SDK 2.3 鏂板缁勪欢娴嬭瘯 - Skills Container
    // ============================================================

    @Test
    public void testSkillContainerBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(SkillContainer.class).length > 0,
                "SkillContainer should be available");
    }

    @Test
    public void testCapabilityRegistryBeanExists() {
        assertTrue(applicationContext.getBeanNamesForType(CapabilityRegistry.class).length > 0,
                "CapabilityRegistry should be available");
    }

    // ============================================================
    // Skills 鎵╁睍缁勪欢娴嬭瘯
    // ============================================================

    @Test
    public void testSkillsHealthIndicatorBeanExists() {
        assertTrue(applicationContext.containsBean("skillsHealthIndicator"),
                "SkillsHealthIndicator should be available");
    }

    @Test
    public void testSkillsMetricsConfigurerBeanExists() {
        assertTrue(applicationContext.containsBean("skillsMetricsConfigurer"),
                "SkillsMetricsConfigurer should be available");
    }
}

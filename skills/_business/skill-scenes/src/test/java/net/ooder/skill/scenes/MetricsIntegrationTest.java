package net.ooder.skill.scenes;

import net.ooder.scene.group.metrics.SqlSceneGroupMetricsManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class MetricsIntegrationTest {

    @Autowired
    private SqlSceneGroupMetricsManager metricsManager;

    @Test
    public void testMetricsManagerInjection() {
        assertNotNull(metricsManager, "SqlSceneGroupMetricsManager should be injected");
    }

    @Test
    public void testRecordMetric() {
        String sceneGroupId = "test-scene-group-metrics-1";
        String metricName = "test.metric";
        double value = 123.45;
        
        metricsManager.recordMetric(sceneGroupId, metricName, value);
        
        List<Map<String, Object>> metrics = metricsManager.getMetrics(sceneGroupId, metricName);
        assertNotNull(metrics, "Metrics should not be null");
        assertTrue(metrics.size() > 0, "Should have at least one metric");
    }

    @Test
    public void testGetMetrics() {
        String sceneGroupId = "test-scene-group-metrics-2";
        
        metricsManager.recordMetric(sceneGroupId, "metric1", 100.0);
        metricsManager.recordMetric(sceneGroupId, "metric2", 200.0);
        metricsManager.recordMetric(sceneGroupId, "metric3", 300.0);
        
        List<Map<String, Object>> metrics = metricsManager.getAllMetrics(sceneGroupId);
        
        assertNotNull(metrics, "Metrics should not be null");
        assertTrue(metrics.size() >= 3, "Should have at least 3 metrics");
    }

    @Test
    public void testGetMetricStatistics() {
        String sceneGroupId = "test-scene-group-metrics-3";
        String metricName = "stat.metric";
        
        for (int i = 0; i < 10; i++) {
            metricsManager.recordMetric(sceneGroupId, metricName, i * 10.0);
        }
        
        Map<String, Object> stats = metricsManager.getMetricStatistics(sceneGroupId, metricName);
        
        assertNotNull(stats, "Statistics should not be null");
    }

    @Test
    public void testSaveMetricSnapshot() {
        String sceneGroupId = "test-scene-group-metrics-4";
        
        metricsManager.recordMetric(sceneGroupId, "snapshot.metric", 999.0);
        
        String snapshotId = metricsManager.saveMetricSnapshot(sceneGroupId, "测试快照");
        
        assertNotNull(snapshotId, "Snapshot ID should not be null");
    }

    @Test
    public void testGetMetricHistory() {
        String sceneGroupId = "test-scene-group-metrics-5";
        String metricName = "history.metric";
        
        for (int i = 0; i < 5; i++) {
            metricsManager.recordMetric(sceneGroupId, metricName, i * 100.0);
        }
        
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 3600000;
        
        List<Map<String, Object>> history = metricsManager.getMetricHistory(
            sceneGroupId, metricName, startTime, endTime);
        
        assertNotNull(history, "History should not be null");
    }

    @Test
    public void testRecordMetricWithTags() {
        String sceneGroupId = "test-scene-group-metrics-6";
        String metricName = "tagged.metric";
        double value = 456.78;
        
        Map<String, String> tags = new HashMap<>();
        tags.put("environment", "test");
        tags.put("version", "1.0.0");
        
        metricsManager.recordMetric(sceneGroupId, metricName, value, tags);
        
        List<Map<String, Object>> metrics = metricsManager.getMetrics(sceneGroupId, metricName);
        assertNotNull(metrics, "Metrics should not be null");
        assertTrue(metrics.size() > 0, "Should have at least one metric");
    }

    @Test
    public void testDeleteOldMetrics() {
        String sceneGroupId = "test-scene-group-metrics-7";
        
        metricsManager.recordMetric(sceneGroupId, "old.metric", 100.0);
        
        long cutoffTime = System.currentTimeMillis() + 3600000;
        int deleted = metricsManager.deleteOldMetrics(sceneGroupId, cutoffTime);
        
        assertTrue(deleted >= 0, "Deleted count should be non-negative");
    }
}

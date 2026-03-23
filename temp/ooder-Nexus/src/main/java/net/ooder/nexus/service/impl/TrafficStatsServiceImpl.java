package net.ooder.nexus.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.ooder.nexus.domain.network.model.TrafficStats;
import net.ooder.nexus.service.TrafficStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 流量统计服务实现�?
 */
@Service
public class TrafficStatsServiceImpl implements TrafficStatsService {

    private static final Logger log = LoggerFactory.getLogger(TrafficStatsServiceImpl.class);
    private static final String DATA_DIR = "./storage/network";
    private static final String TRAFFIC_FILE = "traffic-stats.json";

    private final Map<String, TrafficStats> statsCache = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final Path storagePath;

    public TrafficStatsServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.storagePath = Paths.get(DATA_DIR, TRAFFIC_FILE);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            loadStats();
            
            // 如果没有数据，初始化默认数据
            if (statsCache.isEmpty()) {
                initDefaultData();
            }
            
            log.info("流量统计服务初始化完成，共加�?{} 个设�?, statsCache.size());
        } catch (IOException e) {
            log.error("初始化流量统计服务失�?, e);
        }
    }

    @Override
    public void initDefaultData() {
        List<TrafficStats> defaultStats = Arrays.asList(
            new TrafficStats("traffic-001", "智能电视", "192.168.1.104", 1.2, 12.5, 4.2, 60.0),
            new TrafficStats("traffic-002", "笔记本电�?, "192.168.1.101", 0.8, 4.6, 2.1, 30.0),
            new TrafficStats("traffic-003", "游戏主机", "192.168.1.105", 0.5, 8.7, 3.5, 45.0)
        );
        
        defaultStats.forEach(stats -> statsCache.put(stats.getId(), stats));
        saveStats();
        log.info("初始化默认流量统计数据完成，�?{} 个设�?, defaultStats.size());
    }

    private void loadStats() {
        if (!Files.exists(storagePath)) {
            return;
        }

        try {
            String json = new String(Files.readAllBytes(storagePath), StandardCharsets.UTF_8);
            List<TrafficStats> stats = objectMapper.readValue(json, new TypeReference<List<TrafficStats>>() {});
            stats.forEach(s -> statsCache.put(s.getId(), s));
        } catch (IOException e) {
            log.error("加载流量统计数据失败", e);
        }
    }

    private void saveStats() {
        try {
            List<TrafficStats> stats = new ArrayList<>(statsCache.values());
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stats);
            Files.write(storagePath, json.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("保存流量统计数据失败", e);
        }
    }

    @Override
    public List<TrafficStats> getAllTrafficStats() {
        return new ArrayList<>(statsCache.values());
    }

    @Override
    public BandwidthSummary getBandwidthSummary() {
        BandwidthSummary summary = new BandwidthSummary();
        summary.setTodayUsage(12.5);
        summary.setMonthUsage(87.3);
        summary.setMaxUpload(5.2);
        summary.setMaxDownload(25.6);
        return summary;
    }

    @Override
    public List<BandwidthTrend> getBandwidthTrend(String period) {
        List<BandwidthTrend> trends = new ArrayList<>();
        
        // 生成模拟趋势数据
        Random random = new Random();
        int points = "1h".equals(period) ? 12 : "24h".equals(period) ? 24 : "7d".equals(period) ? 7 : 30;
        
        for (int i = 0; i < points; i++) {
            String time = "1h".equals(period) ? (i * 5) + "min" : 
                         "24h".equals(period) ? i + ":00" : 
                         "7d".equals(period) ? "Day " + (i + 1) : (i + 1) + "�?;
            
            double upload = 50 + random.nextDouble() * 100;
            double download = 100 + random.nextDouble() * 200;
            
            trends.add(new BandwidthTrend(time, upload, download));
        }
        
        return trends;
    }

    @Override
    public TrafficStats updateTrafficStats(TrafficStats stats) {
        if (stats.getId() == null || stats.getId().isEmpty()) {
            stats.setId(UUID.randomUUID().toString());
        }

        stats.setUpdateTime(LocalDateTime.now());
        statsCache.put(stats.getId(), stats);
        saveStats();

        log.info("更新流量统计: {}", stats.getDeviceName());
        return stats;
    }
}

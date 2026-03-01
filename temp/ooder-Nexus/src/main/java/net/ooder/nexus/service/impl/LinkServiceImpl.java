package net.ooder.nexus.service.impl;

import net.ooder.nexus.service.LinkService;
import net.ooder.sdk.api.network.LinkInfo;
import net.ooder.sdk.api.network.LinkQualityInfo;
import net.ooder.sdk.api.network.LinkListener;
import net.ooder.sdk.api.network.NetworkService;
import net.ooder.sdk.service.network.NetworkServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * P2P链路管理服务实现
 * 集成SDK NetworkService
 *
 * @author ooder Team
 * @version 0.7.0
 * @since 0.7.0
 */
@Service
public class LinkServiceImpl implements LinkService, LinkListener {

    private static final Logger log = LoggerFactory.getLogger(LinkServiceImpl.class);

    private NetworkService networkService;

    private final Map<String, LinkInfo> localLinks = new ConcurrentHashMap<>();

    private volatile boolean qualityMonitorEnabled = false;

    @PostConstruct
    public void init() {
        try {
            networkService = new NetworkServiceImpl();
            networkService.addLinkListener(this);
            log.info("LinkService initialized with SDK NetworkService");
        } catch (Exception e) {
            log.warn("Failed to initialize NetworkService, using local implementation: {}", e.getMessage());
            networkService = null;
        }
    }

    @PreDestroy
    public void destroy() {
        if (networkService != null) {
            try {
                networkService.shutdown();
            } catch (Exception e) {
                log.warn("Error shutting down NetworkService: {}", e.getMessage());
            }
        }
    }

    @Override
    public Map<String, Object> createLink(String sourceId, String targetId, String type) {
        log.info("Creating link: {} -> {} ({})", sourceId, targetId, type);

        try {
            if (networkService != null) {
                LinkInfo.LinkType linkType = parseLinkType(type);
                LinkInfo link = networkService.createLink(sourceId, targetId, linkType);
                return convertLinkToMap(link);
            } else {
                String linkId = "link-" + System.currentTimeMillis();
                LinkInfo link = new LinkInfo();
                link.setLinkId(linkId);
                link.setSourceId(sourceId);
                link.setTargetId(targetId);
                link.setType(parseLinkType(type));
                link.setStatus(LinkInfo.LinkStatus.ACTIVE);
                localLinks.put(linkId, link);
                return convertLinkToMap(link);
            }
        } catch (Exception e) {
            log.error("Failed to create link: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    @Override
    public List<Map<String, Object>> getAllLinks() {
        log.info("Getting all links");

        List<Map<String, Object>> result = new ArrayList<>();

        try {
            if (networkService != null) {
                List<LinkInfo> links = networkService.getAllLinks();
                for (LinkInfo link : links) {
                    result.add(convertLinkToMap(link));
                }
            } else {
                for (LinkInfo link : localLinks.values()) {
                    result.add(convertLinkToMap(link));
                }
            }
        } catch (Exception e) {
            log.error("Failed to get all links: {}", e.getMessage(), e);
        }

        return result;
    }

    @Override
    public Map<String, Object> getLink(String linkId) {
        log.info("Getting link: {}", linkId);

        try {
            if (networkService != null) {
                Optional<LinkInfo> link = networkService.getLink(linkId);
                if (link.isPresent()) {
                    return convertLinkToMap(link.get());
                }
            } else {
                LinkInfo localLink = localLinks.get(linkId);
                if (localLink != null) {
                    return convertLinkToMap(localLink);
                }
            }
        } catch (Exception e) {
            log.error("Failed to get link: {}", e.getMessage(), e);
        }

        Map<String, Object> notFound = new HashMap<>();
        notFound.put("error", "Link not found: " + linkId);
        return notFound;
    }

    @Override
    public Map<String, Object> getLinkBetween(String sourceId, String targetId) {
        log.info("Getting link between: {} -> {}", sourceId, targetId);

        try {
            if (networkService != null) {
                Optional<LinkInfo> link = networkService.getLink(sourceId, targetId);
                if (link.isPresent()) {
                    return convertLinkToMap(link.get());
                }
            } else {
                for (LinkInfo link : localLinks.values()) {
                    if (sourceId.equals(link.getSourceId()) && targetId.equals(link.getTargetId())) {
                        return convertLinkToMap(link);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to get link between: {}", e.getMessage(), e);
        }

        Map<String, Object> notFound = new HashMap<>();
        notFound.put("error", "Link not found between " + sourceId + " and " + targetId);
        return notFound;
    }

    @Override
    public boolean removeLink(String linkId) {
        log.info("Removing link: {}", linkId);

        try {
            if (networkService != null) {
                networkService.removeLink(linkId);
                return true;
            } else {
                LinkInfo removed = localLinks.remove(linkId);
                return removed != null;
            }
        } catch (Exception e) {
            log.error("Failed to remove link: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getLinkQuality(String linkId) {
        log.info("Getting link quality: {}", linkId);

        try {
            if (networkService != null) {
                LinkQualityInfo quality = networkService.getLinkQuality(linkId);
                return convertQualityToMap(quality);
            } else {
                LinkInfo link = localLinks.get(linkId);
                if (link != null && link.getQuality() != null) {
                    return convertQualityToMap(link.getQuality());
                }
            }
        } catch (Exception e) {
            log.error("Failed to get link quality: {}", e.getMessage(), e);
        }

        Map<String, Object> notFound = new HashMap<>();
        notFound.put("error", "Link quality not found: " + linkId);
        return notFound;
    }

    @Override
    public void updateLinkQuality(String linkId, int latency, double packetLoss) {
        log.info("Updating link quality: {} latency={}ms packetLoss={}", linkId, latency, packetLoss);

        try {
            if (networkService != null) {
                networkService.updateLinkQuality(linkId, latency, packetLoss);
            } else {
                LinkInfo link = localLinks.get(linkId);
                if (link != null) {
                    LinkQualityInfo quality = link.getQuality();
                    if (quality == null) {
                        quality = new LinkQualityInfo();
                        link.setQuality(quality);
                    }
                    quality.setLatency(latency);
                    quality.setPacketLoss(packetLoss);
                }
            }
        } catch (Exception e) {
            log.error("Failed to update link quality: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> findOptimalPath(String sourceId, String targetId) {
        log.info("Finding optimal path: {} -> {}", sourceId, targetId);

        List<Map<String, Object>> result = new ArrayList<>();

        try {
            if (networkService != null) {
                List<LinkInfo> path = networkService.findOptimalPath(sourceId, targetId);
                for (LinkInfo link : path) {
                    result.add(convertLinkToMap(link));
                }
            } else {
                log.warn("Optimal path finding not available in local mode");
            }
        } catch (Exception e) {
            log.error("Failed to find optimal path: {}", e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<List<Map<String, Object>>> findAllPaths(String sourceId, String targetId, int maxPaths) {
        log.info("Finding all paths: {} -> {} (max {})", sourceId, targetId, maxPaths);

        List<List<Map<String, Object>>> result = new ArrayList<>();

        try {
            if (networkService != null) {
                List<List<LinkInfo>> paths = networkService.findAllPaths(sourceId, targetId, maxPaths);
                for (List<LinkInfo> path : paths) {
                    List<Map<String, Object>> pathLinks = new ArrayList<>();
                    for (LinkInfo link : path) {
                        pathLinks.add(convertLinkToMap(link));
                    }
                    result.add(pathLinks);
                }
            } else {
                log.warn("All paths finding not available in local mode");
            }
        } catch (Exception e) {
            log.error("Failed to find all paths: {}", e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getLinksFrom(String sourceId) {
        log.info("Getting links from: {}", sourceId);

        List<Map<String, Object>> result = new ArrayList<>();

        try {
            if (networkService != null) {
                List<LinkInfo> links = networkService.getLinksFrom(sourceId);
                for (LinkInfo link : links) {
                    result.add(convertLinkToMap(link));
                }
            } else {
                for (LinkInfo link : localLinks.values()) {
                    if (sourceId.equals(link.getSourceId())) {
                        result.add(convertLinkToMap(link));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to get links from: {}", e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getLinksTo(String targetId) {
        log.info("Getting links to: {}", targetId);

        List<Map<String, Object>> result = new ArrayList<>();

        try {
            if (networkService != null) {
                List<LinkInfo> links = networkService.getLinksTo(targetId);
                for (LinkInfo link : links) {
                    result.add(convertLinkToMap(link));
                }
            } else {
                for (LinkInfo link : localLinks.values()) {
                    if (targetId.equals(link.getTargetId())) {
                        result.add(convertLinkToMap(link));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to get links to: {}", e.getMessage(), e);
        }

        return result;
    }

    @Override
    public Map<String, Object> getNetworkStats() {
        log.info("Getting network stats");

        Map<String, Object> result = new HashMap<>();

        try {
            if (networkService != null) {
                NetworkService.NetworkStats stats = networkService.getNetworkStats();
                result.put("totalLinks", stats.getTotalLinks());
                result.put("activeLinks", stats.getActiveLinks());
                result.put("degradedLinks", stats.getDegradedLinks());
                result.put("failedLinks", stats.getFailedLinks());
                result.put("averageLatency", stats.getAverageLatency());
                result.put("averagePacketLoss", stats.getAveragePacketLoss());
                result.put("totalBytesTransmitted", stats.getTotalBytesTransmitted());
                result.put("totalBytesReceived", stats.getTotalBytesReceived());
            } else {
                result.put("totalLinks", localLinks.size());
                result.put("activeLinks", localLinks.size());
                result.put("degradedLinks", 0);
                result.put("failedLinks", 0);
            }
        } catch (Exception e) {
            log.error("Failed to get network stats: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @Override
    public void enableQualityMonitor(long intervalMs) {
        log.info("Enabling quality monitor with interval: {}ms", intervalMs);

        try {
            if (networkService != null) {
                networkService.enableQualityMonitor(intervalMs);
            }
            qualityMonitorEnabled = true;
        } catch (Exception e) {
            log.error("Failed to enable quality monitor: {}", e.getMessage(), e);
        }
    }

    @Override
    public void disableQualityMonitor() {
        log.info("Disabling quality monitor");

        try {
            if (networkService != null) {
                networkService.disableQualityMonitor();
            }
            qualityMonitorEnabled = false;
        } catch (Exception e) {
            log.error("Failed to disable quality monitor: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isQualityMonitorEnabled() {
        try {
            if (networkService != null) {
                return networkService.isQualityMonitorEnabled();
            }
            return qualityMonitorEnabled;
        } catch (Exception e) {
            return qualityMonitorEnabled;
        }
    }

    @Override
    public int getLinkCount() {
        try {
            if (networkService != null) {
                return networkService.getLinkCount();
            }
            return localLinks.size();
        } catch (Exception e) {
            return localLinks.size();
        }
    }

    @Override
    public boolean hasLink(String sourceId, String targetId) {
        try {
            if (networkService != null) {
                return networkService.hasLink(sourceId, targetId);
            }
            for (LinkInfo link : localLinks.values()) {
                if (sourceId.equals(link.getSourceId()) && targetId.equals(link.getTargetId())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onLinkCreated(LinkInfo link) {
        log.info("Link created: {}", link.getLinkId());
    }

    @Override
    public void onLinkRemoved(String linkId) {
        log.info("Link removed: {}", linkId);
        localLinks.remove(linkId);
    }

    @Override
    public void onQualityChanged(String linkId, LinkQualityInfo quality) {
        log.debug("Link quality changed: {} -> {}", linkId, quality.getQualityLevel());
    }

    @Override
    public void onStatusChanged(String linkId, String status) {
        log.info("Link status changed: {} -> {}", linkId, status);
    }

    private LinkInfo.LinkType parseLinkType(String type) {
        if (type == null) {
            return LinkInfo.LinkType.DIRECT;
        }
        try {
            return LinkInfo.LinkType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LinkInfo.LinkType.DIRECT;
        }
    }

    private Map<String, Object> convertLinkToMap(LinkInfo link) {
        Map<String, Object> map = new HashMap<>();
        map.put("linkId", link.getLinkId());
        map.put("sourceId", link.getSourceId());
        map.put("targetId", link.getTargetId());
        map.put("type", link.getType() != null ? link.getType().name() : "UNKNOWN");
        map.put("status", link.getStatus() != null ? link.getStatus().name() : "UNKNOWN");
        map.put("createTime", link.getCreateTime());
        map.put("lastActiveTime", link.getLastActiveTime());
        map.put("avgLatency", link.getAvgLatency());
        map.put("packetLossRate", link.getPacketLossRate());
        map.put("qualityLevel", link.getQualityLevel());
        map.put("totalBytesSent", link.getTotalBytesSent());
        map.put("totalBytesReceived", link.getTotalBytesReceived());
        map.put("reconnectCount", link.getReconnectCount());

        if (link.getQuality() != null) {
            map.put("quality", convertQualityToMap(link.getQuality()));
        }

        return map;
    }

    private Map<String, Object> convertQualityToMap(LinkQualityInfo quality) {
        Map<String, Object> map = new HashMap<>();
        map.put("linkId", quality.getLinkId());
        map.put("qualityLevel", quality.getQualityLevel() != null ? quality.getQualityLevel().name() : "UNKNOWN");
        map.put("latency", quality.getLatency());
        map.put("packetLoss", quality.getPacketLoss());
        map.put("bandwidth", quality.getBandwidth());
        map.put("score", quality.getScore());
        map.put("lastCheckTime", quality.getLastCheckTime());
        map.put("consecutiveFailures", quality.getConsecutiveFailures());
        return map;
    }
}

package net.ooder.skill.network.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class NetworkProviderImpl implements NetworkProvider {
    
    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    
    private final Map<String, NetworkLink> links = new HashMap<>();
    private final Map<String, NetworkRoute> routes = new HashMap<>();
    
    @Override
    public String getProviderName() {
        return "skill-network";
    }
    
    @Override
    public String getVersion() {
        return "0.7.3";
    }
    
    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
    }
    
    @Override
    public void start() {
        this.running = true;
    }
    
    @Override
    public void stop() {
        this.running = false;
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public NetworkStatus getStatus() {
        NetworkStatus status = new NetworkStatus();
        status.setStatus("ONLINE");
        status.setNodeId("node-" + UUID.randomUUID().toString().substring(0, 8));
        status.setNodeType("skill-network");
        status.setOnline(true);
        status.setConnectedPeers(links.size());
        status.setLocalAddress("127.0.0.1");
        status.setLocalPort(8083);
        status.setUptime(System.currentTimeMillis());
        return status;
    }
    
    @Override
    public NetworkStats getStats() {
        NetworkStats stats = new NetworkStats();
        stats.setTotalLinks(links.size());
        stats.setActiveLinks(links.values().stream()
                .filter(l -> "active".equals(l.getStatus()))
                .count());
        stats.setTotalRoutes(routes.size());
        stats.setTotalBytes(0);
        stats.setTotalPackets(0);
        stats.setAvgLatency(50.0);
        stats.setPacketLoss(0.0);
        return stats;
    }
    
    @Override
    public PageResult<NetworkLink> listLinks(int page, int size) {
        List<NetworkLink> allLinks = new ArrayList<>(links.values());
        int start = page * size;
        int end = Math.min(start + size, allLinks.size());
        List<NetworkLink> pageLinks = start < allLinks.size() 
                ? allLinks.subList(start, end) 
                : new ArrayList<>();
        return new PageResult<>(pageLinks, allLinks.size(), page, size);
    }
    
    @Override
    public NetworkLink getLink(String linkId) {
        return links.get(linkId);
    }
    
    @Override
    public boolean disconnectLink(String linkId) {
        NetworkLink link = links.get(linkId);
        if (link != null) {
            link.setStatus("disconnected");
            return true;
        }
        return false;
    }
    
    @Override
    public boolean reconnectLink(String linkId) {
        NetworkLink link = links.get(linkId);
        if (link != null) {
            link.setStatus("active");
            return true;
        }
        return false;
    }
    
    @Override
    public PageResult<NetworkRoute> listRoutes(int page, int size) {
        List<NetworkRoute> allRoutes = new ArrayList<>(routes.values());
        int start = page * size;
        int end = Math.min(start + size, allRoutes.size());
        List<NetworkRoute> pageRoutes = start < allRoutes.size() 
                ? allRoutes.subList(start, end) 
                : new ArrayList<>();
        return new PageResult<>(pageRoutes, allRoutes.size(), page, size);
    }
    
    @Override
    public NetworkRoute getRoute(String routeId) {
        return routes.get(routeId);
    }
    
    @Override
    public NetworkRoute findRoute(String source, String target, String algorithm, int maxHops) {
        NetworkRoute route = new NetworkRoute();
        route.setRouteId(UUID.randomUUID().toString());
        route.setSourceNode(source);
        route.setTargetNode(target);
        route.setHops(Arrays.asList(source, target));
        route.setTotalLatency(50);
        route.setHopCount(1);
        route.setStatus("active");
        route.setRouteType(algorithm != null ? algorithm : "direct");
        route.setCreatedAt(System.currentTimeMillis());
        return route;
    }
    
    @Override
    public NetworkTopology getTopology() {
        NetworkTopology topology = new NetworkTopology();
        topology.setTopologyId(UUID.randomUUID().toString());
        
        List<TopologyNode> nodes = new ArrayList<>();
        TopologyNode localNode = new TopologyNode();
        localNode.setNodeId("local");
        localNode.setNodeType("skill-network");
        localNode.setLabel("Local Node");
        localNode.setX(0);
        localNode.setY(0);
        nodes.add(localNode);
        
        topology.setNodes(nodes);
        topology.setEdges(new ArrayList<>());
        topology.setUpdatedAt(System.currentTimeMillis());
        
        return topology;
    }
    
    @Override
    public NetworkQuality getQuality() {
        NetworkQuality quality = new NetworkQuality();
        quality.setLatency(50.0);
        quality.setJitter(5.0);
        quality.setPacketLoss(0.0);
        quality.setBandwidth(1000);
        quality.setQuality("good");
        return quality;
    }
}

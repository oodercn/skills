package net.ooder.skill.network.service.impl;

import net.ooder.skill.network.dto.*;
import net.ooder.skill.network.service.NetworkService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NetworkServiceImpl implements NetworkService {

    private final Map<String, NetworkLink> links = new ConcurrentHashMap<>();
    private final Map<String, NetworkRoute> routes = new ConcurrentHashMap<>();
    private final String localNodeId = "node-" + UUID.randomUUID().toString().substring(0, 8);

    public NetworkServiceImpl() {
        initDefaultData();
    }

    private void initDefaultData() {
        NetworkLink defaultLink = new NetworkLink();
        defaultLink.setLinkId("link-default-001");
        defaultLink.setSourceNode(localNodeId);
        defaultLink.setTargetNode("node-mcp-server");
        defaultLink.setLinkType("p2p");
        defaultLink.setStatus("active");
        defaultLink.setLatency(10);
        defaultLink.setBandwidth(1000);
        links.put(defaultLink.getLinkId(), defaultLink);
    }

    @Override
    public NetworkStatus getStatus() {
        NetworkStatus status = new NetworkStatus();
        status.setNodeId(localNodeId);
        status.setStatus("online");
        status.setOnline(true);
        status.setConnectedPeers(links.size());
        status.setLocalAddress("0.0.0.0");
        status.setLocalPort(8080);
        return status;
    }

    @Override
    public NetworkStats getStats() {
        NetworkStats stats = new NetworkStats();
        stats.setTotalBytesSent(System.currentTimeMillis() % 10000000);
        stats.setTotalBytesReceived(System.currentTimeMillis() % 10000000);
        stats.setTotalPacketsSent(System.currentTimeMillis() % 100000);
        stats.setTotalPacketsReceived(System.currentTimeMillis() % 100000);
        stats.setTotalErrors(0);
        stats.setAverageLatency(15.5);
        stats.setBandwidth(100.0);
        return stats;
    }

    @Override
    public PageResult<NetworkLink> listLinks(int page, int size) {
        List<NetworkLink> allLinks = new ArrayList<>(links.values());
        int start = page * size;
        int end = Math.min(start + size, allLinks.size());
        List<NetworkLink> pageItems = start < allLinks.size() ? allLinks.subList(start, end) : new ArrayList<>();
        return new PageResult<>(pageItems, page, size, allLinks.size());
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
            link.setLastActive(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public boolean reconnectLink(String linkId) {
        NetworkLink link = links.get(linkId);
        if (link != null) {
            link.setStatus("active");
            link.setLastActive(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public PageResult<NetworkRoute> listRoutes(int page, int size) {
        List<NetworkRoute> allRoutes = new ArrayList<>(routes.values());
        int start = page * size;
        int end = Math.min(start + size, allRoutes.size());
        List<NetworkRoute> pageItems = start < allRoutes.size() ? allRoutes.subList(start, end) : new ArrayList<>();
        return new PageResult<>(pageItems, page, size, allRoutes.size());
    }

    @Override
    public NetworkRoute getRoute(String routeId) {
        return routes.get(routeId);
    }

    @Override
    public NetworkRoute findRoute(String source, String target, String algorithm, int maxHops) {
        NetworkRoute route = new NetworkRoute();
        route.setRouteId("route-" + UUID.randomUUID().toString().substring(0, 8));
        route.setSourceNode(source != null ? source : localNodeId);
        route.setTargetNode(target != null ? target : "node-mcp-server");
        route.setHops(Arrays.asList(route.getSourceNode(), route.getTargetNode()));
        route.setHopCount(1);
        route.setTotalLatency(10);
        route.setRouteType(algorithm != null ? algorithm : "shortest");
        routes.put(route.getRouteId(), route);
        return route;
    }

    @Override
    public NetworkTopology getTopology() {
        NetworkTopology topology = new NetworkTopology();
        topology.setTopologyId("topology-" + System.currentTimeMillis());
        topology.setNodes(Arrays.asList(localNodeId, "node-mcp-server"));
        topology.setLinks(new ArrayList<>(links.values()));
        topology.setMetadata(new HashMap<>());
        topology.setUpdatedAt(System.currentTimeMillis());
        return topology;
    }

    @Override
    public NetworkQuality getQuality() {
        NetworkQuality quality = new NetworkQuality();
        quality.setNodeId(localNodeId);
        quality.setLatency(15.5);
        quality.setJitter(2.3);
        quality.setPacketLoss(0.01);
        quality.setBandwidth(100.0);
        quality.setQuality("good");
        return quality;
    }
}

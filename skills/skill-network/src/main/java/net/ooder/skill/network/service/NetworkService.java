package net.ooder.skill.network.service;

import net.ooder.skill.network.dto.*;

import java.util.List;

public interface NetworkService {
    NetworkStatus getStatus();
    NetworkStats getStats();
    PageResult<NetworkLink> listLinks(int page, int size);
    NetworkLink getLink(String linkId);
    boolean disconnectLink(String linkId);
    boolean reconnectLink(String linkId);
    PageResult<NetworkRoute> listRoutes(int page, int size);
    NetworkRoute getRoute(String routeId);
    NetworkRoute findRoute(String source, String target, String algorithm, int maxHops);
    NetworkTopology getTopology();
    NetworkQuality getQuality();
}

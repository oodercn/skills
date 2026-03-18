package net.ooder.mvp.skill.scene.network.service.impl;

import net.ooder.mvp.skill.scene.capability.service.CapabilityBindingService;
import net.ooder.mvp.skill.scene.network.dto.LinkDTO;
import net.ooder.mvp.skill.scene.network.dto.NetworkTopologyDTO;
import net.ooder.mvp.skill.scene.network.service.NetworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NetworkServiceImpl implements NetworkService {

    private static final Logger log = LoggerFactory.getLogger(NetworkServiceImpl.class);

    @Value("${network.service.url:http://localhost:8085}")
    private String networkServiceUrl;

    @Value("${network.mock.enabled:true}")
    private boolean mockEnabled;

    @Autowired
    private CapabilityBindingService bindingService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, LinkDTO> linkCache = new ConcurrentHashMap<>();

    @Override
    public NetworkTopologyDTO getTopology() {
        if (!mockEnabled) {
            try {
                String url = networkServiceUrl + "/api/network/topology";
                ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return convertToTopologyDTO(response.getBody());
                }
            } catch (Exception e) {
                log.warn("Failed to get topology from network service: {}", e.getMessage());
            }
        }
        
        return getDefaultTopology();
    }

    @Override
    public List<LinkDTO> listLinks() {
        NetworkTopologyDTO topology = getTopology();
        List<LinkDTO> links = new ArrayList<>();
        
        if (topology.getLinks() != null) {
            for (NetworkTopologyDTO.TopologyLink topoLink : topology.getLinks()) {
                LinkDTO link = convertToLinkDTO(topoLink, topology);
                link.setBindingCount(getBindingCount(link.getLinkId()));
                links.add(link);
                linkCache.put(link.getLinkId(), link);
            }
        }
        
        if (links.isEmpty()) {
            links = getDefaultLinks();
        }
        
        return links;
    }

    @Override
    public LinkDTO getLink(String linkId) {
        LinkDTO cached = linkCache.get(linkId);
        if (cached != null) {
            return cached;
        }
        
        List<LinkDTO> allLinks = listLinks();
        for (LinkDTO link : allLinks) {
            if (linkId.equals(link.getLinkId())) {
                return link;
            }
        }
        return null;
    }

    @Override
    public List<LinkDTO> searchLinks(String keyword) {
        List<LinkDTO> allLinks = listLinks();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allLinks;
        }
        
        String lowerKeyword = keyword.toLowerCase();
        List<LinkDTO> result = new ArrayList<>();
        for (LinkDTO link : allLinks) {
            if ((link.getLinkId() != null && link.getLinkId().toLowerCase().contains(lowerKeyword)) ||
                (link.getSourceNode() != null && link.getSourceNode().toLowerCase().contains(lowerKeyword)) ||
                (link.getTargetNode() != null && link.getTargetNode().toLowerCase().contains(lowerKeyword))) {
                result.add(link);
            }
        }
        return result;
    }

    @Override
    public boolean reconnectLink(String linkId) {
        if (!mockEnabled) {
            try {
                String url = networkServiceUrl + "/api/network/links/" + linkId + "/reconnect";
                ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
                return response.getStatusCode().is2xxSuccessful();
            } catch (Exception e) {
                log.warn("Failed to reconnect link: {}", e.getMessage());
                return false;
            }
        }
        
        LinkDTO link = getLink(linkId);
        if (link != null) {
            link.setStatus("active");
            link.setLastActive(System.currentTimeMillis());
            linkCache.put(linkId, link);
            log.info("Link reconnected: {}", linkId);
            return true;
        }
        return false;
    }

    @Override
    public boolean disconnectLink(String linkId) {
        if (!mockEnabled) {
            try {
                String url = networkServiceUrl + "/api/network/links/" + linkId;
                restTemplate.delete(url);
                return true;
            } catch (Exception e) {
                log.warn("Failed to disconnect link: {}", e.getMessage());
                return false;
            }
        }
        
        LinkDTO link = getLink(linkId);
        if (link != null) {
            link.setStatus("inactive");
            linkCache.put(linkId, link);
            log.info("Link disconnected: {}", linkId);
            return true;
        }
        return false;
    }

    @Override
    public int getBindingCount(String linkId) {
        return bindingService.listByLink(linkId).size();
    }

    @SuppressWarnings("unchecked")
    private NetworkTopologyDTO convertToTopologyDTO(Map<String, Object> response) {
        NetworkTopologyDTO topology = new NetworkTopologyDTO();
        
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        if (data == null) {
            data = response;
        }
        
        topology.setTopologyId((String) data.getOrDefault("id", "topology-1"));
        topology.setName((String) data.getOrDefault("name", "Network Topology"));
        topology.setUpdatedAt(System.currentTimeMillis());
        
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("nodes");
        if (nodes != null) {
            for (Map<String, Object> node : nodes) {
                NetworkTopologyDTO.TopologyNode topoNode = new NetworkTopologyDTO.TopologyNode(
                    (String) node.get("id"),
                    (String) node.get("name"),
                    (String) node.get("type"),
                    (String) node.get("ip"),
                    (String) node.get("status")
                );
                if (node.containsKey("x")) topoNode.setX(((Number) node.get("x")).intValue());
                if (node.containsKey("y")) topoNode.setY(((Number) node.get("y")).intValue());
                topology.getNodes().add(topoNode);
            }
        }
        
        List<Map<String, Object>> links = (List<Map<String, Object>>) data.get("links");
        if (links != null) {
            for (Map<String, Object> link : links) {
                NetworkTopologyDTO.TopologyLink topoLink = new NetworkTopologyDTO.TopologyLink(
                    (String) link.get("id"),
                    (String) link.get("source"),
                    (String) link.get("target"),
                    (String) link.get("type")
                );
                if (link.containsKey("latency")) topoLink.setLatency(((Number) link.get("latency")).longValue());
                if (link.containsKey("status")) topoLink.setStatus((String) link.get("status"));
                topology.getLinks().add(topoLink);
            }
        }
        
        return topology;
    }

    private LinkDTO convertToLinkDTO(NetworkTopologyDTO.TopologyLink topoLink, NetworkTopologyDTO topology) {
        LinkDTO link = new LinkDTO();
        link.setLinkId(topoLink.getId());
        link.setLinkType(topoLink.getType());
        link.setSourceNode(topoLink.getSource());
        link.setTargetNode(topoLink.getTarget());
        link.setStatus(topoLink.getStatus() != null ? topoLink.getStatus() : "active");
        link.setLatency(topoLink.getLatency());
        link.setLastActive(System.currentTimeMillis());
        link.setEstablishedAt(System.currentTimeMillis() - 3600000);
        return link;
    }

    private NetworkTopologyDTO getDefaultTopology() {
        NetworkTopologyDTO topology = new NetworkTopologyDTO();
        topology.setTopologyId("topology-local-1");
        topology.setName("Local Network Topology");
        topology.setUpdatedAt(System.currentTimeMillis());
        
        topology.getNodes().add(new NetworkTopologyDTO.TopologyNode(
            "node-local", "Local Node", "personal", "127.0.0.1", "online"
        ));
        topology.getNodes().add(new NetworkTopologyDTO.TopologyNode(
            "node-agent-001", "Agent Node 1", "agent", "192.168.1.101", "online"
        ));
        topology.getNodes().add(new NetworkTopologyDTO.TopologyNode(
            "node-agent-002", "Agent Node 2", "agent", "192.168.1.102", "online"
        ));
        topology.getNodes().add(new NetworkTopologyDTO.TopologyNode(
            "node-super-001", "Super Agent", "super-agent", "192.168.1.201", "online"
        ));
        
        topology.getLinks().add(new NetworkTopologyDTO.TopologyLink(
            "link-local-agent1", "node-local", "node-agent-001", "direct"
        ));
        topology.getLinks().add(new NetworkTopologyDTO.TopologyLink(
            "link-local-agent2", "node-local", "node-agent-002", "relay"
        ));
        topology.getLinks().add(new NetworkTopologyDTO.TopologyLink(
            "link-local-super", "node-local", "node-super-001", "tunnel"
        ));
        topology.getLinks().add(new NetworkTopologyDTO.TopologyLink(
            "link-agent1-super", "node-agent-001", "node-super-001", "p2p"
        ));
        topology.getLinks().add(new NetworkTopologyDTO.TopologyLink(
            "link-agent2-super", "node-agent-002", "node-super-001", "p2p"
        ));
        
        int i = 0;
        for (NetworkTopologyDTO.TopologyLink link : topology.getLinks()) {
            link.setLatency(10 + i * 15);
            link.setStatus("active");
            i++;
        }
        
        return topology;
    }

    private List<LinkDTO> getDefaultLinks() {
        List<LinkDTO> links = new ArrayList<>();
        
        LinkDTO link1 = new LinkDTO();
        link1.setLinkId("link-local-agent1");
        link1.setLinkType("DIRECT");
        link1.setSourceNode("node-local");
        link1.setTargetNode("node-agent-001");
        link1.setStatus("active");
        link1.setLatency(15);
        link1.setBandwidth(1000);
        link1.setEstablishedAt(System.currentTimeMillis() - 7200000);
        link1.setLastActive(System.currentTimeMillis());
        link1.setBindingCount(getBindingCount("link-local-agent1"));
        links.add(link1);
        
        LinkDTO link2 = new LinkDTO();
        link2.setLinkId("link-local-agent2");
        link2.setLinkType("RELAY");
        link2.setSourceNode("node-local");
        link2.setTargetNode("node-agent-002");
        link2.setStatus("active");
        link2.setLatency(45);
        link2.setBandwidth(500);
        link2.setEstablishedAt(System.currentTimeMillis() - 3600000);
        link2.setLastActive(System.currentTimeMillis() - 60000);
        link2.setBindingCount(getBindingCount("link-local-agent2"));
        links.add(link2);
        
        LinkDTO link3 = new LinkDTO();
        link3.setLinkId("link-local-super");
        link3.setLinkType("TUNNEL");
        link3.setSourceNode("node-local");
        link3.setTargetNode("node-super-001");
        link3.setStatus("active");
        link3.setLatency(25);
        link3.setBandwidth(2000);
        link3.setEstablishedAt(System.currentTimeMillis() - 86400000);
        link3.setLastActive(System.currentTimeMillis());
        link3.setBindingCount(getBindingCount("link-local-super"));
        links.add(link3);
        
        return links;
    }
}

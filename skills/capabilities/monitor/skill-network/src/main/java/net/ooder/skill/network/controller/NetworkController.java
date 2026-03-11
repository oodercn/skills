package net.ooder.skill.network.controller;

import net.ooder.skill.network.model.NetworkDevice;
import net.ooder.skill.network.model.NetworkNode;
import net.ooder.skill.network.model.NetworkStats;
import net.ooder.skill.network.model.NetworkTopology;
import net.ooder.skill.network.p2p.P2PNodeManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/network")
public class NetworkController {

    private final P2PNodeManager p2pManager;

    public NetworkController() {
        this.p2pManager = P2PNodeManager.getInstance();
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", Map.of(
            "p2pRunning", p2pManager.isRunning(),
            "localNode", p2pManager.getLocalNode(),
            "totalNodes", p2pManager.getNodeCount(),
            "onlineNodes", p2pManager.getOnlineNodeCount()
        ));
        return result;
    }

    @GetMapping("/nodes")
    public Map<String, Object> getNodes() {
        List<NetworkNode> nodes = p2pManager.getAllDiscoveredNodes();
        nodes.add(0, p2pManager.getLocalNode());

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", nodes);
        result.put("total", nodes.size());
        return result;
    }

    @GetMapping("/nodes/{nodeId}")
    public Map<String, Object> getNode(@PathVariable String nodeId) {
        Map<String, Object> result = new HashMap<>();
        
        if (nodeId.equals(p2pManager.getLocalNode().getId())) {
            result.put("status", "success");
            result.put("data", p2pManager.getLocalNode());
            return result;
        }

        for (NetworkNode node : p2pManager.getAllDiscoveredNodes()) {
            if (node.getId().equals(nodeId)) {
                result.put("status", "success");
                result.put("data", node);
                return result;
            }
        }

        result.put("status", "error");
        result.put("message", "Node not found: " + nodeId);
        return result;
    }

    @PostMapping("/start")
    public Map<String, Object> startP2P() {
        Map<String, Object> result = new HashMap<>();
        try {
            p2pManager.start();
            result.put("status", "success");
            result.put("message", "P2P service started");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/stop")
    public Map<String, Object> stopP2P() {
        Map<String, Object> result = new HashMap<>();
        try {
            p2pManager.stop();
            result.put("status", "success");
            result.put("message", "P2P service stopped");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/topology")
    public Map<String, Object> getTopology() {
        NetworkTopology topology = new NetworkTopology();
        topology.setId("network-topology-1");
        topology.setName("Local Network Topology");

        NetworkNode localNode = p2pManager.getLocalNode();
        topology.addNode(new NetworkTopology.TopologyNode(
            localNode.getId(),
            localNode.getName(),
            localNode.getType() != null ? localNode.getType().getCode() : "personal",
            localNode.getIp(),
            localNode.getStatus() != null ? localNode.getStatus().getCode() : "online"
        ));

        int x = 100, y = 100;
        for (NetworkNode node : p2pManager.getAllDiscoveredNodes()) {
            NetworkTopology.TopologyNode topoNode = new NetworkTopology.TopologyNode(
                node.getId(),
                node.getName(),
                node.getType() != null ? node.getType().getCode() : "personal",
                node.getIp(),
                node.getStatus() != null ? node.getStatus().getCode() : "offline"
            );
            topoNode.setX(x);
            topoNode.setY(y);
            topology.addNode(topoNode);

            topology.addLink(new NetworkTopology.TopologyLink(
                "link-" + localNode.getId() + "-" + node.getId(),
                localNode.getId(),
                node.getId(),
                "ethernet"
            ));

            x += 150;
            if (x > 500) {
                x = 100;
                y += 100;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", topology);
        return result;
    }

    @GetMapping("/devices")
    public Map<String, Object> getDevices() {
        List<NetworkDevice> devices = new ArrayList<>();

        NetworkDevice router = new NetworkDevice();
        router.setId("device-router-1");
        router.setName("Main Router");
        router.setType("router");
        router.setIpAddress("192.168.1.1");
        router.setMacAddress("00:11:22:33:44:55");
        router.setStatus("online");
        router.setVendor("TP-Link");
        router.setModel("AX3000");
        devices.add(router);

        NetworkDevice ap = new NetworkDevice();
        ap.setId("device-ap-1");
        ap.setName("Access Point");
        ap.setType("access-point");
        ap.setIpAddress("192.168.1.2");
        ap.setMacAddress("00:11:22:33:44:56");
        ap.setStatus("online");
        ap.setVendor("Ubiquiti");
        ap.setModel("UAP-AC-Pro");
        devices.add(ap);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", devices);
        result.put("total", devices.size());
        return result;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        NetworkStats stats = new NetworkStats();
        stats.setTotalNodes(p2pManager.getNodeCount() + 1);
        stats.setOnlineNodes(p2pManager.getOnlineNodeCount() + 1);
        stats.setActiveConnections(5);
        stats.setTotalBytesIn(1024 * 1024 * 500);
        stats.setTotalBytesOut(1024 * 1024 * 300);
        stats.setAvgLatency(15.5);
        stats.setBandwidthUsage(45.2);
        stats.setCpuUsage(32.1);
        stats.setMemoryUsage(58.3);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", stats);
        return result;
    }

    @PostMapping("/share/{skillId}")
    public Map<String, Object> shareSkill(@PathVariable String skillId) {
        Map<String, Object> result = new HashMap<>();
        try {
            p2pManager.shareSkill(skillId);
            result.put("status", "success");
            result.put("message", "Skill shared: " + skillId);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/share/{skillId}")
    public Map<String, Object> unshareSkill(@PathVariable String skillId) {
        Map<String, Object> result = new HashMap<>();
        try {
            p2pManager.unshareSkill(skillId);
            result.put("status", "success");
            result.put("message", "Skill unshared: " + skillId);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }
}

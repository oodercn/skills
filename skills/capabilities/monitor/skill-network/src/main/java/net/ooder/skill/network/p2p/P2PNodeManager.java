package net.ooder.skill.network.p2p;

import net.ooder.skill.network.model.NetworkNode;
import net.ooder.skill.network.model.NetworkNode.NodeStatus;
import net.ooder.skill.network.model.NetworkNode.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class P2PNodeManager {
    private static final Logger logger = LoggerFactory.getLogger(P2PNodeManager.class);

    private static P2PNodeManager instance;

    private NetworkNode localNode;
    private Map<String, NetworkNode> discoveredNodes;
    private Map<String, List<NetworkNode>> sharedSkills;
    private ExecutorService executorService;
    private List<P2PEventListener> eventListeners;
    private boolean running;

    private P2PNodeManager() {
        this.discoveredNodes = new ConcurrentHashMap<>();
        this.sharedSkills = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(5);
        this.eventListeners = Collections.synchronizedList(new ArrayList<>());
        this.localNode = createLocalNode();
        this.running = false;
    }

    public static synchronized P2PNodeManager getInstance() {
        if (instance == null) {
            instance = new P2PNodeManager();
        }
        return instance;
    }

    private NetworkNode createLocalNode() {
        NetworkNode node = new NetworkNode();
        node.setId(UUID.randomUUID().toString());
        node.setName("Ooder-Node-" + System.getProperty("user.name"));
        node.setType(NodeType.PERSONAL);
        node.setIp(getLocalIp());
        node.setPort(8080);
        node.setStatus(NodeStatus.ONLINE);
        node.setLastSeen(System.currentTimeMillis());
        return node;
    }

    private String getLocalIp() {
        return "127.0.0.1";
    }

    public void start() {
        if (running) {
            return;
        }
        
        running = true;
        logger.info("Starting P2P service for node: {}", localNode.getName());

        executorService.submit(this::discoverNodes);
        executorService.submit(this::heartbeatLoop);

        notifyEventListeners(P2PEventType.SERVICE_STARTED, localNode);
    }

    public void stop() {
        running = false;
        logger.info("Stopping P2P service for node: {}", localNode.getName());

        localNode.setStatus(NodeStatus.OFFLINE);
        executorService.shutdown();

        notifyEventListeners(P2PEventType.SERVICE_STOPPED, localNode);
    }

    private void discoverNodes() {
        logger.info("Discovering nodes in network...");

        if (discoveredNodes.isEmpty()) {
            simulateNodeDiscovery();
        }
    }

    private void simulateNodeDiscovery() {
        NetworkNode node1 = new NetworkNode();
        node1.setId(UUID.randomUUID().toString());
        node1.setName("Home-Server");
        node1.setType(NodeType.HOME_SERVER);
        node1.setIp("192.168.1.100");
        node1.setPort(8080);
        node1.setStatus(NodeStatus.ONLINE);
        node1.setLastSeen(System.currentTimeMillis());

        NetworkNode node2 = new NetworkNode();
        node2.setId(UUID.randomUUID().toString());
        node2.setName("Workstation");
        node2.setType(NodeType.PERSONAL);
        node2.setIp("192.168.1.101");
        node2.setPort(8080);
        node2.setStatus(NodeStatus.ONLINE);
        node2.setLastSeen(System.currentTimeMillis());

        NetworkNode node3 = new NetworkNode();
        node3.setId(UUID.randomUUID().toString());
        node3.setName("Smart-Hub");
        node3.setType(NodeType.IOT_DEVICE);
        node3.setIp("192.168.1.102");
        node3.setPort(8080);
        node3.setStatus(NodeStatus.ONLINE);
        node3.setLastSeen(System.currentTimeMillis());

        addDiscoveredNode(node1);
        addDiscoveredNode(node2);
        addDiscoveredNode(node3);
    }

    public void addDiscoveredNode(NetworkNode node) {
        if (node == null || node.getId() == null) {
            return;
        }

        discoveredNodes.put(node.getId(), node);
        notifyEventListeners(P2PEventType.NODE_DISCOVERED, node);
        logger.info("Discovered node: {} at {}", node.getName(), node.getIp());
    }

    public void removeNode(String nodeId) {
        NetworkNode node = discoveredNodes.remove(nodeId);
        if (node != null) {
            notifyEventListeners(P2PEventType.NODE_LOST, node);
            logger.info("Removed node: {}", node.getName());
        }
    }

    private void heartbeatLoop() {
        while (running) {
            try {
                Thread.sleep(30000);
                performHeartbeat();
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                logger.error("Heartbeat error: {}", e.getMessage());
            }
        }
    }

    private void performHeartbeat() {
        long currentTime = System.currentTimeMillis();
        List<String> offlineNodes = new ArrayList<>();

        for (Map.Entry<String, NetworkNode> entry : discoveredNodes.entrySet()) {
            NetworkNode node = entry.getValue();

            if (currentTime - node.getLastSeen() > 60000) {
                node.setStatus(NodeStatus.OFFLINE);
                offlineNodes.add(entry.getKey());
                notifyEventListeners(P2PEventType.NODE_LOST, node);
            }
        }

        for (String nodeId : offlineNodes) {
            discoveredNodes.remove(nodeId);
        }
    }

    public void shareSkill(String skillId) {
        logger.info("Sharing skill: {}", skillId);
        notifyEventListeners(P2PEventType.SKILL_SHARED, skillId);
    }

    public void unshareSkill(String skillId) {
        logger.info("Unsharing skill: {}", skillId);
        notifyEventListeners(P2PEventType.SKILL_UNSHARED, skillId);
    }

    public List<NetworkNode> findNodesBySkill(String skillId) {
        return sharedSkills.getOrDefault(skillId, new ArrayList<>());
    }

    public List<NetworkNode> getAllDiscoveredNodes() {
        return new ArrayList<>(discoveredNodes.values());
    }

    public NetworkNode getLocalNode() {
        return localNode;
    }

    public void setLocalNode(NetworkNode localNode) {
        this.localNode = localNode;
    }

    public int getNodeCount() {
        return discoveredNodes.size();
    }

    public int getOnlineNodeCount() {
        return (int) discoveredNodes.values().stream()
            .filter(NetworkNode::isOnline)
            .count();
    }

    public void addEventListener(P2PEventListener listener) {
        if (listener != null && !eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }

    public void removeEventListener(P2PEventListener listener) {
        eventListeners.remove(listener);
    }

    private void notifyEventListeners(P2PEventType eventType, Object data) {
        for (P2PEventListener listener : eventListeners) {
            try {
                listener.onEvent(eventType, data);
            } catch (Exception e) {
                logger.error("Event listener error: {}", e.getMessage());
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}

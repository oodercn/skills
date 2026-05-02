package net.ooder.bpm.event.test.distributed;

import net.ooder.bpm.event.test.*;
import net.ooder.sdk.a2a.AgentInfo;
import net.ooder.sdk.api.agent.AgentMessage;
import net.ooder.sdk.service.network.p2p.GossipProtocol;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class BpmSkillNode {

    private final String nodeId;
    private final int broadcastPort;
    private final int controlPort;
    private final int tcpEventPort;
    private final AgentInfo agentInfo;

    private GossipProtocol gossipProtocol;
    private DatagramSocket udpSocket;
    private ServerSocket controlServer;
    private ServerSocket tcpEventServer;
    private final ExecutorService executor;
    private volatile boolean running = false;
    private ScheduledExecutorService announcementScheduler;

    private final Map<String, PeerInfo> discoveredPeers = new ConcurrentHashMap<>();
    private final Map<String, Socket> peerTcpConnections = new ConcurrentHashMap<>();
    private final Map<String, ObjectOutputStream> peerOutputStreams = new ConcurrentHashMap<>();

    private final BpmEventRecord record;
    private final AtomicLong eventsSent = new AtomicLong(0);
    private final AtomicLong eventsReceived = new AtomicLong(0);
    private final AtomicLong bytesSent = new AtomicLong(0);
    private final AtomicLong bytesReceived = new AtomicLong(0);

    private static final byte[] OODE_HEADER = "OODE".getBytes(StandardCharsets.UTF_8);
    private static final byte MSG_TYPE_EVENT = 0x10;
    private static final byte MSG_TYPE_ANNOUNCE = 0x01;
    private static final byte MSG_TYPE_ACK = 0x20;
    private static final int MAX_UDP_PAYLOAD = 60000;

    public BpmSkillNode(String nodeId, int broadcastPort, int controlPort, int tcpEventPort) throws IOException {
        this.nodeId = nodeId;
        this.broadcastPort = broadcastPort;
        this.controlPort = controlPort;
        this.tcpEventPort = tcpEventPort;
        this.record = new BpmEventRecord(nodeId);
        this.executor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "bpm-node-" + nodeId);
            t.setDaemon(true);
            return t;
        });
        this.agentInfo = AgentInfo.builder()
            .agentId(nodeId)
            .agentName("BPM-Skill-" + nodeId)
            .agentType("SCENE")
            .version("3.0.3")
            .endpoint("tcp://localhost:" + tcpEventPort)
            .capabilities(Arrays.asList("bpm-event-broadcast", "bpm-event-receive", "bpm-skill-lifecycle"))
            .status(AgentInfo.AgentStatus.ONLINE)
            .build();
    }

    public void start() throws IOException {
        running = true;

        udpSocket = new DatagramSocket(null);
        udpSocket.setReuseAddress(true);
        udpSocket.bind(new InetSocketAddress(broadcastPort));
        udpSocket.setBroadcast(true);

        controlServer = new ServerSocket(controlPort);
        controlServer.setReuseAddress(true);

        tcpEventServer = new ServerSocket(tcpEventPort);
        tcpEventServer.setReuseAddress(true);

        initGossipProtocol();

        executor.submit(this::receiveUdpLoop);
        executor.submit(this::acceptControlLoop);
        executor.submit(this::acceptTcpEventLoop);

        announcementScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "bpm-announce-" + nodeId);
            t.setDaemon(true);
            return t;
        });
        sendAnnouncement();
        announcementScheduler.scheduleAtFixedRate(this::sendAnnouncement, 3, 3, TimeUnit.SECONDS);

        System.out.println("[" + nodeId + "] Node started (ooderAgent SDK): broadcastPort=" + broadcastPort +
            ", controlPort=" + controlPort + ", tcpEventPort=" + tcpEventPort +
            ", agentId=" + agentInfo.getAgentId() + ", endpoint=" + agentInfo.getEndpoint());
    }

    private void initGossipProtocol() {
        try {
            gossipProtocol = new GossipProtocol(nodeId);
            gossipProtocol.setFanout(3);
            gossipProtocol.setMessageTtl(30000);
            gossipProtocol.start();
            System.out.println("[" + nodeId + "] GossipProtocol started with fanout=3");
        } catch (Exception e) {
            System.out.println("[" + nodeId + "] GossipProtocol init skipped: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        if (announcementScheduler != null) announcementScheduler.shutdownNow();
        try { if (gossipProtocol != null) gossipProtocol.stop(); } catch (Exception e) {}
        try { udpSocket.close(); } catch (Exception e) {}
        try { controlServer.close(); } catch (Exception e) {}
        try { tcpEventServer.close(); } catch (Exception e) {}
        for (Socket s : peerTcpConnections.values()) {
            try { s.close(); } catch (Exception e) {}
        }
        executor.shutdownNow();
    }

    private void sendAnnouncement() {
        if (!running) return;
        try {
            String payload = nodeId + ":" + tcpEventPort + ":" + controlPort;
            byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
            byte[] message = encodeUdpMessage(MSG_TYPE_ANNOUNCE, payloadBytes);
            DatagramPacket packet = new DatagramPacket(message, message.length,
                InetAddress.getByName("255.255.255.255"), broadcastPort);
            udpSocket.send(packet);
        } catch (Exception e) {
            if (running) System.err.println("[" + nodeId + "] Announcement error: " + e.getMessage());
        }
    }

    private void receiveUdpLoop() {
        byte[] buffer = new byte[65507];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);
                byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
                processUdpMessage(data, packet.getAddress());
            } catch (SocketException e) {
                if (running) System.err.println("[" + nodeId + "] UDP socket closed");
                break;
            } catch (IOException e) {
                continue;
            } catch (Exception e) {
                if (running) System.err.println("[" + nodeId + "] UDP receive error: " + e.getMessage());
            }
        }
    }

    private void processUdpMessage(byte[] data, InetAddress senderAddr) {
        if (data.length < 7) return;
        if (!verifyHeader(data)) return;

        byte msgType = data[4];
        int payloadLen = ((data[5] & 0xFF) << 8) | (data[6] & 0xFF);
        if (data.length < 7 + payloadLen) return;

        byte[] payload = Arrays.copyOfRange(data, 7, 7 + payloadLen);

        switch (msgType) {
            case MSG_TYPE_ANNOUNCE:
                String announceStr = new String(payload, StandardCharsets.UTF_8);
                String[] parts = announceStr.split(":");
                if (parts.length >= 2 && !parts[0].equals(nodeId)) {
                    String peerId = parts[0];
                    int peerTcpPort = Integer.parseInt(parts[1]);
                    PeerInfo existing = discoveredPeers.get(peerId);
                    if (existing == null) {
                        discoveredPeers.put(peerId, new PeerInfo(peerId, senderAddr.getHostAddress(), peerTcpPort));
                        System.out.println("[" + nodeId + "] Discovered peer: " + peerId + " at " + senderAddr.getHostAddress() + ":" + peerTcpPort);
                        connectToPeerTcp(peerId, senderAddr.getHostAddress(), peerTcpPort);
                        registerGossipPeer(peerId);
                    } else {
                        existing.updateLastSeen();
                    }
                }
                break;
            case MSG_TYPE_EVENT:
                processIncomingEvent(payload);
                break;
        }
    }

    private void registerGossipPeer(String peerId) {
        if (gossipProtocol == null) return;
        try {
            GossipProtocol.GossipPeer gossipPeer = new GossipProtocol.GossipPeer() {
                @Override
                public String getNodeId() {
                    return peerId;
                }

                @Override
                public void send(GossipProtocol.GossipMessage message) {
                    ObjectOutputStream oos = peerOutputStreams.get(peerId);
                    if (oos == null) return;
                    try {
                        byte[] msgPayload = message.getPayload();
                        if (msgPayload != null && msgPayload.length > 0) {
                            synchronized (oos) {
                                oos.writeInt(msgPayload.length);
                                oos.write(msgPayload);
                                oos.flush();
                            }
                        }
                    } catch (Exception ignored) {}
                }
            };
            gossipProtocol.addPeer(gossipPeer);
            System.out.println("[" + nodeId + "] Registered GossipPeer: " + peerId);
        } catch (Exception e) {
            System.err.println("[" + nodeId + "] Register GossipPeer failed: " + e.getMessage());
        }
    }

    private void acceptTcpEventLoop() {
        while (running) {
            try {
                Socket clientSocket = tcpEventServer.accept();
                clientSocket.setTcpNoDelay(true);
                executor.submit(() -> readTcpEvents(clientSocket));
            } catch (Exception e) {
                if (running) System.err.println("[" + nodeId + "] TCP event accept error: " + e.getMessage());
            }
        }
    }

    private void readTcpEvents(Socket socket) {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            while (running) {
                Object obj = ois.readObject();
                if (obj instanceof BpmTestEvent event) {
                    if (nodeId.equals(event.getSource())) continue;
                    eventsReceived.incrementAndGet();
                    Map<String, String> fields = extractFields(event);
                    record.recordReceived(event.getEventId(), event.getEventType(), event.getSource(), event);
                    record.recordConsumed(event.getEventId(), event.getEventType(), fields);
                    System.out.println("[" + nodeId + "] TCP received event: " + event.getEventId() + " type=" + event.getEventType());
                } else if (obj instanceof AgentMessage agentMsg) {
                    if (!nodeId.equals(agentMsg.getFromAgentId())) {
                        processAgentMessage(agentMsg);
                    }
                }
            }
        } catch (Exception e) {
            if (running) System.err.println("[" + nodeId + "] TCP read error: " + e.getMessage());
        }
    }

    private void connectToPeerTcp(String peerId, String host, int tcpPort) {
        if (peerTcpConnections.containsKey(peerId)) return;
        try {
            Socket socket = new Socket(host, tcpPort);
            socket.setTcpNoDelay(true);
            peerTcpConnections.put(peerId, socket);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            peerOutputStreams.put(peerId, oos);
            System.out.println("[" + nodeId + "] TCP connected to peer: " + peerId + " at " + host + ":" + tcpPort);
        } catch (Exception e) {
            System.err.println("[" + nodeId + "] TCP connect to " + peerId + " failed: " + e.getMessage());
        }
    }

    private void processIncomingEvent(byte[] payload) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(payload));
            Object obj = ois.readObject();
            if (obj instanceof BpmTestEvent event) {
                if (nodeId.equals(event.getSource())) return;
                eventsReceived.incrementAndGet();
                bytesReceived.addAndGet(payload.length);
                Map<String, String> fields = extractFields(event);
                record.recordReceived(event.getEventId(), event.getEventType(), event.getSource(), event);
                record.recordConsumed(event.getEventId(), event.getEventType(), fields);
                System.out.println("[" + nodeId + "] UDP received event: " + event.getEventId() + " type=" + event.getEventType());
            }
        } catch (Exception e) {
            System.err.println("[" + nodeId + "] Event deserialization error: " + e.getMessage());
        }
    }

    private void processAgentMessage(AgentMessage agentMsg) {
        eventsReceived.incrementAndGet();
        System.out.println("[" + nodeId + "] AgentMessage received: from=" + agentMsg.getFromAgentId() +
            " type=" + agentMsg.getType() + " subject=" + agentMsg.getSubject());
    }

    private Map<String, String> extractFields(BpmTestEvent event) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("eventType", event.getEventType() != null ? event.getEventType() : "null");
        fields.put("domain", event.getDomain() != null ? event.getDomain().name() : "null");
        fields.put("payload", event.getPayload() != null ? event.getPayload() : "null");
        fields.put("source", event.getSource() != null ? event.getSource() : "null");
        if (event.getSceneEventCode() != null) fields.put("sceneEventCode", event.getSceneEventCode());
        if (event.getMetadata() != null) {
            List<String> keys = new ArrayList<>(event.getMetadata().keySet());
            Collections.sort(keys);
            for (String key : keys) {
                String value = event.getMetadata().get(key);
                fields.put(key, value != null ? value : "null");
            }
        }
        return fields;
    }

    public void broadcastEvent(BpmTestEvent event) {
        record.recordSent(event.getEventId(), event.getEventType(), "BROADCAST", event);
        eventsSent.incrementAndGet();

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(event);
            oos.flush();
            byte[] payload = bos.toByteArray();
            bytesSent.addAndGet(payload.length);

            if (payload.length <= MAX_UDP_PAYLOAD) {
                byte[] message = encodeUdpMessage(MSG_TYPE_EVENT, payload);
                DatagramPacket packet = new DatagramPacket(message, message.length,
                    InetAddress.getByName("255.255.255.255"), broadcastPort);
                udpSocket.send(packet);
            } else {
                for (Map.Entry<String, ObjectOutputStream> entry : peerOutputStreams.entrySet()) {
                    synchronized (entry.getValue()) {
                        entry.getValue().writeObject(event);
                        entry.getValue().flush();
                        entry.getValue().reset();
                    }
                }
            }

            if (gossipProtocol != null) {
                gossipProtocol.broadcast(event.getEventType(), payload);
            }
        } catch (Exception e) {
            System.err.println("[" + nodeId + "] Broadcast error: " + e.getMessage());
        }
    }

    public void sendAgentMessage(String targetAgentId, AgentMessage.MessageType type, String subject, Object payload) {
        AgentMessage msg = AgentMessage.builder()
            .from(nodeId)
            .to(targetAgentId)
            .type(type)
            .subject(subject)
            .payload(payload)
            .build();

        ObjectOutputStream oos = peerOutputStreams.get(targetAgentId);
        if (oos != null) {
            try {
                synchronized (oos) {
                    oos.writeObject(msg);
                    oos.flush();
                    oos.reset();
                }
                eventsSent.incrementAndGet();
                System.out.println("[" + nodeId + "] AgentMessage sent to " + targetAgentId + " subject=" + subject);
            } catch (Exception e) {
                System.err.println("[" + nodeId + "] AgentMessage send error: " + e.getMessage());
            }
        }
    }

    private byte[] encodeUdpMessage(byte msgType, byte[] payload) {
        byte[] message = new byte[7 + payload.length];
        System.arraycopy(OODE_HEADER, 0, message, 0, 4);
        message[4] = msgType;
        message[5] = (byte) ((payload.length >> 8) & 0xFF);
        message[6] = (byte) (payload.length & 0xFF);
        System.arraycopy(payload, 0, message, 7, payload.length);
        return message;
    }

    private boolean verifyHeader(byte[] data) {
        if (data.length < 4) return false;
        return data[0] == 'O' && data[1] == 'O' && data[2] == 'D' && data[3] == 'E';
    }

    private void acceptControlLoop() {
        while (running) {
            try {
                Socket client = controlServer.accept();
                executor.submit(() -> handleControl(client));
            } catch (Exception e) {
                if (running) System.err.println("[" + nodeId + "] Control accept error: " + e.getMessage());
            }
        }
    }

    private void handleControl(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            socket.setSoTimeout(10000);
            String command = reader.readLine();
            if (command == null) return;

            String response;
            switch (command.trim()) {
                case "PING":
                    response = "PONG:" + nodeId;
                    break;
                case "STATUS":
                    response = String.format("nodeId=%s,sent=%d,received=%d,consumed=%d,bytesSent=%d,bytesReceived=%d,peers=%d,agentStatus=%s",
                        nodeId, eventsSent.get(), eventsReceived.get(), record.getTotalConsumed(), bytesSent.get(), bytesReceived.get(), discoveredPeers.size(), agentInfo.getStatus());
                    break;
                case "COUNTS":
                    response = String.format("sent=%d,received=%d,consumed=%d", eventsSent.get(), eventsReceived.get(), record.getTotalConsumed());
                    break;
                case "RECEIVED_IDS":
                    response = String.join(",", record.getReceivedEventIds());
                    break;
                case "CONSUMED_FIELDS": {
                    String eventId = reader.readLine();
                    String fieldName = reader.readLine();
                    String value = record.getConsumedFieldValue(eventId, fieldName);
                    response = value != null ? value : "NOT_FOUND";
                    break;
                }
                case "SEND_EVENT": {
                    String eventType = reader.readLine();
                    String eventPayload = reader.readLine();
                    String metadataStr = reader.readLine();
                    BpmTestEvent event = new BpmTestEvent(nodeId, BpmEventDomain.PROCESS, eventType, eventPayload);
                    if (metadataStr != null && !metadataStr.isEmpty()) {
                        for (String pair : metadataStr.split(",")) {
                            String[] kv = pair.split("=", 2);
                            if (kv.length == 2) event.getMetadata().put(kv[0], kv[1]);
                        }
                    }
                    broadcastEvent(event);
                    response = "OK:" + event.getEventId();
                    break;
                }
                case "BATCH_SEND": {
                    String countStr = reader.readLine();
                    String eventType = reader.readLine();
                    String eventPayload = reader.readLine();
                    int count = Integer.parseInt(countStr);
                    List<String> sentIds = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        BpmTestEvent event = new BpmTestEvent(nodeId, BpmEventDomain.PROCESS, eventType, eventPayload + "-" + i);
                        event.getMetadata().put("batchIndex", String.valueOf(i));
                        event.getMetadata().put("batchSize", countStr);
                        broadcastEvent(event);
                        sentIds.add(event.getEventId());
                    }
                    response = "OK:" + String.join(",", sentIds);
                    break;
                }
                case "CONCURRENT_SEND": {
                    String countStr = reader.readLine();
                    String threadCountStr = reader.readLine();
                    String eventType = reader.readLine();
                    String eventPayload = reader.readLine();
                    int count = Integer.parseInt(countStr);
                    int threadCount = Integer.parseInt(threadCountStr);
                    CountDownLatch startLatch = new CountDownLatch(1);
                    CountDownLatch doneLatch = new CountDownLatch(threadCount);
                    AtomicLong concurrentSent = new AtomicLong(0);
                    int perThread = count / threadCount;
                    for (int t = 0; t < threadCount; t++) {
                        final int threadIdx = t;
                        executor.submit(() -> {
                            try {
                                startLatch.await();
                                for (int i = 0; i < perThread; i++) {
                                    BpmTestEvent event = new BpmTestEvent(nodeId, BpmEventDomain.PROCESS, eventType,
                                        eventPayload + "-t" + threadIdx + "-i" + i);
                                    event.getMetadata().put("threadIndex", String.valueOf(threadIdx));
                                    event.getMetadata().put("concurrentIndex", String.valueOf(i));
                                    broadcastEvent(event);
                                    concurrentSent.incrementAndGet();
                                }
                            } catch (Exception e) {
                                System.err.println("[" + nodeId + "] Concurrent send thread " + threadIdx + " error: " + e.getMessage());
                            } finally {
                                doneLatch.countDown();
                            }
                        });
                    }
                    startLatch.countDown();
                    doneLatch.await(30, TimeUnit.SECONDS);
                    response = "OK:concurrentSent=" + concurrentSent.get();
                    break;
                }
                case "AGENT_INFO":
                    response = String.format("agentId=%s,agentName=%s,agentType=%s,endpoint=%s,status=%s,capabilities=%s",
                        agentInfo.getAgentId(), agentInfo.getAgentName(), agentInfo.getAgentType(),
                        agentInfo.getEndpoint(), agentInfo.getStatus(), String.join("|", agentInfo.getCapabilities()));
                    break;
                case "PEERS":
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, PeerInfo> entry : discoveredPeers.entrySet()) {
                        if (sb.length() > 0) sb.append("|");
                        sb.append(entry.getKey()).append("@").append(entry.getValue().host).append(":").append(entry.getValue().port);
                    }
                    response = sb.length() > 0 ? sb.toString() : "NONE";
                    break;
                case "SHUTDOWN":
                    response = "OK";
                    writer.println(response);
                    running = false;
                    return;
                default:
                    response = "UNKNOWN_COMMAND";
            }
            writer.println(response);
        } catch (Exception e) {
            System.err.println("[" + nodeId + "] Control error: " + e.getMessage());
        }
    }

    public BpmEventRecord getRecord() { return record; }
    public String getNodeId() { return nodeId; }
    public int getBroadcastPort() { return broadcastPort; }
    public int getControlPort() { return controlPort; }
    public int getTcpEventPort() { return tcpEventPort; }
    public int getPeerCount() { return discoveredPeers.size(); }
    public AgentInfo getAgentInfo() { return agentInfo; }

    public static class PeerInfo {
        public final String peerId;
        public final String host;
        public final int port;
        private volatile long lastSeen;

        public PeerInfo(String peerId, String host, int port) {
            this.peerId = peerId;
            this.host = host;
            this.port = port;
            this.lastSeen = System.currentTimeMillis();
        }

        public void updateLastSeen() {
            this.lastSeen = System.currentTimeMillis();
        }

        public long getLastSeen() {
            return lastSeen;
        }
    }
}

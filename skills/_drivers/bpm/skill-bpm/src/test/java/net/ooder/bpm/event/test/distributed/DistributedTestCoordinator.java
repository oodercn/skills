package net.ooder.bpm.event.test.distributed;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedTestCoordinator {

    private final Map<String, Process> processes = new ConcurrentHashMap<>();
    private final Map<String, Integer> controlPorts = new ConcurrentHashMap<>();
    private final String classpath;
    private final String javaHome;

    public DistributedTestCoordinator() {
        this.javaHome = System.getProperty("java.home");
        String cp = System.getProperty("java.class.path");
        this.classpath = cp != null ? cp : ".";
    }

    public void startNode(String nodeId, int broadcastPort, int controlPort, int tcpEventPort) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(javaHome + File.separator + "bin" + File.separator + "java");
        command.add("-cp");
        command.add(classpath);
        command.add("-Dfile.encoding=UTF-8");
        command.add("net.ooder.bpm.event.test.distributed.BpmSkillNodeMain");
        command.add(nodeId);
        command.add(String.valueOf(broadcastPort));
        command.add(String.valueOf(controlPort));
        command.add(String.valueOf(tcpEventPort));

        System.out.println("[Coordinator] Starting node: " + nodeId + " broadcast=" + broadcastPort +
            " control=" + controlPort + " tcpEvent=" + tcpEventPort);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        Thread outputThread = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[" + nodeId + "] " + line);
                }
            } catch (Exception e) {}
        }, "output-" + nodeId);
        outputThread.setDaemon(true);
        outputThread.start();

        processes.put(nodeId, process);
        controlPorts.put(nodeId, controlPort);

        Thread.sleep(3000);
        assertTrue(isNodeAlive(nodeId), "Node " + nodeId + " should be alive after startup");
    }

    public void stopAllNodes() {
        for (Map.Entry<String, Process> entry : processes.entrySet()) {
            try {
                queryControl(entry.getKey(), "SHUTDOWN");
            } catch (Exception e) {}
            entry.getValue().destroyForcibly();
        }
        processes.clear();
    }

    public boolean isNodeAlive(String nodeId) {
        Process p = processes.get(nodeId);
        return p != null && p.isAlive();
    }

    public String queryControl(String nodeId, String command) throws Exception {
        return queryControl(nodeId, command, null);
    }

    public String queryControl(String nodeId, String command, String extraLine) throws Exception {
        int port = controlPorts.get(nodeId);
        try (Socket socket = new Socket("localhost", port);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            socket.setSoTimeout(10000);
            writer.println(command);
            if (extraLine != null) writer.println(extraLine);
            return reader.readLine();
        }
    }

    public Map<String, String> queryStatus(String nodeId) throws Exception {
        String response = queryControl(nodeId, "STATUS");
        return parseKvResponse(response);
    }

    public Map<String, String> queryCounts(String nodeId) throws Exception {
        String response = queryControl(nodeId, "COUNTS");
        return parseKvResponse(response);
    }

    public String queryAgentInfo(String nodeId) throws Exception {
        return queryControl(nodeId, "AGENT_INFO");
    }

    public String queryPeers(String nodeId) throws Exception {
        return queryControl(nodeId, "PEERS");
    }

    public String sendEvent(String nodeId, String eventType, String payload, String metadata) throws Exception {
        String response = queryControl(nodeId, "SEND_EVENT", eventType + "\n" + payload + "\n" + (metadata != null ? metadata : ""));
        return response;
    }

    public String batchSend(String nodeId, int count, String eventType, String payload) throws Exception {
        return queryControl(nodeId, "BATCH_SEND", count + "\n" + eventType + "\n" + payload);
    }

    public String concurrentSend(String nodeId, int count, int threadCount, String eventType, String payload) throws Exception {
        return queryControl(nodeId, "CONCURRENT_SEND", count + "\n" + threadCount + "\n" + eventType + "\n" + payload);
    }

    public String queryConsumedField(String nodeId, String eventId, String fieldName) throws Exception {
        String response = queryControl(nodeId, "CONSUMED_FIELDS", eventId + "\n" + fieldName);
        return response;
    }

    public boolean waitForPeers(String nodeId, int expectedPeers, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            Map<String, String> status = queryStatus(nodeId);
            int peers = Integer.parseInt(status.getOrDefault("peers", "0"));
            if (peers >= expectedPeers) return true;
            Thread.sleep(500);
        }
        return false;
    }

    public boolean waitForReceived(String nodeId, long expectedCount, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            Map<String, String> counts = queryCounts(nodeId);
            long received = Long.parseLong(counts.getOrDefault("received", "0"));
            if (received >= expectedCount) return true;
            Thread.sleep(500);
        }
        return false;
    }

    private Map<String, String> parseKvResponse(String response) {
        Map<String, String> result = new LinkedHashMap<>();
        if (response == null || response.isEmpty()) return result;
        for (String pair : response.split(",")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) result.put(kv[0].trim(), kv[1].trim());
        }
        return result;
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}

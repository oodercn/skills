package net.ooder.skill.vfs.minio;

import com.alibaba.fastjson.JSONObject;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinioCacheSyncService {

    private static final Log log = LogFactory.getLog("vfs", MinioCacheSyncService.class);

    private static final int DEFAULT_PORT = 9880;
    private static final String MULTICAST_GROUP = "239.255.255.254";

    private static MinioCacheSyncService instance;

    private final int port;
    private final String multicastGroup;
    private DatagramSocket socket;
    private ExecutorService executorService;
    private volatile boolean running = false;
    private MinioFileObjectManager fileObjectManager;

    private MinioCacheSyncService() {
        this.port = getPortFromConfig();
        this.multicastGroup = getMulticastGroupFromConfig();
    }

    public static synchronized MinioCacheSyncService getInstance() {
        if (instance == null) {
            instance = new MinioCacheSyncService();
        }
        return instance;
    }

    public void setFileObjectManager(MinioFileObjectManager manager) {
        this.fileObjectManager = manager;
    }

    public void start() {
        if (running) {
            return;
        }
        try {
            socket = new DatagramSocket(port);
            executorService = Executors.newSingleThreadExecutor();
            running = true;
            executorService.submit(this::receiveLoop);
            log.info("MinioCacheSyncService started on port " + port);
        } catch (SocketException e) {
            log.error("Failed to start MinioCacheSyncService", e);
        }
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
        log.info("MinioCacheSyncService stopped");
    }

    public void broadcastCacheInvalidation(String action, String fileId) {
        if (socket == null || socket.isClosed()) {
            return;
        }
        try {
            JSONObject message = new JSONObject();
            message.put("type", "vfs-minio-cache-sync");
            message.put("action", action);
            message.put("fileId", fileId);
            message.put("timestamp", System.currentTimeMillis());
            message.put("source", getLocalIpAddress());

            byte[] data = message.toJSONString().getBytes("UTF-8");
            InetAddress group = InetAddress.getByName(multicastGroup);
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            socket.send(packet);
            log.debug("Broadcast cache invalidation: " + action + " - " + fileId);
        } catch (Exception e) {
            log.error("Failed to broadcast cache invalidation", e);
        }
    }

    private void receiveLoop() {
        byte[] buffer = new byte[4096];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                processMessage(message);
            } catch (SocketException e) {
                if (running) {
                    log.error("Socket error in receive loop", e);
                }
            } catch (IOException e) {
                log.error("IO error in receive loop", e);
            }
        }
    }

    private void processMessage(String message) {
        try {
            JSONObject json = JSONObject.parseObject(message);
            String type = json.getString("type");
            if (!"vfs-minio-cache-sync".equals(type)) {
                return;
            }
            String source = json.getString("source");
            String localIp = getLocalIpAddress();
            if (source != null && source.equals(localIp)) {
                return;
            }
            String action = json.getString("action");
            String fileId = json.getString("fileId");
            log.info("Received cache sync message: " + action + " - " + fileId + " from " + source);

            if (fileObjectManager != null) {
                fileObjectManager.removeFromCache(fileId);
            }
        } catch (Exception e) {
            log.error("Failed to process cache sync message", e);
        }
    }

    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            log.error("Failed to get local IP address", e);
        }
        return "127.0.0.1";
    }

    private int getPortFromConfig() {
        String portStr = System.getProperty("vfs.minio.sync.port", System.getenv("VFS_MINIO_SYNC_PORT"));
        if (portStr != null) {
            try {
                return Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid VFS MinIO sync port: " + portStr + ", using default");
            }
        }
        return DEFAULT_PORT;
    }

    private String getMulticastGroupFromConfig() {
        String group = System.getProperty("vfs.minio.sync.multicast", System.getenv("VFS_MINIO_SYNC_MULTICAST"));
        return group != null ? group : MULTICAST_GROUP;
    }
}

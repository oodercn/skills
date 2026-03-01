package net.ooder.nexus.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NetUtils {
    
    private static final Logger log = LoggerFactory.getLogger(NetUtils.class);
    
    public static void downloadFile(String url, Path targetFile) throws IOException {
        log.info("Downloading from: {}", url);
        
        URL downloadUrl = new URL(url);
        try (InputStream is = downloadUrl.openStream()) {
            Files.copy(is, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
        
        log.info("Downloaded to: {}", targetFile);
    }
    
    public static String fetchString(String url) throws IOException {
        URL apiUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);
        
        try (InputStream is = conn.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }
    
    public static String getLocalIpAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
    
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
    
    public static boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static int findAvailablePort(int startPort) {
        int port = startPort;
        while (port < 65535) {
            if (isPortAvailable(port)) {
                return port;
            }
            port++;
        }
        return -1;
    }
    
    private NetUtils() {}
}

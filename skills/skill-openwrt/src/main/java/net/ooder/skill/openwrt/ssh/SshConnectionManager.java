package net.ooder.skill.openwrt.ssh;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SshConnectionManager {
    
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConnectionConfig> configs = new ConcurrentHashMap<>();
    
    private static final int DEFAULT_PORT = 22;
    private static final int DEFAULT_TIMEOUT = 30000;
    private static final String DEFAULT_CONNECTION_ID = "default";
    
    public boolean connect(String host, int port, String username, String password) throws JSchException {
        return connect(DEFAULT_CONNECTION_ID, host, port, username, password, null);
    }
    
    public boolean connect(String host, int port, String username, String password, String privateKeyPath) throws JSchException {
        return connect(DEFAULT_CONNECTION_ID, host, port, username, password, privateKeyPath);
    }
    
    public boolean connect(String connectionId, String host, int port, String username, String password, String privateKeyPath) throws JSchException {
        if (sessions.containsKey(connectionId)) {
            Session existingSession = sessions.get(connectionId);
            if (existingSession.isConnected()) {
                return true;
            }
            disconnect(connectionId);
        }
        
        JSch jsch = new JSch();
        
        if (privateKeyPath != null && !privateKeyPath.isEmpty()) {
            jsch.addIdentity(privateKeyPath);
        }
        
        Session session = jsch.getSession(username, host, port > 0 ? port : DEFAULT_PORT);
        session.setPassword(password);
        
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("UserKnownHostsFile", "/dev/null");
        session.setConfig(config);
        session.setTimeout(DEFAULT_TIMEOUT);
        
        session.connect();
        
        sessions.put(connectionId, session);
        configs.put(connectionId, new ConnectionConfig(host, port, username));
        
        log.info("SSH connected to {}@{}:{}", username, host, port);
        return true;
    }
    
    public boolean disconnect() {
        return disconnect(DEFAULT_CONNECTION_ID);
    }
    
    public boolean disconnect(String connectionId) {
        Session session = sessions.remove(connectionId);
        configs.remove(connectionId);
        
        if (session != null) {
            session.disconnect();
            log.info("SSH disconnected: {}", connectionId);
            return true;
        }
        return false;
    }
    
    public boolean isConnected() {
        return isConnected(DEFAULT_CONNECTION_ID);
    }
    
    public boolean isConnected(String connectionId) {
        Session session = sessions.get(connectionId);
        return session != null && session.isConnected();
    }
    
    public SshExecuteResult executeCommand(String command) throws JSchException, IOException {
        return executeCommand(DEFAULT_CONNECTION_ID, command);
    }
    
    public SshExecuteResult executeCommand(String connectionId, String command) throws JSchException, IOException {
        Session session = sessions.get(connectionId);
        if (session == null || !session.isConnected()) {
            throw new JSchException("SSH session not connected");
        }
        
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            
            InputStream in = channel.getInputStream();
            InputStream err = channel.getErrStream();
            
            channel.connect();
            
            String stdout = readStream(in);
            String stderr = readStream(err);
            
            while (!channel.isClosed()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            int exitCode = channel.getExitStatus();
            
            return new SshExecuteResult(exitCode, stdout, stderr);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
    
    public SshExecuteResult executeScript(String script) throws JSchException, IOException {
        return executeScript(DEFAULT_CONNECTION_ID, script);
    }
    
    public SshExecuteResult executeScript(String connectionId, String script) throws JSchException, IOException {
        Session session = sessions.get(connectionId);
        if (session == null || !session.isConnected()) {
            throw new JSchException("SSH session not connected");
        }
        
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("sh -s");
            
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();
            InputStream err = channel.getErrStream();
            
            channel.connect();
            
            out.write(script.getBytes());
            out.flush();
            out.close();
            
            String stdout = readStream(in);
            String stderr = readStream(err);
            
            while (!channel.isClosed()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            int exitCode = channel.getExitStatus();
            
            return new SshExecuteResult(exitCode, stdout, stderr);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
    
    public ConnectionConfig getConnectionConfig() {
        return getConnectionConfig(DEFAULT_CONNECTION_ID);
    }
    
    public ConnectionConfig getConnectionConfig(String connectionId) {
        return configs.get(connectionId);
    }
    
    private String readStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString().trim();
    }
    
    public static class ConnectionConfig {
        private final String host;
        private final int port;
        private final String username;
        
        public ConnectionConfig(String host, int port, String username) {
            this.host = host;
            this.port = port;
            this.username = username;
        }
        
        public String getHost() { return host; }
        public int getPort() { return port; }
        public String getUsername() { return username; }
    }
    
    public static class SshExecuteResult {
        private final int exitCode;
        private final String stdout;
        private final String stderr;
        
        public SshExecuteResult(int exitCode, String stdout, String stderr) {
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
        }
        
        public int getExitCode() { return exitCode; }
        public String getStdout() { return stdout; }
        public String getStderr() { return stderr; }
        public boolean isSuccess() { return exitCode == 0; }
    }
}

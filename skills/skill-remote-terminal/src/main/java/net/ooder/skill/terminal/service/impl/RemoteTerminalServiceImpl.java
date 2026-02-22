package net.ooder.skill.terminal.service.impl;

import net.ooder.skill.terminal.dto.*;
import net.ooder.skill.terminal.service.RemoteTerminalService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RemoteTerminalServiceImpl implements RemoteTerminalService {

    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    private static final int MAX_SESSIONS = 100;

    @Override
    public SessionInfo connect(ConnectionRequest request) {
        if (sessions.size() >= MAX_SESSIONS) {
            throw new RuntimeException("Maximum sessions reached");
        }
        
        SessionInfo session = new SessionInfo();
        session.setSessionId("session-" + UUID.randomUUID().toString().substring(0, 8));
        session.setHost(request.getHost());
        session.setPort(request.getPort());
        session.setUsername(request.getUsername());
        session.setConnectionType(request.getConnectionType());
        session.setStatus("connected");
        session.setConnectedAt(System.currentTimeMillis());
        session.setLastActivity(System.currentTimeMillis());
        
        sessions.put(session.getSessionId(), session);
        return session;
    }

    @Override
    public boolean disconnect(String sessionId) {
        SessionInfo session = sessions.remove(sessionId);
        if (session != null) {
            session.setStatus("disconnected");
            return true;
        }
        return false;
    }

    @Override
    public SessionInfo getSession(String sessionId) {
        SessionInfo session = sessions.get(sessionId);
        if (session != null) {
            session.setLastActivity(System.currentTimeMillis());
        }
        return session;
    }

    @Override
    public List<SessionInfo> listSessions() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public CommandResult executeCommand(String sessionId, String command, String workingDirectory) {
        SessionInfo session = sessions.get(sessionId);
        if (session == null) {
            throw new RuntimeException("Session not found: " + sessionId);
        }
        
        if (!"connected".equals(session.getStatus())) {
            throw new RuntimeException("Session is not connected");
        }
        
        CommandResult result = new CommandResult();
        result.setSessionId(sessionId);
        result.setCommand(command);
        result.setStartTime(System.currentTimeMillis());
        
        result.setExitCode(0);
        result.setOutput("Command executed: " + command + "\nOutput: OK");
        
        result.setEndTime(System.currentTimeMillis());
        result.setDuration(result.getEndTime() - result.getStartTime());
        
        session.setLastActivity(System.currentTimeMillis());
        session.setBytesSent(session.getBytesSent() + command.length());
        session.setBytesReceived(session.getBytesReceived() + result.getOutput().length());
        
        return result;
    }

    @Override
    public FileTransferResult uploadFile(String sessionId, String localPath, String remotePath) {
        SessionInfo session = sessions.get(sessionId);
        if (session == null) {
            throw new RuntimeException("Session not found: " + sessionId);
        }
        
        FileTransferResult result = new FileTransferResult();
        result.setSessionId(sessionId);
        result.setDirection("upload");
        result.setLocalPath(localPath);
        result.setRemotePath(remotePath);
        result.setBytesTransferred(1024);
        result.setStatus("completed");
        result.setEndTime(System.currentTimeMillis());
        
        session.setLastActivity(System.currentTimeMillis());
        session.setBytesSent(session.getBytesSent() + result.getBytesTransferred());
        
        return result;
    }

    @Override
    public FileTransferResult downloadFile(String sessionId, String remotePath, String localPath) {
        SessionInfo session = sessions.get(sessionId);
        if (session == null) {
            throw new RuntimeException("Session not found: " + sessionId);
        }
        
        FileTransferResult result = new FileTransferResult();
        result.setSessionId(sessionId);
        result.setDirection("download");
        result.setLocalPath(localPath);
        result.setRemotePath(remotePath);
        result.setBytesTransferred(1024);
        result.setStatus("completed");
        result.setEndTime(System.currentTimeMillis());
        
        session.setLastActivity(System.currentTimeMillis());
        session.setBytesReceived(session.getBytesReceived() + result.getBytesTransferred());
        
        return result;
    }

    @Override
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalSessions", sessions.size());
        status.put("activeSessions", sessions.values().stream()
                .filter(s -> "connected".equals(s.getStatus()))
                .count());
        status.put("maxSessions", MAX_SESSIONS);
        status.put("status", "running");
        return status;
    }
}

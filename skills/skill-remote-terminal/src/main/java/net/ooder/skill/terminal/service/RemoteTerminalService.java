package net.ooder.skill.terminal.service;

import net.ooder.skill.terminal.dto.*;

import java.util.List;
import java.util.Map;

public interface RemoteTerminalService {
    SessionInfo connect(ConnectionRequest request);
    boolean disconnect(String sessionId);
    SessionInfo getSession(String sessionId);
    List<SessionInfo> listSessions();
    CommandResult executeCommand(String sessionId, String command, String workingDirectory);
    FileTransferResult uploadFile(String sessionId, String localPath, String remotePath);
    FileTransferResult downloadFile(String sessionId, String remotePath, String localPath);
    Map<String, Object> getStatus();
}

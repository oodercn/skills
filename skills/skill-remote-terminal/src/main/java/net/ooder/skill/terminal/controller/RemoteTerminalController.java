package net.ooder.skill.terminal.controller;

import net.ooder.skill.terminal.dto.*;
import net.ooder.skill.terminal.service.RemoteTerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/terminal")
public class RemoteTerminalController {

    @Autowired
    private RemoteTerminalService terminalService;

    @PostMapping("/connect")
    public ResponseEntity<SessionInfo> connect(@RequestBody ConnectionRequest request) {
        return ResponseEntity.ok(terminalService.connect(request));
    }

    @PostMapping("/disconnect")
    public ResponseEntity<Boolean> disconnect(@RequestBody Map<String, String> params) {
        String sessionId = params.get("sessionId");
        return ResponseEntity.ok(terminalService.disconnect(sessionId));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionInfo>> listSessions() {
        return ResponseEntity.ok(terminalService.listSessions());
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionInfo> getSession(@PathVariable String sessionId) {
        SessionInfo session = terminalService.getSession(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Boolean> closeSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(terminalService.disconnect(sessionId));
    }

    @PostMapping("/execute")
    public ResponseEntity<CommandResult> executeCommand(@RequestBody CommandRequest request) {
        return ResponseEntity.ok(terminalService.executeCommand(
                request.getSessionId(),
                request.getCommand(),
                request.getWorkingDirectory()));
    }

    @PostMapping("/upload")
    public ResponseEntity<FileTransferResult> uploadFile(@RequestBody FileTransferRequest request) {
        return ResponseEntity.ok(terminalService.uploadFile(
                request.getSessionId(),
                request.getLocalPath(),
                request.getRemotePath()));
    }

    @PostMapping("/download")
    public ResponseEntity<FileTransferResult> downloadFile(@RequestBody FileTransferRequest request) {
        return ResponseEntity.ok(terminalService.downloadFile(
                request.getSessionId(),
                request.getRemotePath(),
                request.getLocalPath()));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(terminalService.getStatus());
    }
}

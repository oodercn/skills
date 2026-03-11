package net.ooder.skill.terminal.controller;

import net.ooder.skill.terminal.dto.*;
import net.ooder.skill.terminal.service.RemoteTerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 杩滅▼缁堢REST API鎺у埗鍣?
 * 
 * <p>鎻愪緵SSH杩滅▼杩炴帴銆佸懡浠ゆ墽琛屽拰鏂囦欢浼犺緭鐨凥TTP鎺ュ彛銆?/p>
 * 
 * <h3>API绔偣鍒楄〃锛?/h3>
 * <table border="1">
 *   <tr><th>鏂规硶</th><th>璺緞</th><th>鎻忚堪</th></tr>
 *   <tr><td>POST</td><td>/api/terminal/connect</td><td>寤虹珛SSH杩炴帴</td></tr>
 *   <tr><td>POST</td><td>/api/terminal/disconnect</td><td>鏂紑杩炴帴</td></tr>
 *   <tr><td>GET</td><td>/api/terminal/sessions</td><td>鍒楀嚭鎵€鏈変細璇?/td></tr>
 *   <tr><td>GET</td><td>/api/terminal/sessions/{id}</td><td>鑾峰彇浼氳瘽璇︽儏</td></tr>
 *   <tr><td>DELETE</td><td>/api/terminal/sessions/{id}</td><td>鍏抽棴浼氳瘽</td></tr>
 *   <tr><td>POST</td><td>/api/terminal/execute</td><td>鎵ц鍛戒护</td></tr>
 *   <tr><td>POST</td><td>/api/terminal/upload</td><td>涓婁紶鏂囦欢</td></tr>
 *   <tr><td>POST</td><td>/api/terminal/download</td><td>涓嬭浇鏂囦欢</td></tr>
 *   <tr><td>GET</td><td>/api/terminal/status</td><td>鑾峰彇鏈嶅姟鐘舵€?/td></tr>
 * </table>
 * 
 * <h3>浣跨敤绀轰緥锛?/h3>
 * <pre>{@code
 * // 1. 寤虹珛杩炴帴
 * POST /api/terminal/connect
 * Content-Type: application/json
 * {
 *   "host": "192.168.1.100",
 *   "port": 22,
 *   "username": "admin",
 *   "password": "password",
 *   "connectionType": "ssh"
 * }
 * 
 * // 鍝嶅簲
 * {
 *   "sessionId": "session-a1b2c3d4",
 *   "host": "192.168.1.100",
 *   "port": 22,
 *   "username": "admin",
 *   "status": "connected",
 *   "connectedAt": 1700000000000
 * }
 * 
 * // 2. 鎵ц鍛戒护
 * POST /api/terminal/execute
 * Content-Type: application/json
 * {
 *   "sessionId": "session-a1b2c3d4",
 *   "command": "ls -la /home",
 *   "timeout": 60000
 * }
 * 
 * // 3. 鏂紑杩炴帴
 * POST /api/terminal/disconnect
 * Content-Type: application/json
 * {
 *   "sessionId": "session-a1b2c3d4"
 * }
 * }</pre>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
@RestController
@RequestMapping("/api/terminal")
public class RemoteTerminalController {

    @Autowired
    private RemoteTerminalService terminalService;

    /**
     * 寤虹珛SSH杩炴帴
     * 
     * <p>涓庤繙绋嬫湇鍔″櫒寤虹珛SSH杩炴帴锛屾敮鎸佸瘑鐮佸拰瀵嗛挜涓ょ璁よ瘉鏂瑰紡銆?/p>
     * 
     * <h4>璇锋眰绀轰緥锛堝瘑鐮佽璇侊級锛?/h4>
     * <pre>{@code
     * {
     *   "host": "192.168.1.100",
     *   "port": 22,
     *   "username": "admin",
     *   "password": "your-password",
     *   "connectionType": "ssh",
     *   "timeout": 30000
     * }
     * }</pre>
     * 
     * <h4>璇锋眰绀轰緥锛堝瘑閽ヨ璇侊級锛?/h4>
     * <pre>{@code
     * {
     *   "host": "192.168.1.100",
     *   "port": 22,
     *   "username": "admin",
     *   "privateKey": "-----BEGIN RSA PRIVATE KEY-----\n...",
     *   "passphrase": "key-passphrase",
     *   "connectionType": "ssh"
     * }
     * }</pre>
     * 
     * @param request 杩炴帴璇锋眰
     * @return 浼氳瘽淇℃伅
     */
    @PostMapping("/connect")
    public ResponseEntity<SessionInfo> connect(@RequestBody ConnectionRequest request) {
        return ResponseEntity.ok(terminalService.connect(request));
    }

    /**
     * 鏂紑SSH杩炴帴
     * 
     * <h4>璇锋眰绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "sessionId": "session-a1b2c3d4"
     * }
     * }</pre>
     * 
     * @param params 鍖呭惈sessionId鐨凪ap
     * @return 鏄惁鏂紑鎴愬姛
     */
    @PostMapping("/disconnect")
    public ResponseEntity<Boolean> disconnect(@RequestBody Map<String, String> params) {
        String sessionId = params.get("sessionId");
        return ResponseEntity.ok(terminalService.disconnect(sessionId));
    }

    /**
     * 鍒楀嚭鎵€鏈変細璇?
     * 
     * <p>杩斿洖褰撳墠鎵€鏈塖SH浼氳瘽鐨勫垪琛紝鍖呮嫭娲昏穬鍜岄潪娲昏穬浼氳瘽銆?/p>
     * 
     * @return 浼氳瘽鍒楄〃
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionInfo>> listSessions() {
        return ResponseEntity.ok(terminalService.listSessions());
    }

    /**
     * 鑾峰彇浼氳瘽璇︽儏
     * 
     * @param sessionId 浼氳瘽ID
     * @return 浼氳瘽淇℃伅锛屼笉瀛樺湪杩斿洖404
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionInfo> getSession(@PathVariable String sessionId) {
        SessionInfo session = terminalService.getSession(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }

    /**
     * 鍏抽棴浼氳瘽
     * 
     * <p>鍏抽棴鎸囧畾浼氳瘽骞堕噴鏀捐祫婧愶紝绛夊悓浜巇isconnect鎿嶄綔銆?/p>
     * 
     * @param sessionId 浼氳瘽ID
     * @return 鏄惁鍏抽棴鎴愬姛
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Boolean> closeSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(terminalService.disconnect(sessionId));
    }

    /**
     * 鎵ц杩滅▼鍛戒护
     * 
     * <p>鍦ㄦ寚瀹氫細璇濈殑杩滅▼鏈嶅姟鍣ㄤ笂鎵цShell鍛戒护銆?/p>
     * 
     * <h4>璇锋眰绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "sessionId": "session-a1b2c3d4",
     *   "command": "docker ps -a",
     *   "workingDirectory": "/home/admin",
     *   "timeout": 60000
     * }
     * }</pre>
     * 
     * <h4>鍝嶅簲绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "commandId": "cmd-1700000000000",
     *   "sessionId": "session-a1b2c3d4",
     *   "command": "docker ps -a",
     *   "exitCode": 0,
     *   "output": "CONTAINER ID   IMAGE     ...",
     *   "error": "",
     *   "startTime": 1700000000000,
     *   "endTime": 1700000001000,
     *   "duration": 1000
     * }
     * }</pre>
     * 
     * @param request 鍛戒护鎵ц璇锋眰
     * @return 鍛戒护鎵ц缁撴灉
     */
    @PostMapping("/execute")
    public ResponseEntity<CommandResult> executeCommand(@RequestBody CommandRequest request) {
        return ResponseEntity.ok(terminalService.executeCommand(
                request.getSessionId(),
                request.getCommand(),
                request.getWorkingDirectory()));
    }

    /**
     * 涓婁紶鏂囦欢
     * 
     * <p>閫氳繃SFTP鍗忚灏嗘湰鍦版枃浠朵笂浼犲埌杩滅▼鏈嶅姟鍣ㄣ€?/p>
     * 
     * <h4>璇锋眰绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "sessionId": "session-a1b2c3d4",
     *   "localPath": "/local/path/file.txt",
     *   "remotePath": "/remote/path/file.txt",
     *   "overwrite": true,
     *   "mode": 644
     * }
     * }</pre>
     * 
     * @param request 鏂囦欢浼犺緭璇锋眰
     * @return 浼犺緭缁撴灉
     */
    @PostMapping("/upload")
    public ResponseEntity<FileTransferResult> uploadFile(@RequestBody FileTransferRequest request) {
        return ResponseEntity.ok(terminalService.uploadFile(
                request.getSessionId(),
                request.getLocalPath(),
                request.getRemotePath()));
    }

    /**
     * 涓嬭浇鏂囦欢
     * 
     * <p>閫氳繃SFTP鍗忚浠庤繙绋嬫湇鍔″櫒涓嬭浇鏂囦欢鍒版湰鍦般€?/p>
     * 
     * <h4>璇锋眰绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "sessionId": "session-a1b2c3d4",
     *   "remotePath": "/remote/path/file.txt",
     *   "localPath": "/local/path/file.txt",
     *   "overwrite": true
     * }
     * }</pre>
     * 
     * @param request 鏂囦欢浼犺緭璇锋眰
     * @return 浼犺緭缁撴灉
     */
    @PostMapping("/download")
    public ResponseEntity<FileTransferResult> downloadFile(@RequestBody FileTransferRequest request) {
        return ResponseEntity.ok(terminalService.downloadFile(
                request.getSessionId(),
                request.getRemotePath(),
                request.getLocalPath()));
    }

    /**
     * 鑾峰彇鏈嶅姟鐘舵€?
     * 
     * <p>杩斿洖杩滅▼缁堢鏈嶅姟鐨勮繍琛岀姸鎬佺粺璁°€?/p>
     * 
     * <h4>鍝嶅簲绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "totalSessions": 5,
     *   "activeSessions": 3,
     *   "maxSessions": 100,
     *   "status": "running"
     * }
     * }</pre>
     * 
     * @return 鐘舵€佷俊鎭?
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(terminalService.getStatus());
    }
}

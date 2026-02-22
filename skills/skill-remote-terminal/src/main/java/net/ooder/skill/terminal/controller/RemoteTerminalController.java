package net.ooder.skill.terminal.controller;

import net.ooder.skill.terminal.dto.*;
import net.ooder.skill.terminal.service.RemoteTerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 远程终端REST API控制器
 * 
 * <p>提供SSH远程连接、命令执行和文件传输的HTTP接口。</p>
 * 
 * <h3>API端点列表：</h3>
 * <table border="1">
 *   <tr><th>方法</th><th>路径</th><th>描述</th></tr>
 *   <tr><td>POST</td><td>/api/terminal/connect</td><td>建立SSH连接</td></tr>
 *   <tr><td>POST</td><td>/api/terminal/disconnect</td><td>断开连接</td></tr>
 *   <tr><td>GET</td><td>/api/terminal/sessions</td><td>列出所有会话</td></tr>
 *   <tr><td>GET</td><td>/api/terminal/sessions/{id}</td><td>获取会话详情</td></tr>
 *   <tr><td>DELETE</td><td>/api/terminal/sessions/{id}</td><td>关闭会话</td></tr>
 *   <tr><td>POST</td><td>/api/terminal/execute</td><td>执行命令</td></tr>
 *   <tr><td>POST</td><td>/api/terminal/upload</td><td>上传文件</td></tr>
 *   <tr><td>POST</td><td>/api/terminal/download</td><td>下载文件</td></tr>
 *   <tr><td>GET</td><td>/api/terminal/status</td><td>获取服务状态</td></tr>
 * </table>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 建立连接
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
 * // 响应
 * {
 *   "sessionId": "session-a1b2c3d4",
 *   "host": "192.168.1.100",
 *   "port": 22,
 *   "username": "admin",
 *   "status": "connected",
 *   "connectedAt": 1700000000000
 * }
 * 
 * // 2. 执行命令
 * POST /api/terminal/execute
 * Content-Type: application/json
 * {
 *   "sessionId": "session-a1b2c3d4",
 *   "command": "ls -la /home",
 *   "timeout": 60000
 * }
 * 
 * // 3. 断开连接
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
     * 建立SSH连接
     * 
     * <p>与远程服务器建立SSH连接，支持密码和密钥两种认证方式。</p>
     * 
     * <h4>请求示例（密码认证）：</h4>
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
     * <h4>请求示例（密钥认证）：</h4>
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
     * @param request 连接请求
     * @return 会话信息
     */
    @PostMapping("/connect")
    public ResponseEntity<SessionInfo> connect(@RequestBody ConnectionRequest request) {
        return ResponseEntity.ok(terminalService.connect(request));
    }

    /**
     * 断开SSH连接
     * 
     * <h4>请求示例：</h4>
     * <pre>{@code
     * {
     *   "sessionId": "session-a1b2c3d4"
     * }
     * }</pre>
     * 
     * @param params 包含sessionId的Map
     * @return 是否断开成功
     */
    @PostMapping("/disconnect")
    public ResponseEntity<Boolean> disconnect(@RequestBody Map<String, String> params) {
        String sessionId = params.get("sessionId");
        return ResponseEntity.ok(terminalService.disconnect(sessionId));
    }

    /**
     * 列出所有会话
     * 
     * <p>返回当前所有SSH会话的列表，包括活跃和非活跃会话。</p>
     * 
     * @return 会话列表
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionInfo>> listSessions() {
        return ResponseEntity.ok(terminalService.listSessions());
    }

    /**
     * 获取会话详情
     * 
     * @param sessionId 会话ID
     * @return 会话信息，不存在返回404
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
     * 关闭会话
     * 
     * <p>关闭指定会话并释放资源，等同于disconnect操作。</p>
     * 
     * @param sessionId 会话ID
     * @return 是否关闭成功
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Boolean> closeSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(terminalService.disconnect(sessionId));
    }

    /**
     * 执行远程命令
     * 
     * <p>在指定会话的远程服务器上执行Shell命令。</p>
     * 
     * <h4>请求示例：</h4>
     * <pre>{@code
     * {
     *   "sessionId": "session-a1b2c3d4",
     *   "command": "docker ps -a",
     *   "workingDirectory": "/home/admin",
     *   "timeout": 60000
     * }
     * }</pre>
     * 
     * <h4>响应示例：</h4>
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
     * @param request 命令执行请求
     * @return 命令执行结果
     */
    @PostMapping("/execute")
    public ResponseEntity<CommandResult> executeCommand(@RequestBody CommandRequest request) {
        return ResponseEntity.ok(terminalService.executeCommand(
                request.getSessionId(),
                request.getCommand(),
                request.getWorkingDirectory()));
    }

    /**
     * 上传文件
     * 
     * <p>通过SFTP协议将本地文件上传到远程服务器。</p>
     * 
     * <h4>请求示例：</h4>
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
     * @param request 文件传输请求
     * @return 传输结果
     */
    @PostMapping("/upload")
    public ResponseEntity<FileTransferResult> uploadFile(@RequestBody FileTransferRequest request) {
        return ResponseEntity.ok(terminalService.uploadFile(
                request.getSessionId(),
                request.getLocalPath(),
                request.getRemotePath()));
    }

    /**
     * 下载文件
     * 
     * <p>通过SFTP协议从远程服务器下载文件到本地。</p>
     * 
     * <h4>请求示例：</h4>
     * <pre>{@code
     * {
     *   "sessionId": "session-a1b2c3d4",
     *   "remotePath": "/remote/path/file.txt",
     *   "localPath": "/local/path/file.txt",
     *   "overwrite": true
     * }
     * }</pre>
     * 
     * @param request 文件传输请求
     * @return 传输结果
     */
    @PostMapping("/download")
    public ResponseEntity<FileTransferResult> downloadFile(@RequestBody FileTransferRequest request) {
        return ResponseEntity.ok(terminalService.downloadFile(
                request.getSessionId(),
                request.getRemotePath(),
                request.getLocalPath()));
    }

    /**
     * 获取服务状态
     * 
     * <p>返回远程终端服务的运行状态统计。</p>
     * 
     * <h4>响应示例：</h4>
     * <pre>{@code
     * {
     *   "totalSessions": 5,
     *   "activeSessions": 3,
     *   "maxSessions": 100,
     *   "status": "running"
     * }
     * }</pre>
     * 
     * @return 状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(terminalService.getStatus());
    }
}

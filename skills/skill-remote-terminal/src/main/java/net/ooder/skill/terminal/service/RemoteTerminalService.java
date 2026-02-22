package net.ooder.skill.terminal.service;

import net.ooder.skill.terminal.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 远程终端服务接口
 * 
 * <p>提供SSH远程连接、命令执行和文件传输功能，支持远程服务器管理。</p>
 * 
 * <h3>核心功能：</h3>
 * <ul>
 *   <li><b>SSH连接</b>：支持密码和密钥两种认证方式</li>
 *   <li><b>命令执行</b>：在远程服务器上执行Shell命令</li>
 *   <li><b>文件传输</b>：支持SFTP协议的上传和下载</li>
 *   <li><b>会话管理</b>：管理多个并发SSH会话</li>
 * </ul>
 * 
 * <h3>应用场景：</h3>
 * <ul>
 *   <li>运维管理：远程服务器运维、批量命令执行</li>
 *   <li>DevOps：CI/CD流水线中的远程部署</li>
 *   <li>云管理：云服务器远程管理</li>
 *   <li>网络设备：路由器、交换机配置管理</li>
 *   <li>IoT设备：边缘设备远程调试</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 建立SSH连接
 * ConnectionRequest connReq = new ConnectionRequest();
 * connReq.setHost("192.168.1.100");
 * connReq.setPort(22);
 * connReq.setUsername("admin");
 * connReq.setPassword("password");
 * SessionInfo session = remoteTerminalService.connect(connReq);
 * 
 * // 执行命令
 * CommandResult result = remoteTerminalService.executeCommand(
 *     session.getSessionId(), 
 *     "ls -la /home", 
 *     null);
 * System.out.println(result.getOutput());
 * 
 * // 上传文件
 * FileTransferResult upload = remoteTerminalService.uploadFile(
 *     session.getSessionId(),
 *     "/local/file.txt",
 *     "/remote/file.txt");
 * 
 * // 断开连接
 * remoteTerminalService.disconnect(session.getSessionId());
 * }</pre>
 * 
 * <h3>安全建议：</h3>
 * <ul>
 *   <li>优先使用密钥认证而非密码认证</li>
 *   <li>使用SSH证书进行主机验证</li>
 *   <li>限制会话超时时间</li>
 *   <li>记录所有操作日志用于审计</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
public interface RemoteTerminalService {
    
    /**
     * 建立SSH连接
     * 
     * <p>与远程服务器建立SSH连接，支持密码认证和密钥认证两种方式。</p>
     * 
     * <h4>认证方式：</h4>
     * <ul>
     *   <li>密码认证：设置password字段</li>
     *   <li>密钥认证：设置privateKey字段，可选设置passphrase</li>
     * </ul>
     * 
     * @param request 连接请求，包含主机地址、端口、认证信息等
     * @return 会话信息，包含会话ID和连接状态
     * @throws RuntimeException 如果连接失败或达到最大会话数限制
     */
    SessionInfo connect(ConnectionRequest request);
    
    /**
     * 断开SSH连接
     * 
     * <p>关闭指定会话并释放相关资源。</p>
     * 
     * @param sessionId 会话ID
     * @return 是否断开成功
     */
    boolean disconnect(String sessionId);
    
    /**
     * 获取会话详情
     * 
     * @param sessionId 会话ID
     * @return 会话信息，不存在则返回null
     */
    SessionInfo getSession(String sessionId);
    
    /**
     * 列出所有会话
     * 
     * <p>返回当前所有活跃和非活跃的会话列表。</p>
     * 
     * @return 会话列表
     */
    List<SessionInfo> listSessions();
    
    /**
     * 执行远程命令
     * 
     * <p>在指定会话的远程服务器上执行Shell命令。</p>
     * 
     * <h4>执行流程：</h4>
     * <ol>
     *   <li>验证会话是否有效</li>
     *   <li>可选切换工作目录</li>
     *   <li>执行命令并捕获输出</li>
     *   <li>返回执行结果和退出码</li>
     * </ol>
     * 
     * @param sessionId 会话ID
     * @param command 要执行的命令
     * @param workingDirectory 工作目录（可选，为null使用默认目录）
     * @return 命令执行结果，包含输出、错误和退出码
     * @throws RuntimeException 如果会话不存在或未连接
     */
    CommandResult executeCommand(String sessionId, String command, String workingDirectory);
    
    /**
     * 上传文件
     * 
     * <p>通过SFTP协议将本地文件上传到远程服务器。</p>
     * 
     * @param sessionId 会话ID
     * @param localPath 本地文件路径
     * @param remotePath 远程目标路径
     * @return 传输结果，包含传输字节数和状态
     */
    FileTransferResult uploadFile(String sessionId, String localPath, String remotePath);
    
    /**
     * 下载文件
     * 
     * <p>通过SFTP协议从远程服务器下载文件到本地。</p>
     * 
     * @param sessionId 会话ID
     * @param remotePath 远程文件路径
     * @param localPath 本地目标路径
     * @return 传输结果，包含传输字节数和状态
     */
    FileTransferResult downloadFile(String sessionId, String remotePath, String localPath);
    
    /**
     * 获取服务状态
     * 
     * <p>返回远程终端服务的运行状态统计。</p>
     * 
     * <h4>返回数据：</h4>
     * <ul>
     *   <li>totalSessions - 总会话数</li>
     *   <li>activeSessions - 活跃会话数</li>
     *   <li>maxSessions - 最大会话数限制</li>
     *   <li>status - 服务状态</li>
     * </ul>
     * 
     * @return 状态信息Map
     */
    Map<String, Object> getStatus();
}

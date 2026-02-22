package net.ooder.skill.audit.controller;

import net.ooder.skill.audit.dto.*;
import net.ooder.skill.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 审计日志REST API控制器
 * 
 * <p>提供审计日志相关的HTTP接口，支持日志记录、查询、统计和导出功能。</p>
 * 
 * <h3>API端点列表：</h3>
 * <table border="1">
 *   <tr><th>方法</th><th>路径</th><th>描述</th></tr>
 *   <tr><td>POST</td><td>/api/audit/record</td><td>记录审计日志</td></tr>
 *   <tr><td>GET</td><td>/api/audit/logs/{logId}</td><td>根据ID获取日志</td></tr>
 *   <tr><td>POST</td><td>/api/audit/logs</td><td>查询审计日志</td></tr>
 *   <tr><td>GET</td><td>/api/audit/statistics</td><td>获取审计统计</td></tr>
 *   <tr><td>POST</td><td>/api/audit/export</td><td>导出审计日志</td></tr>
 *   <tr><td>GET</td><td>/api/audit/users/{userId}/logs</td><td>获取用户日志</td></tr>
 * </table>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 记录审计日志
 * POST /api/audit/record
 * Content-Type: application/json
 * {
 *   "userId": "user-001",
 *   "userName": "张三",
 *   "action": "login",
 *   "resourceType": "session",
 *   "result": "success",
 *   "ipAddress": "192.168.1.100"
 * }
 * 
 * // 查询审计日志
 * POST /api/audit/logs
 * Content-Type: application/json
 * {
 *   "userId": "user-001",
 *   "startTime": 1700000000000,
 *   "endTime": 1700086400000,
 *   "page": 0,
 *   "size": 20
 * }
 * }</pre>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    /**
     * 记录审计日志
     * 
     * <p>将一条审计日志记录保存到系统中。通常在执行敏感操作时调用此接口进行记录。</p>
     * 
     * <h4>请求示例：</h4>
     * <pre>{@code
     * {
     *   "userId": "user-001",
     *   "userName": "张三",
     *   "action": "delete",
     *   "resourceType": "file",
     *   "resourceId": "file-123",
     *   "resourceName": "重要文档.pdf",
     *   "result": "success",
     *   "ipAddress": "192.168.1.100",
     *   "userAgent": "Mozilla/5.0...",
     *   "description": "删除重要文档"
     * }
     * }</pre>
     * 
     * @param log 审计日志对象
     * @return 保存后的审计日志，包含生成的ID和时间戳
     */
    @PostMapping("/record")
    public ResponseEntity<AuditLog> record(@RequestBody AuditLog log) {
        return ResponseEntity.ok(auditService.record(log));
    }

    /**
     * 根据ID获取审计日志
     * 
     * @param logId 日志ID
     * @return 审计日志对象，如果不存在返回404
     */
    @GetMapping("/logs/{logId}")
    public ResponseEntity<AuditLog> getById(@PathVariable String logId) {
        AuditLog log = auditService.getById(logId);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(log);
    }

    /**
     * 查询审计日志
     * 
     * <p>支持多条件组合查询，包括用户ID、操作类型、资源类型、时间范围等条件。
     * 支持分页和排序。</p>
     * 
     * <h4>查询条件：</h4>
     * <ul>
     *   <li>userId - 用户ID</li>
     *   <li>action - 操作类型（如：login、logout、create、update、delete）</li>
     *   <li>resourceType - 资源类型（如：user、file、session）</li>
     *   <li>resourceId - 资源ID</li>
     *   <li>result - 操作结果（success/failure）</li>
     *   <li>startTime - 开始时间（毫秒时间戳）</li>
     *   <li>endTime - 结束时间（毫秒时间戳）</li>
     *   <li>page - 页码（从0开始）</li>
     *   <li>size - 每页大小</li>
     *   <li>sortBy - 排序字段（默认：timestamp）</li>
     *   <li>sortOrder - 排序方向（asc/desc，默认：desc）</li>
     * </ul>
     * 
     * @param request 查询请求对象
     * @return 查询结果，包含日志列表和分页信息
     */
    @PostMapping("/logs")
    public ResponseEntity<AuditQueryResult> query(@RequestBody AuditQueryRequest request) {
        return ResponseEntity.ok(auditService.query(request));
    }

    /**
     * 获取审计统计数据
     * 
     * <p>统计指定时间范围内的审计数据，包括总量统计、结果统计和分布统计。</p>
     * 
     * <h4>返回数据：</h4>
     * <ul>
     *   <li>totalLogs - 总日志数</li>
     *   <li>todayLogs - 今日日志数</li>
     *   <li>successCount - 成功次数</li>
     *   <li>failureCount - 失败次数</li>
     *   <li>actionCounts - 操作类型分布</li>
     *   <li>resourceTypeCounts - 资源类型分布</li>
     *   <li>userCounts - 用户活跃度统计</li>
     * </ul>
     * 
     * @param startTime 统计开始时间（可选）
     * @param endTime 统计结束时间（可选）
     * @return 审计统计数据
     */
    @GetMapping("/statistics")
    public ResponseEntity<AuditStatistics> getStatistics(
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        return ResponseEntity.ok(auditService.getStatistics(startTime, endTime));
    }

    /**
     * 导出审计日志
     * 
     * <p>将查询结果导出为指定格式的文件下载。</p>
     * 
     * <h4>支持的导出格式：</h4>
     * <ul>
     *   <li>json - JSON格式，适合程序处理</li>
     *   <li>csv - CSV格式，可用Excel打开</li>
     * </ul>
     * 
     * @param request 查询请求对象，用于筛选要导出的日志
     * @param format 导出格式（默认：json）
     * @return 文件下载响应
     */
    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody AuditQueryRequest request,
                                         @RequestParam(defaultValue = "json") String format) {
        byte[] content = auditService.export(request, format);
        String filename = "audit-export." + format;
        String contentType = "csv".equalsIgnoreCase(format) ? "text/csv" : "application/json";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(content);
    }

    /**
     * 获取指定用户的审计日志
     * 
     * <p>便捷接口，用于快速查询某个用户的所有操作记录。</p>
     * 
     * @param userId 用户ID
     * @param page 页码（从0开始，默认：0）
     * @param size 每页大小（默认：20）
     * @return 查询结果
     */
    @GetMapping("/users/{userId}/logs")
    public ResponseEntity<AuditQueryResult> getByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getByUserId(userId, page, size));
    }
}

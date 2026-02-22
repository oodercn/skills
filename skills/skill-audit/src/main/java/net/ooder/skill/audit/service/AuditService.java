package net.ooder.skill.audit.service;

import net.ooder.skill.audit.dto.*;

import java.util.Map;

/**
 * 审计日志服务接口
 * 
 * <p>提供审计日志的记录、查询、统计和导出功能。审计日志用于记录系统中所有重要操作的轨迹，
 * 支持安全审计、合规检查和问题追溯。</p>
 * 
 * <h3>应用场景：</h3>
 * <ul>
 *   <li>安全审计：记录用户登录、权限变更、敏感数据访问等安全相关操作</li>
 *   <li>合规检查：满足企业内控、行业监管对操作记录留存的要求</li>
 *   <li>问题追溯：在系统故障或安全事件发生时，追溯操作历史定位问题</li>
 *   <li>行为分析：统计分析用户行为模式，发现异常操作</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 记录审计日志
 * AuditLog log = new AuditLog();
 * log.setUserId("user-001");
 * log.setAction("login");
 * log.setResourceType("session");
 * log.setResult("success");
 * auditService.record(log);
 * 
 * // 查询审计日志
 * AuditQueryRequest request = new AuditQueryRequest();
 * request.setUserId("user-001");
 * request.setStartTime(System.currentTimeMillis() - 86400000L); // 最近24小时
 * AuditQueryResult result = auditService.query(request);
 * }</pre>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
public interface AuditService {
    
    /**
     * 记录审计日志
     * 
     * <p>将一条审计日志记录保存到存储中。如果日志ID未设置，将自动生成；
     * 如果时间戳未设置，将使用当前时间。</p>
     * 
     * @param log 审计日志对象，包含用户ID、操作类型、资源信息等
     * @return 保存后的审计日志对象，包含生成的ID和时间戳
     * @throws IllegalArgumentException 如果必填字段为空
     */
    AuditLog record(AuditLog log);
    
    /**
     * 根据ID获取审计日志
     * 
     * @param logId 日志ID
     * @return 审计日志对象，如果不存在返回null
     */
    AuditLog getById(String logId);
    
    /**
     * 查询审计日志
     * 
     * <p>支持多条件组合查询，包括用户ID、操作类型、资源类型、时间范围等。
     * 支持分页和排序。</p>
     * 
     * @param request 查询请求对象，包含查询条件和分页参数
     * @return 查询结果，包含日志列表和分页信息
     */
    AuditQueryResult query(AuditQueryRequest request);
    
    /**
     * 获取审计统计数据
     * 
     * <p>统计指定时间范围内的审计数据，包括总日志数、成功/失败次数、
     * 操作类型分布、资源类型分布、用户活跃度等。</p>
     * 
     * @param startTime 统计开始时间（毫秒时间戳），为null表示不限制开始时间
     * @param endTime 统计结束时间（毫秒时间戳），为null表示不限制结束时间
     * @return 审计统计数据对象
     */
    AuditStatistics getStatistics(Long startTime, Long endTime);
    
    /**
     * 导出审计日志
     * 
     * <p>将查询结果导出为指定格式的文件，支持JSON和CSV格式。</p>
     * 
     * @param request 查询请求对象，用于筛选要导出的日志
     * @param format 导出格式，支持"json"和"csv"
     * @return 导出文件的字节数组
     */
    byte[] export(AuditQueryRequest request, String format);
    
    /**
     * 获取指定用户的审计日志
     * 
     * <p>便捷方法，用于快速查询某个用户的所有操作记录。</p>
     * 
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 查询结果
     */
    AuditQueryResult getByUserId(String userId, int page, int size);
}

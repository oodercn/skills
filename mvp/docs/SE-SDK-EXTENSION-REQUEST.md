# SE SDK 扩展协作申请

## 申请编号

**SE-REQ-2026-001**

## 申请标题

LLM调用四级审计统计服务扩展

## 申请方

MVP团队

## 申请日期

2026-03-20

## 优先级

**高**

## 当前状态

**已解决** - SE SDK 2.3.1 已完整支持

---

## 重要更新 (2026-03-20)

经检查 SE SDK 2.3.1 (`scene-engine-2.3.1.jar`)，发现 **已完整支持** LLM 四级审计统计服务：

### 已提供的接口

#### 1. LlmAuditService (net.ooder.scene.llm.audit)

```java
public interface LlmAuditService {
    void logLlmCall(LlmCallContext context, LlmCallResult result);
    CompletableFuture<List<LlmCallLog>> queryLlmLogs(LlmLogQuery query);
    CompletableFuture<LlmUserStats> getUserLlmStats(String userId, long startTime, long endTime);
    CompletableFuture<LlmDepartmentStats> getDepartmentLlmStats(String departmentId, long startTime, long endTime);
    CompletableFuture<LlmCompanyStats> getCompanyLlmStats(String companyId, long startTime, long endTime);
    CompletableFuture<LlmModuleStats> getModuleLlmStats(String moduleId, String userId, long startTime, long endTime);
}
```

#### 2. LlmStatsAggregationService (net.ooder.scene.llm.stats)

```java
public interface LlmStatsAggregationService {
    CompletableFuture<LlmCompanyStats> getCompanyStats(String companyId, StatsTimeRange range);
    CompletableFuture<LlmDepartmentStats> getDepartmentStats(String departmentId, StatsTimeRange range);
    CompletableFuture<LlmUserStats> getUserStats(String userId, StatsTimeRange range);
    CompletableFuture<LlmModuleStats> getModuleStats(String moduleId, String userId, StatsTimeRange range);
    CompletableFuture<List<LlmDepartmentStats>> getDepartmentRanking(String companyId, StatsTimeRange range, int topN);
    CompletableFuture<List<LlmUserStats>> getUserRanking(String departmentId, StatsTimeRange range, int topN);
    CompletableFuture<List<LlmModuleStats>> getModuleRanking(String userId, StatsTimeRange range, int topN);
    CompletableFuture<Void> refreshStats(String companyId);
}
```

#### 3. 四级维度支持 (LlmCallContext)

| 维度 | 字段 |
|------|------|
| 公司级 | companyId, companyName |
| 部门级 | departmentId, departmentName |
| 用户级 | userId, userName |
| 模块级 | moduleId, moduleName, sceneId, sceneName, capabilityId, capabilityName |
| 业务维度 | businessType, clientIp, sessionId, requestId |

#### 4. LLM调用指标 (LlmCallResult)

| 指标 | 字段 |
|------|------|
| Provider | providerId, providerName |
| Model | model, requestType |
| Token | inputTokens, outputTokens, totalTokens |
| 成本 | cost |
| 性能 | latency |
| 状态 | status, errorMessage |

#### 5. 统计结果 (LlmCompanyStats)

| 统计项 | 字段 |
|--------|------|
| 调用统计 | totalCalls, successCalls, failedCalls, successRate |
| Token统计 | totalInputTokens, totalOutputTokens, totalTokens |
| 成本统计 | totalCost, monthToDateCost, budgetLimit, budgetUsedPercent |
| 延迟统计 | avgLatency, maxLatency, minLatency |
| 时间维度 | todayCalls, weekCalls, monthCalls |
| 排名 | topDepartments |
| 分布 | providerDistribution, modelDistribution |

### 实现类

- `JsonLlmAuditServiceImpl` - JSON文件存储实现
- `JsonLlmStatsAggregationServiceImpl` - JSON文件统计聚合实现

---

## 原始需求（已满足）

~~以下为原始申请内容，现已由 SE SDK 2.3.1 完整支持~~

---

### 1.1 当前现状

MVP项目当前使用 SE SDK 2.3.1 版本，该版本提供通用审计服务 `net.ooder.scene.core.security.AuditService`，但**不提供** LLM 调用专用的审计统计服务。

### 1.2 业务需求

MVP 需要实现 **公司-部门-人员-模块** 四级 LLM 调用统计体系，用于：

1. **成本管控** - 按组织维度核算 LLM 调用成本
2. **预算管理** - 设置各级预算限额并监控
3. **性能分析** - 分析各维度调用延迟和成功率
4. **合规审计** - 满足企业审计合规要求

### 1.3 现有能力限制

| 限制项 | 说明 |
|--------|------|
| 无 LLM 专用字段 | 不支持 `inputTokens`、`outputTokens`、`cost`、`latency` 等 LLM 特有指标 |
| 无四级维度 | 不支持公司-部门-用户-模块的层级统计 |
| 无 Provider 维度 | 不支持按 LLM 提供者（DeepSeek、通义千问等）统计 |
| 无模型维度 | 不支持按模型类型统计 |

---

## 二、需求详情

### 2.1 功能需求

#### FR-001: LLM调用审计接口

**描述：** 提供LLM调用的审计日志记录和查询能力

**接口定义：**

```java
package net.ooder.scene.core.llm;

public interface LlmAuditService {
    
    /**
     * 记录LLM调用审计日志
     * @param context LLM审计上下文，包含四级维度信息
     */
    void logLlmCall(LlmAuditContext context);
    
    /**
     * 查询LLM审计日志
     * @param query 查询条件
     * @return 审计日志列表
     */
    CompletableFuture<List<LlmAuditLog>> queryLlmLogs(LlmAuditQuery query);
    
    /**
     * 获取LLM调用统计
     * @param query 统计查询条件
     * @return 统计结果
     */
    CompletableFuture<LlmCallStats> getLlmStats(LlmStatsQuery query);
}
```

#### FR-002: 四级维度支持

**描述：** 审计日志需支持公司-部门-人员-模块四级维度

**数据结构定义：**

```java
package net.ooder.scene.core.llm;

public class LlmAuditContext {
    
    // 四级维度
    private String companyId;          // 公司ID
    private String companyName;        // 公司名称
    private String departmentId;       // 部门ID
    private String departmentName;     // 部门名称
    private String userId;             // 用户ID
    private String userName;           // 用户名称
    private String moduleId;           // 模块ID（场景/能力/工具）
    private String moduleName;         // 模块名称
    private String moduleType;         // 模块类型: SCENE/CAPABILITY/TOOL
    
    // LLM调用信息
    private String providerId;         // 提供者ID (deepseek/qianwen/wenxin等)
    private String providerName;       // 提供者名称
    private String model;              // 模型名称
    private String requestType;        // 请求类型: chat/complete/translate/summarize
    
    // Token统计
    private int inputTokens;           // 输入Token数
    private int outputTokens;          // 输出Token数
    private int totalTokens;           // 总Token数
    
    // 成本与性能
    private double cost;               // 成本(美元)
    private long latency;              // 延迟(毫秒)
    
    // 状态
    private String status;             // 状态: success/error
    private String errorMessage;       // 错误信息
    
    // 业务维度
    private String businessType;       // 业务类型
    private String clientIp;           // 客户端IP
    private String sessionId;          // 会话ID
    private String requestId;          // 请求ID（追踪链路）
    
    // 扩展元数据
    private Map<String, Object> metadata;
    
    // getter/setter 省略
}
```

#### FR-003: 统计聚合接口

**描述：** 提供按维度聚合的统计查询能力

**接口定义：**

```java
package net.ooder.scene.core.llm;

public interface LlmStatsService {
    
    /**
     * 获取公司级统计
     */
    CompletableFuture<CompanyLlmStats> getCompanyStats(
        String companyId, long startTime, long endTime);
    
    /**
     * 获取部门级统计
     */
    CompletableFuture<DepartmentLlmStats> getDepartmentStats(
        String departmentId, long startTime, long endTime);
    
    /**
     * 获取用户级统计
     */
    CompletableFuture<UserLlmStats> getUserStats(
        String userId, long startTime, long endTime);
    
    /**
     * 获取模块级统计
     */
    CompletableFuture<ModuleLlmStats> getModuleStats(
        String moduleId, long startTime, long endTime);
    
    /**
     * 获取排名列表
     */
    CompletableFuture<List<StatsRankingItem>> getRanking(
        StatsRankingQuery query);
}
```

#### FR-004: 统计结果数据结构

```java
package net.ooder.scene.core.llm;

public class CompanyLlmStats {
    private String companyId;
    private String companyName;
    
    // 调用统计
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double successRate;
    
    // Token统计
    private long totalInputTokens;
    private long totalOutputTokens;
    private long totalTokens;
    
    // 成本统计
    private double totalCost;
    private double monthToDateCost;
    
    // 延迟统计
    private double avgLatency;
    private long maxLatency;
    private long minLatency;
    
    // 时间维度
    private long todayCalls;
    private long weekCalls;
    private long monthCalls;
    
    // 部门排名
    private List<DepartmentLlmStats> topDepartments;
    
    // 时间戳
    private long statsTime;
}
```

### 2.2 非功能需求

#### NFR-001: 性能要求

| 指标 | 要求 |
|------|------|
| 审计日志写入延迟 | < 10ms |
| 统计查询响应时间 | < 500ms |
| 并发写入支持 | 1000+ TPS |
| 批量写入支持 | 支持批量写入以提升吞吐量 |

#### NFR-002: 数据保留

| 数据类型 | 保留期限 |
|----------|----------|
| 审计日志 | 90 天 |
| 统计聚合数据 | 1 年 |
| 支持数据归档和导出 | 是 |

#### NFR-003: 安全要求

| 要求 | 说明 |
|------|------|
| 数据隔离 | 按公司隔离，支持多租户 |
| 权限控制 | 按维度控制访问权限 |
| 敏感数据脱敏 | prompt/response 可配置脱敏 |
| 审计追踪 | 所有查询操作可审计 |

#### NFR-004: 高可用要求

| 要求 | 说明 |
|------|------|
| 服务可用性 | 99.9% |
| 故障降级 | 支持本地缓存降级 |
| 数据备份 | 支持数据备份恢复 |

---

## 三、集成方式

### 3.1 Maven依赖

```xml
<dependency>
    <groupId>net.ooder.scene</groupId>
    <artifactId>scene-llm-audit</artifactId>
    <version>2.4.0</version>
</dependency>
```

### 3.2 配置示例

```yaml
ooder:
  scene:
    llm-audit:
      enabled: true
      storage: elasticsearch    # elasticsearch / database
      retention-days: 90
      batch-size: 100
      flush-interval: 1000      # ms
      sensitive-fields:
        - prompt
        - response
      masking-enabled: true
```

### 3.3 使用示例

```java
@Autowired
private LlmAuditService llmAuditService;

// 记录LLM调用
public void recordLlmCall(LlmCallResult result) {
    LlmAuditContext context = new LlmAuditContext();
    context.setCompanyId(getCurrentCompanyId());
    context.setDepartmentId(getCurrentDepartmentId());
    context.setUserId(getCurrentUserId());
    context.setModuleId(getCurrentModuleId());
    context.setProviderId(result.getProviderId());
    context.setModel(result.getModel());
    context.setInputTokens(result.getInputTokens());
    context.setOutputTokens(result.getOutputTokens());
    context.setCost(result.getCost());
    context.setLatency(result.getLatency());
    context.setStatus(result.getStatus());
    
    llmAuditService.logLlmCall(context);
}

// 查询统计
public void queryStats() {
    LlmStatsQuery query = new LlmStatsQuery();
    query.setCompanyId("company-001");
    query.setStartTime(System.currentTimeMillis() - 7 * 86400000L);
    query.setEndTime(System.currentTimeMillis());
    
    CompletableFuture<CompanyLlmStats> future = 
        llmAuditService.getLlmStats(query);
    // ...
}
```

---

## 四、验收标准

| 序号 | 验收项 | 验收标准 |
|------|--------|----------|
| 1 | 接口完整性 | 提供 `LlmAuditService` 和 `LlmStatsService` 完整接口 |
| 2 | 四级维度 | 支持公司-部门-用户-模块四级维度记录和查询 |
| 3 | 写入性能 | 单条写入延迟 < 10ms，批量写入吞吐 > 1000 TPS |
| 4 | 查询性能 | 统计查询响应时间 < 500ms |
| 5 | 数据隔离 | 按公司隔离数据，跨公司查询返回空 |
| 6 | 数据保留 | 审计日志保留 90 天，统计聚合保留 1 年 |
| 7 | 降级支持 | SDK不可用时支持本地缓存降级 |
| 8 | 文档完整 | 提供接口文档和集成指南 |

---

## 五、期望交付时间

| 里程碑 | 时间 | 交付物 |
|--------|------|--------|
| 接口评审 | 2026-04-01 | 接口定义文档 |
| Alpha版本 | 2026-04-15 | 可集成测试版本 |
| Beta版本 | 2026-04-30 | 功能完整版本 |
| 正式版本 | 2026-05-15 | 2.4.0 正式发布 |

---

## 六、联系方式

**申请方：** MVP团队

**联系人：** [请填写]

**邮箱：** [请填写]

---

## 七、附件

1. [LLM监控四级统计设计方案](./LLM-MONITOR-MULTI-LEVEL-STATS-DESIGN.md)
2. 接口定义详细文档（待SE团队确认后补充）

---

## 八、审批记录

| 日期 | 审批人 | 状态 | 备注 |
|------|--------|------|------|
| 2026-03-20 | - | 待审批 | 申请提交 |
| | | | |

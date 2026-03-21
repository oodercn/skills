# LLM监控四级统计设计方案

## 一、背景与需求

### 1.1 当前现状

**现有统计能力：**
- LLM调用日志记录 (`LlmCallLogService`)
- 基础统计指标：总调用次数、Token消耗、成本、延迟、成功率
- 按Provider维度的统计

**现有问题：**
1. 缺少组织架构维度（公司、部门）
2. 缺少模块/能力维度的深度统计
3. 接口统计与自行统计未整合
4. 无多租户隔离能力

### 1.2 目标需求

实现**公司-部门-人员-模块**四级统计体系：

```
公司级 (Company Level)
    └── 部门级 (Department Level)
            └── 人员级 (User Level)
                    └── 模块级 (Module Level)
```

---

## 二、数据模型设计

### 2.1 组织架构扩展

#### 2.1.1 公司实体 (新增)

```java
public class OrgCompanyDTO {
    private String companyId;          // 公司ID
    private String name;               // 公司名称
    private String code;               // 公司编码
    private String industry;           // 行业
    private String contactEmail;       // 联系邮箱
    private String contactPhone;       // 联系电话
    private String address;            // 地址
    private int maxUsers;              // 最大用户数
    private int maxDepartments;        // 最大部门数
    private long createTime;
    private long updateTime;
    private boolean active;
    private Map<String, Object> settings; // 公司级配置
}
```

#### 2.1.2 部门实体 (扩展)

```java
public class OrgDepartmentDTO {
    private String departmentId;
    private String companyId;          // 新增：所属公司ID
    private String name;
    private String description;
    private String parentId;           // 父部门ID（支持多级部门）
    private String managerId;
    private List<String> memberIds;
    private int level;                 // 新增：部门层级
    private String fullPath;           // 新增：部门路径（如：公司/研发部/后端组）
    private long createTime;
    private long updateTime;
}
```

#### 2.1.3 用户实体 (扩展)

```java
public class OrgUserDTO {
    private String userId;
    private String companyId;          // 新增：所属公司ID
    private String departmentId;
    private String departmentName;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String title;
    private String avatar;
    private List<String> permissions;
    private long createTime;
    private long updateTime;
    private boolean active;
}
```

### 2.2 LLM调用日志扩展

```java
public class LlmCallLogDTO {
    // 原有字段
    private String logId;
    private String providerId;
    private String providerName;
    private String model;
    private String requestType;
    private String prompt;
    private String response;
    private int inputTokens;
    private int outputTokens;
    private int totalTokens;
    private double cost;
    private long latency;
    private String status;
    private String errorMessage;
    private long createTime;
    private Map<String, Object> metadata;
    
    // 新增四级维度字段
    private String companyId;          // 公司ID
    private String companyName;        // 公司名称
    private String departmentId;       // 部门ID
    private String departmentName;     // 部门名称
    private String userId;             // 用户ID
    private String userName;           // 用户名称
    private String sceneId;            // 场景ID
    private String sceneName;          // 场景名称
    private String capabilityId;       // 能力ID
    private String capabilityName;     // 能力名称
    private String moduleId;           // 模块ID（能力所属模块）
    private String moduleName;         // 模块名称
    
    // 新增业务维度字段
    private String businessType;       // 业务类型
    private String clientIp;           // 客户端IP
    private String sessionId;          // 会话ID
    private String requestId;          // 请求ID（追踪链路）
}
```

### 2.3 统计聚合模型

#### 2.3.1 公司级统计

```java
public class CompanyLlmStatsDTO {
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
    private double budgetLimit;
    private double budgetUsedPercent;
    
    // 延迟统计
    private double avgLatency;
    private long maxLatency;
    private long minLatency;
    
    // 时间维度
    private long todayCalls;
    private long weekCalls;
    private long monthCalls;
    
    // 部门排名
    private List<DepartmentLlmStatsDTO> topDepartments;
    
    // 时间戳
    private long statsTime;
    private long startTime;
    private long endTime;
}
```

#### 2.3.2 部门级统计

```java
public class DepartmentLlmStatsDTO {
    private String departmentId;
    private String departmentName;
    private String companyId;
    
    // 调用统计
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double successRate;
    
    // Token统计
    private long totalTokens;
    private long totalInputTokens;
    private long totalOutputTokens;
    
    // 成本统计
    private double totalCost;
    private double budgetLimit;
    
    // 用户排名
    private List<UserLlmStatsDTO> topUsers;
    
    // 时间戳
    private long statsTime;
}
```

#### 2.3.3 人员级统计

```java
public class UserLlmStatsDTO {
    private String userId;
    private String userName;
    private String departmentId;
    private String departmentName;
    
    // 调用统计
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double successRate;
    
    // Token统计
    private long totalTokens;
    private long totalInputTokens;
    private long totalOutputTokens;
    
    // 成本统计
    private double totalCost;
    private double quotaLimit;
    private double quotaUsed;
    
    // 模块使用分布
    private List<ModuleLlmStatsDTO> moduleStats;
    
    // 时间戳
    private long statsTime;
}
```

#### 2.3.4 模块级统计

```java
public class ModuleLlmStatsDTO {
    private String moduleId;
    private String moduleName;
    private String moduleType;         // SCENE, CAPABILITY, TOOL
    private String userId;
    
    // 调用统计
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double successRate;
    
    // Token统计
    private long totalTokens;
    private long totalInputTokens;
    private long totalOutputTokens;
    
    // 成本统计
    private double totalCost;
    
    // 延迟统计
    private double avgLatency;
    
    // Provider分布
    private Map<String, Long> providerDistribution;
    
    // Model分布
    private Map<String, Long> modelDistribution;
    
    // 时间戳
    private long statsTime;
}
```

---

## 三、接口统计与自行统计结合方案

### 3.1 双轨统计架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        LLM调用入口                               │
│                   (LlmController / LlmProvider)                  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     统计记录协调器                                │
│                   LlmStatsCoordinator                            │
│  ┌─────────────────┐    ┌─────────────────┐                    │
│  │  接口统计       │    │  自行统计        │                    │
│  │ (SE SDK)        │    │ (本地服务)       │                    │
│  │ AuditService    │    │ LlmCallLogService│                    │
│  └─────────────────┘    └─────────────────┘                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     统计聚合服务                                  │
│                   LlmStatsAggregationService                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    统计数据存储                           │   │
│  │  - 实时统计 (Redis/内存)                                  │   │
│  │  - 历史统计 (数据库)                                      │   │
│  │  - 聚合缓存                                               │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 接口统计 (SE SDK)

**适用场景：**
- 审计合规要求
- 安全事件追溯
- 权限变更记录
- 敏感操作追踪

**SE SDK 提供的能力：**
```java
// 审计服务
net.ooder.scene.core.security.AuditService

// 核心方法
void logOperation(OperationContext context, String operation, 
                  String resource, String resourceId, 
                  OperationResult result, Map<String, Object> details);

CompletableFuture<List<AuditLog>> queryLogs(AuditLogQuery query);
CompletableFuture<UserOperationStats> getUserStats(String userId, long startTime, long endTime);
CompletableFuture<ResourceAccessStats> getResourceStats(String resourceType, String resourceId);
```

**需要SE扩展的接口：**
```java
// 新增：LLM调用审计接口
public interface LlmAuditService {
    // 记录LLM调用审计
    void logLlmCall(LlmAuditContext context);
    
    // 查询LLM审计日志
    CompletableFuture<List<LlmAuditLog>> queryLlmLogs(LlmAuditQuery query);
    
    // 获取LLM调用统计
    CompletableFuture<LlmCallStats> getLlmStats(LlmStatsQuery query);
}

// LLM审计上下文
public class LlmAuditContext {
    private String companyId;
    private String departmentId;
    private String userId;
    private String moduleId;
    private String providerId;
    private String model;
    private int inputTokens;
    private int outputTokens;
    private double cost;
    private long latency;
    private String status;
    private Map<String, Object> metadata;
}
```

### 3.3 自行统计 (本地服务)

**适用场景：**
- 实时监控大屏
- 业务指标统计
- 成本核算
- 性能分析

**本地服务能力：**
```java
public interface LlmCallLogService {
    // 记录调用日志
    void recordCall(LlmCallLogDTO log);
    
    // 查询日志
    List<LlmCallLogDTO> getLogs(LlmLogQuery query);
    
    // 四级统计
    CompanyLlmStatsDTO getCompanyStats(String companyId, StatsTimeRange range);
    DepartmentLlmStatsDTO getDepartmentStats(String departmentId, StatsTimeRange range);
    UserLlmStatsDTO getUserStats(String userId, StatsTimeRange range);
    ModuleLlmStatsDTO getModuleStats(String moduleId, String userId, StatsTimeRange range);
    
    // 聚合统计
    List<DepartmentLlmStatsDTO> getDepartmentRanking(String companyId, int topN);
    List<UserLlmStatsDTO> getUserRanking(String departmentId, int topN);
    List<ModuleLlmStatsDTO> getModuleRanking(String userId, int topN);
}
```

### 3.4 统计协调器实现

```java
@Service
public class LlmStatsCoordinator {
    
    @Autowired(required = false)
    private LlmAuditService sdkAuditService;  // SE SDK审计服务
    
    @Autowired
    private LlmCallLogService localLogService;  // 本地日志服务
    
    @Autowired
    private LlmStatsAggregationService aggregationService;  // 聚合服务
    
    /**
     * 记录LLM调用（双轨统计）
     */
    public void recordLlmCall(LlmCallLogDTO logEntry) {
        // 1. 本地统计（始终执行）
        localLogService.recordCall(logEntry);
        
        // 2. SE SDK审计（可选，根据配置）
        if (sdkAuditService != null && isAuditEnabled()) {
            LlmAuditContext auditContext = convertToAuditContext(logEntry);
            sdkAuditService.logLlmCall(auditContext);
        }
        
        // 3. 实时聚合更新
        aggregationService.updateRealtimeStats(logEntry);
    }
    
    /**
     * 获取综合统计（合并数据源）
     */
    public CompanyLlmStatsDTO getCompanyStats(String companyId, StatsTimeRange range) {
        // 优先从本地服务获取
        CompanyLlmStatsDTO localStats = localLogService.getCompanyStats(companyId, range);
        
        // 如果SE SDK有数据，进行合并
        if (sdkAuditService != null) {
            LlmStatsQuery query = new LlmStatsQuery();
            query.setCompanyId(companyId);
            query.setStartTime(range.getStartTime());
            query.setEndTime(range.getEndTime());
            
            LlmCallStats sdkStats = sdkAuditService.getLlmStats(query).join();
            if (sdkStats != null) {
                mergeStats(localStats, sdkStats);
            }
        }
        
        return localStats;
    }
}
```

---

## 四、API接口设计

### 4.1 统计查询接口

```yaml
# 公司级统计
GET /api/v1/llm/monitor/stats/company/{companyId}
参数:
  - startTime: 开始时间
  - endTime: 结束时间
  - granularity: 时间粒度 (hour/day/week/month)
返回: CompanyLlmStatsDTO

# 部门级统计
GET /api/v1/llm/monitor/stats/department/{departmentId}
参数:
  - startTime, endTime, granularity
返回: DepartmentLlmStatsDTO

# 人员级统计
GET /api/v1/llm/monitor/stats/user/{userId}
参数:
  - startTime, endTime, granularity
返回: UserLlmStatsDTO

# 模块级统计
GET /api/v1/llm/monitor/stats/module/{moduleId}
参数:
  - userId: 用户ID（可选）
  - startTime, endTime, granularity
返回: ModuleLlmStatsDTO

# 排名查询
GET /api/v1/llm/monitor/ranking/departments
参数:
  - companyId: 公司ID
  - topN: 返回数量
  - orderBy: 排序字段 (calls/tokens/cost)
返回: List<DepartmentLlmStatsDTO>

GET /api/v1/llm/monitor/ranking/users
参数:
  - departmentId: 部门ID
  - topN, orderBy
返回: List<UserLlmStatsDTO>

GET /api/v1/llm/monitor/ranking/modules
参数:
  - userId: 用户ID
  - topN, orderBy
返回: List<ModuleLlmStatsDTO>
```

### 4.2 日志查询接口

```yaml
# 多维度日志查询
GET /api/v1/llm/monitor/logs
参数:
  - companyId: 公司ID
  - departmentId: 部门ID
  - userId: 用户ID
  - moduleId: 模块ID
  - providerId: 提供者ID
  - status: 状态
  - startTime, endTime
  - pageNum, pageSize
返回: PageResult<LlmCallLogDTO>
```

---

## 五、实现计划

### 5.1 Phase 1: 数据模型扩展 (MVP侧)

**工作量：2天**

1. 扩展 `LlmCallLogDTO` 添加四级维度字段
2. 扩展 `OrgDepartmentDTO` 添加 companyId
3. 扩展 `OrgUserDTO` 添加 companyId
4. 新增 `OrgCompanyDTO` 实体
5. 修改 `LlmController` 在记录日志时填充维度信息

### 5.2 Phase 2: 统计服务实现 (MVP侧)

**工作量：3天**

1. 实现 `CompanyLlmStatsDTO` 等统计DTO
2. 扩展 `LlmCallLogService` 添加四级统计方法
3. 实现 `LlmStatsAggregationService` 聚合服务
4. 实现 `LlmStatsCoordinator` 协调器

### 5.3 Phase 3: SE SDK扩展 (SE侧)

**工作量：3天**

1. 设计并实现 `LlmAuditService` 接口
2. 实现 `LlmAuditContext` 和 `LlmAuditLog` 实体
3. 实现审计日志存储和查询
4. 提供统计聚合接口

### 5.4 Phase 4: 前端展示 (MVP侧)

**工作量：2天**

1. 扩展 llm-monitor.html 添加四级统计展示
2. 实现公司/部门/人员/模块切换
3. 实现排名图表
4. 实现趋势分析图表

---

## 六、SE SDK协作需求文档

### 6.1 需求概述

**需求编号：** SE-REQ-2026-001  
**需求名称：** LLM调用四级审计统计服务  
**优先级：** 高  
**提出方：** MVP团队

### 6.2 功能需求

#### FR-001: LLM调用审计接口

**描述：** 提供LLM调用的审计日志记录和查询能力

**接口定义：**
```java
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
}
```

#### FR-002: 四级维度支持

**描述：** 审计日志需支持公司-部门-人员-模块四级维度

**数据结构：**
```java
public class LlmAuditContext {
    // 四级维度
    private String companyId;      // 公司ID
    private String departmentId;   // 部门ID  
    private String userId;         // 用户ID
    private String moduleId;       // 模块ID（场景/能力/工具）
    
    // LLM调用信息
    private String providerId;     // 提供者ID
    private String model;          // 模型名称
    private int inputTokens;       // 输入Token
    private int outputTokens;      // 输出Token
    private double cost;           // 成本
    private long latency;          // 延迟
    private String status;         // 状态
    private String errorMessage;   // 错误信息
    private Map<String, Object> metadata; // 扩展元数据
}
```

#### FR-003: 统计聚合接口

**描述：** 提供按维度聚合的统计查询能力

**接口定义：**
```java
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

### 6.3 非功能需求

#### NFR-001: 性能要求

- 审计日志写入延迟 < 10ms
- 统计查询响应时间 < 500ms
- 支持每秒 1000+ 并发写入

#### NFR-002: 数据保留

- 审计日志保留 90 天
- 统计聚合数据保留 1 年
- 支持数据归档和导出

#### NFR-003: 安全要求

- 支持数据隔离（按公司）
- 支持权限控制（按维度）
- 敏感数据脱敏

### 6.4 集成方式

```xml
<!-- Maven依赖 -->
<dependency>
    <groupId>net.ooder.scene</groupId>
    <artifactId>scene-llm-audit</artifactId>
    <version>2.4.0</version>
</dependency>
```

```yaml
# 配置
ooder:
  scene:
    llm-audit:
      enabled: true
      storage: elasticsearch  # 或 database
      retention-days: 90
```

### 6.5 验收标准

1. 能够记录包含四级维度的LLM调用审计日志
2. 能够按任意维度组合查询审计日志
3. 能够获取各级别的统计数据
4. 能够获取排名列表
5. 性能指标满足NFR要求

---

## 七、附录

### 7.1 现有代码参考

| 模块 | 文件路径 | 说明 |
|------|----------|------|
| LLM日志服务 | `service/LlmCallLogService.java` | 本地日志记录 |
| LLM监控控制器 | `controller/LlmMonitorController.java` | 监控API |
| 组织适配器 | `adapter/OrgWebAdapter.java` | 组织架构管理 |
| SE SDK集成 | `integration/SceneEngineIntegration.java` | SDK集成层 |
| 审计服务 | `service/impl/AuditServiceSdkImpl.java` | SE审计服务实现 |

### 7.2 数据流向图

```
用户请求
    │
    ▼
LlmController
    │
    ├── 调用 LlmProvider
    │
    ▼
LlmStatsCoordinator.recordLlmCall()
    │
    ├── 本地统计: LlmCallLogService.recordCall()
    │       │
    │       ▼
    │   内存存储 / 数据库存储
    │
    └── SE审计: LlmAuditService.logLlmCall()
            │
            ▼
        SE SDK审计存储
```

---

## 八、数据库设计

### 8.1 公司表 (llm_company)

```sql
CREATE TABLE llm_company (
    company_id VARCHAR(64) PRIMARY KEY COMMENT '公司ID',
    company_code VARCHAR(32) UNIQUE COMMENT '公司编码',
    company_name VARCHAR(128) NOT NULL COMMENT '公司名称',
    industry VARCHAR(64) COMMENT '行业',
    contact_email VARCHAR(128) COMMENT '联系邮箱',
    contact_phone VARCHAR(32) COMMENT '联系电话',
    address VARCHAR(256) COMMENT '地址',
    max_users INT DEFAULT 100 COMMENT '最大用户数',
    max_departments INT DEFAULT 20 COMMENT '最大部门数',
    budget_limit DECIMAL(12,2) DEFAULT 0 COMMENT '预算限额(美元)',
    settings TEXT COMMENT '公司级配置(JSON)',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    create_time BIGINT NOT NULL COMMENT '创建时间',
    update_time BIGINT COMMENT '更新时间',
    INDEX idx_company_code (company_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM公司表';
```

### 8.2 部门表扩展 (修改现有 departments 表)

```sql
ALTER TABLE departments ADD COLUMN company_id VARCHAR(64) COMMENT '所属公司ID';
ALTER TABLE departments ADD COLUMN level INT DEFAULT 1 COMMENT '部门层级';
ALTER TABLE departments ADD COLUMN full_path VARCHAR(256) COMMENT '部门路径';
ALTER TABLE departments ADD COLUMN budget_limit DECIMAL(12,2) DEFAULT 0 COMMENT '部门预算限额';

CREATE INDEX idx_dept_company ON departments(company_id);
```

### 8.3 用户表扩展 (修改现有 users 表)

```sql
ALTER TABLE users ADD COLUMN company_id VARCHAR(64) COMMENT '所属公司ID';
ALTER TABLE users ADD COLUMN quota_limit DECIMAL(10,2) DEFAULT 100 COMMENT '用户配额限额(美元)';

CREATE INDEX idx_user_company ON users(company_id);
```

### 8.4 LLM调用日志表 (llm_call_log)

```sql
CREATE TABLE llm_call_log (
    log_id VARCHAR(64) PRIMARY KEY COMMENT '日志ID',
    
    -- 四级维度
    company_id VARCHAR(64) COMMENT '公司ID',
    department_id VARCHAR(64) COMMENT '部门ID',
    user_id VARCHAR(64) COMMENT '用户ID',
    module_id VARCHAR(64) COMMENT '模块ID',
    module_type VARCHAR(32) COMMENT '模块类型: SCENE/CAPABILITY/TOOL',
    
    -- 场景/能力信息
    scene_id VARCHAR(64) COMMENT '场景ID',
    capability_id VARCHAR(64) COMMENT '能力ID',
    
    -- LLM信息
    provider_id VARCHAR(32) NOT NULL COMMENT '提供者ID',
    provider_name VARCHAR(64) COMMENT '提供者名称',
    model VARCHAR(64) NOT NULL COMMENT '模型名称',
    request_type VARCHAR(32) COMMENT '请求类型: chat/complete/translate/summarize',
    
    -- 调用内容
    prompt TEXT COMMENT '输入提示(脱敏)',
    response TEXT COMMENT '响应内容(脱敏)',
    
    -- Token统计
    input_tokens INT DEFAULT 0 COMMENT '输入Token数',
    output_tokens INT DEFAULT 0 COMMENT '输出Token数',
    total_tokens INT DEFAULT 0 COMMENT '总Token数',
    
    -- 成本与性能
    cost DECIMAL(10,6) DEFAULT 0 COMMENT '成本(美元)',
    latency BIGINT DEFAULT 0 COMMENT '延迟(毫秒)',
    
    -- 状态
    status VARCHAR(16) NOT NULL COMMENT '状态: success/error',
    error_message TEXT COMMENT '错误信息',
    
    -- 业务维度
    business_type VARCHAR(32) COMMENT '业务类型',
    client_ip VARCHAR(64) COMMENT '客户端IP',
    session_id VARCHAR(64) COMMENT '会话ID',
    request_id VARCHAR(64) COMMENT '请求ID',
    
    -- 元数据
    metadata TEXT COMMENT '扩展元数据(JSON)',
    create_time BIGINT NOT NULL COMMENT '创建时间',
    
    -- 索引
    INDEX idx_company_time (company_id, create_time),
    INDEX idx_department_time (department_id, create_time),
    INDEX idx_user_time (user_id, create_time),
    INDEX idx_module_time (module_id, create_time),
    INDEX idx_provider_time (provider_id, create_time),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM调用日志表';

-- 分区策略 (按月分区)
ALTER TABLE llm_call_log PARTITION BY RANGE (create_time) (
    PARTITION p202601 VALUES LESS THAN (UNIX_TIMESTAMP('2026-02-01') * 1000),
    PARTITION p202602 VALUES LESS THAN (UNIX_TIMESTAMP('2026-03-01') * 1000),
    PARTITION p202603 VALUES LESS THAN (UNIX_TIMESTAMP('2026-04-01') * 1000),
    PARTITION pmax VALUES LESS THAN MAXVALUE
);
```

### 8.5 统计聚合表 (llm_stats_daily)

```sql
CREATE TABLE llm_stats_daily (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 维度
    stats_level VARCHAR(16) NOT NULL COMMENT '统计级别: COMPANY/DEPARTMENT/USER/MODULE',
    dimension_id VARCHAR(64) NOT NULL COMMENT '维度ID',
    parent_id VARCHAR(64) COMMENT '父维度ID',
    
    -- 时间
    stats_date DATE NOT NULL COMMENT '统计日期',
    
    -- 调用统计
    total_calls BIGINT DEFAULT 0 COMMENT '总调用次数',
    success_calls BIGINT DEFAULT 0 COMMENT '成功调用次数',
    failed_calls BIGINT DEFAULT 0 COMMENT '失败调用次数',
    
    -- Token统计
    total_input_tokens BIGINT DEFAULT 0 COMMENT '总输入Token',
    total_output_tokens BIGINT DEFAULT 0 COMMENT '总输出Token',
    total_tokens BIGINT DEFAULT 0 COMMENT '总Token',
    
    -- 成本统计
    total_cost DECIMAL(12,6) DEFAULT 0 COMMENT '总成本',
    
    -- 延迟统计
    avg_latency BIGINT DEFAULT 0 COMMENT '平均延迟',
    max_latency BIGINT DEFAULT 0 COMMENT '最大延迟',
    min_latency BIGINT DEFAULT 0 COMMENT '最小延迟',
    
    -- Provider分布 (JSON)
    provider_distribution TEXT COMMENT 'Provider分布',
    
    -- Model分布 (JSON)
    model_distribution TEXT COMMENT 'Model分布',
    
    create_time BIGINT NOT NULL COMMENT '创建时间',
    update_time BIGINT COMMENT '更新时间',
    
    UNIQUE KEY uk_level_date_dim (stats_level, stats_date, dimension_id),
    INDEX idx_date (stats_date),
    INDEX idx_level_date (stats_level, stats_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM统计聚合表(日)';
```

---

## 九、前端页面设计

### 9.1 页面结构

```
llm-monitor-v2.html
├── 顶部导航栏
│   ├── 公司选择器
│   ├── 时间范围选择
│   └── 刷新按钮
├── 统计概览卡片
│   ├── 总调用次数
│   ├── 总Token消耗
│   ├── 总成本
│   └── 平均延迟
├── 四级统计切换
│   ├── 公司级视图
│   ├── 部门级视图
│   ├── 人员级视图
│   └── 模块级视图
├── 图表区域
│   ├── 调用趋势图 (折线图)
│   ├── 成本分布图 (饼图)
│   ├── 排名列表 (柱状图)
│   └── Provider分布 (饼图)
└── 日志列表
    ├── 多维度筛选
    ├── 分页表格
    └── 详情弹窗
```

### 9.2 组件设计

#### 9.2.1 统计卡片组件

```html
<div class="stats-card-grid">
    <div class="stats-card" data-level="company">
        <div class="stats-card-header">
            <span class="stats-card-title">总调用次数</span>
            <span class="stats-card-trend up">+12%</span>
        </div>
        <div class="stats-card-value" id="totalCalls">0</div>
        <div class="stats-card-footer">
            <span>今日: <span id="todayCalls">0</span></span>
        </div>
    </div>
    <!-- 更多卡片... -->
</div>
```

#### 9.2.2 四级切换组件

```html
<div class="level-tabs">
    <button class="level-tab active" data-level="company">
        <i class="ri-building-line"></i> 公司
    </button>
    <button class="level-tab" data-level="department">
        <i class="ri-team-line"></i> 部门
    </button>
    <button class="level-tab" data-level="user">
        <i class="ri-user-line"></i> 人员
    </button>
    <button class="level-tab" data-level="module">
        <i class="ri-apps-line"></i> 模块
    </button>
</div>
```

#### 9.2.3 排名列表组件

```html
<div class="ranking-panel">
    <div class="ranking-header">
        <h3>调用排名 TOP 10</h3>
        <select id="rankingOrderBy">
            <option value="calls">按调用次数</option>
            <option value="tokens">按Token消耗</option>
            <option value="cost">按成本</option>
        </select>
    </div>
    <div class="ranking-list" id="rankingList">
        <!-- 动态生成 -->
    </div>
</div>
```

### 9.3 JavaScript API调用

```javascript
var LLMMonitorV2 = {
    currentLevel: 'company',
    currentCompanyId: null,
    currentDepartmentId: null,
    currentUserId: null,
    timeRange: { start: null, end: null },
    
    init: function() {
        this.loadCompanyList();
        this.loadStats();
        this.bindEvents();
    },
    
    loadStats: function() {
        var url = '/api/v1/llm/monitor/stats/' + this.currentLevel;
        var params = this.buildParams();
        
        fetch(url + '?' + new URLSearchParams(params))
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (data.status === 'success') {
                    this.renderStats(data.data);
                }
            }.bind(this));
    },
    
    loadRanking: function() {
        var url = '/api/v1/llm/monitor/ranking/' + this.getRankingType();
        var params = this.buildRankingParams();
        
        fetch(url + '?' + new URLSearchParams(params))
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (data.status === 'success') {
                    this.renderRanking(data.data);
                }
            }.bind(this));
    },
    
    switchLevel: function(level) {
        this.currentLevel = level;
        this.loadStats();
        this.loadRanking();
        this.loadLogs();
    },
    
    buildParams: function() {
        var params = {
            startTime: this.timeRange.start,
            endTime: this.timeRange.end
        };
        
        switch (this.currentLevel) {
            case 'department':
                params.companyId = this.currentCompanyId;
                break;
            case 'user':
                params.departmentId = this.currentDepartmentId;
                break;
            case 'module':
                params.userId = this.currentUserId;
                break;
        }
        
        return params;
    }
};
```

---

## 十、测试用例

### 10.1 单元测试

#### 10.1.1 LlmCallLogService 测试

```java
@SpringBootTest
public class LlmCallLogServiceTest {

    @Autowired
    private LlmCallLogService llmCallLogService;

    @Test
    public void testRecordCall() {
        LlmCallLogDTO log = new LlmCallLogDTO();
        log.setCompanyId("company-001");
        log.setDepartmentId("dept-001");
        log.setUserId("user-001");
        log.setModuleId("scene-001");
        log.setProviderId("qianwen");
        log.setModel("qwen-plus");
        log.setInputTokens(100);
        log.setOutputTokens(200);
        log.setCost(0.005);
        log.setLatency(1500L);
        log.setStatus("success");
        
        llmCallLogService.recordCall(log);
        
        LlmCallLogDTO retrieved = llmCallLogService.getLogById(log.getLogId());
        assertNotNull(retrieved);
        assertEquals("company-001", retrieved.getCompanyId());
    }

    @Test
    public void testGetCompanyStats() {
        StatsTimeRange range = new StatsTimeRange();
        range.setStartTime(System.currentTimeMillis() - 86400000L);
        range.setEndTime(System.currentTimeMillis());
        
        CompanyLlmStatsDTO stats = llmCallLogService.getCompanyStats("company-001", range);
        
        assertNotNull(stats);
        assertTrue(stats.getTotalCalls() >= 0);
    }

    @Test
    public void testGetDepartmentRanking() {
        List<DepartmentLlmStatsDTO> ranking = llmCallLogService.getDepartmentRanking("company-001", 10);
        
        assertNotNull(ranking);
        assertTrue(ranking.size() <= 10);
    }
}
```

#### 10.1.2 LlmStatsCoordinator 测试

```java
@SpringBootTest
public class LlmStatsCoordinatorTest {

    @Autowired
    private LlmStatsCoordinator coordinator;

    @Test
    public void testDualTrackRecording() {
        LlmCallLogDTO log = createTestLog();
        
        coordinator.recordLlmCall(log);
        
        // 验证本地存储
        LlmCallLogDTO local = coordinator.getLocalLogService().getLogById(log.getLogId());
        assertNotNull(local);
        
        // 验证SE SDK审计 (如果启用)
        // ...
    }

    @Test
    public void testMergedStats() {
        StatsTimeRange range = createTestRange();
        
        CompanyLlmStatsDTO stats = coordinator.getCompanyStats("company-001", range);
        
        assertNotNull(stats);
        // 验证合并逻辑
    }
}
```

### 10.2 集成测试

#### 10.2.1 API集成测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LlmMonitorApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetCompanyStats() throws Exception {
        mockMvc.perform(get("/api/v1/llm/monitor/stats/company/company-001")
                .param("startTime", String.valueOf(System.currentTimeMillis() - 86400000L))
                .param("endTime", String.valueOf(System.currentTimeMillis())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.companyId").value("company-001"));
    }

    @Test
    public void testGetDepartmentRanking() throws Exception {
        mockMvc.perform(get("/api/v1/llm/monitor/ranking/departments")
                .param("companyId", "company-001")
                .param("topN", "10")
                .param("orderBy", "calls"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testMultiDimensionLogQuery() throws Exception {
        mockMvc.perform(get("/api/v1/llm/monitor/logs")
                .param("companyId", "company-001")
                .param("departmentId", "dept-001")
                .param("status", "success")
                .param("pageNum", "1")
                .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"));
    }
}
```

### 10.3 性能测试

```java
@SpringBootTest
public class LlmMonitorPerformanceTest {

    @Autowired
    private LlmCallLogService logService;

    @Test
    public void testBatchInsertPerformance() {
        int count = 1000;
        long start = System.currentTimeMillis();
        
        for (int i = 0; i < count; i++) {
            LlmCallLogDTO log = createRandomLog();
            logService.recordCall(log);
        }
        
        long elapsed = System.currentTimeMillis() - start;
        double avgLatency = elapsed / (double) count;
        
        System.out.println("Total time: " + elapsed + "ms");
        System.out.println("Avg latency: " + avgLatency + "ms");
        
        assertTrue(avgLatency < 10, "Avg latency should be < 10ms");
    }

    @Test
    public void testStatsQueryPerformance() {
        StatsTimeRange range = createTestRange();
        
        long start = System.currentTimeMillis();
        CompanyLlmStatsDTO stats = logService.getCompanyStats("company-001", range);
        long elapsed = System.currentTimeMillis() - start;
        
        System.out.println("Stats query time: " + elapsed + "ms");
        assertTrue(elapsed < 500, "Query should complete in < 500ms");
    }
}
```

---

## 十一、风险评估与应对

### 11.1 技术风险

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|----------|
| 大数据量查询性能下降 | 高 | 中 | 1. 实现分表分区策略<br>2. 增加缓存层<br>3. 异步聚合统计 |
| SE SDK接口不稳定 | 高 | 低 | 1. 本地服务作为主数据源<br>2. SDK降级策略<br>3. 数据同步补偿机制 |
| Token统计不准确 | 中 | 中 | 1. 多数据源校验<br>2. 定期对账<br>3. 误差阈值告警 |

### 11.2 业务风险

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|----------|
| 组织架构变更导致统计断层 | 中 | 中 | 1. 记录历史组织快照<br>2. 支持维度映射<br>3. 数据迁移工具 |
| 成本预算超支 | 高 | 低 | 1. 实时预算监控<br>2. 阈值告警<br>3. 自动熔断机制 |
| 敏感数据泄露 | 高 | 低 | 1. 数据脱敏存储<br>2. 权限隔离<br>3. 审计日志 |

### 11.3 运维风险

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|----------|
| 存储空间不足 | 高 | 中 | 1. 数据归档策略<br>2. 冷热数据分离<br>3. 定期清理过期数据 |
| 监控告警风暴 | 中 | 中 | 1. 告警聚合<br>2. 静默期设置<br>3. 分级告警 |

---

## 十二、部署与运维

### 12.1 部署架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        负载均衡器                                 │
│                      (Nginx/Gateway)                             │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
        ┌──────────┐   ┌──────────┐   ┌──────────┐
        │ MVP App  │   │ MVP App  │   │ MVP App  │
        │ Instance │   │ Instance │   │ Instance │
        └──────────┘   └──────────┘   └──────────┘
              │               │               │
              └───────────────┼───────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
        ┌──────────┐   ┌──────────┐   ┌──────────┐
        │  MySQL   │   │  Redis   │   │   SE     │
        │ (主从)   │   │ (集群)   │   │  SDK     │
        └──────────┘   └──────────┘   └──────────┘
```

### 12.2 配置示例

```yaml
# application.yml
ooder:
  llm:
    monitor:
      enabled: true
      storage:
        type: database          # database / memory
        database:
          partition-enabled: true
          retention-days: 90
      cache:
        enabled: true
        ttl: 300                # 缓存过期时间(秒)
      aggregation:
        enabled: true
        cron: "0 5 * * * ?"     # 每小时聚合一次
      audit:
        sdk-enabled: true       # 是否启用SE SDK审计
        fallback-local: true    # SDK不可用时降级到本地
```

### 12.3 监控指标

```yaml
# Prometheus指标
llm_monitor:
  calls_total{company, department, user, module, provider, status}
  tokens_total{company, department, user, module, type}  # type: input/output
  cost_total{company, department, user, module}
  latency_seconds{company, department, user, module, provider}
  errors_total{company, department, user, module, provider}
```

---

## 十三、版本规划

### 13.1 版本路线图

| 版本 | 功能 | 发布时间 |
|------|------|----------|
| v2.4.0 | 四级统计基础框架、数据模型扩展 | 2026-04 |
| v2.5.0 | SE SDK审计集成、双轨统计 | 2026-05 |
| v2.6.0 | 前端可视化、排名分析 | 2026-06 |
| v2.7.0 | 预算管理、告警通知 | 2026-07 |

### 13.2 兼容性说明

- **数据库**: MySQL 5.7+ / MySQL 8.0+
- **JDK**: Java 8+ / Java 11+
- **SE SDK**: 2.3.0+ (审计功能需要 2.4.0+)
- **浏览器**: Chrome 90+, Firefox 88+, Safari 14+

---

## 十四、术语表

| 术语 | 说明 |
|------|------|
| 四级统计 | 公司-部门-人员-模块四个层级的统计体系 |
| 双轨统计 | 本地统计 + SE SDK审计的双重统计机制 |
| 模块 | 场景(Scene)、能力(Capability)、工具(Tool)的统称 |
| Provider | LLM提供者，如DeepSeek、通义千问、百度文心等 |
| Token | LLM调用的计费单位，包括输入Token和输出Token |
| 成本 | LLM调用的费用，通常以美元计价 |
| 延迟 | 从发起请求到收到响应的时间，单位毫秒 |

---

## 十五、参考资料

1. [SE SDK 审计服务文档](./SE-SDK-AUDIT-INTEGRATION.md)
2. [Ooder 组织架构设计](./ORG-ARCHITECTURE.md)
3. [LLM Provider 接口规范](./LLM-PROVIDER-SPEC.md)
4. [数据库分表分区最佳实践](./DATABASE-PARTITION-GUIDE.md)

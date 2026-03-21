# LLM监控日志集成 - 协作申请

## 一、需求背景

### 1.1 当前问题

MVP项目中的LLM调用日志存在以下问题：

| 问题 | 影响 | 严重程度 |
|------|------|----------|
| 日志存储在内存中 | 重启后数据丢失 | 🔴 高 |
| 无持久化机制 | 无法追溯历史调用 | 🔴 高 |
| 趋势数据硬编码 | 前端显示不准确 | 🟡 中 |

### 1.2 当前实现

**位置**: `LlmCallLogServiceImpl.java`

```java
// 问题：数据存储在内存中
private final Deque<LlmCallLogDTO> callLogs = new ConcurrentLinkedDeque<LlmCallLogDTO>();
```

---

## 二、SDK已有能力

### 2.1 SDK已提供的服务

SDK v2.3.1 已提供完整的LLM调用审计服务：

| 模块 | 包路径 | 说明 |
|------|--------|------|
| `LlmAuditService` | `net.ooder.scene.llm.audit` | 审计服务接口 |
| `LlmCallLog` | `net.ooder.scene.llm.audit` | 调用日志实体 |
| `JsonLlmAuditServiceImpl` | `net.ooder.scene.llm.audit.impl` | JSON文件持久化实现 |

### 2.2 SDK实现特性

```java
// SDK已实现JSON文件持久化
public class JsonLlmAuditServiceImpl implements LlmAuditService {
    
    // 启动时加载
    private void loadLogs() {
        File file = new File(dataDir, "llm-call-logs.json");
        LlmCallLog[] array = objectMapper.readValue(file, LlmCallLog[].class);
        // ...
    }
    
    // 每次调用后保存
    private void saveLogs() {
        objectMapper.writeValue(file, callLogs);
    }
}
```

### 2.3 SDK支持的功能

| 功能 | 方法 | 说明 |
|------|------|------|
| 记录调用 | `logLlmCall(context, result)` | 记录LLM调用日志 |
| 查询日志 | `queryLlmLogs(query)` | 多条件查询 |
| 用户统计 | `getUserLlmStats(userId, start, end)` | 用户维度统计 |
| 部门统计 | `getDepartmentLlmStats(deptId, start, end)` | 部门维度统计 |
| 公司统计 | `getCompanyLlmStats(companyId, start, end)` | 公司维度统计 |
| 模块统计 | `getModuleLlmStats(moduleId, userId, start, end)` | 模块维度统计 |

---

## 三、集成需求

### 3.1 MVP需要的工作

| 任务 | 负责团队 | 说明 |
|------|----------|------|
| SDK集成配置 | SE | 提供Spring Boot自动配置 |
| 服务注入 | MVP | 注入SDK的`LlmAuditService` |
| Controller适配 | MVP | 调用SDK服务 |
| 前端适配 | MVP | 显示真实趋势数据 |

### 3.2 需要SE提供的支持

#### 3.2.1 Spring Boot自动配置

```java
// 需要SE提供的自动配置类
@Configuration
@ConditionalOnClass(LlmAuditService.class)
public class LlmAuditAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public LlmAuditService llmAuditService(
            @Value("${scene.engine.llm.audit.data-path:data/llm-audit}") String dataPath) {
        return new JsonLlmAuditServiceImpl(dataPath);
    }
}
```

#### 3.2.2 配置属性

```yaml
# 需要SE支持的配置项
scene:
  engine:
    llm:
      audit:
        enabled: true
        data-path: data/llm-audit
        max-log-size: 10000
```

#### 3.2.3 趋势计算支持

```java
// 需要SDK支持趋势计算
public interface LlmAuditService {
    
    // 新增：获取趋势数据
    CompletableFuture<LlmTrendStats> getTrendStats(String companyId, long startTime, long endTime);
}

public class LlmTrendStats {
    private double callsTrend;      // 调用次数趋势（相比上周期）
    private double tokensTrend;     // Token消耗趋势
    private double costTrend;       // 成本趋势
    private double latencyTrend;    // 延迟趋势
}
```

---

## 四、数据模型对比

### 4.1 MVP现有模型

```java
public class LlmCallLogDTO {
    private String logId;
    private String providerId;
    private String providerName;
    private String model;
    private int inputTokens;
    private int outputTokens;
    private int totalTokens;
    private double cost;
    private long latency;
    private String status;
    private String errorMessage;
    private long createTime;
}
```

### 4.2 SDK模型

```java
public class LlmCallLog {
    // 基础信息
    private String logId;
    
    // 组织架构（新增）
    private String companyId;
    private String companyName;
    private String departmentId;
    private String departmentName;
    private String userId;
    private String userName;
    
    // 场景信息（新增）
    private String sceneId;
    private String sceneName;
    private String capabilityId;
    private String capabilityName;
    private String moduleId;
    private String moduleName;
    
    // LLM信息
    private String providerId;
    private String providerName;
    private String model;
    private int inputTokens;
    private int outputTokens;
    private int totalTokens;
    private double cost;
    private long latency;
    private String status;
    private String errorMessage;
    private long timestamp;
}
```

### 4.3 字段映射

| MVP字段 | SDK字段 | 说明 |
|---------|---------|------|
| `createTime` | `timestamp` | 时间戳 |
| - | `companyId/departmentId/userId` | 新增组织架构 |
| - | `sceneId/capabilityId/moduleId` | 新增场景信息 |

---

## 五、集成计划

### 5.1 里程碑

| 阶段 | 内容 | 负责团队 | 预计时间 |
|------|------|----------|----------|
| Phase 1 | SDK提供Spring Boot自动配置 | SE | 0.5天 |
| Phase 2 | SDK支持趋势计算 | SE | 0.5天 |
| Phase 3 | MVP集成SDK服务 | MVP | 0.5天 |
| Phase 4 | 前端适配真实趋势 | MVP | 0.5天 |

**总计**: 2天

### 5.2 依赖关系

```
SE: 自动配置 → 趋势计算API
            ↓
MVP:       SDK集成 → Controller适配 → 前端适配
```

---

## 六、验收标准

### 6.1 SE团队交付物

- [ ] `LlmAuditAutoConfiguration` 自动配置类
- [ ] `LlmTrendStats` 趋势统计模型
- [ ] `getTrendStats()` 趋势计算方法
- [ ] 配置属性支持

### 6.2 MVP团队交付物

- [ ] 集成SDK的`LlmAuditService`
- [ ] Controller调用SDK服务
- [ ] 前端显示真实趋势数据
- [ ] 日志持久化验证

---

## 七、联系方式

- SE团队负责人: [待定]
- MVP团队负责人: [待定]
- 协作沟通渠道: [待定]

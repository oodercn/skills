# Skills团队答复补充文档

> 针对6个未明确答复问题的补充说�?> 版本: v1.0
> 日期: 2026-02-25

---

## 问题清单与答�?
### 🔴 问题7: Skill配置规范 (skill.yaml) 【P0�?
**问题**: skill.yaml完整规范未定义，影响发现服务实现

**答复**:

#### skill.yaml完整规范 (v1.0)

```yaml
# skill.yaml - Skill配置规范 v1.0
# 位置: skill/src/main/resources/skill.yaml

# ==================== 基础信息 ====================
skill:
  # 唯一标识符，格式: reverse-domain.name
  # 示例: com.ooder.skills.data-sync
  id: com.ooder.skills.example
  
  # 显示名称 (支持多语言)
  name:
    zh_CN: "示例Skill"
    en_US: "Example Skill"
  
  # 版本号，遵循SemVer规范
  version: 1.2.3
  
  # 描述信息 (支持多语言)
  description:
    zh_CN: "这是一个示例Skill，用于演示配置规�?
    en_US: "This is an example skill for demonstrating config spec"
  
  # 作者信�?  author:
    name: "Ooder Team"
    email: "team@ooder.cn"
    url: "https://gitee.com/ooderCN"
  
  # 许可�?  license: "Apache-2.0"
  
  # Skill分类
  category: "data-processing"  # 可�? data-processing, visualization, automation, integration
  
  # 标签，用于搜索和过滤
  tags:
    - "example"
    - "demo"
    - "data"

# ==================== 依赖声明 ====================
dependencies:
  # Skill依赖
  skills:
    - id: "com.ooder.skills.base"
      version: "^1.0.0"  # ^表示兼容1.x.x
      optional: false     # 是否可选依�?    
    - id: "com.ooder.skills.database"
      version: ">=2.0.0 <3.0.0"
      optional: true
  
  # 系统依赖 (检查环�?
  system:
    java: ">=1.8"           # Java版本要求
    memory: ">=512MB"       # 内存要求
    disk: ">=100MB"         # 磁盘空间要求
  
  # 外部服务依赖
  services:
    - name: "redis"
      required: false
      checkEndpoint: "/health"

# ==================== 生命周期配置 ====================
lifecycle:
  # 启动顺序优先�?(0-100, 数字越小越先启动)
  startupOrder: 50
  
  # 启动超时时间(毫秒)
  startupTimeout: 30000
  
  # 停止超时时间(毫秒)
  shutdownTimeout: 10000
  
  # 健康检查配�?  healthCheck:
    enabled: true
    interval: 30000        # 检查间�?    timeout: 5000          # 超时时间
    retries: 3             # 重试次数
    endpoint: "/health"    # 健康检查端�?  
  # 自动重启策略
  restartPolicy:
    enabled: true
    maxRetries: 3
    backoffMultiplier: 2   # 退避倍数

# ==================== 路由配置 (A2A协议) ====================
routing:
  # 基础路径
  basePath: "/api/v1/skills/${skill.id}"
  
  # 端点定义
  endpoints:
    # Agent Card端点 (必须)
    - path: "/agent-card"
      method: "GET"
      handler: "agentCardHandler"
      description: "返回Agent Card信息"
    
    # Tasks端点 (必须)
    - path: "/tasks"
      method: "POST"
      handler: "taskHandler"
      description: "处理任务请求"
    
    # 自定义端�?    - path: "/custom/action"
      method: "POST"
      handler: "customActionHandler"
      auth: true              # 需要认�?      rateLimit: "100/min"    # 限流配置

# ==================== 服务配置 ====================
services:
  # 暴露的服�?  exports:
    - name: "DataSyncService"
      interface: "com.ooder.skills.example.DataSync"
      version: "1.0.0"
  
  # 引用的服�?  imports:
    - name: "DatabaseService"
      required: true

# ==================== UI配置 ====================
ui:
  # 是否提供UI
  enabled: true
  
  # 入口�?(Web Component)
  entry: "/ui/skill-example.js"
  
  # 组件标签�?  tagName: "skill-example-ui"
  
  # 默认尺寸
  defaultSize:
    width: "800px"
    height: "600px"
  
  # 图标
  icon: "/ui/assets/icon.svg"
  
  # 主题适配
  theming:
    cssVariables: true      # 支持CSS变量
    darkMode: true          # 支持暗黑模式

# ==================== 能力声明 (A2A) ====================
capabilities:
  # 支持的能力类�?  types:
    - "task"                # 任务处理
    - "notification"        # 通知
    - "streaming"           # 流式响应
  
  # 输入/输出格式
  formats:
    input: ["text", "json", "file"]
    output: ["text", "json", "file", "stream"]
  
  # 认证方式
  auth:
    - "none"
    - "apiKey"
    - "oauth2"

# ==================== 资源限制 ====================
resources:
  # CPU限制 (相对�?
  cpu:
    limit: 0.5              # 最多使�?0%单核
    priority: "normal"      # normal, high, low
  
  # 内存限制
  memory:
    max: "256MB"
    reserved: "64MB"
  
  # 线程限制
  threads:
    max: 20
  
  # 文件句柄限制
  fileHandles:
    max: 100

# ==================== 安全配置 ====================
security:
  # 权限声明
  permissions:
    - "network:read"
    - "filesystem:read"
    - "database:write"
  
  # 沙箱配置
  sandbox:
    enabled: true
    fileSystem: "restricted"  # none, restricted, full
    network: "whitelist"      # none, whitelist, full

# ==================== 扩展配置 ====================
extensions:
  # 自定义配置项
  custom:
    batchSize: 100
    retryTimes: 3
```

#### 字段验证规则

| 字段 | 必填 | 格式 | 说明 |
|------|------|------|------|
| skill.id | �?| 反向域名格式 | 全局唯一标识 |
| skill.version | �?| SemVer | �?�?修订 |
| dependencies.skills | �?| 数组 | 依赖其他Skill |
| routing.endpoints | �?| 数组 | 至少包含agent-card |
| lifecycle.startupOrder | �?| 0-100 | 默认50 |

---

### 🟡 问题8: 依赖解析策略 【P1�?
**问题**: 依赖版本冲突如何解决？循环依赖如何处理？

**答复**:

#### 依赖解析流程

```
┌─────────────────────────────────────────────────────────────�?�?                   依赖解析流程                              �?├─────────────────────────────────────────────────────────────�?�? 1. 解析声明 �?读取skill.yaml中的dependencies                 �?�? 2. 构建依赖�?�?创建有向无环�?DAG)                          �?�? 3. 版本冲突解决 �?应用解析策略                               �?�? 4. 循环依赖检�?�?检测并阻断循环依赖                          �?�? 5. 拓扑排序 �?确定加载顺序                                   �?�? 6. 按序安装 �?并行加载无依赖的Skill                          �?└─────────────────────────────────────────────────────────────�?```

#### 版本冲突解决策略

**策略优先�?* (从高到低):

1. **显式声明优先** - 用户显式指定的版本优�?2. **最高版本优�?* - 默认策略，选择满足约束的最高版�?3. **最早声明优�?* - 按声明顺序，先声明的优先

**配置方式**:
```yaml
# 在application.yml中配�?cooder:
  skills:
    dependency:
      resolution-strategy: "highest"  # highest, explicit, first
```

#### 版本约束语法

| 语法 | 含义 | 示例 |
|------|------|------|
| `1.2.3` | 精确版本 | 必须等于1.2.3 |
| `^1.2.3` | 兼容版本 | >=1.2.3 <2.0.0 |
| `~1.2.3` | 近似版本 | >=1.2.3 <1.3.0 |
| `>=1.0.0` | 最低版�?| 1.0.0及以�?|
| `>=1.0.0 <2.0.0` | 范围版本 | 1.x.x |
| `*` | 任意版本 | 最新版�?|

#### 循环依赖处理

**检测机�?*:
```java
// 使用DFS检测循环依�?public class CircularDependencyDetector {
    public void detectCycle(DependencyGraph graph) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        for (String skillId : graph.getAllSkills()) {
            if (hasCycle(graph, skillId, visited, recursionStack)) {
                throw new CircularDependencyException(
                    "检测到循环依赖: " + skillId
                );
            }
        }
    }
}
```

**处理策略**:
1. **阻断模式** (默认) - 检测到循环依赖时抛出异常，阻止安装
2. **警告模式** - 记录警告日志，尝试打破循环（不推荐用于生产）

---

### 🟡 问题9: 热插拔事务边�?【P1�?
**问题**: 热插拔操作的事务边界如何界定？失败如何回滚？

**答复**:

#### 热插拔事务模�?
采用**两阶段提�?2PC)**模型，确保原子�?

```
┌────────────────────────────────────────────────────────────────�?�?                     热插拔事务流�?                            �?├────────────────────────────────────────────────────────────────�?�?                                                                �?�? Phase 1: 准备阶段 (Prepare)                                    �?�? ┌─────────�?   ┌──────────�?   ┌──────────�?   ┌─────────�?  �?�? �?开始事�?�?�?�?资源预留  �?�?�?依赖检�? �?�?�?预加�?  �?  �?�? └─────────�?   └──────────�?   └──────────�?   └─────────�?  �?�?      �?                                             �?        �?�?      �?                                             �?        �?�? ┌─────────�?                                 ┌─────────�?    �?�? �?准备就绪 �?←─────────────────────────────── �?状态标记│     �?�? └─────────�?                                 └─────────�?    �?�?                                                                �?�? Phase 2: 提交阶段 (Commit)                                     �?�? ┌─────────�?   ┌──────────�?   ┌──────────�?   ┌─────────�?  �?�? �?提交事务 �?�?�?注册服务  �?�?�?启动组件  �?�?�?状态更�?�?  �?�? └─────────�?   └──────────�?   └──────────�?   └─────────�?  �?�?                                                                �?�? [失败时] Phase 2': 回滚阶段 (Rollback)                         �?�? ┌─────────�?   ┌──────────�?   ┌──────────�?   ┌─────────�?  �?�? �?回滚事务 �?�?�?释放资源  �?�?�?清理状�? �?�?�?通知失败 �?  �?�? └─────────�?   └──────────�?   └──────────�?   └─────────�?  �?�?                                                                �?└────────────────────────────────────────────────────────────────�?```

#### 事务边界定义

**安装事务边界**:
```java
@Transactional(rollbackFor = HotPlugException.class)
public class SkillInstallationTransaction {
    
    // 事务包含以下操作:
    // 1. 文件解压到临时目�?    // 2. 验证skill.yaml完整�?    // 3. 检查依赖可用�?    // 4. 分配资源配额
    // 5. 注册到PluginManager
    // 6. 启动Skill生命周期
    // 7. 更新状态为RUNNING
    
    // 任一步骤失败，触发完整回�?}
```

**卸载事务边界**:
```java
@Transactional(rollbackFor = HotPlugException.class)
public class SkillUninstallationTransaction {
    
    // 事务包含以下操作:
    // 1. 停止Skill (优雅关闭)
    // 2. 注销服务
    // 3. 释放资源配额
    // 4. 从PluginManager移除
    // 5. 删除文件
    // 6. 更新状态为UNINSTALLED
}
```

#### 失败回滚机制

**补偿操作�?*:

| 阶段 | 操作 | 补偿操作 |
|------|------|----------|
| 准备 | 文件解压 | 删除临时文件 |
| 准备 | 资源预留 | 释放资源配额 |
| 提交 | 服务注册 | 注销服务 |
| 提交 | 组件启动 | 停止组件 |

**回滚实现**:
```java
public class TransactionCompensator {
    private final List<CompensableAction> actions = new ArrayList<>();
    
    public void addAction(Runnable action, Runnable compensation) {
        actions.add(new CompensableAction(action, compensation));
    }
    
    public void execute() {
        int successIndex = -1;
        try {
            for (int i = 0; i < actions.size(); i++) {
                actions.get(i).execute();
                successIndex = i;
            }
        } catch (Exception e) {
            // 执行补偿
            for (int i = successIndex; i >= 0; i--) {
                actions.get(i).compensate();
            }
            throw new TransactionRollbackException(e);
        }
    }
}
```

---

### 🟡 问题10: 资源泄漏检测范�?【P1�?
**问题**: 资源泄漏检测覆盖哪些资源类型？检测阈值如何设置？

**答复**:

#### 检测资源类�?(5级覆�?

| 级别 | 资源类型 | 检测方�?| 阈值配�?|
|------|----------|----------|----------|
| L1 | ClassLoader | 引用计数 | 卸载后引�?0告警 |
| L2 | 线程 | Thread.activeCount() | 僵尸线程>0告警 |
| L3 | 数据库连�?| DataSource监控 | 未关闭连�?0告警 |
| L4 | 文件句柄 | /proc/{pid}/fd | 句柄增长>10%/小时告警 |
| L5 | 内存映射 | MappedByteBuffer | 未释放映�?0告警 |

#### 检测实�?
```java
@Component
public class ResourceLeakDetector {
    
    // L1: ClassLoader泄漏检�?    public void detectClassLoaderLeak(String skillId) {
        ClassLoader cl = skillClassLoaders.get(skillId);
        if (cl != null) {
            int refCount = getReferenceCount(cl);
            if (refCount > 0) {
                reportLeak(skillId, "ClassLoader", refCount);
            }
        }
    }
    
    // L2: 线程泄漏检�?    public void detectThreadLeak(String skillId) {
        Set<Thread> skillThreads = threadTracker.getThreads(skillId);
        long aliveCount = skillThreads.stream()
            .filter(Thread::isAlive)
            .count();
        if (aliveCount > 0) {
            reportLeak(skillId, "Thread", (int) aliveCount);
        }
    }
    
    // L3: 数据库连接泄�?    public void detectConnectionLeak(String skillId) {
        int active = dataSourceMonitor.getActiveConnections(skillId);
        int idle = dataSourceMonitor.getIdleConnections(skillId);
        if (active + idle > 0) {
            reportLeak(skillId, "DB Connection", active + idle);
        }
    }
    
    // L4: 文件句柄泄漏
    public void detectFileHandleLeak(String skillId) {
        long current = fileHandleMonitor.getCount(skillId);
        long baseline = baselines.get(skillId);
        if (current - baseline > threshold) {
            reportLeak(skillId, "File Handle", (int)(current - baseline));
        }
    }
}
```

#### 阈值配�?
```yaml
# application.yml
cooder:
  skills:
    leak-detection:
      enabled: true
      check-interval: 30000  # 检测间�?ms)
      thresholds:
        classloader-refs: 0   # ClassLoader引用阈�?        zombie-threads: 0     # 僵尸线程阈�?        db-connections: 0     # 数据库连接阈�?        file-handles: 10      # 文件句柄增长阈�?        memory-mappings: 0    # 内存映射阈�?      actions:               # 发现泄漏后的动作
        - "log"              # 记录日志
        - "notify"           # 发送通知
        - "force-cleanup"    # 强制清理(谨慎使用)
```

---

### 🟡 问题11: 版本兼容性规�?【P1�?
**问题**: Skill版本兼容性如何定义？升级/降级策略是什么？

**答复**:

#### 兼容性规�?(基于SemVer)

| 版本变化 | 兼容�?| 自动升级 | 说明 |
|----------|--------|----------|------|
| MAJOR (x.0.0) | �?不兼�?| �?禁止 | API可能破坏性变�?|
| MINOR (0.x.0) | �?向后兼容 | �?允许 | 新增功能，兼容旧�?|
| PATCH (0.0.x) | �?完全兼容 | �?自动 | 仅bug修复 |

#### 兼容性矩�?
```
                    依赖方版�?                 1.0.0   1.1.0   2.0.0
               ┌───────┬───────┬───────�?    1.0.0      �? �?  �? �?  �? �?  �?被依赖方        ├───────┼───────┼───────�?    1.1.0      �? �?  �? �?  �? �?  �?               ├───────┼───────┼───────�?    2.0.0      �? �?  �? �?  �? �?  �?               └───────┴───────┴───────�?```

#### 升级策略

```java
public class VersionUpgradeStrategy {
    
    public UpgradeDecision canUpgrade(String current, String target) {
        Version cur = Version.parse(current);
        Version tar = Version.parse(target);
        
        // PATCH级别: 自动升级
        if (cur.getMajor() == tar.getMajor() && 
            cur.getMinor() == tar.getMinor()) {
            return UpgradeDecision.AUTO;
        }
        
        // MINOR级别: 允许升级，需通知
        if (cur.getMajor() == tar.getMajor()) {
            return UpgradeDecision.ALLOWED;
        }
        
        // MAJOR级别: 禁止自动升级
        return UpgradeDecision.BLOCKED;
    }
    
    public void performUpgrade(String skillId, String targetVersion) {
        // 1. 检查兼容�?        // 2. 备份当前版本
        // 3. 下载新版�?        // 4. 执行热替�?        // 5. 验证功能
    }
}
```

#### 降级策略

**允许降级场景**:
- PATCH级别降级: 始终允许 (回滚bug修复)
- MINOR级别降级: 需确认 (可能丢失功能)
- MAJOR级别降级: 禁止 (API不兼�?

---

### 🟡 问题13: SDK与Nexus团队接口 【P1�?
**问题**: SDK团队与Nexus团队的协作接口如何定义？

**答复**:

#### 三层接口协议

```
┌─────────────────────────────────────────────────────────────────�?�?                     三层协作接口                                �?├─────────────────────────────────────────────────────────────────�?�?                                                                 �?�? Layer 1: JavaScript API (Nexus UI �?Skill UI)                  �?�? ┌─────────────────────────────────────────────────────────�?  �?�? �? window.parent.postMessage()                            �?  �?�? �? - 主题切换通知                                          �?  �?�? �? - 布局调整通知                                          �?  �?�? �? - 消息传�?                                             �?  �?�? └─────────────────────────────────────────────────────────�?  �?�?                             �?                                  �?�? Layer 2: WebSocket (实时通信)                                   �?�? ┌─────────────────────────────────────────────────────────�?  �?�? �? /ws/skills/{skillId}                                   �?  �?�? �? - 状态变更推�?                                         �?  �?�? �? - 实时数据�?                                           �?  �?�? �? - 双向通知                                              �?  �?�? └─────────────────────────────────────────────────────────�?  �?�?                             �?                                  �?�? Layer 3: RESTful API (HTTP请求)                                �?�? ┌─────────────────────────────────────────────────────────�?  �?�? �? /api/v1/skills/{skillId}/...                           �?  �?�? �? - 配置管理                                              �?  �?�? �? - 生命周期控制                                          �?  �?�? �? - 数据查询                                              �?  �?�? └─────────────────────────────────────────────────────────�?  �?�?                                                                 �?└─────────────────────────────────────────────────────────────────�?```

#### Layer 1: JavaScript API 规范

**Nexus �?Skill (父窗口向iframe发�?**:
```javascript
// 主题切换
parent.postMessage({
    type: 'THEME_CHANGE',
    data: {
        mode: 'dark',  // 'light' | 'dark'
        cssVariables: {
            '--primary-color': '#1890ff',
            '--bg-color': '#141414'
        }
    }
}, '*');

// 布局调整
parent.postMessage({
    type: 'LAYOUT_RESIZE',
    data: {
        width: 800,
        height: 600
    }
}, '*');
```

**Skill �?Nexus (iframe向父窗口发�?**:
```javascript
// 高度自适应
window.parent.postMessage({
    type: 'RESIZE_REQUEST',
    data: {
        height: 500  // 请求的新高度
    }
}, '*');

// 打开对话�?window.parent.postMessage({
    type: 'OPEN_DIALOG',
    data: {
        title: '配置',
        url: '/skills/example/config',
        width: 600,
        height: 400
    }
}, '*');
```

#### Layer 2: WebSocket 规范

**连接URL**: `wss://{host}/ws/skills/{skillId}?token={jwt}`

**消息格式**:
```json
{
    "type": "STATE_CHANGE",
    "timestamp": 1700000000000,
    "skillId": "com.ooder.skills.example",
    "data": {
        "from": "STARTING",
        "to": "RUNNING",
        "reason": "启动完成"
    }
}
```

**消息类型**:
| 类型 | 方向 | 说明 |
|------|------|------|
| STATE_CHANGE | S→C | 状态变更通知 |
| CONFIG_UPDATE | C→S | 配置更新请求 |
| LOG_STREAM | S→C | 日志流推�?|
| HEARTBEAT | 双向 | 心跳保活 |

#### Layer 3: RESTful API 规范

**基础路径**: `/api/v1/skills/{skillId}`

| 方法 | 路径 | 说明 | 请求�?| 响应 |
|------|------|------|--------|------|
| GET | `/{skillId}/state` | 获取状�?| - | StateDTO |
| POST | `/{skillId}/start` | 启动Skill | - | Result |
| POST | `/{skillId}/stop` | 停止Skill | - | Result |
| GET | `/{skillId}/config` | 获取配置 | - | ConfigDTO |
| PUT | `/{skillId}/config` | 更新配置 | ConfigDTO | Result |
| GET | `/{skillId}/logs` | 获取日志 | query | LogDTO[] |
| GET | `/{skillId}/metrics` | 获取指标 | - | MetricsDTO |

**示例请求**:
```bash
# 获取Skill状�?curl -X GET \
  'http://localhost:8080/api/v1/skills/com.ooder.skills.example/state' \
  -H 'Authorization: Bearer {token}'

# 响应
{
    "skillId": "com.ooder.skills.example",
    "state": "RUNNING",
    "version": "1.2.3",
    "uptime": 3600000,
    "health": {
        "status": "UP",
        "checks": [...]
    }
}
```

#### 错误码规�?
| 错误�?| 说明 | 处理建议 |
|--------|------|----------|
| 4001 | Skill不存�?| 检查skillId |
| 4002 | 状态转换非�?| 检查当前状�?|
| 4003 | 配置验证失败 | 检查配置格�?|
| 5001 | 内部错误 | 查看日志 |
| 5002 | 依赖未满�?| 检查依赖Skill状�?|

---

## 实施建议

### 优先级排�?
1. **立即实施** (本周�?
   - skill.yaml规范 (问题7) - 阻塞发现服务
   - SDK与Nexus接口 (问题13) - 阻塞协作

2. **尽快实施** (下周�?
   - 依赖解析策略 (问题8)
   - 版本兼容性规�?(问题11)

3. **后续优化** (迭代�?
   - 热插拔事务边�?(问题9)
   - 资源泄漏检�?(问题10)

### 团队协作要点

- **SDK团队**: 基于skill.yaml规范实现发现服务
- **Nexus团队**: 基于三层接口协议实现UI集成
- **Skills团队**: 提供接口实现和验证工�?
---

## 附录

### A. skill.yaml JSON Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Skill Configuration",
  "type": "object",
  "required": ["skill"],
  "properties": {
    "skill": {
      "type": "object",
      "required": ["id", "version"],
      "properties": {
        "id": {
          "type": "string",
          "pattern": "^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$"
        },
        "version": {
          "type": "string",
          "pattern": "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)$"
        }
      }
    }
  }
}
```

### B. 版本号比较算�?
```java
public int compareVersion(String v1, String v2) {
    String[] parts1 = v1.split("\\.");
    String[] parts2 = v2.split("\\.");
    
    for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
        int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
        int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
        
        if (p1 != p2) {
            return Integer.compare(p1, p2);
        }
    }
    return 0;
}
```

---

**文档结束**

如有疑问，请联系Skills团队或在此文档基础上提出Issue�?
# 系统健康技能设计方案

> **文档版本**: v1.0  
> **创建日期**: 2026-03-15  
> **技能ID**: skill-system-health  
> **文档状态**: 设计方案

---

## 一、需求背景

### 1.1 问题陈述

当前系统缺乏统一的健康检查机制，无法快速诊断：
- 基础环境是否配置正确
- LLM配置是否完整有效
- 底座Skills功能是否正常运行
- 关键配置是否缺失

### 1.2 目标

创建一个系统健康技能，提供：
1. **一键诊断** - 快速检查系统各组件状态
2. **配置验证** - 验证关键配置是否完整
3. **问题定位** - 帮助用户快速定位配置问题
4. **修复建议** - 提供具体的修复建议

---

## 二、检查项设计

### 2.1 检查项清单（17项底座功能）

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        系统健康检查项（17项）                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  基础环境 (4项)                                                             │
│  ├── 1. Java运行环境                                                        │
│  ├── 2. 数据库连接                                                          │
│  ├── 3. 文件存储路径                                                        │
│  └── 4. 网络连通性                                                          │
│                                                                             │
│  LLM配置 (4项)                                                              │
│  ├── 5. LLM Provider配置                                                   │
│  ├── 6. API Key配置                                                         │
│  ├── 7. 模型可用性                                                          │
│  └── 8. 连接测试                                                            │
│                                                                             │
│  系统Skills (5项)                                                           │
│  ├── 9.  skill-common       通用工具库                                      │
│  ├── 10. skill-capability   能力管理                                        │
│  ├── 11. skill-llm          LLM集成                                         │
│  ├── 12. skill-llm-chat     LLM聊天助手                                     │
│  └── 13. skill-scene-management 场景管理                                    │
│                                                                             │
│  核心功能 (4项)                                                             │
│  ├── 14. 认证服务                                                           │
│  ├── 15. 组织管理                                                           │
│  ├── 16. 能力注册                                                           │
│  └── 17. 场景创建                                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 检查项详细定义

| 序号 | 检查项 | 类别 | 检查内容 | 默认配置要求 |
|------|--------|------|----------|--------------|
| 1 | Java运行环境 | 基础环境 | Java版本、JVM参数 | Java 8+ |
| 2 | 数据库连接 | 基础环境 | 数据库连接池状态 | H2/MySQL可用 |
| 3 | 文件存储路径 | 基础环境 | 存储目录读写权限 | data/目录可写 |
| 4 | 网络连通性 | 基础环境 | 外部API可达性 | 可访问LLM API |
| 5 | LLM Provider配置 | LLM配置 | 至少配置一个Provider | DeepSeek/OpenAI |
| 6 | API Key配置 | LLM配置 | API Key已设置 | 非空且有效格式 |
| 7 | 模型可用性 | LLM配置 | 模型列表可获取 | 至少一个模型 |
| 8 | 连接测试 | LLM配置 | 实际调用测试 | 返回正常响应 |
| 9 | skill-common | 系统Skills | 已安装并运行 | 版本 >= 2.3.1 |
| 10 | skill-capability | 系统Skills | 已安装并运行 | 版本 >= 2.3.1 |
| 11 | skill-llm | 系统Skills | 已安装并运行 | 版本 >= 2.3.1 |
| 12 | skill-llm-chat | 系统Skills | 已安装并运行 | 版本 >= 2.3.1 |
| 13 | skill-scene-management | 系统Skills | 已安装并运行 | 版本 >= 2.3.1 |
| 14 | 认证服务 | 核心功能 | 登录接口可用 | 返回200 |
| 15 | 组织管理 | 核心功能 | 组织API可用 | 返回200 |
| 16 | 能力注册 | 核心功能 | 能力CRUD可用 | 返回200 |
| 17 | 场景创建 | 核心功能 | 场景CRUD可用 | 返回200 |

---

## 三、技术设计

### 3.1 技能结构

```
skill-system-health/
├── src/main/java/net/ooder/skill/health/
│   ├── controller/
│   │   └── SystemHealthController.java    # REST API
│   ├── service/
│   │   ├── HealthCheckService.java        # 健康检查服务接口
│   │   └── impl/
│   │       └── HealthCheckServiceImpl.java # 实现类
│   ├── checker/
│   │   ├── EnvironmentChecker.java        # 环境检查器
│   │   ├── LlmConfigChecker.java          # LLM配置检查器
│   │   ├── SkillStatusChecker.java        # Skills状态检查器
│   │   └── FunctionChecker.java           # 功能检查器
│   └── model/
│       ├── HealthCheckResult.java         # 检查结果
│       ├── HealthCheckItem.java           # 检查项
│       └── HealthStatus.java              # 健康状态枚举
├── src/main/resources/
│   ├── skill.yaml                         # 技能元数据
│   └── static/console/pages/
│       └── system-health.html             # 健康检查页面
└── pom.xml
```

### 3.2 核心模型

```java
public enum HealthStatus {
    HEALTHY,      // 健康 - 所有检查通过
    WARNING,      // 警告 - 部分非关键检查失败
    ERROR,        // 错误 - 关键检查失败
    UNKNOWN       // 未知 - 无法检查
}

public class HealthCheckItem {
    private String id;              // 检查项ID
    private String name;            // 检查项名称
    private String category;        // 类别
    private HealthStatus status;    // 状态
    private String message;         // 检查消息
    private String suggestion;      // 修复建议
    private boolean required;       // 是否必需
    private long checkTime;         // 检查时间
}

public class HealthCheckResult {
    private HealthStatus overallStatus;     // 总体状态
    private int totalItems;                  // 总检查项
    private int healthyCount;                // 健康数量
    private int warningCount;                // 警告数量
    private int errorCount;                  // 错误数量
    private List<HealthCheckItem> items;     // 检查项列表
    private Date checkTime;                  // 检查时间
}
```

### 3.3 API设计

| 方法 | 端点 | 功能 |
|------|------|------|
| GET | `/api/v1/system/health/check` | 执行完整健康检查 |
| GET | `/api/v1/system/health/check/{category}` | 按类别检查 |
| GET | `/api/v1/system/health/status` | 获取快速状态 |
| GET | `/api/v1/system/health/items` | 获取检查项列表 |
| POST | `/api/v1/system/health/llm-test` | LLM连接测试 |

### 3.4 检查器实现

```java
@Component
public class EnvironmentChecker {
    
    public HealthCheckItem checkJavaRuntime() {
        HealthCheckItem item = new HealthCheckItem();
        item.setId("env-java");
        item.setName("Java运行环境");
        item.setCategory("基础环境");
        item.setRequired(true);
        
        try {
            String version = System.getProperty("java.version");
            if (version != null && version.compareTo("1.8") >= 0) {
                item.setStatus(HealthStatus.HEALTHY);
                item.setMessage("Java版本: " + version);
            } else {
                item.setStatus(HealthStatus.ERROR);
                item.setMessage("Java版本过低: " + version);
                item.setSuggestion("请升级到Java 8或更高版本");
            }
        } catch (Exception e) {
            item.setStatus(HealthStatus.ERROR);
            item.setMessage("检查失败: " + e.getMessage());
        }
        
        return item;
    }
    
    public HealthCheckItem checkDatabase() {
        // 检查数据库连接
    }
    
    public HealthCheckItem checkStorage() {
        // 检查文件存储
    }
    
    public HealthCheckItem checkNetwork() {
        // 检查网络连通性
    }
}

@Component
public class LlmConfigChecker {
    
    @Autowired
    private LlmConfigService llmConfigService;
    
    public HealthCheckItem checkProviderConfig() {
        HealthCheckItem item = new HealthCheckItem();
        item.setId("llm-provider");
        item.setName("LLM Provider配置");
        item.setCategory("LLM配置");
        item.setRequired(true);
        
        List<LlmProvider> providers = llmConfigService.getConfiguredProviders();
        
        if (providers.isEmpty()) {
            item.setStatus(HealthStatus.ERROR);
            item.setMessage("未配置任何LLM Provider");
            item.setSuggestion("请在LLM配置页面添加至少一个Provider");
        } else {
            item.setStatus(HealthStatus.HEALTHY);
            item.setMessage("已配置 " + providers.size() + " 个Provider");
        }
        
        return item;
    }
    
    public HealthCheckItem checkApiKey() {
        // 检查API Key配置
    }
    
    public HealthCheckItem checkModelAvailability() {
        // 检查模型可用性
    }
    
    public HealthCheckItem checkConnection() {
        // 检查LLM连接
    }
}

@Component
public class SkillStatusChecker {
    
    @Autowired
    private PluginManager pluginManager;
    
    public HealthCheckItem checkSkillInstalled(String skillId, String skillName) {
        HealthCheckItem item = new HealthCheckItem();
        item.setId("skill-" + skillId);
        item.setName(skillName);
        item.setCategory("系统Skills");
        item.setRequired(true);
        
        if (pluginManager.isInstalled(skillId)) {
            String version = pluginManager.getSkillVersion(skillId);
            item.setStatus(HealthStatus.HEALTHY);
            item.setMessage("已安装，版本: " + version);
        } else {
            item.setStatus(HealthStatus.ERROR);
            item.setMessage("未安装");
            item.setSuggestion("请安装 " + skillId);
        }
        
        return item;
    }
}

@Component
public class FunctionChecker {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public HealthCheckItem checkApiEndpoint(String endpoint, String name) {
        HealthCheckItem item = new HealthCheckItem();
        item.setId("func-" + endpoint.replace("/", "-"));
        item.setName(name);
        item.setCategory("核心功能");
        item.setRequired(true);
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:8080" + endpoint, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                item.setStatus(HealthStatus.HEALTHY);
                item.setMessage("接口正常");
            } else {
                item.setStatus(HealthStatus.WARNING);
                item.setMessage("接口返回: " + response.getStatusCode());
            }
        } catch (Exception e) {
            item.setStatus(HealthStatus.ERROR);
            item.setMessage("接口异常: " + e.getMessage());
            item.setSuggestion("请检查服务是否正常启动");
        }
        
        return item;
    }
}
```

---

## 四、前端页面设计

### 4.1 页面布局

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  系统健康检查                                                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 总体状态: ✅ 健康    检查时间: 2026-03-15 10:30:00    [重新检查]      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 基础环境 (4项)                                                       │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ ✅ Java运行环境     Java版本: 11.0.2                             │ │   │
│  │ │ ✅ 数据库连接       连接池状态: 正常                              │ │   │
│  │ │ ✅ 文件存储路径     路径: data/  可写                             │ │   │
│  │ │ ✅ 网络连通性       外部API可达                                   │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ LLM配置 (4项)                                                        │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ ✅ LLM Provider配置   已配置 2 个Provider                        │ │   │
│  │ │ ✅ API Key配置        DeepSeek: 已配置                           │ │   │
│  │ │ ✅ 模型可用性         可用模型: 5个                              │ │   │
│  │ │ ⚠️ 连接测试          DeepSeek连接超时   [重试]                   │ │   │
│  │ │                       建议: 检查网络或API Key是否正确             │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 系统Skills (5项)                                                     │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ ✅ skill-common        已安装，版本: 2.3.1                       │ │   │
│  │ │ ✅ skill-capability    已安装，版本: 2.3.1                       │ │   │
│  │ │ ✅ skill-llm           已安装，版本: 2.3.1                       │ │   │
│  │ │ ✅ skill-llm-chat      已安装，版本: 2.3.1                       │ │   │
│  │ │ ✅ skill-scene-management  已安装，版本: 2.3.1                   │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 核心功能 (4项)                                                       │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ ✅ 认证服务           接口正常                                   │ │   │
│  │ │ ✅ 组织管理           接口正常                                   │ │   │
│  │ │ ✅ 能力注册           接口正常                                   │ │   │
│  │ │ ✅ 场景创建           接口正常                                   │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 状态指示器

| 状态 | 图标 | 颜色 | 说明 |
|------|------|------|------|
| HEALTHY | ✅ | 绿色 | 检查通过 |
| WARNING | ⚠️ | 黄色 | 非关键问题 |
| ERROR | ❌ | 红色 | 关键问题 |
| UNKNOWN | ❓ | 灰色 | 无法检查 |

---

## 五、实现计划

### 5.1 第一阶段：核心功能（1周）

- [ ] 创建 skill-system-health 项目结构
- [ ] 实现 HealthCheckService 核心服务
- [ ] 实现 4 个检查器
- [ ] 创建 REST API
- [ ] 创建前端页面

### 5.2 第二阶段：增强功能（1周）

- [ ] 添加检查项配置化
- [ ] 添加历史检查记录
- [ ] 添加自动修复功能
- [ ] 添加定时检查功能

### 5.3 第三阶段：集成优化（持续）

- [ ] 集成到系统启动流程
- [ ] 添加健康检查API到网关
- [ ] 添加告警通知

---

## 六、skill.yaml 配置

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-system-health
  name: 系统健康检查
  version: 1.0.0
  description: 系统健康检查技能 - 检查基础环境、LLM配置、底座Skills状态
  author: Ooder Team
  license: Apache-2.0
  type: service-skill

spec:
  type: service-skill
  
  ownership: platform
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
  
  dependencies:
    - id: skill-common
      version: ">=2.3.1"
      required: true
  
  capabilities:
    - id: system-health-check
      name: 系统健康检查
      description: 检查系统各组件健康状态
      category: management
      
    - id: llm-config-check
      name: LLM配置检查
      description: 检查LLM配置是否完整
      category: management
      
    - id: skill-status-check
      name: Skills状态检查
      description: 检查底座Skills安装状态
      category: management
  
  routes:
    - path: /api/v1/system/health/check
      method: GET
      controller: net.ooder.skill.health.controller.SystemHealthController
      methodName: checkAll
      produces: application/json
      
    - path: /api/v1/system/health/check/{category}
      method: GET
      controller: net.ooder.skill.health.controller.SystemHealthController
      methodName: checkByCategory
      produces: application/json
      
    - path: /api/v1/system/health/status
      method: GET
      controller: net.ooder.skill.health.controller.SystemHealthController
      methodName: getQuickStatus
      produces: application/json
      
    - path: /api/v1/system/health/llm-test
      method: POST
      controller: net.ooder.skill.health.controller.SystemHealthController
      methodName: testLlmConnection
      produces: application/json
  
  services:
    - name: healthCheckService
      interface: net.ooder.skill.health.service.HealthCheckService
      implementation: net.ooder.skill.health.service.impl.HealthCheckServiceImpl
      singleton: true
  
  resources:
    cpu: "100m"
    memory: "128Mi"
```

---

## 七、附录

### A. 检查项默认配置

```yaml
health-check:
  items:
    - id: env-java
      name: Java运行环境
      category: 基础环境
      required: true
      defaultValue: "1.8+"
      
    - id: env-database
      name: 数据库连接
      category: 基础环境
      required: true
      defaultValue: "H2/MySQL"
      
    - id: llm-provider
      name: LLM Provider配置
      category: LLM配置
      required: true
      defaultValue: "至少一个Provider"
      
    - id: skill-common
      name: skill-common
      category: 系统Skills
      required: true
      minVersion: "2.3.1"
```

### B. 错误码定义

| 错误码 | 说明 |
|--------|------|
| HEALTH_001 | Java版本过低 |
| HEALTH_002 | 数据库连接失败 |
| HEALTH_003 | 存储路径不可写 |
| HEALTH_004 | 网络不通 |
| HEALTH_005 | LLM未配置 |
| HEALTH_006 | API Key无效 |
| HEALTH_007 | Skill未安装 |
| HEALTH_008 | 功能接口异常 |

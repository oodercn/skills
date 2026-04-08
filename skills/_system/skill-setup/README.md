# skill-setup

系统设置服务 - 提供系统初始化设置和配置功能。

## 功能特性

- **系统初始化** - 系统初始化设置
- **配置向导** - 提供配置向导
- **系统检查** - 检查系统状态
- **设置管理** - 管理系统设置

## 核心接口

### SetupController

系统设置控制器。

```java
@RestController
@RequestMapping("/api/v1/setup")
public class SetupController {
    /**
     * 检查系统状态
     */
    @GetMapping("/status")
    public SetupStatusDTO checkStatus();
    
    /**
     * 初始化系统
     */
    @PostMapping("/initialize")
    public InitializeResultDTO initialize(@RequestBody InitializeRequest request);
    
    /**
     * 完成设置
     */
    @PostMapping("/complete")
    public void completeSetup();
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/setup/status | GET | 检查系统状态 |
| /api/v1/setup/initialize | POST | 初始化系统 |
| /api/v1/setup/complete | POST | 完成设置 |

## 设置模型

### SetupStatusDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| initialized | boolean | 是否已初始化 |
| steps | List<SetupStep> | 设置步骤 |
| currentStep | String | 当前步骤 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-setup</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private SetupService setupService;

// 检查系统状态
SetupStatusDTO status = setupService.checkStatus();

// 初始化系统
InitializeRequest request = new InitializeRequest();
request.setAdminUser("admin");
request.setAdminPassword("password");
InitializeResultDTO result = setupService.initialize(request);

// 完成设置
setupService.completeSetup();
```

## 许可证

Apache-2.0

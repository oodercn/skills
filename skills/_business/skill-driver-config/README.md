# skill-driver-config

驱动配置管理服务 - 提供LLM驱动和其他驱动的配置管理功能。

## 功能特性

- **驱动配置管理** - 管理LLM、IM、ORG等驱动的配置
- **配置验证** - 验证驱动配置的有效性
- **配置同步** - 同步驱动配置到相关服务

## 核心接口

### DriverConfigController

驱动配置管理控制器。

```java
@RestController
@RequestMapping("/api/v1/config")
public class DriverConfigController {
    /**
     * 获取驱动配置列表
     */
    @GetMapping("/driver-configs")
    public List<DriverConfig> listDriverConfigs();
    
    /**
     * 获取驱动配置详情
     */
    @GetMapping("/driver-configs/{driverId}")
    public DriverConfig getDriverConfig(@PathVariable String driverId);
    
    /**
     * 更新驱动配置
     */
    @PutMapping("/driver-configs/{driverId}")
    public DriverConfig updateDriverConfig(
        @PathVariable String driverId,
        @RequestBody DriverConfig config
    );
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/config/driver-configs | GET | 获取驱动配置列表 |
| /api/v1/config/driver-configs/{driverId} | GET | 获取驱动配置详情 |
| /api/v1/config/driver-configs/{driverId} | PUT | 更新驱动配置 |
| /api/v1/drivers | GET | 获取可用驱动列表 |

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| ooder.driver-config.enabled | boolean | true | 是否启用驱动配置服务 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-driver-config</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private DriverConfigService driverConfigService;

// 获取LLM驱动配置
DriverConfig llmConfig = driverConfigService.getDriverConfig("llm-deepseek");

// 更新驱动配置
llmConfig.setProperty("apiKey", "new-api-key");
driverConfigService.updateDriverConfig(llmConfig);
```

## 许可证

Apache-2.0

# skill-config

配置管理服务 - 提供系统配置的集中管理和动态更新功能。

## 功能特性

- **配置管理** - 集中管理系统配置
- **动态更新** - 支持配置的动态更新
- **配置验证** - 验证配置的有效性
- **配置历史** - 记录配置变更历史

## 核心接口

### ConfigController

配置管理控制器。

```java
@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {
    /**
     * 获取配置
     */
    @GetMapping("/{configKey}")
    public ConfigDTO getConfig(@PathVariable String configKey);
    
    /**
     * 更新配置
     */
    @PutMapping("/{configKey}")
    public ConfigDTO updateConfig(
        @PathVariable String configKey,
        @RequestBody UpdateConfigRequest request
    );
    
    /**
     * 获取所有配置
     */
    @GetMapping
    public List<ConfigDTO> listConfigs();
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/config | GET | 获取所有配置 |
| /api/v1/config/{configKey} | GET | 获取配置 |
| /api/v1/config/{configKey} | PUT | 更新配置 |

## 配置模型

### ConfigDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| key | String | 配置键 |
| value | String | 配置值 |
| type | ConfigType | 配置类型 |
| description | String | 配置描述 |
| updatedAt | Long | 更新时间 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-config</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private ConfigService configService;

// 获取配置
ConfigDTO config = configService.getConfig("system.name");

// 更新配置
UpdateConfigRequest request = new UpdateConfigRequest();
request.setValue("OoderOS");
configService.updateConfig("system.name", request);
```

## 许可证

Apache-2.0

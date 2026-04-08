# skill-install-scene

场景安装流程管理服务 - 提供场景的安装、配置和激活流程管理。

## 功能特性

- **场景安装** - 安装新场景到系统
- **配置管理** - 管理场景的配置参数
- **激活流程** - 控制场景的激活步骤
- **依赖检查** - 检查场景依赖是否满足

## 核心接口

### SceneInstallController

场景安装控制器。

```java
@RestController
@RequestMapping("/api/v1/install")
public class SceneInstallController {
    /**
     * 安装场景
     */
    @PostMapping("/scenes")
    public InstallResult installScene(@RequestBody InstallRequest request);
    
    /**
     * 获取安装进度
     */
    @GetMapping("/scenes/{installId}/progress")
    public InstallProgress getProgress(@PathVariable String installId);
    
    /**
     * 激活场景
     */
    @PostMapping("/scenes/{sceneId}/activate")
    public ActivationResult activateScene(@PathVariable String sceneId);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/install/scenes | POST | 安装场景 |
| /api/v1/install/scenes/{installId}/progress | GET | 获取安装进度 |
| /api/v1/install/scenes/{sceneId}/activate | POST | 激活场景 |

## 安装流程

1. **准备阶段** - 检查依赖和环境
2. **配置阶段** - 配置场景参数
3. **安装阶段** - 执行安装操作
4. **激活阶段** - 激活场景功能

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-install-scene</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private SceneInstallService installService;

// 安装场景
InstallRequest request = new InstallRequest();
request.setSceneId("my-scene");
request.setConfig(config);
InstallResult result = installService.installScene(request);

// 检查安装进度
InstallProgress progress = installService.getProgress(result.getInstallId());
```

## 许可证

Apache-2.0

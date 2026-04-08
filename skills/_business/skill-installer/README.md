# skill-installer

安装器服务 - 提供技能和场景的安装、卸载、升级功能。

## 功能特性

- **技能安装** - 安装新技能到系统
- **场景安装** - 安装场景模板
- **版本管理** - 管理技能和场景的版本
- **依赖解析** - 自动解析和安装依赖

## 核心接口

### InstallerController

安装器控制器。

```java
@RestController
@RequestMapping("/api/v1/installer")
public class InstallerController {
    /**
     * 安装技能
     */
    @PostMapping("/skills/{skillId}")
    public InstallResult installSkill(@PathVariable String skillId);
    
    /**
     * 卸载技能
     */
    @DeleteMapping("/skills/{skillId}")
    public UninstallResult uninstallSkill(@PathVariable String skillId);
    
    /**
     * 升级技能
     */
    @PutMapping("/skills/{skillId}/upgrade")
    public UpgradeResult upgradeSkill(@PathVariable String skillId);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/installer/skills/{skillId} | POST | 安装技能 |
| /api/v1/installer/skills/{skillId} | DELETE | 卸载技能 |
| /api/v1/installer/skills/{skillId}/upgrade | PUT | 升级技能 |

## 安装流程

1. **依赖检查** - 检查所需依赖是否满足
2. **下载资源** - 下载技能或场景资源
3. **安装配置** - 配置安装参数
4. **注册服务** - 注册到系统服务

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-installer</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private InstallerService installerService;

// 安装技能
InstallResult result = installerService.installSkill("skill-llm-deepseek");

// 升级技能
UpgradeResult upgrade = installerService.upgradeSkill("skill-llm-deepseek", "2.0.0");
```

## 许可证

Apache-2.0

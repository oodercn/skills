# MVP 协作回复 - 系统配置管理界面

## 回复编号

**MVP-REPLY-2026-001**

## 回复日期

2026-03-20

## 原始需求

[SE协作需求 - 系统配置管理界面](./MVP-COLLABORATION-SYSTEM-CONFIG-UI.md)

---

## 一、MVP侧现有实现

### 1.1 已有页面

MVP已实现系统配置管理页面：

- **页面路径**: `/console/pages/config-system.html`
- **API路径**: `/api/v1/config/*`

### 1.2 已有功能

| 功能 | 状态 | 说明 |
|------|------|------|
| 系统级配置 | ✅ 已实现 | `ConfigController.getSystemConfig()` |
| 技能级配置 | ✅ 已实现 | `ConfigController.getSkillConfig()` |
| 场景级配置 | ✅ 已实现 | `ConfigController.getSceneConfig()` |
| 继承链查询 | ✅ 已实现 | `ConfigController.getInheritanceChain()` |
| Profile模板 | ✅ 已实现 | 前端支持 micro/small/large/enterprise |
| 配置预览 | ⚠️ 待完善 | `ConfigController.previewMergedConfig()` |
| 配置验证 | ⚠️ 待完善 | `ConfigController.validateConfig()` |

### 1.3 现有API端点

```
GET  /api/v1/config/system                    - 获取系统配置
GET  /api/v1/config/system/capabilities/{address} - 获取能力配置
PUT  /api/v1/config/system/capabilities/{address} - 更新能力配置
GET  /api/v1/config/skills/{skillId}          - 获取技能配置
PUT  /api/v1/config/skills/{skillId}          - 更新技能配置
GET  /api/v1/config/skills/{skillId}/inheritance - 获取继承链
GET  /api/v1/config/scenes/{sceneId}          - 获取场景配置
PUT  /api/v1/config/scenes/{sceneId}          - 更新场景配置
GET  /api/v1/config/scenes/{sceneId}/inheritance - 获取继承链
POST /api/v1/config/preview                   - 预览合并配置
POST /api/v1/config/validate                  - 验证配置
```

---

## 二、SE SDK 2.3.1 已提供能力

### 2.1 配置相关类

| 类 | 包路径 | 说明 |
|---|--------|------|
| `SystemConfig` | `net.ooder.scene.provider.model.config` | 系统配置模型 |
| `SceneConfigManager` | `net.ooder.scene.monitor` | 场景配置管理器 |
| `ConfigProvider` | `net.ooder.scene.provider` | 配置提供者 |
| `LayeredConfigLoader` | `net.ooder.scene.llm.config.layered` | 分层配置加载器 |
| `ConfigHotReloadService` | `net.ooder.scene.llm.config.hotreload` | 配置热重载服务 |

### 2.2 SceneConfigManager 接口

```java
public interface SceneConfigManager {
    CompletableFuture<ConfigHistory> getConfigHistory(String configId);
    CompletableFuture<Boolean> rollbackConfig(String configId, int version);
    CompletableFuture<String> exportConfig(String configId, String format);
    CompletableFuture<Boolean> importConfig(String configId, String format, String content);
}
```

### 2.3 SystemConfig 模型

```java
public class SystemConfig {
    private String environment;      // 环境标识
    private String dataPath;         // 数据路径
    private String tempPath;         // 临时路径
    private long maxMemoryMB;        // 最大内存
    private int cpuCores;            // CPU核数
    private Map<String, Object> extra; // 扩展配置
}
```

---

## 三、需求对接分析

### 3.1 SE需求 vs MVP实现对照

| SE需求 | MVP状态 | 说明 |
|--------|---------|------|
| SystemConfigService 接口 | ⚠️ 部分实现 | MVP有ConfigLoaderService，可对接SE的SceneConfigManager |
| 三级配置继承 | ✅ 已实现 | 系统→技能→场景 |
| 配置热重载 | ⚠️ 待集成 | SE有ConfigHotReloadService，MVP需要集成 |
| 配置版本历史 | ⚠️ 待集成 | SE有SceneConfigManager.getConfigHistory() |
| 配置回滚 | ⚠️ 待集成 | SE有SceneConfigManager.rollbackConfig() |
| 配置导入导出 | ⚠️ 待集成 | SE有SceneConfigManager.exportConfig/importConfig() |

### 3.2 需要SE SDK扩展的接口

**当前SE SDK 2.3.1 已满足大部分需求**，但建议增加以下接口：

```java
// 建议新增：系统级配置服务
public interface SystemConfigService {
    // 获取系统配置
    CompletableFuture<SystemConfig> getSystemConfig();
    
    // 更新系统配置
    CompletableFuture<Void> updateSystemConfig(SystemConfig config);
    
    // 获取所有技能配置
    CompletableFuture<List<SkillConfig>> getAllSkillConfigs();
    
    // 获取Profile模板列表
    CompletableFuture<List<ProfileTemplate>> getProfileTemplates();
    
    // 应用Profile模板
    CompletableFuture<Void> applyProfile(String profileName);
}
```

---

## 四、实现计划

### 4.1 Phase 1: 集成SE SDK配置服务 (MVP侧)

**工期：2天**

1. 创建 `SystemConfigSdkAdapter` 适配器
2. 集成 `SceneConfigManager` 到 `ConfigLoaderService`
3. 实现配置历史和回滚功能
4. 实现配置导入导出功能

### 4.2 Phase 2: 完善前端界面 (MVP侧)

**工期：1天**

1. 添加配置历史查看面板
2. 添加配置回滚按钮
3. 添加配置导入导出功能
4. 优化Profile模板切换

### 4.3 Phase 3: SE SDK扩展 (SE侧，可选)

**工期：2天**

如果SE SDK需要扩展 `SystemConfigService`，MVP将配合更新。

---

## 五、结论

### 5.1 当前状态

MVP已实现系统配置管理的基础功能，SE SDK 2.3.1 提供了配置历史、回滚、导入导出等能力。

### 5.2 下一步

1. MVP集成SE SDK的 `SceneConfigManager`
2. 完善前端界面的高级功能
3. 如需要，SE SDK可扩展 `SystemConfigService`

### 5.3 联系方式

**MVP团队**

---

## 六、附录

### 6.1 MVP现有文件

| 文件 | 路径 |
|------|------|
| 配置页面 | `src/main/resources/static/console/pages/config-system.html` |
| 配置控制器 | `src/main/java/net/ooder/mvp/skill/scene/config/controller/ConfigController.java` |
| 配置加载服务 | `src/main/java/net/ooder/mvp/skill/scene/config/service/ConfigLoaderService.java` |

### 6.2 SE SDK相关类

| 类 | JAR路径 |
|---|---------|
| SceneConfigManager | `net/ooder/scene/monitor/SceneConfigManager.class` |
| SystemConfig | `net/ooder/scene/provider/model/config/SystemConfig.class` |
| ConfigHotReloadService | `net/ooder/scene/llm/config/hotreload/ConfigHotReloadService.class` |

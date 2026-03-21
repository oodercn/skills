# 协作需求：Skills API 接口适配

## 一、需求背景

**项目**：MVP (mvp-core)  
**版本**：2.3.1  
**发起方**：MVP 团队  
**接收方**：SE SDK 团队  
**日期**：2026-03-20  
**状态**：✅ 已完成

---

## 二、已确认的包路径

### 2.1 skills-framework 模块

| 类 | 包路径 |
|----|--------|
| `SkillPackageManager` | `net.ooder.skills.api.SkillPackageManager` |
| `SkillRegistry` | `net.ooder.skills.api.SkillRegistry` |
| `SkillDiscoverer` | `net.ooder.skills.api.SkillDiscoverer` |
| `SkillInstaller` | `net.ooder.skills.api.SkillInstaller` |
| `SkillPackage` | `net.ooder.skills.api.SkillPackage` |
| `SkillDefinition` | `net.ooder.skills.api.SkillDefinition` |
| `InstalledSkill` | `net.ooder.skills.api.InstalledSkill` |
| `SkillPackageManagerImpl` | `net.ooder.skills.core.impl.SkillPackageManagerImpl` |
| `SkillRegistryImpl` | `net.ooder.skills.core.impl.SkillRegistryImpl` |
| `SkillInstallerImpl` | `net.ooder.skills.core.installer.SkillInstallerImpl` |
| `LocalDiscoverer` | `net.ooder.skills.core.discovery.LocalDiscoverer` |

### 2.2 scene-engine 模块

| 类 | 包路径 |
|----|--------|
| `SceneEngine` | `net.ooder.scene.core.SceneEngine` |
| `UnifiedSceneService` | `net.ooder.scene.service.UnifiedSceneService` |
| `UnifiedSceneService.SceneInfo` | `net.ooder.scene.service.UnifiedSceneService.SceneInfo` |
| `UnifiedSceneService.SkillDetail` | `net.ooder.scene.service.UnifiedSceneService.SkillDetail` |

---

## 三、已完成的集成工作

### 3.1 SdkConfiguration.java

```java
import net.ooder.skills.core.discovery.LocalDiscoverer;
import net.ooder.skills.core.impl.SkillRegistryImpl;
import net.ooder.skills.core.installer.SkillInstallerImpl;
import net.ooder.skills.core.impl.SkillPackageManagerImpl;
import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillInstaller;
import net.ooder.skills.api.SkillPackageManager;
```

### 3.2 SceneServiceImpl.java

```java
import net.ooder.scene.service.UnifiedSceneService;
import net.ooder.scene.service.UnifiedSceneService.SceneInfo;
import net.ooder.scene.service.UnifiedSceneService.SkillDetail;

@Autowired(required = false)
private UnifiedSceneService unifiedSceneService;
```

### 3.3 DiscoveryController.java

```java
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.api.InstalledSkill;
```

---

## 四、数据来源架构

| 组件 | SE SDK 数据源 | 本地 Fallback |
|------|--------------|--------------|
| `DiscoveryController` | `SkillPackageManager.discoverAll()` | `SkillIndexLoader` |
| `SceneServiceImpl` | `UnifiedSceneService.listInstalledScenes()` | 内存 Map |
| `SkillIndexLoader` | `SkillPackageManager.isInstalled()` | Mock 数据 |

---

## 五、后续行动

- [x] MVP 团队：更新 `SdkConfiguration.java`
- [x] MVP 团队：更新 `DiscoveryController.java`
- [x] MVP 团队：重构 `SceneServiceImpl` 使用 SE SDK 数据源
- [x] MVP 团队：验证编译通过
- [ ] MVP 团队：验证页面数据正确显示

---

## 六、联系方式

**MVP 团队负责人**：[待填写]  
**SE 团队负责人**：[待填写]  
**协作状态**：✅ 已完成

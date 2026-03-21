# Scene-Engine 2.3.1 API 迁移协作文档

## 背景
skill-common 2.3.1 移除了与 scene-engine 2.3.1 重复的实现，统一使用 scene-engine 提供的核心类。

## API 变更对照表

### UnifiedSceneService 接口变更

| 旧 API | 新 API | 说明 |
|--------|--------|------|
| `installSkill(skillId, options)` | `installSkill(sessionId, skillId, options)` | 需要 sessionId 参数 |
| `uninstallSkill(skillId)` | `uninstallSkill(sessionId, skillId, boolean)` | 需要 sessionId 和 purge 参数 |
| `getSkillDetail(skillId)` | `getSkillDetail(sessionId, skillId)` | 需要 sessionId 参数 |
| `listInstalledScenes()` | `getScenes(sessionId)` | 方法名和参数变更 |
| `installSkill(sceneId, null)` | `installSkill(sessionId, skillId, options)` | 参数结构变更 |

### SceneInfo 类变更

| 旧字段/方法 | 新字段/方法 | 说明 |
|-------------|-------------|------|
| `getSkillId()` | `getSceneId()` | 方法名变更 |
| `getVersion()` | 无对应 | 移除版本字段 |

### SkillDiscoverer 接口变更

| 旧 API | 新 API | 说明 |
|--------|--------|------|
| `discoverAll()` | `discover()` | 返回 `CompletableFuture<List<SkillPackage>>` |
| 返回 `List<SkillInfo>` | 返回 `List<SkillPackage>` | 返回类型变更 |

### SkillRegistry 接口变更

| 旧 API | 新 API | 说明 |
|--------|--------|------|
| `getAllSkills()` | `getInstalledSkills()` | 返回 `List<InstalledSkill>` |

## 需要修复的文件

1. **SceneServiceImpl.java**
   - 修复 `installSkill` 调用参数
   - 修复 `uninstallSkill` 调用参数
   - 修复 `getSkillDetail` 调用参数
   - 修复 `listInstalledScenes` 为 `getScenes`
   - 修复 `SceneInfo` 字段访问

2. **SeCapabilityServiceImpl.java**
   - 修复 `SkillDiscoverer` 返回类型
   - 修复 `SkillRegistry` 返回类型
   - 修复 `CapabilityStatus.INSTALLED` 为 `ENABLED`

3. **AuditServiceSdkImpl.java**
   - 修复 `AuditLogQuery` 字段设置

## 修复策略

### Session ID 获取
```java
// 方案1: 使用默认 session
String sessionId = "default";

// 方案2: 从当前上下文获取
SessionContext ctx = unifiedSceneService.getCurrentSession();
String sessionId = ctx != null ? ctx.getSessionId() : "default";
```

### SceneInfo 转换
```java
private SceneDefinitionDTO convertSceneInfoToDTO(SceneInfo info) {
    SceneDefinitionDTO dto = new SceneDefinitionDTO();
    dto.setSceneId(info.getSceneId());  // 使用 getSceneId()
    dto.setName(info.getName());
    dto.setDescription(info.getDescription());
    // dto.setVersion(info.getVersion()); // 移除，SceneInfo 无此字段
    return dto;
}
```

## 状态
- [x] SceneServiceImpl.java 修复
- [x] SeCapabilityServiceImpl.java 修复
- [x] AuditServiceSdkImpl.java 修复
- [x] 编译验证
- [x] 功能测试
- [x] SkillIndexLoader 重命名为 MvpSkillIndexLoader 避免冲突

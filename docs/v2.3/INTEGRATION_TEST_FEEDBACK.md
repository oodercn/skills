# 集成测试问题反馈 - scene-engine v2.3

**反馈日期**: 2026-03-03  
**反馈团队**: SKILL团队  
**测试环境**: skill-scene v2.3 + scene-engine v2.3 + ooder-sdk v2.3

---

## 一、问题概述

在skill-scene与scene-engine v2.3集成测试过程中，发现以下问题需要ENGINE团队协助解决：

| 序号 | 问题类型 | 严重程度 | 状态 |
|------|----------|----------|------|
| 1 | SceneEngineImpl有@Component注解 | 高 | ✅ **已解决** |
| 2 | JDSConfig默认配置缺失 | 高 | ⚠️ **部分解决** |
| 3 | JDSServerSceneConfiguration加载顺序 | 高 | ✅ **已解决** |
| 4 | SceneEngineAutoConfiguration.initJDSConfig()内部调用问题 | 高 | **阻塞** |
| 5 | 方法签名不一致 | 中 | 待解决 |
| 6 | API文档不明确 | 低 | 待解决 |

---

## 二、问题详情

### 问题1: SceneEngineImpl有@Component注解 ✅ 已解决

**严重程度**: 高 - **已解决**

**问题描述**:
`SceneEngineImpl` 被标记为 `@Component`，导致Spring自动扫描并尝试创建它。但根据二次开发指南，`SceneEngine` 应该通过JDSServer获取代理，不应该直接注入。

**解决方案**:
ENGINE团队已移除 `SceneEngineImpl` 的 `@Component` 注解。

**验证结果**:
```
# 更新后字节码检查 - 无@Component注解
javap -v SceneEngineImpl.class | Select-String -Pattern "Component"
# (无输出)
```

---

### 问题2: JDSConfig默认配置缺失 ⚠️ 部分解决

**严重程度**: 高 - **部分解决**

**问题描述**:
`JDSConfig` 在静态初始化阶段需要配置，但没有提供合理的默认值，导致应用启动时抛出 `NullPointerException`。

**ENGINE团队修复**:
- ✅ 添加 `initJDSConfig()` 方法
- ✅ 自动创建 JDSHome 目录结构
- ✅ 自动创建 engine_config.xml

**验证结果**:
- ✅ JDSHome路径正确找到
- ⚠️ 但 `JDSServerSceneConfiguration` 在 `initJDSConfig()` 之前执行
- ❌ 应用仍无法启动

---

### 问题3: JDSServerSceneConfiguration加载顺序问题 ⚠️ 需进一步修复

**严重程度**: 高 - **需进一步修复**

**问题描述**:
`JDSServerSceneConfiguration` 有 `@Configuration` 注解，被Spring扫描并初始化。但它的 `@PostConstruct` 方法在 `SceneEngineAutoConfiguration.initJDSConfig()` 之前执行，导致 `JDSConfig` 未初始化就被使用。

**ENGINE团队修复**:
1. **SceneEngineAutoConfiguration** 添加了 `initJDSConfig()` 方法
2. **JDSServerSceneConfiguration** 仍在scene-engine中

**验证结果**:
- ✅ `initJDSConfig()` 方法存在
- ❌ `JDSServerSceneConfiguration.registerSceneEngineToJDSServer()` 先于 `initJDSConfig()` 执行
- ❌ 应用无法启动

**错误日志**:
```
Error creating bean with name 'JDSServerSceneConfiguration': Invocation of init method failed
Caused by: java.lang.NullPointerException
    at net.ooder.config.JDSConfig$Config.currServerHome(JDSConfig.java:267)
```

**建议修复方案**:
```java
// 方案1: 添加@DependsOn
@Configuration
@DependsOn("sceneEngineAutoConfiguration")
public class JDSServerSceneConfiguration {
    // ...
}

// 方案2: 合并到SceneEngineAutoConfiguration
// 将registerSceneEngineToJDSServer()移到SceneEngineAutoConfiguration中
// 在initJDSConfig()之后执行
```

---

### 问题4: GiteeDiscoverer.discoverSkills() 方法签名不一致

**严重程度**: 中

**问题描述**:
- `GitHubDiscoverer` 和 `GiteeDiscoverer` 的方法签名不一致
- GitHub版本接受 `(owner, repo)` 两个参数
- Gitee版本只接受 `(owner)` 一个参数

**代码对比**:
```java
// GitHubDiscoverer - 可以指定仓库
List<SkillPackage> packages = gitHubDiscoverer.discoverSkills(owner, repo).get();

// GiteeDiscoverer - 只能发现owner下所有仓库
List<SkillPackage> packages = giteeDiscoverer.discoverSkills(owner).get();
```

**影响范围**:
- 无法精确发现Gitee上的特定仓库
- 与GitHub发现接口不一致，增加调用复杂度

**建议方案**:
```java
// 统一方法签名
public CompletableFuture<List<SkillPackage>> discoverSkills(String owner, String repo);
public CompletableFuture<List<SkillPackage>> discoverSkills(String owner); // 发现owner下所有仓库
```

---

### 问题5: DiscoveryResult.getSkills() 方法命名不明确

**严重程度**: 低

**问题描述**:
- `UnifiedSceneService.DiscoveryResult` 的方法名在文档中不明确
- 实际方法名是 `getSkills()`，但直觉上会尝试 `getSkillInfos()` 或 `getSkillsInfos()`

**错误示例**:
```java
// 编译错误 - 方法不存在
discoveryResult.getSkillsInfos();
discoveryResult.getSkillInfos();

// 正确方法
discoveryResult.getSkills();
```

**建议方案**:
- 更新API文档，明确方法名
- 或考虑添加别名方法提高可发现性

---

### 问题4: scene-engine与SDK职责边界模糊

**严重程度**: 中

**问题描述**:
根据二次开发指南，应用层应该只依赖scene-engine，不直接依赖SDK底层接口。但当前实现中：

| 服务 | 来源 | 应用层是否应直接使用 |
|------|------|---------------------|
| `UnifiedSceneService` | scene-engine | ✅ 推荐 |
| `GiteeDiscoverer` | sdk | ❌ 不推荐 |
| `GitHubDiscoverer` | sdk | ❌ 不推荐 |
| `SkillPackageManager` | skills-api | ✅ 推荐 |

**当前状态**:
- ✅ `UnifiedSceneService` 已自动配置
- ⚠️ `JDSServerSceneConfiguration` 加载顺序问题待解决

**建议方案**:
1. ✅ scene-engine已自动配置 `UnifiedSceneService` Bean
2. ✅ `UnifiedSceneService` 内部封装SDK的Discoverer
3. 应用层只依赖scene-engine提供的统一接口

---

## 三、架构建议

### 3.1 推荐的服务层次结构

```
┌─────────────────────────────────────────────────────────┐
│                    应用层 (skill-scene)                   │
│  GitDiscoveryController                                  │
│  ├─ @Autowired UnifiedSceneService  ← 推荐使用           │
│  └─ @Autowired SkillPackageManager                        │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                   scene-engine (v2.3)                    │
│  UnifiedSceneService                                     │
│  ├─ discoverSkills(owner, repo, options)                 │
│  ├─ installSkill(skillId)                                │
│  └─ getSkillDetail(skillId)                              │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                     ooder-sdk (v2.3)                     │
│  GiteeDiscoverer / GitHubDiscoverer / SkillPackageManager│
│  (应用层不应直接依赖)                                      │
└─────────────────────────────────────────────────────────┘
```

### 3.2 当前临时解决方案

在ENGINE团队修复前，skill-scene使用以下临时方案：

```java
// GitDiscoveryController.java
if (unifiedSceneService != null) {
    // 优先使用UnifiedSceneService（推荐）
    unifiedSceneService.discoverSkills(owner, repo, options);
} else if (giteeDiscoverer != null) {
    // 回退到SDK底层（临时方案，不推荐）
    giteeDiscoverer.discoverSkills(owner);
}
```

---

## 四、测试验证

### 4.1 测试命令

```bash
# 测试Gitee发现API
curl -X POST http://localhost:8084/api/v1/discovery/gitee \
  -H "Content-Type: application/json" \
  -d '{"repoUrl": "https://gitee.com/ooderCN/skills", "branch": "master"}'
```

### 4.2 当前测试结果

**当前状态**: ✅ 所有问题已修复，等待最终验证

**ENGINE团队修复内容**:
- ✅ SceneEngineImpl 移除 @Component 注解
- ✅ CapRouter 移除 @Component 注解
- ✅ UnifiedSceneServiceImpl 移除 @Service 注解
- ✅ JDSConfig 添加自动配置和默认值
- ✅ 自动创建 JDSHome 目录结构
- ✅ 自动创建 engine_config.xml
- ✅ JDSServerSceneConfiguration 加载顺序修复

---

## 五、ENGINE团队响应

### 5.1 已完成修复

| 问题 | 修复内容 | 状态 |
|------|----------|------|
| SceneEngineImpl @Component | 移除 @Component 和 @Autowired 注解 | ✅ 已修复 |
| CapRouter @Component | 移除 @Component 和 @Autowired 注解 | ✅ 已修复 |
| UnifiedSceneServiceImpl @Service | 移除 @Service 和 @Autowired 注解 | ✅ 已修复 |
| JDSConfig 默认值 | 添加 initJDSConfig() 方法 | ⚠️ 部分修复 |
| 目录结构自动创建 | 自动创建 JDSHome 目录结构 | ✅ 已修复 |
| 默认配置文件 | 自动创建 engine_config.xml | ✅ 已修复 |
| JDSServer 注册 | 加载顺序问题 | ❌ 需进一步修复 |

### 5.2 待解决问题

**JDSServerSceneConfiguration加载顺序**（P0 - 阻塞）

当前状态：
- `JDSServerSceneConfiguration` 在 `SceneEngineAutoConfiguration.initJDSConfig()` 之前执行
- 导致 `JDSConfig.currServerHome()` 返回 null

建议修复：
```java
@Configuration
@DependsOn("sceneEngineAutoConfiguration")
public class JDSServerSceneConfiguration {
    // ...
}
```

### 5.3 新增功能

1. **SecureSceneEngineProxy**: 安全的 SceneEngine 代理，强制通过 JDSServer 获取
2. **SceneEngineHolder**: 单例模式管理 SceneEngine 实例
3. **ClusterClient.registerApplication()**: 支持动态注册应用到 JDSServer
4. **开箱即用**: 引入依赖后自动完成所有配置

### 5.4 版本信息

- **版本**: scene-engine 2.3
- **Maven 坐标**: `net.ooder:scene-engine:2.3`
- **安装位置**: `D:\maven\.m2\repository\net\ooder\scene-engine\2.3\`

### 5.5 使用方式（改进后）

```java
// 1. 引入依赖（pom.xml）
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>

// 2. 直接使用（无需任何配置）
@Autowired
private UnifiedSceneService unifiedSceneService;

// 或通过 JDSServer 获取代理
JDSSessionHandle sessionHandle = JDSServer.getInstance().connect(clientService);
SecureSceneEngineProxy proxy = (SecureSceneEngineProxy) JDSServer.getInstance()
    .getJDSClientService(sessionHandle, ConfigCode.fromType("scene"));
```

---

## 六、待解决问题

### 6.1 JDSConfig.currServerHome()返回null（P0 - 阻塞）⚠️ **需ENGINE修复**

**问题描述**: `ooder-common-client` 中的 `JDSConfig$Config.currServerHome()` 返回 null，即使 `SceneEngineAutoConfiguration.initJDSConfig()` 已经设置了 `System.setProperty("JDSHome", "./JDSHome")`。

**错误日志**:
```
Caused by: java.lang.NullPointerException: null
    at net.ooder.config.JDSConfig$Config.currServerHome(JDSConfig.java:267)
    at net.ooder.config.JDSConfig$Config.configPath(JDSConfig.java:276)
    at net.ooder.common.CommonConfig.init(CommonConfig.java:126)
    ...
    at net.ooder.server.JDSServer.<clinit>(JDSServer.java:101)
```

**问题分析**:
1. `SceneEngineAutoConfiguration.initJDSConfig()` 设置了 `System.setProperty("JDSHome", "./JDSHome")`
2. `initJDSConfig()` 最后调用 `registerSceneEngineToJDSServer()`
3. `registerSceneEngineToJDSServer()` 触发 `JDSServer` 静态初始化
4. `JDSServer` 静态初始化调用 `LogFactory.getLog()`
5. `LogFactory` 调用 `CommonConfig.init()`
6. `CommonConfig.init()` 调用 `JDSConfig$Config.configPath()`
7. `JDSConfig$Config.configPath()` 调用 `currServerHome()`
8. `currServerHome()` 返回 null（没有正确读取 `System.getProperty("JDSHome")`）

**根因**:
`JDSConfig$Config.currServerHome()` 没有正确读取 `System.getProperty("JDSHome")`，可能是因为:
- `currServerHome()` 读取的是其他配置源（如 `UserBean.getInstance().getConfigName()`）
- 没有回退到读取 `System.getProperty("JDSHome")`

**建议修复**:
```java
// JDSConfig.java - 建议修复
public static String currServerHome() {
    // 先检查系统属性
    String jdsHome = System.getProperty("JDSHome");
    if (jdsHome != null) {
        return jdsHome;
    }
    // 再检查其他配置源
    // ...
}
```

**影响范围**:
- **应用无法启动**
- 阻塞所有功能测试

**需要ENGINE团队修复 `ooder-common-client` 中的 `JDSConfig` 问题！**

---

### 6.2 方法签名不一致（P2）

| 接口 | 方法签名 | 建议 |
|------|----------|------|
| GitHubDiscoverer | `discoverSkills(owner, repo)` | - |
| GiteeDiscoverer | `discoverSkills(owner)` | 添加repo参数 |

### 6.3 API文档完善（P3）

- 提供完整的scene-engine API文档
- UnifiedSceneService使用示例

---

## 七、联系方式

- **反馈团队**: SKILL团队
- **相关文档**: 
  - [SDK_INJECTION_SECONDARY_DEVELOPMENT_GUIDE.md](file:///E:/github/ooder-sdk/agent-sdk/docs/SDK_INJECTION_SECONDARY_DEVELOPMENT_GUIDE.md)
  - [SCENE-ENGINE-SPEC.md](file:///e:/github/ooder-skills/docs/v2.3/SCENE-ENGINE-SPEC.md)
  - [JDS_CONFIG_SOLUTION.md](file:///E:/github/ooder-sdk/scene-engine/JDS_CONFIG_SOLUTION.md) ← ENGINE团队提供
- **测试代码**: [GitDiscoveryController.java](file:///e:/github/ooder-skills/skills/skill-scene/src/main/java/net/ooder/skill/scene/controller/GitDiscoveryController.java)

---

**文档版本**: v1.6  
**最后更新**: 2026-03-03 12:25  
**ENGINE团队**: P0问题需进一步修复

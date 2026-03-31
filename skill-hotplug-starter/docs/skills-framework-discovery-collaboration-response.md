# skills-framework 发现服务协作回复

## 问题确认

感谢应用团队的详细反馈。针对文档 `E:\apex\os\docs\skills-framework-discovery-collaboration-request.md` 中提出的问题，已进行分析和修复。

---

## 问题分析

### 问题 1: LocalDiscoverer 只支持 skill.json 格式

**根本原因**: `skills-framework` 中的 `LocalDiscoverer` 是外部依赖，硬编码了 `skill.json` 文件名。

**解决方案**: 在 `skill-common` 模块中提供 `LocalSkillDiscoverer` 实现，支持多种格式。

### 问题 2: GitRepositoryDiscovererAdapter 返回空列表

**根本原因**: 
1. 缺少 Gitee/GitHub Token（虽然公开仓库不需要 Token，但 API 有速率限制）
2. Git 仓库中可能没有正确的索引文件结构

**解决方案**: 建议使用 `skill-index` 索引文件作为主要发现方式，Git 发现作为补充。

---

## 修复内容

### 1. 新增 LocalSkillDiscoverer

**文件**: `net.ooder.skill.common.discovery.LocalSkillDiscoverer`

**位置**: `D:\maven\.m2\repository\net\ooder\skill-common\3.0.1\skill-common-3.0.1.jar`

**特性**:
- 支持 `skill.yaml`、`skill.yml`、`skill.json` 三种格式
- 支持多路径搜索（可配置）
- 支持 `src/main/resources` 目录下的配置文件
- 自动解析 metadata 和 spec 配置

```java
public class LocalSkillDiscoverer implements SkillDiscoverer {

    private static final String[] SKILL_FILE_NAMES = {"skill.yaml", "skill.yml", "skill.json"};

    @Override
    public CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request) {
        // 扫描多个目录，查找 skill.yaml/skill.yml/skill.json 文件
    }
}
```

### 2. 更新 DiscoveryOrchestrator

**文件**: `net.ooder.skill.common.discovery.DiscoveryOrchestrator`

自动注册 `LocalSkillDiscoverer`:

```java
@PostConstruct
public void init() {
    if (localSkillDiscoverer != null && localSkillDiscoverer.isAvailable()) {
        registerDiscoverer(DiscoveryMethod.LOCAL, localSkillDiscoverer);
    }
}
```

### 3. 自动配置更新

**文件**: `net.ooder.skill.common.config.SkillCommonAutoConfiguration`

```java
@Bean
@ConditionalOnMissingBean
@ConditionalOnProperty(name = "ooder.discovery.local.enabled", havingValue = "true", matchIfMissing = true)
public LocalSkillDiscoverer localSkillDiscoverer() {
    return new LocalSkillDiscoverer();
}
```

---

## 使用方式

### 1. 配置搜索路径

```yaml
ooder:
  discovery:
    local:
      enabled: true
  skills:
    path: ./skills
    search-paths: ./skills,./.ooder/downloads,./.ooder/installed
```

### 2. 使用 DiscoveryOrchestrator

```java
@Autowired
private DiscoveryOrchestrator discoveryOrchestrator;

public List<CapabilityDTO> discoverLocalSkills() {
    DiscoveryRequest request = DiscoveryRequest.forLocal();
    DiscoveryResult result = discoveryOrchestrator.discover(request).join();
    return result.getCapabilities();
}
```

### 3. 多层发现策略（推荐）

```java
// 第一层: 从 skill-index 索引加载
List<CapabilityDTO> indexSkills = skillIndexLoader.getAllCapabilities("INDEX");

// 第二层: 从本地文件系统发现
if (indexSkills.isEmpty()) {
    DiscoveryResult localResult = discoveryOrchestrator.discoverLocal();
}

// 第三层: 从 Git 仓库发现（需要 Token）
if (useGitDiscovery && gitToken != null) {
    // ...
}
```

---

## GitRepositoryDiscovererAdapter 返回空列表的原因

### 可能原因

1. **缺少 Token**: 虽然公开仓库不需要 Token，但 Gitee API 有速率限制
2. **API 限流**: Gitee API 对匿名请求有严格限制
3. **索引文件不存在**: Git 仓库中可能没有 `skill-index` 目录

### 解决方案

1. **配置 Gitee Token**:
```yaml
scene:
  engine:
    discovery:
      gitee:
        token: ${GITEE_TOKEN}
        default-owner: ooderCN
        default-repo: skills
```

2. **使用 skill-index 索引**: 确保 Git 仓库中有 `skill-index` 目录

3. **使用 UnifiedDiscoveryService**: 如果使用 `scene-engine` 依赖，可以使用 `UnifiedDiscoveryService`

---

## 本地仓库安装位置

| 组件 | 版本 | 本地仓库路径 |
|------|------|-------------|
| skill-common | 3.0.1 | `D:\maven\.m2\repository\net\ooder\skill-common\3.0.1\skill-common-3.0.1.jar` |
| skill-hotplug-starter | 3.0.1 | `D:\maven\.m2\repository\net\ooder\skill-hotplug-starter\3.0.1\skill-hotplug-starter-3.0.1.jar` |

---

## 修改文件清单

| 文件路径 | 修改内容 |
|---------|---------|
| `net/ooder/skill/common/discovery/LocalSkillDiscoverer.java` | 新增：支持 skill.yaml/yml/json 多格式的本地发现器 |
| `net/ooder/skill/common/discovery/DiscoveryOrchestrator.java` | 更新：自动注册 LocalSkillDiscoverer |
| `net/ooder/skill/common/config/SkillCommonAutoConfiguration.java` | 更新：添加 LocalSkillDiscoverer Bean 定义 |

---

## 后续建议

1. **统一使用 skill.yaml 格式**: 建议所有 Skills 都使用 `skill.yaml` 格式
2. **维护 skill-index 索引**: 在 Git 仓库中维护 `skill-index` 目录，便于快速发现
3. **配置 Gitee/GitHub Token**: 如果需要从 Git 发现，建议配置 Token
4. **使用 DiscoveryOrchestrator**: 推荐使用 `DiscoveryOrchestrator` 进行统一发现管理

---

## 回复应用团队的问题

### 问题 1: LocalDiscoverer 是否计划支持 skill.yaml 格式？

**回复**: 已在 `skill-common` 模块中提供 `LocalSkillDiscoverer` 实现，支持 `skill.yaml`、`skill.yml`、`skill.json` 三种格式。

### 问题 2: GitRepositoryDiscovererAdapter 返回空列表的原因是什么？

**回复**: 
1. 缺少 Gitee Token（虽然公开仓库不需要，但 API 有限流）
2. Git 仓库中可能没有正确的索引文件结构
3. 建议使用 `skill-index` 索引文件作为主要发现方式

### 问题 3: 是否有 UnifiedDiscoveryService 的实现可供使用？

**回复**: `UnifiedDiscoveryService` 在 `scene-engine` 模块中实现。如果应用依赖了 `scene-engine`，可以直接使用。

### 问题 4: 是否需要应用团队提供更多信息？

**回复**: 不需要。已根据文档中的信息完成修复。

---

**文档创建时间**: 2026-03-31  
**文档版本**: 1.0  
**回复团队**: ooder skills 团队

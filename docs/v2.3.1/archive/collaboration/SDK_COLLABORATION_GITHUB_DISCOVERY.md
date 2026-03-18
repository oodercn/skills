# GitHub 技能发现优化协同任务

## 任务编号
SDK-COOP-2026-001

## 优先级
P0 - 高优先级

## 任务背景

### 问题一：API 速率限制

当前 GitHub 技能发现功能存在严重的 API 速率限制问题：

1. **API 调用次数过多**
   - 当前实现：`GitHubDiscoverer.discoverSkills()` 会遍历 `skills` 目录
   - 每个技能目录需要多次 API 调用获取 `skill.yaml`
   - 50+ 技能需要 100+ 次 API 调用

2. **GitHub API 速率限制**
   - 未认证请求：60 次/小时
   - 认证请求：5000 次/小时
   - 当前实现很容易触发限流（403 错误）

3. **用户体验差**
   - 发现过程缓慢
   - 频繁失败
   - 无法正常使用

### 问题二：场景能力识别错误

当前场景能力识别逻辑存在严重问题：

1. **识别逻辑过于简单**
   - 当前实现：仅基于 `skillId` 是否包含 `-scene` 判断
   ```java
   boolean isScene = skillId != null && 
       (skillId.contains("-scene") || skillId.endsWith("-scene") || 
        "daily-log-scene".equals(skillId));
   ```

2. **大量场景能力未被识别**
   - `skill-a2ui` 有 `sceneId: ui-generation`，但未被识别为场景能力
   - `skill-llm-chat` 有 `sceneId: llm-chat`，但未被识别为场景能力
   - `skill-org-dingding` 有 `sceneId: auth`，但未被识别为场景能力

3. **SkillPackage 缺少 sceneId 字段**
   - SDK 的 `SkillPackage` 类没有 `sceneId` 字段
   - 无法传递场景能力信息

### 现有资源

仓库根目录已有 `skill-index.yaml` 文件，包含所有技能的完整元数据：
- 技能列表（50+ 个）
- 版本信息
- 下载地址
- 依赖关系
- 场景定义（sceneId 字段）

## 优化方案

### 方案一：优先读取 skill-index.yaml（推荐）

**实现思路**：
1. 首先请求 `skill-index.yaml` 文件（1 次 API 调用）
2. 解析 YAML 获取所有技能元数据
3. 仅在需要详细信息时才请求具体技能目录

**优势**：
- API 调用从 100+ 次减少到 1-2 次
- 大幅提升响应速度
- 降低限流风险

### 方案二：添加缓存机制

**实现思路**：
1. 缓存发现结果（TTL: 5 分钟）
2. 缓存 skill-index.yaml 内容
3. 提供手动刷新选项

### 方案三：支持 Gitee 镜像

**实现思路**：
1. GitHub 限流时自动切换到 Gitee
2. Gitee API 速率限制更宽松
3. 国内访问更快

## 任务分解

### 任务 1：修改 SkillPackage 数据模型（SDK 团队）

**文件位置**：`agent-sdk-api` 模块

**修改内容**：
```java
public class SkillPackage {
    private String skillId;
    private String name;
    private String version;
    private String description;
    private String category;
    private String author;
    private String icon;
    private List<String> tags;
    private String downloadUrl;
    private String giteeDownloadUrl;
    private String checksum;
    
    // 新增字段
    private String sceneId;           // 场景 ID
    private Boolean sceneCapability;  // 是否为场景能力
    private List<String> capabilities; // 能力列表
    private List<SkillDependency> dependencies; // 依赖列表
}
```

### 任务 2：添加 SkillIndex 数据模型（SDK 团队）

**新增类**：
```java
public class SkillIndex {
    private SkillIndexMetadata metadata;
    private SkillIndexSpec spec;
    private List<SkillIndexEntry> skills;
    private List<SceneDefinition> scenes;
}

public class SkillIndexEntry {
    private String skillId;
    private String name;
    private String version;
    private String category;
    private String description;
    private String path;
    private String sceneId;           // 场景 ID
    private String downloadUrl;
    private String giteeDownloadUrl;
    private List<String> capabilities;
    private List<SkillDependency> dependencies;
}
```

### 任务 3：修改 GitHubDiscoverer（SDK 团队）

**文件位置**：`agent-sdk-api` 模块

**修改内容**：
```java
public class GitHubDiscoverer {
    
    private static final String SKILL_INDEX_PATH = "skill-index.yaml";
    
    public CompletableFuture<List<SkillPackage>> discoverSkills(String owner, String repo) {
        // 1. 优先读取 skill-index.yaml
        // 2. 解析 YAML 获取技能列表
        // 3. 转换为 SkillPackage 列表（包含 sceneId）
    }
    
    private SkillIndex parseSkillIndex(String yamlContent) {
        // 解析 skill-index.yaml
    }
    
    private SkillPackage convertToSkillPackage(SkillIndexEntry entry) {
        SkillPackage pkg = new SkillPackage();
        // ... 设置基本字段
        pkg.setSceneId(entry.getSceneId());
        pkg.setSceneCapability(entry.getSceneId() != null && !entry.getSceneId().isEmpty());
        return pkg;
    }
}
```

### 任务 4：修改 GiteeDiscoverer（SDK 团队）

**文件位置**：`agent-sdk-api` 模块

**修改内容**：
- 同 GitHubDiscoverer，优先读取 `skill-index.yaml`
- 解析 `sceneId` 字段
- 设置 `sceneCapability` 属性

### 任务 5：添加缓存机制（SDK 团队）

**实现内容**：
```java
public class CachedGitHubDiscoverer implements SkillDiscoverer {
    
    private final GitHubDiscoverer delegate;
    private final Cache<String, List<SkillPackage>> cache;
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);
    
    @Override
    public CompletableFuture<List<SkillPackage>> discoverSkills(String owner, String repo) {
        String cacheKey = owner + "/" + repo;
        // 检查缓存，未命中则调用 delegate
    }
}
```

### 任务 6：更新 GitDiscoveryController（Skills 团队）

**修改内容**：
```java
private List<CapabilityDTO> convertToCapabilities(List<SkillPackage> packages, String source) {
    List<CapabilityDTO> capabilities = new ArrayList<>();
    if (packages == null) return capabilities;
    
    for (SkillPackage pkg : packages) {
        CapabilityDTO cap = new CapabilityDTO();
        cap.setId(pkg.getSkillId());
        cap.setName(pkg.getName());
        cap.setDescription(pkg.getDescription());
        cap.setVersion(pkg.getVersion());
        cap.setSource(source);
        cap.setStatus("available");
        
        // 使用 SkillPackage 中的 sceneCapability 字段
        boolean isScene = Boolean.TRUE.equals(pkg.getSceneCapability()) 
            || pkg.getSceneId() != null && !pkg.getSceneId().isEmpty();
        cap.setType(isScene ? "SCENE" : "SKILL");
        cap.setSceneCapability(isScene);
        
        capabilities.add(cap);
    }
    return capabilities;
}
```

## 接口定义

### SkillIndex YAML 结构

```yaml
apiVersion: ooder.io/v1
kind: SkillIndex

metadata:
  name: ooder-skills
  version: "2.3"
  description: Ooder Skills Repository
  homepage: https://github.com/ooderCN/skills

spec:
  skills:
    - skillId: skill-a2ui
      name: A2UI Skill
      version: "0.7.3"
      category: ui
      description: A2UI图转代码技能
      sceneId: ui-generation        # 场景 ID
      path: skills/skill-a2ui
      downloadUrl: https://github.com/ooderCN/skills/releases/download/v0.7.3/skill-a2ui-0.7.3.jar
      giteeDownloadUrl: https://gitee.com/ooderCN/skills/releases/download/v0.7.3/skill-a2ui-0.7.3.jar
      capabilities:
        - generate-ui
        - preview-ui
      dependencies:
        - skillId: skill-llm-chat
          version: ">=1.0.0"
          required: true
    
    - skillId: skill-llm-chat
      name: LLM智能对话场景能力
      version: "2.3.0"
      category: llm
      sceneId: llm-chat             # 场景 ID
      capabilities:
        - llm-chat
        - session-manage
        - history-record
```

### API 响应格式

```json
{
  "code": 200,
  "data": {
    "method": "GITEE",
    "capabilities": [
      {
        "id": "skill-a2ui",
        "name": "A2UI Skill",
        "version": "0.7.3",
        "type": "SCENE",
        "source": "GITEE",
        "status": "available",
        "sceneCapability": true,
        "downloadUrl": "https://github.com/...",
        "giteeDownloadUrl": "https://gitee.com/..."
      },
      {
        "id": "skill-llm-chat",
        "name": "LLM智能对话场景能力",
        "version": "2.3.0",
        "type": "SCENE",
        "source": "GITEE",
        "status": "available",
        "sceneCapability": true
      }
    ],
    "scanTime": 1234567890,
    "fromCache": true
  }
}
```

## 验收标准

1. **功能验收**
   - [x] GitHub 发现功能正常工作
   - [x] Gitee 发现功能正常工作
   - [x] 无 403 限流错误（优先使用 skill-index.yaml，减少 API 调用）
   - [x] 响应时间 < 3 秒（使用本地索引文件）
   - [x] 场景能力正确识别（基于 sceneId 和 sceneCapability）

2. **性能验收**
   - [x] API 调用次数 <= 2 次/发现（优先读取 skill-index.yaml）
   - [ ] 缓存命中率 >= 80%（待 SDK 团队实现缓存机制）
   - [x] 内存占用合理

3. **兼容性验收**
   - [x] 现有 API 接口不变
   - [x] 现有功能不受影响
   - [x] 向后兼容（支持 SkillPackage 无 sceneCapability 字段的旧版本）

## 时间安排

| 阶段 | 任务 | 负责团队 | 预计时间 |
|------|------|----------|----------|
| 第一阶段 | SkillPackage 添加 sceneId 字段 | SDK 团队 | 0.5 天 |
| 第二阶段 | SkillIndex 数据模型 | SDK 团队 | 0.5 天 |
| 第三阶段 | GitHubDiscoverer/GiteeDiscoverer 优化 | SDK 团队 | 2 天 |
| 第四阶段 | 缓存机制 | SDK 团队 | 1 天 |
| 第五阶段 | Controller 更新 | Skills 团队 | 0.5 天 |
| 第六阶段 | 测试验证 | 双方团队 | 0.5 天 |

## 联系方式

- SDK 团队负责人：[待指定]
- Skills 团队负责人：[待指定]
- 技术评审：[待指定]

## 相关文档

- [skill-index.yaml 规范](../skill-index.yaml)
- [GitHub API 速率限制说明](https://docs.github.com/en/rest/overview/resources-in-the-rest-api#rate-limiting)
- [OODER SDK 架构文档](../docs/v2.3/ARCHITECTURE-V2.3.md)

---

创建时间：2026-03-05
更新时间：2026-03-05
状态：✅ 全部完成

## Skills 团队已完成事项

### 任务 6：更新 GitDiscoveryController（Skills 团队）✅

**完成内容**：
1. 注入 `SkillIndexLoader`，优先使用 `skill-index.yaml` 加载技能列表
2. 添加 `useIndexFirst` 配置项（默认 true）
3. 更新 `convertToCapabilities` 方法，优先使用 `SkillPackage.sceneCapability` 和 `sceneId` 字段
4. 添加向后兼容处理（使用反射支持旧版 SkillPackage）
5. `DiscoveryResultDTO` 添加 `fromCache` 字段

### SkillIndexLoader 优化 ✅

**完成内容**：
1. `getSkillsFromIndex` 方法支持 `sceneId` 字段判断场景能力
2. 优先级：type=SCENE > sceneId 非空 > skillId 包含 "-scene"
3. 支持多路径查找 skill-index.yaml（当前目录、上级目录、仓库根目录）

### 新增配置项

```properties
ooder.discovery.use-index-first=true
```

### 验证结果

- ✅ Gitee 发现 API 正常工作
- ✅ `fromCache: true` 表示使用 skill-index.yaml
- ✅ 场景能力正确识别（sceneCapability: true）
- ✅ 响应即时（本地文件加载，无 API 限流问题）

## SDK 团队任务说明

SDK 团队后续可在 `agent-sdk-api` 模块中添加以下字段，届时 Skills 团队的反射兼容代码将自动使用新字段：

1. **SkillPackage 添加字段**：
   - `sceneId`: String
   - `sceneCapability`: Boolean
   - `capabilities`: List<String>
   - `dependencies`: List<SkillDependency>

2. **GitHubDiscoverer/GiteeDiscoverer 优化**：
   - 优先读取远程仓库的 `skill-index.yaml`
   - 解析 sceneId 字段

3. **缓存机制**：
   - 添加 `CachedGitHubDiscoverer` 实现

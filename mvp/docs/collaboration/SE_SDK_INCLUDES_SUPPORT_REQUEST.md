# SE SDK 协作申请：完全支持 skill-index.yaml includes 格式

> **文档类型**: 协作申请  
> **版本**: v2.3.1  
> **创建日期**: 2026-03-21  
> **状态**: 待处理  
> **优先级**: P0 - 阻塞性问题  
> **指派**: SE SDK 团队

---

## 一、问题描述

### 1.1 问题现象

MVP 项目集成 SE SDK 2.3.1 后，Gitee 发现功能始终返回 0 个技能，导致技能发现功能完全不可用。

### 1.2 问题历史

此问题已**反复出现多次**，每次都以临时方案解决，未从根本上解决：

| 时间 | 问题 | 临时方案 | 根本原因未解决 |
|------|------|----------|----------------|
| 2026-02 | Gitee发现返回0技能 | 在skill-index.yaml添加完整skills列表 | SE SDK不支持includes |
| 2026-03-10 | SE SDK 2.3.1集成问题 | 修改本地加载逻辑 | 远程发现仍不支持 |
| 2026-03-21 | 格式再次不兼容 | 本次协作申请 | **需要彻底解决** |

---

## 二、根本原因深度分析

### 2.1 格式差异对比

#### SE SDK 期望格式（直接 skills 列表）

```yaml
# SE SDK 期望的格式
skills:
  - id: skill-network
    name: 网络管理服务
    category: sys
    version: "2.3.1"
  - id: skill-security
    name: 安全管理服务
    category: sys
    version: "2.3.1"
```

#### Gitee 仓库实际格式（includes 引用）

```yaml
# Gitee 仓库实际格式（v2.3.1标准）
apiVersion: ooder.io/v1
kind: SkillIndex

metadata:
  name: ooder-skills
  version: "2.3.1"

spec:
  includes:
    - categories.yaml
    - scene-drivers.yaml
    - skills/*.yaml
    - scenes/*.yaml
  
  statistics:
    totalSkills: 63
    totalScenes: 50
```

### 2.2 代码层面分析

#### SE SDK 本地加载器

```java
private void loadFromDirectory(File indexDir, Yaml yaml) {
    // 直接加载目录下的分散文件
    File skillsDir = new File(indexDir, "skills");
    if (skillsDir.exists()) {
        File[] skillFiles = skillsDir.listFiles((dir, name) -> name.endsWith(".yaml"));
        for (File skillFile : skillFiles) {
            // 逐个加载 skills/*.yaml
        }
    }
}
```

**结论**: 本地加载器**已支持目录结构**，不依赖根目录 skill-index.yaml。

#### SE SDK 远程发现

```java
private void parseSkillIndex(String yamlContent) {
    for (String line : yamlContent.split("\n")) {
        if (trimmed.startsWith("skills:")) {
            currentSection = "skills";
        }
        // 直接解析 skills: 字段
    }
}
```

**结论**: 远程发现**只识别 skills: 字段**，不支持 includes 引用。

### 2.3 问题根源

| 组件 | 期望格式 | 实际支持 | 问题 |
|------|----------|----------|------|
| **SkillIndexLoader (本地)** | 目录结构 | ✅ 已支持 | 无 |
| **SkillDiscoveryService (远程)** | 单文件列表 | ❌ 不支持includes | **根本问题** |
| **根目录 skill-index.yaml** | includes 格式 | ⚠️ 声明性文件 | 未被远程使用 |

---

## 三、影响范围

### 3.1 功能影响

| 功能 | 影响 | 严重程度 |
|------|------|----------|
| Gitee 技能发现 | 完全不可用 | 🔴 阻塞 |
| GitHub 技能发现 | 完全不可用 | 🔴 阻塞 |
| 远程技能安装 | 无法获取下载地址 | 🔴 阻塞 |
| 技能市场 | 无法展示技能列表 | 🔴 阻塞 |

### 3.2 业务影响

- MVP 项目无法演示技能发现功能
- 用户无法从远程仓库安装技能
- 技能市场功能完全瘫痪

---

## 四、解决方案对比

### 方案 A：SE SDK 支持 includes 格式（推荐）

#### 实现方式

```java
public class SkillIndexLoader {
    
    public void loadFromRemote(String indexUrl) {
        String content = fetchUrl(indexUrl);
        Map<String, Object> indexData = yaml.load(content);
        
        // 检查是否使用 includes 格式
        Map<String, Object> spec = (Map<String, Object>) indexData.get("spec");
        if (spec != null && spec.containsKey("includes")) {
            // 解析 includes 引用
            List<String> includes = (List<String>) spec.get("includes");
            for (String include : includes) {
                if (include.contains("*")) {
                    // 处理通配符: skills/*.yaml
                    loadWildcardIncludes(include, indexUrl);
                } else {
                    // 处理具体文件: categories.yaml
                    loadSingleInclude(include, indexUrl);
                }
            }
        } else {
            // 兼容旧格式：直接 skills 列表
            parseDirectSkills(indexData);
        }
    }
    
    private void loadWildcardIncludes(String pattern, String baseUrl) {
        // 例如: skills/*.yaml
        // 1. 获取目录列表（通过 GitHub/Gitee API）
        // 2. 匹配所有符合条件的文件
        // 3. 逐个加载并合并
    }
}
```

#### 优点

1. **根本解决**：彻底解决格式兼容问题
2. **符合标准**：includes 是 v2.3.1 标准格式
3. **可维护性**：模块化文件结构，易于维护
4. **向后兼容**：同时支持新旧两种格式

#### 缺点

1. 需要 SE SDK 团队开发
2. 需要处理远程文件通配符解析

### 方案 B：skill-index.yaml 同时包含两种格式

```yaml
apiVersion: ooder.io/v1
kind: SkillIndex

metadata:
  name: ooder-skills
  version: "2.3.1"

spec:
  includes:
    - categories.yaml
    - scene-drivers.yaml
    - skills/*.yaml
    - scenes/*.yaml

# 兼容层：为远程发现提供完整列表
skills:
  - skillId: skill-network
    name: 网络管理服务
    version: "2.3.1"
    # ... 63个技能完整列表
  
scenes:
  - sceneId: llm-chat
    name: LLM智能对话场景
    # ... 50个场景完整列表
```

#### 优点

1. 不需要修改 SE SDK
2. 立即可用

#### 缺点

1. **维护负担**：需要在两处维护相同数据
2. **文件过大**：skill-index.yaml 会超过 2000 行
3. **同步问题**：容易产生不一致
4. **临时方案**：未从根本上解决问题

---

## 五、协作请求

### 5.1 正式请求

**请求 SE SDK 团队在 v2.3.1 版本中完全支持 includes 格式**

### 5.2 具体要求

| 序号 | 要求 | 优先级 | 说明 |
|------|------|:------:|------|
| 1 | 支持 spec.includes 字段解析 | P0 | 核心功能 |
| 2 | 支持通配符匹配 (skills/*.yaml) | P0 | 必需功能 |
| 3 | 支持 GitHub/Gitee 远程文件获取 | P0 | 必需功能 |
| 4 | 向后兼容直接 skills 列表格式 | P1 | 兼容性 |
| 5 | 提供格式验证工具 | P2 | 开发辅助 |

### 5.3 接口设计建议

```java
package net.ooder.sdk.discovery;

/**
 * 技能索引解析器接口
 */
public interface SkillIndexParser {
    
    /**
     * 解析技能索引
     * @param indexUrl 索引文件URL
     * @param options 解析选项
     * @return 解析结果
     */
    CompletableFuture<SkillIndexResult> parseIndex(String indexUrl, ParseOptions options);
    
    /**
     * 解析选项
     */
    class ParseOptions {
        private boolean followIncludes = true;  // 是否解析includes
        private boolean cacheEnabled = true;    // 是否启用缓存
        private int cacheTimeout = 3600;        // 缓存超时(秒)
    }
    
    /**
     * 解析结果
     */
    class SkillIndexResult {
        private List<SkillDefinition> skills;
        private List<SceneDefinition> scenes;
        private List<CategoryDefinition> categories;
        private List<SceneDriver> sceneDrivers;
        private Map<String, Object> metadata;
    }
}
```

### 5.4 时间节点

| 阶段 | 内容 | 期望时间 |
|------|------|----------|
| 需求确认 | SE SDK 团队确认需求 | 2026-03-22 |
| 设计评审 | 接口设计评审 | 2026-03-23 |
| 开发实现 | SE SDK 开发 | 2026-03-25 |
| 联调测试 | Skills 团队联调 | 2026-03-26 |
| 发布 | v2.3.1 发布 | 2026-03-28 |

---

## 六、v2.3.1 文档更新

### 6.1 需要更新的文档

| 文档 | 路径 | 更新内容 |
|------|------|----------|
| SKILL_INDEX_V2.3.1_EXECUTION_PLAN.md | archive/execution/ | 添加includes支持说明 |
| SKILL_INDEX_FIELD_AUDIT_REPORT.md | archive/analysis/ | 更新字段检查结果 |
| SE_SDK_Collaboration_Report.md | archive/collaboration/ | 添加本次协作记录 |
| README.md | v2.3.1/ | 更新格式说明 |

### 6.2 更新内容

#### SKILL_INDEX_V2.3.1_EXECUTION_PLAN.md 更新

```markdown
### Phase 4: includes 格式支持 (待SE SDK支持)

┌─────────────────────────────────────────────────────────────────┐
│  Phase 4: includes 格式支持                              ⏳ 待SE │
├─────────────────────────────────────────────────────────────────┤
│  [⏳] SE SDK 支持 spec.includes 字段解析                        │
│  [⏳] SE SDK 支持通配符匹配 (skills/*.yaml)                      │
│  [⏳] SE SDK 支持 GitHub/Gitee 远程文件获取                      │
│  [⏳] 向后兼容直接 skills 列表格式                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 七、验证方案

### 7.1 验证步骤

```bash
# 1. 检查远程索引文件
curl -s "https://gitee.com/ooderCN/skills/raw/master/skill-index.yaml"

# 2. 验证 includes 格式
# 应包含 spec.includes 字段

# 3. 测试远程发现
# MVP 项目调用 Gitee 发现接口
# 应返回 63 个技能 + 50 个场景

# 4. 测试本地加载
# 本地加载应继续正常工作
```

### 7.2 验收标准

| 标准 | 预期结果 |
|------|----------|
| 远程发现返回技能数 | 63 |
| 远程发现返回场景数 | 50 |
| 本地加载正常 | ✅ |
| 旧格式兼容 | ✅ |

---

## 八、附录

### 8.1 相关代码文件

| 文件 | 路径 | 说明 |
|------|------|------|
| SkillIndexLoader.java | skill-scene/src/.../discovery/ | 本地加载器 |
| SkillDiscoveryService.java | skills/capabilities/.../mqtt/ | 远程发现服务 |
| GitDiscoveryController.java | skill-scene/src/.../controller/ | 发现控制器 |

### 8.2 相关文档

- [SKILL_INDEX_FORMAT_ALIGNMENT.md](./SKILL_INDEX_FORMAT_ALIGNMENT.md)
- [SKILL_INDEX_V2.3.1_EXECUTION_PLAN.md](../../archive/execution/SKILL_INDEX_V2.3.1_EXECUTION_PLAN.md)
- [SKILL_INDEX_FIELD_AUDIT_REPORT.md](../../archive/analysis/SKILL_INDEX_FIELD_AUDIT_REPORT.md)
- [SE_SDK_Collaboration_Report.md](../../archive/collaboration/SE_SDK_Collaboration_Report.md)

### 8.3 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-21 | 初始版本，详细分析问题根源 |

---

**创建人**: Skills 团队  
**期望回复时间**: 2026-03-22  
**下次更新时间**: 收到 SE SDK 团队回复后

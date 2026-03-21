# Gitee 技能发现集成方案

## 一、问题分析

### 1.1 当前状态

| 项目 | 状态 | 说明 |
|------|------|------|
| Gitee Token | ✅ 已配置 | `f0d11903a8e10e3ce09d51bc9552b664` |
| `application.yml` | ✅ 已配置 | `ooder.gitee.*` |
| `SdkConfiguration` | ✅ 已简化 | 使用 `SkillPackageManager.discoverAll(GITEE)` |
| `DiscoveryController` | ✅ 已更新 | 使用 SE SDK 接口 |
| Gitee 仓库索引 | ❌ 缺失 | 需要 `skill-index.yaml` |

### 1.2 根本原因

**Gitee 仓库缺少 `skill-index.yaml` 文件**

SE SDK Gitee 发现器需要在仓库中有以下结构：
```
your-repo/
├── skills/
│   ├── skill-index.yaml      # 技能索引文件（必需）
│   ├── skill-xxx/
│   │   ├── SKILL.md
│   │   └── ...
│   └── ...
└── README.md
```

---

## 二、SE SDK Gitee 发现器规范

### 2.1 配置要求

```yaml
ooder:
  discovery:
    gitee:
      enabled: true
      default-owner: ooderCN
      default-repo: skills
      default-branch: main
      skills-path: skills
      token: ${GITEE_TOKEN:}
      cache-ttl-ms: 3600000
```

### 2.2 代码使用

```java
@Autowired
private UnifiedDiscoveryService discoveryService;

public void discoverFromGitee() {
    String repoUrl = "https://gitee.com/ooderCN/skills";
    
    CompletableFuture<List<SkillPackage>> future = 
        discoveryService.discoverSkills(repoUrl);
    
    List<SkillPackage> skills = future.get(60, TimeUnit.SECONDS);
}
```

### 2.3 skill-index.yaml 格式

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillIndex

metadata:
  id: skill-example-scene
  name: 示例场景技能
  version: 1.0.0
  description: 技能描述
  author: your-name
  license: Apache-2.0

spec:
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  businessCategory: PRODUCTIVITY
  category: TASK_MANAGEMENT
  capabilityCategory: BASIC_SERVICE

  tags:
    - 标签1
    - 标签2

  capabilityAddresses:
    required:
      - name: llm-service
        address: cap://llm/default
        description: LLM服务

  roles:
    - name: admin
      displayName: 管理员
      minCount: 1
      maxCount: 1
      permissions: [MANAGE, CONFIGURE, VIEW]
```

---

## 三、解决方案

### 方案 A：创建 Gitee 仓库索引文件（推荐）

**步骤**：
1. 在 Gitee 仓库 `skills/` 目录下创建 `skill-index.yaml`
2. 为每个技能创建 `SKILL.md` 或 `skill-manifest.yaml`
3. 重试发现

**示例结构**：
```
skills/
├── skill-index.yaml
├── skill-audit/
│   ├── SKILL.md
│   └── skill-manifest.yaml
├── skill-im/
│   ├── SKILL.md
│   └── skill-manifest.yaml
└── ...
```

### 方案 B：使用本地发现（当前可用）

**配置**：
```yaml
ooder:
  discovery:
    use-se-sdk: true
    use-index-first: true
  skills:
    path: ../skills
```

**使用**：
- 选择 "本地文件系统" 发现方式
- 从本地 `skill-index` 目录加载

### 方案 C：集成 UnifiedDiscoveryService（SE SDK 推荐）

**修改 `SdkConfiguration.java`**：
```java
@Bean
@ConditionalOnProperty(name = "ooder.sdk.enabled", havingValue = "true")
public UnifiedDiscoveryService unifiedDiscoveryService() {
    return sdk.getUnifiedDiscoveryService();
}
```

**修改 `DiscoveryController.java`**：
```java
@Autowired(required = false)
private UnifiedDiscoveryService unifiedDiscoveryService;

@PostMapping("/gitee")
public ResultModel<GitDiscoveryResultDTO> discoverFromGitee(...) {
    if (unifiedDiscoveryService != null) {
        List<SkillPackage> skills = unifiedDiscoveryService
            .discoverSkills("https://gitee.com/ooderCN/skills")
            .get(60, TimeUnit.SECONDS);
        // ...
    }
}
```

---

## 四、实施计划

### 4.1 短期（立即可用）

| 任务 | 状态 |
|------|------|
| 使用本地发现方式 | ✅ 已可用 |
| 配置 Gitee Token | ✅ 已完成 |
| 更新 `DiscoveryController` | ✅ 已完成 |

### 4.2 中期（需要 SE 团队支持）

| 任务 | 说明 |
|------|------|
| 创建 Gitee 仓库 `skill-index.yaml` | 在 `ooderCN/skills` 仓库添加索引文件 |
| 为每个技能添加 `SKILL.md` | 标准化技能描述 |

### 4.3 长期（完整集成）

| 任务 | 说明 |
|------|------|
| 集成 `UnifiedDiscoveryService` | 使用 SE SDK 推荐接口 |
| 实现缓存机制 | 减少API调用 |
| 支持多仓库配置 | 企业私有仓库 |

---

## 五、配置对照

### 5.1 当前配置 vs SE SDK 规范

| 配置项 | 当前配置 | SE SDK 规范 | 状态 |
|--------|----------|-------------|------|
| `ooder.gitee.token` | ✅ | `ooder.discovery.gitee.token` | 需调整 |
| `ooder.gitee.owner` | ✅ | `ooder.discovery.gitee.default-owner` | 需调整 |
| `ooder.gitee.skills-repo` | ✅ | `ooder.discovery.gitee.default-repo` | 需调整 |
| `ooder.discovery.gitee.enabled` | ❌ | 必需 | 需添加 |
| `ooder.discovery.gitee.skills-path` | ❌ | 默认 `skills` | 需添加 |

### 5.2 建议配置更新

```yaml
ooder:
  discovery:
    use-se-sdk: true
    use-index-first: true
    gitee:
      enabled: true
      default-owner: ooderCN
      default-repo: skills
      default-branch: main
      skills-path: skills
      token: f0d11903a8e10e3ce09d51bc9552b664
      cache-ttl-ms: 3600000
  skills:
    path: ../skills
```

---

## 六、故障排查

### 6.1 发现返回空列表

**检查清单**：
1. ✅ Token 是否有效
2. ✅ 仓库地址是否正确
3. ❌ `skill-index.yaml` 是否存在
4. ❌ `skill-index.yaml` 格式是否正确

**验证命令**：
```bash
# 检查仓库访问
curl -H "Authorization: token YOUR_TOKEN" \
  https://gitee.com/api/v5/repos/ooderCN/skills

# 检查索引文件
curl -H "Authorization: token YOUR_TOKEN" \
  https://gitee.com/api/v5/repos/ooderCN/skills/contents/skills/skill-index.yaml
```

### 6.2 API 限流

| 类型 | 限制 |
|------|------|
| 未认证请求 | 60 次/小时 |
| 认证请求 | 5000 次/小时 |

---

## 七、相关文档

- [SE SDK Gitee 发现器文档](file:///E:/github/ooder-sdk/scene-engine/docs/v2.3.1/10-integration/04-gitee-discovery.md)
- [SE SDK 2.3.1 升级计划](file:///e:/github/ooder-skills/mvp/docs/upgrade/SE_SDK_2.3.1_UPGRADE_PLAN.md)
- [场景组详情优化](file:///e:/github/ooder-skills/mvp/docs/features/SCENE_GROUP_DETAIL_OPTIMIZATION.md)

---

**文档版本**: 1.0  
**更新日期**: 2026-03-20  
**状态**: 🔴 等待 Gitee 仓库添加 `skill-index.yaml`

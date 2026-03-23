# 技能安装流程协作文档

## 一、逻辑归属分析

| 层级 | 组件 | 职责 | 归属 |
|------|------|------|------|
| **API 层** | `DiscoveryController.installSkill()` | 接收安装请求，调用 SE SDK | MVP |
| **核心安装** | `SkillPackageManager.installWithDependencies()` | 执行下载、安装、依赖处理 | **SE SDK** (skills-framework) |
| **本地安装** | `InstallServiceImpl.downloadAndInstall()` | 本地目录复制、移动 | MVP |
| **下载服务** | `SkillDownloadService` | 从源码目录复制或下载 | MVP |
| **注册管理** | `registerSkillInRegistry()` | 写入 registry.properties | MVP |

**结论：核心安装逻辑在 SE SDK（skills-framework），MVP 负责本地文件操作和注册。**

---

## 二、Gitee 压缩包检测

| 检测项 | 结果 | 说明 |
|--------|------|------|
| Gitee 仓库存在 | ✅ | https://gitee.com/ooderCN/skills |
| Releases 发布包 | ❌ | 无正式发布的 .zip 包 |
| 单技能压缩包 | ❌ | 无单技能独立下载链接 |
| 仓库整体下载 | ✅ | `https://gitee.com/ooderCN/skills/repository/archive/master.zip` |

**问题：Gitee 上没有单个技能的 .zip 下载包，只能下载整个仓库。**

---

## 三、用户逻辑 vs 实际逻辑对比

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          用户理解的流程                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│  1. 点击安装 → 从 Gitee 下载 .zip 文件                                       │
│  2. 解压 .zip → 读取 skill.yaml 配置                                        │
│  3. 注册信息 → 写入 registry.properties                                     │
│  4. 下载依赖 → 安装依赖技能                                                  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
                              实际情况对比
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│                          实际程序流程                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│  1. 点击安装 → 调用 SkillPackageManager.installWithDependencies()            │
│     ↓                                                                       │
│  2. SE SDK 内部处理（黑盒）                                                  │
│     - 从 ../skills 目录查找技能                                              │
│     - 复制到 ./.ooder/downloads 目录                                        │
│     - 移动到 ./.ooder/installed 目录                                        │
│     ↓                                                                       │
│  3. ✅ 已修复：调用 registerSkillInRegistry() 更新 registry.properties       │
│     ↓                                                                       │
│  4. "我的能力"页面可发现已安装技能                                            │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、问题清单

| # | 问题 | 影响范围 | 责任方 | 状态 |
|---|------|----------|--------|------|
| 1 | Gitee 无单技能 .zip 下载包 | 无法从远程下载单个技能 | SKILLS 团队 | 待确认 |
| 2 | `installWithDependencies()` 实现不透明 | 无法确认下载/注册逻辑 | SKILLS 团队 | 待确认 |
| 3 | 安装后未更新 registry.properties | "我的能力"无法发现技能 | MVP 团队 | ✅ 已修复 |
| 4 | `downloadFromUrl()` 未被调用 | 远程下载功能未实现 | MVP 团队 | 待实现 |

---

## 五、需向 SKILLS 团队确认的问题

```
致：SKILLS 团队
来自：MVP 团队
主题：SkillPackageManager.installWithDependencies() 实现确认

问题 1：installWithDependencies() 的完整流程是什么？
  - 是否包含从 Gitee/GitHub 远程下载？
  - 是否会更新 registry.properties？
  - 依赖下载的具体逻辑？

问题 2：Gitee 技能仓库的发布策略
  - 是否计划发布单技能 .zip 包？
  - 是否有技能市场 API 可用？
  - 建议的远程下载方式是什么？

问题 3：技能安装后的注册机制
  - 是否有 onSkillInstalled 事件？
  - 谁负责维护 registry.properties？
  - 推荐的注册方式是什么？

问题 4：skill-index-entry.yaml 的用途
  - 该文件是否用于技能发现？
  - giteeDownloadUrl 字段的预期格式？
  - 是否需要配置下载链接？
```

---

## 六、MVP 团队已修复项

### 1. 安装后注册 ✅

**文件：** `DiscoveryController.java`

```java
// 在 installSkill() 成功后添加注册逻辑
if (installResult != null && installResult.isSuccess()) {
    // ... 原有逻辑
    registerSkillInRegistry(skillId);  // 新增
}

private void registerSkillInRegistry(String skillId) {
    // 写入 data/installed-skills/registry.properties
    props.setProperty(skillId + ".id", skillId);
    props.setProperty(skillId + ".path", skillPath);
    props.setProperty(skillId + ".installedAt", timestamp);
}
```

### 2. "我的能力"页面数据源 ✅

**文件：** `MvpSkillIndexLoader.java`

```java
// getWorkspaceCapabilities() 现在会扫描 registry.properties 中的路径
private List<CapabilityDTO> scanRegistryInstalledSkills(String source) {
    // 从 registry.properties 读取已安装技能路径
    // 扫描技能目录并返回 CapabilityDTO
}
```

---

## 七、配置确认

```yaml
# application.yml
ooder:
  skills:
    path: ../skills                    # 技能源码目录（本地开发）
    directories:
      downloads: ./.ooder/downloads    # 下载临时目录
      installed: ./.ooder/installed    # 已安装目录
      activated: ./.ooder/activated    # 已激活目录
      dev: ./.ooder/dev                # 开发目录
      cache: ./.ooder/cache            # 缓存目录

# registry.properties 路径
data/installed-skills/registry.properties
```

---

## 八、下一步行动

| 优先级 | 行动项 | 负责方 | 状态 |
|--------|--------|--------|------|
| P0 | 修复安装后注册逻辑 | MVP 团队 | ✅ 完成 |
| P1 | 向 SKILLS 团队发送协作文档 | MVP 团队 | 进行中 |
| P1 | 确认 Gitee 发布策略 | SKILLS 团队 | 待确认 |
| P2 | 实现远程下载功能 | 待确认 | 待实现 |

---

## 九、测试验证

### 验证步骤

1. 重启应用
2. 访问 Gitee 发现页面，选择技能点击安装
3. 检查 `data/installed-skills/registry.properties` 是否更新
4. 访问"我的能力"页面，确认已安装技能显示

### 预期结果

```
# registry.properties
skill-xxx.id=skill-xxx
skill-xxx.path=E\\:\\github\\ooder-skills\\mvp\\..\\skills\\_system\\skill-xxx
skill-xxx.installedAt=1774139418000
```

---

*文档版本：1.0*
*更新时间：2026-03-22*
*作者：MVP 团队*

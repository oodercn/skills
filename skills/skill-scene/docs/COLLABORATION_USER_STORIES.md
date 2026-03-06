# 协同用户故事汇总

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-06 |
| 所属模块 | skill-scene |
| 状态 | 进行中 |

---

## 一、协同任务概览

| 任务编号 | 任务名称 | 优先级 | 状态 | 负责团队 |
|----------|----------|--------|------|----------|
| SDK-COOP-2026-001 | GitHub 技能发现优化 | P0 | ✅ 已完成 | SDK + Skills |
| SDK-COOP-2026-002 | SDK 安装功能增强 | P0 | ⏳ 待分配 | SDK + Skills |

---

## 二、SDK-COOP-2026-001: GitHub 技能发现优化

### 用户故事

| 故事ID | 角色 | 需求 | 目的 | 状态 |
|--------|------|------|------|------|
| US-DISC-001 | 用户 | 快速发现GitHub上的技能 | 减少等待时间 | ✅ 已完成 |
| US-DISC-002 | 用户 | 避免API限流错误 | 正常使用发现功能 | ✅ 已完成 |
| US-DISC-003 | 用户 | 正确识别场景能力 | 准确分类技能类型 | ✅ 已完成 |
| US-DISC-004 | 用户 | 从Gitee镜像发现技能 | 国内访问更快 | ✅ 已完成 |

### 技术实现

**问题一：API 速率限制**
- 原问题：每个技能需要多次API调用，50+技能需要100+次调用
- 解决方案：优先读取 `skill-index.yaml`，API调用从100+次减少到1-2次

**问题二：场景能力识别错误**
- 原问题：仅基于 `skillId` 是否包含 `-scene` 判断
- 解决方案：使用 `sceneId` 字段和 `sceneCapability` 属性

### 验收标准

| 标准 | 状态 |
|------|------|
| GitHub 发现功能正常工作 | ✅ |
| Gitee 发现功能正常工作 | ✅ |
| 无 403 限流错误 | ✅ |
| 响应时间 < 3 秒 | ✅ |
| 场景能力正确识别 | ✅ |

---

## 三、SDK-COOP-2026-002: SDK 安装功能增强

### 用户故事

| 故事ID | 角色 | 需求 | 目的 | 状态 |
|--------|------|------|------|------|
| US-INST-001 | 用户 | 从URL直接安装技能 | 完成安装闭环 | ⏳ 待开发 |
| US-INST-002 | 用户 | 自动安装技能依赖 | 一键安装完整功能 | ⏳ 待开发 |
| US-INST-003 | 用户 | 校验下载文件完整性 | 确保安装安全 | ⏳ 待开发 |
| US-INST-004 | 用户 | 自动切换镜像地址 | 提高下载成功率 | ⏳ 待开发 |

### 技术方案

**新增接口**：
```java
// SkillPackageManager.java
CompletableFuture<InstallResult> installFromUrl(String downloadUrl, InstallFromUrlOptions options);
CompletableFuture<InstallResultWithDependencies> installFromUrlWithDependencies(String downloadUrl, InstallFromUrlOptions options);
```

**InstallFromUrlOptions 类**：
```java
public class InstallFromUrlOptions {
    private String skillId;           // 可选，用于验证
    private String version;           // 可选，用于验证
    private boolean verifyChecksum;   // 是否校验 checksum
    private String checksum;          // 校验值
    private boolean installDependencies; // 是否安装依赖
    private String mirrorUrl;         // 镜像地址（备用）
}
```

### 任务分解

| 任务 | 负责团队 | 预计时间 | 状态 |
|------|----------|----------|------|
| 添加 InstallFromUrlOptions 类 | SDK 团队 | 0.5 天 | ⏳ 待开始 |
| 定义 installFromUrl 接口 | SDK 团队 | 0.5 天 | ⏳ 待开始 |
| 实现 installFromUrl 方法 | SDK 团队 | 2 天 | ⏳ 待开始 |
| 更新 GitDiscoveryController | Skills 团队 | 0.5 天 | ⏳ 待开始 |
| 测试验证 | 双方团队 | 0.5 天 | ⏳ 待开始 |

### 验收标准

| 标准 | 状态 |
|------|------|
| SDK 支持从 URL 安装技能 | ⏳ |
| 安装成功后技能可用 | ⏳ |
| 依赖自动安装 | ⏳ |
| checksum 校验正常 | ⏳ |

---

## 四、场景与能力管理用户故事

### 4.1 场景管理

| 故事ID | 角色 | 需求 | 目的 | 状态 |
|--------|------|------|------|------|
| US-SCENE-001 | 用户 | 查看我的角色可用的场景列表 | 了解当前可参与的场景 | ✅ |
| US-SCENE-002 | 用户 | 查看P2P模式下已加入的场景 | 了解协作场景状态 | ✅ |
| US-SCENE-003 | 用户 | 按状态筛选场景 | 快速定位目标场景 | ✅ |
| US-SCENE-004 | 用户 | 搜索场景名称 | 快速查找场景 | ✅ |
| US-SCENE-005 | 用户 | 查看场景基本信息 | 了解场景概述 | ✅ |
| US-SCENE-006 | 用户 | 查看场景中的能力列表 | 了解场景提供的能力 | ✅ |
| US-SCENE-010 | 管理员 | 创建新场景 | 扩展系统能力 | ✅ |
| US-SCENE-011 | 管理员 | 激活/停用场景 | 控制场景状态 | ✅ |
| US-SCENE-015 | 管理员 | 创建场景组 | 组织多个场景 | ✅ |

### 4.2 能力管理

| 故事ID | 角色 | 需求 | 目的 | 状态 |
|--------|------|------|------|------|
| US-CAP-001 | 用户 | 查看场景中的能力列表 | 了解可用能力 | ✅ |
| US-CAP-002 | 用户 | 按类型筛选能力 | 快速定位能力 | ✅ |
| US-CAP-003 | 用户 | 搜索能力名称 | 快速查找能力 | ✅ |
| US-CAP-004 | 用户 | 查看能力详情 | 了解能力参数 | ✅ |
| US-CAP-005 | 用户 | 执行能力调用 | 使用能力功能 | ✅ |
| US-CAP-009 | 管理员 | 添加能力到场景 | 扩展场景功能 | ✅ |

### 4.3 可观测性

| 故事ID | 角色 | 需求 | 目的 | 状态 |
|--------|------|------|------|------|
| US-OBS-001 | 用户 | 查看场景运行状态 | 了解场景健康度 | ✅ |
| US-OBS-002 | 用户 | 查看能力运行状态 | 了解能力健康度 | ✅ |
| US-OBS-003 | 用户 | 查看场景组成员状态 | 了解协作状态 | ✅ |

---

## 五、角色分类用户故事

### 5.1 系统安装者 (installer)

| 故事ID | 需求 | 目的 | 页面 | 状态 |
|--------|------|------|------|------|
| US-ROLE-001 | 查看安装检查清单 | 了解安装进度 | role-installer.html | ✅ |
| US-ROLE-002 | 安装基础技能包 | 初始化系统环境 | role-installer.html | ✅ |
| US-ROLE-003 | 查看安装日志 | 排查安装问题 | role-installer.html | ✅ |
| US-ROLE-004 | 查看已安装技能 | 管理已安装技能 | my-capabilities.html | ✅ |

### 5.2 系统管理员 (admin)

| 故事ID | 需求 | 目的 | 页面 | 状态 |
|--------|------|------|------|------|
| US-ROLE-010 | 查看闭环二流程 | 了解分发任务 | role-admin.html | ✅ |
| US-ROLE-011 | 发现场景技能 | 扩展系统能力 | capability-discovery.html | ✅ |
| US-ROLE-012 | 管理场景组 | 组织场景 | scene-group-management.html | ✅ |
| US-ROLE-013 | 查看能力统计 | 分析使用情况 | capability-stats.html | ✅ |
| US-ROLE-014 | 管理组织架构 | 管理用户和部门 | org-management.html | ✅ |
| US-ROLE-015 | 架构规范检查 | 确保代码质量 | arch-check.html | ✅ |

### 5.3 主导者 (leader)

| 故事ID | 需求 | 目的 | 页面 | 状态 |
|--------|------|------|------|------|
| US-ROLE-020 | 查看激活流程 | 了解激活步骤 | role-leader.html | ✅ |
| US-ROLE-021 | 查看待激活场景 | 处理待办任务 | my-todos.html | ✅ |
| US-ROLE-022 | 管理我的场景 | 管理参与场景 | my-scenes.html | ✅ |
| US-ROLE-023 | 管理密钥 | 生成和管理密钥 | key-management.html | ✅ |

### 5.4 协作者 (collaborator)

| 故事ID | 需求 | 目的 | 页面 | 状态 |
|--------|------|------|------|------|
| US-ROLE-030 | 查看待办任务 | 处理分配任务 | role-collaborator.html | ✅ |
| US-ROLE-031 | 查看参与场景 | 了解协作场景 | my-scenes.html | ✅ |
| US-ROLE-032 | 查看历史记录 | 追溯操作记录 | my-history.html | ✅ |

---

## 六、进度汇总

### 协同任务进度

| 任务 | 进度 | 完成日期 |
|------|------|----------|
| SDK-COOP-2026-001 | 100% | 2026-03-05 |
| SDK-COOP-2026-002 | 0% | - |

### 用户故事完成统计

| 类别 | 总数 | 已完成 | 进行中 | 待开始 |
|------|------|--------|--------|--------|
| 技能发现 | 4 | 4 | 0 | 0 |
| 技能安装 | 4 | 0 | 0 | 4 |
| 场景管理 | 9 | 9 | 0 | 0 |
| 能力管理 | 6 | 6 | 0 | 0 |
| 可观测性 | 3 | 3 | 0 | 0 |
| 角色分类 | 15 | 15 | 0 | 0 |
| **总计** | **41** | **37** | **0** | **4** |

---

## 七、下一步行动

### 待完成任务

1. **SDK-COOP-2026-002: SDK 安装功能增强**
   - SDK 团队：实现 `installFromUrl` 接口
   - Skills 团队：更新 `GitDiscoveryController`

### 联系方式

- SDK 团队负责人：[待指定]
- Skills 团队负责人：[待指定]
- 技术评审：[待指定]

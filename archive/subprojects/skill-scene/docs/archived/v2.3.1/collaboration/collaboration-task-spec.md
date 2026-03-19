# 场景技能安装流程优化 - 协作任务说明

## 一、项目背景

### 1.1 当前状态

场景技能安装流程已完成基础框架搭建，但存在以下问题：

1. **分类体系变更**：ABS/ASS/TBS 分类已废弃，改用 SceneType + visibility 二维分类
2. **核心逻辑模拟**：依赖安装、健康检查等核心逻辑为模拟实现
3. **差异化不足**：未按场景类型实现差异化安装流程
4. **文档滞后**：部分文档仍使用旧的分类术语

### 1.2 分类体系

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        当前分类体系 (二维)                                        │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│   维度1: SkillForm (技能形态)                                                    │
│   ├── STANDALONE  - 独立技能 (非场景技能)                                        │
│   └── SCENE       - 场景技能                                                    │
│                                                                                 │
│   维度2: SceneType (场景类型) - 仅 SkillForm=SCENE 时有效                         │
│   ├── AUTO        - 自驱场景 (自动运行，hasSelfDrive=true)                       │
│   └── TRIGGER     - 触发场景 (需要触发，hasSelfDrive=false)                      │
│                                                                                 │
│   维度3: visibility (可见性)                                                     │
│   ├── public      - 公开可见 (用户可发现、可激活)                                │
│   └── internal    - 内部使用 (后台运行，用户不可见)                              │
│                                                                                 │
│   组合示例:                                                                      │
│   ├── SCENE + AUTO + public     = 自驱业务场景 (用户可见，自动运行)              │
│   ├── SCENE + AUTO + internal   = 自驱系统场景 (后台静默运行)                    │
│   └── SCENE + TRIGGER + public  = 触发业务场景 (用户参与触发)                    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、任务列表

### 2.0 SE SDK 协作依赖

> **说明**：部分任务依赖 SE SDK 团队提供的接口，需要在 SE SDK 发布对应版本后才能完成集成。

#### SE SDK 版本计划

| 版本 | 发布日期 | 主要内容 |
|-----|---------|---------|
| v2.3.2 | 2026-03-14 | SceneEngineAutoConfiguration、KnowledgeBindingService、**CapabilityInstallLifecycle** |
| v2.4.0 | 2026-03-20 | **NotificationService**、流式ToolCalls解析 |

#### SE SDK 提供的接口

```java
// v2.3.2 - CapabilityInstallLifecycle (T1依赖)
package net.ooder.scene.skill.install;

public interface CapabilityInstallLifecycle {
    void onPreInstall(Capability capability, InstallContext context);
    void onPostInstall(Capability capability, InstallResult result);
    void onUninstall(Capability capability);
    void onInstallFailed(Capability capability, Exception error);
}

// v2.4.0 - NotificationService (T4依赖)
package net.ooder.scene.skill.notification;

public interface NotificationService {
    void push(String userId, String title, String content, PushChannel channel);
    void pushToParticipants(String activationId, NotificationMessage message);
    
    enum PushChannel {
        EMAIL, WECOM, DINGTALK, SMS, IN_APP
    }
}
```

---

### 2.1 P0 - 高优先级 (必须完成)

| ID | 任务 | 说明 | 涉及文件 | 预估工时 | SE协作 |
|----|------|------|---------|:--------:|:------:|
| T1 | 完善依赖安装逻辑 | executeInstall() 真正安装依赖能力 | InstallServiceImpl.java | 2天 | ✅ 已就绪 |
| T2 | 实现差异化安装流程 | AUTO+internal自动激活，TRIGGER等待触发 | InstallServiceImpl.java | 2天 | ⬜ 不需要 |
| T3 | 创建健康检查服务 | 检查依赖服务状态 | 新建 DependencyHealthCheckService.java | 1天 | ⬜ 不需要 |

### 2.2 P1 - 中优先级 (重要功能)

| ID | 任务 | 说明 | 涉及文件 | 预估工时 | SE协作 |
|----|------|------|---------|:--------:|:------:|
| T4 | 集成通知服务 | MQTT/邮件/钉钉等通知渠道 | NetworkActionExecutor.java | 2天 | ✅ 已就绪 |
| T5 | 完善菜单自动注册 | 按角色生成不同菜单 | MenuAutoRegisterService.java | 1天 | ⬜ 不需要 |
| T6 | 完善密钥管理 | 集成实际密钥管理服务 | ActivationServiceImpl.java | 1天 | ⬜ 不需要 |

### 2.3 P2 - 低优先级 (优化改进)

| ID | 任务 | 说明 | 涉及文件 | 预估工时 | SE协作 |
|----|------|------|---------|:--------:|:------:|
| T7 | 更新文档 | 移除ABS/ASS/TBS分类，改用SceneType+visibility | docs/*.md | 0.5天 | ⬜ 不需要 |
| T8 | 安装回滚 | 安装失败时回滚已安装的依赖 | InstallServiceImpl.java | 1天 | ⬜ 不需要 |
| T9 | 安装日志 | 持久化安装日志，支持查询 | 新建 InstallLogService.java | 1天 | ⬜ 不需要 |

**SE协作标识说明**：
- 🔴 需要 - 依赖 SE SDK 提供的接口，需等待对应版本发布
- ⬜ 不需要 - 可独立完成，不依赖 SE SDK

---

## 三、任务详细说明

### T1: 完善依赖安装逻辑

> ✅ **SE SDK 已就绪**：`CapabilityInstallLifecycle` 接口已集成
> - 实现文件：[SceneCapabilityInstallLifecycle.java](../src/main/java/net/ooder/skill/scene/capability/install/SceneCapabilityInstallLifecycle.java)

**当前状态**：
```java
// InstallServiceImpl.java L116-170
// 当前仅模拟安装，返回固定的依赖列表
InstallConfig.DependencyInfo dep1 = new InstallConfig.DependencyInfo();
dep1.setCapabilityId("kb-management");
dep1.setName("知识库管理");
dep1.setStatus(InstallConfig.DependencyInfo.DependencyStatus.INSTALLED);
```

**需要实现**：
1. 解析 Capability.dependencies 获取依赖列表
2. 检查依赖是否已安装
3. 未安装的依赖调用 CapabilityService 安装
4. 更新安装进度
5. 处理安装失败情况

**验收标准**：
- [ ] 能正确解析依赖列表
- [ ] 能检查依赖安装状态
- [ ] 能安装缺失的依赖
- [ ] 能更新安装进度
- [ ] 能处理安装失败

---

### T2: 实现差异化安装流程

**当前状态**：
- 所有场景类型使用统一安装流程
- 未根据 SceneType 和 visibility 差异化处理

**需要实现**：

| 场景类型 | 安装后状态 | 激活方式 | 用户可见 |
|---------|-----------|---------|:--------:|
| AUTO + public | SCHEDULED | 需要用户确认 | ✅ |
| AUTO + internal | INITIALIZING → RUNNING | 自动激活 | ❌ |
| TRIGGER + public | PENDING | 等待触发 | ✅ |

**验收标准**：
- [ ] AUTO + internal 安装后自动激活
- [ ] AUTO + public 安装后等待用户确认
- [ ] TRIGGER + public 安装后等待触发条件
- [ ] internal 类型不在发现页展示

---

### T3: 创建健康检查服务

**需要实现**：
```java
public interface DependencyHealthCheckService {
    
    HealthCheckResult checkDependency(String capabilityId);
    
    Map<String, HealthCheckResult> checkAllDependencies(List<String> capabilityIds);
    
    boolean isHealthy(String capabilityId);
}

public class HealthCheckResult {
    private String capabilityId;
    private HealthStatus status;  // HEALTHY, UNHEALTHY, UNKNOWN
    private String message;
    private long checkTime;
    private Map<String, Object> details;
}
```

**验收标准**：
- [ ] 能检查单个依赖健康状态
- [ ] 能批量检查依赖健康状态
- [ ] 能返回详细的健康检查结果
- [ ] 能集成到安装流程中

---

### T4: 集成通知服务

> ✅ **SE SDK 已就绪**：`NotificationService` 接口已集成
> - 实现文件：[SceneNotificationService.java](../src/main/java/net/ooder/skill/scene/notification/SceneNotificationService.java)

**当前状态**：
- ✅ NetworkAction 框架存在
- ✅ 已集成 SE SDK NotificationService
- ⚠️ 需要完善实际通知渠道配置

**需要实现**：
1. 集成 MQTT 通知
2. 集成邮件通知
3. 集成钉钉机器人通知
4. 支持通知模板

**验收标准**：
- [ ] 能通过 MQTT 发送通知
- [ ] 能通过邮件发送通知
- [ ] 能通过钉钉发送通知
- [ ] 能使用通知模板

---

### T5: 完善菜单自动注册

**当前状态**：
- confirmActivation() 中调用 menuAutoRegisterService
- 未按角色生成不同菜单

**需要实现**：
1. 从模板读取角色菜单配置
2. 按角色生成不同菜单
3. 支持菜单权限控制

**验收标准**：
- [ ] 能从模板读取菜单配置
- [ ] 能按角色生成不同菜单
- [ ] 能控制菜单权限

---

### T6: 完善密钥管理

**当前状态**：
```java
// ActivationServiceImpl.java L225-240
// 当前仅生成模拟密钥
KeyResult result = new KeyResult();
result.setKeyId("key-" + installId);
result.setKeyStatus("ACTIVE");
result.setExpireTime(System.currentTimeMillis() + 86400000L);
```

**需要实现**：
1. 集成实际密钥管理服务
2. 支持密钥过期刷新
3. 支持密钥权限控制

**验收标准**：
- [ ] 能生成真实密钥
- [ ] 能验证密钥有效性
- [ ] 能刷新过期密钥
- [ ] 能控制密钥权限

---

## 四、SE SDK 团队任务

> **责任团队**：SE SDK 团队
> **对接人**：[待填写]

### 4.1 SE SDK 待交付任务

| ID | 任务 | 交付版本 | 发布日期 | 状态 | 依赖任务 |
|----|------|:--------:|:--------:|:----:|:--------:|
| SE-1 | CapabilityInstallLifecycle 接口 | v2.3.2 | 2026-03-14 | ⬜ 待发布 | T1 |
| SE-2 | NotificationService 接口 | v2.4.0 | 2026-03-20 | ⬜ 待发布 | T4 |
| SE-3 | SceneEngineAutoConfiguration 类 | v2.3.2 | 2026-03-14 | ⬜ 待发布 | - |
| SE-4 | KnowledgeBindingService 接口 | v2.3.2 | 2026-03-14 | ⬜ 待发布 | - |

### 4.2 SE SDK 接口详细说明

#### SE-1: CapabilityInstallLifecycle

```java
package net.ooder.scene.skill.install;

public interface CapabilityInstallLifecycle {
    void onPreInstall(Capability capability, InstallContext context);
    void onPostInstall(Capability capability, InstallResult result);
    void onUninstall(Capability capability);
    void onInstallFailed(Capability capability, Exception error);
}
```

**用途**：为 T1 (完善依赖安装逻辑) 提供安装生命周期回调

#### SE-2: NotificationService

```java
package net.ooder.scene.skill.notification;

public interface NotificationService {
    void push(String userId, String title, String content, PushChannel channel);
    void pushToParticipants(String activationId, NotificationMessage message);
    
    enum PushChannel {
        EMAIL, WECOM, DINGTALK, SMS, IN_APP
    }
}
```

**用途**：为 T4 (集成通知服务) 提供统一通知接口

### 4.3 SE SDK 发布时间线

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        SE SDK 发布时间线                                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  2026-03-14 (v2.3.2)                                                            │
│  ├── SE-1: CapabilityInstallLifecycle ✅ 发布                                   │
│  ├── SE-3: SceneEngineAutoConfiguration ✅ 发布                                 │
│  └── SE-4: KnowledgeBindingService ✅ 发布                                      │
│                                                                                 │
│  2026-03-15                                                                     │
│  └── T1: skill-scene 开始集成 CapabilityInstallLifecycle                        │
│                                                                                 │
│  2026-03-20 (v2.4.0)                                                            │
│  └── SE-2: NotificationService ✅ 发布                                          │
│                                                                                 │
│  2026-03-21                                                                     │
│  └── T4: skill-scene 开始集成 NotificationService                               │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 五、协作规范

### 5.1 分支管理

```
main
  ├── feature/T1-dependency-install      # T1: 依赖安装逻辑
  ├── feature/T2-differentiated-install  # T2: 差异化安装流程
  ├── feature/T3-health-check            # T3: 健康检查服务
  └── feature/T4-notification            # T4: 通知服务集成
```

### 5.2 提交规范

```
feat(install): 实现依赖安装逻辑
fix(activation): 修复激活流程状态转换问题
docs(classification): 更新分类体系文档
test(health-check): 添加健康检查单元测试
```

### 5.3 代码审查

- 每个 PR 需要至少 1 人审查
- 高优先级任务需要 2 人审查
- 审查重点：
  - 代码质量
  - 测试覆盖
  - 文档更新

---

## 六、进度跟踪

### 6.1 skill-scene 团队任务

| 阶段 | 任务 | 负责人 | 状态 | 开始时间 | 完成时间 |
|------|------|--------|:----:|---------|---------|
| P0 | T1: 依赖安装逻辑 | - | ✅ 已完成 | 2026-03-11 | 2026-03-11 |
| P0 | T2: 差异化安装流程 | - | ✅ 已完成 | 2026-03-11 | 2026-03-11 |
| P0 | T3: 健康检查服务 | - | ✅ 已完成 | 2026-03-11 | 2026-03-11 |
| P1 | T4: 通知服务集成 | - | ✅ 已完成 | 2026-03-11 | 2026-03-11 |
| P1 | T5: 菜单自动注册 | - | ✅ 已完成 | 2026-03-11 | 2026-03-11 |
| P1 | T6: 密钥管理 | - | ✅ 已完成 | 2026-03-11 | 2026-03-11 |
| P2 | T7: 更新文档 | - | ✅ 已完成 | 2026-03-11 | 2026-03-11 |
| P2 | T8: 安装回滚 | - | ✅ 已完成 | 2026-03-11 | 2026-03-11 |
| P2 | T9: 安装日志 | - | ✅ 已完成 | 2026-03-11 | 2026-03-11 |

### 6.2 SE SDK 团队任务

| ID | 任务 | 交付版本 | 发布日期 | 状态 |
|----|------|:--------:|:--------:|:----:|
| SE-1 | CapabilityInstallLifecycle 接口 | v2.3.2 | 2026-03-14 | ✅ 已集成 |
| SE-2 | NotificationService 接口 | v2.4.0 | 2026-03-20 | ✅ 已集成 |
| SE-3 | SceneEngineAutoConfiguration 类 | v2.3.2 | 2026-03-14 | ⬜ 待确认 |
| SE-4 | KnowledgeBindingService 接口 | v2.3.2 | 2026-03-14 | ⬜ 待确认 |

**SE任务状态说明**：
- ✅ 已集成 - SE SDK 接口已发布，skill-scene 已完成集成
- ⬜ 待确认 - 需要确认 SE SDK 是否已发布该接口

**状态说明**：
- ⬜ 待开始
- 🔄 进行中
- ✅ 已完成
- ⏸️ 暂停
- ❌ 取消

---

## 七、相关文档

- [能力地址空间设计 v5](./capability-address-space-design-v5.md)
- [场景技能分类规范](./scene-skill-types-specification.md)
- [安装激活规范](./scene-skill-installation-activation-spec.md)
- [SE SDK 协作响应](./SE_SDK_Collaboration_Response.md)

---

## 八、联系方式

如有问题，请在项目仓库提交 Issue 或联系项目维护者。

---

**文档版本**: 1.1.0  
**创建日期**: 2026-03-11  
**最后更新**: 2026-03-11

### 更新记录

| 版本 | 日期 | 更新内容 |
|-----|------|---------|
| v1.0.0 | 2026-03-11 | 初始版本 |
| v1.1.0 | 2026-03-11 | 添加 SE SDK 协作依赖说明，新增 SE SDK 团队任务章节 |

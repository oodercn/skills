# skill-scene 开发任务清单

> **文档版本**: 1.0.0  
> **整理日期**: 2026-03-09  
> **计划周期**: 4周  
> **状态**: 待批准

---

## 一、任务总览

### 1.1 任务分类统计

| 分类 | 任务数 | 预计工时 | 优先级 |
|------|:------:|:--------:|:------:|
| **A. 模板扩展** | 6 | 8天 | 高 |
| **B. 菜单系统** | 4 | 5天 | 高 |
| **C. 激活流程** | 5 | 6天 | 高 |
| **D. 依赖管理** | 3 | 4天 | 中 |
| **E. 页面开发** | 4 | 6天 | 中 |
| **F. 预制组件** | 6 | 20天 | 中 |
| **合计** | 28 | 49天 | - |

### 1.2 任务依赖关系

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        任务依赖关系图                                            │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  Phase 1: 基础设施 (Week 1)                                                      │
│  ├── A1: 扩展SceneTemplate类 ──────────────────────────────────────────────┐    │
│  ├── A2: 添加dependencies配置 ─────────────────────────────────────────────┤    │
│  ├── A3: 添加roles配置 ────────────────────────────────────────────────────┤    │
│  ├── A4: 添加activationSteps配置 ──────────────────────────────────────────┤    │
│  ├── A5: 添加menus配置 ────────────────────────────────────────────────────┤    │
│  └── A6: 添加uiSkills配置 ─────────────────────────────────────────────────┘    │
│                                                                                 │
│  Phase 2: 核心服务 (Week 2)                                                      │
│  ├── B1: 扩展MenuRoleConfigService ────────────────────────────────────────┐    │
│  ├── B2: 实现菜单自动注册 ─────────────────────────────────────────────────┤    │
│  ├── B3: 实现按角色菜单生成 ───────────────────────────────────────────────┤    │
│  └── B4: 实现场景销毁菜单清理 ─────────────────────────────────────────────┘    │
│                                                                                 │
│  Phase 3: 激活流程 (Week 3)                                                      │
│  ├── C1: 扩展激活流程服务 ─────────────────────────────────────────────────┐    │
│  ├── C2: 实现按角色激活步骤 ───────────────────────────────────────────────┤    │
│  ├── C3: 实现激活完成回调 ─────────────────────────────────────────────────┤    │
│  ├── C4: 实现员工激活流程 ─────────────────────────────────────────────────┤    │
│  └── C5: 实现私有能力配置 ─────────────────────────────────────────────────┘    │
│                                                                                 │
│  Phase 4: 完善功能 (Week 4)                                                      │
│  ├── D1: 实现依赖健康检查 ─────────────────────────────────────────────────┐    │
│  ├── D2: 实现自动安装依赖 ─────────────────────────────────────────────────┤    │
│  ├── D3: 实现依赖服务检查 ─────────────────────────────────────────────────┤    │
│  ├── E1: 创建历史查询页面 ─────────────────────────────────────────────────┐    │
│  ├── E2: 创建项目跟踪页面 ─────────────────────────────────────────────────┤    │
│  ├── E3: 创建团队日志页面 ─────────────────────────────────────────────────┤    │
│  └── E4: 创建日志提醒页面 ─────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、详细任务清单

### A. 模板扩展任务 (高优先级)

#### A1: 扩展SceneTemplate类

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-A1 |
| **任务名称** | 扩展SceneTemplate类添加新配置字段 |
| **优先级** | 高 |
| **预计工时** | 1天 |
| **依赖任务** | 无 |
| **交付物** | SceneTemplate.java (扩展) |

**任务详情**:
```java
// 需要添加的字段
public class SceneTemplate {
    // 现有字段...
    
    // 新增字段
    private DependenciesConfig dependencies;    // 依赖配置
    private List<RoleConfig> roles;             // 角色配置
    private Map<String, List<ActivationStepConfig>> activationSteps; // 按角色的激活步骤
    private Map<String, List<MenuConfig>> menus; // 按角色的菜单
    private List<UiSkillConfig> uiSkills;       // UI技能配置
    private List<PrivateCapabilityConfig> privateCapabilities; // 私有能力配置
    private String participantMode;             // 参与者模式: single-user, multi-role
}
```

---

#### A2: 添加dependencies配置

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-A2 |
| **任务名称** | 添加dependencies配置支持 |
| **优先级** | 高 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-A1 |
| **交付物** | DependenciesConfig.java, DependencyConfig.java |

**任务详情**:
```java
public class DependenciesConfig {
    private List<DependencyConfig> required;   // 必需依赖
    private List<DependencyConfig> optional;   // 可选依赖
}

public class DependencyConfig {
    private String skillId;
    private String version;
    private boolean autoInstall;
    private String healthCheck;    // 健康检查URL
    private String description;
}
```

---

#### A3: 添加roles配置

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-A3 |
| **任务名称** | 添加roles配置支持 |
| **优先级** | 高 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-A1 |
| **交付物** | RoleConfig.java |

**任务详情**:
```java
public class RoleConfig {
    private String name;           // 角色名称: MANAGER, EMPLOYEE, HR
    private String description;
    private boolean required;
    private int minCount;
    private int maxCount;
    private List<String> permissions;
}
```

---

#### A4: 添加activationSteps配置

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-A4 |
| **任务名称** | 添加activationSteps配置支持（按角色区分） |
| **优先级** | 高 |
| **预计工时** | 2天 |
| **依赖任务** | TASK-A1, TASK-A3 |
| **交付物** | ActivationStepConfig.java |

**任务详情**:
```java
public class ActivationStepConfig {
    private String stepId;
    private String name;
    private String description;
    private boolean required;
    private boolean skippable;
    private boolean autoExecute;
    private List<ActionConfig> actions;
    private List<String> privateCapabilities;  // 员工可选的私有能力
}

public class ActionConfig {
    private String type;          // push-notification, create-todo, send-email
    private String target;        // selected-users, all-users
    private Map<String, Object> params;
}
```

---

#### A5: 添加menus配置

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-A5 |
| **任务名称** | 添加menus配置支持（按角色区分） |
| **优先级** | 高 |
| **预计工时** | 2天 |
| **依赖任务** | TASK-A1, TASK-A3 |
| **交付物** | MenuConfig.java |

**任务详情**:
```java
public class MenuConfig {
    private String id;
    private String name;
    private String icon;
    private String url;
    private int order;
    private boolean visible;
    private List<MenuConfig> children;  // 子菜单
}
```

---

#### A6: 添加uiSkills配置

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-A6 |
| **任务名称** | 添加uiSkills配置支持 |
| **优先级** | 高 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-A1, TASK-A3 |
| **交付物** | UiSkillConfig.java |

**任务详情**:
```java
public class UiSkillConfig {
    private String id;
    private String name;
    private String entryUrl;
    private String icon;
    private List<String> roles;    // 可访问的角色
    private int order;
}
```

---

### B. 菜单系统任务 (高优先级)

#### B1: 扩展MenuRoleConfigService

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-B1 |
| **任务名称** | 扩展MenuRoleConfigService支持场景菜单 |
| **优先级** | 高 |
| **预计工时** | 2天 |
| **依赖任务** | TASK-A5 |
| **交付物** | MenuRoleConfigService.java (扩展) |

**任务详情**:
- 已完成部分: registerSceneMenus, getUserSceneMenus, removeSceneMenus
- 需要完成: 与激活流程的集成

---

#### B2: 实现菜单自动注册

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-B2 |
| **任务名称** | 实现激活完成时自动注册菜单 |
| **优先级** | 高 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-B1, TASK-C3 |
| **交付物** | MenuAutoRegisterService.java |

---

#### B3: 实现按角色菜单生成

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-B3 |
| **任务名称** | 实现根据角色生成不同菜单 |
| **优先级** | 高 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-B1 |
| **交付物** | RoleBasedMenuGenerator.java |

---

#### B4: 实现场景销毁菜单清理

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-B4 |
| **任务名称** | 实现场景销毁时自动清理菜单 |
| **优先级** | 高 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-B1 |
| **交付物** | MenuCleanupService.java |

---

### C. 激活流程任务 (高优先级)

#### C1: 扩展激活流程服务

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-C1 |
| **任务名称** | 扩展ActivationService支持模板配置 |
| **优先级** | 高 |
| **预计工时** | 2天 |
| **依赖任务** | TASK-A4 |
| **交付物** | ActivationService.java (扩展) |

---

#### C2: 实现按角色激活步骤

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-C2 |
| **任务名称** | 实现根据角色执行不同激活步骤 |
| **优先级** | 高 |
| **预计工时** | 2天 |
| **依赖任务** | TASK-C1 |
| **交付物** | RoleBasedActivationProcessor.java |

---

#### C3: 实现激活完成回调

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-C3 |
| **任务名称** | 实现激活完成后的回调处理 |
| **优先级** | 高 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-C1 |
| **交付物** | ActivationCallbackHandler.java |

**回调内容**:
- 菜单自动注册
- 通知发送
- 能力绑定
- 日志记录

---

#### C4: 实现员工激活流程

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-C4 |
| **任务名称** | 实现员工收到邀请后的激活流程 |
| **优先级** | 高 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-C2 |
| **交付物** | EmployeeActivationService.java |

---

#### C5: 实现私有能力配置

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-C5 |
| **任务名称** | 实现员工配置私有能力 |
| **优先级** | 中 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-C4, TASK-A6 |
| **交付物** | PrivateCapabilityService.java |

---

### D. 依赖管理任务 (中优先级)

#### D1: 实现依赖健康检查

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-D1 |
| **任务名称** | 实现依赖技能健康检查 |
| **优先级** | 中 |
| **预计工时** | 2天 |
| **依赖任务** | TASK-A2 |
| **交付物** | DependencyHealthChecker.java |

---

#### D2: 实现自动安装依赖

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-D2 |
| **任务名称** | 实现自动安装autoInstall=true的依赖 |
| **优先级** | 中 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-D1 |
| **交付物** | DependencyAutoInstaller.java |

---

#### D3: 实现依赖服务检查

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-D3 |
| **任务名称** | 实现MQTT、邮件、LLM等服务健康检查 |
| **优先级** | 中 |
| **预计工时** | 1天 |
| **依赖任务** | TASK-D1 |
| **交付物** | ServiceHealthChecker.java |

---

### E. 页面开发任务 (中优先级)

#### E1: 创建历史查询页面

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-E1 |
| **任务名称** | 创建日志历史查询页面 |
| **优先级** | 中 |
| **预计工时** | 2天 |
| **依赖任务** | 无 |
| **交付物** | daily-report-history.html, daily-report-history.js, daily-report-history.css |

---

#### E2: 创建项目跟踪页面

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-E2 |
| **任务名称** | 创建项目跟踪页面 |
| **优先级** | 中 |
| **预计工时** | 2天 |
| **依赖任务** | 无 |
| **交付物** | daily-report-tracking.html, daily-report-tracking.js, daily-report-tracking.css |

---

#### E3: 创建团队日志页面

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-E3 |
| **任务名称** | 创建团队日志查看页面 |
| **优先级** | 中 |
| **预计工时** | 1天 |
| **依赖任务** | 无 |
| **交付物** | daily-report-team.html, daily-report-team.js, daily-report-team.css |

---

#### E4: 创建日志提醒页面

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-E4 |
| **任务名称** | 创建日志提醒页面 |
| **优先级** | 中 |
| **预计工时** | 1天 |
| **依赖任务** | 无 |
| **交付物** | daily-report-reminder.html, daily-report-reminder.js, daily-report-reminder.css |

---

### F. 预制组件任务 (来自协作计划)

#### F1: 安装说明书解析器

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-F1 |
| **任务名称** | 解析LLM安装说明书 |
| **优先级** | 中 |
| **预计工时** | 3天 |
| **依赖任务** | 无 |
| **交付物** | InstallationManualParser.java |

---

#### F2: 工具定义注册表

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-F2 |
| **任务名称** | 管理LLM可调用的工具定义 |
| **优先级** | 中 |
| **预计工时** | 4天 |
| **依赖任务** | 无 |
| **交付物** | ToolRegistry.java |

---

#### F3: Schema验证器

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-F3 |
| **任务名称** | 验证LLM输出是否符合JSON Schema |
| **优先级** | 中 |
| **预计工时** | 3天 |
| **依赖任务** | 无 |
| **交付物** | SchemaValidator.java |

---

#### F4: 安装状态机

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-F4 |
| **任务名称** | 管理安装流程的状态流转 |
| **优先级** | 中 |
| **预计工时** | 4天 |
| **依赖任务** | 无 |
| **交付物** | InstallationStateMachine.java |

---

#### F5: 角色识别服务

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-F5 |
| **任务名称** | 识别用户在组织中的角色 |
| **优先级** | 中 |
| **预计工时** | 3天 |
| **依赖任务** | 无 |
| **交付物** | RoleDetectionService.java |

---

#### F6: 降级策略处理器

| 项目 | 内容 |
|------|------|
| **任务ID** | TASK-F6 |
| **任务名称** | 处理LLM不可用时的降级逻辑 |
| **优先级** | 中 |
| **预计工时** | 3天 |
| **依赖任务** | 无 |
| **交付物** | DegradationHandler.java |

---

## 三、执行计划

### 3.1 Week 1: 模板扩展

| 日期 | 任务 | 负责人 | 状态 |
|------|------|--------|------|
| Day 1 | TASK-A1: 扩展SceneTemplate类 | - | 待开始 |
| Day 2 | TASK-A2: 添加dependencies配置 | - | 待开始 |
| Day 2-3 | TASK-A3: 添加roles配置 | - | 待开始 |
| Day 3-4 | TASK-A4: 添加activationSteps配置 | - | 待开始 |
| Day 4-5 | TASK-A5: 添加menus配置 | - | 待开始 |
| Day 5 | TASK-A6: 添加uiSkills配置 | - | 待开始 |

### 3.2 Week 2: 菜单系统 + 激活流程

| 日期 | 任务 | 负责人 | 状态 |
|------|------|--------|------|
| Day 1-2 | TASK-B1: 扩展MenuRoleConfigService | - | 待开始 |
| Day 2-3 | TASK-B2: 实现菜单自动注册 | - | 待开始 |
| Day 3 | TASK-B3: 实现按角色菜单生成 | - | 待开始 |
| Day 4 | TASK-B4: 实现场景销毁菜单清理 | - | 待开始 |
| Day 4-5 | TASK-C1: 扩展激活流程服务 | - | 待开始 |

### 3.3 Week 3: 激活流程 + 依赖管理

| 日期 | 任务 | 负责人 | 状态 |
|------|------|--------|------|
| Day 1-2 | TASK-C2: 实现按角色激活步骤 | - | 待开始 |
| Day 2-3 | TASK-C3: 实现激活完成回调 | - | 待开始 |
| Day 3 | TASK-C4: 实现员工激活流程 | - | 待开始 |
| Day 4 | TASK-C5: 实现私有能力配置 | - | 待开始 |
| Day 4-5 | TASK-D1: 实现依赖健康检查 | - | 待开始 |

### 3.4 Week 4: 页面开发 + 预制组件

| 日期 | 任务 | 负责人 | 状态 |
|------|------|--------|------|
| Day 1-2 | TASK-E1: 创建历史查询页面 | - | 待开始 |
| Day 2-3 | TASK-E2: 创建项目跟踪页面 | - | 待开始 |
| Day 3 | TASK-E3: 创建团队日志页面 | - | 待开始 |
| Day 4 | TASK-E4: 创建日志提醒页面 | - | 待开始 |
| Day 4-5 | 集成测试与文档 | - | 待开始 |

---

## 四、验收标准

### 4.1 功能验收

- [ ] 场景模板支持dependencies、roles、activationSteps、menus、uiSkills配置
- [ ] 领导激活后自动生成领导专属菜单
- [ ] 员工激活后自动生成员工专属菜单
- [ ] 场景销毁后自动清理菜单
- [ ] 依赖技能自动检查和安装
- [ ] 所有页面正常访问和运行

### 4.2 代码验收

- [ ] 所有接口有完整的JavaDoc注释
- [ ] 所有实现类有单元测试（覆盖率≥80%）
- [ ] 所有配置有默认值和校验
- [ ] 所有错误有明确的错误码和消息

### 4.3 文档验收

- [ ] 规范文档完整
- [ ] API文档完整
- [ ] 使用示例可运行

---

## 五、批准签字

| 角色 | 姓名 | 签字 | 日期 |
|------|------|------|------|
| 项目负责人 | | | |
| 技术负责人 | | | |
| 开发负责人 | | | |

---

**文档状态**: 待批准  
**批准后**: 开始执行 Week 1 任务

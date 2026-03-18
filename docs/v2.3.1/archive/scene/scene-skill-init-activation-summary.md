# 场景技能初始化与激活任务总结

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 分析报告 |

---

## 一、场景技能分类与场景故事

### 1.1 现有场景技能类型

| 类型 | 简写 | 特征 | 示例场景 |
|------|------|------|---------|
| **自驱业务场景** | ABS | mainFirst=true + 业务语义完整 | 文档助手、新人培训 |
| **自驱系统场景** | ASS | mainFirst=true + 业务语义弱 | LLM对话、知识问答 |
| **触发业务场景** | TBS | mainFirst=false + 业务语义完整 | 会议纪要、项目知识 |

### 1.2 场景故事分析

| 场景技能 | 分类 | 场景故事 | 参与角色 |
|---------|------|---------|---------|
| skill-llm-chat | ASS | 用户与LLM进行智能对话，支持多轮对话和上下文感知 | 单用户 |
| skill-knowledge-qa | ASS | 用户查询知识库，系统自动检索并返回答案 | 单用户 |
| skill-document-assistant | ABS | 员工查询公司文档，系统自动回答问题 | 单用户 + 知识管理员 |
| skill-meeting-minutes | TBS | 会议结束后，整理会议纪要并提取行动项 | 组织者 + 参与者 |
| skill-onboarding-assistant | ABS | 新员工入职，系统自动创建学习路径并跟踪进度 | 新员工 + HR + 导师 |
| skill-project-knowledge | TBS | 项目结束后，自动沉淀项目知识 | 项目经理 + 团队成员 |

---

## 二、初始化任务需求

### 2.1 按场景类型的初始化任务

#### ABS (自驱业务场景) 初始化任务

| 任务 | 说明 | 声明位置 | SE支持 |
|------|------|---------|--------|
| selfCheck.capabilities | 检查必需能力是否就绪 | mainFirstConfig.selfCheck | ✅ 支持 |
| selfCheck.driverCapabilities | 检查驱动能力是否就绪 | mainFirstConfig.selfCheck | ✅ 支持 |
| selfCheck.dependencies | 检查依赖技能是否安装 | mainFirstConfig.selfCheck | ✅ 支持 |
| selfStart.initDriverCapabilities | 初始化驱动能力 | mainFirstConfig.selfStart | ✅ 支持 |
| selfStart.initCapabilities | 初始化业务能力 | mainFirstConfig.selfStart | ✅ 支持 |
| selfStart.bindAddresses | 绑定服务地址 | mainFirstConfig.selfStart | ✅ 支持 |
| startCollaboration | 启动协作场景 | mainFirstConfig.startCollaboration | ✅ 支持 |
| selfDrive.scheduleRules | 定时任务调度 | mainFirstConfig.selfDrive | ✅ 支持 |
| selfDrive.eventRules | 事件驱动规则 | mainFirstConfig.selfDrive | ✅ 支持 |
| selfDrive.capabilityChains | 能力调用链 | mainFirstConfig.selfDrive | ✅ 支持 |

#### ASS (自驱系统场景) 初始化任务

| 任务 | 说明 | 声明位置 | SE支持 |
|------|------|---------|--------|
| selfCheck.capabilities | 检查系统能力 | mainFirstConfig.selfCheck | ✅ 支持 |
| selfStart.initCapabilities | 初始化系统能力 | mainFirstConfig.selfStart | ✅ 支持 |
| selfDrive.eventRules | 系统事件响应 | mainFirstConfig.selfDrive | ✅ 支持 |

#### TBS (触发业务场景) 初始化任务

| 任务 | 说明 | 声明位置 | SE支持 |
|------|------|---------|--------|
| participants | 定义参与角色 | sceneCapabilities.participants | ✅ 支持 |
| visibility | 定义可见性 | sceneCapabilities.visibility | ✅ 支持 |
| driverConditions | 定义触发条件 | sceneCapabilities.driverConditions | ✅ 支持 |

---

## 三、激活任务需求

### 3.1 按场景类型的激活任务

#### ABS 激活任务

| 任务 | 说明 | 声明位置 | SE支持 |
|------|------|---------|--------|
| 配置知识库 | 设置知识库连接 | config.required | ⚠️ 部分支持 |
| 配置LLM | 设置LLM Provider | config.required | ⚠️ 部分支持 |
| 菜单生成 | 生成用户菜单 | 未定义 | ❌ 未实现 |
| 能力绑定 | 绑定能力到场景组 | capabilityBindings | ✅ 支持 |

#### ASS 激活任务

| 任务 | 说明 | 声明位置 | SE支持 |
|------|------|---------|--------|
| 配置存储路径 | 设置数据存储位置 | config.required | ✅ 支持 |
| 配置模型参数 | 设置LLM参数 | config.optional | ✅ 支持 |
| 菜单生成 | 生成用户菜单 | 未定义 | ❌ 未实现 |

#### TBS 激活任务

| 任务 | 说明 | 声明位置 | SE支持 |
|------|------|---------|--------|
| 确认参与者 | 选择场景参与者 | participants | ⚠️ 部分支持 |
| 配置触发条件 | 设置触发规则 | driverConditions | ⚠️ 部分支持 |
| 推送通知 | 通知参与者 | 未定义 | ❌ 未实现 |
| 菜单生成 | 生成角色菜单 | 未定义 | ❌ 未实现 |

---

## 四、SE 支持情况分析

### 4.1 已支持的配置项

| 配置项 | Java类 | 说明 |
|--------|--------|------|
| mainFirstConfig | MainFirstConfig | 自驱配置完整支持 |
| selfCheck | CheckStep | 自检步骤支持 |
| selfStart | StartStep | 启动步骤支持 |
| selfDrive | DriveConfig | 自驱规则支持 |
| scheduleRules | ScheduleRule | 定时调度支持 |
| eventRules | EventRule | 事件规则支持 |
| capabilityChains | CapabilityChain | 能力链支持 |
| participants | Participant | 参与者支持 |
| visibility | Capability.visibility | 可见性支持 |
| driverConditions | DriverCondition | 驱动条件支持 |
| dependencies | Capability.dependencies | 依赖声明支持 |

### 4.2 未支持的配置项

| 配置项 | 说明 | 建议 |
|--------|------|------|
| activationSteps | 激活流程步骤 | 需新增 ActivationStep 类 |
| menus | 菜单配置 | 需新增 MenuConfig 类 |
| menuItems | 菜单项配置 | 需新增 MenuItem 类 |
| roles | 角色配置 | 需扩展 Participant |
| privateCapabilities | 私有能力配置 | 需新增配置项 |
| networkActions | 入网动作 | 需新增配置项 |
| getKey | 获取密钥 | 需新增配置项 |

### 4.3 SE 支持矩阵

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        SE 支持情况矩阵                                            │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  初始化阶段                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  ✅ selfCheck          - 完整支持                                        │    │
│  │  ✅ selfStart          - 完整支持                                        │    │
│  │  ✅ selfDrive          - 完整支持                                        │    │
│  │  ✅ startCollaboration - 完整支持                                        │    │
│  │  ✅ dependencies       - 完整支持                                        │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  激活阶段                                                                        │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  ✅ capabilityBindings - 完整支持                                        │    │
│  │  ✅ config             - 完整支持                                        │    │
│  │  ⚠️ participants       - 部分支持（缺少角色菜单配置）                      │    │
│  │  ⚠️ driverConditions   - 部分支持（缺少激活流程绑定）                      │    │
│  │  ❌ activationSteps    - 未实现                                          │    │
│  │  ❌ menus              - 未实现                                          │    │
│  │  ❌ networkActions     - 未实现                                          │    │
│  │  ❌ getKey             - 未实现                                          │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 五、建议的 skill.yaml 扩展

### 5.1 激活流程配置扩展

```yaml
sceneCapabilities:
  - id: scene-daily-report
    name: 日志汇报场景
    type: SCENE
    mainFirst: true
    
    # 现有配置
    capabilities:
      - report-remind
      - report-submit
    
    participants:
      - role: MANAGER
        name: 场景管理者
        permissions: [READ, WRITE, CONFIG]
      - role: EMPLOYEE
        name: 普通员工
        permissions: [READ, WRITE]
    
    visibility: TEAM
    
    # 建议新增：激活流程配置
    activationSteps:
      MANAGER:
        - stepId: confirm-participants
          name: 确认参与者
          required: true
          type: SELECT_USERS
        - stepId: select-push-targets
          name: 选择推送目标
          required: true
          type: SELECT_USERS
        - stepId: config-conditions
          name: 配置驱动条件
          required: true
          type: CONFIG_FORM
        - stepId: get-key
          name: 获取KEY
          required: false
          type: OAUTH
        - stepId: confirm-activation
          name: 确认激活
          required: true
          type: CONFIRM
        - stepId: network-actions
          name: 入网动作
          required: true
          type: AUTO
      EMPLOYEE:
        - stepId: confirm-join
          name: 确认加入场景
          required: true
          type: CONFIRM
        - stepId: config-private-capabilities
          name: 配置私有能力
          required: false
          type: SELECT_CAPABILITIES
        - stepId: confirm-activation
          name: 确认激活
          required: true
          type: CONFIRM
    
    # 建议新增：菜单配置
    menus:
      MANAGER:
        - id: write-log
          name: 填写日志
          icon: ri-edit-line
          url: /console/pages/daily-report-form.html
          order: 1
        - id: team-logs
          name: 团队日志
          icon: ri-team-line
          url: /console/pages/daily-report-team.html
          order: 2
      EMPLOYEE:
        - id: log-reminder
          name: 日志提醒
          icon: ri-notification-3-line
          url: /console/pages/daily-report-reminder.html
          order: 1
        - id: write-log
          name: 填写日志
          icon: ri-edit-line
          url: /console/pages/daily-report-form.html
          order: 2
    
    # 建议新增：私有能力配置
    privateCapabilities:
      - capId: email-skill
        name: 邮件能力
        description: 自动获取邮件内容
        optional: true
      - capId: git-skill
        name: Git Skill
        description: 自动获取代码提交记录
        optional: true
```

### 5.2 需要新增的 Java 类

```java
// ActivationStep.java
public class ActivationStep {
    private String stepId;
    private String name;
    private boolean required;
    private String type;  // SELECT_USERS, CONFIG_FORM, OAUTH, CONFIRM, AUTO, SELECT_CAPABILITIES
    private Map<String, Object> config;
}

// MenuConfig.java
public class MenuConfig {
    private String role;
    private List<MenuItem> items;
}

// MenuItem.java
public class MenuItem {
    private String id;
    private String name;
    private String icon;
    private String url;
    private int order;
}

// PrivateCapability.java
public class PrivateCapability {
    private String capId;
    private String name;
    private String description;
    private boolean optional;
}
```

---

## 六、实施建议

### 6.1 第一阶段：完善激活流程支持

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 新增 ActivationStep 类 | P0 | 支持激活流程配置 |
| 新增 MenuConfig 类 | P0 | 支持菜单配置 |
| 扩展 Participant | P1 | 支持角色菜单绑定 |
| 实现菜单生成服务 | P1 | 根据角色生成菜单 |

### 6.2 第二阶段：完善激活流程执行

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 实现激活流程引擎 | P0 | 执行激活步骤 |
| 实现推送通知服务 | P1 | 支持入网动作 |
| 实现私有能力绑定 | P1 | 支持员工私有能力配置 |
| 实现 KEY 获取流程 | P2 | 支持 OAuth 等认证 |

### 6.3 第三阶段：完善 UI 支持

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 激活流程向导页面 | P0 | 引导用户完成激活 |
| 菜单管理页面 | P1 | 管理场景菜单 |
| 私有能力配置页面 | P1 | 配置私有能力 |

---

## 七、总结

### 7.1 现状

- **初始化阶段**: SE 已完整支持 selfCheck、selfStart、selfDrive 等配置
- **激活阶段**: SE 仅部分支持，缺少激活流程、菜单配置等关键功能

### 7.2 差距

| 功能 | 规范要求 | SE现状 | 差距 |
|------|---------|--------|------|
| 激活流程配置 | activationSteps | 未实现 | 需新增 |
| 菜单配置 | menus | 未实现 | 需新增 |
| 角色菜单绑定 | roles.menuItems | 未实现 | 需扩展 |
| 私有能力配置 | privateCapabilities | 未实现 | 需新增 |
| 入网动作 | networkActions | 未实现 | 需新增 |

### 7.3 建议

1. **优先实现菜单配置** - 这是激活后用户最直接感知的功能
2. **其次实现激活流程** - 这是多角色场景的核心功能
3. **最后实现私有能力** - 这是增强用户体验的可选功能

---

**文档状态**: 分析报告  
**下一步**: 根据实施建议进行开发

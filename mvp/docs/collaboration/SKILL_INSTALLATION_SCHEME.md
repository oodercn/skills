# Ooder Skills 安装方案完整设计文档

> **文档版本**: v2.3.1  
> **创建日期**: 2026-03-21  
> **状态**: 设计规范  
> **适用范围**: 所有技能分类

---

## 一、技能分类与安装类型

### 1.1 技能分类体系

根据 `skill-index/categories.yaml`，技能分为以下类型：

| 分类ID | 名称 | 安装类型 | 场景驱动 | 用户可见 |
|--------|------|----------|----------|----------|
| **org** | 组织服务 | PROVIDER | org | ❌ |
| **vfs** | 存储服务 | PROVIDER | vfs | ❌ |
| **llm** | LLM服务 | PROVIDER/SCENE | - | ✅ |
| **knowledge** | 知识服务 | SCENE | - | ✅ |
| **biz** | 业务场景 | SCENE | - | ✅ |
| **sys** | 系统管理 | PROVIDER/SCENE | sys | ❌ |
| **msg** | 消息通讯 | PROVIDER | msg | ❌ |
| **ui** | UI生成 | PROVIDER | - | ❌ |
| **payment** | 支付服务 | PROVIDER | payment | ❌ |
| **media** | 媒体发布 | PROVIDER | media | ❌ |
| **util** | 工具服务 | PROVIDER/SCENE | - | ✅ |
| **nexus-ui** | Nexus界面 | PROVIDER | - | ❌ |

### 1.2 安装类型定义

根据 `InstallServiceImpl.java` 中的 `skillForm` 字段：

```java
public enum SkillForm {
    PROVIDER,      // 独立能力提供者
    SCENE,         // 场景能力
    STANDALONE     // 独立运行
}
```

| 安装类型 | 说明 | 安装后状态 | 入口位置 |
|----------|------|------------|----------|
| **PROVIDER** | 独立能力提供者，被其他技能依赖 | INSTALLED | 不可见，作为依赖 |
| **SCENE** | 场景能力，需要激活 | PENDING_ACTIVATION | 场景管理/菜单 |
| **STANDALONE** | 独立运行，无需激活 | INSTALLED | 工具列表 |

---

## 二、安装流程详细设计

### 2.1 安装流程图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         技能安装完整流程                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐                  │
│  │ 1. 发现技能   │───▶│ 2. 创建安装   │───▶│ 3. 执行安装   │                  │
│  │              │    │   配置        │    │              │                  │
│  └──────────────┘    └──────────────┘    └──────┬───────┘                  │
│                                                  │                           │
│                              ┌───────────────────┼───────────────────┐      │
│                              ▼                   ▼                   ▼      │
│                      ┌──────────────┐    ┌──────────────┐    ┌──────────────┐│
│                      │ PROVIDER     │    │ SCENE        │    │ STANDALONE   ││
│                      │ 独立能力     │    │ 场景能力     │    │ 独立运行     ││
│                      └──────┬───────┘    └──────┬───────┘    └──────┬───────┘│
│                             │                   │                   │        │
│                             ▼                   ▼                   ▼        │
│                      ┌──────────────┐    ┌──────────────┐    ┌──────────────┐│
│                      │ INSTALLED    │    │PENDING_      │    │ INSTALLED   ││
│                      │ 已安装       │    │ACTIVATION    │    │ 已安装      ││
│                      └──────────────┘    │ 待激活       │    └──────────────┘│
│                                          └──────┬───────┘                     │
│                                                 │                             │
│                                                 ▼                             │
│                                          ┌──────────────┐                     │
│                                          │ 4. 激活场景   │                     │
│                                          └──────┬───────┘                     │
│                                                 │                             │
│                              ┌──────────────────┼───────────────────┐        │
│                              ▼                   ▼                   ▼        │
│                      ┌──────────────┐    ┌──────────────┐    ┌──────────────┐│
│                      │ AUTO+internal│    │ AUTO+public │    │ TRIGGER/     ││
│                      │ 自动激活运行 │    │ 用户确认激活│    │ INTERACTIVE  ││
│                      │ RUNNING      │    │ SCHEDULED   │    │ ENABLED      ││
│                      └──────────────┘    └──────────────┘    └──────────────┘│
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 安装状态流转

```java
// InstallServiceImpl.java 中的状态判断逻辑
private InstallConfig.InstallStatus determinePostInstallStatus(InstallConfig config) {
    String skillForm = config.getSkillForm();
    String sceneType = config.getSceneType();
    String visibility = config.getVisibility();
    
    // 非场景类型：直接进入待激活状态
    if (!"SCENE".equals(skillForm)) {
        return InstallConfig.InstallStatus.PENDING_ACTIVATION;
    }
    
    // 场景类型：根据 sceneType 和 visibility 决定
    if ("AUTO".equals(sceneType)) {
        if ("internal".equals(visibility)) {
            return InstallConfig.InstallStatus.RUNNING;  // 自动运行
        } else {
            return InstallConfig.InstallStatus.PENDING_ACTIVATION;  // 待用户激活
        }
    } else if ("TRIGGER".equals(sceneType)) {
        return InstallConfig.InstallStatus.PENDING_ACTIVATION;  // 待配置触发器
    }
    
    return InstallConfig.InstallStatus.PENDING_ACTIVATION;
}
```

---

## 三、不同类型技能的安装入口

### 3.1 入口位置矩阵

| 分类 | 安装入口 | 菜单位置 | 用户配置 |
|------|----------|----------|----------|
| **llm** | 技能市场 → LLM分类 | 智能助手菜单 | Provider配置 |
| **knowledge** | 技能市场 → 知识分类 | 知识库菜单 | 存储配置 |
| **biz** | 技能市场 → 业务分类 | 业务场景菜单 | 角色配置、菜单配置 |
| **util** | 技能市场 → 工具分类 | 工具菜单 | 无 |
| **org** | 系统设置 → 组织集成 | 系统管理 | 认证配置 |
| **vfs** | 系统设置 → 存储配置 | 系统管理 | 存储路径配置 |
| **sys** | 系统设置 → 系统管理 | 系统管理 | 监控配置 |
| **msg** | 系统设置 → 消息配置 | 系统管理 | Broker配置 |
| **payment** | 系统设置 → 支付配置 | 系统管理 | 商户配置 |
| **media** | 技能市场 → 媒体分类 | 媒体发布菜单 | 账号授权 |
| **ui** | 开发者工具 | 开发者菜单 | 无 |
| **nexus-ui** | 系统自动安装 | Nexus界面 | 无 |

### 3.2 场景模板与技能包的关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      场景模板 vs 技能包                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  场景模板 (SceneTemplate)                                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │ 位置: skill-scene/src/main/resources/templates/*.yaml               │    │
│  │ 加载: SceneTemplateLoader (@PostConstruct)                          │    │
│  │ 作用: 定义场景的完整安装配置                                          │    │
│  │                                                                      │    │
│  │ 包含:                                                                │    │
│  │   - skills: 技能列表和版本要求                                        │    │
│  │   - capabilities: 能力定义                                           │    │
│  │   - roles: 角色配置                                                  │    │
│  │   - activationSteps: 激活步骤                                        │    │
│  │   - menus: 菜单配置                                                  │    │
│  │   - scene: 场景配置                                                  │    │
│  │   - installOrder: 安装顺序                                           │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  技能包 (skill.yaml)                                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │ 位置: skills/{category}/{skill-id}/skill.yaml                       │    │
│  │ 加载: SkillPackageManager.install()                                  │    │
│  │ 作用: 定义单个技能的元数据和配置                                       │    │
│  │                                                                      │    │
│  │ 包含:                                                                │    │
│  │   - metadata: 基本信息 (id, name, version, category)                 │    │
│  │   - capabilities: 提供的能力                                         │    │
│  │   - dependencies: 依赖的其他技能                                      │    │
│  │   - config: 配置模式 (JSON Schema)                                   │    │
│  │   - roles: 角色定义                                                  │    │
│  │   - activationSteps: 激活步骤                                        │    │
│  │   - menus: 菜单定义                                                  │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  关系: 场景模板引用技能包，一个场景模板可包含多个技能包                        │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、场景模板规范

### 4.1 场景模板必须字段

根据 `SceneTemplate.java` 定义：

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneTemplate

metadata:
  id: {template-id}           # 必需：模板唯一标识
  name: {模板名称}             # 必需：显示名称
  description: {描述}          # 必需：功能描述
  category: {分类}             # 必需：所属分类
  icon: {图标}                 # 可选：图标
  version: {版本}              # 必需：模板版本
  author: {作者}               # 可选：作者
  participantMode: {模式}      # 可选：single-user | multi-user

spec:
  skills:                      # 必需：技能列表
    - id: {skill-id}
      version: ">=x.x.x"
      required: true/false
      description: "说明"
      
  capabilities:                # 必需：能力定义
    - id: {cap-id}
      name: {能力名称}
      description: {描述}
      category: service | ai | data
      
  roles:                       # SCENE类型必需：角色配置
    - id: {role-id}
      name: {角色名称}
      permissions: [...]
      
  activationSteps:             # SCENE类型必需：激活步骤
    {role-id}:
      - step: 1
        action: {动作}
        description: {说明}
        
  menus:                       # SCENE类型必需：菜单配置
    {role-id}:
      - id: {menu-id}
        name: {菜单名称}
        icon: {图标}
        path: {路由}
        
  scene:                       # 必需：场景配置
    type: {场景类型}
    name: {场景名称}
    description: {描述}
    config:
      {...}
      
  installOrder:                # 可选：安装顺序
    - skill-1
    - skill-2
```

### 4.2 哪些分类需要场景模板？

| 分类 | 需要场景模板 | 原因 |
|------|:------------:|------|
| **llm** | ✅ | 用户可见，需要菜单和激活步骤 |
| **knowledge** | ✅ | 用户可见，需要配置知识库 |
| **biz** | ✅ | 用户可见，需要角色和菜单配置 |
| **util** | ⚠️ 部分 | 工具类技能，部分需要场景模板 |
| **org** | ❌ | 系统级，通过场景驱动管理 |
| **vfs** | ❌ | 系统级，通过场景驱动管理 |
| **sys** | ⚠️ 部分 | 监控类需要场景模板 |
| **msg** | ❌ | 系统级，通过场景驱动管理 |
| **payment** | ❌ | 系统级，通过场景驱动管理 |
| **media** | ⚠️ 部分 | 发布类需要场景模板 |
| **ui** | ❌ | 开发者工具，不需要场景模板 |
| **nexus-ui** | ❌ | 系统内置，不需要场景模板 |

---

## 五、安装过程详细分析

### 5.1 InstallServiceImpl 核心流程

```java
// 1. 创建安装配置
public InstallConfig createInstall(CreateInstallRequest request) {
    // 1.1 生成安装ID
    String installId = "install-" + System.currentTimeMillis();
    
    // 1.2 创建配置对象
    InstallConfig config = new InstallConfig();
    config.setInstallId(installId);
    config.setCapabilityId(request.getCapabilityId());
    
    // 1.3 确定场景类型和可见性
    determineSceneTypeAndVisibility(config, request.getCapabilityId());
    
    // 1.4 确定下一步操作
    List<String> nextSteps = determineNextSteps(config);
    config.setNextSteps(nextSteps);
    
    return config;
}

// 2. 执行安装
public CompletableFuture<InstallConfig> executeInstall(String installId) {
    return CompletableFuture.supplyAsync(() -> {
        // 2.1 获取能力信息
        Capability capability = capabilityService.findById(config.getCapabilityId());
        
        // 2.2 获取依赖列表
        List<String> dependencyIds = capability.getDependencies();
        
        // 2.3 安装依赖
        for (String depId : dependencyIds) {
            installDependency(depId);
        }
        
        // 2.4 更新状态
        InstallConfig.InstallStatus targetStatus = determinePostInstallStatus(config);
        config.setStatus(targetStatus);
        
        // 2.5 创建待办事项（如果需要激活）
        if (targetStatus == InstallConfig.InstallStatus.PENDING_ACTIVATION) {
            todoService.createActivationTodo(leaderId, installId, capabilityId, capabilityName);
        }
        
        return config;
    });
}
```

### 5.2 缺失的场景模板摄入逻辑

**问题**: 当前 `InstallServiceImpl` 没有从技能包读取场景配置！

```java
// 当前实现 - 缺失场景配置读取
private void determineSceneTypeAndVisibility(InstallConfig config, String capabilityId) {
    Capability cap = capabilityService.findById(capabilityId);
    // 只从 Capability 读取，没有从 skill.yaml 读取
    config.setSkillForm(cap.getSkillForm().getCode());
    config.setSceneType(cap.getSceneType());
    config.setVisibility(cap.getVisibility());
}
```

**需要增强**:

```java
// 建议实现 - 从技能包读取场景配置
private void determineSceneTypeAndVisibility(InstallConfig config, String capabilityId) {
    // 1. 从 Capability 读取基本信息
    Capability cap = capabilityService.findById(capabilityId);
    
    // 2. 从技能包读取场景配置（新增）
    SkillPackage skillPackage = skillPackageManager.getSkillPackage(capabilityId);
    if (skillPackage != null && skillPackage.getSceneConfig() != null) {
        SceneConfig sceneConfig = skillPackage.getSceneConfig();
        config.setSceneType(sceneConfig.getType());
        config.setVisibility(sceneConfig.getVisibility());
        config.setRoles(sceneConfig.getRoles());
        config.setActivationSteps(sceneConfig.getActivationSteps());
        config.setMenus(sceneConfig.getMenus());
    } else {
        // 3. 回退到 Capability 默认值
        config.setSkillForm(cap.getSkillForm().getCode());
        config.setSceneType(cap.getSceneType());
        config.setVisibility(cap.getVisibility());
    }
}
```

---

## 六、skill.yaml 完整配置规范

### 6.1 配置模板

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillPackage

metadata:
  id: {skill-id}
  name: {技能名称}
  version: "2.3.1"
  category: {分类}
  description: {描述}
  author: {作者}
  icon: {图标}

spec:
  # 能力定义
  capabilities:
    - id: {cap-id}
      name: {能力名称}
      description: {描述}
      category: service | ai | data | trigger
      
  # 依赖技能
  dependencies:
    - skillId: {dep-skill-id}
      version: ">=x.x.x"
      required: true/false
      
  # 技能形式
  skillForm: PROVIDER | SCENE | STANDALONE
  
  # 场景配置（SCENE类型必需）
  scene:
    type: AUTO | TRIGGER | INTERACTIVE
    visibility: public | internal
    driver: {scene-driver-id}
    
  # 角色配置（SCENE类型必需）
  roles:
    - id: admin
      name: 管理员
      permissions:
        - manage
        - configure
    - id: user
      name: 用户
      permissions:
        - use
        
  # 激活步骤（SCENE类型必需）
  activationSteps:
    admin:
      - step: 1
        action: configure-storage
        title: 配置存储
        description: 选择知识库存储位置
        required: true
      - step: 2
        action: import-documents
        title: 导入文档
        description: 上传知识库文档
        required: false
    user:
      - step: 1
        action: accept-invitation
        title: 接受邀请
        description: 加入场景
        required: true
        
  # 菜单配置（SCENE类型必需）
  menus:
    admin:
      - id: dashboard
        name: 概览
        icon: ri-dashboard-line
        path: /{scene-id}/dashboard
        order: 1
      - id: documents
        name: 文档管理
        icon: ri-folder-line
        path: /{scene-id}/documents
        order: 2
    user:
      - id: search
        name: 知识检索
        icon: ri-search-line
        path: /{scene-id}/search
        order: 1
        
  # 配置模式
  configSchema:
    type: object
    properties:
      storageType:
        type: string
        enum: [local, database, oss]
        default: local
      maxDocuments:
        type: integer
        default: 1000
        
  # 安装顺序
  installOrder:
    - {dep-skill-1}
    - {dep-skill-2}
    - {skill-id}
```

### 6.2 为 skill-recruitment-management 创建完整配置

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillPackage

metadata:
  id: skill-recruitment-management
  name: 招聘管理系统
  version: "2.3.1"
  category: biz
  subCategory: hr
  description: 企业招聘全流程管理系统，支持职位发布、简历收集、面试安排、录用审批
  author: Ooder Team
  icon: ri-user-add-line
  tags:
    - recruitment
    - hr
    - hiring
    - interview

spec:
  capabilities:
    - id: job-posting
      name: 职位发布
      description: 创建、编辑、发布招聘职位
      category: service
    - id: resume-collection
      name: 简历收集
      description: 接收、解析、存储候选人简历
      category: service
    - id: interview-scheduling
      name: 面试安排
      description: 安排面试时间、地点、面试官
      category: service
    - id: offer-approval
      name: 录用审批
      description: 发起录用审批流程
      category: service
      
  dependencies:
    - skillId: skill-vfs-base
      version: ">=2.3.1"
      required: true
      description: 文件存储服务（简历存储）
    - skillId: skill-approval-form
      version: ">=2.3.1"
      required: true
      description: 审批表单服务（录用审批）
    - skillId: skill-msg
      version: ">=2.3.1"
      required: false
      description: 消息通知服务（面试提醒）
      
  skillForm: SCENE
  
  scene:
    type: INTERACTIVE
    visibility: public
    driver: null
    
  roles:
    - id: hr-manager
      name: HR管理员
      description: 管理招聘流程和职位
      permissions:
        - manage-jobs
        - view-resumes
        - schedule-interviews
        - approve-offers
    - id: interviewer
      name: 面试官
      description: 参与面试评估
      permissions:
        - view-assigned-resumes
        - submit-feedback
    - id: candidate
      name: 候选人
      description: 查看职位和提交简历
      permissions:
        - view-jobs
        - submit-resume
        
  activationSteps:
    hr-manager:
      - step: 1
        action: configure-departments
        title: 配置部门
        description: 设置招聘部门信息
        required: true
      - step: 2
        action: configure-workflow
        title: 配置流程
        description: 设置招聘审批流程
        required: true
      - step: 3
        action: invite-interviewers
        title: 邀请面试官
        description: 添加面试官账号
        required: false
    interviewer:
      - step: 1
        action: accept-invitation
        title: 接受邀请
        description: 加入招聘团队
        required: true
    candidate:
      - step: 1
        action: register
        title: 注册账号
        description: 创建候选人账号
        required: true
        
  menus:
    hr-manager:
      - id: dashboard
        name: 招聘概览
        icon: ri-dashboard-line
        path: /recruitment/dashboard
        order: 1
      - id: jobs
        name: 职位管理
        icon: ri-briefcase-line
        path: /recruitment/jobs
        order: 2
      - id: resumes
        name: 简历管理
        icon: ri-file-user-line
        path: /recruitment/resumes
        order: 3
      - id: interviews
        name: 面试安排
        icon: ri-calendar-line
        path: /recruitment/interviews
        order: 4
      - id: offers
        name: 录用管理
        icon: ri-user-follow-line
        path: /recruitment/offers
        order: 5
    interviewer:
      - id: my-interviews
        name: 我的面试
        icon: ri-calendar-check-line
        path: /recruitment/my-interviews
        order: 1
      - id: feedback
        name: 面试反馈
        icon: ri-chat-check-line
        path: /recruitment/feedback
        order: 2
    candidate:
      - id: jobs
        name: 浏览职位
        icon: ri-search-line
        path: /recruitment/jobs
        order: 1
      - id: my-application
        name: 我的申请
        icon: ri-file-list-line
        path: /recruitment/my-application
        order: 2
        
  configSchema:
    type: object
    properties:
      resumeStorage:
        type: string
        enum: [local, database, oss]
        default: database
        title: 简历存储位置
      maxResumeSize:
        type: integer
        default: 10
        title: 最大简历大小(MB)
      interviewReminder:
        type: boolean
        default: true
        title: 面试提醒
      approvalWorkflow:
        type: string
        default: default
        title: 审批流程
        
  installOrder:
    - skill-vfs-base
    - skill-approval-form
    - skill-msg
    - skill-recruitment-management
    
  estimatedResources:
    cpu: "500m"
    memory: "512Mi"
    storage: "1Gi"
    
  estimatedDuration: "5-10分钟"
```

---

## 七、SE SDK 需要增强的功能

### 7.1 当前缺失

| 功能 | 当前状态 | 需要增强 |
|------|----------|----------|
| 从skill.yaml读取场景配置 | ❌ 未实现 | loadSceneConfigFromSkillPackage() |
| 验证场景配置完整性 | ❌ 未实现 | validateSceneConfig() |
| 创建场景时应用角色配置 | ⚠️ 部分 | applyRolesToScene() |
| 创建场景时应用菜单配置 | ❌ 未实现 | applyMenusToScene() |
| 执行激活步骤 | ❌ 未实现 | executeActivationSteps() |

### 7.2 建议接口设计

```java
public interface SceneConfigLoader {
    
    /**
     * 从技能包加载场景配置
     */
    SceneConfig loadSceneConfigFromSkillPackage(String skillId);
    
    /**
     * 验证场景配置完整性
     */
    ValidationResult validateSceneConfig(SceneConfig config);
    
    /**
     * 应用角色配置到场景
     */
    void applyRolesToScene(String sceneId, List<RoleConfig> roles);
    
    /**
     * 应用菜单配置到场景
     */
    void applyMenusToScene(String sceneId, Map<String, List<MenuConfig>> menus);
    
    /**
     * 执行激活步骤
     */
    void executeActivationSteps(String sceneId, String roleId, List<ActivationStepConfig> steps);
}
```

---

## 八、总结

### 8.1 关键发现

1. **场景模板与技能包分离**：当前场景模板在应用级别，技能定义在技能包级别，两者未关联
2. **安装过程缺失场景配置读取**：InstallServiceImpl 没有从技能包读取 roles、menus、activationSteps
3. **场景模板不是所有分类都需要**：只有用户可见的场景类型技能需要

### 8.2 下一步行动

| 优先级 | 任务 | 负责团队 |
|:------:|------|----------|
| P0 | SE SDK 增强 loadSceneConfigFromSkillPackage | SE SDK |
| P0 | SE SDK 增强 validateSceneConfig | SE SDK |
| P1 | Skills 团队完善 skill.yaml 配置 | Skills |
| P1 | 创建招聘管理场景模板 | Skills |
| P2 | 统一场景模板和技能包配置格式 | 双方 |

---

**文档维护**: Skills 团队  
**最后更新**: 2026-03-21

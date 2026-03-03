# 故事1：能力发现与安装闭环需求规格

## 文档信息

| 属性 | 值 |
|------|-----|
| **文档版本** | 1.0 |
| **编写日期** | 2026-03-02 |
| **故事编号** | STORY-001 |
| **优先级** | P0 |

---

## 一、故事概述

### 1.1 故事名称

**能力发现与安装闭环** - 用户发现场景能力，配置参与者，安装依赖，确认激活的完整闭环流程。

### 1.2 故事背景

当前系统采用"模板驱动"的设计，用户需要先选择场景模板才能创建场景。本故事将改为"能力驱动"设计，用户直接发现和安装能力，能力自动驱动场景创建和运行。

### 1.3 核心概念

| 概念 | 定义 |
|------|------|
| **场景能力** | 独立分支，可单独安装/启动的能力，核心功能是驱动能力运行 |
| **协作能力** | 可选补充能力，作为场景的协作组件 |
| **驱动条件** | 场景能力的运行条件，不同条件产生不同闭环分支 |
| **主导者** | 场景的启动者，拥有激活权限 |
| **协作者** | 场景的参与者，激活后才可见 |
| **入网动作** | 激活后的一系列自动化动作（获取KEY、通知、建立联系） |

---

## 二、能力分类规范

### 2.1 场景能力

**定义**：可独立安装和启动的能力，核心功能是驱动能力运行。

**特征**：
- 可单独安装/启动
- 包含驱动条件定义
- 不同驱动条件产生不同闭环分支
- 需要主导者激活

**示例**：
| 场景能力 | 驱动条件分支 |
|----------|-------------|
| 日志汇报 | 每日日志 / 周报 / 项目日志 |
| 审批流程 | 请假审批 / 报销审批 / 合同审批 |
| 项目管理 | 敏捷开发 / 瀑布模型 / 看板管理 |
| 会议管理 | 日常会议 / 项目会议 / 决策会议 |

### 2.2 协作能力

**定义**：可选补充能力，作为场景的协作组件。

**特征**：
- 不能独立启动
- 需要绑定到场景能力
- 可被多个场景能力复用

**示例**：
| 协作能力 | 说明 |
|----------|------|
| 通知能力 | 消息推送、邮件通知 |
| 存储能力 | 文件存储、数据存储 |
| 检索能力 | 知识检索、文档搜索 |
| LLM能力 | 智能对话、文本生成 |

---

## 三、用户故事

### 3.1 步骤1：发现能力

**用户角色**：所有用户

**用户故事**：
> 作为用户，我希望在能力发现页面看到场景能力和协作能力的分类展示，以便我选择需要安装的能力。

**验收标准**：
- [ ] 能力发现页面展示能力分类（场景能力 / 协作能力）
- [ ] 场景能力有明确的标识（如 📦 图标）
- [ ] 协作能力有明确的标识（如 🔧 图标）
- [ ] 可以按类型过滤能力
- [ ] 显示能力的支持场景类型

### 3.2 步骤2：确定参与者和驱动条件

**用户角色**：主导者

**用户故事**：
> 作为主导者，我希望在安装场景能力时选择驱动条件和确定参与者，以便配置场景的运行方式。

**验收标准**：
- [ ] 选择场景能力后显示驱动条件选项
- [ ] 可以选择不同的驱动条件分支
- [ ] 可以添加主导者（自己或他人）
- [ ] 可以添加协作者
- [ ] 可以选择可选的协作能力
- [ ] 显示驱动条件对应的闭环预览

### 3.3 步骤3：安装依赖并推送分享

**用户角色**：系统

**用户故事**：
> 作为系统，我希望在用户确认安装后自动解析依赖、安装能力、打包配置并推送给参与者。

**验收标准**：
- [ ] 自动解析场景能力的依赖
- [ ] 自动安装所有依赖能力
- [ ] 打包配置（角色、权限、驱动条件）
- [ ] 支持指定推送（委派）- 强制使用
- [ ] 主导者收到"待激活"通知
- [ ] 协作者暂不可见（等待激活）

### 3.4 步骤4：确认激活与入网

**用户角色**：主导者

**用户故事**：
> 作为主导者，我希望通过多步骤确认流程激活场景，以便完成入网并让协作者可见。

**验收标准**：
- [ ] 显示多步骤激活向导
- [ ] Step 4.1: 确认参与者名单
- [ ] Step 4.2: 配置驱动条件参数
- [ ] Step 4.3: 获取KEY（访问安全数据）
- [ ] Step 4.4: 确认激活
- [ ] Step 4.5: 执行入网动作
- [ ] 整个过程可视化展示进度
- [ ] 激活后协作者可见
- [ ] 更新"我的能力"列表
- [ ] 更新"我的待办"列表
- [ ] 通知其他场景建立联系

---

## 四、数据模型

### 4.1 能力类型扩展

```java
public enum CapabilityType {
    // 场景能力
    SCENE,          // 场景能力（独立启动）
    
    // 协作能力
    SERVICE,        // 服务能力
    AI,             // AI能力
    STORAGE,        // 存储能力
    COMMUNICATION,  // 通信能力
    SECURITY,       // 安全能力
    MONITORING,     // 监控能力
    DRIVER,         // 驱动能力
    TOOL,           // 工具能力
    
    // 特殊类型
    SKILL,          // 技能
    SCENE_GROUP,    // 场景组
    CAPABILITY_CHAIN, // 能力链
    CUSTOM          // 自定义
}
```

### 4.2 驱动条件模型

```yaml
DriverCondition:
  conditionId: "daily-log"           # 条件ID
  name: "每日日志"                    # 条件名称
  description: "每日工作日志汇报"     # 条件描述
  sceneType: DAILY_REPORT            # 场景类型
  triggers:                          # 触发条件
    - type: SCHEDULE
      cron: "0 0 18 * * ?"           # 每天18:00触发
  participants:                      # 参与者配置
    leader:                          # 主导者
      required: true
      permissions: [ACTIVATE, CONFIG, MANAGE]
    collaborators:                   # 协作者
      required: false
      permissions: [USE, VIEW]
  capabilities:                      # 所需能力
    required:
      - notification
    optional:
      - llm-assistant
      - knowledge-search
```

### 4.3 安装配置模型

```yaml
InstallConfig:
  installId: "install-001"
  capabilityId: "daily-log-scene"
  driverCondition: "daily-log"
  
  participants:
    leader: 
      userId: "user-001"
      name: "张三"
    collaborators:
      - userId: "user-002"
        name: "李四"
      - userId: "user-003"
        name: "王五"
  
  optionalCapabilities:
    - notification
    - llm-assistant
  
  status: PENDING_ACTIVATION        # 安装状态
  createTime: 1704067200000
  pushType: DELEGATE                 # 推送类型：DELEGATE/INVITE
```

### 4.4 激活流程模型

```yaml
ActivationProcess:
  processId: "activation-001"
  installId: "install-001"
  
  steps:
    - stepId: "confirm-participants"
      name: "确认参与者"
      status: COMPLETED
      data:
        leader: "user-001"
        collaborators: ["user-002", "user-003"]
      
    - stepId: "config-conditions"
      name: "配置驱动条件"
      status: IN_PROGRESS
      data:
        schedule: "0 0 18 * * ?"
        reminder: true
        
    - stepId: "get-key"
      name: "获取KEY"
      status: PENDING
      data:
        keyId: null
        
    - stepId: "confirm-activation"
      name: "确认激活"
      status: PENDING
      
    - stepId: "network-actions"
      name: "入网动作"
      status: PENDING
      actions:
        - notifyOtherScenes
        - updateMyCapabilities
        - updateMyTodos
        - notifyCollaborators
  
  currentStep: 2
  totalSteps: 5
```

---

## 五、API接口设计

### 5.1 发现能力接口

```http
# 获取能力列表（带分类）
GET /api/v1/capabilities/discover?method=LOCAL_FS&type=SCENE

# 响应
{
  "sceneCapabilities": [
    {
      "capabilityId": "daily-log-scene",
      "name": "日志汇报",
      "type": "SCENE",
      "driverConditions": [
        {"id": "daily-log", "name": "每日日志"},
        {"id": "weekly-report", "name": "周报"}
      ],
      "supportedSceneTypes": ["DAILY_REPORT", "WEEKLY_REPORT"]
    }
  ],
  "collaborationCapabilities": [
    {
      "capabilityId": "notification",
      "name": "通知服务",
      "type": "COMMUNICATION"
    }
  ]
}
```

### 5.2 安装配置接口

```http
# 创建安装配置
POST /api/v1/installs
{
  "capabilityId": "daily-log-scene",
  "driverCondition": "daily-log",
  "participants": {
    "leader": "user-001",
    "collaborators": ["user-002", "user-003"]
  },
  "optionalCapabilities": ["notification"],
  "pushType": "DELEGATE"
}

# 响应
{
  "installId": "install-001",
  "status": "PENDING_ACTIVATION",
  "dependencies": ["notification"],
  "installedCapabilities": ["daily-log-scene", "notification"]
}
```

### 5.3 激活流程接口

```http
# 获取激活流程
GET /api/v1/activations/{installId}/process

# 执行激活步骤
POST /api/v1/activations/{installId}/steps/{stepId}/execute
{
  "data": {
    "schedule": "0 0 18 * * ?",
    "reminder": true
  }
}

# 确认激活
POST /api/v1/activations/{installId}/activate

# 响应
{
  "status": "ACTIVATED",
  "sceneId": "scene-001",
  "sceneGroupId": "group-001",
  "networkActions": [
    {"action": "notifyOtherScenes", "status": "COMPLETED"},
    {"action": "updateMyCapabilities", "status": "COMPLETED"},
    {"action": "updateMyTodos", "status": "COMPLETED"},
    {"action": "notifyCollaborators", "status": "COMPLETED"}
  ]
}
```

---

## 六、UI设计要点

### 6.1 能力发现页面

- 分类标签：场景能力 / 协作能力
- 场景能力卡片显示驱动条件分支
- 支持按场景类型过滤
- 安装按钮区分"安装场景能力"和"添加协作能力"

### 6.2 安装配置向导

- 步骤1：选择驱动条件分支
- 步骤2：配置参与者（主导者/协作者）
- 步骤3：选择可选协作能力
- 步骤4：确认并推送

### 6.3 激活流程可视化

- 进度条显示当前步骤
- 每个步骤状态可视化
- 入网动作实时展示
- 完成后跳转到场景详情

---

## 七、验收清单

### 7.1 功能验收

- [ ] 能力发现页面正确区分场景能力和协作能力
- [ ] 安装流程支持选择驱动条件
- [ ] 安装流程支持配置参与者
- [ ] 安装流程支持选择可选协作能力
- [ ] 安装后自动解析和安装依赖
- [ ] 支持指定推送（委派）
- [ ] 主导者收到待激活通知
- [ ] 激活流程多步骤可视化
- [ ] 激活后协作者可见
- [ ] 入网动作正确执行

### 7.2 数据验收

- [ ] 能力类型正确标记
- [ ] 驱动条件正确存储
- [ ] 参与者角色正确分配
- [ ] 激活流程状态正确更新

### 7.3 UI验收

- [ ] 能力分类展示清晰
- [ ] 安装向导步骤清晰
- [ ] 激活进度可视化
- [ ] 入网动作实时展示

---

## 八、开发任务分解

### 8.1 后端任务

| 任务ID | 任务描述 | 优先级 |
|--------|----------|--------|
| BE-001 | 扩展CapabilityType枚举，添加SCENE类型 | P0 |
| BE-002 | 实现DriverCondition模型和服务 | P0 |
| BE-003 | 实现InstallConfig模型和服务 | P0 |
| BE-004 | 实现ActivationProcess模型和服务 | P0 |
| BE-005 | 实现能力发现接口（带分类过滤） | P0 |
| BE-006 | 实现安装配置接口 | P0 |
| BE-007 | 实现激活流程接口 | P0 |
| BE-008 | 实现入网动作服务 | P1 |

### 8.2 前端任务

| 任务ID | 任务描述 | 优先级 |
|--------|----------|--------|
| FE-001 | 更新能力发现页面（分类展示） | P0 |
| FE-002 | 实现安装配置向导组件 | P0 |
| FE-003 | 实现激活流程可视化组件 | P0 |
| FE-004 | 实现入网动作进度展示 | P1 |
| FE-005 | 更新"我的能力"页面 | P1 |
| FE-006 | 更新"我的待办"页面 | P1 |

---

**文档编写者**: Ooder 开发团队  
**文档日期**: 2026-03-02

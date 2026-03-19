# Ooder v2.3 用户闭环故事文档（场景驱动版）

> **文档版本**: 2.3.1  
> **编写日期**: 2026-03-04  
> **核心思想**: 场景驱动 - 场景即技能

---

## 核心概念：场景驱动

### 场景即技能

```
┌─────────────────────────────────────────────────────────────────┐
│                    场景驱动核心思想                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   传统模式（错误）：                                              │
│   开发时定义模板 → 用户选择模板 → 系统安装                         │
│                                                                 │
│   场景驱动模式（正确）：                                          │
│   场景即技能 → 管理员发现场景 → 配置分发 → 用户使用                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 场景技能定义

场景技能（scene-skill）是一种特殊的 Skill，具有以下特征：

| 特征 | 说明 |
|------|------|
| `spec.type: scene-skill` | 类型标识为场景技能 |
| `sceneCapabilities` | 包含场景能力定义 |
| `mainFirst: true` | 自驱入口标识 |
| `selfDrive` | 自驱配置（定时触发、事件触发等） |
| 可发现 | 在能力发现页面可见 |
| 可分发 | 可推送给组织成员 |

---

## 闭环一：系统初始化（基础技能安装）

### 1.1 用户故事

> **作为** 系统安装者  
> **我希望** 安装基础技能包  
> **以便** 系统具备运行场景的能力

### 1.2 闭环流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    系统初始化闭环                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [开始]                                                         │
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────┐                                            │
│  │ 安装基础技能包   │ ◄── skill-scene, skill-health, etc.        │
│  └─────────────────┘                                            │
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────┐                                            │
│  │ 系统自检        │ ◄── 检查核心能力是否就绪                     │
│  └─────────────────┘                                            │
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────┐                                            │
│  │ 进入就绪状态    │ ◄── 可以发现和安装场景技能                   │
│  └─────────────────┘                                            │
│    │                                                            │
│    ▼                                                            │
│  [结束] ──► 系统就绪，等待场景安装                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.3 基础技能清单

| 技能 | 类型 | 说明 |
|------|------|------|
| skill-scene | service-skill | 场景管理核心服务 |
| skill-capability | service-skill | 能力管理服务 |
| skill-health | system-service | 健康检查服务 |
| skill-monitor | system-service | 监控服务 |
| skill-user-auth | service-skill | 用户认证服务 |

---

## 闭环二：系统管理员配置系统（场景驱动）

### 2.1 用户故事

> **作为** 系统管理员  
> **我希望** 发现和安装场景技能  
> **以便** 为组织成员配置业务能力

### 2.2 闭环流程

```
┌─────────────────────────────────────────────────────────────────┐
│                  管理员场景分发闭环（场景驱动）                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [开始]                                                         │
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ 发现场景技能                                                 ││
│  │ ◄── GET /api/v1/capabilities/discover?type=SCENE            ││
│  │ ◄── 从 Gitee/GitHub/本地 发现场景技能                        ││
│  └─────────────────────────────────────────────────────────────┘│
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ 选择场景技能                                                 ││
│  │ ◄── 查看场景技能详情、驱动条件、所需能力                      ││
│  │ ◄── GET /api/v1/capabilities/{id}                           ││
│  └─────────────────────────────────────────────────────────────┘│
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ 配置场景安装                                                 ││
│  │ ├── 选择驱动条件分支                                        ││
│  │ ├── 配置参与者（主导者/协作者）                              ││
│  │ ├── 选择协作能力                                            ││
│  │ └── 配置推送方式                                            ││
│  └─────────────────────────────────────────────────────────────┘│
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ 安装场景技能                                                 ││
│  │ ◄── POST /api/v1/installs                                   ││
│  │ ├── 自动解析依赖技能                                        ││
│  │ ├── 自动安装依赖                                            ││
│  │ └── 创建场景实例                                            ││
│  └─────────────────────────────────────────────────────────────┘│
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ 推送给参与者                                                 ││
│  │ ◄── POST /api/v1/installs/{id}/push                         ││
│  │ ├── 主导者收到"待激活"通知                                   ││
│  │ └── 协作者暂不可见（等待激活）                               ││
│  └─────────────────────────────────────────────────────────────┘│
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ 主导者激活场景                                               ││
│  │ ◄── POST /api/v1/activations/{id}/activate                  ││
│  │ ├── 确认参与者                                              ││
│  │ ├── 配置驱动条件参数                                        ││
│  │ ├── 获取KEY                                                ││
│  │ └── 执行入网动作                                            ││
│  └─────────────────────────────────────────────────────────────┘│
│    │                                                            │
│    ▼                                                            │
│  [结束] ──► 场景激活，协作者可见，业务就绪                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.3 关键差异：场景驱动 vs 模板驱动

| 维度 | 模板驱动（错误） | 场景驱动（正确） |
|------|-----------------|-----------------|
| **发现方式** | 从预定义模板列表选择 | 从技能仓库发现场景技能 |
| **创建权限** | 开发时定义 | 管理员可创建新场景技能 |
| **灵活性** | 固定组合 | 可动态发现新场景 |
| **分发方式** | 模板部署 | 场景技能安装+推送 |
| **定制能力** | 有限 | 管理员可配置驱动条件 |

### 2.4 场景技能发现来源

| 来源 | API | 说明 |
|------|-----|------|
| Gitee | POST /api/v1/discovery/gitee | 从 Gitee 仓库发现 |
| GitHub | POST /api/v1/discovery/github | 从 GitHub 仓库发现 |
| 本地文件系统 | GET /api/v1/discovery/local | 从本地目录发现 |

---

## 闭环三：实际用户业务流转

### 3.1 用户故事

> **作为** 实际用户（主导者/协作者）  
> **我希望** 完成业务流转闭环  
> **以便** 实现业务目标

### 3.2 闭环流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    业务流转闭环                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [场景已激活]                                                    │
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────┐                                            │
│  │ 用户查看待办    │ ◄── GET /api/v1/todos                       │
│  └─────────────────┘                                            │
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────┐                                            │
│  │ 进入场景        │ ◄── GET /api/v1/scenes/{id}                 │
│  └─────────────────┘                                            │
│    │                                                            │
│    ├────────────────────────────────────────────┐               │
│    │                                            │               │
│    ▼                                            ▼               │
│  ┌─────────────┐                        ┌─────────────┐         │
│  │ 主导者操作  │                        │ 协作者操作  │         │
│  │ - 配置场景  │                        │ - 查看任务  │         │
│  │ - 分配任务  │                        │ - 执行任务  │         │
│  │ - 监控进度  │                        │ - 提交结果  │         │
│  └─────────────┘                        └─────────────┘         │
│    │                                            │               │
│    └────────────────────────────────────────────┘               │
│                         │                                       │
│                         ▼                                       │
│  ┌─────────────────┐                                            │
│  │ 场景自驱执行    │ ◄── 场景技能的 selfDrive 配置               │
│  │ - 定时触发      │                                            │
│  │ - 事件触发      │                                            │
│  │ - 能力调用链    │                                            │
│  └─────────────────┘                                            │
│    │                                                            │
│    ▼                                                            │
│  ┌─────────────────┐                                            │
│  │ 场景状态流转    │                                            │
│  │ DRAFT → ACTIVE │                                            │
│  │ → SUSPENDED    │                                            │
│  │ → COMPLETED    │                                            │
│  └─────────────────┘                                            │
│    │                                                            │
│    ▼                                                            │
│  [结束] ──► 业务目标达成                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.3 场景自驱能力

场景技能的核心特征是**自驱**，通过 `selfDrive` 配置实现：

```yaml
sceneCapabilities:
  - id: scene-daily-report
    name: 日志汇报场景能力
    mainFirst: true
    
    mainFirstConfig:
      selfDrive:
        scheduleRules:
          - trigger: "0 17 * * 1-5"  # 每天17:00触发
            action: remind-flow
            
        eventRules:
          - event: user-submitted
            condition: "all_submitted"
            action: aggregate-flow
            
        capabilityChains:
          remind-flow:
            - capability: report-remind
              input: { targetUsers: "${role.employee}" }
```

---

## 三闭环完整性总结

### 完整度统计

| 闭环 | 核心概念 | 完整度 |
|------|---------|--------|
| **闭环一：系统初始化** | 基础技能安装 | 100% ✅ |
| **闭环二：管理员分发** | 场景驱动发现和分发 | 97% ✅ |
| **闭环三：业务流转** | 场景自驱执行 | 100% ✅ |

### 核心思想验证

| 验证点 | 状态 | 说明 |
|--------|------|------|
| 场景即技能 | ✅ | 场景技能是特殊的 Skill 类型 |
| 场景可发现 | ✅ | 从 Gitee/GitHub/本地发现 |
| 场景可分发 | ✅ | 推送给参与者，激活后可见 |
| 场景可自驱 | ✅ | selfDrive 配置定时/事件触发 |
| 管理员可创建 | ✅ | 不依赖预定义模板 |

---

## 附录：场景技能规范

### A. 场景技能 skill.yaml 模板

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-daily-report
  name: 日志汇报场景技能
  version: 1.0.0
  description: 日志汇报场景能力，支持定时提醒、日志汇总、AI分析
  author: ooder Team
  type: scene-skill

spec:
  type: scene-skill
  
  dependencies:
    - id: skill-email-notification
      version: ">=1.0.0"
      required: true
      description: "邮件通知服务"
    - id: skill-llm-assistant
      version: ">=1.0.0"
      required: false
      description: "LLM分析助手（可选）"
  
  sceneCapabilities:
    - id: scene-daily-report
      name: 日志汇报场景能力
      type: SCENE
      mainFirst: true
      
      mainFirstConfig:
        selfCheck:
          - checkCapabilities: [report-remind, report-submit, report-aggregate]
          - checkDriverCapabilities: [scheduler, event-listener]
          
        selfStart:
          - initDriverCapabilities: [scheduler, event-listener]
          - initCapabilities: [report-remind, report-submit, report-aggregate]
          
        selfDrive:
          scheduleRules:
            - trigger: "0 17 * * 1-5"
              action: remind-flow
              
          capabilityChains:
            remind-flow:
              - capability: report-remind
                input: { targetUsers: "${role.employee}" }
                
      capabilities:
        - report-remind
        - report-submit
        - report-aggregate
        - report-analyze
        
      collaborativeCapabilities:
        - capabilityId: scene-email-notification
          role: PROVIDER
          interface: notification-service
          autoStart: true
  
  capabilities:
    - id: report-remind
      name: 日志提醒
      description: 提醒员工提交日志
      category: communication
      type: ATOMIC
      
    - id: report-submit
      name: 日志提交
      description: 员工提交日志
      category: service
      type: ATOMIC
      
    - id: report-aggregate
      name: 日志汇总
      description: 汇总员工日志
      category: service
      type: COMPOSITE
      
    - id: report-analyze
      name: 日志分析
      description: AI分析日志内容
      category: ai
      type: ATOMIC
```

### B. 场景技能检测标准

```java
public boolean isSceneSkill(SkillPackage skill) {
    // 标准1: spec.type = scene-skill
    if (!"scene-skill".equals(skill.getSpec().getType())) {
        return false;
    }
    
    // 标准2: 存在 sceneCapabilities 定义
    if (skill.getSpec().getSceneCapabilities() == null 
        || skill.getSpec().getSceneCapabilities().isEmpty()) {
        return false;
    }
    
    // 标准3: 存在 mainFirst 标识
    for (SceneCapability cap : skill.getSpec().getSceneCapabilities()) {
        if (cap.isMainFirst()) {
            return true;
        }
    }
    
    return false;
}
```

---

**文档编写者**: Ooder 开发团队  
**文档日期**: 2026-03-04  
**核心思想**: 场景驱动 - 场景即技能

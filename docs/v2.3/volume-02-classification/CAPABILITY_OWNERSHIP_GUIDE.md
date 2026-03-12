# 能力归属判定指南

> **文档版本**: v1.0  
> **创建日期**: 2026-03-08  
> **适用范围**: Ooder 技能开发  
> **关联文档**: GLOSSARY_V2.3.md, CAPABILITY_GOVERNANCE_TASKS.md

---

## 一、概述

本指南帮助开发者判断一个能力应归属于哪种类型，确保能力治理规范落地执行。

### 1.1 能力归属类型

| 类型 | 简写 | 定义位置 | 可见性 | 生命周期 |
|------|------|----------|--------|----------|
| 场景内部能力 | SIC | skill.yaml 的 capabilities 列表 | 仅场景内 | 绑定场景 |
| 独立能力 | IC | 独立 skill 包 | supportedSceneTypes | 独立运行 |
| 平台能力 | PC | 平台内置 | 全局 | 平台管理 |

---

## 二、判定流程

### 2.1 决策树

```
开始判定
    │
    ▼
┌─────────────────────────────────────┐
│ 是否为平台核心功能？                  │
│ (认证、存储、组织架构等)              │
└─────────────────────────────────────┘
    │                           │
   是                          否
    │                           │
    ▼                           ▼
┌─────────────┐    ┌─────────────────────────────────────┐
│ 平台能力(PC) │    │ 是否只服务于单一场景？               │
└─────────────┘    │ (如招聘场景的简历收集)               │
                   └─────────────────────────────────────┘
                       │                           │
                      是                          否
                       │                           │
                       ▼                           ▼
               ┌─────────────┐    ┌─────────────────────────────────────┐
               │场景内部能力  │    │ 是否需要跨场景复用？                 │
               │   (SIC)     │    │ (多个场景需要相同功能)               │
               └─────────────┘    └─────────────────────────────────────┘
                                       │                           │
                                      是                          否
                                       │                           │
                                       ▼                           ▼
                               ┌─────────────┐           ┌─────────────┐
                               │ 独立能力(IC) │           │场景内部能力  │
                               └─────────────┘           │   (SIC)     │
                                                        └─────────────┘
```

### 2.2 快速判定表

| 问题 | 是 | 否 |
|------|-----|-----|
| 是否为平台核心基础设施？ | PC | 继续判断 |
| 是否只服务于单一业务场景？ | SIC | 继续判断 |
| 是否需要被多个场景复用？ | IC | SIC |
| 是否需要独立部署和扩展？ | IC | SIC |
| 是否需要跨场景组运行？ | IC | SIC |

---

## 三、详细判定规则

### 3.1 场景内部能力 (SIC)

**判定条件** (满足以下全部条件):

1. 定义在场景技能包的 `capabilities` 列表中
2. 仅服务于特定业务场景
3. 不需要独立部署
4. 生命周期与场景绑定

**典型示例**:

| 能力 | 所属场景 | 判定理由 |
|------|---------|---------|
| 简历收集 | 招聘场景 | 仅招聘场景使用 |
| 面试邀约 | 招聘场景 | 仅招聘场景使用 |
| 会议记录生成 | 会议纪要场景 | 仅会议场景使用 |
| 文档摘要 | 文档助手场景 | 仅文档场景使用 |

**skill.yaml 配置**:

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill
metadata:
  id: skill-recruitment
  name: 招聘管理
spec:
  type: scene-skill
  
  capabilities:
    - id: resume-collection
      name: 简历收集
      description: 收集和管理候选人简历
      category: business
    - id: interview-invitation
      name: 面试邀约
      description: 发送面试邀请
      category: business
```

### 3.2 独立能力 (IC)

**判定条件** (满足以下任意条件):

1. 被多个场景复用
2. 需要独立部署和扩展
3. 需要跨场景组运行
4. 提供通用技术服务

**典型示例**:

| 能力 | 支持场景 | 判定理由 |
|------|---------|---------|
| MQTT消息推送 | IoT、招聘、通知 | 多场景复用 |
| 知识库管理 | 知识问答、文档助手 | 多场景复用 |
| RAG检索增强 | 知识问答、文档助手 | 多场景复用 |
| LLM对话服务 | 多个AI场景 | 多场景复用 |

**skill.yaml 配置**:

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill
metadata:
  id: skill-mqtt
  name: MQTT Service
spec:
  type: service-skill
  
  ownership: independent
  
  supportedSceneTypes:
    - iot-device
    - smart-home
    - recruitment
    
  dynamicSceneTypes: true
  
  autoStart:
    enabled: true
    delay: 5s
    
  autoJoin:
    enabled: true
    matchSceneTypes: true
    maxSceneGroups: 10
```

### 3.3 平台能力 (PC)

**判定条件** (满足以下全部条件):

1. 平台核心基础设施
2. 全局可见和可用
3. 由平台统一管理
4. 不属于特定业务场景

**典型示例**:

| 能力 | 类型 | 判定理由 |
|------|------|---------|
| 用户认证 | 认证 | 平台核心 |
| 本地存储 | 存储 | 平台核心 |
| MinIO存储 | 存储 | 平台核心 |
| LDAP组织 | 组织 | 平台核心 |
| 飞书组织 | 组织 | 平台核心 |

**skill.yaml 配置**:

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill
metadata:
  id: skill-user-auth
  name: 用户认证服务
spec:
  type: system-service
  
  capabilities:
    - id: authentication
      name: 用户认证
      category: security
    - id: authorization
      name: 权限管理
      category: security
```

---

## 四、边界案例分析

### 4.1 案例1: 文档处理能力

**场景**: 文档上传、解析、转换

**分析**:
- 被多个场景使用？→ 是 (文档助手、知识问答、会议纪要)
- 需要独立部署？→ 是 (资源密集型)
- 需要跨场景组？→ 是

**判定**: **独立能力 (IC)**

### 4.2 案例2: 日报生成能力

**场景**: 自动生成工作日报

**分析**:
- 被多个场景使用？→ 否 (仅日报场景)
- 需要独立部署？→ 否
- 需要跨场景组？→ 否

**判定**: **场景内部能力 (SIC)**

### 4.3 案例3: 消息通知能力

**场景**: 发送各类通知消息

**分析**:
- 被多个场景使用？→ 是 (几乎所有场景)
- 需要独立部署？→ 是
- 是否平台核心？→ 是 (基础设施)

**判定**: **平台能力 (PC)**

---

## 五、配置检查清单

### 5.1 场景内部能力 (SIC) 检查清单

- [ ] `spec.type: scene-skill`
- [ ] 能力定义在 `spec.capabilities` 列表中
- [ ] 无 `ownership` 字段 (隐式 SIC)
- [ ] 无 `supportedSceneTypes` 字段

### 5.2 独立能力 (IC) 检查清单

- [ ] `spec.type: service-skill`
- [ ] `spec.ownership: independent`
- [ ] 配置 `spec.supportedSceneTypes` 列表
- [ ] 配置 `spec.dynamicSceneTypes: true` (如需动态扩展)
- [ ] 配置 `spec.autoStart` (自主启动)
- [ ] 配置 `spec.autoJoin` (自主加入场景组)

### 5.3 平台能力 (PC) 检查清单

- [ ] `spec.type: system-service`
- [ ] 无 `ownership` 字段 (隐式 PC)
- [ ] 无 `supportedSceneTypes` 字段 (全局可用)

---

## 六、常见错误

### 6.1 错误: 将独立能力定义为场景内部能力

```yaml
spec:
  type: scene-skill
  capabilities:
    - id: mqtt-push
      name: MQTT推送
```

**问题**: MQTT推送是通用能力，不应绑定单一场景

**修正**:

```yaml
spec:
  type: service-skill
  ownership: independent
  supportedSceneTypes:
    - iot-device
    - recruitment
```

### 6.2 错误: 将场景内部能力定义为独立能力

```yaml
spec:
  type: service-skill
  ownership: independent
  supportedSceneTypes:
    - recruitment
  capabilities:
    - id: resume-collection
      name: 简历收集
```

**问题**: 简历收集仅招聘场景使用，不应定义为独立能力

**修正**:

```yaml
spec:
  type: scene-skill
  capabilities:
    - id: resume-collection
      name: 简历收集
```

---

## 七、判定工具

### 7.1 代码判定方法

```java
public CapabilityOwnership determineOwnership(Capability capability) {
    if (capability.getParentSkill() != null && capability.getParentScene() != null) {
        return CapabilityOwnership.SCENE_INTERNAL;
    }
    if (capability.getSupportedSceneTypes() != null && 
        !capability.getSupportedSceneTypes().isEmpty()) {
        return CapabilityOwnership.INDEPENDENT;
    }
    return CapabilityOwnership.PLATFORM;
}
```

### 7.2 API 查询

```bash
GET /api/capability/{id}/ownership

Response:
{
  "capabilityId": "mqtt-push",
  "ownership": "INDEPENDENT",
  "supportedSceneTypes": ["iot-device", "recruitment"],
  "dynamicSceneTypes": true
}
```

---

## 八、附录

### 8.1 能力归属速查表

| 能力类型 | 典型示例 | 归属类型 |
|---------|---------|---------|
| 消息推送 | MQTT、WebSocket | IC |
| 知识管理 | 知识库、RAG | IC |
| LLM服务 | 对话、上下文 | IC |
| 文档处理 | 解析、转换 | IC |
| 业务流程 | 审批、流程 | SIC |
| 数据采集 | 表单、收集 | SIC |
| 报告生成 | 日报、周报 | SIC |
| 用户认证 | 登录、权限 | PC |
| 文件存储 | 本地、云存储 | PC |
| 组织管理 | LDAP、飞书 | PC |

---

**文档维护**: Ooder 开发团队  
**最后更新**: 2026-03-08

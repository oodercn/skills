# 场景设计规范

## 概述

场景(Scene)是 Ooder Agent Platform 中技能协作的基本单元。本文档介绍场景的设计规范和最佳实践。

## 场景定义

场景是一组相关能力的集合，定义了技能如何协作完成特定任务。

## 场景类型

| 类型 | 说明 | 示例 |
|------|------|------|
| auth | 认证场景 | 用户认证、组织数据访问 |
| ui-generation | UI生成场景 | 图转代码、组件生成 |
| utility | 工具场景 | 实用功能、数据处理 |

## 场景规范

```yaml
sceneId: scene-name              # 场景ID
name: Scene Display Name         # 场景名称
description: 场景描述             # 场景描述
version: "1.0.0"                 # 版本号
requiredCapabilities:            # 必需能力
  - capability-1
  - capability-2
maxMembers: 100                  # 最大成员数
```

## 当前场景

### auth (认证场景)

```yaml
sceneId: auth
name: Authentication
description: 认证场景，支持用户认证和组织数据访问
version: "1.0.0"
requiredCapabilities:
  - user-auth
  - org-data-read
maxMembers: 100
```

**适用技能**:
- skill-user-auth: 用户认证
- skill-org-feishu: 飞书组织数据
- skill-org-dingding: 钉钉组织数据

### ui-generation (UI生成场景)

```yaml
sceneId: ui-generation
name: UI Generation
description: UI生成场景，支持图转代码、组件生成等能力
version: "1.0.0"
requiredCapabilities:
  - generate-ui
  - preview-ui
maxMembers: 10
```

**适用技能**:
- skill-a2ui: A2UI图转代码

### utility (工具场景)

```yaml
sceneId: utility
name: Utility
description: 实用工具场景，提供各种实用功能
version: "1.0.0"
requiredCapabilities:
  - execute-task
maxMembers: 50
```

**适用技能**:
- skill-trae-solo: 实用工具服务

## 场景角色

场景中的成员可以担任不同角色：

| 角色 | 说明 | 权限 |
|------|------|------|
| PRIMARY | 主节点 | 完全控制 |
| BACKUP | 备份节点 | 同步数据 |
| OBSERVER | 观察者 | 只读访问 |

## 场景协作

### 场景组

多个场景可以组成场景组，实现跨场景协作：

```yaml
sceneGroupId: group-id
name: Scene Group Name
scenes:
  - sceneId: auth
    roleId: PRIMARY
  - sceneId: ui-generation
    roleId: PRIMARY
```

### 协作流程

```
1. 创建场景
   └── 定义场景规范和能力需求

2. 加入场景
   └── 技能注册到场景，分配角色

3. 能力调用
   └── 通过场景协调器调用能力

4. 结果汇总
   └── 收集各技能执行结果

5. 场景关闭
   └── 释放资源，保存状态
```

## 场景配置

### 创建场景

```java
SceneManager sceneManager = ooderSDK.getSceneManager();
SceneDefinition scene = sceneManager.create(
    "auth",
    "Authentication Scene",
    Arrays.asList("user-auth", "org-data-read")
).get();
```

### 加入场景

```java
sceneManager.addCapability("auth", "user-auth").get();
```

### 激活场景

```java
sceneManager.activate("auth").get();
```

### 获取场景状态

```java
SceneState state = sceneManager.getState("auth").get();
```

## 场景索引

场景索引用于快速发现可用场景：

```yaml
apiVersion: ooder.io/v1
kind: SkillIndex
spec:
  scenes:
    - sceneId: auth
      name: Authentication
      description: 认证场景
      version: "1.0.0"
      requiredCapabilities:
        - user-auth
        - org-data-read
      maxMembers: 100
```

## 最佳实践

1. **单一职责**: 每个场景专注于一个业务领域
2. **能力最小化**: 只定义必需的能力
3. **版本兼容**: 保持向后兼容性
4. **文档完善**: 提供详细的场景说明
5. **测试覆盖**: 编写场景测试用例

## 参考

- [技能开发指南](SKILL_DEVELOPMENT.md)
- [技能索引](../skill-index.yaml)

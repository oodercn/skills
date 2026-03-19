# 场景技能分类规范 v2.0

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v2.0 |
| 创建日期 | 2026-03-11 |
| 所属模块 | skill-scene |
| 状态 | 正式规范 |
| 更新说明 | 废弃 ABS/ASS/TBS 分类，改用 SceneType + visibility 二维分类 |

---

## 一、分类体系概述

### 1.1 二维分类模型

场景技能采用**二维分类模型**，通过两个独立的维度进行分类：

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        场景技能二维分类模型                                        │
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
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 与旧分类对照

| 旧分类 | 新分类 | 说明 |
|--------|--------|------|
| **ABS** (自驱业务场景) | `SceneType.AUTO` + `visibility=public` | 自驱场景，用户可见 |
| **ASS** (自驱系统场景) | `SceneType.AUTO` + `visibility=internal` | 自驱场景，后台运行 |
| **TBS** (触发业务场景) | `SceneType.TRIGGER` + `visibility=public` | 触发场景，用户参与 |

> **重要**: ABS/ASS/TBS 分类已废弃，请使用新的二维分类体系。

---

## 二、分类维度详解

### 2.1 SkillForm (技能形态)

| 值 | 说明 | 判定条件 |
|-----|------|---------|
| `STANDALONE` | 独立技能 | 非场景技能，无场景实例 |
| `SCENE` | 场景技能 | `sceneSkill=true` 或 `type=scene-skill` |

### 2.2 SceneType (场景类型)

| 值 | 说明 | 判定条件 |
|-----|------|---------|
| `AUTO` | 自驱场景 | `hasSelfDrive=true` (mainFirst + selfStart + selfDrive) |
| `TRIGGER` | 触发场景 | `hasSelfDrive=false`，需要外部触发 |

### 2.3 visibility (可见性)

| 值 | 说明 | 判定条件 |
|-----|------|---------|
| `public` | 公开可见 | 业务语义评分 ≥ 8 或显式声明 |
| `internal` | 内部使用 | 业务语义评分 < 8 或显式声明 |

---

## 三、分类组合与行为

### 3.1 组合矩阵

| SceneType | visibility | 安装后状态 | 激活方式 | 用户可见 |
|-----------|------------|-----------|---------|:--------:|
| AUTO | public | SCHEDULED | 需要用户确认 | ✅ |
| AUTO | internal | RUNNING | 自动激活 | ❌ |
| TRIGGER | public | PENDING | 等待触发 | ✅ |

### 3.2 行为差异

#### AUTO + public (原 ABS)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  AUTO + public 行为流程                                                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. 安装技能包                                                                  │
│  2. 状态 → SCHEDULED                                                            │
│  3. 用户在发现页可见                                                            │
│  4. 用户确认激活                                                                │
│  5. 状态 → RUNNING                                                              │
│  6. 定时调度执行                                                                │
│                                                                                 │
│  特点: 用户可见，需要确认，自动运行                                              │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

#### AUTO + internal (原 ASS)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  AUTO + internal 行为流程                                                        │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. 安装技能包                                                                  │
│  2. 状态 → RUNNING                                                              │
│  3. 用户不可见                                                                  │
│  4. 后台自动运行                                                                │
│  5. 不注册用户菜单                                                              │
│                                                                                 │
│  特点: 后台静默运行，用户无感知                                                  │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

#### TRIGGER + public (原 TBS)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  TRIGGER + public 行为流程                                                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. 安装技能包                                                                  │
│  2. 状态 → PENDING                                                              │
│  3. 用户在发现页可见                                                            │
│  4. 配置触发条件                                                                │
│  5. 等待触发事件 (用户操作/定时任务/外部事件)                                     │
│  6. 触发后状态 → RUNNING                                                        │
│  7. 执行场景逻辑                                                                │
│                                                                                 │
│  特点: 需要触发，用户参与配置                                                    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、代码实现

### 4.1 分类判定逻辑

```java
// SkillCapabilitySyncService.java
private void calculateAndSetClassification(Capability cap) {
    if (!cap.isSceneCapability()) {
        cap.setSkillForm(SkillForm.STANDALONE.getCode());
        cap.setSceneType(null);
        return;
    }
    
    cap.setSkillForm(SkillForm.SCENE.getCode());
    
    boolean hasSelfDrive = cap.isHasSelfDrive();
    
    if (hasSelfDrive) {
        cap.setSceneType(SceneType.AUTO.getCode());
    } else {
        cap.setSceneType(SceneType.TRIGGER.getCode());
    }
}

private boolean isInternal(Capability cap) {
    if (cap.getSceneType() == null) {
        return false;
    }
    if (SceneType.AUTO.getCode().equals(cap.getSceneType())) {
        int score = cap.getBusinessSemanticsScore() != null 
            ? cap.getBusinessSemanticsScore() : 5;
        return score < 8;
    }
    return false;
}
```

### 4.2 状态转换逻辑

```java
// SceneSkillLifecycleServiceImpl.java
private CapabilityStatus determineActivateStatus(SceneType sceneType, boolean isInternal) {
    if (sceneType == SceneType.AUTO) {
        return isInternal ? CapabilityStatus.INITIALIZING : CapabilityStatus.SCHEDULED;
    } else if (sceneType == SceneType.TRIGGER) {
        return CapabilityStatus.PENDING;
    }
    return CapabilityStatus.DRAFT;
}
```

---

## 五、模板配置示例

### 5.1 AUTO + public 示例

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneTemplate

metadata:
  id: knowledge-qa
  name: 知识问答场景
  
spec:
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  
  selfDrive:
    mainFirst: true
    selfStart: true
    selfDrive: true
  
  businessSemantics:
    score: 9
    tags: [knowledge, ai, qa]
```

### 5.2 AUTO + internal 示例

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneTemplate

metadata:
  id: system-monitor
  name: 系统监控
  
spec:
  skillForm: SCENE
  sceneType: AUTO
  visibility: internal
  
  selfDrive:
    mainFirst: true
    selfStart: true
    selfDrive: true
  
  businessSemantics:
    score: 5
    tags: [system, monitoring]
```

### 5.3 TRIGGER + public 示例

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneTemplate

metadata:
  id: daily-report
  name: 日志汇报场景
  
spec:
  skillForm: SCENE
  sceneType: TRIGGER
  visibility: public
  
  selfDrive:
    mainFirst: false
  
  participants:
    - role: MANAGER
      minCount: 1
      maxCount: 1
    - role: EMPLOYEE
      minCount: 1
      maxCount: 100
  
  driverConditions:
    - type: schedule
      cron: "0 30 17 * * ?"
    - type: manual
```

---

## 六、迁移指南

### 6.1 代码迁移

| 旧代码 | 新代码 |
|--------|--------|
| `skillType == "ABS"` | `sceneType == "AUTO" && visibility == "public"` |
| `skillType == "ASS"` | `sceneType == "AUTO" && visibility == "internal"` |
| `skillType == "TBS"` | `sceneType == "TRIGGER" && visibility == "public"` |

### 6.2 模板迁移

| 旧配置 | 新配置 |
|--------|--------|
| `skillType: ABS` | `sceneType: AUTO` + `visibility: public` |
| `skillType: ASS` | `sceneType: AUTO` + `visibility: internal` |
| `skillType: TBS` | `sceneType: TRIGGER` + `visibility: public` |

---

## 七、相关文档

- [场景技能安装激活规范](./scene-skill-installation-activation-spec.md)
- [协作任务说明](./collaboration-task-spec.md)
- [SE SDK 协作响应](./SE_SDK_Collaboration_Response.md)

---

**文档版本**: v2.0  
**创建日期**: 2026-03-11  
**最后更新**: 2026-03-11

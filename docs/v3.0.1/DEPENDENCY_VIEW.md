# Ooder Skills 模块完整依赖视图

**创建日期**: 2026-04-04  
**项目路径**: `E:\github\ooder-skills`  
**Maven本地仓库**: `D:\maven\.m2`

---

## 一、模块清单与坐标

| 序号 | 模块名 | groupId | artifactId | version | 路径 |
|-----|-------|---------|------------|---------|------|
| 1 | **skill-common** | `net.ooder` | `skill-common` | 3.0.1 | `skills/_system/skill-common/` |
| 2 | **skill-management** | `net.ooder` | `skill-management` | 3.0.1 | `skills/_system/skill-management/` |
| 3 | **skill-agent** | `net.ooder.skill` | `skill-agent` | 1.0.0 | `skills/_system/skill-agent/` |
| 4 | **skill-capability** | `net.ooder.skill` | `skill-capability` | 1.0.0 | `skills/_system/skill-capability/` |
| 5 | **skill-scene** | `net.ooder.skill` | `skill-scene` | 1.0.0 | `skills/_system/skill-scene/` |
| 6 | **skill-group** | `net.ooder` | `skill-group` | 3.0.1 | `skills/capabilities/communication/skill-group/` |
| 7 | **skill-protocol** | `net.ooder` | `skill-protocol` | 3.0.1 | `skills/_system/skill-protocol/` |

---

## 二、依赖矩阵

### 2.1 内部模块间依赖（provided scope）

```
                    │ common │ mgmt │ agent │ cap  │ scene│ group│ protocol
───────────────────┼───────┼──────┼───────┼──────┼──────┼──────┼─────────
skill-common       │   -   │      │      │      │      │      │         │
skill-management   │  ✅    │  -   │      │      │      │      │         │
skill-agent        │  ✅    │      │  -   │      │  ✅  │      │         │
skill-capability   │  ✅    │      │      │  -   │      │      │         │
skill-scene        │  ✅    │      │      │      │  -   │      │         │
skill-group         │  ✅    │      │      │      │      │  -   │         │
skill-protocol      │  ✅    │  ✅  │  ✅  │  ✅  │  ✅  │  ✅  │    -    │
```

### 2.2 各模块详细依赖列表

#### ① skill-common（基础层，无内部依赖）

```xml
<groupId>net.ooder</groupId>
<artifactId>skill-common</artifactId>
<version>3.0.1</version>
```

| 依赖项 | groupId | artifactId | version | scope | 类型 |
|-------|---------|------------|---------|-------|------|
| Spring Boot Starter | org.springframework.boot | spring-boot-starter | 3.2.5 | compile | 外部 |
| Scene Engine | net.ooder | scene-engine | 3.0.1 | compile | 外部 |
| Agent SDK Core | net.ooder | agent-sdk-core | 3.0.1 | compile | 外部 |
| Spring Boot Autoconfigure | org.springframework.boot | spring-boot-autoconfigure | 3.2.5 | compile | 外部 |
| Spring Boot Web | org.springframework.boot | spring-boot-starter-web | 3.2.5 | **provided** | 外部 |
| FreeMarker | org.springframework.boot | spring-boot-starter-freemarker | 3.2.5 | compile | 外部 |
| Jackson | com.fasterxml.jackson.core | jackson-databind | 2.17.0 | compile | 外部 |
| SnakeYAML | org.yaml | snakeyaml | 2.2 | compile | 外部 |
| Servlet API | jakarta.servlet | jakarta.servlet-api | 6.0.0 | **provided** | 外部 |

**特点**: 基础模块，不依赖任何其他内部模块。

---

#### ② skill-management（技能管理层）

```xml
<groupId>net.ooder</groupId>
<artifactId>skill-management</artifactId>
<version>3.0.1</version>
```

| 依赖项 | groupId | artifactId | version | scope | 类型 |
|-------|---------|------------|---------|-------|------|
| **skill-common** | net.ooder | skill-common | 3.0.1 | **provided** | 🔵 内部 |
| Spring Boot Web | org.springframework.boot | spring-boot-starter-web | - | **provided** | 外部 |
| Spring Boot Actuator | org.springframework.boot | spring-boot-starter-actuator | - | **provided** | 外部 |
| SLF4J | org.slf4j | slf4j-api | - | **provided** | 外部 |
| Jackson | com.fasterxml.jackson.core | jackson-databind | - | **provided** | 外部 |

**依赖链**: `management → common`

---

#### ③ skill-agent（智能体层）⚠️ groupId不一致

```xml
<groupId>net.ooder.skill</groupId>
<artifactId>skill-agent</artifactId>
<version>1.0.0</version>
```

| 依赖项 | groupId | artifactId | version | scope | 类型 | 状态 |
|-------|---------|------------|---------|-------|------|------|
| **skill-common** | net.ooder | skill-common | 3.0.1 | **provided** | 🔵 内部 | ✅ |
| **skill-scene** | net.ooder.skill | skill-scene | 1.0.0 | **provided** | 🔵 内部 | ✅ |
| skill-messaging | net.ooder | skill-messaging | 1.0.0 | compile | 🔴 内部 | ❌ 缺失 |
| apex-os | net.ooder | apex-os | 1.0.0 | **provided** | 🔴 内部 | ❌ 缺失 |
| skill-dict | net.ooder.skill | skill-dict | 1.0.0 | **provided** | 🔴 内部 | ❌ 缺失 |
| Spring Boot Web | org.springframework.boot | spring-boot-starter-web | - | **provided** | 外部 | ✅ |
| Lombok | org.projectlombok | lombok | 1.18.30 | **provided** | 外部 | ✅ |
| Jakarta Validation | jakarta.validation | jakarta.validation-api | 3.0.2 | compile | 外部 | ✅ |

**⚠️ 问题**: 
- groupId为`net.ooder.skill`（与其他模块的`net.ooder`不一致）
- 依赖了3个缺失模块：**skill-messaging**, **apex-os**, **skill-dict**

**依赖链**: `agent → {common, scene}` + 缺失模块

---

#### ④ skill-capability（能力层）⚠️ groupId不一致

```xml
<groupId>net.ooder.skill</groupId>
<artifactId>skill-capability</artifactId>
<version>1.0.0</version>
```

| 依赖项 | groupId | artifactId | version | scope | 类型 | 状态 |
|-------|---------|------------|---------|-------|------|------|
| **skill-common** | net.ooder | skill-common | 3.0.1 | **provided** | 🔵 内部 | ✅ |
| Spring Boot Web | org.springframework.boot | spring-boot-starter-web | - | **provided** | 外部 | ✅ |
| Lombok | org.projectlombok | lombok | 1.18.30 | **provided** | 外部 | ✅ |

**⚠️ 问题**: groupId为`net.ooder.skill`

**依赖链**: `capability → common`

---

#### ⑤ skill-scene（场景层）⚠️ groupId不一致

```xml
<groupId>net.ooder.skill</groupId>
<artifactId>skill-scene</artifactId>
<version>1.0.0</version>
```

| 依赖项 | groupId | artifactId | version | scope | 类型 | 状态 |
|-------|---------|------------|---------|-------|------|------|
| **skill-common** | net.ooder | skill-common | 3.0.1 | **provided** | 🔵 内部 | ✅ |
| Spring Boot Web | org.springframework.boot | spring-boot-starter-web | - | **provided** | 外部 | ✅ |
| Lombok | org.projectlombok | lombok | 1.18.30 | **provided** | 外部 | ✅ |

**⚠️ 问题**: groupId为`net.ooder.skill`

**依赖链**: `scene → common`

---

#### ⑥ skill-group（群组层）

```xml
<groupId>net.ooder</groupId>
<artifactId>skill-group</artifactId>
<version>3.0.1</version>
```

| 依赖项 | groupId | artifactId | version | scope | 类型 | 状态 |
|-------|---------|------------|---------|-------|------|------|
| **skill-common** | net.ooder | skill-common | ${project.version} | compile | 🔵 内部 | ✅ |
| Spring Boot Web | org.springframework.boot | spring-boot-starter-web | - | compile | 外部 | ✅ |
| Spring Boot Actuator | org.springframework.boot | spring-boot-starter-actuator | - | compile | 外部 | ✅ |
| Lombok | org.projectlombok | lombok | - | optional | 外部 | ✅ |

**依赖链**: `group → common`

---

#### ⑦ skill-protocol（协议层 - 最终聚合层）

```xml
<groupId>net.ooder</groupId>
<artifactId>skill-protocol</artifactId>
<version>3.0.1</version>
```

| 依赖项 | groupId | artifactId | version | scope | 类型 | 状态 |
|-------|---------|------------|---------|-------|------|------|
| **skill-common** | net.ooder | skill-common | 3.0.1 | **provided** | 🔵 内部 | ✅ |
| **skill-management** | net.ooder | skill-management | 3.0.1 | **provided** | 🔵 内部 | ✅ |
| **skill-agent** | net.ooder.skill | skill-agent | 1.0.0 | **provided** | 🔵 内部 | ✅ |
| **skill-scene** | net.ooder.skill | skill-scene | 1.0.0 | **provided** | 🔵 内部 | ✅ |
| **skill-capability** | net.ooder.skill | skill-capability | 1.0.0 | **provided** | 🔵 内部 | ✅ |
| **skill-group** | net.ooder | skill-group | 3.0.1 | **provided** | 🔵 内部 | ✅ |
| Spring Boot Web | org.springframework.boot | spring-boot-starter-web | - | **provided** | 外部 | ✅ |
| Spring Boot Actuator | org.springframework.boot | spring-boot-starter-actuator | - | **provided** | 外部 | ✅ |
| Lombok | org.projectlombok | lombok | - | **provided** | 外部 | ✅ |

**依赖链**: `protocol → {common, management, agent, scene, capability, group}`

---

## 三、循环依赖检测

### 3.1 依赖层级图（DAG方向图）

```
Level 0 (基础层):
┌─────────────────┐
│  skill-common   │  ← 无内部依赖
└────────┬────────┘
         │
    ┌────┴────┬────────┬────────┬────────┐
    ▼        ▼        ▼        ▼        ▼
Level 1:
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│mgmt    │ │scene   │ │cap     │ │group   │ │(other) │
│        │ │        │ │        │ │        │ │modules │
└────────┘ └───┬────┘ └────────┘ └────────┘ └────────┘
               │
               ▼
Level 2:
┌─────────────────┐
│  skill-agent   │  → 依赖 scene (同level但无环)
└────────┬────────┘
         │
         ▼
Level 3 (聚合层):
┌─────────────────────────────────────────────────────┐
│                  skill-protocol                       │
│  依赖: common, mgmt, agent, scene, cap, group       │
└─────────────────────────────────────────────────────┘
```

### 3.2 循环依赖检测结果

| 检测项 | 结果 | 说明 |
|-------|------|------|
| 直接循环依赖 A→A | ✅ 无 | 无自引用 |
| 两两循环 A↔B | ✅ 无 | 所有依赖单向 |
| 三方循环 A→B→C→A | ✅ 无 | DAG结构清晰 |
| 长循环依赖 | ✅ 无 | 无任何环路 |

**结论**: 🟢 **无循环依赖！** 依赖图为有向无环图(DAG)，可以按拓扑顺序编译。

---

## 四、问题汇总

### 4.1 🔴 严重问题（阻塞编译）

| # | 问题 | 影响模块 | 详情 |
|---|------|---------|------|
| P1 | **skill-messaging 缺失** | skill-agent | artifactId=skill-messaging, v1.0.0 不存在 |
| P2 | **apex-os 缺失** | skill-agent | artifactId=apex-os, v1.0.0 不存在 |
| P3 | **skill-dict 缺失** | skill-agent | artifactId=skill-dict, v1.0.0 不存在 |

### 4.2 🟡 中等问题（坐标不一致）

| # | 问题 | 影响模块 | 当前值 | 应改为 |
|---|------|---------|--------|--------|
| C1 | **groupId 不一致** | skill-agent | `net.ooder.skill` | `net.ooder` |
| C2 | **groupId 不一致** | skill-capability | `net.ooder.skill` | `net.ooder` |
| C3 | **groupId 不一致** | skill-scene | `net.ooder.skill` | `net.ooder` |
| C4 | **version 不一致** | skill-agent/cap/scene | 1.0.0 | 3.0.1 |

### 4.3 🟢 轻微问题（可忽略）

| # | 问题 | 影响 |
|---|------|------|
| L1 | spring-boot-maven-plugin 版本缺失 | 多个模块有WARNING |
| L2 | driver模块pom损坏 | 已修复9个 |

---

## 五、编译顺序规划

基于DAG拓扑排序，推荐编译顺序：

```
Step 1: skill-common          (无内部依赖)
Step 2: skill-management      (仅依赖 common)
Step 3: skill-capability      (仅依赖 common)
Step 4: skill-scene           (仅依赖 common)
Step 5: skill-group           (仅依赖 common)
Step 6: skill-agent           (依赖 common + scene; ⚠️ 有3个缺失依赖需排除)
Step 7: skill-protocol        (依赖以上全部)
```

---

## 六、下一步行动

### 方案A：快速编译（排除问题模块）

```bash
# 只编译核心模块（跳过skill-agent中的缺失依赖）
mvn clean install -DskipTests \
  -pl skills/_system/skill-common,\
skills/_system/skill-management,\
skills/_system/skill-capability,\
skills/_system/skill-scene,\
skills/capabilities/communication/skill-group \
  -am
```

### 方案B：完整修复后编译

1. 创建缺失的 stub 模块（skill-messaging, apex-os, skill-dict）
2. 统一 groupId 为 `net.ooder`
3. 统一 version 为 `3.0.1`
4. 全量编译

---

**文档维护**: 本文档应在模块变更时同步更新。

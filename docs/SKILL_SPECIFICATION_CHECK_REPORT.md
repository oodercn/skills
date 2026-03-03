# Skill 规范匹配检查报告

> **检查日期**: 2026-03-03  
> **检查范围**: ooder-skills/skills 目录下所有 skill 配置文件  
> **规范版本**: skill.ooder.net/v1

---

## 一、检查概要

### 1.1 文件统计

| 文件类型 | 数量 |
|---------|------|
| skill.yaml | 35 |
| skill-manifest.yaml | 35 |
| 同时存在两种文件 | 15 |

### 1.2 问题总览

| 问题等级 | 数量 | 说明 |
|---------|------|------|
| 🔴 严重 | 3 | 规范不一致，影响解析 |
| 🟡 警告 | 5 | 字段缺失，建议补充 |
| 🟢 提示 | 4 | 风格不统一，建议优化 |

---

## 二、规范问题详情

### 2.1 🔴 严重问题

#### 问题1: apiVersion 和 kind 不一致

**规范要求**:
```yaml
apiVersion: skill.ooder.net/v1
kind: Skill
```

**实际发现**:

| apiVersion | kind | 文件数 | 示例文件 |
|------------|------|--------|---------|
| skill.ooder.net/v1 | Skill | 20 | skill-health, skill-monitor |
| skill.ooder.net/v1 | SkillManifest | 10 | skill-network, skill-openwrt |
| ooder.io/v1 | SkillPackage | 10 | skill-a2ui, skill-user-auth |

**影响**: 解析器可能无法正确识别文件类型

**建议**: 统一使用 `skill.ooder.net/v1` + `Skill`

---

#### 问题2: capabilities 定义格式不一致

**规范要求**:
```yaml
capabilities:
  - id: capability-id
    name: Capability Name
    description: Capability description
    category: category-name
```

**实际发现**:

| 格式类型 | 文件数 | 示例 |
|---------|--------|------|
| 详细对象格式 | 25 | skill-health, skill-knowledge-ui |
| 简化字符串格式 | 10 | skill-a2ui (skill-manifest.yaml) |

**简化格式示例**:
```yaml
capabilities:
  - user-auth
  - token-validate
  - session-manage
```

**影响**: 前端无法获取 capability 的名称和描述

**建议**: 统一使用详细对象格式

---

#### 问题3: metadata.id 字段缺失

**规范要求**:
```yaml
metadata:
  id: skill-xxx  # 必填
  name: Skill Name
  version: 1.0.0
```

**实际发现**:

| 状态 | 文件数 | 示例 |
|------|--------|------|
| 有 id 字段 | 30 | skill-health, skill-monitor |
| 无 id 字段 | 5 | skill-a2ui (skill-manifest.yaml) |

**影响**: 无法唯一标识 skill

**建议**: 所有 skill 配置必须包含 `metadata.id`

---

### 2.2 🟡 警告问题

#### 问题4: endpoints 与 capabilities 对应关系缺失

**规范要求**:
```yaml
endpoints:
  - path: /api/xxx
    method: POST
    description: API description
    capability: capability-id  # 必须关联到 capabilities
```

**实际发现**:

| 状态 | 文件数 |
|------|--------|
| 有 capability 关联 | 25 |
| 无 capability 关联 | 10 |

**建议**: 所有 endpoints 必须关联到 capabilities

---

#### 问题5: spec.type 与 metadata.type 不一致

**规范要求**:
```yaml
metadata:
  type: service-skill  # 可选
  
spec:
  type: service-skill  # 必填
```

**实际发现**:

| spec.type | 数量 |
|-----------|------|
| service-skill | 10 |
| tool-skill | 15 |
| nexus-ui | 5 |
| system-service | 5 |

**建议**: 统一在 spec.type 中定义类型

---

#### 问题6: runtime 配置缺失

**规范要求**:
```yaml
spec:
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
    mainClass: xxx.Application
```

**实际发现**:

| 状态 | 文件数 |
|------|--------|
| 有完整 runtime | 20 |
| 无 runtime | 15 |

**建议**: 所有 Java skill 必须配置 runtime

---

#### 问题7: resources 配置缺失

**规范要求**:
```yaml
spec:
  resources:
    cpu: "100m"
    memory: "128Mi"
    storage: "50Mi"
```

**实际发现**:

| 状态 | 文件数 |
|------|--------|
| 有 resources | 30 |
| 无 resources | 5 |

**建议**: 所有 skill 必须配置资源限制

---

#### 问题8: offline 配置缺失

**规范要求**:
```yaml
spec:
  offline:
    enabled: true
    cacheStrategy: local
    syncOnReconnect: true
```

**实际发现**:

| 状态 | 文件数 |
|------|--------|
| 有 offline | 20 |
| 无 offline | 15 |

**建议**: 配置离线支持策略

---

### 2.3 🟢 提示问题

#### 问题9: 文件命名不一致

**发现**:
- 部分目录只有 `skill.yaml`
- 部分目录只有 `skill-manifest.yaml`
- 部分目录同时有两种文件

**建议**: 统一使用 `skill.yaml` 作为配置文件名

---

#### 问题10: homepage URL 不一致

**发现**:
- 部分使用 `https://gitee.com/ooderCN/skills`
- 部分使用 `https://github.com/ooderCN/skills`

**建议**: 统一使用 Gitee 地址

---

#### 问题11: keywords 格式不一致

**发现**:
- 部分使用小写: `monitor`, `health`
- 部分使用混合: `Network`, `WiFi`

**建议**: 统一使用小写关键词

---

#### 问题12: version 格式不一致

**发现**:
- 部分使用语义版本: `1.0.0`
- 部分使用 SDK 版本: `0.7.3`

**建议**: 使用语义版本号，与 SDK 版本解耦

---

## 三、规范符合性统计

### 3.1 按文件统计

| Skill | apiVersion | kind | metadata.id | capabilities详细 | endpoints关联 | 符合率 |
|-------|------------|------|-------------|-----------------|--------------|--------|
| skill-health | ✅ | ✅ | ✅ | ✅ | ✅ | 100% |
| skill-monitor | ✅ | ✅ | ✅ | ✅ | ✅ | 100% |
| skill-network | ✅ | ❌ | ✅ | ✅ | ✅ | 80% |
| skill-openwrt | ✅ | ❌ | ✅ | ✅ | ✅ | 80% |
| skill-a2ui | ❌ | ❌ | ❌ | ❌ | ✅ | 40% |
| skill-user-auth | ❌ | ❌ | ❌ | ❌ | ❌ | 20% |
| skill-knowledge-ui | ✅ | ✅ | ✅ | ✅ | ✅ | 100% |
| skill-llm-assistant-ui | ✅ | ✅ | ✅ | ✅ | ✅ | 100% |
| skill-vfs-local | ✅ | ✅ | ✅ | ✅ | ✅ | 100% |
| skill-scene | ✅ | ✅ | ✅ | ✅ | ✅ | 100% |

### 3.2 总体符合率

| 符合率区间 | 文件数 | 占比 |
|-----------|--------|------|
| 100% | 20 | 57% |
| 80% | 8 | 23% |
| 60% | 4 | 11% |
| <60% | 3 | 9% |

---

## 四、修复建议

### 4.1 高优先级修复

1. **统一 apiVersion 和 kind**
   - 将所有 `ooder.io/v1` 改为 `skill.ooder.net/v1`
   - 将所有 `SkillPackage`/`SkillManifest` 改为 `Skill`

2. **统一 capabilities 格式**
   - 将简化格式改为详细对象格式
   - 添加 id, name, description, category 字段

3. **添加 metadata.id**
   - 确保所有配置文件都有唯一的 id

### 4.2 中优先级修复

4. **完善 endpoints 关联**
   - 确保所有 endpoint 都关联到 capability

5. **添加 runtime 配置**
   - 为所有 Java skill 添加 runtime 配置

6. **添加 resources 配置**
   - 为所有 skill 添加资源限制

### 4.3 低优先级优化

7. **统一文件命名**
   - 将 `skill-manifest.yaml` 重命名为 `skill.yaml`

8. **统一 URL 和关键词**
   - 统一 homepage URL
   - 统一 keywords 格式

---

## 五、规范模板

### 5.1 标准 skill.yaml 模板

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-xxx
  name: Skill Name
  version: 1.0.0
  description: Skill description
  author: Ooder Team
  license: Apache-2.0
  homepage: https://gitee.com/ooderCN/skills
  keywords:
    - keyword1
    - keyword2

spec:
  type: service-skill  # service-skill | tool-skill | nexus-ui | system-service
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
    mainClass: net.ooder.skill.xxx.XxxSkillApplication
  
  capabilities:
    - id: capability-1
      name: Capability 1
      description: Capability 1 description
      category: category-name
    - id: capability-2
      name: Capability 2
      description: Capability 2 description
      category: category-name
  
  scenes:
    - name: scene-1
      description: Scene description
      capabilities:
        - capability-1
        - capability-2
  
  dependencies:
    - id: skill-dependency
      version: ">=1.0.0"
      required: false
      description: Dependency description
  
  endpoints:
    - path: /api/v1/xxx
      method: GET
      description: API description
      capability: capability-1
    - path: /api/v1/xxx
      method: POST
      description: API description
      capability: capability-2
  
  config:
    optional:
      - name: CONFIG_NAME
        type: string
        default: default-value
        description: Config description
  
  resources:
    cpu: "100m"
    memory: "128Mi"
    storage: "50Mi"
  
  offline:
    enabled: true
    cacheStrategy: local
    syncOnReconnect: true
```

### 5.2 Nexus UI Skill 模板

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-xxx-ui
  name: UI Skill Name
  version: 1.0.0
  description: UI skill description
  author: Ooder Team
  type: nexus-ui
  license: Apache-2.0
  homepage: https://gitee.com/ooderCN/skills
  keywords:
    - ui
    - xxx

spec:
  type: nexus-ui
  
  dependencies:
    - id: skill-backend
      version: ">=1.0.0"
      required: true
      description: Backend service
  
  capabilities:
    - id: ui-view
      name: View
      description: View capability
      category: ui
    - id: ui-manage
      name: Manage
      description: Manage capability
      category: ui
  
  nexusUi:
    entry:
      page: index.html
      title: UI Title
      icon: ri-xxx-line
      
    menu:
      position: sidebar
      category: category-name
      order: 10
      
    layout:
      type: default
      sidebar: true
      header: true
  
  sceneBindings:
    - sceneType: xxx
      autoBind: true
      capabilities:
        - ui-view
        - ui-manage
  
  resources:
    cpu: "50m"
    memory: "64Mi"
    storage: "10Mi"
```

---

## 六、后续行动

1. **立即修复**: 问题1-3（严重问题）
2. **本周修复**: 问题4-6（警告问题）
3. **下周优化**: 问题7-12（提示问题）
4. **建立规范**: 更新开发文档，确保新 skill 遵循规范

---

**报告生成时间**: 2026-03-03 19:30:00

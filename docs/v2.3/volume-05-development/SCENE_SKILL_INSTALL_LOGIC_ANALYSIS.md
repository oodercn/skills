# Ooder 场景技能安装逻辑分析报告

> **文档版本**: 1.0.0  
> **编写日期**: 2026-03-05  
> **分析范围**: 现有安装逻辑对四项标准的支持情况

---

## 一、现有代码结构概览

```
skill-scene/src/main/java/net/ooder/skill/scene/
├── capability/
│   ├── service/
│   │   ├── CapabilityDiscoveryService.java          # 能力发现服务接口
│   │   └── impl/
│   │       └── CapabilityDiscoveryServiceImpl.java  # 能力发现服务实现
│   ├── install/
│   │   ├── InstallService.java                      # 安装服务接口
│   │   ├── InstallServiceImpl.java                  # 安装服务实现
│   │   └── InstallConfig.java                       # 安装配置
│   ├── activation/
│   │   └── ActivationProcess.java                   # 激活流程
│   ├── model/
│   │   ├── Capability.java                          # 能力模型
│   │   ├── CapabilityType.java                      # 能力类型枚举
│   │   └── DriverType.java                          # 驱动类型
│   └── driver/
│       └── DriverCondition.java                     # 驱动条件
└── controller/
    └── InstallController.java                       # 安装控制器
```

---

## 二、四项标准支持情况分析

### 2.1 标准1: `metadata.type = scene-skill`

#### 支持情况: ⚠️ **部分支持**

| 检查点 | 支持状态 | 说明 |
|--------|---------|------|
| 类型声明检查 | ⚠️ 部分支持 | Capability模型有`type`字段，但未明确区分`scene-skill` |
| Skill类型识别 | ❌ 不支持 | 代码中未检查`metadata.type` |
| 类型过滤 | ✓ 支持 | `CapabilityDiscoveryServiceImpl`支持按`CapabilityType`过滤 |

**代码证据**:
```java
// Capability.java - 有type字段
private CapabilityType type;

// CapabilityDiscoveryServiceImpl.java - 支持按类型过滤
if (request.getType() != null) {
    allCapabilities = allCapabilities.stream()
        .filter(c -> c.getType() == request.getType())
        .collect(Collectors.toList());
}
```

**缺失功能**:
- 未在`CapabilityType`枚举中定义`SCENE_SKILL`类型
- 未从skill.yaml中读取`metadata.type`进行验证
- 安装流程未根据类型进行分支处理

---

### 2.2 标准2: `spec.sceneCapabilities` 非空

#### 支持情况: ⚠️ **部分支持**

| 检查点 | 支持状态 | 说明 |
|--------|---------|------|
| sceneCapabilities字段 | ✓ 支持 | Capability模型有`capabilities`字段 |
| 非空检查 | ❌ 不支持 | 安装流程未检查sceneCapabilities |
| 场景能力识别 | ⚠️ 部分支持 | 通过`CapabilityType.SCENE`识别 |

**代码证据**:
```java
// Capability.java - 有capabilities字段
private List<String> capabilities;

// CapabilityDiscoveryServiceImpl.java - 识别场景能力
if (cap.getType() == CapabilityType.SCENE) {
    sceneCapabilities.add(item);
}
```

**缺失功能**:
- 未检查`spec.sceneCapabilities`是否非空
- 未解析sceneCapabilities的具体配置
- 安装时未验证场景能力依赖

---

### 2.3 标准3: `mainFirst = true` 及 `mainFirstConfig`

#### 支持情况: ✓ **基本支持**

| 检查点 | 支持状态 | 说明 |
|--------|---------|------|
| mainFirst字段 | ✓ 支持 | Capability模型有`mainFirst`字段 |
| mainFirstConfig字段 | ✓ 支持 | Capability模型有`mainFirstConfig`字段 |
| hasMainFirst()方法 | ✓ 支持 | 提供`hasMainFirst()`检查方法 |
| 自驱流程执行 | ⚠️ 部分支持 | ActivationProcess定义了步骤，但未完整实现 |

**代码证据**:
```java
// Capability.java - 支持mainFirst
private boolean mainFirst;
private MainFirstConfig mainFirstConfig;

public boolean hasMainFirst() {
    return mainFirst && mainFirstConfig != null;
}

// ActivationProcess.java - 定义激活步骤
public static ActivationProcess createDefault(String installId) {
    // 定义了5个激活步骤
    // 1. confirm-participants
    // 2. config-conditions
    // 3. get-key
    // 4. confirm-activation
    // 5. network-actions
}
```

**缺失功能**:
- 未根据`mainFirst`值进行安装分支处理
- `mainFirstConfig`的具体配置未解析（selfCheck, selfStart, selfDrive）
- 自驱系统场景和触发业务场景的分支逻辑缺失

---

### 2.4 标准4: 业务语义（driverConditions/participants）

#### 支持情况: ✓ **基本支持**

| 检查点 | 支持状态 | 说明 |
|--------|---------|------|
| driverConditions | ✓ 支持 | `CapabilityDiscoveryServiceImpl`提供`getDriverConditions()` |
| participants | ✓ 支持 | `InstallConfig`支持`participants`配置 |
| 可发现性 | ✓ 支持 | 支持按`sceneType`和`query`查询 |
| 可分发性 | ✓ 支持 | `InstallServiceImpl`支持`pushToParticipants()` |

**代码证据**:
```java
// CapabilityDiscoveryServiceImpl.java - 驱动条件
public List<DriverCondition> getDriverConditions(String capabilityId) {
    // 返回驱动条件列表
}

// InstallServiceImpl.java - 参与者管理
public InstallConfig createInstall(CreateInstallRequest request) {
    if (request.getParticipants() != null) {
        InstallConfig.Participants participants = new InstallConfig.Participants();
        // 设置leader和collaborators
    }
}

// 分发推送
public InstallConfig pushToParticipants(String installId, PushRequest request) {
    config.setPushed(true);
}
```

**缺失功能**:
- 未根据`driverConditions`进行场景分类
- 未根据参与者配置进行权限分支处理
- 缺少`visibility`标签支持（public/internal）

---

## 三、分类查询支持情况

### 3.1 现有查询能力

| 查询维度 | 支持状态 | API/方法 |
|---------|---------|---------|
| 按类型查询 | ✓ 支持 | `GET /api/v1/capabilities/discover?type={type}` |
| 按场景类型查询 | ✓ 支持 | `GET /api/v1/capabilities/discover?sceneType={type}` |
| 关键词搜索 | ✓ 支持 | `GET /api/v1/capabilities/discover?query={keyword}` |
| 按分类查询 | ❌ 不支持 | 缺少完整/技术/半自动分类查询 |
| 按自驱能力查询 | ❌ 不支持 | 缺少mainFirst筛选 |
| 按可见性查询 | ❌ 不支持 | 缺少visibility筛选 |

### 3.2 缺失的分类查询

```java
// 建议增加的查询参数
public class DiscoveryRequest {
    private CapabilityType type;                    // 已有
    private String sceneType;                       // 已有
    private String query;                           // 已有
    
    // 建议增加
    private SceneSkillCategory category;            // 完整/技术/半自动
    private Boolean mainFirst;                      // 是否自驱
    private Visibility visibility;                  // public/internal
    private Boolean hasParticipants;                // 是否有参与者
    private Boolean hasDriverConditions;            // 是否有驱动条件
}
```

---

## 四、安装及分发过程中的分支处理

### 4.1 现有安装流程

```
CreateInstallRequest
    ↓
InstallServiceImpl.createInstall()
    ↓
InstallConfig (PENDING状态)
    ↓
executeInstall() - 模拟安装
    ↓
pushToParticipants() - 推送
    ↓
ActivationProcess.createDefault() - 激活流程
```

### 4.2 缺失的分支处理逻辑

| 分支点 | 现有处理 | 应有处理 |
|--------|---------|---------|
| **类型检测** | 无分支 | 根据`metadata.type`区分scene-skill和普通skill |
| **自驱检测** | 无分支 | 根据`mainFirst`区分自驱业务场景和自驱系统场景 |
| **业务语义检测** | 无分支 | 根据`driverConditions/participants`区分触发业务场景 |
| **安装流程** | 统一流程 | 三类场景不同的安装步骤 |
| **激活流程** | 统一5步 | 根据分类有不同的激活步骤 |
| **生命周期** | 无管理 | 根据分类有不同的状态流转 |

### 4.3 建议的分支处理架构

```java
public class SceneSkillInstallService {
    
    public InstallResult install(InstallRequest request) {
        // 1. 检测场景技能分类
        SceneSkillCategory category = detectCategory(request);
        
        // 2. 根据分类选择安装策略
        InstallStrategy strategy = getStrategy(category);
        
        // 3. 执行分类特定的安装流程
        return strategy.install(request);
    }
    
    private SceneSkillCategory detectCategory(InstallRequest request) {
        // 标准1: 检查 metadata.type
        if (!"scene-skill".equals(request.getMetadata().getType())) {
            throw new NotSceneSkillException();
        }
        
        // 标准2: 检查 sceneCapabilities
        if (request.getSceneCapabilities() == null || request.getSceneCapabilities().isEmpty()) {
            throw new MissingSceneCapabilitiesException();
        }
        
        SceneCapability cap = request.getSceneCapabilities().get(0);
        
        // 标准3: 检查 mainFirst
        boolean hasMainFirst = cap.isMainFirst() && cap.getMainFirstConfig() != null;
        
        // 标准4: 检查业务语义
        boolean hasBusinessSemantics = cap.getDriverConditions() != null 
            && cap.getParticipants() != null;
        
        // 分类判断
        if (hasMainFirst && hasBusinessSemantics) {
            return SceneSkillCategory.FULL;           // 自驱业务场景
        } else if (hasMainFirst && !hasBusinessSemantics) {
            return SceneSkillCategory.TECHNICAL;      // 自驱系统场景
        } else if (!hasMainFirst && hasBusinessSemantics) {
            return SceneSkillCategory.SEMI_AUTO;      // 触发业务场景
        } else {
            throw new InvalidSceneSkillException();   // 不符合任何分类
        }
    }
}
```

---

## 五、具体缺失功能清单

### 5.1 模型层缺失

| 缺失项 | 影响 | 优先级 |
|--------|------|--------|
| `SceneSkillCategory` 枚举 | 无法区分三类场景 | P0 |
| `Visibility` 枚举 | 无法区分public/internal | P1 |
| `Metadata` 类缺少`type`字段 | 无法检查标准1 | P0 |
| `SceneCapability` 类 | 未从skill.yaml解析 | P0 |
| `MainFirstConfig` 详细字段 | 无法获取自驱配置 | P1 |

### 5.2 服务层缺失

| 缺失项 | 影响 | 优先级 |
|--------|------|--------|
| 场景技能分类检测 | 无法根据四项标准分类 | P0 |
| 分类特定的安装策略 | 所有场景使用同一流程 | P0 |
| 分类特定的激活流程 | 激活步骤未根据分类调整 | P1 |
| 分类查询API | 无法按分类查询场景技能 | P1 |
| 生命周期状态管理 | 缺少不同分类的状态流转 | P1 |

### 5.3 控制器层缺失

| 缺失项 | 影响 | 优先级 |
|--------|------|--------|
| 分类查询接口 | 前端无法筛选场景类型 | P1 |
| 分类统计接口 | 无法统计各分类数量 | P2 |
| 批量操作接口 | 无法按分类批量操作 | P2 |

---

## 六、建议实现方案

### 6.1 短期方案（快速修复）

1. **增加分类检测逻辑**
   ```java
   // 在InstallServiceImpl中增加
   public SceneSkillCategory detectCategory(Capability capability) {
       // 实现四项标准检测
   }
   ```

2. **扩展查询参数**
   ```java
   // 在DiscoveryRequest中增加
   private Boolean mainFirst;
   private String visibility;
   ```

3. **增加分类标记**
   ```java
   // 在Capability模型中增加
   private String sceneSkillCategory;  // FULL/TECHNICAL/SEMI_AUTO
   ```

### 6.2 长期方案（完整实现）

1. **重构安装流程**
   - 实现策略模式，三类场景各自独立的安装策略
   - 根据分类自动选择安装流程

2. **完善生命周期管理**
   - 实现不同分类的状态流转
   - 增加生命周期钩子支持

3. **增强查询能力**
   - 支持复合条件查询
   - 增加分类统计功能

---

## 七、总结

### 7.1 支持情况总览

| 标准 | 支持度 | 主要缺失 |
|------|--------|---------|
| **标准1** | 50% | 未检查`metadata.type`，缺少`SCENE_SKILL`类型 |
| **标准2** | 60% | 未检查`sceneCapabilities`非空，未解析详细配置 |
| **标准3** | 70% | 有字段支持，但未根据`mainFirst`进行分支处理 |
| **标准4** | 75% | 有基础支持，缺少`visibility`标签和分类逻辑 |

### 7.2 关键问题

1. **缺少分类检测**: 现有代码无法根据四项标准将场景技能分为三类
2. **统一处理流程**: 所有场景技能使用相同的安装和激活流程
3. **查询能力不足**: 无法按分类、自驱能力、可见性等维度查询
4. **生命周期管理缺失**: 未实现不同分类的生命周期状态流转

### 7.3 建议优先级

| 优先级 | 任务 | 影响 |
|--------|------|------|
| **P0** | 实现场景技能分类检测 | 核心功能，影响所有场景技能识别 |
| **P0** | 区分自驱业务/自驱系统/触发业务场景的安装流程 | 核心功能，影响场景技能正确安装 |
| **P1** | 增加分类查询API | 重要功能，影响前端场景发现 |
| **P1** | 实现生命周期状态管理 | 重要功能，影响场景技能运行 |
| **P2** | 增加批量操作和统计功能 | 增强功能，提升用户体验 |

---

**分析人员**: Ooder 开发团队  
**分析日期**: 2026-03-05  
**结论**: 现有安装逻辑对四项标准有基础支持，但缺少关键的分类检测和分支处理逻辑，需要优先实现场景技能分类识别功能。

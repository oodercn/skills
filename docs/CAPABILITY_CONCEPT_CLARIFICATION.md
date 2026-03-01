# 能力管理核心概念澄清与扩展建议

> **文档类型**: 技术答疑与扩展建议  
> **目标读者**: SDK团队  
> **创建日期**: 2026-03-01  
> **文档状态**: 待讨论

---

## 一、背景

在skill-scene模块开发过程中，我们对能力管理机制进行了深入分析。基于对 `agent-sdk` 源码的研究，发现了一些需要澄清和扩展的设计点。本文档旨在：

1. 澄清现有SDK设计中的核心概念
2. 提出需要扩展的功能点
3. 为skill-scene模块开发提供设计依据

---

## 二、核心概念澄清

### 2.1 能力ID的双重标识

**源码发现**：

```java
// Capability.java
public interface Capability {
    String getCapId();           // 能力ID
    String getCapabilityId();    // 能力标识ID
    void setCapabilityId(String capabilityId);
    // ...
}
```

**问题**：`capId` 与 `capabilityId` 的区别是什么？

**当前理解**：

| 标识 | 用途 | 示例 | 说明 |
|------|------|------|------|
| `capabilityId` | 全局唯一标识 | `email-access-001` | 能力的全局唯一ID，跨场景使用 |
| `capId` | 场景内短ID | `report-email` | 通过 `scenePrefix + functionName` 生成 |

**建议澄清**：

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      能力ID双重标识关系                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   场景外（全局）                        场景内（局部）                   │
│   ─────────────                        ─────────────                   │
│                                                                         │
│   capabilityId                         capId                           │
│   "skill-email-001"                    "daily-report-email"            │
│        │                                    │                          │
│        │    加入场景时映射                   │                          │
│        └────────────────────────────────────┘                          │
│                                                                         │
│   用途：                                用途：                          │
│   - 能力注册表索引                      - 场景内能力引用                │
│   - 跨场景能力发现                      - 工作流步骤定义                │
│   - 能力路由查找                        - 能力绑定关系                  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

**需要SDK团队确认**：

1. `capId` 是否由场景在运行时生成？
2. 一个 `capabilityId` 是否可以在多个场景中映射为不同的 `capId`？
3. 两者之间的映射关系由谁维护？

---

### 2.2 能力注册时声明支持的场景

**源码发现**：

当前 `Capability` 接口中没有 `supportedScenes` 或类似字段：

```java
public interface Capability {
    String getCapId();
    String getName();
    String getType();
    String getVersion();
    CapAddress getAddress();
    // 没有 supportedScenes 字段
}
```

**业务需求**：

根据skill-scene模块的设计，存在两种场景类型：

1. **声明式场景**：能力注册时声明支持的场景类型，系统自动匹配加入
   - 示例：灯泡注册时声明支持"开关场景"，网关自动将其加入"离家模式"场景

2. **绑定式场景**：需要手动绑定能力到场景
   - 示例：开关控制灯泡，需要手动建立1:1或1:N的绑定关系

**建议扩展**：

```java
public interface Capability {
    // 现有字段...
    
    /**
     * 获取能力支持的场景类型列表
     * 用于声明式场景自动匹配
     * 
     * @return 支持的场景类型列表，为空表示不支持自动匹配
     */
    List<String> getSupportedSceneTypes();
    
    /**
     * 设置能力支持的场景类型
     * 
     * @param sceneTypes 场景类型列表
     */
    void setSupportedSceneTypes(List<String> sceneTypes);
    
    /**
     * 判断能力是否支持指定场景类型
     * 
     * @param sceneType 场景类型
     * @return true表示支持
     */
    default boolean supportsSceneType(String sceneType) {
        List<String> types = getSupportedSceneTypes();
        return types != null && types.contains(sceneType);
    }
}
```

**场景类型匹配流程**：

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    声明式场景自动匹配流程                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   1. 能力注册                                                           │
│      ┌─────────────┐                                                    │
│      │   灯泡      │ supportedSceneTypes: ["switch", "dimmer"]          │
│      │ capabilityId│                                                    │
│      │ "light-001" │                                                    │
│      └──────┬──────┘                                                    │
│             │                                                           │
│             │ 注册到 CapRegistry                                         │
│             ▼                                                           │
│      ┌─────────────┐                                                    │
│      │ CapRegistry │                                                    │
│      └──────┬──────┘                                                    │
│             │                                                           │
│             │ 触发场景匹配                                               │
│             ▼                                                           │
│   2. 场景匹配                                                           │
│      ┌─────────────┐                                                    │
│      │ SceneManager│ 查询支持"switch"类型的场景                         │
│      └──────┬──────┘                                                    │
│             │                                                           │
│             │ 匹配到"离家模式"场景                                       │
│             ▼                                                           │
│   3. 自动加入                                                           │
│      ┌─────────────┐                                                    │
│      │ "离家模式"  │ 自动添加 light-001 到场景                          │
│      │ SceneGroup  │ CAP地址: A0:01                                     │
│      └─────────────┘                                                    │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### 2.3 CAP地址区域划分

**源码发现**：

当前 `CapAddress` 实现较为简单：

```java
public class CapAddress {
    private final int address;      // 0-255
    private final String domainId;  // 域ID
    
    public static CapAddress of(int address) {
        return new CapAddress(address, "default");
    }
}
```

`InMemoryCapRegistry` 中的地址分配：

```java
public CapAddress allocateAddress(String domainId) {
    Set<Integer> usedAddresses = domainAddresses.getOrDefault(domainId, Collections.emptySet());
    
    for (int i = 0; i <= 255; i++) {  // 简单遍历分配
        if (!usedAddresses.contains(i)) {
            return CapAddress.of(i, domainId);
        }
    }
    throw new CapRegistryException("No available address");
}
```

**文档规范**：

根据 `CAP-REGISTRY-SPEC.md` 规范，CAP地址空间应有明确的区域划分：

| 区域 | 地址范围 | 数量 | 用途 |
|------|----------|------|------|
| 系统区 | 00-3F | 64 | 核心系统能力 |
| 通用区 | 40-9F | 96 | 通用业务能力 |
| 扩展区 | A0-FF | 96 | 扩展能力（私有域） |

**问题**：当前SDK实现中没有体现这个划分，权限控制如何实现？

**建议扩展**：

```java
public class CapAddress {
    private final int address;
    private final String domainId;
    
    /**
     * 地址区域枚举
     */
    public enum AddressZone {
        SYSTEM(0x00, 0x3F, "系统区", false),      // 不可自定义分配
        GENERAL(0x40, 0x9F, "通用区", false),     // 不可自定义分配
        EXTENSION(0xA0, 0xFF, "扩展区", true);    // 可自定义分配
        
        private final int start;
        private final int end;
        private final String name;
        private final boolean customizable;
        
        public static AddressZone fromAddress(int address) {
            if (address <= 0x3F) return SYSTEM;
            if (address <= 0x9F) return GENERAL;
            return EXTENSION;
        }
        
        public boolean isAccessibleFrom(String sourceDomain, String targetDomain) {
            if (this == SYSTEM) return true;  // 系统区全局可访问
            if (this == GENERAL) return true;  // 通用区场景内可访问
            return sourceDomain.equals(targetDomain);  // 扩展区仅同域可访问
        }
    }
    
    /**
     * 获取地址所属区域
     */
    public AddressZone getZone() {
        return AddressZone.fromAddress(address);
    }
    
    /**
     * 检查访问权限
     */
    public boolean isAccessibleFrom(String sourceDomain) {
        return getZone().isAccessibleFrom(sourceDomain, this.domainId);
    }
}
```

**权限控制示例**：

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    CAP地址区域权限控制                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   场景：日志汇报                                                        │
│   ─────────────────                                                     │
│                                                                         │
│   员工A的私有能力：                                                     │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │  邮箱访问能力                                                    │   │
│   │  capabilityId: "email-001"                                       │   │
│   │  address: A0:01 (扩展区)                                         │   │
│   │  domainId: "user-a-private"                                      │   │
│   │                                                                  │   │
│   │  访问控制：                                                      │   │
│   │  ├── 员工A的私有LLM (domain: user-a-private) → ✅ 可访问        │   │
│   │  └── 公司LLM (domain: company-shared) → ❌ 不可访问             │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│   公司公共能力：                                                        │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │  日志提醒能力                                                    │   │
│   │  capabilityId: "remind-001"                                      │   │
│   │  address: 40:01 (通用区)                                         │   │
│   │  domainId: "company-shared"                                      │   │
│   │                                                                  │   │
│   │  访问控制：                                                      │   │
│   │  ├── 员工A的私有LLM → ✅ 可访问                                  │   │
│   │  └── 公司LLM → ✅ 可访问                                         │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 三、扩展建议汇总

### 3.1 Capability接口扩展

```java
public interface Capability {
    
    // ===== 现有字段 =====
    String getCapId();
    void setCapId(String capId);
    
    String getCapabilityId();
    void setCapabilityId(String capabilityId);
    
    String getName();
    void setName(String name);
    
    String getType();
    void setType(String type);
    
    CapAddress getAddress();
    void setAddress(CapAddress address);
    
    // ===== 建议新增字段 =====
    
    /**
     * 能力支持的场景类型列表（用于声明式场景匹配）
     */
    List<String> getSupportedSceneTypes();
    void setSupportedSceneTypes(List<String> sceneTypes);
    
    /**
     * 能力的访问级别
     */
    AccessLevel getAccessLevel();
    void setAccessLevel(AccessLevel level);
    
    /**
     * 能力的所有者/提供者
     */
    String getOwnerId();
    void setOwnerId(String ownerId);
    
    /**
     * 访问级别枚举
     */
    enum AccessLevel {
        PRIVATE,      // 私有：仅所有者可访问
        DOMAIN,       // 域内：同域可访问
        SCENE,        // 场景内：同场景可访问
        PUBLIC        // 公共：全局可访问
    }
}
```

### 3.2 CapAddress扩展

```java
public class CapAddress {
    
    private final int address;
    private final String domainId;
    
    // ===== 建议新增 =====
    
    /**
     * 获取地址区域
     */
    public AddressZone getZone();
    
    /**
     * 检查访问权限
     */
    public boolean isAccessibleFrom(String sourceDomain);
    
    /**
     * 地址区域枚举
     */
    public enum AddressZone {
        SYSTEM(0x00, 0x3F),
        GENERAL(0x40, 0x9F),
        EXTENSION(0xA0, 0xFF);
    }
}
```

### 3.3 CapRegistry扩展

```java
public interface CapRegistry {
    
    // ===== 现有方法 =====
    void register(Capability capability);
    Capability findByAddress(CapAddress address);
    CapAddress allocateAddress(String domainId);
    
    // ===== 建议新增 =====
    
    /**
     * 按场景类型查找能力
     */
    List<Capability> findBySceneType(String sceneType);
    
    /**
     * 在指定区域分配地址
     */
    CapAddress allocateAddress(String domainId, AddressZone zone);
    
    /**
     * 检查访问权限
     */
    boolean checkAccess(String sourceDomain, CapAddress targetAddress);
}
```

---

## 四、对skill-scene模块的影响

### 4.1 场景创建流程

```
场景创建时：
1. 解析场景模板，获取能力需求列表
2. 对于每个能力需求：
   a. 如果是声明式场景，查询 supportedSceneTypes 匹配的能力
   b. 如果是绑定式场景，等待手动绑定
3. 为绑定的能力分配CAP地址（考虑区域权限）
4. 建立能力路由表
```

### 4.2 能力调用流程

```
能力调用时：
1. 根据capId查找对应的capabilityId
2. 获取能力的CapAddress
3. 检查调用者是否有权限访问该地址
4. 通过CapabilityRouter路由执行
```

### 4.3 权限控制流程

```
权限检查时：
1. 获取目标能力的CapAddress
2. 判断地址所属区域（系统区/通用区/扩展区）
3. 根据区域规则检查访问权限：
   - 系统区：全局可访问
   - 通用区：场景内可访问
   - 扩展区：仅同域可访问
```

---

## 五、SDK更新记录

### 5.1 已完成更新

| 文件 | 更新内容 | 状态 |
|------|----------|------|
| `CapAddress.java` | 添加区域划分功能（AddressZone枚举） | ✅ 已完成 |
| `Capability.java` | 添加场景类型支持（supportedSceneTypes） | ✅ 已完成 |
| `SceneTypes.java` | 新增场景类型常量类 | ✅ 已完成 |

### 5.2 新增功能说明

#### CapAddress.java 更新

```java
// 地址区域枚举
public enum AddressZone {
    SYSTEM(0x00, 0x3F, "系统区"),    // 全局可访问
    GENERAL(0x40, 0x9F, "通用区"),   // 场景内可访问
    EXTENSION(0xA0, 0xFF, "扩展区"); // 同域可访问
}

// 新增方法
public AddressZone getZone();
public boolean isAccessibleFrom(String sourceDomain);
public boolean isInZone(AddressZone zone);
public static CapAddress ofZone(AddressZone zone, String domainId);
```

#### Capability.java 更新

```java
// 新增方法
List<String> getSupportedSceneTypes();
void setSupportedSceneTypes(List<String> sceneTypes);
default boolean supportsSceneType(String sceneType);
default boolean supportsDeclarativeScenes();
```

#### SceneTypes.java 新增

```java
// 场景类型常量
public static final String SWITCH = "switch";           // 开关场景
public static final String DIMMER = "dimmer";           // 调光场景
public static final String SENSOR = "sensor";           // 传感器场景
public static final String AWAY_MODE = "away-mode";     // 离家模式
// ... 更多预定义场景类型

// 工具方法
public static boolean isDeclarativeType(String sceneType);
public static boolean isBindingType(String sceneType);
public static boolean isValidSceneType(String sceneType);
```

### 5.3 待确认事项

| # | 问题 | 影响范围 | 优先级 | 状态 |
|---|------|----------|--------|------|
| 1 | `capId` 与 `capabilityId` 的映射关系由谁维护？ | 能力路由 | 高 | 待确认 |
| 2 | 是否计划支持 `supportedSceneTypes` 字段？ | 声明式场景 | 中 | ✅ 已实现 |
| 3 | CAP地址区域划分是否在SDK层面实现？ | 权限控制 | 高 | ✅ 已实现 |
| 4 | 能力的 `AccessLevel` 是否需要新增？ | 安全控制 | 中 | 待确认 |
| 5 | 场景内能力绑定的生命周期如何管理？ | 场景管理 | 高 | 待确认 |

---

## 六、附录

### A. 相关源码文件

| 文件 | 路径 | 说明 |
|------|------|------|
| Capability.java | agent-sdk-api/.../capability/Capability.java | 能力接口定义 |
| CapAddress.java | agent-sdk-api/.../capability/CapAddress.java | CAP地址定义 |
| CapRegistry.java | agent-sdk-core/.../capability/CapRegistry.java | CAP注册表接口 |
| InMemoryCapRegistry.java | agent-sdk-core/.../capability/impl/ | CAP注册表实现 |
| SceneDefinition.java | agent-sdk-api/.../scene/SceneDefinition.java | 场景定义 |
| SceneGroup.java | agent-sdk-api/.../scene/SceneGroup.java | 场景组定义 |

### B. 相关文档

| 文档 | 路径 | 说明 |
|------|------|------|
| CAP-REGISTRY-SPEC.md | docs/v2.3/ | CAP注册表规范 |
| CAPABILITY-DISCOVERY-PROTOCOL.md | docs/v2.3/ | 能力发现协议 |
| CAPABILITY_CENTER_SPECIFICATION.md | agent-sdk/docs/manuals/ | 能力中心规范 |

---

**文档维护者**: skill-scene开发团队  
**联系方式**: [待补充]

---

*本文档将根据SDK团队的反馈持续更新*

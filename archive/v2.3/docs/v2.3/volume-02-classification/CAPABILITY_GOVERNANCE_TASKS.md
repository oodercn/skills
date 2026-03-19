# 能力治理专项任务清单

> **文档版本**: v1.1  
> **创建日期**: 2026-03-08  
> **更新日期**: 2026-03-08  
> **状态**: 进行中  
> **关联文档**: GLOSSARY_V2.3.md

---

## 一、背景与目标

### 1.1 背景

随着技能开发进度推进，进入"能力大爆炸"阶段，需要针对能力进行专项治理：

1. **场景内部能力**：完全附属于特定场景的内部功能（如招聘场景的简历收集、面试邀约）
2. **独立能力**：具有独立单一功能，可被多场景复用（如 MQTT Push、PDF转换）

### 1.2 目标

- 明确能力归属关系
- 规范能力调用规则
- 实现能力生命周期治理
- 支持独立能力多场景组运行

---

## 二、核心概念

### 2.1 能力归属分类

| 类型 | 简写 | 定义位置 | 可见性 | 生命周期 |
|------|------|----------|--------|----------|
| 场景内部能力 | SIC | skill.yaml的capabilities列表 | 仅场景内 | 绑定场景 |
| 独立能力 | IC | 独立skill包 | supportedSceneTypes | 独立运行 |
| 平台能力 | PC | 平台内置 | 全局 | 平台管理 |

### 2.2 协作关系规则

| 协作类型 | 规则 |
|---------|------|
| 同场景内能力调用 | ✅ 允许 - 通过 CapabilityInvoker |
| 跨场景能力调用 | ⚠️ 有条件 - 通过 MCP Agent 命令转发 |
| 场景间直接协作 | ❌ 禁止 - 通过 LLM 命令方式间接协作 |
| 独立能力多场景组 | ✅ 允许 - 数据严格隔离 |
| 跨场景数据交互 | ❌ 严格禁止 |

---

## 三、任务清单

### 3.1 Phase 1: 规范与文档 (P0)

| 任务ID | 任务名称 | 优先级 | 状态 | 负责人 |
|--------|---------|--------|------|--------|
| GOV-001 | 创建能力治理专项任务清单 | P0 | ✅ 完成 | - |
| GOV-002 | 更新术语表 GLOSSARY_V2.3.md | P0 | ✅ 完成 | - |
| GOV-003 | 更新 skill.yaml 模板规范 | P0 | ✅ 完成 | - |
| GOV-004 | 编写能力归属判定指南 | P0 | ✅ 完成 | CAPABILITY_OWNERSHIP_GUIDE.md |

### 3.2 Phase 2: 模型与代码 (P0)

| 任务ID | 任务名称 | 优先级 | 状态 | 说明 |
|--------|---------|--------|------|------|
| GOV-010 | 检查 Capability 模型 | P0 | ✅ 完成 | 确认现有字段是否满足需求 |
| GOV-011 | 增加 dynamicSceneTypes 字段 | P0 | ✅ 完成 | 支持动态扩展场景类型 |
| GOV-012 | 增加 parentSkill 字段 | P1 | ✅ 完成 | 场景内部能力标识父技能 |
| GOV-013 | 增加 parentScene 字段 | P1 | ✅ 完成 | 场景内部能力标识父场景 |
| GOV-014 | 新增 CapabilityOwnership 枚举 | P0 | ✅ 完成 | 能力归属类型枚举 |

### 3.3 Phase 3: 服务与API (P1)

| 任务ID | 任务名称 | 优先级 | 状态 | 说明 |
|--------|---------|--------|------|------|
| GOV-020 | 实现 supportedSceneTypes 动态更新 API | P1 | ✅ 完成 | PUT /api/capability/{id}/scene-types |
| GOV-021 | 实现场景类型更新权限控制 | P1 | ✅ 完成 | 能力所有者申请，管理员审批 |
| GOV-022 | 实现场景类型更新事件通知 | P1 | ✅ 完成 | EventBus 通知已运行实例 |
| GOV-023 | 更新 CapabilityRegistry | P1 | ✅ 完成 | 增加场景类型索引 |
| GOV-024 | 更新 CapabilityDiscoveryService | P1 | ✅ 完成 | 支持归属类型过滤 |
| GOV-025 | 新增 SceneTypeUpdateRequest DTO | P1 | ✅ 完成 | 场景类型更新请求对象 |

### 3.4 Phase 4: 现有能力盘点 (P1)

| 任务ID | 任务名称 | 优先级 | 状态 | 说明 |
|--------|---------|--------|------|------|
| GOV-030 | 盘点现有能力归属类型 | P1 | ✅ 完成 | 分类为 SIC/IC/PC |
| GOV-031 | 更新现有 skill.yaml | P1 | ✅ 完成 | 添加归属相关配置 |
| GOV-032 | 验证协作关系规则 | P1 | ✅ 完成 | 确保符合治理规范 |
| GOV-033 | 创建能力盘点文档 | P1 | ✅ 完成 | CAPABILITY_INVENTORY.md |

---

## 四、技术设计

### 4.1 skill.yaml 模板更新

```yaml
capabilities:
  # 场景内部能力示例
  - id: resume-collection
    name: 简历收集
    type: ATOMIC
    # 隐式: 定义在 capabilities 列表 = 场景内部能力
    # 隐式: 可见性 = 仅场景内

dependencies:
  # 独立能力引用示例
  - id: mqtt-push
    version: ">=1.0.0"
    required: true
    # 独立能力配置
    supportedSceneTypes:      # 静态基础列表
      - recruitment
      - iot-monitoring
    dynamicSceneTypes: true   # 允许动态扩展
```

### 4.2 Capability 模型更新

```java
public class Capability {
    // 已有字段
    private String capabilityId;
    private String name;
    private CapabilityType type;
    private List<String> supportedSceneTypes;
    
    // 新增字段
    private boolean dynamicSceneTypes;  // 是否允许动态扩展场景类型
    private String parentSkill;         // 父技能ID (场景内部能力)
    private String parentScene;         // 父场景ID (场景内部能力)
    
    // 运行时属性 (不持久化)
    private transient CapabilityOwnership ownership;  // 归属类型 (推导)
    
    public CapabilityOwnership getOwnership() {
        if (parentSkill != null && parentScene != null) {
            return CapabilityOwnership.SCENE_INTERNAL;
        } else if (supportedSceneTypes != null && !supportedSceneTypes.isEmpty()) {
            return CapabilityOwnership.INDEPENDENT;
        } else {
            return CapabilityOwnership.PLATFORM;
        }
    }
}

public enum CapabilityOwnership {
    SCENE_INTERNAL,   // 场景内部能力
    INDEPENDENT,      // 独立能力
    PLATFORM          // 平台能力
}
```

### 4.3 API 设计

```yaml
# 场景类型动态更新 API
PUT /api/capability/{capabilityId}/scene-types
Request:
  {
    "action": "add",           # add | remove
    "sceneType": "smart-home",
    "reason": "新增智能家居场景支持",
    "approvedBy": "admin-001"  # 审批人 (可选)
  }
Response:
  {
    "status": "success",
    "capabilityId": "mqtt-push",
    "supportedSceneTypes": ["recruitment", "iot-monitoring", "smart-home"],
    "updatedAt": "2026-03-08T10:00:00Z"
  }

# 能力发现 API 扩展
GET /api/capability/discover?ownership=INDEPENDENT&sceneType=recruitment
```

---

## 五、验收标准

### 5.1 Phase 1 验收标准

- [ ] 术语表包含能力归属术语
- [ ] skill.yaml 模板包含归属配置示例
- [ ] 能力归属判定指南完成

### 5.2 Phase 2 验收标准

- [ ] Capability 模型支持归属类型推导
- [ ] 支持动态场景类型配置
- [ ] 单元测试覆盖

### 5.3 Phase 3 验收标准

- [ ] 场景类型动态更新 API 可用
- [ ] 权限控制生效
- [ ] 事件通知机制工作正常

### 5.4 Phase 4 验收标准

- [ ] 现有能力完成归属分类
- [ ] 所有 skill.yaml 更新完成
- [ ] 协作关系规则验证通过

---

## 六、风险与依赖

### 6.1 风险

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| 现有能力迁移工作量 | 中 | 分批迁移，优先核心能力 |
| 动态更新权限控制复杂 | 低 | 复用现有权限体系 |
| 多场景组数据隔离 | 中 | 复用现有场景组隔离机制 |

### 6.2 依赖

| 依赖 | 说明 |
|------|------|
| skill-scene 模块 | 能力模型、注册表、发现服务 |
| MCP Agent | 场景间命令转发 |
| EventBus | 场景类型更新通知 |

---

## 七、时间规划

| 阶段 | 预计时间 | 说明 |
|------|---------|------|
| Phase 1 | 1天 | 规范与文档 |
| Phase 2 | 2天 | 模型与代码 |
| Phase 3 | 3天 | 服务与API |
| Phase 4 | 2天 | 现有能力盘点 |

**总计**: 约 8 个工作日

---

**文档维护**: Ooder 开发团队  
**最后更新**: 2026-03-08

# 第四分册：架构设计

> **版本**: v2.3.1  
> **更新日期**: 2026-03-05

---

## 分册说明

本分册包含系统架构设计和 Engine 协作状态。

---

## 文档清单

| 文档 | 说明 | 状态 |
|------|------|------|
| [CAPABILITY_DRIVEN_ARCHITECTURE.md](../CAPABILITY_DRIVEN_ARCHITECTURE.md) | 能力驱动架构 | ✅ 已发布 |
| [ENGINE_COLLABORATION_STATUS.md](../ENGINE_COLLABORATION_STATUS_V2.3.md) | Engine协作状态 | ✅ 已发布 |

---

## 能力驱动架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    能力驱动架构核心                               │
├─────────────────────────────────────────────────────────────────┤
│   1. 场景特性 = SuperAgent能力                                   │
│   2. 自驱机制 (mainFirst)                                        │
│   3. 驱动能力（DRIVER）                                          │
│   4. 执行器（EXECUTOR）                                          │
└─────────────────────────────────────────────────────────────────┘
```

---

## DRIVER vs EXECUTOR

| 类型 | 说明 | 示例 |
|------|------|------|
| **DRIVER** | 外部触发源 | intent-receiver, scheduler, event-listener |
| **EXECUTOR** | 内部执行器 | capability-invoker, collaboration-coordinator |

---

## Engine 协作状态

| Provider | 版本 | 状态 |
|---------|------|------|
| NetworkProviderImpl | 0.7.3 | ✅ 已完成 |
| SecurityProviderImpl | 0.7.3 | ✅ 已完成 |
| HostingProviderImpl | 0.7.3 | ✅ 已完成 |
| AgentProviderImpl | 0.8.0 | ✅ 已完成 |
| HealthProviderImpl | 0.9.0 | ✅ 已完成 |
| ProtocolProviderImpl | 0.9.0 | ✅ 已完成 |
| OpenWrtProviderImpl | 1.0.0 | ✅ 已完成 |
| SkillShareProviderImpl | 1.0.0 | ✅ 已完成 |

---

## 相关分册

- [第三分册：生命周期](../volume-03-lifecycle/)
- [第五分册：开发指南](../volume-05-development/)
- [术语表](../GLOSSARY_V2.3.md)

# Nexus 项目 SDK 协作需求说明

## 概述

本文档描述 ooder-Nexus 项目在升级到 SDK 2.3 过程中，需要 SDK 团队提供支持或扩展的功能需求。

---

## 一、需要 SDK 扩展的功能

### 1. OpenWrt 路由器管理能力

**现状**: 当前使用 `OpenWrtMockService` 模拟实现，无法真正管理 OpenWrt 路由器。

**需求**: SDK 提供路由器管理能力，包括：
- SSH 连接管理
- 网络配置（IP、DNS、DHCP）
- 防火墙规则管理
- 系统监控（CPU、内存、流量）

**影响范围**:
- `OpenWrtMockService.java`
- `OpenWrtBridge.java`
- 相关 Controller 约 20+ API

**优先级**: 中

---

### 2. Mock 服务保留建议

**现状**: `MockNexusService.java` 提供完整的模拟数据，用于开发和测试环境。

**建议**: SDK 升级时保留 Mock 服务作为测试用途，不要强制替换为真实实现。

**影响范围**:
- `MockNexusService.java`
- 测试环境配置

**优先级**: 低

---

## 二、可直接使用 SDK 2.3 替换的实现

以下 Mock/内存实现可以直接使用 SDK 2.3 的 `StorageService` 替换：

| Service | 当前实现 | SDK 替换方案 | 迁移难度 |
|---------|---------|-------------|----------|
| `ShareServiceMockImpl` | 内存 Map | `StorageService` | 低 |
| `LogRepositoryMock` | 内存 Queue | `StorageService` | 低 |
| `CollaborationServiceImpl` | ConcurrentHashMap | `StorageService` | 低 |
| `IMServiceImpl` | ConcurrentHashMap | `StorageService` | 中 |
| `FileServiceImpl` | 混合实现 | `StorageProvider` | 中 |

**迁移计划**: Nexus 团队将自行完成这些迁移工作。

---

## 三、SDK 2.3 包路径变更确认

以下包路径变更需要确认是否正确：

| 旧包路径 (0.7.x) | 新包路径 (2.3) | 确认状态 |
|-----------------|---------------|----------|
| `net.ooder.sdk.api.skill.InstalledSkill` | `net.ooder.skills.api.InstalledSkill` | ✅ 已确认 |
| `net.ooder.sdk.api.skill.SkillPackageManager` | `net.ooder.skills.api.SkillPackageManager` | ✅ 已确认 |
| `net.ooder.sdk.api.protocol.CommandPacket` | `net.ooder.sdk.api.command.CommandPacket` | ✅ 已确认 |
| `net.ooder.sdk.core.skill.lifecycle.SkillLifecycle` | `net.ooder.skills.core.lifecycle.SkillLifecycle` | ✅ 已确认 |

---

## 四、协作时间线

| 阶段 | 时间 | 内容 | 负责团队 |
|------|------|------|----------|
| 阶段1 | Week 1 | SDK 2.3 本地安装验证 | Nexus 团队 |
| 阶段2 | Week 2 | Mock 服务迁移到 StorageService | Nexus 团队 |
| 阶段3 | Week 3 | OpenWrt 真实实现（如 SDK 支持） | 协作 |
| 阶段4 | Week 4 | 集成测试与发布 | Nexus 团队 |

---

## 五、联系方式

- Nexus 项目路径: `E:\github\super-Agent\ooder-Nexus`
- SDK 项目路径: `E:\github\ooder-sdk`
- 协作文档路径: `E:\github\ooder-skills\docs\v2.3\`

---

## 附录：当前项目状态

- **SDK 版本**: 0.7.3 → 2.3 (计划升级)
- **Mock 服务数量**: 9 个
- **前端调用缺失 API**: 32 个
- **后端未使用 API**: 约 100+ 个

---

*文档创建时间: 2026-02-28*
*最后更新: 2026-02-28*

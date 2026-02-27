# Nexus-UI Skill 迁移协作清单

> **版本**: v1.1  
> **日期**: 2026-02-27  
> **状态**: ✅ 第一阶段完成

---

## 一、完成状态

### 1.1 Nexus 团队任务 ✅ 全部完成

| ID | 任务 | 状态 | 交付物 |
|----|------|------|--------|
| NX-001 | 扩展 skill.yaml 支持 nexusUi 配置 | ✅ 完成 | `docs/NEXUS_UI_SKILL_SPEC.md` |
| NX-002 | 扩展 StaticResourceConfig 支持 skills 目录 | ✅ 完成 | `nexus-config/StaticResourceConfig.java` |
| NX-003 | 实现菜单动态注册 | ✅ 完成 | `nexus-service/MenuRegistry.java` |
| NX-004 | 集成 SkillDiscoveryService | ✅ 完成 | `nexus-service/NexusSkillManager.java` |
| NX-005 | 集成 SkillService | ✅ 完成 | `nexus-service/NexusSkillManager.java` |

### 1.2 Skills 团队任务 ✅ 全部完成

| ID | 任务 | 状态 | 交付物 |
|----|------|------|--------|
| SK-001 | 迁移 nexus-dashboard | ✅ 完成 | `skills/skill-nexus-dashboard-nexus-ui/` |
| SK-002 | 迁移 system-status | ✅ 完成 | `skills/skill-nexus-system-status-nexus-ui/` |
| SK-003 | 迁移 personal-dashboard | ✅ 完成 | `skills/skill-personal-dashboard-nexus-ui/` |
| SK-004 | 迁移 health-check | ✅ 完成 | `skills/skill-nexus-health-check-nexus-ui/` |
| SK-005 | 迁移 storage-management | ✅ 完成 | `skills/skill-storage-management-nexus-ui/` |

---

## 二、交付物清单

### 2.1 规范文档

| 文档 | 路径 |
|------|------|
| Nexus-UI Skill 规范 | `docs/NEXUS_UI_SKILL_SPEC.md` |

### 2.2 服务代码

| 文件 | 说明 |
|------|------|
| `nexus-config/StaticResourceConfig.java` | 静态资源配置（支持 skills 目录） |
| `nexus-service/MenuRegistry.java` | 菜单动态注册服务 |
| `nexus-service/NexusSkillManager.java` | Skill 管理器（集成 SDK） |

### 2.3 Nexus-UI Skills

| Skill ID | 名称 | 菜单分类 |
|----------|------|----------|
| skill-nexus-dashboard-nexus-ui | Nexus仪表盘 | nexus |
| skill-nexus-system-status-nexus-ui | 系统状态 | nexus |
| skill-personal-dashboard-nexus-ui | 个人仪表盘 | personal-center |
| skill-nexus-health-check-nexus-ui | 健康检查 | system-config |
| skill-storage-management-nexus-ui | 存储管理 | resource-mgmt |

---

## 三、Skill 目录结构

```
skills/
├── skill-nexus-dashboard-nexus-ui/
│   ├── skill.yaml
│   └── ui/pages/index.html
├── skill-nexus-system-status-nexus-ui/
│   ├── skill.yaml
│   └── ui/pages/index.html
├── skill-personal-dashboard-nexus-ui/
│   ├── skill.yaml
│   └── ui/pages/index.html
├── skill-nexus-health-check-nexus-ui/
│   ├── skill.yaml
│   └── ui/pages/index.html
└── skill-storage-management-nexus-ui/
    ├── skill.yaml
    └── ui/pages/index.html
```

---

## 四、访问路径

| Skill | URL |
|-------|-----|
| Nexus仪表盘 | `/console/skills/skill-nexus-dashboard-nexus-ui/pages/index.html` |
| 系统状态 | `/console/skills/skill-nexus-system-status-nexus-ui/pages/index.html` |
| 个人仪表盘 | `/console/skills/skill-personal-dashboard-nexus-ui/pages/index.html` |
| 健康检查 | `/console/skills/skill-nexus-health-check-nexus-ui/pages/index.html` |
| 存储管理 | `/console/skills/skill-storage-management-nexus-ui/pages/index.html` |

---

## 五、后续工作

### 5.1 第二阶段（NLP 生成 UI）

- [ ] NLP 解析引擎集成
- [ ] 页面模板系统
- [ ] 能力绑定机制

### 5.2 更多页面迁移

- [ ] 网络管理页面（8个）
- [ ] 协议管理页面（5个）
- [ ] 管理后台页面（5个）

---

**文档版本**: v1.1  
**创建日期**: 2026-02-27  
**完成日期**: 2026-02-27

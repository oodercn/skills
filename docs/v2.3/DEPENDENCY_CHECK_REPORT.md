# Skills 依赖检查报告

> **检查日期**: 2026-03-06  
> **检查范围**: 本地 skills 目录 + skill-index.yaml  
> **检查目的**: 验证依赖项是否存在于本地或 Gitee 能力库

---

## 一、检查摘要

| 指标 | 数量 |
|------|------|
| 总技能数 | 83 |
| 声明依赖的技能数 | 11 |
| 依赖声明总数 | 24 |
| 本地存在的依赖 | 20 |
| 本地缺失的依赖 | 4 |

---

## 二、依赖检查详情

### 2.1 ✅ 依赖完整的技能

| 技能ID | 依赖项 | 本地存在 | skill-index.yaml |
|--------|--------|---------|-----------------|
| skill-llm-chat | skill-llm-conversation | ✅ | ✅ |
| skill-llm-chat | skill-llm-context-builder | ✅ | ❌ |
| skill-llm-chat | skill-llm-config-manager | ✅ | ❌ |
| skill-llm-conversation | skill-llm-context-builder | ✅ | ❌ |
| skill-llm-assistant-ui | skill-llm-conversation | ✅ | ✅ |
| skill-llm-assistant-ui | skill-llm-context-builder | ✅ | ❌ |
| skill-llm-assistant-ui | skill-local-knowledge | ✅ | ❌ |
| skill-llm-management-ui | skill-llm-conversation | ✅ | ✅ |
| skill-llm-management-ui | skill-llm-config-manager | ✅ | ❌ |
| skill-knowledge-ui | skill-knowledge-base | ✅ | ❌ |
| skill-knowledge-ui | skill-rag | ✅ | ❌ |
| skill-knowledge-ui | skill-document-processor | ✅ | ❌ |
| skill-nexus-system-status-nexus-ui | skill-monitor | ✅ | ✅ |
| skill-nexus-system-status-nexus-ui | skill-health | ✅ | ✅ |
| skill-nexus-health-check-nexus-ui | skill-health | ✅ | ✅ |
| skill-rag | skill-knowledge-base | ✅ | ❌ |
| skill-a2ui | skill-trae-solo | ✅ | ✅ |
| skill-trae-solo | skill-a2ui | ✅ | ✅ |

### 2.2 ❌ 依赖缺失的技能

| 技能ID | 缺失依赖 | 问题类型 | 建议 |
|--------|---------|---------|------|
| **skill-knowledge-qa** | skill-llm-assistant | ID错误 | 改为 skill-llm-assistant-ui |
| **skill-knowledge-qa** | skill-indexing | 不存在 | 创建或移除依赖 |

---

## 三、skill-index.yaml 缺失的技能

以下技能存在于本地但未添加到 skill-index.yaml：

| 技能ID | 分类 | 建议 |
|--------|------|------|
| skill-llm-context-builder | llm | 需添加 |
| skill-llm-config-manager | llm | 需添加 |
| skill-knowledge-base | knowledge | 需添加 |
| skill-rag | knowledge | 需添加 |
| skill-local-knowledge | knowledge | 需添加 |
| skill-document-processor | util | 需添加 |
| skill-knowledge-qa | scene-skill | 需添加 |
| skill-knowledge-ui | scene-skill | 需添加 |
| skill-llm-assistant-ui | scene-skill | 需添加 |
| skill-llm-management-ui | nexus-ui | 需添加 |
| skill-nexus-system-status-nexus-ui | scene-skill | 需添加 |
| skill-nexus-health-check-nexus-ui | scene-skill | 需添加 |
| skill-nexus-dashboard-nexus-ui | nexus-ui | 需添加 |
| skill-personal-dashboard-nexus-ui | nexus-ui | 需添加 |
| skill-storage-management-nexus-ui | nexus-ui | 需添加 |
| skill-org-base | org | 需添加 |
| skill-capability | sys | 需添加 |
| skill-management | sys | 需添加 |
| skill-vector-sqlite | knowledge | 需添加 |
| skill-vfs-base | vfs | 需添加 |

---

## 四、skill-scene 安装问题分析

### 4.1 问题描述

用户反馈安装失败，原因是 skill-scene 引用了不存在的依赖项：
- skill-capability
- skill-health
- collaborator

### 4.2 问题定位

经检查，skill-scene 的 skill.yaml **没有声明 dependencies**。问题出在：

1. **前端代码硬编码**：`role-installer.js` 中硬编码了安装列表：
   ```javascript
   { id: 'skill-capability', name: 'skill-capability 能力管理包' },
   { id: 'skill-health', name: 'skill-health 健康检查包' },
   ```

2. **skill-capability 存在但未发布**：本地有 `skills/skill-capability/` 目录，但未添加到 skill-index.yaml

3. **collaborator 不是技能**：这是角色名称，不是技能依赖

### 4.3 修复建议

1. 将 skill-capability 添加到 skill-index.yaml
2. 将 skill-health 添加到 skill-index.yaml（已存在）
3. 修改 role-installer.js 使用正确的安装逻辑

---

## 五、循环依赖检查

| 技能A | 技能B | 循环类型 |
|-------|-------|---------|
| skill-a2ui | skill-trae-solo | ⚠️ 双向依赖 |

**建议**: 移除其中一个依赖，改为可选依赖

---

## 六、修复清单

### P0 - 紧急修复

- [ ] 修复 skill-knowledge-qa 的依赖 ID（skill-llm-assistant → skill-llm-assistant-ui）
- [ ] 创建 skill-indexing 或移除该依赖
- [ ] 将 skill-capability 添加到 skill-index.yaml

### P1 - 重要修复

- [ ] 将缺失的 20 个技能添加到 skill-index.yaml
- [ ] 解决 skill-a2ui 和 skill-trae-solo 的循环依赖

### P2 - 一般优化

- [ ] 统一依赖版本声明格式
- [ ] 添加依赖自动安装配置（autoInstall）

---

## 七、依赖关系图

```
skill-llm-chat (ABS)
├── skill-llm-conversation ✅
│   └── skill-llm-context-builder ✅
├── skill-llm-context-builder ✅
└── skill-llm-config-manager ✅

skill-llm-assistant-ui (ABS)
├── skill-llm-conversation ✅
├── skill-llm-context-builder ✅
└── skill-local-knowledge ✅ (可选)

skill-knowledge-qa (ABS)
├── skill-knowledge-base ✅
├── skill-rag ✅
│   └── skill-knowledge-base ✅
├── skill-llm-assistant ❌ (ID错误)
└── skill-indexing ❌ (不存在)

skill-knowledge-ui (ABS)
├── skill-knowledge-base ✅
├── skill-rag ✅
└── skill-document-processor ✅ (可选)

skill-nexus-system-status-nexus-ui (ABS)
├── skill-monitor ✅
└── skill-health ✅ (可选)
```

---

**报告生成**: Skills Team  
**最后更新**: 2026-03-06

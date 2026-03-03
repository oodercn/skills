# Ooder Skills 零配置安装完整指南

## 文档信息

| 属性 | 值 |
|------|-----|
| **文档版本** | 2.3 |
| **编写日期** | 2026-03-02 |
| **适用版本** | SDK v2.3 |

---

## 一、概述

### 1.1 零配置安装目标

实现**一键部署**，用户只需选择场景模板即可完成：
- Skill 自动安装（含依赖解析）
- 场景自动创建
- 能力自动绑定
- UI 菜单自动注册

### 1.2 核心流程

```
用户选择场景模板
        ↓
┌─────────────────────────────────────────────────────────────────┐
│  TemplateService.deployTemplate(templateId)                      │
├─────────────────────────────────────────────────────────────────┤
│  1. 解析模板定义                                                 │
│  2. 构建依赖图 + 拓扑排序                                        │
│  3. 按序安装 Skills                                             │
│  4. 创建场景 + 绑定能力                                          │
│  5. 注册 UI 菜单                                                 │
└─────────────────────────────────────────────────────────────────┘
        ↓
返回部署结果
```

---

## 二、Skill 依赖层级

### 2.1 层级定义

```
Level 0: 基础服务（无依赖，可独立运行）
├── skill-health          # 健康检查
├── skill-monitor         # 监控服务
├── skill-security        # 安全服务
├── skill-vfs-local       # 本地存储
└── skill-local-knowledge # 本地知识

Level 1: 核心服务（依赖 Level 0）
├── skill-knowledge-base  # 知识库核心
├── skill-rag             # RAG检索
└── skill-llm-context-builder # 上下文构建

Level 2: 业务服务（依赖 Level 0-1）
├── skill-llm-conversation # LLM对话
└── skill-llm-openai      # OpenAI适配

Level 3: UI组件（依赖 Level 0-2）
├── skill-knowledge-ui    # 知识库界面
├── skill-llm-assistant-ui # LLM助手
└── skill-llm-management-ui # LLM管理
```

### 2.2 安装顺序规则

1. **先底层后上层**：Level 0 → Level 1 → Level 2 → Level 3
2. **依赖优先**：被依赖的 Skill 先安装
3. **可选后置**：可选依赖在必选依赖之后安装

---

## 三、场景模板列表

### 3.1 知识问答场景 (knowledge-qa)

| 属性 | 值 |
|------|-----|
| **模板ID** | knowledge-qa |
| **名称** | 知识问答场景 |
| **分类** | knowledge |
| **图标** | ri-book-3-line |

**包含 Skills**：

| Skill | 必选 | 说明 |
|-------|------|------|
| skill-knowledge-base | ✅ | 知识库核心服务 |
| skill-knowledge-ui | ✅ | 知识库管理界面 |
| skill-rag | ❌ | RAG检索增强 |
| skill-llm-assistant-ui | ❌ | LLM智能助手 |

**安装顺序**：
```
skill-knowledge-base → skill-rag → skill-knowledge-ui → skill-llm-assistant-ui
```

**预估资源**：
- CPU: 500m
- 内存: 512Mi
- 存储: 1Gi

---

### 3.2 LLM工作空间 (llm-workspace)

| 属性 | 值 |
|------|-----|
| **模板ID** | llm-workspace |
| **名称** | LLM工作空间 |
| **分类** | llm |
| **图标** | ri-robot-line |

**包含 Skills**：

| Skill | 必选 | 说明 |
|-------|------|------|
| skill-local-knowledge | ✅ | 本地知识服务 |
| skill-llm-context-builder | ✅ | 上下文构建 |
| skill-llm-conversation | ✅ | LLM对话服务 |
| skill-llm-assistant-ui | ✅ | LLM助手界面 |
| skill-llm-management-ui | ❌ | LLM管理界面 |
| skill-llm-openai | ❌ | OpenAI适配器 |
| skill-llm-deepseek | ❌ | DeepSeek适配器 |

**安装顺序**：
```
skill-local-knowledge → skill-llm-context-builder → skill-llm-openai/deepseek 
→ skill-llm-conversation → skill-llm-assistant-ui → skill-llm-management-ui
```

**预估资源**：
- CPU: 1000m
- 内存: 1Gi
- 存储: 500Mi

---

### 3.3 系统监控场景 (system-monitor)

| 属性 | 值 |
|------|-----|
| **模板ID** | system-monitor |
| **名称** | 系统监控场景 |
| **分类** | monitoring |
| **图标** | ri-pulse-line |

**包含 Skills**：

| Skill | 必选 | 说明 |
|-------|------|------|
| skill-health | ✅ | 健康检查服务 |
| skill-monitor | ✅ | 监控服务 |
| skill-security | ❌ | 安全服务 |
| skill-nexus-dashboard-nexus-ui | ✅ | 系统仪表盘 |
| skill-nexus-health-check-nexus-ui | ✅ | 健康检查界面 |
| skill-nexus-system-status-nexus-ui | ✅ | 系统状态界面 |

**安装顺序**：
```
skill-health → skill-monitor → skill-security 
→ skill-nexus-dashboard-nexus-ui → skill-nexus-health-check-nexus-ui 
→ skill-nexus-system-status-nexus-ui
```

**预估资源**：
- CPU: 200m
- 内存: 256Mi
- 存储: 100Mi

---

### 3.4 存储管理场景 (storage-management)

| 属性 | 值 |
|------|-----|
| **模板ID** | storage-management |
| **名称** | 存储管理场景 |
| **分类** | storage |
| **图标** | ri-folder-line |

**包含 Skills**：

| Skill | 必选 | 说明 |
|-------|------|------|
| skill-vfs-local | ✅ | 本地文件存储 |
| skill-vfs-database | ❌ | 数据库存储 |
| skill-vfs-minio | ❌ | MinIO对象存储 |
| skill-vfs-oss | ❌ | 阿里云OSS |
| skill-vfs-s3 | ❌ | AWS S3 |
| skill-storage-management-nexus-ui | ✅ | 存储管理界面 |

**安装顺序**：
```
skill-vfs-local → skill-vfs-database/minio/oss/s3 → skill-storage-management-nexus-ui
```

**预估资源**：
- CPU: 200m
- 内存: 256Mi
- 存储: 10Gi

---

### 3.5 组织集成场景 (org-integration)

| 属性 | 值 |
|------|-----|
| **模板ID** | org-integration |
| **名称** | 组织集成场景 |
| **分类** | org |
| **图标** | ri-team-line |

**包含 Skills**：

| Skill | 必选 | 说明 |
|-------|------|------|
| skill-org-base | ✅ | 组织架构基础 |
| skill-user-auth | ✅ | 用户认证服务 |
| skill-org-dingding | ❌ | 钉钉集成 |
| skill-org-feishu | ❌ | 飞书集成 |
| skill-org-wecom | ❌ | 企业微信集成 |
| skill-org-ldap | ❌ | LDAP集成 |

**安装顺序**：
```
skill-org-base → skill-user-auth → skill-org-dingding/feishu/wecom/ldap
```

**预估资源**：
- CPU: 200m
- 内存: 256Mi
- 存储: 100Mi

---

## 四、API 接口

### 4.1 模板管理接口

```http
# 获取模板列表
GET /api/v1/templates

# 获取模板详情
GET /api/v1/templates/{templateId}

# 一键部署模板
POST /api/v1/templates/{templateId}/deploy
```

### 4.2 部署请求示例

```bash
curl -X POST http://localhost:8084/api/v1/templates/knowledge-qa/deploy
```

### 4.3 部署响应示例

```json
{
  "code": 200,
  "data": {
    "sceneId": "knowledge-qa-a1b2c3d4",
    "templateId": "knowledge-qa",
    "status": "deployed",
    "skills": [
      {
        "skillId": "skill-knowledge-base",
        "status": "installed",
        "version": "1.0.0"
      },
      {
        "skillId": "skill-knowledge-ui",
        "status": "installed",
        "version": "1.0.0"
      }
    ],
    "capabilities": [
      "kb-management",
      "kb-search"
    ],
    "duration": 45000
  }
}
```

---

## 五、依赖声明规范

### 5.1 必选依赖

```yaml
dependencies:
  - id: skill-knowledge-base
    version: ">=1.0.0 <2.0.0"
    required: true
    description: "知识库核心服务"
    capabilities:
      - kb-management
      - search
```

### 5.2 可选依赖

```yaml
dependencies:
  - id: skill-rag
    version: ">=1.0.0"
    required: false
    description: "RAG检索增强（可选）"
```

### 5.3 版本约束语法

| 语法 | 说明 |
|------|------|
| `1.0.0` | 精确版本 |
| `>=1.0.0` | 最低版本 |
| `<2.0.0` | 最高版本 |
| `>=1.0.0 <2.0.0` | 版本范围 |
| `~1.0.0` | 兼容版本（1.0.x） |
| `^1.0.0` | 主版本兼容（1.x.x） |

---

## 六、文件清单

### 6.1 规范文档

| 文件 | 路径 | 说明 |
|------|------|------|
| SKILL_YAML_STANDARD.md | docs/v2.3/ | Skill配置规范 |
| SDK_ZERO_CONFIG_INSTALL_TASKS.md | docs/v2.3/ | 零配置安装任务 |
| SKILLS_REFACTOR_TECHNICAL_PROPOSAL.md | docs/v2.3/ | 重构技术方案 |
| ZERO_CONFIG_INSTALL_GUIDE.md | docs/v2.3/ | 零配置安装指南 |

### 6.2 场景模板

| 模板 | 路径 | 说明 |
|------|------|------|
| knowledge-qa.yaml | skills/skill-scene/src/main/resources/templates/ | 知识问答场景 |
| llm-workspace.yaml | skills/skill-scene/src/main/resources/templates/ | LLM工作空间 |
| system-monitor.yaml | skills/skill-scene/src/main/resources/templates/ | 系统监控场景 |
| storage-management.yaml | skills/skill-scene/src/main/resources/templates/ | 存储管理场景 |
| org-integration.yaml | skills/skill-scene/src/main/resources/templates/ | 组织集成场景 |

### 6.3 已更新 Skill 配置

| Skill | 配置文件 | 依赖层级 |
|-------|---------|---------|
| skill-knowledge-base | skill.yaml | Level 1 |
| skill-knowledge-ui | skill.yaml | Level 3 |
| skill-llm-assistant-ui | skill.yaml | Level 3 |
| skill-llm-management-ui | skill.yaml | Level 3 |
| skill-llm-conversation | skill.yaml | Level 2 |
| skill-rag | skill.yaml | Level 1 |
| skill-local-knowledge | skill.yaml | Level 0 |
| skill-health | skill-manifest.yaml | Level 0 |
| skill-monitor | skill-manifest.yaml | Level 0 |
| skill-security | skill-manifest.yaml | Level 0 |
| skill-vfs-local | skill-manifest.yaml | Level 0 |

---

## 七、验收清单

### 7.1 功能验收

- [ ] 场景模板列表可正常获取
- [ ] 一键部署可正常执行
- [ ] 依赖自动安装正常工作
- [ ] 安装失败时正确回滚
- [ ] UI菜单自动注册成功

### 7.2 配置验收

- [ ] 所有 Skill 配置符合规范
- [ ] 依赖声明完整（含版本约束）
- [ ] 能力声明完整（含分类）
- [ ] UI Skill 有 nexusUi 配置

### 7.3 文档验收

- [ ] 规范文档完整
- [ ] 场景模板文档完整
- [ ] API 文档完整

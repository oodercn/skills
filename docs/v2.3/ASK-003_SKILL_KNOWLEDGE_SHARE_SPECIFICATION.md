# TASK-003 skill-knowledge-share 知识共享管理 需求规格

> **版本**: v1.0  
> **分类**: ASS (自驱系统场景)  
> **创建日期**: 2026-03-07  
> **状态**: 需求细化中

---

## 一、用户故事

### 1.1 核心用户故事

**作为** 知识库管理员  
**我希望** 能够灵活设置知识库的共享权限  
**以便于** 实现部门内和跨部门的知识安全共享

### 1.2 用户故事分解

| 编号 | 用户故事 | 优先级 | 验收标准 |
|------|---------|--------|---------|
| US-001 | 设置知识库权限 | P0 | 支持READ/WRITE/ADMIN/OWNER四种权限级别 |
| US-002 | 创建分享链接 | P0 | 生成带有效期、密码、访问次数限制的分享链接 |
| US-003 | 访问分享链接 | P0 | 通过分享码和密码访问知识库内容 |
| US-004 | 撤销分享链接 | P0 | 管理员可随时撤销已创建的分享链接 |
| US-005 | 协作编辑模式 | P1 | 支持多人同时编辑同一文档 |
| US-006 | 版本控制 | P1 | 文档修改自动保存版本，支持回滚 |
| US-007 | 访问统计 | P2 | 统计分享链接的访问次数、访问者信息 |
| US-008 | 权限审计日志 | P2 | 记录所有权限变更操作 |

### 1.3 逻辑推理流程

```
知识库创建 → 默认私有权限 → 管理员设置权限
     ↓
添加用户/部门 → 选择权限级别 → 保存权限配置
     ↓
创建分享链接 → 设置有效期/密码/访问限制 → 生成分享码
     ↓
外部用户访问 → 输入分享码+密码 → 验证通过 → 授权访问
     ↓
协作模式 → 实时同步编辑 → 自动版本管理
```

---

## 二、三闭环检查设计

### 2.1 生命周期闭环

```
┌──────────┐    创建    ┌──────────┐    使用    ┌──────────┐
│   有效   │ ────────► │   活跃   │ ────────► │   过期   │
│  VALID   │           │  ACTIVE  │           │ EXPIRED  │
└──────────┘           └──────────┘           └──────────┘
      │                      │                      │
      │                      │                      │
      ▼                      ▼                      ▼
   可编辑               可访问/撤销            自动失效/可续期
```

**分享链接状态定义**:

| 状态 | 代码 | 说明 | 可执行操作 |
|------|------|------|-----------|
| 有效 | VALID | 已创建，未开始使用 | 编辑、删除 |
| 活跃 | ACTIVE | 正在被访问使用 | 查看、撤销 |
| 过期 | EXPIRED | 已超过有效期 | 续期、删除 |
| 已撤销 | REVOKED | 管理员主动撤销 | 删除 |

**生命周期API检查表**:

| 生命周期阶段 | API | 方法 | 路径 | 状态 |
|-------------|-----|------|------|------|
| 创建分享 | createShare | POST | /api/v1/knowledge-share/share | ✅ |
| 查询分享列表 | listShares | GET | /api/v1/knowledge-share/share | ❌ 待实现 |
| 查询分享详情 | getShare | GET | /api/v1/knowledge-share/share/{shareId} | ❌ 待实现 |
| 验证分享 | verifyShare | GET | /api/v1/knowledge-share/share/{shareCode}/verify | ❌ 待实现 |
| 撤销分享 | revokeShare | PUT | /api/v1/knowledge-share/share/{shareId}/revoke | ❌ 待实现 |
| 删除分享 | deleteShare | DELETE | /api/v1/knowledge-share/share/{shareId} | ❌ 待实现 |

### 2.2 数据实体关系闭环

```
┌─────────────────┐
│  KnowledgeBase  │
│    知识库表      │
├─────────────────┤
│ kbId (PK)       │
│ name            │
│ ownerId         │
│ visibility      │
└────────┬────────┘
         │
         │ 1:N
         │
         ▼
┌─────────────────┐      ┌─────────────────┐
│   Permission    │      │     Share       │
│    权限表        │      │   分享链接表     │
├─────────────────┤      ├─────────────────┤
│ permissionId(PK)│      │ shareId (PK)    │
│ kbId (FK)       │      │ kbId (FK)       │
│ userId          │      │ shareCode       │
│ permissionType  │      │ password        │
│ grantedBy       │      │ expiresAt       │
│ grantedAt       │      │ maxAccessCount  │
└─────────────────┘      │ currentCount    │
                         │ status          │
                         │ createdBy       │
                         └────────┬────────┘
                                  │
                                  │ 1:N
                                  │
                                  ▼
                         ┌─────────────────┐
                         │   AccessLog     │
                         │   访问日志表     │
                         ├─────────────────┤
                         │ logId (PK)      │
                         │ shareId (FK)    │
                         │ visitorId       │
                         │ visitorIp       │
                         │ accessTime      │
                         │ action          │
                         └─────────────────┘
```

**数据一致性检查项**:

| 检查项 | 要求 | 实现状态 |
|--------|------|---------|
| 级联创建 | 创建分享时关联知识库 | ❌ 待实现 |
| 级联删除 | 删除知识库时清理分享和权限 | ❌ 待实现 |
| 外键验证 | kbId 存在性验证 | ❌ 待实现 |
| 访问计数 | 每次访问更新 currentCount | ❌ 待实现 |

### 2.3 按钮事件和API闭环

**前端页面按钮闭环检查表**:

| 页面 | 按钮 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|------|---------|---------|---------|---------|
| 权限管理 | 添加权限 | `grantPermission()` | POST /permission | KnowledgeShareController.grantPermission() | ❌ |
| 权限管理 | 移除权限 | `revokePermission()` | DELETE /permission/{id} | KnowledgeShareController.revokePermission() | ❌ |
| 权限管理 | 检查权限 | `checkPermission()` | GET /permission/check | KnowledgeShareController.checkPermission() | ✅ |
| 分享管理 | 创建分享 | `createShare()` | POST /share | KnowledgeShareController.createShare() | ✅ |
| 分享管理 | 查看分享 | `listShares()` | GET /share | KnowledgeShareController.listShares() | ❌ |
| 分享管理 | 撤销分享 | `revokeShare()` | PUT /share/{id}/revoke | KnowledgeShareController.revokeShare() | ❌ |
| 分享访问 | 验证分享 | `verifyShare()` | GET /share/{code}/verify | KnowledgeShareController.verifyShare() | ❌ |
| 协作管理 | 开始协作 | `startCollaboration()` | POST /collaboration | KnowledgeShareController.startCollaboration() | ✅ |
| 协作管理 | 停止协作 | `stopCollaboration()` | PUT /collaboration/{id}/stop | KnowledgeShareController.stopCollaboration() | ❌ |

---

## 三、字典枚举定义

### 3.1 权限类型枚举

```java
@Dict(code = "permission_type", name = "权限类型", description = "知识库访问权限级别")
public enum PermissionType implements DictItem {
    
    READ("READ", "只读", "仅可查看知识库内容", "ri-eye-line", 1),
    WRITE("WRITE", "读写", "可查看和编辑知识库内容", "ri-edit-line", 2),
    ADMIN("ADMIN", "管理", "可管理权限和分享", "ri-admin-line", 3),
    OWNER("OWNER", "所有者", "知识库所有者，拥有全部权限", "ri-key-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    PermissionType(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}
```

### 3.2 分享状态枚举

```java
@Dict(code = "share_status", name = "分享状态", description = "分享链接的状态")
public enum ShareStatus implements DictItem {
    
    VALID("VALID", "有效", "分享链接已创建，未开始使用", "ri-checkbox-circle-line", 1),
    ACTIVE("ACTIVE", "活跃", "分享链接正在被访问", "ri-play-circle-line", 2),
    EXPIRED("EXPIRED", "已过期", "分享链接已超过有效期", "ri-time-line", 3),
    REVOKED("REVOKED", "已撤销", "分享链接已被管理员撤销", "ri-close-circle-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ShareStatus(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}
```

### 3.3 协作状态枚举

```java
@Dict(code = "collaboration_status", name = "协作状态", description = "知识库协作编辑状态")
public enum CollaborationStatus implements DictItem {
    
    IDLE("IDLE", "空闲", "无协作会话", "ri-stop-circle-line", 1),
    ACTIVE("ACTIVE", "协作中", "正在协作编辑", "ri-team-line", 2),
    PAUSED("PAUSED", "已暂停", "协作已暂停", "ri-pause-circle-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    CollaborationStatus(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}
```

---

## 四、API接口设计

### 4.1 现有API（已实现）

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 权限管理 | POST | /api/v1/knowledge-share/permission | 权限操作 |
| 检查权限 | GET | /api/v1/knowledge-share/permission/check | 检查用户权限 |
| 创建分享 | POST | /api/v1/knowledge-share/share | 创建分享链接 |
| 验证分享 | GET | /api/v1/knowledge-share/share/{shareCode} | 验证分享链接 |
| 协作管理 | POST | /api/v1/knowledge-share/collaboration | 协作操作 |

### 4.2 待实现API

#### 4.2.1 查询分享列表

```
GET /api/v1/knowledge-share/share

Query Parameters:
  - kbId: 知识库ID（可选）
  - status: 状态过滤（可选）
  - pageNum: 页码（默认1）
  - pageSize: 每页数量（默认20）

Response:
{
  "code": 200,
  "data": {
    "list": [
      {
        "shareId": "share-001",
        "shareCode": "ABC123",
        "kbId": "kb-001",
        "kbName": "产品文档库",
        "status": "ACTIVE",
        "expiresAt": "2026-03-14 10:30:00",
        "currentCount": 15,
        "maxAccessCount": 100,
        "createdAt": "2026-03-07 10:30:00"
      }
    ],
    "total": 50
  }
}
```

#### 4.2.2 撤销分享

```
PUT /api/v1/knowledge-share/share/{shareId}/revoke

Response:
{
  "code": 200,
  "message": "分享已撤销"
}
```

#### 4.2.3 删除分享

```
DELETE /api/v1/knowledge-share/share/{shareId}

Response:
{
  "code": 200,
  "message": "分享已删除"
}
```

#### 4.2.4 授权权限

```
POST /api/v1/knowledge-share/permission/grant

Request Body:
{
  "kbId": "kb-001",
  "userId": "user-002",
  "permissionType": "WRITE"
}

Response:
{
  "code": 200,
  "data": {
    "permissionId": "perm-001",
    "kbId": "kb-001",
    "userId": "user-002",
    "permissionType": "WRITE",
    "grantedAt": "2026-03-07 10:30:00"
  }
}
```

#### 4.2.5 移除权限

```
DELETE /api/v1/knowledge-share/permission/{permissionId}

Response:
{
  "code": 200,
  "message": "权限已移除"
}
```

#### 4.2.6 查询权限列表

```
GET /api/v1/knowledge-share/permission

Query Parameters:
  - kbId: 知识库ID

Response:
{
  "code": 200,
  "data": [
    {
      "permissionId": "perm-001",
      "kbId": "kb-001",
      "userId": "user-002",
      "userName": "张三",
      "permissionType": "WRITE",
      "grantedBy": "user-001",
      "grantedAt": "2026-03-07 10:30:00"
    }
  ]
}
```

#### 4.2.7 停止协作

```
PUT /api/v1/knowledge-share/collaboration/{kbId}/stop

Response:
{
  "code": 200,
  "message": "协作已停止"
}
```

#### 4.2.8 访问日志

```
GET /api/v1/knowledge-share/share/{shareId}/logs

Response:
{
  "code": 200,
  "data": [
    {
      "logId": "log-001",
      "shareId": "share-001",
      "visitorIp": "192.168.1.100",
      "accessTime": "2026-03-07 10:30:00",
      "action": "VIEW"
    }
  ]
}
```

---

## 五、UI/UE设计

### 5.1 页面结构

```
┌─────────────────────────────────────────────────────────────┐
│                        顶部导航栏                            │
├──────────────┬──────────────────────────────────────────────┤
│              │                                              │
│   侧边栏      │              主内容区                        │
│              │                                              │
│  - 权限管理   │   ┌────────────────────────────────────┐   │
│  - 分享管理   │   │                                    │   │
│  - 协作管理   │   │         权限/分享/协作列表          │   │
│  - 访问日志   │   │                                    │   │
│              │   └────────────────────────────────────┘   │
│              │                                              │
└──────────────┴──────────────────────────────────────────────┘
```

### 5.2 权限管理页面

```
┌─────────────────────────────────────────────────────────────┐
│  权限管理                                                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  知识库: [全部知识库 ▼]                                      │
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 用户          │ 权限      │ 授权人  │ 授权时间   │ 操作 ││
│  │───────────────│───────────│─────────│────────────│──────││
│  │ 张三          │ 🔑 所有者 │ -       │ 2026-03-01 │ -    ││
│  │ 李四          │ ✏️ 读写   │ 张三    │ 2026-03-03 │ [移除]││
│  │ 王五          │ 👁️ 只读   │ 张三    │ 2026-03-05 │ [移除]││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  [+ 添加权限]                                                │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.3 分享管理页面

```
┌─────────────────────────────────────────────────────────────┐
│  分享管理                                    [+ 创建分享]   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 分享码   │ 知识库     │ 状态   │ 有效期    │ 访问次数 │操作││
│  │──────────│────────────│────────│───────────│──────────│────││
│  │ ABC123   │ 产品文档库 │ 🟢活跃 │ 2026-03-14│ 15/100   │[撤销]││
│  │ DEF456   │ 技术文档   │ 🟡有效 │ 2026-03-20│ 0/50     │[编辑]││
│  │ GHI789   │ 培训资料   │ ⚫过期 │ 2026-03-05│ 45/50    │[删除]││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.4 创建分享弹窗

```
┌─────────────────────────────────────────────────────────────┐
│  创建分享链接                                          [×]  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  选择知识库: [产品文档库 ▼]                                  │
│                                                             │
│  有效期:     [7天 ▼]  [自定义: ____ 天]                     │
│                                                             │
│  访问密码:   [自动生成 ▼]  [自定义: ______]                 │
│                                                             │
│  最大访问次数: [100] 次                                      │
│                                                             │
│  ─────────────────────────────────────────────────────────  │
│                                                             │
│                    [取消]  [创建分享]                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 六、外部集成需求

### 6.1 依赖服务

| 服务 | 用途 | 必需性 | 当前状态 |
|------|------|--------|---------|
| skill-knowledge-base | 知识库核心服务 | 必需 | ✅ 已依赖 |
| skill-user-auth | 用户认证服务 | 必需 | ✅ 已依赖 |

### 6.2 可选集成

| 集成项 | 用途 | 优先级 |
|--------|------|--------|
| 通知服务 | 分享链接通知 | P1 |
| 审计服务 | 操作日志记录 | P2 |

---

## 七、开发任务清单

### 7.1 后端开发任务

| 任务 | 说明 | 优先级 | 状态 |
|------|------|--------|------|
| 添加字典枚举 | PermissionType, ShareStatus, CollaborationStatus | P0 | ❌ |
| 实现权限CRUD | 授权、移除、查询权限 | P0 | ❌ |
| 实现分享CRUD | 创建、查询、撤销、删除分享 | P0 | ❌ |
| 实现分享验证 | 验证分享码和密码 | P0 | ❌ |
| 实现访问日志 | 记录访问日志 | P1 | ❌ |
| 实现协作管理 | 开始/停止协作 | P1 | ❌ |

### 7.2 前端开发任务

| 任务 | 说明 | 优先级 | 状态 |
|------|------|--------|------|
| 创建UI技能包 | skill-knowledge-share-ui | P0 | ❌ |
| 权限管理页 | 权限列表、添加、移除 | P0 | ❌ |
| 分享管理页 | 分享列表、创建、撤销 | P0 | ❌ |
| 协作管理页 | 协作状态、参与者 | P1 | ❌ |

---

## 八、验收标准

### 8.1 功能验收

- [ ] 能够为用户设置知识库访问权限
- [ ] 能够创建带有效期和密码的分享链接
- [ ] 能够通过分享码访问知识库
- [ ] 能够撤销和删除分享链接
- [ ] 能够查看访问日志

### 8.2 三闭环验收

- [ ] 生命周期闭环：分享链接CRUD完整
- [ ] 数据实体闭环：Share与AccessLog关联正确
- [ ] 按钮API闭环：每个操作都有对应API调用

---

## 九、变更记录

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|---------|------|
| v1.0 | 2026-03-07 | 初始版本 | Ooder Team |

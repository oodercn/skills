# 待解决问题拆分与协作需求

## 一、问题分类

### 🔴 SE 协作需求（需要后端工程师处理）

| # | 问题 | 原因 | 需求描述 | 优先级 |
|---|------|------|---------|--------|
| Q1 | 统一角色定义 | 后端存在4套角色体系 | 统一角色定义，提供 `/api/v1/auth/roles` 返回完整角色列表 | P0 |
| Q3 | 权限拦截机制 | 后端缺少统一拦截 | 实现统一权限拦截器，返回 403 错误码 | P0 |
| Q4 | 安装API | 后端API不存在 | 提供 `/api/v1/install/*` 系列API | P1 |

### 🟡 可在 skill-common 中实现

| # | 问题 | 说明 | 实现位置 |
|---|------|------|---------|
| Q2 | 统一菜单API | 前端菜单配置 + 后端动态加载 | skill-common/controller/MenuController.java |
| Q5 | LLM配置API | LLM 驱动配置管理 | skill-common/controller/LlmConfigController.java |

---

## 二、SE 协作需求详情

### Q1: 统一角色定义

**现状问题**:
- `menu-config.json`: `personal`, `enterprise`, `admin`
- `menu-role-config.json`: `admin`, `user`, `developer`
- `AuthService`: `installer`, `admin`, `leader`, `collaborator`
- `RoleManagementService`: `admin`, `user`, `developer`

**需求**:
1. 统一角色定义为: `installer`, `admin`, `leader`, `collaborator`
2. 提供 API: `GET /api/v1/auth/roles` 返回:
```json
{
  "code": 200,
  "data": [
    {"id": "installer", "name": "系统安装者", "permissions": ["skill:install", "skill:view", "system:init"]},
    {"id": "admin", "name": "系统管理员", "permissions": ["capability:discover", "capability:install", ...]},
    {"id": "leader", "name": "主导者", "permissions": ["scene:activate", "scene:manage", ...]},
    {"id": "collaborator", "name": "协作者", "permissions": ["task:view", "task:execute", ...]}
  ]
}
```

---

### Q3: 权限拦截机制

**现状问题**:
- 没有 Spring Security 注解
- 没有拦截器/过滤器
- 权限检查依赖手动调用

**需求**:
1. 实现统一权限拦截器 `PermissionInterceptor`
2. 支持 `@RequirePermission` 注解
3. 返回标准错误格式:
```json
{
  "code": 403,
  "status": "error",
  "message": "权限不足: 缺少 capability:install 权限"
}
```

---

### Q4: 安装API

**需求**:
1. `GET /api/v1/install/status` - 获取安装状态
2. `POST /api/v1/install/start` - 开始安装
3. `GET /api/v1/install/progress` - 获取安装进度
4. `POST /api/v1/install/complete` - 完成安装

---

## 三、skill-common 实现计划

### Q2: 统一菜单API

**实现位置**: `skill-common/controller/MenuController.java`

**API 设计**:
- `GET /api/v1/menu` - 获取当前用户菜单
- `GET /api/v1/menu/config` - 获取菜单配置

---

### Q5: LLM配置API

**实现位置**: `skill-common/controller/LlmConfigController.java`

**API 设计**:
- `GET /api/v1/llm/providers` - 获取 LLM 提供商列表
- `GET /api/v1/llm/models` - 获取可用模型列表
- `POST /api/v1/llm/config` - 保存 LLM 配置
- `GET /api/v1/llm/config` - 获取当前配置

---

## 四、时间计划

| 任务 | 负责人 | 预计完成 |
|------|--------|---------|
| Q1 统一角色定义 | SE | 2天 |
| Q2 统一菜单API | Skills Team | 1天 |
| Q3 权限拦截机制 | SE | 2天 |
| Q4 安装API | SE | 3天 |
| Q5 LLM配置API | Skills Team | 1天 |

---

**文档版本**: v1.0
**创建日期**: 2026-03-13
**作者**: Skills Team

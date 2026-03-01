# 场景页面闭环分析报告 v1.0

> **文档版本**: v1.0  
> **发布日期**: 2026-03-01  
> **适用范围**: skill-scene 模块场景页面  
> **文档状态**: 正式发布

---

## 一、能力生命周期流程闭环分析

### 1.1 能力生命周期状态机

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力生命周期状态机                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│    ┌──────────┐    注册     ┌──────────┐    启用     ┌──────────┐          │
│    │   无     │ ─────────► │  DRAFT   │ ─────────► │ ENABLED  │          │
│    └──────────┘            └──────────┘            └──────────┘          │
│                                  │                      │                  │
│                                  │ 禁用                 │ 停用             │
│                                  ▼                      ▼                  │
│                            ┌──────────┐            ┌──────────┐          │
│                            │ DISABLED │ ◄──────────│ DISABLED │          │
│                            └──────────┘            └──────────┘          │
│                                  │                      │                  │
│                                  │ 删除                 │ 删除             │
│                                  ▼                      ▼                  │
│                            ┌──────────┐            ┌──────────┐          │
│                            │ DELETED  │ ◄──────────│ DELETED  │          │
│                            └──────────┘            └──────────┘          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 闭环检查结果

| 生命周期阶段 | 前端入口 | API接口 | 后端服务 | 状态 | 问题 |
|-------------|---------|---------|---------|------|------|
| **能力注册** | capability-discovery.html | POST /api/v1/capabilities | CapabilityService.register() | ✅ 完整 | - |
| **能力发现** | capability-discovery.html | GET /api/v1/capabilities/discovery | CapabilityDiscoveryController | ✅ 完整 | - |
| **能力查询** | my-capabilities.html | GET /api/v1/capabilities | CapabilityService.findAll() | ✅ 完整 | - |
| **能力更新** | my-capabilities.html | PUT /api/v1/capabilities/{id} | CapabilityService.update() | ⚠️ 缺失 | 前端未调用 |
| **能力状态变更** | my-capabilities.html | PATCH /api/v1/capabilities/{id}/status | CapabilityService.updateStatus() | ⚠️ 缺失 | API未实现 |
| **能力删除** | my-capabilities.html | DELETE /api/v1/capabilities/{id} | CapabilityService.unregister() | ⚠️ 缺失 | 前端未调用 |

### 1.3 能力绑定生命周期闭环

| 生命周期阶段 | 前端入口 | API接口 | 后端服务 | 状态 | 问题 |
|-------------|---------|---------|---------|------|------|
| **能力绑定** | scene-group-detail.html | POST /api/v1/scene-groups/{id}/capabilities | SceneGroupService.bindCapability() | ⚠️ 部分 | 前端仅本地操作 |
| **绑定查询** | scene-group-detail.html | GET /api/v1/scene-groups/{id}/capabilities | SceneGroupService.listCapabilityBindings() | ✅ 完整 | - |
| **绑定解绑** | scene-group-detail.html | DELETE /api/v1/scene-groups/{id}/capabilities/{bindingId} | SceneGroupService.unbindCapability() | ⚠️ 部分 | 前端仅本地操作 |
| **绑定状态变更** | - | PATCH /api/v1/scene-groups/{id}/capabilities/{bindingId}/status | - | ❌ 缺失 | 需要实现 |

---

## 二、能力数据实体关系闭环分析

### 2.1 核心实体关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        核心实体关系图                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌────────────────┐         使用          ┌────────────────┐             │
│   │ SceneTemplate  │ ───────────────────► │ SceneDefinition │             │
│   │ (场景模板)      │                       │ (场景定义)       │             │
│   └────────────────┘                       └────────────────┘             │
│          │                                          │                      │
│          │ 实例化                                    │ 定义                  │
│          ▼                                          ▼                      │
│   ┌────────────────┐         包含          ┌────────────────┐             │
│   │  SceneGroup    │ ───────────────────► │  Capability    │             │
│   │ (场景组)        │                       │ (能力定义)      │             │
│   └────────────────┘                       └────────────────┘             │
│          │                                          │                      │
│          │ 绑定                                     │ 实例化                │
│          ▼                                          ▼                      │
│   ┌────────────────┐         关联          ┌────────────────┐             │
│   │CapabilityBinding│ ──────────────────► │ CapabilityInst │             │
│   │ (能力绑定)       │                       │ (能力实例)      │             │
│   └────────────────┘                       └────────────────┘             │
│          │                                                                 │
│          │ 执行者                                                          │
│          ▼                                                                 │
│   ┌────────────────┐                                                      │
│   │  Participant   │                                                      │
│   │ (参与者)        │                                                      │
│   └────────────────┘                                                      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 实体关系闭环检查

| 实体关系 | 数据流向 | 前端展示 | API支持 | 问题 |
|---------|---------|---------|---------|------|
| **模板 → 场景组** | 模板实例化创建场景组 | ✅ template-detail.html | ✅ POST /scene-groups | - |
| **场景组 → 参与者** | 场景组包含多个参与者 | ✅ scene-group-detail.html | ✅ CRUD API | 前端仅本地操作 |
| **场景组 → 能力绑定** | 场景组绑定多个能力 | ✅ scene-group-detail.html | ✅ CRUD API | 前端仅本地操作 |
| **能力绑定 → 提供者** | 绑定关联能力提供者 | ⚠️ 显示不完整 | ✅ Provider API | 缺少提供者详情 |
| **参与者 → 角色** | 参与者拥有角色 | ✅ 显示角色 | ⚠️ 角色来自字典 | 角色权限未实现 |
| **场景组 → 工作流** | 场景组执行工作流 | ⚠️ 仅显示列表 | ❌ 无工作流API | 需要实现 |
| **场景组 → 快照** | 场景组创建快照 | ✅ scene-group-detail.html | ✅ Snapshot API | 前端仅本地操作 |

### 2.3 数据一致性检查

| 检查项 | 前端状态 | 后端状态 | 一致性 | 问题 |
|--------|---------|---------|--------|------|
| 参与者数量 | 本地计算 | 服务端计算 | ❌ 不一致 | 需同步 |
| 能力绑定数量 | 本地计算 | 服务端计算 | ❌ 不一致 | 需同步 |
| 场景组状态 | 本地切换 | 服务端切换 | ❌ 不一致 | 需调用API |
| 快照创建 | 本地创建 | 服务端创建 | ❌ 不一致 | 需调用API |

---

## 三、按钮事件和API闭环分析

### 3.1 场景组详情页面按钮闭环检查

| 按钮/操作 | 前端函数 | API调用 | 后端接口 | 闭环状态 | 问题 |
|----------|---------|---------|---------|---------|------|
| **加载场景组** | loadSceneGroup() | GET /api/v1/scene-groups/{id} | SceneGroupController.get() | ✅ 闭环 | - |
| **暂停/激活** | toggleStatus() | ❌ 未调用 | POST /scene-groups/{id}/activate | ❌ 断开 | 仅本地修改 |
| **邀请参与者** | inviteParticipant() | - | - | ⚠️ 半闭环 | 需调用join API |
| **保存参与者** | saveParticipant() | ❌ 未调用 | POST /scene-groups/{id}/participants | ❌ 断开 | 仅本地修改 |
| **移除参与者** | removeParticipant() | ❌ 未调用 | DELETE /scene-groups/{id}/participants/{pid} | ❌ 断开 | 仅本地修改 |
| **绑定能力** | bindCapability() | - | - | ⚠️ 半闭环 | 需调用bind API |
| **保存能力绑定** | saveCapabilityBinding() | ❌ 未调用 | POST /scene-groups/{id}/capabilities | ❌ 断开 | 仅本地修改 |
| **解绑能力** | unbindCapability() | ❌ 未调用 | DELETE /scene-groups/{id}/capabilities/{bid} | ❌ 断开 | 仅本地修改 |
| **创建快照** | createSnapshot() | ❌ 未调用 | POST /scene-groups/{id}/snapshots | ❌ 断开 | 仅本地修改 |
| **恢复快照** | restoreSnapshot() | ❌ 未调用 | POST /scene-groups/{id}/snapshots/{sid}/restore | ❌ 断开 | 仅本地修改 |
| **删除快照** | deleteSnapshot() | ❌ 未调用 | DELETE /scene-groups/{id}/snapshots/{sid} | ❌ 断开 | API缺失 |
| **启动工作流** | startWorkflow() | ❌ 未实现 | - | ❌ 断开 | API缺失 |

### 3.2 场景组管理页面按钮闭环检查

| 按钮/操作 | 前端函数 | API调用 | 后端接口 | 闭环状态 | 问题 |
|----------|---------|---------|---------|---------|------|
| **加载列表** | refreshSceneGroups() | GET /api/v1/scene-groups | SceneGroupController.listAll() | ✅ 闭环 | - |
| **创建场景组** | saveSceneGroup() | POST /api/v1/scene-groups | SceneGroupController.create() | ✅ 闭环 | - |
| **查看详情** | viewSceneGroup() | - | - | ✅ 闭环 | 页面跳转 |
| **切换状态** | toggleStatus() | POST /api/v1/scene-groups/{id}/activate | SceneGroupController.activate() | ✅ 闭环 | - |
| **删除场景组** | deleteSceneGroup() | DELETE /api/v1/scene-groups/{id} | SceneGroupController.destroy() | ✅ 闭环 | - |
| **模板筛选** | filterByTemplate() | GET /api/v1/scene-groups?templateId=xxx | SceneGroupController.listAll() | ✅ 闭环 | - |

### 3.3 闭环问题汇总

#### 高优先级问题（P0）

1. **场景组详情页数据未持久化**
   - 问题：参与者、能力绑定、快照等操作仅在前端本地修改
   - 影响：刷新页面后数据丢失
   - 解决：所有操作需调用后端API

2. **状态切换未调用API**
   - 问题：toggleStatus() 仅修改本地状态
   - 影响：状态变更不生效
   - 解决：调用 activate/deactivate API

#### 中优先级问题（P1）

3. **工作流功能缺失**
   - 问题：工作流相关API和前端功能未实现
   - 影响：无法执行场景工作流
   - 解决：需要设计和实现工作流模块

4. **快照删除API缺失**
   - 问题：后端无删除快照接口
   - 影响：无法删除快照
   - 解决：添加 DELETE /scene-groups/{id}/snapshots/{sid} 接口

---

## 四、"我的"系列页面功能设计

### 4.1 设计原则

遵循"以用户为中心"的展示原则：
- **我的场景**：侧重我创建的、我启动的场景
- **我的待办**：别人把我拉到协作里、领导委派给我的任务
- **已完成场景**：侧重查询历史数据

### 4.2 我的场景页面设计

#### 功能定位
展示当前用户创建的、启动的场景组，提供快速操作入口。

#### 页面结构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  我的场景                                                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 筛选: [全部状态 ▼] [全部类型 ▼]    搜索: [____________] [搜索]       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 我创建的场景 (3)                                                      │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ 📋 研发部日志汇报组    [运行中]    5人    创建于 2026-02-26       │ │   │
│  │ │ [查看] [暂停] [管理参与者] [绑定能力]                              │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ 📋 项目Alpha协作组     [已暂停]   8人    创建于 2026-02-24       │ │   │
│  │ │ [查看] [激活] [管理参与者] [绑定能力]                              │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 我启动的场景 (2)                                                      │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ 🚀 日志提交流程       [执行中]    开始于 2026-03-01 09:00        │ │   │
│  │ │ 进度: ████████░░ 80%    [查看详情] [取消]                          │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  [+ 创建新场景]                                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### API需求

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/v1/my/scenes | GET | 获取我的场景列表 |
| /api/v1/my/scenes/created | GET | 获取我创建的场景 |
| /api/v1/my/scenes/started | GET | 获取我启动的场景 |

### 4.3 我的待办页面设计

#### 功能定位
展示需要当前用户处理的任务，包括协作邀请、领导委派的任务。

#### 页面结构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  我的待办                                                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 待处理 (5)    已处理 (12)    全部                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 🔔 协作邀请                                                          │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ 张经理 邀请您加入 "研发部日志汇报组"                               │ │   │
│  │ │ 角色: 员工    时间: 2026-03-01 10:30                              │ │   │
│  │ │ [接受] [拒绝]                                                      │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 📋 领导委派                                                          │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ 任务: 完成项目周报汇总                                             │ │   │
│  │ │ 来源: 李总监    截止: 2026-03-03 18:00                            │ │   │
│  │ │ 场景: 项目周报汇报组    [开始处理] [查看详情]                       │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ ⏰ 待办提醒                                                          │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ 提交日志提醒    场景: 研发部日志汇报组    截止: 今天 17:00         │ │   │
│  │ │ [立即提交] [稍后提醒]                                              │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 待办类型定义

| 类型 | 代码 | 说明 | 来源 |
|------|------|------|------|
| 协作邀请 | INVITATION | 被邀请加入场景组 | 其他用户邀请 |
| 领导委派 | DELEGATION | 领导分配的任务 | 上级委派 |
| 待办提醒 | REMINDER | 场景触发的提醒 | 场景工作流 |
| 审批请求 | APPROVAL | 需要审批的事项 | 业务流程 |

#### API需求

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/v1/my/todos | GET | 获取我的待办列表 |
| /api/v1/my/todos/{id}/accept | POST | 接受待办 |
| /api/v1/my/todos/{id}/reject | POST | 拒绝待办 |
| /api/v1/my/todos/{id}/complete | POST | 完成待办 |

### 4.4 已完成场景页面设计

#### 功能定位
展示历史场景执行记录，支持查询和统计。

#### 页面结构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  已完成场景                                                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 时间范围: [最近7天 ▼]    类型: [全部 ▼]    搜索: [____________]      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 统计概览                                                              │   │
│  │ ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐        │   │
│  │ │ 完成场景    │ │ 参与次数    │ │ 成功率     │ │ 平均耗时    │        │   │
│  │ │    28      │ │    45      │ │   96.5%    │ │  12分钟     │        │   │
│  │ └────────────┘ └────────────┘ └────────────┘ └────────────┘        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 历史记录                                                              │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ 📋 研发部日志汇报 - 第12周    [成功]    2026-03-01 17:30         │ │   │
│  │ │ 参与者: 5人    耗时: 15分钟    [查看详情] [重新执行]               │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  │ ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │ │ 📋 项目周报汇总 - 第12周     [成功]    2026-02-26 18:00         │ │   │
│  │ │ 参与者: 8人    耗时: 22分钟    [查看详情] [重新执行]               │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  [导出报表]                                                                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### API需求

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/v1/my/history/scenes | GET | 获取已完成场景列表 |
| /api/v1/my/history/statistics | GET | 获取统计数据 |
| /api/v1/my/history/{id}/rerun | POST | 重新执行场景 |
| /api/v1/my/history/export | GET | 导出历史记录 |

---

## 五、修复建议

### 5.1 场景组详情页闭环修复

需要修改 `scene-group-detail.js` 中的以下函数：

```javascript
// 修复：状态切换需调用API
async function toggleStatus() {
    const newStatus = currentGroup.status === 'ACTIVE' ? 'deactivate' : 'activate';
    const action = currentGroup.status === 'ACTIVE' ? 'deactivate' : 'activate';
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/' + action, {
            method: 'POST'
        });
        const result = await response.json();
        
        if (result.code === 200) {
            currentGroup.status = action === 'activate' ? 'ACTIVE' : 'SUSPENDED';
            renderSceneGroup();
        } else {
            alert('操作失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to toggle status:', error);
        alert('操作失败');
    }
}

// 修复：保存参与者需调用API
async function saveParticipant() {
    if (!selectedParticipant) {
        alert('请选择参与者');
        return;
    }
    
    const participant = {
        participantId: selectedParticipant.id,
        participantType: document.getElementById('participantType').value,
        role: document.getElementById('participantRole').value,
        name: selectedParticipant.name
    };
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/participants', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(participant)
        });
        const result = await response.json();
        
        if (result.code === 200) {
            closeParticipantModal();
            loadSceneGroup(sceneGroupId);
        } else {
            alert('保存失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to save participant:', error);
        alert('保存失败');
    }
}
```

### 5.2 新增后端API

需要在 `SceneGroupController` 中添加以下接口：

```java
@DeleteMapping("/{sceneGroupId}/snapshots/{snapshotId}")
public ResultModel<Boolean> deleteSnapshot(
        @PathVariable String sceneGroupId,
        @PathVariable String snapshotId) {
    boolean result = sceneGroupService.deleteSnapshot(sceneGroupId, snapshotId);
    return ResultModel.success(result);
}

@GetMapping("/my/created")
public ResultModel<PageResult<SceneGroupDTO>> listMyCreated(
        @RequestParam(defaultValue = "1") int pageNum,
        @RequestParam(defaultValue = "10") int pageSize) {
    String currentUserId = getCurrentUserId();
    PageResult<SceneGroupDTO> result = sceneGroupService.listByCreator(currentUserId, pageNum, pageSize);
    return ResultModel.success(result);
}

@GetMapping("/my/participated")
public ResultModel<PageResult<SceneGroupDTO>> listMyParticipated(
        @RequestParam(defaultValue = "1") int pageNum,
        @RequestParam(defaultValue = "10") int pageSize) {
    String currentUserId = getCurrentUserId();
    PageResult<SceneGroupDTO> result = sceneGroupService.listByParticipant(currentUserId, pageNum, pageSize);
    return ResultModel.success(result);
}
```

---

## 六、菜单结构调整建议

### 6.1 新菜单结构

```json
{
    "menu": [
        {
            "id": "my-workspace",
            "name": "我的工作台",
            "icon": "ri-home-line",
            "roles": ["personal", "enterprise", "admin"],
            "children": [
                {
                    "id": "my-scenes",
                    "name": "我的场景",
                    "icon": "ri-artboard-line",
                    "url": "/console/pages/my-scenes.html",
                    "description": "我创建的、我启动的场景"
                },
                {
                    "id": "my-todos",
                    "name": "我的待办",
                    "icon": "ri-task-line",
                    "url": "/console/pages/my-todos.html",
                    "description": "协作邀请、领导委派的任务"
                },
                {
                    "id": "my-history",
                    "name": "已完成场景",
                    "icon": "ri-history-line",
                    "url": "/console/pages/my-history.html",
                    "description": "历史场景执行记录"
                }
            ]
        },
        {
            "id": "scene-management",
            "name": "场景管理",
            "icon": "ri-artboard-line",
            "roles": ["enterprise", "admin"],
            "children": [
                {
                    "id": "scene-group-list",
                    "name": "场景组列表",
                    "url": "/console/pages/scene-group-management.html"
                },
                {
                    "id": "template-list",
                    "name": "模板管理",
                    "url": "/console/pages/template-management.html"
                }
            ]
        },
        {
            "id": "capability-center",
            "name": "能力中心",
            "icon": "ri-flashlight-line",
            "roles": ["personal", "enterprise", "admin"],
            "children": [
                {
                    "id": "capability-discovery",
                    "name": "发现能力",
                    "url": "/console/pages/capability-discovery.html"
                },
                {
                    "id": "my-capabilities",
                    "name": "我的能力",
                    "url": "/console/pages/my-capabilities.html"
                }
            ]
        }
    ]
}
```

---

## 附录

### A. 相关文档

- [公共技术规范](COMMON_TECHNICAL_SPECIFICATION.md)
- [字典表规范与范例](DICT_SPECIFICATION.md)
- [场景需求规格说明书](SCENE_REQUIREMENT_SPEC.md)
- [能力管理需求规格说明书](CAPABILITY_REQUIREMENT_SPEC.md)

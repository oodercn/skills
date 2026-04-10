# BPM高级动态插件迁移计划

## 文档信息
- **版本**: 1.0
- **创建日期**: 2026-04-09
- **目标**: 将现有面板架构迁移到新的高级动态插件架构
- **预计周期**: 4-6周

---

## 目录
1. [现状分析](#1-现状分析)
2. [目标架构](#2-目标架构)
3. [迁移阶段](#3-迁移阶段)
4. [详细任务清单](#4-详细任务清单)
5. [风险与应对](#5-风险与应对)
6. [验证方案](#6-验证方案)

---

## 1. 现状分析

### 1.1 现有架构组件

| 组件类型 | 文件路径 | 状态 | 迁移优先级 |
|---------|---------|------|-----------|
| **旧面板管理器** | `panel/PanelManager.js` | 运行中 | P0 - 需替换 |
| **旧面板基类** | `panel/BasicPanel.js` | 运行中 | P0 - 需替换 |
| **旧时限面板** | `panel/TimingPanel.js` | 运行中 | P1 - 需迁移 |
| **旧路由面板** | `panel/RoutePanel.js` | 运行中 | P1 - 需迁移 |
| **旧Agent面板** | `panel/AgentPanel.js` | 运行中 | P2 - 需迁移 |
| **旧场景面板** | `panel/ScenePanel.js` | 运行中 | P2 - 需迁移 |
| **PanelSchema** | `panel/schemas/*.js` | 运行中 | P0 - 需整合 |

### 1.2 新架构组件（已创建）

| 组件 | 文件路径 | 状态 | 说明 |
|-----|---------|------|------|
| **PluginEnvironment** | `panel/PluginEnvironment.js` | 已创建 | 独立插件环境 |
| **PluginDataSource** | `panel/core/PluginDataSource.js` | 已创建 | 统一数据源 |
| **PluginDataAdapter** | `panel/core/PluginDataAdapter.js` | 已创建 | 数据适配器 |
| **AbstractPluginPanel** | `panel/core/AbstractPluginPanel.js` | 已创建 | 抽象面板基类 |
| **PanelPluginManager** | `panel/core/PanelPluginManager.js` | 已创建 | 插件管理器 |
| **TableListPanel** | `panel/components/TableListPanel.js` | 已创建 | 表格列表面板 |
| **ExternalDictionaryPanel** | `panel/components/ExternalDictionaryPanel.js` | 已创建 | 外部字典面板 |

### 1.3 架构差异对比

| 特性 | 旧架构 | 新架构 |
|-----|-------|-------|
| **数据获取** | 直接内嵌在面板中 | PluginDataSource统一获取 |
| **数据存储** | 直接操作store | 通过Adapter序列化/反序列化 |
| **UI与数据** | 紧耦合 | 完全分离 |
| **远程数据** | 无统一支持 | 内置缓存、拦截器 |
| **插件扩展** | 需修改核心代码 | 注册式插件 |
| **XML支持** | 无 | 完整支持 |

---

## 2. 目标架构

### 2.1 架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                      应用层 (Application)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │ 流程设计器   │  │ 活动设计器   │  │      路由设计器          │ │
│  └──────┬──────┘  └──────┬──────┘  └──────────┬──────────────┘ │
│         │                │                    │                │
│         └────────────────┼────────────────────┘                │
│                          ▼                                     │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │              PluginEnvironment (插件环境)                 │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │  │
│  │  │DataSource   │  │DataAdapter  │  │ PluginManager   │  │  │
│  │  │(远程数据)    │  │ (数据转换)   │  │  (插件管理)      │  │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────┘  │  │
│  └─────────────────────────────────────────────────────────┘  │
│                          │                                     │
│         ┌────────────────┼────────────────┐                   │
│         ▼                ▼                ▼                   │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │  Activity   │  │   Process   │  │    Route    │           │
│  │   Plugins   │  │   Plugins   │  │   Plugins   │           │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 数据流

```
用户操作 → AbstractPluginPanel → PluginDataAdapter → Backend API
                ↓
         PluginDataSource (缓存/远程)
                ↓
         数据变更事件 → Store更新
```

---

## 3. 迁移阶段

### 阶段一：基础架构迁移（第1-2周）

**目标**: 建立新架构基础，确保向后兼容

| 任务 | 优先级 | 负责人 | 验收标准 |
|-----|-------|-------|---------|
| 创建迁移适配器 | P0 | - | 旧面板可在新环境运行 |
| 整合PluginEnvironment | P0 | - | 环境初始化成功 |
| 迁移BasicPanel | P0 | - | 基本信息面板正常 |
| 双架构并行运行 | P0 | - | 新旧面板可同时使用 |

### 阶段二：核心面板迁移（第2-4周）

**目标**: 迁移所有核心面板

| 任务 | 优先级 | 依赖 | 验收标准 |
|-----|-------|------|---------|
| 迁移TimingPanel | P1 | 阶段一 | 时限配置正常 |
| 迁移RoutePanel | P1 | 阶段一 | 路由配置正常 |
| 迁移权限面板 | P1 | 阶段一 | 远程数据加载正常 |
| 迁移表单面板 | P1 | 阶段一 | 表单选择正常 |

### 阶段三：高级功能迁移（第4-5周）

**目标**: 迁移高级动态插件

| 任务 | 优先级 | 依赖 | 验收标准 |
|-----|-------|------|---------|
| 迁移监听器面板 | P2 | 阶段二 | XML序列化正常 |
| 迁移权限组面板 | P2 | 阶段二 | XML序列化正常 |
| 迁移表达式面板 | P2 | 阶段二 | 表达式验证正常 |
| 迁移组织机构面板 | P2 | 阶段二 | 懒加载正常 |

### 阶段四：清理与优化（第5-6周）

**目标**: 移除旧架构，优化性能

| 任务 | 优先级 | 依赖 | 验收标准 |
|-----|-------|------|---------|
| 移除旧PanelManager | P1 | 阶段三 | 无旧代码引用 |
| 移除旧PanelSchema | P1 | 阶段三 | 无旧代码引用 |
| 性能优化 | P2 | 阶段三 | 加载速度提升 |
| 文档更新 | P2 | 阶段三 | 文档完整 |

---

## 4. 详细任务清单

### 4.1 阶段一任务详情

#### 任务1.1: 创建迁移适配器

```javascript
// MigrationAdapter.js
class MigrationAdapter {
    // 将旧面板包装为新插件
    static wrapOldPanel(OldPanelClass, options) {
        return {
            id: options.id,
            name: options.name,
            type: options.type,
            
            render(container, context) {
                // 创建旧面板实例
                const oldPanel = new OldPanelClass(container);
                
                // 适配数据格式
                const adaptedData = this._adaptData(context.model);
                
                // 调用旧渲染
                oldPanel.render(adaptedData);
                
                // 保存引用用于后续操作
                container._oldPanel = oldPanel;
            },
            
            onBind(context) {
                // 数据绑定回调
            }
        };
    }
}
```

**验收标准**:
- [ ] 旧BasicPanel可在新环境渲染
- [ ] 数据变更可同步到store
- [ ] 无控制台错误

#### 任务1.2: 迁移BasicPanel

**迁移步骤**:
1. 创建新的BasicPanel继承AbstractPluginPanel
2. 将渲染逻辑移至`_renderContent()`
3. 将事件绑定移至`_bindEvents()`
4. 使用`updateField()`更新数据

**代码对比**:

旧代码:
```javascript
class BasicPanel {
    render(activity) {
        this.container.innerHTML = `...`;
        this._bindEvents(activity);
    }
    
    _updateActivity(activity) {
        activity.name = this.container.querySelector('#fieldName').value;
        window.app.store.updateActivity(activity);
    }
}
```

新代码:
```javascript
class BasicPanel extends AbstractPluginPanel {
    _renderContent() {
        const activity = this.data;
        this.elements.body.innerHTML = `...`;
    }
    
    _bindEvents() {
        this.elements.body.querySelector('#fieldName')
            .addEventListener('change', (e) => {
                this.updateField('name', e.target.value);
            });
    }
}
```

### 4.2 阶段二任务详情

#### 任务2.1: 迁移TimingPanel

**需要支持的数据**:
- 时限类型 (deadlineType)
- 时限时长 (deadlineDuration)
- 提醒设置 (reminderEnabled, reminderInterval)

**存储格式**: JSON

#### 任务2.2: 迁移权限面板

**需要支持的数据**:
- 办理人 (performers)
- 办理部门 (departments)
- 表达式 (expression)

**存储格式**: MultiValue (id1:id2:id3)

**远程数据**:
- 人员列表
- 部门列表

### 4.3 阶段三任务详情

#### 任务3.1: 迁移监听器面板

**需要支持的数据**:
- 监听器列表 (listeners)
- 监听器类型 (Expression/Script/Listener)
- 实现类 (realizeClass)
- 表达式内容 (expressionStr)

**存储格式**: XML

```xml
<itjds:Listeners>
    <itjds:Listener Id="..." Name="..." ... />
</itjds:Listeners>
```

**使用组件**: TableListPanel

#### 任务3.2: 迁移组织机构面板

**功能需求**:
- 树形展示组织架构
- 懒加载子节点
- 人员选择
- 多选支持

**使用组件**: ExternalDictionaryPanel

---

## 5. 风险与应对

### 5.1 风险清单

| 风险 | 可能性 | 影响 | 应对措施 |
|-----|-------|------|---------|
| 数据格式不兼容 | 中 | 高 | 提供数据迁移脚本 |
| 性能下降 | 低 | 中 | 提前进行性能测试 |
| 用户不习惯新界面 | 中 | 低 | 保持UI一致性 |
| 远程服务不可用 | 低 | 高 | 实现降级方案 |
| 第三方插件不兼容 | 中 | 中 | 提供兼容层 |

### 5.2 降级方案

```javascript
// 如果新架构初始化失败，回退到旧架构
if (!pluginEnvironment.initialized) {
    console.warn('New plugin system failed, falling back to legacy');
    window.panelManager = new PanelManager(container, store);
}
```

---

## 6. 验证方案

### 6.1 单元测试

```javascript
// PluginDataAdapter.test.js
describe('PluginDataAdapter', () => {
    test('should serialize listeners to XML', () => {
        const listeners = [{ id: '1', name: 'Test' }];
        const xml = adapter.serializeListenersToXML(listeners);
        expect(xml).toContain('<itjds:Listeners>');
        expect(xml).toContain('Id="1"');
    });
    
    test('should deserialize XML to listeners', () => {
        const xml = '<itjds:Listeners>...</itjds:Listeners>';
        const listeners = adapter.deserializeListenersFromXML(xml);
        expect(listeners).toHaveLength(1);
    });
});
```

### 6.2 集成测试

| 测试场景 | 测试步骤 | 预期结果 |
|---------|---------|---------|
| 流程创建 | 1. 创建流程 2. 配置监听器 3. 保存 | 数据正确存储为XML |
| 活动配置 | 1. 添加活动 2. 配置办理人 3. 保存 | 人员ID正确序列化 |
| 数据加载 | 1. 打开已有流程 2. 查看配置 | 数据正确反序列化 |
| 远程数据 | 1. 打开权限面板 2. 选择人员 | 人员列表正确加载 |

### 6.3 回归测试清单

- [ ] 创建新流程
- [ ] 添加活动节点
- [ ] 配置活动属性
- [ ] 配置流程属性
- [ ] 保存流程定义
- [ ] 加载流程定义
- [ ] 发布流程
- [ ] 流程实例运行

---

## 附录

### A. 文件映射表

| 旧文件 | 新文件 | 迁移状态 |
|-------|-------|---------|
| `PanelManager.js` | `PluginEnvironment.js` | 待迁移 |
| `BasicPanel.js` | `plugins/ActivityPanelPlugins.js` | 待迁移 |
| `TimingPanel.js` | `plugins/ActivityPanelPlugins.js` | 待迁移 |
| `RoutePanel.js` | `plugins/RoutePanelPlugins.js` | 待迁移 |
| `AgentPanel.js` | `plugins/ActivityPanelPlugins.js` | 待迁移 |
| `ScenePanel.js` | `plugins/ActivityPanelPlugins.js` | 待迁移 |

### B. 后端API清单

| API | 用途 | 状态 |
|-----|------|------|
| GET /api/bpm/org/list | 获取组织机构 | 需实现 |
| GET /api/bpm/person/list | 获取人员列表 | 需实现 |
| POST /api/bpm/formula/validate | 验证表达式 | 需实现 |
| GET /api/bpm/form/list | 获取表单列表 | 需实现 |
| GET /api/bpm/service/list | 获取服务列表 | 需实现 |

### C. 迁移检查清单

- [ ] 所有旧面板已迁移
- [ ] 所有数据格式测试通过
- [ ] 远程数据加载正常
- [ ] 性能测试通过
- [ ] 用户验收测试通过
- [ ] 文档更新完成

---

**文档结束**

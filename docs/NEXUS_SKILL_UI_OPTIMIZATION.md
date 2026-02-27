# Nexus 页面转 Skill UI 优化方案

> **目标**: 降低失真率，提供复杂页面兼容方案  
> **当前失真率**: 50.0%  
> **目标失真率**: < 20%  
> **最后更新**: 2026-02-25

---

## 1. 失真问题分析（具体例子）

### 1.1 问题类型A：功能缺失（最严重）

#### 例子1：网络拓扑页面 - 缺少拓扑组件

**Nexus 现有实现**:
```html
<!-- network-topology.html -->
<div id="topologyGraph" style="width: 100%; height: 400px;">
    <svg width="100%" height="100%" id="topologySvg">
        <!-- 手动绘制 SVG -->
    </svg>
</div>

<script>
// 手动实现 D3.js 风格的力导向图
function renderTopology(nodes, links) {
    const svg = document.getElementById('topologySvg');
    // 200+ 行手动 SVG 操作代码
    // 节点拖拽、连线、缩放全部手工实现
}
</script>
```

**规范期望**:
```json
{
  "type": "skill-topology",
  "name": "network-topology",
  "props": {
    "layout": "force",
    "nodeTypes": ["router", "switch", "endpoint"],
    "interactions": ["drag", "zoom", "select"]
  },
  "data": {
    "nodes": [...],
    "links": [...]
  }
}
```

**失真点**:
- ❌ 没有声明式配置
- ❌ 没有复用组件
- ❌ 200+ 行代码手动实现
- ❌ 无法通过 LLM 生成

---

#### 例子2：技能列表页面 - 数据表格功能缺失

**Nexus 现有实现**:
```javascript
// installed-skills.js - 第71行
container.innerHTML = skills.map(function(skill) {
    return '\
    <div class="skill-card ' + skill.status.toLowerCase() + '" data-id="' + skill.id + '">\
        <div class="skill-header">\
            <div class="skill-icon">\
                <i class="' + (skill.icon || 'ri-apps-line') + '"></i>\
            </div>\
            ... 更多字符串拼接
    </div>';
}).join('');
```

**规范期望**:
```json
{
  "type": "skill-grid",
  "name": "skill-list",
  "props": {
    "columns": [
      { "field": "skillName", "title": "名称" },
      { "field": "status", "title": "状态", "component": "status-badge" }
    ],
    "pagination": { "enabled": true, "pageSize": 20 }
  }
}
```

**失真点**:
- ❌ 命令式 DOM 操作（字符串拼接）
- ❌ 缺少声明式列定义
- ❌ 缺少分页、排序配置
- ❌ 状态标签样式硬编码

---

### 1.2 问题类型B：实现方式差异

#### 例子3：事件绑定方式

**Nexus 现有实现**:
```html
<!-- 行内 onclick -->
<button class="nx-btn" onclick="refreshTopology()">
    <i class="ri-refresh-line"></i> 刷新
</button>

<button class="nx-btn" onclick="showRunModal('${skill.id}')">
    运行
</button>
```

**规范期望**:
```json
{
  "events": [
    {
      "name": "click",
      "action": "api",
      "target": "/api/topology/refresh",
      "params": { "skillId": "{{skill.id}}" }
    }
  ]
}
```

**失真点**:
- ❌ 行内 onclick（难以维护）
- ❌ 事件与逻辑耦合
- ❌ 无法声明式配置

---

#### 例子4：API 响应处理

**Nexus 现有实现**:
```javascript
// installed-skills.js - 第42行
var rs = await response.json();
if (rs.requestStatus === 200) {  // ❌ 使用 requestStatus
    skillsData = rs.data || [];
} else {
    InstalledSkills.showError(rs.message);
}
```

**规范期望**:
```javascript
// A2A 标准响应
const response = await fetch('/api/a2a/tasks/send', {...});
const result = await response.json();

if (result.status === 'success' && result.data) {  // ✅ 使用 status
    skillsData = result.data.skills || [];
}
```

**失真点**:
- ❌ 响应字段不统一（requestStatus vs status）
- ❌ 错误处理方式不一致
- ❌ 数据结构不标准

---

### 1.3 问题类型C：扩展过度

#### 例子5：自定义样式过多

**Nexus 现有实现**:
```html
<div class="skill-card" style="border-left: 4px solid var(--nx-primary);">
    <div class="skill-header" style="display: flex; align-items: center;">
        <!-- 大量内联样式 -->
    </div>
</div>
```

**规范期望**:
```html
<!-- 纯类名，无内联样式 -->
<div class="skill-card skill-card--bordered">
    <div class="skill-card__header">
        <!-- 样式由 CSS 变量控制 -->
    </div>
</div>
```

**失真点**:
- ❌ 内联样式难以主题化
- ❌ 无法通过 CSS 变量统一调整
- ❌ LLM 生成时容易出错

---

## 2. 降低失真率的方案

### 2.1 核心策略：三层适配架构

```
┌─────────────────────────────────────────────────────────────────┐
│                     降低失真率架构                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Layer 3: 规范层 (Standard)                              │   │
│  │  - 标准 Skill UI JSON 配置                               │   │
│  │  - A2A 协议兼容                                          │   │
│  │  - 声明式组件定义                                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ▲                                   │
│                              │ 转换                              │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Layer 2: 适配层 (Adapter)                               │   │
│  │  - Nexus 组件映射表                                      │   │
│  │  - API 响应转换器                                        │   │
│  │  - 事件绑定适配器                                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ▲                                   │
│                              │ 兼容                              │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Layer 1: 现有层 (Legacy)                                │   │
│  │  - Nexus 现有 HTML/JS/CSS                               │   │
│  │  - 自定义组件                                           │   │
│  │  - 现有 API 端点                                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 具体实施方案

#### 方案1：组件映射表（解决类型B差异）

```javascript
// nexus-component-mapper.js
const NexusComponentMapper = {
    // 映射 Nexus 类名到规范组件
    classNameToComponent: {
        'nx-card': 'skill-card',
        'nx-table': 'skill-grid',
        'nx-form': 'skill-form',
        'nx-modal': 'skill-dialog',
        'nx-tabs': 'skill-tabs',
        'nx-stat-card': 'skill-card--stat',
        'skill-card': 'skill-card',
        'skill-list': 'skill-list'
    },
    
    // 映射事件绑定方式
    eventMapping: {
        'onclick': { type: 'click', binding: 'declarative' },
        'onchange': { type: 'change', binding: 'declarative' },
        'onsubmit': { type: 'submit', binding: 'declarative' }
    },
    
    // 映射 API 响应格式
    responseMapping: {
        'requestStatus': 'status',
        'requestStatus === 200': "status === 'success'",
        'rs.data': 'result.data'
    },
    
    // 自动转换函数
    convert(html, js) {
        // 1. 替换类名
        let convertedHtml = html;
        for (const [nexusClass, specComponent] of Object.entries(this.classNameToComponent)) {
            convertedHtml = convertedHtml.replace(
                new RegExp(`class="[^"]*${nexusClass}[^"]*"`, 'g'),
                `data-component="${specComponent}"`
            );
        }
        
        // 2. 转换事件绑定
        let convertedJs = js;
        convertedJs = convertedJs.replace(
            /onclick="([^"]*)"/g,
            (match, fn) => `data-action="${this.extractAction(fn)}"`
        );
        
        return { html: convertedHtml, js: convertedJs };
    }
};
```

**效果**: 将类型B差异从 30% 降低到 5%

---

#### 方案2：渐进式增强（解决类型A缺失）

```javascript
// progressive-enhancement.js
class ProgressiveEnhancement {
    
    // 检测缺失功能并自动增强
    enhance(container) {
        // 1. 检测是否需要图表
        const chartContainers = container.querySelectorAll('[data-chart-type]');
        chartContainers.forEach(el => this.enhanceChart(el));
        
        // 2. 检测是否需要表格增强
        const tables = container.querySelectorAll('table');
        tables.forEach(el => this.enhanceTable(el));
        
        // 3. 检测是否需要拓扑图
        const topologyContainers = container.querySelectorAll('[data-topology]');
        topologyContainers.forEach(el => this.enhanceTopology(el));
    }
    
    enhanceChart(element) {
        const chartType = element.dataset.chartType;
        
        // 动态加载 ECharts
        if (!window.echarts) {
            this.loadScript('https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js')
                .then(() => this.initChart(element, chartType));
        } else {
            this.initChart(element, chartType);
        }
    }
    
    enhanceTable(element) {
        // 为普通 table 添加排序、筛选功能
        const wrapper = document.createElement('div');
        wrapper.className = 'skill-grid-wrapper';
        element.parentNode.insertBefore(wrapper, element);
        wrapper.appendChild(element);
        
        // 添加表头排序
        const headers = element.querySelectorAll('th');
        headers.forEach(th => {
            th.addEventListener('click', () => this.sortTable(element, th));
            th.style.cursor = 'pointer';
            th.innerHTML += ' <i class="ri-arrow-up-down-line"></i>';
        });
    }
    
    enhanceTopology(element) {
        // 为 SVG 容器添加 D3.js 力导向图
        if (!window.d3) {
            this.loadScript('https://d3js.org/d3.v7.min.js')
                .then(() => this.initTopology(element));
        } else {
            this.initTopology(element);
        }
    }
}
```

**效果**: 将类型A缺失从 16.7% 降低到 8%

---

#### 方案3：API 适配器（统一响应格式）

```javascript
// api-adapter.js
class ApiAdapter {
    
    // 包装现有 API 为 A2A 格式
    async adapt(endpoint, options = {}) {
        const response = await fetch(endpoint, options);
        const legacyData = await response.json();
        
        // 转换为 A2A 标准格式
        return this.toA2AFormat(legacyData);
    }
    
    toA2AFormat(legacyData) {
        // Nexus 格式: { requestStatus: 200, data: {...} }
        // A2A 格式: { status: 'success', data: {...} }
        
        const status = legacyData.requestStatus === 200 ? 'success' : 'error';
        const message = legacyData.message || (status === 'success' ? '操作成功' : '操作失败');
        
        return {
            status: status,
            message: message,
            data: legacyData.data || legacyData,
            code: legacyData.requestStatus,
            timestamp: Date.now()
        };
    }
    
    // 反向转换（新 API 兼容旧前端）
    toLegacyFormat(a2aData) {
        return {
            requestStatus: a2aData.status === 'success' ? 200 : 500,
            message: a2aData.message,
            data: a2aData.data
        };
    }
}

// 使用示例
const apiAdapter = new ApiAdapter();

// 旧代码可以继续使用
const legacyResult = await apiAdapter.adapt('/api/skillcenter/installed/list');
// 返回: { status: 'success', data: {...} }
```

**效果**: 统一 API 格式，降低类型B差异

---

### 2.3 预期效果

| 优化措施 | 目标失真类型 | 预计降低失真率 |
|----------|-------------|---------------|
| 组件映射表 | 类型B | 30% → 5% (-25%) |
| 渐进式增强 | 类型A | 16.7% → 8% (-8.7%) |
| API 适配器 | 类型B | 包含在上面的30%中 |
| **合计** | - | **50% → 16.3%** |

---

## 3. 复杂页面兼容方案

### 3.1 复杂页面识别标准

```yaml
complexity_rules:
  high_complexity:
    - 包含自定义可视化（拓扑图、流程图）
    - 包含实时数据流（WebSocket + 图表）
    - 包含复杂交互（拖拽、画布操作）
    - 包含多组件联动（>3个组件相互影响）
    - 代码量 > 500 行 JavaScript
    
  medium_complexity:
    - 包含图表展示（ECharts/D3）
    - 包含文件操作（上传/下载/预览）
    - 包含 IM 功能（聊天/消息）
    - 代码量 200-500 行 JavaScript
```

### 3.2 兼容方案：混合模式

```
┌─────────────────────────────────────────────────────────────────┐
│                    复杂页面兼容架构                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              Skill Container (规范层)                    │   │
│  │  ┌─────────────────────────────────────────────────┐   │   │
│  │  │           Legacy Page Wrapper                    │   │   │
│  │  │  ┌─────────────────────────────────────────┐   │   │   │
│  │  │  │         原有 HTML/JS/CSS                 │   │   │   │
│  │  │  │  (完全保留，不做修改)                     │   │   │   │
│  │  │  │                                          │   │   │   │
│  │  │  │  - 网络拓扑图 (D3.js/SVG)                │   │   │   │
│  │  │  │  - 实时监控图表 (ECharts)                │   │   │   │
│  │  │  │  - IM 聊天界面                           │   │   │   │
│  │  │  │  - 复杂表单联动                          │   │   │   │
│  │  │  └─────────────────────────────────────────┘   │   │   │
│  │  │                                                 │   │   │
│  │  │  + 新增: Skill Bridge API                      │   │   │
│  │  │    - 与 Skill 容器通信                         │   │   │
│  │  │    - 发送/接收消息                             │   │   │
│  │  │    - 生命周期回调                              │   │   │
│  │  └─────────────────────────────────────────────────┘   │   │
│  │                                                         │   │
│  │  + 新增: 规范接口层                                     │   │
│  │    - Skill Card 声明                                    │   │
│  │    - A2A Task 支持                                      │   │
│  │    - 事件上报                                           │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 3.3 具体实现

#### 复杂页面包装器

```javascript
// legacy-page-wrapper.js
class LegacyPageWrapper {
    constructor(skillId, config) {
        this.skillId = skillId;
        this.config = config;
        this.iframe = null;
        this.bridge = null;
    }
    
    mount(container) {
        // 1. 创建 iframe 加载原有页面
        this.iframe = document.createElement('iframe');
        this.iframe.src = this.config.legacyUrl;
        this.iframe.style.cssText = 'width:100%;height:100%;border:none;';
        
        // 2. 等待 iframe 加载完成
        this.iframe.onload = () => {
            this.initBridge();
            this.injectAdapter();
        };
        
        container.appendChild(this.iframe);
    }
    
    initBridge() {
        // 3. 初始化与父窗口的通信桥
        this.bridge = new MessageBridge({
            target: this.iframe.contentWindow,
            origin: window.location.origin
        });
        
        // 4. 监听 Skill 容器消息
        this.bridge.on('skill:action', (data) => {
            // 转发到 iframe 内部
            this.iframe.contentWindow.postMessage({
                type: 'skill:action',
                payload: data
            }, '*');
        });
        
        // 5. 监听 iframe 内部事件
        window.addEventListener('message', (e) => {
            if (e.source === this.iframe.contentWindow) {
                this.handleLegacyEvent(e.data);
            }
        });
    }
    
    injectAdapter() {
        // 6. 向 iframe 注入适配脚本
        const script = this.iframe.contentDocument.createElement('script');
        script.textContent = `
            // 在原有页面中注入 Skill API
            window.SkillAPI = {
                sendMessage: (data) => {
                    parent.postMessage({
                        type: 'skill:message',
                        skillId: '${this.skillId}',
                        data: data
                    }, '*');
                },
                
                // 包装原有 API 调用
                callApi: async (endpoint, options) => {
                    const response = await fetch(endpoint, options);
                    const legacyData = await response.json();
                    
                    // 转换为 A2A 格式
                    return {
                        status: legacyData.requestStatus === 200 ? 'success' : 'error',
                        data: legacyData.data,
                        message: legacyData.message
                    };
                }
            };
            
            // 拦截原有事件，上报到 Skill 容器
            document.addEventListener('click', (e) => {
                const action = e.target.closest('[data-skill-action]');
                if (action) {
                    window.SkillAPI.sendMessage({
                        type: 'action',
                        action: action.dataset.skillAction,
                        data: action.dataset
                    });
                }
            });
        `;
        
        this.iframe.contentDocument.head.appendChild(script);
    }
    
    handleLegacyEvent(data) {
        // 7. 处理 iframe 内部事件，转换为 A2A Task 更新
        switch (data.type) {
            case 'skill:message':
                this.emit('task:update', {
                    state: 'working',
                    output: {
                        parts: [{
                            type: 'data',
                            data: data.data
                        }]
                    }
                });
                break;
                
            case 'skill:complete':
                this.emit('task:update', {
                    state: 'completed',
                    output: data.result
                });
                break;
        }
    }
}
```

#### 使用示例

```yaml
# skill-network-topology/skill.yaml
metadata:
  id: skill-network-topology
  name: 网络拓扑
  
spec:
  type: nexus-ui
  
  ui:
    enabled: true
    
    # 标记为复杂页面，使用包装模式
    complexity: high
    
    # 使用 wrapper 模式
    mode: legacy-wrapper
    
    config:
      # 指向原有页面
      legacyUrl: /console/pages/nexus/network-topology.html
      
      # 声明暴露的能力
      capabilities:
        - id: view-topology
          name: 查看拓扑
          description: 查看网络拓扑图
          
        - id: find-path
          name: 路径计算
          description: 计算节点间最短路径
          
      # 事件映射
      events:
        - legacy: "refreshTopology()"
          skill: "topology:refresh"
          
        - legacy: "findPath()"
          skill: "topology:find-path"
          params:
            - source: "pathSource.value"
            - target: "pathTarget.value"
```

### 3.4 适用场景

| 页面类型 | 复杂度 | 推荐方案 | 工作量 |
|----------|--------|----------|--------|
| 网络拓扑 | 高 | Legacy Wrapper | 2-3天 |
| 实时监控 | 高 | Legacy Wrapper | 2-3天 |
| IM 聊天 | 高 | Legacy Wrapper | 3-5天 |
| 数据表格 | 中 | 渐进增强 | 1-2天 |
| 表单编辑 | 低 | 完全转换 | 0.5-1天 |
| 列表展示 | 低 | 完全转换 | 0.5-1天 |

---

## 4. 总结

### 4.1 失真率优化效果

| 优化前 | 优化后 | 改进 |
|--------|--------|------|
| **50.0%** | **16.3%** | **-33.7%** |

### 4.2 关键措施

1. **组件映射表**: 自动转换 Nexus 组件到规范组件
2. **渐进式增强**: 动态加载缺失功能（图表、拓扑）
3. **API 适配器**: 统一响应格式
4. **Legacy Wrapper**: 复杂页面零改动兼容

### 4.3 实施建议

**Phase 1 (2周)**: 实现基础适配层
- 组件映射表
- API 适配器
- 简单页面转换工具

**Phase 2 (4周)**: 渐进式增强
- 图表组件集成
- 拓扑组件集成
- 中复杂度页面转换

**Phase 3 (持续)**: 复杂页面包装
- 识别复杂页面
- 实现 Legacy Wrapper
- 逐步迁移或保持包装

### 4.4 预期收益

- **短期**: 50% 页面可在 6 周内完成转换
- **中期**: 80% 页面可在 3 个月内完成
- **长期**: 复杂页面通过 Wrapper 模式 100% 覆盖

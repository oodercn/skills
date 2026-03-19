# Nexus 页面转 Skill UI 三阶段重构方案

> **重构目标**: 将 Nexus 页面逐步转换为标准化 Skill UI  
> **核心思路**: 组件抽取 → 前后端分离 → Skill 集成  
> **最后更新**: 2026-02-25

---

## 架构总览

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        三阶段重构架构                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Phase 1: 组件抽取与封装                                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  输入: Nexus 页面 (HTML + JS + CSS)                                  │   │
│  │  处理:                                                               │   │
│  │    1. 识别页面中的 UI 组件 (卡片/表格/表单/图表等)                    │   │
│  │    2. 抽取组件模板 (template.html)                                   │   │
│  │    3. 抽取组件逻辑 (component.js)                                    │   │
│  │    4. 抽取组件样式 (component.css)                                   │   │
│  │    5. 定义组件数据接口 (schema.json)                                 │   │
│  │  输出: 标准化组件库 + 数据接口定义                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    ↓                                         │
│  Phase 2: 前后端分离重构                                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  输入: 标准化组件库 + 原有后端逻辑                                     │   │
│  │  处理:                                                               │   │
│  │    1. 设计 RESTful API (符合 A2A 规范)                               │   │
│  │    2. 实现数据服务层 (Service)                                       │   │
│  │    3. 重构前端为纯展示层 (只调 API)                                   │   │
│  │    4. 建立数据绑定机制 (Data Binding)                                │   │
│  │    5. 实现动作处理器 (Action Handler)                                │   │
│  │  输出: 独立前端 + 独立后端 API                                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    ↓                                         │
│  Phase 3: Skill 集成与安装                                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  输入: 分离后的前后端 + 组件库                                         │   │
│  │  处理:                                                               │   │
│  │    1. 创建 Skill 元数据 (skill.yaml)                                 │   │
│  │    2. 打包 Skill 资源 (ui/ + api/ + config/)                         │   │
│  │    3. 实现 Skill 生命周期管理 (安装/启动/停止/卸载)                    │   │
│  │    4. 集成到 Skill 市场 (发布/版本管理)                               │   │
│  │    5. 实现动态加载机制 (运行时加载)                                   │   │
│  │  输出: 可安装的 Skill 包 + 市场集成                                    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Phase 1: 组件抽取与封装

### 1.1 组件识别与分类

#### 识别规则

```javascript
// component-detector.js
class ComponentDetector {
    
    // 检测页面中的所有组件
    detect(html, js, css) {
        const components = [];
        
        // 1. 检测统计卡片
        const statCards = this.detectStatCards(html);
        components.push(...statCards);
        
        // 2. 检测数据表格
        const tables = this.detectTables(html, js);
        components.push(...tables);
        
        // 3. 检测表单
        const forms = this.detectForms(html);
        components.push(...forms);
        
        // 4. 检测图表容器
        const charts = this.detectCharts(html, js);
        components.push(...charts);
        
        // 5. 检测列表
        const lists = this.detectLists(html, js);
        components.push(...lists);
        
        return components;
    }
    
    detectStatCards(html) {
        // 匹配 nx-stat-card 或自定义统计卡片
        const patterns = [
            /class="[^"]*nx-stat-card[^"]*"/g,
            /class="[^"]*stat-card[^"]*"/g,
            /<div[^>]*class="[^"]*card[^"]*"[^>]*>[^]*?<div[^>]*class="[^"]*icon[^"]*"/g
        ];
        
        return this.extractComponents(html, patterns, 'stat-card');
    }
    
    detectTables(html, js) {
        // 检测表格组件
        const hasTable = html.includes('<table');
        const hasGridJs = js.includes('renderGrid') || js.includes('data-table');
        
        if (hasTable || hasGridJs) {
            return [{
                type: 'skill-grid',
                name: this.extractComponentName(html, 'table'),
                source: 'html-table'
            }];
        }
        return [];
    }
    
    detectForms(html) {
        // 检测表单
        const forms = html.match(/<form[^>]*>[^]*?<\/form>/g) || [];
        return forms.map((form, index) => ({
            type: 'skill-form',
            name: `form-${index}`,
            source: 'html-form',
            fields: this.extractFormFields(form)
        }));
    }
    
    detectCharts(html, js) {
        // 检测图表（ECharts/D3/Canvas）
        const patterns = [
            /echarts|chart|graph|canvas/i,
            /id="[^"]*(chart|graph|topology)[^"]*"/i
        ];
        
        if (patterns.some(p => p.test(html) || p.test(js))) {
            return [{
                type: 'skill-chart',
                name: this.extractComponentName(html, 'chart'),
                library: this.detectChartLibrary(js)
            }];
        }
        return [];
    }
}
```

#### 组件分类表

| Nexus 组件特征 | 检测规则 | 映射到 Skill 组件 |
|---------------|---------|------------------|
| `nx-stat-card` | class 包含 stat-card | `skill-card--stat` |
| `nx-card` | class 包含 card | `skill-card` |
| `<table>` | table 标签 | `skill-grid` |
| `<form>` | form 标签 | `skill-form` |
| `#chart` | id 包含 chart | `skill-chart` |
| `#topology` | id 包含 topology | `skill-topology` |
| `nx-tabs` | class 包含 tabs | `skill-tabs` |
| `nx-modal` | class 包含 modal | `skill-dialog` |
| 列表渲染 | JS 中有 map/join | `skill-list` |

### 1.2 组件抽取流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    组件抽取流程                                  │
└─────────────────────────────────────────────────────────────────┘

输入: Nexus 页面文件 (page.html + page.js + page.css)
  │
  ▼
[步骤1: 解析页面结构]
  │
  ├── 解析 HTML DOM 树
  ├── 识别组件边界
  └── 标记组件区域
  │
  ▼
[步骤2: 提取组件模板]
  │
  ├── 提取 HTML 片段
  ├── 替换动态内容为占位符 {{field}}
  └── 标准化类名 (nx-* → skill-*)
  │
  ▼
[步骤3: 提取组件逻辑]
  │
  ├── 分析与组件相关的 JS 函数
  ├── 提取数据获取逻辑
  ├── 提取事件处理逻辑
  └── 提取状态管理逻辑
  │
  ▼
[步骤4: 提取组件样式]
  │
  ├── 提取组件相关 CSS
  ├── 转换为 CSS 变量
  └── 生成组件级样式文件
  │
  ▼
[步骤5: 定义数据接口]
  │
  ├── 分析数据字段
  ├── 定义数据类型
  ├── 生成 JSON Schema
  └── 生成 TypeScript 接口
  │
  ▼
输出: 标准化组件包
  ├── ui/components/{name}/
  │   ├── template.html
  │   ├── component.js
  │   ├── component.css
  │   └── schema.json
  └── docs/component-api.md
```

### 1.3 组件封装示例

#### 抽取前（Nexus 原始代码）

```html
<!-- network-topology.html 片段 -->
<div class="nx-grid nx-grid-cols-4 nx-mb-6">
    <div class="nx-stat-card">
        <div class="nx-stat-card__icon nx-stat-card__icon--primary">
            <i class="ri-node-tree"></i>
        </div>
        <div class="nx-stat-card__content">
            <div class="nx-stat-card__label">节点总数</div>
            <div class="nx-stat-card__value" id="totalNodes">0</div>
        </div>
    </div>
    <!-- 更多统计卡片... -->
</div>
```

```javascript
// network-topology.js 片段
async function loadStatistics() {
    const response = await fetch('/api/network/statistics');
    const rs = await response.json();
    if (rs.requestStatus === 200) {
        document.getElementById('totalNodes').textContent = rs.data.totalNodes;
        document.getElementById('totalConnections').textContent = rs.data.totalConnections;
    }
}
```

#### 抽取后（标准化组件）

```html
<!-- ui/components/stat-card/template.html -->
<div class="skill-card skill-card--stat" data-component="stat-card">
    <div class="skill-card__icon skill-card__icon--{{iconType}}">
        <i class="{{icon}}"></i>
    </div>
    <div class="skill-card__content">
        <div class="skill-card__label">{{label}}</div>
        <div class="skill-card__value">{{value}}</div>
    </div>
</div>
```

```javascript
// ui/components/stat-card/component.js
class StatCardComponent extends SkillUIComponent {
    
    static get componentName() {
        return 'stat-card';
    }
    
    static get props() {
        return {
            icon: { type: 'string', required: true },
            iconType: { type: 'string', default: 'primary' },
            label: { type: 'string', required: true },
            value: { type: ['string', 'number'], required: true }
        };
    }
    
    render() {
        const template = this.getTemplate();
        return this.compile(template, this.props);
    }
}
```

```json
// ui/components/stat-card/schema.json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "StatCard",
  "type": "object",
  "properties": {
    "icon": {
      "type": "string",
      "description": "Remix Icon 类名"
    },
    "iconType": {
      "type": "string",
      "enum": ["primary", "success", "warning", "info", "danger"],
      "default": "primary"
    },
    "label": {
      "type": "string",
      "description": "标签文本"
    },
    "value": {
      "type": ["string", "number"],
      "description": "统计值"
    }
  },
  "required": ["icon", "label", "value"]
}
```

```css
/* ui/components/stat-card/component.css */
.skill-card--stat {
    display: flex;
    align-items: center;
    padding: var(--ns-spacing-md);
    background: var(--ns-card-bg);
    border: 1px solid var(--ns-border);
    border-radius: var(--ns-radius);
}

.skill-card__icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    margin-right: var(--ns-spacing-md);
}

.skill-card__icon--primary {
    background: var(--ns-primary-light);
    color: var(--ns-primary);
}

/* 更多样式... */
```

### 1.4 数据接口定义

```yaml
# ui/components/stat-card/data-interface.yaml
interface:
  name: StatCardData
  description: 统计卡片数据接口
  
  input:
    - name: dataSource
      type: string
      description: 数据来源 API
      example: "/api/network/statistics"
      
    - name: fieldMapping
      type: object
      description: 字段映射
      example:
        value: "totalNodes"
        label: "节点总数"
        icon: "ri-node-tree"
        iconType: "primary"
        
  output:
    schema:
      type: object
      properties:
        value:
          type: number
          description: 统计数值
        label:
          type: string
          description: 标签文本
        icon:
          type: string
          description: 图标类名
        iconType:
          type: string
          description: 图标类型
          
  actions:
    - name: refresh
      description: 刷新数据
      trigger: click
      handler: loadData
      
    - name: navigate
      description: 点击跳转
      trigger: click
      handler: navigateToDetail
```

---

## Phase 2: 前后端分离重构

### 2.1 架构重构

```
┌─────────────────────────────────────────────────────────────────┐
│                    前后端分离架构                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────┐      ┌─────────────────────────┐   │
│  │       前端层             │      │       后端层             │   │
│  │  (Pure Presentation)    │◄────►│    (API Service)        │   │
│  ├─────────────────────────┤      ├─────────────────────────┤   │
│  │                         │      │                         │   │
│  │  ┌─────────────────┐   │      │  ┌─────────────────┐    │   │
│  │  │  UI Components  │   │      │  │  Controller     │    │   │
│  │  │  (抽取的组件)    │   │      │  │  (REST API)     │    │   │
│  │  └────────┬────────┘   │      │  └────────┬────────┘    │   │
│  │           │            │      │           │             │   │
│  │  ┌────────▼────────┐   │      │  ┌────────▼────────┐    │   │
│  │  │  Data Binding   │   │      │  │  Service Layer  │    │   │
│  │  │  (数据绑定)      │◄──┼──────┼──►│  (业务逻辑)      │    │   │
│  │  └────────┬────────┘   │      │  └────────┬────────┘    │   │
│  │           │            │      │           │             │   │
│  │  ┌────────▼────────┐   │      │  ┌────────▼────────┐    │   │
│  │  │  Action Handler │   │      │  │  Repository     │    │   │
│  │  │  (动作处理)      │   │      │  │  (数据访问)      │    │   │
│  │  └─────────────────┘   │      │  └─────────────────┘    │   │
│  │                         │      │                         │   │
│  └─────────────────────────┘      └─────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 API 设计规范

#### RESTful API 设计

```yaml
# api/openapi.yaml
openapi: 3.0.0
info:
  title: Network Topology Skill API
  version: 1.0.0

paths:
  /api/skills/network-topology/statistics:
    get:
      summary: 获取网络统计信息
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/NetworkStatistics'
                
  /api/skills/network-topology/nodes:
    get:
      summary: 获取节点列表
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: pageSize
          in: query
          schema:
            type: integer
            default: 20
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/NodeList'
                
  /api/skills/network-topology/topology:
    get:
      summary: 获取拓扑数据
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TopologyData'
                
  /api/skills/network-topology/path:
    post:
      summary: 计算节点间路径
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                source:
                  type: string
                target:
                  type: string
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PathResult'

components:
  schemas:
    NetworkStatistics:
      type: object
      properties:
        totalNodes:
          type: integer
        totalConnections:
          type: integer
        activeNodes:
          type: integer
        networkStatus:
          type: string
          
    NodeList:
      type: object
      properties:
        list:
          type: array
          items:
            $ref: '#/components/schemas/Node'
        pagination:
          $ref: '#/components/schemas/Pagination'
          
    TopologyData:
      type: object
      properties:
        nodes:
          type: array
          items:
            $ref: '#/components/schemas/TopologyNode'
        links:
          type: array
          items:
            $ref: '#/components/schemas/TopologyLink'
```

### 2.3 前端重构

#### 数据绑定机制

```javascript
// frontend/data-binding.js
class DataBinding {
    constructor(component, config) {
        this.component = component;
        this.config = config;
        this.data = null;
    }
    
    async bind() {
        // 1. 获取数据
        this.data = await this.fetchData();
        
        // 2. 映射数据到组件属性
        const props = this.mapDataToProps(this.data);
        
        // 3. 更新组件
        this.component.updateProps(props);
        
        // 4. 设置自动刷新
        if (this.config.autoRefresh) {
            this.startAutoRefresh();
        }
    }
    
    async fetchData() {
        const response = await fetch(this.config.dataSource);
        const result = await response.json();
        
        // 统一转换为 A2A 格式
        return {
            status: result.status || 'success',
            data: result.data || result
        };
    }
    
    mapDataToProps(data) {
        const mapping = this.config.fieldMapping;
        const props = {};
        
        for (const [propName, fieldPath] of Object.entries(mapping)) {
            props[propName] = this.getNestedValue(data, fieldPath);
        }
        
        return props;
    }
}
```

#### 动作处理器

```javascript
// frontend/action-handler.js
class ActionHandler {
    constructor(config) {
        this.config = config;
        this.handlers = new Map();
        this.registerDefaultHandlers();
    }
    
    registerDefaultHandlers() {
        // 注册默认动作处理器
        this.register('api', this.handleApiAction.bind(this));
        this.register('navigate', this.handleNavigateAction.bind(this));
        this.register('emit', this.handleEmitAction.bind(this));
        this.register('modal', this.handleModalAction.bind(this));
    }
    
    async handle(action, context) {
        const handler = this.handlers.get(action.type);
        if (!handler) {
            throw new Error(`Unknown action type: ${action.type}`);
        }
        return handler(action, context);
    }
    
    async handleApiAction(action, context) {
        const { endpoint, method, params } = action;
        
        // 替换参数占位符
        const resolvedEndpoint = this.resolveParams(endpoint, context);
        const resolvedParams = this.resolveParams(params, context);
        
        const response = await fetch(resolvedEndpoint, {
            method: method || 'GET',
            headers: { 'Content-Type': 'application/json' },
            body: resolvedParams ? JSON.stringify(resolvedParams) : null
        });
        
        return response.json();
    }
    
    handleNavigateAction(action, context) {
        const { target, params } = action;
        const resolvedTarget = this.resolveParams(target, context);
        
        // 构建 URL 参数
        const url = new URL(resolvedTarget, window.location.origin);
        if (params) {
            Object.entries(params).forEach(([key, value]) => {
                url.searchParams.set(key, this.resolveParams(value, context));
            });
        }
        
        window.location.href = url.toString();
    }
}
```

### 2.4 重构示例

#### 重构前（混合代码）

```javascript
// 原有代码：数据和视图混合
async function loadStatistics() {
    const response = await fetch('/api/network/statistics');
    const rs = await response.json();
    if (rs.requestStatus === 200) {
        // 直接操作 DOM
        document.getElementById('totalNodes').textContent = rs.data.totalNodes;
        document.getElementById('totalConnections').textContent = rs.data.totalConnections;
        document.getElementById('activeNodes').textContent = rs.data.activeNodes;
        document.getElementById('networkStatus').textContent = rs.data.networkStatus;
    }
}
```

#### 重构后（前后端分离）

**后端 API**:

```java
// NetworkTopologyController.java
@RestController
@RequestMapping("/api/skills/network-topology")
public class NetworkTopologyController {
    
    @Autowired
    private NetworkTopologyService topologyService;
    
    @GetMapping("/statistics")
    public ResponseEntity<A2AResponse<NetworkStatistics>> getStatistics() {
        NetworkStatistics stats = topologyService.getStatistics();
        return ResponseEntity.ok(A2AResponse.success(stats));
    }
    
    @GetMapping("/topology")
    public ResponseEntity<A2AResponse<TopologyData>> getTopology() {
        TopologyData data = topologyService.getTopologyData();
        return ResponseEntity.ok(A2AResponse.success(data));
    }
}
```

**前端组件**:

```javascript
// StatCardGroup.jsx
function StatCardGroup({ dataSource, config }) {
    const [statistics, setStatistics] = useState(null);
    
    useEffect(() => {
        // 只负责获取数据
        fetch(dataSource)
            .then(res => res.json())
            .then(result => {
                if (result.status === 'success') {
                    setStatistics(result.data);
                }
            });
    }, [dataSource]);
    
    // 只负责渲染
    return (
        <div className="skill-grid skill-grid--cols-4">
            {config.cards.map(cardConfig => (
                <StatCard
                    key={cardConfig.field}
                    icon={cardConfig.icon}
                    label={cardConfig.label}
                    value={statistics?.[cardConfig.field]}
                    iconType={cardConfig.iconType}
                />
            ))}
        </div>
    );
}
```

---

## Phase 3: Skill 集成与安装

### 3.1 Skill 打包结构

```
skill-network-topology/
├── skill.yaml                    # Skill 元数据
├── README.md                     # 说明文档
├── CHANGELOG.md                  # 变更日志
├── ui/                           # 前端资源
│   ├── index.html               # 入口页面
│   ├── components/              # 组件库
│   │   ├── stat-card/
│   │   │   ├── template.html
│   │   │   ├── component.js
│   │   │   ├── component.css
│   │   │   └── schema.json
│   │   ├── topology-graph/
│   │   └── path-finder/
│   ├── pages/                   # 页面
│   │   └── topology-dashboard.html
│   ├── styles/                  # 全局样式
│   │   └── main.css
│   └── scripts/                 # 全局脚本
│       └── app.js
├── api/                          # 后端 API
│   ├── spec/
│   │   └── openapi.yaml
│   └── docs/
│       └── api-guide.md
├── config/                       # 配置文件
│   ├── menu.json                # 菜单配置
│   └── routes.json              # 路由配置
└── resources/                    # 静态资源
    ├── images/
    └── fonts/
```

### 3.2 Skill 元数据

```yaml
# skill.yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-network-topology
  name: 网络拓扑管理
  version: 1.0.0
  description: 提供网络拓扑可视化、路径计算、节点管理等功能
  type: nexus-ui
  author: Nexus Team
  
spec:
  type: nexus-ui
  
  # 依赖声明
  dependencies:
    nexusVersion: ">=2.3.0"
    skills: []
    libraries:
      - name: d3
        version: "^7.0.0"
      - name: echarts
        version: "^5.4.0"
  
  # UI 配置
  ui:
    enabled: true
    entry:
      page: index.html
      title: 网络拓扑
      icon: ri-node-tree
    
    menu:
      position: sidebar
      category: network
      order: 100
    
    # 使用抽取的组件
    components:
      - name: stat-card
        type: skill-card--stat
        dataSource: /api/skills/network-topology/statistics
        config:
          cards:
            - field: totalNodes
              label: 节点总数
              icon: ri-node-tree
              iconType: primary
            - field: totalConnections
              label: 连接总数
              icon: ri-link
              iconType: success
              
      - name: topology-graph
        type: skill-topology
        dataSource: /api/skills/network-topology/topology
        config:
          layout: force
          interactions: [drag, zoom, select]
          
      - name: path-finder
        type: skill-form
        config:
          fields:
            - name: source
              type: select
              label: 源节点
              source: /api/skills/network-topology/nodes
            - name: target
              type: select
              label: 目标节点
              source: /api/skills/network-topology/nodes
          actions:
            - name: submit
              label: 计算路径
              action: api
              endpoint: /api/skills/network-topology/path
  
  # API 配置
  api:
    basePath: /api/skills/network-topology
    spec: api/spec/openapi.yaml
    
  # 生命周期钩子
  lifecycle:
    hooks:
      preInstall:
        - checkDatabaseSchema
      postInstall:
        - initializeDefaultData
      preUninstall:
        - backupUserData
```

### 3.3 安装流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    Skill 安装流程                                │
└─────────────────────────────────────────────────────────────────┘

[1] 上传/下载 Skill 包
    │
    ▼
[2] 解析 skill.yaml
    │
    ▼
[3] 检查依赖
    ├── Nexus 版本兼容性
    ├── 依赖 Skill 是否已安装
    └── 依赖库版本
    │
    ▼
[4] 验证组件
    ├── 检查组件配置完整性
    ├── 验证数据接口定义
    └── 检查 API 规范
    │
    ▼
[5] 安装资源
    ├── 复制 UI 资源到 /ui/{skill-id}/
    ├── 注册 API 路由
    ├── 注册菜单项
    └── 初始化数据库（如有）
    │
    ▼
[6] 启动服务
    ├── 启动后端服务（如有）
    ├── 加载前端组件
    └── 执行 postInstall 钩子
    │
    ▼
[7] 完成安装
    ├── 更新 Skill 列表
    ├── 记录安装日志
    └── 通知用户
```

### 3.4 动态加载机制

```javascript
// skill-loader.js
class SkillLoader {
    
    async load(skillId) {
        // 1. 获取 Skill 元数据
        const metadata = await this.fetchSkillMetadata(skillId);
        
        // 2. 检查依赖
        await this.checkDependencies(metadata);
        
        // 3. 加载前端资源
        await this.loadUIResources(metadata);
        
        // 4. 注册组件
        this.registerComponents(metadata.ui.components);
        
        // 5. 注册菜单
        this.registerMenu(metadata.ui.menu);
        
        // 6. 注册 API 路由
        this.registerAPIRoutes(metadata.api);
        
        // 7. 初始化 Skill
        await this.initializeSkill(metadata);
    }
    
    async loadUIResources(metadata) {
        const basePath = `/ui/${metadata.id}`;
        
        // 加载 CSS
        const cssLink = document.createElement('link');
        cssLink.rel = 'stylesheet';
        cssLink.href = `${basePath}/styles/main.css`;
        document.head.appendChild(cssLink);
        
        // 加载 JS
        const script = document.createElement('script');
        script.src = `${basePath}/scripts/app.js`;
        script.type = 'module';
        document.head.appendChild(script);
        
        // 等待加载完成
        await Promise.all([
            this.waitForLoad(cssLink),
            this.waitForLoad(script)
        ]);
    }
    
    registerComponents(components) {
        components.forEach(config => {
            const component = ComponentFactory.create(config.type, config);
            SkillUIRegistry.register(config.name, component);
        });
    }
}
```

---

## 4. 实施路线图

### 4.1 阶段划分

| 阶段 | 时间 | 目标 | 产出 |
|------|------|------|------|
| **Phase 1** | 4周 | 组件抽取与封装 | 20个标准化组件 |
| **Phase 2** | 6周 | 前后端分离重构 | 30个页面重构完成 |
| **Phase 3** | 4周 | Skill 集成 | 30个可安装 Skill |

### 4.2 优先级排序

```
高优先级（先实施）:
├── 通用组件
│   ├── stat-card (统计卡片)
│   ├── skill-grid (数据表格)
│   ├── skill-form (表单)
│   └── skill-card (卡片)
├── 高频页面
│   ├── skill-market (技能市场)
│   ├── my-skills (我的技能)
│   ├── dashboard (仪表盘)
│   └── user-management (用户管理)

中优先级（后实施）:
├── 复杂组件
│   ├── skill-topology (拓扑图)
│   ├── skill-chart (图表)
│   └── skill-chat (聊天)
├── 专业页面
│   ├── network-topology (网络拓扑)
│   ├── monitor-dashboard (监控)
│   └── im-main (IM)
```

### 4.3 预期收益

| 指标 | 当前 | 目标 | 收益 |
|------|------|------|------|
| 代码复用率 | 20% | 80% | +60% |
| 页面开发效率 | 1x | 3x | +200% |
| 维护成本 | 高 | 低 | -70% |
| LLM 生成支持 | 无 | 完整 | 新能力 |
| 生态扩展性 | 差 | 强 | 质变 |

---

## 5. 总结

### 5.1 核心思路

1. **组件抽取**: 将 Nexus 页面中的 UI 元素抽取为标准化组件
2. **前后端分离**: 将数据和视图解耦，通过 API 通信
3. **Skill 集成**: 将分离后的前后端打包为可安装的 Skill

### 5.2 关键产出

- **组件库**: 20+ 标准化 UI 组件
- **API 规范**: RESTful + A2A 兼容
- **Skill 市场**: 可安装、可升级的 Skill 生态
- **开发工具**: 组件抽取工具、代码生成器

### 5.3 长期价值

- **技术层面**: 标准化、可复用、易维护
- **业务层面**: 快速开发、灵活扩展、生态开放
- **战略层面**: 构建 Skill 生态，支持第三方开发

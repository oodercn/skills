# Skill 生命周期管理界面设计文档

## 文档信息
- **版本**: 1.0
- **日期**: 2026-02-25
- **基于**: 需求规格书 v1.0

---

## 一、界面架构

### 1.1 页面结构

```
Skill Lifecycle Management
├── Dashboard (仪表盘)
│   ├── 概览卡片
│   ├── 实时状态图表
│   └── 最近事件流
│
├── Skill Management (Skill管理)
│   ├── Skill列表视图
│   ├── Skill详情视图
│   └── 配置对比视图
│
├── Runtime Monitor (运行时监控)
│   ├── 实时状态面板
│   ├── 健康检查图表
│   └── 资源使用监控
│
├── Capability Trace (能力追踪)
│   ├── 调用链路视图
│   ├── 性能分析图表
│   └── 错误分析面板
│
└── Event Center (事件中心)
    ├── 事件时间线
    ├── 事件过滤器
    └── 告警通知
```

### 1.2 路由设计

| 路由 | 页面 | 描述 |
|------|------|------|
| `/skills` | Skill列表页 | 展示所有Skill的概览信息 |
| `/skills/:id` | Skill详情页 | 单个Skill的详细信息 |
| `/skills/:id/config` | 配置页 | YAML配置查看和对比 |
| `/monitor` | 监控仪表盘 | 实时状态监控 |
| `/capabilities` | 能力管理页 | 能力列表和调用统计 |
| `/capabilities/:id/trace` | 调用链路页 | 单个能力的调用追踪 |
| `/events` | 事件中心 | 生命周期事件查看 |
| `/analysis` | 分析报告 | 闭环分析和性能报告 |

---

## 二、页面详细设计

### 2.1 Dashboard (仪表盘)

#### 布局
```
+----------------------------------------------------------+
|  [Header] Skill Lifecycle Management Dashboard          |
+----------------------------------------------------------+
|                                                          |
|  +----------------+  +----------------+  +------------+  |
|  | Total Skills   |  | Active Skills  |  | Warnings   |  |
|  |     11         |  |      8         |  |     2      |  |
|  +----------------+  +----------------+  +------------+  |
|                                                          |
|  +--------------------------------------------------+    |
|  |              Real-time Status Chart               |    |
|  |  [Line Chart: Active/Inactive/Error over time]    |    |
|  +--------------------------------------------------+    |
|                                                          |
|  +------------------------+  +----------------------+    |
|  |   Recent Events        |  |   Top Capabilities   |    |
|  |   - skill-llm STARTED  |  |   1. llm-chat (45)   |    |
|  |   - skill-org ERROR    |  |   2. heartbeat (32)  |    |
|  |   - ...                |  |   3. ...             |    |
|  +------------------------+  +----------------------+    |
|                                                          |
+----------------------------------------------------------+
```

#### 组件清单
1. **统计卡片组件** (`StatCard`)
   - 标题、数值、趋势指示器
   - 点击可下钻到详情页

2. **实时状态图表** (`StatusChart`)
   - 折线图展示Skill状态变化
   - 时间范围选择器（1小时/6小时/24小时）
   - 悬停显示详细数据

3. **事件流组件** (`EventStream`)
   - 滚动列表展示最新事件
   - 事件类型图标和颜色标识
   - 点击查看事件详情

4. **热门能力组件** (`TopCapabilities`)
   - 柱状图展示调用次数
   - 点击跳转到能力详情

---

### 2.2 Skill列表页

#### 布局
```
+----------------------------------------------------------+
|  [Breadcrumb] Skills                                    |
|  [Toolbar] [Filter ▼] [Search...] [View Toggle] [+ Add] |
+----------------------------------------------------------+
|                                                          |
|  +--------------------------------------------------+    |
|  |  [Table/Grid View]                                |    |
|  |                                                   |    |
|  |  Skill ID    | Name    | Type   | Status | Actions|    |
|  |  ------------|---------|--------|--------|--------|    |
|  |  skill-llm   | LLM服务 | APP    | ● Active| [View]|    |
|  |  skill-org   | 组织服务 | SYS    | ● Active| [View]|    |
|  |  ...         | ...     | ...    | ...    | ...    |    |
|  |                                                   |    |
|  +--------------------------------------------------+    |
|                                                          |
|  [Pagination] < 1 2 3 ... 10 >                          |
|                                                          |
+----------------------------------------------------------+
```

#### 功能特性
1. **多视图切换**
   - 表格视图：详细信息，适合管理
   - 卡片视图：视觉化展示，适合概览
   - 拓扑视图：Skill依赖关系图

2. **过滤和搜索**
   - 按类型过滤（系统服务/应用能力）
   - 按状态过滤（活跃/停止/错误）
   - 关键词搜索（ID/名称/描述）

3. **批量操作**
   - 批量启动/停止
   - 批量导出配置
   - 批量删除

4. **行内操作**
   - 查看详情
   - 编辑配置
   - 查看日志
   - 重启服务

---

### 2.3 Skill详情页

#### 布局
```
+----------------------------------------------------------+
|  [Breadcrumb] Skills > skill-llm                         |
|  [Header] LLM服务  [Status: Active] [Actions: Restart]   |
+----------------------------------------------------------+
|  [Tabs] [Overview] [Config] [Runtime] [Capabilities] [Logs]|
+----------------------------------------------------------+
|                                                          |
|  Tab: Overview                                           |
|  +------------------------+  +------------------------+  |
|  |  Basic Info            |  |  Health Status         |  |
|  |  - ID: skill-llm       |  |  - Status: Healthy     |  |
|  |  - Version: 2.3.1      |  |  - Uptime: 3d 2h       |  |
|  |  - Type: APP           |  |  - Last Check: 10s ago |  |
|  |  - Created: 2024-01-15 |  |  - Error Rate: 0.1%    |  |
|  +------------------------+  +------------------------+  |
|                                                          |
|  +--------------------------------------------------+    |
|  |              Lifecycle Timeline                   |    |
|  |  [Timeline: INSTALLED -> STARTED -> RUNNING]      |    |
|  +--------------------------------------------------+    |
|                                                          |
|  Tab: Config                                             |
|  +--------------------------------------------------+    |
|  |  [YAML Editor with Syntax Highlighting]           |    |
|  |  metadata:                                        |    |
|  |    skillId: skill-llm                             |    |
|  |    ...                                            |    |
|  +--------------------------------------------------+    |
|                                                          |
|  Tab: Runtime                                            |
|  +------------------------+  +------------------------+  |
|  |  Resource Usage        |  |  Performance Metrics   |  |
|  |  [CPU/Memory Charts]   |  |  [Latency/Throughput]  |  |
|  +------------------------+  +------------------------+  |
|                                                          |
|  Tab: Capabilities                                       |
|  +--------------------------------------------------+    |
|  |  Capability List with Invocation Stats            |    |
|  |  - llm-chat: 45 calls, 98% success                |    |
|  |  - nlp-convert: 21 calls, 95% success             |    |
|  +--------------------------------------------------+    |
|                                                          |
+----------------------------------------------------------+
```

#### 功能特性
1. **标签页导航**
   - Overview: 基本信息和生命周期时间线
   - Config: YAML配置查看和编辑
   - Runtime: 运行时指标和资源使用
   - Capabilities: 能力列表和调用统计
   - Logs: 实时日志流

2. **生命周期时间线**
   - 可视化展示状态转换
   - 点击节点查看详情
   - 支持缩放和时间范围选择

3. **配置编辑器**
   - YAML语法高亮
   - 实时验证
   - 版本对比

---

### 2.4 运行时监控页

#### 布局
```
+----------------------------------------------------------+
|  [Header] Runtime Monitor                               |
|  [Toolbar] [Time Range ▼] [Refresh] [Export]            |
+----------------------------------------------------------+
|                                                          |
|  +------------------------+  +------------------------+  |
|  |  Active Skills         |  |  System Health         |  |
|  |  [Gauge Chart]         |  |  [Health Score: 95]    |  |
|  |  8/11 Running          |  |  [Status Indicators]   |  |
|  +------------------------+  +------------------------+  |
|                                                          |
|  +--------------------------------------------------+    |
|  |              Resource Usage Overview              |    |
|  |  [Multi-line Chart: CPU/Memory/Network per Skill] |    |
|  +--------------------------------------------------+    |
|                                                          |
|  +--------------------------------------------------+    |
|  |              Skill Status Grid                    |    |
|  |  [Grid of status cards for each skill]            |    |
|  |  [Green/Red/Yellow indicators]                    |    |
|  +--------------------------------------------------+    |
|                                                          |
|  +--------------------------------------------------+    |
|  |              Alert Panel                          |    |
|  |  - [Warning] skill-org: High latency              |    |
|  |  - [Error] skill-vfs: Connection failed           |    |
|  +--------------------------------------------------+    |
|                                                          |
+----------------------------------------------------------+
```

#### 功能特性
1. **实时监控**
   - WebSocket推送状态更新
   - 自动刷新（5秒间隔）
   - 手动刷新按钮

2. **图表组件**
   - 折线图：资源使用趋势
   - 仪表盘：健康度评分
   - 热力图：Skill状态分布

3. **告警面板**
   - 按严重程度分类
   - 一键定位到问题Skill
   - 告警历史查看

---

### 2.5 能力追踪页

#### 布局
```
+----------------------------------------------------------+
|  [Header] Capability Trace                              |
|  [Filter] [Skill ▼] [Capability ▼] [Time Range ▼]       |
+----------------------------------------------------------+
|                                                          |
|  +--------------------------------------------------+    |
|  |              Invocation Timeline                  |    |
|  |  [Waterfall Chart showing call sequences]         |    |
|  |  Request -> Skill -> Capability -> Response       |    |
|  +--------------------------------------------------+    |
|                                                          |
|  +------------------------+  +------------------------+  |
|  |  Performance Stats     |  |  Error Analysis        |  |
|  |  - Total: 156          |  |  - Errors: 3           |  |
|  |  - Avg Latency: 245ms  |  |  - Error Rate: 1.9%    |  |
|  |  - P95: 520ms          |  |  - Top Error: Timeout  |  |
|  |  - P99: 890ms          |  |                        |  |
|  +------------------------+  +------------------------+  |
|                                                          |
|  +--------------------------------------------------+    |
|  |              Call Detail List                     |    |
|  |  [Table with expandable rows for trace details]   |    |
|  |  Time | Skill | Capability | Latency | Status      |    |
|  +--------------------------------------------------+    |
|                                                          |
+----------------------------------------------------------+
```

#### 功能特性
1. **调用链路追踪**
   - 瀑布图展示调用序列
   - 点击展开详细时间线
   - 错误调用高亮显示

2. **性能分析**
   - 延迟分布直方图
   - 百分位数统计（P50/P95/P99）
   - 趋势对比

3. **错误分析**
   - 错误类型分布
   - 错误趋势图
   - 关联日志查看

---

### 2.6 事件中心页

#### 布局
```
+----------------------------------------------------------+
|  [Header] Event Center                                  |
|  [Filter] [Type ▼] [Skill ▼] [Severity ▼] [Time ▼]      |
|  [Search] [...] [Export] [Clear All]                    |
+----------------------------------------------------------+
|                                                          |
|  +--------------------------------------------------+    |
|  |              Event Timeline                       |    |
|  |  [Vertical timeline with event nodes]             |    |
|  |                                                   |    |
|  |  ● 10:23:45  skill-llm    STARTED    [Green]     |    |
|  |  ● 10:24:12  skill-org    ERROR      [Red]       |    |
|  |  ● 10:25:01  skill-llm    HEALTH_CHECK [Blue]    |    |
|  |  ...                                             |    |
|  +--------------------------------------------------+    |
|                                                          |
|  +--------------------------------------------------+    |
|  |              Event Detail Panel                   |    |
|  |  [Shows details when timeline node is selected]   |    |
|  |  - Event ID: xxx                                  |    |
|  |  - Timestamp: 2024-01-20 10:23:45                |    |
|  |  - Skill: skill-llm                               |    |
|  |  - Type: STARTED                                  |    |
|  |  - Metadata: {...}                                |    |
|  +--------------------------------------------------+    |
|                                                          |
+----------------------------------------------------------+
```

#### 功能特性
1. **时间线视图**
   - 垂直时间线布局
   - 事件类型颜色编码
   - 支持缩放和滚动

2. **高级过滤**
   - 按事件类型过滤
   - 按Skill过滤
   - 按严重程度过滤
   - 时间范围选择

3. **事件详情**
   - 完整的事件元数据
   - 关联的Skill信息
   - 上下文日志

---

## 三、组件设计

### 3.1 通用组件

#### StatusBadge (状态徽章)
```typescript
interface StatusBadgeProps {
  status: 'active' | 'inactive' | 'error' | 'warning' | 'unknown';
  size?: 'small' | 'medium' | 'large';
  showLabel?: boolean;
  pulse?: boolean;  // 是否闪烁动画
}
```

#### SkillCard (Skill卡片)
```typescript
interface SkillCardProps {
  skill: {
    id: string;
    name: string;
    type: 'system' | 'application';
    status: string;
    version: string;
    capabilities: number;
    lastUpdated: number;
  };
  viewMode: 'grid' | 'list';
  onClick?: (skillId: string) => void;
  actions?: Action[];
}
```

#### Timeline (时间线)
```typescript
interface TimelineProps {
  events: TimelineEvent[];
  orientation: 'vertical' | 'horizontal';
  onEventClick?: (event: TimelineEvent) => void;
  groupBy?: 'hour' | 'day' | 'skill';
}

interface TimelineEvent {
  id: string;
  timestamp: number;
  type: string;
  title: string;
  description?: string;
  metadata?: Record<string, any>;
  color?: string;
}
```

### 3.2 图表组件

#### MetricsChart (指标图表)
```typescript
interface MetricsChartProps {
  type: 'line' | 'bar' | 'area' | 'gauge';
  data: ChartData[];
  metrics: string[];
  timeRange: TimeRange;
  refreshInterval?: number;
  onZoom?: (range: TimeRange) => void;
}
```

#### TopologyGraph (拓扑图)
```typescript
interface TopologyGraphProps {
  nodes: GraphNode[];
  edges: GraphEdge[];
  layout: 'force' | 'hierarchical' | 'circular';
  onNodeClick?: (node: GraphNode) => void;
  onEdgeClick?: (edge: GraphEdge) => void;
}
```

---

## 四、交互设计

### 4.1 主要交互流程

#### 流程1: 查看Skill详情
1. 用户在列表页点击Skill名称
2. 页面跳转到详情页，默认显示Overview标签
3. 用户可切换标签查看不同信息
4. 点击"Edit Config"进入编辑模式
5. 保存后显示成功提示，返回查看模式

#### 流程2: 监控实时状态
1. 用户进入Monitor页面
2. 系统自动建立WebSocket连接
3. 实时数据更新图表和状态卡片
4. 用户点击某个Skill查看详情
5. 弹窗或侧边栏显示Skill详细信息

#### 流程3: 追踪能力调用
1. 用户进入Capability Trace页面
2. 选择要追踪的Skill和Capability
3. 系统显示调用时间线
4. 用户点击某个调用查看详情
5. 展开显示完整的调用链路和日志

### 4.2 响应式设计

#### 断点定义
- **Desktop**: >= 1200px - 完整布局
- **Tablet**: 768px - 1199px - 侧边栏收起
- **Mobile**: < 768px - 单列布局

#### 适配策略
- 表格在移动端转为卡片列表
- 图表支持手势缩放和滑动
- 导航栏在移动端转为汉堡菜单

---

## 五、视觉设计

### 5.1 色彩系统

```css
/* 主色调 */
--primary: #1890ff;
--primary-light: #40a9ff;
--primary-dark: #096dd9;

/* 状态色 */
--status-active: #52c41a;
--status-inactive: #bfbfbf;
--status-error: #f5222d;
--status-warning: #faad14;
--status-unknown: #d9d9d9;

/* 背景色 */
--bg-primary: #ffffff;
--bg-secondary: #f5f5f5;
--bg-tertiary: #fafafa;

/* 文字色 */
--text-primary: #262626;
--text-secondary: #595959;
--text-tertiary: #8c8c8c;
```

### 5.2 字体规范

- **标题**: 24px / 600 weight / -0.5px letter-spacing
- **副标题**: 18px / 500 weight
- **正文**: 14px / 400 weight / 1.5 line-height
- **辅助文字**: 12px / 400 weight

### 5.3 间距系统

```css
--space-xs: 4px;
--space-sm: 8px;
--space-md: 16px;
--space-lg: 24px;
--space-xl: 32px;
--space-xxl: 48px;
```

---

## 六、技术实现建议

### 6.1 前端技术栈
- **框架**: React 18 + TypeScript
- **状态管理**: Zustand / Redux Toolkit
- **UI组件库**: Ant Design 5.x
- **图表库**: ECharts / D3.js
- **实时通信**: Socket.io / WebSocket

### 6.2 性能优化
- 虚拟滚动处理大量数据
- 图表懒加载和按需渲染
- API请求缓存和去重
- WebSocket连接池管理

### 6.3 可访问性
- 支持键盘导航
- ARIA标签完整
- 高对比度模式
- 屏幕阅读器友好

---

*文档结束*

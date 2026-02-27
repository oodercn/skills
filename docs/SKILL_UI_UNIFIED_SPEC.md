# Skill UI 统一规范：模板+脚本+文档模式

> **版本**: 1.0.0  
> **适用**: 所有 Skill 类型  
> **最后更新**: 2026-02-25

---

## 1. 架构决策：扩展 vs 分类

### 1.1 方案对比

| 维度 | 方案A：扩展所有Skill | 方案B：单独分类 | 推荐 |
|------|-------------------|---------------|------|
| **实现复杂度** | 低（复用现有机制） | 高（需新架构） | ✅ A |
| **维护成本** | 低（统一标准） | 高（两套系统） | ✅ A |
| **LLM理解度** | 高（统一概念） | 中（需区分类型） | ✅ A |
| **灵活性** | 高（任意Skill可用） | 中（仅限特定类型） | ✅ A |
| **向后兼容** | 好（渐进增强） | 差（破坏性变更） | ✅ A |
| **生态扩展** | 易（Skill自带UI） | 难（需单独开发） | ✅ A |

### 1.2 决策结论

**采用方案A：扩展所有 Skill 支持 `ui/` 目录**

理由：
1. **统一概念**：LLM 只需理解 "Skill 可以带 UI"，无需区分类型
2. **渐进增强**：现有 Skill 无需修改，新增 UI 即可生效
3. **生态友好**：任何 Skill 都可以自带展示界面
4. **降低门槛**：开发者无需学习新类型，按规范添加文件即可

---

## 2. 统一目录结构

### 2.1 标准 Skill 结构（含 UI）

```
skill-{name}/
├── skill.yaml                    # Skill 元数据（新增 ui 配置段）
├── README.md                     # 说明文档
├── src/                          # 后端代码（Java/Python/Node）
│   └── main/
│       ├── java/                 # 或 python/, node/
│       └── resources/
│           └── skill.yaml
│
├── ui/                           # 【新增】UI 资源目录
│   ├── templates/                # HTML 模板
│   │   ├── index.html           # 主页面
│   │   ├── detail.html          # 详情页
│   │   └── components/          # 组件模板
│   │       ├── card.html
│   │       ├── table.html
│   │       └── form.html
│   │
│   ├── scripts/                  # JavaScript 脚本
│   │   ├── app.js               # 主应用逻辑
│   │   ├── api-client.js        # API 客户端
│   │   ├── components/          # 组件脚本
│   │   │   ├── selector.js      # 选择器组件
│   │   │   ├── status-badge.js  # 状态标签
│   │   │   └── data-table.js    # 数据表格
│   │   └── utils/
│   │       ├── formatter.js     # 格式化工具
│   │       └── validator.js     # 验证工具
│   │
│   ├── styles/                   # CSS 样式
│   │   ├── main.css             # 主样式
│   │   ├── components.css       # 组件样式
│   │   └── theme.css            # 主题变量
│   │
│   └── docs/                     # UI 使用文档
│       ├── README.md            # UI 说明
│       ├── API.md               # API 接口文档
│       └── LLM-PROMPT.md        # LLM 生成提示词
│
└── package.json / pom.xml        # 构建配置
```

### 2.2 最小化 UI 结构

```
skill-{name}/
├── skill.yaml
└── ui/
    ├── templates/
    │   └── index.html           # 必需：至少一个模板
    ├── scripts/
    │   └── app.js               # 可选：基础交互
    └── docs/
        └── README.md            # 可选：使用说明
```

---

## 3. skill.yaml UI 配置规范

### 3.1 新增 `ui` 配置段

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-weather
  name: 天气预报 Skill
  version: 1.0.0
  description: 提供天气预报查询和展示
  
spec:
  type: tool-skill  # 保持原有类型
  
  # ============================================
  # 【新增】UI 配置段
  # ============================================
  ui:
    enabled: true                    # 是否启用 UI
    
    # 入口配置
    entry:
      template: index.html           # 入口模板
      title: 天气预报                # 页面标题
      icon: ri-sun-line             # Remix Icon
      
    # 菜单配置
    menu:
      position: sidebar              # sidebar | header | inline
      category: tools               # 菜单分类
      order: 100                    # 排序权重
      
    # 数据展示配置
    display:
      # 默认视图
      defaultView: card             # card | table | list | custom
      
      # 卡片视图配置
      cardView:
        template: components/weather-card.html
        fields:
          - name: city
            label: 城市
            icon: ri-map-pin-line
          - name: temperature
            label: 温度
            formatter: temperature    # 使用 formatter
            unit: °C
          - name: condition
            label: 天气状况
            component: status-badge   # 使用组件
            mappings:
              sunny: { text: 晴, color: success, icon: ri-sun-line }
              cloudy: { text: 多云, color: warning, icon: ri-cloud-line }
              rainy: { text: 雨, color: info, icon: ri-rainy-line }
              
      # 表格视图配置
      tableView:
        template: components/weather-table.html
        columns:
          - field: city
            title: 城市
            width: 120
            sortable: true
          - field: temperature
            title: 温度
            width: 100
            align: right
            formatter: temperature
          - field: condition
            title: 状况
            component: status-badge
          - field: updateTime
            title: 更新时间
            formatter: datetime
            
    # 表单配置（用于搜索/筛选）
    forms:
      search:
        template: components/search-form.html
        fields:
          - name: city
            type: select               # 自动关联 Skill 数据
            label: 选择城市
            source:                    # 数据来源
              type: skill-api         # skill-api | static | external
              api: /api/cities        # Skill 内部 API
              valueField: id
              labelField: name
              
          - name: dateRange
            type: date-range
            label: 日期范围
            
          - name: forecastType
            type: radio
            label: 预报类型
            options:
              - value: today
                label: 今日
              - value: week
                label: 一周
              - value: month
                label: 一月
                
    # 组件库配置
    components:
      # 状态标签
      status-badge:
        template: components/status-badge.html
        script: components/status-badge.js
        props:
          - name: status
            type: string
          - name: size
            type: string
            default: medium
            
      # 数据选择器
      data-selector:
        template: components/data-selector.html
        script: components/selector.js
        props:
          - name: dataSource
            type: string
          - name: multiple
            type: boolean
            default: false
            
      # 数据表格
      data-table:
        template: components/data-table.html
        script: components/data-table.js
        props:
          - name: columns
            type: array
          - name: data
            type: array
          - name: pagination
            type: boolean
            default: true
            
    # 交互配置
    interactions:
      # 点击事件
      onItemClick:
        action: navigate            # navigate | modal | drawer | api
        target: detail.html
        params:
          - city
          - date
          
      # 刷新事件
      onRefresh:
        action: api
        api: /api/weather/refresh
        
      # 导出事件
      onExport:
        action: api
        api: /api/weather/export
        format: [csv, excel, pdf]
        
    # 自动关联配置
    autoBind:
      # 关联 Skill 分类数据
      categories:
        enabled: true
        field: category
        display: select
        
      # 关联 Skill 状态
      statuses:
        enabled: true
        field: status
        display: badge
        mappings:
          active: { text: 运行中, color: success }
          inactive: { text: 已停止, color: danger }
          pending: { text: 待启动, color: warning }
          
      # 关联其他 Skill
      relations:
        - skillId: skill-location
          field: location
          display: map-marker
          
        - skillId: skill-calendar
          field: eventDate
          display: calendar
```

---

## 4. 模板规范（LLM 友好）

### 4.1 HTML 模板规范

```html
<!-- ui/templates/index.html -->
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{{skill.ui.entry.title}}</title>
    
    <!-- 公共资源（CDN） -->
    <link rel="stylesheet" href="https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css">
    
    <!-- Skill 本地样式 -->
    <link rel="stylesheet" href="../styles/main.css">
</head>
<body>
    <div class="skill-ui-container" data-skill-id="{{skill.id}}">
        <!-- 头部区域 -->
        <header class="skill-ui-header">
            <h1 class="skill-ui-title">
                <i class="{{skill.ui.entry.icon}}"></i>
                {{skill.ui.entry.title}}
            </h1>
            
            <!-- 搜索表单（自动绑定） -->
            <div class="skill-ui-search" data-form="search"></div>
        </header>
        
        <!-- 内容区域 -->
        <main class="skill-ui-content">
            <!-- 视图切换 -->
            <div class="skill-ui-toolbar">
                <div class="view-switcher">
                    <button class="active" data-view="card">
                        <i class="ri-grid-line"></i> 卡片
                    </button>
                    <button data-view="table">
                        <i class="ri-table-line"></i> 表格
                    </button>
                </div>
                
                <div class="actions">
                    <button class="nx-btn" data-action="refresh">
                        <i class="ri-refresh-line"></i> 刷新
                    </button>
                    <button class="nx-btn nx-btn--primary" data-action="export">
                        <i class="ri-download-line"></i> 导出
                    </button>
                </div>
            </div>
            
            <!-- 数据展示区域（自动渲染） -->
            <div class="skill-ui-data" 
                 data-view="{{skill.ui.display.defaultView}}"
                 data-source="/api/skills/{{skill.id}}/data">
                <!-- 动态内容 -->
            </div>
        </main>
    </div>
    
    <!-- 公共脚本 -->
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/nexus.js"></script>
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/skill-ui-runtime.js"></script>
    
    <!-- Skill 本地脚本 -->
    <script src="../scripts/app.js"></script>
</body>
</html>
```

### 4.2 组件模板示例

```html
<!-- ui/templates/components/weather-card.html -->
<template id="weather-card-template">
    <div class="weather-card" data-city="{{city}}">
        <div class="weather-card__header">
            <i class="{{condition.icon}}"></i>
            <span class="weather-card__city">{{city}}</span>
        </div>
        <div class="weather-card__body">
            <div class="temperature">{{temperature}}°C</div>
            <div class="condition">{{condition.text}}</div>
            <div class="details">
                <span><i class="ri-drop-line"></i> {{humidity}}%</span>
                <span><i class="ri-windy-line"></i> {{windSpeed}}km/h</span>
            </div>
        </div>
        <div class="weather-card__footer">
            <span class="update-time">{{updateTime | datetime}}</span>
        </div>
    </div>
</template>
```

### 4.3 JavaScript 组件规范

```javascript
// ui/scripts/components/selector.js

/**
 * 数据选择器组件
 * 自动关联 Skill 数据
 */
class SkillDataSelector extends SkillUIComponent {
    
    static get componentName() {
        return 'data-selector';
    }
    
    static get props() {
        return {
            dataSource: { type: 'string', required: true },
            multiple: { type: 'boolean', default: false },
            placeholder: { type: 'string', default: '请选择...' }
        };
    }
    
    async init() {
        // 加载数据
        await this.loadData();
        
        // 渲染选项
        this.renderOptions();
        
        // 绑定事件
        this.bindEvents();
    }
    
    async loadData() {
        const config = this.getSkillConfig();
        const source = config.ui.forms.search.fields
            .find(f => f.name === this.props.dataSource)?.source;
        
        if (source.type === 'skill-api') {
            const response = await SkillAPI.get(source.api);
            if (response.status === 'success') {
                this.data = response.data;
            }
        }
    }
    
    renderOptions() {
        const options = this.data.map(item => `
            <option value="${item[source.valueField]}">
                ${item[source.labelField]}
            </option>
        `).join('');
        
        this.element.innerHTML = `
            <select class="nx-select" ${this.props.multiple ? 'multiple' : ''}>
                <option value="">${this.props.placeholder}</option>
                ${options}
            </select>
        `;
    }
    
    bindEvents() {
        this.element.querySelector('select').addEventListener('change', (e) => {
            this.emit('change', {
                value: e.target.value,
                values: Array.from(e.target.selectedOptions).map(o => o.value)
            });
        });
    }
}

// 注册组件
SkillUI.registerComponent(SkillDataSelector);
```

---

## 5. 自动关联机制

### 5.1 Skill 分类自动关联

```javascript
// 自动绑定 Skill 分类到选择器
class SkillCategoryBinder {
    
    async bind(element, config) {
        // 获取所有 Skill 分类
        const categories = await this.fetchSkillCategories();
        
        // 渲染选择器
        const selector = new SkillDataSelector(element, {
            data: categories,
            valueField: 'id',
            labelField: 'name',
            iconField: 'icon'
        });
        
        await selector.init();
    }
    
    async fetchSkillCategories() {
        // 从 Skill Registry 获取分类
        const response = await SkillAPI.get('/api/skills/categories');
        return response.data.map(cat => ({
            id: cat.id,
            name: cat.name,
            icon: cat.metadata?.icon || 'ri-folder-line'
        }));
    }
}
```

### 5.2 Skill 状态自动关联

```javascript
// 自动绑定 Skill 状态到标签
class SkillStatusBinder {
    
    async bind(element, config) {
        const status = element.dataset.status;
        const mapping = config.mappings[status];
        
        if (mapping) {
            element.innerHTML = `
                <span class="nx-badge nx-badge--${mapping.color}">
                    <i class="${mapping.icon || ''}"></i>
                    ${mapping.text}
                </span>
            `;
        }
    }
}
```

### 5.3 跨 Skill 数据关联

```javascript
// 关联其他 Skill 的数据
class SkillRelationBinder {
    
    async bind(element, config) {
        const { skillId, field, display } = config;
        
        // 获取关联 Skill 的数据
        const relatedData = await SkillAPI.get(
            `/api/skills/${skillId}/data?field=${field}`
        );
        
        // 根据 display 类型渲染
        switch (display) {
            case 'map-marker':
                this.renderMapMarker(element, relatedData);
                break;
            case 'calendar':
                this.renderCalendar(element, relatedData);
                break;
            case 'select':
                this.renderSelect(element, relatedData);
                break;
        }
    }
}
```

---

## 6. LLM 生成规范

### 6.1 LLM 提示词模板（README.md）

```markdown
## LLM 生成指南

### 快速开始

使用以下提示词通过 AI（文心/豆包/千问）生成本 Skill 的 UI：

#### 基础模板

```
请为 "{skill.name}" Skill 创建 UI 界面：

【Skill 信息】
- 名称: {skill.name}
- 描述: {skill.description}
- 类型: {skill.spec.type}
- 功能: {skill.spec.capabilities}

【架构规范】（必须遵守）
1. 使用 CDN 资源:
   - CSS: https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css
   - JS: https://gitee.com/ooderCN/nexus-assets/raw/main/js/skill-ui-runtime.js
   - Icon: https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css

2. 使用 Remix Icon (ri-* 前缀)

3. 使用 CSS 变量 (--ns-*):
   - var(--ns-card-bg) - 卡片背景
   - var(--ns-border) - 边框
   - var(--ns-dark) - 文字颜色
   - var(--ns-primary) - 主题色

4. 页面结构:
   <div class="skill-ui-container">
     <header class="skill-ui-header">...</header>
     <main class="skill-ui-content">...</main>
   </div>

5. 使用组件类:
   - nx-card, nx-btn, nx-input, nx-select, nx-badge

【UI 需求】
- 展示方式: 卡片视图 + 表格视图
- 需要字段: {fields}
- 交互功能: 搜索、筛选、导出

【输出要求】
1. ui/templates/index.html
2. ui/templates/components/*.html
3. ui/scripts/app.js
4. ui/styles/main.css
```

#### 组件生成提示词

```
请生成 "{component.name}" 组件：

【组件类型】{component.type}
【功能描述】{component.description}

【Props】
{component.props}

【使用示例】
<{component.tag} prop1="value1" prop2="value2"></{component.tag}>

【输出】
1. HTML 模板 (template)
2. JavaScript 类 (继承 SkillUIComponent)
3. CSS 样式
```
```

### 6.2 自动化生成配置

```yaml
# ui/llm-config.yaml
llm:
  # 生成配置
  generation:
    # 模板变量
    variables:
      skillName: "${skill.metadata.name}"
      skillDescription: "${skill.metadata.description}"
      capabilities: "${skill.spec.capabilities}"
      
    # 输出文件映射
    outputs:
      - template: index.html
        path: ui/templates/index.html
        prompt: prompts/index-html.txt
        
      - template: app.js
        path: ui/scripts/app.js
        prompt: prompts/app-js.txt
        
      - template: main.css
        path: ui/styles/main.css
        prompt: prompts/main-css.txt
        
  # 验证规则
  validation:
    rules:
      - name: check-cdn-resources
        pattern: "https://gitee.com/ooderCN/nexus-assets"
        required: true
        
      - name: check-remix-icon
        pattern: "ri-"
        required: true
        
      - name: check-css-variables
        pattern: "var\\(--ns-"
        required: true
```

---

## 7. 使用场景示例

### 7.1 天气预报 Skill

```yaml
# skill-weather/skill.yaml
spec:
  ui:
    enabled: true
    entry:
      template: index.html
      title: 天气预报
      icon: ri-sun-cloudy-line
      
    display:
      defaultView: card
      cardView:
        template: components/weather-card.html
        fields:
          - name: city
            label: 城市
            icon: ri-map-pin-line
          - name: temperature
            label: 温度
            formatter: temperature
            unit: °C
          - name: condition
            label: 天气
            component: status-badge
            mappings:
              sunny: { text: 晴, color: warning, icon: ri-sun-line }
              rainy: { text: 雨, color: info, icon: ri-rainy-line }
              
    forms:
      search:
        fields:
          - name: city
            type: select
            source:
              type: skill-api
              api: /api/cities
```

### 7.2 Skill 管理器

```yaml
# skill-manager/skill.yaml
spec:
  ui:
    enabled: true
    entry:
      template: index.html
      title: Skill 管理
      icon: ri-apps-line
      
    display:
      defaultView: table
      tableView:
        columns:
          - field: name
            title: 名称
          - field: category
            title: 分类
            autoBind: skill-category  # 自动关联分类
          - field: status
            title: 状态
            autoBind: skill-status    # 自动关联状态
            component: status-badge
          - field: version
            title: 版本
          - field: actions
            title: 操作
            component: action-buttons
            
    autoBind:
      categories:
        enabled: true
      statuses:
        enabled: true
        mappings:
          active: { text: 运行中, color: success }
          stopped: { text: 已停止, color: danger }
```

### 7.3 用户管理 Skill

```yaml
# skill-user/skill.yaml
spec:
  ui:
    enabled: true
    
    forms:
      create:
        fields:
          - name: username
            type: text
            required: true
          - name: role
            type: select
            source:
              type: skill-api
              api: /api/roles
          - name: department
            type: select
            source:
              type: external
              skillId: skill-org-structure
              api: /api/departments
```

---

## 8. 运行时架构

### 8.1 Skill UI 运行时

```
┌─────────────────────────────────────────────────────────────────┐
│                    Skill UI 运行时架构                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                  Skill UI Runtime                        │   │
│  │                                                          │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │   │
│  │  │  配置解析器  │  │  模板引擎   │  │  组件注册表  │     │   │
│  │  │  (Parser)  │  │  (Engine)  │  │ (Registry)  │     │   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘     │   │
│  │         └─────────────────┼─────────────────┘            │   │
│  │                           ▼                             │   │
│  │                  ┌─────────────────┐                   │   │
│  │                  │   渲染协调器    │                   │   │
│  │                  │  (Coordinator)  │                   │   │
│  │                  └────────┬────────┘                   │   │
│  │                           ▼                             │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │   │
│  │  │  数据绑定   │  │  事件处理   │  │  API 客户端  │     │   │
│  │  │  (Binder)  │  │  (Handler)  │  │  (Client)   │     │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘     │   │
│  │                                                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    自动关联层                            │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │   │
│  │  │ Skill分类   │  │ Skill状态   │  │ 跨Skill关联 │     │   │
│  │  │ 绑定器     │  │ 绑定器     │  │ 绑定器     │     │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 8.2 渲染流程

```javascript
// Skill UI 渲染流程
class SkillUIRuntime {
    
    async render(skillId, container) {
        // 1. 加载 Skill 配置
        const config = await this.loadSkillConfig(skillId);
        
        // 2. 解析 UI 配置
        const uiConfig = config.spec.ui;
        
        // 3. 加载模板
        const template = await this.loadTemplate(skillId, uiConfig.entry.template);
        
        // 4. 渲染基础结构
        container.innerHTML = template;
        
        // 5. 初始化组件
        await this.initComponents(container, uiConfig.components);
        
        // 6. 绑定自动关联
        await this.bindAutoRelations(container, uiConfig.autoBind);
        
        // 7. 加载数据
        await this.loadData(container, uiConfig.display);
        
        // 8. 绑定事件
        this.bindEvents(container, uiConfig.interactions);
    }
    
    async bindAutoRelations(container, autoBindConfig) {
        // 绑定 Skill 分类
        if (autoBindConfig.categories?.enabled) {
            const categoryElements = container.querySelectorAll('[data-autobind="category"]');
            for (const el of categoryElements) {
                await this.categoryBinder.bind(el, autoBindConfig.categories);
            }
        }
        
        // 绑定 Skill 状态
        if (autoBindConfig.statuses?.enabled) {
            const statusElements = container.querySelectorAll('[data-autobind="status"]');
            for (const el of statusElements) {
                await this.statusBinder.bind(el, autoBindConfig.statuses);
            }
        }
        
        // 绑定跨 Skill 关联
        if (autoBindConfig.relations) {
            for (const relation of autoBindConfig.relations) {
                const elements = container.querySelectorAll(`[data-relation="${relation.field}"]`);
                for (const el of elements) {
                    await this.relationBinder.bind(el, relation);
                }
            }
        }
    }
}
```

---

## 9. 总结

### 9.1 核心优势

1. **统一规范**：所有 Skill 使用相同的 UI 规范，降低学习成本
2. **LLM 友好**：标准化模板，便于 AI 生成代码
3. **自动关联**：自动绑定 Skill 分类、状态等元数据
4. **渐进增强**：现有 Skill 无需修改，按需添加 UI
5. **生态开放**：任何 Skill 都可以自带展示界面

### 9.2 关键设计

- **`ui/` 目录**：统一的 UI 资源存放位置
- **`skill.yaml` 配置**：声明式 UI 配置
- **自动绑定**：减少重复开发，自动关联 Skill 元数据
- **组件化**：可复用的 UI 组件
- **CDN 资源**：公共资源托管，Skill 包体积最小化

### 9.3 实施路径

1. **Phase 1**：实现基础运行时（配置解析、模板渲染）
2. **Phase 2**：实现自动绑定（分类、状态、跨 Skill）
3. **Phase 3**：实现组件库（选择器、表格、表单）
4. **Phase 4**：实现 LLM 生成工具
5. **Phase 5**：迁移现有 Skill 添加 UI

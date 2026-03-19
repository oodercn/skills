# Skill UI 规范：基于 Google A2A 协议 + Nexus 架构

> **版本**: 1.0.0  
> **协议基础**: Google A2A (Agent2Agent) Protocol  
> **UI架构**: Nexus Console  
> **最后更新**: 2026-02-25

---

## 1. 架构概述

### 1.1 核心概念映射

将 Google A2A 协议的核心概念映射到 Skill UI 系统：

| A2A 概念 | Skill UI 映射 | 说明 |
|---------|--------------|------|
| **Agent Card** | **Skill Card** | Skill 的能力声明和元数据 |
| **Task** | **UI Task** | 用户与 Skill UI 的交互任务 |
| **Message** | **UI Message** | 组件间通信消息 |
| **Part** | **UI Part** | UI 组件片段（text/file/data） |
| **Skill** | **UI Capability** | Skill 提供的 UI 能力 |
| **Input Mode** | **Interaction Mode** | 交互模式（text/form/voice） |

### 1.2 架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Nexus Console 前端                                  │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Skill UI Runtime (A2A Compatible)                 │   │
│  │                                                                      │   │
│  │   ┌──────────────┐    ┌──────────────┐    ┌──────────────┐         │   │
│  │   │  Skill Card   │◄──►│   UI Task    │◄──►│  UI Message  │         │   │
│  │   │   Registry    │    │   Manager    │    │   Handler    │         │   │
│  │   └──────────────┘    └──────────────┘    └──────────────┘         │   │
│  │          ▲                   ▲                   ▲                  │   │
│  │          │                   │                   │                  │   │
│  │   ┌──────────────┐    ┌──────────────┐    ┌──────────────┐         │   │
│  │   │  Component   │    │   Part       │    │  Capability  │         │   │
│  │   │   Factory    │    │   Renderer   │    │   Registry   │         │   │
│  │   └──────────────┘    └──────────────┘    └──────────────┘         │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    ▲                                         │
│                                    │                                         │
│  ┌─────────────────┐    ┌─────────┴──────────┐    ┌─────────────────┐      │
│  │   Skill Card    │◄──►│   A2A Protocol     │◄──►│   UI Component  │      │
│  │   (JSON)        │    │   (HTTP/SSE)       │    │   (HTML/JS/CSS) │      │
│  └─────────────────┘    └────────────────────┘    └─────────────────┘      │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Skill Card 规范（A2A 兼容）

### 2.1 Skill Card JSON Schema

基于 A2A Agent Card 规范，定义 Skill Card：

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "$id": "https://ooder.net/schemas/skill-card.json",
  "title": "Skill Card",
  "description": "A2A-compatible Skill capability declaration",
  "type": "object",
  "required": ["name", "url", "version", "capabilities"],
  "properties": {
    "name": {
      "type": "string",
      "description": "Skill 名称"
    },
    "description": {
      "type": "string",
      "description": "Skill 描述"
    },
    "url": {
      "type": "string",
      "format": "uri",
      "description": "Skill 服务端点 URL"
    },
    "version": {
      "type": "string",
      "description": "Skill 版本"
    },
    "authentication": {
      "type": "object",
      "properties": {
        "schemes": {
          "type": "array",
          "items": {
            "type": "string",
            "enum": ["apiKey", "oauth2", "jwt", "none"]
          }
        }
      }
    },
    "defaultInputModes": {
      "type": "array",
      "items": {
        "type": "string",
        "enum": ["text", "file", "form", "voice", "canvas"]
      },
      "default": ["text", "file", "form"]
    },
    "defaultOutputModes": {
      "type": "array",
      "items": {
        "type": "string",
        "enum": ["text", "file", "form", "voice", "canvas", "ui"]
      },
      "default": ["text", "ui"]
    },
    "capabilities": {
      "type": "object",
      "properties": {
        "streaming": {
          "type": "boolean",
          "default": false
        },
        "pushNotifications": {
          "type": "boolean",
          "default": false
        },
        "ui": {
          "type": "object",
          "description": "UI 能力声明",
          "properties": {
            "enabled": {
              "type": "boolean",
              "default": false
            },
            "components": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/UIComponent"
              }
            },
            "interactionModes": {
              "type": "array",
              "items": {
                "type": "string",
                "enum": ["form", "canvas", "dialog", "panel"]
              }
            }
          }
        }
      }
    },
    "skills": {
      "type": "array",
      "items": {
        "$ref": "#/definitions/SkillInfo"
      }
    }
  },
  "definitions": {
    "UIComponent": {
      "type": "object",
      "required": ["type", "name"],
      "properties": {
        "type": {
          "type": "string",
          "enum": [
            "card", "form", "grid", "table", "list",
            "chart", "map", "calendar", "gallery", "tree",
            "dialog", "panel", "tabs", "steps", "timeline"
          ]
        },
        "name": {
          "type": "string"
        },
        "description": {
          "type": "string"
        },
        "schema": {
          "type": "object",
          "description": "组件数据 Schema"
        },
        "config": {
          "type": "object",
          "description": "组件配置"
        }
      }
    },
    "SkillInfo": {
      "type": "object",
      "required": ["id", "name"],
      "properties": {
        "id": {
          "type": "string"
        },
        "name": {
          "type": "string"
        },
        "description": {
          "type": "string"
        },
        "tags": {
          "type": "array",
          "items": {
            "type": "string"
          }
        },
        "examples": {
          "type": "array",
          "items": {
            "type": "string"
          }
        },
        "inputModes": {
          "type": "array",
          "items": {
            "type": "string",
            "enum": ["text", "file", "form", "voice", "canvas"]
          }
        },
        "outputModes": {
          "type": "array",
          "items": {
            "type": "string",
            "enum": ["text", "file", "form", "voice", "canvas", "ui"]
          }
        }
      }
    }
  }
}
```

### 2.2 Skill Card 示例

```json
{
  "name": "Weather Skill",
  "description": "提供全球城市天气预报查询",
  "url": "https://skills.ooder.net/weather",
  "version": "1.0.0",
  "authentication": {
    "schemes": ["apiKey"]
  },
  "defaultInputModes": ["text", "form"],
  "defaultOutputModes": ["text", "ui"],
  "capabilities": {
    "streaming": false,
    "pushNotifications": true,
    "ui": {
      "enabled": true,
      "components": [
        {
          "type": "card",
          "name": "weather-card",
          "description": "天气信息卡片",
          "schema": {
            "type": "object",
            "properties": {
              "city": { "type": "string" },
              "temperature": { "type": "number" },
              "condition": { 
                "type": "string",
                "enum": ["sunny", "cloudy", "rainy", "snowy"]
              },
              "humidity": { "type": "number" },
              "windSpeed": { "type": "number" },
              "updateTime": { "type": "string", "format": "date-time" }
            }
          },
          "config": {
            "theme": "auto",
            "showDetails": true,
            "refreshInterval": 300
          }
        },
        {
          "type": "form",
          "name": "weather-search",
          "description": "天气查询表单",
          "schema": {
            "type": "object",
            "properties": {
              "city": {
                "type": "string",
                "title": "城市",
                "component": "select",
                "source": "/api/cities"
              },
              "dateRange": {
                "type": "string",
                "title": "日期范围",
                "component": "date-range"
              }
            }
          }
        },
        {
          "type": "grid",
          "name": "weather-grid",
          "description": "多城市天气对比",
          "schema": {
            "type": "array",
            "items": {
              "$ref": "#/components/weather-card/schema"
            }
          }
        }
      ],
      "interactionModes": ["form", "panel"]
    }
  },
  "skills": [
    {
      "id": "get-current-weather",
      "name": "获取当前天气",
      "description": "获取指定城市的当前天气信息",
      "tags": ["weather", "current"],
      "examples": [
        "北京今天天气怎么样？",
        "查询上海的温度"
      ],
      "inputModes": ["text", "form"],
      "outputModes": ["text", "ui"]
    },
    {
      "id": "get-forecast",
      "name": "获取天气预报",
      "description": "获取未来7天天气预报",
      "tags": ["weather", "forecast"],
      "examples": [
        "北京未来一周天气",
        "上海下周天气预报"
      ],
      "inputModes": ["text", "form"],
      "outputModes": ["ui"]
    }
  ]
}
```

---

## 3. UI Task 规范（A2A 兼容）

### 3.1 Task 生命周期

基于 A2A Task 状态机：

```
┌─────────────┐    submit    ┌─────────────┐
│   created   │─────────────►│  submitted  │
└─────────────┘              └──────┬──────┘
                                    │
         ┌──────────────────────────┼──────────────────────────┐
         │                          │                          │
         ▼                          ▼                          ▼
┌─────────────┐              ┌─────────────┐            ┌─────────────┐
│  cancelled  │◄─────────────│   working   │───────────►│  input-required
└─────────────┘   cancel     └──────┬──────┘  need input └─────────────┘
                                    │                               │
         ┌──────────────────────────┼──────────────────────────┐   │
         │                          │                          │   │
         ▼                          ▼                          ▼   │
┌─────────────┐              ┌─────────────┐            ┌────────┴───┘
│   failed    │◄─────────────│  completed  │            │   submit   │
└─────────────┘   error      └─────────────┘            └────────────►
```

### 3.2 Task JSON Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "$id": "https://ooder.net/schemas/skill-task.json",
  "title": "Skill UI Task",
  "type": "object",
  "required": ["id", "sessionId", "skillId", "state"],
  "properties": {
    "id": {
      "type": "string",
      "description": "Task 唯一标识"
    },
    "sessionId": {
      "type": "string",
      "description": "会话标识"
    },
    "skillId": {
      "type": "string",
      "description": "关联的 Skill ID"
    },
    "state": {
      "type": "string",
      "enum": ["created", "submitted", "working", "input-required", "completed", "cancelled", "failed"]
    },
    "input": {
      "$ref": "#/definitions/TaskInput"
    },
    "output": {
      "$ref": "#/definitions/TaskOutput"
    },
    "ui": {
      "type": "object",
      "description": "UI 相关配置",
      "properties": {
        "component": {
          "type": "string",
          "description": "使用的组件类型"
        },
        "config": {
          "type": "object",
          "description": "组件配置"
        },
        "parts": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/UIPart"
          }
        }
      }
    },
    "history": {
      "type": "array",
      "items": {
        "$ref": "#/definitions/TaskMessage"
      }
    },
    "createdAt": {
      "type": "string",
      "format": "date-time"
    },
    "updatedAt": {
      "type": "string",
      "format": "date-time"
    }
  },
  "definitions": {
    "TaskInput": {
      "type": "object",
      "properties": {
        "mode": {
          "type": "string",
          "enum": ["text", "file", "form", "voice", "canvas"]
        },
        "parts": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/UIPart"
          }
        }
      }
    },
    "TaskOutput": {
      "type": "object",
      "properties": {
        "mode": {
          "type": "string",
          "enum": ["text", "file", "form", "voice", "canvas", "ui"]
        },
        "parts": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/UIPart"
          }
        }
      }
    },
    "UIPart": {
      "type": "object",
      "required": ["type"],
      "properties": {
        "type": {
          "type": "string",
          "enum": ["text", "file", "data", "ui", "form", "canvas"]
        },
        "text": {
          "type": "string"
        },
        "file": {
          "type": "object",
          "properties": {
            "name": { "type": "string" },
            "mimeType": { "type": "string" },
            "bytes": { "type": "string" },
            "uri": { "type": "string" }
          }
        },
        "data": {
          "type": "object",
          "description": "结构化数据"
        },
        "ui": {
          "type": "object",
          "description": "UI 组件定义",
          "properties": {
            "component": { "type": "string" },
            "props": { "type": "object" },
            "data": { "type": "object" }
          }
        }
      }
    },
    "TaskMessage": {
      "type": "object",
      "properties": {
        "role": {
          "type": "string",
          "enum": ["user", "skill", "system"]
        },
        "parts": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/UIPart"
          }
        },
        "timestamp": {
          "type": "string",
          "format": "date-time"
        }
      }
    }
  }
}
```

---

## 4. UI 组件规范（Nexus + A2A 融合）

### 4.1 组件类型映射

| A2A 交互模式 | Nexus 组件 | Skill UI 组件 | 用途 |
|-------------|-----------|--------------|------|
| text | - | text-display | 纯文本展示 |
| file | - | file-viewer | 文件预览 |
| form | nx-form | skill-form | 表单输入 |
| canvas | - | skill-canvas | 画布/图表 |
| ui | nx-card | skill-card | 卡片容器 |
| ui | nx-table | skill-grid | 数据表格 |
| ui | - | skill-list | 列表展示 |
| ui | - | skill-chart | 图表组件 |
| ui | - | skill-map | 地图组件 |
| ui | - | skill-calendar | 日历组件 |
| ui | - | skill-gallery | 画廊/卡片组 |
| ui | - | skill-tree | 树形组件 |
| ui | nx-modal | skill-dialog | 对话框 |
| ui | nx-panel | skill-panel | 面板容器 |
| ui | - | skill-tabs | 标签页 |
| ui | - | skill-steps | 步骤条 |
| ui | - | skill-timeline | 时间线 |

### 4.2 通用组件 Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "$id": "https://ooder.net/schemas/ui-component.json",
  "title": "Skill UI Component",
  "type": "object",
  "required": ["type", "name"],
  "properties": {
    "type": {
      "type": "string",
      "enum": [
        "text-display", "file-viewer",
        "skill-form", "skill-canvas",
        "skill-card", "skill-grid", "skill-list",
        "skill-chart", "skill-map", "skill-calendar",
        "skill-gallery", "skill-tree",
        "skill-dialog", "skill-panel", "skill-tabs",
        "skill-steps", "skill-timeline"
      ]
    },
    "name": {
      "type": "string"
    },
    "props": {
      "type": "object",
      "description": "组件属性"
    },
    "data": {
      "type": "object",
      "description": "组件数据"
    },
    "events": {
      "type": "array",
      "items": {
        "$ref": "#/definitions/ComponentEvent"
      }
    },
    "slots": {
      "type": "object",
      "description": "插槽内容"
    },
    "style": {
      "type": "object",
      "description": "自定义样式"
    }
  },
  "definitions": {
    "ComponentEvent": {
      "type": "object",
      "properties": {
        "name": {
          "type": "string"
        },
        "action": {
          "type": "string",
          "enum": ["submit", "navigate", "api", "emit", "close"]
        },
        "target": {
          "type": "string"
        },
        "params": {
          "type": "object"
        }
      }
    }
  }
}
```

### 4.3 具体组件定义

#### 4.3.1 skill-card（卡片）

```json
{
  "type": "skill-card",
  "name": "weather-card",
  "props": {
    "title": "天气信息",
    "icon": "ri-sun-line",
    "bordered": true,
    "hoverable": true,
    "size": "default"
  },
  "data": {
    "city": "北京",
    "temperature": 25,
    "condition": "sunny",
    "humidity": 60,
    "windSpeed": 10
  },
  "slots": {
    "header": {
      "type": "text-display",
      "props": {
        "content": "{{city}} - {{condition}}"
      }
    },
    "body": {
      "type": "skill-canvas",
      "props": {
        "template": "weather-display"
      }
    },
    "footer": {
      "type": "text-display",
      "props": {
        "content": "更新时间: {{updateTime}}"
      }
    }
  },
  "events": [
    {
      "name": "click",
      "action": "navigate",
      "target": "/detail/{{city}}"
    },
    {
      "name": "refresh",
      "action": "api",
      "target": "/api/weather/refresh",
      "params": {
        "city": "{{city}}"
      }
    }
  ]
}
```

#### 4.3.2 skill-form（表单）

```json
{
  "type": "skill-form",
  "name": "weather-search",
  "props": {
    "layout": "horizontal",
    "labelWidth": 100,
    "submitText": "查询",
    "resetText": "重置"
  },
  "data": {
    "fields": [
      {
        "name": "city",
        "type": "select",
        "label": "城市",
        "required": true,
        "source": {
          "type": "skill-api",
          "api": "/api/cities",
          "valueField": "id",
          "labelField": "name"
        },
        "placeholder": "请选择城市"
      },
      {
        "name": "dateRange",
        "type": "date-range",
        "label": "日期范围",
        "required": false,
        "defaultValue": ["today", "+7days"]
      },
      {
        "name": "forecastType",
        "type": "radio",
        "label": "预报类型",
        "options": [
          { "value": "today", "label": "今日" },
          { "value": "week", "label": "一周" },
          { "value": "month", "label": "一月" }
        ],
        "defaultValue": "today"
      }
    ]
  },
  "events": [
    {
      "name": "submit",
      "action": "submit",
      "target": "/api/weather/search"
    },
    {
      "name": "reset",
      "action": "emit",
      "target": "form-reset"
    }
  ]
}
```

#### 4.3.3 skill-grid（数据表格）

```json
{
  "type": "skill-grid",
  "name": "weather-grid",
  "props": {
    "bordered": true,
    "striped": true,
    "hover": true,
    "pagination": {
      "enabled": true,
      "pageSize": 10,
      "pageSizeOptions": [10, 20, 50]
    },
    "sortable": true,
    "filterable": true,
    "selectable": false
  },
  "data": {
    "columns": [
      {
        "field": "city",
        "title": "城市",
        "width": 120,
        "sortable": true,
        "filterable": true
      },
      {
        "field": "temperature",
        "title": "温度",
        "width": 100,
        "align": "right",
        "formatter": {
          "type": "number",
          "suffix": "°C"
        }
      },
      {
        "field": "condition",
        "title": "天气状况",
        "component": "status-badge",
        "mappings": {
          "sunny": { "text": "晴", "color": "warning", "icon": "ri-sun-line" },
          "cloudy": { "text": "多云", "color": "info", "icon": "ri-cloud-line" },
          "rainy": { "text": "雨", "color": "primary", "icon": "ri-rainy-line" }
        }
      },
      {
        "field": "humidity",
        "title": "湿度",
        "formatter": {
          "type": "progress",
          "max": 100,
          "unit": "%"
        }
      },
      {
        "field": "actions",
        "title": "操作",
        "component": "action-buttons",
        "buttons": [
          { "type": "primary", "icon": "ri-eye-line", "action": "view" },
          { "type": "default", "icon": "ri-refresh-line", "action": "refresh" }
        ]
      }
    ],
    "dataSource": "/api/weather/list"
  },
  "events": [
    {
      "name": "rowClick",
      "action": "navigate",
      "target": "/detail/{{city}}"
    },
    {
      "name": "sort",
      "action": "api",
      "target": "/api/weather/list"
    }
  ]
}
```

#### 4.3.4 skill-chart（图表）

```json
{
  "type": "skill-chart",
  "name": "temperature-chart",
  "props": {
    "chartType": "line",
    "title": "温度趋势",
    "xAxis": {
      "field": "date",
      "title": "日期"
    },
    "yAxis": {
      "field": "temperature",
      "title": "温度 (°C)"
    },
    "series": [
      {
        "name": "最高温度",
        "field": "maxTemp",
        "color": "#ef4444"
      },
      {
        "name": "最低温度",
        "field": "minTemp",
        "color": "#3b82f6"
      }
    ],
    "tooltip": {
      "enabled": true,
      "formatter": "{seriesName}: {value}°C"
    },
    "legend": {
      "enabled": true,
      "position": "top"
    }
  },
  "data": {
    "source": "/api/weather/trend"
  }
}
```

---

## 5. Nexus 前端集成

### 5.1 HTML 模板规范

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{{skillCard.name}}</title>
    
    <!-- Nexus 核心资源 -->
    <link rel="stylesheet" href="https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css">
    
    <!-- Skill UI Runtime -->
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/skill-ui-runtime.js"></script>
</head>
<body>
    <div class="nx-page">
        <aside class="nx-page__sidebar" id="sidebar"></aside>
        
        <main class="nx-page__content">
            <header class="nx-page__header">
                <h1 class="nx-page__title">
                    <i class="{{skillCard.capabilities.ui.components[0].icon}}"></i>
                    {{skillCard.name}}
                </h1>
            </header>
            
            <div class="nx-page__main">
                <div class="nx-container">
                    <!-- Skill UI 容器 -->
                    <div id="skill-ui-container" 
                         data-skill-id="{{skillId}}"
                         data-task-id="{{taskId}}">
                        <!-- 动态渲染 Skill UI -->
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <script>
        // 初始化 Skill UI Runtime
        const skillRuntime = new SkillUIRuntime({
            skillCard: {{skillCard | json}},
            taskId: '{{taskId}}',
            sessionId: '{{sessionId}}',
            container: document.getElementById('skill-ui-container')
        });
        
        // 启动 Skill UI
        skillRuntime.start();
    </script>
</body>
</html>
```

### 5.2 JavaScript Runtime

```javascript
// Skill UI Runtime 核心类
class SkillUIRuntime {
    constructor(config) {
        this.skillCard = config.skillCard;
        this.taskId = config.taskId;
        this.sessionId = config.sessionId;
        this.container = config.container;
        this.task = null;
    }
    
    async start() {
        // 1. 创建 Task
        this.task = await this.createTask();
        
        // 2. 根据 Task 输出渲染 UI
        await this.renderUI();
        
        // 3. 绑定事件
        this.bindEvents();
    }
    
    async createTask() {
        const response = await fetch('/api/a2a/tasks/send', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id: this.taskId,
                sessionId: this.sessionId,
                skillId: this.skillCard.name,
                input: {
                    mode: 'ui',
                    parts: [{
                        type: 'ui',
                        ui: {
                            component: 'skill-panel',
                            props: { skillCard: this.skillCard }
                        }
                    }]
                }
            })
        });
        
        return response.json();
    }
    
    async renderUI() {
        const uiConfig = this.task.ui;
        
        // 使用 Component Factory 渲染组件
        const component = ComponentFactory.create(uiConfig.component, {
            props: uiConfig.config,
            data: uiConfig.parts,
            container: this.container
        });
        
        await component.render();
    }
    
    bindEvents() {
        // 监听 Task 状态变化（SSE）
        const eventSource = new EventSource(
            `/api/a2a/tasks/${this.taskId}/subscribe`
        );
        
        eventSource.onmessage = (event) => {
            const update = JSON.parse(event.data);
            this.handleTaskUpdate(update);
        };
    }
    
    handleTaskUpdate(update) {
        switch (update.state) {
            case 'working':
                this.showLoading();
                break;
            case 'input-required':
                this.showInputForm(update.input);
                break;
            case 'completed':
                this.updateUI(update.output);
                break;
            case 'failed':
                this.showError(update.error);
                break;
        }
    }
}

// 组件工厂
class ComponentFactory {
    static create(type, config) {
        const componentMap = {
            'skill-card': SkillCardComponent,
            'skill-form': SkillFormComponent,
            'skill-grid': SkillGridComponent,
            'skill-chart': SkillChartComponent,
            // ... 其他组件
        };
        
        const ComponentClass = componentMap[type];
        if (!ComponentClass) {
            throw new Error(`Unknown component type: ${type}`);
        }
        
        return new ComponentClass(config);
    }
}
```

---

## 6. API 规范（A2A 兼容）

### 6.1 核心端点

```yaml
openapi: 3.0.0
info:
  title: Skill UI A2A API
  version: 1.0.0

paths:
  # Agent Card 发现
  /.well-known/skill.json:
    get:
      summary: 获取 Skill Card
      responses:
        '200':
          description: Skill Card
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SkillCard'

  # Task 管理
  /api/a2a/tasks/send:
    post:
      summary: 发送 Task
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/Task'
      responses:
        '200':
          description: Task 创建成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Task'

  /api/a2a/tasks/{taskId}:
    get:
      summary: 获取 Task 状态
      parameters:
        - name: taskId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Task 详情
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Task'

  /api/a2a/tasks/{taskId}/subscribe:
    get:
      summary: 订阅 Task 更新（SSE）
      parameters:
        - name: taskId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: SSE 流
          content:
            text/event-stream:
              schema:
                type: string

  /api/a2a/tasks/{taskId}/cancel:
    post:
      summary: 取消 Task
      parameters:
        - name: taskId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 取消成功

components:
  schemas:
    SkillCard:
      $ref: 'https://ooder.net/schemas/skill-card.json'
    
    Task:
      $ref: 'https://ooder.net/schemas/skill-task.json'
```

---

## 7. 使用示例

### 7.1 天气预报 Skill 完整示例

```yaml
# skill-weather/skill.yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-weather
  name: Weather Skill
  version: 1.0.0
  description: 全球城市天气预报查询
  
spec:
  type: tool-skill
  
  # A2A Skill Card 配置
  a2a:
    card:
      name: Weather Skill
      description: 提供全球城市天气预报查询
      url: https://skills.ooder.net/weather
      version: "1.0.0"
      authentication:
        schemes: ["apiKey"]
      defaultInputModes: ["text", "form"]
      defaultOutputModes: ["text", "ui"]
      capabilities:
        streaming: false
        pushNotifications: true
        ui:
          enabled: true
          components:
            - type: skill-card
              name: weather-card
              description: 天气信息卡片
            - type: skill-form
              name: weather-search
              description: 天气查询表单
            - type: skill-grid
              name: weather-grid
              description: 多城市天气对比
          interactionModes: ["form", "panel"]
      skills:
        - id: get-current-weather
          name: 获取当前天气
          description: 获取指定城市的当前天气信息
          tags: ["weather", "current"]
          examples:
            - "北京今天天气怎么样？"
            - "查询上海的温度"
          inputModes: ["text", "form"]
          outputModes: ["text", "ui"]
        - id: get-forecast
          name: 获取天气预报
          description: 获取未来7天天气预报
          tags: ["weather", "forecast"]
          examples:
            - "北京未来一周天气"
          inputModes: ["text", "form"]
          outputModes: ["ui"]

  # Nexus UI 配置
  ui:
    enabled: true
    entry:
      template: index.html
      title: 天气预报
      icon: ri-sun-cloudy-line
    menu:
      position: sidebar
      category: tools
      order: 100
```

```json
// skill-weather/ui/skill-card.json
{
  "type": "skill-card",
  "name": "weather-card",
  "props": {
    "title": "{{city}}天气",
    "icon": "ri-sun-line",
    "theme": "auto"
  },
  "slots": {
    "body": {
      "type": "skill-canvas",
      "template": "weather-display"
    }
  },
  "data": {
    "schema": {
      "type": "object",
      "properties": {
        "city": { "type": "string" },
        "temperature": { "type": "number" },
        "condition": { 
          "type": "string",
          "enum": ["sunny", "cloudy", "rainy", "snowy"]
        }
      }
    }
  }
}
```

---

## 8. 总结

### 8.1 核心设计

1. **A2A 兼容**：完全遵循 Google A2A 协议规范
   - Skill Card = Agent Card
   - UI Task = A2A Task
   - UI Part = A2A Part

2. **Nexus 集成**：使用 Nexus 组件类和样式
   - CSS 变量：`--ns-*`
   - 图标：Remix Icon
   - 组件类：`nx-card`, `nx-btn` 等

3. **组件化**：18 种标准 UI 组件
   - 展示：card, list, gallery, tree
   - 数据：grid, table, chart, map
   - 输入：form, canvas
   - 容器：dialog, panel, tabs, steps, timeline

4. **声明式配置**：JSON Schema 定义组件
   - 属性配置（props）
   - 数据绑定（data）
   - 事件处理（events）
   - 插槽内容（slots）

### 8.2 优势

- **标准化**：基于 Google A2A 开放协议
- **互操作性**：不同 Skill 可以无缝协作
- **LLM 友好**：结构化 JSON，易于 AI 生成
- **渐进增强**：现有 Skill 可逐步添加 UI
- **生态开放**：任何 Skill 都可以暴露 UI 能力

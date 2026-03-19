# Ooder v2.3 正式规范文档

> **文档版本**: v1.0.0  
> **生效日期**: 2026-02-25  
> **状�?*: 正式生效  
> **适用范围**: Nexus团队、SDK团队、Skills团队

---

## 目录

1. [概述](#一概述)
2. [架构规范](#二架构规�?
3. [Ooder-A2A规范](#三ooder-a2a规范)
4. [Skill配置规范](#四skill配置规范)
5. [生命周期规范](#五生命周期规�?
6. [依赖管理规范](#六依赖管理规�?
7. [版本兼容性规范](#七版本兼容性规�?
8. [热插拔规范](#八热插拔规范)
9. [资源管理规范](#九资源管理规�?
10. [团队协作接口](#十团队协作接�?
11. [任务分配](#十一任务分配)
12. [里程碑计划](#十二里程碑计�?

---

## 一、概�?
### 1.1 文档目的

本文档是Ooder v2.3版本的正式技术规范，整合了三团队（Nexus、SDK、Skills）的所有决策和确认事项，作为开发实施的权威依据�?
### 1.2 决策确认汇�?
| 编号 | 争议�?| 决策方案 | 状�?|
|------|--------|----------|------|
| D1 | A2A规范来源 | **Ooder-A2A自定义规�?* | �?已确�?|
| D2 | 前端技术栈 | **原生JavaScript + Web Components** | �?已确�?|
| D3 | PluginManager定位 | **Facade门面模式** | �?已确�?|
| D4 | 状态枚举定�?| **文档定义为主，兼容现�?* | �?已确�?|
| D5 | CSS变量策略 | **三层变量架构** | �?已确�?|
| D6 | 极复杂页面方�?| **分层策略（A/B/C方案�?* | �?已确�?|

### 1.3 关键约束

- **Java版本**: 必须兼容Java 8
- **浏览器支�?*: Chrome 53+, Firefox 63+, Safari 10+, Edge 79+ (IE 11降级到iframe)
- **命名空间**: 统一使用`ooder-a2a-*`前缀

---

## 二、架构规�?
### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────�?�?                       Nexus 前端�?                             �?�? ┌─────────────�? ┌─────────────�? ┌─────────────────────────�?�?�? �?主题系统     �? �?组件�?      �? �?Skill UI渲染引擎         �?�?�? �?(CSS变量)   �? �?Web Components)�? �?(Shadow DOM/iframe)     �?�?�? └─────────────�? └─────────────�? └─────────────────────────�?�?└─────────────────────────────────────────────────────────────────�?                              �?                              �?┌─────────────────────────────────────────────────────────────────�?�?                       SDK 服务�?                               �?�? ┌─────────────────────────────────────────────────────────�?  �?�? �?                   PluginManager (Facade)                �?  �?�? �? ┌──────────�?┌──────────�?┌──────────�?┌──────────�?  �?  �?�? �? │SkillManager�?│PackageManager�?│LifecycleManager�?│ClassLoaderManager�?�?  �?�? �? └──────────�?└──────────�?└──────────�?└──────────�?  �?  �?�? └─────────────────────────────────────────────────────────�?  �?�? ┌─────────────�? ┌─────────────�? ┌─────────────────────────�?�?�? �?发现服务     �? �?版本管理     �? �?依赖解析�?              �?�?�? └─────────────�? └─────────────�? └─────────────────────────�?�?└─────────────────────────────────────────────────────────────────�?                              �?                              �?┌─────────────────────────────────────────────────────────────────�?�?                       Skills 业务�?                            �?�? ┌─────────────�? ┌─────────────�? ┌─────────────────────────�?�?�? �?页面转换     �? �?组件映射     �? �?能力声明                 �?�?�? └─────────────�? └─────────────�? └─────────────────────────�?�?└─────────────────────────────────────────────────────────────────�?```

### 2.2 分层职责

| 层级 | 团队 | 核心职责 |
|------|------|----------|
| 前端展示�?| Nexus | CSS变量、Web Components、主题系统、Skill UI渲染 |
| 生命周期�?| SDK | Skill生命周期管理、热插拔、发现机制、依赖解�?|
| 业务逻辑�?| Skills | 页面分析、转换实施、组件映射、总体协调 |

---

## 三、Ooder-A2A规范

### 3.1 规范定义

**命名空间**: `ooder-a2a`  
**版本**: `1.0.0`  
**基础**: 参考Google A2A草案 + Ooder实际需�?
### 3.2 CSS变量规范

#### 3.2.1 三层变量架构

```css
/* ========================================
   第一�? Ooder-A2A标准变量 (规范�?
   ======================================== */
:root {
  /* 主色�?*/
  --ooder-a2a-color-primary: #1976d2;
  --ooder-a2a-color-secondary: #dc004e;
  --ooder-a2a-color-success: #4caf50;
  --ooder-a2a-color-warning: #ff9800;
  --ooder-a2a-color-error: #f44336;
  
  /* 中性色 */
  --ooder-a2a-color-white: #ffffff;
  --ooder-a2a-color-black: #000000;
  --ooder-a2a-color-gray-100: #f5f5f5;
  --ooder-a2a-color-gray-200: #eeeeee;
  --ooder-a2a-color-gray-300: #e0e0e0;
  --ooder-a2a-color-gray-400: #bdbdbd;
  --ooder-a2a-color-gray-500: #9e9e9e;
  --ooder-a2a-color-gray-600: #757575;
  --ooder-a2a-color-gray-700: #616161;
  --ooder-a2a-color-gray-800: #424242;
  --ooder-a2a-color-gray-900: #212121;
  
  /* 间距 */
  --ooder-a2a-spacing-unit: 8px;
  --ooder-a2a-spacing-xs: calc(var(--ooder-a2a-spacing-unit) * 0.5);
  --ooder-a2a-spacing-sm: var(--ooder-a2a-spacing-unit);
  --ooder-a2a-spacing-md: calc(var(--ooder-a2a-spacing-unit) * 2);
  --ooder-a2a-spacing-lg: calc(var(--ooder-a2a-spacing-unit) * 3);
  --ooder-a2a-spacing-xl: calc(var(--ooder-a2a-spacing-unit) * 4);
  
  /* 圆角 */
  --ooder-a2a-border-radius-sm: 2px;
  --ooder-a2a-border-radius-md: 4px;
  --ooder-a2a-border-radius-lg: 8px;
  
  /* 字体 */
  --ooder-a2a-font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  --ooder-a2a-font-size-xs: 12px;
  --ooder-a2a-font-size-sm: 14px;
  --ooder-a2a-font-size-md: 16px;
  --ooder-a2a-font-size-lg: 20px;
  --ooder-a2a-font-size-xl: 24px;
  
  /* 阴影 */
  --ooder-a2a-shadow-sm: 0 1px 2px rgba(0,0,0,0.05);
  --ooder-a2a-shadow-md: 0 4px 6px rgba(0,0,0,0.1);
  --ooder-a2a-shadow-lg: 0 10px 15px rgba(0,0,0,0.1);
}

/* ========================================
   第二�? Ooder品牌变量 (品牌�?
   ======================================== */
:root {
  --ooder-primary: var(--ooder-a2a-color-primary);
  --ooder-secondary: var(--ooder-a2a-color-secondary);
  --ooder-success: var(--ooder-a2a-color-success);
  --ooder-warning: var(--ooder-a2a-color-warning);
  --ooder-error: var(--ooder-a2a-color-error);
}

/* ========================================
   第三�? Nexus兼容�?(兼容�?
   ======================================== */
:root {
  --nexus-primary: var(--ooder-primary);
  --nx-primary: var(--ooder-primary);
  --ns-primary: var(--ooder-primary);
  --nexus-color-primary: var(--ooder-primary);
  --nx-color-primary: var(--ooder-primary);
}
```

#### 3.2.2 暗黑模式变量

```css
[data-theme="dark"] {
  --ooder-a2a-color-primary: #64b5f6;
  --ooder-a2a-color-background: #121212;
  --ooder-a2a-color-surface: #1e1e1e;
  --ooder-a2a-color-text-primary: #ffffff;
  --ooder-a2a-color-text-secondary: rgba(255,255,255,0.7);
}
```

### 3.3 Web Components规范

#### 3.3.1 组件命名

所有组件必须使用`ooder-a2a-`前缀�?
| 组件类型 | 组件�?| 说明 |
|----------|--------|------|
| 按钮 | `ooder-a2a-button` | 基础按钮组件 |
| 输入�?| `ooder-a2a-input` | 文本输入组件 |
| 选择�?| `ooder-a2a-select` | 下拉选择组件 |
| 表格 | `ooder-a2a-table` | 数据表格组件 |
| 对话�?| `ooder-a2a-dialog` | 模态对话框组件 |
| 卡片 | `ooder-a2a-card` | 信息卡片组件 |
| 图标 | `ooder-a2a-icon` | 图标组件 |
| 表单 | `ooder-a2a-form` | 表单容器组件 |

#### 3.3.2 组件实现模板

```javascript
/**
 * Ooder A2A Button Component
 * @extends HTMLElement
 */
class OoderA2AButton extends HTMLElement {
  static get observedAttributes() {
    return ['type', 'size', 'disabled', 'loading'];
  }

  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
    this._type = 'default';
    this._size = 'medium';
    this._disabled = false;
    this._loading = false;
  }

  connectedCallback() {
    this.render();
    this.addEventListeners();
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (oldValue !== newValue) {
      this[`_${name}`] = newValue;
      this.render();
    }
  }

  render() {
    this.shadowRoot.innerHTML = `
      <style>
        :host {
          display: inline-block;
          --btn-color: var(--ooder-a2a-color-primary, #1976d2);
          --btn-text-color: var(--ooder-a2a-color-white, #ffffff);
        }
        
        button {
          padding: var(--ooder-a2a-spacing-sm) var(--ooder-a2a-spacing-md);
          border: none;
          border-radius: var(--ooder-a2a-border-radius-md);
          background: var(--btn-color);
          color: var(--btn-text-color);
          font-family: var(--ooder-a2a-font-family);
          font-size: var(--ooder-a2a-font-size-sm);
          cursor: pointer;
          transition: all 0.2s;
        }
        
        button:hover {
          opacity: 0.8;
        }
        
        button:disabled {
          opacity: 0.5;
          cursor: not-allowed;
        }
        
        button.loading::after {
          content: '';
          display: inline-block;
          width: 12px;
          height: 12px;
          margin-left: 8px;
          border: 2px solid transparent;
          border-top-color: currentColor;
          border-radius: 50%;
          animation: spin 1s linear infinite;
        }
        
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      </style>
      <button 
        class="${this._loading ? 'loading' : ''}"
        ?disabled="${this._disabled || this._loading}"
      >
        <slot></slot>
      </button>
    `;
  }

  addEventListeners() {
    const button = this.shadowRoot.querySelector('button');
    button.addEventListener('click', (e) => {
      if (!this._disabled && !this._loading) {
        this.dispatchEvent(new CustomEvent('a2a-click', {
          bubbles: true,
          composed: true,
          detail: { type: this._type }
        }));
      }
    });
  }
}

customElements.define('ooder-a2a-button', OoderA2AButton);
```

### 3.4 消息类型规范

| 消息类型 | 方向 | 用�?| 格式 |
|----------|------|------|------|
| `task_send` | C→S | 发送任�?| JSON |
| `task_get` | C→S | 获取任务 | JSON |
| `task_resubscribe` | C→S | 重新订阅 | JSON |
| `task_cancel` | C→S | 取消任务 | JSON |
| `skill_card` | S→C | Skill卡片 | JSON |
| `state_change` | S→C | 状态变�?| JSON |
| `config_update` | C→S | 配置更新 | JSON |

---

## 四、Skill配置规范

### 4.1 skill.yaml完整规范

```yaml
# skill.yaml - Skill配置规范 v1.0
# 位置: skill/src/main/resources/skill.yaml

# ==================== 基础信息 ====================
skill:
  # 唯一标识符，格式: reverse-domain.name
  # 示例: com.ooder.skills.data-sync
  id: com.ooder.skills.example
  
  # 显示名称 (支持多语言)
  name:
    zh_CN: "示例Skill"
    en_US: "Example Skill"
  
  # 版本号，遵循SemVer规范
  version: 1.2.3
  
  # 描述信息 (支持多语言)
  description:
    zh_CN: "这是一个示例Skill，用于演示配置规�?
    en_US: "This is an example skill for demonstrating config spec"
  
  # 作者信�?  author:
    name: "Ooder Team"
    email: "team@ooder.cn"
    url: "https://gitee.com/ooderCN"
  
  # 许可�?  license: "Apache-2.0"
  
  # Skill分类
  category: "data-processing"  # 可�? data-processing, visualization, automation, integration
  
  # 标签，用于搜索和过滤
  tags:
    - "example"
    - "demo"
    - "data"

# ==================== 依赖声明 ====================
dependencies:
  # Skill依赖
  skills:
    - id: "com.ooder.skills.base"
      version: "^1.0.0"  # ^表示兼容1.x.x
      optional: false     # 是否可选依�?    
    - id: "com.ooder.skills.database"
      version: ">=2.0.0 <3.0.0"
      optional: true
  
  # 系统依赖 (检查环�?
  system:
    java: ">=1.8"           # Java版本要求
    memory: ">=512MB"       # 内存要求
    disk: ">=100MB"         # 磁盘空间要求
  
  # 外部服务依赖
  services:
    - name: "redis"
      required: false
      checkEndpoint: "/health"

# ==================== 生命周期配置 ====================
lifecycle:
  # 启动顺序优先�?(0-100, 数字越小越先启动)
  startupOrder: 50
  
  # 启动超时时间(毫秒)
  startupTimeout: 30000
  
  # 停止超时时间(毫秒)
  shutdownTimeout: 10000
  
  # 健康检查配�?  healthCheck:
    enabled: true
    interval: 30000        # 检查间�?    timeout: 5000          # 超时时间
    retries: 3             # 重试次数
    endpoint: "/health"    # 健康检查端�?  
  # 自动重启策略
  restartPolicy:
    enabled: true
    maxRetries: 3
    backoffMultiplier: 2   # 退避倍数

# ==================== 路由配置 (A2A协议) ====================
routing:
  # 基础路径
  basePath: "/api/v1/skills/${skill.id}"
  
  # 端点定义
  endpoints:
    # Agent Card端点 (必须)
    - path: "/agent-card"
      method: "GET"
      handler: "agentCardHandler"
      description: "返回Agent Card信息"
    
    # Tasks端点 (必须)
    - path: "/tasks"
      method: "POST"
      handler: "taskHandler"
      description: "处理任务请求"
    
    # 自定义端�?    - path: "/custom/action"
      method: "POST"
      handler: "customActionHandler"
      auth: true              # 需要认�?      rateLimit: "100/min"    # 限流配置

# ==================== 服务配置 ====================
services:
  # 暴露的服�?  exports:
    - name: "DataSyncService"
      interface: "com.ooder.skills.example.DataSync"
      version: "1.0.0"
  
  # 引用的服�?  imports:
    - name: "DatabaseService"
      required: true

# ==================== UI配置 ====================
ui:
  # 是否提供UI
  enabled: true
  
  # 入口�?(Web Component)
  entry: "/ui/skill-example.js"
  
  # 组件标签�?  tagName: "skill-example-ui"
  
  # 默认尺寸
  defaultSize:
    width: "800px"
    height: "600px"
  
  # 图标
  icon: "/ui/assets/icon.svg"
  
  # 主题适配
  theming:
    cssVariables: true      # 支持CSS变量
    darkMode: true          # 支持暗黑模式

# ==================== 能力声明 (A2A) ====================
capabilities:
  # 支持的能力类�?  types:
    - "task"                # 任务处理
    - "notification"        # 通知
    - "streaming"           # 流式响应
  
  # 输入/输出格式
  formats:
    input: ["text", "json", "file"]
    output: ["text", "json", "file", "stream"]
  
  # 认证方式
  auth:
    - "none"
    - "apiKey"
    - "oauth2"

# ==================== 资源限制 ====================
resources:
  # CPU限制 (相对�?
  cpu:
    limit: 0.5              # 最多使�?0%单核
    priority: "normal"      # normal, high, low
  
  # 内存限制
  memory:
    max: "256MB"
    reserved: "64MB"
  
  # 线程限制
  threads:
    max: 20
  
  # 文件句柄限制
  fileHandles:
    max: 100

# ==================== 安全配置 ====================
security:
  # 权限声明
  permissions:
    - "network:read"
    - "filesystem:read"
    - "database:write"
  
  # 沙箱配置
  sandbox:
    enabled: true
    fileSystem: "restricted"  # none, restricted, full
    network: "whitelist"      # none, whitelist, full

# ==================== 扩展配置 ====================
extensions:
  # 自定义配置项
  custom:
    batchSize: 100
    retryTimes: 3
```

### 4.2 字段验证规则

| 字段 | 必填 | 格式 | 说明 |
|------|------|------|------|
| skill.id | �?| 反向域名格式 | 全局唯一标识 |
| skill.version | �?| SemVer | �?�?修订 |
| skill.name | �?| 对象 | 至少一种语言 |
| dependencies.skills | �?| 数组 | 依赖其他Skill |
| routing.endpoints | �?| 数组 | 至少包含agent-card |
| lifecycle.startupOrder | �?| 0-100 | 默认50 |
| ui.enabled | �?| boolean | 默认false |

### 4.3 JSON Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Skill Configuration",
  "type": "object",
  "required": ["skill"],
  "properties": {
    "skill": {
      "type": "object",
      "required": ["id", "version"],
      "properties": {
        "id": {
          "type": "string",
          "pattern": "^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$"
        },
        "version": {
          "type": "string",
          "pattern": "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)$"
        }
      }
    }
  }
}
```

---

## 五、生命周期规�?
### 5.1 状态定�?
```java
public enum SkillState {
    // 基础状�?    CREATED,        // 已创�?    
    // 安装阶段
    INSTALLING,     // 安装�?    INSTALLED,      // 已安�?    
    // 启动阶段
    STARTING,       // 启动�?    ACTIVE,         // 运行�?(替代RUNNING)
    
    // 停止阶段
    STOPPING,       // 停止�?    STOPPED,        // 已停�?    
    // 卸载阶段
    UNINSTALLING,   // 卸载�?    UNINSTALLED,    // 已卸�?    
    // 异常状�?    ERROR           // 错误
}
```

### 5.2 状态转换图

```
                    ┌─────────�?         ┌─────────�? CREATED �?         �?        └────┬────�?         �?             �?install()
         �?             �?         �?        ┌─────────�?    ┌─────────�?         �?   ┌───│INSTALLING│────�? ERROR  �?         �?   �?   └────┬────�?    └─────────�?         �?   �?        �?         �?   �?        �?         �?   �?   ┌─────────�?         �?   └───│INSTALLED �?         �?        └────┬────�?         �?             �?start()
         �?             �?         �?        ┌─────────�?    ┌─────────�?         �?   ┌───�?STARTING │────�? ERROR  �?         �?   �?   └────┬────�?    └─────────�?         �?   �?        �?         �?   �?        �?         �?   �?   ┌─────────�?         �?   └───�? ACTIVE  │◄──────�?         �?        └────┬────�?      �?         �?             �?stop()     �?start()
         �?             �?           �?         �?        ┌─────────�?      �?         �?   ┌───�?STOPPING │───────�?         �?   �?   └────┬────�?         �?   �?        �?         �?   �?        �?         �?   �?   ┌─────────�?         �?   └───�?STOPPED  �?         �?        └────┬────�?         �?             �?uninstall()
         �?             �?         �?        ┌─────────�?    ┌─────────�?         └────────│UNINSTALLING│───�? ERROR  �?                   └────┬────�?    └─────────�?                        �?                        �?                   ┌─────────�?                   │UNINSTALLED�?                   └─────────�?```

### 5.3 生命周期事件

```java
public enum SkillLifecycleEvent {
    // 文档定义 (必须实现)
    SKILL_INSTALLING,
    SKILL_INSTALLED,
    SKILL_STARTING,
    SKILL_STARTED,
    SKILL_STOPPING,
    SKILL_STOPPED,
    SKILL_UNINSTALLING,
    SKILL_UNINSTALLED,
    SKILL_ERROR,
    
    // 现有扩展 (保留兼容)
    SKILL_DISCOVERED,
    SKILL_METADATA_LOADED,
    SKILL_LOADING,
    SKILL_LOADED,
    SKILL_LOAD_FAILED,
    SKILL_INITIALIZING,
    SKILL_INITIALIZED,
    SKILL_INIT_FAILED,
    SKILL_STATUS_CHANGED,
    SKILL_IDLE,
    SKILL_RUNNING,
    SKILL_PAUSED,
    SKILL_UNLOADING,
    SKILL_UNLOADED,
    SKILL_RECOVERED
}
```

---

## 六、依赖管理规�?
### 6.1 依赖解析流程

```
┌─────────────────────────────────────────────────────────────�?�?                   依赖解析流程                              �?├─────────────────────────────────────────────────────────────�?�? 1. 解析声明 �?读取skill.yaml中的dependencies                 �?�? 2. 构建依赖�?�?创建有向无环�?DAG)                          �?�? 3. 版本冲突解决 �?应用解析策略                               �?�? 4. 循环依赖检�?�?检测并阻断循环依赖                          �?�? 5. 拓扑排序 �?确定加载顺序                                   �?�? 6. 按序安装 �?并行加载无依赖的Skill                          �?└─────────────────────────────────────────────────────────────�?```

### 6.2 版本约束语法

| 语法 | 含义 | 示例 |
|------|------|------|
| `1.2.3` | 精确版本 | 必须等于1.2.3 |
| `^1.2.3` | 兼容版本 | >=1.2.3 <2.0.0 |
| `~1.2.3` | 近似版本 | >=1.2.3 <1.3.0 |
| `>=1.0.0` | 最低版�?| 1.0.0及以�?|
| `>=1.0.0 <2.0.0` | 范围版本 | 1.x.x |
| `*` | 任意版本 | 最新版�?|

### 6.3 版本冲突解决策略

**策略优先�?* (从高到低):

1. **显式声明优先** - 用户显式指定的版本优�?2. **最高版本优�?* - 默认策略，选择满足约束的最高版�?3. **最早声明优�?* - 按声明顺序，先声明的优先

**配置方式**:
```yaml
# 在application.yml中配�?cooder:
  skills:
    dependency:
      resolution-strategy: "highest"  # highest, explicit, first
```

### 6.4 循环依赖处理

**检测机�?*:
```java
public class CircularDependencyDetector {
    public void detectCycle(DependencyGraph graph) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        for (String skillId : graph.getAllSkills()) {
            if (hasCycle(graph, skillId, visited, recursionStack)) {
                throw new CircularDependencyException(
                    "检测到循环依赖: " + skillId
                );
            }
        }
    }
}
```

**处理策略**:
1. **阻断模式** (默认) - 检测到循环依赖时抛出异常，阻止安装
2. **警告模式** - 记录警告日志，尝试打破循环（不推荐用于生产）

---

## 七、版本兼容性规�?
### 7.1 兼容性规�?(基于SemVer)

| 版本变化 | 兼容�?| 自动升级 | 说明 |
|----------|--------|----------|------|
| MAJOR (x.0.0) | �?不兼�?| �?禁止 | API可能破坏性变�?|
| MINOR (0.x.0) | �?向后兼容 | �?允许 | 新增功能，兼容旧�?|
| PATCH (0.0.x) | �?完全兼容 | �?自动 | 仅bug修复 |

### 7.2 兼容性矩�?
```
                    依赖方版�?                 1.0.0   1.1.0   2.0.0
               ┌───────┬───────┬───────�?    1.0.0      �? �?  �? �?  �? �?  �?被依赖方        ├───────┼───────┼───────�?    1.1.0      �? �?  �? �?  �? �?  �?               ├───────┼───────┼───────�?    2.0.0      �? �?  �? �?  �? �?  �?               └───────┴───────┴───────�?```

### 7.3 升级策略

```java
public class VersionUpgradeStrategy {
    
    public UpgradeDecision canUpgrade(String current, String target) {
        Version cur = Version.parse(current);
        Version tar = Version.parse(target);
        
        // PATCH级别: 自动升级
        if (cur.getMajor() == tar.getMajor() && 
            cur.getMinor() == tar.getMinor()) {
            return UpgradeDecision.AUTO;
        }
        
        // MINOR级别: 允许升级，需通知
        if (cur.getMajor() == tar.getMajor()) {
            return UpgradeDecision.ALLOWED;
        }
        
        // MAJOR级别: 禁止自动升级
        return UpgradeDecision.BLOCKED;
    }
}
```

### 7.4 降级策略

**允许降级场景**:
- PATCH级别降级: 始终允许 (回滚bug修复)
- MINOR级别降级: 需确认 (可能丢失功能)
- MAJOR级别降级: 禁止 (API不兼�?

---

## 八、热插拔规范

### 8.1 事务模型

采用**两阶段提�?2PC)**模型，确保原子�?

```
┌────────────────────────────────────────────────────────────────�?�?                     热插拔事务流�?                            �?├────────────────────────────────────────────────────────────────�?�?                                                                �?�? Phase 1: 准备阶段 (Prepare)                                    �?�? ┌─────────�?   ┌──────────�?   ┌──────────�?   ┌─────────�?  �?�? �?开始事�?�?�?�?资源预留  �?�?�?依赖检�? �?�?�?预加�?  �?  �?�? └─────────�?   └──────────�?   └──────────�?   └─────────�?  �?�?      �?                                             �?        �?�?      �?                                             �?        �?�? ┌─────────�?                                 ┌─────────�?    �?�? �?准备就绪 �?←─────────────────────────────── �?状态标记│     �?�? └─────────�?                                 └─────────�?    �?�?                                                                �?�? Phase 2: 提交阶段 (Commit)                                     �?�? ┌─────────�?   ┌──────────�?   ┌──────────�?   ┌─────────�?  �?�? �?提交事务 �?�?�?注册服务  �?�?�?启动组件  �?�?�?状态更�?�?  �?�? └─────────�?   └──────────�?   └──────────�?   └─────────�?  �?�?                                                                �?�? [失败时] Phase 2': 回滚阶段 (Rollback)                         �?�? ┌─────────�?   ┌──────────�?   ┌──────────�?   ┌─────────�?  �?�? �?回滚事务 �?�?�?释放资源  �?�?�?清理状�? �?�?�?通知失败 �?  �?�? └─────────�?   └──────────�?   └──────────�?   └─────────�?  �?�?                                                                �?└────────────────────────────────────────────────────────────────�?```

### 8.2 事务边界

**安装事务边界**:
```java
@Transactional(rollbackFor = HotPlugException.class)
public class SkillInstallationTransaction {
    
    // 事务包含以下操作:
    // 1. 文件解压到临时目�?    // 2. 验证skill.yaml完整�?    // 3. 检查依赖可用�?    // 4. 分配资源配额
    // 5. 注册到PluginManager
    // 6. 启动Skill生命周期
    // 7. 更新状态为RUNNING
    
    // 任一步骤失败，触发完整回�?}
```

**卸载事务边界**:
```java
@Transactional(rollbackFor = HotPlugException.class)
public class SkillUninstallationTransaction {
    
    // 事务包含以下操作:
    // 1. 停止Skill (优雅关闭)
    // 2. 注销服务
    // 3. 释放资源配额
    // 4. 从PluginManager移除
    // 5. 删除文件
    // 6. 更新状态为UNINSTALLED
}
```

### 8.3 补偿机制

**补偿操作�?*:

| 阶段 | 操作 | 补偿操作 |
|------|------|----------|
| 准备 | 文件解压 | 删除临时文件 |
| 准备 | 资源预留 | 释放资源配额 |
| 提交 | 服务注册 | 注销服务 |
| 提交 | 组件启动 | 停止组件 |

---

## 九、资源管理规�?
### 9.1 资源泄漏检�?(5级覆�?

| 级别 | 资源类型 | 检测方�?| 阈值配�?|
|------|----------|----------|----------|
| L1 | ClassLoader | 引用计数 | 卸载后引�?0告警 |
| L2 | 线程 | Thread.activeCount() | 僵尸线程>0告警 |
| L3 | 数据库连�?| DataSource监控 | 未关闭连�?0告警 |
| L4 | 文件句柄 | /proc/{pid}/fd | 句柄增长>10%/小时告警 |
| L5 | 内存映射 | MappedByteBuffer | 未释放映�?0告警 |

### 9.2 检测配�?
```yaml
# application.yml
cooder:
  skills:
    leak-detection:
      enabled: true
      check-interval: 30000  # 检测间�?ms)
      thresholds:
        classloader-refs: 0   # ClassLoader引用阈�?        zombie-threads: 0     # 僵尸线程阈�?        db-connections: 0     # 数据库连接阈�?        file-handles: 10      # 文件句柄增长阈�?        memory-mappings: 0    # 内存映射阈�?      actions:               # 发现泄漏后的动作
        - "log"              # 记录日志
        - "notify"           # 发送通知
        - "force-cleanup"    # 强制清理(谨慎使用)
```

### 9.3 资源限制

| 资源类型 | 默认限制 | 可配�?|
|----------|----------|--------|
| CPU | 50%单核 | �?|
| 内存 | 256MB | �?|
| 线程 | 20 | �?|
| 文件句柄 | 100 | �?|

---

## 十、团队协作接�?
### 10.1 三层接口协议

```
┌─────────────────────────────────────────────────────────────────�?�?                     三层协作接口                                �?├─────────────────────────────────────────────────────────────────�?�?                                                                 �?�? Layer 1: JavaScript API (Nexus UI �?Skill UI)                  �?�? ┌─────────────────────────────────────────────────────────�?  �?�? �? window.parent.postMessage()                            �?  �?�? �? - 主题切换通知                                          �?  �?�? �? - 布局调整通知                                          �?  �?�? �? - 消息传�?                                             �?  �?�? └─────────────────────────────────────────────────────────�?  �?�?                             �?                                  �?�? Layer 2: WebSocket (实时通信)                                   �?�? ┌─────────────────────────────────────────────────────────�?  �?�? �? /ws/skills/{skillId}                                   �?  �?�? �? - 状态变更推�?                                         �?  �?�? �? - 实时数据�?                                           �?  �?�? �? - 双向通知                                              �?  �?�? └─────────────────────────────────────────────────────────�?  �?�?                             �?                                  �?�? Layer 3: RESTful API (HTTP请求)                                �?�? ┌─────────────────────────────────────────────────────────�?  �?�? �? /api/v1/skills/{skillId}/...                           �?  �?�? �? - 配置管理                                              �?  �?�? �? - 生命周期控制                                          �?  �?�? �? - 数据查询                                              �?  �?�? └─────────────────────────────────────────────────────────�?  �?�?                                                                 �?└─────────────────────────────────────────────────────────────────�?```

### 10.2 Layer 1: JavaScript API

**Nexus �?Skill**:
```javascript
// 主题切换
parent.postMessage({
    type: 'THEME_CHANGE',
    data: {
        mode: 'dark',
        cssVariables: {
            '--primary-color': '#1890ff',
            '--bg-color': '#141414'
        }
    }
}, '*');

// 布局调整
parent.postMessage({
    type: 'LAYOUT_RESIZE',
    data: { width: 800, height: 600 }
}, '*');
```

**Skill �?Nexus**:
```javascript
// 高度自适应
window.parent.postMessage({
    type: 'RESIZE_REQUEST',
    data: { height: 500 }
}, '*');

// 打开对话�?window.parent.postMessage({
    type: 'OPEN_DIALOG',
    data: {
        title: '配置',
        url: '/skills/example/config',
        width: 600,
        height: 400
    }
}, '*');
```

### 10.3 Layer 2: WebSocket

**连接URL**: `wss://{host}/ws/skills/{skillId}?token={jwt}`

**消息格式**:
```json
{
    "type": "STATE_CHANGE",
    "timestamp": 1700000000000,
    "skillId": "com.ooder.skills.example",
    "data": {
        "from": "STARTING",
        "to": "RUNNING",
        "reason": "启动完成"
    }
}
```

### 10.4 Layer 3: RESTful API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/skills/{skillId}/state` | 获取状�?|
| POST | `/api/v1/skills/{skillId}/start` | 启动Skill |
| POST | `/api/v1/skills/{skillId}/stop` | 停止Skill |
| GET | `/api/v1/skills/{skillId}/config` | 获取配置 |
| PUT | `/api/v1/skills/{skillId}/config` | 更新配置 |
| GET | `/api/v1/skills/{skillId}/logs` | 获取日志 |
| GET | `/api/v1/skills/{skillId}/metrics` | 获取指标 |

---

## 十一、任务分�?
### 11.1 Nexus团队任务

| 优先�?| 任务ID | 任务名称 | 工期 | 依赖 |
|--------|--------|----------|------|------|
| P0 | N-1.1.1 | CSS变量系统重构 | 5�?| �?|
| P0 | N-1.1.2 | Remix Icon �?A2A Icon映射 | 3�?| N-1.1.1 |
| P0 | N-1.1.3 | 基础组件A2A封装 | 10�?| N-1.1.1 |
| P0 | N-1.2.1 | A2A组件渲染�?| 10�?| Skills规范文档 |
| P0 | N-1.2.2 | CDN资源加载�?| 5�?| �?|
| P0 | N-1.2.3 | Skill UI容器组件 | 5�?| N-1.2.1 |
| P1 | N-1.3.1 | 主题切换机制 | 5�?| N-1.1.1 |
| P1 | N-1.3.2 | 样式隔离方案 | 7�?| N-1.2.3 |

**总工�?*: 40�?
### 11.2 SDK团队任务

| 优先�?| 任务ID | 任务名称 | 工期 | 依赖 |
|--------|--------|----------|------|------|
| P0 | S-2.1.1 | 生命周期状态机完善 | 7�?| �?|
| P0 | S-2.1.2 | 生命周期事件系统 | 5�?| S-2.1.1 |
| P0 | S-2.2.1 | PluginManager Facade | 5�?| �?|
| P0 | S-2.2.2 | 类加载器优化 | 10�?| S-2.2.1 |
| P0 | S-2.2.3 | 资源泄漏防护 | 5�?| S-2.2.2 |
| P0 | S-2.3.1 | Skill发现服务 | 7�?| S-2.1.1 |
| P0 | S-2.3.2 | 版本管理 | 5�?| S-2.3.1 |
| P0 | S-2.3.3 | 依赖解析 | 5�?| S-2.3.1 |
| P1 | S-2.4.1 | A2A消息格式支持 | 7�?| �?等待Ooder-A2A规范v1.0 |
| P1 | S-2.4.2 | A2A能力声明 | 5�?| �?等待S-2.4.1完成 |

**总工�?*: 49�?
### 11.3 Skills团队任务

| 优先�?| 任务ID | 任务名称 | 工期 | 依赖 |
|--------|--------|----------|------|------|
| P0 | K-3.0.1 | Ooder-A2A规范文档 | 3�?| �?|
| P0 | K-3.1.1 | Nexus页面全面梳理 | 10�?| �?|
| P0 | K-3.1.2 | 组件映射表制�?| 5�?| K-3.0.1 |
| P0 | K-3.1.3 | 页面分类与优先级 | 3�?| K-3.1.1 |
| P0 | K-3.2.1 | 简单页面转�?4�? | 10�?| Nexus组件完成 |
| P0 | K-3.2.2 | 中等页面转换(4�? | 15�?| K-3.2.1 |
| P0 | K-3.2.3 | 复杂页面转换(3�? | 20�?| K-3.2.2 |
| P0 | K-3.2.4 | 极复杂页面方�?| 10�?| K-3.2.3 |
| P0 | K-3.3.1 | 三团队接口定�?| 5�?| Week 2 |
| P0 | K-3.3.2 | 集成测试计划 | 3�?| Week 6 |
| P1 | K-3.4.1 | Skill开发规�?| 5�?| K-3.0.1 |
| P1 | K-3.4.2 | 转换工具开�?| 10�?| K-3.2.1 |

**总工�?*: 84�?
---

## 十二、里程碑计划

### 12.1 里程碑时间表

| 里程�?| 时间 | 关键交付�?| 负责团队 |
|--------|------|------------|----------|
| **M1** | Week 1-2 | CSS变量系统、SkillState定义、页面清�?| 全部 |
| **M2** | Week 3-4 | 生命周期API、组件映射表、Ooder-A2A规范 | SDK+Skills |
| **M3** | Week 5-6 | 组件封装完成、简单页面转�?| Nexus+Skills |
| **M4** | Week 7-8 | 渲染引擎、中等页面转换、集成测�?| 全部 |
| **M5** | Week 9-10 | 复杂页面、极复杂页面方案、优�?| Skills |

### 12.2 检查点

| 检查点 | 时间 | 检查内�?| 通过标准 |
|--------|------|----------|----------|
| CP1 | Week 2结束 | 基础设施 | CSS变量+状态定�?页面清单完成 |
| CP2 | Week 4结束 | 核心能力 | 生命周期API+组件映射+规范发布 |
| CP3 | Week 6结束 | 组件完成 | 6个基础组件+简单页面转�?|
| CP4 | Week 8结束 | 集成验证 | 渲染引擎+中等页面+集成测试通过 |
| CP5 | Week 10结束 | 全面上线 | 所有页面转�?优化完成 |

---

## 附录

### A. 术语�?
| 术语 | 定义 |
|------|------|
| Ooder-A2A | Ooder自定义的A2A规范 |
| Web Components | W3C标准组件技�?|
| Facade模式 | 门面设计模式 |
| Shadow DOM | Web Components的样式隔离技�?|
| SemVer | 语义化版本控�?|

### B. 参考文�?
- [TEAM_TASKS_ALLOCATION.md](TEAM_TASKS_ALLOCATION.md) - 任务分配
- [SKILLS_TEAM_RESPONSE.md](SKILLS_TEAM_RESPONSE.md) - Skills团队答复
- [SKILLS_TEAM_RESPONSE_SUPPLEMENT.md](SKILLS_TEAM_RESPONSE_SUPPLEMENT.md) - 补充答复
- [COLLABORATION_TASK_INSTRUCTION.md](COLLABORATION_TASK_INSTRUCTION.md) - 协作说明

### C. 变更记录

| 版本 | 日期 | 变更内容 | 作�?|
|------|------|----------|------|
| v1.0.0 | 2026-02-25 | 初始版本，整合所有决策和答复 | Skills团队 |

---

**文档结束**

**生效日期**: 2026-02-25  
**下次评审**: Week 2结束

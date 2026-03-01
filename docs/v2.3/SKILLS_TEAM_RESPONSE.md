# Skills团队正式答复

**文档版本**: v1.0  
**创建日期**: 2026-02-25  
**答复对象**: Nexus团队 + SDK团队  
**状�?*: 正式生效

---

## 一、决策确�?(4个关键项)

| 编号 | 争议�?| 决策方案 | 决策依据 |
|------|--------|----------|----------|
| **Q1** | A2A规范来源 | **方案C: Ooder-A2A自定义规�?* | 以Google A2A草案为参考，结合Ooder实际需求定义，避免外部依赖 |
| **Q2** | 前端技术栈 | **方案A: 原生JavaScript + Web Components** | 保持技术栈一致性，无额外学习成本，符合Java 8兼容原则 |
| **Q3** | PluginManager定位 | **方案C: Facade门面模式** | PluginManager作为统一入口，封装现有Manager，不破坏现有架构 |
| **Q13** | 极复杂页面方�?| **分层策略** | 不同复杂度采用不同策略，灵活可控 |

---

## 二�?6个问题详细答�?
### 🔴 阻塞级问�?(3�?

#### Q1: A2A规范来源

**答复**: 采用**Ooder-A2A自定义规�?*

**规范定义**:
```
命名空间: ooder-a2a
版本: 1.0.0
基础: 参考Google A2A草案 + Ooder实际需�?```

**命名规则**:
- CSS变量: `--ooder-a2a-*` (�?`--ooder-a2a-color-primary`)
- 组件�? `ooder-a2a-*` (�?`ooder-a2a-button`)
- 消息类型: 保持文档定义 (�?`task_send`, `skill_card`)

**文档交付**: Skills团队将在3个工作日内提供《Ooder-A2A规范v1.0�?
---

#### Q2: 前端技术栈

**答复**: 采用**原生JavaScript + Web Components**

**技术选型理由**:
1. 与现有Nexus代码风格一�?2. 无需引入Vue构建工具�?3. Web Components是W3C标准，长期稳�?4. 支持Shadow DOM实现样式隔离

**组件实现方式**:
```javascript
// 原Vue方案 (废弃)
// A2AIconAdapter.vue

// 新方�?(采用)
class OoderA2AButton extends HTMLElement {
  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
  }
  
  connectedCallback() {
    this.render();
  }
  
  render() {
    this.shadowRoot.innerHTML = `
      <style>
        :host {
          --btn-color: var(--ooder-a2a-color-primary, #1976d2);
        }
        button {
          background: var(--btn-color);
        }
      </style>
      <button><slot></slot></button>
    `;
  }
}

customElements.define('ooder-a2a-button', OoderA2AButton);
```

**工期调整**: 任务1.1.3�?.2.1�?.2.3工期不变，因Web Components实现复杂度与Vue相当

---

#### Q3: PluginManager定位

**答复**: PluginManager作为**Facade门面模式**

**架构定位**:
```
PluginManager (Facade)
    ├── SkillManager (现有)
    ├── SkillPackageManager (现有)
    ├── SkillLifecycleManager (现有)
    └── ClassLoaderManager (新增)
```

**职责划分**:
| �?| 职责 | 状�?|
|----|------|------|
| PluginManager | 统一入口，协调各Manager | 新建 |
| SkillManager | Skill运行时管�?| 保留 |
| SkillPackageManager | 包安�?卸载/更新 | 保留 |
| SkillLifecycleManager | 生命周期状态管�?| 保留 |
| ClassLoaderManager | 类加载器管理 | 新建 |

**代码示例**:
```java
@Component
public class PluginManager {
    @Autowired
    private SkillManager skillManager;
    
    @Autowired
    private SkillPackageManager packageManager;
    
    @Autowired
    private SkillLifecycleManager lifecycleManager;
    
    @Autowired
    private ClassLoaderManager classLoaderManager;
    
    public PluginInstallResult install(SkillPackage pkg) {
        // 1. 包安�?        packageManager.install(pkg);
        
        // 2. 类加载器创建
        classLoaderManager.create(pkg);
        
        // 3. 生命周期启动
        lifecycleManager.start(pkg.getId());
        
        // 4. Skill注册
        skillManager.register(pkg.getId());
        
        return PluginInstallResult.success(pkg.getId());
    }
}
```

---

### 🟡 高优先级问题 (4�?

#### Q4: 状态枚举统一

**答复**: 采用**文档定义为主，兼容现�?*

**最终状态定�?*:
```java
public enum SkillState {
    // 基础状�?    CREATED,        // 已创�?    
    // 安装阶段
    INSTALLING,     // 安装�?    INSTALLED,      // 已安�?(新增)
    
    // 启动阶段
    STARTING,       // 启动�?    ACTIVE,         // 运行�?(替代RUNNING)
    
    // 停止阶段
    STOPPING,       // 停止�?    STOPPED,        // 已停�?    
    // 卸载阶段
    UNINSTALLING,   // 卸载�?    UNINSTALLED,    // 已卸�?(新增)
    
    // 异常状�?    ERROR           // 错误
}
```

**兼容性处�?*:
- `RUNNING` �?映射�?`ACTIVE`
- `FAILED` �?作为内部子状态保�?- `NOT_FOUND` �?作为内部子状态保�?
**迁移策略**:
1. 新建`SkillState`枚举
2. 现有`SkillStatus`标记为@Deprecated
3. 提供转换工具类`SkillStateConverter`

---

#### Q5: CSS变量映射策略

**答复**: 采用**三层变量架构**

**架构定义**:
```css
/* ========================================
   第一�? Ooder-A2A标准变量 (规范�?
   ======================================== */
:root {
  --ooder-a2a-color-primary: #1976d2;
  --ooder-a2a-color-secondary: #dc004e;
  --ooder-a2a-color-success: #4caf50;
  --ooder-a2a-color-warning: #ff9800;
  --ooder-a2a-color-error: #f44336;
  
  --ooder-a2a-spacing-unit: 8px;
  --ooder-a2a-border-radius: 4px;
  --ooder-a2a-font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
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
  
  /* 保持所有现有变量向后兼�?*/
  --nexus-color-primary: var(--ooder-primary);
  --nx-color-primary: var(--ooder-primary);
}
```

**实施步骤**:
1. Nexus团队创建`ooder-a2a-variables.css`
2. 逐步替换现有硬编码颜色�?3. 保持现有变量作为别名

---

#### Q14: 任务依赖关系

**答复**: 确认依赖关系，调整执行顺�?
**关键依赖�?*:
```
Week 1-2 (并行):
  ├─ SDK: SkillState定义
  ├─ Nexus: CSS变量系统
  └─ Skills: 页面清单

Week 3-4 (SDK优先):
  ├─ SDK: 生命周期API (阻塞)
  └─ Skills: 组件映射�?(依赖SDK状态定�?

Week 5-6 (Nexus优先):
  ├─ Nexus: 组件封装 (阻塞)
  └─ Skills: 简单页面转�?(依赖组件)

Week 7-8 (集成):
  ├─ Nexus: 渲染引擎
  ├─ SDK: 热插拔完�?  └─ Skills: 集成测试

Week 9-10 (收尾):
  └─ 全部: 复杂页面 + 优化
```

**协作机制**:
- 每周三下�?�? 三团队站�?- 阻塞问题24小时内必须响�?- 接口变更需提前3天通知

---

#### Q15: 团队人力配置

**答复**: 确认人力配置，支持团队内并行

**配置方案**:

| 团队 | 配置 | 并行策略 | 总工�?|
|------|------|----------|--------|
| **Nexus** | 2前端 + 1UI设计�?| CSS变量与组件并�?| 8�?|
| **SDK** | 2后端 + 1架构�?| 生命周期与热插拔并行 | 8�?|
| **Skills** | 1架构 + 2开�?+ 1测试 | 分析与转换并�?| 10�?|

**Skills团队工期较长原因**:
- 承担总体协调职责
- 页面转换工作量大 (120+页面)
- 包含测试和文档工�?
**优化措施**:
- Skills团队可提前开始页面分�?(Week 1)
- 简单页面转换与Nexus组件开发并�?- 引入自动化转换工具减少人工工作量

---

### 🟡 中优先级问题 (6�?

#### Q6: 生命周期事件类型

**答复**: 采用**文档定义 + 扩展现有**

**最终事件定�?*:
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

**实现策略**:
- 优先实现文档定义�?个事�?- 现有20个事件作为扩展保�?- 提供事件映射�?
---

#### Q7: 类加载器优化工期

**答复**: **接受调整，工期改�?0�?*

**调整原因**:
1. 从零实现PluginClassLoader
2. 需处理Java 8兼容�?3. 需实现类预加载和缓存机�?
**详细排期**:
| 阶段 | 工期 | 内容 |
|------|------|------|
| 基础实现 | 3�?| PluginClassLoader基础结构 |
| 隔离策略 | 2�?| 父子加载器优先级策略 |
| 缓存机制 | 2�?| 类缓存、资源缓�?|
| 预加�?| 2�?| 异步预加载、懒加载 |
| 测试优化 | 1�?| 单元测试、性能测试 |

---

#### Q8: A2A组件渲染器工�?
**答复**: **接受调整，工期改�?0�?*

**调整原因**:
1. 需先完成Ooder-A2A规范定义 (3�?
2. 规范确认后才能开始实�?3. 包含Web Components适配

**详细排期**:
| 阶段 | 工期 | 内容 |
|------|------|------|
| 规范确认 | 3�?| 等待Ooder-A2A规范文档 |
| 基础架构 | 3�?| 渲染器基础结构 |
| 组件实现 | 3�?| 6个基础组件渲染 |
| 测试优化 | 1�?| 单元测试、集成测�?|

---

#### Q9: 样式隔离方案工期

**答复**: **接受调整，工期改�?�?*

**调整原因**:
1. Shadow DOM需要浏览器兼容性处�?2. 需要降级方�?(iframe/CSS命名空间)

**实施方案**:
```javascript
class StyleIsolation {
  static createContainer(skillId, content) {
    // 检测Shadow DOM支持
    if (document.body.attachShadow) {
      // 使用Shadow DOM
      return this.createShadowContainer(skillId, content);
    } else {
      // 降级方案: iframe
      return this.createIframeContainer(skillId, content);
    }
  }
}
```

**浏览器支�?*:
- Chrome 53+
- Firefox 63+
- Safari 10+
- Edge 79+
- **IE 11**: 使用iframe降级

---

#### Q13: 极复杂页面方�?
**答复**: 采用**分层策略**

**页面分类与方�?*:

| 页面 | 复杂�?| 方案 | 说明 |
|------|--------|------|------|
| 流程设计�?| 极高 | 方案A | 保留原页面，Skill包装，iframe嵌入 |
| 报表设计�?| 极高 | 方案B | 功能拆分：设计器+预览+数据源，3个Skill协作 |
| 低代码平�?| 极高 | 方案C | 渐进式改造，先迁移配置页�?|
| 代码编辑�?| �?| 方案A | 保留Monaco Editor，Skill包装 |
| 数据大屏 | �?| 方案B | 拆分为：画布+组件�?数据�?|

**方案A - Skill包装**:
```yaml
id: skill-bpmn-designer
name: 流程设计�?type: wrapper
wrapper:
  type: iframe
  src: /legacy/bpmn-designer
  height: 100vh
```

**方案B - 功能拆分**:
```yaml
id: skill-report-suite
name: 报表套件
type: composite
children:
  - skill-report-designer
  - skill-report-preview
  - skill-report-datasource
```

**方案C - 渐进�?*:
- Phase 1: 迁移配置页面 (简�?
- Phase 2: 迁移组件选择�?(中等)
- Phase 3: 迁移画布 (复杂)

---

### 🟢 低优先级问题 (3�?

#### Q10: Java 8兼容�?
**答复**: **强制要求，所有代码必须兼容Java 8**

**检查清�?*:
- [ ] 不使用`var`关键�?- [ ] 不使用`List.of()` / `Set.of()` / `Map.of()`
- [ ] 不使用`Optional.isEmpty()` (Java 11)
- [ ] 不使用`String.strip()` (Java 11)
- [ ] 日期时间使用`java.util.Date`或ThreeTen Backport
- [ ] 接口默认方法需谨慎使用

**代码审查**: 所有PR必须通过Java 8兼容性检�?
---

#### Q11: Shadow DOM浏览器兼容�?
**答复**: **支持Chrome 53+, Firefox 63+, Safari 10+, Edge 79+**

**降级策略**:
- 现代浏览�? Shadow DOM
- IE 11: iframe隔离
- 检测方�? `document.body.attachShadow`

---

#### Q12: CDN资源冲突处理

**答复**: **版本隔离 + 加载失败降级**

**冲突处理策略**:
```javascript
class CDNResourceLoader {
  load(resource) {
    const key = `${resource.name}@${resource.version}`;
    
    // 1. 检查是否已加载不同版本
    if (this.loaded.has(resource.name)) {
      const loaded = this.loaded.get(resource.name);
      if (loaded.version !== resource.version) {
        // 版本冲突，使用iframe隔离
        return this.loadInIframe(resource);
      }
      return Promise.resolve(loaded);
    }
    
    // 2. 加载资源
    return this.doLoad(resource).catch(err => {
      // 3. 加载失败降级
      return this.loadFromMirror(resource);
    });
  }
}
```

**降级优先�?*:
1. 主CDN加载
2. 备用CDN加载
3. 本地缓存加载
4. 失败提示

---

## 三、修订后的任务分�?
### Nexus团队修订任务

| 原任�?| 修订内容 | 工期调整 |
|--------|----------|----------|
| 1.1.1 CSS变量系统 | 采用三层架构，Web Components适配 | 不变 |
| 1.1.2 Icon映射 | 改为Web Components实现 | 不变 |
| 1.1.3 基础组件 | 改为`ooder-a2a-*` Web Components | 不变 |
| 1.2.1 组件渲染�?| 等待规范文档，工�?0�?| +3�?|
| 1.2.2 CDN加载�?| 增加版本冲突处理 | 不变 |
| 1.2.3 UI容器 | Shadow DOM + iframe降级 | 不变 |
| 1.3.1 主题切换 | 基于CSS变量 | 不变 |
| 1.3.2 样式隔离 | Shadow DOM + 降级方案，工�?�?| +2�?|

**Nexus团队总工�?*: 38�?�?**40�?*

---

### SDK团队修订任务

| 原任�?| 修订内容 | 工期调整 |
|--------|----------|----------|
| 2.1.1 状态机 | 采用SkillState定义，兼容现�?| 不变 |
| 2.1.2 事件系统 | 文档定义9�?+ 现有20个扩�?| 不变 |
| 2.2.1 PluginManager | Facade模式，封装现有Manager | 不变 |
| 2.2.2 类加载器 | 工期10�?| +5�?|
| 2.2.3 资源泄漏 | 增加CleanupHook | 不变 |
| 2.3.1 发现服务 | 基于现有SkillDiscoveryService | 不变 |
| 2.3.2 版本管理 | 新增 | 不变 |
| 2.3.3 依赖解析 | 基于现有DependencyManager | 不变 |
| 2.4.1 A2A消息 | 基于Ooder-A2A规范 | 不变 |
| 2.4.2 能力声明 | 基于现有CapabilityRegistry | 不变 |

**SDK团队总工�?*: 44�?�?**49�?*

---

### Skills团队修订任务

| 原任�?| 修订内容 | 工期调整 |
|--------|----------|----------|
| 3.1.1 页面梳理 | 120+页面，按复杂度分�?| 不变 |
| 3.1.2 组件映射 | 基于Web Components | 不变 |
| 3.1.3 页面分类 | 简�?中等/复杂/极复�?| 不变 |
| 3.2.1 简单页�?| 4个页�?| 不变 |
| 3.2.2 中等页面 | 4个页�?| 不变 |
| 3.2.3 复杂页面 | 3个页�?| 不变 |
| 3.2.4 极复杂页�?| 分层策略，明确方�?| 不变 |
| 3.3.1 接口定义 | 三团队协作接�?| 不变 |
| 3.3.2 集成测试 | 基于各团队交付物 | 不变 |
| 3.3.3 进度协调 | 每周站会 | 不变 |
| 3.4.1 开发规�?| 包含Web Components规范 | 不变 |
| 3.4.2 转换工具 | 自动化提取组�?| 不变 |
| **新增** | Ooder-A2A规范文档 | +3�?|

**Skills团队总工�?*: 81�?�?**84�?* (含规范文�?

---

## 四、修订后的里程碑

| 里程�?| 时间 | 关键交付�?| 负责团队 |
|--------|------|------------|----------|
| **M1** | Week 1-2 | CSS变量系统、SkillState定义、页面清�?| 全部 |
| **M2** | Week 3-4 | 生命周期API、组件映射表、Ooder-A2A规范 | SDK+Skills |
| **M3** | Week 5-6 | 组件封装完成、简单页面转�?| Nexus+Skills |
| **M4** | Week 7-8 | 渲染引擎、中等页面转换、集成测�?| 全部 |
| **M5** | Week 9-10 | 复杂页面、极复杂页面方案、优�?| Skills |

**总工�?*: 10�?(�?周，延期2�?

---

## 五、立即行动项

### 本周�?(Week 1)

| 序号 | 任务 | 负责 | 交付时间 |
|------|------|------|----------|
| 1 | 提供Ooder-A2A规范v1.0草案 | Skills | 3个工作日�?|
| 2 | 创建CSS变量系统基础文件 | Nexus | Week 1结束 |
| 3 | 定义SkillState枚举 | SDK | Week 1结束 |
| 4 | 召开三团队协调会�?| Skills | Week 1周三 |
| 5 | 建立项目沟通群 | Skills | 立即 |

### 协作机制

**会议安排**:
- 每周�?15:00-16:00: 三团队站�?- 紧急问�? 随时@相关团队
- 阻塞问题: 24小时内必须响�?
**文档协作**:
- 规范文档: Git版本控制
- 接口定义: Swagger/OpenAPI
- 进度跟踪: 共享看板

---

## 六、附�?
### A. Ooder-A2A规范预览

**命名空间**: `ooder-a2a`

**CSS变量**:
```css
--ooder-a2a-color-primary: #1976d2;
--ooder-a2a-color-secondary: #dc004e;
--ooder-a2a-spacing-unit: 8px;
--ooder-a2a-border-radius: 4px;
```

**组件前缀**: `ooder-a2a-*`
- `ooder-a2a-button`
- `ooder-a2a-input`
- `ooder-a2a-select`
- `ooder-a2a-table`
- `ooder-a2a-dialog`
- `ooder-a2a-card`

**消息类型**:
- `task_send`
- `task_get`
- `task_resubscribe`
- `task_cancel`
- `skill_card`

### B. 联系方式

| 团队 | 负责�?| 联系方式 |
|------|--------|----------|
| Skills团队 | 架构�?| skills-arch@ooder.net |
| Nexus团队 | 前端�?| nexus-fe@ooder.net |
| SDK团队 | 后端�?| sdk-backend@ooder.net |

---

**文档结束**

**生效日期**: 2026-02-25  
**下次评审**: Week 2结束

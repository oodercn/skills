# BPM Designer 面板属性枚举对比分析报告

**分析日期**: 2026-04-08  
**分析范围**: 前端面板、XPDL标准、BPM Server后端、Swing客户端

---

## 📊 一、活动类型 (ActivityType) 对比

### 1.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
options: [
    { value: 'TASK', label: '任务' },
    { value: 'SERVICE', label: '服务' },
    { value: 'SCRIPT', label: '脚本' },
    { value: 'GATEWAY', label: '网关' },
    { value: 'SUBPROCESS', label: '子流程' }
]
```

### 1.2 XPDL标准定义
**文件**: `docs/bpm-spec/vol-06-yaml-standard/README.md`

| 枚举值 | 中文名 | 说明 | 前端支持 |
|--------|--------|------|---------|
| TASK | 任务 | 用户任务 | ✅ |
| SERVICE | 服务 | 系统服务 | ✅ |
| SCRIPT | 脚本 | 脚本执行 | ✅ |
| GATEWAY | 网关 | 路由网关 | ✅ |
| SUBPROCESS | 子流程 | 子流程调用 | ✅ |
| START | 开始节点 | 流程开始 | ❌ 缺失 |
| END | 结束节点 | 流程结束 | ❌ 缺失 |
| XOR_GATEWAY | 异或网关 | 排他网关 | ❌ 缺失 |
| AND_GATEWAY | 并行网关 | 并行网关 | ❌ 缺失 |
| OR_GATEWAY | 或网关 | 包容网关 | ❌ 缺失 |
| LLM_TASK | LLM任务 | Agent任务 | ❌ 缺失 |

### 1.3 Canvas.js中的实际使用
**文件**: `js/Canvas.js`

```javascript
_isSmallNode(activityType) {
    const smallTypes = ['START', 'END', 'XOR_GATEWAY', 'AND_GATEWAY', 'OR_GATEWAY'];
    return smallTypes.includes(activityType);
}
```

**问题**: Canvas已经支持START、END、XOR_GATEWAY等类型，但面板Schema未提供选项。

---

## 📊 二、活动分类 (ActivityCategory) 对比

### 2.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
// 未提供选项，使用默认值 'HUMAN'
```

### 2.2 XPDL标准定义
**文件**: `docs/bpm-spec/vol-06-yaml-standard/README.md`

| 枚举值 | 中文名 | 说明 | 前端支持 |
|--------|--------|------|---------|
| HUMAN | 人工活动 | 人工办理 | ✅ 默认 |
| AGENT | Agent活动 | AI代理 | ❌ 缺失 |
| SCENE | 场景活动 | A2UI场景 | ❌ 缺失 |

### 2.3 Chat.js中的实际使用
**文件**: `js/Chat.js`

```javascript
activityCategory: data.category || 'HUMAN'
// 创建Agent任务时使用 'AGENT'
activityCategory: 'AGENT'
```

**问题**: Chat.js已经使用AGENT分类，但面板Schema未提供选项。

---

## 📊 三、实现方式 (Implementation) 对比

### 3.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
options: [
    { value: 'IMPL_NO', label: '无实现' },
    { value: 'IMPL_TOOL', label: '工具' },
    { value: 'IMPL_SUBFLOW', label: '子流程' },
    { value: 'IMPL_AGENT', label: 'Agent' },
    { value: 'IMPL_ROUTE', label: '路由' },
    { value: 'IMPL_BLOCK', label: '块活动' }
]
```

### 3.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 前端枚举 | 后端枚举 | 中文名 | 前端支持 |
|---------|---------|--------|---------|
| IMPL_NO | No | 无实现 | ✅ |
| IMPL_TOOL | Tool | 工具 | ✅ |
| IMPL_SUBFLOW | SubFlow | 子流程 | ✅ |
| IMPL_OUTFLOW | OutFlow | 外部流程 | ❌ 缺失 |
| IMPL_DEVICE | Device | 设备 | ❌ 缺失 |
| IMPL_EVENT | Event | 事件 | ❌ 缺失 |
| IMPL_SERVICE | Service | 服务 | ❌ 缺失 |
| IMPL_AGENT | - | Agent | ⚠️ 非标准 |
| IMPL_ROUTE | - | 路由 | ⚠️ 非标准 |
| IMPL_BLOCK | - | 块活动 | ⚠️ 非标准 |

**问题**: 前端定义了IMPL_AGENT、IMPL_ROUTE、IMPL_BLOCK，但这些不在EnumMapping标准中。

---

## 📊 四、活动位置 (Position) 对比

### 4.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
// 未提供选项，使用默认值 'NORMAL'
```

### 4.2 ActivityDef.js中的定义
**文件**: `js/model/ActivityDef.js`

```javascript
this.position = data?.position || 'NORMAL';
```

### 4.3 数据库中的实际值
**文件**: `db/data.sql`

```sql
POSITION: 'POSITION_START', 'POSITION_NORMAL', 'POSITION_END'
```

| 前端枚举 | 数据库枚举 | 中文名 | 前端支持 |
|---------|-----------|--------|---------|
| START | POSITION_START | 开始位置 | ❌ 缺失 |
| NORMAL | POSITION_NORMAL | 正常位置 | ✅ 默认 |
| END | POSITION_END | 结束位置 | ❌ 缺失 |

**问题**: 数据库使用POSITION_前缀，前端需要映射。

---

## 📊 五、路由方向 (RouteDirection) 对比

### 5.1 当前前端面板定义
**文件**: `js/panel/plugins/RoutePanelPlugin.js`

```javascript
options: [
    { value: 'FORWARD', label: '前进' },
    { value: 'BACK', label: '退回' }
]
```

### 5.2 数据库中的实际值
**文件**: `db/data.sql`

```sql
ROUTEDIRECTION: 'FORWARD', 'BACK'
```

**结论**: ✅ 完全匹配，无需修改。

---

## 📊 六、流程状态 (PublicationStatus) 对比

### 6.1 当前前端面板定义
**文件**: `js/panel/schemas/ProcessPanelSchema.js`

```javascript
options: [
    { value: 'DRAFT', label: '草稿' },
    { value: 'PUBLISHED', label: '已发布' },
    { value: 'ARCHIVED', label: '已归档' }
]
```

### 6.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 前端枚举 | 后端枚举 | 中文名 | 前端支持 |
|---------|---------|--------|---------|
| UNDER_REVISION | DRAFT | 草稿 | ⚠️ 不匹配 |
| RELEASED | PUBLISHED | 已发布 | ⚠️ 不匹配 |
| UNDER_TEST | TESTING | 测试中 | ❌ 缺失 |
| - | FROZEN | 已冻结 | ❌ 缺失 |
| ARCHIVED | - | 已归档 | ⚠️ 非标准 |

**问题**: 前端使用DRAFT/PUBLISHED/ARCHIVED，但EnumMapping定义的是UNDER_REVISION/RELEASED/UNDER_TEST。

---

## 📊 七、时间单位 (DurationUnit) 对比

### 7.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
options: [
    { value: 'HOUR', label: '小时' },
    { value: 'DAY', label: '天' },
    { value: 'WEEK', label: '周' },
    { value: 'MONTH', label: '月' }
]
```

### 7.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 枚举值 | 中文名 | 前端支持 |
|--------|--------|---------|
| Y | 年 | ❌ 缺失 |
| M | 月 | ✅ |
| D | 天 | ✅ |
| H | 小时 | ✅ |
| m | 分钟 | ❌ 缺失 |
| s | 秒 | ❌ 缺失 |
| W | 周 | ✅ |

**问题**: 前端使用HOUR/DAY/WEEK/MONTH，标准定义是H/D/W/M/Y/m/s。

---

## 📊 八、等待合并类型 (Join) 对比

### 8.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
// 未提供选项
```

### 8.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 前端枚举 | 后端枚举 | 中文名 | 前端支持 |
|---------|---------|--------|---------|
| JOIN_AND | AND | 与合并 | ❌ 缺失 |
| JOIN_XOR | XOR | 异或合并 | ❌ 缺失 |
| DEFAULT | DEFAULT | 默认 | ❌ 缺失 |

### 8.3 数据库中的实际值
**文件**: `db/data.sql`

```sql
INJOIN: 'JOIN_AND'
```

**问题**: 数据库使用JOIN_前缀，需要映射。

---

## 📊 九、并行处理类型 (Split) 对比

### 9.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
// 未提供选项
```

### 9.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 前端枚举 | 后端枚举 | 中文名 | 前端支持 |
|---------|---------|--------|---------|
| SPLIT_AND | AND | 与分支 | ❌ 缺失 |
| SPLIT_XOR | XOR | 异或分支 | ❌ 缺失 |
| DEFAULT | DEFAULT | 默认 | ❌ 缺失 |

### 9.3 数据库中的实际值
**文件**: `db/data.sql`

```sql
SPLIT: 'SPLIT_AND'
```

**问题**: 数据库使用SPLIT_前缀，需要映射。

---

## 📊 十、办理类型 (PerformType) 对比

### 10.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
// 未提供选项
```

### 10.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 枚举值 | 中文名 | 前端支持 |
|--------|--------|---------|
| SINGLE | 单人办理 | ❌ 缺失 |
| MULTIPLE | 多人办理 | ❌ 缺失 |
| JOINTSIGN | 会签 | ❌ 缺失 |
| NEEDNOTSELECT | 无需选择 | ❌ 缺失 |
| NOSELECT | 不选择 | ❌ 缺失 |
| DEFAULT | 默认 | ❌ 缺失 |

---

## 📊 十一、办理顺序 (PerformSequence) 对比

### 11.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
// 未提供选项
```

### 11.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 枚举值 | 中文名 | 前端支持 |
|--------|--------|---------|
| FIRST | 第一人办理 | ❌ 缺失 |
| SEQUENCE | 顺序办理 | ❌ 缺失 |
| MEANWHILE | 同时办理 | ❌ 缺失 |
| AUTOSIGN | 自动签收 | ❌ 缺失 |
| DEFAULT | 默认 | ❌ 缺失 |

---

## 📊 十二、到期处理 (DeadlineOperation) 对比

### 12.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
// 未提供选项
```

### 12.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 枚举值 | 中文名 | 前端支持 |
|--------|--------|---------|
| DEFAULT | 默认处理 | ❌ 缺失 |
| DELAY | 延期处理 | ❌ 缺失 |
| TAKEBACK | 收回处理 | ❌ 缺失 |
| SURROGATE | 代理处理 | ❌ 缺失 |

---

## 📊 十三、退回路径 (RouteBackMethod) 对比

### 13.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
// 未提供选项
```

### 13.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 枚举值 | 中文名 | 前端支持 |
|--------|--------|---------|
| DEFAULT | 默认 | ❌ 缺失 |
| LAST | 上一环节 | ❌ 缺失 |
| ANY | 任意环节 | ❌ 缺失 |
| SPECIFY | 指定环节 | ❌ 缺失 |

---

## 📊 十四、特送范围 (SpecialSendScope) 对比

### 14.1 当前前端面板定义
**文件**: `js/panel/schemas/ActivityPanelSchema.js`

```javascript
// 未提供选项
```

### 14.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 枚举值 | 中文名 | 前端支持 |
|--------|--------|---------|
| DEFAULT | 默认 | ❌ 缺失 |
| ALL | 所有人 | ❌ 缺失 |
| PERFORMERS | 办理人 | ❌ 缺失 |

---

## 📊 十五、流程访问级别 (AccessLevel) 对比

### 15.1 当前前端面板定义
**文件**: `js/panel/schemas/ProcessPanelSchema.js`

```javascript
options: [
    { value: 'PUBLIC', label: '公开' },
    { value: 'PRIVATE', label: '私有' },
    { value: 'PROTECTED', label: '受保护' }
]
```

### 15.2 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 前端枚举 | 后端枚举 | 中文名 | 前端支持 |
|---------|---------|--------|---------|
| Public | INDEPENDENT | 独立流程 | ⚠️ 不匹配 |
| Private | SUBPROCESS | 子流程 | ⚠️ 不匹配 |
| Block | BLOCK | 块流程 | ❌ 缺失 |

**问题**: 前端使用PUBLIC/PRIVATE/PROTECTED，但EnumMapping定义的是Public/Private/Block。

---

## 📊 十六、表单标识类型 (Mark) 对比

### 16.1 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 前端枚举 | 后端枚举 | 中文名 | 前端支持 |
|---------|---------|--------|---------|
| ProcessInst | GLOBAL | 流程实例级 | ❌ 缺失 |
| ActivityInst | ACTIVITY | 活动实例级 | ❌ 缺失 |
| Person | PERSON | 人员级 | ❌ 缺失 |
| ActivityInstPerson | ACTIVITY_PERSON | 活动人员级 | ❌ 缺失 |

---

## 📊 十七、锁定策略 (Lock) 对比

### 17.1 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 前端枚举 | 后端枚举 | 中文名 | 前端支持 |
|---------|---------|--------|---------|
| Msg | MSG | 消息锁定 | ❌ 缺失 |
| Lock | LOCK | 锁定 | ❌ 缺失 |
| Person | PERSON | 人员锁定 | ❌ 缺失 |
| Last | LAST | 最后锁定 | ❌ 缺失 |
| NO | NO_LOCK | 不锁定 | ❌ 缺失 |

---

## 📊 十八、权限组 (RightGroup) 对比

### 18.1 EnumMapping.js标准定义
**文件**: `js/sdk/EnumMapping.js`

| 枚举值 | 中文名 | 前端支持 |
|--------|--------|---------|
| PERFORMER | 办理人 | ❌ 缺失 |
| SPONSOR | 发起人 | ❌ 缺失 |
| READER | 阅办人 | ❌ 缺失 |
| HISTORYPERFORMER | 历史办理人 | ❌ 缺失 |
| HISSPONSOR | 历史发起人 | ❌ 缺失 |
| HISTORYREADER | 历史阅办人 | ❌ 缺失 |
| NORIGHT | 无权限 | ❌ 缺失 |
| NULL | 空权限 | ❌ 缺失 |

---

## 📋 总结：不完全匹配项统计

### 🔴 高优先级（核心功能缺失）

| 序号 | 字段名称 | 缺失枚举 | 影响范围 |
|------|---------|---------|---------|
| 1 | ActivityType | START, END, XOR_GATEWAY, AND_GATEWAY, OR_GATEWAY, LLM_TASK | 流程建模 |
| 2 | ActivityCategory | AGENT, SCENE | Agent和场景支持 |
| 3 | Implementation | IMPL_OUTFLOW, IMPL_DEVICE, IMPL_EVENT, IMPL_SERVICE | 活动实现 |
| 4 | Position | START, END | 活动位置 |
| 5 | PublicationStatus | UNDER_TEST, FROZEN | 流程状态管理 |

### 🟡 中优先级（功能完善）

| 序号 | 字段名称 | 缺失枚举 | 影响范围 |
|------|---------|---------|---------|
| 6 | DurationUnit | Y, m, s | 时间配置 |
| 7 | Join | JOIN_AND, JOIN_XOR, DEFAULT | 路由合并 |
| 8 | Split | SPLIT_AND, SPLIT_XOR, DEFAULT | 路由分支 |
| 9 | PerformType | SINGLE, MULTIPLE, JOINTSIGN等 | 办理方式 |
| 10 | PerformSequence | FIRST, SEQUENCE, MEANWHILE等 | 办理顺序 |
| 11 | DeadlineOperation | DELAY, TAKEBACK, SURROGATE | 到期处理 |
| 12 | RouteBackMethod | LAST, ANY, SPECIFY | 退回路径 |
| 13 | SpecialSendScope | ALL, PERFORMERS | 特送范围 |

### 🟢 低优先级（高级功能）

| 序号 | 字段名称 | 缺失枚举 | 影响范围 |
|------|---------|---------|---------|
| 14 | AccessLevel | Block | 流程访问 |
| 15 | Mark | ProcessInst, ActivityInst等 | 表单标识 |
| 16 | Lock | Msg, Lock, Person等 | 锁定策略 |
| 17 | RightGroup | PERFORMER, SPONSOR等 | 权限配置 |

---

## 🎯 建议升级方案

### 方案一：完全对齐（推荐）
- 将所有前端枚举与EnumMapping.js完全对齐
- 添加所有缺失的枚举选项
- 统一使用标准枚举值

### 方案二：渐进式升级
- 先实现高优先级缺失项
- 逐步完善中低优先级功能
- 保持向后兼容

### 方案三：双轨制
- 前端使用友好枚举（如：任务、服务）
- 后端使用标准枚举（如：TASK, SERVICE）
- 通过EnumMapper自动转换

---

## 📝 下一步行动

1. **立即修复**：ActivityType、ActivityCategory、Implementation
2. **短期完善**：Position、DurationUnit、Join、Split
3. **中期优化**：PerformType、PerformSequence、DeadlineOperation
4. **长期规划**：Mark、Lock、RightGroup等高级功能

---

**报告生成时间**: 2026-04-08  
**文档路径**: E:\github\ooder-skills\docs\bpm-designer\panel-enum-comparison-report.md

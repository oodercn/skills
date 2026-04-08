# BPM Designer 面板属性升级方案

**版本**: v1.0  
**创建日期**: 2026-04-08  
**文档路径**: E:\github\ooder-skills\docs\bpm-designer\panel-upgrade-plan.md

---

## 📋 升级目标

将BPM Designer面板属性完全对齐XPDL标准、BPM Server后端和Swing客户端，确保枚举值的一致性和完整性。

---

## 🎯 升级策略

采用**渐进式升级**策略，分三个阶段完成：

1. **第一阶段（立即）**：核心功能缺失项
2. **第二阶段（短期）**：功能完善项
3. **第三阶段（中期）**：高级功能项

---

## 📦 第一阶段：核心功能升级

### 1. ActivityType（活动类型）

**当前状态**:
```javascript
// js/panel/schemas/ActivityPanelSchema.js
options: [
    { value: 'TASK', label: '任务' },
    { value: 'SERVICE', label: '服务' },
    { value: 'SCRIPT', label: '脚本' },
    { value: 'GATEWAY', label: '网关' },
    { value: 'SUBPROCESS', label: '子流程' }
]
```

**升级后**:
```javascript
options: [
    { value: 'START', label: '开始节点', icon: 'start' },
    { value: 'END', label: '结束节点', icon: 'end' },
    { value: 'TASK', label: '用户任务', icon: 'task' },
    { value: 'SERVICE', label: '系统服务', icon: 'service' },
    { value: 'SCRIPT', label: '脚本任务', icon: 'script' },
    { value: 'XOR_GATEWAY', label: '排他网关', icon: 'xor-gateway' },
    { value: 'AND_GATEWAY', label: '并行网关', icon: 'and-gateway' },
    { value: 'OR_GATEWAY', label: '包容网关', icon: 'or-gateway' },
    { value: 'SUBPROCESS', label: '子流程', icon: 'subprocess' },
    { value: 'LLM_TASK', label: 'LLM任务', icon: 'llm-task' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`
- `js/Canvas.js` (已支持)
- `js/Elements.js` (元素库)

---

### 2. ActivityCategory（活动分类）

**当前状态**: 未提供选项，默认'HUMAN'

**升级后**:
```javascript
options: [
    { value: 'HUMAN', label: '人工活动', description: '需要人工办理的活动' },
    { value: 'AGENT', label: 'Agent活动', description: '由AI Agent执行的活动' },
    { value: 'SCENE', label: '场景活动', description: 'A2UI场景驱动的活动' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`
- `js/model/ActivityDef.js`
- `js/Chat.js` (已使用)

---

### 3. Implementation（实现方式）

**当前状态**:
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

**升级后** (对齐EnumMapping):
```javascript
options: [
    { value: 'IMPL_NO', label: '无实现', backendValue: 'No' },
    { value: 'IMPL_TOOL', label: '工具', backendValue: 'Tool' },
    { value: 'IMPL_SUBFLOW', label: '子流程', backendValue: 'SubFlow' },
    { value: 'IMPL_OUTFLOW', label: '外部流程', backendValue: 'OutFlow' },
    { value: 'IMPL_DEVICE', label: '设备', backendValue: 'Device' },
    { value: 'IMPL_EVENT', label: '事件', backendValue: 'Event' },
    { value: 'IMPL_SERVICE', label: '服务', backendValue: 'Service' }
]
```

**说明**: 移除非标准的IMPL_AGENT、IMPL_ROUTE、IMPL_BLOCK，改用ActivityCategory区分。

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`
- `js/sdk/EnumMapping.js`
- `js/model/ActivityDef.js`

---

### 4. Position（活动位置）

**当前状态**: 未提供选项，默认'NORMAL'

**升级后**:
```javascript
options: [
    { value: 'START', label: '开始位置', backendValue: 'POSITION_START' },
    { value: 'NORMAL', label: '正常位置', backendValue: 'POSITION_NORMAL' },
    { value: 'END', label: '结束位置', backendValue: 'POSITION_END' }
]
```

**需要在EnumMapping中添加**:
```javascript
ActivityDefPosition: {
    toBackend: {
        'START': 'POSITION_START',
        'NORMAL': 'POSITION_NORMAL',
        'END': 'POSITION_END'
    },
    toFrontend: {
        'POSITION_START': 'START',
        'POSITION_NORMAL': 'NORMAL',
        'POSITION_END': 'END'
    }
}
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`
- `js/sdk/EnumMapping.js`
- `js/model/ActivityDef.js`

---

### 5. PublicationStatus（流程状态）

**当前状态**:
```javascript
options: [
    { value: 'DRAFT', label: '草稿' },
    { value: 'PUBLISHED', label: '已发布' },
    { value: 'ARCHIVED', label: '已归档' }
]
```

**升级后** (对齐EnumMapping):
```javascript
options: [
    { value: 'UNDER_REVISION', label: '修订中', backendValue: 'DRAFT' },
    { value: 'RELEASED', label: '已发布', backendValue: 'PUBLISHED' },
    { value: 'UNDER_TEST', label: '测试中', backendValue: 'TESTING' },
    { value: 'FROZEN', label: '已冻结', backendValue: 'FROZEN' }
]
```

**影响文件**:
- `js/panel/schemas/ProcessPanelSchema.js`
- `js/sdk/EnumMapping.js`

---

## 📦 第二阶段：功能完善升级

### 6. DurationUnit（时间单位）

**当前状态**:
```javascript
options: [
    { value: 'HOUR', label: '小时' },
    { value: 'DAY', label: '天' },
    { value: 'WEEK', label: '周' },
    { value: 'MONTH', label: '月' }
]
```

**升级后** (对齐EnumMapping):
```javascript
options: [
    { value: 'Y', label: '年' },
    { value: 'M', label: '月' },
    { value: 'D', label: '天' },
    { value: 'H', label: '小时' },
    { value: 'm', label: '分钟' },
    { value: 's', label: '秒' },
    { value: 'W', label: '周' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`
- `js/panel/schemas/ProcessPanelSchema.js`

---

### 7. Join（等待合并）

**当前状态**: 未提供选项

**升级后**:
```javascript
options: [
    { value: 'DEFAULT', label: '默认' },
    { value: 'JOIN_AND', label: '与合并', backendValue: 'AND', description: '所有输入路由都到达后合并' },
    { value: 'JOIN_XOR', label: '异或合并', backendValue: 'XOR', description: '任意输入路由到达后合并' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`

---

### 8. Split（并行处理）

**当前状态**: 未提供选项

**升级后**:
```javascript
options: [
    { value: 'DEFAULT', label: '默认' },
    { value: 'SPLIT_AND', label: '与分支', backendValue: 'AND', description: '所有输出路由并行执行' },
    { value: 'SPLIT_XOR', label: '异或分支', backendValue: 'XOR', description: '根据条件选择一条路由执行' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`

---

### 9. PerformType（办理类型）

**当前状态**: 未提供选项

**升级后**:
```javascript
options: [
    { value: 'DEFAULT', label: '默认' },
    { value: 'SINGLE', label: '单人办理', description: '只需一人办理' },
    { value: 'MULTIPLE', label: '多人办理', description: '多人参与办理' },
    { value: 'JOINTSIGN', label: '会签', description: '多人同时办理' },
    { value: 'NEEDNOTSELECT', label: '无需选择', description: '无需选择办理人' },
    { value: 'NOSELECT', label: '不选择', description: '不选择办理人' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`

---

### 10. PerformSequence（办理顺序）

**当前状态**: 未提供选项

**升级后**:
```javascript
options: [
    { value: 'DEFAULT', label: '默认' },
    { value: 'FIRST', label: '第一人办理', description: '第一人签收后即可办理' },
    { value: 'SEQUENCE', label: '顺序办理', description: '按顺序依次办理' },
    { value: 'MEANWHILE', label: '同时办理', description: '多人同时办理' },
    { value: 'AUTOSIGN', label: '自动签收', description: '自动签收办理' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`

---

### 11. DeadlineOperation（到期处理）

**当前状态**: 未提供选项

**升级后**:
```javascript
options: [
    { value: 'DEFAULT', label: '默认处理' },
    { value: 'DELAY', label: '延期处理', description: '允许延期办理' },
    { value: 'TAKEBACK', label: '收回处理', description: '自动收回任务' },
    { value: 'SURROGATE', label: '代理处理', description: '转交代理人办理' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`

---

### 12. RouteBackMethod（退回路径）

**当前状态**: 未提供选项

**升级后**:
```javascript
options: [
    { value: 'DEFAULT', label: '默认' },
    { value: 'LAST', label: '上一环节', description: '退回到上一办理环节' },
    { value: 'ANY', label: '任意环节', description: '退回到任意历史环节' },
    { value: 'SPECIFY', label: '指定环节', description: '退回到指定环节' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`

---

### 13. SpecialSendScope（特送范围）

**当前状态**: 未提供选项

**升级后**:
```javascript
options: [
    { value: 'DEFAULT', label: '默认' },
    { value: 'ALL', label: '所有人', description: '特送给所有人' },
    { value: 'PERFORMERS', label: '办理人', description: '特送给办理人' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`

---

## 📦 第三阶段：高级功能升级

### 14. AccessLevel（流程访问级别）

**当前状态**:
```javascript
options: [
    { value: 'PUBLIC', label: '公开' },
    { value: 'PRIVATE', label: '私有' },
    { value: 'PROTECTED', label: '受保护' }
]
```

**升级后** (对齐EnumMapping):
```javascript
options: [
    { value: 'Public', label: '公开流程', backendValue: 'INDEPENDENT' },
    { value: 'Private', label: '私有流程', backendValue: 'SUBPROCESS' },
    { value: 'Block', label: '块流程', backendValue: 'BLOCK' }
]
```

**影响文件**:
- `js/panel/schemas/ProcessPanelSchema.js`

---

### 15. Mark（表单标识类型）

**当前状态**: 未提供选项

**升级后**:
```javascript
options: [
    { value: 'ProcessInst', label: '流程实例级', backendValue: 'GLOBAL' },
    { value: 'ActivityInst', label: '活动实例级', backendValue: 'ACTIVITY' },
    { value: 'Person', label: '人员级', backendValue: 'PERSON' },
    { value: 'ActivityInstPerson', label: '活动人员级', backendValue: 'ACTIVITY_PERSON' }
]
```

**影响文件**:
- `js/panel/schemas/ProcessPanelSchema.js`

---

### 16. Lock（锁定策略）

**当前状态**: 未提供选项

**升级后**:
```javascript
options: [
    { value: 'NO', label: '不锁定', backendValue: 'NO_LOCK' },
    { value: 'Msg', label: '消息锁定', backendValue: 'MSG' },
    { value: 'Lock', label: '锁定', backendValue: 'LOCK' },
    { value: 'Person', label: '人员锁定', backendValue: 'PERSON' },
    { value: 'Last', label: '最后锁定', backendValue: 'LAST' }
]
```

**影响文件**:
- `js/panel/schemas/ProcessPanelSchema.js`

---

### 17. RightGroup（权限组）

**当前状态**: 未提供选项

**升级后**:
```javascript
options: [
    { value: 'PERFORMER', label: '办理人' },
    { value: 'SPONSOR', label: '发起人' },
    { value: 'READER', label: '阅办人' },
    { value: 'HISTORYPERFORMER', label: '历史办理人' },
    { value: 'HISSPONSOR', label: '历史发起人' },
    { value: 'HISTORYREADER', label: '历史阅办人' },
    { value: 'NORIGHT', label: '无权限' },
    { value: 'NULL', label: '空权限' }
]
```

**影响文件**:
- `js/panel/schemas/ActivityPanelSchema.js`

---

## 📝 实施步骤

### 第一阶段实施（预计2天）

**Day 1**:
1. ✅ 更新ActivityType枚举选项
2. ✅ 更新ActivityCategory枚举选项
3. ✅ 更新Implementation枚举选项
4. ✅ 测试活动创建和编辑功能

**Day 2**:
1. ✅ 更新Position枚举选项
2. ✅ 更新PublicationStatus枚举选项
3. ✅ 更新EnumMapping.js映射关系
4. ✅ 测试流程保存和加载功能

### 第二阶段实施（预计3天）

**Day 3**:
1. ✅ 更新DurationUnit枚举选项
2. ✅ 更新Join枚举选项
3. ✅ 更新Split枚举选项
4. ✅ 测试路由合并和分支功能

**Day 4**:
1. ✅ 更新PerformType枚举选项
2. ✅ 更新PerformSequence枚举选项
3. ✅ 更新DeadlineOperation枚举选项
4. ✅ 测试办理配置功能

**Day 5**:
1. ✅ 更新RouteBackMethod枚举选项
2. ✅ 更新SpecialSendScope枚举选项
3. ✅ 综合测试所有枚举功能

### 第三阶段实施（预计2天）

**Day 6**:
1. ✅ 更新AccessLevel枚举选项
2. ✅ 更新Mark枚举选项
3. ✅ 更新Lock枚举选项
4. ✅ 测试流程配置功能

**Day 7**:
1. ✅ 更新RightGroup枚举选项
2. ✅ 完善面板UI显示
3. ✅ 全面回归测试
4. ✅ 文档更新

---

## 🧪 测试计划

### 单元测试

1. **枚举映射测试**
   - 测试所有枚举值的前后端转换
   - 验证EnumMapper.toBackend/toFrontend方法
   - 测试边界情况（空值、无效值）

2. **面板渲染测试**
   - 测试所有枚举选项的渲染
   - 验证选项显示文本和值
   - 测试动态字段显示/隐藏

### 集成测试

1. **流程创建测试**
   - 创建包含所有活动类型的流程
   - 验证流程保存和加载
   - 测试流程导出YAML

2. **活动编辑测试**
   - 编辑所有枚举字段
   - 验证数据保存正确性
   - 测试字段联动关系

### 回归测试

1. **兼容性测试**
   - 测试旧流程数据加载
   - 验证枚举值向后兼容
   - 测试数据库迁移

2. **性能测试**
   - 测试大量枚举选项渲染性能
   - 验证面板切换流畅度
   - 测试内存占用

---

## 📚 相关文档

1. [BPM Designer 面板属性枚举对比分析报告](./panel-enum-comparison-report.md)
2. [EnumMapping.js 源码](../skills/_drivers/bpm/bpm-designer/src/main/resources/static/designer/js/sdk/EnumMapping.js)
3. [XPDL YAML标准](../bpm-spec/vol-06-yaml-standard/README.md)
4. [A2UI扩展规格](../bpm-spec/vol-05-a2ui-extension/README.md)

---

## 📌 注意事项

1. **向后兼容性**
   - 保留旧枚举值的映射关系
   - 提供数据迁移脚本
   - 支持渐进式升级

2. **用户体验**
   - 提供枚举值的中英文对照
   - 添加枚举值说明和提示
   - 优化选项分组和排序

3. **数据一致性**
   - 确保前后端枚举值一致
   - 定期同步EnumMapping配置
   - 建立枚举值变更流程

---

**文档版本**: v1.0  
**最后更新**: 2026-04-08  
**负责人**: AI Assistant

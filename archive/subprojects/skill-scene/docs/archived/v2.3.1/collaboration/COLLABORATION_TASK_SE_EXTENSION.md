# SE标准扩展协作任务

## 任务概述

**任务ID**: SE-EXT-2026-001  
**创建日期**: 2026-03-12  
**优先级**: 高  
**状态**: 待协作  

## 背景

在实现能力发现页面的安装向导功能时，发现SE标准（Skill Index Schema）缺少对用户参与配置和驱动条件的标准定义。这导致前端安装向导无法从后端获取完整的配置信息，影响用户体验。

## 问题分析

### 当前SE标准已有字段

| 字段 | 类型 | 说明 | 状态 |
|------|------|------|------|
| `roles` | array | 场景角色配置 | ✅ 已有 |
| `capabilityAddresses` | object | 能力地址配置 | ✅ 已有 |
| `dependencies` | array | 依赖项列表 | ✅ 已有 |

### 缺失字段

| 字段 | 类型 | 说明 | 影响 |
|------|------|------|------|
| `participants` | object | 参与者配置 | 安装向导步骤3无法获取配置 |
| `driverConditions` | object | 驱动条件配置 | 安装向导步骤4无法获取配置 |

## 需求详情

### 1. participants 参与者配置

**用途**: 定义场景安装时的参与者配置规则

**建议Schema结构**:

```yaml
- name: participants
  type: object
  required: false
  condition: "skillForm == SCENE"
  description: "参与者配置"
  properties:
    leader:
      type: object
      required: true
      description: "主导者配置"
      properties:
        required:
          type: boolean
          default: true
          description: "是否必须指定主导者"
        defaultToCurrentUser:
          type: boolean
          default: true
          description: "默认为当前用户"
        permissions:
          type: array
          itemType: string
          description: "主导者权限列表"
    
    collaborators:
      type: object
      required: false
      description: "协作者配置"
      properties:
        minCount:
          type: integer
          default: 0
          description: "最小协作者数量"
        maxCount:
          type: integer
          default: 10
          description: "最大协作者数量"
        selectionType:
          type: enum
          values: [USER, ROLE, GROUP]
          default: USER
          description: "选择类型"
    
    pushType:
      type: enum
      required: false
      values: [SHARE, INVITE, DELEGATE]
      default: SHARE
      description: "推送类型"
```

**示例数据**:

```yaml
participants:
  leader:
    required: true
    defaultToCurrentUser: true
    permissions: [ACTIVATE, CONFIGURE, INVITE]
  collaborators:
    minCount: 0
    maxCount: 5
    selectionType: USER
  pushType: SHARE
```

### 2. driverConditions 驱动条件配置

**用途**: 定义场景的触发条件配置规则

**建议Schema结构**:

```yaml
- name: driverConditions
  type: object
  required: false
  condition: "skillForm == SCENE || skillForm == DRIVER"
  description: "驱动条件配置"
  properties:
    supportedTypes:
      type: array
      itemType: enum
      values: [MANUAL, SCHEDULE, EVENT, WEBHOOK]
      required: true
      description: "支持的触发类型"
    
    defaultType:
      type: enum
      values: [MANUAL, SCHEDULE, EVENT, WEBHOOK]
      default: MANUAL
      description: "默认触发类型"
    
    scheduleConfig:
      type: object
      required: false
      description: "定时触发配置"
      properties:
        cronExpression:
          type: string
          description: "Cron表达式"
        timezone:
          type: string
          default: "Asia/Shanghai"
          description: "时区"
    
    eventConfig:
      type: object
      required: false
      description: "事件触发配置"
      properties:
        eventTypes:
          type: array
          itemType: string
          description: "监听的事件类型"
        eventSource:
          type: string
          description: "事件来源"
```

**示例数据**:

```yaml
driverConditions:
  supportedTypes: [MANUAL, SCHEDULE, EVENT]
  defaultType: MANUAL
  scheduleConfig:
    timezone: "Asia/Shanghai"
  eventConfig:
    eventTypes: [USER_LOGIN, DATA_UPDATE]
    eventSource: "system"
```

## 影响范围

### 需要修改的文件

1. **SE标准定义**
   - `E:\github\ooder-skills\skills\config\schema.yaml` - 添加新字段定义

2. **后端服务**
   - `SkillIndexLoader.java` - 解析新字段
   - `SkillIndexEntry.java` - 添加新字段属性
   - `GitDiscoveryController.java` - 返回新字段数据

3. **前端页面**
   - `capability-discovery.js` - 使用新字段渲染安装向导

### 兼容性考虑

- 新字段均为可选字段，不影响现有技能
- 后端需要处理字段缺失的默认值逻辑
- 前端需要兼容旧版本技能（无新字段时使用默认配置）

## 协作分工

| 任务 | 负责方 | 预计工时 | 依赖 |
|------|--------|----------|------|
| SE标准Schema扩展 | 架构组 | 2h | 无 |
| 后端模型更新 | 后端组 | 4h | Schema完成 |
| 前端适配 | 前端组 | 4h | 后端API完成 |
| 测试验证 | 测试组 | 2h | 全部完成 |

## 验收标准

1. Schema定义完整，符合YAML规范
2. 后端API返回包含新字段的数据
3. 前端安装向导正确显示配置选项
4. 兼容性测试通过（旧技能正常工作）

## 附录

### 相关文档

- [SKILLS_APP_DEVELOPMENT_STATISTICS.md](./SKILLS_APP_DEVELOPMENT_STATISTICS.md)
- [schema.yaml](../../config/schema.yaml)
- [categories.yaml](../../config/categories.yaml)

### 参考实现

前端安装向导步骤定义（capability-discovery.js）:

```javascript
getInstallSteps: function(cap) {
    var steps = [1];
    if (cap.skillForm === 'SCENE') {
        steps.push(2, 3);  // 选择角色、配置参与者
    }
    if (cap.skillForm === 'SCENE' || cap.skillForm === 'DRIVER') {
        steps.push(4);  // 驱动条件
    }
    steps.push(5);  // 确认依赖
    if (cap.skillForm === 'SCENE' && CapabilityDiscovery.needsLLMConfig(cap)) {
        steps.push(6);  // LLM配置
    }
    steps.push(7, 8);  // 安装进度、完成
    return steps;
}
```

---

**创建人**: AI Assistant  
**最后更新**: 2026-03-12

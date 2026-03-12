# SE多级配置继承标准协作任务

## 任务概述

**任务ID**: SE-CONFIG-2026-001  
**创建日期**: 2026-03-12  
**优先级**: 高  
**状态**: 待协作  

## 背景

为了实现Skills配置的三级继承体系（系统级 → 技能级 → 场景级），需要SE标准支持配置文件的标准化定义，确保配置继承、合并、覆盖等操作的一致性和可移植性。

## 一、配置继承架构

### 1.1 三级配置继承模型

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     配置继承优先级 (从高到低)                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Level 3: 场景内部技能配置                                               │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  文件: skills/{scene}/skills/{skill}/skill-config.yaml          │   │
│  │  优先级: 最高 (可覆盖所有上级)                                    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↑ 继承                                      │
│  Level 2: 场景配置                                                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  文件: skills/{scene}/scene-config.yaml                         │   │
│  │  优先级: 中高 (覆盖技能级)                                        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↑ 继承                                      │
│  Level 1: 技能配置                                                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  文件: skills/{skill}/skill-config.yaml                         │   │
│  │  优先级: 中 (覆盖系统级)                                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↑ 继承                                      │
│  Level 0: 系统配置                                                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  文件: config/system-config.yaml (SDK JSON存储)                  │   │
│  │  优先级: 最低 (全局默认)                                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

## 二、需要SE协作的内容

### 2.1 配置文件标准定义

**需求**: 在SE标准中定义配置文件的结构和位置

**建议Schema扩展**:

```yaml
# skill-index-entry.yaml 扩展
skillConfig:
  type: object
  required: false
  description: "技能配置定义"
  properties:
    file:
      type: string
      default: "skill-config.yaml"
      description: "配置文件路径 (相对于技能根目录)"
    
    inheritStrategy:
      type: enum
      values: [MERGE, OVERRIDE, APPEND]
      default: MERGE
      description: "继承策略"
    
    capabilities:
      type: array
      itemType: object
      description: "能力配置覆盖定义"
      properties:
        address:
          type: string
          pattern: "^0x[0-9A-Fa-f]{2}$"
          description: "能力地址"
        
        configKeys:
          type: array
          itemType: string
          description: "可配置的键列表"
        
        inheritKeys:
          type: array
          itemType: string
          description: "继承上级的键列表"

# scene-index-entry.yaml 扩展
sceneConfig:
  type: object
  required: false
  description: "场景配置定义"
  properties:
    file:
      type: string
      default: "scene-config.yaml"
      description: "场景配置文件路径"
    
    internalSkillConfigs:
      type: array
      itemType: object
      description: "内部技能配置"
      properties:
        skillId:
          type: string
          description: "内部技能ID"
        
        configOverrides:
          type: object
          description: "配置覆盖"
```

### 2.2 配置继承语法标准

**需求**: 标准化配置继承语法

**建议语法定义**:

```yaml
# 配置继承语法规范
inheritanceSyntax:
  # 完全继承上级值
  inherit:
    syntax: "${inherit}"
    description: "继承上级配置的值"
    example:
      temperature: "${inherit}"
  
  # 条件继承 (带默认值)
  inheritWithDefault:
    syntax: "${inherit:defaultValue}"
    description: "继承上级值，若不存在则使用默认值"
    example:
      timeout: "${inherit:60000}"
  
  # 深度合并
  merge:
    syntax: "${merge}"
    description: "与上级配置深度合并"
    example:
      extraParams:
        "${merge}":
          - "${inherit}"
          - customParam: "value"
  
  # 数组追加
  append:
    syntax: "${append}"
    description: "追加到上级数组"
    example:
      models:
        "${append}":
          - id: "custom-model"
  
  # 完全覆盖
  override:
    syntax: "${override}"
    description: "完全覆盖上级配置"
    example:
      maxTokens: 8192  # 直接赋值即覆盖
```

### 2.3 配置文件位置标准

**需求**: 标准化配置文件在技能包中的位置

**建议目录结构**:

```
skills/
├── capabilities/
│   └── {skill-id}/
│       ├── skill-index-entry.yaml    # SE标准元数据
│       ├── skill-config.yaml         # 技能配置 (可选)
│       └── config/
│           ├── default.yaml          # 默认配置
│           └── profiles/
│               ├── micro.yaml        # 微型配置
│               └── large.yaml        # 大型配置
│
└── scenes/
    └── {scene-id}/
        ├── scene-index-entry.yaml    # SE标准元数据
        ├── scene-config.yaml         # 场景配置 (可选)
        └── skills/
            └── {internal-skill-id}/
                └── skill-config.yaml # 内部技能配置 (可选)
```

## 三、存储方案说明

### 3.1 SDK JSON存储方案

**设计原则**: 微方案不使用数据库，统一使用SDK JSON文件存储

```
config/
├── system-config.json          # 系统级配置 (JSON格式)
├── profiles/
│   ├── micro.json              # 微型配置模板
│   ├── small.json              # 小型配置模板
│   ├── large.json              # 大型配置模板
│   └── enterprise.json         # 企业配置模板
├── capabilities/               # 能力运行时配置
│   ├── llm.json
│   ├── db.json
│   └── ...
└── runtime/                    # 运行时配置缓存
    ├── skill-{id}.json
    └── scene-{id}.json
```

### 3.2 配置存储格式

```json
{
  "apiVersion": "skills.ooder.io/v1",
  "kind": "SystemConfig",
  "metadata": {
    "name": "ooder-skills-system",
    "version": "1.0.0",
    "profile": "micro",
    "createdAt": "2026-03-12T00:00:00Z",
    "updatedAt": "2026-03-12T00:00:00Z"
  },
  "spec": {
    "capabilities": {
      "llm": {
        "enabled": true,
        "default": "skill-llm-deepseek",
        "config": {
          "temperature": 0.7,
          "maxTokens": 4096
        }
      }
    }
  }
}
```

## 四、协作分工

| 任务 | 负责方 | 说明 | 依赖 |
|------|--------|------|------|
| SE Schema扩展定义 | SE架构组 | 定义配置文件Schema | 无 |
| 继承语法标准化 | SE架构组 | 定义继承语法规范 | 无 |
| 配置文件位置规范 | SE架构组 | 定义目录结构标准 | 无 |
| SDK JSON存储实现 | SDK组 | 实现JSON配置存储服务 | Schema完成 |
| 配置继承解析器 | SDK组 | 实现继承语法解析 | 语法规范完成 |
| 配置可视化界面 | 前端组 | 配置管理UI | API完成 |
| 配置API开发 | 后端组 | 配置CRUD API | 存储实现完成 |

## 五、接口定义需求

### 5.1 配置查询接口

```http
# 获取系统配置
GET /api/v1/config/system

# 获取指定能力配置
GET /api/v1/config/system/capabilities/{address}

# 获取技能配置 (含继承链解析)
GET /api/v1/config/skills/{skillId}

# 获取场景配置 (含继承链解析)
GET /api/v1/config/scenes/{sceneId}

# 获取配置继承链
GET /api/v1/config/inheritance/{targetType}/{targetId}
```

### 5.2 配置更新接口

```http
# 更新系统配置
PUT /api/v1/config/system/capabilities/{address}

# 更新技能配置
PUT /api/v1/config/skills/{skillId}

# 重置配置到继承值
DELETE /api/v1/config/{targetType}/{targetId}/{key}
```

## 六、验收标准

1. **Schema完整性**
   - [ ] skill-config.yaml Schema定义完整
   - [ ] scene-config.yaml Schema定义完整
   - [ ] 继承语法规范文档完整

2. **存储方案**
   - [ ] SDK JSON存储服务实现
   - [ ] Profile模板加载实现
   - [ ] 配置热更新支持

3. **继承解析**
   - [ ] ${inherit} 语法解析正确
   - [ ] ${merge} 深度合并正确
   - [ ] ${append} 数组追加正确
   - [ ] 多级继承链解析正确

4. **兼容性**
   - [ ] 无配置文件的技能使用系统默认
   - [ ] 旧版技能正常工作
   - [ ] 配置迁移工具可用

## 七、时间计划

| 阶段 | 内容 | 预计时间 |
|------|------|----------|
| 第一阶段 | SE Schema定义 | 2天 |
| 第二阶段 | SDK JSON存储实现 | 3天 |
| 第三阶段 | 配置继承解析器 | 2天 |
| 第四阶段 | 配置API开发 | 3天 |
| 第五阶段 | 可视化界面 | 4天 |
| 第六阶段 | 集成测试 | 2天 |

---

**创建人**: AI Assistant  
**最后更新**: 2026-03-12

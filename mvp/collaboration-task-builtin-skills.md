# 协作任务说明 - Skills 团队

## 任务概述

**提交人**: MVP Core 团队  
**提交时间**: 2026-03-18  
**优先级**: 高  
**任务类型**: 架构调整 - 内置程序移除

---

## 背景

为了简化 MVP 的部署和调试流程，我们决定将核心 skills 直接内置到 MVP 中，不再通过动态加载方式。这样可以：

1. **简化部署** - 减少 JAR 包依赖和插件加载流程
2. **方便调试** - 直接在 MVP 中修改和调试核心代码
3. **提高稳定性** - 避免动态加载带来的不确定性

---

## 已内置的 Skills

以下 skills 已从 `skill-scene` 项目复制到 MVP 中：

### 核心系统 Skills

| 原 Skill | 内置路径 | 说明 |
|----------|----------|------|
| skill-scene (核心模块) | `net.ooder.mvp.skill.scene` | 场景管理核心功能 |
| skill-common | `net.ooder.skill.common` | 公共基础服务（保持依赖） |
| skill-capability | `net.ooder.skill.capability` | 能力管理服务（保持依赖） |
| skill-llm | `net.ooder.skill.llm` | LLM 基础服务（保持依赖） |

### 内置的模块

```
net.ooder.mvp.skill.scene/
├── adapter/          # 适配器
├── agent/            # Agent 服务
├── capability/       # 能力管理
│   ├── activation/   # 激活服务
│   ├── config/       # 配置
│   ├── connector/    # 连接器
│   ├── controller/   # 控制器
│   ├── driver/       # 驱动
│   ├── fallback/     # 降级服务
│   ├── install/      # 安装服务
│   ├── invoke/       # 调用服务
│   ├── model/        # 数据模型
│   ├── registry/     # 注册中心
│   └── service/      # 业务服务
├── config/           # 配置管理
├── controller/       # 控制器
├── data/             # 数据初始化
├── discovery/        # 发现服务
├── engine/           # 引擎
├── integration/      # 集成服务
├── knowledge/        # 知识库
├── llm/              # LLM 服务
├── model/            # 数据模型
├── org/              # 组织管理
├── report/           # 报表服务
├── scene/            # 场景管理
├── skill/            # 技能管理
├── spi/              # SPI 接口
├── task/             # 任务管理
├── terminology/      # 术语服务
├── todo/             # 待办服务
└── util/             # 工具类
```

---

## 需要 Skills 团队执行的任务

### 任务 1: 从 skill-index.yaml 中移除已内置的 skills

**文件**: `skill-index.yaml`

**需要移除的 skills**:
- `skill-scene-management`（已内置为 `net.ooder.mvp.skill.scene`）
- 其他已内置的 skills

**原因**: 避免在能力发现时出现重复安装

### 任务 2: 更新 skill-capability 的依赖声明

**文件**: `skills/_system/skill-capability/skill.yaml`

**修改前**:
```yaml
dependencies:
  - id: skill-common
    required: true
  - id: skill-scene-management
    required: true
  - id: skill-llm
    required: false
```

**修改后**:
```yaml
dependencies:
  - id: skill-common
    required: true
  - id: skill-llm
    required: false
# 注意: skill-scene-management 已内置到 MVP，无需声明依赖
```

### 任务 3: 添加内置标识

**建议**: 在 `skill-index.yaml` 中为内置 skills 添加 `builtIn: true` 标识

```yaml
- id: skill-scene-management
  name: 场景管理
  builtIn: true
  builtInPath: net.ooder.mvp.skill.scene
  # ... 其他配置
```

---

## 影响范围

### 不受影响
- 动态加载的业务 skills（如 skill-a2ui、支付、媒体发布等）
- skill-hotplug-starter 的功能
- 已安装的 skills 注册表

### 受影响
- 能力发现页面（需要过滤已内置的 skills）
- skill-index.yaml 的维护
- skill-capability 的依赖声明

---

## 验证步骤

1. **检查能力发现页面**
   - 访问 `/console/skills/skill-capability/pages/capability-discovery.html`
   - 确认已内置的 skills 不再出现在可安装列表中

2. **检查菜单显示**
   - 确认场景管理相关菜单正常显示
   - 确认 LLM 相关菜单正常显示

3. **检查功能正常**
   - 场景创建、启动、执行
   - 能力绑定、激活
   - LLM 对话

---

## 时间节点

| 阶段 | 时间 | 负责人 |
|------|------|--------|
| MVP 内置完成 | 2026-03-18 | MVP Core 团队 |
| skill-index.yaml 更新 | 待定 | Skills 团队 |
| 验证测试 | 待定 | 双方 |

---

## 联系方式

如有疑问，请联系 MVP Core 团队。

---

**任务状态**: 进行中  
**MVP 内置进度**: ✅ 已完成

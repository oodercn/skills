# OODER Skills 目录结构分析与重组方案

## 一、当前目录结构摸底

### 1.1 目录层级概览

```
skills/
├── _drivers/           # 驱动类技能（外部系统集成）
│   ├── iot/           # IoT设备驱动
│   ├── llm/           # LLM提供商驱动（6个）
│   ├── media/         # 媒体平台驱动（5个）
│   ├── org/           # 组织系统驱动（5个）
│   ├── payment/       # 支付驱动（3个）
│   └── vfs/           # 虚拟文件系统驱动（6个）
│
├── _system/           # 系统核心技能
│   ├── skill-capability/          # 能力管理
│   ├── skill-common/              # 公共基础模块
│   ├── skill-llm/                 # LLM核心
│   ├── skill-llm-chat/            # LLM聊天
│   ├── skill-management/          # 系统管理
│   ├── skill-protocol/            # 协议支持
│   └── skill-scene-management/    # 场景管理UI
│
├── capabilities/      # 能力类技能（业务能力）
│   ├── auth/          # 认证（1个）
│   ├── communication/ # 通信（7个）
│   ├── infrastructure/# 基础设施（2个）
│   ├── iot/           # IoT（2个）⚠️ 与_drivers/iot重复
│   ├── knowledge/     # 知识管理（4个）
│   ├── llm/           # LLM能力（3个）⚠️ 与_system/llm重复
│   ├── monitor/       # 监控（7个）
│   ├── scheduler/     # 调度（2个）
│   ├── search/        # 搜索（1个）
│   └── security/      # 安全（3个）
│
├── scenes/            # 场景类技能（业务场景）
│   ├── skill-business/
│   ├── skill-collaboration/
│   ├── skill-document-assistant/
│   ├── skill-knowledge-qa/
│   ├── skill-knowledge-share/
│   ├── skill-llm-chat/            ⚠️ 与_system/skill-llm-chat重复
│   ├── skill-meeting-minutes/
│   ├── skill-onboarding-assistant/
│   └── skill-project-knowledge/
│
├── tools/             # 工具类技能
│   ├── skill-document-processor/
│   ├── skill-market/
│   ├── skill-report/
│   └── skill-share/
│
├── config/            # 配置文件
│
└── 根目录散落技能：
    ├── skill-scene/                  # 场景核心引擎
    ├── skill-capability-coordinator/ # 能力协调器
    ├── skill-agent-recommendation/
    ├── skill-command-shortcut/
    ├── skill-failover-manager/
    ├── skill-httpclient-okhttp/
    ├── skill-load-balancer/
    └── skill-update-checker/
```

### 1.2 工程数量统计

| 目录 | 数量 | 说明 |
|------|------|------|
| _drivers | 25 | 驱动类技能 |
| _system | 7 | 系统核心技能 |
| capabilities | 32 | 能力类技能 |
| scenes | 9 | 场景类技能 |
| tools | 4 | 工具类技能 |
| 根目录散落 | 8 | 未分类技能 |
| **总计** | **85** | |

---

## 二、重复问题分析

### 2.1 明确重复的模块

| 重复类型 | 模块1 | 模块2 | 问题说明 |
|----------|-------|-------|----------|
| **LLM聊天** | `_system/skill-llm-chat` | `scenes/skill-llm-chat` | 同名不同位置，功能可能重复 |
| **IoT** | `_drivers/iot/skill-openwrt` | `capabilities/iot/skill-hosting` | 同类功能不同位置 |
| **能力管理** | `_system/skill-capability` | `skill-capability-coordinator` | 功能重叠，需明确职责 |

### 2.2 职责不清的模块

| 模块 | 当前位置 | 问题 |
|------|----------|------|
| `skill-scene` | 根目录 | 应归入 `_system/` 或独立 |
| `skill-capability-coordinator` | 根目录 | 与 `_system/skill-capability` 职责重叠 |
| `skill-llm` | `_system/` | 与 `_drivers/llm/skill-llm-base` 关系不清 |
| `capabilities/llm/*` | `capabilities/llm/` | 与 `_system/skill-llm*` 关系不清 |

### 2.3 目录命名不一致

- `_drivers/` 使用下划线前缀
- `capabilities/` 使用全小写
- `scenes/` 使用全小写
- 根目录散落技能无分类

---

## 三、重组方案

### 3.1 新目录结构设计

```
skills/
├── core/                          # 核心系统模块（原_system）
│   ├── skill-common/              # 公共基础
│   ├── skill-capability/          # 能力管理核心
│   ├── skill-scene/               # 场景引擎核心
│   ├── skill-protocol/            # 协议支持
│   └── skill-management/          # 系统管理
│
├── drivers/                       # 驱动模块（原_drivers）
│   ├── llm/                       # LLM提供商
│   │   ├── skill-llm-base/        # LLM基础抽象
│   │   ├── skill-llm-openai/
│   │   ├── skill-llm-deepseek/
│   │   ├── skill-llm-qianwen/
│   │   ├── skill-llm-ollama/
│   │   └── skill-llm-volcengine/
│   ├── vfs/                       # 虚拟文件系统
│   ├── org/                       # 组织系统
│   ├── media/                     # 媒体平台
│   ├── payment/                   # 支付
│   └── iot/                       # IoT设备
│
├── capabilities/                  # 能力模块
│   ├── llm/                       # LLM能力（合并）
│   │   ├── skill-llm-chat/        # 聊天能力
│   │   ├── skill-llm-conversation/# 会话管理
│   │   └── skill-llm-context/     # 上下文构建
│   ├── knowledge/                 # 知识管理
│   ├── communication/             # 通信
│   ├── monitor/                   # 监控
│   ├── security/                  # 安全
│   ├── scheduler/                 # 调度
│   └── search/                    # 搜索
│
├── scenes/                        # 场景模块
│   ├── business/                  # 业务场景
│   │   ├── skill-business/
│   │   ├── skill-collaboration/
│   │   └── skill-meeting-minutes/
│   ├── knowledge/                 # 知识场景
│   │   ├── skill-knowledge-qa/
│   │   ├── skill-knowledge-share/
│   │   └── skill-project-knowledge/
│   └── assistant/                 # 助手场景
│       ├── skill-document-assistant/
│       └── skill-onboarding-assistant/
│
├── tools/                         # 工具模块
│   ├── skill-document-processor/
│   ├── skill-market/
│   ├── skill-report/
│   └── skill-share/
│
├── infrastructure/                # 基础设施模块（新增）
│   ├── skill-hosting/
│   ├── skill-k8s/
│   ├── skill-load-balancer/
│   ├── skill-failover-manager/
│   └── skill-httpclient-okhttp/
│
└── config/                        # 配置文件
```

### 3.2 重复模块处理方案

| 模块 | 处理方式 | 目标位置 |
|------|----------|----------|
| `_system/skill-llm-chat` | 保留 | `capabilities/llm/skill-llm-chat` |
| `scenes/skill-llm-chat` | 删除或合并 | 合并到上述模块 |
| `skill-capability-coordinator` | 合并 | 合并到 `core/skill-capability` |
| `skill-scene` | 移动 | `core/skill-scene` |
| `capabilities/iot/*` | 移动 | `drivers/iot/` |
| `_system/skill-llm` | 合并 | 合并到 `drivers/llm/skill-llm-base` |

---

## 四、开发/测试/发布目录规划

### 4.1 推荐目录结构

```
ooder-skills/
├── skills/                        # 源码目录
│   ├── core/
│   ├── drivers/
│   ├── capabilities/
│   ├── scenes/
│   ├── tools/
│   └── infrastructure/
│
├── mvp/                           # MVP测试环境
│   ├── src/
│   ├── data/                      # 运行时数据
│   ├── plugins/                   # 安装的技能JAR
│   └── logs/                      # 日志
│
├── dist/                          # 发布目录
│   ├── releases/                  # 正式发布
│   │   └── v2.3.1/
│   │       ├── skill-common-2.3.1.jar
│   │       ├── skill-capability-2.3.1.jar
│   │       └── ...
│   └── snapshots/                 # 快照发布
│       └── v2.3.2-SNAPSHOT/
│
├── docs/                          # 文档目录
│   ├── architecture/
│   ├── development/
│   └── deployment/
│
├── scripts/                       # 脚本目录
│   ├── build.sh
│   ├── deploy.sh
│   └── test.sh
│
└── tests/                         # 集成测试
    ├── integration/
    └── e2e/
```

### 4.2 版本管理规范

```
版本号格式: {major}.{minor}.{patch}[-{qualifier}]

示例:
- 2.3.1           正式版本
- 2.3.2-SNAPSHOT  开发快照
- 2.3.2-RC1       发布候选
- 2.3.2-beta1     测试版本
```

### 4.3 发布流程

```
开发 → 测试 → 发布

1. 开发阶段
   - 在 skills/ 目录下开发
   - 使用 SNAPSHOT 版本
   - 单元测试通过

2. 测试阶段
   - 部署到 mvp/ 环境测试
   - 集成测试通过
   - 生成 RC 版本

3. 发布阶段
   - 版本号去除 SNAPSHOT
   - 构建正式 JAR
   - 复制到 dist/releases/
   - 发布到 Maven 仓库
```

---

## 五、迁移执行计划

### 5.1 第一阶段：清理重复 ✅ 已完成

| 操作 | 状态 | 说明 |
|------|------|------|
| 删除 `scenes/skill-llm-chat` | ✅ 完成 | 与 `_system/skill-llm-chat` 重复 |
| 移动 `skill-capability-coordinator` | ✅ 完成 | 移动到 `_system/skill-capability-coordinator` |
| 移动 `capabilities/iot/*` | ✅ 完成 | 移动到 `_drivers/iot/` |

### 5.2 第二阶段：目录重组 ✅ 已完成

| 操作 | 状态 | 说明 |
|------|------|------|
| 移动 `skill-command-shortcut` | ✅ 完成 | 移动到 `tools/` |
| 移动 `skill-update-checker` | ✅ 完成 | 移动到 `tools/` |
| 移动 `skill-failover-manager` | ✅ 完成 | 移动到 `capabilities/infrastructure/` |
| 移动 `skill-httpclient-okhttp` | ✅ 完成 | 移动到 `capabilities/infrastructure/` |
| 移动 `skill-load-balancer` | ✅ 完成 | 移动到 `capabilities/infrastructure/` |
| 移动 `skill-agent-recommendation` | ✅ 完成 | 移动到 `scenes/` |

### 5.3 第三阶段：规范统一 ✅ 已完成

| 操作 | 状态 | 说明 |
|------|------|------|
| 更新父pom.xml modules | ✅ 完成 | 按新目录结构更新模块列表 |
| 修复relativePath | ✅ 完成 | 更新移动后模块的相对路径 |
| 删除旧模块引用 | ✅ 完成 | 移除根目录散落模块的旧引用 |

---

## 五点五、迁移后目录结构

```
skills/
├── _drivers/           # 驱动类技能
│   ├── iot/           # IoT设备驱动（含新增的skill-hosting, skill-k8s）
│   ├── llm/           # LLM提供商驱动
│   ├── media/         # 媒体平台驱动
│   ├── org/           # 组织系统驱动
│   ├── payment/       # 支付驱动
│   ├── vfs/           # 虚拟文件系统驱动
│
├── _system/           # 系统核心技能
│   ├── skill-capability/              # 能力管理
│   ├── skill-capability-coordinator/  # 能力协调器（新增）
│   ├── skill-common/                  # 公共基础模块
│   ├── skill-llm/                     # LLM核心
│   ├── skill-llm-chat/                # LLM聊天
│   ├── skill-management/              # 系统管理
│   ├── skill-protocol/                # 协议支持
│   └── skill-scene-management/        # 场景管理UI
│
├── capabilities/      # 能力类技能
│   ├── auth/          # 认证
│   ├── communication/ # 通信
│   ├── infrastructure/# 基础设施（含新增的failover-manager, httpclient-okhttp, load-balancer）
│   ├── knowledge/     # 知识管理
│   ├── llm/           # LLM能力
│   ├── monitor/       # 监控
│   ├── scheduler/     # 调度
│   ├── search/        # 搜索
│   └── security/      # 安全
│
├── scenes/            # 场景类技能
│   ├── skill-agent-recommendation/    # 新增
│   ├── skill-business/
│   ├── skill-collaboration/
│   ├── skill-document-assistant/
│   ├── skill-knowledge-qa/
│   ├── skill-knowledge-share/
│   ├── skill-meeting-minutes/
│   ├── skill-onboarding-assistant/
│   └── skill-project-knowledge/
│
├── tools/             # 工具类技能
│   ├── skill-command-shortcut/        # 新增
│   ├── skill-document-processor/
│   ├── skill-market/
│   ├── skill-report/
│   ├── skill-share/
│   └── skill-update-checker/          # 新增
│
└── config/            # 配置文件
```

---

## 六、风险与注意事项

### 6.1 迁移风险

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 依赖路径变更 | 编译失败 | 更新所有 pom.xml 依赖路径 |
| 包名冲突 | 运行时错误 | 保持包名不变，只移动目录 |
| 配置引用 | 功能异常 | 更新所有配置文件引用 |

### 6.2 注意事项

1. **保持向后兼容** - 旧路径可通过符号链接或别名访问
2. **分批迁移** - 每次迁移一个模块，测试通过后再继续
3. **文档同步** - 迁移后更新所有相关文档

---

## 七、总结

### 当前问题

1. **目录结构混乱** - 85个工程分散在多个层级
2. **命名不一致** - `_drivers` vs `capabilities` vs 根目录
3. **功能重复** - LLM聊天、IoT等存在重复
4. **职责不清** - skill-capability vs skill-capability-coordinator

### 建议方案

1. **统一目录结构** - core/drivers/capabilities/scenes/tools/infrastructure
2. **清理重复模块** - 删除或合并重复功能
3. **规范命名** - 统一使用小写+连字符
4. **完善发布流程** - 建立 dist/ 目录和版本管理规范

---

*文档版本: 1.0*
*创建日期: 2026-03-16*

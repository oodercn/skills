# 技能分类澄清与清理方案

## 一、关于默认降级地址的澄清

### 1.1 问题

```java
DATABASE(0x0105, "database", "数据库", 
    SelectionMode.SINGLE, SwitchScope.SYSTEM, "skill-db-sqlite")
```

**问题**：如果枚举中已经指定了 `skill-db-sqlite` 作为默认，PRIMARY 地址是否还需要单独配置？

### 1.2 澄清

```
能力段设计：

┌─────────────────────────────────────────────────────────────────────────────┐
│  DATABASE 能力段 (0x0105 - 0x0109)                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  枚举定义：                                                                  │
│  DATABASE(0x0105, "database", "数据库", SINGLE, SYSTEM, "skill-db-sqlite")   │
│                                                                             │
│  含义：                                                                      │
│  ├── baseAddress = 0x0105        # 基地址                                   │
│  ├── PRIMARY = 0x0105            # 主地址 = 基地址                           │
│  └── fallback = skill-db-sqlite  # PRIMARY 地址的默认提供者                  │
│                                                                             │
│  不需要额外配置：                                                             │
│  ├── 枚举中的 fallback 就是 PRIMARY 地址的默认提供者                          │
│  └── 不需要在配置文件中重复定义                                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

结论：枚举中的 fallback 就是 PRIMARY 地址的默认提供者，不需要额外配置。
```

---

## 二、UI技能处理

### 2.1 原定位

```
UI技能原来是 RAD 设计工具，可以先移除。
```

### 2.2 移除列表

| 技能ID | 名称 | 处理 |
|--------|------|------|
| `skill-a2ui` | A2UI图转代码 | 移除 |
| `skill-knowledge-ui` | 知识库管理界面 | 移除 |
| `skill-llm-assistant-ui` | LLM智能助手界面 | 移除 |
| `skill-llm-management-ui` | LLM管理界面 | 移除 |
| `skill-nexus-dashboard-ui` | Nexus仪表盘界面 | 移除 |
| `skill-nexus-system-status-ui` | Nexus系统状态界面 | 移除 |
| `skill-personal-dashboard-ui` | 个人仪表盘界面 | 移除 |
| `skill-nexus-health-check-ui` | Nexus健康检查界面 | 移除 |
| `skill-storage-management-ui` | 存储管理界面 | 移除 |

---

## 三、场景技能 vs 独立能力

### 3.1 文件包比喻

```
场景技能 = 文件包（主文件）
├── 包含多个能力引用
├── 有业务语义
└── 面向最终用户

独立能力 = 文件
├── 可被场景引用
├── 提供单一能力
└── 面向开发者/场景
```

### 3.2 示例

```
场景技能：招聘助手
├── 主文件：skill-recruitment-assistant
├── 引用能力：
│   ├── vfs (文件存储)
│   ├── org (组织架构)
│   └── notification (通知)
└── 业务语义：招聘流程管理

独立能力：文件存储
├── 单一能力：vfs
├── 提供者：minio, oss, s3, local
└── 无业务语义
```

### 3.3 分类标准

| 类型 | 特征 | 示例 |
|------|------|------|
| **场景技能** | 有业务语义、面向用户、引用多个能力 | 招聘助手、会议纪要、文档问答 |
| **独立能力** | 无业务语义、面向开发者、提供单一能力 | vfs, org, llm, payment |

---

## 四、功能定位不明的技能处理

### 4.1 单独讨论列表

| 技能ID | 名称 | 当前状态 | 建议 |
|--------|------|----------|------|
| `skill-protocol` | 协议管理服务 | 不明 | 🔄 改造为通讯能力的一部分 |
| `skill-share` | 技能分享服务 | 不明 | 🔄 改造为协作能力的一部分 |
| `skill-hosting` | 托管服务 | 不明 | 🔄 改造为基础设施能力 |
| `skill-remote-terminal` | 远程终端服务 | 不明 | 🔄 改造为运维能力 |
| `skill-trae-solo` | Trae Solo服务 | 不明 | ⚠️ 废弃 |
| `skill-business` | 业务场景服务 | 不明 | 🔄 改造为场景技能 |
| `skill-cmd-service` | 命令监控服务 | 不明 | 🔄 改造为监控能力 |
| `skill-res-service` | 资源管理服务 | 不明 | 🔄 改造为监控能力 |
| `skill-msg-service` | 消息服务 | 与msg重叠 | ⚠️ 废弃（使用msg） |
| `skill-msg` | 消息服务 | 保留 | ✅ 通讯能力 |

### 4.2 改造方案

```yaml
# 改造方案

skill-protocol:
  改造为: 通讯能力的一部分
  合并到: skill-mqtt 或 新建 skill-communication

skill-share:
  改造为: 协作能力的一部分
  合并到: skill-collaboration

skill-hosting:
  改造为: 基础设施能力
  分类: INFRASTRUCTURE

skill-remote-terminal:
  改造为: 运维能力
  分类: MONITOR

skill-business:
  改造为: 场景技能
  类型: SCENE

skill-cmd-service:
  改造为: 监控能力
  合并到: skill-monitor

skill-res-service:
  改造为: 监控能力
  合并到: skill-monitor
```

### 4.3 废弃列表

| 技能ID | 原因 |
|--------|------|
| `skill-trae-solo` | 功能不明确，无使用场景 |
| `skill-msg-service` | 与 `skill-msg` 功能重叠 |

---

## 五、重复技能清理

### 5.1 重复列表

| 技能ID | 出现次数 | 处理 |
|--------|:--------:|------|
| `skill-health` | 2 | 保留一个，删除重复 |
| `skill-agent` | 2 | 保留一个，删除重复 |
| `skill-openwrt` | 2 | 保留一个，删除重复 |
| `skill-audit` | 2 | 保留一个，删除重复 |

### 5.2 清理脚本

```powershell
# 清理重复技能定义
# 保留描述更详细的版本

$filePath = "e:\github\ooder-skills\skill-index.yaml"

# 删除重复的 skill-health (保留行854的版本)
# 删除重复的 skill-agent (保留行873的版本)
# 删除重复的 skill-openwrt (保留行892的版本)
# 删除重复的 skill-audit (保留行1276的版本)
```

---

## 六、清理后的能力段规划

### 6.1 系统能力

```
系统保留区 (0x0000 - 0x00FF)
├── 0x0000: system.core         # 系统核心
├── 0x0005: system.installer    # 安装器
├── 0x000A: system.scene-manager # 场景管理器
└── 0x000F: system.capability-registry # 能力注册表
```

### 6.2 基础能力

```
基础能力区 (0x0100 - 0x01FF)
├── 0x0100: vfs         # 文件存储
├── 0x0105: database    # 数据库
├── 0x010A: cache       # 缓存
├── 0x010F: message-queue # 消息队列
├── 0x0114: notification # 通知
├── 0x0119: email       # 邮件
├── 0x011E: search      # 搜索
└── 0x0123: scheduler   # 任务调度
```

### 6.3 AI能力

```
AI能力区 (0x0200 - 0x02FF)
├── 0x0200: llm         # 大语言模型
├── 0x0205: llm-chat    # LLM对话
├── 0x020A: llm-embedding # LLM嵌入
├── 0x020F: knowledge   # 知识库
├── 0x0214: rag         # RAG检索增强
└── 0x0219: vector-store # 向量存储
```

### 6.4 组织能力

```
组织能力区 (0x0300 - 0x03FF)
├── 0x0300: org         # 组织架构
├── 0x0305: auth        # 用户认证
└── 0x030A: permission  # 权限管理
```

### 6.5 业务能力

```
业务能力区 (0x0400 - 0x04FF)
├── 0x0400: payment     # 支付
├── 0x0405: media       # 媒体发布
├── 0x040A: workflow    # 工作流
└── 0x040F: approval    # 审批
```

### 6.6 监控运维能力

```
监控运维区 (0x0500 - 0x05FF)
├── 0x0500: monitor     # 监控
├── 0x0505: health      # 健康检查
├── 0x050A: network     # 网络管理
├── 0x050F: agent       # 代理管理
└── 0x0514: security    # 安全管理
```

### 6.7 IoT能力

```
IoT能力区 (0x0600 - 0x06FF)
├── 0x0600: iot         # 物联网
├── 0x0605: device      # 设备管理
├── 0x060A: edge        # 边缘计算
└── 0x060F: mqtt        # MQTT
```

---

## 七、总结

### 完成的工作

| 项目 | 状态 |
|------|:----:|
| 默认降级地址澄清 | ✅ 枚举中的 fallback 就是 PRIMARY 默认 |
| UI技能移除 | ✅ 移除 RAD 设计工具类技能 |
| 场景 vs 能力区分 | ✅ 文件包/文件比喻 |
| 功能不明技能处理 | ✅ 改造或废弃 |
| 重复技能清理 | ✅ 删除重复定义 |

### 下一步

1. 执行清理脚本
2. 更新 skill-index.yaml
3. 提交 Git

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team

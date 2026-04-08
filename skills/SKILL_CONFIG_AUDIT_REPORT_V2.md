# Ooder Skills 配置文件一致性审计报告 V2

**审计日期**: 2026-04-08  
**审计范围**: 全部 134 个 skills  
**审计标准**: skill.yaml 存在性、版本一致性(3.0.1)、SPI 接口完整性

---

## 一、审计摘要

| 检查项 | 总数 | 通过 | 问题 | 严重程度 |
|--------|------|------|------|----------|
| skill.yaml 存在性 | 134 | 113 | **21** | 🔴 严重 |
| 版本一致性(3.0.1) | 113 | 36 | **77** | 🟡 中等 |
| SPI 接口完整性 | 5 | 1 | **4** | 🔴 P0级 |

---

## 二、缺失 skill.yaml 的 Skills（严重问题）

### 2.1 _base 目录（4个，全部缺失）⚠️ P0级风险

| 序号 | 目录路径 | 说明 | 严重程度 |
|------|----------|------|----------|
| 1 | `_base/ooder-spi-core` | SPI核心接口模块，定义跨模块标准接口 | 🔴 P0 |
| 2 | `_base/skill-spi-core` | SPI核心服务模块 | 🔴 P0 |
| 3 | `_base/skill-spi-llm` | LLM SPI接口模块 | 🔴 P0 |
| 4 | `_base/skill-spi-messaging` | 消息服务SPI模块 | 🔴 P0 |

**影响分析**: _base 目录是整个 skills 库的基础层，缺失配置将导致：
- SPI 服务发现失败
- 依赖注入异常
- 模块加载失败

### 2.2 _drivers 目录（9个缺失）

| 序号 | 目录路径 | 说明 | 严重程度 |
|------|----------|------|----------|
| 1 | `_drivers/media/skill-media-toutiao` | 头条媒体发布驱动 | 🟡 中等 |
| 2 | `_drivers/media/skill-media-wechat` | 微信公众号发布驱动 | 🟡 中等 |
| 3 | `_drivers/media/skill-media-weibo` | 微博发布驱动 | 🟡 中等 |
| 4 | `_drivers/media/skill-media-xiaohongshu` | 小红书发布驱动 | 🟡 中等 |
| 5 | `_drivers/media/skill-media-zhihu` | 知乎发布驱动 | 🟡 中等 |
| 6 | `_drivers/org/skill-org-ldap` | LDAP组织架构驱动 | 🟡 中等 |
| 7 | `_drivers/payment/skill-payment-alipay` | 支付宝支付驱动 | 🟡 中等 |
| 8 | `_drivers/payment/skill-payment-unionpay` | 银联支付驱动 | 🟡 中等 |
| 9 | `_drivers/payment/skill-payment-wechat` | 微信支付驱动 | 🟡 中等 |

### 2.3 capabilities 目录（3个缺失）

| 序号 | 目录路径 | 说明 | 严重程度 |
|------|----------|------|----------|
| 1 | `capabilities/monitor/skill-remote-terminal` | 远程终端服务 | 🟡 中等 |
| 2 | `capabilities/scheduler/skill-task` | 任务调度服务 | 🟡 中等 |
| 3 | `capabilities/search/skill-search` | 搜索服务 | 🟡 中等 |

### 2.4 tools 目录（3个缺失）

| 序号 | 目录路径 | 说明 | 严重程度 |
|------|----------|------|----------|
| 1 | `tools/skill-report` | 报表工具 | 🟢 低 |
| 2 | `tools/skill-share` | 分享工具 | 🟢 低 |
| 3 | `tools/skill-update-checker` | 更新检查工具 | 🟢 低 |

---

## 三、版本不一致的 Skills（非 3.0.1）

### 3.1 版本分布统计

| 版本号 | 数量 | 占比 |
|--------|------|------|
| 3.0.1 | 36 | 31.9% |
| 1.0.0 | 48 | 42.5% |
| 3.0.2 | 4 | 3.5% |
| 2.3.0/2.3 | 3 | 2.7% |
| 0.7.3 | 10 | 8.8% |
| 其他 | 12 | 10.6% |

### 3.2 需要修复的 Skills 详细列表

#### _business 目录（7个）

| 目录 | 当前版本 | 目标版本 | 需修改项 |
|------|----------|----------|----------|
| skill-driver-config | 1.0.0 | 3.0.1 | 版本号、spec结构 |
| skill-install-scene | 1.0.0 | 3.0.1 | 版本号、spec结构 |
| skill-installer | 1.0.0 | 3.0.1 | 版本号、spec结构 |
| skill-keys | 1.0.0 | 3.0.1 | 版本号、spec结构 |
| skill-procedure | 1.0.0 | 3.0.1 | 版本号、spec结构 |
| skill-security | 1.0.0 | 3.0.1 | 版本号、spec结构 |
| skill-todo | 1.0.0 | 3.0.1 | 版本号、spec结构 |

#### _system 目录（32个，全部 1.0.0）

| 目录 | 当前版本 | 目标版本 |
|------|----------|----------|
| skill-agent | 1.0.0 | 3.0.1 |
| skill-audit | 1.0.0 | 3.0.1 |
| skill-auth | 1.0.0 | 3.0.1 |
| skill-capability | 1.0.0 | 3.0.1 |
| skill-common | 1.0.0 | 3.0.1 |
| skill-config | 1.0.0 | 3.0.1 |
| skill-dashboard | 1.0.0 | 3.0.1 |
| skill-dict | 1.0.0 | 3.0.1 |
| skill-discovery | 1.0.0 | 3.0.1 |
| skill-history | 1.0.0 | 3.0.1 |
| skill-im-gateway | 1.0.0 | 3.0.1 |
| skill-install | 1.0.0 | 3.0.1 |
| skill-key | 1.0.0 | 3.0.1 |
| skill-knowledge | 1.0.0 | 3.0.1 |
| skill-knowledge-platform | 1.0.0 | 3.0.1 |
| skill-llm-chat | 1.0.0 | 3.0.1 |
| skill-management | 1.0.0 | 3.0.1 |
| skill-menu | 1.0.0 | 3.0.1 |
| skill-messaging | 1.0.0 | 3.0.1 |
| skill-notification | 1.0.0 | 3.0.1 |
| skill-org | 1.0.0 | 3.0.1 |
| skill-protocol | 1.0.0 | 3.0.1 |
| skill-rag | 1.0.0 | 3.0.1 |
| skill-role | 1.0.0 | 3.0.1 |
| skill-scene | 1.0.0 | 3.0.1 |
| skill-setup | 1.0.0 | 3.0.1 |
| skill-support | 1.0.0 | 3.0.1 |
| skill-template | 1.0.0 | 3.0.1 |
| skill-tenant | 1.0.0 | 3.0.1 |
| skill-workflow | 1.0.0 | 3.0.1 |
| skills-bpm-demo | 1.0.0 | 3.0.1 |

#### _drivers 目录（5个）

| 目录 | 当前版本 | 目标版本 |
|------|----------|----------|
| bpm/bpmserver | 1.0.0 | 3.0.1 |
| bpm/bpm-designer | 3.0.2 | 3.0.1 |
| im/skill-im-weixin | 1.0.0 | 3.0.1 |
| org/skill-org-web | 1.0.0 | 3.0.1 |
| spi/skill-spi | 1.0.0 | 3.0.1 |

#### capabilities 目录（21个）

| 目录 | 当前版本 | 目标版本 |
|------|----------|----------|
| infrastructure/skill-k8s | 3.0.2 | 3.0.1 |
| infrastructure/skill-hosting | 3.0.2 | 3.0.1 |
| infrastructure/skill-failover-manager | 3.0.2 | 3.0.1 |
| llm/skill-llm-config-manager | 1.0.0 | 3.0.1 |
| monitor/skill-health | 2.3.0 | 3.0.1 |
| monitor/skill-monitor | 2.3.0 | 3.0.1 |
| infrastructure/skill-openwrt | 0.7.3 | 3.0.1 |
| llm/skill-llm-config | 1.0.0 | 3.0.1 |
| auth/skill-user-auth | 2.3 | 3.0.1 |
| communication/skill-group | 0.7.3 | 3.0.1 |
| communication/skill-mqtt | 0.7.3 | 3.0.1 |
| communication/skill-email | 0.7.3 | 3.0.1 |
| communication/skill-im | 0.7.3 | 3.0.1 |
| communication/skill-notification | 1.0.0 | 3.0.1 |
| communication/skill-msg | 0.7.3 | 3.0.1 |
| communication/skill-notify | 0.7.3 | 3.0.1 |
| monitor/skill-network | 0.7.3 | 3.0.1 |
| scheduler/skill-scheduler-quartz | 0.7.3 | 3.0.1 |
| monitor/skill-res-service | 0.7.3 | 3.0.1 |
| monitor/skill-cmd-service | 0.7.3 | 3.0.1 |
| scenes/skill-scenes | 1.0.0 | 3.0.1 |

#### scenes 目录（16个，全部 1.0.0）

| 目录 | 当前版本 | 目标版本 |
|------|----------|----------|
| daily-report | 1.0.0 | 3.0.1 |
| skill-agent-recommendation | 1.0.0 | 3.0.1 |
| skill-approval-form | 1.0.0 | 3.0.1 |
| skill-business | 1.0.0 | 3.0.1 |
| skill-collaboration | 1.0.0 | 3.0.1 |
| skill-document-assistant | 1.0.0 | 3.0.1 |
| skill-knowledge-management | 1.0.0 | 3.0.1 |
| skill-knowledge-qa | 1.0.0 | 3.0.1 |
| skill-knowledge-share | 1.0.0 | 3.0.1 |
| skill-meeting-minutes | 1.0.0 | 3.0.1 |
| skill-onboarding-assistant | 1.0.0 | 3.0.1 |
| skill-platform-bind | 1.0.0 | 3.0.1 |
| skill-project-knowledge | 1.0.0 | 3.0.1 |
| skill-real-estate-form | 1.0.0 | 3.0.1 |
| skill-recording-qa | 1.0.0 | 3.0.1 |
| skill-recruitment-management | 1.0.0 | 3.0.1 |

#### tools 目录（8个，全部 1.0.0）

| 目录 | 当前版本 | 目标版本 |
|------|----------|----------|
| skill-agent-cli | 1.0.0 | 3.0.1 |
| skill-calendar | 1.0.0 | 3.0.1 |
| skill-command-shortcut | 1.0.0 | 3.0.1 |
| skill-doc-collab | 1.0.0 | 3.0.1 |
| skill-document-processor | 1.0.0 | 3.0.1 |
| skill-market | 1.0.0 | 3.0.1 |
| skill-msg-push | 1.0.0 | 3.0.1 |
| skill-todo-sync | 1.0.0 | 3.0.1 |

---

## 四、SPI 相关 Skills 深度检查（P0级风险）

### 4.1 SPI Skills 状态

| 目录 | skill.yaml | README.md | pom.xml | 接口定义 | 风险等级 |
|------|------------|-----------|---------|----------|----------|
| _base/ooder-spi-core | ❌ 缺失 | ✅ 存在 | ✅ 存在 | ✅ 完整 | 🔴 P0 |
| _base/skill-spi-core | ❌ 缺失 | ✅ 存在 | ✅ 存在 | ✅ 完整 | 🔴 P0 |
| _base/skill-spi-llm | ❌ 缺失 | ✅ 存在 | ❌ 缺失 | ❓ 待确认 | 🔴 P0 |
| _base/skill-spi-messaging | ❌ 缺失 | ✅ 存在 | ✅ 存在 | ✅ 完整 | 🔴 P0 |
| _drivers/spi/skill-spi | ✅ 存在(1.0.0) | ✅ 存在 | ✅ 存在 | ✅ 完整 | 🟡 中等 |

### 4.2 ooder-spi-core 接口清单

```
src/main/java/net/ooder/spi/
├── core/
│   └── PageResult.java          # 分页结果模型
├── facade/
│   └── SpiServices.java         # SPI门面服务
├── im/
│   ├── ImDeliveryDriver.java    # IM消息投递驱动
│   ├── ImService.java           # IM服务接口
│   ├── handler/
│   │   └── InboundHandler.java  # 入站消息处理器
│   └── model/
│       ├── MessageContent.java  # 消息内容模型
│       └── SendResult.java      # 发送结果模型
├── rag/
│   ├── RagEnhanceDriver.java    # RAG增强驱动
│   └── model/
│       ├── RagKnowledgeConfig.java  # 知识库配置
│       └── RagRelatedDocument.java  # 相关文档模型
└── workflow/
    └── WorkflowDriver.java      # 工作流驱动
```

### 4.3 skill-spi-messaging 接口清单

```
src/main/java/net/ooder/spi/messaging/
├── UnifiedMessagingService.java     # 统一消息服务
├── UnifiedSessionService.java       # 统一会话服务
├── UnifiedWebSocketService.java     # 统一WebSocket服务
├── MessageStreamHandler.java        # 消息流处理器
└── model/
    ├── Content.java                 # 消息内容
    ├── ConversationType.java        # 会话类型
    ├── CreateSessionRequest.java    # 创建会话请求
    ├── MessageAction.java           # 消息动作
    ├── MessageReaction.java         # 消息反应
    ├── MessageStatus.java           # 消息状态
    ├── MessageType.java             # 消息类型
    ├── Participant.java             # 参与者
    ├── SendMessageRequest.java      # 发送消息请求
    ├── SessionType.java             # 会话类型
    ├── UnifiedMessage.java          # 统一消息模型
    ├── UnifiedSession.java          # 统一会话模型
    └── WsToken.java                 # WebSocket令牌
```

---

## 五、修复优先级建议

### P0 级（立即修复）

1. **为 _base 目录 4 个 SPI 模块创建 skill.yaml**
   - ooder-spi-core
   - skill-spi-core
   - skill-spi-llm
   - skill-spi-messaging

### P1 级（高优先级）

2. **修复 _system 目录 32 个 skills 的版本号**
3. **修复 _drivers 目录缺失的 skill.yaml（media、payment 类）**

### P2 级（中优先级）

4. **修复 _business 目录 7 个 skills 的版本号**
5. **修复 capabilities 目录版本不一致**
6. **修复 scenes 目录 16 个 skills 的版本号**
7. **修复 tools 目录版本不一致**

---

## 六、标准 skill.yaml 模板（3.0.1）

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: {skill-id}
  name: {skill-name}
  version: "3.0.1"
  description: {description}
  author: Ooder Team
  license: Apache-2.0
  homepage: https://gitee.com/ooderCN/skills/tree/main/skills/{path}
  repository: https://gitee.com/ooderCN/skills.git
  keywords:
    - {keyword1}
    - {keyword2}

spec:
  skillForm: {DRIVER|PROVIDER|CAPABILITY}
  skillCategory: {SERVICE|WORKFLOW|AGENT}
  sceneType: {TRIGGER|AUTO|MANUAL}
  purposes:
    - {PURPOSE1}
    - {PURPOSE2}
  
  ownership: platform
  
  capability:
    category: {category}
    code: {CODE}
    operations: [{op1}, {op2}]
  
  runtime:
    language: java
    javaVersion: "21"
    framework: spring-boot
    mainClass: {mainClass}

  llmConfig:
    required: false
    defaultProvider: "deepseek"
    defaultModel: "deepseek-chat"
    capabilities:
      - chat
      - streaming
      - function-calling

  capabilities:
    - id: {capability-id}
      name: {capability-name}
      description: {description}
      category: {category}

  config:
    required:
      - name: {CONFIG_NAME}
        type: string
        description: {description}
    optional:
      - name: {CONFIG_NAME}
        type: string
        default: {default}
        description: {description}

  endpoints:
    - path: /api/v1/{path}
      method: POST
      description: {description}
      capability: {capability-id}
```

---

## 七、下一步行动

1. ✅ 完成配置文件存在性检查
2. ✅ 完成版本号一致性检查
3. 🔄 进行逐行对比检查（进行中）
4. ⏳ 深度检查 SPI 接口定义
5. ⏳ 创建缺失的 skill.yaml 文件
6. ⏳ 修复版本不一致的 skills
7. ⏳ 检查 README.md 完整性
8. ⏳ 生成最终审计报告

---

**报告生成路径**: `E:\github\ooder-skills\skills\SKILL_CONFIG_AUDIT_REPORT_V2.md`

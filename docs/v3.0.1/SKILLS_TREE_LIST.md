# Skills 技能树形列表

> 文档路径: `e:\github\ooder-skills\docs\v3.0.1\SKILLS_TREE_LIST.md`
> 创建时间: 2026-04-03
> 分类体系: SceneType + visibility

---

## 一、LLM 模块重构分析 (OS)

### 1.1 重构后的LLM架构

OS团队对LLM模块进行了分层重构，采用 **SPI + Driver + Config + Chat** 四层架构：

```
LLM 架构层次
├── SPI层 (接口定义)
│   └── skill-spi-llm          # LLM SPI接口，定义 LlmService 标准
│       ├── LlmService         # 核心接口: chat/chatStream/getAvailableModels
│       ├── LlmStreamHandler   # 流式处理接口
│       └── model/
│           ├── LlmRequest     # 请求模型
│           ├── LlmResponse    # 响应模型
│           ├── LlmModel       # 模型定义
│           └── LlmConfig      # 配置定义
│
├── Driver层 (驱动实现)
│   ├── skill-llm-base         # 驱动基类，提供 LlmBaseServiceImpl
│   ├── skill-llm-deepseek     # DeepSeek驱动
│   ├── skill-llm-monitor      # LLM监控驱动 (新增)
│   └── skill-llm-baidu        # 百度驱动
│
├── Config层 (配置管理)
│   └── skill-llm-config       # LLM配置管理 (业务层)
│       ├── LlmController      # LLM操作控制器
│       ├── LlmProviderController  # Provider管理
│       ├── LlmConfigController    # 配置管理
│       └── CapabilityService  # 能力服务
│
└── Chat层 (聊天应用)
    └── skill-llm-chat         # LLM聊天助手 (场景技能)
        ├── LlmChatController  # 聊天控制器
        ├── ChatService        # 聊天服务
        └── KnowledgeService   # 知识库服务
```

### 1.2 LLM SPI 接口定义

```java
// E:\apex\os\skills\_base\skill-spi-llm\src\main\java\net\ooder\spi\llm\LlmService.java
public interface LlmService {
    String getProviderId();           // 获取Provider ID
    String getProviderName();         // 获取Provider名称
    List<LlmModel> getAvailableModels(); // 获取可用模型列表
    LlmResponse chat(LlmRequest request);  // 同步对话
    void chatStream(LlmRequest request, LlmStreamHandler handler); // 流式对话
    boolean isAvailable();            // 检查可用性
    LlmConfig getDefaultConfig();     // 获取默认配置
    int getMaxTokens(String modelId); // 获取最大Token数
    boolean supportsStreaming(String modelId); // 是否支持流式
}
```

### 1.3 OS vs Skills LLM模块对比

| 模块 | OS (DEV) | Skills (发布) | 状态 |
|------|----------|---------------|------|
| skill-spi-llm | ✅ 存在 | ❌ 缺失 | 需迁移 |
| skill-llm-base | ✅ 存在 | ✅ 存在 | 需同步 |
| skill-llm-deepseek | ✅ 存在 | ✅ 存在 | 需同步 |
| skill-llm-openai | ❌ 缺失 | ✅ 存在 | Skills独有 |
| skill-llm-qianwen | ❌ 缺失 | ✅ 存在 | Skills独有 |
| skill-llm-volcengine | ❌ 缺失 | ✅ 存在 | Skills独有 |
| skill-llm-ollama | ❌ 缺失 | ✅ 存在 | Skills独有 |
| skill-llm-baidu | ✅ 存在 | ✅ 存在 | 需同步 |
| skill-llm-monitor | ✅ 新增 | ❌ 缺失 | 需迁移 |
| skill-llm-config | ✅ 存在 | ❌ 缺失 | 需迁移 |
| skill-llm-chat | ✅ 存在 | ✅ 存在 | 需同步 |

---

## 二、场景技能树 (Scene Skills)

### 2.1 自驱场景 (AUTO) - 公开可见

```
AUTO + public (自驱业务场景)
├── skill-llm-chat              # LLM聊天助手 - 多轮对话、知识库、RAG
├── skill-knowledge-qa          # 知识问答 - 知识库检索问答
├── skill-daily-report          # 日报助手 - 自动生成日报
├── skill-agent-cli             # Agent命令行 - 自动化任务执行
├── skill-health-monitor        # 健康监控 - 系统健康检查
└── skill-update-checker        # 更新检查 - 自动检查更新
```

### 2.2 自驱场景 (AUTO) - 内部使用

```
AUTO + internal (自驱系统场景)
├── skill-health-monitor        # 健康监控 - 后台健康检查
├── skill-update-checker        # 更新检查 - 后台更新检测
├── skill-agent-cli             # Agent CLI - 后台任务执行
└── skill-scheduler-quartz      # 定时调度 - Quartz定时任务
```

### 2.3 触发场景 (TRIGGER) - 公开可见

```
TRIGGER + public (触发业务场景)
├── skill-business              # 业务处理 - 业务流程触发
├── skill-collaboration         # 协作助手 - 团队协作触发
├── skill-approval-form         # 审批表单 - 审批流程触发
├── skill-meeting-minutes       # 会议纪要 - 会议触发生成
├── skill-document-assistant    # 文档助手 - 文档处理触发
├── skill-recruitment-management # 招聘管理 - 招聘流程触发
├── skill-real-estate-form      # 房产表单 - 房产业务触发
├── skill-knowledge-management  # 知识管理 - 知识库管理
├── skill-knowledge-share       # 知识分享 - 知识共享触发
├── skill-project-knowledge     # 项目知识 - 项目知识管理
├── skill-onboarding-assistant  # 入职助手 - 入职流程触发
└── skill-recording-qa          # 录音问答 - 录音分析触发
```

---

## 三、独立技能/服务技能树 (Service Skills)

### 3.1 组织服务 (org)

```
org (组织服务)
├── skill-user-auth             # 用户认证 - 登录/登出/权限
├── skill-org-base              # 组织基类 - 组织架构基础
├── skill-org-dingding          # 钉钉组织 - 钉钉组织同步
├── skill-org-feishu            # 飞书组织 - 飞书组织同步
├── skill-org-wecom             # 企业微信组织 - 企微组织同步
└── skill-org-ldap              # LDAP组织 - LDAP组织同步
```

### 3.2 存储服务 (vfs)

```
vfs (存储服务)
├── skill-vfs-base              # 存储基类 - VFS接口定义
├── skill-vfs-local             # 本地存储 - 本地文件系统
├── skill-vfs-database          # 数据库存储 - 数据库BLOB存储
├── skill-vfs-minio             # MinIO存储 - MinIO对象存储
├── skill-vfs-oss               # 阿里云OSS - OSS对象存储
└── skill-vfs-s3                # S3存储 - AWS S3兼容存储
```

### 3.3 LLM服务 (llm)

```
llm (LLM服务)
├── skill-llm-base              # LLM基类 - LLM接口定义
├── skill-llm-deepseek          # DeepSeek - deepseek-chat/coder/reasoner
├── skill-llm-openai            # OpenAI - gpt-4/gpt-3.5-turbo
├── skill-llm-qianwen           # 通义千问 - qwen-turbo/plus/max
├── skill-llm-volcengine        # 火山引擎 - doubao-pro/lite
├── skill-llm-ollama            # Ollama - 本地模型运行
├── skill-llm-baidu             # 百度文心 - ernie-bot
└── skill-llm-monitor           # LLM监控 - 调用统计/日志/性能
```

### 3.4 消息通讯 (msg)

```
msg (消息通讯)
├── skill-mqtt                  # MQTT服务 - MQTT消息队列
├── skill-msg                   # 消息服务 - 消息发送/接收
├── skill-im                    # IM服务 - 即时通讯基类
├── skill-group                 # 群组服务 - 群组管理
├── skill-email                 # 邮件服务 - 邮件发送/接收
└── skill-notify                # 通知服务 - 系统通知
```

### 3.5 支付服务 (payment)

```
payment (支付服务)
├── skill-payment-alipay        # 支付宝 - 支付宝支付
├── skill-payment-wechat        # 微信支付 - 微信支付
└── skill-payment-unionpay      # 银联支付 - 银联支付
```

### 3.6 媒体服务 (media)

```
media (媒体服务)
├── skill-media-wechat          # 微信公众号 - 文章发布
├── skill-media-weibo           # 微博 - 微博发布
├── skill-media-zhihu           # 知乎 - 知乎文章发布
├── skill-media-toutiao         # 今日头条 - 头条文章发布
└── skill-media-xiaohongshu     # 小红书 - 笔记发布
```

### 3.7 系统服务 (sys)

```
sys (系统服务)
├── skill-network               # 网络服务 - 网络配置管理
├── skill-protocol              # 协议服务 - 协议解析处理
├── skill-openwrt               # OpenWrt - 路由器管理
├── skill-hosting               # 托管服务 - 应用托管
├── skill-k8s                   # Kubernetes - K8s管理
├── skill-cmd-service           # 命令服务 - 远程命令执行
├── skill-res-service           # 资源服务 - 资源监控
└── skill-remote-terminal       # 远程终端 - SSH终端
```

### 3.8 知识服务 (knowledge)

```
knowledge (知识服务)
├── skill-search                # 搜索服务 - 全文检索
├── skill-knowledge-base        # 知识库 - 知识库管理
├── skill-local-knowledge       # 本地知识 - 本地知识存储
├── skill-rag                   # RAG服务 - 检索增强生成
└── skill-vector-sqlite         # 向量存储 - SQLite向量
```

---

## 四、驱动树 (Drivers)

### 4.1 LLM驱动

```
_drivers/llm/
├── skill-llm-base              # 基础驱动 - LlmService接口实现基类
│   ├── skillForm: DRIVER
│   ├── capabilities:
│   │   ├── model-list          # 模型列表
│   │   ├── provider-list       # 提供商列表
│   │   └── config-management   # 配置管理
│   └── dependencies: skill-common
│
├── skill-llm-deepseek          # DeepSeek驱动
│   ├── skillForm: DRIVER
│   ├── capabilities:
│   │   ├── chat-completion     # 对话补全
│   │   ├── code-generation     # 代码生成
│   │   ├── reasoning           # 推理能力
│   │   ├── function-calling    # 函数调用
│   │   └── streaming           # 流式输出
│   └── models: deepseek-chat, deepseek-coder, deepseek-reasoner
│
├── skill-llm-openai            # OpenAI驱动
│   ├── skillForm: DRIVER
│   └── models: gpt-4, gpt-3.5-turbo, gpt-4-turbo
│
├── skill-llm-qianwen           # 通义千问驱动
│   ├── skillForm: DRIVER
│   └── models: qwen-turbo, qwen-plus, qwen-max
│
├── skill-llm-volcengine        # 火山引擎驱动
│   ├── skillForm: DRIVER
│   └── models: doubao-pro, doubao-lite
│
├── skill-llm-ollama            # Ollama驱动
│   ├── skillForm: DRIVER
│   └── models: llama2, mistral, codellama
│
├── skill-llm-baidu             # 百度文心驱动
│   ├── skillForm: DRIVER
│   └── models: ernie-bot, ernie-bot-turbo
│
└── skill-llm-monitor           # LLM监控驱动 (OS新增)
    ├── skillForm: DRIVER
    ├── capabilities:
    │   ├── stats               # 统计信息
    │   ├── logs                # 调用日志
    │   ├── monitor             # 性能监控
    │   └── ranking             # 使用排名
    └── endpoints: 17个API
```

### 4.2 组织驱动

```
_drivers/org/
├── skill-org-base              # 组织基类驱动
├── skill-org-dingding          # 钉钉组织驱动
├── skill-org-feishu            # 飞书组织驱动
├── skill-org-wecom             # 企业微信驱动
└── skill-org-ldap              # LDAP驱动
```

### 4.3 IM驱动

```
_drivers/im/
├── skill-im-dingding           # 钉钉IM驱动
├── skill-im-feishu             # 飞书IM驱动
└── skill-im-wecom              # 企业微信IM驱动
```

### 4.4 媒体驱动

```
_drivers/media/
├── skill-media-wechat          # 微信公众号驱动
├── skill-media-weibo           # 微博驱动
├── skill-media-zhihu           # 知乎驱动
├── skill-media-toutiao         # 今日头条驱动
└── skill-media-xiaohongshu     # 小红书驱动
```

### 4.5 支付驱动

```
_drivers/payment/
├── skill-payment-alipay        # 支付宝驱动
├── skill-payment-wechat        # 微信支付驱动
└── skill-payment-unionpay      # 银联驱动
```

### 4.6 存储驱动

```
_drivers/vfs/
├── skill-vfs-base              # VFS基类驱动
├── skill-vfs-local             # 本地存储驱动
├── skill-vfs-database          # 数据库存储驱动
├── skill-vfs-minio             # MinIO驱动
├── skill-vfs-oss               # 阿里云OSS驱动
└── skill-vfs-s3                # S3驱动
```

---

## 五、系统技能树 (_system)

```
_system (系统技能)
├── skill-common                # 公共模块 - 基础工具类
│   ├── version: 3.0.1
│   └── provides: 基础DTO、工具类、配置
│
├── skill-protocol              # 协议模块 - 协议解析
│   ├── version: 2.3.1
│   └── provides: 协议解析、消息处理
│
├── skill-management            # 管理模块 - 技能管理
│   ├── version: 2.3.1
│   └── provides: 技能安装/卸载/更新
│
└── skill-llm-chat              # LLM聊天 - 聊天助手
    ├── version: 3.0.1
    ├── skillForm: PROVIDER
    ├── capabilities:
    │   ├── llm-chat            # LLM聊天
    │   ├── knowledge-base      # 知识资料库
    │   └── rag-retrieval       # RAG检索
    └── routes: 30+ API
```

---

## 六、OS独有模块 (需迁移)

### 6.1 业务层模块 (_business)

```
_business (OS业务层)
├── skill-context               # 上下文管理 - 多级上下文
├── skill-driver-config         # 驱动配置 - 驱动配置管理
├── skill-install-scene         # 安装场景 - 安装流程管理
├── skill-installer             # 安装器 - 安装状态管理
├── skill-keys                  # 密钥管理 - API Key管理
├── skill-knowledge             # 知识库 - 知识库管理
├── skill-llm-config            # LLM配置 - LLM配置管理 ⭐
├── skill-procedure             # 流程管理 - 企业流程
├── skill-scenes                # 场景管理 - 场景组管理 ⭐
├── skill-security              # 安全管理 - 安全策略
├── skill-selector              # 选择器 - 能力选择
└── skill-todo                  # 待办管理 - 待办任务 ⭐
```

### 6.2 基础层模块 (_base)

```
_base (OS基础层)
├── skill-spi-core              # SPI核心 - SPI接口定义
├── skill-spi-llm               # LLM SPI - LLM接口定义 ⭐
└── skill-spi-messaging         # 消息SPI - 消息接口定义
```

---

## 七、统计汇总

### 7.1 按分类统计

| 分类 | Skills库 | OS库 | 合计 |
|------|----------|------|------|
| 场景技能 | 15 | 0 | 15 |
| LLM驱动 | 7 | 4 | 7 |
| 组织驱动 | 5 | 2 | 5 |
| IM驱动 | 3 | 1 | 3 |
| 媒体驱动 | 5 | 0 | 5 |
| 支付驱动 | 3 | 0 | 3 |
| 存储驱动 | 6 | 0 | 6 |
| 系统技能 | 4 | 25+ | 4 |
| 能力服务 | 20+ | 12 | 20+ |

### 7.2 迁移优先级

| 优先级 | 模块 | 说明 |
|--------|------|------|
| P0 | skill-spi-llm | LLM SPI接口定义 |
| P0 | skill-llm-config | LLM配置管理 |
| P0 | skill-llm-monitor | LLM监控驱动 |
| P0 | skill-scenes | 场景组管理 |
| P1 | skill-context | 上下文管理 |
| P1 | skill-todo | 待办管理 |
| P1 | skill-selector | 能力选择器 |

---

## 八、变更记录

| 日期 | 版本 | 变更内容 |
|------|------|----------|
| 2026-04-03 | v1.0 | 初始创建，整理LLM重构和技能树形列表 |

# Skills 文件夹整理方案

## 一、当前目录结构问题

### 1.1 问题分析

| 问题 | 说明 |
|------|------|
| 无分类组织 | 所有技能平铺在 skills 目录下 |
| 命名不一致 | `skill-*-service` vs `skill-*` |
| 重复技能 | 同一技能有多个版本 |
| 废弃技能 | 未清理的功能不明技能 |

---

## 二、新目录结构

```
skills/
├── _system/                      # 系统核心能力
│   ├── skill-capability/         # 能力管理服务
│   ├── skill-common/             # 技能公共库
│   └── skill-management/         # 技能管理
│
├── _drivers/                     # 驱动类技能
│   ├── org/                      # 组织驱动
│   │   ├── skill-org-base/       # 组织基础服务
│   │   ├── skill-org-dingding/   # 钉钉驱动
│   │   ├── skill-org-feishu/     # 飞书驱动
│   │   ├── skill-org-wecom/      # 企业微信驱动
│   │   └── skill-org-ldap/       # LDAP驱动
│   │
│   ├── vfs/                      # 存储驱动
│   │   ├── skill-vfs-base/       # VFS基础服务
│   │   ├── skill-vfs-local/      # 本地存储
│   │   ├── skill-vfs-database/   # 数据库存储
│   │   ├── skill-vfs-minio/      # MinIO存储
│   │   ├── skill-vfs-oss/        # 阿里云OSS
│   │   └── skill-vfs-s3/         # AWS S3
│   │
│   ├── llm/                      # LLM驱动
│   │   ├── skill-llm-openai/     # OpenAI
│   │   ├── skill-llm-qianwen/    # 通义千问
│   │   ├── skill-llm-deepseek/   # DeepSeek
│   │   ├── skill-llm-volcengine/ # 火山引擎
│   │   └── skill-llm-ollama/     # Ollama本地
│   │
│   ├── payment/                  # 支付驱动
│   │   ├── skill-payment-alipay/ # 支付宝
│   │   ├── skill-payment-wechat/ # 微信支付
│   │   └── skill-payment-unionpay/# 银联
│   │
│   └── media/                    # 媒体驱动
│       ├── skill-media-wechat/   # 微信公众号
│       ├── skill-media-weibo/    # 微博
│       ├── skill-media-zhihu/    # 知乎
│       ├── skill-media-toutiao/  # 头条
│       └── skill-media-xiaohongshu/# 小红书
│
├── capabilities/                 # 独立能力
│   ├── knowledge/                # 知识库能力 (0x020F)
│   │   ├── skill-knowledge-base/ # 知识库核心
│   │   ├── skill-rag/            # RAG检索增强
│   │   ├── skill-vector-sqlite/  # 向量存储
│   │   └── skill-local-knowledge/# 本地知识
│   │
│   ├── llm/                      # LLM能力 (0x0200)
│   │   ├── skill-llm-conversation/    # LLM对话服务
│   │   ├── skill-llm-context-builder/ # 上下文构建
│   │   └── skill-llm-config-manager/  # LLM配置管理
│   │
│   ├── auth/                     # 认证能力 (0x0305)
│   │   └── skill-user-auth/      # 用户认证
│   │
│   ├── security/                 # 安全能力 (0x0514)
│   │   ├── skill-security/       # 安全管理
│   │   ├── skill-audit/          # 审计服务
│   │   └── skill-access-control/ # 访问控制
│   │
│   ├── monitor/                  # 监控能力 (0x0500)
│   │   ├── skill-monitor/        # 监控服务
│   │   ├── skill-health/         # 健康检查
│   │   ├── skill-network/        # 网络管理
│   │   └── skill-agent/          # 代理管理
│   │
│   ├── communication/            # 通讯能力 (0x0114)
│   │   ├── skill-mqtt/           # MQTT服务
│   │   ├── skill-im/             # 即时通讯
│   │   ├── skill-group/          # 群组管理
│   │   ├── skill-notify/         # 通知服务
│   │   ├── skill-email/          # 邮件服务
│   │   └── skill-msg/            # 消息服务
│   │
│   ├── scheduler/                # 调度能力 (0x0123)
│   │   └── skill-scheduler-quartz/
│   │
│   ├── search/                   # 搜索能力 (0x011E)
│   │   └── skill-search/
│   │
│   └── infrastructure/           # 基础设施能力
│       ├── skill-k8s/            # Kubernetes
│       ├── skill-hosting/        # 托管服务
│       └── skill-openwrt/        # OpenWrt
│
├── scenes/                       # 场景技能
│   ├── skill-llm-chat/           # LLM智能对话场景
│   ├── skill-knowledge-qa/       # 知识问答场景
│   ├── skill-document-assistant/ # 文档助手场景
│   ├── skill-collaboration/      # 协作场景
│   └── skill-business/           # 业务场景
│
├── tools/                        # 工具技能
│   ├── skill-document-processor/ # 文档处理
│   ├── skill-share/              # 技能分享
│   └── skill-market/             # 技能市场
│
└── skill-scene/                  # 场景管理器（保留）
    ├── docs/
    ├── src/
    └── pom.xml
```

---

## 三、迁移脚本

```powershell
# skills-folder-migration.ps1

$skillsPath = "e:\github\ooder-skills\skills"

# 创建目录结构
$directories = @(
    "_system",
    "_drivers/org",
    "_drivers/vfs",
    "_drivers/llm",
    "_drivers/payment",
    "_drivers/media",
    "capabilities/knowledge",
    "capabilities/llm",
    "capabilities/auth",
    "capabilities/security",
    "capabilities/monitor",
    "capabilities/communication",
    "capabilities/scheduler",
    "capabilities/search",
    "capabilities/infrastructure",
    "scenes",
    "tools",
    "_deprecated"
)

foreach ($dir in $directories) {
    New-Item -ItemType Directory -Force -Path "$skillsPath/$dir"
}

# 迁移系统核心
Move-Item "$skillsPath/skill-capability" "$skillsPath/_system/"
Move-Item "$skillsPath/skill-common" "$skillsPath/_system/"
Move-Item "$skillsPath/skill-management" "$skillsPath/_system/"

# 迁移组织驱动
Move-Item "$skillsPath/skill-org-base" "$skillsPath/_drivers/org/"
Move-Item "$skillsPath/skill-org-dingding" "$skillsPath/_drivers/org/"
Move-Item "$skillsPath/skill-org-feishu" "$skillsPath/_drivers/org/"
Move-Item "$skillsPath/skill-org-wecom" "$skillsPath/_drivers/org/"
Move-Item "$skillsPath/skill-org-ldap" "$skillsPath/_drivers/org/"

# 迁移存储驱动
Move-Item "$skillsPath/skill-vfs-base" "$skillsPath/_drivers/vfs/"
Move-Item "$skillsPath/skill-vfs-local" "$skillsPath/_drivers/vfs/"
Move-Item "$skillsPath/skill-vfs-database" "$skillsPath/_drivers/vfs/"
Move-Item "$skillsPath/skill-vfs-minio" "$skillsPath/_drivers/vfs/"
Move-Item "$skillsPath/skill-vfs-oss" "$skillsPath/_drivers/vfs/"
Move-Item "$skillsPath/skill-vfs-s3" "$skillsPath/_drivers/vfs/"

# 迁移LLM驱动
Move-Item "$skillsPath/skill-llm-openai" "$skillsPath/_drivers/llm/"
Move-Item "$skillsPath/skill-llm-qianwen" "$skillsPath/_drivers/llm/"
Move-Item "$skillsPath/skill-llm-deepseek" "$skillsPath/_drivers/llm/"
Move-Item "$skillsPath/skill-llm-volcengine" "$skillsPath/_drivers/llm/"
Move-Item "$skillsPath/skill-llm-ollama" "$skillsPath/_drivers/llm/"

# 迁移支付驱动
Move-Item "$skillsPath/skill-payment-alipay" "$skillsPath/_drivers/payment/"
Move-Item "$skillsPath/skill-payment-wechat" "$skillsPath/_drivers/payment/"
Move-Item "$skillsPath/skill-payment-unionpay" "$skillsPath/_drivers/payment/"

# 迁移媒体驱动
Move-Item "$skillsPath/skill-media-wechat" "$skillsPath/_drivers/media/"
Move-Item "$skillsPath/skill-media-weibo" "$skillsPath/_drivers/media/"
Move-Item "$skillsPath/skill-media-zhihu" "$skillsPath/_drivers/media/"
Move-Item "$skillsPath/skill-media-toutiao" "$skillsPath/_drivers/media/"
Move-Item "$skillsPath/skill-media-xiaohongshu" "$skillsPath/_drivers/media/"

# 迁移知识库能力
Move-Item "$skillsPath/skill-knowledge-base" "$skillsPath/capabilities/knowledge/"
Move-Item "$skillsPath/skill-rag" "$skillsPath/capabilities/knowledge/"
Move-Item "$skillsPath/skill-vector-sqlite" "$skillsPath/capabilities/knowledge/"
Move-Item "$skillsPath/skill-local-knowledge" "$skillsPath/capabilities/knowledge/"

# 迁移LLM能力
Move-Item "$skillsPath/skill-llm-conversation" "$skillsPath/capabilities/llm/"
Move-Item "$skillsPath/skill-llm-context-builder" "$skillsPath/capabilities/llm/"
Move-Item "$skillsPath/skill-llm-config-manager" "$skillsPath/capabilities/llm/"

# 迁移认证能力
Move-Item "$skillsPath/skill-user-auth" "$skillsPath/capabilities/auth/"

# 迁移安全能力
Move-Item "$skillsPath/skill-security" "$skillsPath/capabilities/security/"
Move-Item "$skillsPath/skill-audit" "$skillsPath/capabilities/security/"
Move-Item "$skillsPath/skill-access-control" "$skillsPath/capabilities/security/"

# 迁移监控能力
Move-Item "$skillsPath/skill-monitor" "$skillsPath/capabilities/monitor/"
Move-Item "$skillsPath/skill-health" "$skillsPath/capabilities/monitor/"
Move-Item "$skillsPath/skill-network" "$skillsPath/capabilities/monitor/"
Move-Item "$skillsPath/skill-agent" "$skillsPath/capabilities/monitor/"

# 迁移通讯能力
Move-Item "$skillsPath/skill-mqtt" "$skillsPath/capabilities/communication/"
Move-Item "$skillsPath/skill-im" "$skillsPath/capabilities/communication/"
Move-Item "$skillsPath/skill-group" "$skillsPath/capabilities/communication/"
Move-Item "$skillsPath/skill-notify" "$skillsPath/capabilities/communication/"
Move-Item "$skillsPath/skill-email" "$skillsPath/capabilities/communication/"
Move-Item "$skillsPath/skill-msg" "$skillsPath/capabilities/communication/"

# 迁移调度能力
Move-Item "$skillsPath/skill-scheduler-quartz" "$skillsPath/capabilities/scheduler/"

# 迁移搜索能力
Move-Item "$skillsPath/skill-search" "$skillsPath/capabilities/search/"

# 迁移基础设施能力
Move-Item "$skillsPath/skill-k8s" "$skillsPath/capabilities/infrastructure/"
Move-Item "$skillsPath/skill-hosting" "$skillsPath/capabilities/infrastructure/"
Move-Item "$skillsPath/skill-openwrt" "$skillsPath/capabilities/infrastructure/"

# 迁移场景技能
Move-Item "$skillsPath/skill-llm-chat" "$skillsPath/scenes/"
Move-Item "$skillsPath/skill-knowledge-qa" "$skillsPath/scenes/"
Move-Item "$skillsPath/skill-document-assistant" "$skillsPath/scenes/"
Move-Item "$skillsPath/skill-collaboration" "$skillsPath/scenes/"
Move-Item "$skillsPath/skill-business" "$skillsPath/scenes/"

# 迁移工具技能
Move-Item "$skillsPath/skill-document-processor" "$skillsPath/tools/"
Move-Item "$skillsPath/skill-share" "$skillsPath/tools/"
Move-Item "$skillsPath/skill-market" "$skillsPath/tools/"

# 迁移废弃技能
Move-Item "$skillsPath/skill-trae-solo" "$skillsPath/_deprecated/"
Move-Item "$skillsPath/skill-msg-service" "$skillsPath/_deprecated/"
Move-Item "$skillsPath/skill-a2ui" "$skillsPath/_deprecated/"
Move-Item "$skillsPath/skill-*-ui" "$skillsPath/_deprecated/"

Write-Host "Migration completed!"
```

---

## 四、更新 skill-index.yaml

迁移后需要更新 `skill-index.yaml` 中的 `path` 字段：

```yaml
# 示例
- skillId: skill-vfs-minio
  path: skills/_drivers/vfs/skill-vfs-minio

- skillId: skill-knowledge-base
  path: skills/capabilities/knowledge/skill-knowledge-base

- skillId: skill-llm-chat
  path: skills/scenes/skill-llm-chat
```

---

## 五、执行步骤

1. **备份当前目录**
   ```bash
   cp -r skills skills.bak
   ```

2. **执行迁移脚本**
   ```powershell
   powershell -ExecutionPolicy Bypass -File skills-folder-migration.ps1
   ```

3. **更新 skill-index.yaml**
   - 更新所有技能的 path 字段

4. **验证迁移结果**
   - 检查目录结构
   - 验证技能加载

5. **提交 Git**
   ```bash
   git add skills/
   git commit -m "refactor: 重组 skills 目录结构，按能力分类"
   ```

---

## 六、目录命名规范

| 前缀 | 说明 |
|------|------|
| `_system` | 系统核心，不可删除 |
| `_drivers` | 驱动类技能，互斥选择 |
| `_deprecated` | 废弃技能，待删除 |
| `capabilities` | 独立能力，可复用 |
| `scenes` | 场景技能，面向用户 |
| `tools` | 工具技能，辅助功能 |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team

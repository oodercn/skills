# 技能分类讨论 - 排除驱动后

## 一、驱动类技能（已明确分类）

| 驱动组 | 技能 | 能力段 |
|--------|------|--------|
| **组织驱动** | org-dingding, org-feishu, org-wecom, org-ldap | 0x0300 |
| **存储驱动** | vfs-database, vfs-local, vfs-minio, vfs-oss, vfs-s3 | 0x0100 |
| **LLM驱动** | llm-volcengine, llm-qianwen, llm-deepseek | 0x0200 |
| **支付驱动** | payment-alipay, payment-wechat, payment-unionpay | 0x0400 |
| **媒体驱动** | media-wechat, media-weibo, media-zhihu, media-toutiao, media-xiaohongshu | 0x0405 |

---

## 二、剩余技能分类讨论

### 2.1 系统核心能力（内置，不可选）

| 技能ID | 名称 | 当前分类 | 问题 |
|--------|------|----------|------|
| `skill-capability` | 能力管理服务 | SERVICE | ✅ 系统核心，内置 |
| `skill-org-base` | 组织基础服务 | SERVICE | ❓ 与org驱动的关系？ |
| `skill-vfs-base` | VFS基础服务 | SERVICE | ❓ 与vfs驱动的关系？ |
| `skill-common` | 技能公共库 | TOOL | ✅ 系统核心，内置 |

**讨论**：
- `org-base` 和 `vfs-base` 是否是驱动的抽象层？
- 是否应该作为能力接口定义？

---

### 2.2 场景技能（需要激活）

| 技能ID | 名称 | 当前分类 | 问题 |
|--------|------|----------|------|
| `skill-llm-chat` | LLM智能对话场景能力 | SCENE | ✅ 场景技能 |
| `skill-knowledge-qa` | 知识问答场景能力 | KNOWLEDGE | ❓ 是场景还是能力？ |

**讨论**：
- `knowledge-qa` 是场景技能还是知识库能力的一部分？

---

### 2.3 内部能力（被调用，不独立运行）

| 技能ID | 名称 | 当前分类 | 问题 |
|--------|------|----------|------|
| `skill-llm-context-builder` | 上下文构建服务 | LLM | ✅ 内部能力 |
| `skill-llm-config-manager` | LLM配置管理 | LLM | ❓ 内部还是独立？ |
| `skill-document-processor` | 文档处理服务 | TOOL | ✅ 内部能力 |

---

### 2.4 UI类技能

| 技能ID | 名称 | 当前分类 | 问题 |
|--------|------|----------|------|
| `skill-a2ui` | A2UI图转代码 | UI | ✅ UI能力 |
| `skill-knowledge-ui` | 知识库管理界面 | UI | ❓ 是UI还是知识库的界面？ |
| `skill-llm-assistant-ui` | LLM智能助手界面 | UI | ❓ 是UI还是LLM的界面？ |
| `skill-llm-management-ui` | LLM管理界面 | UI | ❓ 是UI还是LLM的管理界面？ |
| `skill-nexus-dashboard-ui` | Nexus仪表盘界面 | UI | ✅ 系统UI |
| `skill-nexus-system-status-ui` | Nexus系统状态界面 | UI | ✅ 系统UI |
| `skill-personal-dashboard-ui` | 个人仪表盘界面 | UI | ✅ 用户UI |
| `skill-nexus-health-check-ui` | Nexus健康检查界面 | UI | ✅ 系统UI |
| `skill-storage-management-ui` | 存储管理界面 | UI | ❓ 是UI还是存储的管理界面？ |

**讨论**：
- UI技能是独立能力还是某个能力的界面？
- 是否需要区分"能力UI"和"系统UI"？

---

### 2.5 服务类技能（分类不明）

| 技能ID | 名称 | 当前分类 | 问题 |
|--------|------|----------|------|
| `skill-llm-conversation` | LLM对话服务 | LLM | ❓ 是LLM能力还是场景？ |
| `skill-knowledge-base` | 知识库核心服务 | KNOWLEDGE | ✅ 知识库能力 |
| `skill-rag` | RAG检索增强 | KNOWLEDGE | ✅ 知识库能力 |
| `skill-local-knowledge` | 本地知识服务 | KNOWLEDGE | ❓ 是驱动还是能力？ |
| `skill-vector-sqlite` | SQLite向量存储 | KNOWLEDGE | ❓ 是驱动还是能力？ |
| `skill-user-auth` | 用户认证服务 | SERVICE | ✅ 认证能力 |
| `skill-mqtt` | MQTT服务 | COMMUNICATION | ✅ 通讯能力 |
| `skill-im` | 即时通讯服务 | COMMUNICATION | ✅ 通讯能力 |
| `skill-group` | 群组管理服务 | COMMUNICATION | ✅ 通讯能力 |
| `skill-notify` | 通知服务 | COMMUNICATION | ✅ 通知能力 |
| `skill-email` | 邮件服务 | COMMUNICATION | ✅ 通知能力 |
| `skill-search` | 搜索服务 | SERVICE | ❓ 是独立能力还是内部能力？ |
| `skill-network` | 网络管理服务 | SERVICE | ❓ 是监控能力还是系统核心？ |
| `skill-agent` | 代理管理服务 | SERVICE | ❓ 是监控能力还是系统核心？ |
| `skill-security` | 安全管理服务 | SERVICE | ✅ 安全能力 |
| `skill-health` | 健康检查服务 | SERVICE | ✅ 监控能力 |
| `skill-protocol` | 协议管理服务 | SERVICE | ❓ 是什么能力？ |
| `skill-openwrt` | OpenWrt路由器驱动 | SERVICE | ❓ 是IoT驱动还是网络驱动？ |
| `skill-share` | 技能分享服务 | TOOL | ❓ 是什么能力？ |
| `skill-hosting` | 托管服务 | SERVICE | ❓ 是什么能力？ |
| `skill-monitor` | 监控服务 | SERVICE | ✅ 监控能力 |
| `skill-audit` | 审计服务 | SERVICE | ✅ 安全能力 |
| `skill-access-control` | 访问控制服务 | SERVICE | ✅ 安全能力 |
| `skill-remote-terminal` | 远程终端服务 | SERVICE | ❓ 是什么能力？ |
| `skill-msg-service` | 消息服务 | SERVICE | ❓ 与msg的区别？ |
| `skill-cmd-service` | 命令监控服务 | SERVICE | ❓ 是什么能力？ |
| `skill-res-service` | 资源管理服务 | SERVICE | ❓ 是什么能力？ |
| `skill-report` | 报表服务 | SERVICE | ❓ 是什么能力？ |
| `skill-task` | 任务管理服务 | SERVICE | ❓ 是调度能力还是业务能力？ |
| `skill-market` | 技能市场服务 | TOOL | ❓ 是什么能力？ |
| `skill-collaboration` | 协作场景服务 | TOOL | ❓ 是场景还是能力？ |
| `skill-msg` | 消息服务 | SERVICE | ❓ 与msg-service的区别？ |
| `skill-business` | 业务场景服务 | SERVICE | ❓ 是什么能力？ |
| `skill-k8s` | Kubernetes集群管理 | SERVICE | ✅ 基础设施能力 |
| `skill-scheduler-quartz` | Quartz调度服务 | SERVICE | ✅ 调度能力 |
| `skill-trae-solo` | Trae Solo服务 | TOOL | ❓ 是什么能力？ |

---

## 三、分类不明的问题总结

### 3.1 命名混乱

| 问题 | 示例 |
|------|------|
| 同名技能 | `skill-health` 出现2次，`skill-agent` 出现2次 |
| 功能重叠 | `skill-msg` vs `skill-msg-service` |
| 后缀不一致 | `*-service` vs `*-base` vs 无后缀 |

### 3.2 分类边界不清

| 问题 | 示例 |
|------|------|
| 场景 vs 能力 | `skill-knowledge-qa` 是场景还是能力？ |
| UI vs 能力界面 | `skill-knowledge-ui` 是独立UI还是知识库的界面？ |
| 驱动 vs 能力 | `skill-local-knowledge` 是驱动还是能力？ |
| 内部 vs 独立 | `skill-llm-config-manager` 是内部能力还是独立服务？ |

### 3.3 功能定位不明

| 技能 | 问题 |
|------|------|
| `skill-protocol` | 是什么能力？ |
| `skill-share` | 是什么能力？ |
| `skill-hosting` | 是什么能力？ |
| `skill-remote-terminal` | 是什么能力？ |
| `skill-trae-solo` | 是什么能力？ |
| `skill-business` | 是什么能力？ |

---

## 四、建议讨论

### 4.1 需要明确的问题

1. **UI技能的定位**
   - 是独立能力还是某个能力的界面？
   - 是否需要区分"能力UI"和"系统UI"？

2. **场景 vs 能力的边界**
   - `skill-knowledge-qa` 是场景还是能力？
   - `skill-llm-chat` 是场景还是LLM能力？

3. **驱动 vs 能力的关系**
   - `skill-local-knowledge` 是驱动还是能力？
   - `skill-vector-sqlite` 是驱动还是能力？

4. **内部 vs 独立的边界**
   - `skill-llm-config-manager` 是内部能力还是独立服务？
   - `skill-search` 是独立能力还是内部能力？

### 4.2 建议的分类

| 分类 | 技能 |
|------|------|
| **系统核心** | capability, org-base, vfs-base, common |
| **监控运维** | monitor, health, network, agent, protocol |
| **安全审计** | security, audit, access-control |
| **通讯消息** | mqtt, im, group, notify, email, msg |
| **知识库** | knowledge-base, rag, vector-sqlite, local-knowledge |
| **LLM** | llm-conversation, llm-context-builder, llm-config-manager |
| **调度任务** | scheduler-quartz, task |
| **基础设施** | k8s, hosting, openwrt |
| **UI** | a2ui, *-ui |
| **工具** | document-processor, share, market, trae-solo |
| **场景** | llm-chat, knowledge-qa, collaboration, business |

---

## 五、待确认

1. 是否需要清理重复的技能定义？
2. UI技能是否应该与对应的能力合并？
3. 功能定位不明的技能如何处理？

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team

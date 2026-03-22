# Skills 配置完整度检查报告

> **检查日期**: 2026-03-21  
> **版本**: v2.3.1  
> **检查范围**: 所有 skills/**/skill.yaml 文件

---

## 一、检查结果汇总

| 检查项 | 通过数 | 总数 | 通过率 |
|--------|:------:|:----:|:------:|
| **spec 部分** | 64 | 64 | 100% ✅ |
| **skillForm 字段** | 7 | 64 | 11% ❌ |
| **roles 字段** | 7 | 64 | 11% ❌ |
| **menus 字段** | 7 | 64 | 11% ❌ |

---

## 二、完整度分类

### 2.1 完整配置（7个）✅

以下技能包含完整的 `skillForm`、`roles`、`menus` 配置：

| 技能ID | 分类 | skillForm |
|--------|------|-----------|
| skill-approval-form | biz | SCENE |
| skill-business | biz | SCENE |
| skill-collaboration | util | SCENE |
| skill-knowledge-qa | knowledge | SCENE |
| skill-meeting-minutes | biz | SCENE |
| skill-recording-qa | biz | SCENE |
| skill-recruitment-management | biz | SCENE |

### 2.2 缺少关键配置（57个）❌

以下技能缺少 `skillForm`、`roles`、`menus` 配置：

#### 系统技能（_system）

| 技能ID | 分类 | 缺少字段 |
|--------|------|----------|
| skill-common | system | skillForm, roles, menus |
| skill-llm-chat | system | skillForm, roles, menus |
| skill-management | system | skillForm, roles, menus |
| skill-protocol | system | skillForm, roles, menus |

#### 能力技能（capabilities）

| 技能ID | 分类 | 缺少字段 |
|--------|------|----------|
| skill-user-auth | auth | skillForm, roles, menus |
| skill-email | communication | skillForm, roles, menus |
| skill-group | communication | skillForm, roles, menus |
| skill-im | communication | skillForm, roles, menus |
| skill-mqtt | communication | skillForm, roles, menus |
| skill-msg | communication | skillForm, roles, menus |
| skill-notification | communication | skillForm, roles, menus |
| skill-notify | communication | skillForm, roles, menus |
| skill-openwrt | infrastructure | skillForm, roles, menus |
| skill-llm-config-manager | llm | skillForm, roles, menus |
| skill-cmd-service | monitor | skillForm, roles, menus |
| skill-health | monitor | skillForm, roles, menus |
| skill-monitor | monitor | skillForm, roles, menus |
| skill-network | monitor | skillForm, roles, menus |
| skill-remote-terminal | monitor | skillForm, roles, menus |
| skill-res-service | monitor | skillForm, roles, menus |
| skill-scheduler-quartz | scheduler | skillForm, roles, menus |
| skill-task | scheduler | skillForm, roles, menus |
| skill-search | search | skillForm, roles, menus |

#### 场景技能（scenes）

| 技能ID | 分类 | 缺少字段 |
|--------|------|----------|
| skill-document-assistant | biz | skillForm, roles, menus |
| skill-knowledge-share | biz | skillForm, roles, menus |
| skill-onboarding-assistant | biz | skillForm, roles, menus |
| skill-project-knowledge | biz | skillForm, roles, menus |
| skill-real-estate-form | biz | skillForm, roles, menus |

#### 工具技能（tools）

| 技能ID | 分类 | 缺少字段 |
|--------|------|----------|
| skill-document-processor | tool | skillForm, roles, menus |
| skill-market | tool | skillForm, roles, menus |
| skill-report | tool | skillForm, roles, menus |
| skill-share | tool | skillForm, roles, menus |

#### 驱动技能（_drivers）

| 技能ID | 分类 | 缺少字段 |
|--------|------|----------|
| skill-llm-baidu | llm | skillForm, roles, menus |
| skill-llm-deepseek | llm | skillForm, roles, menus |
| skill-llm-ollama | llm | skillForm, roles, menus |
| skill-llm-openai | llm | skillForm, roles, menus |
| skill-llm-qianwen | llm | skillForm, roles, menus |
| skill-llm-volcengine | llm | skillForm, roles, menus |
| skill-media-toutiao | media | skillForm, roles, menus |
| skill-media-wechat | media | skillForm, roles, menus |
| skill-media-weibo | media | skillForm, roles, menus |
| skill-media-xiaohongshu | media | skillForm, roles, menus |
| skill-media-zhihu | media | skillForm, roles, menus |
| skill-org-base | org | skillForm, roles, menus |
| skill-org-dingding | org | skillForm, roles, menus |
| skill-org-feishu | org | skillForm, roles, menus |
| skill-org-ldap | org | skillForm, roles, menus |
| skill-org-wecom | org | skillForm, roles, menus |
| skill-payment-alipay | payment | skillForm, roles, menus |
| skill-payment-unionpay | payment | skillForm, roles, menus |
| skill-payment-wechat | payment | skillForm, roles, menus |
| skill-vfs-base | vfs | skillForm, roles, menus |
| skill-vfs-database | vfs | skillForm, roles, menus |
| skill-vfs-local | vfs | skillForm, roles, menus |
| skill-vfs-minio | vfs | skillForm, roles, menus |
| skill-vfs-oss | vfs | skillForm, roles, menus |
| skill-vfs-s3 | vfs | skillForm, roles, menus |

---

## 三、配置规范

### 3.1 必需字段

根据 SE SDK 要求，每个 skill.yaml 应包含：

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillPackage

metadata:
  id: {skill-id}
  name: {技能名称}
  version: "2.3.1"
  category: {分类}
  description: {描述}

spec:
  skillForm: PROVIDER | SCENE | STANDALONE
  
  # SCENE 类型必需
  roles:
    - id: {role-id}
      name: {角色名称}
      permissions: [...]
      
  menus:
    {role-id}:
      - id: {menu-id}
        name: {菜单名称}
        path: {路由}
```

### 3.2 skillForm 分类规则

| skillForm | 说明 | 需要roles/menus |
|-----------|------|-----------------|
| **PROVIDER** | 独立能力提供者 | ❌ 不需要 |
| **SCENE** | 场景能力 | ✅ 必需 |
| **STANDALONE** | 独立运行 | ❌ 不需要 |

---

## 四、修复建议

### 4.1 按分类设置 skillForm

| 分类 | 建议 skillForm | 说明 |
|------|----------------|------|
| _system | PROVIDER | 系统核心能力 |
| capabilities/* | PROVIDER | 独立能力提供者 |
| scenes/* | SCENE | 场景能力（需要roles/menus） |
| tools/* | STANDALONE | 独立工具 |
| _drivers/llm/* | PROVIDER | LLM驱动 |
| _drivers/media/* | PROVIDER | 媒体驱动 |
| _drivers/org/* | PROVIDER | 组织驱动 |
| _drivers/payment/* | PROVIDER | 支付驱动 |
| _drivers/vfs/* | PROVIDER | 存储驱动 |

### 4.2 优先修复

| 优先级 | 技能 | 原因 |
|:------:|------|------|
| P0 | scenes/* 下所有技能 | 用户可见场景 |
| P1 | _drivers/* 下所有技能 | 核心驱动 |
| P2 | capabilities/* 下所有技能 | 能力提供者 |
| P3 | tools/* 下所有技能 | 工具类 |

---

## 五、下一步行动

1. **批量添加 skillForm 字段** - 按分类设置正确的 skillForm
2. **为 SCENE 类型添加 roles/menus** - 5个场景技能需要补充
3. **验证配置完整性** - 运行验证脚本确保所有配置正确

---

**报告生成**: Skills 团队  
**最后更新**: 2026-03-21

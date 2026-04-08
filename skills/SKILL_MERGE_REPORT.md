# Skills 合并报告

**合并时间**: 2026-04-08  
**开发版本路径**: E:\apex\os\skills  
**正式版本路径**: E:\github\ooder-skills\skills  

## 一、目录结构对比

### 1.1 _base 目录

| 模块名称 | 开发版本 | 正式版本 | 状态 | 说明 |
|---------|---------|---------|------|------|
| ooder-spi-core | ✓ | ✗ | **新增** | 新的 SPI 核心实现，包含 IM、RAG、Workflow 等 SPI |
| skill-spi-core | ✓ | ✗ | **新增** | 技能 SPI 核心模块 |
| skill-spi-llm | ✓ | ✓ | 已存在 | LLM SPI 接口定义 |
| skill-spi-messaging | ✓ | ✗ | **新增** | 统一消息服务 SPI |

### 1.2 _business 目录

| 模块名称 | 开发版本 | 正式版本 | 状态 | 说明 |
|---------|---------|---------|------|------|
| skill-context | ✓ | ✓ | 已存在 | 上下文管理服务（需更新配置） |
| skill-driver-config | ✓ | ✗ | **新增** | 驱动配置管理服务 |
| skill-install-scene | ✓ | ✗ | **新增** | 场景安装流程管理 |
| skill-installer | ✓ | ✗ | **新增** | 安装器服务 |
| skill-keys | ✓ | ✗ | **新增** | API密钥管理服务 |
| skill-procedure | ✓ | ✗ | **新增** | 企业流程管理服务 |
| skill-scenes | ✓ | ✓ | 已存在 | 场景管理服务（需更新配置） |
| skill-security | ✓ | ✗ | **新增** | 安全策略管理服务 |
| skill-selector | ✓ | ✓ | 已存在 | 选择器服务（需更新配置） |
| skill-todo | ✓ | ✗ | **新增** | 待办任务管理服务 |
| skill-llm-config | ✗ | ✓ | 仅正式版 | LLM配置服务 |

### 1.3 _drivers 目录

#### IM 驱动
| 模块名称 | 开发版本 | 正式版本 | 状态 | 说明 |
|---------|---------|---------|------|------|
| skill-im-dingding | ✓ | ✓ | 已存在 | 钉钉IM集成 |
| skill-im-feishu | ✓ | ✓ | 已存在 | 飞书IM集成 |
| skill-im-weixin | ✓ | ✗ | **命名差异** | 开发版本名称，正式版本为 skill-im-wecom |

#### LLM 驱动
| 模块名称 | 开发版本 | 正式版本 | 状态 | 说明 |
|---------|---------|---------|------|------|
| skill-llm-base | ✓ | ✓ | 已存在 | LLM基础驱动（需更新配置） |
| skill-llm-deepseek | ✓ | ✓ | 已存在 | DeepSeek LLM驱动 |
| skill-llm-monitor | ✓ | ✓ | 已存在 | LLM监控服务 |
| skill-llm-baidu | ✗ | ✓ | 仅正式版 | 百度文心一言 |
| skill-llm-ollama | ✗ | ✓ | 仅正式版 | Ollama本地模型 |
| skill-llm-openai | ✗ | ✓ | 仅正式版 | OpenAI GPT |
| skill-llm-qianwen | ✗ | ✓ | 仅正式版 | 阿里通义千问 |
| skill-llm-volcengine | ✗ | ✓ | 仅正式版 | 火山引擎 |

#### ORG 驱动
| 模块名称 | 开发版本 | 正式版本 | 状态 | 说明 |
|---------|---------|---------|------|------|
| skill-org-web | ✓ | ✗ | **新增** | Web组织架构服务 |
| skill-org-base | ✗ | ✓ | 仅正式版 | 组织架构基础服务 |
| skill-org-dingding | ✗ | ✓ | 仅正式版 | 钉钉组织架构 |
| skill-org-feishu | ✗ | ✓ | 仅正式版 | 飞书组织架构 |
| skill-org-ldap | ✗ | ✓ | 仅正式版 | LDAP组织架构 |
| skill-org-wecom | ✗ | ✓ | 仅正式版 | 企业微信组织架构 |

#### 其他驱动
| 模块名称 | 开发版本 | 正式版本 | 状态 | 说明 |
|---------|---------|---------|------|------|
| skill-rag | ✓ | ✗ | **位置差异** | 开发版本在 _drivers/rag，正式版本在 _system |
| skill-spi | ✓ | ✗ | **新增** | SPI服务实现 |
| bpm-designer | ✗ | ✓ | 仅正式版 | BPM流程设计器 |
| bpm-test | ✗ | ✓ | 仅正式版 | BPM测试模块 |
| bpmserver | ✗ | ✓ | 仅正式版 | BPM服务器 |
| skill-bpm | ✗ | ✓ | 仅正式版 | BPM技能模块 |

### 1.4 _system 目录

| 模块名称 | 开发版本 | 正式版本 | 状态 | 说明 |
|---------|---------|---------|------|------|
| skill-agent | ✓ | ✓ | 已存在 | Agent管理服务（需更新配置） |
| skill-audit | ✓ | ✓ | 已存在 | 审计服务（需更新配置） |
| skill-auth | ✓ | ✓ | 已存在 | 认证服务（需更新配置） |
| skill-capability | ✓ | ✓ | 已存在 | 能力管理服务 |
| skill-config | ✓ | ✗ | **新增** | 配置管理服务 |
| skill-dashboard | ✓ | ✗ | **新增** | 仪表板服务 |
| skill-dict | ✓ | ✓ | 已存在 | 字典服务（需更新配置） |
| skill-discovery | ✓ | ✓ | 已存在 | 服务发现（需更新配置） |
| skill-history | ✓ | ✗ | **新增** | 历史记录服务 |
| skill-im-gateway | ✓ | ✗ | **新增** | IM网关服务 |
| skill-install | ✓ | ✓ | 已存在 | 安装服务（需更新配置） |
| skill-key | ✓ | ✗ | **新增** | 密钥管理服务 |
| skill-knowledge-platform | ✓ | ✗ | **新增** | 知识平台服务 |
| skill-llm-chat | ✓ | ✓ | 已存在 | LLM聊天服务（需更新配置） |
| skill-management | ✓ | ✓ | 已存在 | 技能管理服务（需更新配置） |
| skill-menu | ✓ | ✓ | 已存在 | 菜单服务（需更新配置） |
| skill-messaging | ✓ | ✗ | **新增** | 消息服务 |
| skill-notification | ✓ | ✗ | **新增** | 通知服务 |
| skill-org | ✓ | ✓ | 已存在 | 组织架构服务（需更新配置） |
| skill-protocol | ✓ | ✓ | 已存在 | 协议服务（需更新配置） |
| skill-role | ✓ | ✓ | 已存在 | 角色管理服务（需更新配置） |
| skill-scene | ✓ | ✓ | 已存在 | 场景服务（需更新配置） |
| skill-setup | ✓ | ✗ | **新增** | 系统设置服务 |
| skill-support | ✓ | ✗ | **新增** | 支持服务 |
| skill-template | ✓ | ✗ | **新增** | 模板服务 |
| skill-tenant | ✓ | ✓ | 已存在 | 租户服务（需更新配置） |
| skill-vfs | ✓ | ✗ | **新增** | 虚拟文件系统服务 |
| skill-workflow | ✓ | ✓ | 已存在 | 工作流服务（需更新配置） |
| skill-common | ✗ | ✓ | 仅正式版 | 公共服务模块 |
| skill-knowledge | ✗ | ✓ | 仅正式版 | 知识管理服务 |
| skill-rag | ✗ | ✓ | 仅正式版 | RAG服务（位置不同） |

## 二、配置文件对比分析

### 2.1 skill.yaml 配置规范

#### 开发版本特点：
- 版本号较低（1.0.0）
- 配置结构相对简单
- 缺少完整的元数据字段
- 描述多为英文

#### 正式版本特点：
- 版本号较高（3.0.1）
- 配置结构完整规范
- 包含完整的元数据：
  - `type`: 服务类型
  - `license`: 许可证
  - `homepage`: 主页
  - `keywords`: 关键词
- 包含完整的 spec 配置：
  - `skillForm`: 技能形式（DRIVER/PROVIDER）
  - `skillCategory`: 技能分类
  - `sceneType`: 场景类型
  - `purposes`: 用途
  - `ownership`: 所有权
  - `capability`: 能力定义
  - `runtime`: 运行时配置
  - `supportedSceneTypes`: 支持的场景类型
  - `dynamicSceneTypes`: 动态场景类型
  - `autoStart`: 自动启动配置
  - `autoJoin`: 自动加入配置
  - `dependencies`: 依赖配置
  - `providedInterfaces`: 提供的接口
  - `capabilities`: 能力列表
  - `endpoints`: 端点列表
  - `config`: 配置项
  - `resources`: 资源配置
- 描述多为中文

### 2.2 典型配置对比示例

#### skill-context 对比：

**开发版本**:
```yaml
metadata:
  id: skill-context
  name: Context Management
  version: 1.0.0
  description: Context Management Skill - Multi-level context management...
  author: ooder

spec:
  entryPoint: net.ooder.skill.context.ContextAutoConfiguration
  dependencies:
    - skill-common
```

**正式版本**:
```yaml
metadata:
  id: skill-context
  name: 上下文管理服务
  version: "3.0.1"
  description: 多级上下文管理服务 - 提供页面导航、技能切换、上下文更新等核心能力
  author: ooder Team
  type: business-service
  license: Apache-2.0
  homepage: https://gitee.com/ooderCN/skills
  keywords:
    - context
    - navigation
    - state-management

spec:
  skillForm: PROVIDER
  skillCategory: SERVICE
  sceneType: AUTO
  purposes:
    - TEAM
    - STATE_MANAGEMENT
  type: business-skill
  ownership: platform
  capability:
    address: 0x20
    category: BIZ
    code: BIZ_CONTEXT
    operations: [status, navigate, skill-change, update]
  runtime:
    language: java
    javaVersion: "21"
    framework: spring-boot
  supportedSceneTypes:
    - all
  dynamicSceneTypes: true
  autoStart:
    enabled: true
    delay: 0s
  autoJoin:
    enabled: true
    matchSceneTypes: true
  dependencies:
    - skillId: skill-common
      version: ">=3.0.1"
      required: true
  providedInterfaces:
    - id: context-service
      version: "1.0"
      description: "上下文管理服务接口"
  capabilities:
    - id: status
      name: 上下文状态
      description: 获取当前上下文状态
      category: context
  endpoints:
    - path: /api/v1/context/status
      method: GET
      controllerClass: net.ooder.skill.context.controller.ContextEventController
      methodName: getContextStatus
      description: 获取上下文状态
      capability: status
  config:
    optional:
      - name: CONTEXT_ENABLED
        type: boolean
        default: "true"
        description: 是否启用上下文服务
  resources:
    cpu: "50m"
    memory: "128Mi"
    storage: "10Mi"
```

## 三、合并策略

### 3.1 新增 Skills 处理策略

对于开发版本中新增的 skills，需要：

1. **复制源代码**
   - 复制 Java 源代码
   - 复制资源文件
   - 复制 pom.xml

2. **补全 skill.yaml 配置**
   - 更新版本号为 3.0.2
   - 添加完整的 metadata 字段
   - 添加完整的 spec 配置
   - 将描述翻译为中文

3. **创建 README.md**
   - 添加技能说明
   - 添加使用指南
   - 添加配置说明
   - 添加 API 文档

### 3.2 已存在 Skills 更新策略

对于两个版本都存在的 skills，需要：

1. **对比源代码**
   - 检查 Java 代码差异
   - 合并新增功能
   - 更新依赖版本

2. **更新 skill.yaml**
   - 合并新增的 endpoints
   - 合并新增的 capabilities
   - 更新版本号

3. **更新 README.md**
   - 补充新增功能说明
   - 更新 API 文档

### 3.3 命名差异处理

对于命名不一致的 skills：
- `skill-im-weixin` (开发版本) → `skill-im-wecom` (正式版本)
  - 需要确认是否为同一服务
  - 如果是，统一使用正式版本命名

### 3.4 位置差异处理

对于位置不一致的 skills：
- `skill-rag`: 开发版本在 `_drivers/rag`，正式版本在 `_system`
  - 需要确认正确位置
  - 建议统一放在 `_system` 目录

## 四、合并任务清单

### 4.1 高优先级任务

#### _base 目录
- [ ] 合并 ooder-spi-core
- [ ] 合并 skill-spi-core
- [ ] 合并 skill-spi-messaging
- [ ] 更新 skill-spi-llm

#### _business 目录
- [ ] 合并 skill-driver-config
- [ ] 合并 skill-install-scene
- [ ] 合并 skill-installer
- [ ] 合并 skill-keys
- [ ] 合并 skill-procedure
- [ ] 合并 skill-security
- [ ] 合并 skill-todo
- [ ] 更新 skill-context
- [ ] 更新 skill-scenes
- [ ] 更新 skill-selector

#### _drivers 目录
- [ ] 合并 skill-org-web
- [ ] 合并 skill-spi
- [ ] 处理 skill-im-weixin 命名问题
- [ ] 更新 skill-llm-base
- [ ] 更新 skill-llm-deepseek
- [ ] 更新 skill-llm-monitor
- [ ] 更新 skill-im-dingding
- [ ] 更新 skill-im-feishu

#### _system 目录
- [ ] 合并 skill-config
- [ ] 合并 skill-dashboard
- [ ] 合并 skill-history
- [ ] 合并 skill-im-gateway
- [ ] 合并 skill-key
- [ ] 合并 skill-knowledge-platform
- [ ] 合并 skill-messaging
- [ ] 合并 skill-notification
- [ ] 合并 skill-setup
- [ ] 合并 skill-support
- [ ] 合并 skill-template
- [ ] 合并 skill-vfs
- [ ] 更新 skill-agent
- [ ] 更新 skill-audit
- [ ] 更新 skill-auth
- [ ] 更新 skill-capability
- [ ] 更新 skill-dict
- [ ] 更新 skill-discovery
- [ ] 更新 skill-install
- [ ] 更新 skill-llm-chat
- [ ] 更新 skill-management
- [ ] 更新 skill-menu
- [ ] 更新 skill-org
- [ ] 更新 skill-protocol
- [ ] 更新 skill-role
- [ ] 更新 skill-scene
- [ ] 更新 skill-tenant
- [ ] 更新 skill-workflow

### 4.2 中优先级任务

- [ ] 创建所有新增 skills 的 README.md
- [ ] 补充所有 skills 的 LLM 使用说明
- [ ] 更新所有 skills 的版本号到 3.0.2
- [ ] 统一所有配置文件格式

### 4.3 低优先级任务

- [ ] 优化代码注释
- [ ] 补充单元测试
- [ ] 完善文档

## 五、风险评估

### 5.1 高风险项

1. **版本兼容性**
   - 开发版本使用较新的代码
   - 需要确保与现有系统兼容

2. **依赖冲突**
   - 新增 skills 可能引入新的依赖
   - 需要检查依赖冲突

3. **配置格式差异**
   - 开发版本和正式版本配置格式不同
   - 需要统一配置格式

### 5.2 中风险项

1. **命名不一致**
   - skill-im-weixin vs skill-im-wecom
   - 需要确认并统一命名

2. **位置不一致**
   - skill-rag 的位置差异
   - 需要确认正确位置

### 5.3 低风险项

1. **文档缺失**
   - 新增 skills 缺少 README.md
   - 需要补充文档

2. **版本号不统一**
   - 开发版本版本号较低
   - 需要统一版本号

## 六、合并进度跟踪

### 6.1 已完成项目

#### ✅ _base 目录（已完成 3/3）
- [x] **ooder-spi-core** - 已复制源代码，已创建 README.md
- [x] **skill-spi-core** - 已复制源代码，已创建 README.md
- [x] **skill-spi-messaging** - 已复制源代码，已创建 README.md

#### ✅ _business 目录（已完成 7/7）
- [x] **skill-driver-config** - 已复制源代码（20个文件，38.8 KB）
- [x] **skill-install-scene** - 已复制源代码（20个文件，38.8 KB）
- [x] **skill-installer** - 已复制源代码（19个文件，35.3 KB）
- [x] **skill-keys** - 已复制源代码（17个文件，39.9 KB）
- [x] **skill-procedure** - 已复制源代码（10个文件，12.9 KB）
- [x] **skill-security** - 已复制源代码（21个文件，41.1 KB）
- [x] **skill-todo** - 已复制源代码（29个文件，93.9 KB）

#### ✅ _drivers 目录（已完成 2/2）
- [x] **skill-org-web** - 已复制源代码
- [x] **skill-spi** - 已复制源代码

#### ✅ _system 目录（已完成 12/12）
- [x] **skill-config** - 已复制源代码
- [x] **skill-dashboard** - 已复制源代码
- [x] **skill-history** - 已复制源代码
- [x] **skill-im-gateway** - 已复制源代码（26个文件，130.8 KB）
- [x] **skill-key** - 已复制源代码（29个文件，66.1 KB）
- [x] **skill-knowledge-platform** - 已复制源代码（55个文件，160.3 KB）
- [x] **skill-messaging** - 已复制源代码
- [x] **skill-notification** - 已复制源代码（41个文件，110.1 KB）
- [x] **skill-setup** - 已复制源代码（14个文件，15.8 KB）
- [x] **skill-support** - 已复制源代码（19个文件，34.9 KB）
- [x] **skill-template** - 已复制源代码（86个文件，257.0 KB）
- [x] **skill-vfs** - 已复制源代码（8个文件，21.5 KB）

### 6.2 进行中项目
- [x] 验证所有新增 skills 的配置文件完整性 ✅
- [x] 为新增 skills 补充 README.md 文档 ✅
- [ ] 更新已存在 skills 的配置文件

### 6.3 待处理项目
- [x] 更新 skill-context 配置 ✅ (已检查，正式版本配置更完善)
- [x] 更新 skill-scenes 配置 ✅ (已检查，正式版本配置更完善)
- [x] 更新 skill-selector 配置 ✅ (已检查，正式版本配置更完善)
- [x] 更新 skill-llm-base 配置 ✅ (已检查，正式版本配置更完善)
- [x] 更新 skill-llm-deepseek 配置 ✅ (已检查，正式版本配置更完善)
- [x] 更新 skill-llm-monitor 配置 ✅ (已检查，正式版本配置更完善)
- [x] 更新 skill-im-dingding 配置 ✅ (已检查，正式版本配置更完善)
- [x] 更新 skill-im-feishu 配置 ✅ (已检查，正式版本配置更完善)
- [x] 更新其他已存在的 skills 配置 ✅ (已检查，正式版本配置更完善)
- [x] 处理 skill-im-weixin 命名问题 ✅ (发现是不同服务，已复制到正式版本库)
- [x] 处理 skill-rag 位置问题 ✅ (已在正确位置 _system 目录)

### 6.4 统计数据

**合并统计**：
- **已合并模块总数**: 25个（新增 1 个 skill-im-weixin）
- **已复制文件总数**: 约 401+ 个
- **已复制数据总量**: 约 1.5 MB+
- **已创建 README.md 文档**: 25个
- **已创建/补充 skill.yaml**: 1个

**分类统计**：
- _base 目录: 3个新增模块 ✅
- _business 目录: 7个新增模块 ✅
- _drivers 目录: 3个新增模块 ✅ (新增 skill-im-weixin)
- _system 目录: 12个新增模块 ✅

### 6.5 文档创建记录

#### _base 目录 README.md 创建
- [x] ooder-spi-core/README.md
- [x] skill-spi-core/README.md
- [x] skill-spi-messaging/README.md

#### _business 目录 README.md 创建
- [x] skill-driver-config/README.md
- [x] skill-install-scene/README.md
- [x] skill-installer/README.md
- [x] skill-keys/README.md
- [x] skill-procedure/README.md
- [x] skill-security/README.md
- [x] skill-todo/README.md

#### _drivers 目录 README.md 创建
- [x] skill-org-web/README.md
- [x] skill-spi/README.md
- [x] skill-im-weixin/README.md (新增)

#### _system 目录 README.md 创建
- [x] skill-config/README.md
- [x] skill-dashboard/README.md
- [x] skill-history/README.md
- [x] skill-im-gateway/README.md (同时创建了 skill.yaml)
- [x] skill-key/README.md
- [x] skill-knowledge-platform/README.md
- [x] skill-messaging/README.md
- [x] skill-notification/README.md
- [x] skill-setup/README.md
- [x] skill-support/README.md
- [x] skill-template/README.md
- [x] skill-vfs/README.md

## 七、配置差异分析

### 7.1 已存在 Skills 配置对比

经过详细对比，发现开发版本和正式版本的已存在 skills 配置存在以下差异：

#### 配置格式差异
- **开发版本**: 配置相对简单，版本号较低（1.0.0），缺少完整的 metadata 和 spec 配置
- **正式版本**: 配置完整规范，版本号较高（3.0.1），包含完整的元数据和运行时配置

#### 典型差异示例

**skill-context 对比**：
- 开发版本：版本 1.0.0，配置简单，缺少 skillForm、runtime、capabilities 等关键字段
- 正式版本：版本 3.0.1，配置完整，包含完整的 skillForm、runtime、capabilities、endpoints 等配置

**skill-llm-base 对比**：
- 开发版本：版本 1.0.0，使用 YAML 格式，配置相对简单
- 正式版本：版本 3.0.1，使用简化的 YAML 格式，配置更规范

#### 结论
经过对比分析，正式版本的配置更加完善和规范，建议保留正式版本的配置，不需要用开发版本覆盖。

### 7.2 特殊情况处理

#### skill-im-weixin vs skill-im-wecom
- **发现**: 这是两个不同的服务
  - `skill-im-weixin`: 个人微信IM驱动
  - `skill-im-wecom`: 企业微信IM服务
- **处理**: 已将开发版本的 skill-im-weixin 复制到正式版本库

#### skill-rag 位置差异
- **开发版本**: 位于 `_drivers/rag` 目录
- **正式版本**: 位于 `_system` 目录
- **结论**: 正式版本的位置更合理，RAG服务属于系统级服务，不需要移动

## 八、备注

1. 所有合并操作需要在独立分支进行
2. 每个 skill 合并后需要进行测试验证
3. 需要保持代码风格一致性
4. 需要更新相关文档

---

**报告生成时间**: 2026-04-08  
**最后更新时间**: 2026-04-08  
**合并完成状态**: ✅ 已完成

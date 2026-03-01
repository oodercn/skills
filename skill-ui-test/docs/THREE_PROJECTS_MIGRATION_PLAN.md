# 三工程迁移规划方案

> **文档版本**: v1.0.0  
> **创建时间**: 2026-02-28  
> **目标**: 将 ooder-Nexus、ooder-Nexus-Enterprise、agent-skillcenter 三工程功能统一迁移至 skills 体系

---

## 一、现状分析

### 1.1 三工程概览

| 项目 | 定位 | SDK版本 | 端口 | 核心能力 |
|------|------|---------|------|----------|
| **ooder-Nexus** | 个人版/P2P节点 | 0.7.3 | 8081 | 轻量级部署、P2P共享、设备插件 |
| **ooder-Nexus-Enterprise** | 企业版 | 2.3 | 8082 | 固定应用、推送服务、LLM集成 |
| **agent-skillcenter** | 技能中心 | 2.3 | 8083 | 动态技能、K8s托管、云托管 |

### 1.2 功能重复度分析

| 模块 | 重复度 | Nexus | Enterprise | SkillCenter | 迁移策略 |
|------|--------|-------|------------|-------------|----------|
| 技能管理 | 🔴 高 | ✅ | ✅ | ✅ | 统一到 skill-market |
| 菜单管理 | 🔴 高 | ✅ | ✅ | ✅ | 统一到 nexus-ui 规范 |
| 场景管理 | 🔴 高 | ✅ | ✅ | ✅ | 统一到 scene-engine |
| P2P网络 | 🔴 高 | ✅ | ✅ | ✅ | 统一到 SDK 层 |
| 审计日志 | 🔴 高 | ✅ | ✅ | ✅ | 统一到 skill-audit |
| 存储管理 | 🟡 中 | VFS | VFS | 本地 | 保留 VFS 系列 |
| 网络管理 | 🟡 中 | ✅ | ✅ | ❌ | 集中到 skill-network |
| 企业集成 | 🟡 中 | ✅ | ✅ | ❌ | 保留 org 系列 |
| LLM服务 | 🟢 低 | ❌ | ✅ | ❌ | 保留 llm 系列 |
| K8s托管 | 🟢 低 | ❌ | ❌ | ✅ | 保留 skill-k8s |
| 云托管 | 🟢 低 | ❌ | ❌ | ✅ | 保留 skill-hosting |

### 1.3 现有 skills 资产

**已注册技能数量**: 58个

**分类分布**:
| 分类 | 数量 | 说明 |
|------|------|------|
| org (组织服务) | 6 | 钉钉/飞书/企业微信/LDAP/用户认证 |
| vfs (存储服务) | 5 | database/local/minio/oss/s3 |
| sys (系统管理) | 12 | network/agent/security/health/audit等 |
| msg (消息通讯) | 6 | mqtt/im/group/msg-service等 |
| nexus-ui (界面) | 5 | dashboard/health-check/storage等 |
| util (工具) | 8 | a2ui/llm系列/market等 |
| payment (支付) | 3 | alipay/wechat/unionpay |
| media (媒体) | 5 | wechat/weibo/zhihu/toutiao/xiaohongshu |

---

## 二、迁移目标

### 2.1 总体目标

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          迁移目标架构                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                    ooder-skills (统一技能仓库)                       │    │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │    │
│  │  │ org系列  │ │ vfs系列  │ │ sys系列  │ │ msg系列  │ │ ui系列   │ │    │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                    │                                         │
│                                    ▼                                         │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                    ooder-nexus-dev (独立开发工程)                    │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │  产品组装层                                                    │  │    │
│  │  │  ┌────────────┐  ┌────────────┐  ┌────────────┐              │  │    │
│  │  │  │ Nexus-Lite │  │ Enterprise │  │ SkillCenter│              │  │    │
│  │  │  │ (个人版)    │  │ (企业版)    │  │ (技能中心)  │              │  │    │
│  │  │  └────────────┘  └────────────┘  └────────────┘              │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                    │                                         │
│                                    ▼                                         │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                    发布渠道                                          │    │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │    │
│  │  │ GitHub       │  │ Gitee        │  │ SkillCenter  │             │    │
│  │  │ (国际)       │  │ (国内镜像)   │  │ (技能市场)   │             │    │
│  │  └──────────────┘  └──────────────┘  └──────────────┘             │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 具体目标

1. **功能转移**: 将三工程中的业务功能全部迁移为独立 skill
2. **规范统一**: 所有 skill 遵循统一的 skill-manifest.yaml 规范
3. **开发独立**: 建立独立开发工程，按需组装产品
4. **发布统一**: GitHub + Gitee + SkillCenter 三渠道同步发布

---

## 三、功能转移路径

### 3.1 高优先级转移（Phase 1）

#### 3.1.1 技能管理 → skill-market

| 源工程 | 源类 | 目标 skill | 状态 |
|--------|------|------------|------|
| Nexus | SkillDiscoveryService | skill-market | ✅ 已存在 |
| Enterprise | SkillDiscoveryService | skill-market | 需合并 |
| SkillCenter | SkillManager | skill-market | 需合并 |

**合并内容**:
- 技能发现 (GitHub/Gitee)
- 技能安装/卸载
- 技能生命周期管理
- 技能依赖管理

#### 3.1.2 菜单管理 → nexus-ui 规范

| 源工程 | 源类 | 目标 | 状态 |
|--------|------|------|------|
| Nexus | MenuConfiguration | menu-loader.js | ✅ 已实现 |
| Enterprise | MenuController | menu-loader.js | 需合并权限过滤 |
| SkillCenter | MenuItemDTO | menu-loader.js | 需合并角色过滤 |

**统一规范**:
```yaml
# nexus-ui skill 配置规范
nexusUi:
  entry:
    page: index.html
    title: 页面标题
    icon: ri-xxx-line
  menu:
    position: sidebar
    category: nexus
    order: 10
    permission: optional-permission
  layout:
    type: default
    sidebar: true
    header: true
```

#### 3.1.3 P2P网络 → SDK 层

| 源工程 | 源类 | 目标 | 状态 |
|--------|------|------|------|
| Nexus | P2PService | SDK PeerDiscovery | ✅ SDK已支持 |
| Enterprise | P2PNetworkController | SDK GossipProtocol | ✅ SDK已支持 |
| SkillCenter | P2PNodeManager | SDK DhtNode | ✅ SDK已支持 |

**结论**: P2P能力已由 agent-sdk 2.3 统一提供，各工程通过 SDK 调用

### 3.2 中优先级转移（Phase 2）

#### 3.2.1 存储管理 → VFS 系列

| 源工程 | 功能 | 目标 skill | 状态 |
|--------|------|------------|------|
| Nexus | VFS虚拟文件系统 | skill-vfs-local | ✅ 已存在 |
| Enterprise | VFS + 备份 | skill-vfs-database | ✅ 已存在 |
| SkillCenter | 本地JSON存储 | skill-common | ✅ 已存在 |

**补充需求**:
- [ ] skill-vfs-backup (备份服务)
- [ ] skill-vfs-sync (同步服务)

#### 3.2.2 网络管理 → skill-network

| 源工程 | 功能 | 状态 |
|--------|------|------|
| Nexus | 网络配置、设备管理、流量监控 | ✅ skill-network 已存在 |
| Enterprise | + 拓扑可视化 | 需补充 UI |
| SkillCenter | 基础网络状态 | 已覆盖 |

#### 3.2.3 企业集成 → org 系列

| 源工程 | 功能 | 目标 skill | 状态 |
|--------|------|------------|------|
| Nexus | 钉钉集成 | skill-org-dingding | ✅ 已存在 |
| Nexus | 飞书集成 | skill-org-feishu | ✅ 已存在 |
| Nexus | 企业微信 | skill-org-wecom | ✅ 已存在 |
| Enterprise | LDAP集成 | skill-org-ldap | ✅ 已存在 |

### 3.3 低优先级转移（Phase 3）

#### 3.3.1 LLM服务 → llm 系列

| 源工程 | 功能 | 目标 skill | 状态 |
|--------|------|------------|------|
| Enterprise | LLM对话 | skill-llm-volcengine | ✅ 已存在 |
| Enterprise | LLM嵌入 | skill-llm-qianwen | ✅ 已存在 |
| Enterprise | LLM函数 | skill-llm-deepseek | ✅ 已存在 |

**补充需求**:
- [ ] skill-llm-openai (OpenAI支持)
- [ ] skill-llm-ollama (本地模型支持)

#### 3.3.2 托管服务 → hosting 系列

| 源工程 | 功能 | 目标 skill | 状态 |
|--------|------|------------|------|
| SkillCenter | K8s集群管理 | skill-k8s | ✅ 已存在 |
| SkillCenter | 云托管 | skill-hosting | ✅ 已存在 |

**补充需求**:
- [ ] skill-k8s-nexus-ui (K8s管理界面)
- [ ] skill-hosting-nexus-ui (云托管界面)

---

## 四、独立开发工程结构

### 4.1 工程命名

**推荐名称**: `ooder-nexus-dev`

### 4.2 目录结构

```
ooder-nexus-dev/
├── pom.xml                              # 父POM
├── README.md
│
├── skills/                              # 技能仓库 (git submodule → ooder-skills)
│   └── .gitmodules
│
├── modules/                             # 公共模块
│   ├── nexus-common/                    # 公共工具类
│   │   ├── src/main/java/net/ooder/nexus/common/
│   │   │   ├── utils/
│   │   │   ├── constants/
│   │   │   └── exceptions/
│   │   └── pom.xml
│   │
│   ├── nexus-protocol/                  # 协议层
│   │   ├── src/main/java/net/ooder/nexus/protocol/
│   │   │   ├── dto/
│   │   │   ├── api/
│   │   │   └── event/
│   │   └── pom.xml
│   │
│   └── nexus-sdk-adapter/               # SDK适配器
│       ├── src/main/java/net/ooder/nexus/sdk/
│       │   ├── P2PAdapter.java
│       │   ├── SceneAdapter.java
│       │   └── A2AAdapter.java
│       └── pom.xml
│
├── products/                            # 产品组装
│   ├── nexus-lite/                      # 个人版
│   │   ├── src/main/java/
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   └── static/console/
│   │   └── pom.xml
│   │
│   ├── nexus-enterprise/                # 企业版
│   │   ├── src/main/java/
│   │   ├── src/main/resources/
│   │   └── pom.xml
│   │
│   └── skillcenter/                     # 技能中心
│       ├── src/main/java/
│       ├── src/main/resources/
│       └── pom.xml
│
├── ui/                                  # 前端资源
│   ├── console/                         # 管理控制台
│   │   ├── css/
│   │   │   ├── theme.css
│   │   │   ├── nexus.css
│   │   │   └── main.css
│   │   ├── js/
│   │   │   ├── nexus.js
│   │   │   ├── menu-loader.js
│   │   │   └── pages/
│   │   └── pages/
│   │       ├── home/
│   │       ├── k8s/
│   │       └── hosting/
│   │
│   └── components/                      # Web Components
│       ├── nx-card/
│       ├── nx-table/
│       └── nx-modal/
│
├── docs/                                # 文档
│   ├── api/
│   ├── user-guide/
│   └── development/
│
└── scripts/                             # 脚本
    ├── build.sh
    ├── release.sh
    └── deploy.sh
```

### 4.3 Maven 依赖关系

```xml
<!-- 父POM -->
<dependencyManagement>
    <dependencies>
        <!-- SDK -->
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>agent-sdk</artifactId>
            <version>2.3.0</version>
        </dependency>
        
        <!-- 公共模块 -->
        <dependency>
            <groupId>net.ooder.nexus</groupId>
            <artifactId>nexus-common</artifactId>
            <version>${project.version}</version>
        </dependency>
        
        <!-- 技能依赖 -->
        <dependency>
            <groupId>net.ooder.skill</groupId>
            <artifactId>skill-market</artifactId>
            <version>0.7.3</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 4.4 产品组装配置

```yaml
# nexus-lite 产品配置示例
product:
  id: nexus-lite
  name: Nexus Lite (个人版)
  version: 2.3.0
  
skills:
  required:
    - skill-user-auth
    - skill-market
    - skill-network
  optional:
    - skill-org-dingding
    - skill-org-feishu
    - skill-mqtt
    - skill-openwrt
    
features:
  p2p: true
  push: false
  llm: false
  k8s: false
```

---

## 五、Skills 规范统一

### 5.1 Skill 类型定义

| 类型 | 说明 | 示例 |
|------|------|------|
| **service-skill** | 后端服务型技能 | skill-k8s, skill-hosting |
| **tool-skill** | 工具型技能 | skill-a2ui, skill-trae-solo |
| **nexus-ui** | 前端界面型技能 | skill-nexus-dashboard-nexus-ui |
| **provider-skill** | 提供者型技能 | skill-org-dingding, skill-llm-volcengine |

### 5.2 Skill Manifest 规范

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillManifest

metadata:
  id: skill-xxx
  name: Skill Name
  version: 0.7.3
  description: Skill description
  author: Ooder Team
  license: Apache-2.0
  homepage: https://github.com/ooderCN/skills
  repository: https://github.com/ooderCN/skills.git
  keywords:
    - keyword1
    - keyword2

spec:
  type: service-skill | tool-skill | nexus-ui | provider-skill
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
    mainClass: net.ooder.skill.xxx.XxxSkillApplication
  
  capabilities:
    - id: capability-1
      name: Capability Name
      description: Capability description
      category: category
  
  scenes:
    - name: scene-name
      description: Scene description
      capabilities:
        - capability-1
  
  dependencies:
    skills:
      - skill-id: version
    libraries:
      - library-id: version
  
  config:
    required:
      - name: CONFIG_NAME
        type: string
        description: Config description
    optional:
      - name: OPTIONAL_CONFIG
        type: string
        default: default_value
        description: Optional config
  
  endpoints:
    - path: /api/xxx/resource
      method: GET
      description: Endpoint description
      capability: capability-1
  
  resources:
    cpu: "200m"
    memory: "256Mi"
    storage: "100Mi"
  
  offline:
    enabled: true | false
```

### 5.3 Nexus-UI Skill 规范

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-xxx-nexus-ui
  name: UI页面名称
  version: 1.0.0
  description: UI页面描述
  author: ooder Team
  type: nexus-ui

spec:
  type: nexus-ui
  
  nexusUi:
    entry:
      page: index.html
      title: 页面标题
      icon: ri-xxx-line
      
    menu:
      position: sidebar
      category: nexus
      order: 10
      permission: optional-permission
      
    layout:
      type: default
      sidebar: true
      header: true
      
  capabilities:
    - id: xxx-view
      name: 查看xxx
      description: 查看xxx功能
      category: ui
      
  apis:
    - path: /api/xxx/data
      method: GET
      description: 获取数据
```

### 5.4 分类规范

| 分类ID | 名称 | 图标 | 说明 |
|--------|------|------|------|
| org | 组织服务 | users | 企业组织、用户认证 |
| vfs | 存储服务 | database | 文件存储、对象存储 |
| ui | UI生成 | palette | 界面生成、设计转代码 |
| msg | 消息通讯 | message | 消息队列、通讯协议 |
| sys | 系统管理 | settings | 系统监控、网络管理 |
| payment | 支付服务 | credit-card | 支付渠道、退款管理 |
| media | 媒体发布 | edit | 自媒体、内容管理 |
| util | 工具服务 | tool | 通用工具、辅助功能 |
| nexus-ui | Nexus界面 | layout | 管理界面、仪表盘 |
| infrastructure | 基础设施 | server | K8s、云托管 |
| llm | AI服务 | brain | 大语言模型 |

---

## 六、发布流程设计

### 6.1 发布渠道

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          发布流程                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  1. 开发完成                                                                 │
│  ┌────────────┐                                                             │
│  │ 本地开发    │                                                             │
│  │ 测试验证    │                                                             │
│  └─────┬──────┘                                                             │
│        │                                                                     │
│        ▼                                                                     │
│  2. 版本发布                                                                 │
│  ┌────────────┐    ┌────────────┐    ┌────────────┐                        │
│  │ GitHub     │───▶│ Gitee      │───▶│ SkillCenter│                        │
│  │ (主仓库)   │    │ (镜像)     │    │ (市场)     │                        │
│  └────────────┘    └────────────┘    └────────────┘                        │
│        │                 │                 │                                 │
│        ▼                 ▼                 ▼                                 │
│  3. 资源生成                                                                 │
│  ┌────────────┐    ┌────────────┐    ┌────────────┐                        │
│  │ JAR包      │    │ JAR包      │    │ 索引更新    │                        │
│  │ ZIP包(UI)  │    │ ZIP包(UI)  │    │ 元数据注册  │                        │
│  └────────────┘    └────────────┘    └────────────┘                        │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 GitHub 发布配置

```yaml
# .github/workflows/release.yml
name: Release Skills

on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 8
        uses: actions/setup-java@v3
        with:
          java-version: '8'
          distribution: 'temurin'
      
      - name: Build with Maven
        run: mvn clean package -DskipTests
      
      - name: Create Release
        uses: softprops/action-gh-release@v1
        with:
          files: |
            skills/*/target/*.jar
            skills/skill-*-nexus-ui/target/*.zip
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      
      - name: Sync to Gitee
        run: |
          git remote add gitee https://gitee.com/ooderCN/skills.git
          git push gitee --tags
```

### 6.3 SkillCenter 发布流程

```yaml
# 发布到技能中心
skillcenter:
  api: https://skillcenter.ooder.net/api/v1
  actions:
    - name: 更新技能索引
      endpoint: /skills/index
      method: POST
      
    - name: 注册技能元数据
      endpoint: /skills/register
      method: POST
      
    - name: 上传技能包
      endpoint: /skills/upload
      method: POST
```

### 6.4 版本号规范

```
版本格式: 主版本.次版本.修订版本

示例:
- 0.7.3  # 开发版本
- 1.0.0  # 正式版本
- 1.1.0  # 功能更新
- 1.1.1  # Bug修复
```

---

## 七、实施计划

### 7.1 Phase 1: 基础建设（第1-2周）

| 任务 | 负责人 | 工期 | 交付物 |
|------|--------|------|--------|
| 创建 ooder-nexus-dev 工程 | 架构组 | 2天 | 工程骨架 |
| 迁移 nexus-common 模块 | Nexus组 | 2天 | 公共模块 |
| 迁移 nexus-protocol 模块 | Enterprise组 | 2天 | 协议模块 |
| 统一 skill-manifest 规范 | 架构组 | 1天 | 规范文档 |
| 配置 CI/CD 流水线 | DevOps | 2天 | 发布流程 |

### 7.2 Phase 2: 功能迁移（第3-4周）

| 任务 | 负责人 | 工期 | 交付物 |
|------|--------|------|--------|
| 合并技能管理到 skill-market | SkillCenter组 | 3天 | 增强版 skill-market |
| 统一菜单管理规范 | Nexus组 | 2天 | menu-loader.js 增强 |
| 迁移 P2P 到 SDK 层 | Enterprise组 | 2天 | SDK适配器 |
| 补充 VFS 备份/同步服务 | Nexus组 | 3天 | 新 skills |
| 迁移网络管理 UI | Nexus组 | 2天 | nexus-ui skill |

### 7.3 Phase 3: 产品组装（第5-6周）

| 任务 | 负责人 | 工期 | 交付物 |
|------|--------|------|--------|
| Nexus-Lite 产品组装 | Nexus组 | 3天 | 可运行产品 |
| Nexus-Enterprise 产品组装 | Enterprise组 | 3天 | 可运行产品 |
| SkillCenter 产品组装 | SkillCenter组 | 3天 | 可运行产品 |
| 集成测试 | 测试组 | 2天 | 测试报告 |
| 文档完善 | 全体 | 2天 | 用户文档 |

### 7.4 Phase 4: 发布上线（第7周）

| 任务 | 负责人 | 工期 | 交付物 |
|------|--------|------|--------|
| GitHub 发布 | DevOps | 1天 | Release |
| Gitee 同步 | DevOps | 1天 | 镜像更新 |
| SkillCenter 注册 | SkillCenter组 | 1天 | 市场上架 |
| 用户验收 | 产品组 | 2天 | 验收报告 |

---

## 八、风险与应对

| 风险 | 等级 | 应对措施 |
|------|------|----------|
| SDK版本不一致 | 高 | 统一升级到 SDK 2.3 |
| API接口变更 | 中 | 建立版本兼容层 |
| 功能遗漏 | 中 | 建立功能清单核对表 |
| 团队协作不畅 | 中 | 增加站会频率 |
| 发布流程故障 | 低 | 准备回滚方案 |

---

## 九、验收标准

### 9.1 功能验收

- [ ] 三工程所有功能已迁移为独立 skill
- [ ] 所有 skill 遵循统一规范
- [ ] 产品可按需组装运行

### 9.2 质量验收

- [ ] 单元测试覆盖率 ≥ 80%
- [ ] API 文档 100% 覆盖
- [ ] 无 P0/P1 级别 Bug

### 9.3 发布验收

- [ ] GitHub Release 发布成功
- [ ] Gitee 镜像同步成功
- [ ] SkillCenter 市场上架成功

---

**文档版本**: v1.0.0  
**创建时间**: 2026-02-28  
**适用团队**: Nexus, Enterprise, SkillCenter

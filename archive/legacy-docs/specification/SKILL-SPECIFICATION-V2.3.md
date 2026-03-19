# Ooder Skills 分类及管理规范 v2.3

## 文档信息
- **版本**: v2.3
- **日期**: 2026-03-02
- **协议版本**: v0.8.0
- **官方地址**: https://gitee.com/ooderCN
- **Java版本要求**: Java 8+

---

## 一、概述

### 1.1 设计目标

Ooder Skills是一个标准化的技能包管理系统，支持技能的上传、发现、安装和生命周期管理。本规范定义了完整的技能包协议、发现协议和管理接口。

### 1.2 核心特性

| 特性 | 描述 |
|------|------|
| 标准化打包 | 统一的技能包格式和目录结构 |
| 多渠道发现 | 支持GitHub、Gitee、SkillCenter、本地文件系统等多种发现方式 |
| 分类管理 | 6大分类体系，便于技能组织和检索 |
| 生命周期管理 | 完整的安装、更新、卸载、启停流程 |
| 离线支持 | 支持离线模式下的技能安装和缓存 |
| 事件驱动 | 基于EventBus的生命周期事件通知 |

### 1.3 版本兼容性

| Skill版本 | SDK版本 | 协议版本 |
|-----------|---------|----------|
| 2.3.x | 0.8.0 | v0.8.0 |
| 0.7.x | 0.7.3 | v0.7.3 |
| 0.6.x | 0.6.x | v0.6.5 |

---

## 二、技能分类体系

### 2.1 分类定义

| 分类ID | 分类名称 | 英文名称 | 描述 | 图标 |
|--------|----------|----------|------|------|
| `org` | 组织服务 | Organization | 企业组织架构、用户认证相关服务 | users |
| `vfs` | 存储服务 | Storage | 文件存储、对象存储相关服务 | database |
| `ui` | UI生成 | UI Generation | 界面生成、设计转代码服务 | palette |
| `msg` | 消息通讯 | Messaging | 消息队列、通讯协议服务 | message |
| `sys` | 系统管理 | System | 系统监控、网络管理、安全审计 | settings |
| `util` | 工具服务 | Utility | 通用工具、辅助功能 | tool |

### 2.2 技能分类映射

#### 组织服务 (org)

| 技能ID | 技能名称 | 子分类 |
|--------|----------|--------|
| skill-org-dingding | 钉钉组织服务 | dingtalk |
| skill-org-feishu | 飞书组织服务 | feishu |
| skill-org-wecom | 企业微信组织服务 | wecom |
| skill-org-ldap | LDAP组织服务 | ldap |
| skill-user-auth | 用户认证服务 | auth |

#### 存储服务 (vfs)

| 技能ID | 技能名称 | 子分类 |
|--------|----------|--------|
| skill-vfs-local | 本地存储服务 | local |
| skill-vfs-database | 数据库存储服务 | database |
| skill-vfs-minio | MinIO存储服务 | object-storage |
| skill-vfs-oss | 阿里云OSS存储服务 | object-storage |
| skill-vfs-s3 | AWS S3存储服务 | object-storage |

#### UI生成 (ui)

| 技能ID | 技能名称 | 子分类 |
|--------|----------|--------|
| skill-a2ui | A2UI图转代码 | code-gen |

#### 消息通讯 (msg)

| 技能ID | 技能名称 | 子分类 |
|--------|----------|--------|
| skill-mqtt | MQTT服务 | mqtt |

#### 系统管理 (sys)

| 技能ID | 技能名称 | 子分类 |
|--------|----------|--------|
| skill-network | 网络管理 | network |
| skill-agent | 代理管理 | agent |
| skill-security | 安全管理 | security |
| skill-health | 健康检查 | health |
| skill-protocol | 协议管理 | protocol |
| skill-openwrt | OpenWrt管理 | router |

#### 工具服务 (util)

| 技能ID | 技能名称 | 子分类 |
|--------|----------|--------|
| skill-trae-solo | Trae Solo工具 | general |
| skill-share | 技能分享 | share |

---

## 三、技能包协议

### 3.1 包结构

```
skill-{skill-id}/
├── skill.yaml              # 技能清单(必需)
├── skill.jar               # 编译后的技能代码(Java技能必需)
├── SKILLS.md               # 技能文档(必需)
├── README.md               # 简要说明(必需)
├── config/
│   ├── config.yaml         # 默认配置
│   └── config-template.yaml # 配置模板
├── lib/                    # 依赖库(可选)
│   └── *.jar
├── static/                 # 静态资源(可选)
│   ├── css/
│   ├── js/
│   └── images/
└── scenes/                 # 场景定义(可选)
    └── {scene-name}.yaml
```

### 3.2 包命名规范

```
skill-{category}-{name}-{version}.skill

示例:
- skill-org-feishu-0.7.3.skill
- skill-msg-mqtt-1.0.0.skill
- skill-vfs-local-2.1.0.skill
```

### 3.3 技能清单 (skill.yaml)

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-org-feishu
  name: Feishu Organization Service
  version: 0.7.3
  description: 飞书组织数据集成服务
  author: Ooder Team
  license: Apache-2.0
  homepage: https://gitee.com/ooderCN/skill-org-feishu
  repository: https://gitee.com/ooderCN/skill-org-feishu.git
  keywords:
    - feishu
    - lark
    - organization
    - enterprise

spec:
  type: enterprise-skill
  category: org
  subCategory: feishu
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
    mainClass: net.ooder.skill.org.feishu.FeishuSkillApplication
    jvmOpts: "-Xms256m -Xmx512m"
  
  capabilities:
    - id: org-data-read
      name: Organization Data Read
      description: 读取组织架构和成员数据
      category: data-access
    - id: user-auth
      name: User Authentication
      description: 通过飞书OAuth认证用户
      category: authentication
  
  scenes:
    - name: auth
      description: 认证场景，支持组织数据
      capabilities:
        - org-data-read
        - user-auth
      roles:
        - roleId: org-provider
          name: Organization Provider
          required: true
          capabilities: [org-data-read, user-auth]
  
  dependencies:
    skills:
      - id: skill-vfs
        version: ">=1.0.0"
        optional: true
    libraries:
      - group: net.ooder
        artifact: ooder-common
        version: "2.0.0"
  
  config:
    required:
      - name: FEISHU_APP_ID
        type: string
        description: 飞书应用ID
        secret: false
      - name: FEISHU_APP_SECRET
        type: string
        description: 飞书应用密钥
        secret: true
    optional:
      - name: FEISHU_API_BASE_URL
        type: string
        default: https://open.feishu.cn/open-apis
        description: 飞书API基础URL
  
  endpoints:
    - path: /api/org/tree
      method: GET
      description: 获取组织架构树
      capability: org-data-read
    - path: /api/auth/verify
      method: POST
      description: 验证用户认证
      capability: user-auth
  
  resources:
    cpu: "500m"
    memory: "512Mi"
    storage: "1Gi"
  
  offline:
    enabled: true
    cacheStrategy: "local"
    syncOnReconnect: true
```

### 3.4 技能类型

#### Enterprise Skill (企业技能)

企业级技能提供组织级能力：

```yaml
spec:
  type: enterprise-skill
  
  deployment:
    modes:
      - remote-hosted
      - local-deployed
    singleton: true
  
  offline:
    enabled: true
    cacheStrategy: "distributed"
```

特点：
- 支持远程托管和本地部署
- 单例模式运行
- 支持离线缓存
- 需要认证授权

#### Tool Skill (工具技能)

通用工具技能：

```yaml
spec:
  type: tool-skill
  
  deployment:
    modes:
      - local-deployed
    singleton: false
  
  offline:
    enabled: true
    cacheStrategy: "local"
```

特点：
- 仅支持本地部署
- 可多实例运行
- 本地缓存策略

#### Integration Skill (集成技能)

外部系统集成技能：

```yaml
spec:
  type: integration-skill
  
  deployment:
    modes:
      - remote-hosted
      - local-deployed
    singleton: false
  
  offline:
    enabled: false
```

特点：
- 连接外部系统
- 不支持离线模式
- 可多实例运行

---

## 四、技能发现协议

### 4.1 发现方式

| 方式 | 代码 | 范围 | 延迟 | 用途 |
|------|------|------|------|------|
| UDP广播 | `UDP_BROADCAST` | 局域网 | 低 | LAN发现 |
| DHT (Kademlia) | `DHT_KADEMLIA` | 全球 | 中 | P2P发现 |
| SkillCenter API | `SKILL_CENTER` | 全球 | 低 | 集中式目录 |
| mDNS/DNS-SD | `MDNS_DNS_SD` | 局域网 | 低 | 服务发现 |
| GitHub | `GITHUB` | 全球 | 中 | GitHub仓库发现 |
| Gitee | `GITEE` | 全球 | 中 | Gitee仓库发现 |
| Git仓库 | `GIT_REPOSITORY` | 全球 | 中 | 通用Git仓库发现 |
| 本地文件系统 | `LOCAL_FS` | 本地 | 极低 | 本地开发 |

### 4.2 发现方式枚举

```java
public enum DiscoveryMethod {
    UDP_BROADCAST("udp_broadcast", "UDP Broadcast discovery"),
    DHT_KADEMLIA("dht_kademlia", "DHT/Kademlia discovery"),
    MDNS_DNS_SD("mdns_dns_sd", "mDNS/DNS-SD discovery"),
    SKILL_CENTER("skill_center", "SkillCenter API discovery"),
    LOCAL_FS("local_fs", "Local filesystem discovery"),
    GITHUB("github", "GitHub repository discovery"),
    GITEE("gitee", "Gitee repository discovery"),
    GIT_REPOSITORY("git_repository", "Git repository discovery"),
    AUTO("auto", "Auto detect discovery method");
}
```

### 4.3 GitHub/Gitee发现配置

```yaml
discovery:
  github:
    enabled: true
    defaultOwner: ooderCN
    defaultRepo: skills
    defaultBranch: main
    token: ${GITHUB_TOKEN}
    baseUrl: https://api.github.com
    timeout: 60000
    
  gitee:
    enabled: true
    defaultOwner: ooderCN
    defaultRepo: skills
    defaultBranch: main
    token: ${GITEE_TOKEN}
    baseUrl: https://gitee.com/api/v5
    timeout: 60000
```

### 4.4 技能索引文件

仓库根目录必须包含 `skill-index.yaml` 文件：

```yaml
apiVersion: ooder.io/v1
kind: SkillIndex

metadata:
  name: ooder-skills
  version: 2.3.0
  updatedAt: 2026-03-02T00:00:00Z

spec:
  categories:
    - id: org
      name: 组织服务
      nameEn: Organization
      description: 企业组织架构、用户认证相关服务
      icon: users
      order: 1

skills:
  - skillId: skill-org-feishu
    name: Feishu Organization Service
    version: "0.7.3"
    category: org
    subCategory: feishu
    tags:
      - feishu
      - lark
      - organization
      - auth
    description: 飞书组织数据集成服务
    path: skills/skill-org-feishu
    manifest: skill.yaml
    scenes:
      - auth
    capabilities:
      - org-data-read
      - user-auth
    downloadUrl: https://gitee.com/ooderCN/skills/releases/download/v0.7.3/skill-org-feishu-0.7.3.skill
```

### 4.5 发现API接口

```java
public interface DiscoveryProtocol {
    
    CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request);
    
    CompletableFuture<List<PeerInfo>> discoverPeers();
    
    CompletableFuture<PeerInfo> discoverMcp();
    
    void addDiscoveryListener(DiscoveryListener listener);
    
    void removeDiscoveryListener(DiscoveryListener listener);
    
    void startBroadcast();
    
    void stopBroadcast();
    
    boolean isBroadcasting();
}
```

---

## 五、技能生命周期管理

### 5.1 生命周期状态

```java
public enum SkillStatus {
    CREATED,        // 已创建
    STARTING,       // 启动中
    RUNNING,        // 运行中
    STOPPING,       // 停止中
    STOPPED,        // 已停止
    ERROR,          // 错误
    FAILED,         // 失败
    NOT_FOUND,      // 未找到
    INSTALLING,     // 安装中
    UNINSTALLING    // 卸载中
}
```

### 5.2 安装流程

```
1. 查询技能元数据
2. 下载技能包到缓存
3. 校验校验和和签名
4. 解压到安装目录
5. 合并配置模板
6. 注册技能到本地注册表
7. 发布SkillInstalledEvent
8. 如果是企业技能且本地部署:
   a. 启动技能进程
   b. 注册为RouteAgent
   c. 创建场景组
```

### 5.3 安装API

```http
POST /api/skillcenter/installed/install
Content-Type: application/json

{
  "skillId": "skill-org-feishu",
  "skillName": "Feishu Organization Service",
  "version": "0.7.3",
  "downloadUrl": "https://gitee.com/ooderCN/skills/releases/download/v0.7.3/skill-org-feishu-0.7.3.skill",
  "source": "gitee",
  "config": {
    "FEISHU_APP_ID": "cli_xxx",
    "FEISHU_APP_SECRET": "xxx"
  }
}
```

### 5.4 安装预览

```http
POST /api/skill/install/preview
Content-Type: application/json

{
  "skillId": "skill-org-feishu",
  "userId": "user-001",
  "downloadUrl": "https://..."
}
```

响应：
```json
{
  "code": 200,
  "message": "获取安装预览成功",
  "data": {
    "skillId": "skill-org-feishu",
    "name": "Feishu Organization Service",
    "version": "0.7.3",
    "dependencies": [...],
    "permissions": [...],
    "scenes": [...],
    "configSchema": {...}
  }
}
```

### 5.5 更新流程

```
1. 检查SkillCenter是否有新版本
2. 下载新版本
3. 备份当前配置
4. 停止当前技能(如果运行中)
5. 替换技能文件
6. 迁移配置
7. 启动新版本
8. 更新注册表
9. 发布SkillUpdatedEvent
```

### 5.6 卸载流程

```
1. 停止技能进程(如果运行中)
2. 从场景组移除
3. 从本地注册表注销
4. 归档配置(可选)
5. 删除技能文件
6. 发布SkillUninstalledEvent
```

### 5.7 生命周期事件

| 事件类型 | 描述 |
|----------|------|
| SkillDownloadStartedEvent | 包下载开始 |
| SkillDownloadCompletedEvent | 包下载完成 |
| SkillInstallStartedEvent | 安装开始 |
| SkillInstalledEvent | 安装完成 |
| SkillUpdateStartedEvent | 更新开始 |
| SkillUpdatedEvent | 更新完成 |
| SkillUninstallStartedEvent | 卸载开始 |
| SkillUninstalledEvent | 卸载完成 |
| SkillCacheHitEvent | 离线缓存命中 |
| SkillCacheMissEvent | 离线缓存未命中 |

### 5.8 事件订阅示例

```java
@PostConstruct
public void init() {
    eventBus.subscribe(SkillInstalledEvent.class, this::onSkillInstalled);
    eventBus.subscribe(SkillUpdatedEvent.class, this::onSkillUpdated);
    eventBus.subscribe(SkillUninstalledEvent.class, this::onSkillUninstalled);
}

private void onSkillInstalled(SkillInstalledEvent event) {
    if (event.isOffline()) {
        log.info("Skill {} installed in offline mode", event.getSkillId());
        syncQueue.add(event);
    }
}
```

---

## 六、SDK接口规范

### 6.1 SkillPackageManager接口

```java
public interface SkillPackageManager {

    CompletableFuture<InstallResult> installSkill(InstallRequest request);

    CompletableFuture<UninstallResult> uninstallSkill(String skillId);

    CompletableFuture<UpdateResult> updateSkill(String skillId, String version);

    List<InstalledSkill> getInstalledSkills();

    InstalledSkill getInstalledSkill(String skillId);

    CompletableFuture<List<SkillPackage>> discoverSkills(DiscoveryFilter filter);

    CompletableFuture<SkillPackage> getSkillInfo(String skillId);

    CompletableFuture<SceneJoinResult> requestScene(SceneRequest request);

    SkillConnectionInfo getSkillConnection(String skillId);

    SkillStatus getSkillStatus(String skillId);

    CompletableFuture<Boolean> testConnection(String skillId);

    CompletableFuture<Void> startSkill(String skillId);

    CompletableFuture<Void> stopSkill(String skillId);

    void registerObserver(SkillPackageObserver observer);

    void unregisterObserver(SkillPackageObserver observer);
}
```

### 6.2 InstalledSkill模型

```java
public class InstalledSkill {

    private String skillId;
    private String name;
    private String version;
    private String type;
    private InstallMode installMode;
    private SkillStatus status;
    private String installPath;
    private Instant installTime;
    private Instant lastStartTime;
    private List<Capability> capabilities;
    private List<SceneInfo> scenes;
    private Map<String, String> config;
    private SkillConnectionInfo connectionInfo;
}
```

### 6.3 SkillPackage模型

```java
public class SkillPackage {

    private String skillId;
    private String name;
    private String version;
    private String description;
    private String category;
    private String author;
    private String icon;
    private List<String> tags;
    private String downloadUrl;
    private String checksum;
    private AuthStatus authStatus;
    private long downloadCount;
    private long installCount;
    private long updateTime;
    private long createTime;
    private String status;

    public enum AuthStatus {
        VERIFIED,
        PENDING,
        UNVERIFIED,
        REJECTED
    }
}
```

---

## 七、REST API规范

### 7.1 基础信息

| 项目 | 值 |
|------|-----|
| Base URL | `http://localhost:9082/api` |
| 认证方式 | Bearer Token (JWT) |
| Content-Type | `application/json` |
| 字符编码 | UTF-8 |

### 7.2 接口分类

| 分类 | 前缀 | 描述 |
|------|------|------|
| Skill管理 | `/skills` | Skill的CRUD操作 |
| 运行时监控 | `/runtime` | 实时状态和监控数据 |
| 能力管理 | `/capabilities` | 能力列表和调用追踪 |
| 事件中心 | `/events` | 生命周期事件 |
| 技能市场 | `/skillcenter/market` | 技能市场相关 |
| 已安装技能 | `/skillcenter/installed` | 已安装技能管理 |
| 发现服务 | `/skillcenter/discovery` | 技能发现服务 |

### 7.3 核心API列表

#### 技能市场API

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/skillcenter/market/list` | POST | 获取技能列表 |
| `/api/skillcenter/market/search` | POST | 搜索技能 |
| `/api/skillcenter/market/categories` | GET | 获取分类列表 |

#### 已安装技能API

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/skillcenter/installed/list` | POST | 获取已安装列表 |
| `/api/skillcenter/installed/get` | POST | 获取技能详情 |
| `/api/skillcenter/installed/install` | POST | 安装技能 |
| `/api/skillcenter/installed/uninstall` | POST | 卸载技能 |
| `/api/skillcenter/installed/update` | POST | 更新技能 |
| `/api/skillcenter/installed/run` | POST | 启动技能 |
| `/api/skillcenter/installed/stop` | POST | 停止技能 |
| `/api/skillcenter/installed/config/get` | POST | 获取配置 |
| `/api/skillcenter/installed/config/update` | POST | 更新配置 |
| `/api/skillcenter/installed/logs` | POST | 获取日志 |
| `/api/skillcenter/installed/statistics` | POST | 获取统计 |
| `/api/skillcenter/installed/progress` | POST | 获取安装进度 |
| `/api/skillcenter/installed/dependencies` | POST | 获取依赖 |

#### 发现服务API

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/skillcenter/discovery/scan` | POST | 扫描技能 |
| `/api/skillcenter/discovery/github` | POST | GitHub发现 |
| `/api/skillcenter/discovery/gitee` | POST | Gitee发现 |
| `/api/skillcenter/discovery/manifest` | POST | 获取清单 |
| `/api/skillcenter/discovery/status` | GET | 获取状态 |

### 7.4 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": 1708838400000,
  "requestId": "req-xxx-xxx"
}
```

---

## 八、场景管理

### 8.1 场景索引

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneIndex

metadata:
  name: nexus-scenes
  version: "1.0.0"

spec:
  scenes:
    - sceneId: vfs
      name: Virtual File System
      description: 虚拟文件系统场景
      version: "1.0.0"
      type: primary
      required: true
      category: infrastructure
      capabilities:
        - vfs-read
        - vfs-write
        - vfs-delete
        - vfs-list

    - sceneId: auth
      name: Authentication & Authorization
      description: 认证授权场景
      version: "1.0.0"
      type: primary
      required: true
      category: security
      capabilities:
        - auth-login
        - auth-logout
        - auth-validate

  sceneGroups:
    - sceneGroupId: enterprise-nexus
      name: Enterprise Nexus Platform
      description: 企业级Nexus平台默认场景组
      version: "1.0.0"
      type: enterprise
      scenes:
        - vfs
        - auth
        - msg
        - workflow
        - a2ui

    - sceneGroupId: personal-nexus
      name: Personal Nexus
      description: 个人版Nexus默认场景组
      version: "1.0.0"
      type: personal
      scenes:
        - vfs
        - auth
        - a2ui
```

### 8.2 场景加入请求

```java
SceneJoinResult result = sdk.requestScene("auth", Arrays.asList("org-data-read", "user-auth"));

if (result.isJoined()) {
    String endpoint = result.getConnectionInfo().get("endpoint");
    String apiKey = result.getConnectionInfo().get("apiKey");
}
```

---

## 九、离线支持

### 9.1 离线配置

```yaml
package:
  offline:
    enabled: true
    cachePath: ./data/skill-cache
    maxSize: 1GB
    maxAge: 604800000
    autoCleanup: true

discovery:
  offline:
    enabled: true
    cachePath: ./data/skill-cache
    maxAge: 86400000
    maxSkills: 100
```

### 9.2 离线安装流程

```
安装请求
    │
    ▼
检查网络
    │
    ├── 在线 ────────────────────────────────┐
    │                                        │
    │   从SkillCenter下载                     │
    │   验证并安装                            │
    │   缓存技能包                            │
    │                                        │
    └── 离线 ────────────────────────────────┤
                                             │
        检查缓存                              │
        │                                    │
        ├── 已缓存 ────▶ 从缓存安装           │
        │                                    │
        └── 未缓存                           │
                │                            │
                ▼                            │
            加入等待队列                      │
                                             │
                ▼                            │
            网络重连 ◀───────────────────────┘
                │
                ▼
            自动同步并安装
```

### 9.3 离线服务接口

```java
public interface OfflinePackageService {
    
    boolean isSkillCached(String skillId, String version);
    
    Optional<SkillPackage> getCachedPackage(String skillId, String version);
    
    CompletableFuture<InstallResult> installFromCache(String skillId, String version);
    
    CompletableFuture<SyncResult> syncCache();
}
```

---

## 十、安全规范

### 10.1 包签名

技能包应使用GPG签名：

```
skill-org-feishu-0.7.3.skill
skill-org-feishu-0.7.3.skill.sig
skill-org-feishu-0.7.3.skill.asc
```

### 10.2 校验和验证

```
checksum.sha256:
sha256sum skill-org-feishu-0.7.3.skill > checksum.sha256
```

### 10.3 配置安全

敏感配置应：
- 使用环境变量
- 支持密钥管理集成
- 不在日志或API响应中暴露

### 10.4 认证状态

| 状态 | 描述 |
|------|------|
| VERIFIED | 已验证 |
| PENDING | 待审核 |
| UNVERIFIED | 未验证 |
| REJECTED | 已拒绝 |

---

## 十一、错误码

| 代码 | 描述 |
|------|------|
| SKILL_001 | 技能未找到 |
| SKILL_002 | 版本未找到 |
| SKILL_003 | 下载失败 |
| SKILL_004 | 校验和不匹配 |
| SKILL_005 | 签名验证失败 |
| SKILL_006 | 安装失败 |
| SKILL_007 | 配置无效 |
| SKILL_008 | 依赖未满足 |
| SKILL_009 | 卸载失败 |
| SKILL_010 | 更新失败 |
| SKILL_011 | 离线缓存未命中 |
| SKILL_012 | 离线依赖缺失 |
| DISC_001 | 发现超时 |
| DISC_002 | 未找到技能 |
| DISC_003 | 注册失败 |
| DISC_004 | 验证失败 |
| DISC_005 | 网络错误 |
| DISC_006 | 技能不可用 |
| DISC_007 | 离线模式限制 |

---

## 十二、验证工具链

### 12.1 四级验证

| 级别 | 描述 | 检查项 |
|------|------|--------|
| Level 1 | 基础 | 目录结构、配置文件、依赖 |
| Level 2 | 接口 | 接口定义、参数验证、返回类型 |
| Level 3 | 逻辑 | 业务逻辑、状态管理、错误处理 |
| Level 4 | 集成 | 多Agent协作、网络、安全 |

### 12.2 CLI命令

```bash
java -jar agent-sdk-0.8.0.jar init --name my-skill --path ./my-skill
java -jar agent-sdk-0.8.0.jar generate --type driver --interface ./interface.yaml
java -jar agent-sdk-0.8.0.jar validate --skill ./my-skill --level 4
java -jar agent-sdk-0.8.0.jar test --skill ./my-skill --type unit
java -jar agent-sdk-0.8.0.jar package --skill ./my-skill --output ./skill.zip
```

---

## 十三、版本历史

| 版本 | 日期 | 变更内容 |
|------|------|----------|
| v2.3.0 | 2026-03-02 | 完整规格说明书，包含分类、发现、生命周期管理 |
| v0.8.0 | 2026-02-25 | 新增北向协议、业务需求文档 |
| v0.7.3 | 2026-02-20 | 新增离线支持、事件驱动生命周期、GitHub/Gitee发现 |
| v0.7.0 | 2026-02-11 | 初始版本 |

---

## 附录A：技能市场界面设计

```
┌─────────────────────────────────────────────────────────┐
│  技能市场                                    [搜索...]  │
├─────────────────────────────────────────────────────────┤
│  👥 组织服务 (5)    💾 存储服务 (5)    🎨 UI生成 (1)   │
│  📨 消息通讯 (1)    ⚙️ 系统管理 (6)   🔧 工具服务 (2)  │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────┐   │
│  │ 👥 组织服务                                      │   │
│  │ ┌─────────┐ ┌─────────┐ ┌─────────┐            │   │
│  │ │ 飞书    │ │ 钉钉    │ │ 企业微信 │            │   │
│  │ │ [安装]  │ │ [安装]  │ │ [安装]  │            │   │
│  │ └─────────┘ └─────────┘ └─────────┘            │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

---

## 附录B：生命周期管理界面

```
┌──────────────────────────────────────────────────────────┐
│  [Header] Skill Lifecycle Management Dashboard          │
├──────────────────────────────────────────────────────────┤
│  +----------------+  +----------------+  +------------+  │
│  | Total Skills   |  | Active Skills  |  | Warnings   |  │
│  |     11         |  |      8         |  |     2      |  │
│  +----------------+  +----------------+  +------------+  │
│                                                          │
│  +--------------------------------------------------+    │
│  |              Real-time Status Chart               |    │
│  +--------------------------------------------------+    │
│                                                          │
│  +------------------------+  +----------------------+    │
│  |   Recent Events        |  |   Top Capabilities   |    │
│  +------------------------+  +----------------------+    │
└──────────────────────────────────────────────────────────┘
```

---

**文档结束**

*本规范由Ooder Team维护，如有疑问请联系 https://gitee.com/ooderCN*

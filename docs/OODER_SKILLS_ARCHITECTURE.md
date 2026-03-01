# Ooder Skills 架构设计详解

> 构建可插拔、可扩展的智能技能生态系统

---

## 一、概述

Ooder Skills 是一个基于 Spring Boot 的**可插拔技能系统**，采用模块化设计，支持技能的热插拔、动态发现和按需加载。本文将从架构设计、模块结构、配置规范、开发流程等方面进行全面解析。

### 1.1 设计理念

- **可插拔性**: 技能模块独立打包，支持运行时动态加载和卸载
- **可扩展性**: 通过标准接口和 SPI 机制实现功能扩展
- **可发现性**: 基于 YAML 索引实现技能的远程发现和安装
- **场景驱动**: 技能与场景绑定，实现能力按需组合

### 1.2 核心概念

| 概念 | 说明 |
|------|------|
| **Skill** | 技能单元，提供特定能力的独立模块 |
| **Scene** | 场景，定义技能的应用上下文和能力要求 |
| **Category** | 分类，对技能进行功能分组 |
| **SceneDriver** | 场景驱动，提供场景的核心能力实现 |
| **SkillIndex** | 技能索引，用于技能发现和分发 |

---

## 二、整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      Ooder Agent Platform                        │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │  Nexus UI   │  │ Skill Market│  │    Scene Engine         │  │
│  │  (控制台)   │  │  (技能市场) │  │    (场景引擎)            │  │
│  └──────┬──────┘  └──────┬──────┘  └───────────┬─────────────┘  │
│         │                │                      │                │
│         └────────────────┼──────────────────────┘                │
│                          ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │              Skill Hotplug Starter                          │ │
│  │           (技能热插拔启动器)                                  │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │ │
│  │  │ Discovery   │  │ Loader      │  │ Lifecycle Manager   │  │ │
│  │  │ (发现服务)  │  │ (加载器)    │  │ (生命周期管理)       │  │ │
│  │  └─────────────┘  └─────────────┘  └─────────────────────┘  │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                          │                                       │
│                          ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                    Skills Repository                        │ │
│  │                      (技能仓库)                              │ │
│  │                                                              │ │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │ │
│  │  │ org-*   │ │ vfs-*   │ │ msg-*   │ │ sys-*   │ ...       │ │
│  │  │组织服务 │ │存储服务 │ │消息服务 │ │系统服务 │           │ │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 三、技能分类体系

### 3.1 分类定义

Ooder Skills 采用**三级分类体系**：

```
Category (一级分类)
    └── SubCategory (二级分类)
            └── Skill (具体技能)
```

### 3.2 内置分类

| 分类ID | 名称 | 说明 | 场景驱动 |
|--------|------|------|----------|
| `org` | 组织服务 | 企业组织架构、用户认证 | org-driver |
| `vfs` | 存储服务 | 文件存储、对象存储 | vfs-driver |
| `msg` | 消息通讯 | 消息队列、通讯协议 | msg-driver |
| `sys` | 系统管理 | 系统监控、网络管理 | sys-driver |
| `ui` | UI生成 | 界面生成、设计转代码 | - |
| `payment` | 支付服务 | 支付渠道、退款管理 | payment-driver |
| `media` | 媒体发布 | 自媒体文章发布 | media-driver |
| `util` | 工具服务 | 通用工具、辅助功能 | - |
| `nexus-ui` | Nexus界面 | 管理界面、仪表盘 | - |

### 3.3 场景驱动

场景驱动是技能系统的核心能力提供者：

```yaml
sceneDrivers:
  - id: org
    name: Organization Driver
    capabilities:
      - user-auth      # 用户认证
      - user-manage    # 用户管理
      - org-manage     # 组织管理
      - role-manage    # 角色管理
    builtIn: true
    location: scene-engine
```

---

## 四、技能模块结构

### 4.1 目录结构

```
skill-{name}/
├── pom.xml                    # Maven 构建配置
├── skill.yaml                 # 技能元数据（新格式）
├── skill-manifest.yaml        # 技能清单（发布格式）
├── src/
│   └── main/
│       ├── java/
│       │   └── net/ooder/skill/{name}/
│       │       ├── {Name}SkillApplication.java  # 启动类
│       │       ├── controller/                   # 控制器
│       │       ├── service/                      # 服务层
│       │       ├── model/                        # 数据模型
│       │       └── config/                       # 配置类
│       └── resources/
│           ├── application.yml                   # 应用配置
│           └── static/                           # 静态资源
└── ui/                        # UI 资源（nexus-ui 类型）
    └── pages/
        └── index.html
```

### 4.2 技能类型

| 类型 | 说明 | 打包格式 | 示例 |
|------|------|----------|------|
| `service-skill` | 后端服务技能 | JAR | skill-mqtt |
| `nexus-ui` | Nexus界面技能 | ZIP | skill-nexus-dashboard-nexus-ui |
| `provider-skill` | 提供者技能 | JAR | skill-llm-volcengine |

---

## 五、配置规范

### 5.1 skill.yaml（元数据配置）

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-mqtt                    # 技能唯一标识
  name: MQTT服务技能                 # 技能名称
  version: 0.7.3                    # 版本号
  description: 提供MQTT Broker能力   # 描述
  author: Ooder Team                # 作者
  type: service-skill               # 类型

spec:
  type: service-skill
  
  # 能力声明
  capabilities:
    - mqtt-broker
    - mqtt-publish
    - mqtt-subscribe
    
  # 支持的场景
  scenes:
    - mqtt-messaging
    - iot-device
    
  # 配置参数
  parameters:
    - name: MQTT_PORT
      type: number
      required: false
      default: 1883
      description: MQTT服务端口
      
  # 执行配置
  execution:
    timeout: 30000
    memoryLimit: 256M
    cpuLimit: 1
    
  # 分发配置
  distribution:
    format: jar
    entrypoint: net.ooder.skill.mqtt.MqttSkillApplication
    assets:
      - name: skill-mqtt-0.7.3.jar
        platform: all
        url: https://github.com/ooderCN/skills/releases/download/...
        
  # 兼容性
  compatibility:
    sdkVersion: ">=0.7.0"
    javaVersion: ">=8"
```

### 5.2 Nexus-UI 技能配置

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-nexus-dashboard-nexus-ui
  name: Nexus仪表盘
  version: 1.0.0
  type: nexus-ui

spec:
  type: nexus-ui
  
  # Nexus UI 特有配置
  nexusUi:
    entry:
      page: index.html
      title: Nexus仪表盘
      icon: ri-dashboard-line
      
    menu:
      position: sidebar
      category: nexus
      order: 10
      
    layout:
      type: default
      sidebar: true
      header: true
      
  capabilities:
    - id: nexus-dashboard-view
      name: 查看仪表盘
      
  apis:
    - path: /api/nexus/stats
      method: GET
      description: 获取统计数据
```

### 5.3 skill-index.yaml（索引配置）

```yaml
apiVersion: ooder.io/v1
kind: SkillIndex

metadata:
  name: ooder-skills
  version: "2.3"
  description: Ooder Skills Repository

spec:
  # 场景驱动定义
  sceneDrivers:
    - id: org
      name: Organization Driver
      capabilities: [...]
      
  # 分类定义
  categories:
    - id: org
      name: 组织服务
      icon: users
      sceneDriver: org
      
  # 技能列表
  skills:
    - skillId: skill-mqtt
      name: MQTT Service Skill
      version: "0.7.3"
      category: msg
      downloadUrl: https://github.com/ooderCN/skills/releases/...
      
  # 场景定义
  scenes:
    - sceneId: mqtt-messaging
      name: MQTT Messaging
      requiredCapabilities:
        - mqtt-broker
        - mqtt-publish
```

---

## 六、技能生命周期

### 6.1 生命周期状态

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│ Discovered │ ──▶ │ Downloaded │ ──▶ │ Installed │ ──▶ │ Active  │
└─────────┘     └─────────┘     └─────────┘     └─────────┘
     │               │               │               │
     │               │               │               │
     ▼               ▼               ▼               ▼
  (发现)          (下载)          (安装)          (激活)
```

### 6.2 核心流程

#### 发现流程

```
1. 加载 skill-index.yaml
2. 解析分类、技能、场景列表
3. 扫描本地已安装技能
4. 合并远程和本地技能信息
```

#### 安装流程

```
1. 从索引获取技能下载 URL
2. 下载 JAR/ZIP 文件
3. 解压/复制到技能目录
4. 扫描并加载 skill.yaml
5. 注册菜单（Nexus-UI 类型）
```

#### 菜单注册流程

```
1. 读取 skill.yaml 的 nexusUi.menu 配置
2. 生成菜单项配置
3. 写入 menu-config.json
4. 前端动态加载菜单
```

---

## 七、开发指南

### 7.1 创建新技能

#### 步骤 1：创建项目结构

```bash
mkdir -p skills/skill-{name}/src/main/java/net/ooder/skill/{name}
mkdir -p skills/skill-{name}/src/main/resources
```

#### 步骤 2：配置 pom.xml

```xml
<project>
    <parent>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-skills</artifactId>
        <version>0.7.3</version>
    </parent>
    
    <artifactId>skill-{name}</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>skill-hotplug-starter</artifactId>
        </dependency>
    </dependencies>
</project>
```

#### 步骤 3：编写启动类

```java
@SpringBootApplication
public class MySkillApplication {
    public static void main(String[] args) {
        SpringApplication.run(MySkillApplication.class, args);
    }
}
```

#### 步骤 4：编写 skill.yaml

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-my-skill
  name: My Skill
  version: 1.0.0
  type: service-skill

spec:
  capabilities:
    - my-capability
  scenes:
    - my-scene
```

#### 步骤 5：更新索引

在 `skill-index.yaml` 中添加技能条目：

```yaml
- skillId: skill-my-skill
  name: My Skill
  version: "1.0.0"
  category: util
  downloadUrl: https://github.com/ooderCN/skills/releases/download/...
```

### 7.2 技能命名规范

| 类型 | 命名格式 | 示例 |
|------|----------|------|
| 服务技能 | `skill-{功能}-{类型}` | skill-mqtt |
| 组织服务 | `skill-org-{平台}` | skill-org-dingding |
| 存储服务 | `skill-vfs-{存储类型}` | skill-vfs-minio |
| LLM提供者 | `skill-llm-{厂商}` | skill-llm-volcengine |
| 支付服务 | `skill-payment-{渠道}` | skill-payment-alipay |
| 媒体发布 | `skill-media-{平台}` | skill-media-wechat |
| Nexus界面 | `skill-{功能}-nexus-ui` | skill-nexus-dashboard-nexus-ui |

---

## 八、技能统计

### 8.1 当前技能数量

| 分类 | 数量 | 代表技能 |
|------|------|----------|
| 组织服务 | 5 | skill-org-dingding, skill-org-feishu |
| 存储服务 | 5 | skill-vfs-local, skill-vfs-minio |
| 消息通讯 | 7 | skill-mqtt, skill-im, skill-email |
| 系统管理 | 20+ | skill-network, skill-agent, skill-health |
| UI生成 | 1 | skill-a2ui |
| 支付服务 | 3 | skill-payment-alipay, skill-payment-wechat |
| 媒体发布 | 5 | skill-media-wechat, skill-media-weibo |
| LLM提供者 | 4 | skill-llm-volcengine, skill-llm-deepseek |
| Nexus界面 | 5 | skill-nexus-dashboard-nexus-ui |
| **总计** | **62+** | |

### 8.2 场景覆盖

- **系统管理场景**: 15+
- **通讯协作场景**: 10+
- **业务场景**: 8+
- **IoT场景**: 5+
- **基础设施场景**: 3+

---

## 九、最佳实践

### 9.1 技能设计原则

1. **单一职责**: 每个技能专注一个特定功能
2. **接口标准化**: 使用标准 REST API 和数据格式
3. **配置外部化**: 通过参数支持运行时配置
4. **错误处理**: 完善的异常处理和日志记录
5. **版本兼容**: 遵循语义化版本规范

### 9.2 性能优化建议

1. **懒加载**: 按需加载技能模块
2. **资源隔离**: 限制技能的内存和 CPU 使用
3. **缓存策略**: 缓存技能元数据和配置
4. **异步处理**: 耗时操作采用异步执行

### 9.3 安全考虑

1. **权限控制**: 基于角色的技能访问控制
2. **输入验证**: 严格校验外部输入
3. **敏感信息**: 加密存储配置中的敏感信息
4. **审计日志**: 记录技能操作日志

---

## 十、总结

Ooder Skills 架构通过**模块化设计**、**标准化配置**和**场景驱动**的理念，构建了一个灵活、可扩展的技能生态系统。开发者可以快速开发、发布和部署新技能，用户可以按需发现、安装和使用技能，实现了真正的**能力即服务**。

---

**相关链接**:
- GitHub: https://github.com/ooderCN/skills
- Gitee 镜像: https://gitee.com/ooderCN/skills
- 文档: https://github.com/ooderCN/skills/tree/main/docs

---

*作者: Ooder Team*  
*更新时间: 2026-02-27*

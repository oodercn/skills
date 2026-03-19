# skill-mqtt

MQTT服务技能 - 提供轻量级MQTT Broker能力和多提供商支持

## 功能特性

- **零配置启动** - 无需任何配置即可启动 MQTT 服务
- **场景化配置** - 根据场景类型自动应用最佳配置
- **技能发现** - 从 GitHub/Gitee 发现和安装技能
- **SYS 场景配置** - 从系统场景获取推荐配置
- **多提供商支持** - 支持多种 MQTT 服务提供商
- **自动降级** - 外部服务不可用时自动降级

## 快速开始

### 零配置启动

```bash
# 最简单的方式 - 零配置启动
java -jar skill-mqtt-0.7.1.jar

# 服务将自动启动：
# - MQTT Broker: 1883 端口
# - WebSocket: 8083 端口
# - 使用内置轻量级 Broker
```

### 场景化启动

```bash
# 指定场景类型启动
java -jar skill-mqtt-0.7.1.jar --mqtt.scene.scene-type=mqtt-messaging

# IoT 设备场景
java -jar skill-mqtt-0.7.1.jar --mqtt.scene.scene-type=iot-device

# 企业场景
java -jar skill-mqtt-0.7.1.jar --mqtt.scene.scene-type=enterprise
```

### 配置文件方式

```yaml
# application.yml
mqtt:
  enabled: true
  provider: lightweight-mqtt
  
  scene:
    scene-type: mqtt-messaging
    org-id: my-company
    auto-configure: true
  
  discovery:
    enabled: true
    auto-install: true
```

## 场景类型

| 场景类型 | 最大连接 | 匿名访问 | 说明 |
|---------|---------|---------|------|
| mqtt-messaging | 1000 | 否 | 消息场景 |
| iot-device | 10000 | 是 | IoT 设备场景 |
| team | 100 | 否 | 团队协作场景 |
| enterprise | 5000 | 否 | 企业场景 |

## 技能发现与安装

### 发现可用技能

```bash
# 列出所有可用技能
GET /api/mqtt/discovery/skills

# 列出所有场景模板
GET /api/mqtt/discovery/scenes

# 查看场景推荐配置
GET /api/mqtt/discovery/sys-config/{sceneType}
```

### 安装技能

```bash
# 安装指定技能
POST /api/mqtt/discovery/skills/{skillId}/install

# 为场景自动安装推荐技能
POST /api/mqtt/discovery/scenes/{sceneType}/auto-install
```

### SYS 场景配置

```bash
# 获取 SYS 场景配置
GET /api/mqtt/discovery/sys-config/mqtt-messaging

# 应用 SYS 场景配置
POST /api/mqtt/discovery/sys-config/mqtt-messaging/apply
```

## 服务提供商

| 提供者 | 类型 | 优先级 | 说明 |
|--------|------|--------|------|
| lightweight-mqtt | LIGHTWEIGHT | 100 | 内置轻量级 Broker（降级方案） |
| emqx-enterprise | ENTERPRISE_SELF_HOSTED | 50 | EMQX 企业级 Broker |
| mosquitto-enterprise | ENTERPRISE_SELF_HOSTED | 60 | Mosquitto Broker |
| aliyun-iot | CLOUD_MANAGED | 10 | 阿里云 IoT MQTT |
| tencent-iot | CLOUD_MANAGED | 15 | 腾讯云 IoT MQTT |

## Topic 规范

```
ooder/
├── p2p/{userId}/inbox              # 点对点消息
├── group/{groupId}/broadcast       # 群组消息
├── topic/{topicName}/data          # 主题订阅
├── broadcast/{channel}             # 广播消息
├── sensor/{type}/{id}/data         # 传感器数据
├── command/{type}/{id}/request     # 设备命令请求
├── command/{type}/{id}/response    # 设备命令响应
└── system/{eventType}              # 系统消息
```

## API 接口

### 基础 API

```
GET  /api/mqtt/info                    # 服务信息
GET  /api/mqtt/broker/status           # Broker 状态
POST /api/mqtt/broker/start            # 启动 Broker
POST /api/mqtt/broker/stop             # 停止 Broker
POST /api/mqtt/publish                 # 发布消息
POST /api/mqtt/subscribe               # 订阅主题
```

### 提供者管理

```
GET  /api/mqtt/providers               # 列出提供者
POST /api/mqtt/providers/switch        # 切换提供者
```

### 发现与安装

```
GET  /api/mqtt/discovery/skills        # 发现技能
GET  /api/mqtt/discovery/scenes        # 发现场景
GET  /api/mqtt/discovery/sys-config/{sceneType}  # SYS 配置
POST /api/mqtt/discovery/skills/{id}/install     # 安装技能
POST /api/mqtt/discovery/scenes/{type}/auto-install  # 自动安装
```

## 用户故事

### 故事一：零配置启动

```
作为开发者，我希望零配置启动 MQTT 服务，
以便快速开始开发和测试。

步骤：
1. java -jar skill-mqtt-0.7.1.jar
2. 系统自动应用默认配置
3. Broker 在 1883 端口启动
4. 可以通过 REST API 发布/订阅消息
```

### 故事二：场景化配置

```
作为运维人员，我希望根据场景类型自动配置，
以便快速部署不同类型的服务。

步骤：
1. 指定场景类型: --mqtt.scene.scene-type=iot-device
2. 系统从 SYS 场景获取推荐配置
3. 自动应用最佳配置（最大连接、匿名访问等）
4. 服务按场景需求启动
```

### 故事三：自助安装技能

```
作为用户，我希望从 GitHub 发现并安装所需技能，
以便扩展系统功能。

步骤：
1. GET /api/mqtt/discovery/scenes/{sceneType}
2. 查看场景推荐的技能列表
3. POST /api/mqtt/discovery/skills/{skillId}/install
4. 技能自动下载并安装到本地
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| mqtt.enabled | boolean | true | 是否启用 MQTT 服务 |
| mqtt.provider | string | lightweight-mqtt | MQTT 提供者 ID |
| mqtt.broker.port | number | 1883 | MQTT 端口 |
| mqtt.broker.websocket-port | number | 8083 | WebSocket 端口 |
| mqtt.broker.max-connections | number | 10000 | 最大连接数 |
| mqtt.scene.scene-type | string | | 场景类型 |
| mqtt.scene.org-id | string | | 组织 ID |
| mqtt.scene.auto-configure | boolean | true | 自动配置 |
| mqtt.discovery.enabled | boolean | true | 启用技能发现 |
| mqtt.discovery.auto-install | boolean | false | 自动安装技能 |

## 版本历史

- 0.7.1 - 初始版本
  - 零配置启动支持
  - 场景化配置支持
  - 技能发现与自助安装
  - SYS 场景配置获取

# skill-mqtt

MQTT服务技能 - 提供轻量级MQTT Broker能力和多提供商支持

## 功能特性

- **MQTT Broker服务** - 内置轻量级MQTT Broker作为降级方案
- **消息发布/订阅** - 支持MQTT消息发布和订阅
- **点对点消息** - 支持用户间点对点消息通信
- **主题广播** - 支持主题订阅和广播消息
- **设备命令** - 支持IoT设备命令控制
- **多提供商支持** - 支持多种MQTT服务提供商

## 服务提供商

| 提供者 | 类型 | 说明 | 优先级 |
|--------|------|------|--------|
| lightweight-mqtt | LIGHTWEIGHT | 内置轻量级MQTT Broker | 100 |
| emqx-enterprise | ENTERPRISE_SELF_HOSTED | EMQX企业级MQTT Broker | 50 |
| mosquitto-enterprise | ENTERPRISE_SELF_HOSTED | Mosquitto MQTT Broker | 60 |
| aliyun-iot | CLOUD_MANAGED | 阿里云IoT MQTT服务 | 10 |
| tencent-iot | CLOUD_MANAGED | 腾讯云IoT MQTT服务 | 15 |

## Topic规范

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

## 能力列表

| 能力ID | 名称 | 说明 |
|--------|------|------|
| mqtt-broker | MQTT Broker | 提供MQTT Broker服务 |
| mqtt-publish | MQTT Publish | 发布MQTT消息 |
| mqtt-subscribe | MQTT Subscribe | 订阅MQTT主题 |
| mqtt-p2p | MQTT P2P | 点对点消息 |
| mqtt-topic | MQTT Topic | 主题订阅/广播 |
| mqtt-command | MQTT Command | 设备命令 |

## API接口

### 获取服务信息
```
GET /api/mqtt/info
```

### 获取Broker状态
```
GET /api/mqtt/broker/status
```

### 启动Broker
```
POST /api/mqtt/broker/start
```

### 停止Broker
```
POST /api/mqtt/broker/stop
```

### 发布消息
```
POST /api/mqtt/publish
{
  "topic": "ooder/topic/news/data",
  "payload": "Hello World",
  "qos": 1
}
```

### 订阅主题
```
POST /api/mqtt/subscribe
{
  "topic": "ooder/topic/#",
  "qos": 1
}
```

### 发送点对点消息
```
POST /api/mqtt/p2p
{
  "from": "user-001",
  "to": "user-002",
  "content": "Hello!"
}
```

### 发送设备命令
```
POST /api/mqtt/command
{
  "deviceType": "sensor",
  "deviceId": "temp-001",
  "command": "read"
}
```

### 列出提供者
```
GET /api/mqtt/providers
```

### 切换提供者
```
POST /api/mqtt/providers/switch
{
  "providerId": "emqx-enterprise"
}
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| mqttProvider | string | lightweight-mqtt | MQTT服务提供者ID |
| mqttPort | number | 1883 | MQTT服务端口 |
| mqttWsPort | number | 8083 | WebSocket端口 |
| mqttWsEnabled | boolean | true | 是否启用WebSocket |
| mqttMaxConnections | number | 10000 | 最大连接数 |
| mqttAllowAnonymous | boolean | false | 是否允许匿名连接 |
| mqttUsername | string | - | MQTT用户名 |
| mqttPassword | string | - | MQTT密码 |

## 使用示例

```java
// 创建MQTT服务
MqttClusterService mqttService = MqttClusterService.getInstance();

// 初始化
Map<String, Object> config = new HashMap<>();
config.put("mqttProvider", "lightweight-mqtt");
mqttService.initialize("mqtt-messaging", "group-001", config);

// 启动服务
mqttService.start();

// 发布消息
MqttMessage message = MqttMessage.create("ooder/topic/news/data", "Hello World");
message.setQos(1);

// 订阅主题
mqttService.addSubscription("ooder/topic/#", "session-001", 1);

// 停止服务
mqttService.stop();
```

## 版本历史

- 0.7.1 - 初始版本

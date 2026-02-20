# skill-mqtt 架构分析与用户指南

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-02-20 |
| 状态 | 待评审 |

---

## 一、架构规范符合性检查

### 1.1 Skills 架构规范对照

| 规范项 | 要求 | skill-mqtt 实现 | 状态 |
|--------|------|----------------|------|
| **skill.yaml** | 必须包含元数据定义 | ✅ 完整定义 capabilities, scenes, providers, config | ✅ 符合 |
| **skill-manifest.yaml** | 必须包含发布清单 | ✅ 完整定义 apiVersion, spec, distribution | ✅ 符合 |
| **MqttServiceProvider** | 必须定义服务提供者接口 | ✅ 接口定义完整 (getProviderId, createServer, isAvailable, getPriority) | ✅ 符合 |
| **MqttProviderFactory** | 必须支持提供者注册和切换 | ✅ 工厂模式实现，支持 register/unregister/getProvider | ✅ 符合 |
| **MqttServer** | 必须定义服务端接口 | ✅ 接口定义完整 (start, stop, initialize, onConnect) | ✅ 符合 |
| **MqttServerConfig** | 必须支持配置构建 | ✅ Builder 模式实现 | ✅ 符合 |
| **REST API** | 必须提供标准 API | ✅ /api/mqtt/* 系列接口 | ✅ 符合 |
| **Topic 规范** | 必须定义 Topic 命名规范 | ✅ MqttTopicSpec 完整定义 | ✅ 符合 |

### 1.2 问题清单

| 问题 | 严重程度 | 说明 |
|------|---------|------|
| **缺少 SkillService 接口实现** | 高 | 未实现 SDK 的 SkillService 接口，无法与 Agent 集成 |
| **Provider 可用性检查未实现** | 中 | isAvailable() 方法返回硬编码值，未实际检测 |
| **缺少配置文件切换机制** | 中 | 仅支持 API 切换，不支持 application.yml 配置 |
| **缺少健康检查集成** | 低 | 未集成 Spring Boot Actuator 健康检查 |

---

## 二、MQTT 实现切换机制

### 2.1 提供者优先级模型

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           提供者优先级模型                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  优先级值越小，优先级越高                                                    │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  Priority 10: aliyun-iot (CLOUD_MANAGED)                            │    │
│  │  - 阿里云 IoT MQTT 服务                                              │    │
│  │  - 最高优先级，适合生产环境                                           │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                      ↓                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  Priority 15: tencent-iot (CLOUD_MANAGED)                           │    │
│  │  - 腾讯云 IoT MQTT 服务                                              │    │
│  │  - 备选云服务                                                        │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                      ↓                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  Priority 50: emqx-enterprise (ENTERPRISE_SELF_HOSTED)              │    │
│  │  - EMQX 企业级自建服务                                               │    │
│  │  - 适合大规模部署                                                    │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                      ↓                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  Priority 60: mosquitto-enterprise (ENTERPRISE_SELF_HOSTED)         │    │
│  │  - Mosquitto 自建服务                                                │    │
│  │  - 轻量级自建方案                                                    │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                      ↓                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  Priority 100: lightweight-mqtt (LIGHTWEIGHT)                       │    │
│  │  - 内置轻量级 Broker                                                 │    │
│  │  - 降级方案，零依赖                                                  │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 切换机制

#### 2.2.1 自动选择机制

```java
// MqttProviderFactory.createDefaultServer() 实现
public MqttServer createDefaultServer(MqttServerConfig config) {
    // 1. 获取所有可用提供者
    List<MqttServiceProvider> availableProviders = getAvailableProviders();
    
    // 2. 按优先级排序（优先级值小的优先）
    Collections.sort(availableProviders, (p1, p2) -> 
        Integer.compare(p1.getPriority(), p2.getPriority()));
    
    // 3. 选择最高优先级的可用提供者
    MqttServiceProvider selected = availableProviders.get(0);
    return selected.createServer(config);
}
```

#### 2.2.2 手动切换机制

**方式一：REST API 切换**

```bash
# 查看所有提供者
GET /api/mqtt/providers

# 切换到指定提供者
POST /api/mqtt/providers/switch
{
  "providerId": "emqx-enterprise"
}
```

**方式二：启动时指定**

```bash
# 启动时指定提供者
POST /api/mqtt/broker/start
{
  "providerId": "aliyun-iot",
  "port": 1883,
  "maxConnections": 50000
}
```

#### 2.2.3 配置文件切换（建议补充）

```yaml
# application.yml (建议实现)
mqtt:
  broker:
    enabled: true
    provider: emqx-enterprise    # 指定提供者
    port: 1883
    websocketPort: 8083
    maxConnections: 10000
```

---

## 三、最小使用集合

### 3.1 最小依赖

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MQTT Client -->
    <dependency>
        <groupId>org.eclipse.paho</groupId>
        <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
        <version>1.2.5</version>
    </dependency>
</dependencies>
```

### 3.2 最小配置

```yaml
# application.yml
server:
  port: 8080

mqtt:
  broker:
    enabled: true
    provider: lightweight-mqtt
```

### 3.3 最小代码

```java
// 启动类
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// 使用示例
@RestController
public class DemoController {
    
    @PostMapping("/send")
    public String send(@RequestBody String message) {
        // 使用内置轻量级 Broker
        MqttProviderFactory factory = MqttProviderFactory.getInstance();
        factory.initialize();
        
        MqttServer server = factory.createDefaultServer(
            MqttServerConfig.builder()
                .port(1883)
                .build()
        );
        server.start();
        
        // 发送消息
        MqttMessage msg = MqttMessage.create("ooder/topic/demo/data", message);
        return "Message sent: " + msg.getMessageId();
    }
}
```

### 3.4 最小 API 集合

| API | 方法 | 说明 | 最小集合 |
|-----|------|------|---------|
| `/api/mqtt/info` | GET | 获取服务信息 | ✅ |
| `/api/mqtt/broker/start` | POST | 启动 Broker | ✅ |
| `/api/mqtt/broker/stop` | POST | 停止 Broker | ✅ |
| `/api/mqtt/publish` | POST | 发布消息 | ✅ |
| `/api/mqtt/subscribe` | POST | 订阅主题 | ✅ |
| `/api/mqtt/providers` | GET | 列出提供者 | 可选 |
| `/api/mqtt/providers/switch` | POST | 切换提供者 | 可选 |
| `/api/mqtt/p2p` | POST | 点对点消息 | 可选 |
| `/api/mqtt/command` | POST | 设备命令 | 可选 |

---

## 四、用户故事

### 4.1 故事一：零配置启动

```
用户故事:
作为开发者，我希望零配置启动 MQTT 服务，
以便快速开始开发和测试。

前置条件:
1. 已下载 skill-mqtt JAR 包
2. Java 8+ 环境已安装

主流程:
1. 运行 java -jar skill-mqtt-0.7.1.jar
2. 系统自动使用 lightweight-mqtt 提供者
3. Broker 在 1883 端口启动
4. WebSocket 在 8083 端口启动
5. 可以通过 REST API 发布/订阅消息

验收标准:
- 无需任何配置文件即可启动
- 默认使用内置轻量级 Broker
- 基本消息发布/订阅功能可用

技术实现:
- MqttProviderFactory 默认注册 LightweightMqttProvider
- LightweightMqttProvider.isAvailable() 始终返回 true
- 默认配置使用标准端口
```

### 4.2 故事二：切换到云服务

```
用户故事:
作为运维人员，我希望将 MQTT 服务切换到阿里云 IoT，
以便获得更高的可靠性和扩展性。

前置条件:
1. 已有阿里云 IoT 账号
2. 已创建 IoT 产品和设备
3. 已获取 AccessKey 和设备证书

主流程:
1. 配置阿里云 IoT 凭证
2. 调用 API 切换提供者:
   POST /api/mqtt/providers/switch
   { "providerId": "aliyun-iot" }
3. 系统验证配置有效性
4. 切换到阿里云 IoT 服务
5. 后续消息通过阿里云 IoT 转发

验收标准:
- 切换过程无消息丢失
- 配置错误时回退到原提供者
- 切换状态可通过 API 查询

技术实现:
- AliyunIoTProvider.isAvailable() 检测凭证有效性
- MqttProviderFactory 实现优雅切换
- 切换时保持现有连接
```

### 4.3 故事三：自建 EMQX 集群

```
用户故事:
作为架构师，我希望使用自建的 EMQX 集群，
以便满足数据安全和合规要求。

前置条件:
1. 已部署 EMQX 集群
2. 已配置集群地址和认证信息
3. 网络可达

主流程:
1. 配置 EMQX 连接信息:
   - brokerUrl: tcp://emqx-cluster:1883
   - dashboardUrl: http://emqx-cluster:18083
   - apiUsername/apiPassword
2. 启动时指定提供者:
   POST /api/mqtt/broker/start
   { "providerId": "emqx-enterprise" }
3. 系统连接到 EMQX 集群
4. 消息通过 EMQX 路由

验收标准:
- 支持集群连接
- 支持共享订阅
- 支持规则引擎

技术实现:
- EmqxEnterpriseProvider 实现 MqttServer 适配器
- 支持 EMQX 特有功能（共享订阅、规则引擎）
- 集成 EMQX Dashboard API
```

### 4.4 故事四：降级到轻量级 Broker

```
用户故事:
作为系统管理员，我希望在外部 MQTT 服务不可用时，
自动降级到内置轻量级 Broker，
以便保证服务可用性。

前置条件:
1. 当前使用云服务或自建服务
2. 外部服务出现故障

主流程:
1. 系统检测到外部服务不可用
2. 自动切换到 lightweight-mqtt
3. 记录降级事件
4. 发送告警通知
5. 定期尝试恢复外部服务
6. 恢复后自动切回

验收标准:
- 降级时间 < 5秒
- 降级过程无消息丢失
- 支持自动恢复

技术实现:
- MqttProviderFactory.getAvailableProviders() 过滤不可用提供者
- LightweightMqttProvider 作为最终降级方案
- 实现健康检查和自动切换
```

---

## 五、建议补充内容

### 5.1 实现 SkillService 接口

```java
package net.ooder.skill.mqtt;

import net.ooder.sdk.skill.SkillService;
import net.ooder.sdk.skill.SkillContext;
import net.ooder.sdk.skill.SkillResult;

public class MqttSkillService implements SkillService {
    
    private MqttServer mqttServer;
    private MqttProviderFactory providerFactory;
    
    @Override
    public void initialize(SkillContext context) {
        String providerId = context.getConfig("mqttProvider", "lightweight-mqtt");
        providerFactory = MqttProviderFactory.getInstance();
        providerFactory.initialize();
        
        MqttServerConfig config = buildConfig(context);
        mqttServer = providerFactory.createServer(providerId, config);
    }
    
    @Override
    public void start() {
        if (mqttServer != null) {
            mqttServer.start();
        }
    }
    
    @Override
    public void stop() {
        if (mqttServer != null) {
            mqttServer.stop();
        }
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        String action = (String) params.get("action");
        switch (action) {
            case "publish":
                return doPublish(params);
            case "subscribe":
                return doSubscribe(params);
            default:
                return SkillResult.error("Unknown action: " + action);
        }
    }
    
    @Override
    public List<Capability> getCapabilities() {
        return Arrays.asList(
            new Capability("mqtt-broker", "MQTT Broker"),
            new Capability("mqtt-publish", "MQTT Publish"),
            new Capability("mqtt-subscribe", "MQTT Subscribe")
        );
    }
}
```

### 5.2 实现配置文件切换

```java
@ConfigurationProperties(prefix = "mqtt.broker")
public class MqttBrokerProperties {
    private boolean enabled = true;
    private String provider = "lightweight-mqtt";
    private int port = 1883;
    private int websocketPort = 8083;
    private int maxConnections = 10000;
    private boolean allowAnonymous = false;
    private String username;
    private String password;
    
    // getters and setters
}

@Configuration
@EnableConfigurationProperties(MqttBrokerProperties.class)
public class MqttAutoConfiguration {
    
    @Bean
    @ConditionalOnProperty(prefix = "mqtt.broker", name = "enabled", havingValue = "true")
    public MqttServer mqttServer(MqttBrokerProperties properties) {
        MqttProviderFactory factory = MqttProviderFactory.getInstance();
        factory.initialize();
        
        MqttServerConfig config = MqttServerConfig.builder()
            .port(properties.getPort())
            .websocketPort(properties.getWebsocketPort())
            .maxConnections(properties.getMaxConnections())
            .allowAnonymous(properties.isAllowAnonymous())
            .auth(properties.getUsername(), properties.getPassword())
            .build();
        
        return factory.createServer(properties.getProvider(), config);
    }
}
```

### 5.3 实现健康检查

```java
@Component
public class MqttHealthIndicator implements HealthIndicator {
    
    @Autowired
    private MqttServer mqttServer;
    
    @Override
    public Health health() {
        if (mqttServer == null) {
            return Health.down()
                .withDetail("error", "MQTT server not initialized")
                .build();
        }
        
        if (!mqttServer.isRunning()) {
            return Health.down()
                .withDetail("status", mqttServer.getStatus().name())
                .build();
        }
        
        return Health.up()
            .withDetail("serverId", mqttServer.getServerId())
            .withDetail("connectedCount", mqttServer.getConnectedCount())
            .withDetail("status", mqttServer.getStatus().name())
            .build();
    }
}
```

---

## 六、总结

### 6.1 架构符合性评分

| 维度 | 评分 | 说明 |
|------|------|------|
| **接口设计** | 90% | 接口定义完整，缺少 SDK 集成 |
| **提供者抽象** | 95% | 工厂模式完整，优先级机制清晰 |
| **配置管理** | 70% | 缺少配置文件切换支持 |
| **API 设计** | 85% | REST API 完整，缺少 SDK 调用方式 |
| **文档完整性** | 80% | README 完整，缺少用户指南 |

### 6.2 总体评估

skill-mqtt 架构设计 **基本符合** Skills 规范，具备以下优点：

1. **提供者抽象完善** - 支持多种 MQTT 实现的无缝切换
2. **优先级机制清晰** - 自动选择最优可用提供者
3. **降级方案完备** - 内置轻量级 Broker 作为最终降级方案
4. **Topic 规范完整** - 定义了清晰的 Topic 命名规范

需要补充的内容：

1. **实现 SkillService 接口** - 与 Agent SDK 集成
2. **配置文件切换支持** - 支持 application.yml 配置
3. **健康检查集成** - 集成 Spring Boot Actuator
4. **提供者可用性检测** - 实现真实的连接检测

---

**文档状态**: 待评审  
**下一步**: 实现建议补充内容

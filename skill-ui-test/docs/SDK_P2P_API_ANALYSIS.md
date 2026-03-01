# Agent SDK P2P API 能力分析

## 一、SDK P2P 核心能力

### 1.1 已实现的P2P功能

| 功能模块 | 实现类 | 状态 | 说明 |
|----------|--------|------|------|
| 节点发现 | PeerDiscovery | ✅ 完整 | UDP多播发现 |
| 消息广播 | GossipProtocol | ✅ 完整 | Gossip协议 |
| 分布式存储 | DhtNode | ✅ 完整 | DHT实现 |
| 链路管理 | LinkManager | ✅ 完整 | 多类型链路 |
| 路由管理 | RouteManager | ✅ 完整 | 路径发现 |
| A2A通信 | A2ACommunicationManager | ✅ 完整 | Agent间通信 |
| 网络服务 | NetworkService | ✅ 完整 | 完整API |

### 1.2 发现机制

```
混合发现机制
├── UDP广播发现
│   ├── 多播地址: 239.255.255.250:1900
│   ├── 发现消息: OODER_PEER_DISCOVERY
│   └── 超时清理: 120秒
├── HTTP查询发现
│   ├── 查询已知端点
│   └── 查询组织域
└── 配置文件发现
    ├── 读取 peer-config.json
    └── 健康检查
```

---

## 二、需求与SDK能力对照

### 2.1 用户故事对照

| 故事ID | 用户故事 | SDK支持 | 满足度 | 说明 |
|--------|----------|---------|--------|------|
| NL-004 | P2P共享能力 | ✅ | 100% | PeerDiscovery + A2ACommunicationManager |
| NL-005 | 页面复制渲染 | ✅ | 90% | NetworkService + LinkManager |
| SC-002 | 远程部署技能 | ✅ | 80% | A2ACommunicationManager |
| NE-002 | 推送应用到客户端 | ✅ | 90% | GossipProtocol + A2ACommunicationManager |

### 2.2 功能需求对照

| 功能需求 | SDK API | 满足度 | 使用方式 |
|----------|---------|--------|----------|
| 节点发现 | PeerDiscovery.start() | 100% | 直接调用 |
| 节点列表 | PeerDiscovery.getDiscoveredPeers() | 100% | 直接调用 |
| 节点事件 | DiscoveryListener | 100% | 注册监听器 |
| 消息广播 | GossipProtocol.broadcast() | 100% | 直接调用 |
| 消息接收 | GossipProtocol.receive() | 100% | 注册监听器 |
| 数据存储 | DhtNode.put/get() | 100% | 直接调用 |
| 链路创建 | NetworkService.createLink() | 100% | 直接调用 |
| 链路质量 | NetworkService.getLinkQuality() | 100% | 直接调用 |
| 路径发现 | NetworkService.findOptimalPath() | 100% | 直接调用 |
| 能力调用 | A2ACommunicationManager.invokeCapability() | 100% | 直接调用 |
| Agent通信 | A2ACommunicationManager.sendMessage() | 100% | 直接调用 |

---

## 三、SDK API 详细说明

### 3.1 NetworkService 接口

```java
public interface NetworkService {
    // 链路管理
    LinkInfo createLink(String sourceId, String targetId, LinkInfo.LinkType type);
    CompletableFuture<LinkInfo> createLinkAsync(String sourceId, String targetId, LinkInfo.LinkType type);
    Optional<LinkInfo> getLink(String linkId);
    void removeLink(String linkId);
    List<LinkInfo> getAllLinks();
    
    // 质量监控
    LinkQualityInfo getLinkQuality(String linkId);
    void enableQualityMonitor(long intervalMs);
    void updateLinkQuality(String linkId, int latency, double packetLoss);
    
    // 路径发现
    List<LinkInfo> findOptimalPath(String sourceId, String targetId);
    List<List<LinkInfo>> findAllPaths(String sourceId, String targetId, int maxPaths);
    
    // 事件监听
    void addLinkListener(LinkListener listener);
    void removeLinkListener(LinkListener listener);
}
```

**满足需求**: ✅ 完全满足P2P网络管理需求

### 3.2 DiscoveryProtocol 接口

```java
public interface DiscoveryProtocol {
    CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request);
    CompletableFuture<List<PeerInfo>> discoverPeers();
    CompletableFuture<PeerInfo> discoverMcp();
    void addDiscoveryListener(DiscoveryListener listener);
    void startBroadcast();
    void stopBroadcast();
    boolean isBroadcasting();
}
```

**满足需求**: ✅ 完全满足节点发现需求

### 3.3 A2ACommunicationManager 接口

```java
public interface A2ACommunicationManager {
    CompletableFuture<A2AMessage> sendMessage(String targetAgentId, A2AMessage message, A2AContext context);
    CompletableFuture<Map<String, A2AMessage>> broadcast(String sceneId, A2AMessage message, A2AContext context);
    CompletableFuture<Object> invokeCapability(String targetAgentId, String capId, Map<String, Object> params, A2AContext context);
    void registerHandler(A2AMessageHandler handler);
    void start();
    void stop();
}
```

**满足需求**: ✅ 完全满足Agent间通信需求

### 3.4 GossipProtocol 实现

```java
public class GossipProtocol {
    private int fanout = 3;           // 扇出数量
    private long messageTtl = 300000; // 消息TTL: 5分钟
    
    public void broadcast(String topic, byte[] payload);  // 广播消息
    public void receive(GossipMessage message);           // 接收消息
    public void addPeer(GossipPeer peer);                 // 添加节点
    public void addListener(GossipListener listener);     // 添加监听器
}
```

**满足需求**: ✅ 完全满足推送服务需求

---

## 四、集成方案

### 4.1 skill-ui-test 集成 SDK

#### 4.1.1 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>
```

#### 4.1.2 配置P2P服务

```yaml
ooder:
  agent:
    id: "skill-test-agent"
    name: "Skill Test Agent"
    type: "mcp"
  udp:
    port: 9876
    host: 0.0.0.0
  discovery:
    multicast: 239.255.255.250
    port: 1900
    interval: 30000
    timeout: 120000
```

#### 4.1.3 创建P2P服务封装

```java
@Service
public class P2PService {
    
    @Autowired
    private NetworkService networkService;
    
    @Autowired
    private DiscoveryProtocol discoveryProtocol;
    
    @Autowired
    private A2ACommunicationManager communicationManager;
    
    // 发现节点
    public List<PeerInfo> discoverPeers() {
        return discoveryProtocol.discoverPeers().join();
    }
    
    // 创建P2P链路
    public LinkInfo createP2PLink(String targetId) {
        return networkService.createLink(
            getLocalAgentId(), 
            targetId, 
            LinkInfo.LinkType.P2P
        );
    }
    
    // 发送消息
    public A2AMessage sendMessage(String targetId, A2AMessage message) {
        return communicationManager.sendMessage(targetId, message, createContext()).join();
    }
    
    // 广播消息
    public Map<String, A2AMessage> broadcast(String sceneId, A2AMessage message) {
        return communicationManager.broadcast(sceneId, message, createContext()).join();
    }
    
    // 调用远程能力
    public Object invokeCapability(String targetId, String capId, Map<String, Object> params) {
        return communicationManager.invokeCapability(targetId, capId, params, createContext()).join();
    }
}
```

### 4.2 推送服务集成

```java
@Service
public class PushService {
    
    @Autowired
    private GossipProtocol gossipProtocol;
    
    @Autowired
    private A2ACommunicationManager communicationManager;
    
    // 推送到单个客户端
    public void pushToClient(String clientId, PushMessage message) {
        A2AMessage a2aMessage = A2AMessage.builder()
            .type(A2AMessageType.TASK_SEND)
            .payload(message)
            .build();
        communicationManager.sendMessage(clientId, a2aMessage, createContext());
    }
    
    // 广播到所有客户端
    public void broadcastToAll(PushMessage message) {
        gossipProtocol.broadcast("push", serialize(message));
    }
    
    // 推送到场景组
    public void pushToSceneGroup(String sceneId, PushMessage message) {
        A2AMessage a2aMessage = A2AMessage.builder()
            .type(A2AMessageType.TASK_SEND)
            .payload(message)
            .build();
        communicationManager.broadcast(sceneId, a2aMessage, createContext());
    }
}
```

---

## 五、任务调整建议

### 5.1 P2P网络模块调整

| 原任务 | 调整后任务 | 工期变化 | 说明 |
|--------|------------|----------|------|
| P1-101 P2P节点发现服务 | 集成SDK PeerDiscovery | 2天→1天 | 直接使用SDK |
| P1-102 P2P通信协议实现 | 集成SDK A2ACommunicationManager | 2天→1天 | 直接使用SDK |
| P1-103 P2P节点管理API | 封装SDK API | 1天→0.5天 | 简单封装 |
| P1-104 P2P网络状态UI | 保持不变 | 2天 | 前端开发 |
| P1-105 P2P能力共享UI | 保持不变 | 2天 | 前端开发 |
| P1-106 P2P集成测试 | 保持不变 | 1天 | 测试验证 |

**P2P模块工期: 5天 → 3.5天**

### 5.2 推送服务模块调整

| 原任务 | 调整后任务 | 工期变化 | 说明 |
|--------|------------|----------|------|
| P1-201 推送服务设计 | 保持不变 | 1天 | 设计文档 |
| P1-202 WebSocket推送实现 | 集成SDK GossipProtocol | 2天→1天 | 直接使用SDK |
| P1-203 推送任务管理API | 封装SDK API | 2天→1天 | 简单封装 |
| P1-204 推送管理UI | 保持不变 | 2天 | 前端开发 |
| P1-205 客户端接收服务 | 集成SDK监听器 | 2天→1天 | 直接使用SDK |
| P1-206 推送集成测试 | 保持不变 | 1天 | 测试验证 |

**推送服务模块工期: 7天 → 5天**

---

## 六、结论

### 6.1 SDK能力评估

| 评估项 | 结果 | 说明 |
|--------|------|------|
| P2P节点发现 | ✅ 完全满足 | UDP多播 + HTTP查询 + 配置文件 |
| P2P消息通信 | ✅ 完全满足 | A2A消息 + Gossip协议 |
| 推送服务 | ✅ 完全满足 | 广播 + 单播 + 场景组播 |
| 链路管理 | ✅ 完全满足 | 多类型链路 + 质量监控 |
| 能力调用 | ✅ 完全满足 | 远程能力调用 |

### 6.2 工期优化

| Phase | 原工期 | 优化后工期 | 节省 |
|-------|--------|------------|------|
| Phase 2 (P2P+推送) | 12天 | 8.5天 | 3.5天 |
| Phase 3 (高级功能) | 27天 | 27天 | 0天 |
| Phase 4 (企业集成) | 5天 | 5天 | 0天 |
| **总计** | **52天** | **48.5天** | **3.5天** |

### 6.3 建议

1. **直接集成SDK**: P2P和推送功能直接使用SDK实现，无需重新开发
2. **封装API层**: 在SDK之上封装业务API，便于前端调用
3. **前端开发**: 重点放在UI开发和用户体验优化
4. **测试验证**: 确保SDK集成正确，功能稳定

---

**分析时间**: 2026-02-28  
**SDK版本**: agent-sdk 2.3  
**分析范围**: P2P网络、推送服务相关API

# 能力地址空间联调测试计划

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **参与团队**: Engine Team, Skills Team, Agent-SDK Team  
> **状态**: 📋 计划中

---

## 一、联调目标

### 1.1 核心目标

| 目标 | 说明 | 验收标准 |
|------|------|----------|
| **驱动注册验证** | 验证驱动正确注册到 CapabilityAddress | 所有驱动可被正确路由 |
| **能力地址路由** | 验证 CapabilityRouter 正确路由请求 | 地址解析准确率 100% |
| **多实例隔离** | 验证上下文隔离机制 | 多实例数据不混淆 |
| **持久化恢复** | 验证快照和恢复机制 | 状态可完整恢复 |

### 1.2 联调范围

```
┌─────────────────────────────────────────────────────────────────┐
│                     联调测试范围                                  │
├─────────────────────────────────────────────────────────────────┤
│  Engine Team          Skills Team         Agent-SDK Team        │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐       │
│  │CapabilityRouter│←→│skill-vfs-* │     │FunctionDef  │       │
│  │CapabilityRegistry│←→│skill-llm-* │     │DriverRegistry│       │
│  │MappingService │←→│skill-org-* │     │AtomicCap    │       │
│  │Snapshot/Restorer│←→│skill-know-*│     │             │       │
│  └─────────────┘     └─────────────┘     └─────────────┘       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 二、联调阶段

### 2.1 Phase 1: 基础连通性测试 (Day 1)

| 测试项 | 负责团队 | 依赖 | 预期结果 |
|--------|:--------:|:----:|----------|
| Engine 编译验证 | Engine | - | mvn compile 成功 |
| Skills 编译验证 | Skills | - | mvn compile 成功 |
| SDK 编译验证 | Agent-SDK | - | mvn compile 成功 |
| 枚举一致性检查 | All | - | 地址定义一致 |

### 2.2 Phase 2: 驱动注册测试 (Day 2)

| 测试项 | 负责团队 | 依赖 | 预期结果 |
|--------|:--------:|:----:|----------|
| VFS 驱动注册 | Skills | Phase 1 | 5 个驱动注册成功 |
| ORG 驱动注册 | Skills | Phase 1 | 5 个驱动注册成功 |
| LLM 驱动注册 | Skills | Phase 1 | 5 个驱动注册成功 |
| 注册状态查询 | Engine | Phase 1 | 返回正确状态 |

### 2.3 Phase 3: 能力路由测试 (Day 3)

| 测试项 | 负责团队 | 依赖 | 预期结果 |
|--------|:--------:|:----:|----------|
| 固定地址路由 | Engine | Phase 2 | 正确路由到驱动 |
| 分类地址查询 | Engine | Phase 2 | 返回分类下所有驱动 |
| 无效地址处理 | Engine | Phase 2 | 返回明确错误 |
| 并发路由测试 | Engine | Phase 2 | 无竞态条件 |

### 2.4 Phase 4: 上下文隔离测试 (Day 4)

| 测试项 | 负责团队 | 依赖 | 预期结果 |
|--------|:--------:|:----:|----------|
| 多租户隔离 | Engine | Phase 3 | 租户数据隔离 |
| 多实例隔离 | Engine | Phase 3 | 实例配置隔离 |
| LLM 上下文隔离 | Engine | Phase 3 | 对话上下文隔离 |
| 安全上下文隔离 | Engine | Phase 3 | 权限数据隔离 |

### 2.5 Phase 5: 持久化恢复测试 (Day 5)

| 测试项 | 负责团队 | 依赖 | 预期结果 |
|--------|:--------:|:----:|----------|
| 实例快照 | Engine | Phase 4 | 快照生成成功 |
| 实例恢复 | Engine | Phase 4 | 状态完整恢复 |
| 配置持久化 | Engine | Phase 4 | 配置可恢复 |
| 灾难恢复 | Engine | Phase 4 | 完整恢复流程 |

---

## 三、测试用例

### 3.1 驱动注册测试用例

#### TC-REG-001: VFS 驱动注册

```java
@Test
@DisplayName("TC-REG-001: VFS 驱动注册测试")
void testVfsDriverRegistration() {
    // Given: VFS 驱动配置
    CapabilityAddress address = CapabilityAddress.VFS_MINIO;
    Object driver = new MinioVfsDriver();
    
    // When: 注册驱动
    driverRegistry.register(address, driver);
    
    // Then: 驱动可被获取
    Object retrieved = capabilityRouter.getDriver(address, VfsDriver.class);
    assertNotNull(retrieved);
    assertEquals(driver, retrieved);
}
```

#### TC-REG-002: ORG 驱动注册

```java
@Test
@DisplayName("TC-REG-002: ORG 驱动注册测试")
void testOrgDriverRegistration() {
    // Given: ORG 驱动配置
    CapabilityAddress address = CapabilityAddress.ORG_DINGDING;
    Object driver = new DingdingOrgDriver();
    
    // When: 注册驱动
    driverRegistry.register(address, driver);
    
    // Then: 驱动可被获取
    Set<CapabilityAddress> orgDrivers = capabilityRouter.getActiveDrivers(CapabilityCategory.ORG);
    assertTrue(orgDrivers.contains(address));
}
```

#### TC-REG-003: LLM 驱动注册

```java
@Test
@DisplayName("TC-REG-003: LLM 驱动注册测试")
void testLlmDriverRegistration() {
    // Given: LLM 驱动配置
    CapabilityAddress address = CapabilityAddress.LLM_OLLAMA;
    Object driver = new OllamaLlmDriver();
    
    // When: 注册驱动
    driverRegistry.register(address, driver);
    
    // Then: 驱动可被获取
    Object retrieved = capabilityRouter.getDriver(address, LlmDriver.class);
    assertNotNull(retrieved);
}
```

### 3.2 能力路由测试用例

#### TC-ROUTE-001: 固定地址路由

```java
@Test
@DisplayName("TC-ROUTE-001: 固定地址路由测试")
void testFixedAddressRouting() {
    // Given: 已注册的驱动
    driverRegistry.register(CapabilityAddress.VFS_MINIO, minioDriver);
    
    // When: 通过固定地址获取驱动
    VfsDriver driver = capabilityRouter.getDriver(
        CapabilityAddress.VFS_MINIO, 
        VfsDriver.class
    );
    
    // Then: 返回正确的驱动
    assertNotNull(driver);
    assertTrue(driver instanceof MinioVfsDriver);
}
```

#### TC-ROUTE-002: 分类地址查询

```java
@Test
@DisplayName("TC-ROUTE-002: 分类地址查询测试")
void testCategoryAddressQuery() {
    // Given: 已注册多个 VFS 驱动
    driverRegistry.register(CapabilityAddress.VFS_LOCAL, localDriver);
    driverRegistry.register(CapabilityAddress.VFS_MINIO, minioDriver);
    driverRegistry.register(CapabilityAddress.VFS_S3, s3Driver);
    
    // When: 查询 VFS 分类下的所有驱动
    Set<CapabilityAddress> vfsDrivers = capabilityRouter.getActiveDrivers(
        CapabilityCategory.VFS
    );
    
    // Then: 返回所有 VFS 驱动
    assertEquals(3, vfsDrivers.size());
    assertTrue(vfsDrivers.contains(CapabilityAddress.VFS_LOCAL));
    assertTrue(vfsDrivers.contains(CapabilityAddress.VFS_MINIO));
    assertTrue(vfsDrivers.contains(CapabilityAddress.VFS_S3));
}
```

#### TC-ROUTE-003: 无效地址处理

```java
@Test
@DisplayName("TC-ROUTE-003: 无效地址处理测试")
void testInvalidAddressHandling() {
    // Given: 未注册的地址
    CapabilityAddress unregisteredAddress = CapabilityAddress.VFS_COS;
    
    // When: 尝试获取未注册的驱动
    VfsDriver driver = capabilityRouter.getDriver(
        unregisteredAddress, 
        VfsDriver.class
    );
    
    // Then: 返回 null 或抛出明确异常
    assertNull(driver);
}
```

### 3.3 上下文隔离测试用例

#### TC-CTX-001: 多租户隔离

```java
@Test
@DisplayName("TC-CTX-001: 多租户隔离测试")
void testMultiTenantIsolation() {
    // Given: 两个租户
    String tenant1 = "tenant-001";
    String tenant2 = "tenant-002";
    
    // When: 各租户配置不同的 VFS 驱动
    contextRegistry.registerContext(tenant1, 
        new CapabilityContext(CapabilityAddress.VFS_MINIO, minioConfig1));
    contextRegistry.registerContext(tenant2, 
        new CapabilityContext(CapabilityAddress.VFS_MINIO, minioConfig2));
    
    // Then: 各租户获取各自的配置
    CapabilityContext ctx1 = contextRegistry.getContext(tenant1, CapabilityAddress.VFS_MINIO);
    CapabilityContext ctx2 = contextRegistry.getContext(tenant2, CapabilityAddress.VFS_MINIO);
    
    assertNotEquals(ctx1.getConfig(), ctx2.getConfig());
}
```

#### TC-CTX-002: LLM 上下文隔离

```java
@Test
@DisplayName("TC-CTX-002: LLM 上下文隔离测试")
void testLlmContextIsolation() {
    // Given: 两个对话实例
    String conversation1 = "conv-001";
    String conversation2 = "conv-002";
    
    // When: 各实例有不同的对话历史
    llmContextRegistry.registerContext(conversation1, llmContext1);
    llmContextRegistry.registerContext(conversation2, llmContext2);
    
    // Then: 各实例获取各自的上下文
    LlmContext ctx1 = llmContextRegistry.getContext(conversation1);
    LlmContext ctx2 = llmContextRegistry.getContext(conversation2);
    
    assertNotEquals(ctx1.getHistory(), ctx2.getHistory());
}
```

### 3.4 持久化恢复测试用例

#### TC-PERSIST-001: 实例快照

```java
@Test
@DisplayName("TC-PERSIST-001: 实例快照测试")
void testInstanceSnapshot() {
    // Given: 已注册的驱动实例
    driverRegistry.register(CapabilityAddress.VFS_MINIO, minioDriver);
    contextRegistry.registerContext("tenant-001", vfsContext);
    
    // When: 创建快照
    CapabilityInstanceSnapshot snapshot = snapshotService.createSnapshot("tenant-001");
    
    // Then: 快照包含完整信息
    assertNotNull(snapshot);
    assertEquals("tenant-001", snapshot.getTenantId());
    assertNotNull(snapshot.getContexts());
    assertNotNull(snapshot.getTimestamp());
}
```

#### TC-PERSIST-002: 实例恢复

```java
@Test
@DisplayName("TC-PERSIST-002: 实例恢复测试")
void testInstanceRestore() {
    // Given: 已保存的快照
    CapabilityInstanceSnapshot snapshot = loadSnapshot("snapshot-001.json");
    
    // When: 恢复实例
    restorerService.restore(snapshot);
    
    // Then: 状态完整恢复
    CapabilityContext restored = contextRegistry.getContext(
        snapshot.getTenantId(), 
        CapabilityAddress.VFS_MINIO
    );
    assertEquals(snapshot.getContexts().get(0), restored);
}
```

---

## 四、测试环境

### 4.1 环境配置

| 环境 | 用途 | 配置 |
|------|------|------|
| **DEV** | 开发联调 | 本地开发环境 |
| **TEST** | 集成测试 | 独立测试环境 |
| **STAGING** | 预发布验证 | 类生产环境 |

### 4.2 依赖服务

| 服务 | 版本 | 用途 |
|------|------|------|
| MySQL | 8.0+ | 元数据存储 |
| Redis | 6.0+ | 缓存/会话 |
| MinIO | latest | 对象存储测试 |
| Ollama | latest | LLM 本地测试 |

### 4.3 测试数据

```yaml
# 测试租户配置
test_tenants:
  - id: tenant-001
    name: 测试租户A
    capabilities:
      - VFS_MINIO
      - LLM_OLLAMA
  - id: tenant-002
    name: 测试租户B
    capabilities:
      - VFS_S3
      - LLM_OPENAI

# 测试驱动配置
test_drivers:
  vfs:
    minio:
      endpoint: http://localhost:9000
      accessKey: minioadmin
      secretKey: minioadmin
    s3:
      region: us-east-1
      bucket: test-bucket
  llm:
    ollama:
      baseUrl: http://localhost:11434
      model: llama3
```

---

## 五、联调时间表

### 5.1 时间安排

| 日期 | 阶段 | 上午 | 下午 |
|------|------|------|------|
| **Day 1** | Phase 1 | 环境准备、编译验证 | 枚举一致性检查 |
| **Day 2** | Phase 2 | VFS/ORG 驱动注册 | LLM 驱动注册 |
| **Day 3** | Phase 3 | 能力路由测试 | 并发路由测试 |
| **Day 4** | Phase 4 | 上下文隔离测试 | 隔离边界测试 |
| **Day 5** | Phase 5 | 持久化恢复测试 | 回归测试 |

### 5.2 里程碑

| 里程碑 | 日期 | 交付物 |
|--------|------|--------|
| M1: 环境就绪 | Day 1 EOD | 编译通过、环境可用 |
| M2: 注册完成 | Day 2 EOD | 所有驱动注册成功 |
| M3: 路由验证 | Day 3 EOD | 路由测试通过 |
| M4: 隔离验证 | Day 4 EOD | 隔离测试通过 |
| M5: 联调完成 | Day 5 EOD | 所有测试通过 |

---

## 六、验收标准

### 6.1 功能验收

| 验收项 | 标准 | 验证方法 |
|--------|------|----------|
| 驱动注册 | 所有驱动可注册 | 单元测试 |
| 能力路由 | 地址解析准确 | 集成测试 |
| 上下文隔离 | 数据不混淆 | 隔离测试 |
| 持久化恢复 | 状态可恢复 | 恢复测试 |

### 6.2 性能验收

| 指标 | 目标 | 测试方法 |
|------|------|----------|
| 驱动注册延迟 | < 100ms | 性能测试 |
| 路由查询延迟 | < 10ms | 性能测试 |
| 快照生成延迟 | < 1s | 性能测试 |
| 恢复延迟 | < 5s | 性能测试 |

### 6.3 稳定性验收

| 指标 | 目标 | 测试方法 |
|------|------|----------|
| 并发支持 | 100 并发 | 压力测试 |
| 错误率 | < 0.1% | 稳定性测试 |
| 内存泄漏 | 无 | 长时间运行测试 |

---

## 七、风险与应对

### 7.1 风险识别

| 风险 | 概率 | 影响 | 应对措施 |
|------|:----:|:----:|----------|
| 环境配置问题 | 中 | 高 | 提前准备环境检查脚本 |
| 接口不兼容 | 低 | 高 | 编译验证阶段发现 |
| 性能不达标 | 中 | 中 | 优化关键路径 |
| 隔离问题 | 中 | 高 | 加强隔离测试 |

### 7.2 回滚计划

| 场景 | 回滚方案 |
|------|----------|
| 联调失败 | 回退到上一稳定版本 |
| 性能问题 | 临时禁用新功能 |
| 数据问题 | 从备份恢复 |

---

## 八、联系方式

### 8.1 团队联系人

| 团队 | 联系人 | 职责 |
|------|--------|------|
| Engine Team | - | 能力框架、路由实现 |
| Skills Team | - | 驱动实现、注册 |
| Agent-SDK Team | - | SDK 适配 |

### 8.2 沟通渠道

- 日常沟通: 团队群
- 问题追踪: GitHub Issues
- 文档协作: 共享文档

---

**文档状态**: 📋 计划中  
**创建日期**: 2026-03-11  
**维护团队**: Engine Team

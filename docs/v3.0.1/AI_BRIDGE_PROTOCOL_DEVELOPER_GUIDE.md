# AI Bridge Protocol 开发者指南

**文档版本**: v1.0  
**创建日期**: 2026-04-04  
**适用对象**: 后端开发者、架构师

---

## 一、概述

本指南面向需要开发或扩展 AI Bridge Protocol 功能的开发者，提供了详细的开发指南和最佳实践。

### 1.1 架构概览

AI Bridge Protocol 采用策略模式设计，主要组件包括：

```
┌─────────────────────────────────────────────────────────┐
│                    REST API Layer                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │  AiBridgeProtocolController                       │  │
│  │  AgentProtocolController                          │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                   Service Layer                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │  AiBridgeProtocolService                          │  │
│  │  AgentProtocolService                             │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                  Dispatcher Layer                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │  AiBridgeProtocolDispatcher                       │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                    Router Layer                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │  AiBridgeProtocolRouter                           │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                 Handler Registry                         │
│  ┌──────────────────────────────────────────────────┐  │
│  │  CommandHandlerRegistry                           │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                 Command Handlers                         │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐        │
│  │ Skill      │ │ Agent      │ │ Scene      │        │
│  │ Handlers   │ │ Handlers   │ │ Handlers   │        │
│  └────────────┘ └────────────┘ └────────────┘        │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐        │
│  │ Cap        │ │ Group      │ │ Resource   │        │
│  │ Handlers   │ │ Handlers   │ │ Handlers   │        │
│  └────────────┘ └────────────┘ └────────────┘        │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                  Service Integration                     │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐        │
│  │ Skill      │ │ Agent      │ │ Scene      │        │
│  │ Manager    │ │ Service    │ │ Service    │        │
│  └────────────┘ └────────────┘ └────────────┘        │
└─────────────────────────────────────────────────────────┘
```

---

## 二、开发新命令处理器

### 2.1 基本步骤

开发新的命令处理器需要以下步骤：

1. **创建命令处理器类**
2. **实现 CommandHandler 接口**
3. **注册到 Spring 容器**
4. **编写单元测试**

### 2.2 示例：创建自定义命令处理器

#### 步骤1: 创建命令处理器类

**文件路径**: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/handler/custom/CustomCommandHandler.java`

```java
package net.ooder.skill.protocol.handler.custom;

import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomCommandHandler extends AbstractCommandHandler {
    
    @Override
    public String getCommand() {
        return "custom.action";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String param1 = getParamAsString(message, "param1");
        Integer param2 = getParamAsInteger(message, "param2");
        
        // 参数验证
        if (param1 == null || param1.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: param1");
        }
        
        // 业务逻辑处理
        Map<String, Object> result = new HashMap<>();
        result.put("param1", param1);
        result.put("param2", param2);
        result.put("processed", true);
        result.put("timestamp", System.currentTimeMillis());
        
        // 返回成功响应
        return buildSuccessResponse(message, result);
    }
}
```

#### 步骤2: 自动注册

由于使用了 `@Component` 注解，Spring 会自动扫描并注册该命令处理器到 `CommandHandlerRegistry`。

#### 步骤3: 编写单元测试

**文件路径**: `skills/_system/skill-protocol/src/test/java/net/ooder/skill/protocol/handler/custom/CustomCommandHandlerTest.java`

```java
package net.ooder.skill.protocol.handler.custom;

import net.ooder.skill.protocol.builder.AiBridgeMessageBuilder;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomCommandHandlerTest {
    
    @Autowired
    private CustomCommandHandler handler;
    
    @Test
    public void testHandleSuccess() {
        AiBridgeMessage message = AiBridgeMessageBuilder.create()
            .id("test-001")
            .command("custom.action")
            .param("param1", "value1")
            .param("param2", 123)
            .build();
        
        AiBridgeMessage response = handler.handle(message);
        
        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertNotNull(response.getResult());
    }
    
    @Test
    public void testHandleMissingParam() {
        AiBridgeMessage message = AiBridgeMessageBuilder.create()
            .id("test-002")
            .command("custom.action")
            .build();
        
        AiBridgeMessage response = handler.handle(message);
        
        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertNotNull(response.getError());
        assertEquals(ErrorCodes.INVALID_PARAMS, response.getError().getCode());
    }
}
```

---

## 三、集成现有服务

### 3.1 服务注入

通过 Spring 的 `@Autowired` 注解注入现有服务：

```java
@Component
public class MyCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private SkillManager skillManager;
    
    @Autowired
    private AgentService agentService;
    
    @Autowired
    private SceneService sceneService;
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        // 使用注入的服务
        List<SkillDefinition> skills = skillManager.getAllSkills();
        
        // 业务逻辑...
        
        return buildSuccessResponse(message, result);
    }
}
```

### 3.2 可用服务列表

| 服务 | 功能 | 位置 |
|-----|------|------|
| SkillManager | 技能管理 | skill-management |
| AgentService | 智能体管理 | skill-agent |
| SceneService | 场景管理 | skill-scenes |
| CapabilityService | Capability管理 | skill-capability |
| GroupService | Group管理 | skill-group |
| ResourceManager | 资源管理 | skill-common |
| VfsManager | VFS管理 | skill-vfs-base |

---

## 四、错误处理

### 4.1 错误码定义

**文件位置**: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/handler/ErrorCodes.java`

```java
public class ErrorCodes {
    // 通用错误码
    public static final int SUCCESS = 0;
    public static final int INTERNAL_ERROR = 500;
    public static final int INVALID_COMMAND = 400;
    public static final int NOT_FOUND = 404;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int BAD_REQUEST = 400;
    public static final int TIMEOUT = 408;
    public static final int CONFLICT = 409;
    public static final int INVALID_PARAMS = 1001;
    
    // 技能相关错误码
    public static final int SKILL_NOT_FOUND = 2001;
    public static final int SKILL_INVOKE_ERROR = 2002;
    
    // 场景相关错误码
    public static final int SCENE_NOT_FOUND = 3001;
    public static final int SCENE_JOIN_ERROR = 3002;
    
    // 智能体相关错误码
    public static final int AGENT_NOT_FOUND = 4001;
    public static final int AGENT_REGISTER_ERROR = 4002;
    
    // Capability相关错误码
    public static final int CAP_NOT_FOUND = 5001;
    public static final int CAP_DECLARE_ERROR = 5002;
    
    // 资源相关错误码
    public static final int RESOURCE_NOT_FOUND = 6001;
    public static final int RESOURCE_ACCESS_ERROR = 6002;
}
```

### 4.2 错误处理最佳实践

```java
@Override
protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
    try {
        // 1. 参数验证
        String requiredParam = getParamAsString(message, "required_param");
        if (requiredParam == null || requiredParam.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: required_param");
        }
        
        // 2. 业务逻辑处理
        Object result = processBusinessLogic(requiredParam);
        
        // 3. 返回成功响应
        return buildSuccessResponse(message, result);
        
    } catch (BusinessException e) {
        // 业务异常
        logger.error("Business error: {}", e.getMessage());
        return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, 
            "Business error: " + e.getMessage());
            
    } catch (Exception e) {
        // 系统异常
        logger.error("System error", e);
        return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, 
            "System error: " + e.getMessage());
    }
}
```

---

## 五、消息构建

### 5.1 使用 AiBridgeMessageBuilder

```java
// 创建请求消息
AiBridgeMessage request = AiBridgeMessageBuilder.create()
    .id(UUID.randomUUID().toString())
    .command("skill.invoke")
    .param("skill_id", "skill-001")
    .param("parameters", Map.of("interval", 5000))
    .source("client-001")
    .target("server-001")
    .build();

// 创建成功响应
AiBridgeMessage successResponse = AiBridgeMessageBuilder.successResponse(
    request, 
    Map.of("result", "success")
);

// 创建错误响应
AiBridgeMessage errorResponse = AiBridgeMessageBuilder.errorResponse(
    request, 
    ErrorCodes.SKILL_NOT_FOUND, 
    "Skill not found"
);
```

### 5.2 消息字段说明

| 字段 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| id | String | 是 | 消息唯一标识 |
| timestamp | Long | 否 | 消息时间戳 |
| command | String | 是 | 命令类型 |
| params | Map | 否 | 命令参数 |
| source | String | 否 | 消息来源 |
| target | String | 否 | 消息目标 |
| status | String | 否 | 消息状态 |
| result | Object | 否 | 返回结果 |
| error | ErrorInfo | 否 | 错误信息 |
| responseTo | String | 否 | 响应的消息ID |

---

## 六、测试指南

### 6.1 单元测试

**测试原则**:
- 每个命令处理器都应该有对应的单元测试
- 测试覆盖率应该 > 80%
- 测试应该覆盖正常流程和异常流程

**测试模板**:

```java
@SpringBootTest
public class MyCommandHandlerTest {
    
    @Autowired
    private MyCommandHandler handler;
    
    @Test
    public void testHandleSuccess() {
        // 准备测试数据
        AiBridgeMessage message = AiBridgeMessageBuilder.create()
            .id("test-001")
            .command("my.command")
            .param("param1", "value1")
            .build();
        
        // 执行测试
        AiBridgeMessage response = handler.handle(message);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertNotNull(response.getResult());
    }
    
    @Test
    public void testHandleError() {
        // 准备测试数据（缺少必需参数）
        AiBridgeMessage message = AiBridgeMessageBuilder.create()
            .id("test-002")
            .command("my.command")
            .build();
        
        // 执行测试
        AiBridgeMessage response = handler.handle(message);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertNotNull(response.getError());
    }
}
```

### 6.2 集成测试

**测试原则**:
- 测试完整的请求-响应流程
- 测试服务集成
- 测试数据库交互

**测试模板**:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AiBridgeProtocolIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testSkillDiscoverEndpoint() throws Exception {
        String requestJson = """
            {
              "id": "test-001",
              "command": "skill.discover",
              "params": {
                "category": "system"
              }
            }
            """;
        
        mockMvc.perform(post("/api/v1/protocol/aibridge/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.result.skills").isArray());
    }
}
```

---

## 七、性能优化

### 7.1 异步处理

对于耗时操作，建议使用异步处理：

```java
@Override
protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
    // 使用异步处理
    CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
        // 耗时操作
        return processLongRunningTask();
    });
    
    try {
        // 等待结果（设置超时）
        Object result = future.get(30, TimeUnit.SECONDS);
        return buildSuccessResponse(message, result);
    } catch (TimeoutException e) {
        return buildErrorResponse(message, ErrorCodes.TIMEOUT, 
            "Operation timeout");
    }
}
```

### 7.2 缓存

对于频繁访问的数据，建议使用缓存：

```java
@Component
public class CachedCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private CacheManager cacheManager;
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String cacheKey = "skill:" + skillId;
        
        // 尝试从缓存获取
        SkillDefinition skill = cacheManager.get(cacheKey, SkillDefinition.class);
        
        if (skill == null) {
            // 缓存未命中，从数据库加载
            skill = skillManager.getSkill(skillId);
            
            // 存入缓存
            cacheManager.put(cacheKey, skill, 300); // 5分钟过期
        }
        
        return buildSuccessResponse(message, skill);
    }
}
```

---

## 八、最佳实践

### 8.1 命名规范

- **命令名称**: 使用点号分隔的小写字母，如 `skill.discover`
- **类名**: 使用 PascalCase，以 `CommandHandler` 结尾，如 `SkillDiscoverCommandHandler`
- **包名**: 按功能分组，如 `handler.skill`、`handler.agent`

### 8.2 日志规范

```java
@Override
protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
    // 记录开始时间
    long startTime = System.currentTimeMillis();
    
    try {
        logger.debug("Handling command: {} with message id: {}", 
            getCommand(), message.getId());
        
        // 业务逻辑
        AiBridgeMessage response = doHandleInternal(message);
        
        // 记录成功日志
        logger.info("Command {} handled successfully in {} ms", 
            getCommand(), System.currentTimeMillis() - startTime);
        
        return response;
        
    } catch (Exception e) {
        // 记录错误日志
        logger.error("Command {} failed in {} ms: {}", 
            getCommand(), System.currentTimeMillis() - startTime, e.getMessage(), e);
        
        throw e;
    }
}
```

### 8.3 参数验证

```java
@Override
protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
    // 验证必需参数
    String requiredParam = getRequiredParam(message, "required_param");
    Integer optionalParam = getOptionalParam(message, "optional_param", 0);
    
    // 验证参数格式
    if (!isValidFormat(requiredParam)) {
        return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
            "Invalid parameter format: required_param");
    }
    
    // 验证参数范围
    if (optionalParam < 0 || optionalParam > 100) {
        return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
            "Parameter out of range: optional_param");
    }
    
    // 业务逻辑...
}

private String getRequiredParam(AiBridgeMessage message, String key) {
    String value = getParamAsString(message, key);
    if (value == null || value.isEmpty()) {
        throw new IllegalArgumentException("Missing required parameter: " + key);
    }
    return value;
}

private Integer getOptionalParam(AiBridgeMessage message, String key, Integer defaultValue) {
    Integer value = getParamAsInteger(message, key);
    return value != null ? value : defaultValue;
}
```

---

## 九、常见问题

### 9.1 如何处理并发请求？

**问题**: 多个请求同时访问同一个资源。

**解决方案**: 使用同步机制或分布式锁。

```java
@Autowired
private DistributedLockService lockService;

@Override
protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
    String lockKey = "lock:" + resourceId;
    
    try {
        // 获取锁
        if (lockService.tryLock(lockKey, 10, TimeUnit.SECONDS)) {
            try {
                // 处理业务逻辑
                return processBusinessLogic(message);
            } finally {
                // 释放锁
                lockService.unlock(lockKey);
            }
        } else {
            return buildErrorResponse(message, ErrorCodes.CONFLICT, 
                "Resource is locked");
        }
    } catch (Exception e) {
        logger.error("Failed to acquire lock", e);
        return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, 
            "Failed to acquire lock");
    }
}
```

### 9.2 如何处理大文件上传？

**问题**: 上传大文件导致内存溢出。

**解决方案**: 使用流式处理。

```java
@Override
protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
    String fileId = getParamAsString(message, "file_id");
    
    // 使用流式处理
    try (InputStream inputStream = vfsManager.downloadFile(fileId)) {
        // 流式处理文件
        processStream(inputStream);
        
        return buildSuccessResponse(message, Map.of("status", "processed"));
    }
}
```

### 9.3 如何实现事务？

**问题**: 需要保证多个操作的原子性。

**解决方案**: 使用 Spring 事务管理。

```java
@Component
public class TransactionalCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private TransactionalService transactionalService;
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        // 调用事务方法
        Object result = transactionalService.processInTransaction(message);
        
        return buildSuccessResponse(message, result);
    }
}

@Service
public class TransactionalService {
    
    @Transactional
    public Object processInTransaction(AiBridgeMessage message) {
        // 多个数据库操作
        // 如果任何一个操作失败，整个事务会回滚
        return result;
    }
}
```

---

## 十、相关文档

- **API使用文档**: `E:\github\ooder-skills\docs\v3.0.1\AI_BRIDGE_PROTOCOL_API_GUIDE.md`
- **移植报告**: `E:\github\ooder-skills\docs\v3.0.1\AI_BRIDGE_PROTOCOL_MIGRATION_FINAL_REPORT.md`
- **扩展命令完成报告**: `E:\github\ooder-skills\docs\v3.0.1\AI_BRIDGE_PROTOCOL_EXTENSION_COMPLETION_REPORT.md`

---

**文档维护**: 本文档应在功能变更时及时更新。

**变更记录**:
- 2026-04-04 v1.0: 初始版本创建，完整的开发者指南

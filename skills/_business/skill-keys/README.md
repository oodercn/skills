# skill-keys

API密钥管理服务 - 提供API密钥的生成、验证和管理功能。

## 功能特性

- **密钥生成** - 生成安全的API密钥
- **密钥验证** - 验证API密钥的有效性
- **权限控制** - 基于密钥的权限控制
- **使用统计** - 统计密钥使用情况

## 核心接口

### KeyManagementController

密钥管理控制器。

```java
@RestController
@RequestMapping("/api/v1/keys")
public class KeyManagementController {
    /**
     * 创建密钥
     */
    @PostMapping
    public KeyDTO createKey(@RequestBody CreateKeyRequest request);
    
    /**
     * 验证密钥
     */
    @PostMapping("/validate")
    public KeyValidateResultDTO validateKey(@RequestBody String apiKey);
    
    /**
     * 获取密钥列表
     */
    @GetMapping
    public List<KeyDTO> listKeys();
    
    /**
     * 删除密钥
     */
    @DeleteMapping("/{keyId}")
    public void deleteKey(@PathVariable String keyId);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/keys | POST | 创建密钥 |
| /api/v1/keys | GET | 获取密钥列表 |
| /api/v1/keys/{keyId} | DELETE | 删除密钥 |
| /api/v1/keys/validate | POST | 验证密钥 |

## 密钥模型

### KeyDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 密钥ID |
| name | String | 密钥名称 |
| apiKey | String | API密钥 |
| permissions | List<String> | 权限列表 |
| expiresAt | Long | 过期时间 |
| createdAt | Long | 创建时间 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-keys</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private KeyManagementService keyService;

// 创建密钥
CreateKeyRequest request = new CreateKeyRequest();
request.setName("my-api-key");
request.setPermissions(Arrays.asList("read", "write"));
KeyDTO key = keyService.createKey(request);

// 验证密钥
KeyValidateResultDTO result = keyService.validateKey(key.getApiKey());
if (result.isValid()) {
    // 密钥有效，允许访问
}
```

## 许可证

Apache-2.0

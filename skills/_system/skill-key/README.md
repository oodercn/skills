# skill-key

密钥管理服务 - 提供系统密钥的生成、存储和管理功能。

## 功能特性

- **密钥生成** - 生成安全的系统密钥
- **密钥存储** - 安全存储密钥
- **密钥轮换** - 支持密钥定期轮换
- **密钥访问** - 控制密钥访问权限

## 核心接口

### KeyController

密钥管理控制器。

```java
@RestController
@RequestMapping("/api/v1/keys")
public class KeyController {
    /**
     * 生成密钥
     */
    @PostMapping("/generate")
    public KeyDTO generateKey(@RequestBody GenerateKeyRequest request);
    
    /**
     * 获取密钥
     */
    @GetMapping("/{keyId}")
    public KeyDTO getKey(@PathVariable String keyId);
    
    /**
     * 轮换密钥
     */
    @PostMapping("/{keyId}/rotate")
    public KeyDTO rotateKey(@PathVariable String keyId);
    
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
| /api/v1/keys/generate | POST | 生成密钥 |
| /api/v1/keys/{keyId} | GET | 获取密钥 |
| /api/v1/keys/{keyId}/rotate | POST | 轮换密钥 |
| /api/v1/keys/{keyId} | DELETE | 删除密钥 |

## 密钥模型

### KeyDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 密钥ID |
| name | String | 密钥名称 |
| type | KeyType | 密钥类型 |
| algorithm | String | 加密算法 |
| createdAt | Long | 创建时间 |
| expiresAt | Long | 过期时间 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-key</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private KeyService keyService;

// 生成密钥
GenerateKeyRequest request = new GenerateKeyRequest();
request.setName("encryption-key");
request.setType(KeyType.ENCRYPTION);
request.setAlgorithm("AES-256");
KeyDTO key = keyService.generateKey(request);

// 轮换密钥
KeyDTO rotatedKey = keyService.rotateKey(key.getId());

// 删除密钥
keyService.deleteKey(key.getId());
```

## 许可证

Apache-2.0

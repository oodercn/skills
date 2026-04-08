# skill-security

安全策略管理服务 - 提供系统安全策略的定义、执行和审计功能。

## 功能特性

- **策略定义** - 定义安全策略和规则
- **访问控制** - 基于角色的访问控制(RBAC)
- **安全审计** - 记录和审计安全事件
- **威胁检测** - 检测和阻止安全威胁

## 核心接口

### SecurityController

安全管理控制器。

```java
@RestController
@RequestMapping("/api/v1/security")
public class SecurityController {
    /**
     * 创建安全策略
     */
    @PostMapping("/policies")
    public SecurityPolicyDTO createPolicy(@RequestBody CreatePolicyRequest request);
    
    /**
     * 检查权限
     */
    @PostMapping("/check")
    public PermissionCheckResult checkPermission(@RequestBody PermissionCheckRequest request);
    
    /**
     * 获取审计日志
     */
    @GetMapping("/audit-logs")
    public List<AuditLogDTO> getAuditLogs(@RequestParam(required = false) String userId);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/security/policies | POST | 创建安全策略 |
| /api/v1/security/policies | GET | 获取策略列表 |
| /api/v1/security/check | POST | 检查权限 |
| /api/v1/security/audit-logs | GET | 获取审计日志 |

## 安全策略模型

### SecurityPolicyDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 策略ID |
| name | String | 策略名称 |
| type | PolicyType | 策略类型 |
| rules | List<Rule> | 策略规则 |
| enabled | boolean | 是否启用 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-security</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private SecurityService securityService;

// 创建安全策略
CreatePolicyRequest request = new CreatePolicyRequest();
request.setName("数据访问策略");
request.setType(PolicyType.ACCESS_CONTROL);
SecurityPolicyDTO policy = securityService.createPolicy(request);

// 检查权限
PermissionCheckRequest checkRequest = new PermissionCheckRequest();
checkRequest.setUserId("user123");
checkRequest.setResource("/api/v1/sensitive-data");
checkRequest.setAction("read");
PermissionCheckResult result = securityService.checkPermission(checkRequest);
```

## 许可证

Apache-2.0

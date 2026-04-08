# skill-org-web

Web组织架构服务 - 提供基于Web的组织架构管理功能。

## 功能特性

- **组织管理** - 管理组织架构结构
- **用户管理** - 管理组织内的用户
- **部门管理** - 管理组织部门结构
- **权限管理** - 管理组织权限

## 核心接口

### OrgWebController

组织架构控制器。

```java
@RestController
@RequestMapping("/api/v1/org/web")
public class OrgWebController {
    /**
     * 获取组织架构
     */
    @GetMapping("/structure")
    public OrgStructure getOrgStructure();
    
    /**
     * 获取部门列表
     */
    @GetMapping("/departments")
    public List<DepartmentDTO> listDepartments();
    
    /**
     * 获取部门成员
     */
    @GetMapping("/departments/{deptId}/members")
    public List<MemberDTO> getDepartmentMembers(@PathVariable String deptId);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/org/web/structure | GET | 获取组织架构 |
| /api/v1/org/web/departments | GET | 获取部门列表 |
| /api/v1/org/web/departments/{deptId} | GET | 获取部门详情 |
| /api/v1/org/web/departments/{deptId}/members | GET | 获取部门成员 |

## 组织架构模型

### OrgStructure

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 组织ID |
| name | String | 组织名称 |
| departments | List<Department> | 部门列表 |

### DepartmentDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 部门ID |
| name | String | 部门名称 |
| parentId | String | 父部门ID |
| memberCount | int | 成员数量 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-org-web</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private OrgWebService orgWebService;

// 获取组织架构
OrgStructure structure = orgWebService.getOrgStructure();

// 获取部门列表
List<DepartmentDTO> departments = orgWebService.listDepartments();

// 获取部门成员
List<MemberDTO> members = orgWebService.getDepartmentMembers("dept-001");
```

## 许可证

Apache-2.0

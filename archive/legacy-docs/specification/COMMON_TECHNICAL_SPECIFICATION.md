# Ooder 公共技术规范 v1.0

> **文档版本**: v1.0  
> **发布日期**: 2026-03-01  
> **适用范围**: Ooder 全模块开发  
> **文档状态**: 正式发布

---

## 一、概述

### 1.1 文档目的

本文档定义 Ooder 系统的公共技术规范，包括：
- 通用数据模型与接口规范
- 枚举与字典表规范
- API 响应格式规范
- 前后端交互规范

### 1.2 适用范围

本规范适用于以下模块：
- skill-scene（场景管理模块）
- agent-sdk（Agent 开发 SDK）
- ooder-nexus（前端管理界面）
- 其他需要与 Ooder 系统集成的模块

---

## 二、API 响应格式规范

### 2.1 统一响应结构

所有 API 接口应返回统一的响应格式：

```json
{
    "code": 200,
    "status": "success",
    "message": "操作成功",
    "data": {},
    "timestamp": "2026-03-01 10:30:00.000",
    "requestId": "REQ_1234567890_1234"
}
```

### 2.2 响应码定义

| 响应码 | 常量名 | 说明 |
|--------|--------|------|
| 200 | CODE_SUCCESS | 操作成功 |
| 400 | CODE_BAD_REQUEST | 请求参数错误 |
| 401 | CODE_UNAUTHORIZED | 未授权访问 |
| 403 | CODE_FORBIDDEN | 禁止访问 |
| 404 | CODE_NOT_FOUND | 资源不存在 |
| 405 | CODE_METHOD_NOT_ALLOWED | 方法不允许 |
| 409 | CODE_CONFLICT | 资源冲突 |
| 500 | CODE_INTERNAL_SERVER_ERROR | 服务器内部错误 |
| 503 | CODE_SERVICE_UNAVAILABLE | 服务不可用 |

### 2.3 Java 实现

```java
public class ResultModel<T> {
    private int code;
    private String status;
    private String message;
    private T data;
    private String timestamp;
    private String requestId;

    public static <T> ResultModel<T> success(T data) {
        return new ResultModel<>(CODE_SUCCESS, "操作成功", data, true, generateRequestId());
    }

    public static <T> ResultModel<T> error(int code, String message) {
        return new ResultModel<>(code, message, null, false, generateRequestId());
    }
}
```

---

## 三、分页查询规范

### 3.1 分页请求参数

| 参数名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pageNum | int | 1 | 页码，从1开始 |
| pageSize | int | 20 | 每页条数 |
| sortBy | String | - | 排序字段 |
| sortOrder | String | asc | 排序方向：asc/desc |

### 3.2 分页响应结构

```json
{
    "code": 200,
    "data": {
        "list": [],
        "total": 100,
        "pageNum": 1,
        "pageSize": 20,
        "pages": 5
    }
}
```

### 3.3 Java 实现

```java
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;
}
```

---

## 四、枚举规范

### 4.1 枚举基本规范

所有枚举类型应：
1. 实现 `DictItem` 接口，支持字典化
2. 使用 `@Dict` 注解标识字典元信息
3. 包含 `code`、`name`、`description`、`icon`、`sort` 属性

### 4.2 字典接口定义

```java
public interface DictItem {
    String getCode();
    String getName();
    String getDescription();
    String getIcon();
    int getSort();
}
```

### 4.3 字典注解定义

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {
    String code();
    String name() default "";
    String description() default "";
    boolean cacheable() default true;
}
```

### 4.4 枚举示例

```java
@Dict(code = "scene_group_status", name = "场景组状态", description = "场景组的运行状态")
public enum SceneGroupStatus implements DictItem {
    
    ACTIVE("ACTIVE", "运行中", "场景组正常运行", "ri-play-circle-line", 1),
    SUSPENDED("SUSPENDED", "已暂停", "场景组已暂停", "ri-pause-circle-line", 2);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    SceneGroupStatus(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}
```

---

## 五、字典 API 规范

### 5.1 字典服务接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/v1/dicts | GET | 获取所有字典列表 |
| /api/v1/dicts/{code} | GET | 获取指定字典 |
| /api/v1/dicts/{code}/items | GET | 获取字典项列表 |
| /api/v1/dicts/{code}/items/{itemCode} | GET | 获取指定字典项 |
| /api/v1/dicts/{code}/items/{itemCode}/name | GET | 获取字典项名称 |
| /api/v1/dicts/refresh | POST | 刷新字典缓存 |

### 5.2 字典数据结构

```json
{
    "code": "scene_group_status",
    "name": "场景组状态",
    "description": "场景组的运行状态",
    "items": [
        {
            "code": "ACTIVE",
            "name": "运行中",
            "description": "场景组正常运行",
            "icon": "ri-play-circle-line",
            "sort": 1
        }
    ]
}
```

---

## 六、前端交互规范

### 6.1 字典缓存工具

前端应使用统一的字典缓存工具 `DictCache`：

```javascript
// 初始化字典缓存
await DictCache.init();

// 获取字典
const dict = await DictCache.getDict(DictCache.DICT_CODES.SCENE_GROUP_STATUS);

// 获取字典项列表
const items = await DictCache.getDictItems(DictCache.DICT_CODES.SCENE_GROUP_STATUS);

// 获取字典项名称
const name = await DictCache.getDictItemName(DictCache.DICT_CODES.SCENE_GROUP_STATUS, 'ACTIVE');
```

### 6.2 字典代码常量

```javascript
var DICT_CODES = {
    CAPABILITY_TYPE: 'capability_type',
    PARTICIPANT_TYPE: 'participant_type',
    PARTICIPANT_ROLE: 'participant_role',
    PARTICIPANT_STATUS: 'participant_status',
    SCENE_GROUP_STATUS: 'scene_group_status',
    SCENE_TYPE: 'scene_type',
    CONNECTOR_TYPE: 'connector_type',
    CAPABILITY_PROVIDER_TYPE: 'capability_provider_type',
    CAPABILITY_BINDING_STATUS: 'capability_binding_status',
    TEMPLATE_STATUS: 'template_status',
    TEMPLATE_CATEGORY: 'template_category'
};
```

---

## 七、命名规范

### 7.1 包命名规范

| 包名 | 用途 |
|------|------|
| `dto` | 数据传输对象 |
| `model` | 领域模型 |
| `controller` | 控制器 |
| `service` | 服务层 |
| `config` | 配置类 |

### 7.2 类命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| DTO | XxxDTO | SceneGroupDTO |
| 枚举 | XxxType/XxxStatus | SceneGroupStatus |
| 控制器 | XxxController | SceneGroupController |
| 服务 | XxxService | SceneGroupService |

### 7.3 API 路径规范

| 操作 | 路径模板 | 示例 |
|------|----------|------|
| 列表查询 | GET /api/v1/{resource} | /api/v1/scene-groups |
| 详情查询 | GET /api/v1/{resource}/{id} | /api/v1/scene-groups/sg-001 |
| 创建 | POST /api/v1/{resource} | /api/v1/scene-groups |
| 更新 | PUT /api/v1/{resource}/{id} | /api/v1/scene-groups/sg-001 |
| 删除 | DELETE /api/v1/{resource}/{id} | /api/v1/scene-groups/sg-001 |

---

## 八、版本兼容规范

### 8.1 Java 版本要求

- **最低版本**: Java 8
- **推荐版本**: Java 11+

### 8.2 API 版本管理

- API 路径包含版本号：`/api/v1/`
- 版本升级时保持向后兼容
- 废弃接口需在文档中标注

---

## 九、安全规范

### 9.1 敏感信息处理

- 禁止在日志中输出敏感信息
- 禁止在响应中返回密码、密钥等敏感数据
- 敏感配置应使用环境变量或加密存储

### 9.2 跨域配置

```java
@CrossOrigin(origins = "*", allowedHeaders = "*")
```

---

## 十、文档规范

### 10.1 文档头部格式

```markdown
# 文档标题 vX.X

> **文档版本**: vX.X  
> **发布日期**: YYYY-MM-DD  
> **适用范围**: 适用模块  
> **文档状态**: 草稿/评审中/正式发布
```

### 10.2 术语引用

文档中使用的术语应引用术语表：

```markdown
本文档使用的核心术语请参考 [术语表](GLOSSARY.md)
```

---

## 附录

### A. 相关文档

- [术语表](GLOSSARY.md)
- [场景需求规格说明书](SCENE_REQUIREMENT_SPEC.md)
- [能力管理需求规格说明书](CAPABILITY_REQUIREMENT_SPEC.md)
- [字典表规范与范例](DICT_SPECIFICATION.md)

# skill-history

历史记录服务 - 提供操作历史记录的存储和查询功能。

## 功能特性

- **历史记录** - 记录用户操作历史
- **历史查询** - 查询历史操作记录
- **历史统计** - 统计历史操作数据
- **历史清理** - 自动清理过期历史

## 核心接口

### HistoryController

历史记录控制器。

```java
@RestController
@RequestMapping("/api/v1/history")
public class HistoryController {
    /**
     * 获取历史记录
     */
    @GetMapping
    public PageResult<HistoryDTO> getHistory(
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String action,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    );
    
    /**
     * 获取用户历史
     */
    @GetMapping("/users/{userId}")
    public List<HistoryDTO> getUserHistory(@PathVariable String userId);
    
    /**
     * 清理历史
     */
    @DeleteMapping("/cleanup")
    public void cleanupHistory(@RequestParam long beforeTime);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/history | GET | 获取历史记录 |
| /api/v1/history/users/{userId} | GET | 获取用户历史 |
| /api/v1/history/cleanup | DELETE | 清理历史 |

## 历史记录模型

### HistoryDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 历史ID |
| userId | String | 用户ID |
| action | String | 操作类型 |
| resource | String | 资源 |
| details | String | 详细信息 |
| timestamp | Long | 时间戳 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-history</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private HistoryService historyService;

// 获取历史记录
PageResult<HistoryDTO> history = historyService.getHistory("user123", "login", 0, 20);

// 获取用户历史
List<HistoryDTO> userHistory = historyService.getUserHistory("user123");

// 清理历史
historyService.cleanupHistory(System.currentTimeMillis() - 30 * 24 * 3600 * 1000L);
```

## 许可证

Apache-2.0

# skill-todo

待办任务管理服务 - 提供待办任务的创建、分配、跟踪和完成功能。

## 功能特性

- **任务创建** - 创建待办任务
- **任务分配** - 分配任务给用户
- **任务跟踪** - 跟踪任务进度和状态
- **任务提醒** - 任务到期提醒

## 核心接口

### TodoController

待办任务控制器。

```java
@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {
    /**
     * 获取我的待办列表
     */
    @GetMapping("/my/todos")
    public List<TodoDTO> listMyTodos();
    
    /**
     * 获取待处理的待办
     */
    @GetMapping("/my/todos/pending")
    public List<TodoDTO> listPendingTodos();
    
    /**
     * 获取待办数量
     */
    @GetMapping("/my/todos/count")
    public Map<String, Integer> countByType();
    
    /**
     * 创建待办
     */
    @PostMapping
    public TodoDTO createTodo(@RequestBody CreateTodoRequest request);
    
    /**
     * 完成待办
     */
    @PutMapping("/{todoId}/complete")
    public TodoDTO completeTodo(@PathVariable String todoId);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/todos | POST | 创建待办 |
| /api/v1/todos | GET | 获取待办列表 |
| /api/v1/my/todos | GET | 获取我的待办列表 |
| /api/v1/my/todos/pending | GET | 获取待处理的待办 |
| /api/v1/my/todos/count | GET | 获取待办数量 |
| /api/v1/todos/{todoId} | PUT | 更新待办 |
| /api/v1/todos/{todoId}/complete | PUT | 完成待办 |

## 待办模型

### TodoDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 待办ID |
| title | String | 待办标题 |
| description | String | 待办描述 |
| assignee | String | 分配人 |
| status | TodoStatus | 待办状态 |
| priority | Priority | 优先级 |
| dueDate | Long | 到期时间 |
| createdAt | Long | 创建时间 |

## 待办状态

- **PENDING** - 待处理
- **IN_PROGRESS** - 进行中
- **COMPLETED** - 已完成
- **CANCELLED** - 已取消

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-todo</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private TodoService todoService;

// 创建待办
CreateTodoRequest request = new CreateTodoRequest();
request.setTitle("完成报告");
request.setDescription("编写季度报告");
request.setAssignee("user123");
request.setPriority(Priority.HIGH);
request.setDueDate(System.currentTimeMillis() + 86400000); // 1天后
TodoDTO todo = todoService.createTodo(request);

// 获取我的待办
List<TodoDTO> myTodos = todoService.listMyTodos();

// 完成待办
todoService.completeTodo(todo.getId());
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| TODO_CACHE_ENABLED | boolean | true | 是否启用待办缓存 |

## 许可证

Apache-2.0

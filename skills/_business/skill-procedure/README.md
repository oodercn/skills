# skill-procedure

企业流程管理服务 - 提供企业业务流程的定义、执行和监控功能。

## 功能特性

- **流程定义** - 定义企业业务流程
- **流程执行** - 执行业务流程实例
- **流程监控** - 监控流程执行状态
- **流程优化** - 分析和优化流程性能

## 核心接口

### ProcedureController

流程管理控制器。

```java
@RestController
@RequestMapping("/api/v1/procedures")
public class ProcedureController {
    /**
     * 创建流程
     */
    @PostMapping
    public ProcedureDTO createProcedure(@RequestBody CreateProcedureRequest request);
    
    /**
     * 执行流程
     */
    @PostMapping("/{procedureId}/execute")
    public ExecutionResult executeProcedure(
        @PathVariable String procedureId,
        @RequestBody Map<String, Object> params
    );
    
    /**
     * 获取流程状态
     */
    @GetMapping("/{procedureId}/status")
    public ProcedureStatus getStatus(@PathVariable String procedureId);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/procedures | POST | 创建流程 |
| /api/v1/procedures/{procedureId} | GET | 获取流程详情 |
| /api/v1/procedures/{procedureId}/execute | POST | 执行流程 |
| /api/v1/procedures/{procedureId}/status | GET | 获取流程状态 |

## 流程模型

### ProcedureDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 流程ID |
| name | String | 流程名称 |
| steps | List<Step> | 流程步骤 |
| status | Status | 流程状态 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-procedure</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private ProcedureService procedureService;

// 创建流程
CreateProcedureRequest request = new CreateProcedureRequest();
request.setName("审批流程");
request.setSteps(steps);
ProcedureDTO procedure = procedureService.createProcedure(request);

// 执行流程
Map<String, Object> params = new HashMap<>();
params.put("applicant", "user123");
ExecutionResult result = procedureService.executeProcedure(procedure.getId(), params);
```

## 许可证

Apache-2.0

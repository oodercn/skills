# skill-business

业务场景编排服务，支持业务流程编排、工作流执行

## 功能特性

- 场景编排 - 编排业务场景流程
- 工作流执行 - 执行业务工作流
- 数据处理 - 业务数据处理
- 结果追踪 - 执行结果追踪

## 快速开始

### 安装

```bash
skill install skill-business
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/business/scenarios \
  -H "Content-Type: application/json" \
  -d '{
    "name": "订单处理流程",
    "steps": ["validate", "process", "notify"]
  }'
```

## 许可证

Apache-2.0

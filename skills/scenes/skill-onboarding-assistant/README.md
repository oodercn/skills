# skill-onboarding-assistant

入职助手场景服务，支持新员工入职引导、培训

## 功能特性

- 入职引导 - 新员工入职流程引导
- 培训推荐 - 智能培训内容推荐
- 问答支持 - 入职问题解答
- 进度追踪 - 入职进度跟踪

## 快速开始

### 安装

```bash
skill install skill-onboarding-assistant
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/onboarding/start \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "EMP001",
    "name": "张三",
    "department": "研发部"
  }'
```

## 许可证

Apache-2.0

# skill-meeting-minutes

会议纪要场景服务，支持会议记录、摘要生成、任务提取

## 功能特性

- 会议记录 - 记录会议内容
- 摘要生成 - 自动生成会议摘要
- 任务提取 - 提取会议任务
- 分发通知 - 纪要分发通知

## 快速开始

### 安装

```bash
skill install skill-meeting-minutes
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/meeting/minutes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "项目周会",
    "transcript": "会议转录内容..."
  }'
```

## 许可证

Apache-2.0

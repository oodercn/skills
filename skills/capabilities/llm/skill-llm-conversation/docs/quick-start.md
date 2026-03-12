# 快速开始

## 环境要求

- Java 8+
- Maven 3.6+
- Spring Boot 2.x

## 安装

### 方式一：通过Skill市场安装

```bash
skill install skill-llm-conversation
```

### 方式二：手动安装

1. 克隆代码
```bash
git clone https://gitee.com/ooderCN/ooder-skills.git
cd skills/capabilities/llm/skill-llm-conversation
```

2. 编译安装
```bash
mvn clean install
```

## 配置

### 基础配置

编辑 `application.yml`：

```yaml
skill-llm-conversation:
  enabled: true
  max-history: 100
  session-timeout: 3600
  enable-streaming: true
  max-tokens: 4096
```

## 验证安装

```bash
mvn spring-boot:run

curl http://localhost:8080/api/llm/sessions
```

## 发送对话消息

```bash
curl -X POST http://localhost:8080/api/llm/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "你好，请介绍一下你自己",
    "sessionId": "my-session"
  }'
```

## 流式对话

```bash
curl -X POST http://localhost:8080/api/llm/chat/stream \
  -H "Content-Type: application/json" \
  -d '{
    "message": "请写一首诗",
    "sessionId": "stream-session"
  }'
```

## Function Calling

```bash
curl -X POST http://localhost:8080/api/llm/chat/function \
  -H "Content-Type: application/json" \
  -d '{
    "message": "今天北京天气怎么样？",
    "sessionId": "func-session",
    "functions": [
      {
        "name": "get_weather",
        "description": "获取指定城市的天气",
        "parameters": {
          "type": "object",
          "properties": {
            "city": {"type": "string"}
          }
        }
      }
    ]
  }'
```

## 下一步

- [API参考](api-reference.md)
- [会话管理](session-management.md)
- [流式输出](streaming.md)

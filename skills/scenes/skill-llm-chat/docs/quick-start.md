# 快速开始

## 环境要求

- Java 8+
- Maven 3.6+
- Spring Boot 2.x

## 安装

### 方式一：通过Skill市场安装

```bash
skill install skill-llm-chat
```

### 方式二：手动安装

1. 克隆代码
```bash
git clone https://gitee.com/ooderCN/ooder-skills.git
cd skills/scenes/skill-llm-chat
```

2. 编译安装
```bash
mvn clean install
```

## 配置

### 基础配置

编辑 `application.yml`：

```yaml
skill-llm-chat:
  enabled: true
  default-provider: baidu
  default-model: ernie-bot-4
  max-tokens: 4096
  temperature: 0.7
  stream-enabled: true
```

### Provider配置

#### 百度文心一言

```yaml
llm:
  providers:
    baidu:
      api-key: ${BAIDU_API_KEY}
      secret-key: ${BAIDU_SECRET_KEY}
```

#### OpenAI

```yaml
llm:
  providers:
    openai:
      api-key: ${OPENAI_API_KEY}
      base-url: https://api.openai.com/v1
```

#### 阿里千问

```yaml
llm:
  providers:
    qianwen:
      api-key: ${QIANWEN_API_KEY}
```

## 验证安装

```bash
mvn spring-boot:run

curl http://localhost:8080/api/llm/providers
```

## 第一个对话

```bash
curl -X POST http://localhost:8080/api/llm/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "你好",
    "sessionId": "my-first-session"
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

## 下一步

- [API参考](api-reference.md)
- [配置指南](configuration.md)

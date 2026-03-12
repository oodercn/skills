# 快速开始

## 环境要求

- Java 8+
- Maven 3.6+
- Spring Boot 2.x

## 安装

### 方式一：通过Skill市场安装

```bash
skill install skill-rag
```

### 方式二：手动安装

1. 克隆代码
```bash
git clone https://gitee.com/ooderCN/ooder-skills.git
cd skills/capabilities/knowledge/skill-rag
```

2. 编译安装
```bash
mvn clean install
```

## 配置

### 基础配置

编辑 `application.yml`：

```yaml
skill-rag:
  default-strategy: HYBRID
  max-results: 10
  embedding-model: text-embedding-3-small
  score-threshold: 0.7
```

## 验证安装

```bash
mvn spring-boot:run

curl http://localhost:8080/api/v1/rag/retrieve \
  -H "Content-Type: application/json" \
  -d '{"query": "test"}'
```

## 执行检索

```bash
curl -X POST http://localhost:8080/api/v1/rag/retrieve \
  -H "Content-Type: application/json" \
  -d '{
    "query": "如何配置系统？",
    "strategy": "HYBRID",
    "maxResults": 5
  }'
```

## 构建Prompt

```bash
curl -X POST http://localhost:8080/api/v1/rag/prompt \
  -H "Content-Type: application/json" \
  -d '{
    "query": "系统配置方法",
    "systemPrompt": "你是一个专业的技术助手",
    "maxContextTokens": 2000
  }'
```

## 注册知识库

```bash
curl -X POST http://localhost:8080/api/v1/rag/kb/{kbId}/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "技术文档库",
    "description": "技术文档和API参考"
  }'
```

## 下一步

- [API参考](api-reference.md)
- [检索策略](retrieval-guide.md)

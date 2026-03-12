# 快速开始

## 环境要求

- Java 8+
- Maven 3.6+
- Spring Boot 2.x
- 磁盘空间: 至少1GB用于知识库存储

## 安装

### 方式一：通过Skill市场安装

```bash
skill install skill-knowledge-qa
```

### 方式二：手动安装

1. 克隆代码
```bash
git clone https://gitee.com/ooderCN/ooder-skills.git
cd skills/scenes/skill-knowledge-qa
```

2. 编译安装
```bash
mvn clean install
```

## 配置

### 基础配置

编辑 `application.yml`：

```yaml
skill-knowledge-qa:
  enabled: true
  storage-path: ./data/kb
  max-document-size: 10485760
  search-top-k: 10
  embedding-model: text-embedding-3-small
```

### 向量检索配置

```yaml
skill-knowledge-qa:
  vector:
    enabled: true
    embedding-model: text-embedding-3-small
    chunk-size: 500
    chunk-overlap: 50
```

## 验证安装

```bash
mvn spring-boot:run

curl http://localhost:8080/api/knowledge/bases
```

## 创建第一个知识库

```bash
curl -X POST http://localhost:8080/api/knowledge/bases \
  -H "Content-Type: application/json" \
  -d '{
    "name": "技术文档库",
    "description": "技术文档和API参考"
  }'
```

## 上传文档

```bash
curl -X POST http://localhost:8080/api/knowledge/documents \
  -F "file=@document.pdf" \
  -F "kbId=kb-001"
```

## 知识检索

```bash
curl -X POST http://localhost:8080/api/knowledge/search \
  -H "Content-Type: application/json" \
  -d '{
    "kbId": "kb-001",
    "query": "如何配置系统？",
    "topK": 5
  }'
```

## RAG智能问答

```bash
curl -X POST http://localhost:8080/api/knowledge/rag \
  -H "Content-Type: application/json" \
  -d '{
    "kbId": "kb-001",
    "question": "系统支持哪些功能？",
    "topK": 5
  }'
```

## 下一步

- [API参考](api-reference.md)
- [配置指南](configuration.md)

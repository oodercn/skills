# 快速开始

## 环境要求

- Java 8+
- Maven 3.6+
- Spring Boot 2.x
- 磁盘空间: 至少2GB用于知识库存储

## 安装

### 方式一：通过Skill市场安装

```bash
skill install skill-knowledge-base
```

### 方式二：手动安装

1. 克隆代码
```bash
git clone https://gitee.com/ooderCN/ooder-skills.git
cd skills/capabilities/knowledge/skill-knowledge-base
```

2. 编译安装
```bash
mvn clean install
```

## 配置

### 基础配置

编辑 `application.yml`：

```yaml
skill-knowledge-base:
  storage-path: ./data/kb
  max-document-size: 10485760
  search-limit: 10
  enable-vector-search: true
  embedding-model: text-embedding-3-small
  chunk-size: 500
  chunk-overlap: 50
```

## 验证安装

```bash
mvn spring-boot:run

curl http://localhost:8080/api/v1/kb
```

## 创建知识库

```bash
curl -X POST http://localhost:8080/api/v1/kb \
  -H "Content-Type: application/json" \
  -d '{
    "name": "技术文档库",
    "description": "技术文档和API参考"
  }'
```

## 上传文档

```bash
curl -X POST http://localhost:8080/api/v1/kb/{kbId}/documents \
  -F "file=@document.pdf"
```

## 检索知识库

### 关键词检索

```bash
curl -X POST http://localhost:8080/api/v1/kb/{kbId}/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "配置",
    "limit": 10
  }'
```

### 语义检索

```bash
curl -X POST http://localhost:8080/api/v1/kb/{kbId}/search/semantic \
  -H "Content-Type: application/json" \
  -d '{
    "query": "如何配置系统",
    "limit": 10
  }'
```

### 混合检索

```bash
curl -X POST http://localhost:8080/api/v1/kb/{kbId}/search/hybrid \
  -H "Content-Type: application/json" \
  -d '{
    "query": "系统配置方法",
    "limit": 10
  }'
```

## 下一步

- [API参考](api-reference.md)
- [知识库管理](kb-management.md)
- [检索指南](search-guide.md)

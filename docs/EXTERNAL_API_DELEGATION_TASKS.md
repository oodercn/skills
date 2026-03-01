# 外部API委托开发任务说明

> **版本**: v1.0  
> **日期**: 2026-03-01  
> **委托方**: ooder-skills项目组  
> **受托方**: SDK团队/外部开发团队

---

## 一、任务概述

本文档定义需要外部API协助的开发任务，当前项目负责业务逻辑和页面交互，以下核心能力需要委托开发。

---

## 二、委托任务清单

### 2.1 任务总览

| 任务ID | 任务名称 | 优先级 | 预计工时 | 外部依赖 |
|--------|----------|--------|----------|----------|
| EXT-001 | OpenAI真实API调用 | P0 | 2天 | OpenAI API Key |
| EXT-002 | 通义千问真实API调用 | P0 | 2天 | 阿里云API Key |
| EXT-003 | DeepSeek真实API调用 | P1 | 1天 | DeepSeek API Key |
| EXT-004 | Ollama本地模型集成 | P1 | 1天 | 无 |
| EXT-005 | 流式对话(SSE)实现 | P0 | 2天 | 无 |
| EXT-006 | SQLite-Vec向量存储 | P1 | 3天 | 无 |
| EXT-007 | 文本嵌入服务 | P1 | 2天 | LLM API |
| EXT-008 | Function Calling | P2 | 3天 | LLM API |

---

## 三、详细任务说明

### 3.1 EXT-001: OpenAI真实API调用

**优先级**: P0  
**预计工时**: 2天  
**交付物**: `skill-llm-openai` 模块更新

**功能要求**:

| 方法 | 说明 | API端点 |
|------|------|---------|
| chat() | 对话补全 | POST /v1/chat/completions |
| embed() | 文本嵌入 | POST /v1/embeddings |
| chatStream() | 流式对话 | POST /v1/chat/completions (stream=true) |

**请求示例**:

```http
POST https://api.openai.com/v1/chat/completions
Authorization: Bearer {api_key}
Content-Type: application/json

{
  "model": "gpt-4o",
  "messages": [
    {"role": "system", "content": "You are a helpful assistant."},
    {"role": "user", "content": "Hello"}
  ],
  "temperature": 0.7,
  "max_tokens": 1024
}
```

**响应解析**:

```json
{
  "id": "chatcmpl-xxx",
  "object": "chat.completion",
  "model": "gpt-4o",
  "choices": [{
    "index": 0,
    "message": {
      "role": "assistant",
      "content": "Hello! How can I help you today?"
    },
    "finish_reason": "stop"
  }],
  "usage": {
    "prompt_tokens": 10,
    "completion_tokens": 8,
    "total_tokens": 18
  }
}
```

**验收标准**:
- [ ] chat()方法返回正确的对话结果
- [ ] embed()方法返回1536维向量
- [ ] API Key从环境变量读取
- [ ] 错误处理完善（超时、限流、认证失败）

---

### 3.2 EXT-002: 通义千问真实API调用

**优先级**: P0  
**预计工时**: 2天  
**交付物**: `skill-llm-qianwen` 模块更新

**功能要求**:

| 方法 | 说明 | API端点 |
|------|------|---------|
| chat() | 对话补全 | POST /services/aigc/text-generation/generation |
| embed() | 文本嵌入 | POST /services/embeddings/text-embedding/text-embedding |

**请求示例**:

```http
POST https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation
Authorization: Bearer {api_key}
Content-Type: application/json

{
  "model": "qwen-plus",
  "input": {
    "messages": [
      {"role": "system", "content": "You are a helpful assistant."},
      {"role": "user", "content": "你好"}
    ]
  },
  "parameters": {
    "temperature": 0.7,
    "max_tokens": 1024
  }
}
```

**响应解析**:

```json
{
  "output": {
    "text": "你好！有什么我可以帮助你的吗？",
    "finish_reason": "stop"
  },
  "usage": {
    "input_tokens": 10,
    "output_tokens": 12,
    "total_tokens": 22
  },
  "request_id": "xxx"
}
```

**验收标准**:
- [ ] chat()方法返回正确的对话结果
- [ ] embed()方法返回正确维度向量
- [ ] 支持qwen-turbo、qwen-plus、qwen-max模型
- [ ] 错误处理完善

---

### 3.3 EXT-003: DeepSeek真实API调用

**优先级**: P1  
**预计工时**: 1天  
**交付物**: `skill-llm-deepseek` 模块更新

**API端点**: `https://api.deepseek.com/v1/chat/completions`

**请求格式**: 与OpenAI兼容

**验收标准**:
- [ ] 支持deepseek-chat、deepseek-coder模型
- [ ] 返回格式与OpenAI兼容

---

### 3.4 EXT-004: Ollama本地模型集成

**优先级**: P1  
**预计工时**: 1天  
**交付物**: `skill-llm-ollama` 模块更新

**API端点**: `http://localhost:11434/api`

**请求示例**:

```http
POST http://localhost:11434/api/chat
Content-Type: application/json

{
  "model": "qwen2.5:7b",
  "messages": [
    {"role": "user", "content": "Hello"}
  ],
  "stream": false
}
```

**验收标准**:
- [ ] 支持本地部署模型
- [ ] 无需API Key
- [ ] 支持流式和非流式输出

---

### 3.5 EXT-005: 流式对话(SSE)实现

**优先级**: P0  
**预计工时**: 2天  
**交付物**: 所有Provider的流式实现

**技术要求**:

```java
public interface StreamHandler {
    void onChunk(String chunk);
    void onComplete(Map<String, Object> metadata);
    void onError(Throwable error);
}

public interface LlmProvider {
    void chatStream(String model, List<Map<String, Object>> messages,
                    Map<String, Object> options, StreamHandler handler);
}
```

**SSE响应格式**:

```
data: {"choices":[{"delta":{"content":"Hello"}}]}

data: {"choices":[{"delta":{"content":"!"}}]}

data: [DONE]
```

**验收标准**:
- [ ] 实时输出，无延迟
- [ ] 支持中断
- [ ] 错误处理完善

---

### 3.6 EXT-006: SQLite-Vec向量存储

**优先级**: P1  
**预计工时**: 3天  
**交付物**: 新模块 `skill-vector-sqlite`

**数据库Schema**:

```sql
CREATE TABLE vectors (
    id TEXT PRIMARY KEY,
    kb_id TEXT NOT NULL,
    doc_id TEXT NOT NULL,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    embedding BLOB NOT NULL,
    metadata TEXT,
    create_time INTEGER NOT NULL
);

CREATE INDEX idx_vectors_kb_id ON vectors(kb_id);
CREATE INDEX idx_vectors_doc_id ON vectors(doc_id);
```

**接口定义**:

```java
public interface VectorStore {
    void insert(String id, float[] vector, Map<String, Object> metadata);
    void batchInsert(List<VectorData> vectors);
    List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters);
    void delete(String id);
    void deleteByMetadata(Map<String, Object> filters);
    int getDimension();
}
```

**验收标准**:
- [ ] 支持向量存储和检索
- [ ] 支持元数据过滤
- [ ] 支持批量插入
- [ ] 性能：10000条向量检索<100ms

---

### 3.7 EXT-007: 文本嵌入服务

**优先级**: P1  
**预计工时**: 2天  
**交付物**: `EmbeddingService` 实现

**接口定义**:

```java
public interface EmbeddingService {
    float[] embed(String text);
    List<float[]> embedBatch(List<String> texts);
    int getDimension();
    String getModel();
}
```

**实现要求**:
- 支持OpenAI text-embedding-3-small/large
- 支持通义千问 text-embedding-v3
- 支持Ollama本地嵌入模型

**验收标准**:
- [ ] 单文本嵌入正确
- [ ] 批量嵌入正确
- [ ] 向量维度正确（1536/3072）

---

### 3.8 EXT-008: Function Calling

**优先级**: P2  
**预计工时**: 3天  
**交付物**: `FunctionCallingService` 实现

**接口定义**:

```java
public interface FunctionCallingService {
    Map<String, Object> executeFunction(String name, Map<String, Object> args);
    List<FunctionDefinition> getAvailableFunctions();
    void registerFunction(FunctionDefinition function, FunctionExecutor executor);
}

public class FunctionDefinition {
    private String name;
    private String description;
    private Map<String, Object> parameters;
}
```

**验收标准**:
- [ ] 支持函数定义注册
- [ ] LLM自动选择函数调用
- [ ] 返回结果正确

---

## 四、接口规范汇总

### 4.1 LlmProvider接口

```java
package net.ooder.scene.skill;

public interface LlmProvider {
    String getProviderType();
    List<String> getSupportedModels();
    Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options);
    String complete(String model, String prompt, Map<String, Object> options);
    List<double[]> embed(String model, List<String> texts);
    String translate(String model, String text, String targetLanguage, String sourceLanguage);
    String summarize(String model, String text, int maxLength);
    boolean supportsStreaming();
    boolean supportsFunctionCalling();
    void chatStream(String model, List<Map<String, Object>> messages, Map<String, Object> options, StreamHandler handler);
}
```

### 4.2 VectorStore接口

```java
package net.ooder.scene.skill;

public interface VectorStore {
    void insert(String id, float[] vector, Map<String, Object> metadata);
    void batchInsert(List<VectorData> vectors);
    List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters);
    void delete(String id);
    void deleteByMetadata(Map<String, Object> filters);
    int getDimension();
}
```

### 4.3 EmbeddingService接口

```java
package net.ooder.scene.skill;

public interface EmbeddingService {
    float[] embed(String text);
    List<float[]> embedBatch(List<String> texts);
    int getDimension();
    String getModel();
}
```

---

## 五、配置规范

### 5.1 环境变量

| 变量名 | 说明 | 必需 |
|--------|------|------|
| OPENAI_API_KEY | OpenAI API密钥 | 可选 |
| DASHSCOPE_API_KEY | 阿里云DashScope API密钥 | 可选 |
| DEEPSEEK_API_KEY | DeepSeek API密钥 | 可选 |

### 5.2 application.yml

```yaml
llm:
  default-provider: qianwen
  default-model: qwen-plus
  timeout: 60000
  max-retries: 3
  
  providers:
    openai:
      api-key: ${OPENAI_API_KEY:}
      base-url: https://api.openai.com/v1
    qianwen:
      api-key: ${DASHSCOPE_API_KEY:}
      base-url: https://dashscope.aliyuncs.com/api/v1
    deepseek:
      api-key: ${DEEPSEEK_API_KEY:}
      base-url: https://api.deepseek.com/v1
    ollama:
      base-url: http://localhost:11434

vector:
  db:
    path: ./data/vectors.db
    dimension: 1536
```

---

## 六、SPI配置

### 6.1 文件路径

```
META-INF/services/net.ooder.scene.skill.LlmProvider
META-INF/services/net.ooder.scene.skill.VectorStore
META-INF/services/net.ooder.scene.skill.EmbeddingService
```

### 6.2 配置内容

```
net.ooder.skill.llm.openai.OpenAiLlmProvider
net.ooder.skill.llm.qianwen.QianwenLlmProvider
net.ooder.skill.llm.deepseek.DeepSeekLlmProvider
net.ooder.skill.llm.ollama.OllamaLlmProvider
```

---

## 七、里程碑

| 里程碑 | 日期 | 交付物 | 验收标准 |
|--------|------|--------|----------|
| M1 | Week 2 | OpenAI + 千问真实API | API调用成功返回结果 |
| M2 | Week 2 | 流式对话输出 | SSE实时输出正常 |
| M3 | Week 3 | DeepSeek + Ollama | 所有Provider可用 |
| M4 | Week 4 | SQLite-Vec向量存储 | 向量检索正常 |
| M5 | Week 4 | 测试报告 | 覆盖率≥80% |

---

## 八、验收标准

### 8.1 功能验收

- [ ] 所有API调用返回正确结果
- [ ] 流式输出无延迟、无截断
- [ ] 向量存储和检索正常
- [ ] 错误处理完善

### 8.2 性能验收

| 指标 | 要求 |
|------|------|
| API调用响应时间 | < 30秒 |
| 流式首字延迟 | < 2秒 |
| 向量检索(10K) | < 100ms |
| 批量嵌入(100条) | < 10秒 |

### 8.3 测试验收

- [ ] 单元测试覆盖率 ≥ 80%
- [ ] 集成测试通过
- [ ] 无P0/P1级别问题

---

## 九、交付物清单

### 9.1 代码

| 模块 | 文件 | 说明 |
|------|------|------|
| skill-llm-openai | OpenAiLlmProvider.java | OpenAI真实实现 |
| skill-llm-qianwen | QianwenLlmProvider.java | 千问真实实现 |
| skill-llm-deepseek | DeepSeekLlmProvider.java | DeepSeek真实实现 |
| skill-llm-ollama | OllamaLlmProvider.java | Ollama真实实现 |
| skill-vector-sqlite | SQLiteVectorStore.java | SQLite向量存储 |

### 9.2 文档

| 文档 | 说明 |
|------|------|
| API使用文档 | 接口调用说明 |
| 配置指南 | 部署配置说明 |
| 测试报告 | 测试结果报告 |

---

## 十、联系方式

| 角色 | 联系方式 |
|------|----------|
| 委托方 | ooder-skills项目组 |
| 问题反馈 | GitHub Issues |

---

**文档结束**

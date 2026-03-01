# SDK委托开发文档 - LLM与知识能力

> **版本**: v1.0  
> **日期**: 2026-03-01  
> **委托方**: ooder-skills项目组  
> **受托方**: SDK团队

---

## 一、项目背景

ooder平台需要为场景系统提供智能化能力支撑，包括：
- LLM对话与生成能力
- 向量检索与RAG能力
- 知识图谱与术语映射能力

本文档定义SDK团队需要交付的核心接口和技术框架。

---

## 二、交付范围

### 2.1 必须交付 (P0)

| 模块 | 功能 | 优先级 |
|------|------|--------|
| LlmProvider | LLM真实API调用 | P0 |
| StreamChat | 流式对话输出 | P0 |

### 2.2 建议交付 (P1)

| 模块 | 功能 | 优先级 |
|------|------|--------|
| VectorStore | 向量存储与检索 | P1 |
| EmbeddingService | 文本嵌入服务 | P1 |
| FunctionCalling | 函数调用能力 | P1 |

### 2.3 可选交付 (P2)

| 模块 | 功能 | 优先级 |
|------|------|--------|
| ConversationManager | 多轮对话上下文 | P2 |
| UsageStatsService | Token计费统计 | P2 |

---

## 三、接口规范

### 3.1 LlmProvider接口

**包路径**: `net.ooder.scene.skill.LlmProvider`

```java
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
}
```

### 3.2 流式对话接口

**新增方法**:

```java
public interface LlmProvider {
    
    void chatStream(String model, List<Map<String, Object>> messages, 
                    Map<String, Object> options, StreamHandler handler);
}

public interface StreamHandler {
    void onChunk(String chunk);
    void onComplete(Map<String, Object> metadata);
    void onError(Throwable error);
}
```

### 3.3 VectorStore接口

**包路径**: `net.ooder.scene.skill.VectorStore`

```java
public interface VectorStore {
    
    void insert(String id, float[] vector, Map<String, Object> metadata);
    
    void batchInsert(List<VectorData> vectors);
    
    List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters);
    
    void delete(String id);
    
    void deleteByMetadata(Map<String, Object> filters);
    
    int getDimension();
}

public class VectorData {
    private String id;
    private float[] vector;
    private Map<String, Object> metadata;
}

public class SearchResult {
    private String id;
    private float score;
    private Map<String, Object> metadata;
}
```

### 3.4 EmbeddingService接口

**包路径**: `net.ooder.scene.skill.EmbeddingService`

```java
public interface EmbeddingService {
    
    float[] embed(String text);
    
    List<float[]> embedBatch(List<String> texts);
    
    int getDimension();
    
    String getModel();
}
```

---

## 四、Provider实现要求

### 4.1 OpenAI Provider

```java
@Component
public class OpenAiLlmProvider implements LlmProvider {
    
    private String apiKey;
    private String baseUrl = "https://api.openai.com/v1";
    
    @Override
    public String getProviderType() {
        return "openai";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList(
            "gpt-4", "gpt-4-turbo", "gpt-4o", "gpt-4o-mini",
            "gpt-3.5-turbo", "gpt-3.5-turbo-16k",
            "text-embedding-3-small", "text-embedding-3-large"
        );
    }
    
    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, 
                                     Map<String, Object> options) {
        // 真实API调用实现
    }
    
    @Override
    public void chatStream(String model, List<Map<String, Object>> messages,
                           Map<String, Object> options, StreamHandler handler) {
        // SSE流式调用实现
    }
}
```

### 4.2 通义千问 Provider

```java
@Component
public class QianwenLlmProvider implements LlmProvider {
    
    private String apiKey;
    private String baseUrl = "https://dashscope.aliyuncs.com/api/v1";
    
    @Override
    public String getProviderType() {
        return "qianwen";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList(
            "qwen-turbo", "qwen-plus", "qwen-max",
            "qwen2.5-72b-instruct", "qwen2.5-32b-instruct"
        );
    }
}
```

### 4.3 DeepSeek Provider

```java
@Component
public class DeepSeekLlmProvider implements LlmProvider {
    
    private String apiKey;
    private String baseUrl = "https://api.deepseek.com/v1";
    
    @Override
    public String getProviderType() {
        return "deepseek";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList("deepseek-chat", "deepseek-coder", "deepseek-reasoner");
    }
}
```

---

## 五、SQLite-Vec集成要求

### 5.1 依赖配置

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>
```

### 5.2 数据库Schema

```sql
CREATE TABLE IF NOT EXISTS vectors (
    id TEXT PRIMARY KEY,
    kb_id TEXT NOT NULL,
    doc_id TEXT NOT NULL,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    embedding BLOB NOT NULL,
    metadata TEXT,
    create_time INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_vectors_kb_id ON vectors(kb_id);
CREATE INDEX IF NOT EXISTS idx_vectors_doc_id ON vectors(doc_id);
```

### 5.3 实现要求

```java
@Component
public class SQLiteVectorStore implements VectorStore {
    
    private final String dbPath;
    private final int dimension;
    
    public SQLiteVectorStore(
        @Value("${vector.db.path:./data/vectors.db}") String dbPath,
        @Value("${vector.dimension:1536}") int dimension
    ) {
        this.dbPath = dbPath;
        this.dimension = dimension;
        initDatabase();
    }
    
    @Override
    public List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters) {
        // 1. 根据filters构建SQL查询
        // 2. 加载所有候选向量
        // 3. 计算余弦相似度
        // 4. 排序返回TopK
    }
    
    private float cosineSimilarity(float[] a, float[] b) {
        float dotProduct = 0;
        float normA = 0;
        float normB = 0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dotProduct / (float) (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
```

---

## 六、SPI配置

### 6.1 文件路径

```
META-INF/services/net.ooder.scene.skill.LlmProvider
META-INF/services/net.ooder.scene.skill.VectorStore
META-INF/services/net.ooder.scene.skill.EmbeddingService
```

### 6.2 配置内容示例

```
net.ooder.skill.llm.openai.OpenAiLlmProvider
net.ooder.skill.llm.qianwen.QianwenLlmProvider
net.ooder.skill.llm.deepseek.DeepSeekLlmProvider
net.ooder.skill.llm.ollama.OllamaLlmProvider
```

---

## 七、配置规范

### 7.1 application.yml

```yaml
llm:
  default-provider: qianwen
  default-model: qwen-plus
  timeout: 60000
  max-retries: 3
  
  providers:
    openai:
      api-key: ${OPENAI_API_KEY}
      base-url: https://api.openai.com/v1
    qianwen:
      api-key: ${DASHSCOPE_API_KEY}
      base-url: https://dashscope.aliyuncs.com/api/v1
    deepseek:
      api-key: ${DEEPSEEK_API_KEY}
      base-url: https://api.deepseek.com/v1
    ollama:
      base-url: http://localhost:11434

vector:
  db:
    path: ./data/vectors.db
    dimension: 1536
  cache:
    enabled: true
    max-size: 10000
```

---

## 八、测试要求

### 8.1 单元测试

- 每个Provider需要独立的单元测试
- Mock HTTP客户端进行测试
- 覆盖率要求 ≥ 80%

### 8.2 集成测试

- 真实API调用测试（可选，需要API Key）
- 流式输出测试
- 错误处理测试

### 8.3 测试用例

```java
@Test
public void testChatCompletion() {
    LlmProvider provider = new OpenAiLlmProvider(testApiKey);
    
    List<Map<String, Object>> messages = new ArrayList<>();
    messages.add(Map.of("role", "user", "content", "Hello"));
    
    Map<String, Object> result = provider.chat("gpt-3.5-turbo", messages, new HashMap<>());
    
    assertNotNull(result.get("choices"));
    assertTrue(((List)result.get("choices")).size() > 0);
}

@Test
public void testEmbedding() {
    LlmProvider provider = new OpenAiLlmProvider(testApiKey);
    
    List<double[]> embeddings = provider.embed("text-embedding-3-small", 
        Arrays.asList("Hello", "World"));
    
    assertEquals(2, embeddings.size());
    assertEquals(1536, embeddings.get(0).length);
}
```

---

## 九、交付清单

### 9.1 代码交付物

| 模块 | 文件 | 说明 |
|------|------|------|
| skill-llm-openai | OpenAiLlmProvider.java | OpenAI真实实现 |
| skill-llm-qianwen | QianwenLlmProvider.java | 千问真实实现 |
| skill-llm-deepseek | DeepSeekLlmProvider.java | DeepSeek真实实现 |
| skill-llm-ollama | OllamaLlmProvider.java | Ollama真实实现 |
| skill-vector-sqlite | SQLiteVectorStore.java | SQLite向量存储 |

### 9.2 文档交付物

| 文档 | 说明 |
|------|------|
| API文档 | 接口使用说明 |
| 配置指南 | 部署配置说明 |
| 测试报告 | 测试结果报告 |

---

## 十、里程碑与时间表

| 里程碑 | 日期 | 交付物 | 验收标准 |
|--------|------|--------|----------|
| M1 | Week 2 | LlmProvider真实实现 | API调用成功，返回正确结果 |
| M2 | Week 2 | 流式对话输出 | SSE输出正常，无截断 |
| M3 | Week 4 | SQLite-Vec集成 | 向量存储和检索正常 |
| M4 | Week 4 | 测试报告 | 覆盖率≥80%，无P0问题 |

---

## 十一、联系方式

| 角色 | 联系方式 |
|------|----------|
| 委托方负责人 | ooder-skills项目组 |
| 技术对接人 | [待指定] |
| 问题反馈 | GitHub Issues |

---

**文档结束**

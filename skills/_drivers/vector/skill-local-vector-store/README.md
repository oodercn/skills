# 本地向量存储

## 简介

skill-local-vector-store 是一个提供基于 SQLite 的本地向量存储支持的 SPI Driver，通过 `VectorStoreProvider` SPI 接口为 OoderOS 提供向量存储和检索能力。

## 功能特性

- ✅ 基于 SQLite 的本地向量存储
- ✅ 向量相似度搜索（余弦相似度）
- ✅ 向量元数据存储和过滤
- ✅ 批量向量操作
- ✅ 向量缓存机制
- ✅ 健康检查支持

## 安装

### 方式一：作为默认 Skill 安装

将 jar 包放入 `plugins/` 目录：

```bash
cp skill-local-vector-store-1.0.0.jar plugins/
```

### 方式二：Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-local-vector-store</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 配置

### skill.yaml 配置

```yaml
config:
  vector:
    data-dir: ${user.home}/.apexos/data
    database-name: vectors
    default-dimension: 1536
    metric-type: cosine
    pool:
      max-size: 5
      min-idle: 1
```

### 配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| data-dir | 数据库文件存储目录 | ${user.home}/.apexos/data |
| database-name | 数据库名称 | vectors |
| default-dimension | 默认向量维度 | 1536 |
| metric-type | 相似度计算方式 | cosine |
| pool.max-size | 连接池最大连接数 | 5 |
| pool.min-idle | 连接池最小空闲连接数 | 1 |

## 使用示例

### 通过 SPI 存储向量

```java
import net.ooder.spi.vector.VectorStoreProvider;
import net.ooder.spi.vector.VectorData;
import net.ooder.spi.vector.VectorStoreConfig;
import net.ooder.spi.facade.SpiServices;

// 初始化
VectorStoreProvider provider = SpiServices.getVectorStoreProvider();
VectorStoreConfig config = VectorStoreConfig.builder()
    .dimension(1536)
    .metricType("cosine")
    .dataDir("/path/to/data")
    .build();

provider.initialize(config);

// 存储单个向量
float[] vector = new float[1536]; // 你的向量数据
Map<String, Object> metadata = new HashMap<>();
metadata.put("document_id", "doc-001");
metadata.put("chunk_index", 0);

provider.store("vec-001", vector, metadata);

// 批量存储
List<VectorData> vectors = new ArrayList<>();
vectors.add(new VectorData("vec-002", vector2, metadata2));
vectors.add(new VectorData("vec-003", vector3, metadata3));

provider.batchStore(vectors);
```

### 通过 SPI 搜索向量

```java
// 相似度搜索
float[] queryVector = new float[1536]; // 查询向量
List<SearchResult> results = provider.search(queryVector, 10);

for (SearchResult result : results) {
    System.out.println("ID: " + result.getId());
    System.out.println("Score: " + result.getScore());
    System.out.println("Metadata: " + result.getMetadata());
}

// 带过滤条件的搜索
Map<String, Object> filter = new HashMap<>();
filter.put("document_id", "doc-001");

List<SearchResult> filteredResults = provider.search(queryVector, 10, filter);
```

### 其他操作

```java
// 获取向量
VectorData data = provider.get("vec-001");

// 删除向量
provider.delete("vec-001");

// 批量删除
provider.batchDelete(Arrays.asList("vec-002", "vec-003"));

// 获取向量总数
long count = provider.count();

// 清空所有向量
provider.clear();

// 健康检查
boolean healthy = provider.isHealthy();

// 关闭存储
provider.close();
```

## SPI 接口

### VectorStoreProvider

```java
public interface VectorStoreProvider {
    String getProviderType();
    String getProviderName();
    void initialize(VectorStoreConfig config);
    void store(String id, float[] vector, Map<String, Object> metadata);
    void batchStore(List<VectorData> vectors);
    List<SearchResult> search(float[] vector, int topK);
    List<SearchResult> search(float[] vector, int topK, Map<String, Object> filter);
    void delete(String id);
    void batchDelete(List<String> ids);
    VectorData get(String id);
    long count();
    void clear();
    void close();
    boolean isHealthy();
}
```

## 技术栈

- SQLite JDBC 3.49.1.0
- HikariCP 5.1.0
- Spring Boot AutoConfigure
- Ooder SPI Core 3.0.2

## 性能说明

### 适用场景

- ✅ 小规模向量存储（< 100万向量）
- ✅ 本地开发和测试环境
- ✅ 嵌入式应用场景
- ✅ 对延迟要求不高的场景

### 不适用场景

- ❌ 大规模向量存储（> 100万向量）
- ❌ 高并发查询场景
- ❌ 需要分布式部署的场景
- ❌ 对查询性能要求极高的场景

### 性能优化建议

1. 使用向量缓存减少数据库访问
2. 合理设置连接池大小
3. 定期清理无用向量
4. 考虑使用专业的向量数据库（如 Milvus）处理大规模场景

## 版本历史

### v1.0.0 (2026-04-10)
- 初始版本
- 实现 VectorStoreProvider SPI 接口
- 基于 SQLite 的向量存储
- 支持余弦相似度搜索
- 支持元数据存储和过滤

## 许可证

MIT License

## 作者

ooder team

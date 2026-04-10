package net.ooder.spi.vector;

import java.util.List;
import java.util.Map;

/**
 * 向量存储提供者 SPI 接口
 * 用于提供向量存储和检索能力
 */
public interface VectorStoreProvider {
    
    /**
     * 获取提供者类型标识
     * @return 提供者类型，如 "local", "milvus", "pinecone"
     */
    String getProviderType();
    
    /**
     * 获取提供者显示名称
     * @return 提供者名称
     */
    String getProviderName();
    
    /**
     * 初始化向量存储
     * @param config 向量存储配置
     */
    void initialize(VectorStoreConfig config);
    
    /**
     * 存储向量
     * @param id 向量ID
     * @param vector 向量数据
     * @param metadata 元数据
     */
    void store(String id, float[] vector, Map<String, Object> metadata);
    
    /**
     * 批量存储向量
     * @param vectors 向量列表
     */
    void batchStore(List<VectorData> vectors);
    
    /**
     * 相似度搜索
     * @param vector 查询向量
     * @param topK 返回数量
     * @return 相似向量列表
     */
    List<SearchResult> search(float[] vector, int topK);
    
    /**
     * 相似度搜索（带过滤条件）
     * @param vector 查询向量
     * @param topK 返回数量
     * @param filter 过滤条件
     * @return 相似向量列表
     */
    List<SearchResult> search(float[] vector, int topK, Map<String, Object> filter);
    
    /**
     * 删除向量
     * @param id 向量ID
     */
    void delete(String id);
    
    /**
     * 批量删除向量
     * @param ids 向量ID列表
     */
    void batchDelete(List<String> ids);
    
    /**
     * 获取向量
     * @param id 向量ID
     * @return 向量数据
     */
    VectorData get(String id);
    
    /**
     * 获取向量总数
     * @return 向量总数
     */
    long count();
    
    /**
     * 清空所有向量
     */
    void clear();
    
    /**
     * 关闭向量存储
     */
    void close();
    
    /**
     * 检查是否健康
     * @return 是否健康
     */
    boolean isHealthy();
}

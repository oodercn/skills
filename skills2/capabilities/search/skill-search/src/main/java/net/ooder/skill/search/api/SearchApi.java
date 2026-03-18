package net.ooder.skill.search.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 鎼滅储鏈嶅姟API
 */
public interface SearchApi {

    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();

    // 鎼滅储
    Result<List<Map<String, Object>>> search(String index, String query);
    Result<List<Map<String, Object>>> searchWithFilters(String index, Map<String, Object> filters);

    // 绱㈠紩绠＄悊
    Result<Boolean> createIndex(String index, Map<String, Object> settings);
    Result<Boolean> deleteIndex(String index);
    Result<Boolean> indexDocument(String index, String id, Map<String, Object> document);

    // 鑱氬悎鏌ヨ
    Result<Map<String, Object>> aggregate(String index, Map<String, Object> aggregation);
}

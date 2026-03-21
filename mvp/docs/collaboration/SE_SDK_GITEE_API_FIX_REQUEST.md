# SE SDK 团队协作请求：修复 Gitee API 响应解析

## 一、问题描述

### 1.1 现象
MVP 项目集成 SE SDK 2.3.1 后，Gitee 发现功能始终返回 0 个技能。

### 1.2 根本原因

**SE SDK 没有正确处理 Gitee API 的响应格式**

Gitee API 返回的是 JSON 格式，包含 Base64 编码的内容：
```json
{
  "content": "YXBpVmVyc2lvbjogb29kZXIuaW8vdjEKa2luZDogU2tpbGxJbmRl...",
  "sha": "a991e40de751eb0a5f299c73b14521a9380a3e0c",
  "url": "https://gitee.com/api/v5/repos/ooderCN/skills/contents/skill-index.yaml",
  "download_url": "https://gitee.com/ooderCN/skills/raw/master/skill-index.yaml"
}
```

但 SE SDK 的 `fetchSkillsFromGitee` 方法直接把这个 JSON 当作 YAML 来解析：

```java
// UnifiedDiscoveryServiceImpl.java 第 241-247 行
String content = fetchUrlContent(indexUrl);  // 返回的是 JSON 字符串
if (content == null) {
    logger.warn("skill-index.yaml not found at path: {}", indexPath);
    return new ArrayList<>();
}
return parseSkillIndex(content);  // 错误：把 JSON 当作 YAML 解析
```

---

## 二、修复方案

### 2.1 修改 `fetchSkillsFromGitee` 方法

```java
private List<SkillPackage> fetchSkillsFromGitee(String owner, String repo, String branch, 
        String basePath, String token) {
    try {
        String indexPath = buildIndexPath(basePath);
        String indexUrl = String.format(
            "https://gitee.com/api/v5/repos/%s/%s/contents/%s?ref=%s",
            owner, repo, indexPath, branch
        );
        
        if (token != null && !token.isEmpty()) {
            indexUrl += "&access_token=" + token;
        }
        
        logger.debug("Fetching skill-index from: {}", indexUrl.replaceAll("access_token=[^&]+", "access_token=***"));
        
        String jsonResponse = fetchUrlContent(indexUrl);
        if (jsonResponse == null) {
            logger.warn("skill-index.yaml not found at path: {}", indexPath);
            return new ArrayList<>();
        }
        
        // 解析 Gitee API 响应，提取 Base64 编码的内容
        String yamlContent = extractGiteeContent(jsonResponse);
        if (yamlContent == null) {
            logger.warn("Failed to extract content from Gitee API response");
            return new ArrayList<>();
        }
        
        return parseSkillIndex(yamlContent);
        
    } catch (Exception e) {
        logger.error("Failed to fetch skills from Gitee: {}/{} - {}", owner, repo, e.getMessage());
        return new ArrayList<>();
    }
}

/**
 * 从 Gitee API 响应中提取 Base64 解码后的内容
 */
private String extractGiteeContent(String jsonResponse) {
    try {
        ObjectMapper jsonMapper = new ObjectMapper();
        Map<String, Object> response = jsonMapper.readValue(jsonResponse, Map.class);
        
        String base64Content = (String) response.get("content");
        if (base64Content == null) {
            return null;
        }
        
        // Gitee 返回的 Base64 内容包含换行符，需要移除
        base64Content = base64Content.replace("\n", "").replace("\r", "");
        
        // Base64 解码
        byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
        return new String(decodedBytes, StandardCharsets.UTF_8);
        
    } catch (Exception e) {
        logger.error("Failed to decode Gitee content: {}", e.getMessage());
        return null;
    }
}
```

### 2.2 需要添加的 import

```java
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
```

---

## 三、验证方式

### 3.1 API 测试

```bash
# 获取索引文件（返回 JSON）
curl "https://gitee.com/api/v5/repos/ooderCN/skills/contents/skill-index.yaml?ref=master"

# 响应示例
{
  "content": "YXBpVmVyc2lvbjogb29kZXIuaW8vdjEK...",
  "sha": "a991e40de751eb0a5f299c73b14521a9380a3e0c",
  ...
}
```

### 3.2 MVP 集成测试

1. 更新 SE SDK 版本
2. 启动 MVP 服务
3. 访问能力发现页面
4. 选择 Gitee 发现
5. 确认返回技能列表不为空

---

## 四、配置信息

### 4.1 当前配置

```yaml
scene:
  engine:
    discovery:
      gitee:
        enabled: true
        token: f0d11903a8e10e3ce09d51bc9552b664
        default-owner: ooderCN
        default-repo: skills
        default-branch: master
        skills-path: ""              # 索引文件在根目录
```

### 4.2 索引文件位置

- 根目录：`skill-index.yaml`
- 配置 `skills-path: ""` 时，SE SDK 应该获取 `skill-index.yaml`

---

## 五、优先级

**高优先级** - 影响 Gitee 发现功能的核心功能

---

## 六、相关文档

- [SE SDK UnifiedDiscoveryServiceImpl.java](file:///E:/github/ooder-sdk/scene-engine/src/main/java/net/ooder/scene/discovery/impl/UnifiedDiscoveryServiceImpl.java)
- [Gitee API 文档 - 获取文件内容](https://gitee.com/api/v5/swagger#/getV5ReposOwnerRepoContentsPath)
- [MVP Gitee 发现集成文档](file:///e:/github/ooder-skills/mvp/docs/features/GITEE_DISCOVERY_INTEGRATION.md)

---

**创建时间**: 2026-03-21  
**状态**: 待处理  
**指派**: SE SDK 团队

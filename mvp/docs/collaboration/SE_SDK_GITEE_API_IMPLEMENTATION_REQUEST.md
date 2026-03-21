# SE SDK 团队协作请求：实现 Gitee 发现 API 调用

## 一、问题描述

### 1.1 现象
MVP 项目集成 SE SDK 2.3.1 后，Gitee 发现功能始终返回 0 个技能：

```json
{
    "code": 200,
    "data": {
        "capabilities": [],
        "total": 0,
        "source": "gitee"
    }
}
```

### 1.2 根本原因

**SE SDK 2.3.1 的 `UnifiedDiscoveryServiceImpl.discoverFromGitee()` 方法是占位实现**

查看源码 `UnifiedDiscoveryServiceImpl.java` 第 173-200 行：

```java
private List<SkillPackage> discoverFromGitee(String repositoryUrl, String skillsPath) {
    try {
        String cacheKey = "gitee:" + repositoryUrl;
        
        if (cacheManager.exists(cacheKey)) {
            logger.info("Using cached skills for: {}", repositoryUrl);
            return cacheManager.get(cacheKey);
        }
        
        String owner = (String) giteeConfig.getOrDefault("owner", extractOwner(repositoryUrl));
        String repo = (String) giteeConfig.getOrDefault("repo", extractRepo(repositoryUrl));
        String token = (String) giteeConfig.get("token");
        
        logger.info("Discovering from Gitee: owner={}, repo={}", owner, repo);
        
        List<SkillPackage> skills = new ArrayList<>();
        
        // TODO: 实现实际的Gitee API调用
        // 这里需要调用Gitee API获取skill-index.yaml并解析
        
        cacheManager.put(cacheKey, skills, giteeCacheTtl);
        logger.info("Discovered {} skills from Gitee", skills.size());
        
        return skills;
    } catch (Exception e) {
        logger.error("Failed to discover from Gitee: " + repositoryUrl, e);
        return new ArrayList<>();
    }
}
```

**问题**：`discoverFromGitee()` 方法没有实际调用 Gitee API，只是返回空列表。

---

## 二、请求内容

### 2.1 需要实现的功能

在 `UnifiedDiscoveryServiceImpl.discoverFromGitee()` 中实现：

1. **调用 Gitee API 获取 `skill-index.yaml`**
   ```
   GET https://gitee.com/api/v5/repos/{owner}/{repo}/contents/{skillsPath}/skill-index.yaml
   Header: Authorization: token {token}
   ```

2. **解析索引文件内容**
   - 解析 YAML 格式
   - 提取技能列表

3. **构建 `SkillPackage` 列表**
   - 为每个技能创建 `SkillPackage` 对象
   - 填充元数据（名称、版本、描述等）

### 2.2 实现参考

```java
private List<SkillPackage> discoverFromGitee(String repositoryUrl, String skillsPath) {
    try {
        String owner = (String) giteeConfig.getOrDefault("owner", extractOwner(repositoryUrl));
        String repo = (String) giteeConfig.getOrDefault("repo", extractRepo(repositoryUrl));
        String token = (String) giteeConfig.get("token");
        String branch = (String) giteeConfig.getOrDefault("branch", "master");
        String path = skillsPath != null ? skillsPath : (String) giteeConfig.get("skillsPath");
        
        // 1. 构建 API URL
        String indexPath = path != null && !path.isEmpty() 
            ? path + "/skill-index.yaml" 
            : "skill-index.yaml";
        String apiUrl = String.format(
            "https://gitee.com/api/v5/repos/%s/%s/contents/%s?ref=%s",
            owner, repo, indexPath, branch
        );
        
        // 2. 调用 Gitee API
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Authorization", "token " + token)
            .GET()
            .build();
        
        HttpResponse<String> response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            logger.error("Failed to fetch skill-index.yaml: {}", response.statusCode());
            return new ArrayList<>();
        }
        
        // 3. 解析 Base64 编码的内容
        JSONObject json = new JSONObject(response.body());
        String content = new String(Base64.getDecoder().decode(json.getString("content")));
        
        // 4. 解析 YAML
        Yaml yaml = new Yaml();
        Map<String, Object> index = yaml.load(content);
        
        // 5. 构建 SkillPackage 列表
        List<SkillPackage> skills = new ArrayList<>();
        List<Map<String, Object>> skillList = (List<Map<String, Object>>) index.get("skills");
        
        if (skillList != null) {
            for (Map<String, Object> skillData : skillList) {
                SkillPackage pkg = new SkillPackage();
                pkg.setSkillId((String) skillData.get("id"));
                pkg.setName((String) skillData.get("name"));
                pkg.setVersion((String) skillData.get("version"));
                pkg.setSource("gitee");
                // ... 设置其他属性
                skills.add(pkg);
            }
        }
        
        return skills;
    } catch (Exception e) {
        logger.error("Failed to discover from Gitee", e);
        return new ArrayList<>();
    }
}
```

---

## 三、Gitee 仓库信息

### 3.1 仓库结构

```
ooderCN/skills/
├── skill-index.yaml          # 索引文件（根目录）
├── skills/                   # 技能子目录
│   ├── _drivers/
│   ├── capabilities/
│   └── ...
└── README.md
```

### 3.2 索引文件格式

```yaml
apiVersion: ooder.io/v1
kind: SkillIndex

metadata:
  name: ooder-skills
  version: "2.3.1"

spec:
  sceneDrivers:
    - id: org
      name: Organization Driver
      capabilities: [...]
  categories:
    - id: org
      name: 组织服务
```

---

## 四、验证方式

### 4.1 API 测试

```bash
# 获取索引文件
curl -H "Authorization: token YOUR_TOKEN" \
  "https://gitee.com/api/v5/repos/ooderCN/skills/contents/skill-index.yaml?ref=master"
```

### 4.2 MVP 集成测试

1. 更新 SE SDK 版本
2. 启动 MVP 服务
3. 访问能力发现页面
4. 选择 Gitee 发现
5. 确认返回技能列表不为空

---

## 五、优先级

**高优先级** - 影响 Gitee 发现功能的核心功能

---

## 六、相关文档

- [SE SDK UnifiedDiscoveryServiceImpl.java](file:///E:/github/ooder-sdk/scene-engine/src/main/java/net/ooder/scene/discovery/impl/UnifiedDiscoveryServiceImpl.java)
- [Gitee API 文档](https://gitee.com/api/v5/swagger)
- [MVP Gitee 发现集成文档](file:///e:/github/ooder-skills/mvp/docs/features/GITEE_DISCOVERY_INTEGRATION.md)

---

**创建时间**: 2026-03-21  
**状态**: 待处理  
**指派**: SE SDK 团队

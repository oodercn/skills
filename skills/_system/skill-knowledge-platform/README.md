# skill-knowledge-platform

知识平台基础设施 - 提供知识组织架构管理、场景-知识绑定、嵌入模型配置、LLM知识配置等功能。

## 功能特性

- **知识组织管理** - 知识组织（公司级/部门级/专用业务）的增删改查及与知识库的绑定关系
- **场景知识绑定** - 将知识库绑定到场景分组，支持分层检索和跨层搜索
- **嵌入模型配置** - 嵌入模型的注册、配置、测试和管理
- **LLM知识配置** - 术语词典、同义词映射、Prompt模板等LLM增强配置

## 核心接口

### KnowledgeOrganizationController

知识组织管理控制器。

```java
@RestController
@RequestMapping("/api/v1/knowledge-organizations")
public class KnowledgeOrganizationController {
    /**
     * 获取知识组织列表
     */
    @GetMapping
    public List<KnowledgeOrgDTO> listOrganizations();
    
    /**
     * 创建知识组织
     */
    @PostMapping
    public KnowledgeOrgDTO createOrganization(@RequestBody CreateOrgRequest request);
    
    /**
     * 绑定知识库到组织
     */
    @PostMapping("/{orgId}/knowledge-bases/{kbId}")
    public void bindKnowledgeBase(
        @PathVariable String orgId,
        @PathVariable String kbId
    );
}
```

### EmbeddingService

嵌入模型服务。

```java
public class EmbeddingService {
    /**
     * 获取嵌入模型列表
     */
    List<EmbeddingModel> getEmbeddingModels();
    
    /**
     * 测试嵌入模型
     */
    EmbeddingTestResult testEmbedding(EmbeddingTestRequest request);
    
    /**
     * 生成文本嵌入
     */
    float[] generateEmbedding(String text);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/knowledge-organizations | GET | 获取知识组织列表 |
| /api/v1/knowledge-organizations | POST | 创建知识组织 |
| /api/v1/knowledge-organizations/{orgId} | GET | 获取知识组织详情 |
| /api/v1/knowledge-organizations/{orgId}/knowledge-bases | GET | 获取组织的关联知识库 |
| /api/v1/embedding/models | GET | 获取嵌入模型列表 |
| /api/v1/embedding/test | POST | 测试嵌入模型 |

## 知识组织模型

### KnowledgeOrgDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 组织ID |
| name | String | 组织名称 |
| type | OrgType | 组织类型 |
| knowledgeBases | List<KbBinding> | 关联的知识库 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-knowledge-platform</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private KnowledgeOrganizationService orgService;

// 创建知识组织
CreateOrgRequest request = new CreateOrgRequest();
request.setName("技术部门");
request.setType(OrgType.DEPARTMENT);
KnowledgeOrgDTO org = orgService.createOrganization(request);

// 绑定知识库
orgService.bindKnowledgeBase(org.getId(), "kb-001");

// 获取嵌入模型
EmbeddingService embeddingService = new EmbeddingService();
List<EmbeddingModel> models = embeddingService.getEmbeddingModels();
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| persistence.type | string | json | 持久化类型 |
| embedding.model | string | mock | 嵌入模型类型 |

## 许可证

Apache-2.0

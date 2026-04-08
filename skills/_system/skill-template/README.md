# skill-template

场景模板服务 - 提供场景模板管理、部署、依赖安装等功能。

## 功能特性

- **模板管理** - 管理场景模板
- **模板部署** - 部署模板到系统
- **依赖安装** - 自动安装模板依赖
- **模板融合** - 融合多个模板

## 核心接口

### SceneTemplateController

场景模板控制器。

```java
@RestController
@RequestMapping("/api/v1/templates")
public class SceneTemplateController {
    /**
     * 获取模板列表
     */
    @GetMapping
    public PageResult<SceneTemplateDTO> listTemplates();
    
    /**
     * 获取模板详情
     */
    @GetMapping("/{templateId}")
    public SceneTemplateDTO getTemplate(@PathVariable String templateId);
    
    /**
     * 部署模板
     */
    @PostMapping("/{templateId}/deploy")
    public DeployResultDTO deployTemplate(@PathVariable String templateId);
    
    /**
     * SSE方式部署模板
     */
    @GetMapping("/{templateId}/deploy/stream")
    public SseEmitter deployTemplateWithProgress(@PathVariable String templateId);
    
    /**
     * 安装模板依赖
     */
    @PostMapping("/{templateId}/install")
    public InstallResultDTO installTemplate(@PathVariable String templateId);
    
    /**
     * 检查依赖健康状态
     */
    @GetMapping("/{templateId}/dependencies/health")
    public List<DependencyStatusDTO> checkDependenciesHealth(@PathVariable String templateId);
    
    /**
     * 自动安装依赖
     */
    @PostMapping("/{templateId}/dependencies/auto-install")
    public AutoInstallResultDTO autoInstallDependencies(@PathVariable String templateId);
}
```

### FusionTemplateController

模板融合控制器。

```java
@RestController
@RequestMapping("/api/v1/fused-templates")
public class FusionTemplateController {
    /**
     * 获取融合模板列表
     */
    @GetMapping
    public List<FusedWorkflowTemplateDTO> list();
    
    /**
     * 融合模板
     */
    @PostMapping("/fuse")
    public FusedWorkflowTemplateDTO fuse(@RequestBody FusionRequestDTO request);
    
    /**
     * 预览融合结果
     */
    @PostMapping("/preview")
    public FusionPreviewDTO preview(@RequestBody FusionRequestDTO request);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/templates | GET | 获取模板列表 |
| /api/v1/templates/{templateId} | GET | 获取模板详情 |
| /api/v1/templates/{templateId}/deploy | POST | 部署模板 |
| /api/v1/templates/{templateId}/deploy/stream | GET | SSE方式部署模板 |
| /api/v1/templates/{templateId}/install | POST | 安装模板依赖 |
| /api/v1/templates/{templateId}/dependencies/health | GET | 检查依赖健康状态 |
| /api/v1/fused-templates | GET | 获取融合模板列表 |
| /api/v1/fused-templates/fuse | POST | 融合模板 |
| /api/v1/fused-templates/preview | POST | 预览融合结果 |

## 模板模型

### SceneTemplateDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 模板ID |
| name | String | 模板名称 |
| description | String | 模板描述 |
| version | String | 模板版本 |
| dependencies | List<Dependency> | 依赖列表 |
| config | SceneConfig | 场景配置 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-template</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private SceneTemplateService templateService;

// 获取模板列表
PageResult<SceneTemplateDTO> templates = templateService.listTemplates();

// 部署模板
DeployResultDTO result = templateService.deployTemplate("template-001");

// 检查依赖健康状态
List<DependencyStatusDTO> health = templateService.checkDependenciesHealth("template-001");

// 自动安装依赖
AutoInstallResultDTO installResult = templateService.autoInstallDependencies("template-001");

// 融合模板
FusionRequestDTO fusionRequest = new FusionRequestDTO();
fusionRequest.setTemplateIds(Arrays.asList("template-001", "template-002"));
FusedWorkflowTemplateDTO fused = templateService.fuse(fusionRequest);
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| TEMPLATE_PATH | string | ./templates | 模板文件路径 |

## 许可证

Apache-2.0

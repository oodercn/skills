# SDK 零配置安装任务说明

## 背景

当前 Ooder 平台的 Skill 安装流程存在以下问题：
1. 用户需要手动安装每个 Skill 及其依赖
2. UI Skill 与后端 Service Skill 没有自动关联
3. 场景创建和能力绑定是分离的操作

## 目标

实现**零配置完整安装**，用户只需一键即可完成：
- Skill 安装（含依赖）
- 场景创建
- 能力绑定
- UI 菜单注册

---

## SDK 接口变更 (v2.3)

### 新增接口

```java
// SkillPackageManager.java
/**
 * 安装 Skill 及其所有依赖（递归安装）
 * @param skillId Skill 标识
 * @param mode 安装模式 (PRODUCTION, DEVELOPMENT, TEST)
 * @return 安装结果，包含成功/失败的依赖列表
 */
CompletableFuture<InstallResultWithDependencies> installWithDependencies(String skillId, InstallRequest.InstallMode mode);
```

### 新增返回类型

```java
// InstallResultWithDependencies.java
public class InstallResultWithDependencies {
    private String skillId;
    private boolean success;
    private String status;  // installed, failed, partial
    private List<String> installedDependencies;  // 成功安装的依赖
    private List<String> failedDependencies;     // 安装失败的依赖
    private List<String> existingDependencies;   // 已存在的依赖
    private String error;
    private long timestamp;
    private long duration;
}
```

---

## 任务分配

### 任务 T1: 增强安装接口 ✅ 已完成

**负责模块**: skill-scene  
**优先级**: P0  
**工作量**: 2h

**修改内容**:
- `GitDiscoveryController.installSkill()` 调用 `SkillService.installSkill()`
- 返回安装结果和能力列表

**验收标准**:
```bash
# 安装 Skill 后能查询到已安装状态
curl -X POST http://localhost:8084/api/v1/discovery/install \
  -H "Content-Type: application/json" \
  -d '{"skillId":"skill-knowledge-base","source":"GITEE"}'
  
# 返回
{
  "code": 200,
  "data": {
    "skillId": "skill-knowledge-base",
    "status": "installed",
    "capabilities": [...]
  }
}
```

---

### 任务 T2: 依赖自动安装

**负责模块**: SDK/skills-framework  
**优先级**: P0  
**工作量**: 4h

**修改文件**:
- `SkillPackageManager.java` - 添加接口方法
- `SkillPackageManagerImpl.java` - 实现逻辑

**接口定义**:
```java
// SkillPackageManager.java
/**
 * 安装 Skill 及其所有依赖
 * @param skillId Skill 标识
 * @param mode 安装模式
 * @return 安装结果
 */
CompletableFuture<InstallResult> installWithDependencies(String skillId, InstallMode mode);

/**
 * 获取 Skill 的依赖列表
 * @param skillId Skill 标识
 * @return 依赖信息
 */
CompletableFuture<DependencyInfo> getDependencyInfo(String skillId);
```

**实现逻辑**:
```java
// SkillPackageManagerImpl.java
@Override
public CompletableFuture<InstallResult> installWithDependencies(String skillId, InstallMode mode) {
    return CompletableFuture.supplyAsync(() -> {
        // 1. 获取 Skill 元数据
        SkillManifest manifest = getManifest(skillId).join();
        if (manifest == null) {
            throw new SkillException(skillId, "Skill manifest not found");
        }
        
        // 2. 解析依赖
        List<String> dependencies = manifest.getDependencies();
        List<String> installedDeps = new ArrayList<>();
        List<String> failedDeps = new ArrayList<>();
        
        // 3. 递归安装依赖
        for (String dep : dependencies) {
            if (!isInstalled(dep).join()) {
                try {
                    InstallResult depResult = installWithDependencies(dep, mode).join();
                    if (depResult.isSuccess()) {
                        installedDeps.add(dep);
                    } else {
                        failedDeps.add(dep);
                    }
                } catch (Exception e) {
                    log.error("Failed to install dependency: {}", dep, e);
                    failedDeps.add(dep);
                }
            }
        }
        
        // 4. 如果有依赖安装失败，返回错误
        if (!failedDeps.isEmpty()) {
            InstallResult result = new InstallResult();
            result.setSuccess(false);
            result.setError("Failed to install dependencies: " + failedDeps);
            return result;
        }
        
        // 5. 安装主 Skill
        InstallRequest request = new InstallRequest();
        request.setSkillId(skillId);
        request.setMode(mode);
        
        return install(request).join();
    });
}
```

**验收标准**:
```bash
# 安装 UI Skill 时自动安装后端依赖
curl -X POST http://localhost:8084/api/v1/discovery/install \
  -H "Content-Type: application/json" \
  -d '{"skillId":"skill-knowledge-ui","source":"GITEE"}'

# 日志应显示:
# [installWithDependencies] Installing skill-knowledge-ui
# [installWithDependencies] Installing dependency: skill-knowledge-base
# [installWithDependencies] Skill skill-knowledge-base installed
# [installWithDependencies] Skill skill-knowledge-ui installed
```

---

### 任务 T3: 场景模板服务

**负责模块**: skill-scene  
**优先级**: P1  
**工作量**: 4h

**新建文件**:
- `service/SceneTemplateService.java`
- `model/SceneTemplate.java`
- `controller/SceneTemplateController.java`

**模板定义**:
```yaml
# templates/knowledge-qa.yaml
apiVersion: scene.ooder.net/v1
kind: SceneTemplate
metadata:
  id: knowledge-qa
  name: 知识问答场景
  description: 基于知识库的智能问答场景
  category: knowledge
  icon: ri-book-3-line
  
spec:
  skills:
    - id: skill-knowledge-base
      required: true
    - id: skill-knowledge-ui
      required: true
    - id: skill-rag
      required: false
    - id: skill-llm-assistant-ui
      required: false
      
  capabilities:
    - id: kb-management
      name: 知识库管理
    - id: kb-search
      name: 知识搜索
    - id: rag-retrieval
      name: RAG检索
      
  scene:
    type: knowledge
    config:
      knowledgeBase:
        autoCreate: true
        defaultName: "默认知识库"
      llm:
        provider: "mock"
        model: "mock-gpt-4"
```

**API 接口**:
```java
@RestController
@RequestMapping("/api/v1/templates")
public class SceneTemplateController {
    
    @GetMapping
    public ResultModel<List<SceneTemplate>> listTemplates();
    
    @GetMapping("/{id}")
    public ResultModel<SceneTemplate> getTemplate(@PathVariable String id);
    
    @PostMapping("/{id}/deploy")
    public ResultModel<DeployResultDTO> deployTemplate(@PathVariable String id);
}
```

**部署逻辑**:
```java
public DeployResultDTO deployTemplate(String templateId) {
    // 1. 加载模板
    SceneTemplate template = loadTemplate(templateId);
    
    // 2. 安装所有 Skills（含依赖）
    for (SkillRef skill : template.getSkills()) {
        skillPackageManager.installWithDependencies(skill.getId()).join();
    }
    
    // 3. 创建场景
    SceneDefinition scene = new SceneDefinition();
    scene.setSceneId(templateId + "-" + UUID.randomUUID().toString().substring(0, 8));
    scene.setName(template.getName());
    scene.setType(template.getScene().getType());
    sceneManager.create(scene).join();
    
    // 4. 绑定能力
    for (CapabilityRef cap : template.getCapabilities()) {
        sceneManager.addCapability(scene.getSceneId(), cap.toCapability()).join();
    }
    
    // 5. 返回结果
    DeployResultDTO result = new DeployResultDTO();
    result.setSceneId(scene.getSceneId());
    result.setTemplateId(templateId);
    result.setStatus("deployed");
    return result;
}
```

**验收标准**:
```bash
# 一键部署知识问答场景
curl -X POST http://localhost:8084/api/v1/templates/knowledge-qa/deploy

# 返回
{
  "code": 200,
  "data": {
    "sceneId": "knowledge-qa-a1b2c3d4",
    "templateId": "knowledge-qa",
    "status": "deployed",
    "skills": ["skill-knowledge-base", "skill-knowledge-ui"],
    "capabilities": ["kb-management", "kb-search"]
  }
}
```

---

### 任务 T4: UI Skills 装载

**负责模块**: scene-engine  
**优先级**: P1  
**工作量**: 4h

**新建文件**:
- `ui/NexusUiLoader.java`
- `ui/NexusUiRegistry.java`

**装载逻辑**:
```java
@Component
public class NexusUiLoader {
    
    @Autowired
    private SkillPackageManager skillPackageManager;
    
    private final Map<String, NexusUiConfig> uiRegistry = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        scanInstalledUiSkills();
    }
    
    public void scanInstalledUiSkills() {
        List<InstalledSkill> installed = skillPackageManager.listInstalled().join();
        
        for (InstalledSkill skill : installed) {
            SkillManifest manifest = skillPackageManager.getManifest(skill.getSkillId()).join();
            if (manifest != null && "nexus-ui".equals(manifest.getType())) {
                registerUiSkill(skill.getSkillId(), manifest);
            }
        }
    }
    
    private void registerUiSkill(String skillId, SkillManifest manifest) {
        NexusUiConfig config = parseUiConfig(manifest);
        uiRegistry.put(skillId, config);
        
        // 注册菜单
        if (config.getMenu() != null) {
            menuRegistry.register(config.getMenu());
        }
    }
    
    public List<NexusUiConfig> getInstalledUis() {
        return new ArrayList<>(uiRegistry.values());
    }
}
```

**验收标准**:
- UI Skill 安装后，菜单自动出现
- UI Skill 卸载后，菜单自动消失

---

### 任务 T5: 前端一键部署

**负责模块**: console-ui  
**优先级**: P2  
**工作量**: 2h

**修改文件**:
- `pages/capability-discovery.js`
- `pages/templates.js` (新建)

**前端代码**:
```javascript
// templates.js
const TemplateManager = {
    loadTemplates: function() {
        return ApiClient.get('/api/v1/templates');
    },
    
    deployTemplate: function(templateId) {
        return ApiClient.post(`/api/v1/templates/${templateId}/deploy`);
    },
    
    renderTemplateCard: function(template) {
        return `
            <div class="template-card" data-id="${template.id}">
                <div class="template-icon"><i class="${template.icon}"></i></div>
                <div class="template-info">
                    <h3>${template.name}</h3>
                    <p>${template.description}</p>
                </div>
                <button class="btn-deploy" onclick="TemplateManager.deploy('${template.id}')">
                    一键部署
                </button>
            </div>
        `;
    },
    
    deploy: function(templateId) {
        this.deployTemplate(templateId).then(function(result) {
            if (result.code === 200) {
                alert('部署成功！场景ID: ' + result.data.sceneId);
                window.location.href = '/console/pages/my-scenes.html';
            }
        });
    }
};
```

---

## 依赖关系

```
T1 (安装接口) ──→ T2 (依赖安装)
       │                │
       ↓                ↓
T3 (场景模板) ──→ T4 (UI装载)
       │                │
       └───────┬────────┘
               ↓
          T5 (前端)
```

## 时间线

| 阶段 | 任务 | 预计完成 |
|------|------|---------|
| 第1天 | T1 | ✅ 已完成 |
| 第1-2天 | T2 | SDK 团队 |
| 第2-3天 | T3 | skill-scene 团队 |
| 第3-4天 | T4 | scene-engine 团队 |
| 第4天 | T5 | 前端团队 |

## 验收清单

- [ ] 安装 Skill 时自动安装依赖
- [ ] 场景模板一键部署
- [ ] UI Skill 安装后菜单自动出现
- [ ] 前端一键部署按钮可用
- [ ] 知识问答场景完整部署测试通过

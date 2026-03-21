# 协作任务：增强场景配置与安装信息读取

## 一、任务概述

**提交人**: MVP Core 团队  
**提交时间**: 2026-03-21  
**优先级**: 高  
**涉及团队**: Skills 团队、SE SDK 团队  
**关联问题**: 招聘模块安装成功但无法激活、菜单不显示

---

## 二、问题背景

### 2.1 现象描述

在能力生命周期测试中发现以下问题：

1. **招聘模块安装成功，但激活时找不到场景模板**
2. **激活后菜单不显示在左侧导航**
3. **安装过程缺少数据完整性验证，数据不全也显示成功**

### 2.2 根本原因分析

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           问题根因分析                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  问题1: skill.yaml 配置不完整                                               │
│  ═══════════════════════════════                                            │
│  文件: skills/scenes/skill-recruitment-management/skill.yaml                │
│  缺失:                                                                      │
│    - spec.capability.category (能力分类)                                    │
│    - spec.roles (角色定义)                                                  │
│    - spec.activationSteps (激活步骤)                                        │
│    - spec.menus (菜单配置)                                                  │
│                                                                             │
│  问题2: 安装时未从技能包读取场景配置                                         │
│  ═══════════════════════════════════════                                    │
│  文件: InstallServiceImpl.java                                              │
│  缺失:                                                                      │
│    - 未读取 skill.yaml 中的场景配置                                         │
│    - 未验证场景模板完整性                                                   │
│    - 依赖安装失败不中断主流程                                               │
│                                                                             │
│  问题3: 场景模板与技能包分离                                                │
│  ═════════════════════════════════                                          │
│  当前状态:                                                                  │
│    - 场景模板在 src/main/resources/templates/ (应用级别)                    │
│    - 技能定义在 skills/scenes/xxx/skill.yaml (技能包级别)                   │
│  问题: 安装技能时无法获取场景模板                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、Skills 团队任务

### 3.1 增强 skill.yaml 配置

**文件位置**: `skills/scenes/skill-recruitment-management/skill.yaml`

**需要添加的配置**:

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-recruitment-management
  name: 招聘管理系统
  version: 1.0.0
  description: 企业招聘全流程管理系统
  type: scene-skill

spec:
  type: scene-skill
  
  # 【新增】能力分类配置
  capability:
    address: 0x08
    category: HR
    code: HR_RECRUITMENT
    operations: [recruit, interview, offer]
  
  # 【新增】场景类型配置
  scene:
    type: INTERACTIVE          # AUTO / TRIGGER / INTERACTIVE
    visibility: public         # public / internal
    participantMode: multi-user
  
  # 【新增】角色定义
  roles:
    - name: MANAGER
      description: 招聘经理，负责招聘流程管理和审批
      required: true
      minCount: 1
      maxCount: 1
      permissions: [manage, approve, view, config]
    - name: HR
      description: HR专员，执行招聘任务和候选人管理
      required: true
      minCount: 1
      maxCount: 10
      permissions: [execute, view, edit]
    - name: INTERVIEWER
      description: 面试官，参与面试评估
      required: false
      minCount: 0
      maxCount: 50
      permissions: [interview, view]
  
  # 【新增】激活步骤定义
  activationSteps:
    MANAGER:
      - stepId: confirm-participants
        name: 确认参与者
        description: 确认招聘团队角色分配，选择HR和面试官
        required: true
        skippable: false
        autoExecute: false
      - stepId: select-push-targets
        name: 选择推送目标
        description: 选择要推送的下属员工
        required: true
        skippable: false
        autoExecute: false
      - stepId: config-conditions
        name: 配置驱动条件
        description: 配置招聘流程触发规则和自动化条件
        required: true
        skippable: false
        autoExecute: false
      - stepId: get-key
        name: 获取KEY
        description: 获取访问安全数据的密钥
        required: true
        skippable: false
        autoExecute: false
      - stepId: confirm-activation
        name: 确认激活
        description: 确认激活招聘场景
        required: true
        skippable: false
        autoExecute: false
    
    HR:
      - stepId: confirm-task
        name: 确认任务
        description: 您有一个新的招聘任务，确认激活吗？
        required: true
        skippable: false
        autoExecute: false
      - stepId: config-private-capabilities
        name: 配置私有能力
        description: 选择要启用的私有能力
        required: false
        skippable: true
        autoExecute: false
        privateCapabilities:
          - resume-parse
          - interview-schedule
      - stepId: get-key
        name: 获取KEY
        description: 获取访问安全数据的密钥
        required: true
        skippable: false
        autoExecute: false
      - stepId: confirm-activation
        name: 确认激活
        description: 确认激活招聘场景
        required: true
        skippable: false
        autoExecute: false
    
    INTERVIEWER:
      - stepId: confirm-task
        name: 确认任务
        description: 您被邀请参与面试评估
        required: true
        skippable: false
        autoExecute: false
      - stepId: get-key
        name: 获取KEY
        description: 获取访问安全数据的密钥
        required: true
        skippable: false
        autoExecute: false
      - stepId: confirm-activation
        name: 确认激活
        description: 确认参与面试
        required: true
        skippable: false
        autoExecute: false
  
  # 【新增】菜单配置
  menus:
    MANAGER:
      - id: recruitment-overview
        name: 招聘概览
        icon: ri-dashboard-line
        url: /console/pages/recruitment/dashboard.html
        order: 1
        visible: true
      - id: recruitment-statistics
        name: 数据统计
        icon: ri-bar-chart-line
        url: /console/pages/recruitment/statistics.html
        order: 2
        visible: true
      - id: recruitment-candidates
        name: 候选人管理
        icon: ri-user-line
        url: /console/pages/recruitment/candidates.html
        order: 3
        visible: true
      - id: recruitment-approval
        name: 审批中心
        icon: ri-checkbox-line
        url: /console/pages/recruitment/approval.html
        order: 4
        visible: true
      - id: recruitment-settings
        name: 招聘设置
        icon: ri-settings-3-line
        url: /console/pages/recruitment/settings.html
        order: 5
        visible: true
    
    HR:
      - id: recruitment-overview
        name: 招聘概览
        icon: ri-dashboard-line
        url: /console/pages/recruitment/dashboard.html
        order: 1
        visible: true
      - id: recruitment-candidates
        name: 候选人管理
        icon: ri-user-line
        url: /console/pages/recruitment/candidates.html
        order: 2
        visible: true
      - id: interview-arrangement
        name: 面试安排
        icon: ri-calendar-line
        url: /console/pages/recruitment/interview.html
        order: 3
        visible: true
    
    INTERVIEWER:
      - id: my-interviews
        name: 我的面试
        icon: ri-calendar-check-line
        url: /console/pages/recruitment/my-interviews.html
        order: 1
        visible: true
      - id: candidate-evaluation
        name: 候选人评估
        icon: ri-file-list-3-line
        url: /console/pages/recruitment/evaluation.html
        order: 2
        visible: true
  
  # 【新增】私有能力
  privateCapabilities:
    - capId: resume-parse
      name: 简历解析
      description: AI自动解析简历内容，提取关键信息
      optional: true
      enabled: false
      skillId: skill-resume-parse
    - capId: interview-schedule
      name: 面试安排
      description: 自动安排面试时间，发送通知
      optional: true
      enabled: false
      skillId: skill-schedule
    - capId: offer-management
      name: Offer管理
      description: Offer生成、审批、发送流程管理
      optional: true
      enabled: false
      skillId: skill-offer
  
  # 已有配置保持不变
  dependencies:
    - id: skill-form-builder
      version: ">=1.0.0"
      required: true
      autoInstall: true
      description: "表单构建服务"
    - id: skill-storage-management-nexus-ui
      version: ">=1.0.0"
      required: true
      autoInstall: true
      description: "文件存储服务"
    - id: skill-llm-chat
      version: ">=1.0.0"
      required: false
      autoInstall: false
      description: "LLM智能对话"
  
  capabilities:
    - id: recruitment-dashboard
      name: 招聘看板
      description: 招聘状态可视化看板
      category: hr
      type: ATOMIC
    - id: recruitment-statistics
      name: 数据统计
      description: 招聘数据统计分析
      category: hr
      type: ATOMIC
    # ... 其他能力定义
```

### 3.2 其他场景技能同样需要增强

以下技能也需要按照相同格式增强配置：

| 技能ID | 文件路径 | 优先级 |
|--------|----------|--------|
| skill-approval-form | skills/scenes/skill-approval-form/skill.yaml | 高 |
| skill-collaboration | skills/scenes/skill-collaboration/skill.yaml | 高 |
| skill-business | skills/scenes/skill-business/skill.yaml | 中 |
| skill-knowledge-qa | skills/scenes/skill-knowledge-qa/skill.yaml | 中 |

---

## 四、SE SDK 团队任务

### 4.1 增强安装信息读取

**文件位置**: `InstallServiceImpl.java`

**需要添加的方法**:

```java
/**
 * 从技能包的 skill.yaml 读取场景配置
 */
private SceneTemplateConfig loadSceneConfigFromSkillPackage(String skillId) {
    try {
        // 1. 获取技能包路径
        String skillPath = skillPackageManager.getSkillPath(skillId);
        if (skillPath == null) {
            log.warn("[loadSceneConfigFromSkillPackage] Skill path not found: {}", skillId);
            return null;
        }
        
        // 2. 读取 skill.yaml
        File skillYaml = new File(skillPath, "skill.yaml");
        if (!skillYaml.exists()) {
            log.warn("[loadSceneConfigFromSkillPackage] skill.yaml not found: {}", skillYaml.getAbsolutePath());
            return null;
        }
        
        // 3. 解析场景配置
        Yaml yaml = new Yaml();
        try (InputStream is = new FileInputStream(skillYaml)) {
            Map<String, Object> data = yaml.load(is);
            Map<String, Object> spec = (Map<String, Object>) data.get("spec");
            
            if (spec == null) {
                return null;
            }
            
            SceneTemplateConfig config = new SceneTemplateConfig();
            config.setSkillId(skillId);
            
            // 读取 capability 配置
            Map<String, Object> capability = (Map<String, Object>) spec.get("capability");
            if (capability != null) {
                config.setCategory((String) capability.get("category"));
                config.setCapabilityCode((String) capability.get("code"));
            }
            
            // 读取 scene 配置
            Map<String, Object> scene = (Map<String, Object>) spec.get("scene");
            if (scene != null) {
                config.setSceneType((String) scene.get("type"));
                config.setVisibility((String) scene.get("visibility"));
                config.setParticipantMode((String) scene.get("participantMode"));
            }
            
            // 读取 roles 配置
            List<Map<String, Object>> roles = (List<Map<String, Object>>) spec.get("roles");
            if (roles != null) {
                config.setRoles(parseRoles(roles));
            }
            
            // 读取 activationSteps 配置
            Map<String, Object> activationSteps = (Map<String, Object>) spec.get("activationSteps");
            if (activationSteps != null) {
                config.setActivationSteps(parseActivationSteps(activationSteps));
            }
            
            // 读取 menus 配置
            Map<String, Object> menus = (Map<String, Object>) spec.get("menus");
            if (menus != null) {
                config.setMenus(parseMenus(menus));
            }
            
            // 读取 privateCapabilities 配置
            List<Map<String, Object>> privateCaps = (List<Map<String, Object>>) spec.get("privateCapabilities");
            if (privateCaps != null) {
                config.setPrivateCapabilities(parsePrivateCapabilities(privateCaps));
            }
            
            log.info("[loadSceneConfigFromSkillPackage] Loaded scene config for: {}", skillId);
            return config;
        }
        
    } catch (Exception e) {
        log.error("[loadSceneConfigFromSkillPackage] Failed to load scene config for {}: {}", skillId, e.getMessage());
        return null;
    }
}
```

### 4.2 增强安装验证

**修改 `executeInstall` 方法**:

```java
@Override
public CompletableFuture<InstallConfig> executeInstall(String installId) {
    return CompletableFuture.supplyAsync(() -> {
        InstallConfig config = installs.get(installId);
        
        // ... 现有代码 ...
        
        try {
            // 【新增】场景类型能力验证
            if ("SCENE".equals(config.getSkillForm())) {
                validateSceneConfig(config, capability);
            }
            
            // ... 现有安装逻辑 ...
            
        } catch (SceneValidationException e) {
            // 场景配置验证失败
            config.setStatus(InstallConfig.InstallStatus.FAILED);
            progress.setStatus(InstallConfig.InstallStatus.FAILED);
            progress.setMessage("场景配置验证失败: " + e.getMessage());
            log.error("[executeInstall] Scene validation failed: {}", e.getMessage());
            return config;
        }
        // ...
    });
}

/**
 * 验证场景配置完整性
 */
private void validateSceneConfig(InstallConfig config, Capability capability) {
    String skillId = capability.getId();
    
    // 1. 尝试从技能包加载场景配置
    SceneTemplateConfig sceneConfig = loadSceneConfigFromSkillPackage(skillId);
    
    // 2. 如果技能包没有配置，尝试从模板加载
    if (sceneConfig == null) {
        SceneTemplate template = sceneTemplateService.getTemplate(skillId);
        if (template == null) {
            throw new SceneValidationException(
                "场景配置缺失: 技能包中未定义场景配置，且未找到场景模板。" +
                "请在 skill.yaml 中添加 spec.roles、spec.activationSteps、spec.menus 配置，" +
                "或在 src/main/resources/templates/ 目录创建场景模板文件。"
            );
        }
        sceneConfig = convertTemplateToConfig(template);
    }
    
    // 3. 验证角色定义
    if (sceneConfig.getRoles() == null || sceneConfig.getRoles().isEmpty()) {
        throw new SceneValidationException(
            "场景缺少角色定义: 请在 skill.yaml 的 spec.roles 中定义场景角色"
        );
    }
    
    // 4. 验证必需角色
    boolean hasRequiredRole = sceneConfig.getRoles().stream()
        .anyMatch(role -> role.isRequired() && role.getMinCount() > 0);
    if (!hasRequiredRole) {
        throw new SceneValidationException(
            "场景缺少必需角色: 请至少定义一个 required=true 且 minCount>0 的角色"
        );
    }
    
    // 5. 验证激活步骤
    if (sceneConfig.getActivationSteps() == null || sceneConfig.getActivationSteps().isEmpty()) {
        throw new SceneValidationException(
            "场景缺少激活步骤: 请在 skill.yaml 的 spec.activationSteps 中定义激活流程"
        );
    }
    
    // 6. 验证必需角色的激活步骤
    for (RoleConfig role : sceneConfig.getRoles()) {
        if (role.isRequired()) {
            List<ActivationStepConfig> steps = sceneConfig.getActivationSteps().get(role.getName());
            if (steps == null || steps.isEmpty()) {
                throw new SceneValidationException(
                    "必需角色缺少激活步骤: 角色 " + role.getName() + " 需要定义激活步骤"
                );
            }
        }
    }
    
    // 7. 验证菜单配置
    if (sceneConfig.getMenus() == null || sceneConfig.getMenus().isEmpty()) {
        throw new SceneValidationException(
            "场景缺少菜单配置: 请在 skill.yaml 的 spec.menus 中定义菜单"
        );
    }
    
    // 8. 验证必需角色的菜单
    for (RoleConfig role : sceneConfig.getRoles()) {
        if (role.isRequired()) {
            List<MenuConfig> menus = sceneConfig.getMenus().get(role.getName());
            if (menus == null || menus.isEmpty()) {
                throw new SceneValidationException(
                    "必需角色缺少菜单: 角色 " + role.getName() + " 需要定义菜单"
                );
            }
        }
    }
    
    // 9. 保存场景配置到安装记录
    config.setSceneConfig(sceneConfig);
    
    log.info("[validateSceneConfig] Scene config validated for: {}", skillId);
}
```

### 4.3 新增异常类

```java
public class SceneValidationException extends RuntimeException {
    public SceneValidationException(String message) {
        super(message);
    }
    
    public SceneValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 4.4 新增配置模型类

```java
public class SceneTemplateConfig {
    private String skillId;
    private String category;
    private String capabilityCode;
    private String sceneType;
    private String visibility;
    private String participantMode;
    private List<RoleConfig> roles;
    private Map<String, List<ActivationStepConfig>> activationSteps;
    private Map<String, List<MenuConfig>> menus;
    private List<PrivateCapabilityConfig> privateCapabilities;
    
    // getters and setters
}

public class RoleConfig {
    private String name;
    private String description;
    private boolean required;
    private int minCount;
    private int maxCount;
    private List<String> permissions;
    
    // getters and setters
}

public class ActivationStepConfig {
    private String stepId;
    private String name;
    private String description;
    private boolean required;
    private boolean skippable;
    private boolean autoExecute;
    private List<String> privateCapabilities;
    
    // getters and setters
}

public class MenuConfig {
    private String id;
    private String name;
    private String icon;
    private String url;
    private int order;
    private boolean visible;
    private List<MenuConfig> children;
    
    // getters and setters
}

public class PrivateCapabilityConfig {
    private String capId;
    private String name;
    private String description;
    private boolean optional;
    private boolean enabled;
    private String skillId;
    
    // getters and setters
}
```

---

## 五、数据流设计

### 5.1 增强后的安装流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        增强后的安装流程                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  createInstall()                                                            │
│       │                                                                     │
│       ▼                                                                     │
│  executeInstall()                                                           │
│       │                                                                     │
│       ├── 1. 查找能力 ✓                                                     │
│       │                                                                     │
│       ├── 2. 【新增】验证场景配置完整性                                      │
│       │    │                                                                │
│       │    ├── 2.1 从技能包加载 skill.yaml                                  │
│       │    │                                                                │
│       │    ├── 2.2 解析 spec.roles / activationSteps / menus               │
│       │    │                                                                │
│       │    ├── 2.3 验证必需角色存在                                         │
│       │    │                                                                │
│       │    ├── 2.4 验证激活步骤完整                                         │
│       │    │                                                                │
│       │    ├── 2.5 验证菜单配置存在                                         │
│       │    │                                                                │
│       │    └── 2.6 验证失败 → 抛出异常，安装失败                             │
│       │                                                                     │
│       ├── 3. 安装依赖                                                       │
│       │    └── 依赖失败 → 记录错误，继续或中断（根据配置）                   │
│       │                                                                     │
│       ├── 4. 更新状态 → INSTALLED                                           │
│       │                                                                     │
│       └── 5. 返回结果                                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 配置优先级

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           配置加载优先级                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  优先级 1: 技能包内的 skill.yaml                                            │
│  ═══════════════════════════════                                            │
│  位置: skills/scenes/xxx/skill.yaml                                         │
│  优点: 配置与技能版本绑定，随技能升级                                        │
│                                                                             │
│  优先级 2: 应用级场景模板                                                    │
│  ═══════════════════════════                                                │
│  位置: src/main/resources/templates/xxx-scene.yaml                          │
│  用途: 企业自定义覆盖默认配置                                                │
│                                                                             │
│  加载逻辑:                                                                   │
│  if (skill.yaml 有场景配置) {                                               │
│      使用 skill.yaml 配置                                                   │
│  } else if (templates/ 有对应模板) {                                        │
│      使用模板配置                                                            │
│  } else {                                                                   │
│      抛出 SceneValidationException                                          │
│  }                                                                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、验证步骤

### 6.1 Skills 团队验证

1. 更新 `skill-recruitment-management/skill.yaml`
2. 运行技能包验证：
   ```bash
   cd skills/scenes/skill-recruitment-management
   # 验证 YAML 格式
   yamllint skill.yaml
   ```
3. 提交代码到仓库

### 6.2 SE SDK 团队验证

1. 更新 `InstallServiceImpl.java`
2. 运行单元测试：
   ```bash
   mvn test -Dtest=InstallServiceImplTest
   ```
3. 集成测试：
   - 安装招聘模块
   - 验证配置完整性检查生效
   - 验证激活流程正常

### 6.3 端到端验证

1. 启动 MVP 服务
2. 访问能力发现页面
3. 安装招聘模块
4. 验证：
   - 安装成功后状态正确
   - 激活向导正常显示
   - 激活后菜单出现在左侧导航

---

## 七、时间计划

| 阶段 | 任务 | 负责团队 | 预计时间 |
|------|------|----------|----------|
| 阶段1 | 增强 skill.yaml 配置 | Skills 团队 | 2 天 |
| 阶段2 | 增强安装验证逻辑 | SE SDK 团队 | 3 天 |
| 阶段3 | 集成测试 | 联合 | 1 天 |
| 阶段4 | 文档更新 | 联合 | 0.5 天 |

---

## 八、相关文档

- [skill.yaml 规范](file:///e:/github/ooder-skills/templates/skill.yaml)
- [场景模板规范](file:///e:/github/ooder-skills/mvp/src/main/resources/templates/recruitment-scene.yaml)
- [安装流程文档](file:///e:/github/ooder-skills/mvp/src/main/java/net/ooder/mvp/skill/scene/capability/install/InstallServiceImpl.java)

---

## 九、联系方式

- **Skills 团队**: 负责技能包配置增强
- **SE SDK 团队**: 负责安装流程增强
- **MVP Core 团队**: 协调与集成测试

---

**创建时间**: 2026-03-21  
**状态**: 待处理  
**文档版本**: 1.0

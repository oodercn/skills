# SDK 协作开发文档

## 一、项目结构

```
E:\github\ooder-sdk\              # SDK 源码
├── agent-sdk\                    # Agent SDK
│   ├── agent-sdk-core\           # SDK 核心
│   │   └── src\main\java\net\ooder\sdk\service\skill\
│   │       └── SkillService.java
│   └── skills-framework\         # Skills Framework
│       └── src\main\java\net\ooder\skills\
│           ├── api\              # 接口定义
│           ├── core\discovery\   # 发现器
│           │   └── LocalDiscoverer.java
│           └── core\impl\        # 实现
│               └── SkillPackageManagerImpl.java
└── scene-engine\                 # Scene Engine

E:\github\ooder-skills\skills\    # Skills 项目集合
├── skill-scene\                  # 场景管理 Skill
├── skill-capability\             # 能力管理 Skill
├── skill-llm-chat\               # LLM 对话 Skill
└── ...                           # 其他 Skills
```

---

## 二、待修复问题

### 2.1 SDK installWithDependencies 方法逻辑错误（紧急）

**优先级**：🔴 高

**错误信息**：
```
Skill manifest not found: skill-capability
```

**根本原因**：
`installWithDependencies` 方法先从 registry 获取 manifest，但技能还没安装时 registry 中没有数据。

**问题代码**（第 207 行）：
```java
SkillManifest manifest = getManifest(skillId).join();
if (manifest == null) {
    result.setError("Skill manifest not found: " + skillId);
    return result;
}
```

**修复方案**：

修改 `SkillPackageManagerImpl.installWithDependencies()` 方法，先 discover 获取 SkillPackage：

```java
@Override
public CompletableFuture<InstallResultWithDependencies> installWithDependencies(String skillId, InstallRequest.InstallMode mode) {
    return CompletableFuture.supplyAsync(() -> {
        long startTime = System.currentTimeMillis();
        InstallResultWithDependencies result = new InstallResultWithDependencies();
        result.setSkillId(skillId);
        
        log.info("[installWithDependencies] Installing skill: {}", skillId);
        
        try {
            // 1. 检查主 Skill 是否已安装
            if (isInstalled(skillId).join()) {
                result.setSuccess(true);
                result.setStatus("existing");
                result.addExistingDependency(skillId);
                result.setDuration(System.currentTimeMillis() - startTime);
                log.info("[installWithDependencies] Skill already installed: {}", skillId);
                return result;
            }
            
            // 2. 先 discover 获取 SkillPackage（包含 manifest）
            SkillPackage skillPackage = discover(skillId, DiscoveryMethod.LOCAL_FS).join();
            if (skillPackage == null) {
                result.setSuccess(false);
                result.setStatus("failed");
                result.setError("Skill package not found: " + skillId);
                result.setDuration(System.currentTimeMillis() - startTime);
                return result;
            }
            
            // 3. 从 SkillPackage 获取 manifest
            SkillManifest manifest = skillPackage.getManifest();
            
            // 4. 解析依赖
            List<SkillManifest.Dependency> dependencies = manifest != null ? manifest.getDependencies() : null;
            
            // 5. 递归安装依赖...
            
            // 6. 注册并安装主 Skill
            registry.register(skillPackage);
            
            result.setSuccess(true);
            result.setStatus("installed");
            return result;
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(e.getMessage());
            return result;
        }
    });
}
```

**文件位置**：`E:\github\ooder-sdk\agent-sdk\skills-framework\src\main\java\net\ooder\skills\core\impl\SkillPackageManagerImpl.java`

**修复状态**：⏳ 待修复

---

### 2.2 SDK LocalDiscoverer 不支持 src/main/resources 路径（紧急）

**优先级**：🔴 高

**错误信息**：
```
Skill package not found: skill-capability
```

**根本原因**：
`LocalDiscoverer.loadSkillPackage` 只在项目根目录查找 `skill.yaml`，但技能的 `skill.yaml` 在 `src/main/resources/` 目录。

**问题代码**（第 314-328 行）：
```java
private SkillPackage loadSkillPackage(Path skillDir) {
    for (String manifestFile : MANIFEST_FILES) {
        Path manifestPath = skillDir.resolve(manifestFile);  // 只查找根目录
        if (Files.exists(manifestPath)) {
            // ...
        }
    }
    return null;
}
```

**修复方案**：

添加子目录搜索支持：

```java
private static final String[] MANIFEST_SEARCH_PATHS = {
    "",                      // 项目根目录
    "src/main/resources/"    // Maven 标准路径
};

private SkillPackage loadSkillPackage(Path skillDir) {
    for (String searchPath : MANIFEST_SEARCH_PATHS) {
        for (String manifestFile : MANIFEST_FILES) {
            Path manifestPath = skillDir.resolve(searchPath).resolve(manifestFile);
            if (Files.exists(manifestPath)) {
                try {
                    String content = readFileContent(manifestPath);
                    boolean isYaml = manifestFile.endsWith(".yaml");
                    return parseSkillPackage(content, skillDir, isYaml);
                } catch (IOException e) {
                    log.warn("Failed to load skill manifest from {}: {}", manifestPath, e.getMessage());
                }
            }
        }
    }
    return null;
}
```

**文件位置**：`E:\github\ooder-sdk\agent-sdk\skills-framework\src\main\java\net\ooder\skills\core\discovery\LocalDiscoverer.java`

**修复状态**：⏳ 待修复

---

## 三、已修复问题

### 3.1 技能目录路径同步问题（已修复）

**优先级**：🔴 高

**错误信息**：
```
Skill manifest not found: skill-capability
```

**根本原因**：
1. `SkillPackageManagerImpl` 构造函数中创建 `LocalDiscoverer` 时使用默认路径 `~/.ooder/skills`
2. `setSkillRootPath()` 方法只更新了 `skillRootPath` 字段，没有同步更新内部 `LocalDiscoverer` 的路径
3. 导致即使配置了 `ooder.skills.path: ../skills`，实际查找仍在 `~/.ooder/skills` 目录

**修复方案**：

**文件**：`E:\github\ooder-sdk\agent-sdk\skills-framework\src\main\java\net\ooder\skills\core\impl\SkillPackageManagerImpl.java`

**修改后**：
```java
@Override
public void setSkillRootPath(String path) {
    this.skillRootPath = path;
    SkillDiscoverer localDiscoverer = discoverers.get(DiscoveryMethod.LOCAL_FS);
    if (localDiscoverer instanceof LocalDiscoverer) {
        ((LocalDiscoverer) localDiscoverer).setSkillsDirectory(path);
    }
}
```

**修复状态**：✅ 已修复（2026-03-07）

---

### 3.2 LocalDiscoverer 支持 YAML（已修复）

**问题**：`LocalDiscoverer` 只查找 `skill.json`，不支持 `skill.yaml`

**修复状态**：✅ 已修复（SDK 2.3 版本已支持 YAML）

---

## 四、配置规范

### 4.1 技能目录配置

**skill-scene 配置**：
```yaml
ooder:
  skills:
    path: ../skills
```

**SDK 配置注入**：
```java
@Value("${ooder.skills.path:./skills}")
private String skillRootPath;
```

### 4.2 配置属性名规范

| 属性名 | 用途 | 默认值 |
|--------|------|--------|
| `ooder.skills.path` | 技能目录路径 | `./skills` |
| `ooder.sdk.enabled` | SDK 启用开关 | `true` |
| `ooder.sdk.node-id` | 节点ID | `skill-scene` |

---

## 五、验证步骤

### 5.1 SDK 编译

```bash
cd E:\github\ooder-sdk\agent-sdk\skills-framework
mvn clean install -DskipTests
```

### 5.2 Skill 项目编译

```bash
cd E:\github\ooder-skills\skills\skill-scene
mvn clean compile
```

### 5.3 功能测试

1. 启动 skill-scene 服务：`mvn spring-boot:run`
2. 访问场景组详情页：`http://localhost:8084/console/pages/scene-group-detail.html?id=sg-xxx`
3. 测试依赖安装功能
4. 验证技能发现功能

---

## 六、Skill 配置文件规范

### 6.1 skill.yaml 结构

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-xxx
  name: 技能名称
  version: 1.0.0
  description: 技能描述
  author: ooder Team
  license: Apache-2.0
  type: service-skill

spec:
  type: service-skill
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
    mainClass: net.ooder.skill.xxx.XxxSkillApplication
  
  dependencies:
    - id: skill-yyy
      version: ">=1.0.0"
      required: true
      autoInstall: true
  
  capabilities:
    - id: capability-xxx
      name: 能力名称
      description: 能力描述
      category: orchestration
  
  endpoints:
    - path: /api/v1/xxx
      method: GET
      description: 接口描述
      capability: capability-xxx
```

### 6.2 必需字段

| 字段 | 说明 |
|------|------|
| `metadata.id` | Skill ID |
| `metadata.name` | Skill 名称 |
| `metadata.version` | 版本号 |
| `spec.runtime.mainClass` | 主类 |

---

## 七、开发规范

### 7.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| Skill ID | skill-{name} | skill-scene |
| Capability ID | capability-{name} | capability-register |
| Endpoint | /api/v1/{resource} | /api/v1/scenes |

### 7.2 版本规范

- 主版本.次版本.修订版本
- 例如：2.3.1
- 依赖版本使用语义化约束：`>=2.3.0`

### 7.3 目录结构

```
skill-xxx/
├── src/main/java/
│   └── net/ooder/skill/xxx/
│       ├── XxxSkillApplication.java
│       ├── controller/
│       ├── service/
│       └── dto/
├── src/main/resources/
│   ├── skill.yaml
│   ├── application.yml
│   └── static/
└── pom.xml
```

---

## 八、常见问题

### Q1: Skill manifest not found

**原因**：配置文件格式不支持或路径错误

**解决**：
1. 确认 `skill.yaml` 在项目根目录
2. 确认 YAML 格式正确
3. 检查 `ooder.skills.path` 配置是否正确

### Q2: 依赖安装失败

**原因**：依赖的 skill 未发布或版本不匹配

**解决**：
1. 检查 `dependencies` 配置
2. 确认依赖 skill 已编译安装
3. 检查版本约束

### Q3: MainClass 找不到

**原因**：主类路径配置错误

**解决**：
1. 确认 `mainClass` 配置正确
2. 确认类已编译
3. 检查包名是否正确

---

## 九、联系方式

- SDK 仓库：`E:\github\ooder-sdk`
- Skills 仓库：`E:\github\ooder-skills`
- 官方地址：https://gitee.com/ooderCN

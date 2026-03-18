# Skill 开发常见问题

## 1. 服务注册失败：xxx is not an interface

### 错误信息
```
java.lang.IllegalArgumentException: net.ooder.skill.capability.service.SkillIndexLoader is not an interface
```

### 原因
在 `skill.yaml` 的 `services` 配置中，`interface` 字段必须指向一个接口类型，而不是具体类。

### 错误示例
```yaml
services:
  - name: skillIndexLoader
    interface: net.ooder.skill.capability.service.SkillIndexLoader  # 错误：这是一个类
    implementation: net.ooder.skill.capability.service.SkillIndexLoader
    singleton: true
```

### 正确做法
```yaml
services:
  - name: capabilityService
    interface: net.ooder.skill.capability.service.CapabilityService  # 正确：这是一个接口
    implementation: net.ooder.skill.capability.service.CapabilityServiceImpl
    singleton: true
```

### 检查方法
确保 `interface` 指向的类使用 `interface` 关键字声明：
```java
// 正确：这是一个接口
public interface CapabilityService {
    // ...
}

// 错误：这是一个类，不能作为 interface 配置
@Service
public class SkillIndexLoader {
    // ...
}
```

---

## 2. 技能已安装：Skill already installed

### 错误信息
```
Skill already installed: skill-xxx
```

### 原因
1. 技能已经安装过，需要先卸载或清空安装数据
2. **plugins 目录中已存在 JAR 文件**：服务启动时会自动加载 plugins 目录中的 JAR 文件

### 解决方案

**方案一：清空 plugins 目录和安装数据（推荐）**
```bash
# 删除 plugins 目录中的 JAR 文件
rm -rf mvp/plugins/*.jar

# 删除已安装数据
rm -rf mvp/data/installed-skills/*
rm -f mvp/data/.installed
rm -f mvp/data/registry.properties
```

**方案二：通过 API 卸载**
```bash
curl -X DELETE http://localhost:8084/api/v1/skills/{skillId}
```

**重要提示**：安装向导安装技能时，plugins 目录必须为空，否则会报 "Skill already installed" 错误。

---

## 3. skill.yaml 未找到

### 错误信息
```
java.io.IOException: skill.yaml not found in: plugins/skill-xxx.jar
```

### 原因
1. `skill.yaml` 文件不在 `src/main/resources` 目录下
2. JAR 文件打包时未包含 `skill.yaml`
3. `skill.yaml` 文件名错误（如 `Skill.yaml`）

### 解决方案
确保 `skill.yaml` 在正确位置：
```
skill-xxx/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
│           └── skill.yaml  ← 必须在这里
```

---

## 4. 控制器类未找到：ClassNotFoundException

### 错误信息
```
java.lang.ClassNotFoundException: net.ooder.skill.xxx.controller.XxxController
```

### 原因
1. `skill.yaml` 中引用的控制器类不存在
2. 控制器类被删除但 `skill.yaml` 未更新
3. 类路径错误

### 解决方案
检查 `skill.yaml` 中的路由配置：
```yaml
routes:
  - path: /api/v1/xxx
    method: GET
    controller: net.ooder.skill.xxx.controller.XxxController  # 确保此类存在
    methodName: getXxx
```

---

## 5. 路由冲突：Ambiguous mapping

### 错误信息
```
java.lang.IllegalStateException: Ambiguous mapping. Cannot map 'xxxController' method
to {GET [/api/v1/xxx]}: There is already 'yyyController' bean method mapped.
```

### 原因
多个控制器映射了相同的 URL 路径。

### 解决方案
确保每个路由路径唯一：
```yaml
# skill-a 的 skill.yaml
routes:
  - path: /api/v1/a/xxx  # 使用模块前缀区分

# skill-b 的 skill.yaml
routes:
  - path: /api/v1/b/xxx  # 使用模块前缀区分
```

---

## 6. 控制器实例化失败：InstantiationException

### 错误信息
```
java.lang.InstantiationException: net.ooder.skill.xxx.controller.XxxController
Caused by: java.lang.NoSuchMethodException: XxxController.<init>()
```

### 原因
控制器没有无参构造函数，且无法通过 Spring 依赖注入创建实例。

### 解决方案

**方案一：添加无参构造函数**
```java
@RestController
public class XxxController extends BaseController {
    
    @Autowired
    private XxxService xxxService;
    
    // 添加无参构造函数
    public XxxController() {
    }
}
```

**方案二：确保 Spring 可以注入依赖**
```java
@RestController
public class XxxController {
    
    private final XxxService xxxService;
    
    @Autowired  // Spring 会自动注入
    public XxxController(XxxService xxxService) {
        this.xxxService = xxxService;
    }
}
```

---

## 7. 静态资源 404

### 错误信息
访问 `/console/skills/skill-xxx/pages/xxx.html` 返回 404

### 原因
1. `skill.yaml` 缺少 `ui` 配置
2. 静态资源目录结构不正确

### 解决方案

**添加 ui 配置**
```yaml
ui:
  type: html
  entry: index.html
  staticResources:
    - console/
```

**正确的目录结构**
```
skill-xxx/
├── src/
│   └── main/
│       └── resources/
│           ├── skill.yaml
│           └── static/
│               └── console/
│                   ├── pages/
│                   │   └── xxx.html
│                   └── js/
│                       └── pages/
│                           └── xxx.js
```

---

## 8. 菜单为空

### 错误信息
访问 `/api/v1/menu` 返回空数组

### 原因
1. 技能未安装
2. `skill.yaml` 缺少 `menu` 配置

### 解决方案

**添加 menu 配置**
```yaml
menu:
  - id: xxx-management
    name: XXX 管理
    url: xxx-management.html
    icon: ri-settings-line
    roles:
      - admin
      - developer
    sort: 1
```

---

## 9. 依赖缺失

### 错误信息
编译时找不到类，如 `ClassNotFoundException` 或 `NoClassDefFoundError`

### 原因
`pom.xml` 中缺少必要的依赖，或依赖 scope 设置错误。

### 解决方案

**基础依赖（保留）**
```xml
<dependencies>
    <!-- 公共模块 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-common</artifactId>
        <scope>provided</scope>
    </dependency>
    
    <!-- 场景引擎 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine</artifactId>
        <scope>provided</scope>
    </dependency>
    
    <!-- LLM SDK -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

**注意**：所有依赖都应使用 `provided` scope，避免打包到 JAR 中。

---

## 10. API 端点 404

### 错误信息
访问 API 端点返回 404

### 原因
1. 路由未在 `skill.yaml` 中配置
2. 控制器方法不存在
3. HTTP 方法不匹配

### 解决方案

**检查路由配置**
```yaml
routes:
  - path: /api/v1/xxx
    method: GET  # 确保 HTTP 方法正确
    controller: net.ooder.skill.xxx.controller.XxxController
    methodName: getXxx  # 确保方法存在
    produces: application/json
```

**检查控制器方法**
```java
@GetMapping  # 或 @RequestMapping
public ResultModel<Xxx> getXxx() {
    // ...
}
```

---

## 快速排查清单

| 问题 | 检查项 |
|------|--------|
| 服务注册失败 | `interface` 是否指向接口而非类 |
| 技能已安装 | 清空 `mvp/data/installed-skills` |
| skill.yaml 未找到 | 文件是否在 `src/main/resources` |
| ClassNotFoundException | 控制器类是否存在 |
| 路由冲突 | 路径是否唯一 |
| InstantiationException | 是否有无参构造函数 |
| 静态资源 404 | 是否有 `ui` 配置 |
| 菜单为空 | 是否有 `menu` 配置 |
| 依赖缺失 | `pom.xml` 是否有基础依赖 |
| API 404 | 路由配置是否正确 |

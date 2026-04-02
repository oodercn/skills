# Skill JAR结构与静态资源打包发布指南

**版本**: 3.0.1  
**创建日期**: 2026-04-02  
**目的**: 说明Skill JAR结构、静态资源打包、按需装载、发布流程

---

## 📦 一、Skill JAR结构规范

### 1.1 标准JAR结构

```
skill-xxx.jar
├── META-INF/
│   └── MANIFEST.MF                    # JAR清单
│
├── skill.yaml                         # Skill配置文件（必需）
│
├── com/                               # Java类文件
│   └── example/
│       ├── Controller.class
│       ├── Service.class
│       └── Lifecycle.class
│
├── static/                            # 静态资源目录（可选）
│   ├── index.html                     # 入口页面
│   ├── css/
│   │   ├── style.css
│   │   └── components.css
│   ├── js/
│   │   ├── app.js
│   │   └── utils.js
│   └── pages/
│       ├── page1.html
│       └── page2.html
│
├── templates/                         # 模板文件（可选）
│   └── email-template.html
│
└── lib/                               # 依赖JAR（可选）
    └── dependency.jar
```

### 1.2 Spring Boot Fat JAR结构

如果使用Spring Boot打包，结构如下：

```
skill-xxx.jar
├── BOOT-INF/
│   ├── classes/
│   │   ├── skill.yaml
│   │   ├── com/
│   │   │   └── example/
│   │   │       └── *.class
│   │   └── static/
│   │       ├── index.html
│   │       ├── css/
│   │       └── js/
│   └── lib/
│       └── dependency.jar
│
├── META-INF/
│   └── MANIFEST.MF
│
└── org/
    └── springframework/
        └── boot/
            └── loader/
                └── *.class
```

### 1.3 skill.yaml配置规范

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-example
  name: Example Skill
  version: 1.0.0
  description: An example skill
  author: Ooder Team
  type: SERVICE

spec:
  skillForm: PROVIDER
  type: service-skill
  
  lifecycle:
    startup: com.example.MyLifecycle
    shutdown: com.example.MyLifecycle
  
  routes:
    - path: /api/skill/example/hello
      method: GET
      controller: com.example.ExampleController
      methodName: hello
      produces: application/json
  
  services:
    - name: exampleService
      interface: com.example.ExampleService
      implementation: com.example.ExampleServiceImpl
      singleton: true
  
  ui:
    type: html                        # UI类型: html, vue, react
    entry: index.html                 # 入口文件
    staticResources:                  # 静态资源目录
      - css/
      - js/
      - pages/
    cdnDependencies:                  # CDN依赖
      - https://cdn.jsdelivr.net/npm/vue@3.0.0/dist/vue.global.js
      - https://cdn.jsdelivr.net/npm/axios@0.21.0/dist/axios.min.js
```

---

## 🔧 二、静态资源打包方式

### 2.1 方式1: 内嵌到JAR（推荐用于小型Skill）

#### 项目结构

```
skill-example/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── Controller.java
│   │   │       └── Service.java
│   │   └── resources/
│   │       ├── skill.yaml
│   │       └── static/              # 静态资源放在resources/static下
│   │           ├── index.html
│   │           ├── css/
│   │           │   └── style.css
│   │           └── js/
│   │               └── app.js
│   └── test/
│       └── java/
└── pom.xml
```

#### pom.xml配置

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-skills</artifactId>
        <version>0.7.3</version>
    </parent>
    
    <artifactId>skill-example</artifactId>
    <name>Skill Example</name>
    
    <dependencies>
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>skill-hotplug-starter</artifactId>
            <version>${project.version}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <!-- 标准JAR打包 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
            
            <!-- 包含依赖JAR -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>3.2.0</version>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                            <includeScope>runtime</includeScope>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
        
        <resources>
            <!-- 包含skill.yaml -->
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>skill.yaml</include>
                    <include>static/**</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

#### 打包结果

```bash
mvn clean package

# 生成的JAR结构:
skill-example-1.0.0.jar
├── META-INF/
│   └── MANIFEST.MF
├── skill.yaml
├── com/
│   └── example/
│       └── *.class
└── static/
    ├── index.html
    ├── css/
    │   └── style.css
    └── js/
        └── app.js
```

### 2.2 方式2: 页面保留在主工程（推荐用于大型应用）

#### 主工程结构

```
ooder-main/
├── src/main/resources/static/console/
│   ├── pages/
│   │   ├── capability-discovery.html    # 能力发现页面
│   │   ├── capability-management.html   # 能力管理页面
│   │   └── scene-management.html        # 场景管理页面
│   ├── css/
│   │   ├── nexus.css                    # 核心UI框架
│   │   └── components/
│   │       ├── toast.css
│   │       └── modal.css
│   └── js/
│       ├── nexus.js                     # 核心UI框架
│       ├── api-client.js                # API客户端
│       └── pages/
│           ├── capability-discovery.js
│           └── capability-management.js
```

#### Skill JAR结构（只包含API）

```
skill-capability-management.jar
├── skill.yaml
└── com/
    └── example/
        ├── DiscoveryController.class
        ├── CapabilityController.class
        └── Service.class
```

#### skill.yaml配置

```yaml
metadata:
  id: skill-capability-management
  name: Capability Management Skill
  version: 1.0.0

spec:
  routes:
    - path: /api/v1/discovery/methods
      method: GET
      controller: com.example.DiscoveryController
      methodName: getDiscoveryMethods
      produces: application/json
```

#### 页面访问

```
页面URL: /console/pages/capability-discovery.html
API URL: /api/v1/discovery/methods

页面通过API与Skill交互，Skill不需要包含页面文件。
```

---

## 📥 三、静态资源装载机制

### 3.1 安装时装载流程

```
┌─────────────────────────────────────────────────────────────────┐
│                     Skill安装流程                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 上传JAR文件                                                 │
│     └── POST /api/plugins/install                               │
│         └── MultipartFile file                                  │
│                                                                 │
│  2. 解析JAR文件                                                 │
│     └── SkillPackage.fromFile(file)                             │
│         ├── 读取skill.yaml                                      │
│         ├── 解析metadata                                        │
│         └── 解析ui配置                                          │
│                                                                 │
│  3. 创建类加载器                                                │
│     └── PluginClassLoader                                       │
│         ├── 加载Java类                                          │
│         └── 设置资源查找路径                                    │
│                                                                 │
│  4. 注册路由                                                    │
│     └── RouteRegistry.registerRoutes()                          │
│         └── 动态注册Spring MVC路由                              │
│                                                                 │
│  5. 注册服务                                                    │
│     └── ServiceRegistry.registerServices()                      │
│         └── 注册Spring Bean                                     │
│                                                                 │
│  6. 静态资源处理                                                │
│     └── 方式1: 从JAR中按需读取                                   │
│         ├── 请求: /skill/{skillId}/index.html                   │
│         └── 从JAR读取: static/index.html                        │
│     └── 方式2: 解压到临时目录                                    │
│         ├── 解压到: ./plugins/{skillId}/static/                 │
│         └── 映射URL: /skill/{skillId}/**                        │
│                                                                 │
│  7. 启动Skill                                                   │
│     └── lifecycle.onStart(context)                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 静态资源访问方式

#### 方式1: 从JAR中按需读取（推荐）

**实现原理**:

```java
/**
 * Skill资源处理器
 * 从JAR中按需读取静态资源
 */
@Controller
public class SkillResourceController {
    
    @Autowired
    private PluginManager pluginManager;
    
    @GetMapping("/skill/{skillId}/**")
    public ResponseEntity<Resource> getSkillResource(
            @PathVariable String skillId,
            HttpServletRequest request) {
        
        // 1. 获取Skill包
        PluginContext context = pluginManager.getPluginContext(skillId);
        if (context == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 2. 获取资源路径
        String path = extractPath(request, skillId);
        // 例如: /skill/skill-example/css/style.css → css/style.css
        
        // 3. 从JAR读取资源
        SkillPackage skillPackage = context.getSkillPackage();
        InputStream is = skillPackage.getResource("static/" + path);
        
        if (is == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 4. 返回资源
        Resource resource = new InputStreamResource(is);
        return ResponseEntity.ok()
                .contentType(getContentType(path))
                .body(resource);
    }
    
    private String extractPath(HttpServletRequest request, String skillId) {
        String requestPath = request.getRequestURI();
        String prefix = "/skill/" + skillId + "/";
        return requestPath.substring(prefix.length());
    }
    
    private MediaType getContentType(String path) {
        if (path.endsWith(".html")) return MediaType.TEXT_HTML;
        if (path.endsWith(".css")) return MediaType.TEXT_CSS;
        if (path.endsWith(".js")) return MediaType.APPLICATION_JAVASCRIPT;
        if (path.endsWith(".json")) return MediaType.APPLICATION_JSON;
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
```

**访问示例**:

```
Skill JAR: skill-example.jar
├── static/
│   ├── index.html
│   ├── css/
│   │   └── style.css
│   └── js/
│       └── app.js

访问URL:
- http://localhost:8080/skill/skill-example/index.html
- http://localhost:8080/skill/skill-example/css/style.css
- http://localhost:8080/skill/skill-example/js/app.js

处理流程:
1. 请求 /skill/skill-example/css/style.css
2. 提取skillId: skill-example
3. 提取资源路径: css/style.css
4. 从JAR读取: static/css/style.css
5. 返回资源内容
```

#### 方式2: 解压到临时目录

**实现原理**:

```java
/**
 * Skill安装时解压静态资源
 */
public class PluginManager {
    
    public PluginInstallResult installSkill(SkillPackage skillPackage) {
        String skillId = skillPackage.getMetadata().getId();
        
        // ... 其他安装步骤 ...
        
        // 解压静态资源
        if (hasStaticResources(skillPackage)) {
            extractStaticResources(skillPackage, skillId);
        }
        
        // ... 其他安装步骤 ...
    }
    
    private void extractStaticResources(SkillPackage skillPackage, String skillId) {
        File targetDir = new File("./plugins/" + skillId + "/static");
        targetDir.mkdirs();
        
        try (JarFile jarFile = new JarFile(skillPackage.getFile())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                
                if (name.startsWith("static/") && !entry.isDirectory()) {
                    File file = new File(targetDir, name.substring("static/".length()));
                    file.getParentFile().mkdirs();
                    
                    try (InputStream is = jarFile.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }
}
```

**WebMvcConfigurer配置**:

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射Skill静态资源
        registry.addResourceHandler("/skill/**")
                .addResourceLocations("file:./plugins/");
    }
}
```

**访问示例**:

```
解压目录: ./plugins/skill-example/static/
├── index.html
├── css/
│   └── style.css
└── js/
    └── app.js

访问URL:
- http://localhost:8080/skill/skill-example/static/index.html
- http://localhost:8080/skill/skill-example/static/css/style.css
```

### 3.3 按需装载的优势

| 特性 | 方式1: 从JAR读取 | 方式2: 解压到目录 |
|------|-----------------|------------------|
| **磁盘占用** | 小（只存JAR） | 大（JAR+解压文件） |
| **访问速度** | 中（每次从JAR读取） | 快（直接文件访问） |
| **内存占用** | 中（缓存资源） | 小 |
| **卸载清理** | 简单（删除JAR） | 复杂（删除JAR+目录） |
| **更新便捷** | 简单（替换JAR） | 复杂（替换JAR+重新解压） |
| **推荐场景** | 小型Skill | 大型Skill |

---

## 🚀 四、发布流程

### 4.1 开发阶段

```bash
# 1. 编译
mvn clean compile

# 2. 测试
mvn test

# 3. 打包
mvn package

# 4. 本地安装
mvn install

# 5. 本地测试
java -jar target/skill-example-1.0.0.jar
```

### 4.2 发布到Skills仓库

#### 方式1: 直接发布JAR（推荐）

```bash
# 1. 打包
mvn clean package

# 2. 复制到Skills仓库
cp target/skill-example-1.0.0.jar \
   e:/github/ooder-skills/skills/_system/skill-example/

# 3. 提交到Git
cd e:/github/ooder-skills
git add skills/_system/skill-example/skill-example-1.0.0.jar
git commit -m "Add skill-example 1.0.0"
git push origin main

# 4. 发布到Gitee
git push gitee main
```

#### 方式2: 发布到Maven仓库

```xml
<!-- pom.xml -->
<distributionManagement>
    <repository>
        <id>ooder-releases</id>
        <url>https://nexus.ooder.net/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>ooder-snapshots</id>
        <url>https://nexus.ooder.net/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

```bash
# 发布
mvn clean deploy
```

### 4.3 客户端安装流程

```
┌─────────────────────────────────────────────────────────────────┐
│                     客户端安装Skill流程                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 发现Skill                                                   │
│     ├── 从技能中心发现                                          │
│     ├── 从GitHub/Gitee发现                                      │
│     ├── 从本地文件发现                                          │
│     └── 从Maven仓库发现                                         │
│                                                                 │
│  2. 下载Skill JAR                                               │
│     ├── 从URL下载                                               │
│     ├── 从本地上传                                              │
│     └── 从Maven仓库下载                                         │
│                                                                 │
│  3. 安装Skill                                                   │
│     ├── POST /api/plugins/install                               │
│     ├── 解析skill.yaml                                          │
│     ├── 创建ClassLoader                                         │
│     ├── 注册路由和服务                                          │
│     └── 处理静态资源                                            │
│                                                                 │
│  4. 激活Skill                                                   │
│     ├── POST /api/plugins/{skillId}/activate                    │
│     ├── 配置参数                                                │
│     └── 启动服务                                                │
│                                                                 │
│  5. 访问Skill                                                   │
│     ├── 访问API: /api/skill/{skillId}/xxx                       │
│     └── 访问页面: /skill/{skillId}/index.html                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.4 客户端安装示例

#### 方式1: 通过API安装

```bash
# 从本地上传安装
curl -X POST http://localhost:8080/api/plugins/install \
  -F "file=@skill-example-1.0.0.jar"

# 从URL下载安装
curl -X POST http://localhost:8080/api/plugins/install \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://gitee.com/ooder/skills/raw/main/skills/_system/skill-example/skill-example-1.0.0.jar",
    "skillId": "skill-example"
  }'
```

#### 方式2: 通过页面安装

```
1. 访问能力发现页面: /console/pages/capability-discovery.html
2. 选择发现途径: GitHub/Gitee/技能中心/本地文件
3. 扫描可用的Skill
4. 点击"安装"按钮
5. 等待安装完成
6. 激活Skill
```

---

## 📋 五、最佳实践

### 5.1 静态资源打包建议

| Skill类型 | 推荐方式 | 原因 |
|---------|---------|------|
| **小型Skill**（<5MB） | 内嵌到JAR | 简单、一体化管理 |
| **大型Skill**（>5MB） | 页面保留在主工程 | 减小JAR体积、便于更新 |
| **业务场景Skill** | 页面保留在主工程 | 页面通用、API独立 |
| **驱动Skill** | 不需要页面 | 只提供API |

### 5.2 发布建议

1. **版本管理**: 使用语义化版本（1.0.0, 1.1.0, 2.0.0）
2. **文档完善**: 每个Skill必须有README.md和API文档
3. **测试覆盖**: 单元测试覆盖率>80%
4. **依赖管理**: 显式声明所有依赖，避免冲突
5. **安全审计**: 发布前进行安全扫描

### 5.3 安装建议

1. **来源验证**: 只从可信来源安装Skill
2. **签名验证**: 验证Skill的数字签名
3. **依赖检查**: 安装前检查依赖是否满足
4. **资源限制**: 限制Skill的资源使用（CPU、内存）
5. **权限控制**: 限制Skill的访问权限

---

## 📚 六、总结

### 6.1 关键要点

1. **JAR结构**: 标准JAR结构，skill.yaml放在根目录
2. **静态资源**: 可内嵌到JAR或保留在主工程
3. **按需装载**: 从JAR中按需读取，无需解压
4. **发布流程**: 打包JAR → 发布到仓库 → 客户端安装

### 6.2 推荐方案

**对于OS迁移到Skills**:
- **页面**: 保留在主工程（Skills主工程）
- **API**: 打包到Skill JAR
- **发布**: 发布JAR到Gitee
- **安装**: 客户端从Gitee下载安装

### 6.3 下一步行动

1. **制定Skill JAR结构规范**
2. **实现静态资源处理器**
3. **完善发布流程**
4. **编写安装指南**

---

**文档维护**: Ooder Team  
**最后更新**: 2026-04-02  
**下次审核**: 2026-04-09

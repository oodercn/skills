# SE SDK Git 发现能力返回空列表问题分析

## 问题现象

调用 `/api/v1/discovery/gitee` 端点时，SE SDK 返回空列表：

```json
{
  "code": 200,
  "status": "success",
  "data": {
    "capabilities": [],
    "total": 0,
    "source": "gitee",
    "repoUrl": "https://gitee.com/ooderCN/skills",
    "branch": "main"
  }
}
```

## 日志分析

```
2026-03-20 10:01:49.698 [ForkJoinPool.commonPool-worker-2] INFO  n.o.s.c.d.GitRepositoryDiscovererAdapter - Discovering skills from Git repository: null/null
2026-03-20 10:01:49.699 [http-nio-8084-exec-2] INFO  n.o.m.s.s.c.DiscoveryController - [discoverFromGitee] Discovered 0 skills from Gitee via SE SDK
```

**关键问题**: 日志显示 `null/null`，说明 `defaultOwner` 和 `defaultRepo` 没有被正确配置。

## 代码路径分析

### MVP 端调用链

```
DiscoveryController.discoverFromGitee()
  ↓
skillPackageManager.discoverAll(DiscoveryMethod.GITEE)
  ↓
GitRepositoryDiscovererAdapter.discover()  // SE SDK 内部
  ↓
日志: "Discovering skills from Git repository: null/null"
```

### MVP 配置 (SdkConfiguration.java)

```java
@Value("${ooder.gitee.owner:ooderCN}")
private String giteeOwner;

@Value("${ooder.gitee.skills-repo:skills}")
private String giteeSkillsRepo;

@Bean
public GiteeDiscoverer giteeDiscoverer() {
    GitDiscoveryConfig config = GitDiscoveryConfig.forGitee(giteeToken, giteeOwner, giteeSkillsRepo);
    return new GiteeDiscoverer(config);
}

@Bean
public SkillPackageManager skillPackageManager() {
    SkillPackageManagerImpl impl = new SkillPackageManagerImpl();
    impl.setSkillRootPath(skillRootPath);
    return impl;
}
```

### SE SDK 类结构

```
GitRepositoryDiscovererAdapter
├── defaultOwner: String      ← 未配置 (null)
├── defaultRepo: String       ← 未配置 (null)
├── defaultBranch: String     ← 未配置
├── githubToken: String       ← 未配置
├── giteeToken: String        ← 未配置
├── source: String            ← 未配置
└── repositoryConfigs: Map    ← 空
```

## 问题根因

**SE SDK 的 `SkillPackageManagerImpl.discoverAll(DiscoveryMethod.GITEE)` 内部创建的 `GitRepositoryDiscovererAdapter` 没有被正确初始化。**

可能的原因：
1. `SkillPackageManagerImpl` 内部创建 `GitRepositoryDiscovererAdapter` 时没有注入配置
2. `GitRepositoryDiscovererAdapter` 需要 setter 方法设置 `defaultOwner`, `defaultRepo`, `giteeToken` 等
3. MVP 创建的 `GiteeDiscoverer` Bean 没有被 `SkillPackageManagerImpl` 使用

## 需要SE SDK实现的功能

### 方案1: SkillPackageManager 注入 Git Discoverers

```java
// SkillPackageManagerImpl 需要支持注入 Git Discoverers
public class SkillPackageManagerImpl implements SkillPackageManager {
    
    private GitHubDiscoverer gitHubDiscoverer;
    private GiteeDiscoverer giteeDiscoverer;
    
    public void setGitHubDiscoverer(GitHubDiscoverer discoverer) {
        this.gitHubDiscoverer = discoverer;
    }
    
    public void setGiteeDiscoverer(GiteeDiscoverer discoverer) {
        this.giteeDiscoverer = discoverer;
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> discoverAll(DiscoveryMethod method) {
        switch (method) {
            case GITHUB:
                return gitHubDiscoverer != null ? gitHubDiscoverer.discover() : CompletableFuture.completedFuture(Collections.emptyList());
            case GITEE:
                return giteeDiscoverer != null ? giteeDiscoverer.discover() : CompletableFuture.completedFuture(Collections.emptyList());
            // ...
        }
    }
}
```

### 方案2: GitRepositoryDiscovererAdapter 自动配置

```java
// 需要提供配置注入方式
public class GitRepositoryDiscovererAdapter implements SkillDiscoverer {
    
    // 需要支持通过构造函数或 setter 注入配置
    public GitRepositoryDiscovererAdapter(GitDiscoveryConfig config) {
        this.defaultOwner = config.getOwner();
        this.defaultRepo = config.getRepo();
        this.defaultBranch = config.getBranch();
        this.giteeToken = config.getToken();
        this.source = config.getSource();
    }
}
```

### 方案3: Spring Boot Auto-Configuration

```java
// skills-framework-spring-boot-starter 需要提供自动配置
@Configuration
@ConditionalOnClass(SkillPackageManager.class)
@EnableConfigurationProperties(SkillsProperties.class)
public class SkillsAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public SkillPackageManager skillPackageManager(SkillsProperties props, 
                                                    GitHubDiscoverer gitHubDiscoverer,
                                                    GiteeDiscoverer giteeDiscoverer) {
        SkillPackageManagerImpl impl = new SkillPackageManagerImpl();
        impl.setSkillRootPath(props.getSkillRootPath());
        impl.setGitHubDiscoverer(gitHubDiscoverer);
        impl.setGiteeDiscoverer(giteeDiscoverer);
        return impl;
    }
}
```

## MVP 临时解决方案

在 SE SDK 修复之前，MVP 可以：

1. **启用 Mock 数据** (已实施)
   ```yaml
   ooder:
     mock:
       enabled: true
   ```

2. **使用 use-index-first** (已配置)
   ```yaml
   ooder:
     discovery:
       use-index-first: true
   ```

3. **直接注入 GiteeDiscoverer**
   ```java
   // DiscoveryController 可以直接使用 GiteeDiscoverer Bean
   @Autowired(required = false)
   private GiteeDiscoverer giteeDiscoverer;
   
   @PostMapping("/gitee")
   public ResultModel<GitDiscoveryResultDTO> discoverFromGitee(...) {
       if (giteeDiscoverer != null) {
           List<SkillPackage> packages = giteeDiscoverer.discover().get(60, TimeUnit.SECONDS);
           // ...
       }
   }
   ```

## 状态

- [x] 问题分析完成
- [ ] SE SDK 修复 `SkillPackageManagerImpl` 支持注入 Git Discoverers
- [ ] SE SDK 提供 Spring Boot Auto-Configuration
- [ ] MVP 验证修复

## 联系人

- MVP 团队: 需要确认临时解决方案
- SE SDK 团队: 需要实现上述方案之一

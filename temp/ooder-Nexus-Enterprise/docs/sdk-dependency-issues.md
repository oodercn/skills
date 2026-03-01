# SDK 2.3 依赖问题报告

## 报告日期
2026-02-25

## 问题概述
在尝试使用 SDK 2.3 版本时遇到严重的依赖解析问题，导致项目无法正常编译和运行。

---

## 一、发现的问题

### 1.1 Maven 仓库配置冲突

**问题描述**: 阿里云 Maven 镜像配置覆盖了 ooder 私有仓库，导致无法下载 SDK 2.3 依赖。

**现象**:
```
Downloading from aliyunmaven: https://maven.aliyun.com/repository/public/net/ooder/skill-org/2.3/skill-org-2.3.pom
[WARNING] The POM for net.ooder:skill-org:jar:2.3 is missing
```

**影响**: 即使 pom.xml 中配置了 ooder 仓库，settings.xml 中的镜像配置优先，导致所有请求都发送到阿里云仓库。

### 1.2 本地仓库缺少关键依赖

**检查本地仓库发现**:
- ✅ `agent-sdk-2.3.jar` - 存在
- ✅ `llm-sdk-2.3.jar` - 存在（不完整，只有 jar 没有 pom）
- ❌ `skill-org-2.3.jar` - 不存在
- ❌ `skill-vfs-2.3.jar` - 不存在
- ❌ `skill-msg-2.3.jar` - 不存在
- ❌ `skill-business-2.3.jar` - 不存在
- ❌ `scene-engine-2.3.jar` - 存在但不完整

### 1.3 SDK 2.3 缺少 Spring Boot 自动配置

**发现**: SDK 2.3 虽然提供了以下接口和类：
- `SkillLifecycleEventService` - 事件服务接口
- `SkillLifecycleObserver` - 观察者接口
- `SkillLifecycleEvent` - 事件类
- `EventBus` / `EventBusImpl` - 事件总线

**但缺少**:
- ❌ Spring Boot AutoConfiguration
- ❌ `spring.factories` 文件
- ❌ `SkillLifecycleEventService` 的实现类

**结果**: 应用层需要手动创建 Bean：
```java
@Bean
public SkillLifecycleEventService skillLifecycleEventService() {
    // 需要应用层自己实现
}
```

---

## 二、已尝试的解决方案

### 2.1 修改 settings.xml（临时解决）

```xml
<!-- 排除 ooder 仓库 from 镜像 -->
<mirror>
    <id>aliyunmaven</id>
    <mirrorOf>*,!ooder-releases,!ooder-snapshots</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
</mirror>

<!-- 添加 ooder 仓库认证 -->
<server>
    <id>ooder-releases</id>
    <username>IhyTdX</username>
    <password>58yfI0TpbzN6OWPZQgNdKfpKWERUsAN1z</password>
</server>
```

**结果**: settings.xml 被系统还原，修改无法持久化。

### 2.2 离线模式编译

```bash
mvn clean compile -o
```

**结果**: 失败，因为本地缺少 `skill-org-2.3.jar` 等关键依赖。

---

## 三、需要 SDK 团队协助的问题

### 3.1 依赖发布问题（高优先级）

**问题**: SDK 2.3 的以下依赖未正确发布到 Maven 仓库：

| 依赖 | 版本 | 状态 |
|------|------|------|
| skill-org | 2.3 | ❌ 缺失 |
| skill-vfs | 2.3 | ❌ 缺失 |
| skill-msg | 2.3 | ❌ 缺失 |
| skill-business | 2.3 | ❌ 缺失 |
| scene-engine | 2.3 | ⚠️ 不完整 |
| llm-sdk | 2.3 | ⚠️ 不完整（缺少 pom）|

**请求**:
1. 确认这些依赖是否已发布到 `nexus.ooder.cn`
2. 如果已发布，提供正确的仓库 URL 和认证信息
3. 如果未发布，提供预计发布时间

### 3.2 Spring Boot Starter 支持（中优先级）

**问题**: SDK 2.3 缺少 Spring Boot 自动配置，应用层需要手动实现 `SkillLifecycleEventService`。

**期望**:
```xml
<!-- 期望有这样的 starter -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-spring-boot-starter</artifactId>
    <version>2.3</version>
</dependency>
```

**功能**:
- 自动配置 `SkillLifecycleEventService` Bean
- 自动配置 `EventBus` Bean
- 提供 `application.yml` 配置项

### 3.3 文档和示例（中优先级）

**请求**:
1. 提供 SDK 2.3 的完整依赖列表
2. 提供 Maven/Gradle 配置示例
3. 提供 Spring Boot 集成示例
4. 提供事件监听使用的完整示例

---

## 四、临时解决方案

在 SDK 团队修复之前，建议：

### 方案 1: 使用 SDK 0.8.0 版本

本地仓库有完整的 0.8.0 版本依赖：
- ✅ agent-sdk-0.8.0
- ✅ skill-org-0.8.0
- ✅ skill-vfs-0.8.0
- ✅ skill-msg-0.8.0

**修改 pom.xml**:
```xml
<properties>
    <ooder.sdk.version>0.8.0</ooder.sdk.version>
    <ooder.skill.version>0.8.0</ooder.skill.version>
</properties>
```

### 方案 2: 手动下载并安装

如果 SDK 团队提供 jar 包，可以手动安装到本地仓库：

```bash
mvn install:install-file \
    -Dfile=skill-org-2.3.jar \
    -DgroupId=net.ooder \
    -DartifactId=skill-org \
    -Dversion=2.3 \
    -Dpackaging=jar
```

### 方案 3: 使用已实现的适配层

我们已经实现了临时的 `SkillLifecycleEventService` 适配层：

```java
@Configuration
public class SdkEventConfiguration {
    
    @Bean
    public EventBus eventBus() {
        return new EventBusImpl();
    }
    
    @Bean
    public SkillLifecycleEventService skillLifecycleEventService(EventBus eventBus) {
        return new SkillLifecycleEventServiceImpl(eventBus);
    }
}
```

**代码位置**: `net.ooder.nexus.config.SdkEventConfiguration`

---

## 五、联系方式

**报告方**: Nexus Enterprise 能力管理团队
**紧急程度**: 高（阻塞开发进度）
**期望响应时间**: 24 小时内

---

## 附录

### A. 完整的依赖树

```
net.ooder:independent-nexus:jar:2.0
├── net.ooder:agent-sdk:jar:2.3 ✅
├── net.ooder:scene-engine:jar:2.3 ⚠️
├── net.ooder:llm-sdk:jar:2.3 ⚠️
├── net.ooder:skill-org:jar:2.3 ❌
├── net.ooder:skill-vfs:jar:2.3 ❌
├── net.ooder:skill-msg:jar:2.3 ❌
└── net.ooder:skill-business:jar:2.3 ❌
```

### B. 本地仓库路径

```
D:\maven\.m2\repository\net\ooder\
```

### C. 相关代码

- 事件配置: `src/main/java/net/ooder/nexus/config/SdkEventConfiguration.java`
- SDK 原生观察者: `src/main/java/net/ooder/nexus/observer/SdkNativeSkillObserver.java`
- 测试控制器: `src/main/java/net/ooder/nexus/controller/SdkNativeEventTestController.java`

---

*报告生成时间: 2026-02-25*

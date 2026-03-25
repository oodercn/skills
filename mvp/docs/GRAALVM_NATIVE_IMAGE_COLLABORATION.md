# GraalVM Native Image 兼容性协作说明

## 背景

mvp-core 项目计划支持 GraalVM Native Image 编译，以实现：
- 启动时间从 10-15 秒降至 0.1-0.5 秒
- 内存占用从 200-500MB 降至 50-100MB
- 更快的部署和响应速度

## 问题根源

### 1. @Configuration 注解问题

**问题描述：**
Spring Boot 3.x 的 AOT 编译要求所有 `@Configuration` 类设置 `proxyBeanMethods = false`，否则会触发 CGLIB 增强，导致 Native Image 编译失败。

**错误信息：**
```
Could not enhance configuration class [xxx]. 
Consider declaring @Configuration(proxyBeanMethods=false)
```

**影响范围：**
- `net.ooder.skill.common.config.FreeMarkerConfig` (skill-common SDK)
- 其他 SDK 中的 `@Configuration` 类

**修复方案：**
将所有 `@Configuration` 注解改为 `@Configuration(proxyBeanMethods = false)`

**示例：**
```java
// 修改前
@Configuration
public class FreeMarkerConfig {
    // ...
}

// 修改后
@Configuration(proxyBeanMethods = false)
public class FreeMarkerConfig {
    // ...
}
```

### 2. 需要修改的 SDK 列表

| SDK 名称 | 需要修改的类 | 优先级 |
|----------|-------------|--------|
| skill-common | `net.ooder.skill.common.config.FreeMarkerConfig` | 高 |
| skill-common | 其他 `@Configuration` 类 | 高 |
| skill-capability | 所有 `@Configuration` 类 | 中 |
| skill-llm | 所有 `@Configuration` 类 | 中 |
| skill-org | 所有 `@Configuration` 类 | 中 |
| skill-hotplug | 所有 `@Configuration` 类 | 中 |

### 3. Native Image 编译环境要求

| 资源 | 最低要求 | 推荐配置 |
|------|----------|----------|
| 内存 | 16GB | 32GB+ |
| 磁盘空间 | 20GB | 50GB+ |
| CPU | 4核 | 8核+ |
| 构建时间 | 5-15分钟 | - |

### 4. 反射配置要求

如果 SDK 中使用了以下技术，需要提供 GraalVM 反射配置：

- 动态代理 (Proxy.newProxyInstance)
- 反射调用 (Class.forName, Method.invoke)
- 动态脚本执行 (MVEL, Groovy 等)
- SPI 服务加载 (ServiceLoader)

**配置文件位置：**
```
src/main/resources/META-INF/native-image/
├── reflect-config.json    # 反射配置
├── proxy-config.json      # 动态代理配置
├── resource-config.json   # 资源文件配置
└── native-image.properties # Native Image 属性
```

## 临时解决方案

在 SDK 修复之前，mvp-core 项目已采取以下临时措施：

1. 排除问题配置类：
```java
@ComponentScan(
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = net.ooder.skill.common.config.FreeMarkerConfig.class
        )
    }
)
```

2. 添加反射配置文件

## 请 SDK 团队协助

1. **紧急**：将所有 `@Configuration` 注解改为 `@Configuration(proxyBeanMethods = false)`

2. **重要**：检查并添加必要的 GraalVM 反射配置

3. **建议**：在 SDK 中添加 Native Image 测试用例

## 验证方法

修改后，请在 x64 Native Tools Command Prompt 中运行：

```cmd
mvn clean package -Pnative -DskipTests
```

## 联系方式

如有问题，请联系 mvp-core 团队。

---

**文档路径：** `e:\github\ooder-skills\mvp\docs\GRAALVM_NATIVE_IMAGE_COLLABORATION.md`

**日期：** 2026-03-24

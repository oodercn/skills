# SPI 化改造实施指南

## 文档信息
- **版本**: 1.0.0
- **日期**: 2026-04-10
- **状态**: 实施中

---

## 一、已完成工作

### 1.1 SPI 接口定义（已完成 ✅）

已在 `ooder-spi-core` 中创建以下接口：

#### 数据库层
- [DataSourceProvider.java](file:///e:/github/ooder-skills/skills/_base/ooder-spi-core/src/main/java/net/ooder/spi/database/DataSourceProvider.java)
- [DatabaseConfig.java](file:///e:/github/ooder-skills/skills/_base/ooder-spi-core/src/main/java/net/ooder/spi/database/DatabaseConfig.java)

#### 文档解析层
- [DocumentParser.java](file:///e:/github/ooder-skills/skills/_base/ooder-spi-core/src/main/java/net/ooder/spi/document/DocumentParser.java)
- [ParseResult.java](file:///e:/github/ooder-skills/skills/_base/ooder-spi-core/src/main/java/net/ooder/spi/document/ParseResult.java)

#### 向量存储层
- [VectorStoreProvider.java](file:///e:/github/ooder-skills/skills/_base/ooder-spi-core/src/main/java/net/ooder/spi/vector/VectorStoreProvider.java)
- [VectorData.java](file:///e:/github/ooder-skills/skills/_base/ooder-spi-core/src/main/java/net/ooder/spi/vector/VectorData.java)
- [SearchResult.java](file:///e:/github/ooder-skills/skills/_base/ooder-spi-core/src/main/java/net/ooder/spi/vector/SearchResult.java)
- [VectorStoreConfig.java](file:///e:/github/ooder-skills/skills/_base/ooder-spi-core/src/main/java/net/ooder/spi/vector/VectorStoreConfig.java)

#### SpiServices 更新
- [SpiServices.java](file:///e:/github/ooder-skills/skills/_base/ooder-spi-core/src/main/java/net/ooder/spi/facade/SpiServices.java) - 已更新支持新的 SPI

### 1.2 SQLite 驱动开发（已完成 ✅）

已创建 `skill-sqlite-driver` 项目：

**项目路径**: `e:\github\ooder-skills\skills\_drivers\database\skill-sqlite-driver\`

**文件清单**:
- `pom.xml` - Maven 配置
- `skill.yaml` - Skill 配置
- `SQLiteDataSourceProvider.java` - SPI 实现
- `SQLiteDataSourceAutoConfiguration.java` - Spring 自动配置
- `README.md` - 使用文档

### 1.3 Markdown 解析器开发（已完成 ✅）

已创建 `skill-markdown-parser` 项目：

**项目路径**: `e:\github\ooder-skills\skills\_drivers\document\skill-markdown-parser\`

**文件清单**:
- `pom.xml` - Maven 配置
- `skill.yaml` - Skill 配置
- `MarkdownDocumentParser.java` - SPI 实现
- `MarkdownDocumentParserAutoConfiguration.java` - Spring 自动配置
- `README.md` - 使用文档

**支持的文件类型**:
- MIME 类型: `text/markdown`, `text/x-markdown`, `text/plain`, `application/markdown`
- 文件扩展名: `.md`, `.markdown`, `.txt`, `.text`

**元数据提取**:
- 文档标题（从一级标题提取）
- 行数、字数、字符数统计
- 标题数量、代码块数量、链接数量、图片数量
- YAML 前置元数据检测

### 1.4 本地向量存储开发（已完成 ✅）

已创建 `skill-local-vector-store` 项目：

**项目路径**: `e:\github\ooder-skills\skills\_drivers\vector\skill-local-vector-store\`

**文件清单**:
- `pom.xml` - Maven 配置
- `skill.yaml` - Skill 配置
- `LocalVectorStoreProvider.java` - SPI 实现
- `LocalVectorStoreAutoConfiguration.java` - Spring 自动配置
- `README.md` - 使用文档

**功能特性**:
- 基于 SQLite 的本地向量存储
- 余弦相似度搜索
- 向量元数据存储和过滤
- 批量向量操作
- 向量缓存机制
- 健康检查支持

---

## 二、待实施工作

### 2.1 在 ApexOS 主项目中创建 SpiServiceLocator

**目标文件**: `e:\apex\apexos\src\main\java\net\ooder\os\spi\SpiServiceLocator.java`

**代码内容**:

```java
package net.ooder.os.spi;

import net.ooder.spi.database.DataSourceProvider;
import net.ooder.spi.document.DocumentParser;
import net.ooder.spi.vector.VectorStoreProvider;
import net.ooder.spi.facade.SpiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * SPI 服务获取工具
 */
@Component
public class SpiServiceLocator {
    
    private static final Logger logger = LoggerFactory.getLogger(SpiServiceLocator.class);
    
    /**
     * 获取数据源提供者
     */
    public DataSourceProvider getDataSourceProvider(String dbType) {
        DataSourceProvider provider = SpiServices.getDataSourceProvider();
        
        if (provider != null && provider.getDatabaseType().equals(dbType)) {
            return provider;
        }
        
        provider = ServiceLoader.load(DataSourceProvider.class)
            .stream()
            .map(ServiceLoader.Provider::get)
            .filter(p -> p.getDatabaseType().equals(dbType))
            .findFirst()
            .orElse(null);
        
        if (provider == null) {
            throw new SpiServiceNotFoundException("DataSourceProvider", dbType);
        }
        
        return provider;
    }
    
    /**
     * 获取默认数据源提供者
     */
    public DataSourceProvider getDefaultDataSourceProvider() {
        DataSourceProvider provider = SpiServices.getDataSourceProvider();
        
        if (provider != null) {
            return provider;
        }
        
        provider = ServiceLoader.load(DataSourceProvider.class)
            .stream()
            .map(ServiceLoader.Provider::get)
            .findFirst()
            .orElse(null);
        
        if (provider == null) {
            throw new SpiServiceNotFoundException("DataSourceProvider", "default");
        }
        
        return provider;
    }
    
    /**
     * 获取文档解析器
     */
    public DocumentParser getDocumentParser(String mimeType) {
        DocumentParser parser = SpiServices.getDocumentParser(mimeType);
        
        if (parser != null) {
            return parser;
        }
        
        parser = ServiceLoader.load(DocumentParser.class)
            .stream()
            .map(ServiceLoader.Provider::get)
            .filter(p -> p.supports(mimeType))
            .findFirst()
            .orElse(null);
        
        if (parser == null) {
            throw new SpiServiceNotFoundException("DocumentParser", mimeType);
        }
        
        return parser;
    }
    
    /**
     * 获取向量存储提供者
     */
    public VectorStoreProvider getVectorStoreProvider(String providerType) {
        VectorStoreProvider provider = SpiServices.getVectorStoreProvider();
        
        if (provider != null && provider.getProviderType().equals(providerType)) {
            return provider;
        }
        
        provider = ServiceLoader.load(VectorStoreProvider.class)
            .stream()
            .map(ServiceLoader.Provider::get)
            .filter(p -> p.getProviderType().equals(providerType))
            .findFirst()
            .orElse(null);
        
        if (provider == null) {
            throw new SpiServiceNotFoundException("VectorStoreProvider", providerType);
        }
        
        return provider;
    }
    
    /**
     * 获取所有可用的数据源类型
     */
    public List<String> getAvailableDataSourceTypes() {
        return ServiceLoader.load(DataSourceProvider.class)
            .stream()
            .map(ServiceLoader.Provider::get)
            .map(DataSourceProvider::getDatabaseType)
            .collect(Collectors.toList());
    }
}
```

**异常类**: `e:\apex\apexos\src\main\java\net\ooder\os\spi\SpiServiceNotFoundException.java`

```java
package net.ooder.os.spi;

/**
 * SPI 服务未找到异常
 */
public class SpiServiceNotFoundException extends RuntimeException {
    
    private final String serviceType;
    private final String serviceName;
    
    public SpiServiceNotFoundException(String serviceType, String serviceName) {
        super(String.format("SPI service not found: type=%s, name=%s", 
            serviceType, serviceName));
        this.serviceType = serviceType;
        this.serviceName = serviceName;
    }
    
    public String getServiceType() {
        return serviceType;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    /**
     * 提供友好的错误提示，引导用户安装对应的 Skill
     */
    public String getSuggestion() {
        String skillSuggestion = getSkillSuggestion();
        return String.format("请安装对应的 Skill 以支持 %s 功能。建议安装: %s。", 
            serviceName, skillSuggestion);
    }
    
    private String getSkillSuggestion() {
        switch (serviceType) {
            case "DataSourceProvider":
                return "skill-sqlite-driver";
            case "DocumentParser":
                return "skill-markdown-parser";
            case "VectorStoreProvider":
                return "skill-local-vector-store";
            default:
                return "对应的 Skill Driver";
        }
    }
}
```

### 2.2 改造 ApexOS pom.xml

**移除 sqlite-jdbc 直接依赖**:

```xml
<!-- 删除以下依赖 -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.49.1.0</version>
</dependency>
```

**添加 SPI 核心依赖**:

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-spi-core</artifactId>
    <version>3.0.2</version>
</dependency>
```

**添加默认 Skill 依赖**:

```xml
<!-- 默认数据库驱动 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-sqlite-driver</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2.3 改造数据库连接获取逻辑

**改造前**（在需要数据库连接的地方）:

```java
// 错误方式：直接创建数据源
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:sqlite:apexos.db");
DataSource dataSource = new HikariDataSource(config);
```

**改造后**:

```java
// 正确方式：通过 SPI 获取数据源
@Autowired
private SpiServiceLocator spiServiceLocator;

public DataSource getDataSource() {
    DataSourceProvider provider = spiServiceLocator.getDefaultDataSourceProvider();
    
    DatabaseConfig config = DatabaseConfig.builder()
        .databaseName("apexos")
        .dataDir(System.getProperty("user.home") + "/.apexos/data")
        .poolConfig("max-size", 5)
        .build();
    
    return provider.createDataSource(config);
}
```

### 2.4 创建 SPI Controller（前端能力检测）

**目标文件**: `e:\apex\apexos\src\main\java\net\ooder\os\controller\SpiController.java`

```java
package net.ooder.os.controller;

import net.ooder.os.spi.SpiServiceLocator;
import net.ooder.os.model.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SPI 服务能力检测接口
 */
@RestController
@RequestMapping("/api/v1/spi")
@CrossOrigin(origins = "*")
public class SpiController {
    
    @Autowired
    private SpiServiceLocator spiServiceLocator;
    
    /**
     * 获取可用的数据库类型
     */
    @GetMapping("/datasource/types")
    public ResultModel<List<String>> getAvailableDataSources() {
        List<String> types = spiServiceLocator.getAvailableDataSourceTypes();
        return ResultModel.success(types);
    }
    
    /**
     * 获取可用的文档解析器
     */
    @GetMapping("/document/parsers")
    public ResultModel<List<String>> getAvailableParsers() {
        List<String> parsers = spiServiceLocator.getAvailableDocumentParsers()
            .stream()
            .map(p -> p.getParserName())
            .collect(java.util.stream.Collectors.toList());
        return ResultModel.success(parsers);
    }
    
    /**
     * 检查是否支持某功能
     */
    @GetMapping("/features/{feature}")
    public ResultModel<Boolean> isFeatureAvailable(@PathVariable String feature) {
        boolean available = false;
        
        if (feature.startsWith("database:")) {
            String dbType = feature.substring("database:".length());
            available = spiServiceLocator.isDataSourceAvailable(dbType);
        } else if (feature.startsWith("document:")) {
            String mimeType = feature.substring("document:".length());
            available = spiServiceLocator.isDocumentParserAvailable(mimeType);
        } else if (feature.startsWith("vector:")) {
            String providerType = feature.substring("vector:".length());
            available = spiServiceLocator.isVectorStoreAvailable(providerType);
        }
        
        return ResultModel.success(available);
    }
}
```

---

## 三、后续开发任务

### 3.1 开发 skill-markdown-parser（优先级 P0）

**项目路径**: `e:\github\ooder-skills\skills\_drivers\document\skill-markdown-parser\`

**功能**:
- 解析 Markdown 文件
- 解析纯文本文件
- 支持 MIME 类型: `text/markdown`, `text/plain`

### 3.2 开发 skill-local-vector-store（优先级 P1）

**项目路径**: `e:\github\ooder-skills\skills\_drivers\vector\skill-local-vector-store\`

**功能**:
- 基于 SQLite 的本地向量存储
- 支持向量相似度搜索
- 支持元数据过滤

### 3.3 移除 BPM 工作流相关代码（优先级 P1）

**需要删除的文件**:
- `e:\apex\apexos\src\main\resources\static\console\pages\bpm-workflow.html`
- `e:\apex\apexos\src\main\resources\static\console\js\bpm-workflow.js`
- `e:\apex\apexos\src\main\resources\static\console\css\bpm-workflow.css`

---

## 四、验证清单

### 4.1 功能验证

- [ ] 通过 SPI 获取 SQLite 数据源
- [ ] 数据库 CRUD 操作正常
- [ ] SPI 服务未找到时抛出友好异常
- [ ] 前端能力检测接口正常工作

### 4.2 性能验证

- [ ] 打包大小 ≤ 80 MB
- [ ] 启动时间 ≤ 15 秒
- [ ] 内存占用 ≤ 512 MB

### 4.3 代码验证

- [ ] 主项目无 sqlite-jdbc 直接依赖
- [ ] 所有数据库操作通过 SPI
- [ ] 异常处理完善
- [ ] 单元测试通过

---

## 五、部署说明

### 5.1 打包 skill-sqlite-driver

```bash
cd e:\github\ooder-skills\skills\_drivers\database\skill-sqlite-driver
mvn clean package -DskipTests
```

### 5.2 安装到本地仓库

```bash
mvn install -DskipTests
```

### 5.3 部署到中央仓库

```bash
mvn deploy -DskipTests
```

### 5.4 ApexOS 项目更新

```bash
cd e:\apex\apexos
mvn clean package -DskipTests
```

---

## 六、相关文档

- [OS团队需求说明-SPI化改造](file:///e:/apex/apexos/docs/OS团队需求说明-SPI化改造.md)
- [SKILLS团队协作说明书](file:///e:/apex/apexos/docs/SKILLS团队协作说明书.md)
- [skill-sqlite-driver README](file:///e:/github/ooder-skills/skills/_drivers/database/skill-sqlite-driver/README.md)

---

**文档维护**: OS Team & SKILLS Team  
**最后更新**: 2026-04-10

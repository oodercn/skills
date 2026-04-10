# SQLite 数据库驱动

## 简介

skill-sqlite-driver 是一个提供 SQLite 数据库支持的 SPI Driver，通过 `DataSourceProvider` SPI 接口为 OoderOS 提供数据源能力。

## 功能特性

- ✅ 通过 SPI 接口提供 SQLite 数据源
- ✅ 基于 HikariCP 的高性能连接池
- ✅ 支持自定义数据库配置
- ✅ 自动创建数据目录
- ✅ 连接池参数可配置

## 安装

### 方式一：作为默认 Skill 安装

将 jar 包放入 `plugins/` 目录：

```bash
cp skill-sqlite-driver-1.0.0.jar plugins/
```

### 方式二：Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-sqlite-driver</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 配置

### skill.yaml 配置

```yaml
config:
  sqlite:
    data-dir: ${user.home}/.apexos/data
    database-name: apexos
    pool:
      max-size: 5
      min-idle: 1
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| data-dir | 数据库文件存储目录 | ${user.home}/.apexos/data |
| database-name | 数据库名称 | apexos |
| pool.max-size | 连接池最大连接数 | 5 |
| pool.min-idle | 连接池最小空闲连接数 | 1 |
| pool.connection-timeout | 连接超时时间（毫秒） | 30000 |
| pool.idle-timeout | 空闲连接超时时间（毫秒） | 600000 |
| pool.max-lifetime | 连接最大存活时间（毫秒） | 1800000 |

## 使用示例

### 通过 SPI 获取数据源

```java
import net.ooder.spi.database.DataSourceProvider;
import net.ooder.spi.database.DatabaseConfig;
import net.ooder.spi.facade.SpiServices;

// 方式一：使用 SpiServices 工具类
DataSourceProvider provider = SpiServices.getDataSourceProvider();
if (provider != null) {
    DatabaseConfig config = DatabaseConfig.builder()
        .databaseType("sqlite")
        .databaseName("mydb")
        .dataDir("/path/to/data")
        .build();
    
    DataSource dataSource = provider.createDataSource(config);
}

// 方式二：使用 Optional 方式
SpiServices.dataSource().ifPresent(provider -> {
    DataSource dataSource = provider.createDataSource(config);
});
```

### 直接使用 DataSourceProvider

```java
import net.ooder.spi.database.DataSourceProvider;
import net.ooder.spi.database.DatabaseConfig;
import javax.sql.DataSource;

DataSourceProvider provider = new SQLiteDataSourceProvider();

DatabaseConfig config = DatabaseConfig.builder()
    .databaseName("testdb")
    .dataDir("/tmp/data")
    .poolConfig("max-size", 10)
    .build();

if (provider.validateConfig(config)) {
    DataSource dataSource = provider.createDataSource(config);
}
```

## SPI 接口

### DataSourceProvider

```java
public interface DataSourceProvider {
    String getDatabaseType();
    String getDatabaseTypeName();
    DataSource createDataSource(DatabaseConfig config);
    boolean validateConfig(DatabaseConfig config);
    String getJdbcUrlTemplate();
    String getDriverClassName();
}
```

## 技术栈

- SQLite JDBC 3.49.1.0
- HikariCP 5.1.0
- Spring Boot AutoConfigure
- Ooder SPI Core 3.0.2

## 版本历史

### v1.0.0 (2026-04-10)
- 初始版本
- 实现 DataSourceProvider SPI 接口
- 支持 HikariCP 连接池
- 支持自定义配置

## 许可证

MIT License

## 作者

ooder team

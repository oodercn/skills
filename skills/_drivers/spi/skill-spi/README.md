# skill-spi

SPI服务实现 - 提供技能系统的SPI接口实现。

## 功能特性

- **SPI实现** - 实现技能系统的核心SPI接口
- **服务发现** - 支持SPI机制的服务发现
- **动态加载** - 支持动态加载SPI实现
- **扩展机制** - 提供灵活的扩展机制

## 核心接口

### SpiService

SPI服务接口。

```java
public interface SpiService {
    /**
     * 获取SPI实现
     */
    <T> T getSpiImplementation(Class<T> spiInterface);
    
    /**
     * 注册SPI实现
     */
    <T> void registerSpiImplementation(Class<T> spiInterface, T implementation);
    
    /**
     * 获取所有SPI实现
     */
    <T> List<T> getAllImplementations(Class<T> spiInterface);
}
```

## SPI服务发现

支持通过SPI机制自动发现实现：

```
# META-INF/services/net.ooder.spi.SomeService
com.example.SomeServiceImpl
```

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-spi</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private SpiService spiService;

// 获取SPI实现
SomeService service = spiService.getSpiImplementation(SomeService.class);

// 注册SPI实现
spiService.registerSpiImplementation(SomeService.class, new SomeServiceImpl());

// 获取所有实现
List<SomeService> allImpls = spiService.getAllImplementations(SomeService.class);
```

## SPI配置

### application.yml

```yaml
ooder:
  spi:
    scan-packages: com.example.spi
    auto-discovery: true
```

## 许可证

Apache-2.0

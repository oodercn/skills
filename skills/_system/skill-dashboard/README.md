# skill-dashboard

仪表板服务 - 提供系统仪表板的数据展示和统计功能。

## 功能特性

- **数据展示** - 展示系统关键指标
- **统计分析** - 提供数据统计分析
- **实时监控** - 实时监控系统状态
- **自定义仪表板** - 支持自定义仪表板布局

## 核心接口

### DashboardController

仪表板控制器。

```java
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    /**
     * 获取仪表板数据
     */
    @GetMapping
    public DashboardDataDTO getDashboardData();
    
    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public StatisticsDTO getStatistics();
    
    /**
     * 获取实时监控数据
     */
    @GetMapping("/monitor")
    public MonitorDataDTO getMonitorData();
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/dashboard | GET | 获取仪表板数据 |
| /api/v1/dashboard/statistics | GET | 获取统计数据 |
| /api/v1/dashboard/monitor | GET | 获取实时监控数据 |

## 仪表板模型

### DashboardDataDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| widgets | List<Widget> | 小部件列表 |
| statistics | Statistics | 统计数据 |
| alerts | List<Alert> | 告警列表 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-dashboard</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private DashboardService dashboardService;

// 获取仪表板数据
DashboardDataDTO data = dashboardService.getDashboardData();

// 获取统计数据
StatisticsDTO stats = dashboardService.getStatistics();
```

## 许可证

Apache-2.0

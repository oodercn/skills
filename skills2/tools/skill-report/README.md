# skill-report

报表工具服务，支持报表生成、导出

## 功能特性

- 报表生成 - 生成各类报表
- 数据导出 - 导出Excel、PDF等格式
- 模板管理 - 报表模板管理
- 定时报表 - 定时生成报表

## 快速开始

### 安装

```bash
skill install skill-report
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/report/generate \
  -H "Content-Type: application/json" \
  -d '{
    "template": "monthly-report",
    "data": {...}
  }'
```

## 许可证

Apache-2.0

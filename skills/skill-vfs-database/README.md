# skill-vfs-database

数据库存储服务 - 将文件存储在数据库中，支持元数据管理

## 功能特性

- **数据库存储** - 将文件内容存储在数据库中
- **元数据管理** - 支持自定义文件元数据
- **事务支持** - 支持数据库事务
- **多数据库支持** - 支持 MySQL、PostgreSQL、H2 等
- **缓存同步** - 支持多实例缓存同步

## 快速开始

### 配置文件

```yaml
vfs:
  database:
    url: jdbc:mysql://localhost:3306/vfs
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 支持的能力

| 能力 | 说明 | 状态 |
|------|------|------|
| file-read | 读取文件 | ✅ |
| file-write | 写入文件 | ✅ |
| file-delete | 删除文件 | ✅ |
| metadata | 元数据管理 | ✅ |
| file-version | 文件版本 | ✅ |
| file-share | 文件分享 | ❌ |

## API 接口

```
GET  /api/vfs/file/{id}          # 获取文件信息
POST /api/vfs/file               # 上传文件
GET  /api/vfs/file/{id}/download # 下载文件
GET  /api/vfs/capabilities       # 获取支持的能力
```

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| DB_URL | string | 是 | 数据库连接URL |
| DB_USERNAME | string | 是 | 数据库用户名 |
| DB_PASSWORD | string | 是 | 数据库密码 |

## 版本历史

- 0.7.0 - 初始版本

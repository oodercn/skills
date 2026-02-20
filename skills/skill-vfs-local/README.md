# skill-vfs-local

本地文件系统存储服务 - 提供基本的文件操作和版本管理

## 功能特性

- **零配置启动** - 无需任何配置即可启动本地存储服务
- **文件操作** - 支持文件读写、删除、复制、移动
- **目录操作** - 支持目录创建、删除、列表
- **版本管理** - 支持文件版本控制
- **流式下载** - 支持大文件流式下载
- **缓存同步** - 支持多实例缓存同步

## 快速开始

### 零配置启动

```bash
java -jar skill-vfs-local-0.7.0.jar
```

### 配置文件

```yaml
vfs:
  local:
    root-path: ./data/vfs
    temp-path: ./temp/vfs
    max-file-size: 104857600
```

## 支持的能力

| 能力 | 说明 | 状态 |
|------|------|------|
| file-read | 读取文件 | ✅ |
| file-write | 写入文件 | ✅ |
| file-delete | 删除文件 | ✅ |
| file-copy | 复制文件 | ✅ |
| file-move | 移动文件 | ✅ |
| folder-create | 创建目录 | ✅ |
| folder-delete | 删除目录 | ✅ |
| folder-list | 列出目录 | ✅ |
| file-version | 文件版本 | ✅ |
| stream-download | 流式下载 | ✅ |
| file-share | 文件分享 | ❌ |
| file-preview | 文件预览 | ❌ |

## API 接口

```
GET  /api/vfs/file/{id}          # 获取文件信息
POST /api/vfs/file               # 上传文件
GET  /api/vfs/file/{id}/download # 下载文件
GET  /api/vfs/folder/{id}        # 获取目录信息
POST /api/vfs/folder             # 创建目录
GET  /api/vfs/capabilities       # 获取支持的能力
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| VFS_LOCAL_ROOT_PATH | string | ./data/vfs | 存储根路径 |
| VFS_LOCAL_TEMP_PATH | string | ./temp/vfs | 临时文件路径 |
| VFS_LOCAL_MAX_FILE_SIZE | number | 104857600 | 最大文件大小 |

## 版本历史

- 0.7.0 - 初始版本

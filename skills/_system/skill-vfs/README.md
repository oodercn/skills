# skill-vfs

虚拟文件系统服务 - 提供统一的文件存储和访问接口。

## 功能特性

- **文件存储** - 统一的文件存储接口
- **文件访问** - 提供文件访问API
- **文件管理** - 文件的增删改查
- **多存储支持** - 支持多种存储后端

## 核心接口

### VfsFileController

文件管理控制器。

```java
@RestController
@RequestMapping("/api/v1/vfs")
public class VfsFileController {
    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public VfsFileMetadata uploadFile(@RequestParam("file") MultipartFile file);
    
    /**
     * 下载文件
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId);
    
    /**
     * 获取文件列表
     */
    @GetMapping("/files")
    public List<VfsFileMetadata> listFiles(@RequestParam(required = false) String path);
    
    /**
     * 删除文件
     */
    @DeleteMapping("/files/{fileId}")
    public void deleteFile(@PathVariable String fileId);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/vfs/upload | POST | 上传文件 |
| /api/v1/vfs/download/{fileId} | GET | 下载文件 |
| /api/v1/vfs/files | GET | 获取文件列表 |
| /api/v1/vfs/files/{fileId} | DELETE | 删除文件 |

## 文件模型

### VfsFileMetadata

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 文件ID |
| name | String | 文件名 |
| path | String | 文件路径 |
| size | long | 文件大小 |
| mimeType | String | MIME类型 |
| createdAt | Long | 创建时间 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-vfs</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private VfsService vfsService;

// 上传文件
File file = new File("document.pdf");
VfsFileMetadata metadata = vfsService.uploadFile(file);

// 下载文件
InputStream stream = vfsService.downloadFile(metadata.getId());

// 获取文件列表
List<VfsFileMetadata> files = vfsService.listFiles("/documents");

// 删除文件
vfsService.deleteFile(metadata.getId());
```

## 许可证

Apache-2.0

# Markdown/文本解析器

## 简介

skill-markdown-parser 是一个提供 Markdown 和纯文本文件解析支持的 SPI Driver，通过 `DocumentParser` SPI 接口为 OoderOS 提供文档解析能力。

## 功能特性

- ✅ 支持 Markdown 文件解析
- ✅ 支持纯文本文件解析
- ✅ 自动提取文档元数据（标题、字数、行数等）
- ✅ 提取 Markdown 特有元素（标题、代码块、链接、图片等）
- ✅ 基于 CommonMark 标准解析
- ✅ 支持 GFM 表格扩展

## 安装

### 方式一：作为默认 Skill 安装

将 jar 包放入 `plugins/` 目录：

```bash
cp skill-markdown-parser-1.0.0.jar plugins/
```

### 方式二：Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-markdown-parser</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 支持的文件类型

### MIME 类型

- `text/markdown`
- `text/x-markdown`
- `text/plain`
- `application/markdown`

### 文件扩展名

- `.md`
- `.markdown`
- `.txt`
- `.text`

## 配置

### skill.yaml 配置

```yaml
config:
  parser:
    max-file-size: 10485760  # 最大文件大小（字节）
    encoding: UTF-8           # 文件编码
    extract-metadata: true    # 是否提取元数据
```

### 配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| max-file-size | 最大文件大小（字节） | 10485760 (10MB) |
| encoding | 文件编码 | UTF-8 |
| extract-metadata | 是否提取元数据 | true |

## 使用示例

### 通过 SPI 解析文档

```java
import net.ooder.spi.document.DocumentParser;
import net.ooder.spi.document.ParseResult;
import net.ooder.spi.facade.SpiServices;
import java.io.FileInputStream;

// 方式一：使用 SpiServices 工具类
DocumentParser parser = SpiServices.getDocumentParser("text/markdown");
if (parser != null) {
    try (FileInputStream fis = new FileInputStream("document.md")) {
        ParseResult result = parser.parseWithMetadata(fis, "text/markdown");
        
        if (result.isSuccess()) {
            String text = result.getText();
            String title = result.getTitle();
            
            // 获取元数据
            int wordCount = (int) result.getMetadata().get("wordCount");
            int headingCount = (int) result.getMetadata().get("headingCount");
            
            System.out.println("标题: " + title);
            System.out.println("字数: " + wordCount);
            System.out.println("标题数: " + headingCount);
        }
    }
}

// 方式二：使用 Optional 方式
SpiServices.documentParser("text/markdown").ifPresent(parser -> {
    ParseResult result = parser.parseWithMetadata(inputStream, "text/markdown");
    // 处理结果
});
```

### 直接使用 DocumentParser

```java
import net.ooder.spi.document.DocumentParser;
import net.ooder.spi.document.ParseResult;
import net.ooder.skill.document.markdown.MarkdownDocumentParser;

DocumentParser parser = new MarkdownDocumentParser();

if (parser.supports("text/markdown")) {
    ParseResult result = parser.parseWithMetadata(inputStream, "text/markdown");
    
    if (result.isSuccess()) {
        String text = result.getText();
        System.out.println(text);
    } else {
        System.err.println("解析失败: " + result.getErrorMessage());
    }
}
```

## 元数据提取

解析器会自动提取以下元数据：

| 元数据字段 | 说明 | 类型 |
|-----------|------|------|
| title | 文档标题（从第一个一级标题提取） | String |
| lineCount | 总行数 | Integer |
| wordCount | 总字数 | Integer |
| charCount | 总字符数 | Integer |
| headingCount | 标题数量 | Integer |
| codeBlockCount | 代码块数量 | Integer |
| linkCount | 链接数量 | Integer |
| imageCount | 图片数量 | Integer |
| hasYamlFrontMatter | 是否包含 YAML 前置元数据 | Boolean |
| fileSize | 文件大小（字节） | Long |
| mimeType | MIME 类型 | String |
| parser | 解析器名称 | String |

## SPI 接口

### DocumentParser

```java
public interface DocumentParser {
    String getParserName();
    List<String> getSupportedMimeTypes();
    List<String> getSupportedExtensions();
    boolean supports(String mimeType);
    ParseResult parse(InputStream inputStream, String mimeType);
    ParseResult parseWithMetadata(InputStream inputStream, String mimeType);
    default int getPriority() { return 100; }
}
```

## 技术栈

- CommonMark Java 0.22.0
- CommonMark GFM Tables Extension 0.22.0
- CommonMark Heading Anchor Extension 0.22.0
- Spring Boot AutoConfigure
- Ooder SPI Core 3.0.2

## 解析示例

### 输入 Markdown

```markdown
# 示例文档

这是一个示例 Markdown 文档。

## 功能特性

- 特性一
- 特性二

### 代码示例

```java
public class Example {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

[链接示例](https://example.com)

![图片示例](image.png)
```

### 输出元数据

```json
{
  "title": "示例文档",
  "lineCount": 20,
  "wordCount": 25,
  "charCount": 350,
  "headingCount": 3,
  "codeBlockCount": 1,
  "linkCount": 1,
  "imageCount": 1,
  "hasYamlFrontMatter": false
}
```

## 版本历史

### v1.0.0 (2026-04-10)
- 初始版本
- 实现 DocumentParser SPI 接口
- 支持 Markdown 和纯文本解析
- 自动提取元数据
- 基于 CommonMark 标准解析

## 许可证

MIT License

## 作者

ooder team

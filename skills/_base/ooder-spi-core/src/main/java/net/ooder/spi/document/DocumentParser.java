package net.ooder.spi.document;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 文档解析器 SPI 接口
 * 用于解析不同格式的文档
 */
public interface DocumentParser {
    
    /**
     * 获取解析器名称
     * @return 解析器名称
     */
    String getParserName();
    
    /**
     * 获取解析器支持的所有 MIME 类型
     * @return MIME 类型列表
     */
    List<String> getSupportedMimeTypes();
    
    /**
     * 获取解析器支持的所有文件扩展名
     * @return 文件扩展名列表，如 [".md", ".txt", ".markdown"]
     */
    List<String> getSupportedExtensions();
    
    /**
     * 检查是否支持指定的 MIME 类型
     * @param mimeType MIME 类型
     * @return 是否支持
     */
    boolean supports(String mimeType);
    
    /**
     * 解析文档内容
     * @param inputStream 文档输入流
     * @param mimeType 文档 MIME 类型
     * @return 解析结果
     */
    ParseResult parse(InputStream inputStream, String mimeType);
    
    /**
     * 解析文档内容（包含元数据）
     * @param inputStream 文档输入流
     * @param mimeType 文档 MIME 类型
     * @return 解析结果（包含元数据）
     */
    ParseResult parseWithMetadata(InputStream inputStream, String mimeType);
    
    /**
     * 获取解析器优先级
     * 数值越小优先级越高
     * @return 优先级
     */
    default int getPriority() {
        return 100;
    }
}

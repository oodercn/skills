# 常见问题

## 通用问题

### Q: 支持哪些LLM Provider？

A: 目前支持以下Provider：
- 百度文心一言 (ernie-bot-4, ernie-bot-turbo)
- OpenAI (gpt-4, gpt-3.5-turbo)
- 阿里千问 (qwen-max, qwen-plus)
- DeepSeek (deepseek-chat)
- Ollama (本地部署，支持llama2等模型)

### Q: 如何切换不同的模型？

A: 有两种方式：

1. 全局配置
```yaml
skill-llm-chat:
  default-provider: openai
  default-model: gpt-4
```

2. 请求时指定
```json
{
  "message": "你好",
  "provider": "openai",
  "model": "gpt-4"
}
```

### Q: 流式输出和普通输出有什么区别？

A: 
- **普通输出**: 等待LLM完整响应后一次性返回
- **流式输出**: 使用SSE实时返回生成的文本，用户体验更好

建议在生产环境启用流式输出。

## 配置问题

### Q: 如何配置多个Provider？

A: 在配置文件中添加多个Provider即可：

```yaml
llm:
  providers:
    baidu:
      api-key: ${BAIDU_API_KEY}
      secret-key: ${BAIDU_SECRET_KEY}
    openai:
      api-key: ${OPENAI_API_KEY}
    qianwen:
      api-key: ${QIANWEN_API_KEY}
```

### Q: 如何使用环境变量配置敏感信息？

A: 推荐使用环境变量：

```yaml
llm:
  providers:
    baidu:
      api-key: ${BAIDU_API_KEY}
      secret-key: ${BAIDU_SECRET_KEY}
```

然后在环境中设置：
```bash
export BAIDU_API_KEY=your-api-key
export BAIDU_SECRET_KEY=your-secret-key
```

### Q: 如何配置代理？

A: 在Provider配置中添加代理：

```yaml
llm:
  providers:
    openai:
      api-key: ${OPENAI_API_KEY}
      proxy:
        enabled: true
        host: proxy.example.com
        port: 8080
```

## 会话管理

### Q: 会话历史保存在哪里？

A: 默认保存在内存中，重启后丢失。可以配置持久化存储：

```yaml
skill-llm-chat:
  session:
    storage: persistent
```

### Q: 如何限制会话历史长度？

A: 配置max-history参数：

```yaml
skill-llm-chat:
  session:
    max-history: 50
```

### Q: 会话超时时间如何设置？

A: 配置timeout参数（单位：秒）：

```yaml
skill-llm-chat:
  session:
    timeout: 7200  # 2小时
```

## 性能问题

### Q: 如何提高响应速度？

A: 
1. 使用更快的模型（如ernie-bot-turbo）
2. 启用流式输出
3. 减少上下文长度
4. 增加连接池大小

### Q: 如何处理高并发？

A: 
1. 增加连接池配置
```yaml
skill-llm-chat:
  performance:
    connection-pool-size: 20
```

2. 启用限流
```yaml
skill-llm-chat:
  security:
    rate-limit:
      enabled: true
      requests-per-minute: 100
```

### Q: 内存占用过高怎么办？

A: 
1. 减少历史记录保留数量
2. 缩短会话超时时间
3. 定期清理过期会话

## 错误处理

### Q: 遇到401错误怎么办？

A: 检查API Key是否正确配置：
1. 确认环境变量已设置
2. 确认API Key未过期
3. 确认配置文件引用正确

### Q: 遇到429错误怎么办？

A: 请求过于频繁，需要：
1. 启用限流配置
2. 检查是否有异常请求
3. 考虑升级API配额

### Q: 流式输出中断怎么办？

A: 
1. 检查Nginx缓冲配置
2. 增加超时时间
3. 检查网络稳定性

## 更多问题

如有其他问题，请：
- 查看 [故障排查](troubleshooting.md)
- 提交 [Issue](https://gitee.com/ooderCN/ooder-skills/issues)

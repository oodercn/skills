# 故障排查

## 常见问题

### 问题1: API Key无效

**现象**: 调用LLM接口返回401错误

**原因**: API Key配置错误或已过期

**解决方案**:

1. 检查环境变量是否正确设置
```bash
echo $BAIDU_API_KEY
echo $OPENAI_API_KEY
```

2. 验证API Key是否有效
```bash
curl -X POST https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions \
  -H "Content-Type: application/json" \
  -d '{"messages":[{"role":"user","content":"test"}]}'
```

3. 检查配置文件中的API Key引用
```yaml
llm:
  providers:
    baidu:
      api-key: ${BAIDU_API_KEY}  # 确保环境变量名称正确
```

### 问题2: 流式输出中断

**现象**: SSE流式输出中途断开

**原因**: 网络超时或Nginx缓冲配置问题

**解决方案**:

1. 检查Nginx配置
```nginx
location /api/llm/chat/stream {
    proxy_buffering off;
    proxy_cache off;
    proxy_read_timeout 300s;
}
```

2. 增加超时配置
```yaml
skill-llm-chat:
  performance:
    request-timeout: 120000
```

### 问题3: 会话历史丢失

**现象**: 刷新页面后会话历史丢失

**原因**: 会话存储配置问题

**解决方案**:

1. 检查存储配置
```yaml
skill-llm-chat:
  session:
    storage: persistent  # 使用持久化存储
    timeout: 86400       # 增加超时时间
```

2. 检查数据库连接
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ooder
    username: root
    password: ${DB_PASSWORD}
```

### 问题4: 响应速度慢

**现象**: LLM响应时间过长

**原因**: 网络延迟或模型选择问题

**解决方案**:

1. 使用更快的模型
```yaml
skill-llm-chat:
  default-model: ernie-bot-turbo  # Turbo版本更快
```

2. 启用流式输出
```yaml
skill-llm-chat:
  stream-enabled: true
```

3. 减少上下文长度
```yaml
skill-llm-chat:
  context:
    max-tokens: 1000
```

### 问题5: 内存占用过高

**现象**: 服务内存持续增长

**原因**: 会话缓存未及时清理

**解决方案**:

1. 减少历史记录保留
```yaml
skill-llm-chat:
  session:
    max-history: 50
    cleanup-interval: 60
```

2. 启用内存限制
```yaml
skill-llm-chat:
  resources:
    memory: "256Mi"
```

## 日志分析

### 日志位置

```
logs/skill-llm-chat.log
```

### 关键日志关键字

| 关键字 | 说明 |
|--------|------|
| LLM_REQUEST | LLM请求发起 |
| LLM_RESPONSE | LLM响应接收 |
| SESSION_CREATE | 会话创建 |
| SESSION_EXPIRE | 会话过期 |
| ERROR | 错误信息 |

### 日志级别配置

```yaml
logging:
  level:
    net.ooder.skill.llm: DEBUG
    net.ooder.skill.llm.chat: TRACE
```

## 调试模式

启用调试模式：

```yaml
logging:
  level:
    net.ooder.skill.llm: DEBUG
    
skill-llm-chat:
  debug:
    enabled: true
    log-request: true
    log-response: true
```

## 健康检查

```bash
curl http://localhost:8080/actuator/health
```

## 获取帮助

- 查看 [FAQ](faq.md)
- 提交 [Issue](https://gitee.com/ooderCN/ooder-skills/issues)

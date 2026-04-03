# skill-llm-ollama

Ollama本地模型驱动服务，支持本地部署的开源大语言模型。

## 功能特性

- **本地部署** - 完全本地运行，数据安全
- **多模型支持** - Llama、Mistral、CodeLlama等
- **流式输出** - SSE流式响应
- **低资源模式** - 支持量化模型

## 支持模型

| 模型ID | 名称 | 参数量 | 说明 |
|--------|------|--------|------|
| llama2 | Llama 2 | 7B/13B/70B | Meta开源模型 |
| llama3 | Llama 3 | 8B/70B | Llama最新版本 |
| mistral | Mistral | 7B | Mistral AI模型 |
| codellama | Code Llama | 7B/13B/34B | 代码专用模型 |
| deepseek-coder | DeepSeek Coder | 6.7B/33B | 代码生成模型 |
| qwen2 | Qwen2 | 7B/72B | 通义千问开源版 |

## 快速开始

### 安装Ollama

```bash
# macOS/Linux
curl -fsSL https://ollama.com/install.sh | sh

# 拉取模型
ollama pull llama2
```

### 安装Skill

```bash
skill install skill-llm-ollama
```

### 配置

```yaml
skill-llm-ollama:
  base-url: http://localhost:11434
  model: llama2
  max-tokens: 4096
  temperature: 0.7
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/llm/chat \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {"role": "user", "content": "Hello"}
    ],
    "model": "llama2"
  }'
```

## 配置项

| 配置项 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| baseUrl | string | 否 | http://localhost:11434 | Ollama服务地址 |
| model | string | 否 | llama2 | 默认模型 |
| maxTokens | int | 否 | 4096 | 最大Token数 |
| temperature | float | 否 | 0.7 | 温度参数 |

## 常用命令

```bash
# 列出已安装模型
ollama list

# 拉取新模型
ollama pull mistral

# 运行模型
ollama run llama2

# 删除模型
ollama rm llama2
```

## 许可证

Apache-2.0

# skill-llm-config

LLM配置管理技能， 提供多级配置、Provider管理、模型选择、配置加密等功能。

## 功能特性

- **多级配置** - 用户级/会话级/技能级/系统级配置
- **Provider管理** - LLM提供商注册与配置
- **模型选择** - 智能模型选择与切换
- **配置加密** - API Key安全存储
- **配置审计** - 配置变更追踪
- **使用统计** - Token使用量统计

## API端点

### 配置管理

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/llm-config | GET | 获取LLM配置列表 |
| /api/v1/llm-config | POST | 创建LLM配置 |
| /api/v1/llm-config/{id} | PUT | 更新LLM配置 |
| /api/v1/llm-config/{id} | DELETE | 删除LLM配置 |
| /api/v1/llm-config/providers | GET | 获取Providers列表 |
| /api/v1/llm-config/templates | GET | 获取配置模板 |

### Provider管理

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/llm-provider | GET | 获取Provider列表 |
| /api/v1/llm-provider/{id} | GET | 获取Provider详情 |
| /api/v1/llm-provider/{id}/models | GET | 获取Provider可用模型 |

### 使用统计

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/llm-config/{id}/usage-stats | GET | 获取使用统计 |
| /api/v1/llm-config/usage-summary | GET | 获取使用汇总 |

## 配置层级

配置按优先级从高到低：

1. **用户级配置** - 用户个人偏好设置
2. **会话级配置** - 当前会话临时设置
3. **技能级配置** - 技能默认配置
4. **系统级配置** - 系统全局默认配置

## 快速开始

### 安装

```bash
skill install skill-llm-config
```

### 配置示例

```yaml
llm-config:
  default-provider: deepseek
  providers:
    deepseek:
      api-key: ${DEEPSEEK_API_KEY}
      model: deepseek-chat
    openai:
      api-key: ${OPENAI_API_KEY}
      model: gpt-4o
```

### 使用示例

```java
@Autowired
private LlmConfigService llmConfigService;

public void chat(String message) {
    LlmConfig config = llmConfigService.resolveConfig(
        "skill-llm-chat", "user-001", "session-001"
    );
    // 使用配置进行对话
}
```

## 配置项

| 配置项 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| default-provider | string | 否 | deepseek | 默认Provider |
| encryption.enabled | boolean | 否 | true | 是否启用加密 |
| encryption.algorithm | string | 否 | AES-256 | 加密算法 |
| audit.enabled | boolean | 否 | true | 是否启用审计 |

## 依赖

- skill-spi-llm (3.0.1)
- skill-common (3.0.1)

## 许可证

Apache-2.0

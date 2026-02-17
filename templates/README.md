# ${skill.displayName}

## 概述

${skill.description}

## 版本

当前版本: ${skill.version}

## 能力

| 能力ID | 名称 | 描述 |
|--------|------|------|
| ${capability.id} | ${capability.name} | ${capability.description} |

## 配置参数

### 必需参数

| 参数名 | 类型 | 描述 |
|--------|------|------|
| ${config.name} | ${config.type} | ${config.description} |

### 可选参数

| 参数名 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| ${config.name} | ${config.type} | ${config.default} | ${config.description} |

## API端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/${endpoint.path} | ${endpoint.method} | ${endpoint.description} |

## 使用示例

```bash
# 安装技能
ooder skill install ${skill.name}

# 配置参数
ooder skill config ${skill.name} --set ${config.name}=value

# 启动技能
ooder skill start ${skill.name}
```

## 依赖

- SDK版本: >=0.7.0
- Java版本: >=8

## 许可证

Apache-2.0

## 作者

ooder Team

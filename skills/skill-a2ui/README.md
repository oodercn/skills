# A2UI Skill

## 概述

A2UI图转代码技能，支持将设计图转换为前端代码

## 版本

当前版本: 0.7.1

## 能力

| 能力ID | 名称 | 描述 |
|--------|------|------|
| generate-ui | Generate UI | 从设计图生成前端代码 |
| preview-ui | Preview UI | 预览生成的UI |
| create-view | Create View | 创建新的视图组件 |

## 配置参数

### 可选参数

| 参数名 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| A2UI_PORT | number | 8081 | 服务端口 |
| A2UI_OUTPUT_DIR | string | ./output | 输出目录 |

## API端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/info | GET | 获取技能信息 |
| /api/generate | POST | 生成UI代码 |
| /api/preview | POST | 预览UI |
| /api/health | GET | 健康检查 |

## 使用示例

```bash
# 安装技能
ooder skill install skill-a2ui

# 启动技能
ooder skill start skill-a2ui

# 生成UI代码
curl -X POST http://localhost:8081/api/generate -F "designFile=@design.png"
```

## 依赖

- SDK版本: >=0.7.0
- Java版本: >=8

## 许可证

Apache-2.0

## 作者

Ooder Team

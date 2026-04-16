# Skills 配置端点检查报告

> 生成时间: 2026-04-15
> 项目路径: e:\github\ooder-skills\skills

---

## 一、清理完成项

### 1.1 已删除的旧 SPI 模块

| 模块 | 路径 | 状态 |
|------|------|------|
| skill-spi-core | `skills/_base/skill-spi-core` | ✅ 已删除 |
| skill-spi-llm | `skills/_base/skill-spi-llm` | ✅ 已删除 |
| skill-spi-messaging | `skills/_base/skill-spi-messaging` | ✅ 已删除 |

### 1.2 保留的 SPI 模块

| 模块 | 路径 | 版本 |
|------|------|------|
| ooder-spi-core | `skills/_base/ooder-spi-core` | 3.0.5 |

---

## 二、Spring Boot 自动配置检查

### 2.1 spring.factories 文件统计

| 分类 | 数量 |
|------|------|
| _system 模块 | 25 |
| _drivers 模块 | 30 |
| _business 模块 | 12 |
| capabilities 模块 | 20 |
| scenes 模块 | 15 |
| tools 模块 | 12 |
| **总计** | **114** |

### 2.2 AutoConfiguration.imports 文件统计

| 分类 | 数量 |
|------|------|
| _system 模块 | 7 |
| _drivers 模块 | 3 |
| _business 模块 | 4 |
| **总计** | **14** |

### 2.3 配置方式对比

| 配置方式 | Spring Boot 版本 | 文件位置 |
|----------|------------------|----------|
| spring.factories | 2.x 兼容 | META-INF/spring.factories |
| AutoConfiguration.imports | 3.x 推荐 | META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports |

**建议**: 统一迁移到 AutoConfiguration.imports 格式

---

## 三、Controller 端点统计

### 3.1 _system 模块 Controller

| 模块 | Controller 数量 | 主要端点 |
|------|-----------------|----------|
| skill-llm-chat | 6 | /api/v1/llm/chat, /api/v1/chat |
| skill-workflow | 7 | /api/v1/bpm/* |
| skill-config | 5 | /api/v1/config/* |
| skill-capability | 5 | /api/v1/capability/* |
| skill-discovery | 3 | /api/v1/discovery/* |
| skill-agent | 5 | /api/v1/agent/* |
| skill-install | 3 | /api/v1/install/* |
| skill-common | 3 | /api/v1/menu, /api/v1/session |
| skill-template | 2 | /api/v1/template/* |
| skill-key | 2 | /api/v1/key/* |
| skill-org | 2 | /api/v1/org/* |
| skill-menu | 2 | /api/v1/menu/* |
| skill-knowledge-platform | 2 | /api/v1/knowledge/* |
| skill-knowledge | 1 | /api/v1/knowledge/* |
| skill-rag | 1 | /api/v1/rag/* |
| skill-messaging | 1 | /api/v1/messaging/* |
| skill-notification | 1 | /api/v1/notification/* |
| skill-history | 1 | /api/v1/history/* |
| skill-dashboard | 1 | /api/v1/dashboard/* |
| skill-tenant | 1 | /api/v1/tenant/* |
| skill-auth | 1 | /api/v1/auth/* |
| skill-audit | 1 | /api/v1/audit/* |
| skill-dict | 1 | /api/v1/dict/* |
| skill-support | 1 | /api/v1/support/* |
| skill-vfs | 1 | /api/v1/vfs/* |
| skill-protocol | 2 | /api/v1/protocol/* |
| skill-share | 1 | /api/v1/share/* |
| skill-setup | 1 | /api/v1/setup/* |
| skill-scene | 1 | /api/v1/workbench/* |
| skill-management | 1 | /api/v1/skill/* |
| skill-im-gateway | 1 | /api/v1/webhook/* |
| skill-role | 1 | /api/v1/role/* |

**总计**: 60+ Controller

---

## 四、skill.yaml 配置文件检查

### 4.1 配置文件统计

| 分类 | 数量 |
|------|------|
| _system | 27 |
| _drivers | 35 |
| _business | 10 |
| capabilities | 25 |
| scenes | 15 |
| tools | 10 |
| .archive | 15 |
| **总计** | **137** |

### 4.2 配置完整性检查

| 检查项 | 状态 |
|--------|------|
| metadata.id | ✅ 完整 |
| metadata.version | ✅ 完整 |
| spec.routes | ⚠️ 部分缺失 |
| spec.services | ⚠️ 部分缺失 |
| spec.dependencies | ⚠️ 部分缺失 |

### 4.3 版本一致性

| 模块类型 | 版本范围 |
|----------|----------|
| _system | 3.0.1 - 3.0.5 |
| _drivers | 3.0.2 - 3.0.3 |
| _business | 3.0.2 |

---

## 五、发现的问题

### 5.1 配置格式不统一

| 问题 | 影响 | 建议 |
|------|------|------|
| spring.factories 与 AutoConfiguration.imports 混用 | Spring Boot 3.x 兼容性 | 统一迁移到 .imports 格式 |
| skill.yaml 版本不一致 | 依赖管理困难 | 统一到 3.0.5 |

### 5.2 缺失配置

| 模块 | 缺失项 |
|------|--------|
| skill-im-dingding | spring.factories 缺失 AutoConfiguration |
| skill-im-feishu | spring.factories 缺失 AutoConfiguration |
| skill-im-wecom | spring.factories 缺失 AutoConfiguration |

### 5.3 依赖问题

| 模块 | 问题 |
|------|------|
| skill-im-* | 缺少对 ooder-spi-core 的依赖声明 |
| skill-local-vector-store | 需更新依赖到 ooder-spi-core:3.0.5 |

---

## 六、API 端点汇总

### 6.1 核心 API 路由

| 路由前缀 | 模块 | 说明 |
|----------|------|------|
| /api/v1/llm/chat | skill-llm-chat | LLM 聊天服务 |
| /api/v1/chat | skill-llm-chat | 会话管理 |
| /api/v1/knowledge | skill-knowledge | 知识库管理 |
| /api/v1/bpm | skill-workflow | 工作流服务 |
| /api/v1/capability | skill-capability | 能力管理 |
| /api/v1/agent | skill-agent | Agent 服务 |
| /api/v1/config | skill-config | 配置管理 |
| /api/v1/discovery | skill-discovery | 服务发现 |
| /api/v1/install | skill-install | 安装服务 |
| /api/v1/org | skill-org | 组织管理 |
| /api/v1/auth | skill-auth | 认证服务 |
| /api/v1/tenant | skill-tenant | 租户管理 |
| /api/v1/dict | skill-dict | 字典服务 |
| /api/v1/rag | skill-rag | RAG 服务 |
| /api/v1/messaging | skill-messaging | 消息服务 |
| /api/v1/notification | skill-notification | 通知服务 |
| /api/v1/workbench | skill-scene | 工作台 |
| /api/v1/template | skill-template | 模板服务 |
| /api/v1/key | skill-key | 密钥管理 |
| /api/v1/audit | skill-audit | 审计服务 |
| /api/v1/vfs | skill-vfs | 虚拟文件系统 |

### 6.2 端点总数

- **GET 端点**: 约 80+
- **POST 端点**: 约 60+
- **PUT 端点**: 约 20+
- **DELETE 端点**: 约 30+
- **总计**: 约 190+ API 端点

---

## 七、建议改进项

### 7.1 高优先级

1. **统一 AutoConfiguration 格式**
   - 将所有 spring.factories 迁移到 AutoConfiguration.imports
   - 兼容 Spring Boot 3.x

2. **更新依赖版本**
   - 所有模块统一依赖 ooder-spi-core:3.0.5
   - 更新 pom.xml 中的版本声明

### 7.2 中优先级

1. **补充缺失配置**
   - 为 IM 驱动模块添加 AutoConfiguration
   - 完善 skill.yaml 中的 routes 和 services 定义

2. **版本统一**
   - 所有 skill.yaml 版本统一到 3.0.5
   - pom.xml 版本同步更新

### 7.3 低优先级

1. **文档完善**
   - 为每个 API 端点添加 OpenAPI 文档
   - 生成 API 文档汇总

---

## 八、文件路径索引

### 8.1 关键配置文件

```
e:\github\ooder-skills\skills\_base\ooder-spi-core\pom.xml
e:\github\ooder-skills\skills\_base\ooder-spi-core\src\main\java\net\ooder\spi\facade\SpiServices.java

e:\github\ooder-skills\skills\_system\skill-llm-chat\src\main\resources\skill.yaml
e:\github\ooder-skills\skills\_system\skill-llm-chat\src\main\resources\META-INF\spring.factories

e:\github\ooder-skills\skills\_system\skill-config\src\main\resources\META-INF\spring.factories
```

### 8.2 Maven 本地仓库

```
D:\maven\.m2\repository\net\ooder\ooder-spi-core\3.0.5\
```

---

**报告完成时间**: 2026-04-15

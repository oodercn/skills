# OS Skills 迁移任务列表

> 文档路径: `e:\github\ooder-skills\docs\v3.0.1\OS_MIGRATION_TASKS.md`
> 创建时间: 2026-04-03
> 状态: 执行中

---

## 一、迁移概览

### 1.1 迁移范围

| 类别 | 模块数量 | 优先级 | 状态 |
|------|----------|--------|------|
| LLM SPI | 1 | P0 | ⏳ 待迁移 |
| LLM配置管理 | 1 | P0 | ⏳ 待迁移 |
| LLM监控 | 1 | P0 | ⏳ 待迁移 |
| 场景管理 | 1 | P0 | ⏳ 待迁移 |
| 其他业务模块 | 8 | P1 | ⏳ 待迁移 |

### 1.2 迁移原则

1. **保持接口兼容** - 迁移后API接口保持不变
2. **统一版本号** - 所有模块统一使用3.0.1版本
3. **补充文档** - 每个模块必须有README和配置说明
4. **测试验证** - 迁移后必须通过编译测试

---

## 二、P0优先级迁移任务

### 2.1 skill-spi-llm (LLM SPI接口)

**源路径**: `E:\apex\os\skills\_base\skill-spi-llm`
**目标路径**: `e:\github\ooder-skills\skills\_base\skill-spi-llm`

**包含文件**:
```
src/main/java/net/ooder/spi/llm/
├── LlmService.java           # 核心SPI接口
├── LlmStreamHandler.java     # 流式处理接口
└── model/
    ├── LlmConfig.java         # 配置模型
    ├── LlmModel.java          # 模型定义
    ├── LlmRequest.java        # 请求模型
    └── LlmResponse.java       # 响应模型
```

**任务清单**:
- [ ] 创建目标目录结构
- [ ] 复制源代码文件
- [ ] 更新pom.xml版本号为3.0.1
- [ ] 创建README.md文档
- [ ] 创建skill.yaml配置
- [ ] 编译测试

### 2.2 skill-llm-config (LLM配置管理)

**源路径**: `E:\apex\os\skills\_business\skill-llm-config`
**目标路径**: `e:\github\ooder-skills\skills\capabilities\llm\skill-llm-config`

**包含文件**:
```
src/main/java/net/ooder/skill/llm/config/
├── controller/
│   ├── EmbeddingConfigController.java
│   ├── LlmConfigController.java
│   ├── LlmController.java
│   ├── LlmKnowledgeConfigController.java
│   └── LlmProviderController.java
├── dto/ (20+ DTO文件)
├── model/
│   └── ResultModel.java
├── service/
│   ├── ApiKeyProvider.java
│   ├── LlmConfigConverter.java
│   └── LlmConfigService.java
├── LlmConfigAutoConfiguration.java
└── LlmConfigInitializer.java
```

**任务清单**:
- [ ] 创建目标目录结构
- [ ] 复制源代码文件
- [ ] 更新pom.xml版本号为3.0.1
- [ ] 更新依赖引用
- [ ] 创建README.md文档
- [ ] 更新skill.yaml配置
- [ ] 编译测试

### 2.3 skill-llm-monitor (LLM监控)

**源路径**: `E:\apex\os\skills\_drivers\llm\skill-llm-monitor`
**目标路径**: `e:\github\ooder-skills\skills\_drivers\llm\skill-llm-monitor`

**包含文件**:
```
src/main/java/net/ooder/skill/llm/monitor/
├── controller/
│   ├── LlmMonitorController.java
│   └── LlmMonitorService.java
├── dto/ (17+ DTO文件)
└── model/
    └── ResultModel.java
```

**任务清单**:
- [ ] 创建目标目录结构
- [ ] 复制源代码文件
- [ ] 更新pom.xml版本号为3.0.1
- [ ] 创建README.md文档
- [ ] 创建skill.yaml配置
- [ ] 编译测试

### 2.4 skill-scenes (场景管理)

**源路径**: `E:\apex\os\skills\_business\skill-scenes`
**目标路径**: `e:\github\ooder-skills\skills\capabilities\scenes\skill-scenes`

**包含文件**:
```
src/main/java/net/ooder/skill/scenes/
├── controller/
│   ├── SceneController.java
│   └── SceneGroupController.java
├── dto/
│   ├── SceneCapabilityDTO.java
│   ├── SceneDTO.java
│   └── SceneGroupDTO.java
├── entity/
│   └── SceneGroup.java
├── model/
│   ├── PageResult.java
│   └── ResultModel.java
├── repository/
│   └── SceneGroupRepository.java
├── service/
│   ├── impl/
│   │   ├── SceneGroupServiceImpl.java
│   │   └── SceneServiceImpl.java
│   ├── SceneGroupService.java
│   └── SceneService.java
└── ScenesAutoConfiguration.java
```

**任务清单**:
- [ ] 创建目标目录结构
- [ ] 复制源代码文件
- [ ] 更新pom.xml版本号为3.0.1
- [ ] 创建README.md文档
- [ ] 更新skill.yaml配置
- [ ] 编译测试

---

## 三、P1优先级迁移任务

### 3.1 skill-context (上下文管理)

**源路径**: `E:\apex\os\skills\_business\skill-context`
**状态**: ⏳ 待迁移

### 3.2 skill-todo (待办管理)

**源路径**: `E:\apex\os\skills\_business\skill-todo`
**状态**: ⏳ 待迁移

### 3.3 skill-keys (密钥管理)

**源路径**: `E:\apex\os\skills\_business\skill-keys`
**状态**: ⏳ 待迁移

### 3.4 skill-selector (能力选择器)

**源路径**: `E:\apex\os\skills\_business\skill-selector`
**状态**: ⏳ 待迁移

### 3.5 skill-security (安全管理)

**源路径**: `E:\apex\os\skills\_business\skill-security`
**状态**: ⏳ 待迁移

### 3.6 skill-procedure (流程管理)

**源路径**: `E:\apex\os\skills\_business\skill-procedure`
**状态**: ⏳ 待迁移

### 3.7 skill-installer (安装器)

**源路径**: `E:\apex\os\skills\_business\skill-installer`
**状态**: ⏳ 待迁移

### 3.8 skill-knowledge (知识库)

**源路径**: `E:\apex\os\skills\_business\skill-knowledge`
**状态**: ⏳ 待迁移

---

## 四、LLM配置审计检查清单

### 4.1 文档审计

| 检查项 | skill-llm-base | skill-llm-deepseek | skill-llm-openai | skill-llm-qianwen | skill-llm-volcengine | skill-llm-ollama | skill-llm-baidu |
|--------|----------------|--------------------|--------------------|--------------------|----------------------|------------------|-----------------|
| README.md | ✅ 已创建 | ✅ 已创建 | ✅ 已创建 | ✅ 已创建 | ✅ 已存在 | ✅ 已创建 | ✅ 已创建 |
| skill.yaml | ⚠️ 缺失 | ✅ 存在 | ✅ 存在 | ✅ 存在 | ✅ 存在 | ✅ 存在 | ✅ 存在 |
| 配置说明 | ✅ 已补充 | ✅ 已补充 | ✅ 已补充 | ✅ 已补充 | ✅ 已补充 | ✅ 已补充 | ✅ 已补充 |
| API文档 | ⚠️ 待补充 | ⚠️ 待补充 | ⚠️ 待补充 | ⚠️ 待补充 | ⚠️ 待补充 | ⚠️ 待补充 | ⚠️ 待补充 |

### 4.2 代码审计

| 检查项 | 说明 | 状态 |
|--------|------|------|
| LlmProvider接口 | 统一接口定义 | ✅ 已验证 |
| 配置加密 | API Key加密存储 | ⚠️ 待验证 |
| 流式输出 | SSE流式支持 | ✅ 已验证 |
| 错误处理 | 异常处理机制 | ⚠️ 待验证 |
| 日志记录 | 调用日志 | ⚠️ 待验证 |

### 4.3 依赖审计

| 模块 | 依赖项 | 版本 | 状态 |
|------|--------|------|------|
| skill-llm-base | 无外部依赖 | - | ✅ |
| skill-llm-deepseek | llm-sdk | >=2.3.1 | ⚠️ 待验证 |
| skill-llm-openai | llm-sdk | >=2.3.1 | ⚠️ 待验证 |
| skill-llm-qianwen | llm-sdk | >=2.3.1 | ⚠️ 待验证 |

---

## 五、执行进度

| 阶段 | 任务 | 状态 | 完成时间 |
|------|------|------|----------|
| Phase 4.1 | 补充LLM驱动README | ✅ 完成 | 2026-04-03 |
| Phase 4.2 | 创建LLM配置指南 | ✅ 完成 | 2026-04-03 |
| Phase 4.3 | 迁移skill-spi-llm | ⏳ 进行中 | - |
| Phase 4.4 | 迁移skill-llm-config | ⏳ 待执行 | - |
| Phase 4.5 | 迁移skill-llm-monitor | ⏳ 待执行 | - |
| Phase 4.6 | 迁移skill-scenes | ⏳ 待执行 | - |
| Phase 5 | 批量审计检查 | ⏳ 待执行 | - |

---

## 六、变更记录

| 日期 | 版本 | 变更内容 |
|------|------|----------|
| 2026-04-03 | v1.0 | 初始创建，定义迁移任务列表 |

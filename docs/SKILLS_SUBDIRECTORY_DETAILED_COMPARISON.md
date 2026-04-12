# Skills 子目录详细对比报告

## 对比概要

**对比时间**: 2026-04-11  
**源目录**: `E:\apex\os\skills` (开发中的skills)  
**目标目录**: `e:\github\ooder-skills\skills` (库中的skills)

---

## 一、_base 目录详细对比

### 1.1 ooder-spi-core

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 完整+额外类 | 目标有额外的database/, document/, vector/包 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md, skill.yaml | 目标有文档 |
| **额外类** | ❌ 无 | ✅ database/, document/, vector/ | 目标有额外的SPI接口 |

**关键差异**:
- 目标目录有额外的SPI接口：
  - `database/` - 数据库SPI
  - `document/` - 文档解析SPI
  - `vector/` - 向量存储SPI
- 源目录有编译产物（应忽略）

### 1.2 skill-spi-core

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 完整 | 相同 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md, skill.yaml | 目标有文档 |

### 1.3 skill-spi-llm

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ❌ **缺失** | ⚠️ 目标缺少源代码 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md, skill.yaml | 目标有文档 |

**⚠️ 重要发现**: 目标目录缺少 `skill-spi-llm` 的源代码！

### 1.4 skill-spi-messaging

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 完整 | 相同 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md, skill.yaml | 目标有文档 |

---

## 二、_business 目录详细对比

### 2.1 skill-context

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 简化 | 源有更完整的实现 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **配置** | ✅ spring.factories | ✅ AutoConfiguration.imports | 配置方式不同 |

**源目录独有的类**:
- `MultiLevelContextManager` - 多级上下文管理器
- `ContextUpdate` - 上下文更新
- `GlobalContextConfig` - 全局上下文配置
- `PageNavigateEvent` - 页面导航事件

### 2.2 skill-driver-config

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 简化 | 源有更完整的实现 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

**源目录独有的类**:
- `DriverConfigController` - 驱动配置控制器
- `AllDriversTestResultDTO` - 所有驱动测试结果
- `DriverTestResultDTO` - 驱动测试结果

### 2.3 skill-install-scene

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 简化 | 源有更完整的实现 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

**源目录独有的类**:
- `InstallSceneController` - 安装场景控制器
- `InstallProgress` - 安装进度
- `InstallSkillDTO` - 安装技能DTO
- `InstallStepDTO` - 安装步骤DTO

### 2.4 skill-installer

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 完整 | 相同 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

### 2.5 skill-keys

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 完整 | 相同 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

### 2.6 skill-knowledge-backup-20260403-162917

**⚠️ 仅源目录存在** - 这是一个备份目录，包含完整的知识库实现

### 2.7 skill-procedure

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 完整 | 相同 |
| **编译产物** | ✅ 无jar | ❌ 无 | 源无jar |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

### 2.8 skill-scenes

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ **非常完整** | ✅ 简化 | ⚠️ 源有更完整的实现 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **测试代码** | ✅ 完整测试 | ❌ 无 | 源有测试代码 |
| **数据库脚本** | ✅ SQL脚本 | ❌ 无 | 源有数据库脚本 |

**源目录独有的重要内容**:
- **完整的场景管理实现**:
  - `SceneGroupService` - 场景组服务
  - `SceneService` - 场景服务
  - `SceneGroupRepository` - 场景组仓库
  - `JpaSceneGroupStorage` - JPA存储实现
- **数据库脚本**:
  - `metrics-schema.sql` - 指标数据库
  - `scene-group-config-schema.sql` - 场景组配置
  - `template-schema.sql` - 模板数据库
  - `workflow-schema.sql` - 工作流数据库
- **测试代码**:
  - `MetricsIntegrationTest` - 指标集成测试
  - `SceneGroupConfigIntegrationTest` - 场景组配置测试
  - `TemplateIntegrationTest` - 模板测试
  - `WorkflowIntegrationTest` - 工作流测试

**⚠️ 重要发现**: 源目录有更完整的场景管理实现，包括数据库支持和测试！

### 2.9 skill-security

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 完整 | 相同 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

### 2.10 skill-selector

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 简化 | 源有更完整的实现 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **配置** | ✅ spring.factories | ✅ AutoConfiguration.imports | 配置方式不同 |

### 2.11 skill-todo

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **源代码** | ✅ 完整 | ✅ 完整 | 相同 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

### 2.12 skill-llm-config

**⚠️ 仅目标目录存在**

### 2.13 编译产物（仅源目录）

**应忽略的文件**:
- `skill-capability-ext-1.0.0.jar`
- `skill-config-ext-1.0.0.jar`
- `skill-context-1.0.0.jar`
- `skill-install-ext-1.0.0.jar`
- `skill-llm-config-1.0.0.jar`
- `skill-llm-provider-1.0.0.jar`
- `skill-scene-core-1.0.0.jar`

---

## 三、_drivers 目录详细对比

### 3.1 IM驱动对比

#### skill-im-dingding

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **配置文件** | ✅ skill.yaml | ✅ skill.yaml | 相同 |
| **实现代码** | ❌ 无 | ✅ **完整实现** | 目标有完整实现 |
| **POM** | ❌ 无 | ✅ pom.xml | 目标有POM |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

**目标目录独有**:
- 完整的Java实现代码
- Spring Boot自动配置
- 钉钉SDK集成

#### skill-im-feishu

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **配置文件** | ✅ skill.yaml | ✅ skill.yaml | 相同 |
| **实现代码** | ❌ 无 | ✅ **完整实现** | 目标有完整实现 |
| **POM** | ❌ 无 | ✅ pom.xml | 目标有POM |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

**目标目录独有**:
- 完整的Java实现代码
- Spring Boot自动配置
- 飞书SDK集成

#### skill-im-weixin

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **配置文件** | ✅ skill.yaml | ✅ skill.yaml | 相同 |
| **实现代码** | ❌ 无 | ✅ **完整实现** | 目标有完整实现 |
| **POM** | ❌ 无 | ✅ pom.xml | 目标有POM |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

**目标目录独有**:
- 完整的Java实现代码
- Spring Boot自动配置
- 微信SDK集成

#### skill-im-wecom

**⚠️ 仅目标目录存在** - 企业微信驱动

### 3.2 LLM驱动对比

#### skill-llm-base

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **配置文件** | ✅ skill.yaml | ✅ skill.yaml | 相同 |
| **POM** | ✅ pom.xml | ✅ pom.xml | 相同 |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

#### skill-llm-deepseek

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **配置文件** | ✅ skill.yaml | ✅ skill.yaml | 相同 |
| **POM** | ❌ 无 | ✅ pom.xml | 目标有POM |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

#### skill-llm-monitor

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **配置文件** | ✅ skill.yaml | ✅ skill.yaml | 相同 |
| **POM** | ✅ pom.xml | ✅ pom.xml | 相同 |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

#### 其他LLM驱动

**⚠️ 仅目标目录存在**:
- `skill-llm-baidu` - 百度千帆
- `skill-llm-ollama` - Ollama本地
- `skill-llm-openai` - OpenAI
- `skill-llm-qianwen` - 通义千问
- `skill-llm-volcengine` - 火山引擎

### 3.3 其他驱动对比

#### skill-org-web

**⚠️ 仅源目录存在** - 组织Web服务

#### skill-rag

| 对比项 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| **配置文件** | ✅ skill.yaml | ✅ skill.yaml | 相同 |
| **POM** | ✅ pom.xml | ✅ pom.xml | 相同 |
| **编译产物** | ✅ target/, jar | ❌ 无 | 源有编译产物 |
| **文档** | ❌ 无 | ✅ README.md | 目标有文档 |

#### skill-spi

**⚠️ 仅源目录存在** - SPI驱动（有编译产物）

#### bpm

**⚠️ 仅目标目录存在** - 完整的BPM设计器实现

#### document

**⚠️ 仅目标目录存在** - 文档处理驱动

---

## 四、_system 目录详细对比

### 4.1 主要差异总结

| 模块名 | 源目录特点 | 目标目录特点 | 关键差异 |
|--------|-----------|-------------|---------|
| skill-agent | 有jar | 有README | 源有编译产物 |
| skill-audit | 有jar | 有README | 源有编译产物 |
| skill-auth | 有jar | 有README | 源有编译产物 |
| skill-capability | 有target | 有README | 源有编译产物 |
| skill-config | 有jar | 有README | 源有编译产物 |
| skill-dashboard | 有target | 有README | 源有编译产物 |
| skill-dict | 有jar | 有README | 源有编译产物 |
| skill-discovery | 有target, apidocs | 有README | 源有编译产物和API文档 |
| skill-history | 有target | 有README | 源有编译产物 |
| skill-im-gateway | 有pom | 有README | 目标有文档 |
| skill-install | 有target | 有README | 源有编译产物 |
| skill-key | 有target | 有README | 源有编译产物 |
| skill-knowledge-platform | 有pom, skill.yaml | 有README | 目标有文档 |
| skill-llm-chat | 有docs, target | 有docs, README | 源有编译产物 |
| skill-management | 有target | 有README | 源有编译产物 |
| skill-menu | 有jar | 有README | 源有编译产物 |
| skill-messaging | 有target | 有README | 源有编译产物 |
| skill-notification | 有pom, skill.yaml | 有README | 目标有文档 |
| skill-org | 有jar | 有README | 源有编译产物 |
| skill-protocol | 有README, pom, skill.yaml | 有README, pom, skill.yaml | 相同 |
| skill-rag | **不存在** | **存在** | 仅目标有 |
| skill-role | 有target | 有README | 源有编译产物 |
| skill-scene | 有jar | 有README | 源有编译产物 |
| skill-setup | 有jar | 有README | 源有编译产物 |
| skill-support | 有target | 有README | 源有编译产物 |
| skill-template | 有target | 有README | 源有编译产物 |
| skill-tenant | 有jar | 有README | 源有编译产物 |
| skill-vfs | 有pom | 有README | 目标有文档 |
| skill-workflow | 有target | 有README | 源有编译产物 |
| skill-knowledge | **不存在** | **存在** | 仅目标有 |
| skills-bpm-demo | **不存在** | **存在** | 仅目标有 |

---

## 五、关键发现汇总

### 5.1 ⚠️ 重要缺失（需要从源目录补充）

#### 1. skill-spi-llm 源代码缺失
- **位置**: `_base/skill-spi-llm/src/`
- **状态**: 目标目录缺少源代码
- **影响**: 高 - LLM SPI接口定义缺失
- **建议**: 必须从源目录复制

#### 2. skill-scenes 完整实现缺失
- **位置**: `_business/skill-scenes/`
- **状态**: 目标目录实现简化
- **缺失内容**:
  - 数据库脚本（4个SQL文件）
  - 测试代码（4个测试类）
  - 完整的服务实现
- **影响**: 高 - 场景管理功能不完整
- **建议**: 必须从源目录补充

#### 3. skill-org-web 驱动缺失
- **位置**: `_drivers/org/skill-org-web/`
- **状态**: 目标目录不存在
- **影响**: 中 - 组织Web服务缺失
- **建议**: 从源目录复制

#### 4. skill-spi 驱动缺失
- **位置**: `_drivers/spi/skill-spi/`
- **状态**: 目标目录不存在
- **影响**: 中 - SPI驱动缺失
- **建议**: 评估后决定是否复制

### 5.2 ✅ 目标目录优势

#### 1. 完整的IM驱动实现
- `skill-im-dingding` - 钉钉驱动（完整实现）
- `skill-im-feishu` - 飞书驱动（完整实现）
- `skill-im-weixin` - 微信驱动（完整实现）
- `skill-im-wecom` - 企业微信驱动

#### 2. 完整的LLM Provider
- `skill-llm-baidu` - 百度千帆
- `skill-llm-ollama` - Ollama
- `skill-llm-openai` - OpenAI
- `skill-llm-qianwen` - 通义千问
- `skill-llm-volcengine` - 火山引擎

#### 3. 完整的BPM实现
- `_drivers/bpm/` - BPM设计器

#### 4. 完整的场景和工具
- `capabilities/` - 能力模块
- `scenes/` - 场景模块
- `tools/` - 工具模块

#### 5. 额外的SPI接口
- `ooder-spi-core` 中的 database, document, vector SPI

### 5.3 📝 文档差异

| 类型 | 源目录 | 目标目录 | 说明 |
|------|-------|---------|------|
| README.md | ❌ 缺少 | ✅ 完整 | 目标有详细文档 |
| skill.yaml | ✅ 有 | ✅ 有 | 两边都有 |
| API文档 | ✅ 部分有 | ❌ 缺少 | 源有API文档 |
| DTO规范文档 | ✅ 有 | ❌ 缺少 | 源有规范文档 |

### 5.4 🔧 编译产物

**应忽略的内容**:
- 所有 `target/` 目录
- 所有 `*.jar` 文件
- 所有 `*.class` 文件

---

## 六、推荐合并方案（更新）

### 6.1 必须合并的内容

#### 优先级 P0（必须）

1. **skill-spi-llm 源代码**
   ```powershell
   Copy-Item -Path "E:\apex\os\skills\_base\skill-spi-llm\src" -Destination "e:\github\ooder-skills\skills\_base\skill-spi-llm\src" -Recurse
   ```

2. **skill-scenes 完整实现**
   ```powershell
   # 复制数据库脚本
   Copy-Item -Path "E:\apex\os\skills\_business\skill-scenes\src\main\resources\db" -Destination "e:\github\ooder-skills\skills\_business\skill-scenes\src\main\resources\db" -Recurse
   
   # 复制测试代码
   Copy-Item -Path "E:\apex\os\skills\_business\skill-scenes\src\test" -Destination "e:\github\ooder-skills\skills\_business\skill-scenes\src\test" -Recurse
   
   # 复制完整的服务实现
   # 需要手动合并，避免覆盖目标已有的简化实现
   ```

#### 优先级 P1（重要）

3. **skill-org-web 驱动**
   ```powershell
   Copy-Item -Path "E:\apex\os\skills\_drivers\org\skill-org-web" -Destination "e:\github\ooder-skills\skills\_drivers\org\skill-org-web" -Recurse
   ```

4. **DTO规范文档**
   ```powershell
   Copy-Item -Path "E:\apex\os\skills\DTO转换规范*.md" -Destination "e:\github\ooder-skills\docs\"
   ```

#### 优先级 P2（可选）

5. **skill-spi 驱动**（评估后决定）
6. **API文档**（如果有价值）
7. **skill-knowledge-backup**（备份目录，可能不需要）

### 6.2 不应合并的内容

1. ❌ 所有编译产物（target/, *.jar）
2. ❌ 目标目录已有的完整实现（IM驱动、LLM Provider等）
3. ❌ capabilities/, scenes/, tools/ 目录（目标已有完整实现）
4. ❌ .archive/ 目录（归档内容）

### 6.3 需要手动合并的内容

1. **skill-scenes 服务实现**
   - 源目录有更完整的实现
   - 目标目录有简化的实现
   - 需要手动对比和合并

2. **ooder-spi-core 额外SPI**
   - 目标有额外的database, document, vector SPI
   - 源目录没有这些
   - 保留目标的实现

---

## 七、执行计划

### 阶段1：备份（必须）
```powershell
# 创建备份
Copy-Item -Path "e:\github\ooder-skills\skills" -Destination "e:\github\ooder-skills\skills_backup_20260411_detailed" -Recurse
```

### 阶段2：合并P0优先级内容
```powershell
# 1. 合并 skill-spi-llm 源代码
Copy-Item -Path "E:\apex\os\skills\_base\skill-spi-llm\src" -Destination "e:\github\ooder-skills\skills\_base\skill-spi-llm\src" -Recurse

# 2. 合并 skill-scenes 数据库脚本和测试
Copy-Item -Path "E:\apex\os\skills\_business\skill-scenes\src\main\resources\db" -Destination "e:\github\ooder-skills\skills\_business\skill-scenes\src\main\resources\db" -Recurse
Copy-Item -Path "E:\apex\os\skills\_business\skill-scenes\src\test" -Destination "e:\github\ooder-skills\skills\_business\skill-scenes\src\test" -Recurse
```

### 阶段3：合并P1优先级内容
```powershell
# 3. 合并 skill-org-web
Copy-Item -Path "E:\apex\os\skills\_drivers\org\skill-org-web" -Destination "e:\github\ooder-skills\skills\_drivers\org\skill-org-web" -Recurse

# 4. 合并文档
Copy-Item -Path "E:\apex\os\skills\DTO转换规范*.md" -Destination "e:\github\ooder-skills\docs\"
```

### 阶段4：验证
```powershell
# 验证编译
mvn clean compile

# 验证测试
mvn test

# 检查git状态
git status
```

---

## 八、风险评估

### 8.1 合并风险

| 风险项 | 风险等级 | 缓解措施 |
|--------|----------|---------|
| 覆盖目标完整实现 | 🔴 高 | 只复制目标缺失的内容 |
| 编译失败 | 🟡 中 | 合并后立即验证编译 |
| 测试失败 | 🟡 中 | 合并后运行测试 |
| 依赖冲突 | 🟡 中 | 检查pom.xml依赖 |

### 8.2 不合并风险

| 风险项 | 风险等级 | 影响 |
|--------|----------|------|
| skill-spi-llm 缺失 | 🔴 高 | LLM功能不完整 |
| skill-scenes 不完整 | 🔴 高 | 场景管理功能受限 |
| skill-org-web 缺失 | 🟡 中 | 组织Web服务不可用 |

---

## 九、决策建议

### 推荐：执行阶段1-4的完整合并方案

**理由**：
1. 补充关键缺失的源代码（skill-spi-llm）
2. 完善场景管理功能（skill-scenes）
3. 添加组织Web服务（skill-org-web）
4. 保留目标目录的所有优势
5. 风险可控，易于回滚

---

**生成时间**: 2026-04-11  
**等待批准**: 请确认后执行

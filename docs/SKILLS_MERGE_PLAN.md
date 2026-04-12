# Skills 目录对比与合并方案

## 对比概要

**对比时间**: 2026-04-11  
**源目录**: `E:\apex\os\skills` (开发中的skills)  
**目标目录**: `e:\github\ooder-skills\skills` (库中的skills)

## 一、目录结构对比

### 1.1 顶层目录对比

| 目录名 | 源目录(os) | 目标目录(github) | 差异说明 |
|--------|-----------|-----------------|---------|
| `_base/` | ✅ 存在 | ✅ 存在 | 内容有差异 |
| `_business/` | ✅ 存在 | ✅ 存在 | 内容有差异 |
| `_drivers/` | ✅ 存在 | ✅ 存在 | 内容有差异 |
| `_system/` | ✅ 存在 | ✅ 存在 | 内容有差异 |
| `capabilities/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `scenes/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `tools/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `.archive/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `config/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |

### 1.2 _base 目录对比

| 模块名 | 源目录 | 目标目录 | 差异 |
|--------|-------|---------|------|
| `ooder-spi-core/` | ✅ pom.xml | ✅ pom.xml, README.md, skill.yaml | 目标更完整 |
| `skill-spi-core/` | ✅ pom.xml | ✅ pom.xml, README.md, skill.yaml | 目标更完整 |
| `skill-spi-llm/` | ✅ pom.xml, target/jar | ✅ pom.xml, README.md, skill.yaml | 源有jar，目标有文档 |
| `skill-spi-messaging/` | ✅ pom.xml | ✅ pom.xml, README.md, skill.yaml | 目标更完整 |

### 1.3 _business 目录对比

| 模块名 | 源目录 | 目标目录 | 差异 |
|--------|-------|---------|------|
| `skill-context/` | ✅ pom.xml, skill.yaml, target | ✅ pom.xml, skill.yaml | 源有编译产物 |
| `skill-driver-config/` | ✅ pom.xml, skill.yaml | ✅ pom.xml, skill.yaml, README.md | 目标有README |
| `skill-install-scene/` | ✅ pom.xml, skill.yaml | ✅ pom.xml, skill.yaml, README.md | 目标有README |
| `skill-installer/` | ✅ pom.xml, skill.yaml, target | ✅ pom.xml, skill.yaml, README.md | 源有编译产物 |
| `skill-keys/` | ✅ pom.xml, skill.yaml, target/jar | ✅ pom.xml, skill.yaml, README.md | 源有jar，目标有README |
| `skill-procedure/` | ✅ pom.xml, skill.yaml | ✅ pom.xml, skill.yaml, README.md | 目标有README |
| `skill-scenes/` | ✅ pom.xml, skill.yaml, target | ✅ pom.xml, skill.yaml | 源有编译产物 |
| `skill-security/` | ✅ pom.xml, skill.yaml, target | ✅ pom.xml, skill.yaml, README.md | 源有编译产物 |
| `skill-selector/` | ✅ pom.xml, skill.yaml, target | ✅ pom.xml, skill.yaml | 源有编译产物 |
| `skill-todo/` | ✅ pom.xml, skill.yaml, target/jar | ✅ pom.xml, skill.yaml, README.md | 源有jar，目标有README |
| `skill-llm-config/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `skill-capability-ext-1.0.0.jar` | ✅ 存在 | ❌ 不存在 | 仅源目录有 |
| `skill-config-ext-1.0.0.jar` | ✅ 存在 | ❌ 不存在 | 仅源目录有 |
| `skill-context-1.0.0.jar` | ✅ 存在 | ❌ 不存在 | 仅源目录有 |
| `skill-install-ext-1.0.0.jar` | ✅ 存在 | ❌ 不存在 | 仅源目录有 |
| `skill-llm-config-1.0.0.jar` | ✅ 存在 | ❌ 不存在 | 仅源目录有 |
| `skill-llm-provider-1.0.0.jar` | ✅ 存在 | ❌ 不存在 | 仅源目录有 |
| `skill-scene-core-1.0.0.jar` | ✅ 存在 | ❌ 不存在 | 仅源目录有 |

### 1.4 _drivers 目录对比

#### IM驱动

| 模块名 | 源目录 | 目标目录 | 差异 |
|--------|-------|---------|------|
| `skill-im-dingding/` | ✅ skill.yaml | ✅ pom.xml, skill.yaml, README.md, src/ | 目标有完整实现 |
| `skill-im-feishu/` | ✅ skill.yaml | ✅ pom.xml, skill.yaml, README.md, src/ | 目标有完整实现 |
| `skill-im-weixin/` | ✅ skill.yaml | ✅ pom.xml, skill.yaml, README.md, src/ | 目标有完整实现 |
| `skill-im-wecom/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |

#### LLM驱动

| 模块名 | 源目录 | 目标目录 | 差异 |
|--------|-------|---------|------|
| `skill-llm-base/` | ✅ pom.xml, skill.yaml | ✅ pom.xml, skill.yaml, README.md | 目标有README |
| `skill-llm-deepseek/` | ✅ skill.yaml | ✅ pom.xml, skill.yaml, README.md | 目标有pom和README |
| `skill-llm-monitor/` | ✅ pom.xml, skill.yaml | ✅ pom.xml, skill.yaml, README.md | 目标有README |
| `skill-llm-baidu/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `skill-llm-ollama/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `skill-llm-openai/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `skill-llm-qianwen/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `skill-llm-volcengine/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |

#### 其他驱动

| 模块名 | 源目录 | 目标目录 | 差异 |
|--------|-------|---------|------|
| `skill-org-web/` | ✅ pom.xml, skill.yaml | ❌ 不存在 | 仅源目录有 |
| `skill-rag/` | ✅ pom.xml, skill.yaml, target/jar | ✅ pom.xml, skill.yaml, README.md | 源有jar，目标有README |
| `skill-spi/` | ✅ pom.xml, skill.yaml, target/jar | ❌ 不存在 | 仅源目录有 |
| `bpm/` | ❌ 不存在 | ✅ 存在（完整实现） | 仅目标目录有 |
| `document/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |

### 1.5 _system 目录对比

| 模块名 | 源目录 | 目标目录 | 差异 |
|--------|-------|---------|------|
| `skill-agent/` | ✅ docs/, target/jar | ✅ docs/, README.md, pom.xml, skill.yaml | 源有jar |
| `skill-audit/` | ✅ target/jar | ✅ README.md, pom.xml, skill.yaml | 源有jar |
| `skill-auth/` | ✅ target/jar | ✅ README.md, pom.xml, skill.yaml | 源有jar |
| `skill-capability/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-config/` | ✅ target/jar | ✅ README.md, pom.xml, skill.yaml | 源有jar |
| `skill-dashboard/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-dict/` | ✅ target/jar | ✅ README.md, pom.xml, skill.yaml | 源有jar |
| `skill-discovery/` | ✅ target, apidocs | ✅ README.md, pom.xml, skill.yaml | 源有编译产物和文档 |
| `skill-history/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-im-gateway/` | ✅ pom.xml | ✅ README.md, pom.xml | 目标有README |
| `skill-install/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-key/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-knowledge-platform/` | ✅ pom.xml, skill.yaml | ✅ README.md, pom.xml, skill.yaml | 目标有README |
| `skill-llm-chat/` | ✅ docs/, target | ✅ docs/, README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-management/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-menu/` | ✅ target/jar | ✅ README.md, pom.xml, skill.yaml | 源有jar |
| `skill-messaging/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-notification/` | ✅ pom.xml, skill.yaml | ✅ README.md, pom.xml, skill.yaml | 目标有README |
| `skill-org/` | ✅ target/jar | ✅ README.md, pom.xml, skill.yaml | 源有jar |
| `skill-protocol/` | ✅ README.md, pom.xml, skill.yaml | ✅ README.md, pom.xml, skill.yaml | 相同 |
| `skill-rag/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `skill-role/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-scene/` | ✅ target/jar | ✅ README.md, pom.xml, skill.yaml | 源有jar |
| `skill-setup/` | ✅ target/jar | ✅ README.md, pom.xml, skill.yaml | 源有jar |
| `skill-support/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-template/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-tenant/` | ✅ target/jar | ✅ README.md, pom.xml, skill.yaml | 源有jar |
| `skill-vfs/` | ✅ pom.xml | ✅ README.md, pom.xml | 目标有README |
| `skill-workflow/` | ✅ target | ✅ README.md, pom.xml, skill.yaml | 源有编译产物 |
| `skill-knowledge/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |
| `skills-bpm-demo/` | ❌ 不存在 | ✅ 存在 | 仅目标目录有 |

## 二、关键差异分析

### 2.1 源目录独有的内容

#### 编译产物（应忽略）
- `target/` 目录下的所有编译产物
- `*.jar` 文件
- 这些文件不应合并，因为可以从源码重新编译

#### 特定模块
- `skill-org-web/` - 组织Web服务
- `skill-spi/` - SPI驱动

#### 文档文件
- `DTO转换规范完整改进审计报告.md`
- `DTO转换规范审计报告.md`
- `DTO转换规范改进审计报告.md`
- `DTO转换规范检查列表.md`
- `SKILL.md`

### 2.2 目标目录独有的内容

#### 完整的模块实现
- `capabilities/` - 能力模块目录（完整的实现）
- `scenes/` - 场景模块目录（完整的实现）
- `tools/` - 工具模块目录（完整的实现）
- `.archive/` - 归档目录
- `config/` - 配置目录

#### BPM相关
- `_drivers/bpm/` - 完整的BPM设计器实现
- `skills-bpm-demo/` - BPM演示

#### 额外的LLM Provider
- `skill-llm-baidu/`
- `skill-llm-ollama/`
- `skill-llm-openai/`
- `skill-llm-qianwen/`
- `skill-llm-volcengine/`

#### 额外的IM Provider
- `skill-im-wecom/` - 企业微信

#### 文档和图表
- `OODER_AGENT_UPGRADE_BLOG.md`
- 多个架构图SVG文件

## 三、合并方案

### 3.1 合并原则

1. **保护现有代码**: 不删除目标目录中已有的完整实现
2. **增量合并**: 只添加源目录中有价值的新内容
3. **忽略编译产物**: 不合并target目录和jar文件
4. **保留文档**: 合并有价值的文档

### 3.2 推荐合并方案

#### 方案A：保守合并（推荐）

**只合并以下内容**：

1. **源目录独有的模块**
   - `skill-org-web/` - 组织Web服务
   - `skill-spi/` - SPI驱动（如果目标需要）

2. **源目录的文档文件**
   - `DTO转换规范*.md` 文件
   - `SKILL.md`

3. **更新已有模块的配置**
   - 如果源目录的 `skill.yaml` 有更新，手动合并

**不合并**：
- ❌ 编译产物（target/, *.jar）
- ❌ 目标目录已有的完整实现
- ❌ capabilities/, scenes/, tools/ 目录（目标已有）

#### 方案B：选择性合并

**合并以下内容**：

1. **方案A的所有内容**

2. **更新已有模块**
   - 对比 `_base/`, `_business/`, `_drivers/`, `_system/` 下的模块
   - 手动合并有差异的配置文件

3. **合并README文档**
   - 如果源目录缺少README，从目标复制
   - 如果目标缺少README，从源复制

**不合并**：
- ❌ 编译产物
- ❌ 目标独有的完整实现

#### 方案C：完全合并（不推荐）

**风险**：可能覆盖目标目录的完整实现

**不推荐原因**：
- 目标目录有完整的IM驱动实现
- 目标目录有完整的BPM实现
- 目标目录有capabilities, scenes, tools等完整目录
- 源目录主要是配置文件，缺少实现代码

### 3.3 具体执行步骤（方案A）

#### 步骤1：备份目标目录
```powershell
# 创建备份
Copy-Item -Path "e:\github\ooder-skills\skills" -Destination "e:\github\ooder-skills\skills_backup_20260411" -Recurse
```

#### 步骤2：复制源目录独有的模块
```powershell
# 复制 skill-org-web
Copy-Item -Path "E:\apex\os\skills\_drivers\org\skill-org-web" -Destination "e:\github\ooder-skills\skills\_drivers\org\skill-org-web" -Recurse

# 复制 skill-spi（如果需要）
Copy-Item -Path "E:\apex\os\skills\_drivers\spi\skill-spi" -Destination "e:\github\ooder-skills\skills\_drivers\spi\skill-spi" -Recurse
```

#### 步骤3：复制文档文件
```powershell
# 复制文档
Copy-Item -Path "E:\apex\os\skills\DTO转换规范*.md" -Destination "e:\github\ooder-skills\docs\" 
Copy-Item -Path "E:\apex\os\skills\SKILL.md" -Destination "e:\github\ooder-skills\docs\"
```

#### 步骤4：验证合并结果
```powershell
# 检查git状态
git status

# 验证新增的模块
ls e:\github\ooder-skills\skills\_drivers\org\skill-org-web
ls e:\github\ooder-skills\skills\_drivers\spi\skill-spi
```

## 四、风险评估

### 4.1 方案A风险
- 🟢 **低风险**: 只添加新内容，不修改现有代码
- 🟢 **可回滚**: 可以轻松删除新增的文件

### 4.2 方案B风险
- 🟡 **中风险**: 可能覆盖配置文件
- 🟡 **需要手动验证**: 需要逐个对比文件差异

### 4.3 方案C风险
- 🔴 **高风险**: 可能丢失目标目录的完整实现
- 🔴 **不可逆**: 难以恢复被覆盖的内容

## 五、建议

### 5.1 推荐方案
**推荐使用方案A（保守合并）**

**理由**：
1. 目标目录已经有完整的实现
2. 源目录主要是配置文件和编译产物
3. 风险最低，易于回滚
4. 可以保留源目录独有的有价值内容

### 5.2 后续工作

合并后需要：
1. ✅ 验证新增模块可以正常编译
2. ✅ 更新索引文件
3. ✅ 更新文档
4. ✅ 提交git

## 六、决策点

请选择合并方案：

- [ ] **方案A**：保守合并（推荐）- 只添加新模块和文档
- [ ] **方案B**：选择性合并 - 添加新内容并更新已有配置
- [ ] **方案C**：完全合并 - 不推荐
- [ ] **自定义方案**：请说明具体需求

---

**生成时间**: 2026-04-11  
**等待批准**: 请选择方案后执行

# 场景与技能功能列表 v2.3.1

> **版本**: 2.3.1  
> **更新日期**: 2026-03-16  
> **维护团队**: Skills Team

---

## 一、使用视角 - 用户功能列表

### 1.1 技能发现与安装

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 技能市场浏览 | 浏览可安装的技能包 | ✅ | GET /api/v1/discovery/capabilities |
| 技能搜索 | 按名称/分类搜索技能 | ✅ | GET /api/v1/discovery/github/search |
| 技能详情查看 | 查看技能详细信息 | ✅ | GET /api/v1/discovery/capabilities/detail/{id} |
| 技能安装 | 安装技能到本地 | ✅ | POST /api/v1/discovery/install |
| 安装进度查看 | 查看安装进度 | ✅ | GET /api/v1/installs/{id}/progress |
| 依赖检查 | 检查技能依赖 | ✅ | GET /api/v1/templates/{id}/dependencies/health |
| 依赖自动安装 | 自动安装缺失依赖 | ⚠️ | POST /api/v1/templates/{id}/dependencies/auto-install |

### 1.2 场景激活与管理

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 场景激活 | 激活已安装的场景 | ✅ | POST /api/v1/activations/{id}/start |
| 激活流程查看 | 查看激活步骤 | ✅ | GET /api/v1/activations/{id}/process |
| 步骤执行 | 执行激活步骤 | ✅ | POST /api/v1/activations/{id}/steps/{stepId}/execute |
| 步骤跳过 | 跳过可选步骤 | ✅ | POST /api/v1/activations/{id}/steps/{stepId}/skip |
| 密钥配置 | 配置场景密钥 | ✅ | POST /api/v1/activations/{id}/key |
| 私有能力配置 | 配置个人能力 | ✅ | POST /api/v1/activations/{id}/private-capabilities/configure |
| 激活确认 | 确认激活完成 | ✅ | POST /api/v1/activations/{id}/activate |
| 激活取消 | 取消激活流程 | ✅ | POST /api/v1/activations/{id}/cancel |
| SSE流式激活 | 实时激活进度 | ✅ | GET /api/v1/activations/{id}/stream |

### 1.3 场景使用

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 我的场景列表 | 查看我参与的场景 | ✅ | GET /api/v1/scene-groups/my/participated |
| 场景详情 | 查看场景详情 | ✅ | GET /api/v1/scene-groups/{id} |
| 场景能力列表 | 查看场景绑定的能力 | ✅ | GET /api/v1/scene-groups/{id}/capabilities |
| 能力调用 | 调用场景能力 | ⚠️ | POST /api/v1/discovery/capabilities/invoke |
| 快照创建 | 创建场景快照 | ✅ | POST /api/v1/scene-groups/{id}/snapshots |
| 快照恢复 | 恢复场景快照 | ✅ | POST /api/v1/scene-groups/{id}/snapshots/{sid}/restore |
| 场景停用 | 停用场景 | ✅ | POST /api/v1/scene-groups/{id}/deactivate |

### 1.4 LLM对话

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 智能对话 | 与LLM对话 | ✅ | POST /api/llm/chat |
| 流式对话 | SSE流式对话 | ⚠️ | POST /api/llm/chat/stream |
| Provider列表 | 获取LLM提供者 | ✅ | POST /api/llm/providers |
| 模型列表 | 获取可用模型 | ✅ | POST /api/llm/models |
| 模型切换 | 切换LLM模型 | ✅ | POST /api/llm/models/set |
| 文本补全 | 文本补全 | ✅ | POST /api/llm/complete |
| 文本翻译 | 文本翻译 | ✅ | POST /api/llm/translate |
| 文本摘要 | 文本摘要 | ✅ | POST /api/llm/summarize |

### 1.5 知识库使用

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 知识库列表 | 查看知识库列表 | ✅ | GET /api/v1/knowledge-bases |
| 知识库详情 | 查看知识库详情 | ✅ | GET /api/v1/knowledge-bases/{id} |
| 索引重建 | 重建知识库索引 | ✅ | POST /api/v1/knowledge-bases/{id}/rebuild-index |
| 知识库绑定 | 绑定知识库到场景 | ✅ | POST /api/v1/scene-groups/{id}/knowledge-bases |

### 1.6 待办与历史

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 待办列表 | 查看待办事项 | ✅ | GET /api/v1/my/todos |
| 接受待办 | 接受待办任务 | ✅ | POST /api/v1/my/todos/{id}/accept |
| 拒绝待办 | 拒绝待办任务 | ✅ | POST /api/v1/my/todos/{id}/reject |
| 完成待办 | 完成待办任务 | ✅ | POST /api/v1/my/todos/{id}/complete |
| 审批待办 | 审批待办任务 | ✅ | POST /api/v1/my/todos/{id}/approve |
| 历史记录 | 查看操作历史 | ✅ | GET /api/v1/my/history/scenes |
| 历史统计 | 查看历史统计 | ✅ | GET /api/v1/my/history/statistics |
| 历史导出 | 导出历史记录 | ✅ | GET /api/v1/my/history/export |

---

## 二、观察视角 - 管理员功能列表

### 2.1 能力管理

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 能力列表 | 查看所有能力 | ✅ | GET /api/v1/capabilities |
| 能力搜索 | 搜索能力 | ✅ | GET /api/v1/capabilities/search |
| 能力详情 | 查看能力详情 | ✅ | GET /api/v1/capabilities/{id} |
| 能力创建 | 创建能力 | ✅ | POST /api/v1/capabilities |
| 能力更新 | 更新能力 | ✅ | PUT /api/v1/capabilities/{id} |
| 能力删除 | 删除能力 | ✅ | DELETE /api/v1/capabilities/{id} |
| 能力状态更新 | 更新能力状态 | ✅ | POST /api/v1/capabilities/{id}/status |
| 能力分类检测 | 自动分类检测 | ✅ | GET /api/v1/capabilities/{id}/classify |
| 能力同步 | 同步能力数据 | ✅ | POST /api/v1/capabilities/sync |
| 同步状态 | 查看同步状态 | ✅ | GET /api/v1/capabilities/sync/status |

### 2.2 能力绑定管理

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 绑定列表 | 查看能力绑定 | ✅ | GET /api/v1/capabilities/{id}/bindings |
| 创建绑定 | 创建能力绑定 | ✅ | POST /api/v1/capabilities/bindings |
| 绑定详情 | 查看绑定详情 | ✅ | GET /api/v1/capabilities/bindings/{id} |
| 删除绑定 | 删除能力绑定 | ✅ | DELETE /api/v1/capabilities/bindings/{id} |
| 绑定状态更新 | 更新绑定状态 | ✅ | POST /api/v1/capabilities/bindings/{id}/status |
| 按Agent查询 | 按Agent查绑定 | ✅ | GET /api/v1/capabilities/bindings/by-agent/{id} |
| 按Link查询 | 按Link查绑定 | ✅ | GET /api/v1/capabilities/bindings/by-link/{id} |
| 绑定测试 | 测试能力绑定 | ✅ | POST /api/v1/capabilities/bindings/{id}/test |

### 2.3 场景组管理

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 场景组列表 | 查看所有场景组 | ✅ | GET /api/v1/scene-groups |
| 场景组详情 | 查看场景组详情 | ✅ | GET /api/v1/scene-groups/{id} |
| 场景组创建 | 创建场景组 | ✅ | POST /api/v1/scene-groups |
| 场景组更新 | 更新场景组 | ✅ | PUT /api/v1/scene-groups/{id} |
| 场景组删除 | 删除场景组 | ✅ | DELETE /api/v1/scene-groups/{id} |
| 场景组激活 | 激活场景组 | ✅ | POST /api/v1/scene-groups/{id}/activate |
| 场景组停用 | 停用场景组 | ✅ | POST /api/v1/scene-groups/{id}/deactivate |
| 我创建的场景 | 查看我创建的 | ✅ | GET /api/v1/scene-groups/my/created |

### 2.4 参与者管理

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 参与者列表 | 查看场景参与者 | ✅ | GET /api/v1/scene-groups/{id}/participants |
| 参与者详情 | 查看参与者详情 | ✅ | GET /api/v1/scene-groups/{id}/participants/{pid} |
| 添加参与者 | 添加参与者 | ✅ | POST /api/v1/scene-groups/{id}/participants |
| 移除参与者 | 移除参与者 | ✅ | DELETE /api/v1/scene-groups/{id}/participants/{pid} |
| 角色变更 | 变更参与者角色 | ✅ | PUT /api/v1/scene-groups/{id}/participants/{pid}/role |

### 2.5 组织管理

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 用户列表 | 查看用户列表 | ✅ | GET /api/v1/org/users |
| 用户详情 | 查看用户详情 | ✅ | GET /api/v1/org/users/{id} |
| 用户创建 | 创建用户 | ✅ | POST /api/v1/org/users |
| 用户更新 | 更新用户 | ✅ | PUT /api/v1/org/users/{id} |
| 用户删除 | 删除用户 | ✅ | DELETE /api/v1/org/users/{id} |
| 部门列表 | 查看部门列表 | ✅ | GET /api/v1/org/departments |
| 部门详情 | 查看部门详情 | ✅ | GET /api/v1/org/departments/{id} |
| 部门创建 | 创建部门 | ✅ | POST /api/v1/org/departments |
| 部门更新 | 更新部门 | ✅ | PUT /api/v1/org/departments/{id} |
| 部门删除 | 删除部门 | ✅ | DELETE /api/v1/org/departments/{id} |
| 部门成员 | 查看部门成员 | ✅ | GET /api/v1/org/departments/{id}/members |
| 组织树 | 查看组织树 | ✅ | GET /api/v1/org/tree |
| 角色列表 | 查看角色列表 | ✅ | GET /api/v1/org/roles |
| 用户角色更新 | 更新用户角色 | ✅ | PUT /api/v1/org/users/{id}/role |

### 2.6 审计与监控

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 审计日志列表 | 查看审计日志 | ✅ | GET /api/v1/audit/logs |
| 审计日志详情 | 查看日志详情 | ✅ | GET /api/v1/audit/logs/{id} |
| 审计统计 | 审计统计信息 | ✅ | GET /api/v1/audit/stats |
| 日志导出 | 导出审计日志 | ✅ | GET /api/v1/audit/export |
| 技能统计 | 技能统计信息 | ✅ | GET /api/v1/discovery/statistics |

---

## 三、开发配置管理视角 - 开发者功能列表

### 3.1 技能包开发

| 功能 | 描述 | 状态 | 配置文件 |
|------|------|:----:|----------|
| 技能定义 | skill.yaml 定义技能 | ✅ | skill.yaml |
| 能力定义 | capabilities 能力列表 | ✅ | skill.yaml |
| 依赖声明 | dependencies 依赖管理 | ✅ | skill.yaml |
| 场景能力定义 | sceneCapabilities 场景能力 | ✅ | skill.yaml |
| 自驱配置 | mainFirst/selfDrive 自驱规则 | ✅ | skill.yaml |
| 知识库配置 | knowledge 知识库配置 | ✅ | skill.yaml |
| LLM助手配置 | persona/llmAssistant | ✅ | skill.yaml |
| 运行时配置 | runtime 运行时参数 | ✅ | skill.yaml |

### 3.2 配置管理

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 系统配置 | 查看系统配置 | ✅ | GET /api/v1/config/system |
| 能力配置 | 查看能力配置 | ✅ | GET /api/v1/config/system/capabilities/{addr} |
| 更新能力配置 | 更新能力配置 | ✅ | PUT /api/v1/config/system/capabilities/{addr} |
| 技能配置 | 查看技能配置 | ✅ | GET /api/v1/config/skills/{id} |
| 技能继承链 | 查看配置继承 | ✅ | GET /api/v1/config/skills/{id}/inheritance |
| 更新技能配置 | 更新技能配置 | ✅ | PUT /api/v1/config/skills/{id} |
| 重置技能配置 | 重置配置项 | ✅ | DELETE /api/v1/config/skills/{id}/keys/{key} |
| 场景配置 | 查看场景配置 | ✅ | GET /api/v1/config/scenes/{id} |
| 场景继承链 | 查看配置继承 | ✅ | GET /api/v1/config/scenes/{id}/inheritance |
| 更新场景配置 | 更新场景配置 | ✅ | PUT /api/v1/config/scenes/{id} |
| 重置场景配置 | 重置配置项 | ✅ | DELETE /api/v1/config/scenes/{id}/keys/{key} |
| 继承链查询 | 通用继承链查询 | ✅ | GET /api/v1/config/inheritance/{type}/{id} |

### 3.3 地址空间配置

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 能力分类列表 | 查看能力分类 | ✅ | GET /api/v1/config/categories |
| 地址列表 | 查看地址列表 | ✅ | GET /api/v1/config/addresses |
| 地址详情 | 查看地址详情 | ✅ | GET /api/v1/config/addresses/{addr} |
| 驱动列表 | 查看驱动列表 | ✅ | GET /api/v1/config/drivers |
| 驱动配置列表 | 查看驱动配置 | ✅ | GET /api/v1/config/driver-configs |
| 保存驱动配置 | 保存驱动配置 | ✅ | POST /api/v1/config/driver-configs |
| 测试连接 | 测试驱动连接 | ✅ | POST /api/v1/config/test-connection/{id} |

### 3.4 模板管理

| 功能 | 描述 | 状态 | API |
|------|------|:----:|-----|
| 模板列表 | 查看模板列表 | ✅ | GET /api/v1/templates |
| 模板详情 | 查看模板详情 | ✅ | GET /api/v1/templates/{id} |
| 模板部署 | 部署模板 | ✅ | POST /api/v1/templates/{id}/deploy |
| SSE流式部署 | 流式部署 | ✅ | GET /api/v1/templates/{id}/deploy/stream |
| 安装依赖 | 安装模板依赖 | ✅ | POST /api/v1/templates/{id}/install |
| 依赖健康检查 | 检查依赖健康 | ✅ | GET /api/v1/templates/{id}/dependencies/health |
| 缺失依赖 | 查看缺失依赖 | ✅ | GET /api/v1/templates/{id}/dependencies/missing |
| 安装缺失依赖 | 安装缺失依赖 | ✅ | POST /api/v1/templates/{id}/dependencies/install-missing |

### 3.5 分类枚举配置

| 配置文件 | 描述 | 状态 | 位置 |
|----------|------|:----:|------|
| skillCategories | 技能分类枚举 | ✅ | config/categories.yaml |
| capabilityCategories | 能力地址分类 | ✅ | config/categories.yaml |
| businessCategories | 业务分类枚举 | ✅ | config/categories.yaml |
| skillForms | 技能形态枚举 | ✅ | config/categories.yaml |
| schema.yaml | 配置规范定义 | ✅ | config/schema.yaml |
| addresses.yaml | 能力地址配置 | ✅ | config/addresses.yaml |

### 3.6 技能索引管理

| 功能 | 描述 | 状态 | 文件 |
|------|------|:----:|------|
| 技能索引 | 主技能清单 | ✅ | skill-index.yaml |
| 技能分类 | 分类配置 | ✅ | skill-classification.yaml |
| 技能模板 | 标准模板 | ✅ | templates/skill.yaml |
| 场景模板 | 场景技能模板 | ✅ | templates/skill-scene-v2.3.yaml |

---

## 四、功能完整度统计

### 4.1 按视角统计

| 视角 | 总功能数 | 已实现 | 部分实现 | 未实现 | 完整度 |
|------|:--------:|:------:|:--------:|:------:|:------:|
| **使用视角** | 45 | 38 | 5 | 2 | 89% |
| **观察视角** | 48 | 46 | 2 | 0 | 96% |
| **开发配置视角** | 42 | 40 | 2 | 0 | 95% |
| **总计** | 135 | 124 | 9 | 2 | **93%** |

### 4.2 按模块统计

| 模块 | 功能数 | 完整度 |
|------|:------:|:------:|
| 技能发现与安装 | 7 | 93% |
| 场景激活与管理 | 9 | 100% |
| 场景使用 | 7 | 93% |
| LLM对话 | 8 | 88% |
| 知识库使用 | 4 | 100% |
| 待办与历史 | 9 | 100% |
| 能力管理 | 10 | 100% |
| 能力绑定管理 | 8 | 100% |
| 场景组管理 | 8 | 100% |
| 参与者管理 | 5 | 100% |
| 组织管理 | 14 | 100% |
| 审计与监控 | 5 | 100% |
| 技能包开发 | 8 | 100% |
| 配置管理 | 12 | 100% |
| 地址空间配置 | 7 | 100% |
| 模板管理 | 8 | 100% |
| 分类枚举配置 | 6 | 100% |
| 技能索引管理 | 4 | 100% |

---

## 五、待完善功能清单

### 5.1 使用视角

| 功能 | 当前状态 | 待完善内容 |
|------|----------|------------|
| 依赖自动安装 | ⚠️ 部分 | 外部服务集成 |
| 能力调用 | ⚠️ 部分 | 真实调用集成 |
| 流式对话 | ⚠️ 模拟 | 真实流式传输 |

### 5.2 观察视角

| 功能 | 当前状态 | 待完善内容 |
|------|----------|------------|
| Git发现 | ⚠️ Mock | 真实Git API集成 |

### 5.3 开发配置视角

| 功能 | 当前状态 | 待完善内容 |
|------|----------|------------|
| 配置预览 | ❌ 未实现 | 合并预览功能 |
| 配置验证 | ⚠️ 部分 | Schema验证完善 |

---

## 六、API端点汇总

### 6.1 按路径前缀分类

| 路径前缀 | 端点数 | 说明 |
|----------|:------:|------|
| /api/v1/capabilities | 18 | 能力管理 |
| /api/v1/scene-groups | 25 | 场景组管理 |
| /api/v1/activations | 11 | 激活服务 |
| /api/v1/installs | 8 | 安装服务 |
| /api/v1/discovery | 14 | 能力发现 |
| /api/llm | 11 | LLM服务 |
| /api/v1/auth | 7 | 认证授权 |
| /api/v1/org | 15 | 组织管理 |
| /api/v1/knowledge-bases | 6 | 知识库 |
| /api/v1/templates | 7 | 模板服务 |
| /api/v1/audit | 5 | 审计日志 |
| /api/v1/my | 11 | 个人中心 |
| /api/v1/config | 12 | 配置管理 |
| /api/scenes | 15 | 场景管理(旧) |
| /api/v1/skills | 9 | 技能服务 |
| **总计** | **174** | |

---

**文档版本**: 2.3.1  
**更新日期**: 2026-03-16

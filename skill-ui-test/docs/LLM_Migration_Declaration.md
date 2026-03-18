# LLM 功能模块移植委托声明

## 委托方
**项目团队**: skill-ui-test  
**负责人**: [待填写]  
**日期**: 2026-03-08

## 受托方
**团队**: Skills 开发团队  
**任务**: 将 LLM 相关功能模块移植到 skill-llm-chat 技能中

---

## 一、移植背景

当前项目中 LLM 功能分散在多个目录和模块中，存在以下问题：
1. **代码分散**: LLM 相关代码分布在 `src/main/java` 和 `skills/` 目录
2. **重复实现**: 存在两套 LLM 配置服务（sdk 和 skill-llm-config-manager）
3. **维护困难**: 多处代码需要同步更新，容易遗漏
4. **结构混乱**: 前端、后端、配置未统一组织

**目标**: 将所有 LLM 功能统一整合到 `skill-llm-chat` 技能中，实现单一职责、统一管理。

---

## 二、移植范围

### 2.1 需要移植的文件清单

#### A. 后端 Java 文件 (Backend)

| 序号 | 源文件路径 | 目标路径 | 文件类型 | 优先级 |
|------|-----------|---------|---------|--------|
| 1 | `src/main/java/net/ooder/skill/test/controller/LlmChatController.java` | `skills/skill-llm-chat/src/.../controller/` | Controller | P0 |
| 2 | `src/main/java/net/ooder/skill/test/controller/DeepSeekLlmProviderController.java` | `skills/skill-llm-chat/src/.../controller/` | Controller | P0 |
| 3 | `src/main/java/net/ooder/skill/test/controller/BaiduLlmProviderController.java` | `skills/skill-llm-chat/src/.../controller/` | Controller | P0 |
| 4 | `src/main/java/net/ooder/skill/test/service/LLMService.java` | `skills/skill-llm-chat/src/.../service/` | Service | P0 |
| 5 | `src/main/java/net/ooder/skill/llm/service/LlmConfigService.java` | `skills/skill-llm-chat/src/.../service/` | Service Interface | P1 |
| 6 | `src/main/java/net/ooder/skill/llm/service/impl/LlmConfigServiceImpl.java` | `skills/skill-llm-chat/src/.../service/impl/` | Service Impl | P1 |
| 7 | `src/main/java/net/ooder/skill/llm/model/LlmConfig.java` | `skills/skill-llm-chat/src/.../model/` | Model | P1 |
| 8 | `src/main/java/net/ooder/skill/llm/controller/LlmConfigController.java` | `skills/skill-llm-chat/src/.../controller/` | Controller | P1 |
| 9 | `src/main/java/net/ooder/skill/test/model/ChatRequest.java` | `skills/skill-llm-chat/src/.../model/` | Model | P2 |
| 10 | `src/main/java/net/ooder/skill/test/model/Provider.java` | `skills/skill-llm-chat/src/.../model/` | Model | P2 |

#### B. 前端 HTML 文件 (Frontend)

| 序号 | 源文件路径 | 目标路径 | 文件类型 | 优先级 |
|------|-----------|---------|---------|--------|
| 1 | `skills/skill-llm-chat/ui/pages/index.html` | 保持不变 | HTML | P0 |
| 2 | `skills/skill-llm-management-ui/ui/pages/index.html` | `skills/skill-llm-chat/ui/pages/config.html` | HTML | P0 |
| 3 | `skills/skill-llm-deepseek/ui/pages/index.html` | `skills/skill-llm-chat/ui/pages/deepseek.html` | HTML | P1 |
| 4 | `skills/skill-llm-baidu/ui/pages/index.html` | `skills/skill-llm-chat/ui/pages/baidu.html` | HTML | P1 |

#### C. 前端 JavaScript 文件

| 序号 | 源文件路径 | 目标路径 | 文件类型 | 优先级 |
|------|-----------|---------|---------|--------|
| 1 | `skills/skill-llm-chat/ui/js/skill-llm-chat.js` | 保持不变 | JS | P0 |
| 2 | `skills/skill-llm-management-ui/ui/js/llm-management.js` | `skills/skill-llm-chat/ui/js/llm-config.js` | JS | P0 |
| 3 | `skills/skill-llm-management-ui/ui/js/pages/llm-management-init.js` | `skills/skill-llm-chat/ui/js/pages/` | JS | P1 |

#### D. 前端 CSS 文件

| 序号 | 源文件路径 | 目标路径 | 文件类型 | 优先级 |
|------|-----------|---------|---------|--------|
| 1 | `skills/skill-llm-chat/ui/css/skill-llm-chat.css` | 保持不变 | CSS | P0 |
| 2 | `skills/skill-llm-management-ui/ui/css/llm-management.css` | `skills/skill-llm-chat/ui/css/llm-config.css` | CSS | P0 |

#### E. 配置文件

| 序号 | 源文件路径 | 目标路径 | 文件类型 | 优先级 |
|------|-----------|---------|---------|--------|
| 1 | `skills/skill-llm-chat/skill.yaml` | 合并更新 | YAML | P0 |
| 2 | `skills/skill-llm-management-ui/skill.yaml` | 合并到 skill-llm-chat | YAML | P0 |
| 3 | `skills/skill-llm-deepseek/skill.yaml` | 合并到 skill-llm-chat | YAML | P1 |
| 4 | `skills/skill-llm-baidu/skill.yaml` | 合并到 skill-llm-chat | YAML | P1 |
| 5 | `skills/skill-llm-config-manager/src/main/resources/skill.yaml` | 合并到 skill-llm-chat | YAML | P2 |

---

## 三、移植要求

### 3.1 代码规范
1. **包名规范**: 统一使用 `net.ooder.skill.llm.chat` 作为根包名
2. **类名规范**: 保持原有类名，避免破坏现有接口
3. **注释规范**: 保留原有注释，添加移植标记 `@since 1.0.0-migrated`
4. **API 兼容**: 保持原有 API 路径不变，确保前端无需修改

### 3.2 功能要求
1. **功能完整**: 确保移植后所有功能正常工作
2. **配置合并**: 将多个 skill.yaml 合并为一个统一的配置
3. **依赖处理**: 正确处理技能间的依赖关系
4. **测试覆盖**: 移植完成后需通过功能测试

### 3.3 目录结构
移植后的 `skill-llm-chat` 目录结构应为：

```
skill-llm-chat/
├── skill.yaml                          # 合并后的配置
├── ui/
│   ├── pages/
│   │   ├── index.html                  # 主对话页面
│   │   ├── config.html                 # 配置管理页面
│   │   ├── deepseek.html               # DeepSeek配置
│   │   └── baidu.html                  # 百度配置
│   ├── js/
│   │   ├── skill-llm-chat.js           # 对话逻辑
│   │   ├── llm-config.js               # 配置管理逻辑
│   │   └── pages/
│   │       └── llm-management-init.js
│   └── css/
│       ├── skill-llm-chat.css          # 对话样式
│       └── llm-config.css              # 配置样式
└── src/main/java/net/ooder/skill/llm/chat/
    ├── controller/
    │   ├── LlmChatController.java
    │   ├── LlmConfigController.java
    │   ├── DeepSeekLlmProviderController.java
    │   └── BaiduLlmProviderController.java
    ├── service/
    │   ├── LLMService.java
    │   ├── LlmConfigService.java
    │   └── impl/
    │       └── LlmConfigServiceImpl.java
    └── model/
        ├── LlmConfig.java
        ├── ChatRequest.java
        └── Provider.java
```

---

## 四、验收标准

### 4.1 功能验收
- [ ] LLM 对话功能正常
- [ ] 配置管理功能正常
- [ ] DeepSeek 模型调用正常
- [ ] 百度文心模型调用正常
- [ ] 所有 API 接口返回正确

### 4.2 代码验收
- [ ] 所有文件已移植到正确位置
- [ ] 代码编译无错误
- [ ] 无重复代码
- [ ] 代码注释完整

### 4.3 测试验收
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 前端页面正常显示
- [ ] 功能测试通过

---

## 五、时间安排

| 阶段 | 任务 | 预计时间 | 截止日期 |
|------|------|---------|---------|
| 第一阶段 | 后端代码移植 | 2天 | [待填写] |
| 第二阶段 | 前端代码移植 | 1天 | [待填写] |
| 第三阶段 | 配置合并 | 0.5天 | [待填写] |
| 第四阶段 | 测试验证 | 1天 | [待填写] |
| **总计** | | **4.5天** | |

---

## 六、联系方式

**委托方联系人**: [待填写]  
**受托方负责人**: [待填写]  
**沟通方式**: [待填写]

---

## 七、签字确认

**委托方**: _________________ 日期: _________________

**受托方**: _________________ 日期: _________________

---

# 附录：源文件与目标文件对比清单

## 后端文件对比

| 序号 | 源文件 | 目标文件 | 状态 | 备注 |
|------|--------|---------|------|------|
| 1 | `src/.../controller/LlmChatController.java` | `skills/skill-llm-chat/.../controller/LlmChatController.java` | ⬜ 待移植 | 核心控制器 |
| 2 | `src/.../controller/DeepSeekLlmProviderController.java` | `skills/skill-llm-chat/.../controller/DeepSeekLlmProviderController.java` | ⬜ 待移植 | DeepSeek提供者 |
| 3 | `src/.../controller/BaiduLlmProviderController.java` | `skills/skill-llm-chat/.../controller/BaiduLlmProviderController.java` | ⬜ 待移植 | 百度提供者 |
| 4 | `src/.../service/LLMService.java` | `skills/skill-llm-chat/.../service/LLMService.java` | ⬜ 待移植 | LLM服务 |
| 5 | `src/.../llm/service/LlmConfigService.java` | `skills/skill-llm-chat/.../service/LlmConfigService.java` | ⬜ 待移植 | 配置服务接口 |
| 6 | `src/.../llm/service/impl/LlmConfigServiceImpl.java` | `skills/skill-llm-chat/.../service/impl/LlmConfigServiceImpl.java` | ⬜ 待移植 | 配置服务实现 |
| 7 | `src/.../llm/model/LlmConfig.java` | `skills/skill-llm-chat/.../model/LlmConfig.java` | ⬜ 待移植 | 配置实体 |
| 8 | `src/.../llm/controller/LlmConfigController.java` | `skills/skill-llm-chat/.../controller/LlmConfigController.java` | ⬜ 待移植 | 配置控制器 |

## 前端文件对比

| 序号 | 源文件 | 目标文件 | 状态 | 备注 |
|------|--------|---------|------|------|
| 1 | `skills/skill-llm-chat/ui/pages/index.html` | 保持不变 | ✅ 已存在 | 主页面 |
| 2 | `skills/skill-llm-management-ui/ui/pages/index.html` | `skills/skill-llm-chat/ui/pages/config.html` | ⬜ 待移植 | 配置页面 |
| 3 | `skills/skill-llm-deepseek/ui/pages/index.html` | `skills/skill-llm-chat/ui/pages/deepseek.html` | ⬜ 待移植 | DeepSeek页面 |
| 4 | `skills/skill-llm-baidu/ui/pages/index.html` | `skills/skill-llm-chat/ui/pages/baidu.html` | ⬜ 待移植 | 百度页面 |
| 5 | `skills/skill-llm-chat/ui/js/skill-llm-chat.js` | 保持不变 | ✅ 已存在 | 对话JS |
| 6 | `skills/skill-llm-management-ui/ui/js/llm-management.js` | `skills/skill-llm-chat/ui/js/llm-config.js` | ⬜ 待移植 | 配置JS |
| 7 | `skills/skill-llm-chat/ui/css/skill-llm-chat.css` | 保持不变 | ✅ 已存在 | 对话CSS |
| 8 | `skills/skill-llm-management-ui/ui/css/llm-management.css` | `skills/skill-llm-chat/ui/css/llm-config.css` | ⬜ 待移植 | 配置CSS |

## 配置文件对比

| 序号 | 源文件 | 目标文件 | 状态 | 备注 |
|------|--------|---------|------|------|
| 1 | `skills/skill-llm-chat/skill.yaml` | 合并后的 skill.yaml | ⬜ 待合并 | 主配置 |
| 2 | `skills/skill-llm-management-ui/skill.yaml` | 合并到 skill-llm-chat | ⬜ 待合并 | 管理UI配置 |
| 3 | `skills/skill-llm-deepseek/skill.yaml` | 合并到 skill-llm-chat | ⬜ 待合并 | DeepSeek配置 |
| 4 | `skills/skill-llm-baidu/skill.yaml` | 合并到 skill-llm-chat | ⬜ 待合并 | 百度配置 |

---

**说明**:
- ✅ 已存在：文件已在目标位置，无需移植
- ⬜ 待移植：需要移植的文件
- 🔄 待合并：需要合并配置的文件

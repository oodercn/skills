# SE SDK 协同开发报告

## 一、报告概述

| 项目 | 说明 |
|-----|------|
| **报告日期** | 2026-03-10 |
| **项目名称** | skill-scene v2.3.1 |
| **SE SDK版本** | v2.3.1 |
| **报告人** | skill-scene团队 |
| **文档版本** | v1.0 |

---

## 二、SE SDK问题清单

### 2.1 高优先级问题

| 编号 | 问题 | 影响范围 | 建议解决方案 | 状态 |
|-----|------|---------|-------------|------|
| SE-001 | `SceneEngineAutoConfiguration`类不存在 | 启动失败 | 在SE SDK中添加该配置类或移除spring.factories引用 | 待修复 |
| SE-002 | `SkillCategory`枚举缺少导入路径说明 | 编译问题 | 在文档中明确导入路径`net.ooder.scene.skill.model.SkillCategory` | 待修复 |
| SE-003 | 分类体系文档不完整 | 开发困难 | 提供完整的v3.0分类体系迁移指南 | 待修复 |

### 2.2 中优先级问题

| 编号 | 问题 | 影响范围 | 建议解决方案 | 状态 |
|-----|------|---------|-------------|------|
| SE-004 | `ToolOrchestrator.parseToolCalls`不支持流式解析 | LLM对接 | 添加流式响应解析方法 | 待评估 |
| SE-005 | `SkillActivationContext`缺少知识库绑定接口 | 知识库集成 | 添加`bindKnowledgeBase()`方法 | 待评估 |

---

## 三、需要SE SDK支持的功能

| 功能模块 | 具体需求 | 优先级 | 预期接口 |
|---------|---------|--------|---------|
| **安装流程** | 完整的能力安装/卸载生命周期管理 | 高 | `CapabilityInstallLifecycle` |
| **知识库集成** | 向量索引生成和检索接口 | 高 | `KnowledgeBindingService` |
| **通知推送** | 统一的消息推送接口（邮件/企微/钉钉） | 高 | `NotificationService` |
| **数据持久化** | 能力注册信息的持久化存储接口 | 中 | `CapabilityStorageService` |

---

## 四、skill-scene待完成任务分解

### 4.1 阶段一：核心功能完善（高优先级）

| 任务ID | 任务描述 | 预估工时 | 依赖SE | 负责人 | 状态 |
|--------|---------|---------|--------|--------|------|
| SC-001 | 实现真实的安装流程（下载/解析/安装） | 4h | 否 | - | 待开始 |
| SC-002 | 实现推送通知功能 | 3h | 是(SE-006) | - | 阻塞 |
| SC-003 | 添加数据持久化层 | 3h | 否 | - | 待开始 |
| SC-004 | 完善CapabilityClassificationService | 2h | 否 | - | 已完成 |

### 4.2 阶段二：知识库集成（中优先级）

| 任务ID | 任务描述 | 预估工时 | 依赖SE | 负责人 | 状态 |
|--------|---------|---------|--------|--------|------|
| SC-005 | 对接向量数据库 | 4h | 是(SE-005) | - | 阻塞 |
| SC-006 | 实现知识库索引生成 | 3h | 是(SE-005) | - | 阻塞 |
| SC-007 | 实现跨层知识检索 | 2h | 否 | - | 待开始 |

### 4.3 阶段三：LLM对接优化（低优先级）

| 任务ID | 任务描述 | 预估工时 | 依赖SE | 负责人 | 状态 |
|--------|---------|---------|--------|--------|------|
| SC-008 | 优化流式响应处理 | 2h | 是(SE-004) | - | 阻塞 |
| SC-009 | 添加多轮对话上下文管理 | 2h | 否 | - | 待开始 |
| SC-010 | 完善Function Calling错误处理 | 1h | 否 | - | 待开始 |

---

## 五、SE SDK建议新增接口

### 5.1 通知推送接口

```java
package net.ooder.scene.skill.notification;

public interface NotificationService {
    
    /**
     * 推送消息给指定用户
     */
    void push(String userId, String title, String content, PushChannel channel);
    
    /**
     * 推送消息给场景参与者
     */
    void pushToParticipants(String activationId, NotificationMessage message);
    
    /**
     * 推送渠道枚举
     */
    enum PushChannel {
        EMAIL,      // 邮件
        WECOM,      // 企业微信
        DINGTALK,   // 钉钉
        SMS,        // 短信
        IN_APP      // 站内信
    }
}

public class NotificationMessage {
    private String title;
    private String content;
    private Map<String, Object> data;
    private String actionUrl;
}
```

### 5.2 知识库绑定接口

```java
package net.ooder.scene.skill.knowledge;

public interface KnowledgeBindingService {
    
    /**
     * 绑定知识库到场景
     */
    void bindToScene(String sceneGroupId, String kbId, String layer);
    
    /**
     * 解绑知识库
     */
    void unbindFromScene(String sceneGroupId, String kbId);
    
    /**
     * 检索知识
     */
    List<KnowledgeChunk> searchKnowledge(String sceneGroupId, String query, int topK);
    
    /**
     * 跨层检索
     */
    List<KnowledgeChunk> crossLayerSearch(String sceneGroupId, String query, 
                                           List<String> layers, int topK);
}

public class KnowledgeChunk {
    private String chunkId;
    private String content;
    private String source;
    private double score;
    private String layer;
}
```

### 5.3 能力安装生命周期接口

```java
package net.ooder.scene.skill.install;

public interface CapabilityInstallLifecycle {
    
    /**
     * 安装前回调
     */
    void onPreInstall(Capability capability, InstallContext context);
    
    /**
     * 安装后回调
     */
    void onPostInstall(Capability capability, InstallResult result);
    
    /**
     * 卸载回调
     */
    void onUninstall(Capability capability);
    
    /**
     * 安装失败回调
     */
    void onInstallFailed(Capability capability, Exception error);
}

public class InstallContext {
    private String installId;
    private String userId;
    private Map<String, Object> config;
    private List<String> dependencies;
}

public class InstallResult {
    private boolean success;
    private String message;
    private List<String> installedCapabilities;
}
```

---

## 六、当前完善度评估

### 6.1 模块完善度

| 模块 | 当前完善度 | 目标完善度 | 差距 | 阻塞因素 |
|-----|-----------|-----------|------|---------|
| 安装流程 | 60% | 95% | 35% | 无 |
| 知识库绑定 | 40% | 90% | 50% | SE-005 |
| LLM对接 | 85% | 95% | 10% | SE-004 |
| 推送通知 | 20% | 90% | 70% | SE-006 |
| 数据持久化 | 30% | 95% | 65% | 无 |
| **总体** | **47%** | **93%** | **46%** | - |

### 6.2 功能清单

| 功能 | 实现状态 | 说明 |
|-----|---------|------|
| 能力发现 | ✅ 已实现 | 本地/远程发现 |
| 能力分类 | ✅ 已实现 | SkillForm/SceneType/SkillCategory |
| 角色选择 | ✅ 已实现 | 领导/成员角色 |
| 参与者配置 | ✅ 已实现 | 主导者+协作者 |
| 触发条件 | ✅ 已实现 | 自驱/触发/混合 |
| 依赖检查 | ✅ 已实现 | 自动检查依赖项 |
| 安装执行 | ⚠️ 模拟实现 | 需要完善真实安装逻辑 |
| 推送通知 | ⚠️ 模拟实现 | 需要SE SDK支持 |
| 菜单注册 | ✅ 已实现 | MenuAutoRegisterService |
| 知识库绑定 | ⚠️ 模拟实现 | 需要SE SDK支持 |
| LLM初始化 | ✅ 已实现 | Function Call注册 |

---

## 七、协同请求

### 7.1 请SE SDK团队确认以下事项

| 序号 | 请求事项 | 期望回复时间 |
|-----|---------|-------------|
| 1 | 确认SE-001问题修复时间 | 2026-03-12 |
| 2 | 确认是否支持新增接口（NotificationService、KnowledgeBindingService） | 2026-03-12 |
| 3 | 提供v3.0分类体系完整文档 | 2026-03-11 |
| 4 | 确认ToolOrchestrator流式解析支持计划 | 2026-03-12 |

### 7.2 联系方式

- skill-scene团队负责人：[待填写]
- SE SDK团队负责人：[待填写]
- 协同开发群：[待填写]

---

## 八、附录

### 8.1 相关文件路径

| 文件 | 路径 |
|-----|------|
| CapabilityClassificationService | `src/main/java/net/ooder/skill/scene/capability/service/CapabilityClassificationService.java` |
| InstallServiceImpl | `src/main/java/net/ooder/skill/scene/capability/install/InstallServiceImpl.java` |
| SceneKnowledgeController | `src/main/java/net/ooder/skill/scene/controller/SceneKnowledgeController.java` |
| LlmController | `src/main/java/net/ooder/skill/scene/controller/LlmController.java` |
| DeepSeekLlmProvider | `src/main/java/net/ooder/skill/scene/llm/DeepSeekLlmProvider.java` |

### 8.2 版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|-----|------|---------|--------|
| v1.0 | 2026-03-10 | 初始版本 | skill-scene团队 |

---

**报告生成时间**: 2026-03-10 20:50  
**下次更新时间**: 2026-03-12

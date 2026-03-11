# 智能安装技术设计文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 技术设计 |

---

## 一、系统架构设计

### 1.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           智能安装系统架构                                        │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                           前端层 (Frontend)                               │    │
│  ├─────────────────────────────────────────────────────────────────────────┤    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │    │
│  │  │ 能力发现页面 │  │ 安装向导页面 │  │ 激活配置页面 │  │ LLM对话组件 │     │    │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘     │    │
│  │         │                │                │                │            │    │
│  └─────────┼────────────────┼────────────────┼────────────────┼────────────┘    │
│            │                │                │                │                 │
│            ▼                ▼                ▼                ▼                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                           API层 (REST API)                                │    │
│  ├─────────────────────────────────────────────────────────────────────────┤    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │    │
│  │  │DiscoveryAPI │  │ InstallAPI  │  │ActivationAPI│  │   LLM API   │     │    │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘     │    │
│  │         │                │                │                │            │    │
│  └─────────┼────────────────┼────────────────┼────────────────┼────────────┘    │
│            │                │                │                │                 │
│            ▼                ▼                ▼                ▼                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                           服务层 (Service)                                │    │
│  ├─────────────────────────────────────────────────────────────────────────┤    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │    │
│  │  │DiscoverySvc │  │ InstallSvc  │  │ActivationSvc│  │   LLMSvc    │     │    │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘     │    │
│  │         │                │                │                │            │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │    │
│  │  │TemplateSvc  │  │DependencySvc│  │ MenuAutoReg │  │Notification │     │    │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘     │    │
│  │         │                │                │                │            │    │
│  └─────────┼────────────────┼────────────────┼────────────────┼────────────┘    │
│            │                │                │                │                 │
│            ▼                ▼                ▼                ▼                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                           数据层 (Data)                                   │    │
│  ├─────────────────────────────────────────────────────────────────────────┤    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │    │
│  │  │TemplateRepo │  │ InstallRepo │  │ActivationRepo│  │  MenuRepo   │     │    │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 核心服务设计

```java
// 安装服务接口
public interface InstallService {
    // 创建安装任务
    InstallTask createTask(InstallRequest request);
    
    // 获取安装任务
    InstallTask getTask(String taskId);
    
    // 执行安装
    InstallResult execute(String taskId);
    
    // 回滚安装
    void rollback(String taskId);
    
    // 获取安装进度
    InstallProgress getProgress(String taskId);
}

// 激活服务接口
public interface ActivationService {
    // 创建激活流程
    ActivationProcess createProcess(ActivationRequest request);
    
    // 获取激活流程
    ActivationProcess getProcess(String processId);
    
    // 执行激活步骤
    ActivationProcess executeStep(String processId, String stepId, Map<String, Object> data);
    
    // 完成激活
    ActivationResult complete(String processId);
    
    // 跳过步骤
    ActivationProcess skipStep(String processId, String stepId);
}

// 菜单自动注册服务接口
public interface MenuAutoRegisterService {
    // 激活完成时注册菜单
    void registerMenusOnActivation(String sceneGroupId, String templateId, String userId, String role);
    
    // 场景销毁时移除菜单
    void removeMenusOnSceneDestroy(String sceneGroupId, String userId);
    
    // 获取用户场景菜单
    List<MenuItemDTO> getUserSceneMenus(String userId);
}

// 依赖检查服务接口
public interface DependencyCheckService {
    // 检查依赖
    DependencyCheckResult check(DependencyCheckRequest request);
    
    // 健康检查
    HealthCheckResult healthCheck(String serviceId);
    
    // 批量健康检查
    Map<String, HealthCheckResult> healthCheckAll(List<String> serviceIds);
}
```

---

## 二、更多场景故事

### 2.1 知识问答场景

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  知识问答场景                                                                    │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  场景类型：单用户场景                                                            │
│  发起权限：所有用户                                                              │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ 阶段1：发现与选择                                                         │    │
│  │ [用户] 我想创建一个知识问答助手                                           │    │
│  │ [LLM] 好的！知识问答场景可以让您：                                        │    │
│  │       • 上传文档创建知识库                                               │    │
│  │       • 基于知识库进行智能问答                                           │    │
│  │       • 支持多种文档格式（PDF、Word、TXT）                                │    │
│  │       是否创建？                                                         │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ 阶段2：管理员确认（如需要）                                               │    │
│  │ ┌───────────────────────────────────────────────────────────────────┐  │    │
│  │ │ ✓ LLM服务 - 已安装                                                 │  │    │
│  │ │ ✓ 向量数据库 - 需要配置                                             │  │    │
│  │ │   类型：[Milvus ▼]                                                 │  │    │
│  │ │   地址：[localhost:19530]                                          │  │    │
│  │ │   [测试连接] ○ 待测试                                              │  │    │
│  │ │ ✓ 存储服务 - 已安装                                                │  │    │
│  │ └───────────────────────────────────────────────────────────────────┘  │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ 阶段3：激活配置                                                           │    │
│  │ [LLM] 请配置您的知识问答助手：                                           │    │
│  │                                                                         │    │
│  │       步骤1/2：创建知识库                                                │    │
│  │       ─────────────────────────────────────────                        │    │
│  │       知识库名称：[我的知识库                              ]             │    │
│  │       描述：    [用于存储技术文档和产品资料                ]             │    │
│  │                                                                         │    │
│  │       步骤2/2：配置LLM                                                   │    │
│  │       ─────────────────────────────────────────                        │    │
│  │       选择LLM模型：                                                      │    │
│  │       ○ GPT-4 (推荐)                                                    │    │
│  │       ○ GPT-3.5                                                         │    │
│  │       ○ 本地模型                                                        │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ 阶段4：激活完成                                                           │    │
│  │ [LLM] 🎉 知识问答助手已创建成功！                                         │    │
│  │                                                                         │    │
│  │       📌 您的菜单：                                                      │    │
│  │       ┌──────┐ ┌──────┐ ┌──────┐                                       │    │
│  │       │知识库│ │上传  │ │问答  │                                       │    │
│  │       │管理  │ │文档  │ │对话  │                                       │    │
│  │       └──────┘ └──────┘ └──────┘                                       │    │
│  │                                                                         │    │
│  │       🚀 快速开始：                                                      │    │
│  │       [上传文档] [开始问答]                                              │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 会议管理场景

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  会议管理场景                                                                    │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  场景类型：多角色协作                                                            │
│  发起权限：部门领导、项目经理                                                    │
│                                                                                 │
│  角色：                                                                          │
│  • 会议组织者 - 发起者，创建会议、邀请参与者                                      │
│  • 会议参与者 - 参与者，接收通知、确认参会                                        │
│  • 会议记录员 - 记录员，记录会议纪要                                              │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ 阶段1：发现与选择（会议组织者）                                           │    │
│  │ [领导] 我想创建一个会议管理场景                                           │    │
│  │ [LLM] 会议管理场景需要以下条件：                                         │    │
│  │       • 您需要是部门领导或项目经理                                       │    │
│  │       • 系统需要已配置日历服务                                           │    │
│  │       • 系统需要已配置会议室预约系统                                     │    │
│  │                                                                         │    │
│  │       我已验证您的身份：您是"研发部"的部门领导。                         │    │
│  │       是否继续创建？                                                     │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ 阶段2：管理员确认                                                         │    │
│  │ ┌───────────────────────────────────────────────────────────────────┐  │    │
│  │ │ ✓ MQTT推送服务 - 已安装                                            │  │    │
│  │ │ ✓ 邮件服务 - 已安装                                                │  │    │
│  │ │ ⚠️ 日历服务 - 需要配置                                              │  │    │
│  │ │   类型：[企业微信日历 ▼]                                           │  │    │
│  │ │   企业ID：[wwxxxxxxxxxxxxx]                                        │  │    │
│  │ │   [测试连接] ○ 待测试                                              │  │    │
│  │ │ ⚠️ 会议室系统 - 需要配置                                           │  │    │
│  │ │   类型：[飞书会议室 ▼]                                             │  │    │
│  │ │   应用ID：[cli_xxxxxxxxx]                                          │  │    │
│  │ │   [测试连接] ○ 待测试                                              │  │    │
│  │ └───────────────────────────────────────────────────────────────────┘  │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ 阶段3：参与者激活（会议参与者）                                           │    │
│  │                                                                         │    │
│  │ 步骤1：确认加入                                                          │    │
│  │ ┌───────────────────────────────────────────────────────────────────┐  │    │
│  │ │ 📬 您被邀请加入会议管理场景                                        │  │    │
│  │ │ 发起人：张经理                                                     │  │    │
│  │ │ 您的角色：会议参与者                                               │  │    │
│  │ │                                                                   │  │    │
│  │ │ [拒绝] [确认加入]                                                  │  │    │
│  │ └───────────────────────────────────────────────────────────────────┘  │    │
│  │                                                                         │    │
│  │ 步骤2：配置私有能力                                                      │    │
│  │ ┌───────────────────────────────────────────────────────────────────┐  │    │
│  │ │ 📅 日历能力                                                        │  │    │
│  │ │ 会议管理可以自动同步会议到您的日历。                                │  │    │
│  │ │ 您想启用吗？                                                       │  │    │
│  │ │ [启用日历同步] [暂不启用]                                          │  │    │
│  │ └───────────────────────────────────────────────────────────────────┘  │    │
│  │                                                                         │    │
│  │ 步骤3：确认通知设置                                                      │    │
│  │ ┌───────────────────────────────────────────────────────────────────┐  │    │
│  │ │ ⏰ 通知设置                                                        │  │    │
│  │ │ 当有以下情况时会通知您：                                           │  │    │
│  │ │ ☑ 新会议邀请                                                       │  │    │
│  │ │ ☑ 会议提醒（提前15分钟）                                           │  │    │
│  │ │ ☑ 会议变更通知                                                     │  │    │
│  │ │ ☐ 每日会议摘要                                                     │  │    │
│  │ └───────────────────────────────────────────────────────────────────┘  │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 客户服务场景

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  客户服务场景                                                                    │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  场景类型：多角色协作                                                            │
│  发起权限：客服主管、运营经理                                                    │
│                                                                                 │
│  角色：                                                                          │
│  • 客服主管 - 发起者，配置服务规则、查看统计                                      │
│  • 客服人员 - 参与者，处理客户咨询、记录问题                                      │
│  • 技术支持 - 协作者，处理技术问题、升级工单                                      │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ 阶段1：发现与选择（客服主管）                                             │    │
│  │ [主管] 我想创建一个客户服务场景                                           │    │
│  │ [LLM] 客户服务场景需要以下条件：                                         │    │
│  │       • 您需要是客服主管或运营经理                                       │    │
│  │       • 系统需要已配置工单系统                                           │    │
│  │       • 系统需要已配置客服IM系统                                         │    │
│  │                                                                         │    │
│  │       我已验证您的身份：您是"客服部"的主管。                             │    │
│  │       是否继续创建？                                                     │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ 阶段2：管理员确认                                                         │    │
│  │ ┌───────────────────────────────────────────────────────────────────┐  │    │
│  │ │ ✓ MQTT推送服务 - 已安装                                            │  │    │
│  │ │ ✓ 邮件服务 - 已安装                                                │  │    │
│  │ │ ✓ LLM服务 - 已安装                                                 │  │    │
│  │ │ ⚠️ 工单系统 - 需要配置                                             │  │    │
│  │ │   类型：[Zendesk ▼]                                                │  │    │
│  │ │   域名：[mycompany.zendesk.com]                                    │  │    │
│  │ │   API密钥：[xxxxxxxxxxx]                                           │  │    │
│  │ │   [测试连接] ○ 待测试                                              │  │    │
│  │ │ ⚠️ 客服IM - 需要配置                                               │  │    │
│  │ │   类型：[企业微信客服 ▼]                                           │  │    │
│  │ │   [测试连接] ○ 待测试                                              │  │    │
│  │ └───────────────────────────────────────────────────────────────────┘  │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ 阶段3：客服人员激活                                                       │    │
│  │                                                                         │    │
│  │ 步骤1：确认加入                                                          │    │
│  │ ┌───────────────────────────────────────────────────────────────────┐  │    │
│  │ │ 📬 您被邀请加入客户服务场景                                        │  │    │
│  │ │ 发起人：李主管                                                     │  │    │
│  │ │ 您的角色：客服人员                                                 │  │    │
│  │ │                                                                   │  │    │
│  │ │ [拒绝] [确认加入]                                                  │  │    │
│  │ └───────────────────────────────────────────────────────────────────┘  │    │
│  │                                                                         │    │
│  │ 步骤2：配置私有能力                                                      │    │
│  │ ┌───────────────────────────────────────────────────────────────────┐  │    │
│  │ │ 🤖 AI辅助能力                                                      │  │    │
│  │ │ 客户服务可以使用AI自动生成回复建议。                                │  │    │
│  │ │ 您想启用吗？                                                       │  │    │
│  │ │ [启用AI辅助] [暂不启用]                                            │  │    │
│  │ │                                                                   │  │    │
│  │ │ 📝 知识库能力                                                      │  │    │
│  │ │ 可以关联知识库以获取产品文档和FAQ。                                 │  │    │
│  │ │ 选择知识库：[产品知识库 ▼]                                         │  │    │
│  │ └───────────────────────────────────────────────────────────────────┘  │    │
│  │                                                                         │    │
│  │ 步骤3：确认通知设置                                                      │    │
│  │ ┌───────────────────────────────────────────────────────────────────┐  │    │
│  │ │ ⏰ 通知设置                                                        │  │    │
│  │ │ ☑ 新工单分配                                                       │  │    │
│  │ │ ☑ 工单升级通知                                                     │  │    │
│  │ │ ☑ 客户评价通知                                                     │  │    │
│  │ └───────────────────────────────────────────────────────────────────┘  │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、技术实现方案

### 3.1 安装流程状态机

```java
public enum InstallState {
    DISCOVERED,      // 已发现
    PREVIEWING,      // 预览中
    CONFIGURING,     // 配置中
    DEP_CHECKING,    // 依赖检查中
    DEP_CONFIRMING,  // 依赖确认中（管理员）
    INSTALLING,      // 安装中
    INSTALLED,       // 已安装
    ACTIVATING,      // 激活中
    ACTIVE,          // 已激活
    FAILED,          // 失败
    ROLLED_BACK      // 已回滚
}

public class InstallStateMachine {
    
    private static final Map<InstallState, Set<InstallState>> TRANSITIONS = Map.of(
        InstallState.DISCOVERED, Set.of(InstallState.PREVIEWING),
        InstallState.PREVIEWING, Set.of(InstallState.CONFIGURING, InstallState.DISCOVERED),
        InstallState.CONFIGURING, Set.of(InstallState.DEP_CHECKING, InstallState.PREVIEWING),
        InstallState.DEP_CHECKING, Set.of(InstallState.DEP_CONFIRMING, InstallState.INSTALLING),
        InstallState.DEP_CONFIRMING, Set.of(InstallState.INSTALLING, InstallState.FAILED),
        InstallState.INSTALLING, Set.of(InstallState.INSTALLED, InstallState.FAILED),
        InstallState.INSTALLED, Set.of(InstallState.ACTIVATING),
        InstallState.ACTIVATING, Set.of(InstallState.ACTIVE, InstallState.FAILED),
        InstallState.FAILED, Set.of(InstallState.ROLLED_BACK, InstallState.INSTALLING)
    );
    
    public boolean canTransition(InstallState from, InstallState to) {
        return TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
    }
}
```

### 3.2 激活流程处理器

```java
@Service
public class RoleBasedActivationProcessor {
    
    @Autowired
    private SceneTemplateService templateService;
    
    @Autowired
    private MenuAutoRegisterService menuAutoRegisterService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 根据角色创建激活流程
     */
    public ActivationProcess createProcessByRole(String templateId, String userId, String roleName) {
        SceneTemplateDTO template = templateService.get(templateId);
        
        ActivationProcess process = new ActivationProcess();
        process.setTemplateId(templateId);
        process.setRoleName(roleName);
        process.setActivator(userId);
        
        // 从模板获取该角色的激活步骤
        List<ActivationStepConfig> stepConfigs = template.getActivationSteps(roleName);
        List<ActivationProcess.ActivationStep> steps = new ArrayList<>();
        
        for (ActivationStepConfig config : stepConfigs) {
            ActivationProcess.ActivationStep step = new ActivationProcess.ActivationStep();
            step.setStepId(config.getStepId());
            step.setName(config.getName());
            step.setDescription(config.getDescription());
            step.setRequired(config.isRequired());
            step.setSkippable(config.isSkippable());
            step.setStatus(ActivationProcess.ActivationStep.StepStatus.PENDING);
            steps.add(step);
        }
        
        process.setSteps(steps);
        process.setTotalSteps(steps.size());
        
        // 设置私有能力配置（员工角色）
        if ("EMPLOYEE".equals(roleName) || "TEAM_MEMBER".equals(roleName)) {
            List<PrivateCapabilityConfig> privateCaps = template.getPrivateCapabilities();
            process.setPrivateCapabilities(convertPrivateCaps(privateCaps));
        }
        
        return process;
    }
    
    /**
     * 完成激活后的回调处理
     */
    public ActivationResult onActivationComplete(ActivationProcess process) {
        ActivationResult result = new ActivationResult();
        
        // 1. 注册菜单
        menuAutoRegisterService.registerMenusOnActivation(
            process.getSceneGroupId(),
            process.getTemplateId(),
            process.getActivator(),
            process.getRoleName()
        );
        process.setMenuRegistered(true);
        
        // 2. 绑定私有能力
        if (process.getEnabledPrivateCapabilities() != null) {
            bindPrivateCapabilities(process);
        }
        
        // 3. 发送通知
        notificationService.sendActivationCompleteNotification(process);
        process.setNotificationSent(true);
        
        result.setSuccess(true);
        result.setMessage("激活完成");
        result.setMenuRegistered(true);
        result.setNotificationSent(true);
        
        return result;
    }
}
```

### 3.3 依赖检查服务

```java
@Service
public class DependencyCheckServiceImpl implements DependencyCheckService {
    
    @Autowired
    private LlmService llmService;
    
    @Autowired
    private MqttService mqttService;
    
    @Autowired
    private EmailService emailService;
    
    private final Map<String, HealthChecker> healthCheckers = new HashMap<>();
    
    @PostConstruct
    public void init() {
        healthCheckers.put("llm-service", this::checkLlmHealth);
        healthCheckers.put("mqtt-push", this::checkMqttHealth);
        healthCheckers.put("email-service", this::checkEmailHealth);
        healthCheckers.put("database", this::checkDatabaseHealth);
        healthCheckers.put("vector-db", this::checkVectorDbHealth);
    }
    
    @Override
    public DependencyCheckResult check(DependencyCheckRequest request) {
        DependencyCheckResult result = new DependencyCheckResult();
        result.setTemplateId(request.getTemplateId());
        
        List<DependencyStatus> statuses = new ArrayList<>();
        boolean allPassed = true;
        
        for (DependencyConfig dep : request.getDependencies()) {
            HealthCheckResult healthResult = healthCheck(dep.getSkillId());
            
            DependencyStatus status = new DependencyStatus();
            status.setSkillId(dep.getSkillId());
            status.setName(dep.getDescription());
            status.setInstalled(healthResult.isHealthy());
            status.setAutoInstall(dep.isAutoInstall());
            status.setConfigRequired(!healthResult.isHealthy() && healthResult.isConfigRequired());
            
            if (!healthResult.isHealthy()) {
                allPassed = false;
                status.setConfigUrl(healthResult.getConfigUrl());
                status.setErrorMessage(healthResult.getMessage());
            }
            
            statuses.add(status);
        }
        
        result.setDependencies(statuses);
        result.setAllPassed(allPassed);
        result.setRequiresAdminConfirmation(!allPassed);
        
        return result;
    }
    
    @Override
    public HealthCheckResult healthCheck(String serviceId) {
        HealthChecker checker = healthCheckers.get(serviceId);
        if (checker != null) {
            return checker.check();
        }
        return HealthCheckResult.unknown(serviceId);
    }
    
    private HealthCheckResult checkLlmHealth() {
        try {
            boolean healthy = llmService.testConnection();
            return new HealthCheckResult("llm-service", healthy, false);
        } catch (Exception e) {
            return new HealthCheckResult("llm-service", false, true, e.getMessage());
        }
    }
    
    private HealthCheckResult checkMqttHealth() {
        try {
            boolean healthy = mqttService.testConnection();
            return new HealthCheckResult("mqtt-push", healthy, false);
        } catch (Exception e) {
            return new HealthCheckResult("mqtt-push", false, true, e.getMessage());
        }
    }
    
    private HealthCheckResult checkEmailHealth() {
        try {
            boolean healthy = emailService.testConnection();
            return new HealthCheckResult("email-service", healthy, false);
        } catch (Exception e) {
            return new HealthCheckResult("email-service", false, true, e.getMessage());
        }
    }
}
```

### 3.4 LLM安装助手服务

```java
@Service
public class LlmInstallAssistantService {
    
    @Autowired
    private LlmService llmService;
    
    @Autowired
    private InstallService installService;
    
    @Autowired
    private SceneTemplateService templateService;
    
    /**
     * 理解用户意图
     */
    public IntentResult understandIntent(String userInput) {
        String prompt = buildIntentPrompt(userInput);
        String response = llmService.chat(prompt);
        return parseIntentResponse(response);
    }
    
    /**
     * 生成引导对话
     */
    public String generateGuidance(InstallContext context) {
        String prompt = buildGuidancePrompt(context);
        return llmService.chat(prompt);
    }
    
    /**
     * 解析用户配置
     */
    public Map<String, Object> parseUserConfig(String userInput, InstallContext context) {
        String prompt = buildParseConfigPrompt(userInput, context);
        String response = llmService.chat(prompt);
        return parseConfigResponse(response);
    }
    
    /**
     * 生成安装摘要
     */
    public String generateSummary(InstallTask task) {
        String prompt = buildSummaryPrompt(task);
        return llmService.chat(prompt);
    }
    
    private String buildIntentPrompt(String userInput) {
        return """
            用户输入：%s
            
            请分析用户意图，返回JSON格式：
            {
                "intent": "install|query|configure|cancel",
                "capabilityType": "scene|skill|utility",
                "capabilityName": "能力名称",
                "confidence": 0.95
            }
            """.formatted(userInput);
    }
    
    private String buildGuidancePrompt(InstallContext context) {
        return """
            当前安装阶段：%s
            场景名称：%s
            用户角色：%s
            当前步骤：%s
            
            请生成友好的引导对话，帮助用户完成当前步骤。
            """.formatted(
                context.getPhase(),
                context.getSceneName(),
                context.getUserRole(),
                context.getCurrentStep()
            );
    }
}
```

---

## 四、API接口设计

### 4.1 安装API

```yaml
# 创建安装任务
POST /api/v1/installs
Request:
  templateId: string
  userId: string
  roleName: string
  config: object
Response:
  taskId: string
  status: string
  steps: array

# 获取安装任务
GET /api/v1/installs/{taskId}
Response:
  taskId: string
  templateId: string
  status: string
  progress: number
  steps: array
  dependencies: array

# 执行安装步骤
POST /api/v1/installs/{taskId}/steps/{stepId}
Request:
  data: object
Response:
  success: boolean
  nextStep: string

# 完成安装
POST /api/v1/installs/{taskId}/complete
Response:
  success: boolean
  sceneGroupId: string
  menus: array

# 回滚安装
POST /api/v1/installs/{taskId}/rollback
Response:
  success: boolean
  message: string
```

### 4.2 激活API

```yaml
# 创建激活流程
POST /api/v1/activations
Request:
  sceneGroupId: string
  userId: string
  roleName: string
Response:
  processId: string
  steps: array
  privateCapabilities: array

# 获取激活流程
GET /api/v1/activations/{processId}
Response:
  processId: string
  status: string
  currentStep: number
  steps: array

# 执行激活步骤
POST /api/v1/activations/{processId}/steps/{stepId}
Request:
  data: object
Response:
  success: boolean
  nextStep: string

# 启用私有能力
POST /api/v1/activations/{processId}/capabilities/{capId}/enable
Request:
  config: object
Response:
  success: boolean

# 完成激活
POST /api/v1/activations/{processId}/complete
Response:
  success: boolean
  menus: array
  message: string
```

### 4.3 LLM助手API

```yaml
# 发送消息
POST /api/v1/llm-assistant/chat
Request:
  sessionId: string
  message: string
  context: object
Response:
  response: string
  action: object
  suggestions: array

# 获取推荐
GET /api/v1/llm-assistant/recommend
Request:
  userId: string
  query: string
Response:
  recommendations: array

# 解析配置
POST /api/v1/llm-assistant/parse-config
Request:
  userInput: string
  templateId: string
Response:
  config: object
  confidence: number
```

---

## 五、数据库设计

### 5.1 安装任务表

```sql
CREATE TABLE install_task (
    task_id VARCHAR(64) PRIMARY KEY,
    template_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    role_name VARCHAR(32),
    status VARCHAR(32) NOT NULL,
    progress INT DEFAULT 0,
    config JSON,
    dependencies JSON,
    create_time BIGINT NOT NULL,
    update_time BIGINT NOT NULL,
    complete_time BIGINT,
    error_message TEXT
);
```

### 5.2 激活流程表

```sql
CREATE TABLE activation_process (
    process_id VARCHAR(64) PRIMARY KEY,
    task_id VARCHAR(64),
    scene_group_id VARCHAR(64) NOT NULL,
    template_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    role_name VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    current_step INT DEFAULT 0,
    total_steps INT DEFAULT 0,
    steps JSON,
    private_capabilities JSON,
    enabled_capabilities JSON,
    menu_registered BOOLEAN DEFAULT FALSE,
    notification_sent BOOLEAN DEFAULT FALSE,
    create_time BIGINT NOT NULL,
    update_time BIGINT NOT NULL,
    complete_time BIGINT
);
```

### 5.3 用户场景菜单表

```sql
CREATE TABLE user_scene_menu (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    scene_group_id VARCHAR(64) NOT NULL,
    scene_name VARCHAR(128) NOT NULL,
    role_name VARCHAR(32) NOT NULL,
    menus JSON NOT NULL,
    create_time BIGINT NOT NULL,
    update_time BIGINT NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_scene_group_id (scene_group_id)
);
```

---

## 六、实施计划

### 6.1 阶段一：核心服务（2周）

| 任务 | 工作量 | 优先级 |
|------|:------:|:------:|
| 实现InstallService | 3天 | 高 |
| 实现ActivationService扩展 | 2天 | 高 |
| 实现DependencyCheckService | 2天 | 高 |
| 实现MenuAutoRegisterService | 2天 | 高 |
| 实现RoleBasedActivationProcessor | 2天 | 高 |
| 单元测试 | 1天 | 高 |

### 6.2 阶段二：API和前端（2周）

| 任务 | 工作量 | 优先级 |
|------|:------:|:------:|
| 实现InstallAPI | 2天 | 高 |
| 实现ActivationAPI | 2天 | 高 |
| 实现LLM助手API | 2天 | 高 |
| 重构安装向导页面 | 3天 | 高 |
| 创建激活配置页面 | 2天 | 高 |
| 集成测试 | 1天 | 高 |

### 6.3 阶段三：LLM集成（1周）

| 任务 | 工作量 | 优先级 |
|------|:------:|:------:|
| 实现LlmInstallAssistantService | 2天 | 中 |
| 创建LLM对话组件 | 2天 | 中 |
| 集成测试 | 1天 | 中 |

---

**文档状态**: 技术设计  
**下一步**: 开始实施阶段一任务

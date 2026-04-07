# 第一册：流程定义规格

**文档版本**: v3.0  
**创建日期**: 2026-04-06  
**源码路径**: `E:\github\ooder-skills\skills\_drivers\bpm\bpm-web-sources\net\ooder\bpm\client\ProcessDef.java`

---

## 目录

1. [流程定义 (ProcessDef)](#1-流程定义-processdef)
2. [流程版本 (ProcessDefVersion)](#2-流程版本-processdefversion)
3. [流程表单 (ProcessDefForm)](#3-流程表单-processdefform)
4. [流程监听器 (Listener)](#4-流程监听器-listener)
5. [流程事件 (ProcessEventEnums)](#5-流程事件-processeventenums)
6. [流程扩展属性](#6-流程扩展属性)

---

## 1. 流程定义 (ProcessDef)

### 1.1 接口定义

**源码位置**: `net.ooder.bpm.client.ProcessDef`

```java
public interface ProcessDef extends java.io.Serializable {
    public String getProcessDefId();
    public String getName();
    public String getDescription();
    public String getClassification();
    public String getSystemCode();
    public ProcessDefAccess getAccessLevel();
    public ProcessDefVersion getProcessDefVersion(int version) throws BPMException;
    public List<String> getAllProcessDefVersionIds();
    public List<ProcessDefVersion> getAllProcessDefVersions() throws BPMException;
    public ProcessDefVersion getActiveProcessDefVersion() throws BPMException;
}
```

### 1.2 基础属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| processDefId | String | 流程UUID | 流程唯一标识，与版本无关 |
| name | String | 流程名称 | 流程定义的名称 |
| description | String | 流程描述 | 流程定义的描述 |
| classification | String | 流程分类 | 流程定义的分类 |
| systemCode | String | 所属应用系统 | 如：OA、CMS、SP等 |
| accessLevel | ProcessDefAccess | 流程访问级别 | 流程类型 |

### 1.3 流程访问级别 (ProcessDefAccess)

**源码位置**: `net.ooder.bpm.enums.process.ProcessDefAccess`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| Public | Public | 独立启动 | 可以独立启动 |
| Private | Private | 子流程 | 不可以独立启动，只能作为Subflow |
| Block | Block | 流程块 | 流程块定义 |

### 1.4 面板设计

#### 1.4.1 基本信息面板

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| 流程名称 | 文本 | 是 | 流程定义的名称 |
| 流程描述 | 多行文本 | 否 | 流程定义的描述 |
| 流程分类 | 下拉选择 | 是 | 流程定义的分类 |
| 所属应用系统 | 下拉选择 | 是 | 如：OA、CMS、SP等 |
| 流程访问级别 | 单选按钮 | 是 | 独立启动/子流程/流程块 |

---

## 2. 流程版本 (ProcessDefVersion)

### 2.1 接口定义

**源码位置**: `net.ooder.bpm.client.ProcessDefVersion`

```java
public interface ProcessDefVersion extends java.io.Serializable {
    public String getProcessDefId();
    public String getProcessDefVersionId();
    public int getVersion();
    public ProcessDefVersionStatus getPublicationStatus();
    public String getProcessDefName();
    public String getDescription();
    public String getClassification();
    public String getDefDescription();
    public String getSystemCode();
    public ProcessDefAccess getAccessLevel();
    public Date getActiveTime();
    public Date getFreezeTime();
    public String getCreatorId();
    public String getCreatorName();
    public Date getCreated();
    public String getModifierId();
    public String getModifierName();
    public Date getModifyTime();
    public int getLimit();
    public DurationUnit getDurationUnit();
    public List<ActivityDef> getAllActivityDefs();
    public List<RouteDef> getAllRouteDefs() throws BPMException;
    public Object getWorkflowAttribute(String name);
    public Object getAttribute(Attributetype attributetype, String name);
    public Object getRightAttribute(String name);
    public List getAllAttribute();
    public Object getAppAttribute(String name);
    public String getAttribute(String name);
    public List<Listener> getListeners();
    public ProcessDefForm getFormDef() throws BPMException;
    public List<String> getActivityDefIds();
    public List<String> getRouteDefIds();
}
```

### 2.2 基础属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| processDefId | String | 流程UUID | 流程唯一标识 |
| processDefVersionId | String | 版本UUID | 版本唯一标识 |
| version | int | 版本号 | 当前流程定义的版本号 |
| publicationStatus | ProcessDefVersionStatus | 版本状态 | 修订中/已发布/测试中 |
| processDefName | String | 流程定义名称 | 流程定义的名称 |
| description | String | 流程定义描述 | 流程定义的描述 |
| classification | String | 流程定义分类 | 流程定义的分类 |
| systemCode | String | 所属应用系统 | 应用系统的代码 |
| accessLevel | ProcessDefAccess | 流程访问级别 | 流程类型 |

### 2.3 时间属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| activeTime | Date | 激活时间 | 流程版本的激活时间 |
| freezeTime | Date | 冻结时间 | 流程版本的冻结时间 |
| created | Date | 创建时间 | 流程版本的创建时间 |
| modifyTime | Date | 修改时间 | 最后修改时间 |

### 2.4 创建/修改人属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| creatorId | String | 创建人ID | 流程版本创建人的ID |
| creatorName | String | 创建人姓名 | 流程版本创建人的姓名 |
| modifierId | String | 修改人ID | 最后修改人的ID |
| modifierName | String | 修改人姓名 | 最后修改人的姓名 |

### 2.5 时限属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| limit | int | 完成期限 | 流程版本的完成期限 |
| durationUnit | DurationUnit | 时间单位 | Y/M/D/H/m/s/W |

### 2.6 版本状态 (ProcessDefVersionStatus)

**源码位置**: `net.ooder.bpm.enums.process.ProcessDefVersionStatus`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| UNDER_REVISION | UNDER_REVISION | 冻结 | 修订中状态 |
| RELEASED | RELEASED | 激活 | 已发布状态 |
| UNDER_TEST | UNDER_TEST | 测试中 | 测试中状态 |

### 2.7 面板设计

#### 2.7.1 版本管理面板

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| 版本号 | 只读 | - | 当前流程定义的版本号 |
| 版本状态 | 下拉选择 | 是 | 冻结/激活/测试中 |
| 完成期限 | 数字 | 否 | 流程版本的完成期限 |
| 时间单位 | 下拉选择 | 否 | 年/月/日/时/分/秒/工作日 |

#### 2.7.2 创建信息面板

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| 创建人 | 只读 | - | 流程版本创建人 |
| 创建时间 | 只读 | - | 流程版本创建时间 |
| 修改人 | 只读 | - | 最后修改人 |
| 修改时间 | 只读 | - | 最后修改时间 |
| 激活时间 | 只读 | - | 流程版本激活时间 |
| 冻结时间 | 只读 | - | 流程版本冻结时间 |

---

## 3. 流程表单 (ProcessDefForm)

### 3.1 接口定义

**源码位置**: `net.ooder.bpm.client.ProcessDefForm`

```java
public interface ProcessDefForm extends java.io.Serializable {
    public MarkEnum getMark();
    public LockEnum getLock();
    public String getProcessDefVersionId();
    public CommonYesNoEnum getAutoSave();
    public CommonYesNoEnum getNoSqlType();
    public List<String> getTableNames();
    public List<String> getModuleNames();
}
```

### 3.2 表单属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| mark | MarkEnum | 表单标识类型 | 表单实例化方式 |
| lock | LockEnum | 锁定策略 | 表单锁定策略 |
| processDefVersionId | String | 流程版本ID | 所属流程版本UUID |
| autoSave | CommonYesNoEnum | 自动保存 | 是否自动保存 |
| noSqlType | CommonYesNoEnum | NoSQL类型 | 是否NoSQL类型 |
| tableNames | List\<String\> | 关联表名 | 关联的数据库表名 |
| moduleNames | List\<String\> | 模块名称 | 模块名称列表 |

### 3.3 表单标识类型 (MarkEnum)

**源码位置**: `net.ooder.bpm.enums.form.MarkEnum`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| ProcessInst | ProcessInst | 全流程唯一 | 整个流程实例只有一个表单实例 |
| ActivityInst | ActivityInst | 步骤唯一 | 每个活动实例一个表单实例 |
| Person | Person | 办理人唯一 | 每个办理人一个表单实例 |
| ActivityInstPerson | ActivityInstPerson | 全过程记录 | 每次办理都创建新实例 |

### 3.4 表单锁定策略 (LockEnum)

**源码位置**: `net.ooder.bpm.enums.form.LockEnum`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| Msg | Msg | 通知修改 | 冲突时通知用户 |
| Lock | Lock | 锁定数据 | 锁定数据防止冲突 |
| Person | Person | 人工合并 | 人工合并冲突 |
| Last | Last | 保留最后版本 | 最后提交覆盖 |
| NO | NO | 禁止覆盖 | 禁止覆盖 |

### 3.5 面板设计

#### 3.5.1 表单配置面板

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| 表单标识类型 | 下拉选择 | 是 | 全流程唯一/步骤唯一/办理人唯一/全过程记录 |
| 锁定策略 | 下拉选择 | 是 | 通知修改/锁定数据/人工合并/保留最后版本/禁止覆盖 |
| 自动保存 | 复选框 | 否 | 是否自动保存 |
| 关联表名 | 多选 | 否 | 关联的数据库表名 |
| 模块名称 | 多选 | 否 | 模块名称列表 |

---

## 4. 流程监听器 (Listener)

### 4.1 接口定义

**源码位置**: `net.ooder.bpm.client.Listener`

```java
public interface Listener extends JDSListener, java.io.Serializable {
    public String getListenerId();
    public String getListenerName();
    public ListenerEnums getListenerEvent();
    public String getRealizeClass();
    public EventEnums getExpressionEventType();
    public ListenerTypeEnums getExpressionListenerType();
    public String getExpressionStr();
}
```

### 4.2 监听器属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| listenerId | String | 监听器ID | 监听器唯一标识 |
| listenerName | String | 监听器名称 | 监听器名称 |
| listenerEvent | ListenerEnums | 监听器事件 | 监听器注册的事件类型 |
| realizeClass | String | 执行类 | 监听器实现类 |
| expressionEventType | EventEnums | 监听事件类型 | 表达式事件类型 |
| expressionListenerType | ListenerTypeEnums | 监听器类型 | 监听器类型 |
| expressionStr | String | 执行表达式 | 执行表达式 |

### 4.3 监听器类型 (ListenerEnums)

**源码位置**: `net.ooder.bpm.enums.event.ListenerEnums`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| PROCESS_LISTENER_EVENT | Process | Process | 流程监听器 |
| ACTIVITY_LISTENER_EVENT | Activity | Activity | 活动监听器 |
| RIGHT_LISTENER_EVENT | Right | Right | 权限监听器 |
| COMMAND_LISTENER_EVENT | Command | Command | 命令监听器 |
| EXPRESSIONLISENTERTYPE_EXPRESSION | Expression | Expression | 表达式监听器 |

### 4.4 面板设计

#### 4.4.1 监听器配置面板

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| 监听器名称 | 文本 | 是 | 监听器名称 |
| 监听器事件 | 下拉选择 | 是 | Process/Activity/Right/Command/Expression |
| 监听事件类型 | 下拉选择 | 是 | 具体事件类型 |
| 执行类 | 文本 | 是 | 监听器实现类全路径 |
| 执行表达式 | 多行文本 | 否 | 执行表达式 |

---

## 5. 流程事件 (ProcessEventEnums)

### 5.1 枚举定义

**源码位置**: `net.ooder.bpm.enums.event.ProcessEventEnums`

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| STARTING | processStarting | 开始启动 | 流程开始启动 |
| STARTED | processStarted | 启动完成 | 流程启动完成 |
| SAVING | processSaving | 开始保存 | 流程开始保存 |
| SAVED | processSaved | 保存完成 | 流程保存完成 |
| SUSPENDING | processSuspending | 正在被挂起 | 流程正在被挂起 |
| SUSPENDED | processSuspended | 已经被挂起 | 流程已经被挂起 |
| RESUMING | processResuming | 开始恢复 | 流程开始恢复 |
| RESUMED | processResumed | 恢复 | 流程恢复 |
| ABORTING | processAborting | 开始取消 | 流程开始取消 |
| ABORTED | processAborted | 取消 | 流程取消 |
| COMPLETING | processCompleting | 开始完成 | 流程开始完成 |
| COMPLETED | processCompleted | 已经完成 | 流程已经完成 |
| DELETING | processDeleting | 正在被删除 | 流程正在被删除 |
| DELETED | processDeleted | 已经被删除 | 流程已经被删除 |

### 5.2 事件流程图

```
┌─────────┐     ┌─────────┐     ┌─────────┐
│ STARTING│────▶│ STARTED │────▶│ SAVING  │
└─────────┘     └─────────┘     └─────────┘
                                     │
                                     ▼
┌─────────┐     ┌─────────┐     ┌─────────┐
│ SAVED   │────▶│COMPLETING│────▶│COMPLETED│
└─────────┘     └─────────┘     └─────────┘
                                     │
                    ┌────────────────┘
                    ▼
┌─────────┐     ┌─────────┐     ┌─────────┐
│DELETING │────▶│ DELETED │     │         │
└─────────┘     └─────────┘     └─────────┘

┌──────────┐    ┌──────────┐    ┌──────────┐
│SUSPENDING│───▶│SUSPENDED │───▶│ RESUMING │
└──────────┘    └──────────┘    └──────────┘
                                     │
                                     ▼
                               ┌──────────┐
                               │ RESUMED  │
                               └──────────┘

┌──────────┐    ┌──────────┐
│ ABORTING │───▶│ ABORTED  │
└──────────┘    └──────────┘
```

---

## 6. 流程扩展属性

### 6.1 扩展属性类型

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| workflowAttribute | Object | 工作流扩展属性 | 工作流相关扩展属性 |
| rightAttribute | Object | 权限扩展属性 | 权限相关扩展属性 |
| appAttribute | Object | 应用扩展属性 | 应用相关扩展属性 |
| attribute | String | 自定义扩展属性 | 自定义扩展属性 |

### 6.2 流程扩展属性枚举 (ProcessDefEnums)

**源码位置**: `net.ooder.bpm.enums.process.ProcessDefEnums`

| 枚举值 | 中文名 | 类型 |
|--------|--------|------|
| AccessLevel | 流程类型 | ProcessDefAccess |
| PublicationStatus | 版本状态 | ProcessDefVersionStatus |
| CommissionEnums | 流程权限 | CommissionEnums |

---

## 附录

### A. 面板清单

| 面板名称 | 所属对象 | 说明 |
|----------|----------|------|
| 基本信息面板 | ProcessDef | 流程基本信息配置 |
| 版本管理面板 | ProcessDefVersion | 版本状态和时限配置 |
| 创建信息面板 | ProcessDefVersion | 创建和修改信息 |
| 表单配置面板 | ProcessDefForm | 表单标识和锁定策略 |
| 监听器配置面板 | Listener | 监听器事件和执行类 |

### B. 枚举清单

| 枚举名称 | 中文名 | 枚举值数量 |
|----------|--------|------------|
| ProcessDefAccess | 流程访问级别 | 3 |
| ProcessDefVersionStatus | 版本状态 | 3 |
| MarkEnum | 表单标识类型 | 4 |
| LockEnum | 锁定策略 | 5 |
| ListenerEnums | 监听器类型 | 5 |
| ProcessEventEnums | 流程事件 | 14 |

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-spec\vol-01-process-def\README.md`

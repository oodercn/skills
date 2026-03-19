# 日志汇报场景用户故事

> **版本**: v2.3.5  
> **日期**: 2026-03-06  
> **场景类型**: ABS（自驱业务场景）

---

## 一、场景概述

### 1.1 场景定义

| 属性 | 值 |
|------|-----|
| 场景ID | scene-daily-report |
| 场景名称 | 日志汇报场景 |
| 场景分类 | ABS（自驱业务场景） |
| mainFirst | true |
| 业务语义评分 | 9分 |

### 1.2 场景能力

| 能力ID | 名称 | 类型 | 说明 |
|--------|------|------|------|
| report-remind | 日志提醒 | ATOMIC | 定时提醒员工提交日志 |
| report-submit | 日志提交 | ATOMIC | 员工提交工作日志 |
| report-aggregate | 日志汇总 | COMPOSITE | 汇总所有员工日志 |
| report-analyze | 日志分析 | ATOMIC | AI分析日志内容 |

### 1.3 协作能力

| 能力ID | 角色 | 说明 |
|--------|------|------|
| email-send | PROVIDER | 邮件发送通知 |
| llm-chat | PROVIDER | LLM智能分析 |

---

## 二、用户故事

### 故事1：管理员发现并安装场景

```
作为 系统管理员
我希望 从技能仓库发现并安装日志汇报场景
以便 为团队成员配置日志汇报能力
```

**验收标准**：
- [ ] 管理员可在"发现能力"页面搜索"日志汇报"
- [ ] 系统显示场景详情（能力列表、依赖项、驱动条件）
- [ ] 点击安装后自动解析依赖并安装
- [ ] 安装完成后显示在"已安装场景"列表

**API调用**：
```bash
# 发现场景
GET /api/v1/capabilities/discovery?method=GITEE&keyword=daily-report

# 查看详情
GET /api/v1/capabilities/scene-daily-report

# 安装场景
POST /api/v1/installs
{
  "capabilityId": "scene-daily-report",
  "config": {
    "remindTime": "17:00",
    "aggregateTime": "20:00"
  }
}
```

---

### 故事2：管理员配置场景分发

```
作为 系统管理员
我希望 配置场景的参与者和驱动条件
以便 场景能自动分发给相关人员
```

**验收标准**：
- [ ] 可选择主导者（Leader）
- [ ] 可选择协作者（员工）
- [ ] 可配置提醒时间
- [ ] 可配置汇总时间
- [ ] 可选择通知渠道（邮件/IM）

**配置界面**：
```
┌─────────────────────────────────────────┐
│ 场景配置                                 │
├─────────────────────────────────────────┤
│ 主导者: [张经理 ▼]                       │
│ 协作者: [李明, 王芳, 赵强]               │
│                                         │
│ 驱动条件:                                │
│   提醒时间: [17:00]                      │
│   汇总时间: [20:00]                      │
│                                         │
│ 通知渠道:                                │
│   [✓] 邮件通知                          │
│   [✓] 系统消息                          │
│   [ ] 钉钉/飞书                         │
│                                         │
│ [保存配置] [推送给参与者]                │
└─────────────────────────────────────────┘
```

---

### 故事3：主导者激活场景

```
作为 场景主导者
我希望 激活场景并确认参与者
以便 场景开始运行
```

**验收标准**：
- [ ] 收到"待激活"通知
- [ ] 可查看场景详情和配置
- [ ] 可确认/调整参与者
- [ ] 激活后协作者可见场景
- [ ] 获取场景KEY用于API调用

**API调用**：
```bash
# 获取待激活场景
GET /api/v1/activations?status=PENDING

# 激活场景
POST /api/v1/activations/{id}/activate
{
  "confirmedParticipants": ["user1", "user2", "user3"],
  "config": {
    "remindTime": "17:00"
  }
}
```

---

### 故事4：员工接收提醒并提交日志

```
作为 员工
我希望 在指定时间收到日志提交提醒
以便 及时提交工作日志
```

**验收标准**：
- [ ] 17:00收到提醒通知
- [ ] 通知包含提交入口链接
- [ ] 点击链接进入日志提交页面
- [ ] 可输入日志内容
- [ ] 提交后显示成功状态

**用户流程**：
```
17:00 ──► 收到提醒通知
         │
         ▼
      点击提交链接
         │
         ▼
      ┌─────────────────┐
      │ 日志提交         │
      ├─────────────────┤
      │ 今日工作内容:    │
      │ [文本框]         │
      │                 │
      │ 明日计划:        │
      │ [文本框]         │
      │                 │
      │ [提交日志]       │
      └─────────────────┘
         │
         ▼
      提交成功 ✓
```

**API调用**：
```bash
# 提交日志
POST /api/v1/capabilities/report-submit/invoke
{
  "sceneId": "scene-daily-report",
  "userId": "user1",
  "content": {
    "todayWork": "完成用户模块开发",
    "tomorrowPlan": "进行测试和修复"
  }
}
```

---

### 故事5：系统自动汇总日志

```
作为 系统
我希望 在指定时间自动汇总所有日志
以便 生成完整的日志报告
```

**验收标准**：
- [ ] 20:00自动触发汇总
- [ ] 汇总所有已提交日志
- [ ] 记录未提交人员
- [ ] 生成汇总报告

**自驱配置**：
```yaml
selfDrive:
  scheduleRules:
    - trigger: "0 20 * * 1-5"  # 工作日20:00
      action: aggregate-flow
      
  capabilityChains:
    aggregate-flow:
      - capability: report-aggregate
        input:
          sceneId: "${scene.id}"
          includeUnsubmitted: true
```

---

### 故事6：AI分析日志内容

```
作为 主导者
我希望 系统自动分析日志内容
以便 了解团队工作状态和问题
```

**验收标准**：
- [ ] 汇总后自动触发分析
- [ ] AI生成工作摘要
- [ ] 识别关键问题和风险
- [ ] 生成统计图表

**分析报告示例**：
```
┌─────────────────────────────────────────┐
│ 日志分析报告 - 2026-03-06               │
├─────────────────────────────────────────┤
│ 📊 提交统计                              │
│   总人数: 10人                           │
│   已提交: 8人 (80%)                      │
│   未提交: 2人                            │
│                                         │
│ 📝 工作摘要                              │
│   主要工作:                              │
│   - 用户模块开发 (3人)                   │
│   - API接口优化 (2人)                    │
│   - 测试用例编写 (2人)                   │
│   - 文档更新 (1人)                       │
│                                         │
│ ⚠️ 风险提示                              │
│   - 2人未提交日志                        │
│   - API接口进度延迟                      │
│                                         │
│ 💡 建议                                  │
│   - 提醒未提交人员                       │
│   - 关注API接口进度                      │
└─────────────────────────────────────────┘
```

---

## 三、场景生命周期

### 3.1 状态流转

```
DRAFT ──► PENDING ──► ACTIVE ──► SUSPENDED ──► COMPLETED
           │            │            │
           │            │            └── 可恢复
           │            └── 可暂停
           └── 等待激活
```

### 3.2 生命周期事件

| 状态 | 事件 | 触发动作 |
|------|------|---------|
| DRAFT | 创建完成 | 等待配置 |
| PENDING | 配置完成 | 推送给主导者 |
| ACTIVE | 激活成功 | 启动定时任务 |
| ACTIVE | 17:00 | 发送提醒 |
| ACTIVE | 20:00 | 汇总日志 |
| ACTIVE | 20:30 | AI分析 |
| SUSPENDED | 暂停 | 停止定时任务 |
| COMPLETED | 完成 | 归档数据 |

---

## 四、技术实现

### 4.1 场景技能定义

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-daily-report
  name: 日志汇报场景技能
  version: 2.3.0
  type: scene-skill
  category: abs

spec:
  type: scene-skill
  
  classification:
    category: abs
    categoryName: 自驱业务场景
    mainFirst: true
    businessSemanticsScore: 9
    
  dependencies:
    - id: skill-email
      version: ">=1.0.0"
      required: true
      capabilities: [email-send]
      
    - id: skill-llm-chat
      version: ">=2.0.0"
      required: false
      capabilities: [llm-chat]
      
  sceneCapabilities:
    - id: scene-daily-report
      name: 日志汇报场景能力
      type: SCENE
      mainFirst: true
      
      mainFirstConfig:
        selfCheck:
          - checkCapabilities: [report-remind, report-submit, report-aggregate]
          - checkDriverCapabilities: [scheduler]
          
        selfStart:
          - initDriverCapabilities: [scheduler]
          - initCapabilities: [report-remind, report-submit, report-aggregate]
          
        selfDrive:
          scheduleRules:
            - trigger: "0 17 * * 1-5"
              action: remind-flow
            - trigger: "0 20 * * 1-5"
              action: aggregate-flow
              
          capabilityChains:
            remind-flow:
              - capability: report-remind
                input:
                  targetUsers: "${role.employee}"
                  
            aggregate-flow:
              - capability: report-aggregate
                input:
                  sceneId: "${scene.id}"
              - capability: report-analyze
                input:
                  aggregateResult: "${previous}"
                  
      capabilities:
        - report-remind
        - report-submit
        - report-aggregate
        - report-analyze
        
      collaborativeCapabilities:
        - capabilityId: email-send
          role: PROVIDER
          interface: notification-service
          autoStart: true
          
  capabilities:
    - id: report-remind
      name: 日志提醒
      type: ATOMIC
      category: communication
      
    - id: report-submit
      name: 日志提交
      type: ATOMIC
      category: service
      
    - id: report-aggregate
      name: 日志汇总
      type: COMPOSITE
      category: service
      
    - id: report-analyze
      name: 日志分析
      type: ATOMIC
      category: ai
```

### 4.2 API端点

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/capabilities/scene-daily-report | GET | 获取场景详情 |
| /api/v1/capabilities/report-remind/invoke | POST | 发送提醒 |
| /api/v1/capabilities/report-submit/invoke | POST | 提交日志 |
| /api/v1/capabilities/report-aggregate/invoke | POST | 汇总日志 |
| /api/v1/capabilities/report-analyze/invoke | POST | 分析日志 |

---

## 五、验收清单

### 5.1 功能验收

- [ ] 管理员可发现并安装场景
- [ ] 管理员可配置参与者和驱动条件
- [ ] 主导者可激活场景
- [ ] 员工可收到提醒通知
- [ ] 员工可提交日志
- [ ] 系统可自动汇总日志
- [ ] AI可分析日志内容

### 5.2 非功能验收

- [ ] 提醒通知准时发送（误差<1分钟）
- [ ] 日志提交响应时间<2秒
- [ ] 汇总报告生成时间<30秒
- [ ] AI分析时间<60秒

### 5.3 异常场景

- [ ] 未提交人员自动提醒
- [ ] 网络异常重试机制
- [ ] LLM服务不可用降级处理

---

**文档编写**: Skills Team  
**最后更新**: 2026-03-06

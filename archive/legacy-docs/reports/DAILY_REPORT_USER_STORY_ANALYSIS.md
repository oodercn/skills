# 日志汇报完整用户故事 - 实现条件分析

## 一、已具备的条件（真实API支持）

### 1. 后端API（已实现）

| 模块 | API | 方法 | 状态 | 说明 |
|------|-----|------|------|------|
| 场景管理 | /api/v1/scenes | GET | ✅ | 场景列表 |
| 场景管理 | /api/v1/scenes | POST | ✅ | 创建场景 |
| 场景管理 | /api/v1/scenes/{id} | GET | ✅ | 场景详情 |
| 场景组管理 | /api/v1/scene-groups | GET | ✅ | 场景组列表 |
| 场景组管理 | /api/v1/scene-groups | POST | ✅ | 创建场景组 |
| 场景组管理 | /api/v1/scene-groups/{id} | GET | ✅ | 场景组详情 |
| 模板管理 | /api/v1/scene-templates | GET | ✅ | 模板列表 |
| 模板管理 | /api/v1/scene-templates | POST | ✅ | 创建模板 |
| 能力管理 | /api/v1/capabilities | GET | ✅ | 能力列表 |
| 能力管理 | /api/v1/capabilities | POST | ✅ | 注册能力 |
| 能力管理 | /api/v1/capabilities/types | GET | ✅ | 能力类型 |
| 能力发现 | /api/v1/capabilities/discovery | GET | ✅ | 发现能力 |
| 能力发现 | /api/v1/capabilities/discovery/invoke | POST | ✅ | 调用能力 |
| 能力绑定 | /api/v1/capabilities/bindings | GET | ✅ | 绑定列表 |
| 能力绑定 | /api/v1/capabilities/bindings | POST | ✅ | 创建绑定 |
| LLM | /api/llm/providers | GET | ✅ | 提供者列表 |
| LLM | /api/llm/chat | POST | ✅ | 对话 |

### 2. 已有的Skill实现

#### DailyReportSkill（日志汇报技能）
```
/api/skills/daily-report/capabilities  - 能力列表
/api/skills/daily-report/remind        - 发送提醒
/api/skills/daily-report/submit        - 提交日志
/api/skills/daily-report/aggregate     - 汇总日志
/api/skills/daily-report/analyze       - 分析日志
```

**提供的能力**:
- report-remind（日志提醒）
- report-submit（日志提交）
- report-aggregate（日志汇总）
- report-analyze（日志分析）

### 3. 已有的LLM Provider

| Provider | 状态 | 模型数量 |
|----------|------|----------|
| mock | ✅ | 1 |
| openai | ✅ | 6 |
| qianwen | ✅ | 15+ |
| ollama | ✅ | 10+ |
| deepseek | ✅ | 3 |
| volcengine | ✅ | 10+ |

---

## 二、需要Mock/实现的部分

### 1. GitHub/Gitee发现功能

**当前状态**: 前端有配置UI，后端只返回模拟数据

**需要实现**:
- [ ] GitHub API集成（搜索skill仓库）
- [ ] Gitee API集成
- [ ] skill.yaml解析
- [ ] 下载和安装流程

**建议方案**: 
- 使用GitHub REST API搜索`topic:ooder-skill`
- 解析仓库中的skill.yaml获取能力定义

### 2. 能力安装流程

**当前状态**: 前端有安装进度UI，后端无真实安装逻辑

**需要实现**:
- [ ] 下载skill包
- [ ] 验证完整性
- [ ] 解析依赖
- [ ] 注册能力到CapabilityRegistry
- [ ] 更新已安装列表

### 3. 场景激活和数据流转

**当前状态**: 场景组可以创建，但激活后无数据流转

**需要实现**:
- [ ] 场景激活时调用能力链
- [ ] 能力间的数据传递
- [ ] 状态回调通知

---

## 三、日志汇报完整用户故事

### 用户故事流程

```
1. 用户打开"发现能力"页面
   └── 选择"Gitee仓库"发现途径
   └── 输入仓库地址: https://gitee.com/ooderCN/skill-daily-report
   └── 点击"开始扫描"
   └── 雷达显示扫描进度
   └── 发现"日志汇报技能"包含4个能力

2. 用户选择安装"日志汇报技能"
   └── 显示安装进度条
   └── 步骤: 下载→验证→解析依赖→安装→注册
   └── 安装完成后显示"已安装"

3. 用户打开"我的能力"页面
   └── 查看4个已安装能力
   └── 点击"日志提醒"查看详情
   └── 配置提醒时间等参数

4. 用户打开"能力绑定"页面
   └── 选择"日志汇报组"场景组
   └── 绑定4个能力到场景组
   └── 设置调用优先级

5. 用户打开"场景组详情"页面
   └── 激活场景组
   └── 场景开始运行
   └── 能力按顺序执行:
       ├── 09:00 日志提醒 → 发送提醒给所有成员
       ├── 18:00 日志提交 → 员工提交日志
       ├── 20:00 日志汇总 → 汇总所有日志
       └── 20:30 日志分析 → AI分析并生成报告

6. 用户查看"能力统计"页面
   └── 查看调用次数统计
   └── 查看成功率
   └── 查看调用日志
```

---

## 四、实现优先级

### 第一阶段：本地闭环（当前可完成）

| 功能 | 状态 | 说明 |
|------|------|------|
| 本地能力发现 | ✅ 已实现 | LOCAL_FS方式 |
| 能力注册 | ✅ 已实现 | POST /api/v1/capabilities |
| 能力绑定 | ✅ 已实现 | POST /api/v1/capabilities/bindings |
| 场景组创建 | ✅ 已实现 | POST /api/v1/scene-groups |
| 能力调用 | ✅ 已实现 | POST /api/v1/capabilities/discovery/invoke |
| DailyReport Skill | ✅ 已实现 | /api/skills/daily-report/* |

### 第二阶段：远程发现（需要实现）

| 功能 | 状态 | 说明 |
|------|------|------|
| GitHub发现 | ⚠️ 需实现 | 调用GitHub API |
| Gitee发现 | ⚠️ 需实现 | 调用Gitee API |
| skill.yaml解析 | ⚠️ 需实现 | 解析能力定义 |
| 安装流程 | ⚠️ 需实现 | 下载、注册 |

### 第三阶段：数据流转（需要实现）

| 功能 | 状态 | 说明 |
|------|------|------|
| 场景激活 | ⚠️ 需完善 | 触发能力链 |
| 能力链调用 | ⚠️ 需实现 | 顺序调用绑定能力 |
| 数据传递 | ⚠️ 需实现 | 能力间参数传递 |
| 状态回调 | ⚠️ 需实现 | 执行结果通知 |

---

## 五、当前可演示的闭环

基于已具备的条件，当前可以演示以下完整闭环：

### 演示步骤

1. **访问发现页面** → 选择"本地文件系统" → 扫描发现能力
2. **访问我的能力** → 查看已安装能力 → 配置参数
3. **访问能力绑定** → 选择场景组 → 绑定能力
4. **访问场景组详情** → 激活场景组 → 手动调用能力
5. **访问能力统计** → 查看调用日志

### API调用示例

```bash
# 1. 发现能力
curl http://localhost:8084/api/v1/capabilities/discovery?method=LOCAL_FS

# 2. 查看已安装能力
curl http://localhost:8084/api/v1/capabilities

# 3. 创建场景组
curl -X POST http://localhost:8084/api/v1/scene-groups \
  -H "Content-Type: application/json" \
  -d '{"name":"日志汇报组","templateId":"tpl-daily-report"}'

# 4. 绑定能力
curl -X POST http://localhost:8084/api/v1/capabilities/bindings \
  -H "Content-Type: application/json" \
  -d '{"sceneGroupId":"sg-xxx","capabilityId":"report-remind"}'

# 5. 调用能力
curl -X POST http://localhost:8084/api/v1/capabilities/discovery/invoke \
  -H "Content-Type: application/json" \
  -d '{"capabilityId":"report-remind","params":{"sceneGroupId":"sg-xxx"}}'

# 6. 查看DailyReport Skill能力
curl http://localhost:8084/api/skills/daily-report/capabilities

# 7. 发送提醒
curl -X POST http://localhost:8084/api/skills/daily-report/remind \
  -H "Content-Type: application/json" \
  -d '{"sceneGroupId":"sg-xxx","targetUsers":["user1","user2"]}'

# 8. 提交日志
curl -X POST http://localhost:8084/api/skills/daily-report/submit \
  -H "Content-Type: application/json" \
  -d '{"sceneGroupId":"sg-xxx","userId":"user1","content":"今日完成开发任务"}'
```

---

## 六、下一步行动

### 立即可做（无需额外开发）

1. 完善前端页面与现有API的对接
2. 使用DailyReport Skill演示完整流程
3. 补充测试数据

### 需要开发

1. **GitHub/Gitee发现Controller** - 调用Git API搜索skill仓库
2. **Skill安装服务** - 下载、解析、注册skill
3. **场景执行引擎** - 激活场景时调用能力链

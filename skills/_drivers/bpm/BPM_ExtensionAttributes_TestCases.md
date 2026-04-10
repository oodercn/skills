# BPM流程引擎扩展属性测试用例

## 版本信息
- **版本**: v1.0
- **日期**: 2026-04-09
- **状态**: 草案

---

## 1. 测试概述

### 1.1 测试目标
验证所有扩展属性在JSON转换、存储和读取过程中的完整性和准确性。

### 1.2 测试范围
- 流程级属性（BPD标准属性、开始/结束节点坐标、监听器、权限组）
- 活动级属性（视觉位置、WORKFLOW/RIGHT/FORM/SERVICE属性组、块活动特殊属性）
- 路由级属性

### 1.3 测试环境
- **前端**: JS版设计器
- **后端**: BPM Server (bpmserver)
- **客户端**: WorkflowClient

---

## 2. 流程级属性测试用例

### 2.1 BPD标准属性测试

#### TC-BPD-001: 流程基本信息保存测试

**测试目的**: 验证流程基本信息能正确保存和读取

**前置条件**:
- 流程设计器已加载
- 数据库连接正常

**测试步骤**:
1. 创建新流程，填写以下信息：
   - processDefId: "proc_test_001"
   - name: "测试流程"
   - description: "测试流程描述"
   - classification: "测试分类"
   - systemCode: "TEST"
   - accessLevel: "PUBLIC"
2. 保存流程
3. 重新加载流程
4. 验证所有字段值

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| processDefId | "proc_test_001" | | |
| name | "测试流程" | | |
| description | "测试流程描述" | | |
| classification | "测试分类" | | |
| systemCode | "TEST" | | |
| accessLevel | "PUBLIC" | | |

**优先级**: P0

---

#### TC-BPD-002: 时间属性保存测试

**测试目的**: 验证时间属性能正确保存和读取

**测试步骤**:
1. 创建流程并设置：
   - activeTime: "2026-04-09T10:00:00Z"
   - freezeTime: "2026-12-31T23:59:59Z"
2. 保存流程
3. 重新加载流程
4. 验证时间字段

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| activeTime | "2026-04-09T10:00:00Z" | | |
| freezeTime | "2026-12-31T23:59:59Z" | | |

**优先级**: P1

---

#### TC-BPD-003: 人员属性保存测试

**测试目的**: 验证人员属性能正确保存和读取

**测试步骤**:
1. 创建流程并设置：
   - creatorName: "张三"
   - modifierId: "user_001"
   - modifierName: "李四"
2. 保存流程
3. 重新加载流程
4. 验证人员字段

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| creatorName | "张三" | | |
| modifierId | "user_001" | | |
| modifierName | "李四" | | |

**优先级**: P1

---

### 2.2 开始/结束节点坐标测试（关键）

#### TC-COORD-001: 开始节点坐标保存测试

**测试目的**: 验证开始节点坐标能正确保存和读取

**测试步骤**:
1. 创建流程并设置开始节点：
   ```json
   {
     "startNode": {
       "participantId": "Participant_1",
       "firstActivityId": "act_001",
       "x": 100,
       "y": 200,
       "routing": "NO_ROUTING"
     }
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证XPDL中ExtendedAttribute(Name="StartOfWorkflow")的值
5. 验证JSON中startNode对象

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| participantId | "Participant_1" | | |
| firstActivityId | "act_001" | | |
| x | 100 | | |
| y | 200 | | |
| routing | "NO_ROUTING" | | |
| XPDL格式 | "Participant_1;act_001;100;200;NO_ROUTING" | | |

**优先级**: P0

---

#### TC-COORD-002: 结束节点坐标保存测试

**测试目的**: 验证结束节点坐标能正确保存和读取

**测试步骤**:
1. 创建流程并设置结束节点：
   ```json
   {
     "endNodes": [
       {
         "participantId": "Participant_1",
         "lastActivityId": "act_005",
         "x": 900,
         "y": 200,
         "routing": "NO_ROUTING"
       }
     ]
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证XPDL中ExtendedAttribute(Name="EndOfWorkflow")的值
5. 验证JSON中endNodes数组

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| participantId | "Participant_1" | | |
| lastActivityId | "act_005" | | |
| x | 900 | | |
| y | 200 | | |
| routing | "NO_ROUTING" | | |
| XPDL格式 | "Participant_1;act_005;900;200;NO_ROUTING" | | |

**优先级**: P0

---

#### TC-COORD-003: 多个结束节点坐标测试

**测试目的**: 验证多个结束节点坐标能正确保存和读取

**测试步骤**:
1. 创建流程并设置多个结束节点：
   ```json
   {
     "endNodes": [
       {
         "participantId": "Participant_1",
         "lastActivityId": "act_005",
         "x": 900,
         "y": 100,
         "routing": "NO_ROUTING"
       },
       {
         "participantId": "Participant_1",
         "lastActivityId": "act_006",
         "x": 900,
         "y": 300,
         "routing": "NO_ROUTING"
       }
     ]
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证所有结束节点

**预期结果**:
- 两个结束节点都正确保存
- XPDL中EndOfWorkflow包含两个值（用分隔符分隔）

**优先级**: P0

---

#### TC-COORD-004: 坐标边界值测试

**测试目的**: 验证坐标边界值能正确处理

**测试步骤**:
1. 测试坐标值：
   - x: 0, y: 0
   - x: -100, y: -200
   - x: 999999, y: 999999
2. 保存并重新加载
3. 验证坐标值

**预期结果**:
- 所有边界值正确保存和读取

**优先级**: P1

---

### 2.3 监听器测试（关键）

#### TC-LISTENER-001: 监听器保存测试

**测试目的**: 验证监听器能正确保存和读取

**测试步骤**:
1. 创建流程并添加监听器：
   ```json
   {
     "listeners": [
       {
         "id": "listener_001",
         "name": "流程启动监听",
         "event": "PROCESS_START",
         "realizeClass": "com.example.ProcessStartListener"
       }
     ]
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证XPDL中Listeners XML结构
5. 验证JSON中listeners数组

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| id | "listener_001" | | |
| name | "流程启动监听" | | |
| event | "PROCESS_START" | | |
| realizeClass | "com.example.ProcessStartListener" | | |

**优先级**: P0

---

#### TC-LISTENER-002: 多个监听器测试

**测试目的**: 验证多个监听器能正确保存和读取

**测试步骤**:
1. 创建流程并添加多个监听器：
   ```json
   {
     "listeners": [
       {
         "id": "listener_001",
         "name": "流程启动监听",
         "event": "PROCESS_START",
         "realizeClass": "com.example.ProcessStartListener"
       },
       {
         "id": "listener_002",
         "name": "流程结束监听",
         "event": "PROCESS_END",
         "realizeClass": "com.example.ProcessEndListener"
       },
       {
         "id": "listener_003",
         "name": "任务分配监听",
         "event": "ASSIGNMENT",
         "realizeClass": "com.example.AssignmentListener"
       }
     ]
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证所有监听器

**预期结果**:
- 所有监听器正确保存和读取
- XPDL中itjds:Listeners包含多个itjds:Listener元素

**优先级**: P0

---

#### TC-LISTENER-003: 所有监听事件类型测试

**测试目的**: 验证所有监听事件类型能正确处理

**测试步骤**:
1. 测试所有监听事件：
   - PROCESS_START
   - PROCESS_END
   - ACTIVITY_START
   - ACTIVITY_END
   - ROUTE_TAKE
   - ASSIGNMENT
2. 为每种事件创建监听器
3. 保存并重新加载
4. 验证事件类型

**预期结果**:
- 所有事件类型正确保存

**优先级**: P1

---

### 2.4 权限组测试（关键）

#### TC-RIGHTGROUP-001: 权限组保存测试

**测试目的**: 验证权限组能正确保存和读取

**测试步骤**:
1. 创建流程并添加权限组：
   ```json
   {
     "rightGroups": [
       {
         "id": "rg_001",
         "name": "审批组",
         "code": "APPROVAL",
         "order": 1,
         "defaultGroup": true
       }
     ]
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证XPDL中RightGroups XML结构
5. 验证JSON中rightGroups数组

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| id | "rg_001" | | |
| name | "审批组" | | |
| code | "APPROVAL" | | |
| order | 1 | | |
| defaultGroup | true | | |

**优先级**: P0

---

#### TC-RIGHTGROUP-002: 多个权限组测试

**测试目的**: 验证多个权限组能正确保存和读取

**测试步骤**:
1. 创建流程并添加多个权限组：
   ```json
   {
     "rightGroups": [
       {
         "id": "rg_001",
         "name": "审批组",
         "code": "APPROVAL",
         "order": 1,
         "defaultGroup": true
       },
       {
         "id": "rg_002",
         "name": "会签组",
         "code": "COUNTERSIGN",
         "order": 2,
         "defaultGroup": false
       },
       {
         "id": "rg_003",
         "name": "知会组",
         "code": "NOTIFY",
         "order": 3,
         "defaultGroup": false
       }
     ]
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证所有权限组

**预期结果**:
- 所有权限组正确保存和读取
- 顺序保持一致

**优先级**: P0

---

## 3. 活动级属性测试用例

### 3.1 视觉位置属性测试（关键）

#### TC-POSITION-001: 活动坐标保存测试

**测试目的**: 验证活动坐标能正确保存和读取

**测试步骤**:
1. 创建活动并设置坐标：
   ```json
   {
     "position": "NORMAL",
     "positionCoord": {
       "x": 300,
       "y": 400
     },
     "participantId": "Participant_1"
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证XPDL中XOffset、YOffset、ParticipantID
5. 验证JSON中positionCoord和participantId

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| position | "NORMAL" | | |
| positionCoord.x | 300 | | |
| positionCoord.y | 400 | | |
| participantId | "Participant_1" | | |
| XPDL XOffset | 300 | | |
| XPDL YOffset | 400 | | |
| XPDL ParticipantID | "Participant_1" | | |

**优先级**: P0

---

#### TC-POSITION-002: 开始/结束活动位置测试

**测试目的**: 验证开始/结束活动位置类型能正确处理

**测试步骤**:
1. 创建开始活动：
   ```json
   {
     "position": "START",
     "positionCoord": {"x": 100, "y": 200}
   }
   ```
2. 创建结束活动：
   ```json
   {
     "position": "END",
     "positionCoord": {"x": 900, "y": 200}
   }
   ```
3. 保存流程
4. 重新加载流程
5. 验证位置类型

**预期结果**:
| 活动 | position | XPDL Position |
|------|----------|---------------|
| 开始 | "START" | "POSITION_START" |
| 结束 | "END" | "POSITION_END" |

**优先级**: P0

---

### 3.2 WORKFLOW属性组测试

#### TC-WORKFLOW-001: 时限属性测试

**测试目的**: 验证时限属性能正确保存和读取

**测试步骤**:
1. 创建活动并设置时限：
   ```json
   {
     "limitTime": 48,
     "alertTime": 24,
     "durationUnit": "H",
     "deadLineOperation": "NOTIFY"
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证时限字段

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| limitTime | 48 | | |
| alertTime | 24 | | |
| durationUnit | "H" | | |
| deadLineOperation | "NOTIFY" | | |

**优先级**: P1

---

#### TC-WORKFLOW-002: 时间单位枚举测试

**测试目的**: 验证所有时间单位枚举能正确处理

**测试步骤**:
1. 测试所有时间单位：
   - Y (年)
   - M (月)
   - D (日)
   - H (小时)
   - m (分钟)
   - s (秒)
   - W (周)
2. 为每种单位创建活动
3. 保存并重新加载
4. 验证单位值

**预期结果**:
- 所有时间单位正确保存

**优先级**: P2

---

#### TC-WORKFLOW-003: 路由控制属性测试

**测试目的**: 验证路由控制属性能正确保存和读取

**测试步骤**:
1. 创建活动并设置路由控制：
   ```json
   {
     "canRouteBack": "YES",
     "routeBackMethod": "PREV",
     "canSpecialSend": "YES",
     "specialScope": "ALL",
     "join": "XOR",
     "split": "XOR"
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证路由控制字段

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| canRouteBack | "YES" | | |
| routeBackMethod | "PREV" | | |
| canSpecialSend | "YES" | | |
| specialScope | "ALL" | | |
| join | "XOR" | | |
| split | "XOR" | | |

**优先级**: P1

---

#### TC-WORKFLOW-004: 汇聚分支类型枚举测试

**测试目的**: 验证所有汇聚分支类型枚举能正确处理

**测试步骤**:
1. 测试所有类型：
   - AND
   - XOR
   - OR
2. 为每种类型创建活动
3. 保存并重新加载
4. 验证类型值

**预期结果**:
- 所有类型正确保存

**优先级**: P2

---

### 3.3 RIGHT属性组测试

#### TC-RIGHT-001: 权限基本属性测试

**测试目的**: 验证权限基本属性能正确保存和读取

**测试步骤**:
1. 创建活动并设置权限：
   ```json
   {
     "RIGHT": {
       "performType": "SINGLE",
       "performSequence": "FIRST",
       "canInsteadSign": "YES",
       "canTakeBack": "YES",
       "canReSend": "NO",
       "performerSelectedId": "dept_manager",
       "readerSelectedId": "hr_group"
     }
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证权限字段

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| performType | "SINGLE" | | |
| performSequence | "FIRST" | | |
| canInsteadSign | "YES" | | |
| canTakeBack | "YES" | | |
| canReSend | "NO" | | |
| performerSelectedId | "dept_manager" | | |
| readerSelectedId | "hr_group" | | |

**优先级**: P0

---

#### TC-RIGHT-002: 执行类型枚举测试

**测试目的**: 验证所有执行类型枚举能正确处理

**测试步骤**:
1. 测试所有执行类型：
   - SINGLE (单人)
   - MULTIPLE (多人)
   - JOINTSIGN (会签)
2. 为每种类型创建活动
3. 保存并重新加载
4. 验证类型值

**预期结果**:
- 所有类型正确保存

**优先级**: P1

---

#### TC-RIGHT-003: 移动目标属性测试

**测试目的**: 验证移动目标属性能正确保存和读取

**测试步骤**:
1. 创建活动并设置移动目标：
   ```json
   {
     "RIGHT": {
       "movePerformerTo": "rg_002",
       "moveSponsorTo": "rg_003",
       "moveReaderTo": "rg_004"
     }
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证移动目标字段

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| movePerformerTo | "rg_002" | | |
| moveSponsorTo | "rg_003" | | |
| moveReaderTo | "rg_004" | | |

**优先级**: P1

---

#### TC-RIGHT-004: 代理人属性测试

**测试目的**: 验证代理人属性能正确保存和读取

**测试步骤**:
1. 创建活动并设置代理人：
   ```json
   {
     "RIGHT": {
       "surrogateId": "user_999",
       "surrogateName": "王五"
     }
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证代理人字段

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| surrogateId | "user_999" | | |
| surrogateName | "王五" | | |

**优先级**: P1

---

### 3.4 FORM属性组测试

#### TC-FORM-001: 表单属性测试

**测试目的**: 验证表单属性能正确保存和读取

**测试步骤**:
1. 创建活动并设置表单：
   ```json
   {
     "FORM": {
       "formId": "form_leave",
       "formName": "请假单",
       "formType": "CUSTOM",
       "formUrl": "/forms/leave.html"
     }
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证表单字段

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| formId | "form_leave" | | |
| formName | "请假单" | | |
| formType | "CUSTOM" | | |
| formUrl | "/forms/leave.html" | | |

**优先级**: P1

---

### 3.5 SERVICE属性组测试

#### TC-SERVICE-001: 服务属性测试

**测试目的**: 验证服务属性能正确保存和读取

**测试步骤**:
1. 创建活动并设置服务：
   ```json
   {
     "SERVICE": {
       "httpMethod": "POST",
       "httpUrl": "/api/approve",
       "httpRequestType": "JSON",
       "httpResponseType": "JSON",
       "httpServiceParams": "{\"key\":\"value\"}",
       "serviceSelectedId": "svc_001"
     }
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证服务字段

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| httpMethod | "POST" | | |
| httpUrl | "/api/approve" | | |
| httpRequestType | "JSON" | | |
| httpResponseType | "JSON" | | |
| httpServiceParams | "{\"key\":\"value\"}" | | |
| serviceSelectedId | "svc_001" | | |

**优先级**: P1

---

#### TC-SERVICE-002: HTTP方法枚举测试

**测试目的**: 验证所有HTTP方法枚举能正确处理

**测试步骤**:
1. 测试所有HTTP方法：
   - GET
   - POST
   - PUT
   - DELETE
2. 为每种方法创建活动
3. 保存并重新加载
4. 验证方法值

**预期结果**:
- 所有方法正确保存

**优先级**: P2

---

### 3.6 块活动特殊属性测试（关键）

#### TC-BLOCK-001: 块开始/结束坐标测试

**测试目的**: 验证块开始/结束坐标能正确保存和读取

**测试步骤**:
1. 创建块活动并设置坐标：
   ```json
   {
     "startOfBlock": {
       "participantId": "Participant_1",
       "firstActivityId": "act_001",
       "x": 100,
       "y": 200,
       "routing": "NO_ROUTING"
     },
     "endOfBlock": {
       "participantId": "Participant_1",
       "lastActivityId": "act_005",
       "x": 500,
       "y": 200,
       "routing": "NO_ROUTING"
     }
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证XPDL中StartOfBlock和EndOfBlock
5. 验证JSON中startOfBlock和endOfBlock

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| startOfBlock.participantId | "Participant_1" | | |
| startOfBlock.firstActivityId | "act_001" | | |
| startOfBlock.x | 100 | | |
| startOfBlock.y | 200 | | |
| endOfBlock.lastActivityId | "act_005" | | |
| endOfBlock.x | 500 | | |

**优先级**: P0

---

#### TC-BLOCK-002: 块内参与者顺序测试

**测试目的**: 验证块内参与者顺序能正确保存和读取

**测试步骤**:
1. 创建块活动并设置参与者顺序：
   ```json
   {
     "participantVisualOrder": "Participant_1;Participant_2;Participant_3"
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证XPDL中ParticipantVisualOrder
5. 验证JSON中participantVisualOrder

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| participantVisualOrder | "Participant_1;Participant_2;Participant_3" | | |

**优先级**: P1

---

## 4. 路由级属性测试用例

### 4.1 基本属性测试

#### TC-ROUTE-001: 路由基本属性测试

**测试目的**: 验证路由基本属性能正确保存和读取

**测试步骤**:
1. 创建路由并设置属性：
   ```json
   {
     "routeDefId": "route_001",
     "name": "提交审批",
     "fromActivityDefId": "act_001",
     "toActivityDefId": "act_002",
     "routeOrder": 1,
     "routeDirection": "FORWARD",
     "routeCondition": "${approved == true}",
     "routeConditionType": "CONDITION"
   }
   ```
2. 保存流程
3. 重新加载流程
4. 验证路由字段

**预期结果**:
| 字段 | 期望值 | 实际值 | 结果 |
|------|--------|--------|------|
| routeDefId | "route_001" | | |
| name | "提交审批" | | |
| fromActivityDefId | "act_001" | | |
| toActivityDefId | "act_002" | | |
| routeOrder | 1 | | |
| routeDirection | "FORWARD" | | |
| routeCondition | "${approved == true}" | | |
| routeConditionType | "CONDITION" | | |

**优先级**: P0

---

#### TC-ROUTE-002: 路由方向枚举测试

**测试目的**: 验证路由方向枚举能正确处理

**测试步骤**:
1. 测试路由方向：
   - FORWARD (前进)
   - BACKWARD (后退)
2. 为每种方向创建路由
3. 保存并重新加载
4. 验证方向值

**预期结果**:
- 所有方向正确保存

**优先级**: P1

---

#### TC-ROUTE-003: 条件类型枚举测试

**测试目的**: 验证条件类型枚举能正确处理

**测试步骤**:
1. 测试条件类型：
   - CONDITION (条件)
   - OTHERWISE (否则)
   - EXCEPTION (异常)
2. 为每种类型创建路由
3. 保存并重新加载
4. 验证类型值

**预期结果**:
- 所有类型正确保存

**优先级**: P1

---

## 5. 集成测试用例

### 5.1 完整流程测试

#### TC-INTEGRATION-001: 简单审批流程测试

**测试目的**: 验证完整审批流程能正确保存和读取

**测试步骤**:
1. 创建请假审批流程（见测试数据文件）
2. 包含：开始节点、填写请假单、部门经理审批、HR审批、通知结果、结束节点
3. 设置所有属性
4. 保存流程
5. 重新加载流程
6. 验证所有属性和连接关系

**预期结果**:
- 所有节点正确显示
- 所有路由正确连接
- 所有属性正确保存和读取

**优先级**: P0

---

#### TC-INTEGRATION-002: 会签流程测试

**测试目的**: 验证会签流程能正确保存和读取

**测试步骤**:
1. 创建会签流程
2. 包含：开始、并行会签（多个审批人）、汇聚、结束
3. 设置会签属性
4. 保存流程
5. 重新加载流程
6. 验证会签逻辑

**预期结果**:
- 并行分支正确显示
- 汇聚节点正确配置
- 会签属性正确保存

**优先级**: P0

---

#### TC-INTEGRATION-003: 子流程测试

**测试目的**: 验证子流程能正确保存和读取

**测试步骤**:
1. 创建包含子流程的流程
2. 设置子流程属性
3. 保存流程
4. 重新加载流程
5. 验证子流程配置

**预期结果**:
- 子流程正确显示
- 子流程属性正确保存

**优先级**: P1

---

## 6. 性能测试用例

### 6.1 大流程测试

#### TC-PERF-001: 大流程保存性能测试

**测试目的**: 验证大流程的保存性能

**测试步骤**:
1. 创建包含100个活动的流程
2. 设置所有属性
3. 测量保存时间
4. 测量加载时间

**预期结果**:
- 保存时间 < 5秒
- 加载时间 < 3秒

**优先级**: P2

---

## 7. 兼容性测试用例

### 7.1 XPDL导入测试

#### TC-COMPAT-001: 历史XPDL导入测试

**测试目的**: 验证历史XPDL文件能正确导入

**测试步骤**:
1. 准备历史XPDL文件（Swing版导出）
2. 导入到JS版设计器
3. 验证所有属性正确转换
4. 保存为JSON格式
5. 重新加载验证

**预期结果**:
- 所有XPDL属性正确转换为JSON
- 坐标、监听器、权限组等关键属性不丢失

**优先级**: P0

---

## 8. 测试数据文件

### 8.1 测试数据位置

测试数据文件位于：
- `e:\github\ooder-skills\skills\_drivers\bpm\test-data\`

包含：
1. `simple-approval-process.json` - 简单审批流程
2. `countersign-process.json` - 会签流程
3. `subflow-process.json` - 子流程
4. `complex-process.json` - 复杂流程
5. `historical.xpdl` - 历史XPDL文件

---

## 9. 测试执行计划

### 9.1 测试阶段

| 阶段 | 测试用例 | 时间 | 负责人 |
|------|----------|------|--------|
| 阶段1 | TC-BPD-001 ~ TC-BPD-003 | 1天 | |
| 阶段2 | TC-COORD-001 ~ TC-COORD-004 | 2天 | |
| 阶段3 | TC-LISTENER-001 ~ TC-LISTENER-003 | 2天 | |
| 阶段4 | TC-RIGHTGROUP-001 ~ TC-RIGHTGROUP-002 | 2天 | |
| 阶段5 | TC-POSITION-001 ~ TC-POSITION-002 | 2天 | |
| 阶段6 | TC-WORKFLOW-001 ~ TC-WORKFLOW-004 | 2天 | |
| 阶段7 | TC-RIGHT-001 ~ TC-RIGHT-004 | 2天 | |
| 阶段8 | TC-FORM-001, TC-SERVICE-001 ~ TC-SERVICE-002 | 1天 | |
| 阶段9 | TC-BLOCK-001 ~ TC-BLOCK-002 | 2天 | |
| 阶段10 | TC-ROUTE-001 ~ TC-ROUTE-003 | 1天 | |
| 阶段11 | TC-INTEGRATION-001 ~ TC-INTEGRATION-003 | 3天 | |
| 阶段12 | TC-PERF-001, TC-COMPAT-001 | 2天 | |

### 9.2 测试通过标准

- 所有P0测试用例100%通过
- 所有P1测试用例95%以上通过
- 性能测试满足预期指标
- 兼容性测试无数据丢失

---

*文档结束*

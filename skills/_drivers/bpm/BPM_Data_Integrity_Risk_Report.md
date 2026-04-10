# BPM流程引擎数据完整性风险报告

## 执行摘要

本报告基于对Swing版XPDL设计器源码的深入分析，识别出现有JSON转换实现中**严重丢失**的扩展属性。这些属性的丢失将严重影响数据完整性，导致流程定义在保存/加载过程中出现功能异常。

**风险等级**: 🔴 **高危**

---

## 1. Swing版完整扩展属性清单

### 1.1 流程级扩展属性 (WorkflowProcess)

#### 1.1.1 BPD标准属性

| 属性名 | 类型 | 存储位置 | 用途 | 风险等级 |
|--------|------|----------|------|----------|
| **VersionId** | String | BPM_PROCESSDEF_PROPERTY | 流程版本UUID | 🔴 高 |
| **Classification** | String | BPM_PROCESSDEF_PROPERTY | 流程分类 | 🟡 中 |
| **SystemCode** | String | BPM_PROCESSDEF_PROPERTY | 系统代码 | 🟡 中 |
| **Description** | String | BPM_PROCESSDEF_PROPERTY | 版本描述 | 🟢 低 |
| **ActiveTime** | DateTime | BPM_PROCESSDEF_PROPERTY | 激活时间 | 🟡 中 |
| **FreezeTime** | DateTime | BPM_PROCESSDEF_PROPERTY | 冻结时间 | 🟡 中 |
| **CreatorName** | String | BPM_PROCESSDEF_PROPERTY | 创建人姓名 | 🟢 低 |
| **ModifierId** | String | BPM_PROCESSDEF_PROPERTY | 修改人ID | 🟢 低 |
| **ModifierName** | String | BPM_PROCESSDEF_PROPERTY | 修改人姓名 | 🟢 低 |
| **ModifyTime** | DateTime | BPM_PROCESSDEF_PROPERTY | 修改时间 | 🟢 低 |
| **Limit** | Integer | BPM_PROCESSDEF_PROPERTY | 时间限制 | 🟡 中 |
| **DurationUnit** | String | BPM_PROCESSDEF_PROPERTY | 时间单位 | 🟡 中 |

#### 1.1.2 开始/结束节点坐标属性 (⚠️ 关键)

| 属性名 | 类型 | 存储格式 | 用途 | 风险等级 |
|--------|------|----------|------|----------|
| **StartOfWorkflow** | String | `ParticipantID;FirstActivityID;X;Y;Routing` | 开始节点坐标 | 🔴 **严重** |
| **EndOfWorkflow** | String | `ParticipantID;LastActivityID;X;Y;Routing` | 结束节点坐标 | 🔴 **严重** |

**格式说明**:
```
StartOfWorkflow: ParticipantID;FirstActivityID;XOffset;YOffset;IsRouted
EndOfWorkflow: ParticipantID;LastActivityID;XOffset;YOffset;IsRouted
```

#### 1.1.3 特殊结构属性

| 属性名 | 类型 | 存储方式 | 用途 | 风险等级 |
|--------|------|----------|------|----------|
| **Listeners** | XML | `<itjds:Listeners>` | 流程监听器 | 🔴 **严重** |
| **RightGroups** | XML | `<itjds:RightGroups>` | 自定义权限组 | 🔴 **严重** |
| **ParticipantVisualOrder** | String | ID列表 | 参与者视觉顺序 | 🟡 中 |

---

### 1.2 活动级扩展属性 (Activity)

#### 1.2.1 视觉位置属性 (⚠️ 关键)

| 属性名 | 类型 | 存储位置 | 用途 | 风险等级 |
|--------|------|----------|------|----------|
| **XOffset** | Integer | ExtendedAttribute | X坐标偏移 | 🔴 **严重** |
| **YOffset** | Integer | ExtendedAttribute | Y坐标偏移 | 🔴 **严重** |
| **ParticipantID** | String | ExtendedAttribute | 所属参与者ID | 🟡 中 |

#### 1.2.2 块活动特殊属性 (Block Activity)

| 属性名 | 类型 | 存储格式 | 用途 | 风险等级 |
|--------|------|----------|------|----------|
| **StartOfBlock** | String | `ParticipantID;FirstActivityID;X;Y;Routing` | 块开始坐标 | 🔴 **严重** |
| **EndOfBlock** | String | `ParticipantID;LastActivityID;X;Y;Routing` | 块结束坐标 | 🔴 **严重** |
| **ParticipantVisualOrder** | String | ID列表 | 块内参与者顺序 | 🟡 中 |

#### 1.2.3 用户自定义属性

| 属性名 | 类型 | 存储方式 | 用途 | 风险等级 |
|--------|------|----------|------|----------|
| **ActivitiesUserProperty[ID]** | String | ExtendedAttribute | 用户自定义属性 | 🟡 中 |

---

### 1.3 路由级扩展属性 (Transition)

| 属性名 | 类型 | 存储位置 | 用途 | 风险等级 |
|--------|------|----------|------|----------|
| **Routing** | String | ExtendedAttribute | 路由类型 | 🟡 中 |

---

## 2. JSON转换中丢失的属性分析

### 2.1 严重丢失 (🔴 将导致功能异常)

#### 2.1.1 开始/结束节点坐标

**Swing版存储方式**:
```xml
<ExtendedAttribute Name="StartOfWorkflow" Value="ParticipantID;ActivityID;100;200;NO_ROUTING" Type="BPD"/>
<ExtendedAttribute Name="EndOfWorkflow" Value="ParticipantID;ActivityID;500;200;NO_ROUTING" Type="BPD"/>
```

**现有JSON实现**:
```javascript
// 当前实现只保存了positionCoord在流程版本级别
// 丢失了ParticipantID、ActivityID、Routing等关键信息
{
  "startPosition": {"x": 100, "y": 200},
  "endPosition": {"x": 500, "y": 200}
}
```

**丢失信息**:
- ❌ ParticipantID (参与者ID)
- ❌ FirstActivityID/LastActivityID (连接的活动ID)
- ❌ Routing (路由类型: SIMPLE_ROUTING/NO_ROUTING)

**影响**: 流程图无法正确渲染，路由连接丢失

---

#### 2.1.2 活动坐标 (XOffset, YOffset)

**Swing版存储方式**:
```xml
<ExtendedAttribute Name="XOffset" Value="100"/>
<ExtendedAttribute Name="YOffset" Value="200"/>
<ExtendedAttribute Name="ParticipantID" Value="Participant_1"/>
```

**现有JSON实现**:
```javascript
// 当前实现使用WORKFLOW.positionCoord
{
  "WORKFLOW": {
    "positionCoord": "{\"x\":100,\"y\":200}"
  }
}
```

**丢失信息**:
- ❌ ParticipantID (所属泳道/参与者)
- ❌ 坐标与参与者的相对关系

**影响**: 活动无法正确显示在对应的泳道中

---

#### 2.1.3 监听器 (Listeners)

**Swing版存储方式**:
```xml
<ExtendedAttribute Name="Listeners">
  <itjds:Listeners>
    <itjds:Listener Id="..." Name="..." ListenerEvent="..." RealizeClass="..."/>
  </itjds:Listeners>
</ExtendedAttribute>
```

**现有JSON实现**:
```javascript
// 当前实现完全丢失
```

**影响**: 流程事件监听功能完全失效

---

#### 2.1.4 权限组 (RightGroups)

**Swing版存储方式**:
```xml
<ExtendedAttribute Name="RightGroups">
  <itjds:RightGroups>
    <itjds:RightGroup Id="..." Name="..." Code="..." Order="1" DefaultGroup="YES"/>
  </itjds:RightGroups>
</ExtendedAttribute>
```

**现有JSON实现**:
```javascript
// 当前实现完全丢失
```

**影响**: 自定义权限组功能失效

---

### 2.2 中度丢失 (🟡 影响部分功能)

#### 2.2.1 块活动坐标 (StartOfBlock/EndOfBlock)

**Swing版存储方式**:
```xml
<ExtendedAttribute Name="StartOfBlock" Value="ParticipantID;ActivityID;X;Y;Routing"/>
<ExtendedAttribute Name="EndOfBlock" Value="ParticipantID;ActivityID;X;Y;Routing"/>
```

**现有JSON实现**:
```javascript
// 未处理块活动的特殊坐标
```

**影响**: 块活动(子流程容器)的图形渲染异常

---

#### 2.2.2 参与者视觉顺序

**Swing版存储方式**:
```xml
<ExtendedAttribute Name="ParticipantVisualOrder" Value="Participant_1;Participant_2;Participant_3"/>
```

**现有JSON实现**:
```javascript
// 未保存
```

**影响**: 泳道显示顺序不一致

---

#### 2.2.3 用户自定义属性

**Swing版存储方式**:
```xml
<ExtendedAttribute Name="ActivitiesUserProperty123" Value="自定义值"/>
```

**现有JSON实现**:
```javascript
// 未保存
```

**影响**: 用户扩展属性丢失

---

## 3. 数据完整性风险评估

### 3.1 风险矩阵

| 风险项 | 概率 | 影响 | 风险等级 | 说明 |
|--------|------|------|----------|------|
| 开始/结束节点坐标丢失 | 高 | 严重 | 🔴 | 流程图渲染失败 |
| 活动坐标与泳道关系丢失 | 高 | 严重 | 🔴 | 活动位置错乱 |
| 监听器丢失 | 高 | 严重 | 🔴 | 事件处理失效 |
| 权限组丢失 | 中 | 严重 | 🔴 | 权限控制失效 |
| 块活动坐标丢失 | 中 | 中等 | 🟡 | 子流程渲染异常 |
| 参与者顺序丢失 | 低 | 轻微 | 🟢 | 显示顺序不一致 |
| 用户自定义属性丢失 | 中 | 中等 | 🟡 | 扩展功能失效 |

### 3.2 影响范围

1. **流程设计器**: 保存的流程无法正确加载显示
2. **流程引擎**: 监听器不触发，权限控制失效
3. **流程监控**: 流程图渲染异常，无法追踪流程状态
4. **数据迁移**: 历史XPDL数据无法正确转换为JSON

---

## 4. 修复建议

### 4.1 立即修复 (🔴 严重)

#### 4.1.1 开始/结束节点坐标完整保存

```javascript
// 建议的JSON格式
{
  "startNode": {
    "positionCoord": {"x": 100, "y": 200},
    "participantId": "Participant_1",
    "firstActivityId": "Activity_1",
    "routing": "NO_ROUTING"
  },
  "endNodes": [
    {
      "positionCoord": {"x": 500, "y": 200},
      "participantId": "Participant_1",
      "lastActivityId": "Activity_5",
      "routing": "NO_ROUTING"
    }
  ]
}
```

#### 4.1.2 活动坐标完整保存

```javascript
// 建议的JSON格式
{
  "positionCoord": {"x": 100, "y": 200},
  "participantId": "Participant_1",
  "xOffset": 100,
  "yOffset": 200
}
```

#### 4.1.3 监听器完整保存

```javascript
// 建议的JSON格式
{
  "listeners": [
    {
      "id": "...",
      "name": "...",
      "event": "PROCESS_START",
      "realizeClass": "com.example.Listener"
    }
  ]
}
```

### 4.2 后续修复 (🟡 中等)

1. 块活动坐标支持
2. 权限组完整支持
3. 用户自定义属性支持
4. 参与者视觉顺序支持

---

## 5. 兼容性方案

### 5.1 XPDL到JSON的完整转换

```java
public class XPDLToJSONConverter {
    
    public JSONObject convertWorkflowProcess(WorkflowProcess wp) {
        JSONObject json = new JSONObject();
        
        // 1. 基本属性
        json.put("processDefId", wp.getId());
        json.put("name", wp.getName());
        
        // 2. 开始节点坐标 (⚠️ 关键)
        Set startDescriptions = wp.getStartDescriptions();
        if (!startDescriptions.isEmpty()) {
            JSONArray startNodes = new JSONArray();
            for (String desc : startDescriptions) {
                String[] parts = desc.split(";");
                JSONObject startNode = new JSONObject();
                startNode.put("participantId", parts[0]);
                startNode.put("firstActivityId", parts[1]);
                startNode.put("x", Integer.parseInt(parts[2]));
                startNode.put("y", Integer.parseInt(parts[3]));
                startNode.put("routing", parts[4]);
                startNodes.put(startNode);
            }
            json.put("startNodes", startNodes);
        }
        
        // 3. 结束节点坐标 (⚠️ 关键)
        Set endDescriptions = wp.getEndDescriptions();
        // ... 类似处理
        
        // 4. 监听器 (⚠️ 关键)
        Listeners listeners = wp.getListeners();
        if (listeners != null && !listeners.isEmpty()) {
            json.put("listeners", convertListeners(listeners));
        }
        
        // 5. 权限组 (⚠️ 关键)
        RightGroups rightGroups = wp.getRightGroups();
        if (rightGroups != null && !rightGroups.isEmpty()) {
            json.put("rightGroups", convertRightGroups(rightGroups));
        }
        
        return json;
    }
}
```

### 5.2 JSON到XPDL的完整转换

```java
public class JSONToXPDLConverter {
    
    public WorkflowProcess convertToXPDL(JSONObject json) {
        WorkflowProcess wp = new WorkflowProcess();
        
        // 1. 基本属性
        wp.setId(json.getString("processDefId"));
        wp.setName(json.getString("name"));
        
        // 2. 开始节点坐标 (⚠️ 关键)
        if (json.has("startNodes")) {
            JSONArray startNodes = json.getJSONArray("startNodes");
            Set startDescriptions = new HashSet();
            for (int i = 0; i < startNodes.length(); i++) {
                JSONObject node = startNodes.getJSONObject(i);
                String desc = String.format("%s;%s;%d;%d;%s",
                    node.getString("participantId"),
                    node.getString("firstActivityId"),
                    node.getInt("x"),
                    node.getInt("y"),
                    node.getString("routing")
                );
                startDescriptions.add(desc);
            }
            wp.setStartDescriptions(startDescriptions);
        }
        
        // 3. 结束节点坐标 (⚠️ 关键)
        // ... 类似处理
        
        // 4. 监听器 (⚠️ 关键)
        if (json.has("listeners")) {
            wp.setListeners(convertListeners(json.getJSONArray("listeners")));
        }
        
        // 5. 权限组 (⚠️ 关键)
        if (json.has("rightGroups")) {
            wp.setRightGroups(convertRightGroups(json.getJSONArray("rightGroups")));
        }
        
        return wp;
    }
}
```

---

## 6. 测试建议

### 6.1 数据完整性测试用例

1. **开始/结束节点坐标测试**
   - 创建包含开始和结束节点的流程
   - 保存后重新加载
   - 验证坐标、参与者ID、连接活动ID正确

2. **监听器测试**
   - 创建包含监听器的流程
   - 保存后重新加载
   - 验证监听器配置完整

3. **权限组测试**
   - 创建包含自定义权限组的流程
   - 保存后重新加载
   - 验证权限组配置完整

4. **块活动测试**
   - 创建包含块活动的流程
   - 保存后重新加载
   - 验证块内坐标正确

### 6.2 回归测试

1. 历史XPDL文件导入测试
2. 复杂流程（多泳道、多分支）测试
3. 性能测试（大流程保存/加载）

---

## 7. 结论

现有JSON转换实现**严重丢失**了以下关键属性：

1. 🔴 **开始/结束节点坐标** - 包含ParticipantID、ActivityID、Routing等关键信息
2. 🔴 **活动坐标与泳道关系** - ParticipantID丢失导致泳道渲染异常
3. 🔴 **监听器** - 完全丢失，事件处理失效
4. 🔴 **权限组** - 完全丢失，权限控制失效

**建议立即修复**这些严重问题，以确保数据完整性和功能正常。

---

*报告生成时间: 2026-04-09*
*分析范围: Swing版XPDL设计器完整源码*
*风险等级: 🔴 高危*

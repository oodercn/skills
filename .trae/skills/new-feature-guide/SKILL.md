---
name: "new-feature-guide"
description: "Ooder新功能开发必读手册，包含技术架构规范、字典表规范、三闭环检查要求。开发新功能模块前必须阅读并遵循。"
---

# Ooder 新功能开发必读手册

> **版本**: v1.0  
> **适用范围**: Ooder 全模块新功能开发  
> **强制级别**: 必须遵循

---

## 一、开发前置检查清单

在开始任何新功能开发前，请确认已完成以下检查：

- [ ] 阅读并理解本文档所有规范
- [ ] 确认功能涉及的数据实体关系
- [ ] 设计完整的前后端API闭环
- [ ] 确定需要的字典枚举类型
- [ ] 规划页面与菜单结构

---

## 二、三闭环检查要求

### 2.1 能力生命周期流程闭环

**目标**: 确保功能从创建到销毁的完整生命周期可控

**检查项**:

| 生命周期阶段 | 检查内容 | 要求 |
|-------------|---------|------|
| **创建** | 是否有创建API？ | 前端调用 POST 接口 |
| **查询** | 是否有查询API？ | 支持列表和详情查询 |
| **更新** | 是否有更新API？ | 前端调用 PUT/PATCH 接口 |
| **状态变更** | 是否有状态转换API？ | 状态机设计完整 |
| **删除** | 是否有删除API？ | 级联删除关联数据 |

**状态机设计规范**:

```
┌─────────┐    操作    ┌─────────┐    操作    ┌─────────┐
│  初始   │ ───────► │  中间   │ ───────► │  终态   │
└─────────┘          └─────────┘          └─────────┘
```

**代码示例**:

```java
@Dict(code = "xxx_status", name = "XXX状态")
public enum XxxStatus implements DictItem {
    DRAFT("DRAFT", "草稿", "草稿状态", "ri-draft-line", 1),
    ACTIVE("ACTIVE", "激活", "激活状态", "ri-check-line", 2),
    SUSPENDED("SUSPENDED", "暂停", "暂停状态", "ri-pause-line", 3);
    
    // 必须实现 DictItem 接口
}
```

### 2.2 能力数据实体关系闭环

**目标**: 确保数据实体之间的关联关系完整一致

**检查项**:

| 检查项 | 检查内容 | 要求 |
|--------|---------|------|
| **实体关系图** | 是否绘制实体关系图？ | 明确一对多、多对多关系 |
| **数据一致性** | 前后端数据是否一致？ | 使用服务端计算结果 |
| **级联操作** | 是否处理级联操作？ | 删除时清理关联数据 |
| **外键约束** | 是否验证外键有效性？ | 防止孤儿数据 |

**实体关系图模板**:

```
EntityA ──1:N──► EntityB
    │
    └──N:M──► EntityC
                  │
                  └──1:1──► EntityD
```

**数据一致性保障**:

```javascript
// 正确模式：操作后重新加载
async function removeItem(id) {
    const response = await fetch('/api/v1/items/' + id, { method: 'DELETE' });
    const result = await response.json();
    if (result.code === 200) {
        loadData();  // 重新加载确保数据一致
    }
}

// 错误模式：仅本地删除
function removeItem(id) {
    items = items.filter(i => i.id !== id);  // 不要这样做！
    render();
}
```

### 2.3 按钮事件和API闭环

**目标**: 确保每个用户操作都能正确调用后端服务

**检查项**:

| 检查项 | 检查内容 | 要求 |
|--------|---------|------|
| **API存在** | 后端是否有对应API？ | 每个操作都有API |
| **前端调用** | 前端是否正确调用？ | 使用 async/await |
| **错误处理** | 是否处理错误情况？ | 显示错误信息 |
| **数据刷新** | 操作后是否刷新？ | 重新加载数据 |

**标准闭环模式**:

```javascript
async function standardAction(params) {
    // 1. 用户确认（可选）
    if (!confirm('确定要执行此操作吗？')) return;
    
    try {
        // 2. 调用后端API
        const response = await fetch('/api/v1/resource/' + id, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)
        });
        const result = await response.json();
        
        // 3. 处理结果
        if (result.code === 200) {
            // 4. 刷新数据
            loadData();
        } else {
            alert('操作失败: ' + result.message);
        }
    } catch (error) {
        console.error('Operation failed:', error);
        alert('操作失败');
    }
}
```

**闭环检查表模板**:

| 按钮 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|---------|---------|---------|---------|
| 创建 | `create()` | `POST /api/v1/xxx` | `XxxController.create()` | ✅/❌ |
| 更新 | `update()` | `PUT /api/v1/xxx/{id}` | `XxxController.update()` | ✅/❌ |
| 删除 | `remove()` | `DELETE /api/v1/xxx/{id}` | `XxxController.delete()` | ✅/❌ |

---

## 三、字典表规范

### 3.1 架构设计

```
后端枚举 ──@Dict注解──► DictService ──API──► 前端DictCache
    │                        │                      │
    └──实现DictItem接口──► 自动注册字典 ──预加载──► 本地缓存
```

### 3.2 后端实现规范

**字典注解定义**:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {
    String code();                          // 字典代码（必填）
    String name() default "";               // 字典名称
    String description() default "";        // 字典描述
    boolean cacheable() default true;       // 是否缓存
}
```

**字典项接口定义**:

```java
public interface DictItem {
    String getCode();        // 代码
    String getName();        // 名称
    String getDescription(); // 描述
    String getIcon();        // 图标
    int getSort();           // 排序
}
```

**枚举实现范例**:

```java
@Dict(code = "participant_type", name = "参与者类型", description = "场景参与者的类型")
public enum ParticipantType implements DictItem {
    
    USER("USER", "用户", "人类用户参与者", "ri-user-line", 1),
    AGENT("AGENT", "Agent", "智能代理参与者", "ri-robot-line", 2),
    SUPER_AGENT("SUPER_AGENT", "超级Agent", "超级智能代理参与者", "ri-robot-2-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ParticipantType(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}
```

### 3.3 前端使用规范

**字典缓存工具**:

```javascript
// 初始化
await DictCache.init();

// 获取字典
const dict = await DictCache.getDict(DictCache.DICT_CODES.PARTICIPANT_TYPE);

// 获取字典项列表
const items = await DictCache.getDictItems(DictCache.DICT_CODES.PARTICIPANT_TYPE);

// 获取字典项名称
const name = await DictCache.getDictItemName(DictCache.DICT_CODES.PARTICIPANT_TYPE, 'USER');
```

**字典代码常量**:

```javascript
var DICT_CODES = {
    CAPABILITY_TYPE: 'capability_type',
    PARTICIPANT_TYPE: 'participant_type',
    PARTICIPANT_ROLE: 'participant_role',
    SCENE_GROUP_STATUS: 'scene_group_status',
    // ... 新增字典需在此添加
};
```

### 3.4 新增字典步骤

1. 创建枚举类，实现 `DictItem` 接口
2. 添加 `@Dict` 注解
3. 在 `DictService.init()` 中注册枚举类
4. 在前端 `DictCache.DICT_CODES` 中添加字典代码常量

---

## 四、API响应格式规范

### 4.1 统一响应结构

```json
{
    "code": 200,
    "status": "success",
    "message": "操作成功",
    "data": {},
    "timestamp": "2026-03-01 10:30:00.000",
    "requestId": "REQ_1234567890_1234"
}
```

### 4.2 响应码定义

| 响应码 | 常量名 | 说明 |
|--------|--------|------|
| 200 | CODE_SUCCESS | 操作成功 |
| 400 | CODE_BAD_REQUEST | 请求参数错误 |
| 401 | CODE_UNAUTHORIZED | 未授权访问 |
| 403 | CODE_FORBIDDEN | 禁止访问 |
| 404 | CODE_NOT_FOUND | 资源不存在 |
| 500 | CODE_INTERNAL_SERVER_ERROR | 服务器内部错误 |

### 4.3 分页响应结构

```json
{
    "code": 200,
    "data": {
        "list": [],
        "total": 100,
        "pageNum": 1,
        "pageSize": 20,
        "pages": 5
    }
}
```

---

## 五、命名规范

### 5.1 包命名规范

| 包名 | 用途 |
|------|------|
| `dto` | 数据传输对象 |
| `model` | 领域模型 |
| `controller` | 控制器 |
| `service` | 服务层 |
| `config` | 配置类 |

### 5.2 类命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| DTO | XxxDTO | SceneGroupDTO |
| 枚举 | XxxType/XxxStatus | SceneGroupStatus |
| 控制器 | XxxController | SceneGroupController |
| 服务 | XxxService | SceneGroupService |

### 5.3 API路径规范

| 操作 | 路径模板 | 示例 |
|------|----------|------|
| 列表查询 | GET /api/v1/{resource} | /api/v1/scene-groups |
| 详情查询 | GET /api/v1/{resource}/{id} | /api/v1/scene-groups/sg-001 |
| 创建 | POST /api/v1/{resource} | /api/v1/scene-groups |
| 更新 | PUT /api/v1/{resource}/{id} | /api/v1/scene-groups/sg-001 |
| 删除 | DELETE /api/v1/{resource}/{id} | /api/v1/scene-groups/sg-001 |

---

## 六、"我的"系列页面设计原则

遵循"以用户为中心"的展示原则：

| 页面 | 设计原则 | 数据来源 |
|------|---------|---------|
| **我的XX** | 侧重我创建的、我启动的 | `listByCreator()` |
| **我的待办** | 别人把我拉到协作里、领导委派 | `TodoService.listMyTodos()` |
| **已完成XX** | 侧重查询历史数据 | `HistoryService.listMyHistory()` |

---

## 七、版本兼容规范

### 7.1 Java版本要求

- **最低版本**: Java 8
- **推荐版本**: Java 11+

### 7.2 API版本管理

- API 路径包含版本号：`/api/v1/`
- 版本升级时保持向后兼容
- 废弃接口需在文档中标注

---

## 八、开发流程规范

### 8.1 标准开发流程

```
1. 需求分析
   └── 绘制实体关系图
   └── 确定状态机
   
2. 后端开发
   └── 创建DTO/枚举
   └── 实现Service
   └── 实现Controller
   └── 注册字典
   
3. 前端开发
   └── 创建页面
   └── 实现API调用
   └── 使用字典缓存
   
4. 闭环检查
   └── 生命周期闭环
   └── 数据实体闭环
   └── 按钮API闭环
   
5. 测试验证
   └── 功能测试
   └── API测试
   └── 数据一致性测试
```

### 8.2 提交前检查

- [ ] 所有API都有对应的前端调用
- [ ] 所有操作都有错误处理
- [ ] 数据操作后正确刷新
- [ ] 字典枚举已注册
- [ ] 代码编译通过

---

## 九、相关文档

| 文档 | 路径 |
|------|------|
| 公共技术规范 | docs/COMMON_TECHNICAL_SPECIFICATION.md |
| 字典表规范与范例 | docs/DICT_SPECIFICATION.md |
| 场景页面闭环分析报告 | docs/SCENE_PAGE_CLOSED_LOOP_ANALYSIS.md |

---

## 十、快速参考

### 10.1 闭环检查清单

```
□ 生命周期闭环：创建→查询→更新→删除 API完整
□ 数据实体闭环：关系明确、级联处理、数据一致
□ 按钮API闭环：每个操作都调用后端、操作后刷新
```

### 10.2 字典创建清单

```
□ 创建枚举类实现 DictItem
□ 添加 @Dict 注解
□ 在 DictService 注册
□ 在前端添加 DICT_CODES 常量
```

### 10.3 API创建清单

```
□ 定义请求/响应DTO
□ 实现 Service 接口和实现类
□ 实现 Controller
□ 添加 @CrossOrigin 注解
□ 前端创建对应调用函数
```

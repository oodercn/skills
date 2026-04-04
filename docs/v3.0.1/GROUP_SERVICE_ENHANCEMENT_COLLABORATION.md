# Group Service 增强协作需求文档

**文档版本**: v1.0  
**创建日期**: 2026-04-04  
**协作团队**: skill-protocol、skill-group  
**优先级**: 中等

---

## 一、背景说明

在完成 AI Bridge Protocol 移植过程中，我们发现 `GroupService` 需要增强以下功能以支持完整的 Group 相关命令：

1. **链路管理** - `group.link.add`、`group.link.remove`
2. **数据存储** - `group.data.set`、`group.data.get`

---

## 二、当前状态

### 2.1 已实现功能

**文件位置**: `E:\github\ooder-skills\skills\capabilities\communication\skill-group\src\main\java\net\ooder\skill\group\service\GroupService.java`

**现有接口**:
```java
public interface GroupService {
    List<Group> getGroupList(String userId);
    Group createGroup(String name, String ownerId, String ownerName, List<String> memberIds, String groupType);
    Group getGroup(String groupId);
    List<GroupMember> getGroupMembers(String groupId);
    boolean addMember(String groupId, String userId, String userName);
    boolean removeMember(String groupId, String userId);
    boolean updateGroup(String groupId, Map<String, Object> params);
    boolean dismissGroup(String groupId, String userId);
    boolean setAnnouncement(String groupId, String announcement);
    boolean setMemberRole(String groupId, String userId, String role);
}
```

### 2.2 已实现的命令处理器

**文件位置**: `E:\github\ooder-skills\skills\_system\skill-protocol\src\main\java\net\ooder\skill\protocol\handler\group\`

- ✅ `GroupMemberAddCommandHandler` - 添加成员
- ✅ `GroupMemberRemoveCommandHandler` - 移除成员
- ⚠️ `GroupLinkAddCommandHandler` - 添加链路关系（占位符）
- ⚠️ `GroupLinkRemoveCommandHandler` - 移除链路关系（占位符）
- ⚠️ `GroupDataSetCommandHandler` - 设置频道数据（占位符）
- ⚠️ `GroupDataGetCommandHandler` - 获取频道数据（占位符）

---

## 三、需求说明

### 3.1 链路管理功能

#### 3.1.1 功能描述

链路管理用于管理Group之间的关联关系，支持建立Group之间的链接，形成Group网络。

#### 3.1.2 接口设计

```java
public interface GroupService {
    // 现有方法...
    
    /**
     * 添加Group链路关系
     * @param groupId 源Group ID
     * @param linkId 目标Group ID
     * @param linkType 链路类型（如：parent, child, sibling, reference等）
     * @param metadata 链路元数据
     * @return 是否成功
     */
    boolean addGroupLink(String groupId, String linkId, String linkType, Map<String, Object> metadata);
    
    /**
     * 移除Group链路关系
     * @param groupId 源Group ID
     * @param linkId 目标Group ID
     * @return 是否成功
     */
    boolean removeGroupLink(String groupId, String linkId);
    
    /**
     * 获取Group的所有链路关系
     * @param groupId Group ID
     * @return 链路关系列表
     */
    List<GroupLink> getGroupLinks(String groupId);
}
```

#### 3.1.3 数据模型

```java
public class GroupLink {
    private String linkId;           // 链路ID
    private String sourceGroupId;    // 源Group ID
    private String targetGroupId;    // 目标Group ID
    private String linkType;         // 链路类型
    private Map<String, Object> metadata;  // 元数据
    private long createdAt;          // 创建时间
    private String createdBy;        // 创建者
    
    // getters and setters...
}
```

#### 3.1.4 使用场景

1. **层级关系** - 建立父子Group关系，形成组织架构
2. **协作关系** - 建立Group之间的协作链接
3. **引用关系** - Group之间的引用和关联

---

### 3.2 数据存储功能

#### 3.2.1 功能描述

数据存储用于在Group中存储自定义数据，支持键值对存储，便于Group级别的数据共享。

#### 3.2.2 接口设计

```java
public interface GroupService {
    // 现有方法...
    
    /**
     * 设置Group数据
     * @param groupId Group ID
     * @param key 数据键
     * @param value 数据值
     * @return 是否成功
     */
    boolean setGroupData(String groupId, String key, Object value);
    
    /**
     * 获取Group数据
     * @param groupId Group ID
     * @param key 数据键
     * @return 数据值
     */
    Object getGroupData(String groupId, String key);
    
    /**
     * 获取Group的所有数据
     * @param groupId Group ID
     * @return 所有数据（键值对）
     */
    Map<String, Object> getAllGroupData(String groupId);
    
    /**
     * 删除Group数据
     * @param groupId Group ID
     * @param key 数据键
     * @return 是否成功
     */
    boolean deleteGroupData(String groupId, String key);
}
```

#### 3.2.3 数据模型

可以使用现有的键值对存储，或者创建专门的表：

```sql
CREATE TABLE group_data (
    id VARCHAR(64) PRIMARY KEY,
    group_id VARCHAR(64) NOT NULL,
    data_key VARCHAR(255) NOT NULL,
    data_value TEXT,
    data_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_group_id (group_id),
    UNIQUE KEY uk_group_key (group_id, data_key)
);
```

#### 3.2.4 使用场景

1. **配置存储** - 存储Group级别的配置信息
2. **状态共享** - Group成员之间共享状态数据
3. **元数据管理** - 存储Group的扩展元数据

---

## 四、实现建议

### 4.1 实现优先级

**Phase 1**（高优先级）:
- 数据存储功能（`setGroupData`、`getGroupData`）
- 基础的链路管理（`addGroupLink`、`removeGroupLink`）

**Phase 2**（中优先级）:
- 链路查询功能（`getGroupLinks`）
- 数据管理增强（`getAllGroupData`、`deleteGroupData`）

### 4.2 技术建议

1. **数据存储**:
   - 建议使用数据库存储（MySQL/H2）
   - 支持JSON格式的数据值
   - 考虑数据大小限制（建议单个值不超过1MB）

2. **链路管理**:
   - 建议使用独立的表存储链路关系
   - 支持双向链路查询
   - 考虑链路关系的权限控制

### 4.3 兼容性考虑

1. **向后兼容** - 新增方法不影响现有功能
2. **性能优化** - 考虑缓存机制
3. **权限控制** - 只有Group成员才能操作数据和链路

---

## 五、命令处理器集成

### 5.1 GroupLinkAddCommandHandler

**当前状态**: 占位符实现

**完整实现代码**:
```java
@Component
public class GroupLinkAddCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private GroupService groupService;
    
    @Override
    public String getCommand() {
        return "group.link.add";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String groupId = getParamAsString(message, "group_id");
        String linkId = getParamAsString(message, "link_id");
        String linkType = getParamAsString(message, "link_type");
        Map<String, Object> metadata = (Map<String, Object>) getParam(message, "metadata");
        
        if (groupId == null || groupId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: group_id");
        }
        
        if (linkId == null || linkId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: link_id");
        }
        
        boolean success = groupService.addGroupLink(groupId, linkId, linkType, metadata);
        
        if (!success) {
            return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, 
                "Failed to add group link");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("group_id", groupId);
        response.put("link_id", linkId);
        response.put("status", "added");
        response.put("message", "Group link added successfully");
        
        return buildSuccessResponse(message, response);
    }
}
```

### 5.2 GroupDataSetCommandHandler

**当前状态**: 占位符实现

**完整实现代码**:
```java
@Component
public class GroupDataSetCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private GroupService groupService;
    
    @Override
    public String getCommand() {
        return "group.data.set";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String groupId = getParamAsString(message, "group_id");
        String dataKey = getParamAsString(message, "data_key");
        Object dataValue = getParam(message, "data_value");
        
        if (groupId == null || groupId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: group_id");
        }
        
        if (dataKey == null || dataKey.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: data_key");
        }
        
        boolean success = groupService.setGroupData(groupId, dataKey, dataValue);
        
        if (!success) {
            return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, 
                "Failed to set group data");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("group_id", groupId);
        response.put("data_key", dataKey);
        response.put("status", "set");
        response.put("message", "Group data set successfully");
        
        return buildSuccessResponse(message, response);
    }
}
```

---

## 六、测试建议

### 6.1 单元测试

```java
@Test
public void testAddGroupLink() {
    String groupId = "group-001";
    String linkId = "group-002";
    String linkType = "parent";
    
    boolean result = groupService.addGroupLink(groupId, linkId, linkType, null);
    assertTrue(result);
    
    List<GroupLink> links = groupService.getGroupLinks(groupId);
    assertFalse(links.isEmpty());
}

@Test
public void testSetGroupData() {
    String groupId = "group-001";
    String key = "config";
    Object value = Map.of("theme", "dark", "language", "zh-CN");
    
    boolean result = groupService.setGroupData(groupId, key, value);
    assertTrue(result);
    
    Object retrieved = groupService.getGroupData(groupId, key);
    assertNotNull(retrieved);
}
```

### 6.2 集成测试

测试完整的命令处理流程：
1. 创建Group
2. 添加链路关系
3. 设置Group数据
4. 查询验证

---

## 七、时间计划

| 阶段 | 任务 | 预计工期 | 负责团队 |
|-----|------|---------|---------|
| Phase 1 | 数据存储功能实现 | 3个工作日 | skill-group团队 |
| Phase 1 | 链路管理基础功能 | 3个工作日 | skill-group团队 |
| Phase 2 | 命令处理器集成 | 2个工作日 | skill-protocol团队 |
| Phase 2 | 单元测试和集成测试 | 2个工作日 | 测试团队 |

**总计**: 10个工作日

---

## 八、联系方式

**协作文档**: `E:\github\ooder-skills\docs\v3.0.1\GROUP_SERVICE_ENHANCEMENT_COLLABORATION.md`

**相关文档**:
- AI Bridge Protocol 移植报告: `E:\github\ooder-skills\docs\v3.0.1\AI_BRIDGE_PROTOCOL_MIGRATION_FINAL_REPORT.md`
- 移植总结报告: `E:\github\ooder-skills\docs\v3.0.1\OODER_AGENT_MIGRATION_SUMMARY.md`

---

**文档维护**: 本文档应在功能实现过程中持续更新。

**变更记录**:
- 2026-04-04 v1.0: 初始版本创建，说明Group服务增强需求

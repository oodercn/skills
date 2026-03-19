# 技术建议：场景技能分类逻辑统一处理方案

## 致：scene-engine 团队

## 一、背景与问题

### 1.1 当前问题

**问题现象**: skill-index.yaml 中 `category` 字段被错误用于存储场景技能类型 (ABS/TBS/ASS)

**问题根源**: 
1. 概念混淆：`category` (技能分类) 与 `skillType` (场景技能类型) 未分离
2. 逻辑分散：分类计算逻辑在 skills 和 engine 两处都有实现
3. 数据不完整：skill-index.yaml 缺少分类计算所需的字段

### 1.2 影响范围

| 影响 | 说明 |
|------|------|
| 数据闭环 | 16.7% 闭环度，分类计算无法执行 |
| 代码重复 | skills 和 engine 都有分类逻辑 |
| 维护困难 | 两处逻辑需要同步维护 |

---

## 二、建议方案

### 2.1 核心原则

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        设计原则                                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   1. 场景是一种特殊的技能                                                      │
│      - 场景技能是技能的子集                                                    │
│      - 分类体系以技能为中心                                                    │
│                                                                             │
│   2. 技能驱动为中心                                                            │
│      - category: 技能分类 (knowledge/llm/org/...)                            │
│      - skillType: 场景技能类型 (ABS/ASS/TBS)，是技能属性                        │
│                                                                             │
│   3. 统一处理逻辑                                                              │
│      - scene-engine 提供统一的分类计算服务                                      │
│      - skills 调用 engine 服务，不自行实现                                      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 架构设计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        架构设计                                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ooder-skills (数据层)                                                      │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  skill-index.yaml                                                    │   │
│   │  - category: knowledge        # 技能分类                              │   │
│   │  - spec.type: scene-skill     # 技能类型                              │   │
│   │  - spec.mainFirst: true       # 自驱标志                              │   │
│   │  - spec.driverConditions: [...]# 驱动条件                             │   │
│   │  - spec.participants: [...]   # 参与者                                │   │
│   │  - spec.visibility: public    # 可见性                                │   │
│   │  - spec.businessTags: [...]   # 业务标签                              │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼ 调用                                    │
│   scene-engine (服务层)                                                      │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  SceneSkillClassificationService                                     │   │
│   │  ├── isSceneSkill(metadata) → boolean                                │   │
│   │  ├── calculateSkillType(metadata) → ABS | ASS | TBS                  │   │
│   │  ├── calculateBusinessScore(metadata) → int (0-10)                   │   │
│   │  └── checkSelfDrive(metadata) → boolean                              │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、接口定义

### 3.1 服务接口

```java
package net.ooder.scene.skill.classification;

/**
 * 场景技能分类服务接口
 * 统一处理场景技能的分类计算逻辑
 */
public interface SceneSkillClassificationService {
    
    /**
     * 判断是否为场景技能
     * 
     * 判断优先级：
     * 1. sceneSkill = true
     * 2. type = "scene-skill"
     * 
     * @param metadata 技能元数据
     * @return 是否为场景技能
     */
    boolean isSceneSkill(Map<String, Object> metadata);
    
    /**
     * 计算场景技能类型
     * 
     * 分类规则：
     * - ABS: hasSelfDrive=true && score>=8
     * - ASS: hasSelfDrive=true && score<8
     * - TBS: hasSelfDrive=false && score>=8
     * - NOT_SCENE_SKILL: 其他情况
     * 
     * @param metadata 技能元数据
     * @return 场景技能类型
     */
    SceneSkillType calculateSkillType(Map<String, Object> metadata);
    
    /**
     * 计算业务语义评分
     * 
     * 评分规则：
     * - driverConditions非空: +3分
     * - participants非空: +3分
     * - visibility=public: +2分
     * - collaborationCapability: +1分
     * - businessTags非空: +1分
     * 
     * @param metadata 技能元数据
     * @return 业务语义评分 (0-10)
     */
    int calculateBusinessScore(Map<String, Object> metadata);
    
    /**
     * 检查自驱能力
     * 
     * 判断条件：
     * - mainFirst = true
     * - mainFirstConfig 非空
     * - driverConditions 非空
     * 
     * @param metadata 技能元数据
     * @return 是否有自驱能力
     */
    boolean checkSelfDrive(Map<String, Object> metadata);
    
    /**
     * 获取完整的分类结果
     * 
     * @param metadata 技能元数据
     * @return 分类结果
     */
    SceneSkillClassificationResult classify(Map<String, Object> metadata);
}
```

### 3.2 结果模型

```java
package net.ooder.scene.skill.classification;

/**
 * 场景技能分类结果
 */
public class SceneSkillClassificationResult {
    
    private boolean sceneSkill;              // 是否场景技能
    private SceneSkillType skillType;        // 场景技能类型
    private int businessScore;               // 业务语义评分
    private boolean hasSelfDrive;            // 是否有自驱能力
    
    // 评分明细
    private int driverConditionsScore;       // 驱动条件评分
    private int participantsScore;           // 参与者评分
    private int visibilityScore;             // 可见性评分
    private int collaborationScore;          // 协作能力评分
    private int businessTagsScore;           // 业务标签评分
    
    // 元信息
    private String skillId;                  // 技能ID
    private String category;                 // 技能分类
    private long calculatedAt;               // 计算时间
    
    // getters and setters...
}

/**
 * 场景技能类型枚举
 */
public enum SceneSkillType {
    ABS("ABS", "自驱业务场景", "Auto Business Scene"),
    ASS("ASS", "自驱系统场景", "Auto System Scene"),
    TBS("TBS", "触发业务场景", "Trigger Business Scene"),
    NOT_SCENE_SKILL("NOT_SCENE_SKILL", "非场景技能", "Not Scene Skill");
    
    private final String code;
    private final String name;
    private final String nameEn;
    
    // constructor, getters...
    
    public boolean isSceneSkill() {
        return this != NOT_SCENE_SKILL;
    }
    
    public boolean hasSelfDrive() {
        return this == ABS || this == ASS;
    }
}
```

### 3.3 REST API

```yaml
# 场景技能分类 API

# 单个技能分类
POST /api/v1/classification/classify
Request:
  {
    "skillId": "skill-document-assistant",
    "metadata": {
      "type": "scene-skill",
      "mainFirst": true,
      "mainFirstConfig": {...},
      "driverConditions": [...],
      "participants": [...],
      "visibility": "public",
      "businessTags": [...]
    }
  }
Response:
  {
    "sceneSkill": true,
    "skillType": "ABS",
    "businessScore": 9,
    "hasSelfDrive": true,
    "scoreDetails": {
      "driverConditions": 3,
      "participants": 3,
      "visibility": 2,
      "collaboration": 0,
      "businessTags": 1
    }
  }

# 批量分类
POST /api/v1/classification/classify-batch
Request:
  {
    "skills": [
      {"skillId": "skill-1", "metadata": {...}},
      {"skillId": "skill-2", "metadata": {...}}
    ]
  }
Response:
  {
    "results": [
      {"skillId": "skill-1", "skillType": "ABS", ...},
      {"skillId": "skill-2", "skillType": "TBS", ...}
    ]
  }
```

---

## 四、数据结构规范

### 4.1 skill-index.yaml 规范

```yaml
# 技能索引规范 v2.3
- skillId: skill-document-assistant
  name: 智能文档助手
  version: "2.3"
  
  # 技能分类 (必填)
  category: knowledge
  
  # 技能类型 (必填)
  type: scene-skill
  
  # 场景技能类型 (可选，自动计算)
  skillType: abs
  
  # 自驱能力配置
  mainFirst: true
  mainFirstConfig:
    autoStart: true
    delay: 5s
  driverConditions:
    - type: user-query
      
  # 业务语义配置
  participants:
    - role: EMPLOYEE
  visibility: public
  businessTags:
    - document
    - assistant
```

### 4.2 字段说明

| 字段 | 必填 | 来源 | 说明 |
|------|:----:|------|------|
| `category` | ✅ | 手动填写 | 技能分类 |
| `type` | ✅ | 手动填写 | 技能类型 |
| `skillType` | ❌ | 自动计算 | 场景技能类型 |
| `mainFirst` | ❌ | 手动填写 | 自驱标志 |
| `mainFirstConfig` | ❌ | 手动填写 | 自驱配置 |
| `driverConditions` | ❌ | 手动填写 | 驱动条件 |
| `participants` | ❌ | 手动填写 | 参与者 |
| `visibility` | ❌ | 手动填写 | 可见性 |
| `businessTags` | ❌ | 手动填写 | 业务标签 |

---

## 五、迁移计划

### 5.1 Phase 1: 服务实现 (engine 团队)

| 任务 | 说明 | 工作量 |
|------|------|:------:|
| 实现 SceneSkillClassificationService | 统一分类服务 | 2天 |
| 实现 REST API | 分类接口 | 1天 |
| 单元测试 | 覆盖所有分类场景 | 1天 |
| 文档更新 | API 文档 | 0.5天 |

### 5.2 Phase 2: 数据修复 (skills 团队)

| 任务 | 说明 | 工作量 |
|------|------|:------:|
| 修复 skill-index.yaml | category 误用修复 | 1天 |
| 补充缺失字段 | 添加分类计算所需字段 | 2天 |
| 调用 engine 服务 | 替换本地分类逻辑 | 1天 |

### 5.3 Phase 3: 废弃处理 (联合)

| 任务 | 说明 | 工作量 |
|------|------|:------:|
| 废弃 skills 中的分类逻辑 | 标记废弃 | 0.5天 |
| 清理冗余代码 | 移除重复实现 | 1天 |
| 验证测试 | 端到端测试 | 1天 |

---

## 六、兼容性处理

### 6.1 向后兼容

```java
// 兼容旧字段
public List<String> getBusinessTags(Map<String, Object> metadata) {
    // 优先使用 businessTags
    List<String> tags = (List<String>) metadata.get("businessTags");
    if (tags != null && !tags.isEmpty()) {
        return tags;
    }
    // 兼容 tags
    return (List<String>) metadata.get("tags");
}

// 兼容旧分类
public boolean isSceneSkill(Map<String, Object> metadata) {
    // 优先检查 sceneSkill
    if (Boolean.TRUE.equals(metadata.get("sceneSkill"))) {
        return true;
    }
    // 兼容 type
    Object type = metadata.get("type");
    return "scene-skill".equals(type);
}
```

### 6.2 废弃计划

| 字段 | v2.3 | v2.4 | v3.0 |
|------|------|------|------|
| `tags` | 废弃警告 | 移除 | - |
| `sceneSkill` | 保留 | 保留 | 可选 |

---

## 七、验收标准

### 7.1 功能验收

- [ ] engine 提供统一的分类服务
- [ ] skills 调用 engine 服务进行分类
- [ ] 分类结果与现有逻辑一致
- [ ] 支持批量分类

### 7.2 性能验收

- [ ] 单次分类响应时间 < 10ms
- [ ] 批量分类 (100个) 响应时间 < 500ms
- [ ] 支持缓存，重复计算 < 1ms

### 7.3 兼容性验收

- [ ] 旧数据正常工作
- [ ] 新数据符合规范
- [ ] 无破坏性变更

---

## 八、联系方式

如有问题，请联系：

- **skills 团队**: [skills-team@ooder.net]
- **engine 团队**: [engine-team@ooder.net]

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team

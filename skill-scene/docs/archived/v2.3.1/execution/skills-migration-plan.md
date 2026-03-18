# Skills 配置迁移方案

## 一、Engine v3.0 决定汇总

根据 [V3_MODEL_DEBATE_RESPONSE.md](file:///E:/github/ooder-sdk/scene-engine/docs/V3_MODEL_DEBATE_RESPONSE.md)：

| 问题 | Engine 决定 |
|------|-------------|
| 分类体系 | **方案C: 两层分类** - Engine 保持功能分类，Skills 定义业务领域 |
| ASS 归属 | **方案A: 合并到AUTO** - 通过 visibility 字段区分 |
| nexus-ui 归类 | **方案A: 合并到UI** - 通过其他字段区分加载方式 |
| util 命名 | **TOOL** - 与其他分类命名风格一致 |
| visibility | **纳入 Engine 核心模型** |

---

## 二、字段映射

### 2.1 新增字段

| 字段 | 类型 | 说明 | 来源 |
|------|------|------|------|
| `category` | enum | 功能分类 | Engine 定义 |
| `domain` | string | 业务领域 | Skills 定义 |
| `form` | enum | 技能形态 | Engine 定义 |
| `sceneType` | enum | 场景类型 | Engine 定义 |
| `visibility` | enum | 可见性 | Engine 定义 |

### 2.2 分类映射

| 旧分类 | 新分类 (category) | 业务领域 (domain) |
|--------|-------------------|-------------------|
| `abs` | KNOWLEDGE | knowledge |
| `tbs` | WORKFLOW | knowledge |
| `ass` | SERVICE | sys |
| `llm` | LLM | llm |
| `knowledge` | KNOWLEDGE | knowledge |
| `util` | TOOL | - |
| `nexus-ui` | UI | nexus |
| `org` | SERVICE | org |
| `vfs` | DATA | vfs |
| `msg` | SERVICE | msg |
| `sys` | SERVICE | sys |
| `payment` | SERVICE | payment |
| `media` | WORKFLOW | media |

### 2.3 场景类型映射

| 旧类型 | 新类型 (sceneType) | 可见性 (visibility) |
|--------|-------------------|---------------------|
| `abs` | AUTO | PUBLIC |
| `ass` | AUTO | INTERNAL |
| `tbs` | TRIGGER | PUBLIC |

---

## 三、skill-index.yaml 修复

### 3.1 修复前

```yaml
- skillId: skill-document-assistant
  name: 智能文档助手
  version: "1.0.0"
  category: abs              # ❌ 错误
  subCategory: knowledge
  mainFirst: true
```

### 3.2 修复后

```yaml
- skillId: skill-document-assistant
  name: 智能文档助手
  version: "1.0.0"
  category: KNOWLEDGE        # ✅ 功能分类
  domain: knowledge          # ✅ 业务领域
  form: SCENE                # ✅ 技能形态
  sceneType: AUTO            # ✅ 场景类型
  visibility: PUBLIC         # ✅ 可见性
  mainFirst: true
```

---

## 四、完整修复列表

### 4.1 场景技能修复

| skillId | 旧category | 新category | domain | sceneType | visibility |
|---------|------------|------------|--------|-----------|------------|
| skill-document-assistant | abs | KNOWLEDGE | knowledge | AUTO | PUBLIC |
| skill-onboarding-assistant | abs | KNOWLEDGE | hr | AUTO | PUBLIC |
| skill-knowledge-share | ass | SERVICE | knowledge | AUTO | INTERNAL |
| skill-meeting-minutes | tbs | WORKFLOW | content | TRIGGER | PUBLIC |
| skill-project-knowledge | tbs | WORKFLOW | knowledge | TRIGGER | PUBLIC |

### 4.2 普通技能修复

| skillId | 旧category | 新category | domain | form |
|---------|------------|------------|--------|------|
| skill-knowledge-base | knowledge | KNOWLEDGE | knowledge | STANDALONE |
| skill-rag | knowledge | KNOWLEDGE | knowledge | STANDALONE |
| skill-llm-conversation | llm | LLM | llm | STANDALONE |
| skill-llm-openai | llm | LLM | llm | STANDALONE |
| skill-vfs-database | vfs | DATA | vfs | STANDALONE |
| skill-user-auth | org | SERVICE | org | STANDALONE |
| skill-monitor | sys | SERVICE | sys | STANDALONE |
| skill-share | util | TOOL | - | STANDALONE |
| skill-knowledge-ui | nexus-ui | UI | nexus | STANDALONE |

---

## 五、迁移脚本

### 5.1 skill-index.yaml 迁移

```python
# migrate_skill_index.py

CATEGORY_MAP = {
    'abs': ('KNOWLEDGE', 'AUTO', 'PUBLIC'),
    'ass': ('SERVICE', 'AUTO', 'INTERNAL'),
    'tbs': ('WORKFLOW', 'TRIGGER', 'PUBLIC'),
    'llm': ('LLM', None, None),
    'knowledge': ('KNOWLEDGE', None, None),
    'util': ('TOOL', None, None),
    'nexus-ui': ('UI', None, None),
    'org': ('SERVICE', None, None),
    'vfs': ('DATA', None, None),
    'msg': ('SERVICE', None, None),
    'sys': ('SERVICE', None, None),
    'payment': ('SERVICE', None, None),
    'media': ('WORKFLOW', None, None),
}

DOMAIN_MAP = {
    'knowledge': 'knowledge',
    'llm': 'llm',
    'org': 'org',
    'vfs': 'vfs',
    'msg': 'msg',
    'sys': 'sys',
    'payment': 'payment',
    'media': 'media',
    'nexus-ui': 'nexus',
    'util': None,
}

def migrate_skill(skill):
    old_category = skill.get('category', 'OTHER')
    
    if old_category in CATEGORY_MAP:
        new_category, scene_type, visibility = CATEGORY_MAP[old_category]
        skill['category'] = new_category
        
        # 如果是场景技能
        if scene_type:
            skill['form'] = 'SCENE'
            skill['sceneType'] = scene_type
            skill['visibility'] = visibility
        else:
            skill['form'] = 'STANDALONE'
        
        # 设置业务领域
        domain = skill.get('subCategory') or DOMAIN_MAP.get(old_category)
        if domain:
            skill['domain'] = domain
    
    return skill
```

### 5.2 数据库迁移

```sql
-- 1. 添加新字段
ALTER TABLE skills ADD COLUMN form VARCHAR(20) DEFAULT 'STANDALONE';
ALTER TABLE skills ADD COLUMN scene_type VARCHAR(20);
ALTER TABLE skills ADD COLUMN visibility VARCHAR(20) DEFAULT 'PUBLIC';
ALTER TABLE skills ADD COLUMN domain VARCHAR(50);

-- 2. 迁移场景技能
UPDATE skills SET 
  category = 'KNOWLEDGE',
  form = 'SCENE',
  scene_type = 'AUTO',
  visibility = 'PUBLIC',
  domain = 'knowledge'
WHERE category = 'abs';

UPDATE skills SET 
  category = 'SERVICE',
  form = 'SCENE',
  scene_type = 'AUTO',
  visibility = 'INTERNAL',
  domain = 'sys'
WHERE category = 'ass';

UPDATE skills SET 
  category = 'WORKFLOW',
  form = 'SCENE',
  scene_type = 'TRIGGER',
  visibility = 'PUBLIC',
  domain = 'knowledge'
WHERE category = 'tbs';

-- 3. 迁移普通技能
UPDATE skills SET category = 'KNOWLEDGE', domain = 'knowledge' WHERE category = 'knowledge';
UPDATE skills SET category = 'LLM', domain = 'llm' WHERE category = 'llm';
UPDATE skills SET category = 'TOOL' WHERE category = 'util';
UPDATE skills SET category = 'UI', domain = 'nexus' WHERE category = 'nexus-ui';
UPDATE skills SET category = 'SERVICE', domain = 'org' WHERE category = 'org';
UPDATE skills SET category = 'DATA', domain = 'vfs' WHERE category = 'vfs';
UPDATE skills SET category = 'SERVICE', domain = 'msg' WHERE category = 'msg';
UPDATE skills SET category = 'SERVICE', domain = 'sys' WHERE category = 'sys';
UPDATE skills SET category = 'SERVICE', domain = 'payment' WHERE category = 'payment';
UPDATE skills SET category = 'WORKFLOW', domain = 'media' WHERE category = 'media';

-- 4. 设置默认 form
UPDATE skills SET form = 'STANDALONE' WHERE form IS NULL OR form = '';
UPDATE skills SET form = 'SCENE' WHERE scene_type IS NOT NULL;
```

---

## 六、验证清单

### 6.1 数据验证

- [ ] 所有技能都有 `category` 字段
- [ ] 场景技能有 `form: SCENE`
- [ ] 场景技能有 `sceneType` 字段
- [ ] 没有 `abs/tbs/ass` 作为分类

### 6.2 功能验证

- [ ] 技能列表正常显示
- [ ] 分类筛选正常工作
- [ ] 场景技能激活正常
- [ ] 能力绑定正常

---

## 七、执行计划

| 阶段 | 任务 | 负责人 | 时间 |
|------|------|--------|------|
| 1 | 更新 skill-index.yaml categories | Skills | 立即 |
| 2 | 修复错误分类 | Skills | 立即 |
| 3 | 添加新字段 | Skills | 立即 |
| 4 | 数据库迁移 | DBA | 待定 |
| 5 | 功能验证 | QA | 待定 |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team

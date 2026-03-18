# Skills 本地程序对照表文档

> **版本**: v1.0  
> **创建日期**: 2026-03-11  
> **适用范围**: 应用端本地 Skills 程序开发

---

## 一、过滤规则

### 1.1 可见性过滤规则

| 用户角色 | 可见性范围 | 说明 |
|----------|------------|------|
| **user (普通用户)** | PUBLIC | 仅可见 PUBLIC 标记的技能 |
| **developer (开发者)** | PUBLIC + DEVELOPER | 可见 PUBLIC 和 DEVELOPER 标记的技能 |
| **admin (管理员)** | PUBLIC + DEVELOPER + ADMIN | 可见所有技能 |

### 1.2 分类过滤规则

```typescript
interface FilterRule {
  role: 'user' | 'developer' | 'admin';
  allowedVisibility: string[];
  allowedCategories: string[];
  allowedSkillTypes: string[];
}

const FILTER_RULES: Record<string, FilterRule> = {
  user: {
    role: 'user',
    allowedVisibility: ['PUBLIC'],
    allowedCategories: ['llm', 'know', 'util'],
    allowedSkillTypes: ['SCENE', 'PROVIDER']
  },
  developer: {
    role: 'developer',
    allowedVisibility: ['PUBLIC', 'DEVELOPER'],
    allowedCategories: ['llm', 'know', 'util', 'org', 'vfs', 'db', 'payment', 'media', 'comm', 'search'],
    allowedSkillTypes: ['SCENE', 'PROVIDER', 'DRIVER']
  },
  admin: {
    role: 'admin',
    allowedVisibility: ['PUBLIC', 'DEVELOPER', 'ADMIN'],
    allowedCategories: ['*'],  // 所有分类
    allowedSkillTypes: ['SCENE', 'PROVIDER', 'DRIVER', 'INTERNAL']
  }
};
```

### 1.3 安装权限规则

| 用户角色 | 可安装类型 | 说明 |
|----------|------------|------|
| **user** | SCENE | 仅可安装场景技能 |
| **developer** | SCENE + PROVIDER | 可安装场景和提供者技能 |
| **admin** | 全部 | 可安装所有类型技能 |

---

## 二、参考接口实现

### 2.1 获取分类列表接口

```typescript
// GET /api/categories
interface GetCategoriesRequest {
  visibility?: ('PUBLIC' | 'DEVELOPER' | 'ADMIN')[];
}

interface Category {
  id: string;
  name: string;
  nameEn: string;
  description: string;
  icon: string;
  order: number;
  address: string;
  visibility: 'PUBLIC' | 'DEVELOPER' | 'ADMIN';
  ownership: 'SIC' | 'IC' | 'PC' | 'TOOL';
  userFacing: boolean;
  displayGroup?: string;
  skillCount: number;
}

// 响应示例
const response: Category[] = [
  {
    id: 'llm',
    name: 'AI助手',
    nameEn: 'AI Assistant',
    description: '大语言模型对话、上下文、配置管理',
    icon: 'brain',
    order: 7,
    address: '0x30-0x37',
    visibility: 'PUBLIC',
    ownership: 'IC',
    userFacing: true,
    displayGroup: '业务场景',
    skillCount: 7
  }
];
```

### 2.2 获取技能列表接口

```typescript
// GET /api/skills
interface GetSkillsRequest {
  category?: string;
  visibility?: ('PUBLIC' | 'DEVELOPER' | 'ADMIN')[];
  skillType?: ('SCENE' | 'PROVIDER' | 'DRIVER' | 'INTERNAL')[];
  search?: string;
  page?: number;
  pageSize?: number;
}

interface Skill {
  skillId: string;
  name: string;
  version: string;
  category: string;
  subCategory: string;
  visibility: 'PUBLIC' | 'DEVELOPER' | 'ADMIN';
  ownership: 'SIC' | 'IC' | 'PC' | 'TOOL';
  skillType: 'SCENE' | 'PROVIDER' | 'DRIVER' | 'INTERNAL';
  tags: string[];
  description: string;
  sceneId: string;
  path: string;
  downloadUrl?: string;
  checksum?: string;
}

// 响应示例
const response: {
  total: number;
  page: number;
  pageSize: number;
  items: Skill[];
} = {
  total: 63,
  page: 1,
  pageSize: 20,
  items: [...]
};
```

### 2.3 前端过滤实现示例

```typescript
class SkillFilter {
  // 根据用户角色过滤分类
  filterCategoriesByRole(categories: Category[], role: UserRole): Category[] {
    const allowedVisibility = FILTER_RULES[role].allowedVisibility;
    return categories.filter(c => allowedVisibility.includes(c.visibility));
  }

  // 根据用户角色过滤技能
  filterSkillsByRole(skills: Skill[], role: UserRole): Skill[] {
    const allowedVisibility = FILTER_RULES[role].allowedVisibility;
    return skills.filter(s => allowedVisibility.includes(s.visibility));
  }

  // 根据分类过滤技能
  filterSkillsByCategory(skills: Skill[], categoryId: string): Skill[] {
    return skills.filter(s => s.category === categoryId);
  }

  // 根据类型过滤技能
  filterSkillsByType(skills: Skill[], types: string[]): Skill[] {
    return skills.filter(s => types.includes(s.skillType));
  }

  // 搜索技能
  searchSkills(skills: Skill[], query: string): Skill[] {
    const lowerQuery = query.toLowerCase();
    return skills.filter(s => 
      s.name.toLowerCase().includes(lowerQuery) ||
      s.description.toLowerCase().includes(lowerQuery) ||
      s.tags.some(t => t.toLowerCase().includes(lowerQuery))
    );
  }
}
```

---

## 三、测试样本对比数据

### 3.1 普通用户视角 (user)

#### 可见分类 (3个)

| 分类ID | 名称 | 技能数 | 地址范围 |
|--------|------|--------|----------|
| llm | AI助手 | 7 | 0x30-0x37 |
| know | 知识服务 | 5 | 0x38-0x3F |
| util | 工具服务 | 4 | 0xF0-0xFF |

#### 可见技能列表 (11个)

| skillId | 名称 | 分类 | skillType |
|---------|------|------|-----------|
| skill-llm-chat | LLM智能对话场景能力 | llm | SCENE |
| skill-knowledge-qa | 知识问答场景能力 | know | SCENE |
| skill-document-processor | 文档处理服务 | util | PROVIDER |
| skill-share | 技能分享技能 | util | PROVIDER |
| skill-report | 报表服务 | util | PROVIDER |
| skill-market | 技能市场服务 | util | SCENE |
| skill-collaboration | 协作场景服务 | util | SCENE |
| skill-business | 业务场景服务 | util | SCENE |
| skill-llm-volcengine | 火山引擎豆包LLM Provider | llm | DRIVER |
| skill-llm-qianwen | 通义千问LLM Provider | llm | DRIVER |
| skill-llm-deepseek | DeepSeek LLM Provider | llm | DRIVER |

#### 统计汇总

```
普通用户视角统计:
├── 可见分类: 3 个
├── 可见技能: 11 个
├── 按类型分布:
│   ├── SCENE: 5 个
│   ├── PROVIDER: 3 个
│   └── DRIVER: 3 个
└── 按分类分布:
    ├── llm: 4 个
    ├── know: 1 个
    └── util: 6 个
```

---

### 3.2 开发者视角 (developer)

#### 可见分类 (10个)

| 分类ID | 名称 | 可见性 | 技能数 | 地址范围 |
|--------|------|--------|--------|----------|
| llm | AI助手 | PUBLIC | 7 | 0x30-0x37 |
| know | 知识服务 | PUBLIC | 5 | 0x38-0x3F |
| util | 工具服务 | PUBLIC | 4 | 0xF0-0xFF |
| org | 组织服务 | DEVELOPER | 6 | 0x08-0x0F |
| vfs | 存储服务 | DEVELOPER | 6 | 0x20-0x27 |
| db | 数据库服务 | DEVELOPER | 0 | 0x28-0x2F |
| payment | 支付服务 | DEVELOPER | 3 | 0x40-0x47 |
| media | 媒体发布 | DEVELOPER | 5 | 0x48-0x4F |
| comm | 通讯服务 | DEVELOPER | 6 | 0x50-0x57 |
| search | 搜索服务 | DEVELOPER | 1 | 0x68-0x6F |

#### 可见技能列表 (54个)

**PUBLIC 技能 (11个)** - 同普通用户

**DEVELOPER 技能 (43个)**:

| skillId | 名称 | 分类 | skillType |
|---------|------|------|-----------|
| skill-llm-conversation | LLM对话服务 | llm | PROVIDER |
| skill-llm-context-builder | 上下文构建服务 | llm | PROVIDER |
| skill-llm-config-manager | LLM配置管理 | llm | PROVIDER |
| skill-knowledge-base | 知识库核心服务 | know | PROVIDER |
| skill-rag | RAG检索增强 | know | PROVIDER |
| skill-local-knowledge | 本地知识服务 | know | PROVIDER |
| skill-vector-sqlite | SQLite向量存储 | know | PROVIDER |
| skill-org-base | 组织基础服务 | org | PROVIDER |
| skill-org-dingding | 钉钉组织服务 | org | DRIVER |
| skill-org-feishu | 飞书组织服务 | org | DRIVER |
| skill-org-wecom | 企业微信组织服务 | org | DRIVER |
| skill-org-ldap | LDAP组织服务 | org | DRIVER |
| skill-vfs-base | VFS基础服务 | vfs | PROVIDER |
| skill-vfs-database | 数据库存储服务 | vfs | DRIVER |
| skill-vfs-local | 本地文件存储服务 | vfs | DRIVER |
| skill-vfs-minio | MinIO存储服务 | vfs | DRIVER |
| skill-vfs-oss | 阿里云OSS存储服务 | vfs | DRIVER |
| skill-vfs-s3 | AWS S3存储服务 | vfs | DRIVER |
| skill-payment-alipay | 支付宝支付Provider | payment | DRIVER |
| skill-payment-wechat | 微信支付Provider | payment | DRIVER |
| skill-payment-unionpay | 银联支付Provider | payment | DRIVER |
| skill-media-wechat | 微信公众号发布Provider | media | DRIVER |
| skill-media-weibo | 微博发布Provider | media | DRIVER |
| skill-media-zhihu | 知乎发布Provider | media | DRIVER |
| skill-media-toutiao | 头条发布Provider | media | DRIVER |
| skill-media-xiaohongshu | 小红书发布Provider | media | DRIVER |
| skill-mqtt | MQTT服务技能 | comm | PROVIDER |
| skill-im | 即时通讯服务 | comm | PROVIDER |
| skill-group | 群组管理服务 | comm | PROVIDER |
| skill-notify | 通知服务 | comm | PROVIDER |
| skill-email | 邮件服务 | comm | PROVIDER |
| skill-msg | 消息服务 | comm | PROVIDER |
| skill-search | 搜索服务 | search | PROVIDER |

#### 统计汇总

```
开发者视角统计:
├── 可见分类: 10 个
├── 可见技能: 54 个
├── 按可见性分布:
│   ├── PUBLIC: 11 个
│   └── DEVELOPER: 43 个
├── 按类型分布:
│   ├── SCENE: 5 个
│   ├── PROVIDER: 18 个
│   └── DRIVER: 31 个
└── 按分类分布:
    ├── llm: 7 个
    ├── know: 5 个
    ├── util: 4 个
    ├── org: 5 个
    ├── vfs: 6 个
    ├── payment: 3 个
    ├── media: 5 个
    ├── comm: 6 个
    └── search: 1 个
```

---

### 3.3 管理员视角 (admin)

#### 可见分类 (17个)

| 分类ID | 名称 | 可见性 | 技能数 | 地址范围 |
|--------|------|--------|--------|----------|
| sys | 系统核心 | ADMIN | 4 | 0x00-0x07 |
| org | 组织服务 | DEVELOPER | 5 | 0x08-0x0F |
| auth | 认证服务 | ADMIN | 1 | 0x10-0x17 |
| net | 网络服务 | ADMIN | 1 | 0x18-0x1F |
| vfs | 存储服务 | DEVELOPER | 6 | 0x20-0x27 |
| db | 数据库服务 | DEVELOPER | 0 | 0x28-0x2F |
| llm | AI助手 | PUBLIC | 7 | 0x30-0x37 |
| know | 知识服务 | PUBLIC | 5 | 0x38-0x3F |
| payment | 支付服务 | DEVELOPER | 3 | 0x40-0x47 |
| media | 媒体发布 | DEVELOPER | 5 | 0x48-0x4F |
| comm | 通讯服务 | DEVELOPER | 6 | 0x50-0x57 |
| mon | 监控服务 | ADMIN | 7 | 0x58-0x5F |
| iot | 物联网服务 | ADMIN | 3 | 0x60-0x67 |
| search | 搜索服务 | DEVELOPER | 1 | 0x68-0x6F |
| sched | 调度服务 | ADMIN | 2 | 0x70-0x77 |
| sec | 安全服务 | ADMIN | 3 | 0x78-0x7F |
| util | 工具服务 | PUBLIC | 4 | 0xF0-0xFF |

#### 可见技能列表 (63个)

**全部技能，按分类分布:**

| 分类 | 技能数 | 技能列表 |
|------|--------|----------|
| sys | 4 | skill-capability, skill-protocol, skill-common, skill-management |
| org | 5 | skill-org-base, skill-org-dingding, skill-org-feishu, skill-org-wecom, skill-org-ldap |
| auth | 1 | skill-user-auth |
| net | 1 | skill-network |
| vfs | 6 | skill-vfs-base, skill-vfs-database, skill-vfs-local, skill-vfs-minio, skill-vfs-oss, skill-vfs-s3 |
| llm | 7 | skill-llm-chat, skill-llm-conversation, skill-llm-context-builder, skill-llm-config-manager, skill-llm-volcengine, skill-llm-qianwen, skill-llm-deepseek |
| know | 5 | skill-knowledge-base, skill-rag, skill-local-knowledge, skill-vector-sqlite, skill-knowledge-qa |
| payment | 3 | skill-payment-alipay, skill-payment-wechat, skill-payment-unionpay |
| media | 5 | skill-media-wechat, skill-media-weibo, skill-media-zhihu, skill-media-toutiao, skill-media-xiaohongshu |
| comm | 6 | skill-mqtt, skill-im, skill-group, skill-notify, skill-email, skill-msg |
| mon | 7 | skill-health, skill-agent, skill-monitor, skill-network, skill-remote-terminal, skill-cmd-service, skill-res-service |
| iot | 3 | skill-openwrt, skill-hosting, skill-k8s |
| search | 1 | skill-search |
| sched | 2 | skill-task, skill-scheduler-quartz |
| sec | 3 | skill-security, skill-audit, skill-access-control |
| util | 4 | skill-document-processor, skill-share, skill-report, skill-market, skill-collaboration, skill-business |

#### 统计汇总

```
管理员视角统计:
├── 可见分类: 17 个
├── 可见技能: 63 个
├── 按可见性分布:
│   ├── PUBLIC: 11 个
│   ├── DEVELOPER: 43 个
│   └── ADMIN: 9 个
├── 按类型分布:
│   ├── SCENE: 5 个
│   ├── PROVIDER: 34 个
│   ├── DRIVER: 22 个
│   └── INTERNAL: 2 个
└── 按分类分布:
    ├── sys: 4 个
    ├── org: 5 个
    ├── auth: 1 个
    ├── net: 1 个
    ├── vfs: 6 个
    ├── llm: 7 个
    ├── know: 5 个
    ├── payment: 3 个
    ├── media: 5 个
    ├── comm: 6 个
    ├── mon: 7 个
    ├── iot: 3 个
    ├── search: 1 个
    ├── sched: 2 个
    ├── sec: 3 个
    └── util: 4 个
```

---

## 四、快速对照表

### 4.1 用户视角对比

| 视角 | 分类数 | 技能数 | SCENE | PROVIDER | DRIVER | INTERNAL |
|------|--------|--------|-------|----------|--------|----------|
| **普通用户** | 3 | 11 | 5 | 3 | 3 | 0 |
| **开发者** | 10 | 54 | 5 | 18 | 31 | 0 |
| **管理员** | 17 | 63 | 5 | 34 | 22 | 2 |

### 4.2 分类可见性对照

| 分类 | 名称 | 可见性 | 普通用户 | 开发者 | 管理员 |
|------|------|--------|:--------:|:------:|:------:|
| llm | AI助手 | PUBLIC | ✅ | ✅ | ✅ |
| know | 知识服务 | PUBLIC | ✅ | ✅ | ✅ |
| util | 工具服务 | PUBLIC | ✅ | ✅ | ✅ |
| org | 组织服务 | DEVELOPER | ❌ | ✅ | ✅ |
| vfs | 存储服务 | DEVELOPER | ❌ | ✅ | ✅ |
| db | 数据库服务 | DEVELOPER | ❌ | ✅ | ✅ |
| payment | 支付服务 | DEVELOPER | ❌ | ✅ | ✅ |
| media | 媒体发布 | DEVELOPER | ❌ | ✅ | ✅ |
| comm | 通讯服务 | DEVELOPER | ❌ | ✅ | ✅ |
| search | 搜索服务 | DEVELOPER | ❌ | ✅ | ✅ |
| sys | 系统核心 | ADMIN | ❌ | ❌ | ✅ |
| auth | 认证服务 | ADMIN | ❌ | ❌ | ✅ |
| net | 网络服务 | ADMIN | ❌ | ❌ | ✅ |
| mon | 监控服务 | ADMIN | ❌ | ❌ | ✅ |
| iot | 物联网服务 | ADMIN | ❌ | ❌ | ✅ |
| sched | 调度服务 | ADMIN | ❌ | ❌ | ✅ |
| sec | 安全服务 | ADMIN | ❌ | ❌ | ✅ |

---

## 五、测试用例

### 5.1 普通用户测试用例

```typescript
describe('User Role Filter', () => {
  const filter = new SkillFilter();
  const role = 'user';

  test('should show only PUBLIC categories', () => {
    const categories = filter.filterCategoriesByRole(allCategories, role);
    expect(categories.length).toBe(3);
    expect(categories.every(c => c.visibility === 'PUBLIC')).toBe(true);
  });

  test('should show only PUBLIC skills', () => {
    const skills = filter.filterSkillsByRole(allSkills, role);
    expect(skills.length).toBe(11);
    expect(skills.every(s => s.visibility === 'PUBLIC')).toBe(true);
  });

  test('should not show ADMIN skills', () => {
    const skills = filter.filterSkillsByRole(allSkills, role);
    const adminSkills = skills.filter(s => s.visibility === 'ADMIN');
    expect(adminSkills.length).toBe(0);
  });
});
```

### 5.2 开发者测试用例

```typescript
describe('Developer Role Filter', () => {
  const filter = new SkillFilter();
  const role = 'developer';

  test('should show PUBLIC and DEVELOPER categories', () => {
    const categories = filter.filterCategoriesByRole(allCategories, role);
    expect(categories.length).toBe(10);
    expect(categories.every(c => ['PUBLIC', 'DEVELOPER'].includes(c.visibility))).toBe(true);
  });

  test('should show 54 skills', () => {
    const skills = filter.filterSkillsByRole(allSkills, role);
    expect(skills.length).toBe(54);
  });

  test('should not show ADMIN skills', () => {
    const skills = filter.filterSkillsByRole(allSkills, role);
    const adminSkills = skills.filter(s => s.visibility === 'ADMIN');
    expect(adminSkills.length).toBe(0);
  });
});
```

### 5.3 管理员测试用例

```typescript
describe('Admin Role Filter', () => {
  const filter = new SkillFilter();
  const role = 'admin';

  test('should show all categories', () => {
    const categories = filter.filterCategoriesByRole(allCategories, role);
    expect(categories.length).toBe(17);
  });

  test('should show all skills', () => {
    const skills = filter.filterSkillsByRole(allSkills, role);
    expect(skills.length).toBe(63);
  });

  test('should show all visibility levels', () => {
    const skills = filter.filterSkillsByRole(allSkills, role);
    const visibilities = new Set(skills.map(s => s.visibility));
    expect(visibilities.has('PUBLIC')).toBe(true);
    expect(visibilities.has('DEVELOPER')).toBe(true);
    expect(visibilities.has('ADMIN')).toBe(true);
  });
});
```

---

## 六、附录

### 6.1 完整技能清单

详见 [skill-index.yaml](./skill-index.yaml)

### 6.2 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-11 | 初始版本 |

---

**文档维护**: Ooder 开发团队  
**最后更新**: 2026-03-11

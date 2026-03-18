# 协作任务：skill-org-base 本地JSON存储降级实现

## 任务背景

skill-scene 需要使用 OrgSkill 接口进行用户认证和组织管理。当前 OrgSkill 只有接口定义，缺少本地JSON存储的降级实现。

## 实现状态

**已完成** - 2026-03-06

## 实现内容

### 1. LocalOrgSkill 实现类

文件位置: `skill-org-base/src/main/java/net/ooder/skill/org/base/LocalOrgSkill.java`

已实现功能：

| 方法 | 状态 | 说明 |
|------|------|------|
| `login` | ✅ | 从本地JSON验证用户，生成token |
| `logout` | ✅ | 清除token |
| `validateToken` | ✅ | 验证token有效性，支持过期检查 |
| `refreshToken` | ✅ | 刷新token |
| `getUser` | ✅ | 从JSON读取用户信息 |
| `getUserByAccount` | ✅ | 根据账号获取用户 |
| `registerUser` | ✅ | 写入用户到JSON |
| `updateUser` | ✅ | 更新JSON中的用户信息 |
| `deleteUser` | ✅ | 从JSON删除用户（保护admin） |
| `listUsers` | ✅ | 分页读取JSON用户列表 |
| `getOrgTree` | ✅ | 从JSON读取组织树 |
| `getOrg` | ✅ | 获取单个组织 |
| `getOrgUsers` | ✅ | 获取组织下用户 |
| `createDepartment` | ✅ | 创建部门写入JSON |
| `updateDepartment` | ✅ | 更新JSON中的部门 |
| `deleteDepartment` | ✅ | 从JSON删除部门 |
| `createUser` | ✅ | 创建用户 |
| `batchCreateUsers` | ✅ | 批量创建用户 |
| `invoke` | ✅ | 通用能力调用 |

### 2. JSON存储结构

```
data/org/
├── users/              # 用户数据目录
│   ├── admin.json      # 用户记录
│   └── user-xxx.json   # 其他用户
├── orgs/               # 组织数据目录
│   └── root.json       # 组织记录
└── tokens/             # Token缓存目录
    └── xxx.json        # Token记录
```

### 3. 配置项

```yaml
org:
  local:
    enabled: true           # 是否启用
    data-path: ./data/org   # 数据存储路径
    token-expire: 86400     # Token过期时间(秒)
```

### 4. Spring自动配置

文件位置: `skill-org-base/src/main/java/net/ooder/skill/org/base/OrgSkillAutoConfiguration.java`

特性：
- 使用 `@ConditionalOnMissingBean(OrgSkill.class)` 自动注入
- 当没有其他 OrgSkill 实现时自动启用
- 支持配置属性注入

### 5. skill.yaml 配置

文件位置: `skill-org-base/skill.yaml`

包含：
- 能力定义 (user.auth, user.manage, org.manage, role.manage, sync)
- 端点定义
- 离线支持配置
- 降级优先级配置

## 交付物

| 文件 | 状态 |
|------|------|
| `LocalOrgSkill.java` | ✅ 已完成 |
| `OrgSkillAutoConfiguration.java` | ✅ 已完成 |
| `skill.yaml` | ✅ 已完成 |
| `META-INF/spring.factories` | ✅ 已完成 |
| 单元测试 | 待完成 |

## 验收标准

- [x] 所有 OrgSkill 接口方法正常工作
- [x] 数据持久化到JSON文件
- [x] 支持Token生成和验证
- [x] Spring自动配置生效
- [x] 编译通过
- [ ] 单元测试覆盖率 > 80%

## 使用方式

### 方式一：自动注入（推荐）

当没有其他 OrgSkill 实现时，Spring Boot 会自动注入 LocalOrgSkill：

```java
@Autowired
private OrgSkill orgSkill;  // 自动注入 LocalOrgSkill
```

### 方式二：手动配置

```yaml
org:
  local:
    enabled: true
    data-path: ./data/org
    token-expire: 86400
```

### 方式三：SkillsCaption 用户界面配置

通过 SkillsCaption 界面进行能力绑定配置，将 `skill-org-local` 作为降级方案。

## 默认数据

系统启动时自动创建：
- 默认组织: `root` (默认组织)
- 默认用户: `admin` (管理员账号)

## 联系人

skill-scene 团队

# skill-index.yaml 合并清理方案

## 一、问题识别

### 1.1 重复技能定义

| 技能ID | 重复次数 | 位置 |
|--------|:--------:|------|
| `skill-health` | 2 | 行712, 行854 |
| `skill-agent` | 2 | 行678, 行873 |
| `skill-openwrt` | 2 | 行746, 行892 |
| `skill-audit` | 2 | 行911, 行1276 |

### 1.2 分类不一致

发现以下分类使用小写格式，需要统一为大写：

| 当前值 | 应修正为 |
|--------|----------|
| `Category: collaboration` | `category: COLLABORATION` |
| `Category: object` | `subcategory` (不是分类) |
| `Category: health` | `subcategory` (不是分类) |
| `Category: base` | `subcategory` (不是分类) |

### 1.3 分类定义缺失

以下分类在 Engine v3.0 中未定义，需要与 Engine 团队确认：

| 分类 | 建议处理 |
|------|----------|
| `COLLABORATION` | 新增到 Engine v3.0 |
| `SCENE` | 新增到 Engine v3.0 或合并到 SERVICE |
| `MESSAGING` | 合并到 COMMUNICATION |
| `INFRASTRUCTURE` | 合并到 SERVICE |

---

## 二、合并清理方案

### 2.1 重复技能合并

#### skill-health (健康检查)

**保留版本** (行854，更详细):
```yaml
- skillId: skill-health
  name: Health Check Service
  version: "2.3.1"
  category: SERVICE
  domain: sys
  subCategory: health
  tags:
    - health
    - check
    - monitor
    - service
    - report
  description: 健康检查服务 - 提供系统健康检查、服务检查和报告生成能力
  sceneId: health
  path: skills/skill-health
```

**删除版本** (行712):
- 原因: 描述较简单，tags 较少

#### skill-agent (代理管理)

**保留版本** (行873，更详细):
```yaml
- skillId: skill-agent
  name: Agent Management Service
  version: "2.3.1"
  category: SERVICE
  domain: sys
  subCategory: agent
  tags:
    - agent
    - terminal
    - management
    - command
    - execution
  description: 代理管理服务 - 提供终端代理注册、管理和命令执行能力
  sceneId: agent
  path: skills/skill-agent
```

**删除版本** (行678):
- 原因: 描述较简单，tags 较少

#### skill-openwrt (OpenWrt路由器)

**保留版本** (行892，更详细):
```yaml
- skillId: skill-openwrt
  name: OpenWrt Router Driver
  version: "2.3.1"
  category: SERVICE
  domain: sys
  subCategory: openwrt
  tags:
    - openwrt
    - router
    - uci
    - network
    - driver
  description: OpenWrt路由器驱动 - 提供OpenWrt路由器连接、配置和命令执行能力
  sceneId: openwrt
  path: skills/skill-openwrt
```

**删除版本** (行746):
- 原因: 描述较简单，tags 较少

#### skill-audit (审计服务)

**保留版本** (行1276，更详细):
```yaml
- skillId: skill-audit
  name: Audit Service
  version: "2.3.1"
  category: SERVICE
  domain: sys
  subCategory: audit
  tags:
    - audit
    - log
    - compliance
    - security
  description: 审计服务 - 提供操作审计、日志查询、合规报告能力
  sceneId: sys
  path: skills/skill-audit
```

**删除版本** (行911):
- 原因: 描述较简单

### 2.2 分类统一

#### 需要修正的分类

| 行号 | 当前值 | 修正为 |
|------|--------|--------|
| 1547 | `category: COLLABORATION` | 保持不变 (待 Engine 确认) |
| 1788 | `category: SCENE` | 合并到 SERVICE 或新增 |
| 2006 | `category: MESSAGING` | 改为 COMMUNICATION |

#### 建议的分类映射

| 当前分类 | 目标分类 | 说明 |
|----------|----------|------|
| `COLLABORATION` | 新增 | 协作类技能 |
| `SCENE` | SERVICE | 场景技能属于服务类 |
| `MESSAGING` | COMMUNICATION | 消息属于通讯类 |
| `INFRASTRUCTURE` | SERVICE | 基础设施属于服务类 |

---

## 三、清理脚本

```powershell
# skill-index.yaml 清理脚本

$filePath = "e:\github\ooder-skills\skill-index.yaml"

# 读取文件内容
$bytes = [System.IO.File]::ReadAllBytes($filePath)
$content = [System.Text.Encoding]::UTF8.GetString($bytes)
$lines = $content -split "`n"

# 1. 删除重复技能定义
# 需要手动处理，因为需要识别具体行号

# 2. 统一分类
$content = $content -replace 'category: MESSAGING', 'category: COMMUNICATION'
$content = $content -replace 'category: SCENE', 'category: SERVICE'

# 保存文件
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText($filePath, $content, $utf8NoBom)

Write-Host "Cleanup completed!"
```

---

## 四、手动清理清单

### 4.1 需要删除的重复定义

| 技能ID | 删除行号 | 保留行号 |
|--------|:--------:|:--------:|
| `skill-health` | 712-726 | 854-871 |
| `skill-agent` | 678-693 | 873-890 |
| `skill-openwrt` | 746-756 | 892-909 |
| `skill-audit` | 911-925 | 1276-1292 |

### 4.2 需要修正的分类

| 行号 | 当前值 | 修正为 |
|------|--------|--------|
| 2006 | `category: MESSAGING` | `category: COMMUNICATION` |
| 1788 | `category: SCENE` | `category: SERVICE` |

---

## 五、清理后预期结果

### 5.1 技能数量变化

| 项目 | 清理前 | 清理后 | 减少 |
|------|:------:|:------:|:----:|
| 技能总数 | 133 | 129 | 4 |
| 重复定义 | 4 | 0 | 4 |

### 5.2 分类统计变化

| 分类 | 清理前 | 清理后 |
|------|:------:|:------:|
| SERVICE | 71 | 72 |
| COMMUNICATION | 5 | 6 |
| MESSAGING | 1 | 0 |
| SCENE | 1 | 0 |

---

## 六、执行步骤

1. **备份文件**
   ```bash
   cp skill-index.yaml skill-index.yaml.bak
   ```

2. **删除重复定义**
   - 手动删除行 712-726 (skill-health 重复)
   - 手动删除行 678-693 (skill-agent 重复)
   - 手动删除行 746-756 (skill-openwrt 重复)
   - 手动删除行 911-925 (skill-audit 重复)

3. **修正分类**
   - MESSAGING → COMMUNICATION
   - SCENE → SERVICE

4. **验证结果**
   ```bash
   # 检查是否还有重复
   grep -n "skillId:" skill-index.yaml | cut -d: -f2 | sort | uniq -d
   ```

5. **提交变更**
   ```bash
   git add skill-index.yaml
   git commit -m "fix: 清理重复技能定义，统一分类格式"
   ```

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team

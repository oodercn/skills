# Skills 版本升级报告

> 文档路径: `e:\github\ooder-skills\docs\v3.0.1\VERSION_UPGRADE_REPORT.md`
> 创建时间: 2026-04-03
> 目标版本: 3.0.1

---

## 一、版本统计

### 1.1 需要升级的Skills (2.3.1 → 3.0.1)

| 类别 | 数量 | 说明 |
|------|------|------|
| 场景技能 (scenes) | 10 | skill-business, skill-knowledge-qa等 |
| VFS驱动 | 6 | skill-vfs-base, skill-vfs-local等 |
| 组织驱动 | 3 | skill-org-dingding, skill-org-feishu, skill-org-wecom |
| 工具技能 (tools) | 5 | skill-msg-push, skill-agent-cli等 |
| 系统技能 (_system) | 2 | skill-common, skill-protocol |
| LLM驱动 | 1 | skill-llm-baidu |
| **总计** | **27** | 需要升级版本号 |

### 1.2 已升级为3.0.1的Skills

| 类别 | 数量 | 说明 |
|------|------|------|
| LLM驱动 | 8 | skill-llm-base, skill-llm-deepseek等 |
| LLM配置 | 2 | skill-llm-config, skill-llm-config-manager |
| LLM监控 | 1 | skill-llm-monitor |
| 场景管理 | 1 | skill-scenes |
| **总计** | **12** | 已完成升级 |

---

## 二、需要升级的Skills详情

### 2.1 场景技能 (scenes)

| 技能ID | 当前版本 | 目标版本 | 文件路径 |
|--------|----------|----------|----------|
| skill-business | 2.3.1 | 3.0.1 | skills/scenes/skill-business/skill.yaml |
| skill-knowledge-qa | 2.3.1 | 3.0.1 | skills/scenes/skill-knowledge-qa/skill.yaml |
| skill-project-knowledge | 2.3.1 | 3.0.1 | skills/scenes/skill-project-knowledge/skill.yaml |
| skill-platform-bind | 2.3.1 | 3.0.1 | skills/scenes/skill-platform-bind/skill.yaml |
| skill-collaboration | 2.3.1 | 3.0.1 | skills/scenes/skill-collaboration/skill.yaml |
| skill-recruitment-management | 2.3.1 | 3.0.1 | skills/scenes/skill-recruitment-management/skill.yaml |
| skill-knowledge-management | 2.3.1 | 3.0.1 | skills/scenes/skill-knowledge-management/skill.yaml |
| daily-report | 2.3.1 | 3.0.1 | skills/scenes/daily-report/skill.yaml |
| skill-meeting-minutes | 2.3.1 | 3.0.1 | skills/scenes/skill-meeting-minutes/skill.yaml |
| skill-recording-qa | 2.3.1 | 3.0.1 | skills/scenes/skill-recording-qa/skill.yaml |
| skill-approval-form | 2.3.1 | 3.0.1 | skills/scenes/skill-approval-form/skill.yaml |

### 2.2 VFS驱动 (_drivers/vfs)

| 技能ID | 当前版本 | 目标版本 | 文件路径 |
|--------|----------|----------|----------|
| skill-vfs-base | 2.3.1 | 3.0.1 | skills/_drivers/vfs/skill-vfs-base/skill.yaml |
| skill-vfs-local | 2.3.1 | 3.0.1 | skills/_drivers/vfs/skill-vfs-local/skill.yaml |
| skill-vfs-database | 2.3.1 | 3.0.1 | skills/_drivers/vfs/skill-vfs-database/skill.yaml |
| skill-vfs-minio | 2.3.1 | 3.0.1 | skills/_drivers/vfs/skill-vfs-minio/skill.yaml |
| skill-vfs-oss | 2.3.1 | 3.0.1 | skills/_drivers/vfs/skill-vfs-oss/skill.yaml |
| skill-vfs-s3 | 2.3.1 | 3.0.1 | skills/_drivers/vfs/skill-vfs-s3/skill.yaml |

### 2.3 组织驱动 (_drivers/org)

| 技能ID | 当前版本 | 目标版本 | 文件路径 |
|--------|----------|----------|----------|
| skill-org-dingding | 2.3.1 | 3.0.1 | skills/_drivers/org/skill-org-dingding/skill.yaml |
| skill-org-feishu | 2.3.1 | 3.0.1 | skills/_drivers/org/skill-org-feishu/skill.yaml |
| skill-org-wecom | 2.3.1 | 3.0.1 | skills/_drivers/org/skill-org-wecom/skill.yaml |

### 2.4 工具技能 (tools)

| 技能ID | 当前版本 | 目标版本 | 文件路径 |
|--------|----------|----------|----------|
| skill-msg-push | 2.3.1 | 3.0.1 | skills/tools/skill-msg-push/skill.yaml |
| skill-agent-cli | 2.3.1 | 3.0.1 | skills/tools/skill-agent-cli/skill.yaml |
| skill-doc-collab | 2.3.1 | 3.0.1 | skills/tools/skill-doc-collab/skill.yaml |
| skill-calendar | 2.3.1 | 3.0.1 | skills/tools/skill-calendar/skill.yaml |
| skill-todo-sync | 2.3.1 | 3.0.1 | skills/tools/skill-todo-sync/skill.yaml |

### 2.5 系统技能 (_system)

| 技能ID | 当前版本 | 目标版本 | 文件路径 |
|--------|----------|----------|----------|
| skill-common | 2.3.1 | 3.0.1 | skills/_system/skill-common/skill.yaml |
| skill-protocol | 2.3.1 | 3.0.1 | skills/_system/skill-protocol/skill.yaml |

### 2.6 LLM驱动

| 技能ID | 当前版本 | 目标版本 | 文件路径 |
|--------|----------|----------|----------|
| skill-llm-baidu | 2.3.1 | 3.0.1 | skills/_drivers/llm/skill-llm-baidu/skill.yaml |

---

## 三、Maven中央仓库检查

### 3.1 ooder相关依赖

根据Maven中央仓库搜索结果，**net.ooder** 组织的依赖**尚未发布到Maven中央仓库**。

### 3.2 需要上传到Maven中央仓库的依赖

| 依赖坐标 | 版本 | 状态 | 说明 |
|----------|------|------|------|
| net.ooder:skill-common | 3.0.1 | ❌ 未上传 | 基础依赖，被所有技能依赖 |
| net.ooder:skill-spi-llm | 3.0.1 | ❌ 未上传 | LLM SPI接口定义 |
| net.ooder:skill-llm-base | 3.0.1 | ❌ 未上传 | LLM驱动基类 |
| net.ooder:llm-sdk | 3.0.1 | ❌ 未上传 | LLM SDK |
| net.ooder:skill-protocol | 3.0.1 | ❌ 未上传 | 协议模块 |
| net.ooder:skill-management | 3.0.1 | ❌ 未上传 | 管理模块 |
| net.ooder:skill-vfs-base | 3.0.1 | ❌ 未上传 | VFS基类 |
| net.ooder:skill-org-base | 3.0.1 | ❌ 未上传 | 组织基类 |
| net.ooder.skill:skill-scenes | 3.0.1 | ❌ 未上传 | 场景管理 |
| net.ooder.skill:skill-llm-monitor | 3.0.1 | ❌ 未上传 | LLM监控 |
| net.ooder:skill-llm-config | 3.0.1 | ❌ 未上传 | LLM配置管理 |

### 3.3 上传优先级

| 优先级 | 依赖 | 原因 |
|--------|------|------|
| P0 | net.ooder:skill-common | 被所有技能依赖，必须最先上传 |
| P0 | net.ooder:skill-spi-llm | LLM SPI接口，被多个模块依赖 |
| P0 | net.ooder:skill-llm-base | LLM驱动基类，被所有LLM驱动依赖 |
| P1 | net.ooder:skill-vfs-base | VFS基类，被所有VFS驱动依赖 |
| P1 | net.ooder:skill-org-base | 组织基类，被所有组织驱动依赖 |
| P1 | net.ooder:llm-sdk | LLM SDK，被LLM驱动依赖 |
| P2 | 其他技能模块 | 可按需上传 |

---

## 四、升级执行计划

### 4.1 第一阶段：上传基础依赖到Maven中央仓库

```
1. net.ooder:skill-common@3.0.1
2. net.ooder:skill-spi-llm@3.0.1
3. net.ooder:skill-llm-base@3.0.1
4. net.ooder:skill-vfs-base@3.0.1
5. net.ooder:skill-org-base@3.0.1
```

### 4.2 第二阶段：更新所有skill.yaml版本号

批量替换：
- `version: 2.3.1` → `version: "3.0.1"`
- `version: "2.3.1"` → `version: "3.0.1"`
- `version: ">=2.3.1"` → `version: ">=3.0.1"`

### 4.3 第三阶段：更新所有pom.xml版本号

批量替换：
- `<version>2.3.1</version>` → `<version>3.0.1</version>`

### 4.4 第四阶段：验证构建

```bash
mvn clean compile -DskipTests
```

---

## 五、Maven中央仓库发布指南

### 5.1 前置条件

1. Sonatype JIRA账号 (https://issues.sonatype.org/)
2. GPG密钥用于签名
3. 项目pom.xml配置完整

### 5.2 pom.xml必要配置

```xml
<project>
    <!-- 必须配置 -->
    <groupId>net.ooder</groupId>
    <artifactId>skill-common</artifactId>
    <version>3.0.1</version>
    <packaging>jar</packaging>
    
    <!-- 项目信息 -->
    <name>skill-common</name>
    <description>Ooder Skills Common Library</description>
    <url>https://github.com/ooder/skills</url>
    
    <!-- 许可证 -->
    <licenses>
        <license>
            <name>Apache-2.0</name>
            <url>https://www.apache.org/licenses/LICENSE-2.0</url>
        </license>
    </licenses>
    
    <!-- 开发者信息 -->
    <developers>
        <developer>
            <id>ooder</id>
            <name>Ooder Team</name>
            <email>team@ooder.net</email>
        </developer>
    </developers>
    
    <!-- SCM信息 -->
    <scm>
        <connection>scm:git:git://github.com/ooder/skills.git</connection>
        <developerConnection>scm:git:ssh://github.com/ooder/skills.git</developerConnection>
        <url>https://github.com/ooder/skills</url>
    </scm>
    
    <!-- 发布配置 -->
    <distributionManagement>
        <snapshotRepository>
            <id>ossrh</id>
            <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
        </snapshotRepository>
        <repository>
            <id>ossrh</id>
            <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
        </repository>
    </distributionManagement>
</project>
```

### 5.3 发布命令

```bash
# GPG签名并发布
mvn clean deploy -P release

# 或分步执行
mvn clean package
mvn gpg:sign
mvn deploy
```

---

## 六、变更记录

| 日期 | 版本 | 变更内容 |
|------|------|----------|
| 2026-04-03 | v1.0 | 初始创建，统计需要升级的Skills和未上传依赖 |

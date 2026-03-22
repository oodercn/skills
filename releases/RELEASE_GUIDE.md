# Skills 发布指南

## 发布流程

### 1. 本地打包测试

```powershell
# 打包单个技能
.\scripts\pack-skill.ps1 -SkillId skill-network -Version 2.3.1

# 打包所有技能
.\scripts\pack-all-skills.ps1 -Version 2.3.1
```

### 2. 创建 Git 标签

```bash
# 创建带注释的标签
git tag -a v2.3.1 -m "Release v2.3.1"

# 推送标签到远程
git push origin v2.3.1
```

### 3. 自动发布

推送标签后，GitHub Actions 会自动：

1. 构建所有技能 JAR 包
2. 打包每个技能为独立 .zip 文件
3. 创建 GitHub Release
4. 同步标签到 Gitee

### 4. 手动同步到 Gitee（如果自动同步失败）

```bash
# 添加 Gitee 远程仓库
git remote add gitee https://gitee.com/ooderCN/skills.git

# 推送标签
git push gitee v2.3.1

# 在 Gitee 上创建 Release
# 访问 https://gitee.com/ooderCN/skills/releases
# 点击 "创建发行版"，上传打包好的 .zip 文件
```

## 下载链接格式

### GitHub Releases

```
https://github.com/ooderCN/skills/releases/download/v2.3.1/{skill-id}-2.3.1.zip
```

### Gitee Releases

```
https://gitee.com/ooderCN/skills/releases/download/v2.3.1/{skill-id}-2.3.1.zip
```

## skill-index 下载链接配置

```yaml
- skillId: skill-network
  name: Network Management Skill
  version: "2.3.1"
  downloadUrl: https://github.com/ooderCN/skills/releases/download/v2.3.1/skill-network-2.3.1.zip
  giteeDownloadUrl: https://gitee.com/ooderCN/skills/releases/download/v2.3.1/skill-network-2.3.1.zip
  checksum: ""
```

## 发布检查清单

- [ ] 更新所有 skill.yaml 版本号
- [ ] 更新 skill-index 版本号
- [ ] 运行 `mvn clean package -DskipTests` 确保构建成功
- [ ] 本地测试打包脚本
- [ ] 创建并推送 Git 标签
- [ ] 等待 GitHub Actions 完成
- [ ] 检查 GitHub Releases 页面
- [ ] 手动同步到 Gitee（如需要）
- [ ] 更新文档中的下载链接

## 版本命名规范

- **主版本**: v2.0.0 - 重大架构变更
- **次版本**: v2.3.0 - 新功能添加
- **修订版本**: v2.3.1 - Bug修复、文档更新

## 发布内容

每个 Release 包含：

1. **单技能包**: `{skill-id}-{version}.zip`
   - skill.yaml
   - lib/*.jar
   - README.md
   - config/ (可选)

2. **manifest.json**: 发布清单
   ```json
   {
     "version": "2.3.1",
     "createdAt": "2026-03-21T12:00:00Z",
     "totalSkills": 63
   }
   ```

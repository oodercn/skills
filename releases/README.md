# Skills Releases

This directory contains released skill packages for download.

## Directory Structure

```
releases/
├── v0.7.3/                    # Version 0.7.3
│   ├── skill-a2ui-0.7.3.jar
│   ├── skill-agent-0.7.3.jar
│   ├── skill-health-0.7.3.jar
│   └── ...
├── v0.7.2/                    # Version 0.7.2
│   └── ...
└── latest -> v0.7.3/          # Symlink to latest version
```

## Download URLs

### GitHub (Primary)
```
https://github.com/ooderCN/skills/raw/main/releases/v0.7.3/{skill-id}-{version}.jar
```

### Gitee (Mirror)
```
https://gitee.com/ooderCN/skills/raw/main/releases/v0.7.3/{skill-id}-{version}.jar
```

## Usage

Skills can be downloaded using the skill-index.yaml configuration:

```yaml
downloadUrl: https://github.com/ooderCN/skills/raw/main/releases/v0.7.3/skill-a2ui-0.7.3.jar
giteeDownloadUrl: https://gitee.com/ooderCN/skills/raw/main/releases/v0.7.3/skill-a2ui-0.7.3.jar
```

## Version History

| Version | Date | Description |
|---------|------|-------------|
| v0.7.3 | 2026-02-27 | Initial release with 42 skills |

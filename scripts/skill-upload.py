#!/usr/bin/env python3
"""
Skill Upload Tool - Upload skills to GitHub/Gitee repository

Usage:
    python skill-upload.py --skill skill-user-auth --version 0.7.1 --jar target/skill-user-auth-0.7.1.jar
    python skill-upload.py --all --version 0.7.1
    python skill-upload.py --sync-gitee

Author: ooder Team
"""

import argparse
import hashlib
import json
import os
import shutil
import subprocess
import sys
import yaml
from datetime import datetime
from pathlib import Path

SKILLS_DIR = Path(__file__).parent.parent / "skills"
TEMPLATES_DIR = Path(__file__).parent.parent / "templates"
SKILL_INDEX_FILE = Path(__file__).parent.parent / "skill-index.yaml"

GITHUB_REPO = "ooderCN/skills"
GITEE_REPO = "ooderCN/skills"


def calculate_checksum(file_path):
    """Calculate SHA256 checksum of a file"""
    sha256_hash = hashlib.sha256()
    with open(file_path, "rb") as f:
        for byte_block in iter(lambda: f.read(4096), b""):
            sha256_hash.update(byte_block)
    return sha256_hash.hexdigest()


def load_skill_yaml(skill_path):
    """Load skill.yaml file"""
    skill_yaml = skill_path / "skill.yaml"
    if skill_yaml.exists():
        with open(skill_yaml, 'r', encoding='utf-8') as f:
            return yaml.safe_load(f)
    return None


def create_skill_manifest(skill_info, jar_path, version):
    """Create skill-manifest.yaml file"""
    skill_name = skill_info['metadata']['id']
    checksum = calculate_checksum(jar_path)
    
    manifest = {
        'apiVersion': 'ooder.io/v1',
        'kind': 'SkillPackage',
        'metadata': {
            'name': skill_name,
            'version': version,
            'description': skill_info['metadata']['description'],
            'author': skill_info['metadata'].get('author', 'ooder Team'),
            'license': skill_info['metadata'].get('license', 'Apache-2.0'),
            'homepage': f'https://github.com/{GITHUB_REPO}/tree/main/skills/{skill_name}',
            'repository': f'https://github.com/{GITHUB_REPO}.git',
            'keywords': skill_info['metadata'].get('keywords', [])
        },
        'spec': {
            'type': skill_info['spec']['type'],
            'capabilities': [c['id'] for c in skill_info['spec'].get('capabilities', [])],
            'scenes': [s['name'] for s in skill_info['spec'].get('scenes', [])],
            'parameters': [],
            'execution': {
                'timeout': 30000,
                'memoryLimit': '256M',
                'cpuLimit': 1
            },
            'distribution': {
                'format': 'jar',
                'entrypoint': skill_info['spec']['runtime'].get('mainClass', ''),
                'assets': [{
                    'name': f'{skill_name}-{version}.jar',
                    'platform': 'all',
                    'url': f'https://github.com/{GITHUB_REPO}/releases/download/v{version}/{skill_name}-{version}.jar',
                    'checksum': f'sha256:{checksum}'
                }]
            },
            'compatibility': {
                'sdkVersion': '>=0.7.0',
                'javaVersion': '>=8'
            }
        }
    }
    
    config = skill_info['spec'].get('config', {})
    for param in config.get('required', []):
        manifest['spec']['parameters'].append({
            'name': param['name'],
            'type': param['type'],
            'required': True,
            'description': param.get('description', ''),
            'secret': param.get('secret', False)
        })
    for param in config.get('optional', []):
        manifest['spec']['parameters'].append({
            'name': param['name'],
            'type': param['type'],
            'required': False,
            'default': param.get('default'),
            'description': param.get('description', ''),
            'secret': param.get('secret', False)
        })
    
    return manifest


def create_readme(skill_info, version):
    """Create README.md file"""
    skill_name = skill_info['metadata']['id']
    display_name = skill_info['metadata']['name']
    description = skill_info['metadata']['description']
    
    capabilities = skill_info['spec'].get('capabilities', [])
    cap_table = "\n".join([
        f"| {c['id']} | {c['name']} | {c['description']} |"
        for c in capabilities
    ])
    
    config = skill_info['spec'].get('config', {})
    required_config = config.get('required', [])
    optional_config = config.get('optional', [])
    
    required_table = "\n".join([
        f"| {p['name']} | {p['type']} | {p.get('description', '')} |"
        for p in required_config
    ]) if required_config else "无"
    
    optional_table = "\n".join([
        f"| {p['name']} | {p['type']} | {p.get('default', '')} | {p.get('description', '')} |"
        for p in optional_config
    ]) if optional_config else "无"
    
    endpoints = skill_info['spec'].get('endpoints', [])
    endpoint_table = "\n".join([
        f"| {e['path']} | {e['method']} | {e['description']} |"
        for e in endpoints
    ]) if endpoints else "无"
    
    readme = f"""# {display_name}

## 概述

{description}

## 版本

当前版本: {version}

## 能力

| 能力ID | 名称 | 描述 |
|--------|------|------|
{cap_table}

## 配置参数

### 必需参数

| 参数名 | 类型 | 描述 |
|--------|------|------|
{required_table}

### 可选参数

| 参数名 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
{optional_table}

## API端点

| 端点 | 方法 | 描述 |
|------|------|------|
{endpoint_table}

## 使用示例

```bash
# 安装技能
ooder skill install {skill_name}

# 配置参数
ooder skill config {skill_name} --set AUTH_SECRET_KEY=your-secret-key

# 启动技能
ooder skill start {skill_name}
```

## 依赖

- SDK版本: >=0.7.0
- Java版本: >=8

## 许可证

Apache-2.0

## 作者

ooder Team
"""
    return readme


def update_skill_index(skill_info, version, checksum):
    """Update skill-index.yaml file"""
    skill_name = skill_info['metadata']['id']
    
    if SKILL_INDEX_FILE.exists():
        with open(SKILL_INDEX_FILE, 'r', encoding='utf-8') as f:
            index_data = yaml.safe_load(f)
    else:
        index_data = {
            'apiVersion': 'ooder.io/v1',
            'kind': 'SkillIndex',
            'metadata': {
                'name': 'ooder-skills',
                'version': version,
                'description': 'Ooder Skills Repository',
                'author': 'ooder Team',
                'homepage': f'https://github.com/{GITHUB_REPO}',
                'updatedAt': datetime.now().isoformat() + 'Z'
            },
            'spec': {
                'skills': [],
                'scenes': []
            }
        }
    
    skill_entry = {
        'skillId': skill_name,
        'name': skill_info['metadata']['name'],
        'version': version,
        'description': skill_info['metadata']['description'],
        'sceneId': skill_info['spec']['scenes'][0]['name'] if skill_info['spec'].get('scenes') else 'default',
        'path': f'skills/{skill_name}',
        'downloadUrl': f'https://github.com/{GITHUB_REPO}/releases/download/v{version}/{skill_name}-{version}.jar',
        'checksum': f'sha256:{checksum}'
    }
    
    existing_skills = [s for s in index_data['spec']['skills'] if s['skillId'] != skill_name]
    existing_skills.append(skill_entry)
    index_data['spec']['skills'] = existing_skills
    index_data['metadata']['updatedAt'] = datetime.now().isoformat() + 'Z'
    
    with open(SKILL_INDEX_FILE, 'w', encoding='utf-8') as f:
        yaml.dump(index_data, f, allow_unicode=True, default_flow_style=False, sort_keys=False)
    
    return index_data


def upload_skill(skill_name, version, jar_path=None):
    """Upload a single skill"""
    skill_dir = SKILLS_DIR / skill_name
    
    if not skill_dir.exists():
        print(f"Error: Skill directory not found: {skill_dir}")
        return False
    
    skill_yaml_path = skill_dir / "skill.yaml"
    if not skill_yaml_path.exists():
        print(f"Error: skill.yaml not found in {skill_dir}")
        return False
    
    skill_info = load_skill_yaml(skill_dir)
    if not skill_info:
        print(f"Error: Failed to load skill.yaml")
        return False
    
    if jar_path is None:
        jar_path = skill_dir / f"target/{skill_name}-{version}.jar"
    else:
        jar_path = Path(jar_path)
    
    if not jar_path.exists():
        print(f"Error: JAR file not found: {jar_path}")
        return False
    
    print(f"Processing skill: {skill_name}")
    print(f"  Version: {version}")
    print(f"  JAR: {jar_path}")
    
    checksum = calculate_checksum(jar_path)
    print(f"  Checksum: sha256:{checksum[:16]}...")
    
    manifest = create_skill_manifest(skill_info, jar_path, version)
    manifest_path = skill_dir / "skill-manifest.yaml"
    with open(manifest_path, 'w', encoding='utf-8') as f:
        yaml.dump(manifest, f, allow_unicode=True, default_flow_style=False, sort_keys=False)
    print(f"  Created: {manifest_path}")
    
    readme = create_readme(skill_info, version)
    readme_path = skill_dir / "README.md"
    with open(readme_path, 'w', encoding='utf-8') as f:
        f.write(readme)
    print(f"  Created: {readme_path}")
    
    releases_dir = skill_dir / "releases" / f"v{version}"
    releases_dir.mkdir(parents=True, exist_ok=True)
    target_jar = releases_dir / jar_path.name
    shutil.copy2(jar_path, target_jar)
    print(f"  Copied JAR to: {target_jar}")
    
    update_skill_index(skill_info, version, checksum)
    print(f"  Updated: {SKILL_INDEX_FILE}")
    
    return True


def upload_all_skills(version):
    """Upload all skills"""
    skills = [
        "skill-user-auth",
        "skill-org-feishu",
        "skill-org-dingding",
        "skill-a2ui",
        "skill-trae-solo"
    ]
    
    success_count = 0
    for skill_name in skills:
        skill_dir = SKILLS_DIR / skill_name
        if skill_dir.exists():
            if upload_skill(skill_name, version):
                success_count += 1
        else:
            print(f"Skipping {skill_name}: directory not found")
    
    print(f"\nUploaded {success_count}/{len(skills)} skills")
    return success_count == len(skills)


def sync_to_gitee():
    """Sync to Gitee mirror"""
    print("Syncing to Gitee...")
    
    try:
        subprocess.run(["git", "remote", "add", "gitee", f"https://gitee.com/{GITEE_REPO}.git"], check=False)
        subprocess.run(["git", "push", "gitee", "main", "--force"], check=True)
        print("Successfully synced to Gitee")
        return True
    except subprocess.CalledProcessError as e:
        print(f"Error syncing to Gitee: {e}")
        return False


def main():
    parser = argparse.ArgumentParser(description='Skill Upload Tool')
    parser.add_argument('--skill', help='Skill name to upload')
    parser.add_argument('--version', default='0.7.1', help='Skill version')
    parser.add_argument('--jar', help='Path to JAR file')
    parser.add_argument('--all', action='store_true', help='Upload all skills')
    parser.add_argument('--sync-gitee', action='store_true', help='Sync to Gitee mirror')
    
    args = parser.parse_args()
    
    if args.sync_gitee:
        sync_to_gitee()
    elif args.all:
        upload_all_skills(args.version)
    elif args.skill:
        upload_skill(args.skill, args.version, args.jar)
    else:
        parser.print_help()


if __name__ == '__main__':
    main()

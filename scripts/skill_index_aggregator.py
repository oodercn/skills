#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Skill Index 聚合工具 (Python版本)
用于测试 skill-index-entry.yaml 文件的聚合
"""

import yaml
import os
import sys
from pathlib import Path
from datetime import datetime
import hashlib

def load_yaml(path):
    """加载 YAML 文件"""
    with open(path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)

def save_yaml(path, data):
    """保存 YAML 文件"""
    with open(path, 'w', encoding='utf-8') as f:
        yaml.dump(data, f, allow_unicode=True, default_flow_style=False, sort_keys=False)

def find_skill_entries(skills_dir):
    """查找所有 skill-index-entry.yaml 文件"""
    entries = []
    skills_path = Path(skills_dir)
    
    for entry_file in skills_path.rglob('skill-index-entry.yaml'):
        try:
            entry = load_yaml(entry_file)
            if entry:
                entries.append((entry_file, entry))
        except Exception as e:
            print(f"Warning: Failed to load {entry_file}: {e}")
    
    return entries

def normalize_skill_entry(entry):
    """标准化技能条目"""
    normalized = {}
    
    # 提取 metadata
    metadata = entry.get('metadata', entry)
    
    normalized['id'] = metadata.get('id')
    normalized['name'] = metadata.get('name')
    normalized['version'] = metadata.get('version')
    normalized['description'] = metadata.get('description')
    
    # 提取 spec
    spec = entry.get('spec', entry)
    
    # SE三维分类
    normalized['skillForm'] = spec.get('skillForm')
    normalized['sceneType'] = spec.get('sceneType')
    normalized['visibility'] = spec.get('visibility')
    
    # 业务分类
    normalized['businessCategory'] = spec.get('businessCategory')
    normalized['subCategory'] = spec.get('subCategory')
    
    # 技术分类
    normalized['category'] = spec.get('category')
    normalized['capabilityCategory'] = spec.get('capabilityCategory')
    
    # 能力地址
    normalized['capabilityAddresses'] = spec.get('capabilityAddresses')
    
    # 标签
    normalized['tags'] = spec.get('tags', [])
    
    # 依赖
    normalized['dependencies'] = spec.get('dependencies', [])
    
    # 角色 (SCENE技能)
    if spec.get('roles'):
        normalized['roles'] = spec.get('roles')
    
    return normalized

def validate_entry(entry):
    """验证技能条目"""
    required_fields = ['id', 'name', 'version', 'skillForm', 'visibility']
    errors = []
    
    for field in required_fields:
        if not entry.get(field):
            errors.append(f"Missing required field: {field}")
    
    return errors

def aggregate(skills_dir, output_path, schema_path=None, addresses_path=None, categories_path=None):
    """执行聚合"""
    print("Starting Skill Index Aggregation...")
    
    # 1. 加载基础配置
    schema = None
    addresses = None
    categories = None
    
    if schema_path and os.path.exists(schema_path):
        schema = load_yaml(schema_path)
        print(f"✓ Loaded schema from {schema_path}")
    
    if addresses_path and os.path.exists(addresses_path):
        addresses = load_yaml(addresses_path)
        print(f"✓ Loaded addresses from {addresses_path}")
    
    if categories_path and os.path.exists(categories_path):
        categories = load_yaml(categories_path)
        print(f"✓ Loaded categories from {categories_path}")
    
    # 2. 加载所有技能条目
    entries = find_skill_entries(skills_dir)
    print(f"✓ Found {len(entries)} skill entries")
    
    # 3. 标准化并验证
    skills = []
    ids = set()
    errors = []
    
    for entry_file, entry in entries:
        normalized = normalize_skill_entry(entry)
        entry_errors = validate_entry(normalized)
        
        if entry_errors:
            errors.append((entry_file, entry_errors))
            continue
        
        skill_id = normalized.get('id')
        if skill_id in ids:
            errors.append((entry_file, [f"Duplicate skill ID: {skill_id}"]))
            continue
        
        ids.add(skill_id)
        skills.append(normalized)
    
    if errors:
        print("\n❌ Validation errors found:")
        for entry_file, entry_errors in errors:
            print(f"  {entry_file}:")
            for error in entry_errors:
                print(f"    - {error}")
        return False
    
    # 按 ID 排序
    skills.sort(key=lambda s: s.get('id', ''))
    
    print(f"✓ Validated {len(skills)} unique skills")
    
    # 4. 构建聚合索引
    aggregated = {
        'apiVersion': 'skill.ooder.net/v1',
        'kind': 'SkillIndex',
        'metadata': {
            'version': '2.3.1',
            'generatedAt': datetime.now().isoformat(),
            'totalSkills': len(skills),
        },
        'spec': {
            'skills': skills
        }
    }
    
    # 5. 写入输出文件
    save_yaml(output_path, aggregated)
    print(f"✓ Generated skill-index.yaml at: {output_path}")
    
    # 6. 统计信息
    print("\n📊 Statistics:")
    print(f"  Total skills: {len(skills)}")
    
    # 按 skillForm 统计
    form_counts = {}
    for skill in skills:
        form = skill.get('skillForm', 'UNKNOWN')
        form_counts[form] = form_counts.get(form, 0) + 1
    
    print("  By skillForm:")
    for form, count in sorted(form_counts.items()):
        print(f"    - {form}: {count}")
    
    # 按 category 统计
    cat_counts = {}
    for skill in skills:
        cat = skill.get('category', 'UNKNOWN')
        cat_counts[cat] = cat_counts.get(cat, 0) + 1
    
    print("  By category:")
    for cat, count in sorted(cat_counts.items()):
        print(f"    - {cat}: {count}")
    
    # 按 visibility 统计
    vis_counts = {}
    for skill in skills:
        vis = skill.get('visibility', 'UNKNOWN')
        vis_counts[vis] = vis_counts.get(vis, 0) + 1
    
    print("  By visibility:")
    for vis, count in sorted(vis_counts.items()):
        print(f"    - {vis}: {count}")
    
    return True

def main():
    skills_dir = r'E:\github\ooder-skills\skills'
    output_path = r'E:\github\ooder-skills\skills\skill-index-aggregated.yaml'
    schema_path = r'E:\github\ooder-skills\skills\config\schema.yaml'
    addresses_path = r'E:\github\ooder-skills\skills\config\addresses.yaml'
    categories_path = r'E:\github\ooder-skills\skills\config\categories.yaml'
    
    success = aggregate(skills_dir, output_path, schema_path, addresses_path, categories_path)
    
    if success:
        print("\n✅ Aggregation completed successfully!")
        sys.exit(0)
    else:
        print("\n❌ Aggregation failed!")
        sys.exit(1)

if __name__ == '__main__':
    main()

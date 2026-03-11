#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Skill Index v2.3.1 字段标准化脚本
执行: python scripts/update_skill_index_v2.3.1.py
"""

import re
import sys
from pathlib import Path

def update_skill_index(input_file, output_file):
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 1. 更新 skillId → id (在 skills 列表中的顶级定义)
    content = re.sub(r'^(\s+)- skillId:', r'\1- id:', content, flags=re.MULTILINE)
    
    # 2. 更新 category → capabilityCategory (在 skills 列表中)
    # 注意: scenes 部分的 category 保持不变
    # 只替换 skills 部分的 category
    content = re.sub(r'^(\s+)category: (llm|knowledge|org|vfs|comm|mon|sec|iot|payment|media|search|sched|sys|util|auth|net|db)$', 
                     r'\1capabilityCategory: \2', content, flags=re.MULTILINE)
    
    # 3. 更新 skillType → skillForm
    content = re.sub(r'^(\s+)skillType:', r'\1skillForm:', content, flags=re.MULTILINE)
    
    # 4. 更新 visibility 枚举值
    content = re.sub(r'visibility:\s*PUBLIC', 'visibility: public', content)
    content = re.sub(r'visibility:\s*DEVELOPER', 'visibility: developer', content)
    content = re.sub(r'visibility:\s*ADMIN', 'visibility: internal', content)
    
    # 写入输出文件
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f"更新完成: {output_file}")
    return True

def main():
    input_file = Path('E:/github/ooder-skills/skill-index.yaml')
    output_file = Path('E:/github/ooder-skills/skill-index.yaml')
    
    # 备份原文件
    backup_file = input_file.with_suffix('.yaml.bak')
    with open(input_file, 'r', encoding='utf-8') as f:
        backup_content = f.read()
    with open(backup_file, 'w', encoding='utf-8') as f:
        f.write(backup_content)
    print(f"备份文件: {backup_file}")
    
    # 执行更新
    success = update_skill_index(input_file, output_file)
    
    if success:
        print("✅ 字段标准化完成")
        print("已更新字段:")
        print("  - skillId → id")
        print("  - category → capabilityCategory (skills部分)")
        print("  - skillType → skillForm")
        print("  - visibility 枚举值转小写")
    else:
        print("❌ 更新失败")
        sys.exit(1)

if __name__ == '__main__':
    main()

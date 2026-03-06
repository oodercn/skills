#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Skills Classification Migration Script
将现有skills按照ABS/ASS/TBS/SVC分类整理

Usage:
    python scripts/skill-classification-migrate.py --dry-run
    python scripts/skill-classification-migrate.py --execute
"""

import os
import sys
import yaml
import json
import shutil
import argparse
from pathlib import Path
from datetime import datetime
from typing import Dict, List, Optional, Tuple

SKILLS_DIR = Path(__file__).parent.parent / "skills"
INDEX_FILE = Path(__file__).parent.parent / "skill-index.yaml"
CLASSIFICATION_FILE = Path(__file__).parent.parent / "skill-classification.yaml"

class SkillClassifier:
    def __init__(self):
        self.classification_config = self.load_classification_config()
        self.skills = {}
        self.classification_result = {
            "abs": [],
            "ass": [],
            "tbs": [],
            "svc": {}
        }
        
    def load_classification_config(self) -> Dict:
        if CLASSIFICATION_FILE.exists():
            with open(CLASSIFICATION_FILE, 'r', encoding='utf-8') as f:
                return yaml.safe_load(f)
        return {}
    
    def calculate_business_semantics_score(self, skill_data: Dict) -> int:
        score = 0
        
        spec = skill_data.get('spec', {})
        scene_caps = spec.get('sceneCapabilities', [])
        
        if scene_caps:
            for sc in scene_caps:
                if sc.get('driverConditions'):
                    score += 3
                if sc.get('participants'):
                    score += 3
                if sc.get('visibility') == 'public':
                    score += 2
                if sc.get('collaborativeCapabilities'):
                    score += 1
                    
        metadata = skill_data.get('metadata', {})
        labels = metadata.get('labels', {})
        if labels.get('scene', {}).get('category'):
            score += 1
            
        return min(score, 10)
    
    def detect_category(self, skill_data: Dict) -> str:
        spec = skill_data.get('spec', {})
        metadata = skill_data.get('metadata', {})
        
        has_scene_capabilities = (
            spec.get('sceneCapabilities') and 
            len(spec.get('sceneCapabilities', [])) > 0
        )
        
        if not has_scene_capabilities:
            return 'svc'
        
        main_first = False
        for sc in spec.get('sceneCapabilities', []):
            if sc.get('mainFirst', False):
                main_first = True
                break
        
        business_score = self.calculate_business_semantics_score(skill_data)
        
        if main_first:
            if business_score >= 8:
                return 'abs'
            else:
                return 'ass'
        else:
            if business_score >= 8:
                return 'tbs'
            else:
                return 'svc'
    
    def get_sub_category(self, skill_data: Dict, category: str) -> str:
        if category != 'svc':
            return None
            
        metadata = skill_data.get('metadata', {})
        skill_id = metadata.get('id', '')
        
        if 'org' in skill_id or 'user-auth' in skill_id or 'security' in skill_id or 'access' in skill_id or 'audit' in skill_id:
            return 'org'
        elif 'vfs' in skill_id:
            return 'vfs'
        elif 'mqtt' in skill_id or 'msg' in skill_id or 'im' in skill_id or 'group' in skill_id or 'email' in skill_id or 'notify' in skill_id:
            return 'msg'
        elif 'llm' in skill_id:
            return 'llm'
        elif 'knowledge' in skill_id or 'rag' in skill_id or 'vector' in skill_id or 'search' in skill_id:
            return 'knowledge'
        elif 'network' in skill_id or 'protocol' in skill_id or 'openwrt' in skill_id or 'hosting' in skill_id or 'k8s' in skill_id or 'cmd' in skill_id or 'res' in skill_id or 'remote' in skill_id:
            return 'sys'
        elif 'payment' in skill_id:
            return 'payment'
        elif 'media' in skill_id:
            return 'media'
        else:
            return 'util'
    
    def scan_skills(self):
        print("Scanning skills directory...")
        
        for skill_dir in SKILLS_DIR.iterdir():
            if not skill_dir.is_dir():
                continue
                
            skill_yaml = skill_dir / "skill.yaml"
            if not skill_yaml.exists():
                skill_yaml = skill_dir / "skill.yml"
                
            if skill_yaml.exists():
                try:
                    with open(skill_yaml, 'r', encoding='utf-8') as f:
                        skill_data = yaml.safe_load(f)
                    
                    skill_id = skill_data.get('metadata', {}).get('id', skill_dir.name)
                    category = self.detect_category(skill_data)
                    sub_category = self.get_sub_category(skill_data, category)
                    business_score = self.calculate_business_semantics_score(skill_data)
                    
                    self.skills[skill_id] = {
                        'path': str(skill_dir),
                        'data': skill_data,
                        'category': category,
                        'subCategory': sub_category,
                        'businessSemanticsScore': business_score,
                        'mainFirst': any(sc.get('mainFirst', False) for sc in skill_data.get('spec', {}).get('sceneCapabilities', []))
                    }
                    
                    if category == 'svc':
                        if sub_category not in self.classification_result['svc']:
                            self.classification_result['svc'][sub_category] = []
                        self.classification_result['svc'][sub_category].append(skill_id)
                    else:
                        self.classification_result[category].append(skill_id)
                        
                except Exception as e:
                    print(f"Error reading {skill_yaml}: {e}")
    
    def generate_report(self) -> str:
        report = []
        report.append("=" * 60)
        report.append("Skills Classification Report")
        report.append(f"Generated: {datetime.now().isoformat()}")
        report.append("=" * 60)
        report.append("")
        
        report.append("## Summary")
        report.append(f"- Total Skills: {len(self.skills)}")
        report.append(f"- ABS (自驱业务场景): {len(self.classification_result['abs'])}")
        report.append(f"- ASS (自驱系统场景): {len(self.classification_result['ass'])}")
        report.append(f"- TBS (触发业务场景): {len(self.classification_result['tbs'])}")
        report.append(f"- SVC (服务技能): {sum(len(v) for v in self.classification_result['svc'].values())}")
        report.append("")
        
        report.append("## ABS - 自驱业务场景")
        for skill_id in self.classification_result['abs']:
            skill = self.skills[skill_id]
            report.append(f"  - {skill_id}: score={skill['businessSemanticsScore']}, mainFirst={skill['mainFirst']}")
        report.append("")
        
        report.append("## ASS - 自驱系统场景")
        for skill_id in self.classification_result['ass']:
            skill = self.skills[skill_id]
            report.append(f"  - {skill_id}: score={skill['businessSemanticsScore']}, mainFirst={skill['mainFirst']}")
        report.append("")
        
        report.append("## TBS - 触发业务场景")
        for skill_id in self.classification_result['tbs']:
            skill = self.skills[skill_id]
            report.append(f"  - {skill_id}: score={skill['businessSemanticsScore']}, mainFirst={skill['mainFirst']}")
        report.append("")
        
        report.append("## SVC - 服务技能")
        for sub_cat, skill_ids in sorted(self.classification_result['svc'].items()):
            report.append(f"  ### {sub_cat} ({len(skill_ids)})")
            for skill_id in skill_ids:
                report.append(f"    - {skill_id}")
        report.append("")
        
        return "\n".join(report)
    
    def create_directory_structure(self, dry_run: bool = True):
        dirs_to_create = [
            "skills/scene-skills/abs",
            "skills/scene-skills/ass",
            "skills/scene-skills/tbs",
            "skills/service-skills/org",
            "skills/service-skills/vfs",
            "skills/service-skills/msg",
            "skills/service-skills/llm",
            "skills/service-skills/knowledge",
            "skills/service-skills/sys",
            "skills/service-skills/payment",
            "skills/service-skills/media",
            "skills/service-skills/util",
        ]
        
        base_path = SKILLS_DIR.parent
        
        for dir_path in dirs_to_create:
            full_path = base_path / dir_path
            if dry_run:
                print(f"[DRY-RUN] Would create: {full_path}")
            else:
                full_path.mkdir(parents=True, exist_ok=True)
                print(f"Created: {full_path}")
    
    def generate_migration_commands(self) -> List[str]:
        commands = []
        base_path = SKILLS_DIR.parent
        
        for skill_id, skill_info in self.skills.items():
            category = skill_info['category']
            sub_cat = skill_info['subCategory']
            src_path = Path(skill_info['path'])
            
            if category == 'svc':
                dest_path = base_path / "skills" / "service-skills" / sub_cat / src_path.name
            else:
                dest_path = base_path / "skills" / "scene-skills" / category / src_path.name
            
            commands.append(f"Move-Item -Path \"{src_path}\" -Destination \"{dest_path}\"")
        
        return commands
    
    def update_skill_yaml(self, skill_id: str, dry_run: bool = True):
        skill_info = self.skills.get(skill_id)
        if not skill_info:
            return
            
        skill_data = skill_info['data']
        category = skill_info['category']
        sub_cat = skill_info['subCategory']
        
        metadata = skill_data.setdefault('metadata', {})
        spec = skill_data.setdefault('spec', {})
        
        metadata['category'] = category
        metadata['sceneCategory'] = category if category != 'svc' else None
        metadata['subCategory'] = sub_cat
        
        spec['category'] = category
        
        classification = spec.setdefault('classification', {})
        classification['category'] = category
        classification['categoryName'] = {
            'abs': '自驱业务场景',
            'ass': '自驱系统场景',
            'tbs': '触发业务场景',
            'svc': '服务技能'
        }.get(category, '未知')
        classification['mainFirst'] = skill_info['mainFirst']
        classification['businessSemanticsScore'] = skill_info['businessSemanticsScore']
        classification['detectedAt'] = datetime.now().isoformat()
        classification['detectionVersion'] = '2.3.0'
        
        if dry_run:
            print(f"[DRY-RUN] Would update: {skill_id}")
            print(f"  category: {category}")
            print(f"  subCategory: {sub_cat}")
            print(f"  businessSemanticsScore: {skill_info['businessSemanticsScore']}")
        else:
            skill_yaml = Path(skill_info['path']) / 'skill.yaml'
            with open(skill_yaml, 'w', encoding='utf-8') as f:
                yaml.dump(skill_data, f, allow_unicode=True, default_flow_style=False)
            print(f"Updated: {skill_id}")


def main():
    parser = argparse.ArgumentParser(description='Skills Classification Migration')
    parser.add_argument('--dry-run', action='store_true', help='Dry run mode')
    parser.add_argument('--execute', action='store_true', help='Execute migration')
    parser.add_argument('--report', action='store_true', help='Generate report only')
    parser.add_argument('--output', type=str, default=None, help='Output file for report')
    args = parser.parse_args()
    
    classifier = SkillClassifier()
    classifier.scan_skills()
    
    if args.report:
        report = classifier.generate_report()
        if args.output:
            with open(args.output, 'w', encoding='utf-8') as f:
                f.write(report)
            print(f"Report saved to: {args.output}")
        else:
            print(report)
        return
    
    if args.dry_run:
        print("=== DRY RUN MODE ===")
        print()
        print(classifier.generate_report())
        print()
        print("=== Directory Structure ===")
        classifier.create_directory_structure(dry_run=True)
        print()
        print("=== Migration Commands (PowerShell) ===")
        for cmd in classifier.generate_migration_commands()[:10]:
            print(cmd)
        print(f"... and {len(classifier.generate_migration_commands()) - 10} more commands")
        
    elif args.execute:
        print("=== EXECUTING MIGRATION ===")
        classifier.create_directory_structure(dry_run=False)
        
        for skill_id in classifier.skills:
            classifier.update_skill_yaml(skill_id, dry_run=False)
        
        print("\nMigration completed!")
        print(classifier.generate_report())


if __name__ == '__main__':
    main()

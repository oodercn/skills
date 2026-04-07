#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Skills Discovery Comparison Script
Compares local skills with Gitee remote repository
"""

import os
import yaml
import json
import urllib.request
import urllib.error
from pathlib import Path
from datetime import datetime

SKILLS_DIR = Path("e:/github/ooder-skills/skills")
OUTPUT_FILE = Path("e:/github/ooder-skills/docs/SKILLS_DISCOVERY_COMPARISON.md")

GITEE_API_URL = "https://gitee.com/api/v5/repos/ooderCN/skills/contents/skills"
GITEE_TOKEN = os.environ.get("GITEE_TOKEN", "")

def get_nested_value(data, path, default=None):
    """Get nested value from dict using dot notation"""
    keys = path.split(".")
    value = data
    for key in keys:
        if isinstance(value, dict) and key in value:
            value = value[key]
        else:
            return default
    return value

def scan_local_skills():
    """Scan local skills directory for skill.yaml files"""
    skills = {}
    
    for root, dirs, files in os.walk(SKILLS_DIR):
        if "target" in root or "node_modules" in root:
            continue
        
        skill_file = None
        for filename in ["skill.yaml", "skill.yml"]:
            filepath = Path(root) / filename
            if filepath.exists():
                skill_file = filepath
                break
        
        if not skill_file:
            src_resources = Path(root) / "src" / "main" / "resources"
            for filename in ["skill.yaml", "skill.yml"]:
                filepath = src_resources / filename
                if filepath.exists():
                    skill_file = filepath
                    break
        
        if skill_file:
            try:
                with open(skill_file, 'r', encoding='utf-8') as f:
                    data = yaml.safe_load(f)
                
                if not data:
                    continue
                
                metadata = data.get("metadata", {})
                skill_id = metadata.get("id") or data.get("id") or data.get("skillId")
                
                if not skill_id:
                    skill_id = Path(root).name
                
                if skill_id and skill_id not in skills:
                    skills[skill_id] = {
                        "id": skill_id,
                        "name": metadata.get("name") or data.get("name"),
                        "version": metadata.get("version") or data.get("version"),
                        "category": metadata.get("category") or data.get("category"),
                        "description": metadata.get("description") or data.get("description"),
                        "path": str(skill_file.relative_to(SKILLS_DIR.parent)),
                        "source": "LOCAL"
                    }
            except Exception as e:
                print(f"Error parsing {skill_file}: {e}")
    
    return skills

def fetch_gitee_skills():
    """Fetch skills list from Gitee API"""
    skills = {}
    
    try:
        url = GITEE_API_URL
        if GITEE_TOKEN:
            url += f"?access_token={GITEE_TOKEN}"
        
        req = urllib.request.Request(url)
        req.add_header('User-Agent', 'ooder-skills-discovery/1.0')
        
        with urllib.request.urlopen(req, timeout=30) as response:
            data = json.loads(response.read().decode('utf-8'))
        
        for item in data:
            if item.get("type") == "dir":
                skill_name = item.get("name")
                if skill_name:
                    skills[skill_name] = {
                        "id": skill_name,
                        "name": skill_name,
                        "path": item.get("path"),
                        "source": "GITEE"
                    }
    except urllib.error.URLError as e:
        print(f"Warning: Could not fetch from Gitee: {e}")
    except Exception as e:
        print(f"Warning: Error fetching from Gitee: {e}")
    
    return skills

def compare_skills(local_skills, gitee_skills):
    """Compare local and Gitee skills"""
    local_ids = set(local_skills.keys())
    gitee_ids = set(gitee_skills.keys())
    
    only_local = local_ids - gitee_ids
    only_gitee = gitee_ids - local_ids
    both = local_ids & gitee_ids
    
    return {
        "only_local": only_local,
        "only_gitee": only_gitee,
        "both": both,
        "local_count": len(local_ids),
        "gitee_count": len(gitee_ids),
        "match_count": len(both)
    }

def generate_report(local_skills, gitee_skills, comparison):
    """Generate markdown comparison report"""
    report = []
    report.append("# Skills Discovery Comparison Report")
    report.append(f"\n**Generated**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    report.append("\n## Summary")
    report.append(f"\n| Source | Count |")
    report.append(f"|--------|-------|")
    report.append(f"| Local Skills | {comparison['local_count']} |")
    report.append(f"| Gitee Skills | {comparison['gitee_count']} |")
    report.append(f"| Matched | {comparison['match_count']} |")
    report.append(f"| Only Local | {len(comparison['only_local'])} |")
    report.append(f"| Only Gitee | {len(comparison['only_gitee'])} |")
    
    if comparison['only_local']:
        report.append("\n## Skills Only in Local (Need to Push to Gitee)")
        report.append("\nThese skills exist locally but not in Gitee repository:")
        report.append("\n| Skill ID | Name | Category | Path |")
        report.append("|----------|------|----------|------|")
        for skill_id in sorted(comparison['only_local']):
            skill = local_skills[skill_id]
            report.append(f"| {skill_id} | {skill.get('name', '-')} | {skill.get('category', '-')} | `{skill.get('path', '-')}` |")
    
    if comparison['only_gitee']:
        report.append("\n## Skills Only in Gitee (Need to Pull or Remove)")
        report.append("\nThese skills exist in Gitee but not locally:")
        report.append("\n| Skill ID | Path |")
        report.append("|----------|------|")
        for skill_id in sorted(comparison['only_gitee']):
            skill = gitee_skills[skill_id]
            report.append(f"| {skill_id} | `{skill.get('path', '-')}` |")
    
    report.append("\n## Matched Skills")
    report.append(f"\n{comparison['match_count']} skills exist in both local and Gitee.")
    
    report.append("\n## Recommendations")
    
    if comparison['only_local']:
        report.append("\n### Actions Required")
        report.append("\n1. **Push new skills to Gitee**: The following skills need to be pushed:")
        for skill_id in sorted(comparison['only_local'])[:10]:
            report.append(f"   - `{skill_id}`")
        if len(comparison['only_local']) > 10:
            report.append(f"   - ... and {len(comparison['only_local']) - 10} more")
    
    if comparison['only_gitee']:
        report.append("\n2. **Review Gitee-only skills**: These may need to be pulled or are deprecated:")
        for skill_id in sorted(comparison['only_gitee'])[:10]:
            report.append(f"   - `{skill_id}`")
        if len(comparison['only_gitee']) > 10:
            report.append(f"   - ... and {len(comparison['only_gitee']) - 10} more")
    
    if not comparison['only_local'] and not comparison['only_gitee']:
        report.append("\n✅ **All skills are synchronized!** Local and Gitee are in sync.")
    
    report.append("\n## Local Skills Detail")
    report.append("\n| Skill ID | Name | Version | Category |")
    report.append("|----------|------|---------|----------|")
    for skill_id in sorted(local_skills.keys()):
        skill = local_skills[skill_id]
        report.append(f"| {skill_id} | {skill.get('name', '-')} | {skill.get('version', '-')} | {skill.get('category', '-')} |")
    
    return "\n".join(report)

def main():
    print("Scanning local skills...")
    local_skills = scan_local_skills()
    print(f"Found {len(local_skills)} local skills")
    
    print("\nFetching Gitee skills...")
    gitee_skills = fetch_gitee_skills()
    print(f"Found {len(gitee_skills)} Gitee skills")
    
    print("\nComparing skills...")
    comparison = compare_skills(local_skills, gitee_skills)
    
    print(f"\nResults:")
    print(f"  - Local only: {len(comparison['only_local'])}")
    print(f"  - Gitee only: {len(comparison['only_gitee'])}")
    print(f"  - Matched: {comparison['match_count']}")
    
    report = generate_report(local_skills, gitee_skills, comparison)
    
    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write(report)
    
    print(f"\nReport generated: {OUTPUT_FILE}")
    
    return comparison

if __name__ == "__main__":
    main()

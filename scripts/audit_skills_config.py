#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Skills Configuration Audit Script
Analyzes all skill modules for configuration completeness
"""

import os
import yaml
import json
from pathlib import Path
from datetime import datetime

SKILLS_DIR = Path("e:/github/ooder-skills/skills")
OUTPUT_FILE = Path("e:/github/ooder-skills/docs/SKILLS_CONFIGURATION_REPORT.md")

REQUIRED_SKILL_YAML_FIELDS = [
    "apiVersion",
    "kind",
    "metadata.id",
    "metadata.name",
    "metadata.version",
    "metadata.category",
    "metadata.description",
]

RECOMMENDED_SKILL_YAML_FIELDS = [
    "metadata.author",
    "metadata.license",
    "metadata.keywords",
    "spec.type",
    "spec.capabilities",
    "spec.endpoints",
]

def get_nested_value(data, path):
    """Get nested value from dict using dot notation"""
    keys = path.split(".")
    value = data
    for key in keys:
        if isinstance(value, dict) and key in value:
            value = value[key]
        else:
            return None
    return value

def check_yaml_completeness(yaml_path):
    """Check if skill.yaml has all required fields"""
    issues = []
    try:
        with open(yaml_path, 'r', encoding='utf-8') as f:
            data = yaml.safe_load(f)
        
        if not data:
            return ["Empty YAML file"]
        
        for field in REQUIRED_SKILL_YAML_FIELDS:
            if get_nested_value(data, field) is None:
                issues.append(f"Missing required field: {field}")
        
        missing_recommended = []
        for field in RECOMMENDED_SKILL_YAML_FIELDS:
            if get_nested_value(data, field) is None:
                missing_recommended.append(field)
        
        if missing_recommended:
            issues.append(f"Missing recommended fields: {', '.join(missing_recommended)}")
            
    except Exception as e:
        issues.append(f"YAML parse error: {str(e)}")
    
    return issues

def analyze_skills():
    """Analyze all skills directories"""
    skills = []
    
    for root, dirs, files in os.walk(SKILLS_DIR):
        if "target" in root or "node_modules" in root:
            continue
            
        if "pom.xml" in files or "skill.yaml" in files:
            skill_path = Path(root)
            relative_path = skill_path.relative_to(SKILLS_DIR)
            
            if len(relative_path.parts) < 2:
                continue
            
            skill_info = {
                "path": str(relative_path),
                "name": skill_path.name,
                "has_pom": (skill_path / "pom.xml").exists(),
                "has_skill_yaml": (skill_path / "skill.yaml").exists(),
                "has_readme": (skill_path / "README.md").exists(),
                "has_application_yml": (skill_path / "src" / "main" / "resources" / "application.yml").exists(),
                "skill_yaml_issues": [],
                "category": relative_path.parts[0] if relative_path.parts else "unknown",
            }
            
            if skill_info["has_skill_yaml"]:
                skill_info["skill_yaml_issues"] = check_yaml_completeness(skill_path / "skill.yaml")
            
            skills.append(skill_info)
    
    return skills

def generate_report(skills):
    """Generate markdown report"""
    report = []
    report.append("# Skills Configuration Audit Report")
    report.append(f"\n**Generated**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    report.append(f"\n**Total Skills Analyzed**: {len(skills)}")
    
    missing_pom = [s for s in skills if not s["has_pom"]]
    missing_skill_yaml = [s for s in skills if not s["has_skill_yaml"]]
    missing_readme = [s for s in skills if not s["has_readme"]]
    yaml_issues = [s for s in skills if s["skill_yaml_issues"]]
    
    report.append("\n## Summary")
    report.append(f"\n| Check | Count | Status |")
    report.append(f"|-------|-------|--------|")
    report.append(f"| Missing pom.xml | {len(missing_pom)} | {'✅ OK' if len(missing_pom) == 0 else '⚠️ Warning'} |")
    report.append(f"| Missing skill.yaml | {len(missing_skill_yaml)} | {'✅ OK' if len(missing_skill_yaml) == 0 else '❌ Error'} |")
    report.append(f"| Missing README.md | {len(missing_readme)} | {'✅ OK' if len(missing_readme) == 0 else '⚠️ Warning'} |")
    report.append(f"| YAML Issues | {len(yaml_issues)} | {'✅ OK' if len(yaml_issues) == 0 else '⚠️ Warning'} |")
    
    if missing_pom:
        report.append("\n## Skills Missing pom.xml")
        report.append("\n| Skill | Path |")
        report.append("|-------|------|")
        for s in missing_pom:
            report.append(f"| {s['name']} | `{s['path']}` |")
    
    if missing_skill_yaml:
        report.append("\n## Skills Missing skill.yaml")
        report.append("\n| Skill | Path |")
        report.append("|-------|------|")
        for s in missing_skill_yaml:
            report.append(f"| {s['name']} | `{s['path']}` |")
    
    if missing_readme:
        report.append("\n## Skills Missing README.md")
        report.append("\n| Skill | Path |")
        report.append("|-------|------|")
        for s in missing_readme:
            report.append(f"| {s['name']} | `{s['path']}` |")
    
    if yaml_issues:
        report.append("\n## skill.yaml Issues")
        report.append("\n| Skill | Issues |")
        report.append("|-------|--------|")
        for s in yaml_issues:
            issues_str = "<br>".join(s["skill_yaml_issues"])
            report.append(f"| {s['name']} | {issues_str} |")
    
    report.append("\n## Skills by Category")
    categories = {}
    for s in skills:
        cat = s["category"]
        if cat not in categories:
            categories[cat] = []
        categories[cat].append(s)
    
    for cat, cat_skills in sorted(categories.items()):
        report.append(f"\n### {cat} ({len(cat_skills)} skills)")
        report.append("\n| Skill | pom.xml | skill.yaml | README |")
        report.append("|-------|---------|------------|--------|")
        for s in sorted(cat_skills, key=lambda x: x["name"]):
            pom_status = "✅" if s["has_pom"] else "❌"
            yaml_status = "✅" if s["has_skill_yaml"] else "❌"
            readme_status = "✅" if s["has_readme"] else "❌"
            report.append(f"| {s['name']} | {pom_status} | {yaml_status} | {readme_status} |")
    
    report.append("\n## Recommendations")
    report.append("\n1. **Add missing skill.yaml files** - Required for skill discovery and registration")
    report.append("2. **Add missing README.md files** - Important for documentation and GitHub display")
    report.append("3. **Fix YAML field issues** - Ensure all required fields are present")
    report.append("4. **Standardize configuration** - Follow the skill.yaml template structure")
    
    return "\n".join(report)

def main():
    print("Analyzing skills configuration...")
    skills = analyze_skills()
    print(f"Found {len(skills)} skill modules")
    
    report = generate_report(skills)
    
    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write(report)
    
    print(f"Report generated: {OUTPUT_FILE}")

if __name__ == "__main__":
    main()

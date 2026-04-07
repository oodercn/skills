#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os
import re

# 需要替换的模式
replacements = [
    (r'<version>3\.0\.1</version>', '<version>3.0.2</version>'),
    (r'<tag>v3\.0\.1</tag>', '<tag>v3.0.2</tag>'),
    (r'<agent-sdk\.version>3\.0\.1</agent-sdk\.version>', '<agent-sdk.version>3.0.2</agent-sdk.version>'),
    (r'<scene-engine\.version>3\.0\.1</scene-engine\.version>', '<scene-engine.version>3.0.2</scene-engine.version>'),
    (r'<ooder\.version>3\.0\.1</ooder\.version>', '<ooder.version>3.0.2</ooder.version>'),
    (r'<ooder-bpm\.version>3\.0\.1</ooder-bpm\.version>', '<ooder-bpm.version>3.0.2</ooder-bpm.version>'),
    (r'<ooder\.sdk\.version>3\.0\.1</ooder\.sdk\.version>', '<ooder.sdk.version>3.0.2</ooder.sdk.version>'),
    (r'<llm-sdk\.version>3\.0\.1</llm-sdk\.version>', '<llm-sdk.version>3.0.2</llm-sdk.version>'),
]

count = 0
for root, dirs, files in os.walk('e:/github/ooder-skills'):
    for file in files:
        if file == 'pom.xml':
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()

                new_content = content
                for pattern, replacement in replacements:
                    new_content = re.sub(pattern, replacement, new_content)

                if content != new_content:
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(new_content)
                    print(f'Updated: {filepath}')
                    count += 1
            except Exception as e:
                print(f'Error processing {filepath}: {e}')

print(f'Total files updated: {count}')

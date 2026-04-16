# -*- coding: utf-8 -*-
"""
HTML 生成脚本 - 将 Markdown 转换为 HTML 格式
"""

import re
from pathlib import Path

# 读取 Markdown 文件
md_file = Path('从零开始的 SPAC 编程构建 BPM 设计器实战 - 最终版.md')
if not md_file.exists():
    md_file = Path('从零开始的 SPAC 编程构建 BPM 设计器实战 - 最终版.md')

with open(md_file, 'r', encoding='utf-8') as f:
    md_content = f.read()

# 基本的 Markdown 到 HTML 转换函数
def md_to_html(md_text):
    # 代码块
    html = re.sub(r'```(\w*)\n(.*?)```', r'<pre><code>\2</code></pre>', md_text, flags=re.DOTALL)
    
    # 行内代码
    html = re.sub(r'`([^`]+)`', r'<code>\1</code>', html)
    
    # 标题
    html = re.sub(r'^#### (.+)$', r'<h4>\1</h4>', html, flags=re.MULTILINE)
    html = re.sub(r'^### (.+)$', r'<h3>\1</h3>', html, flags=re.MULTILINE)
    html = re.sub(r'^## (.+)$', r'<h2 id="section\1">\1</h2>', html, flags=re.MULTILINE)
    html = re.sub(r'^# (.+)$', r'<h1>\1</h1>', html, flags=re.MULTILINE)
    
    # 粗体和斜体
    html = re.sub(r'\*\*(.+?)\*\*', r'<strong>\1</strong>', html)
    html = re.sub(r'\*(.+?)\*', r'<em>\1</em>', html)
    
    # 列表
    html = re.sub(r'^- (.+)$', r'<li>\1</li>', html, flags=re.MULTILINE)
    
    # 引用
    html = re.sub(r'^> (.+)$', r'<blockquote>\1</blockquote>', html, flags=re.MULTILINE)
    
    # 段落
    lines = html.split('\n')
    paragraphs = []
    for line in lines:
        if line.strip() and not line.startswith('<'):
            paragraphs.append(f'<p>{line}</p>')
        else:
            paragraphs.append(line)
    
    html = '\n'.join(paragraphs)
    
    return html

# 转换
html_content = md_to_html(md_content)

# 输出
output_file = Path('从零开始的 SPAC 编程构建 BPM 设计器实战 - 最终版_auto.html')
with open(output_file, 'w', encoding='utf-8') as f:
    f.write(html_content)

print(f"HTML 文件已生成：{output_file}")

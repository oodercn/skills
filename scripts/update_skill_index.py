import yaml
import re

def update_skill_index():
    with open('E:/github/ooder-skills/skill-index.yaml', 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 定义字段映射规则
    business_category_map = {
        'sys': 'INFRASTRUCTURE',
        'org': 'INFRASTRUCTURE',
        'auth': 'SECURITY_AUDIT',
        'net': 'INFRASTRUCTURE',
        'vfs': 'SYSTEM_TOOLS',
        'db': 'SYSTEM_TOOLS',
        'llm': 'AI_ASSISTANT',
        'know': 'AI_ASSISTANT',
        'knowledge': 'AI_ASSISTANT',
        'payment': 'SYSTEM_TOOLS',
        'media': 'MARKETING_OPERATIONS',
        'comm': 'OFFICE_COLLABORATION',
        'mon': 'SYSTEM_MONITOR',
        'iot': 'INFRASTRUCTURE',
        'search': 'DATA_PROCESSING',
        'sched': 'INFRASTRUCTURE',
        'sec': 'SECURITY_AUDIT',
        'util': 'SYSTEM_TOOLS',
    }
    
    capability_addresses_map = {
        'sys': {'required': [{'address': '0x00', 'name': 'SYS_SYSTEM', 'description': '系统服务'}], 'optional': []},
        'org': {'required': [{'address': '0x08', 'name': 'ORG_ORGANIZATION', 'description': '组织服务'}], 'optional': []},
        'auth': {'required': [{'address': '0x10', 'name': 'AUTH_AUTHENTICATION', 'description': '认证服务'}], 'optional': []},
        'net': {'required': [{'address': '0x18', 'name': 'NET_NETWORK', 'description': '网络服务'}], 'optional': []},
        'vfs': {'required': [{'address': '0x20', 'name': 'VFS_STORAGE', 'description': '文件存储服务'}], 'optional': []},
        'db': {'required': [{'address': '0x28', 'name': 'DB_DATABASE', 'description': '数据库服务'}], 'optional': []},
        'llm': {'required': [{'address': '0x30', 'name': 'LLM_PROVIDER', 'description': 'LLM服务'}], 'optional': []},
        'know': {'required': [{'address': '0x38', 'name': 'KNOWLEDGE_BASE', 'description': '知识库服务'}], 'optional': []},
        'knowledge': {'required': [{'address': '0x38', 'name': 'KNOWLEDGE_BASE', 'description': '知识库服务'}], 'optional': []},
        'payment': {'required': [{'address': '0x40', 'name': 'PAYMENT_SERVICE', 'description': '支付服务'}], 'optional': []},
        'media': {'required': [{'address': '0x48', 'name': 'MEDIA_PUBLISHING', 'description': '媒体发布服务'}], 'optional': []},
        'comm': {'required': [{'address': '0x50', 'name': 'COMM_MESSAGING', 'description': '通讯服务'}], 'optional': []},
        'mon': {'required': [{'address': '0x58', 'name': 'MON_MONITORING', 'description': '监控服务'}], 'optional': []},
        'iot': {'required': [{'address': '0x60', 'name': 'IOT_INFRASTRUCTURE', 'description': '物联网服务'}], 'optional': []},
        'search': {'required': [{'address': '0x68', 'name': 'SEARCH_SERVICE', 'description': '搜索服务'}], 'optional': []},
        'sched': {'required': [{'address': '0x70', 'name': 'SCHED_SCHEDULER', 'description': '调度服务'}], 'optional': []},
        'sec': {'required': [{'address': '0x78', 'name': 'SEC_SECURITY', 'description': '安全服务'}], 'optional': []},
        'util': {'required': [{'address': '0xF0', 'name': 'UTIL_UTILITY', 'description': '工具服务'}], 'optional': []},
    }
    
    # 替换 skillType 为 skillForm
    content = re.sub(r'(\s+)skillType:', r'\1skillForm:', content)
    
    # 替换 visibility 值
    content = re.sub(r'visibility:\s*PUBLIC', 'visibility: public', content)
    content = re.sub(r'visibility:\s*DEVELOPER', 'visibility: developer', content)
    content = re.sub(r'visibility:\s*ADMIN', 'visibility: internal', content)
    
    # 保存更新后的内容
    with open('E:/github/ooder-skills/skill-index.yaml', 'w', encoding='utf-8') as f:
        f.write(content)
    
    print("更新完成！")

if __name__ == '__main__':
    update_skill_index()

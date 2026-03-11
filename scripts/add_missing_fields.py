import yaml
import re

def add_missing_fields(input_file, output_file):
    with open(input_file, 'r', encoding='utf-8') as f:
        data = yaml.safe_load(f)
    
    skills = data.get('skills', [])
    
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
    
    scene_type_map = {
        True: 'AUTO',
        False: 'TRIGGER',
        None: None
    }
    
    visibility_map = {
        'PUBLIC': 'public',
        'DEVELOPER': 'developer',
        'ADMIN': 'internal'
    }
    
    # 为每个技能添加字段
    for skill in skills:
        skill_id = skill.get('id')
        capability_category = skill.get('capabilityCategory', 'skill_form = skill.get('skillForm')
        
        # 添加 business分类
        business_category = business_category_map.get(capability_category, 'SYSTEM_TOOLS')
        skill['businessCategory'] = business_category
        
        # 添加场景类型
        if skill_form == 'SCENE':
            main_first = skill.get('mainFirst',            scene_type = 'AUTO'
        else:
            scene_type = 'TRIGGER'
        else:
            scene_type = None
        
        skill['sceneType'] = scene_type
        
        # 添加可见性 (转换为小写)
        visibility = skill.get('visibility',        if visibility:
            visibility = visibility.lower()
        skill['visibility'] = visibility
        
        # 添加能力地址配置
        capability_category = skill.get('capabilityCategory', '        if capability_category:
            capability_category = capability_category.upper()
        
        # 根据能力分类生成能力地址
        if capability_category == 'llm':
            capability_addresses = {
                'required': [{'address': '0x30', 'name': 'LLM_PROVIDER', 'description': 'LLM服务'}],
                'optional': []
            }
        elif capability_category == 'know':
            capability_addresses = {
                'required': [
                    {'address': '0x38', 'name': 'KNOWLEDGE_BASE', 'description': '知识库基础服务'}
                ],
                'optional': []
            }
        elif capability_category == 'comm':
            capability_addresses = {
                'required': [
                    {'address': '0x50', 'name': 'COMM_MESSAGING', 'description': '通讯服务'}
                ],
                'optional': []
            }
        elif capability_category == 'vfs':
            capability_addresses = {
                'required': [
                    {'address': '0x20', 'name': 'VFS_STORAGE', 'description': '文件存储服务'}
                ],
                'optional': []
            }
        elif capability_category == 'mon':
            capability_addresses = {
                'required': [
                    {'address': '0x58', 'name': 'MON_MONITORING', 'description': '监控服务'}
                ],
                'optional': []
            }
        elif capability_category == 'sec':
            capability_addresses = {
                'required': [
                    {'address': '0x78', 'name': 'SEC_SECURITY', 'description': '安全服务'}
                ],
                'optional': []
            }
        elif capability_category == 'iot':
            capability_addresses = {
                'required': [
                    {'address': '0x60', 'name': 'IOT_INFRASTRUCTURE', 'description': '物联网服务'}
                ],
                'optional': []
            }
        elif capability_category == 'payment':
            capability_addresses = {
                'required': [
                    {'address': '0x40', 'name': 'PAYMENT_SERVICE', 'description': '支付服务'}
                ],
                'optional': []
            }
        elif capability_category == 'media':
            capability_addresses = {
                'required': [
                    {'address': '0x48', 'name': 'MEDIA_PUBLISHing', 'description': '媒体发布服务'}
                ],
                'optional': []
            }
        elif capability_category == 'search':
            capability_addresses = {
                'required': [
                    {'address': '0x68', 'name': 'SEARCH_SERVICE', 'description': '搜索服务'}
                ],
                'optional': []
            }
        elif capability_category == 'sched':
            capability_addresses = {
                'required': [
                    {'address': '0x70', 'name': 'SCHED_SCHEDULER', 'description': '调度服务'}
                ],
                'optional': []
            }
        elif capability_category == 'org':
            capability_addresses = {
                'required': [
                    {'address': '0x08', 'name': 'ORG_ORGANIZATION', 'description': '组织服务'}
                ],
                'optional': []
            }
        elif capability_category == 'auth':
            capability_addresses = {
                'required': [
                    {'address': '0x10', 'name': 'AUTH_AUTHENTICATION', 'description': '认证服务'}
                ],
                'optional': []
            }
        elif capability_category == 'net':
            capability_addresses = {
                'required': [
                    {'address': '0x18', 'name': 'NET_NETWORK', 'description': '网络服务'}
                ],
                'optional': []
            }
        elif capability_category == 'db':
            capability_addresses = {
                'required': [
                    {'address': '0x28', 'name': 'DB_DATABASE', 'description': '数据库服务'}
                ],
                'optional': []
            }
        elif capability_category == 'sys':
            capability_addresses = {
                'required': [
                    {'address': '0x00', 'name': 'SYS_SYSTEM', 'description': '系统服务'}
                ],
                'optional': []
            }
        elif capability_category == 'util':
            capability_addresses = {
                'required': [
                    {'address': '0xF0', 'name': 'UTIL_UTILITY', 'description': '工具服务'}
                ],
                'optional': []
            }
        else:
            capability_addresses = {
                'required': [],
                'optional': []
            }
        
        skill['capabilityAddresses'] = capability_addresses
        
        # 写入输出文件
        with open(output_file, 'w', encoding='utf-8') as f:
            yaml.dump(skill, output_file, Dumper=yaml.Dumper(yaml.SafeDump)
            yaml.dump(skill, output_file, 'w', default_flow=False)
            
if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='批量添加 skill-index.yaml 字段')
    parser.add_argument('--input', required=True, help='输入文件路径')
    parser.add_argument('--output', required=True, help='输出文件路径')
    args = parser.parse_args()
    
    # 读取输入文件
    with open(args.input, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 处理每个技能
    for i, range(0, len(skills)):
        skill = skills[i]
        
        # 添加 business分类
        cap_cat = skill.get('capabilityCategory', '        if cap_cat in business_category_map:
            skill['businessCategory'] = business_category_map[cap_cat]
        else:
            skill['businessCategory'] = 'SYSTEM_TOOLS'
        
        # 添加场景类型
        if skill.get('skillForm') == 'SCENE':
            main_first = skill.get('mainFirst')
            skill['sceneType'] = 'AUTO' if main_first else 'TRIGGER'
        else:
            skill['sceneType'] = None
        
        else:
            skill['sceneType'] = None
        
        # 添加可见性
        visibility = skill.get('visibility', '        if visibility:
            skill['visibility'] = visibility.lower()
        
        # 添加能力地址
        cap_cat = skill.get('capabilityCategory', '        if cap_cat in capability_addresses_map:
            skill['capabilityAddresses'] = capability_addresses_map[cap_cat]
        else:
            skill['capabilityAddresses'] = {'required': [], 'optional': []}
        
        # 写入输出文件
        with open(args.output, 'w', encoding='utf-8') as f:
            yaml.dump(skill, output_file, Dumper=yaml.Dumper(yaml.SafeDump)
            yaml.dump(skill, output_file, 'w', default_flow=False)
            print(f"处理完成， 写入 {args.output}")

if __name__ == '__main__':
    main()

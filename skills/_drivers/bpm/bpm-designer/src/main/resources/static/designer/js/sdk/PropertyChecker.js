/**
 * 属性检查器
 * 负责检查所有属性的读写闭环，生成审计报告
 */

class PropertyChecker {
    constructor() {
        // 完整的属性定义（来自后端Schema）
        this.backendProperties = this._initBackendProperties();
        
        // 前端现有属性
        this.frontendProperties = this._initFrontendProperties();
        
        // 检查结果
        this.checkResults = {
            matched: [],        // 已匹配的属性
            mismatched: [],     // 不匹配的属性
            missing: [],        // 前端缺失的属性
            extra: [],          // 前端多余的属性
            unchecked: []       // 未检查的属性
        };
        
        // 审计日志
        this.auditLog = [];
    }

    /**
     * 初始化后端属性定义
     */
    _initBackendProperties() {
        return {
            // ProcessDef 级别
            processDef: {
                required: ['processDefId', 'name', 'activities', 'routes'],
                fields: {
                    // 基本字段
                    'processDefId': { type: 'string', required: true, category: 'basic' },
                    'name': { type: 'string', required: true, category: 'basic' },
                    'description': { type: 'string', category: 'basic' },
                    'classification': { type: 'string', enum: ['办公流程', '业务流程', '系统流程', '测试流程'], category: 'basic' },
                    'systemCode': { type: 'string', default: 'bpm', category: 'basic' },
                    'accessLevel': { type: 'string', enum: ['PUBLIC', 'PRIVATE', 'BLOCK'], category: 'basic' },
                    
                    // 版本信息 (BPD)
                    'version': { type: 'integer', category: 'version' },
                    'state': { type: 'string', enum: ['DRAFT', 'ACTIVE', 'FROZEN', 'ARCHIVED'], category: 'version' },
                    'creatorName': { type: 'string', category: 'version' },
                    'modifierId': { type: 'string', category: 'version' },
                    'modifierName': { type: 'string', category: 'version' },
                    'modifyTime': { type: 'datetime', category: 'version' },
                    'createTime': { type: 'datetime', category: 'version' },
                    'activeTime': { type: 'datetime', category: 'version' },
                    'freezeTime': { type: 'datetime', category: 'version' },
                    
                    // 时限配置
                    'limit': { type: 'integer', category: 'timing' },
                    'durationUnit': { type: 'string', enum: ['M', 'H', 'D', 'W'], category: 'timing' },
                    
                    // 开始/结束节点 (XPDL)
                    'startNode': { type: 'object', category: 'xpdl', fields: ['participantId', 'firstActivityId', 'positionCoord', 'routing'] },
                    'endNodes': { type: 'array', category: 'xpdl', itemFields: ['participantId', 'lastActivityId', 'positionCoord', 'routing'] },
                    
                    // 监听器 (XML)
                    'listeners': { type: 'array', category: 'xml', itemFields: ['id', 'name', 'event', 'realizeClass'] },
                    
                    // 权限组 (XML)
                    'rightGroups': { type: 'array', category: 'xml', itemFields: ['id', 'name', 'code', 'order', 'defaultGroup'] },
                    
                    // 活动和路由
                    'activities': { type: 'array', category: 'children' },
                    'routes': { type: 'array', category: 'children' }
                }
            },
            
            // ActivityDef 级别
            activityDef: {
                required: ['activityDefId', 'name', 'position'],
                fields: {
                    // 基本字段
                    'activityDefId': { type: 'string', required: true, category: 'basic' },
                    'name': { type: 'string', required: true, category: 'basic' },
                    'description': { type: 'string', category: 'basic' },
                    'position': { type: 'string', enum: ['START', 'END', 'NORMAL'], category: 'basic' },
                    'activityType': { type: 'string', category: 'basic' },
                    
                    // BPD属性
                    'positionCoord': { type: 'object', category: 'bpd', fields: ['x', 'y'] },
                    'participantId': { type: 'string', category: 'bpd' },
                    'implementation': { type: 'string', category: 'bpd' },
                    
                    // 时限配置
                    'limitTime': { type: 'integer', category: 'timing' },
                    'alertTime': { type: 'integer', category: 'timing' },
                    'durationUnit': { type: 'string', category: 'timing' },
                    
                    // 流程控制
                    'join': { type: 'string', enum: ['XOR', 'OR', 'AND'], category: 'flow' },
                    'split': { type: 'string', enum: ['XOR', 'OR', 'AND'], category: 'flow' },
                    
                    // 退回配置
                    'canRouteBack': { type: 'string', enum: ['YES', 'NO'], category: 'flow' },
                    'routeBackMethod': { type: 'string', enum: ['PREV', 'START', 'ANY'], category: 'flow' },
                    
                    // 特送配置
                    'canSpecialSend': { type: 'string', enum: ['YES', 'NO'], category: 'flow' },
                    'specialScope': { type: 'string', category: 'flow' },
                    
                    // RIGHT属性组
                    'RIGHT': { type: 'object', category: 'attributeGroup', groupName: 'RIGHT', fields: [
                        'performType', 'performSequence', 'specialSendScope',
                        'canInsteadSign', 'canTakeBack', 'canReSend',
                        'insteadSignSelected', 'performerSelectedId', 'readerSelectedId',
                        'movePerformerTo', 'moveSponsorTo', 'moveReaderTo',
                        'surrogateId', 'surrogateName'
                    ]},
                    
                    // FORM属性组
                    'FORM': { type: 'object', category: 'attributeGroup', groupName: 'FORM', fields: [
                        'formId', 'formName', 'formType', 'formUrl'
                    ]},
                    
                    // SERVICE属性组
                    'SERVICE': { type: 'object', category: 'attributeGroup', groupName: 'SERVICE', fields: [
                        'httpMethod', 'httpUrl', 'httpRequestType',
                        'httpResponseType', 'httpServiceParams', 'serviceSelectedId'
                    ]},
                    
                    // WORKFLOW属性组
                    'WORKFLOW': { type: 'object', category: 'attributeGroup', groupName: 'WORKFLOW', fields: [
                        'deadLineOperation', 'specialScope'
                    ]},
                    
                    // 块活动属性
                    'startOfBlock': { type: 'object', category: 'block' },
                    'endOfBlock': { type: 'object', category: 'block' },
                    'participantVisualOrder': { type: 'string', category: 'block' }
                }
            },
            
            // RouteDef 级别
            routeDef: {
                required: ['routeDefId', 'fromActivityDefId', 'toActivityDefId'],
                fields: {
                    'routeDefId': { type: 'string', required: true },
                    'name': { type: 'string' },
                    'description': { type: 'string' },
                    'fromActivityDefId': { type: 'string', required: true },
                    'toActivityDefId': { type: 'string', required: true },
                    'routeOrder': { type: 'integer' },
                    'routeDirection': { type: 'string', enum: ['FORWARD', 'BACKWARD', 'LOOP'] },
                    'routeCondition': { type: 'string' },
                    'routeConditionType': { type: 'string', enum: ['CONDITION', 'OTHERWISE', 'EXCEPTION', 'DEFAULT'] },
                    'routing': { type: 'string' }
                }
            }
        };
    }

    /**
     * 初始化前端属性定义
     */
    _initFrontendProperties() {
        return {
            processDef: [
                'processDefId', 'name', 'description', 'category', 'accessLevel',
                'version', 'status', 'createdTime', 'updatedTime',
                'activities', 'routes', 'listeners', 'formulas', 'parameters',
                'extendedAttributes', 'agentConfig', 'sceneConfig',
                'activitySets', 'subProcessRefs'
            ],
            activityDef: [
                'activityDefId', 'name', 'description', 'activityType', 'position',
                'activityCategory', 'implementation', 'performerType',
                'positionCoord', 'limit', 'alertTime', 'durationUnit', 'deadlineOperation',
                'join', 'split', 'canRouteBack', 'routeBackMethod',
                'canSpecialSend', 'specialSendScope', 'canReSend',
                'performType', 'performSequence', 'performerSelectedAtt',
                'canInsteadSign', 'canTakeBack',
                'agentConfig', 'sceneConfig', 'listeners', 'extendedAttributes'
            ],
            routeDef: [
                'routeDefId', 'id', 'name', 'description',
                'from', 'fromActivityDefId', 'to', 'toActivityDefId',
                'order', 'routeOrder', 'direction', 'routeDirection',
                'condition', 'routeCondition', 'conditionType', 'routeConditionType'
            ]
        };
    }

    /**
     * 执行完整检查
     */
    runFullCheck() {
        this._resetResults();
        this._logAudit('开始完整属性检查', 'info');
        
        // 检查 ProcessDef 级别
        this._checkProcessDefProperties();
        
        // 检查 ActivityDef 级别
        this._checkActivityDefProperties();
        
        // 检查 RouteDef 级别
        this._checkRouteDefProperties();
        
        // 生成报告
        const report = this.generateReport();
        
        this._logAudit('检查完成', 'info', { 
            matched: this.checkResults.matched.length,
            mismatched: this.checkResults.mismatched.length,
            missing: this.checkResults.missing.length,
            extra: this.checkResults.extra.length
        });
        
        return report;
    }

    /**
     * 检查 ProcessDef 属性
     */
    _checkProcessDefProperties() {
        const backendFields = Object.keys(this.backendProperties.processDef.fields);
        const frontendFields = this.frontendProperties.processDef;
        
        backendFields.forEach(field => {
            const backendDef = this.backendProperties.processDef.fields[field];
            const frontendHasField = frontendFields.includes(field);
            
            // 特殊字段单独处理
            if (backendDef.category === 'children') {
                this.checkResults.matched.push({
                    level: 'processDef',
                    field: field,
                    status: 'matched',
                    note: '子元素集合'
                });
                return;
            }
            
            if (frontendHasField) {
                this.checkResults.matched.push({
                    level: 'processDef',
                    field: field,
                    status: 'matched',
                    category: backendDef.category
                });
            } else {
                // 检查是否有映射关系
                const mappedField = this._findMappedField(field);
                if (mappedField) {
                    this.checkResults.mismatched.push({
                        level: 'processDef',
                        field: field,
                        status: 'mismatched',
                        frontendField: mappedField,
                        category: backendDef.category,
                        action: '需要映射转换'
                    });
                } else {
                    this.checkResults.missing.push({
                        level: 'processDef',
                        field: field,
                        status: 'missing',
                        category: backendDef.category,
                        priority: this._getPriority(backendDef.category),
                        action: '需要添加'
                    });
                }
            }
        });
        
        // 检查前端多余的字段
        frontendFields.forEach(field => {
            if (!backendFields.includes(field)) {
                const isMapped = this._isMappedToBackend(field);
                if (!isMapped) {
                    this.checkResults.extra.push({
                        level: 'processDef',
                        field: field,
                        status: 'extra',
                        action: '考虑放入extendedAttributes或移除'
                    });
                }
            }
        });
    }

    /**
     * 检查 ActivityDef 属性
     */
    _checkActivityDefProperties() {
        const backendFields = Object.keys(this.backendProperties.activityDef.fields);
        const frontendFields = this.frontendProperties.activityDef;
        
        backendFields.forEach(field => {
            const backendDef = this.backendProperties.activityDef.fields[field];
            const frontendHasField = frontendFields.includes(field);
            
            // 属性组特殊处理
            if (backendDef.category === 'attributeGroup') {
                this._checkAttributeGroup(field, backendDef, frontendFields);
                return;
            }
            
            if (frontendHasField) {
                // 检查数据类型是否匹配
                const typeMatch = this._checkTypeMatch(field, backendDef);
                if (typeMatch.matched) {
                    this.checkResults.matched.push({
                        level: 'activityDef',
                        field: field,
                        status: 'matched',
                        category: backendDef.category
                    });
                } else {
                    this.checkResults.mismatched.push({
                        level: 'activityDef',
                        field: field,
                        status: 'type_mismatch',
                        backendType: backendDef.type,
                        frontendType: typeMatch.frontendType,
                        category: backendDef.category,
                        action: '需要类型转换'
                    });
                }
            } else {
                const mappedField = this._findMappedField(field);
                if (mappedField) {
                    this.checkResults.mismatched.push({
                        level: 'activityDef',
                        field: field,
                        status: 'mismatched',
                        frontendField: mappedField,
                        category: backendDef.category,
                        action: '需要映射转换'
                    });
                } else {
                    this.checkResults.missing.push({
                        level: 'activityDef',
                        field: field,
                        status: 'missing',
                        category: backendDef.category,
                        priority: this._getPriority(backendDef.category),
                        action: '需要添加'
                    });
                }
            }
        });
        
        // 检查属性组内部字段
        this._checkAttributeGroupFields();
        
        // 检查前端多余字段
        frontendFields.forEach(field => {
            if (!backendFields.includes(field) && !this._isInAttributeGroup(field)) {
                const isMapped = this._isMappedToBackend(field);
                if (!isMapped) {
                    this.checkResults.extra.push({
                        level: 'activityDef',
                        field: field,
                        status: 'extra',
                        action: '考虑放入extendedAttributes或移除'
                    });
                }
            }
        });
    }

    /**
     * 检查属性组
     */
    _checkAttributeGroup(groupName, backendDef, frontendFields) {
        const groupFields = backendDef.fields || [];
        const hasGroup = frontendFields.includes(groupName);
        
        if (hasGroup) {
            this.checkResults.matched.push({
                level: 'activityDef',
                field: groupName,
                status: 'matched',
                category: 'attributeGroup',
                note: '属性组存在'
            });
        } else {
            // 检查属性组字段是否以扁平形式存在
            const flatFields = groupFields.filter(f => frontendFields.includes(f));
            if (flatFields.length > 0) {
                this.checkResults.mismatched.push({
                    level: 'activityDef',
                    field: groupName,
                    status: 'flattened',
                    flatFields: flatFields,
                    action: '需要将扁平字段组织为属性组'
                });
            } else {
                this.checkResults.missing.push({
                    level: 'activityDef',
                    field: groupName,
                    status: 'missing',
                    category: 'attributeGroup',
                    priority: 'P1',
                    action: '需要添加属性组'
                });
            }
        }
    }

    /**
     * 检查属性组内部字段
     */
    _checkAttributeGroupFields() {
        const attributeGroups = ['RIGHT', 'FORM', 'SERVICE', 'WORKFLOW'];
        
        attributeGroups.forEach(groupName => {
            const groupDef = this.backendProperties.activityDef.fields[groupName];
            if (!groupDef || !groupDef.fields) return;
            
            groupDef.fields.forEach(field => {
                const frontendHasField = this.frontendProperties.activityDef.includes(field);
                const inGroup = this._isInAttributeGroup(field);
                
                if (!frontendHasField && !inGroup) {
                    this.checkResults.missing.push({
                        level: 'activityDef',
                        field: `${groupName}.${field}`,
                        status: 'missing_in_group',
                        group: groupName,
                        priority: 'P1',
                        action: `需要添加到${groupName}属性组`
                    });
                }
            });
        });
    }

    /**
     * 检查 RouteDef 属性
     */
    _checkRouteDefProperties() {
        const backendFields = Object.keys(this.backendProperties.routeDef.fields);
        const frontendFields = this.frontendProperties.routeDef;
        
        backendFields.forEach(field => {
            const backendDef = this.backendProperties.routeDef.fields[field];
            const frontendHasField = frontendFields.includes(field);
            
            if (frontendHasField) {
                this.checkResults.matched.push({
                    level: 'routeDef',
                    field: field,
                    status: 'matched'
                });
            } else {
                // 检查简写形式
                const shortForm = this._getShortForm(field);
                if (frontendFields.includes(shortForm)) {
                    this.checkResults.mismatched.push({
                        level: 'routeDef',
                        field: field,
                        status: 'mismatched',
                        frontendField: shortForm,
                        action: '需要映射转换'
                    });
                } else {
                    this.checkResults.missing.push({
                        level: 'routeDef',
                        field: field,
                        status: 'missing',
                        priority: backendDef.required ? 'P0' : 'P2',
                        action: '需要添加'
                    });
                }
            }
        });
    }

    /**
     * 生成检查报告
     */
    generateReport() {
        const report = {
            summary: {
                total: this._getTotalFieldCount(),
                matched: this.checkResults.matched.length,
                mismatched: this.checkResults.mismatched.length,
                missing: this.checkResults.missing.length,
                extra: this.checkResults.extra.length,
                coverage: 0
            },
            details: this.checkResults,
            recommendations: this._generateRecommendations(),
            auditLog: this.auditLog
        };
        
        // 计算覆盖率
        const totalBackendFields = this._getTotalBackendFieldCount();
        report.summary.coverage = ((report.summary.matched / totalBackendFields) * 100).toFixed(2);
        
        return report;
    }

    /**
     * 生成建议
     */
    _generateRecommendations() {
        const recommendations = [];
        
        // P0 优先级建议
        const p0Missing = this.checkResults.missing.filter(m => m.priority === 'P0');
        if (p0Missing.length > 0) {
            recommendations.push({
                priority: 'P0',
                title: '紧急修复',
                description: `有 ${p0Missing.length} 个关键属性缺失，需要立即添加`,
                fields: p0Missing.map(m => m.field)
            });
        }
        
        // 属性组建议
        const attributeGroupIssues = this.checkResults.mismatched.filter(
            m => m.status === 'flattened'
        );
        if (attributeGroupIssues.length > 0) {
            recommendations.push({
                priority: 'P1',
                title: '属性组结构调整',
                description: `需要将 ${attributeGroupIssues.length} 个扁平字段组织为属性组`,
                groups: attributeGroupIssues.map(m => m.field)
            });
        }
        
        // 类型转换建议
        const typeIssues = this.checkResults.mismatched.filter(
            m => m.status === 'type_mismatch'
        );
        if (typeIssues.length > 0) {
            recommendations.push({
                priority: 'P1',
                title: '数据类型转换',
                description: `有 ${typeIssues.length} 个字段类型不匹配，需要添加转换逻辑`,
                fields: typeIssues.map(m => ({
                    field: m.field,
                    backendType: m.backendType,
                    frontendType: m.frontendType
                }))
            });
        }
        
        return recommendations;
    }

    /**
     * 导出检查报告
     */
    exportReport(format = 'json') {
        const report = this.generateReport();
        
        if (format === 'json') {
            return JSON.stringify(report, null, 2);
        } else if (format === 'markdown') {
            return this._generateMarkdownReport(report);
        } else if (format === 'html') {
            return this._generateHtmlReport(report);
        }
        
        return report;
    }

    /**
     * 生成 Markdown 报告
     */
    _generateMarkdownReport(report) {
        const lines = [];
        lines.push('# 属性检查报告');
        lines.push('');
        lines.push('## 摘要');
        lines.push(`- 总字段数: ${report.summary.total}`);
        lines.push(`- 已匹配: ${report.summary.matched}`);
        lines.push(`- 不匹配: ${report.summary.mismatched}`);
        lines.push(`- 缺失: ${report.summary.missing}`);
        lines.push(`- 多余: ${report.summary.extra}`);
        lines.push(`- 覆盖率: ${report.summary.coverage}%`);
        lines.push('');
        
        // 缺失字段
        if (report.details.missing.length > 0) {
            lines.push('## 缺失字段');
            lines.push('');
            lines.push('| 级别 | 字段 | 类别 | 优先级 | 处理建议 |');
            lines.push('|------|------|------|--------|----------|');
            report.details.missing.forEach(m => {
                lines.push(`| ${m.level} | ${m.field} | ${m.category || '-'} | ${m.priority || '-'} | ${m.action} |`);
            });
            lines.push('');
        }
        
        // 不匹配字段
        if (report.details.mismatched.length > 0) {
            lines.push('## 不匹配字段');
            lines.push('');
            lines.push('| 级别 | 后端字段 | 前端字段 | 状态 | 处理建议 |');
            lines.push('|------|----------|----------|------|----------|');
            report.details.mismatched.forEach(m => {
                lines.push(`| ${m.level} | ${m.field} | ${m.frontendField || '-'} | ${m.status} | ${m.action} |`);
            });
            lines.push('');
        }
        
        // 建议
        if (report.recommendations.length > 0) {
            lines.push('## 处理建议');
            lines.push('');
            report.recommendations.forEach((rec, index) => {
                lines.push(`${index + 1}. **${rec.title}** (优先级: ${rec.priority})`);
                lines.push(`   - ${rec.description}`);
                if (rec.fields) {
                    lines.push(`   - 涉及字段: ${rec.fields.join(', ')}`);
                }
                lines.push('');
            });
        }
        
        return lines.join('\n');
    }

    /**
     * 生成 HTML 报告
     */
    _generateHtmlReport(report) {
        // 简化版HTML报告
        return `
<!DOCTYPE html>
<html>
<head>
    <title>属性检查报告</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .summary { background: #f0f0f0; padding: 15px; border-radius: 5px; }
        .section { margin-top: 20px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background: #4CAF50; color: white; }
        .p0 { color: red; font-weight: bold; }
        .p1 { color: orange; }
        .p2 { color: gray; }
    </style>
</head>
<body>
    <h1>属性检查报告</h1>
    <div class="summary">
        <h2>摘要</h2>
        <p>总字段数: ${report.summary.total}</p>
        <p>已匹配: ${report.summary.matched}</p>
        <p>不匹配: ${report.summary.mismatched}</p>
        <p>缺失: <span class="p0">${report.summary.missing}</span></p>
        <p>多余: ${report.summary.extra}</p>
        <p>覆盖率: ${report.summary.coverage}%</p>
    </div>
    <!-- 详细内容省略，可参考Markdown版本 -->
</body>
</html>`;
    }

    // ==================== 辅助方法 ====================

    _resetResults() {
        this.checkResults = {
            matched: [],
            mismatched: [],
            missing: [],
            extra: [],
            unchecked: []
        };
        this.auditLog = [];
    }

    _logAudit(message, level, data) {
        this.auditLog.push({
            timestamp: new Date().toISOString(),
            message,
            level,
            data
        });
    }

    _findMappedField(backendField) {
        const mappings = {
            'status': 'state',
            'category': 'classification',
            'limitTime': 'limit',
            'deadLineOperation': 'deadlineOperation',
            'specialScope': 'specialSendScope'
        };
        return mappings[backendField];
    }

    _isMappedToBackend(frontendField) {
        const mappings = {
            'status': 'state',
            'category': 'classification',
            'limit': 'limitTime',
            'deadlineOperation': 'deadLineOperation',
            'specialSendScope': 'specialScope'
        };
        return mappings[frontendField];
    }

    _isInAttributeGroup(field) {
        const attributeGroups = ['RIGHT', 'FORM', 'SERVICE', 'WORKFLOW'];
        for (const group of attributeGroups) {
            const groupDef = this.backendProperties.activityDef.fields[group];
            if (groupDef && groupDef.fields && groupDef.fields.includes(field)) {
                return true;
            }
        }
        return false;
    }

    _getPriority(category) {
        const priorities = {
            'basic': 'P0',
            'xpdl': 'P0',
            'xml': 'P0',
            'bpd': 'P0',
            'attributeGroup': 'P1',
            'version': 'P2',
            'timing': 'P1',
            'flow': 'P1',
            'block': 'P2'
        };
        return priorities[category] || 'P2';
    }

    _checkTypeMatch(field, backendDef) {
        // 简化版类型检查
        const booleanFields = ['canRouteBack', 'canSpecialSend', 'canInsteadSign', 'canTakeBack', 'canReSend'];
        if (booleanFields.includes(field) && backendDef.type === 'string') {
            return { matched: false, frontendType: 'boolean' };
        }
        return { matched: true };
    }

    _getShortForm(field) {
        const shortForms = {
            'routeDefId': 'id',
            'fromActivityDefId': 'from',
            'toActivityDefId': 'to',
            'routeOrder': 'order',
            'routeDirection': 'direction',
            'routeCondition': 'condition',
            'routeConditionType': 'conditionType'
        };
        return shortForms[field];
    }

    _getTotalFieldCount() {
        return this.checkResults.matched.length + 
               this.checkResults.mismatched.length + 
               this.checkResults.missing.length;
    }

    _getTotalBackendFieldCount() {
        let count = 0;
        count += Object.keys(this.backendProperties.processDef.fields).length;
        count += Object.keys(this.backendProperties.activityDef.fields).length;
        count += Object.keys(this.backendProperties.routeDef.fields).length;
        return count;
    }
}

// 创建全局实例
window.PropertyChecker = PropertyChecker;
window.propertyChecker = new PropertyChecker();

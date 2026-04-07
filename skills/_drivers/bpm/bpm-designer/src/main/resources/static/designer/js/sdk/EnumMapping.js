/**
 * 枚举映射配置文件
 * 用于前后端枚举值的双向映射
 * 
 * 创建日期: 2026-04-07
 * 文档路径: E:\github\ooder-skills\docs\bpm-designer\activity-panel-comparison.md
 */

const EnumMapping = {
    /**
     * 流程访问级别映射
     * 需求规格: Public/Private/Block
     * 后端实现: INDEPENDENT/SUBPROCESS/BLOCK
     */
    ProcessDefAccess: {
        toBackend: {
            'Public': 'INDEPENDENT',
            'Private': 'SUBPROCESS',
            'Block': 'BLOCK'
        },
        toFrontend: {
            'INDEPENDENT': 'Public',
            'SUBPROCESS': 'Private',
            'BLOCK': 'Block'
        }
    },

    /**
     * 流程版本状态映射
     * 需求规格: UNDER_REVISION/RELEASED/UNDER_TEST
     * 后端实现: DRAFT/PUBLISHED/FROZEN
     */
    ProcessDefVersionStatus: {
        toBackend: {
            'UNDER_REVISION': 'DRAFT',
            'RELEASED': 'PUBLISHED',
            'UNDER_TEST': 'TESTING'
        },
        toFrontend: {
            'DRAFT': 'UNDER_REVISION',
            'PUBLISHED': 'RELEASED',
            'FROZEN': 'FROZEN',
            'TESTING': 'UNDER_TEST'
        }
    },

    /**
     * 等待合并类型映射
     * 需求规格: JOIN_AND/JOIN_XOR
     * 后端实现: AND/XOR
     */
    ActivityDefJoin: {
        toBackend: {
            'JOIN_AND': 'AND',
            'JOIN_XOR': 'XOR',
            'DEFAULT': 'DEFAULT'
        },
        toFrontend: {
            'AND': 'JOIN_AND',
            'XOR': 'JOIN_XOR',
            'DEFAULT': 'DEFAULT'
        }
    },

    /**
     * 并行处理类型映射
     * 需求规格: SPLIT_AND/SPLIT_XOR
     * 后端实现: AND/XOR
     */
    ActivityDefSplit: {
        toBackend: {
            'SPLIT_AND': 'AND',
            'SPLIT_XOR': 'XOR',
            'DEFAULT': 'DEFAULT'
        },
        toFrontend: {
            'AND': 'SPLIT_AND',
            'XOR': 'SPLIT_XOR',
            'DEFAULT': 'DEFAULT'
        }
    },

    /**
     * 表单标识类型映射
     * 需求规格: ProcessInst/ActivityInst/Person/ActivityInstPerson
     * 后端实现: GLOBAL/ACTIVITY/PERSON/ACTIVITY_PERSON
     */
    MarkEnum: {
        toBackend: {
            'ProcessInst': 'GLOBAL',
            'ActivityInst': 'ACTIVITY',
            'Person': 'PERSON',
            'ActivityInstPerson': 'ACTIVITY_PERSON'
        },
        toFrontend: {
            'GLOBAL': 'ProcessInst',
            'ACTIVITY': 'ActivityInst',
            'PERSON': 'Person',
            'ACTIVITY_PERSON': 'ActivityInstPerson'
        }
    },

    /**
     * 锁定策略映射
     * 需求规格: Msg/Lock/Person/Last/NO
     * 后端实现: LOCK/NO_LOCK/MSG/PERSON/LAST
     */
    LockEnum: {
        toBackend: {
            'Msg': 'MSG',
            'Lock': 'LOCK',
            'Person': 'PERSON',
            'Last': 'LAST',
            'NO': 'NO_LOCK'
        },
        toFrontend: {
            'MSG': 'Msg',
            'LOCK': 'Lock',
            'PERSON': 'Person',
            'LAST': 'Last',
            'NO_LOCK': 'NO'
        }
    },

    /**
     * 活动实现方式映射
     * 需求规格: IMPL_NO/IMPL_TOOL/IMPL_SUBFLOW/IMPL_OUTFLOW/IMPL_DEVICE/IMPL_EVENT/IMPL_SERVICE
     * XPDL实现: No/Tool/SubFlow/OutFlow/Device/Event/Service
     */
    ActivityDefImpl: {
        toBackend: {
            'IMPL_NO': 'No',
            'IMPL_TOOL': 'Tool',
            'IMPL_SUBFLOW': 'SubFlow',
            'IMPL_OUTFLOW': 'OutFlow',
            'IMPL_DEVICE': 'Device',
            'IMPL_EVENT': 'Event',
            'IMPL_SERVICE': 'Service'
        },
        toFrontend: {
            'No': 'IMPL_NO',
            'Tool': 'IMPL_TOOL',
            'SubFlow': 'IMPL_SUBFLOW',
            'OutFlow': 'IMPL_OUTFLOW',
            'Device': 'IMPL_DEVICE',
            'Event': 'IMPL_EVENT',
            'Service': 'IMPL_SERVICE'
        }
    },

    /**
     * 特送范围类型映射
     * 需求规格: DEFAULT/ALL/PERFORMERS
     * 后端实现: DEFAULT/ALL/PERFORMERS
     */
    ActivityDefSpecialSendScope: {
        toBackend: {
            'DEFAULT': 'DEFAULT',
            'ALL': 'ALL',
            'PERFORMERS': 'PERFORMERS'
        },
        toFrontend: {
            'DEFAULT': 'DEFAULT',
            'ALL': 'ALL',
            'PERFORMERS': 'PERFORMERS'
        }
    },

    /**
     * 退回路径类型映射
     * 需求规格: DEFAULT/LAST/ANY/SPECIFY
     * 后端实现: DEFAULT/LAST/ANY/SPECIFY
     */
    ActivityDefRouteBackMethod: {
        toBackend: {
            'DEFAULT': 'DEFAULT',
            'LAST': 'LAST',
            'ANY': 'ANY',
            'SPECIFY': 'SPECIFY'
        },
        toFrontend: {
            'DEFAULT': 'DEFAULT',
            'LAST': 'LAST',
            'ANY': 'ANY',
            'SPECIFY': 'SPECIFY'
        }
    },

    /**
     * 办理类型映射
     * 需求规格: SINGLE/MULTIPLE/JOINTSIGN/NEEDNOTSELECT/NOSELECT/DEFAULT
     * 后端实现: SINGLE/MULTIPLE/JOINTSIGN/NEEDNOTSELECT/NOSELECT/DEFAULT
     */
    ActivityDefPerformtype: {
        toBackend: {
            'SINGLE': 'SINGLE',
            'MULTIPLE': 'MULTIPLE',
            'JOINTSIGN': 'JOINTSIGN',
            'NEEDNOTSELECT': 'NEEDNOTSELECT',
            'NOSELECT': 'NOSELECT',
            'DEFAULT': 'DEFAULT'
        },
        toFrontend: {
            'SINGLE': 'SINGLE',
            'MULTIPLE': 'MULTIPLE',
            'JOINTSIGN': 'JOINTSIGN',
            'NEEDNOTSELECT': 'NEEDNOTSELECT',
            'NOSELECT': 'NOSELECT',
            'DEFAULT': 'DEFAULT'
        }
    },

    /**
     * 办理顺序映射
     * 需求规格: FIRST/SEQUENCE/MEANWHILE/AUTOSIGN/DEFAULT
     * 后端实现: FIRST/SEQUENCE/MEANWHILE/AUTOSIGN/DEFAULT
     */
    ActivityDefPerformSequence: {
        toBackend: {
            'FIRST': 'FIRST',
            'SEQUENCE': 'SEQUENCE',
            'MEANWHILE': 'MEANWHILE',
            'AUTOSIGN': 'AUTOSIGN',
            'DEFAULT': 'DEFAULT'
        },
        toFrontend: {
            'FIRST': 'FIRST',
            'SEQUENCE': 'SEQUENCE',
            'MEANWHILE': 'MEANWHILE',
            'AUTOSIGN': 'AUTOSIGN',
            'DEFAULT': 'DEFAULT'
        }
    },

    /**
     * 到期处理办法映射
     * 需求规格: DEFAULT/DELAY/TAKEBACK/SURROGATE
     * 后端实现: DEFAULT/DELAY/TAKEBACK/SURROGATE
     */
    ActivityDefDeadLineOperation: {
        toBackend: {
            'DEFAULT': 'DEFAULT',
            'DELAY': 'DELAY',
            'TAKEBACK': 'TAKEBACK',
            'SURROGATE': 'SURROGATE'
        },
        toFrontend: {
            'DEFAULT': 'DEFAULT',
            'DELAY': 'DELAY',
            'TAKEBACK': 'TAKEBACK',
            'SURROGATE': 'SURROGATE'
        }
    },

    /**
     * 时间单位映射
     * 需求规格: Y/M/D/H/m/s/W
     * 后端实现: Y/M/D/H/m/s/W
     */
    DurationUnit: {
        toBackend: {
            'Y': 'Y',
            'M': 'M',
            'D': 'D',
            'H': 'H',
            'm': 'm',
            's': 's',
            'W': 'W'
        },
        toFrontend: {
            'Y': 'Y',
            'M': 'M',
            'D': 'D',
            'H': 'H',
            'm': 'm',
            's': 's',
            'W': 'W'
        }
    },

    /**
     * 权限组枚举映射
     * 需求规格: PERFORMER/SPONSOR/READER/HISTORYPERFORMER/HISSPONSOR/HISTORYREADER/NORIGHT/NULL
     * 后端实现: PERFORMER/SPONSOR/READER/HISTORYPERFORMER/HISSPONSOR/HISTORYREADER/NORIGHT/NULL
     */
    RightGroupEnums: {
        toBackend: {
            'PERFORMER': 'PERFORMER',
            'SPONSOR': 'SPONSOR',
            'READER': 'READER',
            'HISTORYPERFORMER': 'HISTORYPERFORMER',
            'HISSPONSOR': 'HISSPONSOR',
            'HISTORYREADER': 'HISTORYREADER',
            'NORIGHT': 'NORIGHT',
            'NULL': 'NULL'
        },
        toFrontend: {
            'PERFORMER': 'PERFORMER',
            'SPONSOR': 'SPONSOR',
            'READER': 'READER',
            'HISTORYPERFORMER': 'HISTORYPERFORMER',
            'HISSPONSOR': 'HISSPONSOR',
            'HISTORYREADER': 'HISTORYREADER',
            'NORIGHT': 'NORIGHT',
            'NULL': 'NULL'
        }
    }
};

/**
 * 枚举映射工具类
 */
class EnumMapper {
    /**
     * 将前端枚举值转换为后端枚举值
     * @param {string} enumType - 枚举类型名称
     * @param {string} frontendValue - 前端枚举值
     * @returns {string} 后端枚举值
     */
    static toBackend(enumType, frontendValue) {
        const mapping = EnumMapping[enumType];
        if (!mapping) {
            console.warn(`未找到枚举类型映射: ${enumType}`);
            return frontendValue;
        }
        
        const backendValue = mapping.toBackend[frontendValue];
        if (!backendValue) {
            console.warn(`未找到前端枚举值映射: ${enumType}.${frontendValue}`);
            return frontendValue;
        }
        
        return backendValue;
    }

    /**
     * 将后端枚举值转换为前端枚举值
     * @param {string} enumType - 枚举类型名称
     * @param {string} backendValue - 后端枚举值
     * @returns {string} 前端枚举值
     */
    static toFrontend(enumType, backendValue) {
        const mapping = EnumMapping[enumType];
        if (!mapping) {
            console.warn(`未找到枚举类型映射: ${enumType}`);
            return backendValue;
        }
        
        const frontendValue = mapping.toFrontend[backendValue];
        if (!frontendValue) {
            console.warn(`未找到后端枚举值映射: ${enumType}.${backendValue}`);
            return backendValue;
        }
        
        return frontendValue;
    }

    /**
     * 批量转换对象中的枚举值（前端 -> 后端）
     * @param {Object} data - 要转换的数据对象
     * @param {Object} enumFields - 字段名到枚举类型的映射
     * @returns {Object} 转换后的数据对象
     */
    static convertToBackend(data, enumFields) {
        const result = { ...data };
        
        for (const [field, enumType] of Object.entries(enumFields)) {
            if (result[field] !== undefined && result[field] !== null) {
                result[field] = this.toBackend(enumType, result[field]);
            }
        }
        
        return result;
    }

    /**
     * 批量转换对象中的枚举值（后端 -> 前端）
     * @param {Object} data - 要转换的数据对象
     * @param {Object} enumFields - 字段名到枚举类型的映射
     * @returns {Object} 转换后的数据对象
     */
    static convertToFrontend(data, enumFields) {
        const result = { ...data };
        
        for (const [field, enumType] of Object.entries(enumFields)) {
            if (result[field] !== undefined && result[field] !== null) {
                result[field] = this.toFrontend(enumType, result[field]);
            }
        }
        
        return result;
    }
}

/**
 * 流程定义字段枚举映射配置
 */
const ProcessDefEnumFields = {
    accessLevel: 'ProcessDefAccess',
    publicationStatus: 'ProcessDefVersionStatus',
    mark: 'MarkEnum',
    lock: 'LockEnum'
};

/**
 * 活动定义字段枚举映射配置
 */
const ActivityDefEnumFields = {
    position: 'ActivityDefPosition',
    join: 'ActivityDefJoin',
    split: 'ActivityDefSplit'
};

// 导出
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { EnumMapping, EnumMapper, ProcessDefEnumFields, ActivityDefEnumFields };
} else {
    window.EnumMapping = EnumMapping;
    window.EnumMapper = EnumMapper;
    window.ProcessDefEnumFields = ProcessDefEnumFields;
    window.ActivityDefEnumFields = ActivityDefEnumFields;
}

INSERT INTO BPM_USER (USER_ID, USERNAME, DISPLAYNAME, EMAIL, DEPARTMENT, STATUS)
VALUES 
('user1', 'zhangsan', '张三', 'zhangsan@example.com', '研发部', 'ACTIVE'),
('user2', 'lisi', '李四', 'lisi@example.com', '产品部', 'ACTIVE'),
('user3', 'wangwu', '王五', 'wangwu@example.com', '运营部', 'ACTIVE');

INSERT INTO BPM_PROCESSDEF (PROCESSDEF_ID, DEFNAME, DESCRIPTION, CLASSIFICATION, SYSTEMCODE, ACCESSLEVEL)
VALUES ('doc-approval-process', '文档审批流程', '标准文档审批流程，包含起草、审批、归档三个环节', '办公流程', 'bpm', 'Public');

INSERT INTO BPM_PROCESSDEF_VERSION (PROCESSDEF_VERSION_ID, PROCESSDEF_ID, VERSION, PUBLICATIONSTATUS, ACTIVETIME, FREEZETIME, DESCRIPTION, CREATORID, CREATORNAME, CREATED, MODIFIERID, MODIFIERNAME, MODIFYTIME, LIMITTIME, DURATIONUNIT)
VALUES ('doc-approval-process-v1', 'doc-approval-process', 1, 'RELEASED', 1704067200000, NULL, '文档审批流程第一版', 'admin', '管理员', 1704067200000, 'admin', '管理员', 1704067200000, 0, 'D');

INSERT INTO BPM_ACTIVITYDEF (ACTIVITYDEF_ID, PROCESSDEF_ID, PROCESSDEF_VERSION_ID, DEFNAME, DESCRIPTION, POSITION, IMPLEMENTATION, EXECCLASS, LIMITTIME, ALERTTIME, DURATIONUNIT, DEADLINEOPERATION, CANROUTEBACK, ROUTEBACKMETHOD, CANSPECIALSEND, INJOIN, SPLIT)
VALUES 
('act-draft', 'doc-approval-process', 'doc-approval-process-v1', '起草', '文档起草环节，发起人填写文档内容', 'POSITION_START', 'No', NULL, 3, 1, 'D', 'DEFAULT', 'N', 'LAST', 'N', 'JOIN_AND', 'SPLIT_AND'),
('act-approve', 'doc-approval-process', 'doc-approval-process-v1', '审批', '审批环节，审批人审核文档内容', 'POSITION_NORMAL', 'No', NULL, 2, 1, 'D', 'DEFAULT', 'Y', 'LAST', 'N', 'JOIN_AND', 'SPLIT_AND'),
('act-archive', 'doc-approval-process', 'doc-approval-process-v1', '归档', '归档环节，将审批通过的文档归档保存', 'POSITION_END', 'No', NULL, 1, 0, 'D', 'DEFAULT', 'N', 'LAST', 'N', 'JOIN_AND', 'SPLIT_AND');

INSERT INTO BPM_ATTRIBUTEDEF (ATTRIBUTEDEF_ID, ACTIVITYDEF_ID, PARENT_ID, ATTRIBUTENAME, ATTRIBUTETYPE, ATTRIBUTEVALUE, INTERPRETEDVALUE)
VALUES 
('attr-draft-pos', 'act-draft', NULL, 'positionCoord', 'WORKFLOW', '{"x":100,"y":200}', NULL),
('attr-approve-pos', 'act-approve', NULL, 'positionCoord', 'WORKFLOW', '{"x":300,"y":200}', NULL),
('attr-archive-pos', 'act-archive', NULL, 'positionCoord', 'WORKFLOW', '{"x":500,"y":200}', NULL);

INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE)
VALUES 
('prop-draft-1-workflow', 'act-draft', 'WORKFLOW', NULL, NULL, 'WORKFLOW', NULL, 0, 'Y'),
('prop-draft-2-pos', 'act-draft', 'positionCoord', '{"x":100,"y":200}', NULL, 'WORKFLOW', 'prop-draft-1-workflow', 0, 'Y'),
('prop-approve-1-workflow', 'act-approve', 'WORKFLOW', NULL, NULL, 'WORKFLOW', NULL, 0, 'Y'),
('prop-approve-2-pos', 'act-approve', 'positionCoord', '{"x":300,"y":200}', NULL, 'WORKFLOW', 'prop-approve-1-workflow', 0, 'Y'),
('prop-archive-1-workflow', 'act-archive', 'WORKFLOW', NULL, NULL, 'WORKFLOW', NULL, 0, 'Y'),
('prop-archive-2-pos', 'act-archive', 'positionCoord', '{"x":500,"y":200}', NULL, 'WORKFLOW', 'prop-archive-1-workflow', 0, 'Y');

INSERT INTO BPM_ROUTEDEF (ROUTEDEF_ID, PROCESSDEF_ID, PROCESSDEF_VERSION_ID, ROUTENAME, DESCRIPTION, FROMACTIVITYDEF_ID, TOACTIVITYDEF_ID, ROUTEORDER, ROUTEDIRECTION, ROUTECONDITION, ROUTECONDITIONTYPE)
VALUES 
('route-draft-to-approve', 'doc-approval-process', 'doc-approval-process-v1', '提交审批', '起草完成后提交审批', 'act-draft', 'act-approve', 1, 'FORWARD', NULL, 'CONDITION'),
('route-approve-to-archive', 'doc-approval-process', 'doc-approval-process-v1', '审批通过', '审批通过后进入归档', 'act-approve', 'act-archive', 1, 'FORWARD', 'approved==true', 'CONDITION'),
('route-approve-back-draft', 'doc-approval-process', 'doc-approval-process-v1', '退回修改', '审批不通过退回起草', 'act-approve', 'act-draft', 2, 'BACK', 'approved==false', 'CONDITION');

INSERT INTO BPM_PROCESSDEF_PARTICIPANT (PARTICIPANT_ID, PROCESSDEF_ID, ACTIVITYDEF_ID, USER_ID, ROLE_TYPE, PARTICIPANT_TYPE)
VALUES 
('participant-draft-1', 'doc-approval-process', 'act-draft', 'user1', 'INITIATOR', 'USER'),
('participant-approve-1', 'doc-approval-process', 'act-approve', 'user2', 'APPROVER', 'USER'),
('participant-archive-1', 'doc-approval-process', 'act-archive', 'user3', 'ARCHIVER', 'USER');

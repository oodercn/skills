package net.ooder.skill.dict.service;

import net.ooder.skill.dict.dto.DictDTO;
import net.ooder.skill.dict.dto.DictItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DictService {

    private static final Logger log = LoggerFactory.getLogger(DictService.class);

    private final Map<String, DictDTO> dictCache = new HashMap<>();

    public DictService() {
        initDefaultDicts();
    }

    private void initDefaultDicts() {
        DictDTO statusDict = new DictDTO();
        statusDict.setCode("status");
        statusDict.setName("状态字典");
        statusDict.setDescription("通用状态字典");
        List<DictItemDTO> statusItems = new ArrayList<>();
        
        DictItemDTO item1 = new DictItemDTO();
        item1.setCode("active");
        item1.setName("启用");
        item1.setValue("1");
        item1.setSort(1);
        item1.setEnabled(true);
        statusItems.add(item1);
        
        DictItemDTO item2 = new DictItemDTO();
        item2.setCode("inactive");
        item2.setName("禁用");
        item2.setValue("0");
        item2.setSort(2);
        item2.setEnabled(true);
        statusItems.add(item2);
        
        statusDict.setItems(statusItems);
        dictCache.put("status", statusDict);

        DictDTO priorityDict = new DictDTO();
        priorityDict.setCode("priority");
        priorityDict.setName("优先级字典");
        priorityDict.setDescription("任务优先级字典");
        List<DictItemDTO> priorityItems = new ArrayList<>();
        
        DictItemDTO p1 = new DictItemDTO();
        p1.setCode("high");
        p1.setName("高");
        p1.setValue("3");
        p1.setSort(1);
        p1.setEnabled(true);
        priorityItems.add(p1);
        
        DictItemDTO p2 = new DictItemDTO();
        p2.setCode("medium");
        p2.setName("中");
        p2.setValue("2");
        p2.setSort(2);
        p2.setEnabled(true);
        priorityItems.add(p2);
        
        DictItemDTO p3 = new DictItemDTO();
        p3.setCode("low");
        p3.setName("低");
        p3.setValue("1");
        p3.setSort(3);
        p3.setEnabled(true);
        priorityItems.add(p3);
        
        priorityDict.setItems(priorityItems);
        dictCache.put("priority", priorityDict);

        DictDTO skillTypeDict = new DictDTO();
        skillTypeDict.setCode("skill_type");
        skillTypeDict.setName("技能类型字典");
        skillTypeDict.setDescription("技能分类字典");
        List<DictItemDTO> skillTypeItems = new ArrayList<>();
        
        DictItemDTO st1 = new DictItemDTO();
        st1.setCode("system");
        st1.setName("系统技能");
        st1.setValue("system");
        st1.setSort(1);
        st1.setEnabled(true);
        skillTypeItems.add(st1);
        
        DictItemDTO st2 = new DictItemDTO();
        st2.setCode("driver");
        st2.setName("驱动技能");
        st2.setValue("driver");
        st2.setSort(2);
        st2.setEnabled(true);
        skillTypeItems.add(st2);
        
        DictItemDTO st3 = new DictItemDTO();
        st3.setCode("capability");
        st3.setName("能力技能");
        st3.setValue("capability");
        st3.setSort(3);
        st3.setEnabled(true);
        skillTypeItems.add(st3);
        
        DictItemDTO st4 = new DictItemDTO();
        st4.setCode("scene");
        st4.setName("场景技能");
        st4.setValue("scene");
        st4.setSort(4);
        st4.setEnabled(true);
        skillTypeItems.add(st4);
        
        skillTypeDict.setItems(skillTypeItems);
        dictCache.put("skill_type", skillTypeDict);

        // 能力类型字典
        DictDTO capabilityTypeDict = new DictDTO();
        capabilityTypeDict.setCode("capability_type");
        capabilityTypeDict.setName("能力类型字典");
        capabilityTypeDict.setDescription("能力分类字典");
        List<DictItemDTO> capabilityTypeItems = new ArrayList<>();
        capabilityTypeItems.add(createDictItem("api", "API能力", "api", 1));
        capabilityTypeItems.add(createDictItem("service", "服务能力", "service", 2));
        capabilityTypeItems.add(createDictItem("data", "数据能力", "data", 3));
        capabilityTypeDict.setItems(capabilityTypeItems);
        dictCache.put("capability_type", capabilityTypeDict);

        // 参与者类型字典
        DictDTO participantTypeDict = new DictDTO();
        participantTypeDict.setCode("participant_type");
        participantTypeDict.setName("参与者类型字典");
        participantTypeDict.setDescription("场景参与者类型字典");
        List<DictItemDTO> participantTypeItems = new ArrayList<>();
        participantTypeItems.add(createDictItem("user", "用户", "user", 1));
        participantTypeItems.add(createDictItem("agent", "Agent", "agent", 2));
        participantTypeItems.add(createDictItem("llm", "LLM", "llm", 3));
        participantTypeItems.add(createDictItem("system", "系统", "system", 4));
        participantTypeDict.setItems(participantTypeItems);
        dictCache.put("participant_type", participantTypeDict);

        // 参与者角色字典
        DictDTO participantRoleDict = new DictDTO();
        participantRoleDict.setCode("participant_role");
        participantRoleDict.setName("参与者角色字典");
        participantRoleDict.setDescription("场景参与者角色字典");
        List<DictItemDTO> participantRoleItems = new ArrayList<>();
        participantRoleItems.add(createDictItem("manager", "管理者", "manager", 1));
        participantRoleItems.add(createDictItem("employee", "员工", "employee", 2));
        participantRoleItems.add(createDictItem("hr", "HR", "hr", 3));
        participantRoleItems.add(createDictItem("llm-assistant", "LLM助手", "llm-assistant", 4));
        participantRoleItems.add(createDictItem("coordinator", "协调Agent", "coordinator", 5));
        participantRoleItems.add(createDictItem("super-agent", "超级Agent", "super-agent", 6));
        participantRoleDict.setItems(participantRoleItems);
        dictCache.put("participant_role", participantRoleDict);

        // 参与者状态字典
        DictDTO participantStatusDict = new DictDTO();
        participantStatusDict.setCode("participant_status");
        participantStatusDict.setName("参与者状态字典");
        participantStatusDict.setDescription("场景参与者状态字典");
        List<DictItemDTO> participantStatusItems = new ArrayList<>();
        participantStatusItems.add(createDictItem("pending", "待接受", "pending", 1));
        participantStatusItems.add(createDictItem("active", "已激活", "active", 2));
        participantStatusItems.add(createDictItem("inactive", "已停用", "inactive", 3));
        participantStatusItems.add(createDictItem("rejected", "已拒绝", "rejected", 4));
        participantStatusItems.add(createDictItem("removed", "已移除", "removed", 5));
        participantStatusDict.setItems(participantStatusItems);
        dictCache.put("participant_status", participantStatusDict);

        // 场景组状态字典
        DictDTO sceneGroupStatusDict = new DictDTO();
        sceneGroupStatusDict.setCode("scene_group_status");
        sceneGroupStatusDict.setName("场景组状态字典");
        sceneGroupStatusDict.setDescription("场景组状态字典");
        List<DictItemDTO> sceneGroupStatusItems = new ArrayList<>();
        sceneGroupStatusItems.add(createDictItem("draft", "草稿", "draft", 1));
        sceneGroupStatusItems.add(createDictItem("active", "运行中", "active", 2));
        sceneGroupStatusItems.add(createDictItem("paused", "已暂停", "paused", 3));
        sceneGroupStatusItems.add(createDictItem("completed", "已完成", "completed", 4));
        sceneGroupStatusItems.add(createDictItem("archived", "已归档", "archived", 5));
        sceneGroupStatusDict.setItems(sceneGroupStatusItems);
        dictCache.put("scene_group_status", sceneGroupStatusDict);

        // 场景类型字典
        DictDTO sceneTypeDict = new DictDTO();
        sceneTypeDict.setCode("scene_type");
        sceneTypeDict.setName("场景类型字典");
        sceneTypeDict.setDescription("场景类型字典");
        List<DictItemDTO> sceneTypeItems = new ArrayList<>();
        sceneTypeItems.add(createDictItem("chat", "对话场景", "chat", 1));
        sceneTypeItems.add(createDictItem("workflow", "工作流场景", "workflow", 2));
        sceneTypeItems.add(createDictItem("approval", "审批场景", "approval", 3));
        sceneTypeItems.add(createDictItem("notification", "通知场景", "notification", 4));
        sceneTypeDict.setItems(sceneTypeItems);
        dictCache.put("scene_type", sceneTypeDict);

        // 连接器类型字典
        DictDTO connectorTypeDict = new DictDTO();
        connectorTypeDict.setCode("connector_type");
        connectorTypeDict.setName("连接器类型字典");
        connectorTypeDict.setDescription("连接器类型字典");
        List<DictItemDTO> connectorTypeItems = new ArrayList<>();
        connectorTypeItems.add(createDictItem("http", "HTTP", "http", 1));
        connectorTypeItems.add(createDictItem("websocket", "WebSocket", "websocket", 2));
        connectorTypeItems.add(createDictItem("grpc", "gRPC", "grpc", 3));
        connectorTypeItems.add(createDictItem("database", "数据库", "database", 4));
        connectorTypeItems.add(createDictItem("message_queue", "消息队列", "message_queue", 5));
        connectorTypeDict.setItems(connectorTypeItems);
        dictCache.put("connector_type", connectorTypeDict);

        // 能力提供者类型字典
        DictDTO capabilityProviderTypeDict = new DictDTO();
        capabilityProviderTypeDict.setCode("capability_provider_type");
        capabilityProviderTypeDict.setName("能力提供者类型字典");
        capabilityProviderTypeDict.setDescription("能力提供者类型字典");
        List<DictItemDTO> capabilityProviderTypeItems = new ArrayList<>();
        capabilityProviderTypeItems.add(createDictItem("skill", "技能", "skill", 1));
        capabilityProviderTypeItems.add(createDictItem("external", "外部服务", "external", 2));
        capabilityProviderTypeItems.add(createDictItem("system", "系统", "system", 3));
        capabilityProviderTypeDict.setItems(capabilityProviderTypeItems);
        dictCache.put("capability_provider_type", capabilityProviderTypeDict);

        // 能力绑定状态字典
        DictDTO capabilityBindingStatusDict = new DictDTO();
        capabilityBindingStatusDict.setCode("capability_binding_status");
        capabilityBindingStatusDict.setName("能力绑定状态字典");
        capabilityBindingStatusDict.setDescription("能力绑定状态字典");
        List<DictItemDTO> capabilityBindingStatusItems = new ArrayList<>();
        capabilityBindingStatusItems.add(createDictItem("pending", "待激活", "pending", 1));
        capabilityBindingStatusItems.add(createDictItem("active", "已激活", "active", 2));
        capabilityBindingStatusItems.add(createDictItem("inactive", "已停用", "inactive", 3));
        capabilityBindingStatusItems.add(createDictItem("expired", "已过期", "expired", 4));
        capabilityBindingStatusDict.setItems(capabilityBindingStatusItems);
        dictCache.put("capability_binding_status", capabilityBindingStatusDict);

        // 模板状态字典
        DictDTO templateStatusDict = new DictDTO();
        templateStatusDict.setCode("template_status");
        templateStatusDict.setName("模板状态字典");
        templateStatusDict.setDescription("模板状态字典");
        List<DictItemDTO> templateStatusItems = new ArrayList<>();
        templateStatusItems.add(createDictItem("draft", "草稿", "draft", 1));
        templateStatusItems.add(createDictItem("published", "已发布", "published", 2));
        templateStatusItems.add(createDictItem("deprecated", "已弃用", "deprecated", 3));
        templateStatusDict.setItems(templateStatusItems);
        dictCache.put("template_status", templateStatusDict);

        // 模板分类字典
        DictDTO templateCategoryDict = new DictDTO();
        templateCategoryDict.setCode("template_category");
        templateCategoryDict.setName("模板分类字典");
        templateCategoryDict.setDescription("模板分类字典");
        List<DictItemDTO> templateCategoryItems = new ArrayList<>();
        templateCategoryItems.add(createDictItem("hr", "人力资源", "hr", 1));
        templateCategoryItems.add(createDictItem("finance", "财务", "finance", 2));
        templateCategoryItems.add(createDictItem("it", "IT", "it", 3));
        templateCategoryItems.add(createDictItem("sales", "销售", "sales", 4));
        templateCategoryItems.add(createDictItem("general", "通用", "general", 5));
        templateCategoryDict.setItems(templateCategoryItems);
        dictCache.put("template_category", templateCategoryDict);

        // 密钥类型字典
        DictDTO keyTypeDict = new DictDTO();
        keyTypeDict.setCode("key_type");
        keyTypeDict.setName("密钥类型字典");
        keyTypeDict.setDescription("密钥类型字典");
        List<DictItemDTO> keyTypeItems = new ArrayList<>();
        keyTypeItems.add(createDictItem("api_key", "API密钥", "api_key", 1));
        keyTypeItems.add(createDictItem("secret", "密钥", "secret", 2));
        keyTypeItems.add(createDictItem("certificate", "证书", "certificate", 3));
        keyTypeDict.setItems(keyTypeItems);
        dictCache.put("key_type", keyTypeDict);

        // 密钥状态字典
        DictDTO keyStatusDict = new DictDTO();
        keyStatusDict.setCode("key_status");
        keyStatusDict.setName("密钥状态字典");
        keyStatusDict.setDescription("密钥状态字典");
        List<DictItemDTO> keyStatusItems = new ArrayList<>();
        keyStatusItems.add(createDictItem("active", "有效", "active", 1));
        keyStatusItems.add(createDictItem("expired", "已过期", "expired", 2));
        keyStatusItems.add(createDictItem("revoked", "已吊销", "revoked", 3));
        keyStatusDict.setItems(keyStatusItems);
        dictCache.put("key_status", keyStatusDict);

        // 审计事件类型字典
        DictDTO auditEventTypeDict = new DictDTO();
        auditEventTypeDict.setCode("audit_event_type");
        auditEventTypeDict.setName("审计事件类型字典");
        auditEventTypeDict.setDescription("审计事件类型字典");
        List<DictItemDTO> auditEventTypeItems = new ArrayList<>();
        auditEventTypeItems.add(createDictItem("login", "登录", "login", 1));
        auditEventTypeItems.add(createDictItem("logout", "登出", "logout", 2));
        auditEventTypeItems.add(createDictItem("create", "创建", "create", 3));
        auditEventTypeItems.add(createDictItem("update", "更新", "update", 4));
        auditEventTypeItems.add(createDictItem("delete", "删除", "delete", 5));
        auditEventTypeItems.add(createDictItem("access", "访问", "access", 6));
        auditEventTypeDict.setItems(auditEventTypeItems);
        dictCache.put("audit_event_type", auditEventTypeDict);

        // 审计结果类型字典
        DictDTO auditResultTypeDict = new DictDTO();
        auditResultTypeDict.setCode("audit_result_type");
        auditResultTypeDict.setName("审计结果类型字典");
        auditResultTypeDict.setDescription("审计结果类型字典");
        List<DictItemDTO> auditResultTypeItems = new ArrayList<>();
        auditResultTypeItems.add(createDictItem("success", "成功", "success", 1));
        auditResultTypeItems.add(createDictItem("failure", "失败", "failure", 2));
        auditResultTypeItems.add(createDictItem("denied", "拒绝", "denied", 3));
        auditResultTypeDict.setItems(auditResultTypeItems);
        dictCache.put("audit_result_type", auditResultTypeDict);

        log.info("DictService initialized with {} dicts", dictCache.size());
    }

    private DictItemDTO createDictItem(String code, String name, String value, int sort) {
        DictItemDTO item = new DictItemDTO();
        item.setCode(code);
        item.setName(name);
        item.setValue(value);
        item.setSort(sort);
        item.setEnabled(true);
        return item;
    }

    public List<DictDTO> getAllDicts() {
        return new ArrayList<>(dictCache.values());
    }

    public DictDTO getDict(String code) {
        return dictCache.get(code);
    }

    public List<DictItemDTO> getDictItems(String code) {
        DictDTO dict = dictCache.get(code);
        return dict != null ? dict.getItems() : new ArrayList<>();
    }

    public DictItemDTO getDictItem(String code, String itemCode) {
        DictDTO dict = dictCache.get(code);
        if (dict != null && dict.getItems() != null) {
            return dict.getItems().stream()
                .filter(item -> itemCode.equals(item.getCode()))
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    public String getDictItemName(String code, String itemCode) {
        DictItemDTO item = getDictItem(code, itemCode);
        return item != null ? item.getName() : itemCode;
    }

    public void refreshCache() {
        dictCache.clear();
        initDefaultDicts();
        log.info("Dict cache refreshed");
    }
}

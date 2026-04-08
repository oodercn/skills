package net.ooder.bpm.designer.function.tools;

import net.ooder.bpm.designer.function.DesignerFunctionDefinition;
import net.ooder.bpm.designer.function.DesignerFunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FormFunctionTools {
    
    private static final Logger log = LoggerFactory.getLogger(FormFunctionTools.class);
    
    @Autowired
    private DesignerFunctionRegistry functionRegistry;
    
    @PostConstruct
    public void init() {
        registerFunctions();
    }
    
    private void registerFunctions() {
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("list_forms")
            .description("列出所有可用表单")
            .category(DesignerFunctionDefinition.FunctionCategory.FORM)
            .addParameter("category", "string", "表单分类过滤", false)
            .addParameter("status", "string", "表单状态：PUBLISHED, DRAFT", false)
            .handler(this::handleListForms)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("search_forms")
            .description("语义搜索表单，根据关键词匹配表单")
            .category(DesignerFunctionDefinition.FunctionCategory.FORM)
            .addParameter("query", "string", "搜索关键词", true)
            .addParameter("category", "string", "限定表单分类", false)
            .addParameter("limit", "integer", "返回数量限制，默认10", false)
            .handler(this::handleSearchForms)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_form_schema")
            .description("获取表单Schema定义")
            .category(DesignerFunctionDefinition.FunctionCategory.FORM)
            .addParameter("formId", "string", "表单ID", true)
            .handler(this::handleGetFormSchema)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("match_form_by_activity")
            .description("根据活动描述匹配最合适的表单")
            .category(DesignerFunctionDefinition.FunctionCategory.FORM)
            .addParameter("activityDesc", "string", "活动描述", true)
            .addParameter("activityType", "string", "活动类型", false)
            .addParameter("requiredFields", "array", "需要的字段列表", false)
            .handler(this::handleMatchFormByActivity)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("generate_form_schema")
            .description("根据活动描述生成表单Schema")
            .category(DesignerFunctionDefinition.FunctionCategory.FORM)
            .addParameter("activityDesc", "string", "活动描述", true)
            .addParameter("formName", "string", "表单名称", false)
            .addParameter("category", "string", "表单分类", false)
            .handler(this::handleGenerateFormSchema)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_form_field_mappings")
            .description("获取表单字段与活动需求的映射关系")
            .category(DesignerFunctionDefinition.FunctionCategory.FORM)
            .addParameter("formId", "string", "表单ID", true)
            .addParameter("requiredFields", "array", "需要的字段列表", true)
            .handler(this::handleGetFormFieldMappings)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("list_form_categories")
            .description("列出所有表单分类")
            .category(DesignerFunctionDefinition.FunctionCategory.FORM)
            .handler(this::handleListFormCategories)
            .build());
        
        log.info("Registered {} form functions", 7);
    }
    
    private Object handleListForms(Map<String, Object> args) {
        String category = (String) args.get("category");
        String status = (String) args.get("status");
        
        List<Map<String, Object>> forms = buildMockForms(category, status);
        
        return Map.of(
            "success", true,
            "data", forms,
            "count", forms.size()
        );
    }
    
    private Object handleSearchForms(Map<String, Object> args) {
        String query = (String) args.get("query");
        String category = (String) args.get("category");
        Integer limit = args.get("limit") != null ? ((Number) args.get("limit")).intValue() : 10;
        
        List<Map<String, Object>> forms = buildMockSearchForms(query, category, limit);
        
        return Map.of(
            "success", true,
            "data", forms,
            "query", query,
            "count", forms.size()
        );
    }
    
    private Object handleGetFormSchema(Map<String, Object> args) {
        String formId = (String) args.get("formId");
        
        Map<String, Object> schema = buildMockFormSchema(formId);
        
        return Map.of(
            "success", true,
            "data", schema
        );
    }
    
    private Object handleMatchFormByActivity(Map<String, Object> args) {
        String activityDesc = (String) args.get("activityDesc");
        String activityType = (String) args.get("activityType");
        @SuppressWarnings("unchecked")
        List<String> requiredFields = (List<String>) args.get("requiredFields");
        
        List<Map<String, Object>> matches = buildMockFormMatches(activityDesc, requiredFields);
        
        return Map.of(
            "success", true,
            "data", matches,
            "activityDesc", activityDesc,
            "count", matches.size()
        );
    }
    
    private Object handleGenerateFormSchema(Map<String, Object> args) {
        String activityDesc = (String) args.get("activityDesc");
        String formName = (String) args.get("formName");
        String category = (String) args.get("category");
        
        Map<String, Object> schema = buildMockGeneratedSchema(activityDesc, formName, category);
        
        return Map.of(
            "success", true,
            "data", schema,
            "generated", true
        );
    }
    
    private Object handleGetFormFieldMappings(Map<String, Object> args) {
        String formId = (String) args.get("formId");
        @SuppressWarnings("unchecked")
        List<String> requiredFields = (List<String>) args.get("requiredFields");
        
        List<Map<String, Object>> mappings = buildMockFieldMappings(formId, requiredFields);
        
        return Map.of(
            "success", true,
            "data", mappings,
            "formId", formId,
            "coverage", calculateCoverage(mappings)
        );
    }
    
    private Object handleListFormCategories(Map<String, Object> args) {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        categories.add(Map.of(
            "categoryId", "HR",
            "categoryName", "人力资源",
            "description", "招聘、入职、离职等HR相关表单",
            "formCount", 12
        ));
        categories.add(Map.of(
            "categoryId", "FIN",
            "categoryName", "财务管理",
            "description", "报销、预算、审批等财务表单",
            "formCount", 8
        ));
        categories.add(Map.of(
            "categoryId", "ADMIN",
            "categoryName", "行政管理",
            "description", "请假、出差、会议等行政表单",
            "formCount", 15
        ));
        categories.add(Map.of(
            "categoryId", "TECH",
            "categoryName", "技术开发",
            "description", "需求、测试、发布等技术表单",
            "formCount", 10
        ));
        
        return Map.of(
            "success", true,
            "data", categories,
            "count", categories.size()
        );
    }
    
    private List<Map<String, Object>> buildMockForms(String category, String status) {
        List<Map<String, Object>> forms = new ArrayList<>();
        
        forms.add(Map.of(
            "formId", "form-001",
            "formName", "简历评估表",
            "description", "候选人简历评估表单",
            "category", "HR",
            "status", "PUBLISHED",
            "fieldCount", 8,
            "version", "1.2.0"
        ));
        forms.add(Map.of(
            "formId", "form-002",
            "formName", "面试评价表",
            "description", "面试官填写面试评价",
            "category", "HR",
            "status", "PUBLISHED",
            "fieldCount", 12,
            "version", "2.0.0"
        ));
        forms.add(Map.of(
            "formId", "form-003",
            "formName", "入职登记表",
            "description", "新员工入职信息登记",
            "category", "HR",
            "status", "PUBLISHED",
            "fieldCount", 20,
            "version", "1.5.0"
        ));
        forms.add(Map.of(
            "formId", "form-004",
            "formName", "请假申请表",
            "description", "员工请假申请表单",
            "category", "ADMIN",
            "status", "PUBLISHED",
            "fieldCount", 6,
            "version", "1.0.0"
        ));
        forms.add(Map.of(
            "formId", "form-005",
            "formName", "报销申请表",
            "description", "费用报销申请表单",
            "category", "FIN",
            "status", "PUBLISHED",
            "fieldCount", 10,
            "version", "1.3.0"
        ));
        forms.add(Map.of(
            "formId", "form-006",
            "formName", "出差申请表",
            "description", "出差申请审批表单",
            "category", "ADMIN",
            "status", "PUBLISHED",
            "fieldCount", 8,
            "version", "1.1.0"
        ));
        forms.add(Map.of(
            "formId", "form-007",
            "formName", "Offer审批表",
            "description", "录用审批表单",
            "category", "HR",
            "status", "PUBLISHED",
            "fieldCount", 15,
            "version", "1.0.0"
        ));
        
        if (category != null) {
            return forms.stream()
                .filter(f -> category.equalsIgnoreCase((String) f.get("category")))
                .collect(Collectors.toList());
        }
        
        if (status != null) {
            return forms.stream()
                .filter(f -> status.equalsIgnoreCase((String) f.get("status")))
                .collect(Collectors.toList());
        }
        
        return forms;
    }
    
    private List<Map<String, Object>> buildMockSearchForms(String query, String category, int limit) {
        List<Map<String, Object>> allForms = buildMockForms(null, null);
        
        String lowerQuery = query.toLowerCase();
        
        return allForms.stream()
            .filter(f -> {
                String name = ((String) f.get("formName")).toLowerCase();
                String desc = ((String) f.get("description")).toLowerCase();
                return name.contains(lowerQuery) || desc.contains(lowerQuery);
            })
            .filter(f -> category == null || category.equalsIgnoreCase((String) f.get("category")))
            .limit(limit)
            .map(f -> {
                Map<String, Object> result = new HashMap<>(f);
                result.put("matchScore", 0.80 + Math.random() * 0.20);
                return result;
            })
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> buildMockFormSchema(String formId) {
        Map<String, Map<String, Object>> schemas = new HashMap<>();
        
        schemas.put("form-001", Map.of(
            "formId", "form-001",
            "formName", "简历评估表",
            "description", "候选人简历评估表单",
            "category", "HR",
            "fields", List.of(
                Map.of("fieldId", "candidate_name", "fieldName", "候选人姓名", "type", "text", "required", true),
                Map.of("fieldId", "candidate_phone", "fieldName", "联系电话", "type", "text", "required", true),
                Map.of("fieldId", "candidate_email", "fieldName", "邮箱", "type", "text", "required", true),
                Map.of("fieldId", "position", "fieldName", "应聘岗位", "type", "select", "required", true),
                Map.of("fieldId", "education", "fieldName", "学历", "type", "select", "required", true),
                Map.of("fieldId", "experience", "fieldName", "工作年限", "type", "number", "required", true),
                Map.of("fieldId", "skill_match", "fieldName", "技能匹配度", "type", "number", "required", false),
                Map.of("fieldId", "evaluation", "fieldName", "综合评价", "type", "textarea", "required", true)
            ),
            "layout", Map.of(
                "type", "vertical",
                "columns", 2
            )
        ));
        
        schemas.put("form-002", Map.of(
            "formId", "form-002",
            "formName", "面试评价表",
            "description", "面试官填写面试评价",
            "category", "HR",
            "fields", List.of(
                Map.of("fieldId", "candidate_name", "fieldName", "候选人姓名", "type", "text", "required", true),
                Map.of("fieldId", "interview_date", "fieldName", "面试日期", "type", "date", "required", true),
                Map.of("fieldId", "interviewer", "fieldName", "面试官", "type", "user", "required", true),
                Map.of("fieldId", "interview_round", "fieldName", "面试轮次", "type", "select", "required", true),
                Map.of("fieldId", "technical_score", "fieldName", "技术能力评分", "type", "number", "required", true),
                Map.of("fieldId", "communication_score", "fieldName", "沟通能力评分", "type", "number", "required", true),
                Map.of("fieldId", "attitude_score", "fieldName", "态度评分", "type", "number", "required", true),
                Map.of("fieldId", "total_score", "fieldName", "综合评分", "type", "number", "required", true),
                Map.of("fieldId", "strengths", "fieldName", "优点", "type", "textarea", "required", false),
                Map.of("fieldId", "weaknesses", "fieldName", "不足", "type", "textarea", "required", false),
                Map.of("fieldId", "recommendation", "fieldName", "推荐意见", "type", "select", "required", true),
                Map.of("fieldId", "comments", "fieldName", "备注", "type", "textarea", "required", false)
            ),
            "layout", Map.of(
                "type", "vertical",
                "columns", 2
            )
        ));
        
        schemas.put("form-003", Map.of(
            "formId", "form-003",
            "formName", "入职登记表",
            "description", "新员工入职信息登记",
            "category", "HR",
            "fields", List.of(
                Map.of("fieldId", "employee_name", "fieldName", "姓名", "type", "text", "required", true),
                Map.of("fieldId", "id_number", "fieldName", "身份证号", "type", "text", "required", true),
                Map.of("fieldId", "gender", "fieldName", "性别", "type", "select", "required", true),
                Map.of("fieldId", "birthday", "fieldName", "出生日期", "type", "date", "required", true),
                Map.of("fieldId", "phone", "fieldName", "联系电话", "type", "text", "required", true),
                Map.of("fieldId", "email", "fieldName", "邮箱", "type", "text", "required", true),
                Map.of("fieldId", "address", "fieldName", "家庭住址", "type", "text", "required", true),
                Map.of("fieldId", "emergency_contact", "fieldName", "紧急联系人", "type", "text", "required", true),
                Map.of("fieldId", "emergency_phone", "fieldName", "紧急联系电话", "type", "text", "required", true),
                Map.of("fieldId", "bank_name", "fieldName", "开户银行", "type", "text", "required", true),
                Map.of("fieldId", "bank_account", "fieldName", "银行账号", "type", "text", "required", true)
            ),
            "layout", Map.of(
                "type", "vertical",
                "columns", 2
            )
        ));
        
        return schemas.getOrDefault(formId, Map.of(
            "formId", formId,
            "formName", "未知表单",
            "status", "NOT_FOUND"
        ));
    }
    
    private List<Map<String, Object>> buildMockFormMatches(String activityDesc, List<String> requiredFields) {
        List<Map<String, Object>> matches = new ArrayList<>();
        
        String lowerDesc = activityDesc.toLowerCase();
        
        if (lowerDesc.contains("简历") || lowerDesc.contains("筛选")) {
            matches.add(Map.of(
                "formId", "form-001",
                "formName", "简历评估表",
                "matchScore", 0.95,
                "matchReason", "活动涉及简历筛选，该表单可直接支持评估记录",
                "fieldMappings", List.of(
                    Map.of("activityField", "候选人信息", "formField", "candidate_name", "mappingScore", 1.0),
                    Map.of("activityField", "评估结果", "formField", "evaluation", "mappingScore", 0.95)
                ),
                "coverage", 0.85
            ));
        }
        
        if (lowerDesc.contains("面试") || lowerDesc.contains("约谈")) {
            matches.add(Map.of(
                "formId", "form-002",
                "formName", "面试评价表",
                "matchScore", 0.92,
                "matchReason", "活动涉及面试，该表单可记录面试评价",
                "fieldMappings", List.of(
                    Map.of("activityField", "面试日期", "formField", "interview_date", "mappingScore", 1.0),
                    Map.of("activityField", "面试官", "formField", "interviewer", "mappingScore", 1.0),
                    Map.of("activityField", "评价结果", "formField", "recommendation", "mappingScore", 0.95)
                ),
                "coverage", 0.90
            ));
        }
        
        if (lowerDesc.contains("入职") || lowerDesc.contains("办理")) {
            matches.add(Map.of(
                "formId", "form-003",
                "formName", "入职登记表",
                "matchScore", 0.98,
                "matchReason", "活动涉及入职办理，该表单可收集入职信息",
                "fieldMappings", List.of(
                    Map.of("activityField", "员工信息", "formField", "employee_name", "mappingScore", 1.0),
                    Map.of("activityField", "联系方式", "formField", "phone", "mappingScore", 1.0),
                    Map.of("activityField", "银行信息", "formField", "bank_account", "mappingScore", 1.0)
                ),
                "coverage", 0.95
            ));
        }
        
        if (lowerDesc.contains("offer") || lowerDesc.contains("录用")) {
            matches.add(Map.of(
                "formId", "form-007",
                "formName", "Offer审批表",
                "matchScore", 0.95,
                "matchReason", "活动涉及Offer发放，该表单可支持审批流程",
                "fieldMappings", List.of(
                    Map.of("activityField", "候选人", "formField", "candidate_name", "mappingScore", 1.0),
                    Map.of("activityField", "薪资", "formField", "salary", "mappingScore", 1.0),
                    Map.of("activityField", "入职日期", "formField", "onboard_date", "mappingScore", 1.0)
                ),
                "coverage", 0.90
            ));
        }
        
        if (matches.isEmpty()) {
            matches.add(Map.of(
                "formId", "form-004",
                "formName", "通用审批表",
                "matchScore", 0.60,
                "matchReason", "通用表单，可自定义字段",
                "fieldMappings", new ArrayList<>(),
                "coverage", 0.40
            ));
        }
        
        return matches;
    }
    
    private Map<String, Object> buildMockGeneratedSchema(String activityDesc, String formName, String category) {
        String lowerDesc = activityDesc.toLowerCase();
        
        List<Map<String, Object>> fields = new ArrayList<>();
        
        if (lowerDesc.contains("简历") || lowerDesc.contains("筛选")) {
            fields.add(Map.of("fieldId", "candidate_name", "fieldName", "候选人姓名", "type", "text", "required", true));
            fields.add(Map.of("fieldId", "resume_url", "fieldName", "简历附件", "type", "file", "required", true));
            fields.add(Map.of("fieldId", "position", "fieldName", "应聘岗位", "type", "text", "required", true));
            fields.add(Map.of("fieldId", "match_score", "fieldName", "匹配分数", "type", "number", "required", false));
            fields.add(Map.of("fieldId", "evaluation", "fieldName", "评估意见", "type", "textarea", "required", true));
        } else if (lowerDesc.contains("面试") || lowerDesc.contains("约谈")) {
            fields.add(Map.of("fieldId", "candidate_name", "fieldName", "候选人姓名", "type", "text", "required", true));
            fields.add(Map.of("fieldId", "interview_time", "fieldName", "面试时间", "type", "datetime", "required", true));
            fields.add(Map.of("fieldId", "interviewer", "fieldName", "面试官", "type", "user", "required", true));
            fields.add(Map.of("fieldId", "interview_type", "fieldName", "面试类型", "type", "select", "required", true));
            fields.add(Map.of("fieldId", "result", "fieldName", "面试结果", "type", "select", "required", true));
            fields.add(Map.of("fieldId", "comments", "fieldName", "评价意见", "type", "textarea", "required", false));
        } else {
            fields.add(Map.of("fieldId", "title", "fieldName", "标题", "type", "text", "required", true));
            fields.add(Map.of("fieldId", "description", "fieldName", "描述", "type", "textarea", "required", false));
            fields.add(Map.of("fieldId", "attachments", "fieldName", "附件", "type", "file", "required", false));
        }
        
        return Map.of(
            "formId", "form-generated-" + System.currentTimeMillis(),
            "formName", formName != null ? formName : "自动生成表单",
            "description", "根据活动描述自动生成: " + activityDesc,
            "category", category != null ? category : "AUTO",
            "fields", fields,
            "generated", true,
            "generationReason", "根据活动描述智能生成表单Schema"
        );
    }
    
    private List<Map<String, Object>> buildMockFieldMappings(String formId, List<String> requiredFields) {
        List<Map<String, Object>> mappings = new ArrayList<>();
        
        if (requiredFields != null) {
            for (String field : requiredFields) {
                mappings.add(Map.of(
                    "requiredField", field,
                    "formField", field.toLowerCase().replace(" ", "_"),
                    "mappingScore", 0.85 + Math.random() * 0.15,
                    "autoMapped", true
                ));
            }
        }
        
        return mappings;
    }
    
    private double calculateCoverage(List<Map<String, Object>> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return 0.0;
        }
        
        return mappings.stream()
            .mapToDouble(m -> ((Number) m.get("mappingScore")).doubleValue())
            .average()
            .orElse(0.0);
    }
}

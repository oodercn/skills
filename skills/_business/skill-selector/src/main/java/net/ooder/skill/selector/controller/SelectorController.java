package net.ooder.skill.selector.controller;

import net.ooder.skill.selector.dto.*;
import net.ooder.skill.selector.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/selectors")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class SelectorController {

    private static final Logger log = LoggerFactory.getLogger(SelectorController.class);

    @GetMapping("/capabilities")
    public ResultModel<List<CapabilityItemDTO>> getCapabilities(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type) {
        log.info("[SelectorController] Get capabilities called - category: {}, type: {}", category, type);

        List<CapabilityItemDTO> capabilities = new ArrayList<>();

        CapabilityItemDTO cap1 = new CapabilityItemDTO();
        cap1.setCapabilityId("cap-001");
        cap1.setName("智能客服");
        cap1.setDescription("基于LLM的智能客服能力");
        cap1.setCategory("customer-service");
        cap1.setType("llm");
        cap1.setIcon("ri-customer-service-2-line");
        cap1.setStatus("active");
        capabilities.add(cap1);

        CapabilityItemDTO cap2 = new CapabilityItemDTO();
        cap2.setCapabilityId("cap-002");
        cap2.setName("文档问答");
        cap2.setDescription("基于知识库的文档问答能力");
        cap2.setCategory("knowledge");
        cap2.setType("llm");
        cap2.setIcon("ri-file-search-line");
        cap2.setStatus("active");
        capabilities.add(cap2);

        CapabilityItemDTO cap3 = new CapabilityItemDTO();
        cap3.setCapabilityId("cap-003");
        cap3.setName("数据分析");
        cap3.setDescription("数据分析和可视化能力");
        cap3.setCategory("data");
        cap3.setType("analytics");
        cap3.setIcon("ri-bar-chart-line");
        cap3.setStatus("active");
        capabilities.add(cap3);

        return ResultModel.success(capabilities);
    }

    @GetMapping("/providers")
    public ResultModel<List<ProviderItemDTO>> getProviders(
            @RequestParam(required = false) String type) {
        log.info("[SelectorController] Get providers called - type: {}", type);

        List<ProviderItemDTO> providers = new ArrayList<>();

        ProviderItemDTO p1 = new ProviderItemDTO();
        p1.setProviderId("qianwen");
        p1.setName("通义千问");
        p1.setType("qianwen");
        p1.setIcon("ri-aliens-line");
        p1.setEnabled(true);
        p1.setModels(List.of("qwen-plus", "qwen-turbo", "qwen-max"));
        p1.setStatus("active");
        providers.add(p1);

        ProviderItemDTO p2 = new ProviderItemDTO();
        p2.setProviderId("deepseek");
        p2.setName("DeepSeek");
        p2.setType("deepseek");
        p2.setIcon("ri-brain-line");
        p2.setEnabled(true);
        p2.setModels(List.of("deepseek-chat", "deepseek-coder"));
        p2.setStatus("active");
        providers.add(p2);

        ProviderItemDTO p3 = new ProviderItemDTO();
        p3.setProviderId("baidu");
        p3.setName("百度千帆");
        p3.setType("baidu");
        p3.setIcon("ri-baidu-line");
        p3.setEnabled(true);
        p3.setModels(List.of("ernie-4.0", "ernie-3.5"));
        p3.setStatus("active");
        providers.add(p3);

        return ResultModel.success(providers);
    }

    @GetMapping("/capability-types")
    public ResultModel<List<CapabilityTypeDTO>> getCapabilityTypes() {
        log.info("[SelectorController] Get capability types called");

        List<CapabilityTypeDTO> types = new ArrayList<>();

        CapabilityTypeDTO t1 = new CapabilityTypeDTO("llm", "大模型", "基于大语言模型的能力");
        t1.setIcon("ri-brain-line");
        t1.setCount(5);
        types.add(t1);

        CapabilityTypeDTO t2 = new CapabilityTypeDTO("knowledge", "知识库", "知识库相关能力");
        t2.setIcon("ri-book-open-line");
        t2.setCount(3);
        types.add(t2);

        CapabilityTypeDTO t3 = new CapabilityTypeDTO("data", "数据分析", "数据处理和分析能力");
        t3.setIcon("ri-bar-chart-line");
        t3.setCount(2);
        types.add(t3);

        CapabilityTypeDTO t4 = new CapabilityTypeDTO("workflow", "工作流", "自动化工作流能力");
        t4.setIcon("ri-flow-chart");
        t4.setCount(4);
        types.add(t4);

        return ResultModel.success(types);
    }

    @GetMapping("/org-tree")
    public ResultModel<List<OrgNodeDTO>> getOrgTree() {
        log.info("[SelectorController] Get org tree called");

        List<OrgNodeDTO> tree = new ArrayList<>();

        OrgNodeDTO rd = new OrgNodeDTO("dept-rd", "研发部", "department");
        List<OrgNodeDTO> rdChildren = new ArrayList<>();
        OrgNodeDTO m1 = new OrgNodeDTO("user-mgr-001", "张经理", "manager");
        OrgNodeDTO e1 = new OrgNodeDTO("user-emp-001", "李员工", "employee");
        OrgNodeDTO e2 = new OrgNodeDTO("user-emp-002", "王员工", "employee");
        rdChildren.add(m1);
        rdChildren.add(e1);
        rdChildren.add(e2);
        rd.setChildren(rdChildren);
        tree.add(rd);

        OrgNodeDTO hr = new OrgNodeDTO("dept-hr", "人力资源部", "department");
        List<OrgNodeDTO> hrChildren = new ArrayList<>();
        OrgNodeDTO hr1 = new OrgNodeDTO("user-hr-001", "刘HR", "hr");
        hrChildren.add(hr1);
        hr.setChildren(hrChildren);
        tree.add(hr);

        OrgNodeDTO ops = new OrgNodeDTO("dept-ops", "运维部", "department");
        List<OrgNodeDTO> opsChildren = new ArrayList<>();
        OrgNodeDTO ops1 = new OrgNodeDTO("user-ops-001", "陈运维", "operator");
        opsChildren.add(ops1);
        ops.setChildren(opsChildren);
        tree.add(ops);

        return ResultModel.success(tree);
    }

    @GetMapping("/templates")
    public ResultModel<List<TemplateItemDTO>> getTemplates(
            @RequestParam(required = false) String category) {
        log.info("[SelectorController] Get templates called - category: {}", category);

        List<TemplateItemDTO> templates = new ArrayList<>();

        TemplateItemDTO tmpl1 = new TemplateItemDTO();
        tmpl1.setTemplateId("tmpl-001");
        tmpl1.setName("智能客服模板");
        tmpl1.setDescription("快速创建智能客服场景");
        tmpl1.setCategory("customer-service");
        tmpl1.setIcon("ri-customer-service-2-line");
        tmpl1.setStatus("active");
        tmpl1.setCapabilityCount(3);
        templates.add(tmpl1);

        TemplateItemDTO tmpl2 = new TemplateItemDTO();
        tmpl2.setTemplateId("tmpl-002");
        tmpl2.setName("知识库问答模板");
        tmpl2.setDescription("基于知识库的问答场景");
        tmpl2.setCategory("knowledge");
        tmpl2.setIcon("ri-book-open-line");
        tmpl2.setStatus("active");
        tmpl2.setCapabilityCount(2);
        templates.add(tmpl2);

        return ResultModel.success(templates);
    }
}
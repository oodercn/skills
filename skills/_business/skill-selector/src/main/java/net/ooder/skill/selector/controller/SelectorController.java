package net.ooder.skill.selector.controller;

import net.ooder.skill.selector.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/selectors")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class SelectorController {

    private static final Logger log = LoggerFactory.getLogger(SelectorController.class);

    @GetMapping("/capabilities")
    public List<CapabilityItemDTO> getCapabilities(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        log.info("[SelectorController] getCapabilities: category={}, keyword={}", category, keyword);
        return new ArrayList<>();
    }

    @GetMapping("/providers")
    public List<ProviderItemDTO> getProviders(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        log.info("[SelectorController] getProviders: type={}, keyword={}", type, keyword);
        return new ArrayList<>();
    }

    @GetMapping("/capability-types")
    public List<CapabilityTypeDTO> getCapabilityTypes() {
        log.info("[SelectorController] getCapabilityTypes");
        List<CapabilityTypeDTO> types = new ArrayList<>();
        types.add(createType("SYS", "系统能力", "系统内置能力"));
        types.add(createType("DRIVER", "驱动能力", "外部系统驱动"));
        types.add(createType("SCENE", "场景能力", "业务场景能力"));
        types.add(createType("TOOL", "工具能力", "工具类能力"));
        return types;
    }

    @GetMapping("/org-tree")
    public List<OrgNodeDTO> getOrgTree(
            @RequestParam(required = false) String rootId,
            @RequestParam(defaultValue = "false") boolean includeUsers) {
        log.info("[SelectorController] getOrgTree: rootId={}, includeUsers={}", rootId, includeUsers);
        return new ArrayList<>();
    }

    @GetMapping("/templates")
    public List<TemplateItemDTO> getTemplates(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        log.info("[SelectorController] getTemplates: category={}, keyword={}", category, keyword);
        return new ArrayList<>();
    }

    private CapabilityTypeDTO createType(String code, String name, String description) {
        CapabilityTypeDTO dto = new CapabilityTypeDTO();
        dto.setCode(code);
        dto.setName(name);
        dto.setDescription(description);
        return dto;
    }
}

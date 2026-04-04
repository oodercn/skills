package net.ooder.skill.context.controller;

import net.ooder.skill.context.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/context")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ContextEventController {

    private static final Logger log = LoggerFactory.getLogger(ContextEventController.class);

    private final Map<String, Object> currentContext = new HashMap<>();

    @GetMapping("/status")
    public ResultModel<Map<String, Object>> getContextStatus() {
        log.info("[ContextEventController] getContextStatus");
        return ResultModel.success(new HashMap<>(currentContext));
    }

    @PostMapping("/navigate")
    public ResultModel<Boolean> onPageNavigate(@RequestBody PageNavigateRequest request) {
        log.info("[ContextEventController] onPageNavigate: {}", request.getPage());
        currentContext.put("currentPage", request.getPage());
        currentContext.put("navigateTime", System.currentTimeMillis());
        return ResultModel.success(true);
    }

    @PostMapping("/skill-change")
    public ResultModel<Boolean> onSkillChange(@RequestBody SkillChangeRequest request) {
        log.info("[ContextEventController] onSkillChange: {}", request.getSkillId());
        currentContext.put("currentSkill", request.getSkillId());
        currentContext.put("skillChangeTime", System.currentTimeMillis());
        return ResultModel.success(true);
    }

    @PostMapping("/update")
    public ResultModel<Boolean> pushContextUpdate(@RequestBody ContextUpdateRequest request) {
        log.info("[ContextEventController] pushContextUpdate: {}", request.getKey());
        currentContext.put(request.getKey(), request.getValue());
        return ResultModel.success(true);
    }

    public static class PageNavigateRequest {
        private String page;
        private Map<String, Object> params;

        public String getPage() { return page; }
        public void setPage(String page) { this.page = page; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }

    public static class SkillChangeRequest {
        private String skillId;
        private String sceneGroupId;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    }

    public static class ContextUpdateRequest {
        private String key;
        private Object value;

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
    }
}

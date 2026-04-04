package net.ooder.skill.capability.controller;

import net.ooder.skill.capability.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/capabilities/bindings")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class CapabilityBindingController {

    private static final Logger log = LoggerFactory.getLogger(CapabilityBindingController.class);

    private final Map<String, Map<String, Object>> bindingStore = new HashMap<>();

    @GetMapping
    public ResultModel<List<Map<String, Object>>> listBindings() {
        log.info("[CapabilityBindingController] List bindings");
        return ResultModel.success(new ArrayList<>(bindingStore.values()));
    }

    @GetMapping("/{id}")
    public ResultModel<Map<String, Object>> getBinding(@PathVariable String id) {
        log.info("[CapabilityBindingController] Get binding: {}", id);
        Map<String, Object> binding = bindingStore.get(id);
        if (binding == null) {
            return ResultModel.notFound("Binding not found: " + id);
        }
        return ResultModel.success(binding);
    }

    @PostMapping
    public ResultModel<Map<String, Object>> createBinding(@RequestBody Map<String, Object> request) {
        log.info("[CapabilityBindingController] Create binding");
        String id = UUID.randomUUID().toString();
        request.put("id", id);
        request.put("createTime", new Date().toString());
        bindingStore.put(id, request);
        return ResultModel.success(request);
    }

    @PutMapping("/{id}")
    public ResultModel<Map<String, Object>> updateBinding(@PathVariable String id, @RequestBody Map<String, Object> request) {
        log.info("[CapabilityBindingController] Update binding: {}", id);
        if (!bindingStore.containsKey(id)) {
            return ResultModel.notFound("Binding not found: " + id);
        }
        request.put("id", id);
        request.put("updateTime", new Date().toString());
        bindingStore.put(id, request);
        return ResultModel.success(request);
    }

    @DeleteMapping("/{id}")
    public ResultModel<Boolean> deleteBinding(@PathVariable String id) {
        log.info("[CapabilityBindingController] Delete binding: {}", id);
        if (!bindingStore.containsKey(id)) {
            return ResultModel.notFound("Binding not found: " + id);
        }
        bindingStore.remove(id);
        return ResultModel.success(true);
    }

    @PostMapping("/{id}/test")
    public ResultModel<Map<String, Object>> testBinding(@PathVariable String id) {
        log.info("[CapabilityBindingController] Test binding: {}", id);
        Map<String, Object> result = new HashMap<>();
        result.put("bindingId", id);
        result.put("status", "success");
        result.put("testTime", new Date().toString());
        return ResultModel.success(result);
    }

    @GetMapping("/by-link/{linkId}")
    public ResultModel<List<Map<String, Object>>> getBindingsByLink(@PathVariable String linkId) {
        log.info("[CapabilityBindingController] Get bindings by link: {}", linkId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> binding : bindingStore.values()) {
            if (linkId.equals(binding.get("linkId"))) {
                result.add(binding);
            }
        }
        return ResultModel.success(result);
    }
}

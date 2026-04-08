package net.ooder.skill.setup.controller;

import net.ooder.skill.setup.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class SetupController {

    private static final Logger log = LoggerFactory.getLogger(SetupController.class);

    @PostMapping("/plugin/install")
    public ResultModel<Map<String, Object>> installPlugin(@RequestBody Map<String, Object> request) {
        log.info("[SetupController] Install plugin: {}", request.get("pluginId"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("pluginId", request.get("pluginId"));
        result.put("message", "Plugin installed successfully");
        result.put("installTime", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @PostMapping("/setup/admin")
    public ResultModel<Map<String, Object>> setupAdmin(@RequestBody Map<String, Object> request) {
        log.info("[SetupController] Setup admin: {}", request.get("username"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("username", request.get("username"));
        result.put("message", "Admin account created successfully");
        result.put("createTime", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @GetMapping("/system/config")
    public ResultModel<Map<String, Object>> getSystemConfig() {
        log.info("[SetupController] Get system config");
        
        Map<String, Object> config = new HashMap<>();
        config.put("version", "1.0.0");
        config.put("environment", "development");
        config.put("initialized", true);
        
        return ResultModel.success(config);
    }
}

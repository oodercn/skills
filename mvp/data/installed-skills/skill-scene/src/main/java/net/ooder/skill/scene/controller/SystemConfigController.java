package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.model.ResultModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统配置API控制器
 */
@RestController
@RequestMapping("/api/v1/system")
@CrossOrigin(origins = "*")
public class SystemConfigController {

    @Value("${spring.application.name:skill-scene}")
    private String applicationName;

    @Value("${ooder.jds.config.name:scene}")
    private String configName;

    @Value("${ooder.sdk.enabled:true}")
    private boolean sdkEnabled;

    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    /**
     * 获取系统配置信息
     */
    @GetMapping("/config")
    public ResultModel<Map<String, Object>> getSystemConfig() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("syscode", applicationName);
        config.put("configName", configName);
        config.put("sdkEnabled", sdkEnabled);
        config.put("mockEnabled", mockEnabled);
        config.put("version", "1.0.0");
        config.put("environment", System.getProperty("spring.profiles.active", "default"));
        
        return ResultModel.success(config);
    }

    /**
     * 获取系统syscode
     */
    @GetMapping("/syscode")
    public ResultModel<String> getSyscode() {
        return ResultModel.success(applicationName);
    }

    /**
     * 检查系统是否为指定环境
     */
    @GetMapping("/check")
    public ResultModel<Map<String, Boolean>> checkSystem(@RequestParam String syscode) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("isCurrentSystem", applicationName.equals(syscode));
        result.put("isScene", applicationName.contains("scene"));
        result.put("isCapability", applicationName.contains("capability"));
        result.put("isHealth", applicationName.contains("health"));
        result.put("isLlmChat", applicationName.contains("llm-chat"));
        
        return ResultModel.success(result);
    }
}

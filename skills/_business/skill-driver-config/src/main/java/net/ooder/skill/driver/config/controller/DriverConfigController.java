package net.ooder.skill.driver.config.controller;

import net.ooder.skill.driver.config.dto.AllDriversTestResultDTO;
import net.ooder.skill.driver.config.dto.DriverConfigDTO;
import net.ooder.skill.driver.config.dto.DriverTestResultDTO;
import net.ooder.skill.driver.config.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class DriverConfigController {

    private static final Logger log = LoggerFactory.getLogger(DriverConfigController.class);

    private final Map<String, DriverConfigDTO> driverConfigs = new HashMap<>();

    public DriverConfigController() {
        initDefaultDrivers();
    }

    private void initDefaultDrivers() {
        DriverConfigDTO qianwen = new DriverConfigDTO();
        qianwen.setDriverId("driver-qianwen");
        qianwen.setName("通义千问");
        qianwen.setType("qianwen");
        qianwen.setCategory("llm");
        qianwen.setDescription("阿里云通义千问LLM驱动");
        qianwen.setEnabled(true);
        qianwen.setConfigured(true);
        qianwen.setStatus("active");
        qianwen.setCreatedAt(System.currentTimeMillis());
        qianwen.setUpdatedAt(System.currentTimeMillis());
        driverConfigs.put(qianwen.getDriverId(), qianwen);

        DriverConfigDTO deepseek = new DriverConfigDTO();
        deepseek.setDriverId("driver-deepseek");
        deepseek.setName("DeepSeek");
        deepseek.setType("deepseek");
        deepseek.setCategory("llm");
        deepseek.setDescription("DeepSeek LLM驱动");
        deepseek.setEnabled(true);
        deepseek.setConfigured(false);
        deepseek.setStatus("active");
        deepseek.setCreatedAt(System.currentTimeMillis());
        deepseek.setUpdatedAt(System.currentTimeMillis());
        driverConfigs.put(deepseek.getDriverId(), deepseek);

        DriverConfigDTO baidu = new DriverConfigDTO();
        baidu.setDriverId("driver-baidu");
        baidu.setName("百度千帆");
        baidu.setType("baidu");
        baidu.setCategory("llm");
        baidu.setDescription("百度千帆LLM驱动");
        baidu.setEnabled(false);
        baidu.setConfigured(false);
        baidu.setStatus("inactive");
        baidu.setCreatedAt(System.currentTimeMillis());
        baidu.setUpdatedAt(System.currentTimeMillis());
        driverConfigs.put(baidu.getDriverId(), baidu);
    }

    @GetMapping("/config/driver-configs")
    public ResultModel<List<DriverConfigDTO>> listDriverConfigs(
            @RequestParam(required = false) String category) {
        log.info("[DriverConfigController] List driver configs called - category: {}", category);

        List<DriverConfigDTO> configs = new ArrayList<>(driverConfigs.values());
        if (category != null && !category.isEmpty()) {
            configs.removeIf(c -> !category.equals(c.getCategory()));
        }

        return ResultModel.success(configs);
    }

    @GetMapping("/config/driver-configs/{driverId}")
    public ResultModel<DriverConfigDTO> getDriverConfig(@PathVariable String driverId) {
        log.info("[DriverConfigController] Get driver config called: {}", driverId);
        DriverConfigDTO config = driverConfigs.get(driverId);
        if (config == null) {
            return ResultModel.notFound("Driver config not found: " + driverId);
        }
        return ResultModel.success(config);
    }

    @PostMapping("/config/driver-configs/{driverId}")
    public ResultModel<DriverConfigDTO> updateDriverConfig(
            @PathVariable String driverId,
            @RequestBody DriverConfigDTO config) {
        log.info("[DriverConfigController] Update driver config called: {}", driverId);

        DriverConfigDTO existing = driverConfigs.get(driverId);
        if (existing == null) {
            return ResultModel.notFound("Driver config not found: " + driverId);
        }

        existing.setConfig(config.getConfig());
        existing.setCredentials(config.getCredentials());
        existing.setEnabled(config.isEnabled());
        existing.setUpdatedAt(System.currentTimeMillis());

        return ResultModel.success(existing);
    }

    @PostMapping("/config/driver-configs")
    public ResultModel<DriverConfigDTO> createDriverConfig(@RequestBody DriverConfigDTO config) {
        log.info("[DriverConfigController] Create driver config called: {}", config.getName());

        String driverId = "driver-" + UUID.randomUUID().toString().substring(0, 8);
        config.setDriverId(driverId);
        config.setCreatedAt(System.currentTimeMillis());
        config.setUpdatedAt(System.currentTimeMillis());

        driverConfigs.put(driverId, config);
        return ResultModel.success(config);
    }

    @GetMapping("/drivers")
    public ResultModel<List<DriverConfigDTO>> listDrivers(
            @RequestParam(required = false) String type) {
        log.info("[DriverConfigController] List drivers called - type: {}", type);

        List<DriverConfigDTO> drivers = new ArrayList<>(driverConfigs.values());
        if (type != null && !type.isEmpty()) {
            drivers.removeIf(d -> !type.equals(d.getType()));
        }

        return ResultModel.success(drivers);
    }

    @GetMapping("/drivers/{driverId}")
    public ResultModel<DriverConfigDTO> getDriver(@PathVariable String driverId) {
        log.info("[DriverConfigController] Get driver called: {}", driverId);
        DriverConfigDTO config = driverConfigs.get(driverId);
        if (config == null) {
            return ResultModel.notFound("Driver not found: " + driverId);
        }
        return ResultModel.success(config);
    }

    @PostMapping("/drivers/{driverId}/test")
    public ResultModel<DriverTestResultDTO> testDriver(@PathVariable String driverId) {
        log.info("[DriverConfigController] Test driver called: {}", driverId);

        DriverConfigDTO config = driverConfigs.get(driverId);
        if (config == null) {
            return ResultModel.notFound("Driver not found: " + driverId);
        }

        DriverTestResultDTO result = new DriverTestResultDTO();
        result.setDriverId(driverId);
        result.setName(config.getName());
        result.setSuccess(config.isConfigured());
        result.setMessage(config.isConfigured() ? "Driver is configured" : "Driver is not configured");

        return ResultModel.success(result);
    }

    @PostMapping("/drivers/test-all")
    public ResultModel<AllDriversTestResultDTO> testAllDrivers() {
        log.info("[DriverConfigController] Test all drivers called");

        AllDriversTestResultDTO result = new AllDriversTestResultDTO();
        Map<String, Boolean> testResults = new HashMap<>();

        for (DriverConfigDTO config : driverConfigs.values()) {
            testResults.put(config.getDriverId(), config.isConfigured());
        }

        result.setTotal(driverConfigs.size());
        result.setConfigured(driverConfigs.values().stream().filter(DriverConfigDTO::isConfigured).count());
        result.setResults(testResults);

        return ResultModel.success(result);
    }
}
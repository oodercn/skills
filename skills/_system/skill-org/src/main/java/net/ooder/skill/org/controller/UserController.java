package net.ooder.skill.org.controller;

import net.ooder.skill.org.dto.*;
import net.ooder.skill.org.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<String, DeviceDTO> deviceStore = new HashMap<>();

    @PostMapping("/password")
    public ResultModel<OperationResultDTO> changePassword(@RequestBody PasswordChangeDTO request) {
        log.info("[UserController] Change password request");
        
        OperationResultDTO result = new OperationResultDTO();
        result.setSuccess(true);
        result.setMessage("Password changed successfully");
        
        return ResultModel.success(result);
    }

    @GetMapping("/devices")
    public ResultModel<List<DeviceDTO>> getDevices() {
        log.info("[UserController] Get user devices");
        
        List<DeviceDTO> devices = new ArrayList<>();
        
        DeviceDTO device1 = new DeviceDTO();
        device1.setId("device-001");
        device1.setName("Chrome Browser");
        device1.setType("browser");
        device1.setLastActive(new Date().toString());
        device1.setCurrent(true);
        devices.add(device1);
        
        return ResultModel.success(devices);
    }

    @DeleteMapping("/devices/{deviceId}")
    public ResultModel<OperationResultDTO> removeDevice(@PathVariable String deviceId) {
        log.info("[UserController] Remove device: {}", deviceId);
        
        OperationResultDTO result = new OperationResultDTO();
        result.setSuccess(true);
        result.setMessage("Device removed successfully");
        
        return ResultModel.success(result);
    }

    @GetMapping("/devices/others")
    public ResultModel<List<DeviceDTO>> getOtherDevices() {
        log.info("[UserController] Get other devices");
        
        List<DeviceDTO> devices = new ArrayList<>();
        
        return ResultModel.success(devices);
    }
}

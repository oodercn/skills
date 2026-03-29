package net.ooder.skill.platform.bind.controller;

import net.ooder.skill.platform.bind.dto.*;
import net.ooder.skill.platform.bind.service.PlatformBindService;
import net.ooder.api.result.ResultModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/bind")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlatformBindController {

    @Autowired
    private PlatformBindService platformBindService;

    @PostMapping("/{platform}/qrcode")
    public ResultModel<QrCodeDTO> generateQrCode(
            @PathVariable String platform,
            @RequestParam(required = false, defaultValue = "anonymous") String userId) {
        log.info("Generating QR code for platform: {}, userId: {}", platform, userId);
        QrCodeDTO qrCode = platformBindService.generateQrCode(platform.toUpperCase(), userId);
        return ResultModel.success(qrCode);
    }

    @GetMapping("/{platform}/status/{sessionId}")
    public ResultModel<BindStatusDTO> checkBindStatus(
            @PathVariable String platform,
            @PathVariable String sessionId) {
        BindStatusDTO status = platformBindService.checkBindStatus(sessionId);
        return ResultModel.success(status);
    }

    @GetMapping("/callback/{platform}")
    public ResultModel<AuthTokenDTO> handleCallback(
            @PathVariable String platform,
            @RequestParam String code,
            @RequestParam(required = false) String state) {
        log.info("Handling callback for platform: {}, code: {}", platform, code);
        AuthTokenDTO token = platformBindService.handleCallback(platform.toUpperCase(), code, state);
        return ResultModel.success(token);
    }

    @DeleteMapping("/{platform}")
    public ResultModel<Boolean> unbind(
            @PathVariable String platform,
            @RequestParam String userId) {
        log.info("Unbinding platform: {} for user: {}", platform, userId);
        boolean result = platformBindService.unbind(userId, platform.toUpperCase());
        return ResultModel.success(result);
    }

    @GetMapping("/{platform}/binding")
    public ResultModel<PlatformBindingDTO> getUserBinding(
            @PathVariable String platform,
            @RequestParam String userId) {
        PlatformBindingDTO binding = platformBindService.getUserBinding(userId, platform.toUpperCase());
        return ResultModel.success(binding);
    }

    @GetMapping("/bindings")
    public ResultModel<List<PlatformBindingDTO>> getUserBindings(@RequestParam String userId) {
        List<PlatformBindingDTO> bindings = java.util.Arrays.asList("DINGTALK", "FEISHU", "WECOM").stream()
                .map(platform -> platformBindService.getUserBinding(userId, platform))
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
        return ResultModel.success(bindings);
    }
}

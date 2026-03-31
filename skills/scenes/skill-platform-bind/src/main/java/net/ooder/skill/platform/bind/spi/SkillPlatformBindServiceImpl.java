package net.ooder.skill.platform.bind.spi;

import net.ooder.skill.common.spi.PlatformBindService;
import net.ooder.skill.common.spi.bind.QrCodeInfo;
import net.ooder.skill.common.spi.bind.BindStatus;
import net.ooder.skill.common.spi.bind.BindInfo;
import net.ooder.skill.platform.bind.dto.QrCodeDTO;
import net.ooder.skill.platform.bind.dto.BindStatusDTO;
import net.ooder.skill.platform.bind.dto.PlatformBindingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@ConditionalOnProperty(name = "skill.platform.bind.enabled", havingValue = "true", matchIfMissing = false)
public class SkillPlatformBindServiceImpl implements PlatformBindService {
    
    private static final Logger log = LoggerFactory.getLogger(SkillPlatformBindServiceImpl.class);
    
    @Autowired
    private net.ooder.skill.platform.bind.service.PlatformBindService platformBindService;
    
    @Override
    public QrCodeInfo generateBindQrCode(String platform) {
        log.info("[generateBindQrCode] platform={}", platform);
        try {
            QrCodeDTO dto = platformBindService.generateQrCode(platform, null);
            QrCodeInfo info = new QrCodeInfo();
            info.setSessionId(dto.getSessionId());
            info.setQrCodeUrl(dto.getQrCodeUrl());
            info.setQrCodeData(dto.getQrCodeData());
            info.setExpireTime(dto.getExpireTime());
            info.setPlatform(platform);
            return info;
        } catch (Exception e) {
            log.error("[generateBindQrCode] Failed to generate QR code", e);
            return null;
        }
    }
    
    @Override
    public BindStatus checkBindStatus(String platform, String sessionId) {
        log.info("[checkBindStatus] platform={}, sessionId={}", platform, sessionId);
        try {
            BindStatusDTO dto = platformBindService.checkBindStatus(sessionId);
            return convertBindStatus(dto.getStatus());
        } catch (Exception e) {
            log.error("[checkBindStatus] Failed to check bind status", e);
            return BindStatus.EXPIRED;
        }
    }
    
    @Override
    public BindInfo getBinding(String platform, String userId) {
        log.info("[getBinding] platform={}, userId={}", platform, userId);
        try {
            PlatformBindingDTO dto = platformBindService.getUserBinding(userId, platform);
            if (dto == null) {
                return null;
            }
            return convertBindInfo(dto);
        } catch (Exception e) {
            log.error("[getBinding] Failed to get binding", e);
            return null;
        }
    }
    
    @Override
    public List<BindInfo> getBindings(String userId) {
        log.info("[getBindings] userId={}", userId);
        List<BindInfo> result = new ArrayList<>();
        for (String platform : getAvailablePlatforms()) {
            BindInfo binding = getBinding(platform, userId);
            if (binding != null) {
                result.add(binding);
            }
        }
        return result;
    }
    
    @Override
    public void unbind(String platform, String userId) {
        log.info("[unbind] platform={}, userId={}", platform, userId);
        try {
            platformBindService.unbind(userId, platform);
        } catch (Exception e) {
            log.error("[unbind] Failed to unbind", e);
        }
    }
    
    @Override
    public List<String> getAvailablePlatforms() {
        return Arrays.asList("dingtalk", "feishu", "wecom");
    }
    
    private BindStatus convertBindStatus(String status) {
        if (status == null) {
            return BindStatus.PENDING;
        }
        switch (status.toLowerCase()) {
            case "pending":
                return BindStatus.PENDING;
            case "scanned":
                return BindStatus.SCANNED;
            case "bound":
            case "confirmed":
                return BindStatus.CONFIRMED;
            case "expired":
                return BindStatus.EXPIRED;
            case "cancelled":
                return BindStatus.CANCELLED;
            default:
                return BindStatus.PENDING;
        }
    }
    
    private BindInfo convertBindInfo(PlatformBindingDTO dto) {
        BindInfo info = new BindInfo();
        info.setPlatform(dto.getPlatform());
        info.setUserId(dto.getUserId());
        info.setPlatformUserId(dto.getPlatformUserId());
        info.setPlatformUserName(dto.getPlatformUserName());
        info.setStatus(BindStatus.CONFIRMED);
        return info;
    }
}

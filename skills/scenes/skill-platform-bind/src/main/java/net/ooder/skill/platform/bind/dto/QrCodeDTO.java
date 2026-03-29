package net.ooder.skill.platform.bind.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String qrCodeUrl;
    private String qrCodeData;
    private String platform;
    private Long expireTime;
    private Integer expireSeconds;
}

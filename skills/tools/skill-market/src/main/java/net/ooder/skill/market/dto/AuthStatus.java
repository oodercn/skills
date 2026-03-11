package net.ooder.skill.market.dto;

import lombok.Data;

@Data
public class AuthStatus {
    private String status;
    private String level;
    private Long verifyTime;
    private String issuer;
}

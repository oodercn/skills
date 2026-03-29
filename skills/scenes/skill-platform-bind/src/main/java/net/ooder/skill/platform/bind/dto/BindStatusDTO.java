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
public class BindStatusDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String status;
    private String platform;
    private String platformUserId;
    private String platformUserName;
    private String platformAvatar;
    private String bindTime;
    private String message;
    private Boolean bound;
}

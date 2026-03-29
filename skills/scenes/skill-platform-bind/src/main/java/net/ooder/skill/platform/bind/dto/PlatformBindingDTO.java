package net.ooder.skill.platform.bind.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformBindingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String userId;
    private String platform;
    private String platformUserId;
    private String platformUserName;
    private String platformAvatar;
    private String status;
    private String bindTime;
    private String lastSyncTime;
    private List<String> permissions;
}

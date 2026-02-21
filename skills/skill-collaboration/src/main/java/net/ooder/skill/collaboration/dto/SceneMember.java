package net.ooder.skill.collaboration.dto;

import lombok.Data;

@Data
public class SceneMember {
    private String memberId;
    private String memberName;
    private String role;
    private Long joinedAt;
}

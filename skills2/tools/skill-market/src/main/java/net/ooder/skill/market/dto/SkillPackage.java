package net.ooder.skill.market.dto;

import lombok.Data;
import java.util.List;

@Data
public class SkillPackage {
    private String skillId;
    private String name;
    private String version;
    private String description;
    private String category;
    private String author;
    private String icon;
    private List<String> tags;
    private String downloadUrl;
    private String checksum;
    private AuthStatus authStatus;
    private Long downloadCount;
    private Long installCount;
    private Long updateTime;
}

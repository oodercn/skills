package net.ooder.skill.scene.dto.discovery;

import javax.validation.constraints.NotBlank;

public class InstallSkillRequestDTO {
    
    @NotBlank(message = "skillId不能为空")
    private String skillId;
    
    private String name;
    
    private String type;
    
    private String description;
    
    private String source;
    
    private String repoUrl;

    public InstallSkillRequestDTO() {}

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }
}

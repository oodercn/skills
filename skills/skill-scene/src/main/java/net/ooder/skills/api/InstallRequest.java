package net.ooder.skills.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InstallRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String skillId;
    private InstallMode mode;
    private String version;
    private String source;
    private String repoUrl;

    public InstallRequest() {
        this.mode = InstallMode.FULL_INSTALL;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public InstallMode getMode() {
        return mode;
    }

    public void setMode(InstallMode mode) {
        this.mode = mode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public enum InstallMode {
        FULL_INSTALL,
        DEPENDENCIES_ONLY,
        MINIMAL,
        TOPOLOGICAL
    }
}

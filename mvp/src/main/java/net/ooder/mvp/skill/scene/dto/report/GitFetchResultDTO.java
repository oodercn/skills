package net.ooder.mvp.skill.scene.dto.report;

import lombok.Data;
import java.util.List;

@Data
public class GitFetchResultDTO {
    private String userId;
    private String repoUrl;
    private List<CommitItem> commits;
    private String summary;
    private List<String> workItems;

    @Data
    public static class CommitItem {
        private String commitId;
        private String message;
        private String branch;
        private Long time;
        private Integer filesChanged;
    }
}

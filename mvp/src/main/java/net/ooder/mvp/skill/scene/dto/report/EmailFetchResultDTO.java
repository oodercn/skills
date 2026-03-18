package net.ooder.mvp.skill.scene.dto.report;

import lombok.Data;
import java.util.List;

@Data
public class EmailFetchResultDTO {
    private String userId;
    private List<EmailItem> emails;
    private String summary;
    private List<String> workItems;

    @Data
    public static class EmailItem {
        private String subject;
        private String from;
        private Long time;
        private String summary;
    }
}

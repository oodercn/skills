package net.ooder.skill.calendar.dto;

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
public class SyncResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String platform;
    private Boolean success;
    private Integer totalEvents;
    private Integer createdEvents;
    private Integer updatedEvents;
    private Integer deletedEvents;
    private Integer failedEvents;
    private List<String> errors;
    private String syncTime;
}

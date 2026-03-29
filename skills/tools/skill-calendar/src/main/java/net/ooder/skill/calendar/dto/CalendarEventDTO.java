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
public class CalendarEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private String title;
    private String description;
    private String location;
    private String startTime;
    private String endTime;
    private String timeZone;
    private String eventType;
    private String status;
    private String organizer;
    private List<String> attendees;
    private List<String> reminders;
    private String platform;
    private String platformEventId;
    private String createTime;
    private String updateTime;
    private Boolean allDay;
    private String recurrence;
}

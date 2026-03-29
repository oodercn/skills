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
public class MeetingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String meetingId;
    private String title;
    private String description;
    private String startTime;
    private String endTime;
    private String organizer;
    private List<AttendeeDTO> attendees;
    private String location;
    private String meetingUrl;
    private String platform;
    private String platformMeetingId;
    private String status;
    private String meetingType;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AttendeeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String userName;
    private String email;
    private String status;
    private Boolean required;
}

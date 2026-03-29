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
public class FreeTimeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String date;
    private List<TimeSlotDTO> freeSlots;
    private List<TimeSlotDTO> busySlots;
    private String platform;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TimeSlotDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String startTime;
    private String endTime;
    private String status;
    private String eventId;
    private String eventTitle;
}

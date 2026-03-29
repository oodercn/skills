package net.ooder.skill.calendar.service;

import net.ooder.skill.calendar.dto.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service
public class DingTalkCalendarService {

    public CalendarEventDTO createEvent(CalendarEventDTO event) {
        log.info("DingTalk: Creating event {}", event.getTitle());
        event.setPlatformEventId("dingtalk_event_" + System.currentTimeMillis());
        return event;
    }

    public CalendarEventDTO updateEvent(CalendarEventDTO event) {
        log.info("DingTalk: Updating event {}", event.getEventId());
        return event;
    }

    public boolean deleteEvent(String eventId) {
        log.info("DingTalk: Deleting event {}", eventId);
        return true;
    }

    public MeetingDTO scheduleMeeting(MeetingDTO meeting) {
        log.info("DingTalk: Scheduling meeting {}", meeting.getTitle());
        meeting.setPlatformMeetingId("dingtalk_meeting_" + System.currentTimeMillis());
        meeting.setMeetingUrl("https://meeting.dingtalk.com/join/" + meeting.getPlatformMeetingId());
        return meeting;
    }

    public SyncResultDTO syncCalendar(String userId) {
        log.info("DingTalk: Syncing calendar for user {}", userId);
        return SyncResultDTO.builder()
                .platform("DINGTALK")
                .success(true)
                .totalEvents(10)
                .createdEvents(2)
                .updatedEvents(3)
                .deletedEvents(0)
                .failedEvents(0)
                .errors(new ArrayList<>())
                .build();
    }
}

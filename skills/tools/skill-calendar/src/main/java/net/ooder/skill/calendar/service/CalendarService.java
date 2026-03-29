package net.ooder.skill.calendar.service;

import net.ooder.skill.calendar.dto.*;
import net.ooder.skill.calendar.dict.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class CalendarService {

    @Autowired
    private DingTalkCalendarService dingTalkCalendarService;

    @Autowired
    private FeishuCalendarService feishuCalendarService;

    @Autowired
    private WeComCalendarService weComCalendarService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CalendarEventDTO createEvent(CalendarEventDTO event) {
        log.info("Creating calendar event: {}", event.getTitle());
        
        event.setEventId(UUID.randomUUID().toString());
        event.setStatus(EventStatus.CONFIRMED.getCode());
        event.setCreateTime(LocalDateTime.now().format(FORMATTER));
        event.setUpdateTime(LocalDateTime.now().format(FORMATTER));
        
        if (event.getPlatform() != null) {
            CalendarPlatform platform = CalendarPlatform.valueOf(event.getPlatform().toUpperCase());
            CalendarEventDTO platformEvent = null;
            
            switch (platform) {
                case DINGTALK:
                    platformEvent = dingTalkCalendarService.createEvent(event);
                    break;
                case FEISHU:
                    platformEvent = feishuCalendarService.createEvent(event);
                    break;
                case WECOM:
                    platformEvent = weComCalendarService.createEvent(event);
                    break;
                default:
                    break;
            }
            
            if (platformEvent != null) {
                event.setPlatformEventId(platformEvent.getPlatformEventId());
            }
        }
        
        return event;
    }

    public CalendarEventDTO getEvent(String eventId) {
        log.info("Getting calendar event: {}", eventId);
        return CalendarEventDTO.builder()
                .eventId(eventId)
                .title("示例日程")
                .description("这是一个示例日程")
                .startTime(LocalDateTime.now().plusHours(1).format(FORMATTER))
                .endTime(LocalDateTime.now().plusHours(2).format(FORMATTER))
                .status(EventStatus.CONFIRMED.getCode())
                .eventType(EventType.MEETING.getCode())
                .build();
    }

    public CalendarEventDTO updateEvent(String eventId, CalendarEventDTO event) {
        log.info("Updating calendar event: {}", eventId);
        event.setEventId(eventId);
        event.setUpdateTime(LocalDateTime.now().format(FORMATTER));
        
        if (event.getPlatform() != null && event.getPlatformEventId() != null) {
            CalendarPlatform platform = CalendarPlatform.valueOf(event.getPlatform().toUpperCase());
            switch (platform) {
                case DINGTALK:
                    dingTalkCalendarService.updateEvent(event);
                    break;
                case FEISHU:
                    feishuCalendarService.updateEvent(event);
                    break;
                case WECOM:
                    weComCalendarService.updateEvent(event);
                    break;
                default:
                    break;
            }
        }
        
        return event;
    }

    public boolean deleteEvent(String eventId, String platform) {
        log.info("Deleting calendar event: {}", eventId);
        
        if (platform != null) {
            CalendarPlatform calPlatform = CalendarPlatform.valueOf(platform.toUpperCase());
            switch (calPlatform) {
                case DINGTALK:
                    return dingTalkCalendarService.deleteEvent(eventId);
                case FEISHU:
                    return feishuCalendarService.deleteEvent(eventId);
                case WECOM:
                    return weComCalendarService.deleteEvent(eventId);
                default:
                    break;
            }
        }
        
        return true;
    }

    public FreeTimeDTO getFreeTime(String userId, String date, String platform) {
        log.info("Getting free time for user: {}, date: {}", userId, date);
        
        List<TimeSlotDTO> freeSlots = new ArrayList<>();
        freeSlots.add(TimeSlotDTO.builder()
                .startTime(date + " 09:00:00")
                .endTime(date + " 12:00:00")
                .status("FREE")
                .build());
        freeSlots.add(TimeSlotDTO.builder()
                .startTime(date + " 14:00:00")
                .endTime(date + " 18:00:00")
                .status("FREE")
                .build());
        
        return FreeTimeDTO.builder()
                .userId(userId)
                .date(date)
                .freeSlots(freeSlots)
                .platform(platform)
                .build();
    }

    public MeetingDTO scheduleMeeting(MeetingDTO meeting) {
        log.info("Scheduling meeting: {}", meeting.getTitle());
        
        meeting.setMeetingId(UUID.randomUUID().toString());
        meeting.setStatus(EventStatus.CONFIRMED.getCode());
        
        if (meeting.getPlatform() != null) {
            CalendarPlatform platform = CalendarPlatform.valueOf(meeting.getPlatform().toUpperCase());
            MeetingDTO platformMeeting = null;
            
            switch (platform) {
                case DINGTALK:
                    platformMeeting = dingTalkCalendarService.scheduleMeeting(meeting);
                    break;
                case FEISHU:
                    platformMeeting = feishuCalendarService.scheduleMeeting(meeting);
                    break;
                case WECOM:
                    platformMeeting = weComCalendarService.scheduleMeeting(meeting);
                    break;
                default:
                    break;
            }
            
            if (platformMeeting != null) {
                meeting.setPlatformMeetingId(platformMeeting.getPlatformMeetingId());
                meeting.setMeetingUrl(platformMeeting.getMeetingUrl());
            }
        }
        
        return meeting;
    }

    public SyncResultDTO syncCalendar(String userId, String platform) {
        log.info("Syncing calendar for user: {}, platform: {}", userId, platform);
        
        SyncResultDTO result = SyncResultDTO.builder()
                .platform(platform)
                .success(true)
                .syncTime(LocalDateTime.now().format(FORMATTER))
                .totalEvents(0)
                .createdEvents(0)
                .updatedEvents(0)
                .deletedEvents(0)
                .failedEvents(0)
                .errors(new ArrayList<>())
                .build();
        
        if (platform != null) {
            CalendarPlatform calPlatform = CalendarPlatform.valueOf(platform.toUpperCase());
            switch (calPlatform) {
                case DINGTALK:
                    result = dingTalkCalendarService.syncCalendar(userId);
                    break;
                case FEISHU:
                    result = feishuCalendarService.syncCalendar(userId);
                    break;
                case WECOM:
                    result = weComCalendarService.syncCalendar(userId);
                    break;
                default:
                    break;
            }
        }
        
        return result;
    }

    public List<CalendarEventDTO> listEvents(String userId, String startDate, String endDate) {
        log.info("Listing events for user: {}, from {} to {}", userId, startDate, endDate);
        
        List<CalendarEventDTO> events = new ArrayList<>();
        events.add(CalendarEventDTO.builder()
                .eventId(UUID.randomUUID().toString())
                .title("团队周会")
                .description("每周团队例会")
                .startTime(startDate + " 10:00:00")
                .endTime(startDate + " 11:00:00")
                .eventType(EventType.MEETING.getCode())
                .status(EventStatus.CONFIRMED.getCode())
                .location("会议室A")
                .build());
        
        return events;
    }
}

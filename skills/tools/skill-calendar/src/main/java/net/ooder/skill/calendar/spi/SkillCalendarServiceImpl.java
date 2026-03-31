package net.ooder.skill.calendar.spi;

import net.ooder.skill.common.spi.CalendarService;
import net.ooder.skill.common.spi.calendar.EventInfo;
import net.ooder.skill.common.spi.calendar.TimeSlot;
import net.ooder.skill.calendar.dto.CalendarEventDTO;
import net.ooder.skill.calendar.dto.FreeTimeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@ConditionalOnProperty(name = "skill.calendar.enabled", havingValue = "true", matchIfMissing = false)
public class SkillCalendarServiceImpl implements CalendarService {
    
    private static final Logger log = LoggerFactory.getLogger(SkillCalendarServiceImpl.class);
    
    @Autowired
    private net.ooder.skill.calendar.service.CalendarService calendarService;
    
    @Override
    public EventInfo createEvent(EventInfo event) {
        log.info("[createEvent] title={}", event.getTitle());
        try {
            CalendarEventDTO dto = convertToDTO(event);
            CalendarEventDTO created = calendarService.createEvent(dto);
            return convertFromDTO(created);
        } catch (Exception e) {
            log.error("[createEvent] Failed to create event", e);
            return null;
        }
    }
    
    @Override
    public EventInfo getEvent(String eventId) {
        log.info("[getEvent] eventId={}", eventId);
        try {
            CalendarEventDTO dto = calendarService.getEvent(eventId);
            return dto != null ? convertFromDTO(dto) : null;
        } catch (Exception e) {
            log.error("[getEvent] Failed to get event", e);
            return null;
        }
    }
    
    @Override
    public EventInfo updateEvent(EventInfo event) {
        log.info("[updateEvent] eventId={}", event.getEventId());
        try {
            CalendarEventDTO dto = convertToDTO(event);
            CalendarEventDTO updated = calendarService.updateEvent(dto);
            return convertFromDTO(updated);
        } catch (Exception e) {
            log.error("[updateEvent] Failed to update event", e);
            return null;
        }
    }
    
    @Override
    public void deleteEvent(String eventId) {
        log.info("[deleteEvent] eventId={}", eventId);
        try {
            calendarService.deleteEvent(eventId);
        } catch (Exception e) {
            log.error("[deleteEvent] Failed to delete event", e);
        }
    }
    
    @Override
    public List<EventInfo> listEvents(String userId, long startTime, long endTime) {
        log.info("[listEvents] userId={}, startTime={}, endTime={}", userId, startTime, endTime);
        try {
            List<CalendarEventDTO> events = calendarService.listEvents(userId, startTime, endTime);
            List<EventInfo> result = new ArrayList<>();
            for (CalendarEventDTO dto : events) {
                result.add(convertFromDTO(dto));
            }
            return result;
        } catch (Exception e) {
            log.error("[listEvents] Failed to list events", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<TimeSlot> findFreeTime(List<String> userIds, long startTime, long endTime) {
        log.info("[findFreeTime] userIds={}, startTime={}, endTime={}", userIds, startTime, endTime);
        try {
            List<FreeTimeDTO> slots = calendarService.findFreeTime(userIds, startTime, endTime);
            List<TimeSlot> result = new ArrayList<>();
            for (FreeTimeDTO slot : slots) {
                result.add(new TimeSlot(slot.getStartTime(), slot.getEndTime(), slot.isAvailable()));
            }
            return result;
        } catch (Exception e) {
            log.error("[findFreeTime] Failed to find free time", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public void syncFromPlatform(String platform) {
        log.info("[syncFromPlatform] platform={}", platform);
        try {
            calendarService.syncFromPlatform(platform);
        } catch (Exception e) {
            log.error("[syncFromPlatform] Failed to sync", e);
        }
    }
    
    @Override
    public List<String> getAvailablePlatforms() {
        return Arrays.asList("dingtalk", "feishu", "wecom");
    }
    
    private CalendarEventDTO convertToDTO(EventInfo info) {
        CalendarEventDTO dto = new CalendarEventDTO();
        dto.setEventId(info.getEventId());
        dto.setTitle(info.getTitle());
        dto.setDescription(info.getDescription());
        dto.setOrganizerId(info.getOrganizerId());
        dto.setAttendeeIds(info.getAttendeeIds());
        dto.setStartTime(info.getStartTime());
        dto.setEndTime(info.getEndTime());
        dto.setLocation(info.getLocation());
        dto.setPlatformSource(info.getPlatformSource());
        return dto;
    }
    
    private EventInfo convertFromDTO(CalendarEventDTO dto) {
        EventInfo info = new EventInfo();
        info.setEventId(dto.getEventId());
        info.setTitle(dto.getTitle());
        info.setDescription(dto.getDescription());
        info.setOrganizerId(dto.getOrganizerId());
        info.setAttendeeIds(dto.getAttendeeIds());
        info.setStartTime(dto.getStartTime());
        info.setEndTime(dto.getEndTime());
        info.setLocation(dto.getLocation());
        info.setPlatformSource(dto.getPlatformSource());
        return info;
    }
}

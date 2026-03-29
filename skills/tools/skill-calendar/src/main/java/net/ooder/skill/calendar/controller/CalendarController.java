package net.ooder.skill.calendar.controller;

import net.ooder.skill.calendar.dto.*;
import net.ooder.skill.calendar.service.CalendarService;
import net.ooder.api.result.ResultModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/calendar")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @PostMapping
    public ResultModel<CalendarEventDTO> createEvent(@RequestBody CalendarEventDTO event) {
        log.info("Creating calendar event: {}", event.getTitle());
        CalendarEventDTO created = calendarService.createEvent(event);
        return ResultModel.success(created);
    }

    @GetMapping("/{eventId}")
    public ResultModel<CalendarEventDTO> getEvent(@PathVariable String eventId) {
        CalendarEventDTO event = calendarService.getEvent(eventId);
        return ResultModel.success(event);
    }

    @PutMapping("/{eventId}")
    public ResultModel<CalendarEventDTO> updateEvent(
            @PathVariable String eventId,
            @RequestBody CalendarEventDTO event) {
        CalendarEventDTO updated = calendarService.updateEvent(eventId, event);
        return ResultModel.success(updated);
    }

    @DeleteMapping("/{eventId}")
    public ResultModel<Boolean> deleteEvent(
            @PathVariable String eventId,
            @RequestParam(required = false) String platform) {
        boolean result = calendarService.deleteEvent(eventId, platform);
        return ResultModel.success(result);
    }

    @GetMapping("/free")
    public ResultModel<FreeTimeDTO> getFreeTime(
            @RequestParam String userId,
            @RequestParam String date,
            @RequestParam(required = false) String platform) {
        FreeTimeDTO freeTime = calendarService.getFreeTime(userId, date, platform);
        return ResultModel.success(freeTime);
    }

    @PostMapping("/meeting")
    public ResultModel<MeetingDTO> scheduleMeeting(@RequestBody MeetingDTO meeting) {
        log.info("Scheduling meeting: {}", meeting.getTitle());
        MeetingDTO scheduled = calendarService.scheduleMeeting(meeting);
        return ResultModel.success(scheduled);
    }

    @PostMapping("/sync")
    public ResultModel<SyncResultDTO> syncCalendar(
            @RequestParam String userId,
            @RequestParam String platform) {
        SyncResultDTO result = calendarService.syncCalendar(userId, platform);
        return ResultModel.success(result);
    }

    @GetMapping("/events")
    public ResultModel<List<CalendarEventDTO>> listEvents(
            @RequestParam String userId,
            @RequestParam String startDate,
            @RequestParam(required = false) String endDate) {
        List<CalendarEventDTO> events = calendarService.listEvents(userId, startDate, endDate);
        return ResultModel.success(events);
    }
}

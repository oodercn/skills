package net.ooder.skill.meeting.minutes.controller;

import net.ooder.skill.meeting.minutes.dto.ActionItemResponse;
import net.ooder.skill.meeting.minutes.dto.ApiResponse;
import net.ooder.skill.meeting.minutes.dto.ArchiveResultResponse;
import net.ooder.skill.meeting.minutes.dto.DecisionResponse;
import net.ooder.skill.meeting.minutes.dto.ExportMinutesResponse;
import net.ooder.skill.meeting.minutes.dto.MeetingMinutesResponse;
import net.ooder.skill.meeting.minutes.dto.OrganizeMeetingRequest;
import net.ooder.skill.meeting.minutes.dto.PageResultResponse;
import net.ooder.skill.meeting.minutes.dto.UpdateMinutesRequest;
import net.ooder.skill.meeting.minutes.service.MeetingMinutesService;
import net.ooder.skill.meeting.minutes.service.MeetingMinutesService.*;
import net.ooder.skill.meeting.minutes.enums.MinutesStatus;
import net.ooder.skill.meeting.minutes.enums.ActionItemStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/meeting-minutes")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MeetingMinutesController {
    
    @Autowired
    private MeetingMinutesService meetingMinutesService;
    
    @PostMapping("/organize")
    public ResponseEntity<ApiResponse<MeetingMinutesResponse>> organizeMeeting(@RequestBody OrganizeMeetingRequest request) {
        String meetingContent = request.getMeetingContent();
        String projectId = request.getProjectId();
        List<String> participants = request.getParticipants();
        
        MeetingMinutes minutes = meetingMinutesService.organizeMeeting(meetingContent, projectId, participants);
        
        return ResponseEntity.ok(new ApiResponse<>(toMinutesResponse(minutes)));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResultResponse<MeetingMinutesResponse>>> listMinutes(
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        PageResult<MeetingMinutes> result = meetingMinutesService.listMinutes(projectId, status, keyword, pageNum, pageSize);
        
        PageResultResponse<MeetingMinutesResponse> pageResult = new PageResultResponse<>();
        pageResult.setList(result.getList().stream().map(this::toMinutesListResponse).collect(Collectors.toList()));
        pageResult.setTotal(result.getTotal());
        pageResult.setPageNum(result.getPageNum());
        pageResult.setPageSize(result.getPageSize());
        pageResult.setPages(result.getPages());
        
        return ResponseEntity.ok(new ApiResponse<>(pageResult));
    }
    
    @GetMapping("/{minutesId}")
    public ResponseEntity<ApiResponse<MeetingMinutesResponse>> getMinutes(@PathVariable String minutesId) {
        MeetingMinutes minutes = meetingMinutesService.getMinutes(minutesId);
        return ResponseEntity.ok(new ApiResponse<>(toMinutesResponse(minutes)));
    }
    
    @PutMapping("/{minutesId}")
    public ResponseEntity<ApiResponse<MeetingMinutesResponse>> updateMinutes(
            @PathVariable String minutesId,
            @RequestBody UpdateMinutesRequest request) {
        
        MeetingMinutes minutes = meetingMinutesService.updateMinutes(minutesId, request);
        return ResponseEntity.ok(new ApiResponse<>(toMinutesResponse(minutes)));
    }
    
    @PutMapping("/{minutesId}/status")
    public ResponseEntity<ApiResponse<MeetingMinutesResponse>> updateStatus(
            @PathVariable String minutesId,
            @RequestParam String status) {
        
        MinutesStatus minutesStatus = MinutesStatus.valueOf(status);
        
        MeetingMinutes minutes = meetingMinutesService.updateStatus(minutesId, minutesStatus);
        return ResponseEntity.ok(new ApiResponse<>(toMinutesResponse(minutes)));
    }
    
    @DeleteMapping("/{minutesId}")
    public ResponseEntity<ApiResponse<Void>> deleteMinutes(@PathVariable String minutesId) {
        meetingMinutesService.deleteMinutes(minutesId);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }
    
    @GetMapping("/my-actions")
    public ResponseEntity<ApiResponse<List<ActionItemResponse>>> getMyActionItems(
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String status) {
        
        List<ActionItem> items = meetingMinutesService.getMyActionItems(assignee, status);
        
        List<ActionItemResponse> itemList = items.stream()
            .map(this::toActionItemResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponse<>(itemList));
    }
    
    @PutMapping("/actions/{actionId}/status")
    public ResponseEntity<ApiResponse<ActionItemResponse>> updateActionStatus(
            @PathVariable String actionId,
            @RequestParam String status) {
        
        ActionItemStatus actionStatus = ActionItemStatus.valueOf(status);
        
        ActionItem item = meetingMinutesService.updateActionStatus(actionId, actionStatus);
        return ResponseEntity.ok(new ApiResponse<>(toActionItemResponse(item)));
    }
    
    @PostMapping("/action-items")
    public ResponseEntity<ApiResponse<List<ActionItemResponse>>> extractActionItems(@RequestParam String meetingContent) {
        List<ActionItem> items = meetingMinutesService.extractActionItems(meetingContent);
        
        List<ActionItemResponse> itemList = items.stream()
            .map(this::toActionItemResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponse<>(itemList));
    }
    
    @PostMapping("/archive")
    public ResponseEntity<ApiResponse<ArchiveResultResponse>> archiveToKb(
            @RequestParam String minutesId,
            @RequestParam String projectId,
            @RequestParam String kbId) {
        
        MeetingMinutes minutes = new MeetingMinutes();
        minutes.setMinutesId(minutesId);
        
        ArchiveResult result = meetingMinutesService.archiveToKb(minutes, projectId, kbId);
        
        ArchiveResultResponse response = new ArchiveResultResponse();
        response.setDocId(result.getDocId());
        response.setStatus(result.getStatus());
        
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
    
    @PostMapping("/export/{format}")
    public ResponseEntity<ApiResponse<ExportMinutesResponse>> exportMinutes(
            @PathVariable String format,
            @RequestParam String minutesId) {
        
        MeetingMinutes minutes = new MeetingMinutes();
        minutes.setMinutesId(minutesId);
        
        String content = meetingMinutesService.exportMinutes(minutes, format);
        
        ExportMinutesResponse response = new ExportMinutesResponse();
        response.setFormat(format);
        response.setContent(content);
        response.setFilename("meeting-minutes." + format.toLowerCase());
        
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
    
    private MeetingMinutesResponse toMinutesListResponse(MeetingMinutes minutes) {
        MeetingMinutesResponse response = new MeetingMinutesResponse();
        response.setMinutesId(minutes.getMinutesId());
        response.setTitle(minutes.getTitle());
        response.setMeetingDate(minutes.getMeetingDate());
        response.setStatus(minutes.getStatus() != null ? minutes.getStatus().getCode() : null);
        response.setStatusName(minutes.getStatus() != null ? minutes.getStatus().getName() : null);
        response.setProjectId(minutes.getProjectId());
        response.setCreatedBy(minutes.getCreatedBy());
        response.setCreatedAt(minutes.getCreatedAt());
        return response;
    }
    
    private MeetingMinutesResponse toMinutesResponse(MeetingMinutes minutes) {
        MeetingMinutesResponse response = new MeetingMinutesResponse();
        response.setMinutesId(minutes.getMinutesId());
        response.setTitle(minutes.getTitle());
        response.setMeetingDate(minutes.getMeetingDate());
        response.setLocation(minutes.getLocation());
        response.setSummary(minutes.getSummary());
        response.setParticipants(minutes.getParticipants());
        response.setProjectId(minutes.getProjectId());
        response.setKbId(minutes.getKbId());
        response.setNextMeetingDate(minutes.getNextMeetingDate());
        response.setNextMeetingAgenda(minutes.getNextMeetingAgenda());
        response.setStatus(minutes.getStatus() != null ? minutes.getStatus().getCode() : null);
        response.setStatusName(minutes.getStatus() != null ? minutes.getStatus().getName() : null);
        response.setMeetingType(minutes.getMeetingType() != null ? minutes.getMeetingType().getCode() : null);
        response.setMeetingTypeName(minutes.getMeetingType() != null ? minutes.getMeetingType().getName() : null);
        response.setCreatedBy(minutes.getCreatedBy());
        response.setCreatedAt(minutes.getCreatedAt());
        response.setUpdatedAt(minutes.getUpdatedAt());
        
        if (minutes.getDecisionList() != null) {
            response.setDecisions(minutes.getDecisionList().stream()
                .map(this::toDecisionResponse)
                .collect(Collectors.toList()));
        }
        
        if (minutes.getActionItems() != null) {
            response.setActionItems(minutes.getActionItems().stream()
                .map(this::toActionItemResponse)
                .collect(Collectors.toList()));
        }
        
        return response;
    }
    
    private DecisionResponse toDecisionResponse(Decision decision) {
        DecisionResponse response = new DecisionResponse();
        response.setDecisionId(decision.getDecisionId());
        response.setContent(decision.getContent());
        response.setPriority(decision.getPriority());
        response.setSeq(decision.getSeq());
        return response;
    }
    
    private ActionItemResponse toActionItemResponse(ActionItem item) {
        ActionItemResponse response = new ActionItemResponse();
        response.setActionId(item.getActionId());
        response.setMinutesId(item.getMinutesId());
        response.setTask(item.getTask());
        response.setAssignee(item.getAssignee());
        response.setDeadline(item.getDeadline());
        response.setStatus(item.getStatus() != null ? item.getStatus().getCode() : null);
        response.setStatusName(item.getStatus() != null ? item.getStatus().getName() : null);
        response.setSeq(item.getSeq());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }
}

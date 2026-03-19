package net.ooder.skill.meeting.minutes.service;

import net.ooder.skill.meeting.minutes.enums.MinutesStatus;
import net.ooder.skill.meeting.minutes.enums.ActionItemStatus;
import net.ooder.skill.meeting.minutes.enums.MeetingType;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.DocumentCreateRequest;
import net.ooder.scene.skill.conversation.ConversationService;
import net.ooder.scene.skill.conversation.MessageRequest;
import net.ooder.scene.skill.conversation.MessageResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MeetingMinutesService {
    
    private static final Logger log = LoggerFactory.getLogger(MeetingMinutesService.class);
    
    @Value("${meeting.llm.model:gpt-4}")
    private String llmModel;
    
    @Value("${meeting.llm.temperature:0.3}")
    private float temperature;
    
    @Value("${meeting.llm.max-tokens:2000}")
    private int maxTokens;
    
    @Autowired(required = false)
    private ConversationService conversationService;
    
    @Autowired(required = false)
    private KnowledgeBaseService knowledgeBaseService;
    
    private final Map<String, MeetingMinutes> minutesStore = new ConcurrentHashMap<>();
    private final Map<String, ActionItem> actionItemStore = new ConcurrentHashMap<>();
    private final Map<String, Decision> decisionStore = new ConcurrentHashMap<>();
    
    public MeetingMinutes organizeMeeting(String meetingContent, String projectId, List<String> participants) {
        log.info("Organizing meeting: projectId={}, participants={}", projectId, participants);
        
        String prompt = buildOrganizePrompt(meetingContent, participants);
        
        String structuredContent = callLlm(prompt);
        
        MeetingMinutes minutes = parseMinutes(structuredContent);
        minutes.setMinutesId("mm-" + System.currentTimeMillis());
        minutes.setProjectId(projectId);
        minutes.setParticipants(participants);
        minutes.setStatus(MinutesStatus.DRAFT);
        minutes.setCreatedAt(new Date());
        
        if (minutes.getActionItems() != null) {
            for (int i = 0; i < minutes.getActionItems().size(); i++) {
                ActionItem item = minutes.getActionItems().get(i);
                item.setActionId("action-" + System.currentTimeMillis() + "-" + i);
                item.setMinutesId(minutes.getMinutesId());
                item.setStatus(ActionItemStatus.PENDING);
                item.setSeq(i + 1);
                actionItemStore.put(item.getActionId(), item);
            }
        }
        
        if (minutes.getDecisions() != null) {
            for (int i = 0; i < minutes.getDecisions().size(); i++) {
                Decision decision = minutes.getDecisions().get(i);
                decision.setDecisionId("dec-" + System.currentTimeMillis() + "-" + i);
                decision.setMinutesId(minutes.getMinutesId());
                decision.setSeq(i + 1);
                decisionStore.put(decision.getDecisionId(), decision);
            }
        }
        
        minutesStore.put(minutes.getMinutesId(), minutes);
        
        return minutes;
    }
    
    public PageResult<MeetingMinutes> listMinutes(String projectId, String status, String keyword, int pageNum, int pageSize) {
        log.info("Listing minutes: projectId={}, status={}, keyword={}", projectId, status, keyword);
        
        List<MeetingMinutes> filtered = minutesStore.values().stream()
            .filter(m -> projectId == null || projectId.equals(m.getProjectId()))
            .filter(m -> status == null || status.equals(m.getStatus().getCode()))
            .filter(m -> keyword == null || keyword.isEmpty() || 
                        (m.getTitle() != null && m.getTitle().contains(keyword)) ||
                        (m.getSummary() != null && m.getSummary().contains(keyword)))
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(Collectors.toList());
        
        int total = filtered.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<MeetingMinutes> page = start < total ? filtered.subList(start, end) : new ArrayList<>();
        
        return new PageResult<>(page, total, pageNum, pageSize);
    }
    
    public MeetingMinutes getMinutes(String minutesId) {
        log.info("Getting minutes: minutesId={}", minutesId);
        
        MeetingMinutes minutes = minutesStore.get(minutesId);
        if (minutes == null) {
            throw new RuntimeException("Meeting minutes not found: " + minutesId);
        }
        
        List<ActionItem> actions = actionItemStore.values().stream()
            .filter(a -> minutesId.equals(a.getMinutesId()))
            .sorted(Comparator.comparingInt(ActionItem::getSeq))
            .collect(Collectors.toList());
        minutes.setActionItems(actions);
        
        List<Decision> decisions = decisionStore.values().stream()
            .filter(d -> minutesId.equals(d.getMinutesId()))
            .sorted(Comparator.comparingInt(Decision::getSeq))
            .collect(Collectors.toList());
        minutes.setDecisionList(decisions);
        
        return minutes;
    }
    
    public MeetingMinutes updateMinutes(String minutesId, Map<String, Object> updates) {
        log.info("Updating minutes: minutesId={}", minutesId);
        
        MeetingMinutes minutes = minutesStore.get(minutesId);
        if (minutes == null) {
            throw new RuntimeException("Meeting minutes not found: " + minutesId);
        }
        
        if (updates.containsKey("title")) {
            minutes.setTitle((String) updates.get("title"));
        }
        if (updates.containsKey("summary")) {
            minutes.setSummary((String) updates.get("summary"));
        }
        if (updates.containsKey("meetingDate")) {
            minutes.setMeetingDate((String) updates.get("meetingDate"));
        }
        if (updates.containsKey("location")) {
            minutes.setLocation((String) updates.get("location"));
        }
        if (updates.containsKey("meetingType")) {
            minutes.setMeetingType(MeetingType.valueOf((String) updates.get("meetingType")));
        }
        
        minutes.setUpdatedAt(new Date());
        minutesStore.put(minutesId, minutes);
        
        return minutes;
    }
    
    public MeetingMinutes updateStatus(String minutesId, MinutesStatus status) {
        log.info("Updating minutes status: minutesId={}, status={}", minutesId, status);
        
        MeetingMinutes minutes = minutesStore.get(minutesId);
        if (minutes == null) {
            throw new RuntimeException("Meeting minutes not found: " + minutesId);
        }
        
        minutes.setStatus(status);
        minutes.setUpdatedAt(new Date());
        minutesStore.put(minutesId, minutes);
        
        return minutes;
    }
    
    public void deleteMinutes(String minutesId) {
        log.info("Deleting minutes: minutesId={}", minutesId);
        
        MeetingMinutes minutes = minutesStore.get(minutesId);
        if (minutes == null) {
            throw new RuntimeException("Meeting minutes not found: " + minutesId);
        }
        
        actionItemStore.entrySet().removeIf(e -> minutesId.equals(e.getValue().getMinutesId()));
        decisionStore.entrySet().removeIf(e -> minutesId.equals(e.getValue().getMinutesId()));
        minutesStore.remove(minutesId);
    }
    
    public ActionItem updateActionStatus(String actionId, ActionItemStatus status) {
        log.info("Updating action item status: actionId={}, status={}", actionId, status);
        
        ActionItem item = actionItemStore.get(actionId);
        if (item == null) {
            throw new RuntimeException("Action item not found: " + actionId);
        }
        
        item.setStatus(status);
        item.setUpdatedAt(new Date());
        actionItemStore.put(actionId, item);
        
        return item;
    }
    
    public List<ActionItem> getMyActionItems(String assignee, String status) {
        log.info("Getting my action items: assignee={}, status={}", assignee, status);
        
        return actionItemStore.values().stream()
            .filter(a -> assignee == null || assignee.equals(a.getAssignee()))
            .filter(a -> status == null || status.equals(a.getStatus().getCode()))
            .sorted(Comparator.comparing(ActionItem::getDeadline))
            .collect(Collectors.toList());
    }
    
    public List<ActionItem> extractActionItems(String meetingContent) {
        log.info("Extracting action items from meeting content");
        
        String prompt = buildActionItemsPrompt(meetingContent);
        
        String result = callLlm(prompt);
        
        return parseActionItems(result);
    }
    
    public ArchiveResult archiveToKb(MeetingMinutes minutes, String projectId, String kbId) {
        log.info("Archiving meeting minutes to KB: kbId={}", kbId);
        
        if (knowledgeBaseService == null) {
            return new ArchiveResult(null, "knowledge-base-not-available");
        }
        
        try {
            MeetingMinutes fullMinutes = getMinutes(minutes.getMinutesId());
            
            DocumentCreateRequest request = new DocumentCreateRequest();
            request.setTitle("会议纪要 - " + fullMinutes.getTitle());
            request.setContent(formatMinutesForArchive(fullMinutes));
            request.setSource(Document.SOURCE_TEXT);
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("type", "meeting-minutes");
            metadata.put("projectId", projectId);
            metadata.put("meetingDate", fullMinutes.getMeetingDate());
            metadata.put("participants", fullMinutes.getParticipants());
            request.setMetadata(metadata);
            
            Document doc = knowledgeBaseService.addDocument(kbId, request);
            
            updateStatus(minutes.getMinutesId(), MinutesStatus.ARCHIVED);
            
            return new ArchiveResult(doc.getDocId(), "archived");
        } catch (Exception e) {
            log.error("Failed to archive meeting minutes", e);
            return new ArchiveResult(null, "error: " + e.getMessage());
        }
    }
    
    public String exportMinutes(MeetingMinutes minutes, String format) {
        log.info("Exporting minutes in format: {}", format);
        
        MeetingMinutes fullMinutes = getMinutes(minutes.getMinutesId());
        
        switch (format.toLowerCase()) {
            case "markdown":
            case "md":
                return exportAsMarkdown(fullMinutes);
            case "html":
                return exportAsHtml(fullMinutes);
            case "json":
                return exportAsJson(fullMinutes);
            case "docx":
            case "word":
                return exportAsMarkdown(fullMinutes);
            default:
                return exportAsMarkdown(fullMinutes);
        }
    }
    
    private String buildOrganizePrompt(String content, List<String> participants) {
        return String.format(
            "请将以下会议内容整理成结构化的会议纪要：\n\n" +
            "参与人：%s\n\n" +
            "会议内容：\n%s\n\n" +
            "请按以下JSON格式输出：\n" +
            "{\n" +
            "  \"title\": \"会议主题\",\n" +
            "  \"meetingDate\": \"会议日期\",\n" +
            "  \"location\": \"会议地点\",\n" +
            "  \"summary\": \"会议摘要\",\n" +
            "  \"decisions\": [{\"content\": \"决策内容\", \"priority\": \"HIGH/MEDIUM/LOW\"}],\n" +
            "  \"actionItems\": [\n" +
            "    {\"task\": \"任务描述\", \"assignee\": \"责任人\", \"deadline\": \"截止时间\"}\n" +
            "  ],\n" +
            "  \"nextMeeting\": {\n" +
            "    \"date\": \"下次会议日期\",\n" +
            "    \"agenda\": \"下次会议议题\"\n" +
            "  }\n" +
            "}",
            participants != null ? String.join(", ", participants) : "未指定",
            content
        );
    }
    
    private String buildActionItemsPrompt(String content) {
        return String.format(
            "从以下会议内容中提取行动项，按JSON数组格式输出：\n\n" +
            "会议内容：\n%s\n\n" +
            "格式：\n" +
            "[\n" +
            "  {\"task\": \"任务描述\", \"assignee\": \"责任人\", \"deadline\": \"截止时间\"}\n" +
            "]",
            content
        );
    }
    
    private String callLlm(String prompt) {
        if (conversationService == null) {
            log.warn("ConversationService not available, returning mock response");
            return generateMockResponse(prompt);
        }
        
        try {
            MessageRequest request = new MessageRequest();
            request.setContent(prompt);
            
            MessageResponse response = conversationService.sendMessage(null, request);
            return response.getContent();
        } catch (Exception e) {
            log.error("Failed to call LLM", e);
            return generateMockResponse(prompt);
        }
    }
    
    private String generateMockResponse(String prompt) {
        return "{\n" +
               "  \"title\": \"项目进度讨论会\",\n" +
               "  \"meetingDate\": \"2026-03-07\",\n" +
               "  \"location\": \"会议室A\",\n" +
               "  \"summary\": \"本次会议讨论了项目进度和下一步计划\",\n" +
               "  \"decisions\": [\n" +
               "    {\"content\": \"决定增加开发资源\", \"priority\": \"HIGH\"},\n" +
               "    {\"content\": \"确定下周发布版本\", \"priority\": \"MEDIUM\"}\n" +
               "  ],\n" +
               "  \"actionItems\": [\n" +
               "    {\"task\": \"完成API开发\", \"assignee\": \"张三\", \"deadline\": \"2026-03-10\"},\n" +
               "    {\"task\": \"编写测试用例\", \"assignee\": \"李四\", \"deadline\": \"2026-03-12\"}\n" +
               "  ],\n" +
               "  \"nextMeeting\": {\n" +
               "    \"date\": \"2026-03-13\",\n" +
               "    \"agenda\": \"版本发布评审\"\n" +
               "  }\n" +
               "}";
    }
    
    @SuppressWarnings("unchecked")
    private MeetingMinutes parseMinutes(String content) {
        MeetingMinutes minutes = new MeetingMinutes();
        
        try {
            Map<String, Object> map = parseJsonToMap(content);
            
            minutes.setTitle((String) map.getOrDefault("title", "未命名会议"));
            minutes.setMeetingDate((String) map.get("meetingDate"));
            minutes.setLocation((String) map.get("location"));
            minutes.setSummary((String) map.get("summary"));
            
            if (map.get("decisions") instanceof List) {
                List<Decision> decisions = new ArrayList<>();
                List<?> decisionList = (List<?>) map.get("decisions");
                for (Object item : decisionList) {
                    if (item instanceof Map) {
                        Map<String, Object> itemMap = (Map<String, Object>) item;
                        Decision decision = new Decision();
                        decision.setContent((String) itemMap.get("content"));
                        decision.setPriority((String) itemMap.getOrDefault("priority", "MEDIUM"));
                        decisions.add(decision);
                    } else if (item instanceof String) {
                        Decision decision = new Decision();
                        decision.setContent((String) item);
                        decision.setPriority("MEDIUM");
                        decisions.add(decision);
                    }
                }
                minutes.setDecisionList(decisions);
            }
            
            if (map.get("actionItems") instanceof List) {
                List<ActionItem> items = new ArrayList<>();
                List<?> actionList = (List<?>) map.get("actionItems");
                for (Object item : actionList) {
                    if (item instanceof Map) {
                        Map<String, Object> itemMap = (Map<String, Object>) item;
                        ActionItem actionItem = new ActionItem();
                        actionItem.setTask((String) itemMap.get("task"));
                        actionItem.setAssignee((String) itemMap.get("assignee"));
                        actionItem.setDeadline((String) itemMap.get("deadline"));
                        items.add(actionItem);
                    }
                }
                minutes.setActionItems(items);
            }
            
            if (map.get("nextMeeting") instanceof Map) {
                Map<String, String> next = (Map<String, String>) map.get("nextMeeting");
                minutes.setNextMeetingDate(next.get("date"));
                minutes.setNextMeetingAgenda(next.get("agenda"));
            }
        } catch (Exception e) {
            log.error("Failed to parse minutes", e);
            minutes.setTitle("解析失败");
            minutes.setSummary(content);
        }
        
        return minutes;
    }
    
    private List<ActionItem> parseActionItems(String content) {
        List<ActionItem> items = new ArrayList<>();
        
        try {
            Object parsed = parseJson(content);
            if (parsed instanceof List) {
                for (Object item : (List<?>) parsed) {
                    if (item instanceof Map) {
                        Map<String, Object> itemMap = (Map<String, Object>) item;
                        ActionItem actionItem = new ActionItem();
                        actionItem.setTask((String) itemMap.get("task"));
                        actionItem.setAssignee((String) itemMap.get("assignee"));
                        actionItem.setDeadline((String) itemMap.get("deadline"));
                        items.add(actionItem);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse action items", e);
        }
        
        return items;
    }
    
    @SuppressWarnings("unchecked")
    private Object parseJson(String content) throws Exception {
        return new org.json.simple.parser.JSONParser().parse(content);
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonToMap(String content) throws Exception {
        Object parsed = parseJson(content);
        if (parsed instanceof Map) {
            return (Map<String, Object>) parsed;
        }
        return new HashMap<>();
    }
    
    private String formatMinutesForArchive(MeetingMinutes minutes) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(minutes.getTitle()).append("\n\n");
        sb.append("**日期**: ").append(minutes.getMeetingDate()).append("\n");
        sb.append("**地点**: ").append(minutes.getLocation()).append("\n");
        sb.append("**参与人**: ").append(minutes.getParticipants()).append("\n");
        sb.append("**状态**: ").append(minutes.getStatus().getName()).append("\n\n");
        
        sb.append("## 会议摘要\n").append(minutes.getSummary()).append("\n\n");
        
        if (minutes.getDecisionList() != null && !minutes.getDecisionList().isEmpty()) {
            sb.append("## 关键决策\n");
            for (int i = 0; i < minutes.getDecisionList().size(); i++) {
                Decision d = minutes.getDecisionList().get(i);
                sb.append((i + 1)).append(". ").append(d.getContent());
                if (d.getPriority() != null) {
                    sb.append(" [").append(d.getPriority()).append("]");
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        
        if (minutes.getActionItems() != null && !minutes.getActionItems().isEmpty()) {
            sb.append("## 行动项\n");
            sb.append("| 序号 | 任务 | 责任人 | 截止时间 | 状态 |\n");
            sb.append("|------|------|--------|----------|------|\n");
            for (int i = 0; i < minutes.getActionItems().size(); i++) {
                ActionItem item = minutes.getActionItems().get(i);
                sb.append("| ").append(i + 1).append(" | ")
                  .append(item.getTask()).append(" | ")
                  .append(item.getAssignee()).append(" | ")
                  .append(item.getDeadline()).append(" | ")
                  .append(item.getStatus().getName()).append(" |\n");
            }
        }
        
        if (minutes.getNextMeetingDate() != null) {
            sb.append("\n## 下次会议\n");
            sb.append("**日期**: ").append(minutes.getNextMeetingDate()).append("\n");
            sb.append("**议题**: ").append(minutes.getNextMeetingAgenda()).append("\n");
        }
        
        return sb.toString();
    }
    
    private String exportAsMarkdown(MeetingMinutes minutes) {
        return formatMinutesForArchive(minutes);
    }
    
    private String exportAsHtml(MeetingMinutes minutes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>")
          .append(minutes.getTitle()).append("</title></head><body>");
        sb.append("<h1>").append(minutes.getTitle()).append("</h1>");
        sb.append("<p><strong>日期:</strong> ").append(minutes.getMeetingDate()).append("</p>");
        sb.append("<p><strong>地点:</strong> ").append(minutes.getLocation()).append("</p>");
        sb.append("<p><strong>摘要:</strong> ").append(minutes.getSummary()).append("</p>");
        
        if (minutes.getDecisionList() != null && !minutes.getDecisionList().isEmpty()) {
            sb.append("<h2>关键决策</h2><ol>");
            for (Decision d : minutes.getDecisionList()) {
                sb.append("<li>").append(d.getContent()).append("</li>");
            }
            sb.append("</ol>");
        }
        
        if (minutes.getActionItems() != null && !minutes.getActionItems().isEmpty()) {
            sb.append("<h2>行动项</h2><table border=\"1\"><tr><th>任务</th><th>责任人</th><th>截止时间</th><th>状态</th></tr>");
            for (ActionItem item : minutes.getActionItems()) {
                sb.append("<tr><td>").append(item.getTask()).append("</td>")
                  .append("<td>").append(item.getAssignee()).append("</td>")
                  .append("<td>").append(item.getDeadline()).append("</td>")
                  .append("<td>").append(item.getStatus().getName()).append("</td></tr>");
            }
            sb.append("</table>");
        }
        
        sb.append("</body></html>");
        return sb.toString();
    }
    
    private String exportAsJson(MeetingMinutes minutes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"minutesId\":\"").append(minutes.getMinutesId()).append("\",");
        sb.append("\"title\":\"").append(escapeJson(minutes.getTitle())).append("\",");
        sb.append("\"meetingDate\":\"").append(minutes.getMeetingDate()).append("\",");
        sb.append("\"summary\":\"").append(escapeJson(minutes.getSummary())).append("\",");
        sb.append("\"status\":\"").append(minutes.getStatus().getCode()).append("\",");
        sb.append("\"actionItems\":[");
        if (minutes.getActionItems() != null) {
            for (int i = 0; i < minutes.getActionItems().size(); i++) {
                ActionItem item = minutes.getActionItems().get(i);
                if (i > 0) sb.append(",");
                sb.append("{\"task\":\"").append(escapeJson(item.getTask())).append("\",");
                sb.append("\"assignee\":\"").append(item.getAssignee()).append("\",");
                sb.append("\"deadline\":\"").append(item.getDeadline()).append("\",");
                sb.append("\"status\":\"").append(item.getStatus().getCode()).append("\"}");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
    
    public static class MeetingMinutes {
        private String minutesId;
        private String title;
        private String meetingDate;
        private String location;
        private String summary;
        private List<Decision> decisionList;
        private List<ActionItem> actionItems;
        private List<String> participants;
        private String projectId;
        private String kbId;
        private String nextMeetingDate;
        private String nextMeetingAgenda;
        private MinutesStatus status;
        private MeetingType meetingType;
        private String createdBy;
        private Date createdAt;
        private Date updatedAt;
        
        public String getMinutesId() { return minutesId; }
        public void setMinutesId(String minutesId) { this.minutesId = minutesId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMeetingDate() { return meetingDate; }
        public void setMeetingDate(String meetingDate) { this.meetingDate = meetingDate; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public List<Decision> getDecisionList() { return decisionList; }
        public void setDecisionList(List<Decision> decisionList) { this.decisionList = decisionList; }
        public List<ActionItem> getActionItems() { return actionItems; }
        public void setActionItems(List<ActionItem> actionItems) { this.actionItems = actionItems; }
        public List<String> getParticipants() { return participants; }
        public void setParticipants(List<String> participants) { this.participants = participants; }
        public String getProjectId() { return projectId; }
        public void setProjectId(String projectId) { this.projectId = projectId; }
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        public String getNextMeetingDate() { return nextMeetingDate; }
        public void setNextMeetingDate(String nextMeetingDate) { this.nextMeetingDate = nextMeetingDate; }
        public String getNextMeetingAgenda() { return nextMeetingAgenda; }
        public void setNextMeetingAgenda(String nextMeetingAgenda) { this.nextMeetingAgenda = nextMeetingAgenda; }
        public MinutesStatus getStatus() { return status; }
        public void setStatus(MinutesStatus status) { this.status = status; }
        public MeetingType getMeetingType() { return meetingType; }
        public void setMeetingType(MeetingType meetingType) { this.meetingType = meetingType; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        public Date getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    }
    
    public static class Decision {
        private String decisionId;
        private String minutesId;
        private String content;
        private String priority;
        private int seq;
        
        public String getDecisionId() { return decisionId; }
        public void setDecisionId(String decisionId) { this.decisionId = decisionId; }
        public String getMinutesId() { return minutesId; }
        public void setMinutesId(String minutesId) { this.minutesId = minutesId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public int getSeq() { return seq; }
        public void setSeq(int seq) { this.seq = seq; }
    }
    
    public static class ActionItem {
        private String actionId;
        private String minutesId;
        private String task;
        private String assignee;
        private String deadline;
        private ActionItemStatus status;
        private int seq;
        private Date updatedAt;
        
        public String getActionId() { return actionId; }
        public void setActionId(String actionId) { this.actionId = actionId; }
        public String getMinutesId() { return minutesId; }
        public void setMinutesId(String minutesId) { this.minutesId = minutesId; }
        public String getTask() { return task; }
        public void setTask(String task) { this.task = task; }
        public String getAssignee() { return assignee; }
        public void setAssignee(String assignee) { this.assignee = assignee; }
        public String getDeadline() { return deadline; }
        public void setDeadline(String deadline) { this.deadline = deadline; }
        public ActionItemStatus getStatus() { return status; }
        public void setStatus(ActionItemStatus status) { this.status = status; }
        public int getSeq() { return seq; }
        public void setSeq(int seq) { this.seq = seq; }
        public Date getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    }
    
    public static class ArchiveResult {
        private String docId;
        private String status;
        
        public ArchiveResult(String docId, String status) {
            this.docId = docId;
            this.status = status;
        }
        
        public String getDocId() { return docId; }
        public String getStatus() { return status; }
    }
    
    public static class PageResult<T> {
        private List<T> list;
        private int total;
        private int pageNum;
        private int pageSize;
        
        public PageResult(List<T> list, int total, int pageNum, int pageSize) {
            this.list = list;
            this.total = total;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
        }
        
        public List<T> getList() { return list; }
        public int getTotal() { return total; }
        public int getPageNum() { return pageNum; }
        public int getPageSize() { return pageSize; }
        public int getPages() { return (total + pageSize - 1) / pageSize; }
    }
}

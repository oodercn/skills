package net.ooder.skill.agent.cli.service;

import net.ooder.skill.agent.cli.dto.*;
import net.ooder.skill.agent.cli.dict.CliPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service
public class DingTalkCliService {

    @Autowired
    private net.ooder.skill.im.dingding.service.DingTalkMessageService messageService;

    @Autowired
    private net.ooder.skill.org.dingding.service.DingTalkOrgSyncService orgSyncService;

    private static final List<CliSkillDTO> SKILLS = Arrays.asList(
            CliSkillDTO.builder()
                    .skillId("dingtalk-send-message")
                    .name("发送消息")
                    .description("发送钉钉消息给指定用户或群组")
                    .platform("DINGTALK")
                    .category("messaging")
                    .commands(Arrays.asList("send-message", "msg"))
                    .examples(Arrays.asList("发送消息给张三", "给研发群发送消息"))
                    .icon("ri-message-2-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("dingtalk-send-ding")
                    .name("发送DING消息")
                    .description("发送高优先级DING消息")
                    .platform("DINGTALK")
                    .category("messaging")
                    .commands(Arrays.asList("send-ding", "ding"))
                    .examples(Arrays.asList("DING张三", "发送DING消息"))
                    .icon("ri-notification-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("dingtalk-query-calendar")
                    .name("查询日程")
                    .description("查询钉钉日程")
                    .platform("DINGTALK")
                    .category("calendar")
                    .commands(Arrays.asList("query-calendar", "calendar"))
                    .examples(Arrays.asList("查询今天的日程", "查看本周日程"))
                    .icon("ri-calendar-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("dingtalk-create-todo")
                    .name("创建待办")
                    .description("创建钉钉待办任务")
                    .platform("DINGTALK")
                    .category("todo")
                    .commands(Arrays.asList("create-todo", "todo"))
                    .examples(Arrays.asList("创建待办", "添加待办任务"))
                    .icon("ri-checkbox-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("dingtalk-sync-org")
                    .name("同步组织")
                    .description("同步钉钉组织架构")
                    .platform("DINGTALK")
                    .category("organization")
                    .commands(Arrays.asList("sync-org", "org"))
                    .examples(Arrays.asList("同步组织架构", "同步钉钉组织"))
                    .icon("ri-team-line")
                    .build()
    );

    public CliCommandDTO execute(CliCommandDTO command) {
        log.info("DingTalk CLI: Executing command {}", command.getCommand());

        try {
            String cmd = command.getCommand();
            Map<String, Object> args = command.getArgs() != null ? command.getOptions() : new HashMap<>();

            switch (cmd) {
                case "send-message":
                case "msg":
                    return executeSendMessage(command);
                case "send-ding":
                case "ding":
                    return executeSendDing(command);
                case "sync-org":
                case "org":
                    return executeSyncOrg(command);
                case "query-calendar":
                case "calendar":
                    return executeQueryCalendar(command);
                case "create-todo":
                case "todo":
                    return executeCreateTodo(command);
                default:
                    command.setOutput("未知命令: " + cmd);
                    command.setStatus("FAILED");
                    return command;
            }
        } catch (Exception e) {
            log.error("Command execution failed", e);
            command.setOutput("执行失败: " + e.getMessage());
            command.setStatus("FAILED");
            return command;
        }
    }

    private CliCommandDTO executeSendMessage(CliCommandDTO command) {
        Map<String, Object> options = command.getOptions() != null ? command.getOptions() : new HashMap<>();

        String receiverId = (String) options.get("receiverId");
        String content = (String) options.get("content");
        String msgType = (String) options.getOrDefault("msgType", "text");

        if (receiverId == null || content == null) {
            command.setOutput("参数错误: 需要 receiverId 和 content");
            command.setStatus("FAILED");
            return command;
        }

        net.ooder.skill.im.dingding.dto.MessageDTO message = new net.ooder.skill.im.dingding.dto.MessageDTO();
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setMsgType(msgType);
        message.setReceiver("user");

        net.ooder.skill.im.dingding.dto.SendResultDTO result = messageService.sendMessage(message);

        if (result.isSuccess()) {
            command.setOutput("消息发送成功，消息ID: " + result.getMessageId());
            command.setStatus("SUCCESS");
        } else {
            command.setOutput("消息发送失败: " + result.getErrorMessage());
            command.setStatus("FAILED");
        }
        return command;
    }

    private CliCommandDTO executeSendDing(CliCommandDTO command) {
        Map<String, Object> options = command.getOptions() != null ? command.getOptions() : new HashMap<>();

        String userId = (String) options.get("userId");
        String content = (String) options.get("content");

        if (userId == null || content == null) {
            command.setOutput("参数错误: 需要 userId 和 content");
            command.setStatus("FAILED");
            return command;
        }

        net.ooder.skill.im.dingding.dto.DingMessageDTO ding = new net.ooder.skill.im.dingding.dto.DingMessageDTO();
        ding.setUserId(userId);
        ding.setContent(content);
        ding.setReminderType(1);

        net.ooder.skill.im.dingding.dto.SendResultDTO result = messageService.sendDing(ding);

        if (result.isSuccess()) {
            command.setOutput("DING发送成功，DING ID: " + result.getMessageId());
            command.setStatus("SUCCESS");
        } else {
            command.setOutput("DING发送失败: " + result.getErrorMessage());
            command.setStatus("FAILED");
        }
        return command;
    }

    private CliCommandDTO executeSyncOrg(CliCommandDTO command) {
        net.ooder.skill.org.dingding.dto.SyncResultDTO result = orgSyncService.syncAll();

        if (result.isSuccess()) {
            command.setOutput(String.format("组织同步成功，部门数: %d，用户数: %d",
                    result.getTotalDepartments(), result.getTotalUsers()));
            command.setStatus("SUCCESS");
        } else {
            command.setOutput("组织同步失败: " + result.getMessage());
            command.setStatus("FAILED");
        }
        return command;
    }

    private CliCommandDTO executeQueryCalendar(CliCommandDTO command) {
        // TODO: 需要接入钉钉日历API
        command.setOutput("日程查询功能待实现（需要接入钉钉日历API）");
        command.setStatus("SUCCESS");
        return command;
    }

    private CliCommandDTO executeCreateTodo(CliCommandDTO command) {
        // TODO: 需要接入钉钉待办API
        command.setOutput("待办创建功能待实现（需要接入钉钉待办API）");
        command.setStatus("SUCCESS");
        return command;
    }

    public List<CliSkillDTO> listSkills() {
        return SKILLS;
    }

    public CliSkillDTO getSkill(String skillId) {
        return SKILLS.stream()
                .filter(s -> s.getSkillId().equals(skillId))
                .findFirst()
                .orElse(null);
    }
}

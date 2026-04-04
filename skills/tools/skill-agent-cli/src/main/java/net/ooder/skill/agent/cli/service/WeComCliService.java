package net.ooder.skill.agent.cli.service;

import net.ooder.skill.agent.cli.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service
public class WeComCliService {

    @Autowired
    private net.ooder.skill.im.wecom.service.WeComMessageService messageService;

    @Autowired
    private net.ooder.skill.org.wecom.service.WeComOrgSyncService orgSyncService;

    private static final List<CliSkillDTO> SKILLS = Arrays.asList(
            CliSkillDTO.builder()
                    .skillId("wecom-send-message")
                    .name("发送消息")
                    .description("发送企业微信消息给指定用户、部门或标签")
                    .platform("WECOM")
                    .category("messaging")
                    .commands(Arrays.asList("send-message", "msg"))
                    .examples(Arrays.asList("发送企业微信消息给张三", "发送消息给市场部"))
                    .icon("ri-wechat-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("wecom-send-group")
                    .name("发送群消息")
                    .description("发送企业微信群消息")
                    .platform("WECOM")
                    .category("messaging")
                    .commands(Arrays.asList("send-group", "group"))
                    .examples(Arrays.asList("发送群消息", "发送到群组"))
                    .icon("ri-group-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("wecom-query-calendar")
                    .name("查询日程")
                    .description("查询企业微信日程")
                    .platform("WECOM")
                    .category("calendar")
                    .commands(Arrays.asList("query-calendar", "calendar"))
                    .examples(Arrays.asList("查询企业微信日程"))
                    .icon("ri-calendar-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("wecom-create-todo")
                    .name("创建待办")
                    .description("创建企业微信待办")
                    .platform("WECOM")
                    .category("todo")
                    .commands(Arrays.asList("create-todo", "todo"))
                    .examples(Arrays.asList("创建企业微信待办"))
                    .icon("ri-checkbox-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("wecom-sync-org")
                    .name("同步组织")
                    .description("同步企业微信组织架构")
                    .platform("WECOM")
                    .category("organization")
                    .commands(Arrays.asList("sync-org", "org"))
                    .examples(Arrays.asList("同步组织架构", "同步企业微信组织"))
                    .icon("ri-team-line")
                    .build()
    );

    public CliCommandDTO execute(CliCommandDTO command) {
        log.info("WeCom CLI: Executing command {}", command.getCommand());

        try {
            String cmd = command.getCommand();

            switch (cmd) {
                case "send-message":
                case "msg":
                    return executeSendMessage(command);
                case "send-group":
                case "group":
                    return executeSendGroup(command);
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
        String partyId = (String) options.get("partyId");
        String tagId = (String) options.get("tagId");
        String content = (String) options.get("content");
        String msgType = (String) options.getOrDefault("msgType", "text");

        if ((receiverId == null && partyId == null && tagId == null) || content == null) {
            command.setOutput("参数错误: 需要 receiverId/partyId/tagId 之一和 content");
            command.setStatus("FAILED");
            return command;
        }

        net.ooder.skill.im.wecom.dto.MessageDTO message = new net.ooder.skill.im.wecom.dto.MessageDTO();
        message.setReceiverId(receiverId);
        message.setPartyId(partyId);
        message.setTagId(tagId);
        message.setContent(content);
        message.setMsgType(msgType);

        net.ooder.skill.im.wecom.dto.SendResultDTO result = messageService.sendMessage(message);

        if (result.isSuccess()) {
            command.setOutput("消息发送成功，消息ID: " + result.getMessageId());
            command.setStatus("SUCCESS");
        } else {
            command.setOutput("消息发送失败: " + result.getErrorMessage());
            command.setStatus("FAILED");
        }
        return command;
    }

    private CliCommandDTO executeSendGroup(CliCommandDTO command) {
        Map<String, Object> options = command.getOptions() != null ? command.getOptions() : new HashMap<>();

        String chatId = (String) options.get("chatId");
        String content = (String) options.get("content");

        if (chatId == null || content == null) {
            command.setOutput("参数错误: 需要 chatId 和 content");
            command.setStatus("FAILED");
            return command;
        }

        net.ooder.skill.im.wecom.dto.SendResultDTO result = messageService.sendToGroup(chatId, content);

        if (result.isSuccess()) {
            command.setOutput("群消息发送成功");
            command.setStatus("SUCCESS");
        } else {
            command.setOutput("群消息发送失败: " + result.getErrorMessage());
            command.setStatus("FAILED");
        }
        return command;
    }

    private CliCommandDTO executeSyncOrg(CliCommandDTO command) {
        net.ooder.skill.org.wecom.dto.SyncResultDTO result = orgSyncService.syncAll();

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
        command.setOutput("日程查询功能待实现（需要接入企业微信日历API）");
        command.setStatus("SUCCESS");
        return command;
    }

    private CliCommandDTO executeCreateTodo(CliCommandDTO command) {
        command.setOutput("待办创建功能待实现（需要接入企业微信待办API）");
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

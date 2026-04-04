package net.ooder.skill.agent.cli.service;

import net.ooder.skill.agent.cli.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service
public class FeishuCliService {

    @Autowired
    private net.ooder.skill.im.feishu.service.FeishuMessageService messageService;

    @Autowired
    private net.ooder.skill.org.feishu.service.FeishuOrgSyncService orgSyncService;

    private static final List<CliSkillDTO> SKILLS = Arrays.asList(
            CliSkillDTO.builder()
                    .skillId("feishu-send-message")
                    .name("发送消息")
                    .description("发送飞书消息给指定用户")
                    .platform("FEISHU")
                    .category("messaging")
                    .commands(Arrays.asList("send-message", "msg"))
                    .examples(Arrays.asList("发送飞书消息给张三"))
                    .icon("ri-message-3-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("feishu-send-group")
                    .name("发送群消息")
                    .description("发送飞书群消息")
                    .platform("FEISHU")
                    .category("messaging")
                    .commands(Arrays.asList("send-group", "group"))
                    .examples(Arrays.asList("发送群消息"))
                    .icon("ri-group-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("feishu-create-doc")
                    .name("创建文档")
                    .description("创建飞书文档")
                    .platform("FEISHU")
                    .category("document")
                    .commands(Arrays.asList("create-doc", "doc"))
                    .examples(Arrays.asList("创建飞书文档", "新建文档"))
                    .icon("ri-file-text-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("feishu-create-bitable")
                    .name("创建多维表格")
                    .description("创建飞书多维表格")
                    .platform("FEISHU")
                    .category("document")
                    .commands(Arrays.asList("create-bitable", "bitable"))
                    .examples(Arrays.asList("创建多维表格"))
                    .icon("ri-table-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("feishu-query-calendar")
                    .name("查询日程")
                    .description("查询飞书日程")
                    .platform("FEISHU")
                    .category("calendar")
                    .commands(Arrays.asList("query-calendar", "calendar"))
                    .examples(Arrays.asList("查询飞书日程"))
                    .icon("ri-calendar-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("feishu-create-todo")
                    .name("创建待办")
                    .description("创建飞书待办")
                    .platform("FEISHU")
                    .category("todo")
                    .commands(Arrays.asList("create-todo", "todo"))
                    .examples(Arrays.asList("创建飞书待办"))
                    .icon("ri-checkbox-line")
                    .build(),
            CliSkillDTO.builder()
                    .skillId("feishu-sync-org")
                    .name("同步组织")
                    .description("同步飞书组织架构")
                    .platform("FEISHU")
                    .category("organization")
                    .commands(Arrays.asList("sync-org", "org"))
                    .examples(Arrays.asList("同步组织架构", "同步飞书组织"))
                    .icon("ri-team-line")
                    .build()
    );

    public CliCommandDTO execute(CliCommandDTO command) {
        log.info("Feishu CLI: Executing command {}", command.getCommand());

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
                case "create-doc":
                case "doc":
                    return executeCreateDoc(command);
                case "create-bitable":
                case "bitable":
                    return executeCreateBitable(command);
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

        net.ooder.skill.im.feishu.dto.MessageDTO message = new net.ooder.skill.im.feishu.dto.MessageDTO();
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setMsgType(msgType);

        net.ooder.skill.im.feishu.dto.SendResultDTO result = messageService.sendMessage(message);

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

        net.ooder.skill.im.feishu.dto.SendResultDTO result = messageService.sendToGroup(chatId, content);

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
        net.ooder.skill.org.feishu.dto.SyncResultDTO result = orgSyncService.syncAll();

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

    private CliCommandDTO executeCreateDoc(CliCommandDTO command) {
        command.setOutput("文档创建功能待实现（需要接入飞书文档API）");
        command.setStatus("SUCCESS");
        return command;
    }

    private CliCommandDTO executeCreateBitable(CliCommandDTO command) {
        command.setOutput("多维表格创建功能待实现（需要接入飞书多维表格API）");
        command.setStatus("SUCCESS");
        return command;
    }

    private CliCommandDTO executeQueryCalendar(CliCommandDTO command) {
        command.setOutput("日程查询功能待实现（需要接入飞书日历API）");
        command.setStatus("SUCCESS");
        return command;
    }

    private CliCommandDTO executeCreateTodo(CliCommandDTO command) {
        command.setOutput("待办创建功能待实现（需要接入飞书待办API）");
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

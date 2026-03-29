package net.ooder.skill.agent.cli.service;

import net.ooder.skill.agent.cli.dto.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service
public class DingTalkCliService {

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
        
        command.setOutput("命令执行成功: " + command.getCommand());
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

package net.ooder.skill.agent.cli.service;

import net.ooder.skill.agent.cli.dto.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service
public class WeComCliService {

    private static final List<CliSkillDTO> SKILLS = Arrays.asList(
            CliSkillDTO.builder()
                    .skillId("wecom-send-message")
                    .name("发送消息")
                    .description("发送企业微信消息")
                    .platform("WECOM")
                    .category("messaging")
                    .commands(Arrays.asList("send-message", "msg"))
                    .examples(Arrays.asList("发送企业微信消息给张三"))
                    .icon("ri-wechat-line")
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
                    .build()
    );

    public CliCommandDTO execute(CliCommandDTO command) {
        log.info("WeCom CLI: Executing command {}", command.getCommand());
        
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

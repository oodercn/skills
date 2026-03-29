package net.ooder.skill.agent.cli.service;

import net.ooder.skill.agent.cli.dto.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service
public class FeishuCliService {

    private static final List<CliSkillDTO> SKILLS = Arrays.asList(
            CliSkillDTO.builder()
                    .skillId("feishu-send-message")
                    .name("发送消息")
                    .description("发送飞书消息")
                    .platform("FEISHU")
                    .category("messaging")
                    .commands(Arrays.asList("send-message", "msg"))
                    .examples(Arrays.asList("发送飞书消息给张三"))
                    .icon("ri-message-3-line")
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
                    .build()
    );

    public CliCommandDTO execute(CliCommandDTO command) {
        log.info("Feishu CLI: Executing command {}", command.getCommand());
        
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

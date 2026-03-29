package net.ooder.skill.agent.cli.service;

import net.ooder.skill.agent.cli.dto.*;
import net.ooder.skill.agent.cli.dict.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class AgentCliService {

    @Autowired
    private DingTalkCliService dingTalkCliService;

    @Autowired
    private FeishuCliService feishuCliService;

    @Autowired
    private WeComCliService weComCliService;

    @Autowired
    private NaturalLanguageParser nlParser;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ParsedCommandDTO parseNaturalLanguage(String text, String platform) {
        log.info("Parsing natural language: {} for platform: {}", text, platform);
        return nlParser.parse(text, platform);
    }

    public CliCommandDTO executeCommand(CliCommandDTO command) {
        log.info("Executing command: {} on platform: {}", command.getCommand(), command.getPlatform());
        
        command.setCommandId(UUID.randomUUID().toString());
        command.setStatus(CliCommandStatus.RUNNING.getCode());
        command.setCreateTime(LocalDateTime.now().format(FORMATTER));
        command.setStartTime(LocalDateTime.now().format(FORMATTER));
        
        long startTime = System.currentTimeMillis();
        
        try {
            CliCommandDTO result = null;
            switch (CliPlatform.valueOf(command.getPlatform().toUpperCase())) {
                case DINGTALK:
                    result = dingTalkCliService.execute(command);
                    break;
                case FEISHU:
                    result = feishuCliService.execute(command);
                    break;
                case WECOM:
                    result = weComCliService.execute(command);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported platform: " + command.getPlatform());
            }
            
            result.setStatus(CliCommandStatus.SUCCESS.getCode());
            result.setExitCode(0);
            result.setEndTime(LocalDateTime.now().format(FORMATTER));
            result.setDuration(System.currentTimeMillis() - startTime);
            
            return result;
        } catch (Exception e) {
            log.error("Command execution failed", e);
            command.setStatus(CliCommandStatus.FAILED.getCode());
            command.setExitCode(1);
            command.setError(e.getMessage());
            command.setEndTime(LocalDateTime.now().format(FORMATTER));
            command.setDuration(System.currentTimeMillis() - startTime);
            return command;
        }
    }

    public CliCommandDTO executeNaturalLanguage(String text, String platform, String userId) {
        log.info("Executing natural language: {} for user: {}", text, userId);
        
        ParsedCommandDTO parsed = parseNaturalLanguage(text, platform);
        
        CliCommandDTO command = CliCommandDTO.builder()
                .platform(platform)
                .command(parsed.getCommand())
                .args(parsed.getArgs())
                .options(parsed.getOptions())
                .naturalLanguage(text)
                .userId(userId)
                .build();
        
        return executeCommand(command);
    }

    public List<CliSkillDTO> listSkills(String platform) {
        log.info("Listing skills for platform: {}", platform);
        
        List<CliSkillDTO> skills = new ArrayList<>();
        
        if (platform == null || "DINGTALK".equalsIgnoreCase(platform)) {
            skills.addAll(dingTalkCliService.listSkills());
        }
        if (platform == null || "FEISHU".equalsIgnoreCase(platform)) {
            skills.addAll(feishuCliService.listSkills());
        }
        if (platform == null || "WECOM".equalsIgnoreCase(platform)) {
            skills.addAll(weComCliService.listSkills());
        }
        
        return skills;
    }

    public CliSkillDTO getSkill(String platform, String skillId) {
        log.info("Getting skill: {} for platform: {}", skillId, platform);
        
        switch (CliPlatform.valueOf(platform.toUpperCase())) {
            case DINGTALK:
                return dingTalkCliService.getSkill(skillId);
            case FEISHU:
                return feishuCliService.getSkill(skillId);
            case WECOM:
                return weComCliService.getSkill(skillId);
            default:
                return null;
        }
    }

    public List<CliCommandDTO> getCommandHistory(String userId, Integer limit) {
        log.info("Getting command history for user: {}", userId);
        
        List<CliCommandDTO> history = new ArrayList<>();
        history.add(CliCommandDTO.builder()
                .commandId(UUID.randomUUID().toString())
                .platform("DINGTALK")
                .command("send-message")
                .naturalLanguage("发送消息给张三")
                .status(CliCommandStatus.SUCCESS.getCode())
                .createTime(LocalDateTime.now().minusHours(1).format(FORMATTER))
                .build());
        history.add(CliCommandDTO.builder()
                .commandId(UUID.randomUUID().toString())
                .platform("FEISHU")
                .command("create-doc")
                .naturalLanguage("创建一个新文档")
                .status(CliCommandStatus.SUCCESS.getCode())
                .createTime(LocalDateTime.now().minusHours(2).format(FORMATTER))
                .build());
        
        return history;
    }
}

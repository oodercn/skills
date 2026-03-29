package net.ooder.skill.agent.cli.controller;

import net.ooder.skill.agent.cli.dto.*;
import net.ooder.skill.agent.cli.service.AgentCliService;
import net.ooder.api.result.ResultModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/agent-cli")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AgentCliController {

    @Autowired
    private AgentCliService agentCliService;

    @PostMapping("/parse")
    public ResultModel<ParsedCommandDTO> parseNaturalLanguage(
            @RequestParam String text,
            @RequestParam(required = false) String platform) {
        ParsedCommandDTO parsed = agentCliService.parseNaturalLanguage(text, platform);
        return ResultModel.success(parsed);
    }

    @PostMapping("/execute")
    public ResultModel<CliCommandDTO> executeCommand(@RequestBody CliCommandDTO command) {
        log.info("Executing command: {} on platform: {}", command.getCommand(), command.getPlatform());
        CliCommandDTO result = agentCliService.executeCommand(command);
        return ResultModel.success(result);
    }

    @PostMapping("/execute-nl")
    public ResultModel<CliCommandDTO> executeNaturalLanguage(
            @RequestParam String text,
            @RequestParam String platform,
            @RequestParam String userId) {
        log.info("Executing natural language: {} for user: {}", text, userId);
        CliCommandDTO result = agentCliService.executeNaturalLanguage(text, platform, userId);
        return ResultModel.success(result);
    }

    @GetMapping("/skills")
    public ResultModel<List<CliSkillDTO>> listSkills(
            @RequestParam(required = false) String platform) {
        List<CliSkillDTO> skills = agentCliService.listSkills(platform);
        return ResultModel.success(skills);
    }

    @GetMapping("/skills/{platform}/{skillId}")
    public ResultModel<CliSkillDTO> getSkill(
            @PathVariable String platform,
            @PathVariable String skillId) {
        CliSkillDTO skill = agentCliService.getSkill(platform, skillId);
        return ResultModel.success(skill);
    }

    @GetMapping("/history")
    public ResultModel<List<CliCommandDTO>> getCommandHistory(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<CliCommandDTO> history = agentCliService.getCommandHistory(userId, limit);
        return ResultModel.success(history);
    }
}

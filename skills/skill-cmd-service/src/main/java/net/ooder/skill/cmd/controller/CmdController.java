package net.ooder.skill.cmd.controller;

import net.ooder.skill.cmd.dto.*;
import net.ooder.skill.cmd.service.CmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/north/command")
public class CmdController {

    @Autowired
    private CmdService cmdService;

    @PostMapping("/dispatch")
    public ResponseEntity<Command> dispatch(@RequestBody Command command) {
        return ResponseEntity.ok(cmdService.dispatch(command));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Command>> batchDispatch(@RequestBody List<Command> commands) {
        return ResponseEntity.ok(cmdService.batchDispatch(commands));
    }

    @GetMapping("/{commandId}")
    public ResponseEntity<Command> getCommand(@PathVariable String commandId) {
        Command command = cmdService.getCommand(commandId);
        if (command == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(command);
    }

    @PostMapping("/{commandId}/cancel")
    public ResponseEntity<Boolean> cancelCommand(@PathVariable String commandId) {
        return ResponseEntity.ok(cmdService.cancelCommand(commandId));
    }

    @PostMapping("/{commandId}/retry")
    public ResponseEntity<Command> retryCommand(@PathVariable String commandId) {
        Command command = cmdService.retryCommand(commandId);
        if (command == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(command);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Command>> getCommands(
            @RequestParam(required = false) String agentId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(cmdService.getCommands(agentId, status, page, size));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<CommandLog>> getCommandLogs(
            @RequestParam String commandId,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return ResponseEntity.ok(cmdService.getCommandLogs(commandId, level, page, size));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(cmdService.getStatistics());
    }
}
